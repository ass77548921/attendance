package com.attendance.repository;

import com.attendance.domain.AttendanceAmendment;
import com.attendance.domain.AmendmentStatus;
import com.attendance.domain.AmendmentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceAmendmentRepository extends JpaRepository<AttendanceAmendment, Long> {
    boolean existsByUserIdAndTargetDateAndAmendmentTypeAndStatusIn(
            Long userId, LocalDate targetDate, AmendmentType type, List<AmendmentStatus> statuses);

    List<AttendanceAmendment> findByUserIdOrderByCreatedAtDesc(Long userId);

    Page<AttendanceAmendment> findByStatusOrderByCreatedAtAsc(AmendmentStatus status, Pageable pageable);
}
