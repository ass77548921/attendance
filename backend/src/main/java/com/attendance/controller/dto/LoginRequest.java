package com.attendance.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private String clientType;

    public boolean isAdminConsoleLogin() {
        return "ADMIN_CONSOLE".equalsIgnoreCase(clientType);
    }
}
