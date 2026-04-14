# 出勤管理系統 (Attendance Backend)

一套基於 Spring Boot 的後端出勤打卡管理系統，提供員工打卡、出勤補登、遲到通知等核心功能，並附完整的管理員後台 API。

## 專案目錄重點

- `backend/`：後端專案根目錄（Gradle、Java 原始碼、Dockerfile）
- 根目錄：專案協作與整合層（OpenSpec、Compose、環境檔、文件）

## 主要功能

- **使用者認證**：JWT 登入（Access Token + Refresh Token）、角色權限控管（員工 / 管理員）
- **打卡出勤**：上下班打卡、出勤記錄查詢
- **出勤補登**：員工提交補登申請（含附件）、管理員審核
- **遲到通知**：自動偵測遲到事件並發送 Email 通知
- **管理員後台**：使用者管理、出勤設定、通知收件人管理、人工修正審計、郵件設定管理

---

## 技術棧

| 類別 | 技術 |
|------|------|
| 語言 | Java 17 |
| 框架 | Spring Boot 3.2 |
| 資料庫 | MySQL 8 |
| ORM | Spring Data JPA / Hibernate |
| Migration | Flyway |
| 認證 | JWT (jjwt 0.12) + Spring Security |
| 郵件 | Spring Mail + Thymeleaf 模板 |
| API 文件 | SpringDoc OpenAPI (Swagger UI) |
| 測試 | JUnit 5 + Testcontainers (MySQL) |
| 建構 | Gradle (Kotlin DSL) |
| 容器 | Docker / Docker Compose |

---

## 本地開發啟動

### 前置需求

- **Java 17+** — 建議使用 [SDKMAN](https://sdkman.io/) 管理版本
- **Docker** — 用於本地資料庫及整合測試（Testcontainers 需要）

### 步驟

**1. 複製環境變數設定**

```bash
cp .env.example .env
```

編輯 `.env`，至少填入 `DB_PASSWORD` 與 `JWT_SECRET`（其餘可使用預設值）。

**2. 啟動本地 MySQL**

```bash
docker run -d \
  --name attendance-mysql \
  -e MYSQL_DATABASE=attendance \
  -e MYSQL_USER=attendance \
  -e MYSQL_PASSWORD=attendance \
  -p 3306:3306 \
  mysql:8
```

> 若已有外部 MySQL，請修改 `.env` 中的 `DB_URL`、`DB_USERNAME`、`DB_PASSWORD`。

**3. 啟動應用程式**

```bash
cd backend
./gradlew bootRun
```

應用將在 `http://localhost:8080` 啟動，Flyway 會自動執行資料庫 Migration。

---

## 環境變數

| 變數名稱 | 說明 | 預設值 / 範例 |
|----------|------|--------------|
| `DB_URL` | MySQL JDBC 連線 URL | `jdbc:mysql://localhost:3306/attendance?...` |
| `DB_USERNAME` | 資料庫使用者 | `attendance` |
| `DB_PASSWORD` | 資料庫密碼 | _(必填)_ |
| `JWT_SECRET` | JWT 簽名金鑰（建議 256 bits 以上） | `openssl rand -base64 64` |
| `JWT_ACCESS_EXPIRY_MS` | Access Token 有效期（毫秒） | `900000`（15 分鐘）|
| `JWT_REFRESH_EXPIRY_MS` | Refresh Token 有效期（毫秒） | `604800000`（7 天）|
| `SMTP_HOST` | SMTP 伺服器 | `smtp.gmail.com` |
| `SMTP_PORT` | SMTP 連接埠 | `587` |
| `SMTP_USERNAME` | SMTP 帳號 | `your-email@gmail.com` |
| `SMTP_PASSWORD` | SMTP 密碼（Gmail 請用 App Password） | _(必填)_ |
| `NOTIFICATION_FROM_EMAIL` | 通知寄件人地址 | `noreply@attendance.local` |
| `APP_SECURITY_SETTINGS_ENCRYPTION_KEY` | 後台郵件設定密碼加密金鑰 | `attendance-settings-default-key` |
| `SERVER_PORT` | 應用程式監聽埠 | `8080` |
| `UPLOAD_DIR` | 附件上傳目錄（容器內路徑） | `/app/uploads` |

> Gmail App Password 申請：https://myaccount.google.com/apppasswords

---

## API 文件

應用啟動後，可透過以下路徑存取 API 文件：

| 說明 | URL |
|------|-----|
| Swagger UI（互動式） | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON 規格 | http://localhost:8080/api-docs |

**步驟：**

1. 先進入 `backend/` 後啟動應用（`./gradlew bootRun`）
2. 瀏覽器開啟 `http://localhost:8080/swagger-ui.html`
3. 使用預設管理員帳號登入，取得 JWT Token
4. 點擊 Swagger UI 右上角「Authorize」，填入 `Bearer <token>` 後即可測試所有 API

> 預設管理員帳號：`admin` / `admin123`（首次啟動由 Flyway Migration 建立）

---

## 管理後台 API 擴充重點

- **出勤查詢回傳修正摘要**：`GET /api/admin/attendance`
- **手動修正出勤**（需理由）：`PATCH /api/admin/attendance/{id}/adjust`
- **查詢修正歷史**：`GET /api/admin/attendance/{id}/adjustments`
- **規則設定驗證**：更新出勤時間設定時，強制 `workStartTime < workEndTime`
- **郵件設定管理**：
  - `GET /api/admin/mail-settings`
  - `PUT /api/admin/mail-settings`（回應不暴露密碼明文）
  - `POST /api/admin/mail-settings/test`（失敗時回傳可診斷訊息）

> 所有 `/api/admin/**` 端點皆僅限 `ADMIN` 角色，且帳號被設為 `INACTIVE` 後，既有 JWT 也無法再通過授權。

---

## 測試

測試使用 Testcontainers，**執行前請確認 Docker 已在背景運作**。

```bash
cd backend
./gradlew test
```

測試報告輸出於：`backend/build/reports/tests/test/index.html`

---

## Docker 部署

**1. 建立 `.env` 設定檔**

```bash
cp .env.example .env
# 編輯 .env，填入所有必要的正式環境設定
```

**2. 啟動應用**

```bash
docker compose up -d
```

> `docker-compose.yml` 僅包含應用容器本身，MySQL 請自行於外部維護並設定 `DB_URL`。

**停止應用**

```bash
docker compose down
```

---

## 結構遷移回滾說明

- 若 backend 目錄重整後需回滾，請直接回退本次「目錄遷移」相關 commit，即可恢復原本 root 結構。
- 本次變更僅涉及檔案路徑與執行入口調整，未新增或修改資料庫 migration 腳本。
- 因此回滾不需要額外執行持久化資料遷移；資料庫內容可沿用現有狀態。
