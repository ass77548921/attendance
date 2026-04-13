package com.attendance.controller.dto;

import com.attendance.domain.AmendmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewAmendmentRequest {
    @NotNull
    private AmendmentStatus status;
    private String reviewNote;
}
