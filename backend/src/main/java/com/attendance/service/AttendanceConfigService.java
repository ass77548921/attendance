package com.attendance.service;

import com.attendance.domain.AttendanceConfig;
import com.attendance.repository.AttendanceConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AttendanceConfigService {

    private final AttendanceConfigRepository configRepository;

    @Transactional(readOnly = true)
    public AttendanceConfig getConfig() {
        return configRepository.findTopByOrderByIdAsc()
                .orElseThrow(() -> new IllegalStateException("Attendance config not initialized"));
    }

    @Transactional
    public AttendanceConfig updateConfig(AttendanceConfig updated) {
        if (updated.getWorkStartTime() == null || updated.getWorkEndTime() == null) {
            throw new IllegalArgumentException("workStartTime and workEndTime are required");
        }
        if (!updated.getWorkStartTime().isBefore(updated.getWorkEndTime())) {
            throw new IllegalArgumentException("workStartTime must be earlier than workEndTime");
        }

        AttendanceConfig config = getConfig();
        config.setWorkStartTime(updated.getWorkStartTime());
        config.setWorkEndTime(updated.getWorkEndTime());
        config.setLateToleranceMinutes(updated.getLateToleranceMinutes());
        config.setLunchBreakMinutes(updated.getLunchBreakMinutes());
        config.setRequiredWorkMinutes(updated.getRequiredWorkMinutes());
        config.setTimezone(updated.getTimezone());
        return configRepository.save(config);
    }
}
