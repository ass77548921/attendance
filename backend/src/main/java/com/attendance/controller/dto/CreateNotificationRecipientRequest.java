package com.attendance.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateNotificationRecipientRequest {
    @NotBlank @Email
    private String email;
}
