package com.attendance.controller.dto;

import com.attendance.domain.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;
    private boolean mustChangePassword;
    private UserRole role;
}
