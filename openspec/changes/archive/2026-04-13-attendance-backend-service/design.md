## Context

本專案為全新開發的打卡後端服務，不涉及現有系統遷移。技術棧以 Spring Boot (Gradle) 為基礎，部署方式採用 Docker 容器化。需支援員工與管理員兩種角色，提供打卡、補打卡、出勤規則管理、後台查勤及遲到通知等功能。

**限制條件：**
- 須透過 Docker / Docker Compose 完成部署，不依賴特定雲平台
- Email 通知依賴外部 SMTP 服務（設定化，不綁定特定供應商）
- API 文件需自動化產生（Swagger UI）

## Goals / Non-Goals

**Goals:**
- 提供完整 RESTful API 支援所有功能需求
- 實作 JWT 身分驗證與 RBAC 角色權限控制（EMPLOYEE / ADMIN）
- 實作出勤規則引擎：遲到判斷、過早下班偵測、午休扣除
- 遲到事件自動觸發 Email 通知至後台設定的收件人
- 透過 Springdoc OpenAPI 自動產生 Swagger UI 規格文件
- Docker Compose 一鍵啟動 App + 資料庫

**Non-Goals:**
- 前端 UI 介面（本服務僅提供後端 API）
- 行動裝置 GPS / 地理圍欄打卡驗證
- 第三方 HR 系統整合
- 即時推播通知（僅支援 Email）

## Decisions

### 1. 技術框架：Spring Boot 3.x + Gradle
**選擇依據：** 符合需求指定。Gradle 較 Maven 更具彈性，Kotlin DSL 提升可讀性。Spring Boot 3.x 搭配 Jakarta EE 為現行主流 LTS 版本。
**替代方案：** Maven → 棄用，需求明確指定 Gradle。

### 2. 資料庫：PostgreSQL
**選擇依據：** 開源、成熟、支援 JSON 欄位擴充、適合容器化部署。Spring Data JPA + Hibernate ORM 提供良好整合。
**替代方案：** MySQL → 相近可行，但 PostgreSQL 在時區處理與 JSONB 支援上更佳；H2 → 僅適合測試環境。

### 3. 身分驗證：Spring Security + JWT（無狀態）
**選擇依據：** REST API 適合無狀態設計，JWT 不需 Session 持久化，水平擴展友善。
**替代方案：** Session-based → 需共享 Session 儲存（Redis），增加複雜度；OAuth2 → 過度設計，無外部 IdP 需求。
**Token 設計：** Access Token（短效，15 分鐘）+ Refresh Token（長效，7 天，儲存於 DB）

### 4. 角色權限：RBAC（EMPLOYEE / ADMIN）
- `EMPLOYEE`：打卡、補打卡申請、查看自身紀錄
- `ADMIN`：所有查詢、規則設定、補打卡審核、通知對象設定
- 透過 Spring Security `@PreAuthorize` 方法層級權限控制

### 5. 出勤規則引擎設計
規則統一存於 `attendance_config` 資料表，支援後台動態調整：
- `work_start_time`：上班時間（HH:mm），可設定緩衝區間（`late_tolerance_minutes`）
- `work_end_time`：下班時間（HH:mm）
- `lunch_break_minutes`：午休扣除分鐘數
- `required_work_minutes`：最低工作分鐘數（預設 480 = 8小時）

**遲到判斷：** 上班打卡時間 > `work_start_time + late_tolerance_minutes`
**過早下班：** 實際工作時長（下班打卡 - 上班打卡 - 午休）< `required_work_minutes`

### 6. 補打卡設計
補打卡為申請流程：
- 員工提交補打卡申請（指定日期、類型：CLOCK_IN / CLOCK_OUT、填寫原因）
- 管理員審核（APPROVED / REJECTED）
- 審核通過後系統自動調整該日出勤紀錄並重新計算遲到 / 早退狀態

### 7. 遲到通知機制：立即觸發（Event-Driven）
**決定：** 採即時觸發，取代原批次排程設計。
- 上班打卡完成且判定為遲到時，透過 Spring `ApplicationEventPublisher` 發布 `LateArrivalEvent`
- `NotificationListener` 以 `@Async` 非同步接收事件，避免阻塞打卡 API 回應
- 透過 Spring Mail 發送 HTML Email 給後台設定的通知收件人（`notification_recipients` 表）
- SMTP 設定透過環境變數注入（username / password 不寫死）
- 補打卡審核通過後若觸發遲到狀態（原本未遲到但補登後判定遲到），同樣發布事件

