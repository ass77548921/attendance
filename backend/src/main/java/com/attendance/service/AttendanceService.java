package com.attendance.service;

import com.attendance.controller.dto.AdminAdjustAttendanceRequest;
import com.attendance.controller.dto.AttendanceAdjustmentAuditResponse;
import com.attendance.controller.dto.AttendanceAdjustmentSummaryResponse;
import com.attendance.controller.dto.AttendanceRecordResponse;
import com.attendance.domain.*;
import com.attendance.event.LateArrivalEvent;
import com.attendance.exception.ConflictException;
import com.attendance.repository.AttendanceAdjustmentAuditRepository;
import com.attendance.repository.AttendanceConfigRepository;
import com.attendance.repository.AttendanceRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final AttendanceAdjustmentAuditRepository attendanceAdjustmentAuditRepository;
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
        Page<AttendanceRecord> records = attendanceRecordRepository.findByAdminFilter(userId, startDate, endDate, isLate, pageable);
        Map<Long, AttendanceAdjustmentAudit> latestAdjustments = getLatestAdjustmentsMap(records.getContent());

        List<AttendanceRecordResponse> content = records.getContent().stream()
                .map(record -> toResponse(record, latestAdjustments.get(record.getId())))
                .toList();

        return new PageImpl<>(content, pageable, records.getTotalElements());
    }

    @Transactional
    public AttendanceRecordResponse adminAdjustAttendance(Long recordId, AdminAdjustAttendanceRequest request, User reviewer) {
        if ((request.getClockInTime() == null && request.getClockOutTime() == null)
                || request.getReason() == null
                || request.getReason().isBlank()) {
            throw new IllegalArgumentException("At least one adjustment field and non-empty reason are required");
        }

        AttendanceRecord record = attendanceRecordRepository.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException("Attendance record not found: " + recordId));

        Instant beforeClockIn = record.getClockInTime();
        Instant beforeClockOut = record.getClockOutTime();
        boolean beforeIsLate = record.isLate();
        int beforeLateMinutes = record.getLateMinutes();
        boolean beforeIsEarlyLeave = record.isEarlyLeave();
        int beforeShortMinutes = record.getShortMinutes();

        if (request.getClockInTime() != null) {
            record.setClockInTime(request.getClockInTime());
        }
        if (request.getClockOutTime() != null) {
            record.setClockOutTime(request.getClockOutTime());
        }

        ruleEngine.recalculate(record, getConfig());
        AttendanceRecord saved = attendanceRecordRepository.save(record);

        AttendanceAdjustmentAudit audit = AttendanceAdjustmentAudit.builder()
                .attendanceRecord(saved)
                .modifiedBy(reviewer)
                .reason(request.getReason().trim())
                .beforeClockInTime(beforeClockIn)
                .beforeClockOutTime(beforeClockOut)
                .beforeIsLate(beforeIsLate)
                .beforeLateMinutes(beforeLateMinutes)
                .beforeIsEarlyLeave(beforeIsEarlyLeave)
                .beforeShortMinutes(beforeShortMinutes)
                .afterClockInTime(saved.getClockInTime())
                .afterClockOutTime(saved.getClockOutTime())
                .afterIsLate(saved.isLate())
                .afterLateMinutes(saved.getLateMinutes())
                .afterIsEarlyLeave(saved.isEarlyLeave())
                .afterShortMinutes(saved.getShortMinutes())
                .build();

        AttendanceAdjustmentAudit persistedAudit = attendanceAdjustmentAuditRepository.save(audit);
        return toResponse(saved, persistedAudit);
    }

    @Transactional(readOnly = true)
    public List<AttendanceAdjustmentAuditResponse> getAttendanceAdjustmentHistory(Long recordId) {
        return attendanceAdjustmentAuditRepository.findByAttendanceRecordIdOrderByModifiedAtDesc(recordId)
                .stream()
                .map(AttendanceAdjustmentAuditResponse::from)
                .toList();
    }

    public AttendanceConfig getConfig() {
        return attendanceConfigRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Attendance config not found"));
    }

    public AttendanceRecordResponse toResponse(AttendanceRecord r) {
        return toResponse(r, null);
    }

    public AttendanceRecordResponse toResponse(AttendanceRecord r, AttendanceAdjustmentAudit latestAdjustment) {
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
                .latestAdjustment(latestAdjustment == null ? null : AttendanceAdjustmentSummaryResponse.from(latestAdjustment))
                .build();
    }

    private Map<Long, AttendanceAdjustmentAudit> getLatestAdjustmentsMap(List<AttendanceRecord> records) {
        if (records.isEmpty()) {
            return Map.of();
        }
        List<Long> ids = records.stream().map(AttendanceRecord::getId).toList();
        List<AttendanceAdjustmentAudit> audits = attendanceAdjustmentAuditRepository.findByAttendanceRecordIdInOrderByModifiedAtDesc(ids);

        Map<Long, AttendanceAdjustmentAudit> latest = new HashMap<>();
        for (AttendanceAdjustmentAudit audit : audits) {
            latest.putIfAbsent(audit.getAttendanceRecord().getId(), audit);
        }
        return latest;
    }
}
