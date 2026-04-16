## Why

目前專案的 CORS 設定分散在多個層面（後端、安全設定、前端環境、部署組態），缺乏單一策略與一致命名，導致不同環境（dev/stage/pro）行為不一致、除錯成本高，且容易在調整來源白名單時產生回歸風險。現在需要先完成結構化整理，建立可預期且可驗證的跨專案 CORS 管理方式。

## What Changes

- 定義並導入單一的 CORS 設定模型，區分「允許來源、允許方法、允許標頭、是否允許憑證」等核心欄位。
- 將 backend 的 CORS 行為集中到單一組態入口，避免在多處重複或互相覆蓋。
- 對齊 frontend 與 flutter 端的 API 呼叫環境設定，確保與 backend 的 CORS 策略一致。
- 建立各環境（dev/stage/pro）對應的 CORS 來源配置規範與命名慣例。
- 補齊文件與驗證流程，讓 CORS 變更可檢查、可回歸測試、可交接。

## Capabilities

### New Capabilities
- `cors-policy-governance`: 建立跨 backend/frontend/flutter/部署組態的一致 CORS 策略、配置來源與驗證流程。

### Modified Capabilities
- `flutter-app-foundation`: 調整環境與 API 連線初始化需求，使客戶端端點設定與 CORS 策略可一致管理。
- `local-config-guide`: 新增與更新本機與多環境的 CORS 設定指引與驗證步驟。

## Impact

- Backend: Spring Security / Web MVC CORS 設定與相關環境變數讀取邏輯。
- Frontend: API base URL 與開發代理/部署設定對 CORS 的相容性。
- Flutter: 各 flavor 對應 API 端點設定與跨環境連線檢查。
- DevOps/Docs: docker-compose、環境設定樣板、README/OpenSpec 文件與測試驗證流程。
