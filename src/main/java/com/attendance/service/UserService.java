package com.attendance.service;

import com.attendance.controller.dto.*;
import com.attendance.domain.*;
import com.attendance.exception.ConflictException;
import com.attendance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
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
                .role(request.getRole())
                .build();
        return toResponse(userRepository.save(user));
    }

    public Page<UserResponse> listUsers(String search, Pageable pageable) {
        return userRepository.findByRoleAndSearch(UserRole.EMPLOYEE, search, pageable)
                .map(this::toResponse);
    }

    @Transactional
    public UserResponse updateStatus(Long userId, UserStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("User not found: " + userId));
        user.setStatus(status);
        return toResponse(userRepository.save(user));
    }

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
