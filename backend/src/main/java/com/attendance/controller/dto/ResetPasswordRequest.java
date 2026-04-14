package com.attendance.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotBlank(message = "新密碼為必填")
    @Size(min = 4, max = 128, message = "密碼長度需在 4 到 128 個字元之間")
    private String newPassword;

    @NotBlank(message = "備注說明為必填")
    @Size(max = 500, message = "備注說明不得超過 500 個字元")
    private String reason;
}
