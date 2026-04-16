## ADDED Requirements

### Requirement: 強制登出當 Refresh Token 無效或不存在
當 Dio 攔截器偵測到 access token 過期（API 回傳 401）且無法透過 refresh token 取得新 token 時，系統 SHALL 呼叫 `AuthController.forceLogout()`，將認證狀態過渡至 `unauthenticated`，並攜帶逾時通知訊息，觸發 GoRouter 重導向至登入頁，不得讓用戶停留在當前畫面看到未處理的 API 錯誤。

#### Scenario: 無 Refresh Token 時觸發強制登出
- **WHEN** API 回傳 401，且 SecureStorage 中無 refresh token（null 或空字串）
- **THEN** 系統呼叫 `forceLogout()`，清除 SecureStorage 與記憶體 token，AuthState 轉為 `unauthenticated`，`sessionExpiredMessage` 設為「登入已逾時，請重新登入」

#### Scenario: Refresh Token 刷新失敗時觸發強制登出
- **WHEN** API 回傳 401，有 refresh token，但 `/api/auth/refresh` 請求回傳 401
- **THEN** 系統呼叫 `forceLogout()`，清除 SecureStorage 與記憶體 token，AuthState 轉為 `unauthenticated`，`sessionExpiredMessage` 設為「登入已逾時，請重新登入」

#### Scenario: 強制登出為冪等操作
- **WHEN** 系統已處於 `unauthenticated` 狀態時再次呼叫 `forceLogout()`
- **THEN** 不做任何狀態變更，直接 return，不觸發額外副作用

### Requirement: 登入頁顯示逾時通知訊息
當用戶因 session 逾時被重導向至登入頁時，系統 SHALL 顯示逾時通知訊息告知用戶，並在用戶看到後自動清除此訊息，避免重複顯示。

#### Scenario: 重導向至登入頁後顯示逾時訊息
- **WHEN** `AuthState.sessionExpiredMessage` 不為 null，且 LoginPage 被渲染
- **THEN** 登入頁顯示 SnackBar，內容為 `sessionExpiredMessage`

#### Scenario: 逾時訊息顯示後清除
- **WHEN** SnackBar 已顯示 `sessionExpiredMessage`
- **THEN** 呼叫 `AuthController.clearSessionExpiredMessage()`，將 `sessionExpiredMessage` 設回 null，不重複顯示
