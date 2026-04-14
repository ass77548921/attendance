package com.attendance.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MailTestRequest {
    @NotBlank
    @Email
    private String to;
}