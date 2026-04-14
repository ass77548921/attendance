package com.attendance.controller;

import com.attendance.controller.dto.MailSettingsResponse;
import com.attendance.controller.dto.MailTestRequest;
import com.attendance.controller.dto.UpdateMailSettingsRequest;
import com.attendance.service.MailSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/mail-settings")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Admin - Mail Settings", description = "郵件寄送設定管理")
@SecurityRequirement(name = "bearerAuth")
public class AdminMailSettingsController {

    private final MailSettingsService mailSettingsService;

    @GetMapping
    @Operation(summary = "查詢目前郵件設定")
    @ApiResponse(responseCode = "200", description = "查詢成功")
    public ResponseEntity<MailSettingsResponse> getSettings() {
        return ResponseEntity.ok(mailSettingsService.getSettings());
    }

    @PutMapping
    @Operation(summary = "更新郵件設定")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "422", description = "欄位驗證失敗")
    })
    public ResponseEntity<MailSettingsResponse> updateSettings(@Valid @RequestBody UpdateMailSettingsRequest request) {
        return ResponseEntity.ok(mailSettingsService.updateSettings(request));
    }

    @PostMapping("/test")
    @Operation(summary = "測試郵件發送")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "發送成功"),
            @ApiResponse(responseCode = "502", description = "發送失敗")
    })
    public ResponseEntity<Map<String, String>> testSend(@Valid @RequestBody MailTestRequest request) {
        try {
            mailSettingsService.sendTestMail(request.getTo());
            return ResponseEntity.ok(Map.of("message", "Test notification sent to " + request.getTo()));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(502).body(Map.of(
                    "message", "Test email failed",
                    "detail", ex.getMessage()
            ));
        }
    }
}