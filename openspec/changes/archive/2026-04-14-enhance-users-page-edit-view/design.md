## Context

`UsersPage.tsx` 目前員工列表每列只有一個「啟用/停用」切換按鈕，管理員無法在後台編輯員工資料或查看完整詳情。後端 `UserController` 只有 `POST /api/admin/users`（建立）、`GET /api/admin/users`（列表）、`PATCH /api/admin/users/{id}/status`（切換狀態），缺少通用的更新 endpoint。

## Goals / Non-Goals

**Goals:**
- 在員工列表新增「查看」與「編輯」按鈕
- 查看彈窗：唯讀顯示員工完整資訊
- 編輯彈窗：可修改帳號、姓名、Email、狀態、密碼（密碼選填）
- 後端新增 `PUT /api/admin/users/{id}` endpoint
- 彈窗外觀採類 SweetAlert2 風格（置中遮罩、backdrop blur、圓角卡片）

**Non-Goals:**
- 角色（role）變更（涉及權限設計，不在本次範圍）
- 批量編輯
- 前端引入 SweetAlert2 函式庫（用 Tailwind 自行實作相同視覺效果）

## Decisions

### D1：自行實作彈窗，不引入 SweetAlert2
SweetAlert2 的 API 不適合複雜表單場景（欄位驗證、非同步提交狀態管理）。使用 Tailwind + React portal 自行實作，視覺效果一致且更易控制狀態。

### D2：後端新增 `PUT /api/admin/users/{id}`
新的 `UpdateUserRequest` DTO 包含 username、fullName、email、status、password（Optional），Service 層個別驗證後更新，密碼只在有值時重新雜湊。

### D3：維持現有「啟用/停用」流程
保留原本 `PATCH /api/admin/users/{id}/status`，不廢棄，確保只切換狀態時不需整包 PUT。編輯彈窗的狀態欄位也走 PUT endpoint，不重複呼叫 PATCH。

### D4：彈窗元件定義在 UsersPage.tsx 同檔案
`ViewUserModal` 和 `EditUserModal` 僅被 `UsersPage` 使用，不提取到 `components/`，降低間接依賴。

## Risks / Trade-offs

- `PUT /api/admin/users/{id}` 需要修改 username 的唯一性驗證邏輯（排除自身 ID）→ Service 層加 `existsByUsernameAndIdNot` 檢查
- 密碼欄位選填，前端留空時不傳 password 欄位；後端以 `null` 判斷是否跳過雜湊 → DTO 加 `@Nullable` 並在 Service 判斷

## Migration Plan

1. 後端新增 DTO `UpdateUserRequest`、Service 方法 `updateUser()`、Controller endpoint
2. 前端新增 `ViewUserModal`、`EditUserModal`，調整操作欄顯示
3. 無資料庫 migration 需求，不需回滾計劃
