## ADDED Requirements

### Requirement: 管理員重設員工密碼紀錄
系統 SHALL 在管理員每次重設員工密碼時，將操作記錄儲存於 `password_reset_logs` table，包含操作者、目標使用者、重設時間與備注說明（reason）。

#### Scenario: 管理員重設密碼（含備注）
- **WHEN** 管理員呼叫 `POST /api/admin/users/{id}/reset-password`，body 包含合法 `newPassword` 及非空字串 `reason`
- **THEN** 系統重設密碼、設定 `mustChangePassword = true`、insert `password_reset_logs` 並回傳 200 OK 及更新後的 `UserResponse`

#### Scenario: 備注說明為空時拒絕重設
- **WHEN** 管理員提交的 `reason` 為空字串或 null
- **THEN** 系統回傳 400 Bad Request，提示備注說明為必填

#### Scenario: 非管理員嘗試重設他人密碼
- **WHEN** 角色為 EMPLOYEE 的使用者呼叫 `POST /api/admin/users/{id}/reset-password`
- **THEN** 系統回傳 403 Forbidden

#### Scenario: 查詢密碼重設歷史(未實作，保留)
- **WHEN** 管理員查詢某員工的密碼重設紀錄（未來功能）
- **THEN** 系統回傳該員工的所有重設紀錄（含操作者、時間、備注）
