## ADDED Requirements

### Requirement: Local Setup Guide SHALL Identify Backend Working Directory
Local development guidance SHALL explicitly instruct contributors to execute backend-related commands from the backend directory after repository restructuring.

#### Scenario: New contributor follows setup guide
- **WHEN** a new contributor executes local setup commands from documentation
- **THEN** the guide SHALL clearly indicate backend working-directory requirements and backend-relative command paths

## REMOVED Requirements

### Requirement: application-local.yml.example 只包含 application.yml 未暴露的設定項目
**Reason**: `application.yml` 已將所有實際需要修改的設定暴露為 `${ENV_VAR:default}`，沒有需要透過 YAML 覆蓋層調整的設定，此 capability 整體廢棄。
**Migration**: 不需遷移。所有設定改透過 `.env` → docker-compose `environment:` → Spring `${ENV_VAR}` 流通。

### Requirement: application-local.yml.example 所有值使用 ${ENV_VAR} 格式
**Reason**: 隨 application-local.yml.example 整體廢棄。
**Migration**: 不需遷移。

### Requirement: application-local.yml.example 包含正確的優先級說明
**Reason**: 隨 application-local.yml.example 整體廢棄。
**Migration**: 不需遷移。

### Requirement: .env.example 的 CONFIG_DIR 預設值為通用可用路徑
**Reason**: CONFIG_DIR 機制已移除，此 requirement 已無意義。
**Migration**: 不需遷移。CONFIG_DIR 從 `.env.example` 移除。

### Requirement: config/README.md 說明設定分工與初始化步驟
**Reason**: `config/` 目錄整個刪除，README 隨之廢棄。
**Migration**: 不需遷移。設定方式改由 `.env.example` 的注釋說明即可。
