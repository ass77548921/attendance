package com.attendance.controller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdatePasswordPolicyRequest {

    @Min(value = 4, message = "最小密碼長度不得少於 4 個字元")
    @Max(value = 128, message = "最大密碼長度不得超過 128 個字元")
    private int minLength = 8;

    private boolean requireUppercase = true;

    private boolean requireLowercase = true;

    private boolean requireNumber = true;

    private boolean requireSpecialChar = false;

    @Min(value = 0, message = "密碼有效天數不得小於 0（0 表示永不過期）")
    private int expiryDays = 0;

    @Min(value = 0, message = "歷史密碼限制不得小於 0（0 表示不限）")
    @Max(value = 24, message = "歷史密碼限制最多 24 次")
    private int historyCount = 5;
}
