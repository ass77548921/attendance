package com.attendance.controller.dto;

import com.attendance.domain.UserRole;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateUserRequest {
    @NotBlank
    @Size(min = 3, max = 100)
    private String username;

    @NotBlank
    @Size(min = 8, max = 100)
    private String password;

    @NotBlank
    @Size(max = 200)
    private String fullName;

    @NotBlank
    @Email
    private String email;

    @NotNull
    private UserRole role;
}
