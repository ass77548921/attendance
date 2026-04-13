-- V10: Fix schema discrepancies between Hibernate 6.4 entities and existing migrations
-- Hibernate 6.4 + MySQL requires ENUM SQL type for @Enumerated(EnumType.STRING) fields.
-- All VARCHAR columns mapped as @Enumerated are converted to ENUM here.
-- Also fixes notification_logs column naming and missing columns.

-- ── attendance_amendments ──────────────────────────────────────────────────
ALTER TABLE attendance_amendments
    MODIFY COLUMN amendment_type ENUM('CLOCK_IN', 'CLOCK_OUT') NOT NULL,
    MODIFY COLUMN status         ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING';

-- ── users ──────────────────────────────────────────────────────────────────
ALTER TABLE users
    MODIFY COLUMN role   ENUM('EMPLOYEE', 'ADMIN')  NOT NULL,
    MODIFY COLUMN status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE';

-- ── notification_logs ──────────────────────────────────────────────────────
-- Drop CHECK constraint on `result` before renaming (MySQL 8.0 Error 3959)
ALTER TABLE notification_logs DROP CHECK notification_logs_chk_1;

-- Rename `result` to `status` (entity field name is `status`)
-- Add missing columns: late_minutes, created_at
ALTER TABLE notification_logs
    CHANGE COLUMN result     status      ENUM('SUCCESS', 'FAILED') NOT NULL,
    ADD    COLUMN late_minutes INT        NOT NULL DEFAULT 0          AFTER work_date,
    ADD    COLUMN created_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6);
