package com.attendance.service;

import com.attendance.domain.AttendanceConfig;
import com.attendance.domain.AttendanceRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

class AttendanceRuleEngineTest {

    private AttendanceRuleEngine ruleEngine;
    private AttendanceConfig config;

    @BeforeEach
    void setUp() {
        ruleEngine = new AttendanceRuleEngine();
        config = new AttendanceConfig();
        config.setWorkStartTime(LocalTime.of(9, 0));
        config.setWorkEndTime(LocalTime.of(18, 0));
        config.setLateToleranceMinutes(10);
        config.setLunchBreakMinutes(60);
        config.setRequiredWorkMinutes(480);
        config.setTimezone("Asia/Taipei");
    }

    @Test
    void clockInOnTimeIsNotLate() {
        // 09:05 — within tolerance
        AttendanceRecord record = recordWithClockIn(LocalTime.of(9, 5));
        ruleEngine.calculateClockIn(record, config);
        assertThat(record.isLate()).isFalse();
        assertThat(record.getLateMinutes()).isZero();
    }

    @Test
    void clockInAfterToleranceIsLate() {
        // 09:15 — 15 min after start, tolerance is 10 min → late by 5 min
        AttendanceRecord record = recordWithClockIn(LocalTime.of(9, 15));
        ruleEngine.calculateClockIn(record, config);
        assertThat(record.isLate()).isTrue();
        assertThat(record.getLateMinutes()).isEqualTo(5);
    }

    @Test
    void clockInExactlyAtToleranceLimitIsNotLate() {
        // 09:10 — exactly at tolerance edge
        AttendanceRecord record = recordWithClockIn(LocalTime.of(9, 10));
        ruleEngine.calculateClockIn(record, config);
        assertThat(record.isLate()).isFalse();
    }

    @Test
    void clockOutOnTimeIsNotEarlyLeave() {
        AttendanceRecord record = recordWithClockInOut(LocalTime.of(9, 0), LocalTime.of(18, 0));
        ruleEngine.calculateClockOut(record, config);
        assertThat(record.isEarlyLeave()).isFalse();
    }

    @Test
    void clockOutEarlyIsEarlyLeave() {
        // 17:00 instead of 18:00; after lunch (60 min): worked 420 min vs required 480 → short 60 min
        AttendanceRecord record = recordWithClockInOut(LocalTime.of(9, 0), LocalTime.of(17, 0));
        ruleEngine.calculateClockOut(record, config);
        assertThat(record.isEarlyLeave()).isTrue();
        assertThat(record.getShortMinutes()).isGreaterThan(0);
    }

    private AttendanceRecord recordWithClockIn(LocalTime time) {
        AttendanceRecord record = new AttendanceRecord();
        record.setClockInTime(toInstant(LocalDate.now(), time));
        return record;
    }

    private AttendanceRecord recordWithClockInOut(LocalTime in, LocalTime out) {
        AttendanceRecord record = new AttendanceRecord();
        record.setClockInTime(toInstant(LocalDate.now(), in));
        record.setClockOutTime(toInstant(LocalDate.now(), out));
        return record;
    }

    private Instant toInstant(LocalDate date, LocalTime time) {
        ZoneId zone = ZoneId.of(config.getTimezone());
        return date.atTime(time).atZone(zone).toInstant();
    }
}
