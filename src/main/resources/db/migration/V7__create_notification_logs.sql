CREATE TABLE notification_logs
(
    id              BIGINT                  NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '通知紀錄主鍵 ID',
    recipient_email VARCHAR(255)            NOT NULL COMMENT '通知寄送目標電子郵件地址',
    user_id         BIGINT COMMENT '對應的員工使用者 ID',
    work_date       DATE                    NOT NULL COMMENT '通知所屬工作日期',
    late_minutes    INT                     NOT NULL DEFAULT 0 COMMENT '本次通知對應的遲到分鐘數',
    status          ENUM('SUCCESS', 'FAILED') NOT NULL COMMENT '通知發送結果狀態',
    error_message   LONGTEXT COMMENT '發送失敗時的錯誤訊息',
    sent_at         DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '通知實際寄送時間',
    created_at      DATETIME(6)             NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '通知紀錄建立時間',
    CONSTRAINT fk_notification_logs_user FOREIGN KEY (user_id) REFERENCES users (id)
) COMMENT='遲到通知寄送結果紀錄';

CREATE INDEX idx_notification_logs_work_date ON notification_logs (work_date);
CREATE INDEX idx_notification_logs_user_id ON notification_logs (user_id);
