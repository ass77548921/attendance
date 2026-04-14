package com.attendance.service;

import com.attendance.controller.dto.AmendmentResponse;
import com.attendance.controller.dto.ReviewAmendmentRequest;
import com.attendance.domain.*;
import com.attendance.event.LateArrivalEvent;
import com.attendance.exception.ConflictException;
import com.attendance.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AmendmentService {

    private final AttendanceAmendmentRepository amendmentRepository;
    private final AmendmentAttachmentRepository attachmentRepository;
    private final AttendanceRecordRepository recordRepository;
    private final AttendanceConfigRepository configRepository;
    private final AttendanceRuleEngine ruleEngine;
    private final FileStorageService fileStorageService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public AmendmentResponse submit(User user, String targetDateStr, AmendmentType amendmentType,
                                    Instant amendedTime, String reason,
                                    List<MultipartFile> files) throws IOException {
        java.time.LocalDate targetDate = java.time.LocalDate.parse(targetDateStr);

        boolean duplicate = amendmentRepository
                .existsByUserIdAndTargetDateAndAmendmentTypeAndStatusIn(
                        user.getId(), targetDate, amendmentType,
                        List.of(AmendmentStatus.PENDING, AmendmentStatus.APPROVED));
        if (duplicate) {
            throw new ConflictException("A pending or approved amendment already exists for this date and type");
        }

        // Validate files first
        if (files != null && !files.isEmpty()) {
            fileStorageService.validate(files);
        }

        AttendanceAmendment amendment = AttendanceAmendment.builder()
                .user(user)
                .targetDate(targetDate)
                .amendmentType(amendmentType)
                .amendedTime(amendedTime)
                .reason(reason)
                .build();
        amendment = amendmentRepository.save(amendment);

        // Store attachments
        if (files != null) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String storedPath = fileStorageService.store(file, amendment.getId());
                    AmendmentAttachment attachment = AmendmentAttachment.builder()
                            .amendment(amendment)
                            .originalFilename(file.getOriginalFilename() != null ? file.getOriginalFilename() : "file")
                            .storedPath(storedPath)
                            .mimeType(file.getContentType())
                            .fileSize(file.getSize())
                            .build();
                    attachmentRepository.save(attachment);
                    amendment.getAttachments().add(attachment);
                }
            }
        }
        return toResponse(amendment);
    }

    @Transactional
    public AmendmentResponse review(Long amendmentId, User reviewer, ReviewAmendmentRequest request) {
        AttendanceAmendment amendment = amendmentRepository.findById(amendmentId)
                .orElseThrow(() -> new EntityNotFoundException("Amendment not found: " + amendmentId));

        if (amendment.getStatus() != AmendmentStatus.PENDING) {
            throw new IllegalArgumentException("Amendment is not in PENDING status");
        }
        if (request.getStatus() == AmendmentStatus.PENDING) {
            throw new IllegalArgumentException("Cannot set status back to PENDING");
        }

        amendment.setStatus(request.getStatus());
        amendment.setReviewNote(request.getReviewNote());
        amendment.setReviewedBy(reviewer);
        amendment.setReviewedAt(Instant.now());

        if (request.getStatus() == AmendmentStatus.APPROVED) {
            applyAmendmentToRecord(amendment);
        }
        return toResponse(amendmentRepository.save(amendment));
    }

    private void applyAmendmentToRecord(AttendanceAmendment amendment) {
        AttendanceConfig config = configRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Attendance config not found"));

        AttendanceRecord record = recordRepository
                .findByUserIdAndWorkDate(amendment.getUser().getId(), amendment.getTargetDate())
                .orElseGet(() -> AttendanceRecord.builder()
                        .user(amendment.getUser())
                        .workDate(amendment.getTargetDate())
                        .build());

        boolean wasLate = record.isLate();

        if (amendment.getAmendmentType() == AmendmentType.CLOCK_IN) {
            record.setClockInTime(amendment.getAmendedTime());
        } else {
            record.setClockOutTime(amendment.getAmendedTime());
        }

        ruleEngine.recalculate(record, config);
        record = recordRepository.save(record);

        // Publish event if late status changed to true
        if (!wasLate && record.isLate()) {
            eventPublisher.publishEvent(new LateArrivalEvent(this,
                    amendment.getUser().getId(), amendment.getTargetDate(), record.getLateMinutes()));
        }
    }

    public List<AmendmentResponse> getMyAmendments(Long userId) {
        return amendmentRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::toResponse).toList();
    }

    public Page<AmendmentResponse> getPendingAmendments(Pageable pageable) {
        return amendmentRepository.findByStatusOrderByCreatedAtAsc(AmendmentStatus.PENDING, pageable)
                .map(this::toResponse);
    }

    private AmendmentResponse toResponse(AttendanceAmendment a) {
        return AmendmentResponse.builder()
                .id(a.getId())
                .userId(a.getUser().getId())
                .userFullName(a.getUser().getFullName())
                .targetDate(a.getTargetDate())
                .amendmentType(a.getAmendmentType())
                .amendedTime(a.getAmendedTime())
                .reason(a.getReason())
                .status(a.getStatus())
                .reviewNote(a.getReviewNote())
                .reviewedBy(a.getReviewedBy() != null ? a.getReviewedBy().getId() : null)
                .reviewedAt(a.getReviewedAt())
                .createdAt(a.getCreatedAt())
                .attachments(a.getAttachments().stream()
                        .map(att -> AmendmentResponse.AttachmentInfo.builder()
                                .id(att.getId())
                                .originalFilename(att.getOriginalFilename())
                                .mimeType(att.getMimeType())
                                .fileSize(att.getFileSize())
                                .build())
                        .toList())
                .build();
    }
}
