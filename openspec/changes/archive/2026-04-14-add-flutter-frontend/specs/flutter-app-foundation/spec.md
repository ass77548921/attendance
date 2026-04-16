## ADDED Requirements

### Requirement: Flutter 專案結構初始化
系統 SHALL 在 repo 根目錄建立 `flutter_app/` 目錄，以標準 Flutter 專案結構組織程式碼，並於 `lib/` 下依功能模組分層（`config/`, `core/`, `features/`, `shared/`）。

#### Scenario: 專案目錄建立
- **WHEN** 開發者執行 `flutter create flutter_app`
- **THEN** `flutter_app/` 目錄存在，包含 `lib/`, `android/`, `ios/`, `web/`, `pubspec.yaml`

#### Scenario: 功能模組分層目錄
- **WHEN** 開發者查看 `lib/` 目錄
- **THEN** 目錄結構包含 `config/`, `core/`, `features/`, `shared/` 四個頂層模組

---

### Requirement: Flavor 多環境設定
系統 SHALL 支援 `dev`、`staging`、`prod` 三個 Flavor，每個 Flavor 擁有獨立的 API Base URL、應用程式名稱（App Name）與 Bundle ID / Application ID。Flavor 設定值以 `dart-define-from-file` 注入，對應 JSON 檔案位於 `flavors/` 目錄。

#### Scenario: 以 dev Flavor 執行
- **WHEN** 開發者執行 `flutter run --flavor dev --dart-define-from-file=flavors/dev.json`
- **THEN** 應用程式名稱顯示為 `打卡(Dev)`，API 請求打至 `dev` 環境 Base URL

#### Scenario: 以 prod Flavor 建置
- **WHEN** CI 執行 `flutter build apk --flavor prod --dart-define-from-file=flavors/prod.json`
- **THEN** 建置產物使用 prod Bundle ID，API Base URL 為正式環境

#### Scenario: 缺少 Flavor 參數時拒絕建置
- **WHEN** 執行 `flutter run` 未指定 `--flavor`
- **THEN** Flutter 工具鏈拋出錯誤，提示需指定 Flavor

---

### Requirement: GoRouter 路由設定
系統 SHALL 使用 GoRouter 定義全域路由，所有路由以具名常數宣告，並支援認證守衛（redirect）。

#### Scenario: 已登入使用者存取根路徑
- **WHEN** 已登入使用者存取 `/`
- **THEN** GoRouter redirect 至 `/attendance`（打卡主頁）

#### Scenario: 未登入使用者存取受保護路由
- **WHEN** 未登入使用者存取 `/attendance`
- **THEN** GoRouter redirect 至 `/login`

#### Scenario: 路由使用具名常數
- **WHEN** 程式碼中進行路由跳轉
- **THEN** 使用 `AppRoutes.attendance` 等常數，不得硬編碼路徑字串

---

### Requirement: Riverpod 狀態管理初始化
系統 SHALL 在 `main.dart` 以 `ProviderScope` 包裹整個應用程式，所有全域狀態透過 Riverpod Provider 宣告，不使用 `InheritedWidget` 或 `setState` 管理跨元件狀態。

#### Scenario: ProviderScope 根級初始化
- **WHEN** 應用程式啟動
- **THEN** `runApp` 接收的 Widget 為被 `ProviderScope` 包裹的 `MyApp`

#### Scenario: 認證狀態 Provider
- **WHEN** 任意 Widget 需要讀取登入狀態
- **THEN** 透過 `ref.watch(authProvider)` 取得，不透過 BuildContext 直接傳遞

---

### Requirement: RWD 斷點佈局系統
系統 SHALL 提供斷點 helper，依視窗寬度回傳佈局類型（mobile < 600px、tablet 600–1200px、desktop > 1200px）。寬螢幕（tablet 以上）SHALL 顯示 Navigation Rail；手機螢幕 SHALL 顯示底部導覽列（BottomNavigationBar）。

#### Scenario: 手機寬度顯示底部導覽列
- **WHEN** 應用程式執行於寬度 375px 裝置
- **THEN** 主佈局顯示底部導覽列，無側邊導覽

#### Scenario: 桌面寬度顯示 Navigation Rail
- **WHEN** 應用程式執行於寬度 1280px 視窗（Web）
- **THEN** 主佈局顯示左側 Navigation Rail，內容區域佔剩餘空間

---

### Requirement: 全域主題設定
系統 SHALL 定義 Material 3 主題（`ThemeData`），包含統一的 ColorScheme、字體大小比例（TextTheme）。深色模式 SHALL 預留介面（可在後續 change 啟用）。

#### Scenario: 主題套用
- **WHEN** 應用程式啟動
- **THEN** 所有頁面使用統一定義的 `AppTheme.light()` 主題，無不一致的顏色或字型
