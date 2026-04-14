package com.attendance.repository;

import com.attendance.domain.MailSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MailSettingsRepository extends JpaRepository<MailSettings, Long> {
    Optional<MailSettings> findTopByOrderByIdAsc();
}