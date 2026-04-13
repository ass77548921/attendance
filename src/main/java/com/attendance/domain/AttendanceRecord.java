package com.attendance.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "attendance_records",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "work_date"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Column(name = "clock_in_time")
    private Instant clockInTime;

    @Column(name = "clock_out_time")
    private Instant clockOutTime;

    @Column(name = "is_late", nullable = false)
    @Builder.Default
    private boolean isLate = false;

    @Column(name = "late_minutes", nullable = false)
    @Builder.Default
    private int lateMinutes = 0;

    @Column(name = "is_early_leave", nullable = false)
    @Builder.Default
    private boolean isEarlyLeave = false;

    @Column(name = "short_minutes", nullable = false)
    @Builder.Default
    private int shortMinutes = 0;

    @Column(name = "notification_sent", nullable = false)
    @Builder.Default
    private boolean notificationSent = false;

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    @Builder.Default
    private Instant updatedAt = Instant.now();

    @PreUpdate
    void onUpdate() { this.updatedAt = Instant.now(); }
}
