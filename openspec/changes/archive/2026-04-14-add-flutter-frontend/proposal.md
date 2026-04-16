## Why

現有前台以 React Web 實作，無法在行動裝置上提供原生體驗，且缺乏離線/原生通知支援。為了讓員工能在 iOS、Android、Web 三端以一致且流暢的體驗執行打卡、查詢紀錄與提交補打卡申請，需建立以 Flutter 開發的員工前台應用程式。

## What Changes

- 新增 `flutter/` 作為 Flutter 專案根目錄，與現有 `backend/`、`frontend/` 並列
- 實作 Flavor 機制（`dev` / `staging` / `prod`）區分多環境設定（API Base URL、App 名稱、Bundle ID）
- 支援 Web（RWD）、iOS、Android 三平台
- 實作員工所需的全部使用者流程：登入、打卡、補打卡申請、個人資料與密碼管理
- 使用 Riverpod 管理狀態，GoRouter 處理路由，dio 處理 HTTP

## Capabilities

### New Capabilities

- `flutter-app-foundation`: Flutter 專案結構、Flavor 多環境、GoRouter 路由、Riverpod 初始化、RWD 介面框架、主題系統
- `flutter-auth-flow`: 登入頁、JWT Token 管理（存取/刷新）、強制改密流程、登出、受保護路由
- `flutter-attendance-ui`: 打卡主頁（上班/下班打卡按鈕、今日狀態顯示）、打卡紀錄列表（依日期範圍篩選）
- `flutter-amendment-ui`: 補打卡申請表單（含附件上傳）、個人申請紀錄列表與狀態追蹤

### Modified Capabilities

（無，現有後端 spec 行為不變，僅在前端實作對應使用者介面）

## Impact

- 新增 `flutter/` 目錄，與 `backend/`、`frontend/` 並列（不影響現有目錄）
- 依賴現有後端 REST API（attendance、user-auth、amendment 等 spec 已定義的端點）
- 需在 `docker-compose.yml` 或 CI 中補充 Flutter Web build 設定（選擇性）
- 需要 Flutter SDK（建議 3.x stable）、Dart 3.x
