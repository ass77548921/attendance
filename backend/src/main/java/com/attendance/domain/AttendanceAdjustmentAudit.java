package com.attendance.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "attendance_adjustment_audits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceAdjustmentAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_record_id", nullable = false)
    private AttendanceRecord attendanceRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modified_by", nullable = false)
    private User modifiedBy;

    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(name = "before_clock_in_time")
    private Instant beforeClockInTime;

    @Column(name = "before_clock_out_time")
    private Instant beforeClockOutTime;

    @Column(name = "before_is_late", nullable = false)
    private boolean beforeIsLate;

    @Column(name = "before_late_minutes", nullable = false)
    private int beforeLateMinutes;

    @Column(name = "before_is_early_leave", nullable = false)
    private boolean beforeIsEarlyLeave;

    @Column(name = "before_short_minutes", nullable = false)
    private int beforeShortMinutes;

    @Column(name = "after_clock_in_time")
    private Instant afterClockInTime;

    @Column(name = "after_clock_out_time")
    private Instant afterClockOutTime;

    @Column(name = "after_is_late", nullable = false)
    private boolean afterIsLate;

    @Column(name = "after_late_minutes", nullable = false)
    private int afterLateMinutes;

    @Column(name = "after_is_early_leave", nullable = false)
    private boolean afterIsEarlyLeave;

    @Column(name = "after_short_minutes", nullable = false)
    private int afterShortMinutes;

    @Column(name = "modified_at", nullable = false)
    @Builder.Default
    private Instant modifiedAt = Instant.now();
}