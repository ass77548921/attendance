package com.attendance.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "password_reset_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "target_user_id", nullable = false)
    private Long targetUserId;

    @Column(name = "reset_by_user_id", nullable = false)
    private Long resetByUserId;

    @Column(name = "reason", nullable = false, length = 500)
    private String reason;

    @Column(name = "reset_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant resetAt = Instant.now();
}
