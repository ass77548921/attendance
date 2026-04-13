package com.attendance.controller;

import com.attendance.controller.dto.AttendanceConfigResponse;
import com.attendance.controller.dto.UpdateAttendanceConfigRequest;
import com.attendance.domain.AttendanceConfig;
import com.attendance.service.AttendanceConfigService;
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

@RestController
@RequestMapping("/api/admin/config")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Admin - Config", description = "出勤規則設定")
@SecurityRequirement(name = "bearerAuth")
public class AttendanceConfigController {

    private final AttendanceConfigService configService;

    @GetMapping
    @Operation(summary = "查詢目前出勤規則")
    @ApiResponse(responseCode = "200", description = "查詢成功")
    public ResponseEntity<AttendanceConfigResponse> getConfig() {
        return ResponseEntity.ok(AttendanceConfigResponse.from(configService.getConfig()));
    }

    @PutMapping
    @Operation(summary = "更新出勤規則")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "422", description = "欄位驗證失敗")
    })
    public ResponseEntity<AttendanceConfigResponse> updateConfig(
            @Valid @RequestBody UpdateAttendanceConfigRequest req) {
        AttendanceConfig updated = new AttendanceConfig();
        updated.setWorkStartTime(req.getWorkStartTime());
        updated.setWorkEndTime(req.getWorkEndTime());
        updated.setLateToleranceMinutes(req.getLateToleranceMinutes());
        updated.setLunchBreakMinutes(req.getLunchBreakMinutes());
        updated.setRequiredWorkMinutes(req.getRequiredWorkMinutes());
        updated.setTimezone(req.getTimezone());
        return ResponseEntity.ok(AttendanceConfigResponse.from(configService.updateConfig(updated)));
    }
}
