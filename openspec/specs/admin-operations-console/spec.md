## ADDED Requirements

### Requirement: 管理員登入
系統 SHALL 提供管理員登入頁面，輸入帳號密碼後向後端 `POST /api/auth/login` 進行驗證。僅具後台管理權限的帳號可完成登入並取得 JWT；若為員工帳號（EMPLOYEE），系統 MUST 拒絕其後台登入並在登入頁顯示「此帳號為員工帳號，無法管理後台」。

#### Scenario: 正常登入（管理帳號）
- **WHEN** 管理員輸入正確的 username 與 password 並提交登入
- **THEN** 系統取得 JWT token，儲存後導向 `/admin/attendance` 頁面

#### Scenario: 員工帳號嘗試登入後台
- **WHEN** 員工帳號（EMPLOYEE）輸入正確的 username 與 password 並提交登入
- **THEN** 系統拒絕登入且不導向管理頁，登入頁顯示「此帳號為員工帳號，無法管理後台」

#### Scenario: 帳號或密碼錯誤
- **WHEN** 管理員輸入錯誤的帳號或密碼
- **THEN** 系統在登入頁顯示「帳號或密碼錯誤」提示，不導向

#### Scenario: 已登入使用者訪問登入頁
- **WHEN** 已有有效 JWT 的管理帳號訪問登入頁
- **THEN** 系統自動導向 `/admin/attendance`

#### Scenario: JWT 過期或 401 回應
- **WHEN** 使用者的 JWT 已失效，對任一 API 呼叫收到 401 回應
- **THEN** 系統清除 localStorage token 並導向登入頁

### Requirement: 受保護路由
系統 SHALL 實作 Protected Route 機制，確保所有 `/admin/*` 頁面在無有效 token 時導向登入頁。

#### Scenario: 未登入訪問管理頁
- **WHEN** 未登入使用者直接訪問任何 `/admin/*` URL
- **THEN** 系統立即導向 `/login`

#### Scenario: 登出
- **WHEN** 管理員點擊登出按鈕
- **THEN** 系統清除 localStorage token 並導向 `/login`

### Requirement: 出勤紀錄查詢頁
系統 SHALL 提供出勤紀錄管理頁 (`/admin/attendance`)，管理員可依員工、日期範圍、遲到狀態篩選並分頁瀏覽所有員工的出勤紀錄。

#### Scenario: 查詢所有員工出勤紀錄
- **WHEN** 管理員訪問出勤紀錄頁（不套用任何篩選條件）
- **THEN** 系統顯示分頁表格，含欄位：員工姓名、日期、上班時間、下班時間、遲到/早退狀態、最近調整摘要

#### Scenario: 依條件篩選
- **WHEN** 管理員輸入 userId、日期範圍、isLate 條件後點擊「查詢」
- **THEN** 表格依條件更新，結果符合所有篩選條件

#### Scenario: 分頁瀏覽
- **WHEN** 管理員點擊分頁控制項
- **THEN** 頁面載入對應頁次資料，顯示目前頁碼與總筆數

#### Scenario: 手動調整出勤紀錄
- **WHEN** 管理員點擊某筆紀錄的「手動調整」按鈕，輸入新的上/下班時間與原因後送出
- **THEN** 系統呼叫 `PATCH /api/admin/attendance/{id}/adjust`，成功後表格原始資料更新，顯示成功提示

#### Scenario: 查看調整歷史
- **WHEN** 管理員點擊「調整歷史」
- **THEN** 系統呼叫 `GET /api/admin/attendance/{id}/adjustments` 並以彈窗或側邊欄顯示調整記錄列表

### Requirement: 補打卡申請審核頁
系統 SHALL 提供補打卡申請管理頁 (`/admin/amendments`)，管理員可查詢補打卡申請並執行核准或駁回。

#### Scenario: 顯示待審核申請列表
- **WHEN** 管理員訪問補打卡審核頁（預設顯示 PENDING 申請）
- **THEN** 表格顯示申請人、日期、類型（CLOCK_IN / CLOCK_OUT）、補登時間、原因，每筆附有「核准」與「駁回」按鈕

