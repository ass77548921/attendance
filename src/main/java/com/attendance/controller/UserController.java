package com.attendance.controller;

import com.attendance.controller.dto.*;
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
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin - Users", description = "管理員帳號管理")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "建立使用者帳號")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "建立成功"),
            @ApiResponse(responseCode = "409", description = "帳號或 Email 重複")
    })
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.created(URI.create("/api/admin/users/" + response.getId())).body(response);
    }

    @GetMapping
    @Operation(summary = "查詢使用者列表")
    @ApiResponse(responseCode = "200", description = "查詢成功")
    public ResponseEntity<Page<UserResponse>> listUsers(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return ResponseEntity.ok(userService.listUsers(search, pageable));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "更新使用者帳號狀態（啟用 / 停用）")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "404", description = "使用者不存在")
    })
    public ResponseEntity<UserResponse> updateStatus(@PathVariable Long id,
                                                     @Valid @RequestBody UpdateUserStatusRequest request) {
        return ResponseEntity.ok(userService.updateStatus(id, request.getStatus()));
    }
}
