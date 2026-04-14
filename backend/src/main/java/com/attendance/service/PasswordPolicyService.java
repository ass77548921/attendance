package com.attendance.service;

import com.attendance.domain.PasswordHistory;
import com.attendance.domain.PasswordPolicy;
import com.attendance.domain.User;
import com.attendance.exception.PolicyViolationException;
import com.attendance.repository.PasswordHistoryRepository;
import com.attendance.repository.PasswordPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PasswordPolicyService {

    private static final int MAX_HISTORY_SIZE = 24;

    private final PasswordPolicyRepository policyRepository;
    private final PasswordHistoryRepository historyRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordPolicy getPolicy() {
        return policyRepository.findById(1L).orElseGet(() -> {
            PasswordPolicy defaults = PasswordPolicy.builder().id(1L).build();
            return policyRepository.save(defaults);
        });
    }

    @Transactional
    public PasswordPolicy updatePolicy(PasswordPolicy updated) {
        updated.setId(1L);
        return policyRepository.save(updated);
    }

    public void validate(String rawPassword, Long userId) {
        PasswordPolicy policy = getPolicy();

        if (rawPassword.length() < policy.getMinLength()) {
            throw new PolicyViolationException("密碼長度至少需要 " + policy.getMinLength() + " 個字元");
        }
        if (policy.isRequireUppercase() && !rawPassword.chars().anyMatch(Character::isUpperCase)) {
            throw new PolicyViolationException("密碼必須包含至少一個大寫字母");
        }
        if (policy.isRequireLowercase() && !rawPassword.chars().anyMatch(Character::isLowerCase)) {
            throw new PolicyViolationException("密碼必須包含至少一個小寫字母");
        }
        if (policy.isRequireNumber() && !rawPassword.chars().anyMatch(Character::isDigit)) {
            throw new PolicyViolationException("密碼必須包含至少一個數字");
        }
        if (policy.isRequireSpecialChar() && rawPassword.chars().allMatch(c -> Character.isLetterOrDigit(c))) {
            throw new PolicyViolationException("密碼必須包含至少一個特殊字元");
        }

        if (userId != null && policy.getHistoryCount() > 0) {
            List<PasswordHistory> history = historyRepository.findByUserIdOrderByCreatedAtDesc(
                    userId, PageRequest.of(0, policy.getHistoryCount()));
            for (PasswordHistory h : history) {
                if (passwordEncoder.matches(rawPassword, h.getPasswordHash())) {
                    throw new PolicyViolationException("新密碼不可與最近 " + policy.getHistoryCount() + " 次使用的密碼相同");
                }
            }
        }
    }

    @Transactional
    public void recordHistory(User user, String encodedPassword) {
        PasswordHistory entry = PasswordHistory.builder()
                .userId(user.getId())
                .passwordHash(encodedPassword)
                .build();
        historyRepository.save(entry);

        long count = historyRepository.countByUserId(user.getId());
        if (count > MAX_HISTORY_SIZE) {
            PasswordHistory oldest = historyRepository.findTopByUserIdOrderByCreatedAtAsc(user.getId());
            if (oldest != null) {
                historyRepository.delete(oldest);
            }
        }
    }
}
