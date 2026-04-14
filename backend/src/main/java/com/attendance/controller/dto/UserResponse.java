package com.attendance.controller.dto;

import com.attendance.domain.UserRole;
import com.attendance.domain.UserStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String address;
    private String personalPhone;
    private String officeExtension;
    private UserRole role;
    private UserStatus status;
    private Instant createdAt;
}
