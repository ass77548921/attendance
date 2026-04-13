-- Fix admin password hash
-- username: admin  password: Admin@1234
UPDATE users
SET password_hash = '$2b$12$wx.YcYiU8/afSwHbl68iouQD4fq9orV.NUaTjrOANDoGac1In70bi'
WHERE username = 'admin';
