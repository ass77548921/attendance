package com.attendance.controller;

import com.attendance.controller.dto.*;
import com.attendance.domain.User;
import com.attendance.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin - Users", description = "管理員帳號管理")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "建立使用者帳號")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "建立成功"),
            @ApiResponse(responseCode = "409", description = "帳號或 Email 重複")
    })
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request,
                                                   @AuthenticationPrincipal User admin) {
        UserResponse response = userService.createUser(admin, request);
        return ResponseEntity.created(URI.create("/api/admin/users/" + response.getId())).body(response);
    }

    @GetMapping
    @Operation(summary = "查詢使用者列表")
    @ApiResponse(responseCode = "200", description = "查詢成功")
    public ResponseEntity<Page<UserResponse>> listUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal User admin,
            Pageable pageable) {
        return ResponseEntity.ok(userService.listUsers(admin, search, id, role, status, pageable));
    }

    @PostMapping("/{id}/reset-password")
    @Operation(summary = "管理員重設員工密碼（需填寫備注）")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "重設成功"),
            @ApiResponse(responseCode = "400", description = "密碼不符規則或備注為空"),
            @ApiResponse(responseCode = "404", description = "使用者不存在")
    })
    public ResponseEntity<UserResponse> resetPassword(@PathVariable Long id,
                                                      @Valid @RequestBody ResetPasswordRequest request,
                                                      @AuthenticationPrincipal User admin) {
        return ResponseEntity.ok(userService.resetPassword(id, request.getNewPassword(), request.getReason(), admin.getId(), admin));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新員工帳號資料")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "404", description = "使用者不存在"),
            @ApiResponse(responseCode = "409", description = "帳號或 Email 重複")
    })
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                                   @Valid @RequestBody UpdateUserRequest request,
                                                   @AuthenticationPrincipal User admin) {
        return ResponseEntity.ok(userService.updateUser(id, request, admin));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "更新使用者帳號狀態（啟用 / 停用）")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "404", description = "使用者不存在")
    })
    public ResponseEntity<UserResponse> updateStatus(@PathVariable Long id,
                                                     @Valid @RequestBody UpdateUserStatusRequest request,
                                                     @AuthenticationPrincipal User admin) {
        return ResponseEntity.ok(userService.updateStatus(id, request.getStatus(), admin));
    }
}
