CREATE TABLE attendance_config
(
    id                     BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '出勤設定主鍵 ID',
    work_start_time        TIME        NOT NULL DEFAULT '09:00:00' COMMENT '標準上班時間',
    work_end_time          TIME        NOT NULL DEFAULT '18:00:00' COMMENT '標準下班時間',
    late_tolerance_minutes INTEGER     NOT NULL DEFAULT 10 COMMENT '遲到寬限分鐘數',
    lunch_break_minutes    INTEGER     NOT NULL DEFAULT 60 COMMENT '午休分鐘數',
    required_work_minutes  INTEGER     NOT NULL DEFAULT 480 COMMENT '每日應工作分鐘數',
    timezone               VARCHAR(50) NOT NULL DEFAULT 'Asia/Taipei' COMMENT '出勤規則適用時區',
    updated_at             DATETIME(6)          DEFAULT CURRENT_TIMESTAMP(6) COMMENT '設定最後更新時間'
) COMMENT='系統出勤規則設定';

-- Insert default configuration
INSERT INTO attendance_config (work_start_time, work_end_time, late_tolerance_minutes, lunch_break_minutes,
                               required_work_minutes, timezone)
VALUES ('09:00:00', '18:00:00', 10, 60, 480, 'Asia/Taipei');
