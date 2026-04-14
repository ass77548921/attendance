package com.attendance.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.Instant;

@Data
public class AdminAdjustAttendanceRequest {
    private Instant clockInTime;
    private Instant clockOutTime;

    @NotBlank
    private String reason;
}