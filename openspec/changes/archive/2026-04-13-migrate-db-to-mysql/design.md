## Context

專案目前使用 PostgreSQL 16，以 Flyway 管理 9 個 migration 腳本（V1–V9）。應用程式使用 Spring Data JPA + Hibernate，SQL migration 中大量使用了 PostgreSQL 特有語法。需要將整個資料庫層切換至 MySQL 8.4，同時保持所有業務邏輯、API 行為不變。

**PostgreSQL 特有語法一覽（需全部替換）：**

| PostgreSQL | MySQL 對應 |
|---|---|
| `BIGSERIAL PRIMARY KEY` | `BIGINT AUTO_INCREMENT PRIMARY KEY` |
| `TIMESTAMPTZ` | `DATETIME(6)` |
| `BOOLEAN` | `TINYINT(1)` |
| `TEXT` | `LONGTEXT` |
| `NOW()` | `CURRENT_TIMESTAMP(6)` |
| `CHECK (col IN (...))` | 移除（MySQL 8 支援，但 Hibernate dialect 行為不同，改用 application-level 驗證） |
| `CREATE UNIQUE INDEX ... ON table ((TRUE))` | 改為 application-level 保護（MySQL 不支援 expression index 用於常數，改為 trigger 或程式邏輯確保 singleton） |

## Goals / Non-Goals

**Goals:**
- 將 `build.gradle.kts` 的資料庫 driver 改為 `mysql-connector-j`
- 重寫 V1–V9 的 Flyway migration，使用純 MySQL 8 語法
- 更新 `docker-compose.yml` 使用 `mysql:8.4` container
- 更新 `application.yml` 與 `application-test.yml` 的 datasource 設定
- 更新 Testcontainers 使用 MySQL image
- 更新 `.env.example` 加入 `DB_ROOT_PASSWORD`

**Non-Goals:**
- 不修改任何 JPA Entity、Service、Controller 程式碼
- 不更改任何 API 行為
- 不做資料遷移（將已有線上資料從 PostgreSQL 搬到 MySQL）

## Decisions

### Decision 1：MySQL 版本選用 8.4 LTS

**選擇**：`mysql:8.4`（Docker image），`mysql-connector-j` 8.x

**理由**：8.4 是目前的 LTS 版，支援 `DATETIME(6)`（微秒精度，對應 `Instant`）、`TINYINT(1)` for boolean、完整的 `CHECK` 約束。比 5.7 有更好的 JSON/CTE 支援。

### Decision 2：`TIMESTAMPTZ` → `DATETIME(6)` + UTC session

**選擇**：使用 `DATETIME(6)` 搭配 JDBC URL 參數 `serverTimezone=UTC`

**理由**：
- MySQL 的 `TIMESTAMP` 只支援到 2038 年（Unix time overflow），`DATETIME` 無此限制
- Hibernate 對 `Instant` 與 `DATETIME(6)` 的映射需要 `serverTimezone=UTC` 確保時區一致
- 替代：`TIMESTAMP(6)` — 有 2038 問題，排除

**JDBC URL 格式**：
```
jdbc:mysql://host:3306/attendance?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8
```

### Decision 3：`BIGSERIAL` → `BIGINT AUTO_INCREMENT`

**選擇**：全部主鍵改為 `BIGINT NOT NULL AUTO_INCREMENT`

**理由**：MySQL 沒有 sequence 物件，`AUTO_INCREMENT` 是等價做法，Hibernate 的 `GenerationType.IDENTITY` 對兩者都通用。

### Decision 4：Singleton attendance_config 的唯一性保護

**選擇**：移除 `CREATE UNIQUE INDEX ... ON attendance_config ((TRUE))`（MySQL 不支援 expression index on constant），改為在 Flyway 初始 INSERT 後由 `AttendanceConfigService.getConfig()` 的查詢邏輯（`findTopByOrderByIdAsc`）自然保護。

**理由**：此表只有 Flyway 初始 seeding 會 INSERT，運行期只有 UPDATE，不會有第二筆產生的途徑。應用程式層已處理，不需要 DB-level constraint。

### Decision 5：Flyway migration 目錄策略

**選擇**：直接就地改寫 V1–V9（不建立新目錄）

**理由**：這是全新部署的 migration 替換，不是在已有 PostgreSQL DB 上做遷移。線上環境若需要資料遷移，屬於部署策略問題（超出本 change 範圍）。

## Risks / Trade-offs

- **風險：MySQL `CHECK` 約束在舊版行為**：MySQL 8.0.16+ 正式支援 `CHECK`，但為安全起見 V1 的 `CHECK (role IN (...))` 仍保留為 MySQL 語法（MySQL 已支援），Hibernate validator 會做 application-level 二次驗證。→ 無額外風險
- **風險：`BOOLEAN` → `TINYINT(1)` 的 Hibernate 映射**：Hibernate 6 (Spring Boot 3.x) 對 MySQL `TINYINT(1)` 默認映射為 `boolean`，無需修改 Entity。→ 無需額外設定
- **取捨：`TEXT` → `LONGTEXT`**：MySQL 的 `TEXT` 最大 65KB，`LONGTEXT` 最大 4GB，使用 `LONGTEXT` 保守確保 `reason`、`review_note`、`error_message` 欄位不受限制。`MEDIUMTEXT`（16MB）也足夠，但 `LONGTEXT` 更保險且無效能差異。
- **風險：Testcontainers MySQL 映像較大，CI 下載時間較長**：相比 PostgreSQL，MySQL image 體積較大。→ 可透過 CI cache 緩解

## Migration Plan

1. 修改 `build.gradle.kts`（driver replacement）
2. 重寫 V1–V9 SQL migration 腳本
3. 更新 `application.yml` datasource 設定（含 JDBC URL 參數）
4. 更新 `docker-compose.yml`（MySQL container）
5. 更新 `application-test.yml` 與 Testcontainers
6. 更新 `.env.example`
7. 本機執行 `docker compose up --build` 驗證 Flyway migration 全部通過
8. 執行 `./gradlew test` 確認整合測試通過

**Rollback**：還原上述所有檔案至 PostgreSQL 版本（git revert）

## Open Questions

（無）
