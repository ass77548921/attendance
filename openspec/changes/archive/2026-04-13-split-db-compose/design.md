## Context

目前 `docker-compose.yml` 結構：
- `db` service：mysql:8.4，含 volume、healthcheck、環境變數
- `app` service：Spring Boot，`depends_on: db: condition: service_healthy`

專案 repo 不應該包含任何資料庫建置設定。MySQL 是**機器維護人員負責的基礎設施**，應在機器上獨立建置與管理，與專案無關。`docker-compose.yml` 只負責「如何執行 app」，DB 連線資訊透過 `.env` 的 `DB_URL` 導入即可。

## Goals / Non-Goals

**Goals:**
- `docker-compose.yml` 只保留 `app` service，移除 `db` service 與 `mysql_data` volume
- `app` service 移除 `depends_on: db`
- 更新 `.env.example` 說明 DB 由外部管理，設定好 `DB_URL` 指向該 MySQL host

**Non-Goals:**
- 不新增任何 DB compose 檔案——MySQL 建置完全不在 repo 中
- 不修改 Dockerfile 或任何 Java / Spring Boot 程式碼
- 不引入 Docker Swarm 或 Kubernetes
- 不改變資料庫實際連線設定（DB_URL 等環境變數不變）

## Decisions

### Decision 1：MySQL 建置完全不放入 repo

**選擇**：移除 `db` service，不新增任何 DB compose 檔案

**理由**：
- Repo 只對「如何執行 app」負責，MySQL 屬於機器基礎設施，應由機器維護人員獨立建置（可用任何方式：Docker、裸機、RDS 等）
- 若將 DB compose 放入 repo，等後專案更換其他 DB 或使用雲端設施時需要修改 repo，這是錯誤的職責邊界
- 替代方案：`docker-compose.db.yml`——被排除，因為同樣違反此原則

### Decision 2：移除 `depends_on: db` 後的連線管理

**選擇**：移除 `depends_on: db`，改由 Spring Boot + Flyway 的預設重試機制處理

**理由**：
- `docker-compose.yml` 不再知道 `db` service 的存在，無法使用 `depends_on`
- 實務上 CI/CD 流程應確保 DB 已就緒再 deploy app（`mysqladmin ping` 可做為前置檢查）

## Risks / Trade-offs

- **風險：app 啟動時 DB 尚未就緒**：移除 `depends_on` 後，若機器上 DB 未啟動，app 會啟動失敗。→ 緩解：在部署腳本加入前置檢查，確認 MySQL 健康後再啟動 app
