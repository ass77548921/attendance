## Why

目前系統已完成完整的後台管理 API（出勤查詢與手動調整、補打卡申請審核、帳號管理、郵件設定、出勤規則設定等），但管理員需要直接呼叫 REST API 才能操作，門檻過高且難以日常使用。需要一套網頁型後台操作介面，讓管理員能透過瀏覽器執行所有管理操作。

## What Changes

- 新增前端管理後台應用（`frontend/` 子目錄），使用輕量 SPA 架構
- 涵蓋功能：登入認證、出勤紀錄查詢與手動調整、補打卡申請審核、員工帳號管理、遲到通知收件人管理、郵件 SMTP 設定、出勤規則設定
- 後台介面以 JWT Bearer Token 與現有 Spring Boot API 溝通，不新增後端 endpoint
- docker-compose 增加前端服務，development 模式下透過 dev server proxy 存取 API

## Capabilities

### New Capabilities
- `admin-operations-console`: 網頁型管理後台 SPA，管理員登入後可操作全部管理功能，包含出勤紀錄管理、補打卡審核、帳號管理、通知設定、郵件設定、出勤規則設定

### Modified Capabilities
- `admin-dashboard`: 補充前端 API 契約細節——查詢出勤紀錄回應需包含 `latestAdjustment` 摘要欄位；手動調整需附帶 `reason`（必填）；回應欄位需與前端顯示需求對齊

## Impact

- 新增 `frontend/` 目錄（Node.js / Vite + React 技術棧）
- `docker-compose.yml` 新增 `frontend` service（開發模式）
- 現有後端 API 不受影響；前端以現有 API 為基礎，僅需確認欄位對齊
- 無資料庫 schema 異動
