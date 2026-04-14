package com.attendance.controller.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalTime;

@Data
public class UpdateAttendanceConfigRequest {
    @NotNull
    private LocalTime workStartTime;

    @NotNull
    private LocalTime workEndTime;

    @Min(0) @Max(60)
    private int lateToleranceMinutes;

    @Min(0) @Max(120)
    private int lunchBreakMinutes;

    @Min(60) @Max(600)
    private int requiredWorkMinutes;

    @NotBlank
    private String timezone;
}
