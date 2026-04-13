## Why

`CONFIG_DIR` 機制允許在容器外掛載一份 `application-local.yml` 覆蓋 Spring Boot 設定。但本專案的 `application.yml` 已將所有實際需要修改的設定暴露為 `${ENV_VAR:default}`，沒有任何設定需要透過 YAML 覆蓋層才能調整。這層機制帶來額外的部署步驟（建立目錄、複製 yml 範本）卻沒有帶來任何實際效益，應予移除。

## What Changes

- 從 `.env.example` 移除 `CONFIG_DIR` 及其說明注釋
- 從 `docker-compose.yml` 移除 volume mount `${CONFIG_DIR:-/tmp/attendance-config}:/config:ro`
- 從 `docker-compose.yml` 移除 `--spring.config.additional-location=optional:file:/config/` command 參數
- 刪除 `config/` 目錄（包含 `application-local.yml.example` 與 `README.md`）

## Capabilities

### New Capabilities
<!-- 無 -->

### Modified Capabilities
- `env-example-sync`: 移除 CONFIG_DIR key，.env.example 不再包含與 compose 無關的變數
- `local-config-guide`: 整個 capability 被廢棄，不再需要 application-local.yml.example 或 config/README.md

## Impact

- `.env.example`：移除 CONFIG_DIR 區塊
- `docker-compose.yml`：移除 volumes 中的 config mount、移除 command 中的 additional-location 參數
- `config/` 目錄：整個刪除
- 部署流程：簡化，不再需要 `mkdir -p $CONFIG_DIR` 步驟
