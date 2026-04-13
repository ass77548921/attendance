## ADDED Requirements

### Requirement: 系統使用 MySQL 8.4 作為資料庫
系統 SHALL 使用 MySQL 8.4（或以上）作為持久化儲存，所有 Flyway migration 腳本 SHALL 使用 MySQL 相容語法，應用程式 SHALL 能正常連線並完成 schema 初始化。

#### Scenario: 應用程式啟動時 Flyway migration 全部通過
- **WHEN** 應用程式啟動，連線至 MySQL
- **THEN** Flyway 執行 V1–V9 全部 migration 無錯誤，schema 完整建立

#### Scenario: MySQL container 健康後 app 才啟動
- **WHEN** Docker Compose 啟動
- **THEN** app service 等待 `db` service 健康檢查通過後才啟動

### Requirement: 資料型別對應正確
系統的資料模型 SHALL 在 MySQL 下保持與 PostgreSQL 相同的語意：`Instant` 欄位使用 `DATETIME(6)` 儲存 UTC 時間，`boolean` 欄位使用 `TINYINT(1)` 儲存，`Long` 主鍵使用 `BIGINT AUTO_INCREMENT`。

#### Scenario: 打卡時間儲存與讀取不失真
- **WHEN** 員工執行打卡，clock_in_time 以 `Instant` 寫入 MySQL
- **THEN** 從資料庫讀回的 clock_in_time 值與寫入值相差不超過 1 微秒（DATETIME(6) 精度）

#### Scenario: 布林欄位正確讀寫
- **WHEN** 打卡紀錄的 is_late 欄位被設為 true
- **THEN** 資料庫中對應的 TINYINT(1) 值為 1，且應用程式讀回時 Java boolean 為 true

### Requirement: 整合測試使用 MySQL Testcontainers
所有整合測試 SHALL 使用 Testcontainers 的 MySQL `8.4` image，不依賴本機安裝的 MySQL。

#### Scenario: 整合測試在 CI 環境執行
- **WHEN** 執行 `./gradlew test`
- **THEN** AbstractIntegrationTest 啟動 MySQL container，所有測試通過，無 PostgreSQL 依賴
