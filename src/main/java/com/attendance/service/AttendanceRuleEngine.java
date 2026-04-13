package com.attendance.service;

import com.attendance.domain.AttendanceConfig;
import com.attendance.domain.AttendanceRecord;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class AttendanceRuleEngine {

    /**
     * Calculate late status based on clock-in time and config.
     * Updates isLate and lateMinutes on the record (in-place).
     */
    public void calculateClockIn(AttendanceRecord record, AttendanceConfig config) {
        Instant clockIn = record.getClockInTime();
        if (clockIn == null) return;

        ZoneId zone = ZoneId.of(config.getTimezone());
        LocalTime clockInLocal = clockIn.atZone(zone).toLocalTime();
        LocalTime deadline = config.getWorkStartTime().plusMinutes(config.getLateToleranceMinutes());

        if (clockInLocal.isAfter(deadline)) {
            record.setLate(true);
            int minutes = (int) java.time.Duration.between(deadline, clockInLocal).toMinutes();
            record.setLateMinutes(minutes);
        } else {
            record.setLate(false);
            record.setLateMinutes(0);
        }
    }

    /**
     * Calculate early-leave status based on clock-out time and config.
     * Deducts lunch break from actual working time.
     */
    public void calculateClockOut(AttendanceRecord record, AttendanceConfig config) {
        Instant clockIn = record.getClockInTime();
        Instant clockOut = record.getClockOutTime();
        if (clockIn == null || clockOut == null) return;

        long workedMinutes = java.time.Duration.between(clockIn, clockOut).toMinutes()
                             - config.getLunchBreakMinutes();
        int required = config.getRequiredWorkMinutes();

        if (workedMinutes < required) {
            record.setEarlyLeave(true);
            record.setShortMinutes((int) (required - workedMinutes));
        } else {
            record.setEarlyLeave(false);
            record.setShortMinutes(0);
        }
    }

    /**
     * Recalculate all rule-based fields after an amendment is approved.
     */
    public void recalculate(AttendanceRecord record, AttendanceConfig config) {
        calculateClockIn(record, config);
        calculateClockOut(record, config);
    }
}
