## Context

目前專案同時包含 backend、frontend、flutter 三個可獨立部署與執行的邊界，CORS 設定分散於框架預設、環境檔與部署組態，缺乏一致入口。現況風險是：同一環境的來源白名單可能在不同模組出現不同值，導致預檢請求（preflight）與實際請求行為不一致。

此變更需要在不影響既有登入與打卡流程的前提下，整理 CORS 管理方式，並讓 dev/stage/pro 的設定具可追蹤性。

## Goals / Non-Goals

**Goals:**
- 建立單一且可版本化的 CORS 設定模型，覆蓋允許來源、方法、標頭與憑證策略。
- 後端只保留一個 CORS 套用入口，避免重複配置與衝突。
- 對齊 frontend 與 flutter 的 API 端點設定，確保跨環境與 CORS 規則一致。
- 建立明確驗證流程，能用自動化測試與手動檢查確認 CORS 行為。

**Non-Goals:**
- 不重寫整個認證或授權流程。
- 不引入 API Gateway 或 Service Mesh 等新基礎設施。
- 不調整 CORS 以外的網路安全政策（如 WAF 規則）。

## Decisions

1. 採用「環境導向」的 CORS 設定來源
- Decision: 以環境變數或環境檔作為唯一來源，產生後端 CORS 設定；frontend/flutter 只維護 API base URL，不各自定義 CORS 規則。
- Rationale: CORS 判斷在服務端，客戶端只需對齊目標 API 網域，避免責任重疊。
- Alternative considered: 在各客戶端配置獨立白名單。此方案會造成多點維護與高漂移風險，故不採用。

2. 在 backend 統一單一路徑套用 CORS
- Decision: 統一透過單一配置模組（例如 Security 或 WebMvc 一處）註冊 CORS，避免雙重註冊。
- Rationale: 降低規則衝突與「某些路由未套用」的機率。
- Alternative considered: 保留多處配置並以註解約束。依賴人工紀律，無法可靠防止回歸。

3. 建立跨環境 CORS 組態命名規範
- Decision: 對 dev/stage/pro 定義一致命名與最小必要欄位，並在文件中維護來源矩陣。
- Rationale: 讓部署、除錯與交接時能快速定位來源差異。
- Alternative considered: 僅在 README 以文字描述，不定義欄位。可讀性高但機械檢查能力不足。

4. 驗證策略採「單元 + 整合 + 手動 smoke」三層
- Decision: 增加後端 CORS 單元/整合測試，並提供跨環境 smoke checklist。
- Rationale: CORS 問題常由環境差異觸發，僅單元測試不足以覆蓋。
- Alternative considered: 只做手動驗證。速度快但容易漏掉 preflight 細節。

## Risks / Trade-offs

- [Risk] CORS 白名單過於嚴格導致既有客戶端被拒絕 → Mitigation: 先盤點現行來源並提供過渡期雙白名單。
- [Risk] 開啟 credentials 與萬用來源 (`*`) 產生衝突 → Mitigation: 在配置驗證中禁止該組合並於啟動時失敗快報錯。
- [Risk] 多環境值不同步造成 stage/pro 行為偏差 → Mitigation: 建立環境矩陣文件與部署前 checklist。
- [Trade-off] 集中化會增加初期遷移工作量，但可換取長期可維護性與回歸可控性。

## Migration Plan

1. 盤點現有 backend/frontend/flutter 的 CORS 相關設定位置與差異。
2. 導入統一 CORS 設定模型，並在 backend 留下單一套用入口。
3. 調整 frontend/flutter 的環境設定文件，對齊 API 來源命名。
4. 補齊測試與 smoke checklist，先於 dev/stage 驗證，再推進至 pro。
5. 部署後觀察錯誤日誌與 preflight 成功率；若異常，回滾至舊設定並保留盤點資料。

## Open Questions

- 是否需要針對管理後台與員工端使用不同 CORS 策略（不同來源集合）？
- 是否已有既定網域命名規則可直接映射到 dev/stage/pro，或需同步調整環境檔命名？
- rollout 期間是否需要短期允許舊網域，以降低切換風險？
