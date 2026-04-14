package com.attendance.controller;

import com.attendance.controller.dto.TestNotificationRequest;
import com.attendance.domain.NotificationLog;
import com.attendance.repository.NotificationLogRepository;
import com.attendance.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/notification")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Admin - Notification", description = "通知測試與紀錄查詢")
@SecurityRequirement(name = "bearerAuth")
public class AdminNotificationController {

    private final NotificationService notificationService;
    private final NotificationLogRepository notificationLogRepository;

    @PostMapping("/test")
    @Operation(summary = "發送測試通知")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "發送成功"),
            @ApiResponse(responseCode = "500", description = "SMTP 設定錯誤或發送失敗")
    })
    public ResponseEntity<Map<String, String>> testNotification(
            @Valid @RequestBody TestNotificationRequest req) {
        notificationService.sendTestNotification(req.getEmail());
        return ResponseEntity.ok(Map.of("message", "Test notification sent to " + req.getEmail()));
    }

    @GetMapping("/logs")
    @Operation(summary = "查詢通知發送紀錄")
    @ApiResponse(responseCode = "200", description = "查詢成功")
    public ResponseEntity<Page<NotificationLog>> getLogs(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(notificationLogRepository.findAllByOrderByCreatedAtDesc(pageable));
    }
}
