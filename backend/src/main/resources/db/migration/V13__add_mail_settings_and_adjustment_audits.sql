CREATE TABLE mail_settings
(
    id                      BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '郵件設定主鍵 ID',
    smtp_host               VARCHAR(255) NOT NULL COMMENT 'SMTP 主機',
    smtp_port               INT          NOT NULL COMMENT 'SMTP 連接埠',
    smtp_username           VARCHAR(255) NOT NULL COMMENT 'SMTP 帳號',
    smtp_password_encrypted VARCHAR(2048) NOT NULL COMMENT '加密後 SMTP 密碼',
    from_email              VARCHAR(255) NOT NULL COMMENT '寄件人 Email',
    from_name               VARCHAR(255) NOT NULL COMMENT '寄件顯示名稱',
    subject_prefix          VARCHAR(255) NOT NULL COMMENT '主旨前綴',
    enabled                 TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '是否啟用',
    updated_at              DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '最後更新時間'
) COMMENT='郵件發送設定';

CREATE TABLE attendance_adjustment_audits
(
    id                    BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '出勤修正審計主鍵 ID',
    attendance_record_id  BIGINT      NOT NULL COMMENT '對應出勤紀錄 ID',
    modified_by           BIGINT      NOT NULL COMMENT '修改人使用者 ID',
    reason                LONGTEXT    NOT NULL COMMENT '修改理由',
    before_clock_in_time  DATETIME(6) COMMENT '修改前上班時間',
    before_clock_out_time DATETIME(6) COMMENT '修改前下班時間',
    before_is_late        TINYINT(1)  NOT NULL COMMENT '修改前是否遲到',
    before_late_minutes   INT         NOT NULL COMMENT '修改前遲到分鐘數',
    before_is_early_leave TINYINT(1)  NOT NULL COMMENT '修改前是否早退',
    before_short_minutes  INT         NOT NULL COMMENT '修改前不足工時分鐘數',
    after_clock_in_time   DATETIME(6) COMMENT '修改後上班時間',
    after_clock_out_time  DATETIME(6) COMMENT '修改後下班時間',
    after_is_late         TINYINT(1)  NOT NULL COMMENT '修改後是否遲到',
    after_late_minutes    INT         NOT NULL COMMENT '修改後遲到分鐘數',
    after_is_early_leave  TINYINT(1)  NOT NULL COMMENT '修改後是否早退',
    after_short_minutes   INT         NOT NULL COMMENT '修改後不足工時分鐘數',
    modified_at           DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '修改時間',
    CONSTRAINT fk_adjustment_audit_attendance_record FOREIGN KEY (attendance_record_id) REFERENCES attendance_records (id) ON DELETE CASCADE,
    CONSTRAINT fk_adjustment_audit_modified_by FOREIGN KEY (modified_by) REFERENCES users (id)
) COMMENT='出勤人工修正審計紀錄';

CREATE INDEX idx_adjustment_audits_record_id ON attendance_adjustment_audits (attendance_record_id);
CREATE INDEX idx_adjustment_audits_modified_at ON attendance_adjustment_audits (modified_at);