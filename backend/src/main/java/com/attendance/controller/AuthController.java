package com.attendance.controller;

import com.attendance.controller.dto.*;
import com.attendance.domain.User;
import com.attendance.service.JwtService;
import com.attendance.service.PasswordPolicyService;
import com.attendance.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "登入、登出、Token 刷新")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordPolicyService passwordPolicyService;

    @Value("${app.jwt.access-token-expiration-ms}")
    private long accessTokenExpirationMs;

    @PostMapping("/login")
    @Operation(summary = "使用者登入，取得 Access Token 與 Refresh Token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "登入成功"),
            @ApiResponse(responseCode = "401", description = "帳密錯誤或帳號停用")
    })
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        User user = (User) auth.getPrincipal();
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user);

        boolean mustChange = user.isMustChangePassword() || isPasswordExpired(user);

        return ResponseEntity.ok(TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpirationMs / 1000)
                .mustChangePassword(mustChange)
                .role(user.getRole())
                .build());
    }

    private boolean isPasswordExpired(User user) {
        int expiryDays = passwordPolicyService.getPolicy().getExpiryDays();
        if (expiryDays <= 0 || user.getPasswordChangedAt() == null) return false;
        return user.getPasswordChangedAt().plusSeconds((long) expiryDays * 86400).isBefore(Instant.now());
    }

    @PostMapping("/refresh")
    @Operation(summary = "使用 Refresh Token 取得新的 Access Token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "刷新成功"),
            @ApiResponse(responseCode = "401", description = "Refresh Token 無效或過期")
    })
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        User user = refreshTokenService.validateAndGetUser(request.getRefreshToken());
        String accessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = refreshTokenService.createRefreshToken(user);

        return ResponseEntity.ok(TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpirationMs / 1000)
                .mustChangePassword(user.isMustChangePassword() || isPasswordExpired(user))
                .role(user.getRole())
                .build());
    }

    @PostMapping("/logout")
    @Operation(summary = "登出並撤銷 Refresh Token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "登出成功"),
            @ApiResponse(responseCode = "401", description = "未授權")
    })
    public ResponseEntity<Void> logout(@AuthenticationPrincipal User user) {
        refreshTokenService.revokeAllForUser(user);
        return ResponseEntity.ok().build();
    }
}
