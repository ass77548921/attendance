## 1. 依賴與 Build 設定

- [x] 1.1 `build.gradle.kts`：移除 `runtimeOnly("org.postgresql:postgresql")`，新增 `runtimeOnly("com.mysql:mysql-connector-j")`
- [x] 1.2 `build.gradle.kts`：移除 `implementation("org.flywaydb:flyway-database-postgresql")`（若存在）

## 2. Flyway Migration 腳本重寫（V1–V9）

- [x] 2.1 `V1__create_users.sql`：`BIGSERIAL` → `BIGINT NOT NULL AUTO_INCREMENT`，`TIMESTAMPTZ` → `DATETIME(6)`，移除 `CHECK` 約束（已有 JPA validation）
- [x] 2.2 `V2__create_refresh_tokens.sql`：同上型別替換
- [x] 2.3 `V3__create_attendance_records.sql`：`BIGSERIAL` → `BIGINT NOT NULL AUTO_INCREMENT`，`BOOLEAN` → `TINYINT(1)`，`TIMESTAMPTZ` → `DATETIME(6)`，`NOW()` → `CURRENT_TIMESTAMP(6)`
- [x] 2.4 `V4__create_attendance_amendments.sql`：`BIGSERIAL` → `BIGINT NOT NULL AUTO_INCREMENT`，`TEXT` → `LONGTEXT`，`TIMESTAMPTZ` → `DATETIME(6)`
- [x] 2.5 `V5__create_attendance_config.sql`：`BIGSERIAL` → `BIGINT NOT NULL AUTO_INCREMENT`，`TIMESTAMPTZ` → `DATETIME(6)`，移除 `CREATE UNIQUE INDEX ... ON attendance_config ((TRUE))`（MySQL 不支援表達式索引於常數）
- [x] 2.6 `V6__create_notification_recipients.sql`：`BIGSERIAL` → `BIGINT NOT NULL AUTO_INCREMENT`，`BOOLEAN` → `TINYINT(1)`，`TIMESTAMPTZ` → `DATETIME(6)`
- [x] 2.7 `V7__create_notification_logs.sql`：`BIGSERIAL` → `BIGINT NOT NULL AUTO_INCREMENT`，`TEXT` → `LONGTEXT`，`TIMESTAMPTZ` → `DATETIME(6)`
- [x] 2.8 `V8__insert_default_admin.sql`：無語法差異，確認 INSERT 語法相容
- [x] 2.9 `V9__create_amendment_attachments.sql`：`BIGSERIAL` → `BIGINT NOT NULL AUTO_INCREMENT`，`TIMESTAMPTZ` → `DATETIME(6)`

## 3. 應用程式設定

- [x] 3.1 `application.yml`：更新 `spring.datasource.url` 為 `jdbc:mysql://` 格式（含 `serverTimezone=UTC&characterEncoding=UTF-8&allowPublicKeyRetrieval=true&useSSL=false`），更新 `driver-class-name` 為 `com.mysql.cj.jdbc.Driver`

## 4. Docker Compose 更新

- [x] 4.1 `docker-compose.yml`：將 `db` service 的 image 從 `postgres:16-alpine` 改為 `mysql:8.4`
- [x] 4.2 `docker-compose.yml`：更新 `db` service 環境變數（`MYSQL_DATABASE`、`MYSQL_USER`、`MYSQL_PASSWORD`、`MYSQL_ROOT_PASSWORD`）
- [x] 4.3 `docker-compose.yml`：更新健康檢查指令（`mysqladmin ping` 取代 `pg_isready`）
- [x] 4.4 `docker-compose.yml`：更新 `app` service 的 `DB_URL` 環境變數格式為 MySQL JDBC URL

## 5. 測試設定更新

- [x] 5.1 `application-test.yml`：更新 `spring.datasource.url` 為 MySQL JDBC URL
- [x] 5.2 `AbstractIntegrationTest.java`：將 `PostgreSQLContainer` 改為 `MySQLContainer`，更新 image 為 `mysql:8.4`，更新 `DynamicPropertySource` 使用 `MySQLContainer` 的 getter

## 6. 環境變數範本

- [x] 6.1 `.env.example`：更新 DB 相關說明，新增 `DB_ROOT_PASSWORD=change_me_root`

## 7. 驗證

- [x] 7.1 執行 `docker compose up --build`，確認 MySQL container 健康且 Flyway V1–V9 migration 全部成功
- [x] 7.2 執行 `./gradlew test`，確認所有整合測試通過
