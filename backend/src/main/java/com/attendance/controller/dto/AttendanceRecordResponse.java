package com.attendance.controller.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
public class AttendanceRecordResponse {
    private Long id;
    private Long userId;
    private String userFullName;
    private LocalDate workDate;
    private Instant clockInTime;
    private Instant clockOutTime;
    private boolean isLate;
    private int lateMinutes;
    private boolean isEarlyLeave;
    private int shortMinutes;
    private Long workDurationMinutes;
    private AttendanceAdjustmentSummaryResponse latestAdjustment;
}
