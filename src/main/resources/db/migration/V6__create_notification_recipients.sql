CREATE TABLE notification_recipients
(
    id         BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '通知收件人主鍵 ID',
    name       VARCHAR(200) COMMENT '通知收件人名稱，若未提供可為空',
    email      VARCHAR(255) NOT NULL UNIQUE COMMENT '通知收件人電子郵件地址',
    active     TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '是否啟用通知寄送',
    created_at DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '收件人建立時間'
) COMMENT='遲到通知收件人設定';
