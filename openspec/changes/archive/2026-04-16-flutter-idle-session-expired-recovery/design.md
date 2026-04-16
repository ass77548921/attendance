## Context

Flutter App 使用 Dio + Riverpod 架構。Access Token TTL 為 15 分鐘，Refresh Token TTL 為 7 天。目前 `dio_client.dart` 的 `onError` 攔截器已有 refresh 重試邏輯，但有以下缺陷：

1. **Refresh 失敗後未同步 AuthController 狀態**：`storage.clear()` 只清除 `FlutterSecureStorage`，但 `authProvider`（Riverpod `StateNotifier`）的記憶體狀態仍停留在 `authenticated`，導致 GoRouter redirect 不觸發，用戶停留當前畫面並看到 API 錯誤。
2. **AuthController 持有 stale in-memory token**：`AuthController` 把 `accessToken`/`refreshToken` 存在 `AuthState` 物件中，DioInterceptor 清除 Storage 後，記憶體版本仍有舊 token，下次請求仍會附帶過期 token（但攔截器會再次 catch 401 — 仍不正確地處理）。
3. **DioProvider 與 AuthProvider 的依賴關係**：`dioProvider` 需要存取 `authProvider.notifier` 呼叫 logout，但 `authProvider` 也可能依賴 `dioProvider`（例如 change-password），存在潛在循環依賴風險。

**後端行為（確認事實）**：
- Access token 過期 → HTTP 401, 空 body
- Refresh token 過期 → HTTP 401, `{"message": "Refresh token expired"}`
- Refresh token 不存在 → HTTP 401, `{"message": "Invalid refresh token"}`
- 登出成功 → revoke 所有 refresh token

## Goals / Non-Goals

**Goals:**
- Refresh 失敗後正確過渡到 `unauthenticated` 狀態，觸發 GoRouter 重導向至登入頁
- 清除 Storage 的同時也清除 AuthController 的記憶體狀態
- 無 refresh token 時（Storage 為空）也觸發同樣的強制登出流程
- 登入頁顯示「登入已逾時，請重新登入」訊息，告知使用者發生什麼事
- 解決 DioProvider ↔ AuthProvider 循環依賴問題

**Non-Goals:**
- 不加入前端 idle 計時器（後端本身是 stateless，依靠 token TTL 控制即可）
- 不修改後端 API
- 不修改 token 存儲加密方式
- 不實作 refresh token rotation 的同步鎖（multi-tab concurrent refresh — 超出本次範圍）

## Decisions

### Decision 1：透過 `ProviderRef` 傳遞解決循環依賴

**問題**：`dioProvider` 是 `Provider<Dio>`，在建立時若直接 `ref.watch(authProvider.notifier)` 會產生循環依賴（authProvider → dioProvider → authProvider）。  
**方案**：`dioProvider` 只存取 `authStorageProvider`（無依賴循環），並在 interceptor 的 `onError` callback 裡呼叫 `ref.read(authProvider.notifier).forceLogout()`。Riverpod `Provider` 傳入 ref，可在 callback 內 `ref.read`，無循環問題（read-on-demand vs watch-on-build 的差別）。  
**替代方案**：建一個獨立的 `SessionExpiredNotifier`，dioProvider 通知它，authProvider 監聽它。這樣更鬆耦合但多一層間接，本次選擇直接 `ref.read` 保持簡單。

### Decision 2：在 `AuthController` 加入 `forceLogout()` 方法

**問題**：原 `logout()` 方法會呼叫 `POST /api/auth/logout` API（需要網路），但 refresh 失敗情境恰好是網路/token 出問題，不應再發 API 請求。  
**方案**：新增 `forceLogout()` 方法，只做：清除 SecureStorage + 更新 AuthState 為 `unauthenticated` + 清除記憶體中的 token。原 `logout()` 保持不變（正常登出流程）。  
**替代方案**：在 `logout()` 加一個 `bool callApi` 參數。選擇分開方法更語義清晰，符合單一職責。

### Decision 3：「逾時訊息」透過 `AuthState` 攜帶

**問題**：需要在登入頁顯示「登入已逾時，請重新登入」，但 `forceLogout()` 是在 Dio interceptor 中呼叫，與 UI 層是非同步的。  
**方案**：在 `AuthState` 加入 `sessionExpiredMessage` 欄位（`String?`），`forceLogout()` 設定此欄位，`LoginPage` 在 build 時讀取並透過 `SnackBar` 或 banner 顯示，顯示後清除此欄位。  
**替代方案**：用全域 event bus（stream）。Stream 更複雜，State 欄位更符合 Riverpod 模式。

## Risks / Trade-offs

- **[風險] concurrent 401 race condition** → 兩個 API 同時失敗各自 trigger refresh，第二個 refresh 會因第一個已 rotate 而失敗，再觸發 `forceLogout()`。這是可接受行為（最終結果正確：登出），但可能聽到兩次 logout 呼叫。緩解：`forceLogout()` 加上 guard：若已是 `unauthenticated` 則直接 return。
- **[風險] AuthController 狀態機漏洞** → `forceLogout()` 在 `initial` 狀態被呼叫。緩解：同上 guard。
- **[取捨] 不加 refresh mutex lock** → 多個同時 401 各自觸發 refresh，可能呼叫兩次 refresh API。短期可接受（後端允許多個有效 refresh token 並存），但日後升級為 token rotation 需補上 mutex。

## Migration Plan

1. 新增 `forceLogout()` 到 `AuthController`，更新 `AuthState` 加入 `sessionExpiredMessage`
2. 修改 `dio_client.dart` interceptor，在 `storage.clear()` 後呼叫 `ref.read(authProvider.notifier).forceLogout()`
3. 修改 `LoginPage`，偵測 `sessionExpiredMessage` 並顯示 SnackBar/banner
4. 提取共用 `extractDioErrorMessage()` 工具函數，更新三個 Repository 使用它
5. 回歸測試全部登入/登出/token refresh 流程

**Rollback**：純前端改動，無資料庫異動。回滾只需 revert dart 檔案即可。

## Open Questions

- 逾時訊息的 UI 呈現形式：SnackBar vs Banner vs inline text？（建議 SnackBar，與現有登出後導向一致）
- 是否需要同步更新 `AuthController` 的 `loadSession()` 在 App 前景化時重新驗證 token 有效性？（本次不含，可作後續 improvement）
