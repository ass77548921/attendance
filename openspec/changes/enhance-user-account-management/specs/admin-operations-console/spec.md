## MODIFIED Requirements

### Requirement: 員工帳號管理頁
系統 SHALL 提供員工帳號管理頁 (`/admin/users`)，SUPER_ADMIN 可查詢所有帳號（EMPLOYEE + ADMIN）、新增 EMPLOYEE 或 ADMIN 帳號、啟用/停用帳號；ADMIN 僅可查詢 EMPLOYEE 帳號列表、新增 EMPLOYEE 帳號、啟用/停用 EMPLOYEE 帳號。

#### Scenario: SUPER_ADMIN 查詢帳號列表
- **WHEN** SUPER_ADMIN 訪問帳號管理頁
- **THEN** 表格顯示所有 EMPLOYEE 與 ADMIN 帳號（不包含 SUPER_ADMIN），欄位包含 userId、username、fullName、email、role、status（ACTIVE / INACTIVE）

#### Scenario: ADMIN 查詢帳號列表
- **WHEN** ADMIN 訪問帳號管理頁
- **THEN** 表格僅顯示 EMPLOYEE 帳號，不顯示 ADMIN 帳號，欄位包含 userId、username、fullName、email、role、status（ACTIVE / INACTIVE）

#### Scenario: 依 ID 搜尋
- **WHEN** 使用者在 ID 搜尋欄輸入 userId 數字後提交搜尋
- **THEN** 表格顯示符合該 userId 的帳號（精確比對）；若無結果則顯示空列表

#### Scenario: 依姓名或 Email 搜尋
- **WHEN** 使用者在姓名/Email 搜尋欄輸入關鍵字後提交搜尋
- **THEN** 表格顯示姓名或 email 包含該關鍵字的帳號（模糊比對）

#### Scenario: SUPER_ADMIN 依角色篩選
- **WHEN** SUPER_ADMIN 在角色篩選器選擇「EMPLOYEE」、「ADMIN」或「全部」
- **THEN** 表格依所選角色更新，角色篩選器可選項目為：全部、EMPLOYEE、ADMIN

#### Scenario: ADMIN 角色篩選不可用
- **WHEN** ADMIN 訪問帳號管理頁
- **THEN** 頁面不顯示角色篩選器（或固定顯示 EMPLOYEE，不可修改）；後端固定只回傳 EMPLOYEE 帳號

#### Scenario: 依狀態篩選
- **WHEN** 使用者在狀態篩選器選擇「ACTIVE」、「INACTIVE」或「全部」
- **THEN** 表格依所選狀態更新，只顯示符合狀態的帳號

#### Scenario: SUPER_ADMIN 建立新員工帳號
- **WHEN** SUPER_ADMIN 點擊「新增員工」，填寫 username、fullName、email、密碼，角色選擇「EMPLOYEE」後送出
- **THEN** 系統呼叫 `POST /api/admin/users`（role=EMPLOYEE），成功後帳號出現在列表中，顯示成功提示

#### Scenario: SUPER_ADMIN 建立新管理員帳號
- **WHEN** SUPER_ADMIN 點擊「新增管理員」，填寫 username、fullName、email、密碼，角色為「ADMIN」後送出
- **THEN** 系統呼叫 `POST /api/admin/users`（role=ADMIN），成功後帳號出現在列表中，顯示成功提示

#### Scenario: ADMIN 只能新增員工帳號
- **WHEN** ADMIN 訪問帳號管理頁
- **THEN** 頁面僅顯示「新增員工」按鈕，不顯示「新增管理員」按鈕

#### Scenario: 停用帳號
- **WHEN** 管理員點擊帳號列的「停用」按鈕
- **THEN** 系統呼叫 `PATCH /api/admin/users/{id}/status`（status=INACTIVE），該帳號狀態欄更新，顯示成功提示

#### Scenario: 重新啟用帳號
- **WHEN** 管理員點擊已停用帳號的「啟用」按鈕
- **THEN** 系統呼叫狀態更新 API（status=ACTIVE），帳號狀態恢復為 ACTIVE
