## 1. 修改 docker-compose.yml

- [x] 1.1 `docker-compose.yml`：移除 `db` service 整個區塊
- [x] 1.2 `docker-compose.yml`：移除 `app` service 的 `depends_on: db`
- [x] 1.3 `docker-compose.yml`：移除 `volumes:` 區塊中的 `mysql_data:`

## 2. 更新 .env.example

- [x] 2.1 `.env.example`：在資料庫設定區塊更新說明——MySQL 為外部基礎設施，請自行在機器上建置，設定好 `DB_URL`、`DB_USERNAME`、`DB_PASSWORD` 指向該 MySQL host
- [x] 2.2 `.env.example`：移除 `DB_ROOT_PASSWORD`（只有 compose 內建 MySQL 時才需要）

## 3. 驗證

- [x] 3.1 執行 `docker compose up --build`，確認 app 成功連線至外部 MySQL 並完成 Flyway migration
