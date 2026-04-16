package com.attendance.repository;

import com.attendance.domain.AttendanceAmendment;
import com.attendance.domain.AmendmentStatus;
import com.attendance.domain.AmendmentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceAmendmentRepository extends JpaRepository<AttendanceAmendment, Long> {
    boolean existsByUserIdAndTargetDateAndAmendmentTypeAndStatusIn(
            Long userId, LocalDate targetDate, AmendmentType type, List<AmendmentStatus> statuses);

    @Query("SELECT a FROM AttendanceAmendment a " +
           "LEFT JOIN FETCH a.user " +
           "LEFT JOIN FETCH a.reviewedBy " +
           "LEFT JOIN FETCH a.attachments " +
           "WHERE a.user.id = :userId " +
           "ORDER BY a.createdAt DESC")
    List<AttendanceAmendment> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Query("SELECT a FROM AttendanceAmendment a " +
           "LEFT JOIN FETCH a.user " +
           "LEFT JOIN FETCH a.reviewedBy " +
           "LEFT JOIN FETCH a.attachments " +
           "WHERE a.status = :status " +
           "ORDER BY a.createdAt ASC")
    Page<AttendanceAmendment> findByStatusOrderByCreatedAtAsc(@Param("status") AmendmentStatus status, Pageable pageable);
}
