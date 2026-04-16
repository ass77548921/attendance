## 1. Flutter 專案初始化與環境設定

- [x] 1.1 在 repo 根目錄執行 `flutter create flutter`，設定 `org` 為正式 Bundle ID prefix
- [x] 1.2 建立 `flutter/flavors/dev.json`、`staging.json`、`prod.json`，定義 `API_BASE_URL`, `APP_NAME`, `APP_SUFFIX`
- [x] 1.3 設定 Android `productFlavors`（dev, staging, prod）於 `android/app/build.gradle`
- [x] 1.4 設定 iOS Scheme / Configuration（Debug-dev, Release-dev, Debug-staging, Release-prod 等）；僅完成設定，不安排 iOS 測試（留待後續）
	- [x] 1.5 於 `lib/config/app_config.dart` 建立讀取 dart-define 環境變數的設定類別
	- [x] 1.6 更新 `pubspec.yaml`，加入所需依賴：`riverpod`, `go_router`, `dio`, `flutter_secure_storage`, `file_picker`

## 2. 應用程式基礎架構

- [x] 2.1 建立 `lib/` 目錄結構：`config/`, `core/`, `features/`, `shared/`
- [x] 2.2 於 `lib/main_dev.dart`、`main_staging.dart`、`main_prod.dart` 分別初始化對應 Flavor 並呼叫共用 `main.dart` 邏輯
- [x] 2.3 建立 `lib/shared/theme/app_theme.dart`，定義 Material 3 `ThemeData`（ColorScheme, TextTheme）
- [x] 2.4 建立斷點 helper `lib/shared/layout/breakpoints.dart`，提供 `isMobile`, `isTablet`, `isDesktop` 判斷函式
- [x] 2.5 建立 `lib/shared/layout/app_scaffold.dart`，依斷點切換 BottomNavigationBar / NavigationRail

## 3. 路由設定

- [x] 3.1 建立 `lib/core/router/app_router.dart`，以 GoRouter 定義全部路由（`/login`, `/attendance`, `/attendance/records`, `/amendments`, `/change-password`, `/profile`）
- [x] 3.2 實作 GoRouter `redirect`：未登入時導向 `/login`；`mustChangePassword = true` 時強制導向 `/change-password`
- [x] 3.3 建立 `lib/core/router/app_routes.dart` 宣告路由路徑常數

## 4. 認證功能（flutter-auth-flow）

- [x] 4.1 建立 `lib/core/network/dio_client.dart`，設定 BaseOptions（baseUrl 從 AppConfig 讀取）與 Interceptor 架構
- [x] 4.2 實作 Token Interceptor：自動附加 Authorization Header；401 時嘗試 Refresh Token 並重試原請求
- [x] 4.3 建立 `lib/features/auth/data/auth_repository.dart`，封裝 login / logout / refreshToken API 呼叫
- [x] 4.4 建立 `lib/features/auth/state/auth_provider.dart`（Riverpod `StateNotifierProvider`），管理 token 存取與登入狀態
- [x] 4.5 建立 `lib/features/auth/ui/login_page.dart`：username/password 欄位、登入按鈕、Loading 狀態、錯誤訊息顯示
- [x] 4.6 建立 `lib/features/auth/ui/change_password_page.dart`：新密碼/確認密碼欄位、即時密碼一致性驗證、強制改密守衛

## 5. 打卡功能（flutter-attendance-ui）

- [x] 5.1 建立 `lib/features/attendance/data/attendance_repository.dart`，封裝 clockIn / clockOut / getTodayStatus / getAttendanceList API 呼叫
- [x] 5.2 建立打卡狀態 Provider `lib/features/attendance/state/attendance_provider.dart`
- [x] 5.3 建立 `lib/features/attendance/ui/attendance_home_page.dart`：今日狀態卡片、依狀態顯示打卡按鈕、打卡按鈕 Loading 與成功/錯誤 Snackbar
- [x] 5.4 建立 `lib/features/attendance/ui/attendance_records_page.dart`：月份切換控制元件、打卡紀錄列表（含遲到標籤）、空狀態、Skeleton Loader

## 6. 補打卡功能（flutter-amendment-ui）

- [x] 6.1 建立 `lib/features/amendment/data/amendment_repository.dart`，封裝 submitAmendment（multipart）/ getAmendments API 呼叫
- [x] 6.2 建立 `lib/features/amendment/state/amendment_provider.dart`
- [x] 6.3 建立 `lib/features/amendment/ui/amendment_form_page.dart`：日期選取、打卡類型選擇、時間選取、原因輸入、附件選取（file_picker）、格式/數量前端驗證
- [x] 6.4 建立 `lib/features/amendment/ui/amendment_list_page.dart`：申請列表、狀態 Chip 顏色區分、篩選 Tab
- [x] 6.5 建立 `lib/features/amendment/ui/amendment_detail_page.dart`：申請詳情、附件縮圖（如有）、審核備註（如有）

## 7. 個人資料與確認密碼

- [x] 7.1 建立 `lib/features/profile/ui/profile_page.dart`：顯示使用者名稱、姓名、Email
- [x] 7.2 在個人資料頁提供「修改密碼」入口，導向 `/change-password`（非強制模式）

## 8. RWD 佈局整合

- [x] 8.1 驗證手機（375px）底部導覽列顯示正確，所有頁面內容不溢出
- [x] 8.2 驗證 Web 桌面（1280px）左側 Navigation Rail 顯示正確，內容區域佔滿剩餘空間
- [x] 8.3 驗證表單頁面在寬螢幕時使用置中最大寬度容器（max-width 600px）

## 9. 建置與 CI 設定

- [x] 9.1 建立 `flutter/Makefile`，封裝常用指令：`make run-dev`, `make build-android-prod`, `make build-web-prod`
- [x] 9.2 在根目錄 `README.md` 補充 Flutter App 開發啟動說明（Flavor 執行指令）
