package com.attendance.repository;

import com.attendance.domain.NotificationRecipient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRecipientRepository extends JpaRepository<NotificationRecipient, Long> {
    List<NotificationRecipient> findAllByActiveTrue();
    boolean existsByEmail(String email);
}
