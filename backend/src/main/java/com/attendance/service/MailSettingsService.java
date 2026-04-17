package com.attendance.service;

import com.attendance.controller.dto.MailSettingsResponse;
import com.attendance.controller.dto.UpdateMailSettingsRequest;
import com.attendance.domain.MailSettings;
import com.attendance.repository.MailSettingsRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class MailSettingsService {

    private static final String AES_GCM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;
    private static final int GCM_IV_LENGTH = 12;

    private final MailSettingsRepository mailSettingsRepository;
    private final JavaMailSender javaMailSender;

    @Value("${app.notification.from-email:noreply@attendance.local}")
    private String defaultFromEmail;

    @Value("${app.notification.from-name:出勤管理系統}")
    private String defaultFromName;

    @Value("${app.notification.subject-prefix:[Attendance]}")
    private String defaultSubjectPrefix;

    @Value("${app.security.settings-encryption-key:attendance-settings-default-key}")
    private String settingsEncryptionKey;

    @Transactional(readOnly = true)
    public MailSettingsResponse getSettings() {
        return mailSettingsRepository.findTopByOrderByIdAsc()
                .map(MailSettingsResponse::from)
                .orElse(MailSettingsResponse.builder()
                        .smtpHost("")
                        .smtpPort(587)
                        .smtpUsername("")
                        .hasPassword(false)
                        .fromEmail(defaultFromEmail)
                        .fromName(defaultFromName)
                        .subjectPrefix(defaultSubjectPrefix)
                        .enabled(true)
                        .build());
    }

    @Transactional
    public MailSettingsResponse updateSettings(UpdateMailSettingsRequest request) {
        MailSettings settings = mailSettingsRepository.findTopByOrderByIdAsc().orElseGet(MailSettings::new);

        settings.setSmtpHost(request.getSmtpHost());
        settings.setSmtpPort(request.getSmtpPort());
        settings.setSmtpUsername(request.getSmtpUsername());
        settings.setFromEmail(request.getFromEmail());
        settings.setFromName(request.getFromName());
        settings.setSubjectPrefix(request.getSubjectPrefix());
        settings.setEnabled(request.getEnabled() == null || request.getEnabled());

        if (request.getSmtpPassword() != null && !request.getSmtpPassword().isBlank()) {
            settings.setSmtpPasswordEncrypted(encrypt(request.getSmtpPassword()));
        } else if (settings.getSmtpPasswordEncrypted() == null) {
            settings.setSmtpPasswordEncrypted(encrypt(""));
        }

        return MailSettingsResponse.from(mailSettingsRepository.save(settings));
    }

    public void sendTestMail(String to) {
        MailProfile profile = getEffectiveProfile();
        JavaMailSender sender = resolveMailSender();
        try {
            MimeMessage mime = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, false, "UTF-8");
            helper.setFrom(profile.fromEmail());
            helper.setTo(to);
            helper.setSubject(profile.subjectPrefix() + " Test Notification");
            helper.setText("This is a test notification from the Attendance Management System.", false);
            sender.send(mime);
        } catch (Exception e) {
            throw new RuntimeException("Test email failed: " + e.getMessage(), e);
        }
    }

    /**
     * 解析實際使用的 JavaMailSender：
     * 1. 若 DB 有啟用的設定且 smtpHost 不為空 → 以 DB 設定動態建立 sender（支援後台熱更新）
     * 2. 否則 fallback 使用環境變數設定的 bean
     */
    @Transactional(readOnly = true)
    public JavaMailSender resolveMailSender() {
        return mailSettingsRepository.findTopByOrderByIdAsc()
                .filter(MailSettings::isEnabled)
                .filter(s -> s.getSmtpHost() != null && !s.getSmtpHost().isBlank())
                .map(this::toMailSenderImpl)
                .map(s -> (JavaMailSender) s)
                .orElse(javaMailSender);
    }

    private JavaMailSenderImpl toMailSenderImpl(MailSettings settings) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(settings.getSmtpHost());
        sender.setPort(settings.getSmtpPort());
        sender.setUsername(settings.getSmtpUsername());
        sender.setPassword(decrypt(settings.getSmtpPasswordEncrypted()));

        Properties props = sender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.connectiontimeout", "5000");
        return sender;
    }

    @Transactional(readOnly = true)
    public MailProfile getEffectiveProfile() {
        return mailSettingsRepository.findTopByOrderByIdAsc()
                .filter(MailSettings::isEnabled)
                .map(s -> new MailProfile(
                        s.getFromEmail(),
                        s.getFromName(),
                        s.getSubjectPrefix(),
                        s.getSmtpUsername(),
                        decrypt(s.getSmtpPasswordEncrypted())
                ))
                .orElseGet(() -> new MailProfile(
                        defaultFromEmail,
                        defaultFromName,
                        defaultSubjectPrefix,
                        "",
                        ""
                ));
    }

    private String encrypt(String plainText) {
        try {
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(AES_GCM);
            cipher.init(Cipher.ENCRYPT_MODE, buildSecretKey(), new GCMParameterSpec(GCM_TAG_LENGTH, iv));
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            byte[] payload = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, payload, 0, iv.length);
            System.arraycopy(encrypted, 0, payload, iv.length, encrypted.length);

            return Base64.getEncoder().encodeToString(payload);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to encrypt settings value", e);
        }
    }

    private String decrypt(String encryptedPayload) {
        try {
            if (encryptedPayload == null || encryptedPayload.isBlank()) {
                return "";
            }
            byte[] payload = Base64.getDecoder().decode(encryptedPayload);
            byte[] iv = Arrays.copyOfRange(payload, 0, GCM_IV_LENGTH);
            byte[] encrypted = Arrays.copyOfRange(payload, GCM_IV_LENGTH, payload.length);

            Cipher cipher = Cipher.getInstance(AES_GCM);
            cipher.init(Cipher.DECRYPT_MODE, buildSecretKey(), new GCMParameterSpec(GCM_TAG_LENGTH, iv));
            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to decrypt settings value", e);
        }
    }

    private SecretKeySpec buildSecretKey() throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(settingsEncryptionKey.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(Arrays.copyOf(hash, 16), "AES");
    }

    public record MailProfile(
            String fromEmail,
            String fromName,
            String subjectPrefix,
            String smtpUsername,
            String smtpPassword
    ) {
    }
}