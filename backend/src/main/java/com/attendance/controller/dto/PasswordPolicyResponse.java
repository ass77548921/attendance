package com.attendance.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PasswordPolicyResponse {
    private Long id;
    private int minLength;
    private boolean requireUppercase;
    private boolean requireLowercase;
    private boolean requireNumber;
    private boolean requireSpecialChar;
    private int expiryDays;
    private int historyCount;
}
