## Context

`PUT /api/users/me` 目前只允許更新 `fullName`、`email`、`password`，而 `address`、`personalPhone`、`officeExtension` 欄位雖已存在於資料庫及 `UserResponse`，但不在 `UpdateProfileRequest` 中，僅能由管理員透過 `PUT /api/admin/users/:id` 修改。

前台 React `ProfilePage` 表單目前也只呈現 `fullName`、`email`、`password` 三欄。Flutter `ProfilePage` 為唯讀展示，沒有任何欄位可直接編輯。

## Goals / Non-Goals

**Goals:**
- 擴充 `UpdateProfileRequest` 加入 `address`、`personalPhone`、`officeExtension` 選填欄位
- 更新 `UserService.updateProfile()` 以寫入上述三欄位
- 前台 React `ProfilePage` 新增三個選填輸入欄
- Flutter `ProfilePage` 新增內嵌編輯功能（EditProfilePage 或 inline edit）

**Non-Goals:**
- 不修改 `username`、`role`、`status` 的自我修改權限
- 不新增 Email 驗證流程
- 不修改管理員編輯員工帳號 (`/api/admin/users/:id`) 的行為

## Decisions

### 1. 後端：擴充 `UpdateProfileRequest` 而非新建 DTO

**決定**：直接在 `UpdateProfileRequest` 加三個選填欄位，`null` 表示「不修改」，空字串 `""` 表示「清空」。

**理由**：現有 `updateProfile()` 已採用「欄位非 null 才更新」的 partial-update 模式，與此語意一致，無需新 DTO 或新端點。

**替代方案**：新建 `UpdateContactRequest` — 增加 endpoint 數量與前端呼叫複雜度，不值得。

### 2. 前台 React：直接在現有 `ProfilePage` 新增欄位

**決定**：在現有 `ProfilePage.tsx` 表單中追加三個 input，不拆分頁面或子元件。

**理由**：目前頁面結構簡單，三個欄位均為選填且型別相同（string），直接追加可維持程式碼一致性。

**替代方案**：拆出獨立「聯絡資訊」Card — 過度工程，不符合最小改動原則。

### 3. Flutter：新增獨立 `EditProfilePage` 路由

**決定**：`ProfilePage` 加入「編輯」按鈕，點擊後 `context.push(AppRoutes.editProfile)` 導向 `EditProfilePage`，表單儲存後 pop 回並 invalidate `profileProvider`。

**理由**：Flutter profile_page 目前為 `ConsumerWidget`（非 stateful），若要做 inline edit 需改成 stateful 並管理 TextEditingController，成本高且 UX 切換明確；獨立頁面符合現有 Flutter 路由慣例（`changePassword` 也是獨立頁面）。

**替代方案**：Inline edit — 需 `ConsumerStatefulWidget`、多個 controller、取消/確認狀態管理，改動量更大。

### 4. 前端型別：新增 `UpdateMyProfileRequest`

**決定**：在 `api.ts` 新增 `UpdateMyProfileRequest` 介面，與 admin 用的 `UpdateUserRequest` 分開。

**理由**：`UpdateUserRequest` 包含 `username`、`status` 等管理員專屬欄位；自我更新請求語意不同，應有獨立型別以避免混淆。

## Risks / Trade-offs

- **空字串清空 vs null 語意**：前端傳空字串時後端要清空欄位，但 `UpdateProfileRequest` 目前對 null = 不修改。需明確約定：空字串 `""` 送出前在前端轉為 `null`（與現有 `emptyToNull` helper 一致）。
  → Mitigation：React 側與 Flutter 側送出前統一呼叫 emptyToNull / null-if-empty 轉換。

- **Flutter DIO 型別**：新增 `EditProfilePage` 需呼叫 `PUT /api/users/me`，應重用現有 `dioProvider` 確保 token 自動注入。
  → Mitigation：參考 `ChangePasswordPage` 的 dio 呼叫模式。

## Migration Plan

1. 後端先行部署（`UpdateProfileRequest` 欄位為選填，API 向後相容）
2. 前台 React 部署
3. Flutter 部署

無資料庫 migration（欄位已存在）。無 rollback 風險，屬純擴充。

## Open Questions

（無）
