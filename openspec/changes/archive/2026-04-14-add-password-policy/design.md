## Context

系統目前以 Spring Boot + MySQL + React/Vite 實作。`User` entity 儲存在 `users` table，密碼用 BCrypt 雜湊存於 `password_hash` 欄位。登入由 `AuthController.login()` 呼叫 `AuthenticationManager`，成功後回傳 JWT `accessToken` + `refreshToken`。目前無密碼複雜度驗證、密碼過期機制、強制密碼強更流程。

## Goals / Non-Goals

**Goals:**
- `PasswordPolicy` 設定：複雜度規則（最小長度、需大寫/小寫/數字/特殊字元）、密碼有效天數（0 = 永不過期）、禁止重複使用最近 N 次密碼（0 = 不限）
- `User` 增加 `mustChangePassword` 旗標與 `passwordChangedAt` 欄位
- 登入時回傳 `mustChangePassword: true` 旗標，前端導向強制密碼變更頁
- 登入時若密碼超過有效期，同樣觸發強制密碼強更
- 管理員重設員工密碼（獨立 endpoint），必須填寫 `reason`，記錄於 `password_reset_logs`
- 密碼歷史紀錄存於 `password_history`，阻止重複使用
- 前端新增「密碼規則設定」管理頁與「強制密碼變更」頁

**Non-Goals:**
- 密碼雜湊演算法升級（保持 BCrypt）
- 多因素驗證（MFA）
- 員工可自行查看密碼規則詳情頁（規則僅在後台管理）
- Email 通知密碼即將到期

## Decisions

### D1：PasswordPolicy 以單例 table 儲存
`password_policy` table 永遠只有一筆 (`id=1`)，透過 `findById(1)` 或 `findFirst()` 存取，不設多筆規則。理由：本系統為單一組織內部系統，全域一套即可；如未來需要按角色差異化再擴充。

### D2：`mustChangePassword` 掛在 `User`，不用獨立 table
將 `must_change_password BOOLEAN DEFAULT FALSE` 加到 `users` table。優點：join 少、查詢簡單；缺點：若未來擴充多種強更原因需要 nullable enum，但目前 boolean 已足夠。

### D3：登入回應加 `mustChangePassword` 旗標
`TokenResponse` 新增 `mustChangePassword: boolean`。前端在 `/api/auth/login` 成功後若此旗標為 `true`，立即導向 `/change-password` 強制變更頁。此頁使用原 JWT 操作（已登入狀態），不需獨立 token。

替代方案：回傳 403 帶特殊 error code → 前端判斷導向。決定不採用，因為 403 語義上是「禁止存取」，但用戶已通過認證，用 200 + 旗標更清楚。

### D4：強制變更頁以 React route guard 防護
前端在 `ProtectedRoute` 中，若 `mustChangePassword === true`（存於 localStorage 或 auth state），則所有路由導向 `/change-password`，防止跳過。`/change-password` 成功後清除旗標並導向原目標。

### D5：管理員重設密碼獨立 endpoint
`POST /api/admin/users/{id}/reset-password`（body: `newPassword`, `reason`），而非複用 `PUT /api/admin/users/{id}`（現有的 EditUserModal）。理由：
- 重設密碼是高風險操作，應從一般資料編輯分離
- 需要 `reason` 欄位並寫入日誌
- 前端 EditUserModal 改為不包含密碼欄位，另開「重設密碼」Modal

### D6：密碼格式驗證邏輯集中在 `PasswordPolicyService`
無論是建立帳號、自行修改密碼、管理員重設密碼，密碼驗證皆呼叫 `PasswordPolicyService.validate(rawPassword)`，確保一致。Bean Validation `@Size(min=8)` 仍保留作第一道 syntax check，但業務規則（特殊字元等）改由 service 層執行。

### D7：密碼歷史只存 BCrypt hash
`password_history(id, user_id, password_hash, created_at)` — 每次變更密碼後 insert 一筆。比對時用 `passwordEncoder.matches()` 逐一比對最近 N 筆（N 由 policy 設定）。最多保留 24 筆（足夠任何合理的 N 值）。

## Risks / Trade-offs

- [風險] 歷史密碼比對需逐筆 BCrypt.matches()，N=10 時有輕微效能影響 → 緩解：BCrypt 比對約 50-100ms，N 一般 ≤ 10，不在 hot path，可接受
- [風險] 現有帳號 `mustChangePassword = false`、`passwordChangedAt = NULL`（無有效期） → 緩解：Flyway migration 設 `password_changed_at = created_at` 作為預設值；`NULL` 視為不觸發過期
- [風險] 前端強制導向邏輯若 state 遺失（頁面重整），用戶可能跳過 → 緩解：每次受保護 API 呼叫若後端回傳 `403 PASSWORD_CHANGE_REQUIRED`，前端仍可補正；或在 token claims 中埋入 `mustChangePassword`（本期不做，列為 open question）
- [Trade-off] 重設密碼從 EditUserModal 分離會增加一個 Modal → 好處：職責清晰、高風險操作獨立審計

## Migration Plan

1. Flyway 新增 migration：建立 `password_policy`、`password_history`、`password_reset_logs` table；`users` table add column `must_change_password`、`password_changed_at`
2. 預設 PasswordPolicy 資料 insert（`id=1`，合理預設值：min_length=8, require_uppercase=true, require_number=true, require_special=false, expiry_days=0, history_count=5）
3. 部署後端後新 endpoints 上線；前端部署時 `/change-password` route 加入，LoginPage 處理 `mustChangePassword` 旗標

## Open Questions

- `mustChangePassword` 是否應埋入 JWT claims，讓每次 API request 後端均可感知進行強制導向？（目前僅登入時設旗標 → 前端守衛）
- 密碼過期時，用戶正在使用中（已登入），是否需要 session 中斷？目前設計是下次登入觸發，在線不受影響。
