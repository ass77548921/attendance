## MODIFIED Requirements

### Requirement: 員工帳號管理
系統 SHALL 允許管理員查詢員工列表、查看帳號詳情、建立帳號、編輯帳號資料及停用帳號。

#### Scenario: 查詢員工列表
- **WHEN** 管理員查詢員工列表（可依姓名或 email 搜尋）
- **THEN** 系統回傳員工列表，含 userId、username、fullName、email、role、status；每列提供「查看」、「編輯」操作按鈕

#### Scenario: 查看員工詳情
- **WHEN** 管理員點擊員工列表的「查看」按鈕
- **THEN** 系統以彈窗顯示該員工的完整唯讀資訊（ID、帳號、姓名、Email、角色、狀態、建立時間）

#### Scenario: 編輯員工資料
- **WHEN** 管理員透過編輯彈窗提交修改後的帳號、姓名、Email、狀態（密碼選填）
- **THEN** 系統呼叫 `PUT /api/admin/users/{id}`，更新成功後回傳 200 及更新後的 `UserResponse`

#### Scenario: 帳號重複衝突
- **WHEN** 管理員嘗試將帳號改為已存在的帳號名稱（排除自身）
- **THEN** 系統回傳 409 Conflict，訊息說明帳號已存在

#### Scenario: 建立員工帳號
- **WHEN** 管理員填寫 username、password、fullName、email、role 並提交
- **THEN** 系統建立帳號並回傳 201 Created

#### Scenario: 停用帳號
- **WHEN** 管理員呼叫 `PATCH /api/admin/users/{id}/status` 並傳入 `status: INACTIVE`
- **THEN** 系統停用帳號，該使用者後續請求將收到 401