#### Scenario: 核准申請
- **WHEN** 管理員點擊某申請的「核准」
- **THEN** 系統呼叫審核 API 設定 status=APPROVED，申請從待審列表移除，顯示成功提示

#### Scenario: 駁回申請
- **WHEN** 管理員點擊某申請的「駁回」
- **THEN** 系統呼叫審核 API 設定 status=REJECTED，申請從待審列表移除，顯示成功提示

#### Scenario: 篩選已審核申請
- **WHEN** 管理員選擇顯示「全部」或「已核准」或「已駁回」
- **THEN** 表格依選擇 status 更新

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

### Requirement: 出勤規則設定頁
系統 SHALL 提供出勤規則設定頁 (`/admin/config`)，管理員可查詢並修改出勤規則（上下班時間、遲到寬限）。

#### Scenario: 查詢目前設定
- **WHEN** 管理員訪問出勤規則頁
- **THEN** 表單紀錄載入目前設定值（上班時間、下班時間、遲到寬限分鐘數）

#### Scenario: 儲存設定
- **WHEN** 管理員修改欄位後點擊「儲存」
- **THEN** 系統呼叫 `PUT /api/admin/config`，成功後顯示「設定已更新」提示

#### Scenario: 時間邏輯錯誤
- **WHEN** 管理員輸入的上班時間不早於下班時間
- **THEN** 系統在頁面上顯示驗證錯誤提示，不送出 API 請求

### Requirement: 郵件 SMTP 設定頁
系統 SHALL 提供郵件設定頁 (`/admin/mail-settings`)，管理員可查詢、更新 SMTP 設定，以及寄送測試信。

#### Scenario: 查詢目前郵件設定
- **WHEN** 管理員訪問郵件設定頁
- **THEN** 系統呼叫 `GET /api/admin/mail-settings`，表單顯示所有欄位（smtpHost、port、username 等）；密碼欄位顯示「●●●●●●」（僅顯示 `hasPassword: true/false`）

#### Scenario: 更新郵件設定
- **WHEN** 管理員修改設定後點擊「儲存」
- **THEN** 系統呼叫 `PUT /api/admin/mail-settings`，成功後顯示「設定已更新」提示

#### Scenario: 寄送測試信
- **WHEN** 管理員輸入測試收件 email 並點擊「寄送測試信」
- **THEN** 系統呼叫 `POST /api/admin/mail-settings/test`；成功顯示「測試信已寄出」；若後端回傳 502，顯示 `detail` 欄位內容作為錯誤訊息

### Requirement: 通知收件人設定頁
系統 SHALL 提供遲到通知收件人設定頁 (`/admin/notifications`)，管理員可查詢、新增、刪除通知收件人。

#### Scenario: 查詢收件人列表
- **WHEN** 管理員訪問通知設定頁
- **THEN** 系統呼叫 `GET /api/admin/notification-recipients`，表格顯示 name 與 email

#### Scenario: 新增收件人
- **WHEN** 管理員填寫 name 與 email 後點擊「新增」
- **THEN** 系統呼叫 `POST /api/admin/notification-recipients`，成功後新收件人出現在列表

#### Scenario: 刪除收件人
- **WHEN** 管理員點擊某收件人的「刪除」按鈕
- **THEN** 系統呼叫 `DELETE /api/admin/notification-recipients/{id}`，成功後該收件人從列表移除

### Requirement: 側邊欄導航
系統 SHALL 提供固定側邊欄，包含所有管理功能頁面的導航連結，並高亮顯示目前所在頁面。

#### Scenario: 切換頁面
- **WHEN** 管理員點擊側邊欄中任一功能連結
- **THEN** 系統以 React Router 切換至對應頁面，側邊欄高亮更新，不刷新整頁

#### Scenario: 顯示目前登入帳號
- **WHEN** 管理員已登入
- **THEN** 側邊欄底部顯示目前登入的 username 及登出按鈕
