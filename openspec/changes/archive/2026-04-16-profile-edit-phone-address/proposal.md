## Why

個人資料頁目前只允許修改姓名、Email 與密碼，但 `address`、`personalPhone`、`officeExtension` 等聯絡資訊欄位已存在於資料庫與 UserResponse，卻只能由管理員透過後台編輯員工帳號時修改。員工應能自行維護自己的聯絡資訊，減少管理員負擔。

## What Changes

- **Backend `UpdateProfileRequest`**：新增 `address`、`personalPhone`、`officeExtension` 三個選填欄位
- **Backend `updateProfile()` service**：更新邏輯以包含上述三欄位的寫入
- **前台 React `ProfilePage.tsx`**：表單新增地址、個人電話、公司分機三個選填輸入欄
- **Flutter `ProfilePage`**：從唯讀顯示改為可編輯頁面（或加入「編輯」入口），支援修改 `address`、`personalPhone`、`officeExtension`

## Capabilities

### New Capabilities

（無）

### Modified Capabilities

- `user-profile-edit`: `PUT /api/users/me` 新增支援 `address`、`personalPhone`、`officeExtension` 欄位；前台 React 個人資料頁與 Flutter 個人資料頁同步新增可編輯欄位

## Impact

- **Backend**: `UpdateProfileRequest.java`（新增三欄位）、`UserService.updateProfile()`（新增欄位更新邏輯）
- **Frontend (React)**: `ProfilePage.tsx`（表單新增地址、電話、分機欄位）、`api.ts` 可能需要新增 `UpdateProfileRequest` 型別或擴充現有型別
- **Frontend (Flutter)**: `profile_page.dart`（新增編輯功能或導向編輯頁）、可能需新增 `ProfileEditPage`、更新 dio 呼叫邏輯
- **API**: `PUT /api/users/me` 為非破壞性擴充；新欄位皆為選填，null 表示不修改
