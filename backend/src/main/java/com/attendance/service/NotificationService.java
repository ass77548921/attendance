package com.attendance.service;

import com.attendance.domain.NotificationLog;
import com.attendance.domain.NotificationLog.NotificationStatus;
import com.attendance.domain.NotificationRecipient;
import com.attendance.domain.User;
import com.attendance.event.LateArrivalEvent;
import com.attendance.service.MailSettingsService.MailProfile;
import com.attendance.repository.NotificationLogRepository;
import com.attendance.repository.NotificationRecipientRepository;
import com.attendance.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final TemplateEngine templateEngine;
    private final NotificationRecipientRepository recipientRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final UserRepository userRepository;
    private final MailSettingsService mailSettingsService;

    @Async("notificationExecutor")
    @EventListener
    public void onLateArrival(LateArrivalEvent event) {
        User user = userRepository.findById(event.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + event.getUserId()));

        List<NotificationRecipient> recipients = recipientRepository.findAllByActiveTrue();
        if (recipients.isEmpty()) {
            log.info("No active notification recipients configured; skipping late arrival notification for user {}",
                    event.getUserId());
            return;
        }

        for (NotificationRecipient recipient : recipients) {
            sendLateArrivalEmail(recipient.getEmail(), user, event);
        }
    }

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 120_000))
    public void sendLateArrivalEmail(String to, User user, LateArrivalEvent event) {
        NotificationLog logEntry = new NotificationLog();
        logEntry.setRecipientEmail(to);
        logEntry.setUserId(event.getUserId());
        logEntry.setWorkDate(event.getWorkDate());
        logEntry.setLateMinutes(event.getLateMinutes());

        try {
            MailProfile profile = mailSettingsService.getEffectiveProfile();
            var mailSender = mailSettingsService.resolveMailSender();
            Context ctx = new Context();
            ctx.setVariable("employeeName", user.getFullName());
            ctx.setVariable("workDate", event.getWorkDate().toString());
            ctx.setVariable("lateMinutes", event.getLateMinutes());
            String htmlBody = templateEngine.process("late-arrival-email", ctx);

            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");
            helper.setFrom(profile.fromEmail(), profile.fromName());
            helper.setTo(to);
            helper.setSubject(profile.subjectPrefix() + " Late Arrival: " + user.getFullName() + " on " + event.getWorkDate());
            helper.setText(htmlBody, true);

            mailSender.send(mime);

            logEntry.setStatus(NotificationStatus.SUCCESS);
            logEntry.setSentAt(Instant.now());
            log.info("Late arrival notification sent to {} for user {} on {}", to, event.getUserId(), event.getWorkDate());
        } catch (Exception e) {
            logEntry.setStatus(NotificationStatus.FAILED);
            logEntry.setErrorMessage(e.getMessage());
            log.error("Failed to send late arrival notification to {}: {}", to, e.getMessage());
            throw new RuntimeException("Email send failed: " + e.getMessage(), e);
        } finally {
            notificationLogRepository.save(logEntry);
        }
    }

    public void sendTestNotification(String to) {
        NotificationLog logEntry = new NotificationLog();
        logEntry.setRecipientEmail(to);
        logEntry.setWorkDate(java.time.LocalDate.now());
        try {
            MailProfile profile = mailSettingsService.getEffectiveProfile();
            var mailSender = mailSettingsService.resolveMailSender();
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, false, "UTF-8");
            helper.setFrom(profile.fromEmail(), profile.fromName());
            helper.setTo(to);
            helper.setSubject(profile.subjectPrefix() + " Test Notification");
            helper.setText("This is a test notification from the Attendance Management System.", false);
            mailSender.send(mime);
            logEntry.setStatus(NotificationStatus.SUCCESS);
            logEntry.setSentAt(Instant.now());
        } catch (Exception e) {
            logEntry.setStatus(NotificationStatus.FAILED);
            logEntry.setErrorMessage(e.getMessage());
            throw new RuntimeException("Test email failed: " + e.getMessage(), e);
        } finally {
            notificationLogRepository.save(logEntry);
        }
    }
}
