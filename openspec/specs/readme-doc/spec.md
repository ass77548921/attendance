# readme-doc Specification

## Purpose
TBD - created by archiving change add-readme. Update Purpose after archive.
## Requirements
### Requirement: README.md 存在於專案根目錄
專案根目錄 SHALL 包含一份 `README.md` 文件，提供開發者快速了解系統所需的完整資訊。

#### Scenario: 開發者開啟專案根目錄
- **WHEN** 開發者瀏覽至專案根目錄
- **THEN** 系統（Git 平台或檔案瀏覽器）SHALL 顯示 `README.md` 文件

### Requirement: README 包含專案簡介
README SHALL 包含專案名稱、用途描述及主要功能清單。

#### Scenario: 閱讀專案簡介
- **WHEN** 開發者開啟 README.md
- **THEN** 文件 SHALL 在開頭呈現專案名稱與一段用途說明，以及主要功能列表

### Requirement: README 包含技術棧說明
README SHALL 列出專案使用的主要技術與框架版本。

#### Scenario: 確認技術棧
- **WHEN** 開發者查閱 README 的技術棧章節
- **THEN** 文件 SHALL 列出 Java 版本、Spring Boot 版本、資料庫、ORM、Migration 工具、認證機制、測試框架

### Requirement: README 包含本地開發啟動步驟
README SHALL 提供從零開始在本機啟動後端應用程式的完整步驟，且路徑與工作目錄需符合 backend folder 結構。

#### Scenario: 首次本地啟動
- **WHEN** 開發者按照 README 的本地啟動章節操作
- **THEN** 文件 SHALL 涵蓋前置需求（Java、Docker）、資料庫啟動、環境變數設定，並明確說明需進入 `backend/` 目錄再執行 Gradle 啟動指令

### Requirement: README 包含環境變數說明
README SHALL 以表格形式列出所有必要與選用的環境變數，含預設值說明。

#### Scenario: 查閱環境變數
- **WHEN** 開發者查閱 README 的環境變數章節
- **THEN** 文件 SHALL 呈現變數名稱、說明、預設值三欄表格，涵蓋 DB、JWT、SMTP、上傳目錄等設定

### Requirement: README 包含 API 文件入口
README SHALL 說明如何啟動後端應用後存取 Swagger UI 及 OpenAPI 文件，包含完整的存取步驟與 backend 目錄指引。

#### Scenario: 存取 Swagger UI
- **WHEN** 開發者依照 README 的 API 文件章節操作
- **THEN** 文件 SHALL 說明：先進入 `backend/` 並啟動應用，再以瀏覽器開啟 `http://localhost:8080/swagger-ui.html` 即可看到互動式 API 文件

#### Scenario: 存取 OpenAPI JSON
- **WHEN** 開發者需要取得原始 OpenAPI 規格
- **THEN** 文件 SHALL 提供 `http://localhost:8080/api-docs` 路徑說明

### Requirement: README 包含測試執行說明
README SHALL 說明如何執行測試，包含前置需求（Docker）。

#### Scenario: 執行測試套件
- **WHEN** 開發者查閱 README 的測試章節
- **THEN** 文件 SHALL 提供 `./gradlew test` 指令及 Testcontainers 需要 Docker 的說明

### Requirement: README 包含 Docker 部署說明
README SHALL 提供使用 Docker Compose 部署應用程式的步驟。

#### Scenario: 使用 Docker Compose 部署
- **WHEN** 開發者依照 README 的部署章節操作
- **THEN** 文件 SHALL 說明建立 `.env` 檔案、執行 `docker compose up` 的流程

