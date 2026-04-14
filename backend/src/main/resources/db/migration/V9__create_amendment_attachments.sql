CREATE TABLE amendment_attachments
(
    id                BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '附件主鍵 ID',
    amendment_id      BIGINT       NOT NULL COMMENT '所屬補卡申請 ID',
    original_filename VARCHAR(255) NOT NULL COMMENT '使用者上傳的原始檔名',
    stored_path       VARCHAR(500) NOT NULL COMMENT '檔案在儲存系統中的路徑',
    mime_type         VARCHAR(100) NOT NULL COMMENT '附件 MIME 類型',
    file_size         BIGINT       NOT NULL COMMENT '附件檔案大小，以位元組為單位',
    created_at        DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '附件建立時間',
    CONSTRAINT fk_amendment_attachments FOREIGN KEY (amendment_id) REFERENCES attendance_amendments (id) ON DELETE CASCADE
) COMMENT='補卡申請附件資料';

CREATE INDEX idx_amendment_attachments_amendment_id ON amendment_attachments (amendment_id);
