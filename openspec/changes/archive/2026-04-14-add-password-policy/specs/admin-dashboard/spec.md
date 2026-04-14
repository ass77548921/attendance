## MODIFIED Requirements

### Requirement: 員工帳號管理
系統 SHALL 允許管理員查詢員工列表、查看帳號詳情、建立帳號、編輯帳號資料、重設密碼及停用帳號。重設密碼操作 SHALL 獨立於一般資料編輯流程，並要求管理員填寫備注說明。

#### Scenario: 查詢員工列表
- **WHEN** 管理員查詢員工列表（可依姓名或 email 搜尋）
- **THEN** 系統回傳員工列表，含 userId、username、fullName、email、role、status；每列提供「查看」、「編輯」、「重設密碼」操作按鈕

#### Scenario: 查看員工詳情
- **WHEN** 管理員點擊員工列表的「查看」按鈕
- **THEN** 系統以彈窗顯示該員工的完整唯讀資訊（ID、帳號、姓名、Email、角色、狀態、建立時間）

#### Scenario: 編輯員工資料
- **WHEN** 管理員透過編輯彈窗提交修改後的帳號、姓名、Email、狀態
- **THEN** 系統呼叫 `PUT /api/admin/users/{id}`，更新成功後回傳 200 及更新後的 `UserResponse`

#### Scenario: 帳號重複衝突
- **WHEN** 管理員嘗試將帳號改為已存在的帳號名稱（排除自身）
- **THEN** 系統回傳 409 Conflict，訊息說明帳號已存在

#### Scenario: 建立員工帳號
- **WHEN** 管理員填寫 username、password、fullName、email、role 並提交
- **THEN** 系統建立帳號並回傳 201 Created，該帳號 `mustChangePassword` 設為 `true`

#### Scenario: 重設員工密碼
- **WHEN** 管理員點擊「重設密碼」按鈕，填寫新密碼及備注說明後提交
- **THEN** 系統呼叫 `POST /api/admin/users/{id}/reset-password`，成功後顯示確認訊息，並記錄操作日誌

#### Scenario: 停用帳號
- **WHEN** 管理員呼叫 `PATCH /api/admin/users/{id}/status` 並傳入 `status: INACTIVE`
- **THEN** 系統停用帳號，該使用者後續請求將收到 401

### Requirement: 密碼規則設定頁
系統 SHALL 在後台管理導覽列提供「密碼規則設定」入口，允許管理員查看與修改全域密碼規則。

#### Scenario: 進入密碼規則設定頁
- **WHEN** 管理員點擊側邊欄「密碼規則設定」Nav 項目
- **THEN** 前端導向密碼規則設定頁，顯示當前 PasswordPolicy 設定值（minLength、複雜度選項、expiryDays、historyCount）

#### Scenario: 儲存密碼規則
- **WHEN** 管理員修改規則並點擊「儲存」
- **THEN** 系統呼叫 `PUT /api/admin/password-policy`，成功後顯示更新成功提示
