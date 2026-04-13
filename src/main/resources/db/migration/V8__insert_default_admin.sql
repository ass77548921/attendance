-- Default admin account
-- username: admin  password: Admin@1234 (BCrypt hash)
INSERT INTO users (username, password_hash, full_name, email, role, status)
VALUES ('admin',
        '$2b$12$wx.YcYiU8/afSwHbl68iouQD4fq9orV.NUaTjrOANDoGac1In70bi',
        '系統管理員',
        'admin@attendance.local',
        'ADMIN',
        'ACTIVE');
