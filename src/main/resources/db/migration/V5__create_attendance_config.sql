CREATE TABLE attendance_config
(
    id                     BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    work_start_time        TIME        NOT NULL DEFAULT '09:00:00',
    work_end_time          TIME        NOT NULL DEFAULT '18:00:00',
    late_tolerance_minutes INTEGER     NOT NULL DEFAULT 10,
    lunch_break_minutes    INTEGER     NOT NULL DEFAULT 60,
    required_work_minutes  INTEGER     NOT NULL DEFAULT 480,
    timezone               VARCHAR(50) NOT NULL DEFAULT 'Asia/Taipei',
    updated_at             DATETIME(6)          DEFAULT CURRENT_TIMESTAMP(6)
);

-- Insert default configuration
INSERT INTO attendance_config (work_start_time, work_end_time, late_tolerance_minutes, lunch_break_minutes,
                               required_work_minutes, timezone)
VALUES ('09:00:00', '18:00:00', 10, 60, 480, 'Asia/Taipei');
