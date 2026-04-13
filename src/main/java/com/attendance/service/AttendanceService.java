package com.attendance.service;

import com.attendance.controller.dto.AttendanceRecordResponse;
import com.attendance.domain.*;
import com.attendance.event.LateArrivalEvent;
import com.attendance.exception.ConflictException;
import com.attendance.repository.AttendanceConfigRepository;
import com.attendance.repository.AttendanceRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final AttendanceConfigRepository attendanceConfigRepository;
    private final AttendanceRuleEngine ruleEngine;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public AttendanceRecordResponse clockIn(User user) {
        AttendanceConfig config = getConfig();
        LocalDate today = LocalDate.now(ZoneId.of(config.getTimezone()));

        attendanceRecordRepository.findByUserIdAndWorkDate(user.getId(), today)
                .ifPresent(r -> { throw new ConflictException("Already clocked in today"); });

        AttendanceRecord record = AttendanceRecord.builder()
                .user(user)
                .workDate(today)
                .clockInTime(Instant.now())
                .build();
        ruleEngine.calculateClockIn(record, config);
        record = attendanceRecordRepository.save(record);

        if (record.isLate()) {
            eventPublisher.publishEvent(new LateArrivalEvent(this, user.getId(), today, record.getLateMinutes()));
        }
        return toResponse(record);
    }

    @Transactional
    public AttendanceRecordResponse clockOut(User user) {
        AttendanceConfig config = getConfig();
        LocalDate today = LocalDate.now(ZoneId.of(config.getTimezone()));

        AttendanceRecord record = attendanceRecordRepository.findByUserIdAndWorkDate(user.getId(), today)
                .orElseThrow(() -> new IllegalArgumentException("No clock-in record found for today"));

        if (record.getClockOutTime() != null) {
            throw new ConflictException("Already clocked out today");
        }
        record.setClockOutTime(Instant.now());
        ruleEngine.calculateClockOut(record, config);
        return toResponse(attendanceRecordRepository.save(record));
    }

    public AttendanceRecordResponse getToday(User user) {
        AttendanceConfig config = getConfig();
        LocalDate today = LocalDate.now(ZoneId.of(config.getTimezone()));
        return attendanceRecordRepository.findByUserIdAndWorkDate(user.getId(), today)
                .map(this::toResponse)
                .orElse(null);
    }

    public List<AttendanceRecordResponse> getByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return attendanceRecordRepository.findByUserAndDateRange(userId, startDate, endDate)
                .stream().map(this::toResponse).toList();
    }

    public Page<AttendanceRecordResponse> adminQuery(Long userId, LocalDate startDate, LocalDate endDate,
                                                     Boolean isLate, Pageable pageable) {
        return attendanceRecordRepository.findByAdminFilter(userId, startDate, endDate, isLate, pageable)
                .map(this::toResponse);
    }

    public AttendanceConfig getConfig() {
        return attendanceConfigRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Attendance config not found"));
    }

    public AttendanceRecordResponse toResponse(AttendanceRecord r) {
        Long workedMinutes = null;
        if (r.getClockInTime() != null && r.getClockOutTime() != null) {
            workedMinutes = Duration.between(r.getClockInTime(), r.getClockOutTime()).toMinutes();
        }
        return AttendanceRecordResponse.builder()
                .id(r.getId())
                .userId(r.getUser().getId())
                .userFullName(r.getUser().getFullName())
                .workDate(r.getWorkDate())
                .clockInTime(r.getClockInTime())
                .clockOutTime(r.getClockOutTime())
                .isLate(r.isLate())
                .lateMinutes(r.getLateMinutes())
                .isEarlyLeave(r.isEarlyLeave())
                .shortMinutes(r.getShortMinutes())
                .workDurationMinutes(workedMinutes)
                .build();
    }
}
