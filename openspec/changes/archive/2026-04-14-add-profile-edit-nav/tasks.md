## 1. 後端：新增 GET /api/users/me

- [x] 1.1 在 UserController（或新建 ProfileController）新增 `GET /api/users/me`，從 SecurityContext 取得當前使用者並回傳 UserResponse
- [x] 1.2 確認 Spring Security 設定允許 EMPLOYEE 與 ADMIN 皆可呼叫此端點（非 admin-only）

## 2. 後端：新增 PUT /api/users/me

- [x] 2.1 建立 `UpdateProfileRequest` DTO，包含選填欄位 `fullName`、`email`、`password`
- [x] 2.2 在 `UpdateProfileRequest` 加入驗證：`password` 若存在則不得為空字串（`@Size(min=1)`）
- [x] 2.3 在 UserController（或 ProfileController）新增 `PUT /api/users/me` handler，從 SecurityContext 取得 userId 後呼叫 service
- [x] 2.4 在 UserService 新增 `updateProfile(Long userId, UpdateProfileRequest req)` 方法：僅更新 payload 中出現的欄位，password 更新時 BCrypt 雜湊；忽略 username 與 role
- [x] 2.5 email 衝突時拋出 409 Conflict（複用現有衝突處理邏輯或新增對應 exception）
- [x] 2.6 新增對應 unit/integration test（覆蓋正常更新、空密碼 400、email 衝突 409）

## 3. 前端：個人資料頁面

- [x] 3.1 建立 `ProfilePage.tsx`，頁面載入時呼叫 `GET /api/users/me` 並將 fullName、email 填入表單
- [x] 3.2 表單包含 fullName、email（必填）及 password（選填，hint 說明空白則不修改）
- [x] 3.3 表單送出時呼叫 `PUT /api/users/me`，password 為空字串時不帶入 payload
- [x] 3.4 成功後顯示操作成功提示訊息
- [x] 3.5 發生 409 Conflict 時顯示對應錯誤訊息（如「Email 已被使用」），不清除表單
- [x] 3.6 若後端回傳的 fullName/username 有變更，更新 AuthContext 的顯示名稱（Sidebar 同步）

## 4. 前端：路由與 Sidebar Nav Item

- [x] 4.1 在 `App.tsx` 新增 `/admin/profile` 路由，對應 `ProfilePage`
- [x] 4.2 在 `Layout.tsx` Sidebar 底部區塊（username 與登出按鈕之間）加入「個人資料」 NavLink（`/admin/profile`），套用與主 Nav Item 相同的 active/inactive 樣式
