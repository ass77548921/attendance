-- Default admin account
-- username: admin  password: Admin@1234 (BCrypt hash)
INSERT INTO users (username, password_hash, full_name, email, role, status)
VALUES ('admin',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQyCiBfH30Rr0VRhS9Q1q6Hm.',
        '系統管理員',
        'admin@attendance.local',
        'ADMIN',
        'ACTIVE');