**替代方案：** 批次排程掃描 → 棄用，通知延遲不符合即時性需求。

### 8. API 文件：Springdoc OpenAPI 3.0
- 自動掃描 Controller 並產生 `/swagger-ui.html` Swagger UI
- 支援 JWT Bearer Token 認證測試

### 9. 容器化：Docker + Docker Compose
- `app` 容器：Spring Boot Fat JAR（multi-stage build 減少映像體積）
- `db` 容器：PostgreSQL 官方映像
- `docker-compose.yml` 管理服務依賴與環境變數注入
- 健康檢查確保 DB 就緒後才啟動 App

### 10. 資料表設計概覽

| 資料表 | 用途 |
|--------|------|
| `users` | 使用者帳號、角色、狀態 |
| `refresh_tokens` | JWT Refresh Token 管理 |
| `attendance_records` | 每日打卡紀錄（上班 / 下班時間、遲到狀態） |
| `attendance_amendments` | 補打卡申請與審核狀態 |
| `amendment_attachments` | 補打卡附件紀錄（檔名、儲存路徑、MIME type） |
| `attendance_config` | 出勤規則設定（單一全局配置） |
| `notification_recipients` | 遲到通知收件人清單 |
| `notification_logs` | 通知發送紀錄（含結果狀態） |

### 11. 補打卡附件上傳
**決定：** 支援補打卡申請附上截圖佐證（選填，最多 5 個檔案，每檔限 10 MB）。
- API 採 `multipart/form-data`，附件欄位名稱為 `attachments`
- 允許的 MIME type：`image/jpeg`、`image/png`、`image/gif`、`application/pdf`
- 檔案儲存至 Docker Volume 掛載的本地路徑（`/app/uploads/amendments/{amendmentId}/`）
- 附件元資料（原始檔名、儲存路徑、MIME type、大小）儲存於 `amendment_attachments` 表
- 管理員審核介面可透過 `GET /api/admin/amendments/{id}/attachments/{fileId}` 下載附件
- 儲存路徑透過環境變數 `UPLOAD_DIR` 注入，配合 Docker volume 掛載

**安全性：** 下載附件 API 需驗證 JWT，且僅允許 ADMIN 或附件所屬申請人存取；儲存路徑做路徑穿越（Path Traversal）防護。

**替代方案：** MinIO / S3 物件儲存 → 引入外部依賴，目前規模不必要；DB BLOB → 影響資料庫效能，棄用。

## Risks / Trade-offs

- **[SMTP 設定錯誤] → 通知靜默失敗** → 緩解：加入通知發送日誌與失敗重試（`@Retryable`），Admin API 提供手動觸發測試郵件端點
- **[JWT Token 無狀態，無法即時撤銷] → 安全性** → 緩解：Access Token 短效（15分鐘）+ Refresh Token 儲存 DB 可主動撤銷
- **[補打卡審核後重新計算出勤狀態] → 計算複雜度** → 緩解：審核事件觸發單一記錄重算，非全表掃描
- **[時區問題] → 跨時區打卡誤判** → 緩解：所有時間儲存 UTC，顯示層轉換，`attendance_config` 設定 `timezone` 欄位

## Migration Plan

1. 初始化 PostgreSQL，執行 Flyway/Liquibase 資料庫遷移腳本
2. 建立初始 ADMIN 帳號（透過 `data.sql` 或初始化 API）
3. 設定 `attendance_config` 基準規則（上下班時間、午休分鐘數）
4. 設定通知收件人
5. 透過 `docker-compose up` 一鍵啟動服務

**Rollback：** 容器映像版本標記，可快速切回前一版本；資料庫 Migration 支援 down script。

## Open Questions

- ✅ **補打卡申請是否需要上傳附件（如截圖佐證）？** → **需要**。已加入附件上傳設計（Decision #11），採本地 Docker Volume 儲存，最多 5 個檔案，限 JPEG / PNG / GIF / PDF，每檔限 10 MB。
- ✅ **是否需要多套出勤設定（不同部門不同規則）？** → **不需要**。維持全局單一設定（`attendance_config`），不做部門級拆分。
- ✅ **遲到通知是否需要立即觸發（打卡後即時）或批次（每日定時）？** → **立即觸發**。已改為 Event-Driven 設計（Decision #7），打卡判定遲到後立即以 `@Async` 發送通知，不依賴排程。
