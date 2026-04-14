package com.attendance.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "password_policy")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordPolicy {

    @Id
    private Long id;

    @Column(name = "min_length", nullable = false)
    @Builder.Default
    private int minLength = 8;

    @Column(name = "require_uppercase", nullable = false)
    @Builder.Default
    private boolean requireUppercase = true;

    @Column(name = "require_lowercase", nullable = false)
    @Builder.Default
    private boolean requireLowercase = true;

    @Column(name = "require_number", nullable = false)
    @Builder.Default
    private boolean requireNumber = true;

    @Column(name = "require_special_char", nullable = false)
    @Builder.Default
    private boolean requireSpecialChar = false;

    @Column(name = "expiry_days", nullable = false)
    @Builder.Default
    private int expiryDays = 0;

    @Column(name = "history_count", nullable = false)
    @Builder.Default
    private int historyCount = 5;
}
