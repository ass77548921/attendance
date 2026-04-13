CREATE TABLE users
(
    id            BIGINT                     NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '使用者主鍵 ID',
    username      VARCHAR(100)               NOT NULL UNIQUE COMMENT '登入帳號名稱',
    password_hash VARCHAR(255)               NOT NULL COMMENT 'BCrypt 密碼雜湊值',
    full_name     VARCHAR(200)               NOT NULL COMMENT '使用者顯示姓名',
    email         VARCHAR(255)               NOT NULL UNIQUE COMMENT '使用者電子郵件地址',
    role          ENUM('EMPLOYEE', 'ADMIN')  NOT NULL COMMENT '使用者角色類型',
    status        ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '帳號啟用狀態',
    created_at    DATETIME(6)                NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '帳號建立時間'
) COMMENT='系統使用者帳號資料';

CREATE INDEX idx_users_username ON users (username);
CREATE INDEX idx_users_email ON users (email);
