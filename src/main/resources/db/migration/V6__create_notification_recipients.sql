CREATE TABLE notification_recipients
(
    id         BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(200) NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE,
    active     TINYINT(1)   NOT NULL DEFAULT 1,
    created_at DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
);
