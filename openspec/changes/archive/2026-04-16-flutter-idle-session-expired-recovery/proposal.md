## Why

Flutter 前台在用戶長時間未操作後，access token（15 分鐘到期）會過期。當用戶返回 App 並觸發 API 呼叫時，攔截器雖已有 refresh token 重試邏輯，但存在一個關鍵缺陷：refresh 失敗後只清除了 `SecureStorage`，卻未通知 `AuthController` 更新狀態。這導致 `authProvider` 狀態仍為 `authenticated`，GoRouter 不觸發重導向，用戶停留在當前畫面並看到 API 呼叫錯誤，而非被引導重新登入。

## What Changes

- **修正 `dio_client.dart` 攔截器**：refresh 失敗（或無 refresh token）後，除清除 Storage 外，同步呼叫 `authProvider.notifier.logout()` 讓狀態機正確轉入 `unauthenticated`，觸發 GoRouter 自動重導向至登入頁。
- **修正 `AuthController`**：在記憶體中 token 已存在、但 Storage 已被清除的情況下，加入一致性保護，避免 stale in-memory token 被繼續使用。
- **加入 `DioProvider` 與 `AuthProvider` 的循環依賴解法**：dio interceptor 需引用 authProvider，透過 `ProviderContainer` ref 傳遞而非建構期注入，避免 Riverpod 循環依賴。
- **統一 error 訊息擷取邏輯**：把三個 Repository 重複的 `_extractMessage` 提取為共用工具函數，減少重複代碼（附帶改善，範圍不擴大）。

## Capabilities

### New Capabilities

- `flutter-session-expired-auto-logout`: 當 access token 過期且 refresh token 無法刷新時，App 自動清除登入狀態並導向登入頁，而非停留在當前畫面並顯示 API 錯誤。

### Modified Capabilities

- `flutter-auth-flow`: 認證流程加入「強制登出」觸發路徑——由 Dio 攔截器呼叫 auth state，修改 auth flow 的狀態轉換規格（新增 `refresh-failed → unauthenticated` 路徑）。

## Impact

- **修改檔案**：
  - `flutter/lib/core/network/dio_client.dart`（攔截器邏輯）
  - `flutter/lib/features/auth/state/auth_provider.dart`（`AuthController`）
  - `flutter/lib/core/network/auth_storage.dart`（可能加入一致性方法）
- **新增檔案**：
  - `flutter/lib/core/network/network_exception_utils.dart`（共用 error 訊息擷取）
- **受影響 API**：無後端 API 變更，純前端修復
- **受影響功能頁面**：所有需登入的頁面（`AttendanceHomePage`、`AttendanceRecordsPage`、`AmendmentListPage`、`AmendmentFormPage`）
- **依賴關係**：`dio_client.dart` 需在 Riverpod 架構下安全引用 `authProvider`
