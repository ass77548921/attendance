## 1. AuthController 擴充

- [x] 1.1 在 `AuthState` 新增 `sessionExpiredMessage` 欄位（`String?`，預設 null），並更新 `copyWith`
- [x] 1.2 在 `AuthController` 新增 `forceLogout()` 方法：若已是 `unauthenticated` 則直接 return（冪等保護）；否則清除 SecureStorage 所有 token、清除記憶體 token、設定 state 為 `unauthenticated` 並帶入 `sessionExpiredMessage: '登入已逾時，請重新登入'`
- [x] 1.3 在 `AuthController` 新增 `clearSessionExpiredMessage()` 方法：將 `sessionExpiredMessage` 設回 null（透過 `state = state.copyWith(sessionExpiredMessage: null)`）

## 2. Dio 攔截器修正

- [x] 2.1 修改 `dio_client.dart` 的 `onError`：在「無 refresh token → `storage.clear()`」後，補上 `ref.read(authProvider.notifier).forceLogout()`
- [x] 2.2 修改 `dio_client.dart` 的 `onError`：在「catch refresh 失敗 → `storage.clear()`」後，補上 `ref.read(authProvider.notifier).forceLogout()`
- [x] 2.3 確認 `ref.read(authProvider.notifier)` 在 `dioProvider` 的 Provider 建立時可正確取得（驗證無循環依賴，透過 Provider ref on-demand read 而非 watch-on-build）

## 3. 登入頁逾時通知

- [x] 3.1 在 `LoginPage` 的 `build` 或 `initState` 中監聽 `authProvider` 的 `sessionExpiredMessage`（用 `ref.listen` 或 `ConsumerStatefulWidget` 的 `didChangeDependencies`）
- [x] 3.2 當 `sessionExpiredMessage` 不為 null 時，呼叫 `ScaffoldMessenger.of(context).showSnackBar(...)` 顯示訊息
- [x] 3.3 顯示 SnackBar 後呼叫 `ref.read(authProvider.notifier).clearSessionExpiredMessage()` 清除訊息，避免重複顯示

## 4. 共用 Error 訊息工具

- [x] 4.1 新增 `flutter/lib/core/network/network_exception_utils.dart`，提取共用函數 `extractDioErrorMessage(DioException error, {required String fallback}) → String`
- [x] 4.2 更新 `AuthRepository` 移除本地 `_extractMessage`，改用 `extractDioErrorMessage`
- [x] 4.3 更新 `AttendanceRepository` 移除本地 `_extractMessage`，改用 `extractDioErrorMessage`
- [x] 4.4 更新 `AmendmentRepository` 移除本地 `_extractMessage`，改用 `extractDioErrorMessage`

## 5. 驗證與測試

- [x] 5.1 手動測試：使用 access token 仍有效時正常 API 呼叫（無回歸）
- [x] 5.2 手動測試：access token 過期但 refresh token 有效 → 自動 refresh 並重試，無感知
- [x] 5.3 手動測試：模擬 refresh token 過期（修改 Storage 中的 refresh token 為無效值）→ App 導向 `/login` 並顯示「登入已逾時，請重新登入」
- [x] 5.4 手動測試：模擬 Storage 中無 refresh token →（刪除 SecureStorage 中 refresh_token key）→ API 呼叫後導向 `/login` 並顯示逾時訊息
- [x] 5.5 手動測試：正常登出流程不受影響（仍呼叫後端 logout API，不顯示逾時訊息）
- [x] 5.6 手動測試：同時發出兩個 API 請求且兩者都收到 401（refresh 失敗）→ `forceLogout()` 被呼叫兩次，但狀態正確（冪等），SnackBar 只顯示一次
