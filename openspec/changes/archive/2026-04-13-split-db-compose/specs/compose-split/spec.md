## MODIFIED Requirements

### Requirement: docker-compose.yml 只包含 app service，不含任何 DB 設定
`docker-compose.yml` SHALL 只定義 `app` service，不包含任何 `db` service、`mysql_data` volume 定義，不使用 `depends_on: db`。MySQL 為外部基礎設施，由機器維護人員獨立建置，不在 repo 管理範圍內。

#### Scenario: 部署 app（DB 已在外部運行）
- **WHEN** 執行 `docker compose up --build`（外部 MySQL 已就緒）
- **THEN** app container 啟動，Spring Boot 成功連線至 `DB_URL` 指定的 MySQL，Flyway migration 正常執行

#### Scenario: DB 未就緒時 app 啟動失敗（預期行為）
- **WHEN** 執行 `docker compose up` 但外部 MySQL 尚未運行
- **THEN** app 啟動失敗並輸出連線錯誤
