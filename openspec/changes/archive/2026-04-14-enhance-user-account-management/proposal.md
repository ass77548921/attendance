## Why

目前系統僅有 ADMIN 與 EMPLOYEE 兩種角色，無法區分「能管理所有帳號（含管理員）」與「只能看到員工列表」的需求。同時員工資料欄位不足（缺少聯絡資訊），且帳號列表的搜尋功能過於簡單，無法依 ID、角色、狀態快速篩選。

## What Changes

- 新增 **SUPER_ADMIN** 角色，具備建立 EMPLOYEE 與 ADMIN 帳號的權限，並可查看所有用戶（員工 + 管理員）的帳號列表
- **ADMIN** 角色僅能查看員工（EMPLOYEE）帳號列表，無法查看或建立管理員帳號
- 員工帳號資料新增三個可選欄位：`address`（地址）、`personalPhone`（個人聯絡電話）、`officeExtension`（公司分機電話）
- 帳號管理頁搜尋介面強化：
  - 新增 ID 精確搜尋
  - 新增角色篩選（SUPER_ADMIN 可篩選 EMPLOYEE / ADMIN / 全部；ADMIN 固定只顯示 EMPLOYEE）
  - 新增狀態篩選（ACTIVE / INACTIVE / 全部）

## Capabilities

### New Capabilities

- `super-admin-role`: 定義 SUPER_ADMIN 角色的身份認證、授權規則及與其他角色的權限邊界

### Modified Capabilities

- `admin-operations-console`: 帳號管理頁 (/admin/users) 的顯示範圍、搜尋篩選條件依角色動態調整；新增角色、ID、狀態三種搜尋過濾器
- `user-edit-modal`: 編輯彈窗新增 `address`、`personalPhone`、`officeExtension` 三個選填欄位
- `user-view-modal`: 查看彈窗新增顯示 `address`、`personalPhone`、`officeExtension` 三個欄位

## Impact

- **Backend**: `User` entity 新增三個可空欄位；新增 SUPER_ADMIN 角色相關授權邏輯；`GET /api/admin/users` 依呼叫者角色過濾回傳資料（SUPER_ADMIN 回傳全部，ADMIN 僅回傳 EMPLOYEE）；`POST /api/admin/users` 限制 ADMIN 只能建立 EMPLOYEE 角色帳號；查詢 API 新增 `id`、`role`、`status` 篩選參數
- **Frontend**: `UsersPage.tsx` 搜尋列新增 ID / 角色 / 狀態篩選；編輯彈窗與查看彈窗新增三個選填欄位；根據當前登入者角色動態顯示可用角色選項
- **Database**: `users` 表新增 `address`、`personal_phone`、`office_extension` 三個可空欄位，需 migration
- **JWT/Auth**: token payload 需包含 SUPER_ADMIN 角色資訊，前端據此控制 UI 顯示範圍
