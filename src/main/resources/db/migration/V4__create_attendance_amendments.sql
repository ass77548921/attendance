CREATE TABLE attendance_amendments
(
    id             BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id        BIGINT      NOT NULL,
    target_date    DATE        NOT NULL,
    amendment_type VARCHAR(20) NOT NULL CHECK (amendment_type IN ('CLOCK_IN', 'CLOCK_OUT')),
    amended_time   DATETIME(6) NOT NULL,
    reason         LONGTEXT    NOT NULL,
    status         VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    review_note    LONGTEXT,
    reviewed_by    BIGINT,
    reviewed_at    DATETIME(6),
    created_at     DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_amendments_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_amendments_reviewer FOREIGN KEY (reviewed_by) REFERENCES users (id)
);

CREATE INDEX idx_amendments_user_id ON attendance_amendments (user_id);
CREATE INDEX idx_amendments_status ON attendance_amendments (status);
CREATE INDEX idx_amendments_target_date ON attendance_amendments (target_date);
