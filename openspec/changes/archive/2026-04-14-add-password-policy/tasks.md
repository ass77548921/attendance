## 1. 資料庫 Migration

- [x] 1.1 建立 Flyway migration：在 `users` table 加入 `must_change_password BOOLEAN NOT NULL DEFAULT TRUE` 與 `password_changed_at TIMESTAMP NULL` 欄位；現有帳號 `password_changed_at` 設為 `created_at`，`must_change_password` 設為 `FALSE`
- [x] 1.2 建立 `password_policy` table：`id`, `min_length`, `require_uppercase`, `require_lowercase`, `require_number`, `require_special_char`, `expiry_days`, `history_count`；insert 預設資料（id=1）
- [x] 1.3 建立 `password_history` table：`id`, `user_id`, `password_hash`, `created_at`（FK: user_id → users.id）
- [x] 1.4 建立 `password_reset_logs` table：`id`, `target_user_id`, `reset_by_user_id`, `reason`, `reset_at`（FK: 兩個 user_id → users.id）

## 2. 後端 Domain & Repository

- [x] 2.1 建立 `PasswordPolicy` entity（對應 `password_policy` table），加上 `@Table` 與欄位映射
- [x] 2.2 建立 `PasswordHistory` entity（對應 `password_history` table）
- [x] 2.3 建立 `PasswordResetLog` entity（對應 `password_reset_logs` table）
- [x] 2.4 在 `User` entity 新增 `mustChangePassword` 與 `passwordChangedAt` 欄位
- [x] 2.5 建立 `PasswordPolicyRepository`、`PasswordHistoryRepository`、`PasswordResetLogRepository`
- [x] 2.6 在 `PasswordHistoryRepository` 新增 `findTop{n}ByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable)` 查詢方法

## 3. 後端 PasswordPolicyService

- [x] 3.1 建立 `PasswordPolicyService`，實作 `getPolicy()` 取得（或初始化）單例規則
- [x] 3.2 實作 `validate(String rawPassword, Long userId)` 方法：依序檢查長度、複雜度字元、歷史密碼（BCrypt matches），任一不符拋出 `PolicyViolationException`
- [x] 3.3 實作 `recordHistory(User user, String encodedPassword)` 方法：insert `password_history`，並在超過 24 筆時刪除最舊紀錄

## 4. 後端 Admin Password Policy API

- [x] 4.1 建立 `PasswordPolicyController`（`/api/admin/password-policy`），實作 `GET`（查詢）與 `PUT`（更新）endpoint，加上 `@PreAuthorize("hasRole('ADMIN')")`
- [x] 4.2 建立 `PasswordPolicyResponse` DTO 與 `UpdatePasswordPolicyRequest` DTO（含 `@Valid` 驗證）

## 5. 後端 Admin Reset Password API

- [x] 5.1 在 `UserController` 新增 `POST /api/admin/users/{id}/reset-password` endpoint，body 包含 `newPassword`（`@NotBlank`）與 `reason`（`@NotBlank`）
- [x] 5.2 在 `UserService` 實作 `resetPassword(Long targetId, String newPassword, String reason, Long adminId)` 方法：呼叫 `PasswordPolicyService.validate()`、更新密碼 hash、設 `mustChangePassword = true`、`passwordChangedAt = now()`、insert `PasswordResetLog`、呼叫 `recordHistory()`
- [x] 5.3 建立 `ResetPasswordRequest` DTO 與對應 `AdminResetPasswordResponse`（或直接複用 `UserResponse`）
- [x] 5.4 更新 `UserService.createUser()` 確保新建帳號 `mustChangePassword = true`

## 6. 後端修改 UserService & AuthController

- [x] 6.1 修改 `UserService.updateUser()`：移除密碼欄位處理（密碼改由 reset-password endpoint 負責）
- [x] 6.2 修改 `TokenResponse` DTO，新增 `mustChangePassword: boolean` 欄位
- [x] 6.3 修改 `AuthController.login()`：登入成功後，若 `user.mustChangePassword` 為 true 或密碼已過期（`passwordChangedAt + expiryDays < now()`），在 `TokenResponse` 中設 `mustChangePassword = true`

## 7. 後端 Self Change Password API

- [x] 7.1 建立 `UserSelfController`（或在現有 controller 中加入）`POST /api/user/change-password` endpoint，供已登入使用者（ADMIN 或 EMPLOYEE）呼叫
- [x] 7.2 在 `UserService` 實作 `changePassword(Long userId, String currentPassword, String newPassword)` 方法：驗證 currentPassword 正確、呼叫 `validate()`、更新密碼 hash、設 `mustChangePassword = false`、更新 `passwordChangedAt`、呼叫 `recordHistory()`
- [x] 7.3 建立 `ChangePasswordRequest` DTO（`currentPassword: @NotBlank`, `newPassword: @NotBlank`）

## 8. 前端型別與 API Client

- [x] 8.1 在 `frontend/src/types/api.ts` 新增 `PasswordPolicyResponse`、`UpdatePasswordPolicyRequest`、`ResetPasswordRequest` 介面
- [x] 8.2 在 `TokenResponse` 型別新增 `mustChangePassword: boolean` 欄位
- [x] 8.3 移除 `UpdateUserRequest` 中的 `password` 欄位（密碼改由重設密碼流程）

## 9. 前端 Auth 邏輯（mustChangePassword 守衛）

- [x] 9.1 修改 `LoginPage.tsx`：登入成功後若 `mustChangePassword === true`，將旗標存入 localStorage（或 auth state），並導向 `/change-password`
- [x] 9.2 修改 `ProtectedRoute.tsx`（或 `Layout.tsx`）：若 auth state 中 `mustChangePassword === true`，redirect 到 `/change-password`，阻止進入其他頁面

## 10. 前端 ChangePasswordPage（強制變更頁）

- [x] 10.1 建立 `frontend/src/pages/ChangePasswordPage.tsx`：表單含「目前密碼」、「新密碼」、「確認新密碼」欄位，提交呼叫 `POST /api/user/change-password`
- [x] 10.2 成功後清除 `mustChangePassword` 旗標，顯示成功訊息並導向首頁（`/admin/attendance` 或依角色）
- [x] 10.3 在 `frontend/src/router.tsx`（或 App.tsx）新增 `/change-password` route，設為公開（登入後可存取）

## 11. 前端 PasswordPolicyPage（管理員）

- [x] 11.1 建立 `frontend/src/pages/PasswordPolicyPage.tsx`：載入 `GET /api/admin/password-policy`，以表單顯示並允許修改 minLength、複雜度 checkbox、expiryDays、historyCount
- [x] 11.2 儲存邏輯呼叫 `PUT /api/admin/password-policy`，成功後顯示 toast
- [x] 11.3 在後台 Layout 側邊欄新增「密碼規則設定」 Nav 項目，route 為 `/admin/password-policy`

## 12. 前端 UsersPage 重設密碼 Modal

- [x] 12.1 在 `UsersPage.tsx` 移除 `EditUserModal` 中的密碼欄位
- [x] 12.2 新增 `ResetPasswordModal` 元件：表單含「新密碼」與「備注說明（reason）」欄位，提交呼叫 `POST /api/admin/users/{id}/reset-password`
- [x] 12.3 員工列表每列新增「重設密碼」按鈕，點擊開啟 `ResetPasswordModal`；新增 `resetPasswordUser` state 控制彈窗
