## MODIFIED Requirements

### Requirement: JWT Token 管理
系統 SHALL 將 Access Token 與 Refresh Token 以 `flutter_secure_storage` 安全儲存。Access Token 過期時，系統 SHALL 自動以 Refresh Token 取得新 Access Token，對使用者透明。當 Refresh Token 失效或不存在時，系統 SHALL 清除所有本地 Token 並同步更新 `AuthController` 的記憶體狀態為 `unauthenticated`，不得僅清除 Storage 而不更新狀態機。

#### Scenario: API 請求自動附帶 Token
- **WHEN** 呼叫任何需認證的 API
- **THEN** HTTP Header 自動包含 `Authorization: Bearer <accessToken>`

#### Scenario: Access Token 過期自動刷新
- **WHEN** API 回傳 401，且本地存有有效 Refresh Token
- **THEN** 系統自動呼叫刷新 API，取得新 Access Token 後重試原請求，使用者無感知

#### Scenario: Refresh Token 亦過期時強制登出並通知
- **WHEN** 刷新 Token 請求回傳 401
- **THEN** 呼叫 `AuthController.forceLogout()`，同時清除 SecureStorage 與記憶體 token，AuthState 轉為 `unauthenticated` 並攜帶 `sessionExpiredMessage`，GoRouter 重導向至 `/login`，登入頁顯示「登入已逾時，請重新登入」

#### Scenario: 無 Refresh Token 時強制登出並通知
- **WHEN** API 回傳 401，且本地無 Refresh Token（null 或空字串）
- **THEN** 呼叫 `AuthController.forceLogout()`，同時清除 SecureStorage 與記憶體 token，AuthState 轉為 `unauthenticated` 並攜帶 `sessionExpiredMessage`，GoRouter 重導向至 `/login`，登入頁顯示「登入已逾時，請重新登入」
