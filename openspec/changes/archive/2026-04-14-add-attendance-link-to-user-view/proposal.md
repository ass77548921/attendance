## Why

員工查看彈窗目前僅顯示員工基本資料，管理員若需要進一步查看該員工的出勤狀況，必須手動切換至出勤紀錄頁面再重新篩選，操作流程繁瑣。加入一個快速跳轉連結，可讓管理員在查看員工詳情時，直接導航至該員工的出勤紀錄，提升管理效率。

## What Changes

- 在員工查看彈窗（`user-view-modal`）底部新增「查看出勤紀錄」按鈕／連結
- 點擊後關閉彈窗，並跳轉至出勤紀錄頁面（`/attendance`），同時帶入該員工的篩選條件（userId）
- 出勤紀錄頁面須支援接收 URL query parameter（`userId`）作為預設篩選，自動載入該員工的近期出勤資料

## Capabilities

### New Capabilities

- `user-view-attendance-link`: 在員工查看彈窗中加入跳轉至出勤紀錄頁面的連結，支援帶入 userId 篩選

### Modified Capabilities

- `user-view-modal`: 查看彈窗底部新增「查看出勤紀錄」操作按鈕，需調整 UI 結構
- `admin-dashboard`: 出勤紀錄頁面需支援 `userId` query parameter 作為員工篩選的初始值

## Impact

- **Frontend**: `UsersPage.tsx`（查看彈窗新增連結）、`AttendancePage.tsx`（支援 URL query param 篩選）、路由導航邏輯
- **無後端 API 異動**：此變更純屬前端導航與 UI 調整
