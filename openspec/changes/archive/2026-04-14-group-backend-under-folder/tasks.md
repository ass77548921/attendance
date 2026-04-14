## 1. 儲存庫結構遷移

- [x] 1.1 建立 `backend/` 目錄，並將後端專案資產（Gradle 檔案、`src/`、後端資源、後端建置內容）移入。
- [x] 1.2 驗證移動後檔案仍保有 Spring Boot、Flyway migration 與 template/resource 載入所需的相對結構。
- [x] 1.3 確認根目錄不再殘留易混淆的後端 source/runtime 檔案。

## 2. 工具鏈與執行路徑更新

- [x] 2.1 更新 Docker 與 Compose 的路徑參考（build context、Dockerfile 位置、掛載路徑）為 `backend/`。
- [x] 2.2 更新 build/test/run 入口與相關腳本，改為以後端目錄路徑執行。
- [x] 2.3 更新 CI workflow 對後端建置與測試工作的路徑假設。

## 3. 文件與開發者體驗

- [x] 3.1 更新 README 的本地啟動章節，要求先進入 `backend/` 再執行 Gradle 指令。
- [x] 3.2 更新 README 的 API 文件章節，補上後端工作目錄指引。
- [x] 3.3 更新本地設定指引，清楚標示後端指令範圍與目錄情境。

## 4. 驗證與回滾準備

- [x] 4.1 於新目錄結構下執行後端自動化測試並確認通過。
- [x] 4.2 以遷移後路徑執行 Docker Compose 啟動，確認後端可正常啟動。
- [x] 4.3 記錄回滾方案（回退結構遷移 commit），並確認不需額外的持久化資料遷移。
