## 1. 建立 README.md 文件

- [x] 1.1 在專案根目錄建立 `README.md` 檔案
- [x] 1.2 撰寫專案簡介章節（名稱、用途、主要功能清單）
- [x] 1.3 撰寫技術棧章節（Java 17、Spring Boot 3.2、MySQL、JPA/Hibernate、Flyway、JWT、Testcontainers）

## 2. 撰寫開發者指南章節

- [x] 2.1 撰寫前置需求說明（Java 17+、Docker）
- [x] 2.2 撰寫本地開發啟動步驟（資料庫啟動、環境變數設定、`./gradlew bootRun`）
- [x] 2.3 撰寫環境變數表格（變數名稱、說明、預設值），對照 `application.yml` 涵蓋 DB、JWT、SMTP、Upload 設定
- [x] 2.4 撰寫 API 文件入口說明（Swagger UI `/swagger-ui.html`、OpenAPI `/api-docs`）

## 3. 撰寫測試與部署章節

- [x] 3.1 撰寫測試執行說明（`./gradlew test`，前置需求 Docker for Testcontainers）
- [x] 3.2 撰寫 Docker Compose 部署步驟（建立 `.env`、`docker compose up -d`）
