## 1. 移除 config/ 目錄

- [x] 1.1 刪除 `config/` 目錄（含 `application-local.yml.example` 與 `README.md`）

## 2. 更新 .env.example

- [x] 2.1 移除 `CONFIG_DIR` 行及其上方的說明注釋區塊（# ── 外部設定檔目錄 ── 區段）

## 3. 更新 docker-compose.yml

- [x] 3.1 移除 `volumes:` 中的 `${CONFIG_DIR:-/tmp/attendance-config}:/config:ro` 項目
- [x] 3.2 移除 `command:` 中的 `--spring.config.additional-location=optional:file:/config/` 參數

## 4. 驗證

- [x] 4.1 執行 `docker compose config` 確認 compose 設定無誤（無 config volume、無 additional-location）
- [x] 4.2 確認 `.env.example` 中不再有 `CONFIG_DIR`
