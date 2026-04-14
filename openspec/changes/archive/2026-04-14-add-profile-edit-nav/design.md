## Context

後台 Sidebar 目前分兩個區塊：上方 `flex-1` 的主功能 Nav（`navItems` 陣列渲染），以及底部固定區（顯示登入使用者名與登出按鈕）。目前沒有任何讓使用者修改自身帳號資料的入口。

後端方面，現有 `/api/admin/users/{id}` 供管理員修改他人帳號，但缺乏讓一般使用者修改自身資料的端點。

## Goals / Non-Goals

**Goals:**
- 在 Sidebar 底部區塊加入「個人資料」Nav Item，位於登出按鈕正上方
- 新增 `/admin/profile` 前端頁面，供當前使用者檢視與修改自身欄位（fullName、email、密碼）
- 後端新增 `PUT /api/users/me`，從 JWT 解出身份後更新自身帳號資料
- 成功儲存後，AuthContext 同步更新 Sidebar 顯示名稱（若 username 有顯示對應欄位）

**Non-Goals:**
- 修改 username 或 role（這兩個欄位不開放自行變更）
- 大頭照或個人圖片上傳
- 通知偏好、語言或時區設定
- 管理員透過此入口編輯他人資料（維持原有 UsersPage 功能不變）

## Decisions

### 1. Nav Item 放置位置：Sidebar 底部區塊，非主導航陣列

**決定**：「個人資料」不加入 `navItems` 陣列，改為直接寫在 Sidebar footer JSX 中，位於使用者名稱下方、登出按鈕上方。

**理由**：語意上「個人資料」與管理功能性質不同，屬於使用者個人操作，和「登出」放在同一區塊更合理。若加入 `navItems`，會在主功能列表中出現，可能造成語意混淆。

**替代方案**：加入 `navItems` 尾端 → 被否決，語意不符且破壞視覺層次。

### 2. API：`PUT /api/users/me`，全欄位選填、忽略 username/role

**決定**：新增 `PUT /api/users/me`，request body 為 `{ fullName?, email?, password? }`，所有欄位皆可選，僅提供的欄位才更新（行為類似 PATCH）。username 與 role 若出現在 payload 中一律忽略。

**理由**：使用 PUT namespace 在此語境合理（更新「我」這個資源），行為設計成 partial update 降低 client 複雜度，不需取得完整物件再重送。

**替代方案**：PATCH `/api/users/me` → 語意更精確但與專案現有風格差異不大，統一用 PUT 即可。

### 3. 密碼修改：選填，提供時必須為非空字串並重新 BCrypt 雜湊

**決定**：若 request body 含 `password` 且為非空字串，則更新密碼（BCrypt 雜湊後儲存）；若 `password` 欄位缺失或為空，則不修改密碼。

**理由**：密碼更新是高頻使用者需求，若需另建流程會增加複雜度；選填設計讓單一端點即可滿足。

**替代方案**：獨立 POST `/api/users/me/password` endpoint → 過度分拆，對此規模專案不必要。

### 4. AuthContext 同步：成功後呼叫 `refreshUser()` 或本地更新 state

**決定**：ProfilePage 成功送出後，以後端回傳的 `fullName`（或 `username`）更新 AuthContext 的 displayName state，使 Sidebar 名稱即時反映。不重新呼叫 `/api/users/me` refresh，而是直接用 response data 更新。

**理由**：避免多餘 API round-trip，response 本身就已包含更新後資料。

## Risks / Trade-offs

- **[風險] 管理員與用戶同時修改同一帳號** → last-write-wins，此規模可接受；資料無高度競爭風險
- **[風險] email 改為與通知收件人重複** → 通知收件人獨立管理，不與帳號 email 強綁定，無直接衝突
- **[Trade-off] 密碼選填的安全隱患** → 若傳入空字串需後端明確拒絕（400），避免意外清空密碼；前端同樣不傳空字串
- **[風險] JWT 過期後仍在 Profile 頁面提交** → 現有 interceptor 已處理 401，會導向登入頁，行為一致
