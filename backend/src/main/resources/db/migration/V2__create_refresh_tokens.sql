CREATE TABLE refresh_tokens
(
    id         BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT 'Refresh token 主鍵 ID',
    user_id    BIGINT       NOT NULL COMMENT '對應的使用者 ID',
    token_hash VARCHAR(255) NOT NULL UNIQUE COMMENT 'Refresh token 雜湊值',
    expires_at DATETIME(6)  NOT NULL COMMENT 'Refresh token 過期時間',
    created_at DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Refresh token 建立時間',
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) COMMENT='使用者 Refresh Token 儲存資料';

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens (user_id);
CREATE INDEX idx_refresh_tokens_token_hash ON refresh_tokens (token_hash);
