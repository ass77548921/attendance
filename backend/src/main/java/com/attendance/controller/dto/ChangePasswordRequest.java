package com.attendance.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotBlank(message = "目前密碼為必填")
    private String currentPassword;

    @NotBlank(message = "新密碼為必填")
    @Size(min = 4, max = 128, message = "密碼長度需在 4 到 128 個字元之間")
    private String newPassword;
}
