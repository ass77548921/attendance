CREATE TABLE notification_logs
(
    id              BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    recipient_email VARCHAR(255) NOT NULL,
    user_id         BIGINT,
    work_date       DATE         NOT NULL,
    result          VARCHAR(20)  NOT NULL CHECK (result IN ('SUCCESS', 'FAILED')),
    error_message   LONGTEXT,
    sent_at         DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_notification_logs_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_notification_logs_work_date ON notification_logs (work_date);
CREATE INDEX idx_notification_logs_user_id ON notification_logs (user_id);
