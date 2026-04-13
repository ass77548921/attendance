## Context

`docker-compose.yml` 目前掛載 `${CONFIG_DIR}:/config:ro`，並透過 `--spring.config.additional-location=optional:file:/config/` 讓 Spring Boot 讀取外部 YAML。這個設計原意是讓部署者能在不修改 JAR 的情況下覆蓋 Spring Boot 設定。

然而 `application.yml` 已將所有實際需要修改的設定（DB、JWT、SMTP、port、upload dir）暴露為 `${ENV_VAR:default}`，透過 docker-compose `environment:` 注入即可，不需要 YAML 層。`config/` 目錄的範本與 README 因此成為空轉的維護負擔。

## Goals / Non-Goals

**Goals:**
- 移除 `CONFIG_DIR` 相關的所有設定與檔案
- 讓 `.env` → compose `environment:` → Spring `${ENV_VAR}` 成為唯一的設定路徑
- 簡化部署步驟（不需要 `mkdir -p $CONFIG_DIR` 或複製 yml 範本）

**Non-Goals:**
- 不修改 `application.yml` 的 `${ENV_VAR:default}` 結構
- 不移除其他 volume（`uploads_data` 保留）
- 不調整 Spring profile 機制（`SPRING_PROFILES_ACTIVE: prod` 保留）

## Decisions

**決策：直接刪除 `config/` 目錄，不保留任何形式的 YAML 覆蓋機制**

理由：所有設定已透過 env var 暴露，無需 YAML 覆蓋層。若未來有需要，可另立 change 補回，比維護一個閒置機制的成本更低。

替代方案考慮：僅「隱藏」此功能（保留 config/ 但從文件移除說明）→ 拒絕，會造成 repo 雜訊且讓使用者困惑。

**決策：從 compose `command:` 移除 `--spring.config.additional-location` 而非保留為空**

理由：保留無效參數會讓人誤以為這是必要的啟動參數。移除後 Spring Boot 預設行為完全足夠（讀取 classpath application.yml + 環境變數）。

## Risks / Trade-offs

- [風險] 若有人在 `CONFIG_DIR` 中放了自訂 YAML 並依賴它 → 遷移前應告知，但本專案目前沒有已知的外部部署，風險極低
- [Trade-off] 未來若需要在不重建 image 的情況下調整 `logging.level` 等 JAR 內設定，需另行設計（如透過新增 env var 暴露）→ 可接受，屬未來需求

## Migration Plan

1. 刪除 `config/` 目錄
2. 編輯 `.env.example`：移除 `CONFIG_DIR` 行及相關注釋
3. 編輯 `docker-compose.yml`：移除 config volume mount 和 command 中的 additional-location 參數
4. 無 rollback 需求（本機開發專案，git 可回復）
