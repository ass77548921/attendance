CREATE TABLE amendment_attachments
(
    id                BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    amendment_id      BIGINT       NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    stored_path       VARCHAR(500) NOT NULL,
    mime_type         VARCHAR(100) NOT NULL,
    file_size         BIGINT       NOT NULL,
    created_at        DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_amendment_attachments FOREIGN KEY (amendment_id) REFERENCES attendance_amendments (id) ON DELETE CASCADE
);

CREATE INDEX idx_amendment_attachments_amendment_id ON amendment_attachments (amendment_id);
