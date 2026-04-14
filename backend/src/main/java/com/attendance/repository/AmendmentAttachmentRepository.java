package com.attendance.repository;

import com.attendance.domain.AmendmentAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AmendmentAttachmentRepository extends JpaRepository<AmendmentAttachment, Long> {
    Optional<AmendmentAttachment> findByIdAndAmendmentId(Long id, Long amendmentId);
}
