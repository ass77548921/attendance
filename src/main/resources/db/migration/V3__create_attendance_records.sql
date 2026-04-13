CREATE TABLE attendance_records
(
    id                BIGINT     NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '打卡紀錄主鍵 ID',
    user_id           BIGINT     NOT NULL COMMENT '對應的使用者 ID',
    work_date         DATE       NOT NULL COMMENT '出勤所屬工作日期',
    clock_in_time     DATETIME(6) COMMENT '上班打卡時間',
    clock_out_time    DATETIME(6) COMMENT '下班打卡時間',
    is_late           TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否遲到',
    late_minutes      INTEGER    NOT NULL DEFAULT 0 COMMENT '遲到分鐘數',
    is_early_leave    TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否早退',
    short_minutes     INTEGER    NOT NULL DEFAULT 0 COMMENT '早退分鐘數',
    notification_sent TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已寄送遲到通知',
    created_at        DATETIME(6)         DEFAULT CURRENT_TIMESTAMP(6) COMMENT '紀錄建立時間',
    updated_at        DATETIME(6)         DEFAULT CURRENT_TIMESTAMP(6) COMMENT '紀錄最後更新時間',
    CONSTRAINT fk_attendance_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    UNIQUE (user_id, work_date)
) COMMENT='員工每日出勤打卡紀錄';

CREATE INDEX idx_attendance_user_date ON attendance_records (user_id, work_date);
CREATE INDEX idx_attendance_is_late ON attendance_records (is_late, work_date);
