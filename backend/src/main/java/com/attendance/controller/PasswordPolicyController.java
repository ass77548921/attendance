package com.attendance.controller;

import com.attendance.controller.dto.PasswordPolicyResponse;
import com.attendance.controller.dto.UpdatePasswordPolicyRequest;
import com.attendance.domain.PasswordPolicy;
import com.attendance.service.PasswordPolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/password-policy")
@RequiredArgsConstructor
@Tag(name = "Admin - Password Policy", description = "密碼規則設定管理")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class PasswordPolicyController {

    private final PasswordPolicyService policyService;

    @GetMapping
    @Operation(summary = "查詢當前密碼規則")
    @ApiResponse(responseCode = "200", description = "查詢成功")
    public ResponseEntity<PasswordPolicyResponse> getPolicy() {
        return ResponseEntity.ok(toResponse(policyService.getPolicy()));
    }

    @PutMapping
    @Operation(summary = "更新密碼規則")
    @ApiResponse(responseCode = "200", description = "更新成功")
    public ResponseEntity<PasswordPolicyResponse> updatePolicy(@Valid @RequestBody UpdatePasswordPolicyRequest request) {
        PasswordPolicy updated = PasswordPolicy.builder()
                .id(1L)
                .minLength(request.getMinLength())
                .requireUppercase(request.isRequireUppercase())
                .requireLowercase(request.isRequireLowercase())
                .requireNumber(request.isRequireNumber())
                .requireSpecialChar(request.isRequireSpecialChar())
                .expiryDays(request.getExpiryDays())
                .historyCount(request.getHistoryCount())
                .build();
        return ResponseEntity.ok(toResponse(policyService.updatePolicy(updated)));
    }

    private PasswordPolicyResponse toResponse(PasswordPolicy p) {
        return PasswordPolicyResponse.builder()
                .id(p.getId())
                .minLength(p.getMinLength())
                .requireUppercase(p.isRequireUppercase())
                .requireLowercase(p.isRequireLowercase())
                .requireNumber(p.isRequireNumber())
                .requireSpecialChar(p.isRequireSpecialChar())
                .expiryDays(p.getExpiryDays())
                .historyCount(p.getHistoryCount())
                .build();
    }
}
