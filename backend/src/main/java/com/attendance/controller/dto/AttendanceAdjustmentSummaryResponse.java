package com.attendance.controller.dto;

import com.attendance.domain.AttendanceAdjustmentAudit;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class AttendanceAdjustmentSummaryResponse {
    private Long auditId;
    private Long modifiedByUserId;
    private String modifiedByUsername;
    private String reason;
    private Instant modifiedAt;

    public static AttendanceAdjustmentSummaryResponse from(AttendanceAdjustmentAudit audit) {
        return AttendanceAdjustmentSummaryResponse.builder()
                .auditId(audit.getId())
                .modifiedByUserId(audit.getModifiedBy().getId())
                .modifiedByUsername(audit.getModifiedBy().getUsername())
                .reason(audit.getReason())
                .modifiedAt(audit.getModifiedAt())
                .build();
    }
}