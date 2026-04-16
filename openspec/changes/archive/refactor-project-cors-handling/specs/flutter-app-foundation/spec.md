## MODIFIED Requirements

### Requirement: Flavor 多環境設定
系統 SHALL 支援 `dev`、`staging`、`prod` 三個 Flavor，每個 Flavor 擁有獨立的 API Base URL、應用程式名稱（App Name）與 Bundle ID / Application ID。Flavor 設定值以 `dart-define-from-file` 注入，對應 JSON 檔案位於 `flavors/` 目錄。每個 Flavor 的 API Base URL SHALL 對應到已核准的 CORS 來源矩陣，且不得指向未定義於該環境 CORS 策略的網域。

#### Scenario: 以 dev Flavor 執行
- **WHEN** 開發者執行 `flutter run --flavor dev --dart-define-from-file=flavors/dev.json`
- **THEN** 應用程式名稱顯示為 `打卡(Dev)`，API 請求打至 `dev` 環境 Base URL

#### Scenario: 以 prod Flavor 建置
- **WHEN** CI 執行 `flutter build apk --flavor prod --dart-define-from-file=flavors/prod.json`
- **THEN** 建置產物使用 prod Bundle ID，API Base URL 為正式環境

#### Scenario: 缺少 Flavor 參數時拒絕建置
- **WHEN** 執行 `flutter run` 未指定 `--flavor`
- **THEN** Flutter 工具鏈拋出錯誤，提示需指定 Flavor

#### Scenario: Flavor Base URL 與 CORS 矩陣不一致
- **WHEN** 某 Flavor 設定的 API Base URL 不在該環境核准來源清單中
- **THEN** 驗證流程 SHALL 回報失敗並阻擋發布
