## 1. 後端：新增 UpdateUser endpoint

- [x] 1.1 建立 `UpdateUserRequest` DTO（username、fullName、email、status、password 選填），加上 `@Valid` 驗證規則（username min=3, password min=8 若有值）
- [x] 1.2 在 `UserService` 新增 `updateUser(Long id, UpdateUserRequest)` 方法：檢查帳號唯一性（排除自身 ID）、若 password 非 null 則重新 BCrypt 雜湊
- [x] 1.3 在 `UserController` 新增 `PUT /{id}` endpoint，呼叫 `userService.updateUser()`，帳號衝突時回傳 409

## 2. 前端：型別定義

- [x] 2.1 在 `src/types/api.ts` 新增 `UpdateUserRequest` 介面（username、fullName、email、status、password? 選填）

## 3. 前端：查看彈窗（ViewUserModal）

- [x] 3.1 在 `UsersPage.tsx` 新增 `ViewUserModal` 元件：接收 `UserResponse` prop，以置中遮罩 + 圓角卡片顯示 ID、帳號、姓名、Email、角色、狀態、建立時間（均唯讀）
- [x] 3.2 點擊遮罩外區域或「關閉」按鈕可關閉彈窗

## 4. 前端：編輯彈窗（EditUserModal）

- [x] 4.1 在 `UsersPage.tsx` 新增 `EditUserModal` 元件：接收 `UserResponse` prop，表單預填帳號、姓名、Email、狀態（select）、密碼（空白）
- [x] 4.2 實作儲存邏輯：呼叫 `PUT /api/admin/users/{id}`，密碼欄位空白時不傳該欄位，成功後關閉並重新載入列表
- [x] 4.3 錯誤處理：後端 400/409 錯誤訊息顯示在彈窗內；點擊遮罩外或「取消」直接關閉不儲存

## 5. 前端：調整操作欄

- [x] 5.1 移除員工列表操作欄的「啟用/停用」直接切換按鈕，改為「查看」與「編輯」兩個按鈕
- [x] 5.2 新增 `viewUser` state（`UserResponse | null`）和 `editUser` state（`UserResponse | null`），用於控制彈窗開關
- [x] 5.3 在列表 JSX 中條件渲染 `ViewUserModal`（當 `viewUser !== null`）和 `EditUserModal`（當 `editUser !== null`）
