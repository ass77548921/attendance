-- Task 1.1: Add password fields to users table
ALTER TABLE users
    ADD COLUMN must_change_password BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN password_changed_at  TIMESTAMP NULL;

-- Existing accounts: set password_changed_at to created_at, must_change_password stays FALSE
UPDATE users SET password_changed_at = created_at WHERE password_changed_at IS NULL;

-- Task 1.2: Password policy (singleton, id=1)
CREATE TABLE password_policy (
    id                   BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    min_length           INT          NOT NULL DEFAULT 8,
    require_uppercase    BOOLEAN      NOT NULL DEFAULT TRUE,
    require_lowercase    BOOLEAN      NOT NULL DEFAULT TRUE,
    require_number       BOOLEAN      NOT NULL DEFAULT TRUE,
    require_special_char BOOLEAN      NOT NULL DEFAULT FALSE,
    expiry_days          INT          NOT NULL DEFAULT 0,
    history_count        INT          NOT NULL DEFAULT 5
);

INSERT INTO password_policy (id, min_length, require_uppercase, require_lowercase, require_number, require_special_char, expiry_days, history_count)
VALUES (1, 8, TRUE, TRUE, TRUE, FALSE, 0, 5);

-- Task 1.3: Password history
CREATE TABLE password_history (
    id            BIGINT    NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT    NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ph_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Task 1.4: Password reset logs
CREATE TABLE password_reset_logs (
    id                BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    target_user_id    BIGINT       NOT NULL,
    reset_by_user_id  BIGINT       NOT NULL,
    reason            VARCHAR(500) NOT NULL,
    reset_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_prl_target FOREIGN KEY (target_user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_prl_admin  FOREIGN KEY (reset_by_user_id) REFERENCES users(id)
);
