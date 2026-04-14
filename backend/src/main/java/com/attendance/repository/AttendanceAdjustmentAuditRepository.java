package com.attendance.repository;

import com.attendance.domain.AttendanceAdjustmentAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttendanceAdjustmentAuditRepository extends JpaRepository<AttendanceAdjustmentAudit, Long> {
    Optional<AttendanceAdjustmentAudit> findTopByAttendanceRecordIdOrderByModifiedAtDesc(Long attendanceRecordId);

    List<AttendanceAdjustmentAudit> findByAttendanceRecordIdInOrderByModifiedAtDesc(List<Long> attendanceRecordIds);

    List<AttendanceAdjustmentAudit> findByAttendanceRecordIdOrderByModifiedAtDesc(Long attendanceRecordId);
}