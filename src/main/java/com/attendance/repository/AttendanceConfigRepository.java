package com.attendance.repository;

import com.attendance.domain.AttendanceConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttendanceConfigRepository extends JpaRepository<AttendanceConfig, Long> {
    Optional<AttendanceConfig> findTopByOrderByIdAsc();
}
