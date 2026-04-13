package com.attendance.controller;

import com.attendance.controller.dto.AmendmentResponse;
import com.attendance.domain.AmendmentType;
import com.attendance.domain.User;
import com.attendance.service.AmendmentService;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/attendance/amendments")
@RequiredArgsConstructor
@Tag(name = "Attendance - Amendments", description = "補打卡申請")
@SecurityRequirement(name = "bearerAuth")
public class AmendmentController {

    private final AmendmentService amendmentService;

    @PostMapping(consumes = "multipart/form-data")
    @Operation(summary = "提交補打卡申請（可附上截圖 / PDF）")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "提交成功"),
            @ApiResponse(responseCode = "409", description = "已有待審核/已核准申請"),
            @ApiResponse(responseCode = "422", description = "欄位或附件格式不合法")
    })
    public ResponseEntity<AmendmentResponse> submit(
            @AuthenticationPrincipal User user,
            @RequestParam String targetDate,
            @RequestParam AmendmentType amendmentType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant amendedTime,
            @RequestParam String reason,
            @RequestParam(required = false) List<MultipartFile> attachments) throws IOException {

        AmendmentResponse response = amendmentService.submit(
                user, targetDate, amendmentType, amendedTime, reason, attachments);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    @Operation(summary = "查詢個人補打卡申請列表")
    @ApiResponse(responseCode = "200", description = "查詢成功")
    public ResponseEntity<List<AmendmentResponse>> myAmendments(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(amendmentService.getMyAmendments(user.getId()));
    }
}
