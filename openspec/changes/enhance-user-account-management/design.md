## Context

目前系統具備 ADMIN 與 EMPLOYEE 兩種角色。帳號管理頁（`/admin/users`）針對所有具備 ADMIN 角色的用戶開放，但業主需要一個層級更高的「總管理」（SUPER_ADMIN），其可管理普通管理員帳號，而 ADMIN 應只能看到員工帳號。此外，帳號資料需擴充聯絡資訊欄位，且搜尋功能需強化。

變更涉及：後端 Entity / Repository / Service / Controller、資料庫 migration、前端 UsersPage / EditModal / ViewModal 以及 AuthContext 的角色判斷邏輯。

## Goals / Non-Goals

**Goals:**
- 引入 SUPER_ADMIN 角色，並正確限制各角色 API 存取範圍
- ADMIN 帳號管理頁只顯示 EMPLOYEE；SUPER_ADMIN 顯示全部（含 ADMIN）
- 帳號資料新增 `address`、`personalPhone`、`officeExtension` 三個可空欄位
- 搜尋列新增 ID、角色、狀態三種篩選器，可選項目依現行登入角色動態調整

**Non-Goals:**
- 不改變出勤紀錄、補打卡、郵件設定等其他管理頁的角色管制
- 不引入細粒度 permission 系統（RBAC table），僅擴充枚舉角色
- 不改動前台（員工打卡端）的任何邏輯

## Decisions

### 1. 角色層級：擴充 Role enum

**決定**：在 `Role` enum 中新增 `SUPER_ADMIN`，層級高於 `ADMIN`。Spring Security 設定中，需要 ADMIN 的端點同時允許 SUPER_ADMIN；而「只有 SUPER_ADMIN 可用」的端點額外限制。

**替代方案**：使用獨立 permission table（RBAC）。但目前角色數量少，over-engineering，且維護成本高；enum 擴充足夠。

### 2. API 過濾策略：Server-side role-based filtering

**決定**：`GET /api/admin/users` 根據 JWT 中的 principal 角色，在 Service 層過濾資料：
- 呼叫者為 SUPER_ADMIN：回傳 EMPLOYEE + ADMIN 帳號（不含其他 SUPER_ADMIN）
- 呼叫者為 ADMIN：只回傳 EMPLOYEE 帳號

`POST /api/admin/users` 同樣在 Service 層驗證：ADMIN 角色嘗試建立 ADMIN/SUPER_ADMIN 帳號時回傳 403。

**替代方案**：在 Controller 層用 `@PreAuthorize` 切分兩個端點。缺點是 URL 語義重複，且 query param 篩選邏輯仍需共用；Server-side service filter 更集中。

### 3. 資料庫欄位：可空欄位直接加在 users 表

**決定**：在 `users` 表中新增三個 nullable VARCHAR 欄位，使用 Flyway migration。不建單獨 `user_profile` 子表，因為資料量少、join 成本不值得。

### 4. 前端角色取得：從 AuthContext 解析 JWT payload

**決定**：登入後 JWT payload 中已含 `role` 欄位（現有行為），前端 `AuthContext` 解析後儲存 `role` 狀態。`UsersPage` 根據 `role === 'SUPER_ADMIN'` 決定是否顯示「新增管理員」按鈕及角色篩選選項。

### 5. 角色搜尋選項：前端動態生成

**決定**：角色篩選 `<select>` 的選項由 `role` 推算：
- SUPER_ADMIN 登入：顯示「全部 / EMPLOYEE / ADMIN」
- ADMIN 登入：直接固定 role=EMPLOYEE，不顯示角色篩選器（或 disabled）

## Risks / Trade-offs

- **SUPER_ADMIN 可見自身以外的 SUPER_ADMIN？** → 決定不回傳其他 SUPER_ADMIN 帳號（過濾邏輯：回傳 `role IN [EMPLOYEE, ADMIN]`），降低帳號枚舉風險
- **JWT 未含 role 欄位？** → 現行登入 API 已回傳 role；須確認 AuthContext 正確解析，否則前端 role guard 失效 → Mitigation: 在 `AuthContext` 加 fallback 處理與 unit test
- **Flyway migration 失敗回滾？** → 新增欄位為 nullable，無 down-migration 需求；若需回滾直接 DROP COLUMN（資料無損）

## Migration Plan

1. 後端：新增 Flyway script `V{n}__add_user_contact_fields.sql`（ALTER TABLE users ADD COLUMN address, personal_phone, office_extension）
2. 後端：更新 `User` entity、`UserRequest` / `UserResponse` DTO，加入新欄位
3. 後端：更新 `Role` enum 新增 SUPER_ADMIN；更新 Spring Security config
4. 後端：更新 `UserService.getUsers()` 加入 role-based filter 與 id/role/status query param 支援
5. 後端：更新 `UserService.createUser()` 加入 caller role guard
6. 前端：AuthContext 確保 role 正確儲存
7. 前端：UsersPage 新增搜尋欄位（ID、角色、狀態），動態顯示角色類型
8. 前端：EditModal / ViewModal 新增三個欄位
9. 整合測試後部署

## Open Questions

- SUPER_ADMIN 是否可以編輯其他 SUPER_ADMIN 帳號？（目前決定：不可，SUPER_ADMIN 帳號不出現在列表中）
- 首個 SUPER_ADMIN 帳號如何建立？（建議：透過後端 data seeder / migration 初始化，不通過 API）
