package com.attendance.controller;

import com.attendance.controller.dto.*;
import com.attendance.domain.User;
import com.attendance.repository.AmendmentAttachmentRepository;
import com.attendance.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin - Dashboard", description = "後台管理")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AttendanceService attendanceService;
    private final AmendmentService amendmentService;
    private final AmendmentAttachmentRepository attachmentRepository;
    private final FileStorageService fileStorageService;

    // ── Attendance Records ────────────────────────────────────────

    @GetMapping("/attendance")
    @Operation(summary = "查詢員工出勤紀錄（支援篩選與分頁）")
    @ApiResponse(responseCode = "200", description = "查詢成功")
    public ResponseEntity<Page<AttendanceRecordResponse>> queryAttendance(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Boolean isLate,
            Pageable pageable) {
        return ResponseEntity.ok(attendanceService.adminQuery(userId, startDate, endDate, isLate, pageable));
    }

    @GetMapping("/attendance/summary")
    @Operation(summary = "出勤統計摘要（月度）")
    @ApiResponse(responseCode = "200", description = "查詢成功")
    public ResponseEntity<?> summary(
            @RequestParam int year,
            @RequestParam int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        Page<AttendanceRecordResponse> page = attendanceService.adminQuery(null, startDate, endDate, null, Pageable.unpaged());
        long total = page.getTotalElements();
        long lateCount = page.getContent().stream().filter(AttendanceRecordResponse::isLate).count();
        return ResponseEntity.ok(java.util.Map.of(
                "year", year, "month", month,
                "totalRecords", total,
                "lateCount", lateCount,
                "normalCount", total - lateCount
        ));
    }

    // ── Amendments ────────────────────────────────────────────────

    @GetMapping("/amendments")
    @Operation(summary = "查詢待審核補打卡申請")
    @ApiResponse(responseCode = "200", description = "查詢成功")
    public ResponseEntity<Page<AmendmentResponse>> pendingAmendments(Pageable pageable) {
        return ResponseEntity.ok(amendmentService.getPendingAmendments(pageable));
    }

    @PostMapping("/amendments/{id}/review")
    @Operation(summary = "審核補打卡申請")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "審核成功"),
            @ApiResponse(responseCode = "404", description = "申請不存在")
    })
    public ResponseEntity<AmendmentResponse> review(@PathVariable Long id,
                                                    @AuthenticationPrincipal User reviewer,
                                                    @RequestBody ReviewAmendmentRequest request) {
        return ResponseEntity.ok(amendmentService.review(id, reviewer, request));
    }

    @GetMapping("/amendments/{id}/attachments/{fileId}")
    @Operation(summary = "下載補打卡附件")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "下載成功"),
            @ApiResponse(responseCode = "404", description = "附件不存在")
    })
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Long id, @PathVariable Long fileId) {
        var attachment = attachmentRepository.findByIdAndAmendmentId(fileId, id)
                .orElseThrow(() -> new EntityNotFoundException("Attachment not found"));

        Path file = fileStorageService.resolveSecurePath(attachment.getStoredPath());
        Resource resource = new PathResource(file);
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getMimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + attachment.getOriginalFilename() + "\"")
                .body(resource);
    }
}
