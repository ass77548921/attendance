## 1. 資料庫 Migration

- [x] 1.1 新增 Flyway migration script：ALTER TABLE users ADD COLUMN address VARCHAR(255) NULL, ADD COLUMN personal_phone VARCHAR(50) NULL, ADD COLUMN office_extension VARCHAR(50) NULL

## 2. 後端 - 角色系統擴充

- [x] 2.1 在 `Role` enum 新增 `SUPER_ADMIN` 值
- [x] 2.2 更新 Spring Security 設定：需要 ADMIN 的端點同時允許 SUPER_ADMIN（使用 `hasAnyRole`）
- [x] 2.3 新增 SUPER_ADMIN 專用授權 guard：在 `UserService.createUser()` 中驗證若 caller 為 ADMIN 嘗試建立 ADMIN/SUPER_ADMIN 角色時回傳 403

## 3. 後端 - User Entity 與 DTO 更新

- [x] 3.1 `User` entity 新增三個可空欄位：`address`、`personalPhone`、`officeExtension`
- [x] 3.2 `UserRequest` DTO 新增三個可選欄位（nullable）
- [x] 3.3 `UserResponse` DTO 新增三個欄位（可為 null）
- [x] 3.4 `UserService.createUser()` 與 `updateUser()` 處理新欄位的 mapping

## 4. 後端 - 帳號列表查詢邏輯調整

- [x] 4.1 `GET /api/admin/users` 新增查詢參數：`id`（Long）、`role`（String）、`status`（String）
- [x] 4.2 `UserService.getUsers()` 依呼叫者角色過濾資料：SUPER_ADMIN 返回 EMPLOYEE + ADMIN；ADMIN 只返回 EMPLOYEE
- [x] 4.3 `UserService.getUsers()` 套用 query params 篩選：id 精確比對、role 精確比對、status 精確比對
- [x] 4.4 確保 SUPER_ADMIN 帳號本身不被任何查詢 API 回傳（role 不在可回傳清單中）

## 5. 前端 - 角色狀態管理

- [x] 5.1 確認 `AuthContext` 正確從 JWT payload 或登入回應中解析並儲存 `role` 欄位
- [x] 5.2 `AuthContext` 暴露 `isSuperAdmin: boolean` 與 `isAdmin: boolean` 輔助屬性供頁面使用

## 6. 前端 - 帳號管理頁搜尋介面

- [x] 6.1 `UsersPage` 搜尋列新增「ID」輸入欄（number type），onChange 觸發搜尋參數更新
- [x] 6.2 `UsersPage` 依 `isSuperAdmin` 條件渲染「角色」篩選 select（SUPER_ADMIN 顯示「全部 / EMPLOYEE / ADMIN」；ADMIN 隱藏或 disabled 固定 EMPLOYEE）
- [x] 6.3 `UsersPage` 新增「狀態」篩選 select（全部 / ACTIVE / INACTIVE）
- [x] 6.4 搜尋參數（id、role、status、keyword）透過 API query params 送至後端，移除前端本地過濾邏輯

## 7. 前端 - 帳號管理頁按鈕控制

- [x] 7.1 `UsersPage` 依 `isSuperAdmin` 條件顯示「新增管理員」按鈕（ADMIN 不顯示）
- [x] 7.2 「新增員工」與「新增管理員」按鈕觸發相同表單，但預設 role 不同（EMPLOYEE / ADMIN）；若為 SUPER_ADMIN 才能手動切換角色

## 8. 前端 - EditModal 欄位更新

- [x] 8.1 `EditModal`（或 `UsersPage` 內的編輯彈窗）新增三個選填輸入欄：地址（address）、個人聯絡電話（personalPhone）、公司分機電話（officeExtension）
- [x] 8.2 開啟彈窗時預填現有值（從 user 物件取得）
- [x] 8.3 儲存時將三個欄位（可為空字串或 null）包含在 `PUT /api/admin/users/{id}` request body 中

## 9. 前端 - ViewModal 欄位更新

- [x] 9.1 `ViewModal`（或 `UsersPage` 內的查看彈窗）新增顯示：地址、個人聯絡電話、公司分機電話
- [x] 9.2 若值為空（null 或空字串）則顯示「—」

## 10. 類型定義更新

- [x] 10.1 `frontend/src/types/api.ts` 中的 `User` 型別新增 `address`、`personalPhone`、`officeExtension`（`string | null`）欄位
- [x] 10.2 確認相關 API 呼叫函式的 request / response 型別均已更新
