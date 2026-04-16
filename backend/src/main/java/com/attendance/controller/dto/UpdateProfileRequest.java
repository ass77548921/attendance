package com.attendance.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @Size(max = 200)
    private String fullName;

    @Email
    @Size(max = 200)
    private String email;

    @Size(min = 1, max = 100, message = "密碼不得為空字串")
    private String password; // null = keep existing; empty string is rejected by @Size(min=1)

    @Size(max = 255)
    private String address;

    @Size(max = 50)
    private String personalPhone;

    @Size(max = 50)
    private String officeExtension;
}
