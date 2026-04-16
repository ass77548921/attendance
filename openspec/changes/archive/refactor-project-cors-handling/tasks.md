## 1. CORS 設定盤點與模型定義

- [x] 1.1 盤點 backend/frontend/flutter 現有 CORS 與 API 來源設定位置，輸出 dev/stage/pro 差異清單
- [x] 1.2 定義統一 CORS 設定欄位（origins/methods/headers/exposedHeaders/allowCredentials）與命名規範
- [x] 1.3 規劃環境變數或設定檔對映方式，確保每個環境只對應一組 CORS policy

## 2. Backend 統一 CORS 套用

- [x] 2.1 在 backend 實作單一 CORS 套用入口，移除或合併重複/衝突配置
- [x] 2.2 加入啟動時設定驗證（缺欄位、格式錯誤、`allowCredentials=true` 與 `*` 衝突時 fail fast）
- [x] 2.3 驗證受保護與公開端點的 preflight/實際請求皆套用相同 CORS 規則

## 3. Client 環境設定對齊

- [x] 3.1 對齊 frontend API base URL 與環境設定，確保對應到核准來源矩陣
- [x] 3.2 更新 flutter flavors（dev/stage/pro）API Base URL，並加入與 CORS 矩陣一致性檢查
- [x] 3.3 確認 CI/建置流程在 flavor 參數與環境檔缺失時能正確失敗

## 4. 文件與驗證流程

- [x] 4.1 更新 local setup 指南，補齊 CORS 變數位置、格式範例與本機驗證步驟
- [x] 4.2 建立 dev/stage/pro 的 CORS smoke checklist（origin、credentials、preflight）
- [x] 4.3 在 README 或對應文件補充 rollout/rollback 操作與排錯指引

## 5. 測試與交付

- [x] 5.1 新增/更新 backend 測試覆蓋允許來源、拒絕來源、preflight 行為
- [x] 5.2 於 dev/stage 執行整合驗證並記錄結果，再安排 pro 上線
- [x] 5.3 驗收變更後整理觀測指標（錯誤率、preflight 成功率）與後續改善項目
