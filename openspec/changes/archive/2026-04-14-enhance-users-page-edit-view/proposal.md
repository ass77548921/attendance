## Why

目前員工帳號管理頁只提供「啟用／停用」切換，無法在後台修改員工資料（帳號、姓名、Email、密碼、狀態），也無法快速查閱單一員工的完整資訊，增加管理員的操作成本。

## What Changes

- 在員工列表每列新增「查看」與「編輯」兩個操作按鈕，取代原本單純的啟用/停用按鈕
- 「查看」按鈕：彈窗顯示員工的完整唯讀資訊（ID、帳號、姓名、Email、角色、狀態、建立時間）
- 「編輯」按鈕：彈窗提供可編輯表單，支援修改帳號、姓名、Email、狀態、密碼（選填）
- 呼叫後端 `PUT /api/admin/users/{id}` 更新員工資料
- 彈窗風格採用類 SweetAlert2 的居中遮罩設計（圓角卡片、backdrop blur）

## Capabilities

### New Capabilities
- `user-edit-modal`: 員工資料編輯彈窗，支援修改帳號/姓名/Email/狀態/密碼
- `user-view-modal`: 員工資料唯讀查看彈窗

### Modified Capabilities
- `admin-dashboard`: 員工帳號管理的操作欄位從單純狀態切換擴展為查看與編輯

## Impact

- `frontend/src/pages/UsersPage.tsx`：新增 `ViewUserModal`、`EditUserModal` 元件，調整操作欄
- 後端需確認 `PUT /api/admin/users/{id}` endpoint 是否存在；若不存在需新增
- `frontend/src/types/api.ts`：視後端回應可能需新增 `UpdateUserRequest` 型別
