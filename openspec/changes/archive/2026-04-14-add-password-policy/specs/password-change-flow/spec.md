## ADDED Requirements

### Requirement: 強制密碼強更流程
系統 SHALL 在特定條件下強制使用者於下次登入時變更密碼，並阻止該使用者在完成變更前存取其他功能。觸發條件：(1) 帳號首次建立，(2) 管理員重設密碼後，(3) 密碼已超過 PasswordPolicy.expiryDays 天未更新（0=永不過期，不觸發）。

#### Scenario: 新帳號首次登入觸發強更
- **WHEN** 管理員建立新帳號後，該使用者首次登入成功
- **THEN** 系統在 TokenResponse 中回傳 `mustChangePassword: true`，前端 SHALL 導向 `/change-password` 頁

#### Scenario: 管理員重設密碼後登入觸發強更
- **WHEN** 管理員重設某員工密碼後，該員工使用新密碼登入成功
- **THEN** 系統在 TokenResponse 中回傳 `mustChangePassword: true`，前端 SHALL 導向 `/change-password` 頁

#### Scenario: 密碼過期後登入觸發強更
- **WHEN** 使用者登入成功，但 `passwordChangedAt` 距今已超過 `expiryDays` 天（且 expiryDays > 0）
- **THEN** 系統在 TokenResponse 中回傳 `mustChangePassword: true`，前端 SHALL 導向 `/change-password` 頁

#### Scenario: 強更頁面路由守衛
- **WHEN** `mustChangePassword` 為 true 的使用者嘗試導向 `/change-password` 以外的任何受保護頁面
- **THEN** 前端 SHALL 自動重定向至 `/change-password`，直到強更完成

### Requirement: 密碼強更操作
系統 SHALL 提供 `POST /api/user/change-password` endpoint，供已登入但 `mustChangePassword = true` 的使用者完成密碼變更。

#### Scenario: 成功完成密碼強更
- **WHEN** 使用者提交符合 PasswordPolicy 規則的新密碼（且與舊密碼不同、不在歷史禁止清單中）
- **THEN** 系統更新密碼、設 `mustChangePassword = false`、更新 `passwordChangedAt`，回傳 200 OK；前端清除強更旗標並導向首頁

#### Scenario: 新密碼不符規則
- **WHEN** 使用者提交不符合 PasswordPolicy 的密碼
- **THEN** 系統回傳 400 Bad Request，說明不符合的規則，強更頁面顯示錯誤訊息

#### Scenario: 新舊密碼相同
- **WHEN** 使用者提交與現有密碼相同的新密碼
- **THEN** 系統回傳 400 Bad Request，提示「新密碼不可與目前密碼相同」

#### Scenario: 未登入使用者存取強更 endpoint
- **WHEN** 無效或缺少 Access Token 的請求呼叫 change-password API
- **THEN** 系統回傳 401 Unauthorized
