package com.attendance.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalTime;

@Entity
@Table(name = "attendance_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "work_start_time", nullable = false)
    private LocalTime workStartTime;

    @Column(name = "work_end_time", nullable = false)
    private LocalTime workEndTime;

    @Column(name = "late_tolerance_minutes", nullable = false)
    private int lateToleranceMinutes;

    @Column(name = "lunch_break_minutes", nullable = false)
    private int lunchBreakMinutes;

    @Column(name = "required_work_minutes", nullable = false)
    private int requiredWorkMinutes;

    @Column(name = "timezone", nullable = false, length = 50)
    private String timezone;

    @Column(name = "updated_at")
    @Builder.Default
    private Instant updatedAt = Instant.now();

    @PreUpdate
    void onUpdate() { this.updatedAt = Instant.now(); }
}
