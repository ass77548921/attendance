## Why

目前專案使用 PostgreSQL 作為資料庫。為了配合部署環境的需求（例如既有 MySQL 基礎設施、DBA 熟悉度或授權成本考量），需要將資料庫從 PostgreSQL 遷移至 MySQL。這是一次基礎設施層的替換，不涉及任何業務邏輯變更。

## What Changes

- **BREAKING** `build.gradle.kts`：移除 `postgresql` driver，改為 `mysql-connector-j`；移除 Flyway 的 `flyway-database-postgresql` 相關設定
- `docker-compose.yml`：將 `postgres:16-alpine` 映像更換為 `mysql:8.4`，調整環境變數（`MYSQL_DATABASE`、`MYSQL_USER`、`MYSQL_PASSWORD`、`MYSQL_ROOT_PASSWORD`）與健康檢查指令
- `application.yml`：更新 `spring.datasource.url`（`jdbc:mysql://`）與 `driver-class-name`（`com.mysql.cj.jdbc.Driver`）；調整 Flyway 的 `locations` 指向 MySQL 專用 migration 目錄
- `src/main/resources/db/migration/`：重寫全部 V1–V9 的 SQL migration，將 PostgreSQL 語法替換為 MySQL 語法（`BIGINT AUTO_INCREMENT`、`TINYINT(1)`、`DATETIME(6)`、`LONGTEXT`、`CURRENT_TIMESTAMP(6)` 等）
- `src/test/resources/application-test.yml`：調整測試設定，使用 MySQL Testcontainers image
- `.env.example`：更新資料庫相關範例設定（`DB_ROOT_PASSWORD` 新增）

## Capabilities

### New Capabilities

（無新能力，此為基礎設施替換）

### Modified Capabilities

（無 spec 層級的行為變更，所有 API / 業務邏輯保持不變，不需要 delta spec）

## Impact

- `build.gradle.kts`：dependency 替換
- `docker-compose.yml`：db service 完整替換
- `application.yml` / `application-test.yml`：datasource 設定
- `src/main/resources/db/migration/V1–V9`：全部重寫（PostgreSQL 特有語法 → MySQL）
- `Testcontainers` 整合測試：改用 `mysql:8.4` image
- `.env.example`：新增 `DB_ROOT_PASSWORD`
- **不影響任何 Java 業務邏輯、Controller、Service、Repository、JPA Entity**
