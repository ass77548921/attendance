## MODIFIED Requirements

### Requirement: 使用者登入與 JWT 發放
系統 SHALL 驗證使用者帳號密碼，驗證通過後發放 Access Token（15 分鐘有效）與 Refresh Token（7 天有效）。登入回應 SHALL 包含 `mustChangePassword` 旗標，若為 `true` 則前端 SHALL 強制導向密碼變更頁。

#### Scenario: 有效憑證登入
- **WHEN** 使用者提交正確的 username 與 password
- **THEN** 系統回傳 200 OK，含 accessToken（JWT）、refreshToken 及 `mustChangePassword: false`

#### Scenario: 錯誤密碼登入
- **WHEN** 使用者提交錯誤的 password
- **THEN** 系統回傳 401 Unauthorized，不揭露帳號是否存在

#### Scenario: 帳號不存在
- **WHEN** 使用者提交不存在的 username
- **THEN** 系統回傳 401 Unauthorized（與密碼錯誤回應一致，防止帳號枚舉）

#### Scenario: 登入後需要強制密碼強更
- **WHEN** 登入成功，但使用者 `mustChangePassword = true`（新帳號、管理員重設後）或密碼已超過 expiryDays 天未更新
- **THEN** 系統回傳 200 OK，含有效 tokens 及 `mustChangePassword: true`；前端 SHALL 導向 `/change-password` 頁
