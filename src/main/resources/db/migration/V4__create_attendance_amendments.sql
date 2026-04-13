CREATE TABLE attendance_amendments
(
    id             BIGINT                              NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '補卡申請主鍵 ID',
    user_id        BIGINT                              NOT NULL COMMENT '提出補卡申請的使用者 ID',
    target_date    DATE                                NOT NULL COMMENT '申請補卡的目標日期',
    amendment_type ENUM('CLOCK_IN', 'CLOCK_OUT')       NOT NULL COMMENT '補卡類型，表示補上班或下班卡',
    amended_time   DATETIME(6)                         NOT NULL COMMENT '申請補登的打卡時間',
    reason         LONGTEXT                            NOT NULL COMMENT '補卡原因說明',
    status         ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING' COMMENT '補卡申請審核狀態',
    review_note    LONGTEXT COMMENT '審核備註內容',
    reviewed_by    BIGINT COMMENT '審核人員的使用者 ID',
    reviewed_at    DATETIME(6) COMMENT '審核完成時間',
    created_at     DATETIME(6)                         NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '申請建立時間',
    CONSTRAINT fk_amendments_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_amendments_reviewer FOREIGN KEY (reviewed_by) REFERENCES users (id)
) COMMENT='員工補打卡申請與審核資料';

CREATE INDEX idx_amendments_user_id ON attendance_amendments (user_id);
CREATE INDEX idx_amendments_status ON attendance_amendments (status);
CREATE INDEX idx_amendments_target_date ON attendance_amendments (target_date);
