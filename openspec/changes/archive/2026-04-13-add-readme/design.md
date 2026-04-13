## Context

專案為 Java Spring Boot 後端打卡出勤管理系統，目前根目錄缺少 README.md。潛在使用者包括新進後端開發者、DevOps 工程師。

## Goals / Non-Goals

**Goals:**
- 在根目錄新增一份完整的 `README.md`
- 涵蓋：專案簡介、技術棧、本地啟動、環境變數、API 文件入口、測試、Docker 部署

**Non-Goals:**
- 不修改任何程式碼或配置檔
- 不建立多語言文件
- 不包含前端說明（本專案僅為後端服務）

## Decisions

**D1: 單一 README.md 置於根目錄**
- 理由：最符合 GitHub/GitLab 慣例，開發者首先查看根目錄
- 替代方案：docs/ 子目錄 → 不必要，規模尚小

**D2: 使用 Markdown 格式**
- 理由：與 openspec 文件一貫，且在 Git 平台自動渲染

**D3: 環境變數以表格呈現（含預設值）**
- 理由：對照 application.yml 中的環境變數，提供完整一覽

## Risks / Trade-offs

- [文件過時風險] 未來程式碼變更後 README 可能未同步更新 → 在 tasks 中標註維護提醒即可，屬低風險
