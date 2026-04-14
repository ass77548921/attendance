package com.attendance.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDate;

@Getter
public class LateArrivalEvent extends ApplicationEvent {

    private final Long userId;
    private final LocalDate workDate;
    private final int lateMinutes;

    public LateArrivalEvent(Object source, Long userId, LocalDate workDate, int lateMinutes) {
        super(source);
        this.userId = userId;
        this.workDate = workDate;
        this.lateMinutes = lateMinutes;
    }
}
