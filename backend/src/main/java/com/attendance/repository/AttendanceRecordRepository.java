package com.attendance.repository;

import com.attendance.domain.AttendanceRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {

    @Query("SELECT a FROM AttendanceRecord a JOIN FETCH a.user WHERE a.user.id = :userId AND a.workDate = :workDate")
    Optional<AttendanceRecord> findByUserIdAndWorkDate(@Param("userId") Long userId, @Param("workDate") LocalDate workDate);

    @Query("SELECT a FROM AttendanceRecord a JOIN FETCH a.user WHERE a.user.id = :userId " +
           "AND a.workDate BETWEEN :startDate AND :endDate ORDER BY a.workDate DESC")
    List<AttendanceRecord> findByUserAndDateRange(@Param("userId") Long userId,
                                                  @Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);

    @Query("SELECT a FROM AttendanceRecord a JOIN FETCH a.user " +
           "WHERE (:userId IS NULL OR a.user.id = :userId) " +
           "AND (:startDate IS NULL OR a.workDate >= :startDate) " +
           "AND (:endDate IS NULL OR a.workDate <= :endDate) " +
           "AND (:isLate IS NULL OR a.isLate = :isLate)")
    Page<AttendanceRecord> findByAdminFilter(@Param("userId") Long userId,
                                             @Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate,
                                             @Param("isLate") Boolean isLate,
                                             Pageable pageable);
}
