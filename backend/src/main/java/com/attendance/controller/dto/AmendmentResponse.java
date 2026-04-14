package com.attendance.controller.dto;

import com.attendance.domain.AmendmentStatus;
import com.attendance.domain.AmendmentType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class AmendmentResponse {
    private Long id;
    private Long userId;
    private String userFullName;
    private LocalDate targetDate;
    private AmendmentType amendmentType;
    private Instant amendedTime;
    private String reason;
    private AmendmentStatus status;
    private String reviewNote;
    private Long reviewedBy;
    private Instant reviewedAt;
    private Instant createdAt;
    private List<AttachmentInfo> attachments;

    @Data
    @Builder
    public static class AttachmentInfo {
        private Long id;
        private String originalFilename;
        private String mimeType;
        private long fileSize;
    }
}
