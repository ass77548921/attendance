## MODIFIED Requirements

### Requirement: README 包含本地開發啟動步驟
README SHALL 提供從零開始在本機啟動後端應用程式的完整步驟，且路徑與工作目錄需符合 backend folder 結構。

#### Scenario: 首次本地啟動
- **WHEN** 開發者按照 README 的本地啟動章節操作
- **THEN** 文件 SHALL 涵蓋前置需求（Java、Docker）、資料庫啟動、環境變數設定，並明確說明需進入 `backend/` 目錄再執行 Gradle 啟動指令

### Requirement: README 包含 API 文件入口
README SHALL 說明如何啟動後端應用後存取 Swagger UI 及 OpenAPI 文件，包含完整的存取步驟與 backend 目錄指引。

#### Scenario: 存取 Swagger UI
- **WHEN** 開發者依照 README 的 API 文件章節操作
- **THEN** 文件 SHALL 說明：先進入 `backend/` 並啟動應用，再以瀏覽器開啟 `http://localhost:8080/swagger-ui.html` 即可看到互動式 API 文件

#### Scenario: 存取 OpenAPI JSON
- **WHEN** 開發者需要取得原始 OpenAPI 規格
- **THEN** 文件 SHALL 提供 `http://localhost:8080/api-docs` 路徑說明
