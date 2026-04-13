CREATE TABLE attendance_records
(
    id                BIGINT     NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id           BIGINT     NOT NULL,
    work_date         DATE       NOT NULL,
    clock_in_time     DATETIME(6),
    clock_out_time    DATETIME(6),
    is_late           TINYINT(1) NOT NULL DEFAULT 0,
    late_minutes      INTEGER    NOT NULL DEFAULT 0,
    is_early_leave    TINYINT(1) NOT NULL DEFAULT 0,
    short_minutes     INTEGER    NOT NULL DEFAULT 0,
    notification_sent TINYINT(1) NOT NULL DEFAULT 0,
    created_at        DATETIME(6)         DEFAULT CURRENT_TIMESTAMP(6),
    updated_at        DATETIME(6)         DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_attendance_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    UNIQUE (user_id, work_date)
);

CREATE INDEX idx_attendance_user_date ON attendance_records (user_id, work_date);
CREATE INDEX idx_attendance_is_late ON attendance_records (is_late, work_date);
