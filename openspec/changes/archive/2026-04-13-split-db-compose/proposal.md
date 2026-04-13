## Why

目前 `docker-compose.yml` 將 `app`（Spring Boot）與 `db`（MySQL）綁在同一個 compose 檔案中。在 CI/CD 流程中，每次部署 app image 會同時觸發 DB service 的重建，而 MySQL 應該是**機器上長期穩定運行的基礎設施**，不應隨 app 部署週期重啟。此外，從映象架構的角度，app image 不應與 DB 綁定——兩者應可獨立啟動與管理。

## What Changes

- **新增 `docker-compose.db.yml`**（或 `docker-compose.infra.yml`）：只包含 `db` service（MySQL），作為機器基礎設施層，手動啟動一次後長期運行
- **修改 `docker-compose.yml`**：只保留 `app` service，移除 `db` service 定義；`depends_on` 改為依賴外部 DB，透過 `DB_URL` 環境變數連線（db 已在外部運行）
- **更新 `.env.example`**：說明兩個 compose 檔案的啟動方式與分工

## Capabilities

### New Capabilities

- `compose-split`：定義 DB 與 app compose 檔案的分工，讓 CI/CD 只需管理 app service，DB 由機器維運人員獨立啟動

### Modified Capabilities

（無 spec 層級行為變更）

## Impact

- `docker-compose.yml`：移除 `db` service、`mysql_data` volume 定義；`app` service 移除 `depends_on: db`（改由啟動前確認 DB 就緒）
- `docker-compose.db.yml`：新增，包含 `db` service 與 `mysql_data` volume
- `.env.example`：更新啟動說明
- **不影響 Dockerfile、Spring Boot 程式碼、任何 Java 檔案**
