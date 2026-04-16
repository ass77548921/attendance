package com.attendance.service;

import com.attendance.controller.dto.*;
import com.attendance.domain.*;
import com.attendance.exception.ConflictException;
import com.attendance.exception.PolicyViolationException;
import com.attendance.repository.PasswordResetLogRepository;
import com.attendance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordPolicyService passwordPolicyService;
    private final PasswordResetLogRepository resetLogRepository;

    @Transactional
    public UserResponse createUser(User actor, CreateUserRequest request) {
        assertCanCreateRole(actor, request.getRole());
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username already taken: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already registered: " + request.getEmail());
        }
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .address(request.getAddress())
                .personalPhone(request.getPersonalPhone())
                .officeExtension(request.getOfficeExtension())
                .role(request.getRole())
                .mustChangePassword(true)
                .passwordChangedAt(Instant.now())
                .build();
        User saved = userRepository.save(user);
        passwordPolicyService.recordHistory(saved, saved.getPassword());
        return toResponse(saved);
    }

    public Page<UserResponse> listUsers(User actor,
                                        String search,
                                        Long id,
                                        String role,
                                        String status,
                                        Pageable pageable) {
        List<UserRole> allowedRoles = actor.getRole() == UserRole.SUPER_ADMIN
                ? List.of(UserRole.EMPLOYEE, UserRole.ADMIN)
                : List.of(UserRole.EMPLOYEE);
        UserRole roleFilter = parseRole(role);
        if (roleFilter != null && !allowedRoles.contains(roleFilter)) {
            return new PageImpl<>(List.of(), pageable, 0);
        }
        UserStatus statusFilter = parseStatus(status);

        return userRepository.findByFilters(
                        allowedRoles,
                        id,
                        roleFilter,
                        statusFilter,
                        normalize(search),
                        pageable)
                .map(this::toResponse);
    }

    @Transactional
    public UserResponse updateUser(Long userId, UpdateUserRequest request, User actor) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("User not found: " + userId));
        assertCanManageTarget(actor, user);
        if (userRepository.existsByUsernameAndIdNot(request.getUsername(), userId)) {
            throw new ConflictException("Username already taken: " + request.getUsername());
        }
        if (userRepository.existsByEmailAndIdNot(request.getEmail(), userId)) {
            throw new ConflictException("Email already registered: " + request.getEmail());
        }
        user.setUsername(request.getUsername());
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setAddress(request.getAddress());
        user.setPersonalPhone(request.getPersonalPhone());
        user.setOfficeExtension(request.getOfficeExtension());
        user.setStatus(request.getStatus());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setMustChangePassword(true);
            user.setPasswordChangedAt(Instant.now());
        }
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse resetPassword(Long targetId, String newPassword, String reason, Long adminId, User actor) {
        User user = userRepository.findById(targetId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("User not found: " + targetId));
        assertCanManageTarget(actor, user);
        passwordPolicyService.validate(newPassword, targetId);
        String encoded = passwordEncoder.encode(newPassword);
        user.setPassword(encoded);
        user.setMustChangePassword(true);
        user.setPasswordChangedAt(Instant.now());
        userRepository.save(user);
        passwordPolicyService.recordHistory(user, encoded);
        resetLogRepository.save(PasswordResetLog.builder()
                .targetUserId(targetId)
                .resetByUserId(adminId)
                .reason(reason)
                .build());
        return toResponse(user);
    }

    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("User not found: " + userId));
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new PolicyViolationException("目前密碼不正確");
        }
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new PolicyViolationException("新密碼不可與目前密碼相同");
        }
        passwordPolicyService.validate(newPassword, userId);
        String encoded = passwordEncoder.encode(newPassword);
        user.setPassword(encoded);
        user.setMustChangePassword(false);
        user.setPasswordChangedAt(Instant.now());
        userRepository.save(user);
        passwordPolicyService.recordHistory(user, encoded);
    }

    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("User not found: " + userId));
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmailAndIdNot(request.getEmail(), userId)) {
                throw new ConflictException("Email already registered: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress().isBlank() ? null : request.getAddress());
        }
        if (request.getPersonalPhone() != null) {
            user.setPersonalPhone(request.getPersonalPhone().isBlank() ? null : request.getPersonalPhone());
        }
        if (request.getOfficeExtension() != null) {
            user.setOfficeExtension(request.getOfficeExtension().isBlank() ? null : request.getOfficeExtension());
        }
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateStatus(Long userId, UserStatus status, User actor) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("User not found: " + userId));
        assertCanManageTarget(actor, user);
        user.setStatus(status);
        return toResponse(userRepository.save(user));
    }

    private void assertCanCreateRole(User actor, UserRole roleToCreate) {
        if (roleToCreate == UserRole.SUPER_ADMIN) {
            throw new AccessDeniedException("Cannot create SUPER_ADMIN via API");
        }
        if (actor.getRole() == UserRole.ADMIN && roleToCreate != UserRole.EMPLOYEE) {
            throw new AccessDeniedException("ADMIN can only create EMPLOYEE accounts");
        }
    }

    private void assertCanManageTarget(User actor, User target) {
        if (actor.getRole() == UserRole.ADMIN && target.getRole() != UserRole.EMPLOYEE) {
            throw new AccessDeniedException("ADMIN can only manage EMPLOYEE accounts");
        }
        if (target.getRole() == UserRole.SUPER_ADMIN) {
            throw new AccessDeniedException("SUPER_ADMIN accounts are not manageable via this API");
        }
    }

    private UserRole parseRole(String role) {
        if (role == null || role.isBlank()) {
            return null;
        }
        try {
            return UserRole.valueOf(role.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
    }

    private UserStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return UserStatus.valueOf(status.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .address(user.getAddress())
                .personalPhone(user.getPersonalPhone())
                .officeExtension(user.getOfficeExtension())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
