package com.attendance.controller;

import com.attendance.controller.dto.CreateNotificationRecipientRequest;
import com.attendance.domain.NotificationRecipient;
import com.attendance.exception.ConflictException;
import com.attendance.repository.NotificationRecipientRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/notification/recipients")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Admin - Notification Recipients", description = "通知收件人管理")
@SecurityRequirement(name = "bearerAuth")
public class NotificationRecipientController {

    private final NotificationRecipientRepository recipientRepository;

    @GetMapping
    @Operation(summary = "查詢通知收件人列表")
    @ApiResponse(responseCode = "200", description = "查詢成功")
    public ResponseEntity<List<NotificationRecipient>> listRecipients() {
        return ResponseEntity.ok(recipientRepository.findAll());
    }

    @PostMapping
    @Operation(summary = "新增通知收件人")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "新增成功"),
            @ApiResponse(responseCode = "409", description = "Email 已存在")
    })
    public ResponseEntity<NotificationRecipient> addRecipient(
            @Valid @RequestBody CreateNotificationRecipientRequest req) {
        if (recipientRepository.existsByEmail(req.getEmail())) {
            throw new ConflictException("Email already registered as notification recipient");
        }
        NotificationRecipient recipient = new NotificationRecipient();
        recipient.setEmail(req.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(recipientRepository.save(recipient));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "刪除通知收件人")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "刪除成功"),
            @ApiResponse(responseCode = "404", description = "收件人不存在")
    })
    public ResponseEntity<Void> removeRecipient(@PathVariable Long id) {
        NotificationRecipient recipient = recipientRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Recipient not found"));
        recipientRepository.delete(recipient);
        return ResponseEntity.noContent().build();
    }
}
