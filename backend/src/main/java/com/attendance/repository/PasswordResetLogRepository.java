package com.attendance.repository;

import com.attendance.domain.PasswordResetLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetLogRepository extends JpaRepository<PasswordResetLog, Long> {
}
