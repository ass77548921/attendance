## Why

後台目前沒有提供讓使用者修改自己帳號資料的入口，使用者若需更改密碼或個人資訊，只能透過管理員代為操作。此功能讓每位登入的使用者都能自主維護個人資料，降低管理員負擔，也符合基本 UX 預期。

## What Changes

- 在左側 Sidebar 底部，登出按鈕上方新增「個人資料」Nav Item
- 新增 `/admin/profile` 頁面，顯示當前登入使用者的個人資料表單（fullName、email、密碼可選）
- 新增後端 API `PUT /api/users/me`，允許已登入使用者更新自身帳號資料（不可修改 username、role）
- AuthContext 在成功儲存後同步更新前端顯示的使用者名稱（若 fullName 有變更）

## Capabilities

### New Capabilities

- `user-profile-edit`: 已登入使用者查閱並修改自身帳號資料（fullName、email、密碼），包含前端頁面、Nav 入口，及後端 `PUT /api/users/me` API

### Modified Capabilities

- `admin-dashboard`: 側邊欄底部新增「個人資料」Nav Item，位於登出按鈕上方

## Impact

- **Frontend**: `Layout.tsx`（新增 Nav Item）、`App.tsx`（新增路由 `/admin/profile`）、新增 `ProfilePage.tsx`
- **Backend**: 新增 `PUT /api/users/me` endpoint（UserController 或獨立 ProfileController），需驗證 JWT 取得當前使用者身份
- **API**: 不影響現有 API；新端點僅供已認證使用者存取，EMPLOYEE 與 ADMIN 皆可使用
- **AuthContext**: 可能需要在成功更新後重新同步 username 狀態
