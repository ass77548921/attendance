## MODIFIED Requirements

### Requirement: 員工出勤紀錄查詢
系統 SHALL 允許管理員查詢所有員工的出勤紀錄，支援依員工、日期範圍、遲到狀態篩選，並支援分頁。出勤紀錄回應物件 SHALL 包含 `latestAdjustment` 欄位，提供最近一次手動調整的摘要（adjustedBy、reason、adjustedAt），若無調整紀錄則為 `null`。出勤紀錄頁 SHALL 支援接收 URL query parameter `userId`，並以其作為員工篩選的初始值自動執行第一次查詢。

#### Scenario: 查詢指定員工指定日期範圍紀錄
- **WHEN** 管理員提供 userId、startDate、endDate 查詢參數
- **THEN** 系統回傳該員工指定範圍內的所有出勤紀錄，含上下班時間、遲到/早退狀態、工作時長，及 `latestAdjustment` 摘要（無調整時為 `null`）

#### Scenario: 查詢所有員工當月紀錄
- **WHEN** 管理員提供 startDate 與 endDate，不指定 userId
- **THEN** 系統回傳所有員工在該範圍的紀錄，支援分頁（pageSize 預設 20），每筆含 `latestAdjustment` 欄位

#### Scenario: 篩選遲到員工
- **WHEN** 管理員提供 isLate=true 篩選條件
- **THEN** 系統僅回傳 isLate=true 的紀錄，每筆含 `latestAdjustment` 欄位

#### Scenario: 非管理員嘗試查詢他人紀錄
- **WHEN** 員工嘗試查詢其他員工的出勤紀錄
- **THEN** 系統回傳 403 Forbidden

#### Scenario: 從 URL query parameter 初始化 userId 篩選
- **WHEN** 管理員從員工查看彈窗的「查看出勤紀錄」連結進入，URL 帶有 `?userId=<id>`
- **THEN** 出勤紀錄頁自動將該 userId 填入篩選欄位並執行初始查詢，顯示該員工的近期出勤紀錄

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

### Requirement: 側邊欄個人資料導航入口
系統 SHALL 在後台左側 Sidebar 底部區塊，顯示「個人資料」Nav Item，位於登入使用者名稱下方、登出按鈕上方。點擊後導航至 `/admin/profile`。

#### Scenario: 使用者點擊個人資料 Nav Item
- **WHEN** 已登入使用者點擊 Sidebar 底部的「個人資料」連結
- **THEN** 系統導航至 `/admin/profile`，並將「個人資料」項目渲染為 active 狀態

#### Scenario: 當前路由為 /admin/profile 時的 Nav 狀態
- **WHEN** 當前路由為 `/admin/profile`
- **THEN** Sidebar 中「個人資料」项目呈現 active 樣式（與其他主功能 Nav Item 的 active 樣式一致）
