## ADDED Requirements

### Requirement: 使用者查閱自身帳號資料
系統 SHALL 允許已登入使用者透過 `GET /api/users/me` 取得自身帳號資料（userId、username、fullName、email、role、status）。

#### Scenario: 已登入使用者查閱個人資料
- **WHEN** 使用者攜帶有效 JWT 呼叫 `GET /api/users/me`
- **THEN** 系統回傳 200 OK，含該使用者的 userId、username、fullName、email、role、status

#### Scenario: 未認證請求查閱個人資料
- **WHEN** 請求未攜帶 Authorization header
- **THEN** 系統回傳 401 Unauthorized

### Requirement: 使用者修改自身帳號資料
系統 SHALL 允許已登入使用者透過 `PUT /api/users/me` 更新自身的 fullName、email、password、address、personalPhone、officeExtension。username 與 role 不得透過此端點修改；若 payload 含有此二欄位，系統 SHALL 忽略之。所有欄位均為選填；`null` 表示保留原值，空字串 `""` 表示清空該欄位（address、personalPhone、officeExtension 適用）。

#### Scenario: 使用者更新 fullName 與 email
- **WHEN** 使用者攜帶有效 JWT，提交 `{ "fullName": "新名字", "email": "new@example.com" }`
- **THEN** 系統更新該使用者的 fullName 與 email，回傳 200 OK 及更新後的 UserResponse

#### Scenario: 使用者更新聯絡資訊
- **WHEN** 使用者提交 `{ "address": "台北市中山區", "personalPhone": "0912345678", "officeExtension": "123" }`
- **THEN** 系統更新 address、personalPhone、officeExtension，回傳 200 OK 及更新後的 UserResponse

#### Scenario: 使用者清空聯絡資訊
- **WHEN** 使用者提交 `{ "address": null, "personalPhone": null }`
- **THEN** 系統保留 address 與 personalPhone 原值不變，回傳 200 OK

#### Scenario: 使用者修改密碼
- **WHEN** 使用者提交 `{ "password": "newPassword123" }`（非空字串）
- **THEN** 系統以 BCrypt 雜湊後更新密碼，回傳 200 OK

#### Scenario: 使用者提交空字串密碼
- **WHEN** 使用者提交 `{ "password": "" }`
- **THEN** 系統回傳 400 Bad Request，說明密碼不得為空

#### Scenario: 使用者嘗試修改 username 或 role
- **WHEN** 使用者提交 `{ "username": "hacker", "role": "ADMIN" }`
- **THEN** 系統忽略 username 與 role 欄位，僅更新允許的欄位，回傳 200 OK

#### Scenario: email 與其他帳號衝突
- **WHEN** 使用者提交的 email 已被其他帳號使用
- **THEN** 系統回傳 409 Conflict，說明 email 已存在

#### Scenario: 未認證請求修改個人資料
- **WHEN** 請求未攜帶 Authorization header 呼叫 `PUT /api/users/me`
- **THEN** 系統回傳 401 Unauthorized

### Requirement: 前端個人資料頁面
系統 SHALL 於 `/admin/profile` 路由提供個人資料編輯頁面，供當前登入使用者查閱與修改自身資料。頁面載入時自動取得當前帳號資料並填入表單，包含 fullName、email、address、personalPhone、officeExtension 五個欄位（address、personalPhone、officeExtension 為選填），以及選填的密碼欄位。

#### Scenario: 使用者開啟個人資料頁面
- **WHEN** 使用者點擊 Sidebar 的「個人資料」Nav Item
- **THEN** 系統導航至 `/admin/profile`，頁面顯示包含 fullName、email、address（選填）、personalPhone（選填）、officeExtension（選填）的已填入表單，及選填的密碼欄位

#### Scenario: 使用者成功送出修改
- **WHEN** 使用者修改資料後點擊儲存，後端回傳 200 OK
- **THEN** 頁面顯示成功提示，Sidebar 顯示的使用者名稱若有變更則即時更新

#### Scenario: 使用者送出時發生衝突錯誤
- **WHEN** 後端回傳 409 Conflict
- **THEN** 頁面顯示對應錯誤訊息（例如「Email 已被使用」），不清除表單內容

### Requirement: Flutter 個人資料頁面支援編輯聯絡資訊
系統 SHALL 於 Flutter ProfilePage 提供「編輯」入口，點擊後導向 EditProfilePage，允許使用者修改 fullName、email、address、personalPhone、officeExtension。儲存成功後回到 ProfilePage 並刷新顯示。

#### Scenario: 使用者點擊編輯進入編輯頁
- **WHEN** 使用者在 Flutter ProfilePage 點擊「編輯」按鈕
- **THEN** 系統導向 EditProfilePage，表單預填目前的 fullName、email、address、personalPhone、officeExtension

#### Scenario: 使用者在 Flutter 成功儲存修改
- **WHEN** 使用者修改欄位後點擊儲存，後端回傳 200 OK
- **THEN** 系統回到 ProfilePage，profileProvider 刷新，頁面顯示更新後的資料

#### Scenario: 使用者在 Flutter 取消編輯
- **WHEN** 使用者點擊取消或返回
- **THEN** 系統回到 ProfilePage，不修改任何資料
