package com.attendance.controller.dto;

import com.attendance.domain.AttendanceAdjustmentAudit;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class AttendanceAdjustmentAuditResponse {
    private Long auditId;
    private Long attendanceRecordId;
    private Long modifiedByUserId;
    private String modifiedByUsername;
    private String reason;
    private Instant beforeClockInTime;
    private Instant beforeClockOutTime;
    private boolean beforeIsLate;
    private int beforeLateMinutes;
    private boolean beforeIsEarlyLeave;
    private int beforeShortMinutes;
    private Instant afterClockInTime;
    private Instant afterClockOutTime;
    private boolean afterIsLate;
    private int afterLateMinutes;
    private boolean afterIsEarlyLeave;
    private int afterShortMinutes;
    private Instant modifiedAt;

    public static AttendanceAdjustmentAuditResponse from(AttendanceAdjustmentAudit audit) {
        return AttendanceAdjustmentAuditResponse.builder()
                .auditId(audit.getId())
                .attendanceRecordId(audit.getAttendanceRecord().getId())
                .modifiedByUserId(audit.getModifiedBy().getId())
                .modifiedByUsername(audit.getModifiedBy().getUsername())
                .reason(audit.getReason())
                .beforeClockInTime(audit.getBeforeClockInTime())
                .beforeClockOutTime(audit.getBeforeClockOutTime())
                .beforeIsLate(audit.isBeforeIsLate())
                .beforeLateMinutes(audit.getBeforeLateMinutes())
                .beforeIsEarlyLeave(audit.isBeforeIsEarlyLeave())
                .beforeShortMinutes(audit.getBeforeShortMinutes())
                .afterClockInTime(audit.getAfterClockInTime())
                .afterClockOutTime(audit.getAfterClockOutTime())
                .afterIsLate(audit.isAfterIsLate())
                .afterLateMinutes(audit.getAfterLateMinutes())
                .afterIsEarlyLeave(audit.isAfterIsEarlyLeave())
                .afterShortMinutes(audit.getAfterShortMinutes())
                .modifiedAt(audit.getModifiedAt())
                .build();
    }
}