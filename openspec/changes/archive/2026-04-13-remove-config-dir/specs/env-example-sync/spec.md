## MODIFIED Requirements

### Requirement: .env.example 中的 key 須與 docker-compose.yml 環境變數一一對應
`.env.example` 中的每一個 key SHALL 對應到 `docker-compose.yml` `environment:` 區塊中實際使用 `${VAR}` 語法引用的環境變數。用於 compose volumes 掛載路徑的變數（如 `CONFIG_DIR`）不屬於注入容器的環境變數，不得出現在 `.env.example` 中。

#### Scenario: 移除 NOTIFICATION_FROM_NAME
- **WHEN** 使用者複製 `.env.example` 為 `.env` 並填寫所有 key
- **THEN** 每個 key 都會被 docker compose 實際注入容器，不存在填了卻無效的 key

#### Scenario: 移除 UPLOAD_DIR
- **WHEN** 使用者查看 `.env.example`
- **THEN** 不存在 `UPLOAD_DIR` 這個 key，因為容器內路徑由 docker-compose.yml 固定設定

#### Scenario: 移除 CONFIG_DIR
- **WHEN** 使用者查看 `.env.example`
- **THEN** 不存在 `CONFIG_DIR` 這個 key，因為外部 config 掛載機制已被移除
