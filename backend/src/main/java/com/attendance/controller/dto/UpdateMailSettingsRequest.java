package com.attendance.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateMailSettingsRequest {
    @NotBlank
    private String smtpHost;

    @Min(1)
    @Max(65535)
    private int smtpPort;

    @NotBlank
    private String smtpUsername;

    private String smtpPassword;

    @NotBlank
    @Email
    private String fromEmail;

    @NotBlank
    private String fromName;

    @NotBlank
    private String subjectPrefix;

    private Boolean enabled;
}