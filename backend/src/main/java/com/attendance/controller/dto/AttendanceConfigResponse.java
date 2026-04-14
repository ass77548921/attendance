package com.attendance.controller.dto;

import com.attendance.domain.AttendanceConfig;
import lombok.Data;

import java.time.LocalTime;

@Data
public class AttendanceConfigResponse {
    private Long id;
    private LocalTime workStartTime;
    private LocalTime workEndTime;
    private int lateToleranceMinutes;
    private int lunchBreakMinutes;
    private int requiredWorkMinutes;
    private String timezone;

    public static AttendanceConfigResponse from(AttendanceConfig config) {
        AttendanceConfigResponse res = new AttendanceConfigResponse();
        res.id = config.getId();
        res.workStartTime = config.getWorkStartTime();
        res.workEndTime = config.getWorkEndTime();
        res.lateToleranceMinutes = config.getLateToleranceMinutes();
        res.lunchBreakMinutes = config.getLunchBreakMinutes();
        res.requiredWorkMinutes = config.getRequiredWorkMinutes();
        res.timezone = config.getTimezone();
        return res;
    }
}
