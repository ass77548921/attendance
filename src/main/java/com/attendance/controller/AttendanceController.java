package com.attendance.controller;

import com.attendance.controller.dto.AttendanceRecordResponse;
import com.attendance.domain.User;
import com.attendance.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance", description = "打卡操作")
@SecurityRequirement(name = "bearerAuth")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/clock-in")
    @Operation(summary = "上班打卡")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "打卡成功"),
            @ApiResponse(responseCode = "409", description = "今日已上班打卡")
    })
    public ResponseEntity<AttendanceRecordResponse> clockIn(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(201).body(attendanceService.clockIn(user));
    }

    @PostMapping("/clock-out")
    @Operation(summary = "下班打卡")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "打卡成功"),
            @ApiResponse(responseCode = "409", description = "今日已下班打卡"),
            @ApiResponse(responseCode = "422", description = "尚未上班打卡")
    })
    public ResponseEntity<AttendanceRecordResponse> clockOut(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(attendanceService.clockOut(user));
    }

    @GetMapping("/today")
    @Operation(summary = "查詢今日打卡狀態")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查詢成功"),
            @ApiResponse(responseCode = "204", description = "今日尚無打卡紀錄")
    })
    public ResponseEntity<AttendanceRecordResponse> today(@AuthenticationPrincipal User user) {
        AttendanceRecordResponse response = attendanceService.getToday(user);
        if (response == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "查詢個人打卡紀錄（依日期範圍）")
    @ApiResponse(responseCode = "200", description = "查詢成功")
    public ResponseEntity<List<AttendanceRecordResponse>> list(
            @AuthenticationPrincipal User user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(attendanceService.getByDateRange(user.getId(), startDate, endDate));
    }
}
