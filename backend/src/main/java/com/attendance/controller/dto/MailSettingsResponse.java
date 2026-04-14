package com.attendance.controller.dto;

import com.attendance.domain.MailSettings;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class MailSettingsResponse {
    private Long id;
    private String smtpHost;
    private int smtpPort;
    private String smtpUsername;
    private boolean hasPassword;
    private String fromEmail;
    private String fromName;
    private String subjectPrefix;
    private boolean enabled;
    private Instant updatedAt;

    public static MailSettingsResponse from(MailSettings settings) {
        return MailSettingsResponse.builder()
                .id(settings.getId())
                .smtpHost(settings.getSmtpHost())
                .smtpPort(settings.getSmtpPort())
                .smtpUsername(settings.getSmtpUsername())
                .hasPassword(settings.getSmtpPasswordEncrypted() != null && !settings.getSmtpPasswordEncrypted().isBlank())
                .fromEmail(settings.getFromEmail())
                .fromName(settings.getFromName())
                .subjectPrefix(settings.getSubjectPrefix())
                .enabled(settings.isEnabled())
                .updatedAt(settings.getUpdatedAt())
                .build();
    }
}