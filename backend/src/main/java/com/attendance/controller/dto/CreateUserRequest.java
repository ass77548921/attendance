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

    @Size(max = 255)
    private String address;

    @Size(max = 50)
    private String personalPhone;

    @Size(max = 50)
    private String officeExtension;

    @NotNull
    private UserRole role;
}
