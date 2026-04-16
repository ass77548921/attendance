## ADDED Requirements

### Requirement: 登入頁面
系統 SHALL 提供登入頁面，包含使用者名稱與密碼輸入欄位及登入按鈕。登入成功後 SHALL 依 `mustChangePassword` 旗標決定導向打卡主頁或強制改密頁。

#### Scenario: 有效憑證登入並導向主頁
- **WHEN** 使用者輸入正確 username / password，且 `mustChangePassword = false`
- **THEN** 應用程式儲存 Access Token 與 Refresh Token，導向 `/attendance`

#### Scenario: 有效憑證登入但需強制改密
- **WHEN** 使用者輸入正確憑證，且 API 回傳 `mustChangePassword = true`
- **THEN** 應用程式導向 `/change-password`，不允許跳過

#### Scenario: 憑證錯誤顯示錯誤訊息
- **WHEN** API 回傳 401
- **THEN** 頁面顯示「帳號或密碼錯誤」提示訊息，不清空密碼欄位

#### Scenario: 登入中顯示 Loading 狀態
- **WHEN** 登入請求進行中
- **THEN** 登入按鈕顯示 CircularProgressIndicator，無法再次點擊

---

### Requirement: JWT Token 管理
系統 SHALL 將 Access Token 與 Refresh Token 以 `flutter_secure_storage` 安全儲存。Access Token 過期時，系統 SHALL 自動以 Refresh Token 取得新 Access Token，對使用者透明。

#### Scenario: API 請求自動附帶 Token
- **WHEN** 呼叫任何需認證的 API
- **THEN** HTTP Header 自動包含 `Authorization: Bearer <accessToken>`

#### Scenario: Access Token 過期自動刷新
- **WHEN** API 回傳 401，且本地存有有效 Refresh Token
- **THEN** 系統自動呼叫刷新 API，取得新 Access Token 後重試原請求，使用者無感知

#### Scenario: Refresh Token 亦過期時登出
- **WHEN** 刷新 Token 請求回傳 401
- **THEN** 清除本地所有 Token，導向 `/login` 並顯示「登入逾時，請重新登入」

---

### Requirement: 登出
系統 SHALL 提供登出功能，呼叫後端登出 API 撤銷 Refresh Token，並清除本地端存儲的 Token，導向登入頁。

#### Scenario: 登出成功
- **WHEN** 使用者點擊登出
- **THEN** 呼叫 `POST /api/auth/logout`，清除 Secure Storage 中所有 Token，導向 `/login`

#### Scenario: 登出時 API 失敗仍清除本地 Token
- **WHEN** 登出 API 請求失敗（網路問題）
- **THEN** 仍清除本地 Token，導向 `/login`，不阻塞使用者

---

### Requirement: 強制改密頁
系統 SHALL 提供強制改密頁面，要求使用者輸入新密碼（含確認密碼），送出後若成功則導向打卡主頁。

#### Scenario: 成功送出新密碼
- **WHEN** 使用者於強制改密頁輸入符合密碼政策的新密碼並確認
- **THEN** 呼叫改密 API，成功後導向 `/attendance`

#### Scenario: 兩次密碼不一致時顯示錯誤
- **WHEN** 確認密碼與新密碼不符
- **THEN** 即時顯示驗證錯誤「兩次密碼輸入不一致」，禁止送出

#### Scenario: 強制改密頁無法跳過
- **WHEN** `mustChangePassword = true` 時使用者嘗試存取其他路由
- **THEN** GoRouter redirect 回 `/change-password`

---

### Requirement: 受保護路由守衛
系統 SHALL 在路由層級確保未登入使用者無法存取受保護頁面。

#### Scenario: Token 存在時通過守衛
- **WHEN** 使用者存取 `/attendance`，且 Secure Storage 中有有效 Access Token
- **THEN** 正常顯示打卡主頁

#### Scenario: 無 Token 時重定向登入頁
- **WHEN** 使用者存取 `/attendance`，Secure Storage 中無 Token
- **THEN** GoRouter redirect 至 `/login`
