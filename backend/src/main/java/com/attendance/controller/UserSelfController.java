package com.attendance.controller;

import com.attendance.controller.dto.ChangePasswordRequest;
import com.attendance.domain.User;
import com.attendance.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User - Self", description = "使用者自身操作")
@SecurityRequirement(name = "bearerAuth")
public class UserSelfController {

    private final UserService userService;

    @PostMapping("/change-password")
    @Operation(summary = "修改自己的密碼（含強制密碼強更）")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "密碼修改成功"),
            @ApiResponse(responseCode = "400", description = "密碼不符規則"),
            @ApiResponse(responseCode = "401", description = "未授權")
    })
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                               @AuthenticationPrincipal User user) {
        userService.changePassword(user.getId(), request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }
}
