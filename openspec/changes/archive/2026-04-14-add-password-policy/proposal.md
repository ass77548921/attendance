## Why

目前系統缺乏密碼安全機制，帳號建立後密碼永不過期、無複雜度要求、管理員重設密碼後也無強制變更流程。這導致弱密碼長期存在的風險，並不符合最基本的帳號安全標準。

## What Changes

- 新增「密碼規則設定」管理頁面，允許管理員設定密碼複雜度（長度、是否需要特殊字元/大小寫/數字）、密碼有效期（天數）、禁止重複使用最近 N 次密碼
- 新建帳號或管理員重設密碼後，帳號標記 `mustChangePassword = true`，該用戶下次登入時強制導向密碼變更頁
- 管理員重設員工密碼時，必須填寫備注說明（reason），記錄於 `PasswordResetLog`
- 每次密碼變更時檢查：新密碼不可與最近 N 次舊密碼相同（N 由規則設定）
- 登入時檢查密碼是否超過有效期，若已過期則強制導向密碼變更頁

## Capabilities

### New Capabilities
- `password-policy`: 管理密碼規則設定（複雜度、有效期、歷史不重複限制）
- `password-change-flow`: 強制密碼強更流程（首次登入、管理員重設後、密碼過期）
- `password-reset-log`: 管理員重設員工密碼的備注紀錄

### Modified Capabilities
- `user-auth`: 登入流程新增 `mustChangePassword` 檢查與密碼過期檢查，回傳中包含是否需要強更的旗標
- `admin-dashboard`: 員工列表新增「重設密碼」操作入口（取代目前的直接在編輯表單中改密碼），並要求填寫備注

## Impact

- **後端**：新增 `PasswordPolicy` entity、`PasswordResetLog` entity、`PasswordHistory` entity；`User` 新增 `mustChangePassword` 欄位與 `passwordChangedAt` 欄位；新增 `/api/admin/password-policy`、`/api/admin/users/{id}/reset-password`、`/api/user/change-password` endpoints
- **前端**：新增「密碼規則設定」頁（admin nav）；新增「強制密碼變更」頁（登入後跳轉）；登入 flow 需處理 `mustChangePassword` 旗標
- **資料庫**：3 張新 table；`users` table 新增 2 個欄位
- **相依**：密碼驗證邏輯需引用 `PasswordPolicy`，所有密碼變更入口（建立、重設、自行修改）皆需走同一驗證管道
