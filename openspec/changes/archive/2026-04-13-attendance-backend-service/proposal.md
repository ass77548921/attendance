## Why

目前缺乏一套統一的員工出勤管理後端系統，導致打卡記錄難以追蹤、遲到通知無法自動化、主管查勤效率低落。建立這套 Spring Boot 後端服務，能提供完整的出勤管理能力，並透過 Docker 容器化確保快速部署與環境一致性。

## What Changes

- 全新建立 Spring Boot + Gradle 後端專案，包含 Docker 容器化配置
- 新增身分認證與角色權限管理（員工 / 管理員）
- 新增打卡（上班 / 下班）與補打卡功能
- 新增遲到判斷邏輯與過早下班偵測（需滿 8 小時工作時長）
- 新增上下班時間範圍與午休時長設定（後台管理）
- 新增後台員工出勤紀錄查詢功能
- 新增遲到通知機制（Email，通知對象可由後台設定）
- 新增 API 規格文件（OpenAPI / Swagger）

## Capabilities

### New Capabilities
- `user-auth`: 使用者身份建立、登入驗證、角色權限管理（員工 / 管理員），包含 JWT Token 機制
- `attendance`: 打卡操作（上班打卡、下班打卡）與補打卡申請，記錄每筆打卡的時間戳記與狀態
- `attendance-rules`: 出勤規則設定，包含上下班時間範圍、午休時長、遲到判斷邏輯、過早下班偵測（未滿 8 小時工作時長）
- `admin-dashboard`: 後台管理功能，查詢員工出勤紀錄、設定遲到通知對象（指定收件人）
- `notification`: 遲到事件觸發 Email 通知，依後台設定發送給指定管理人員

### Modified Capabilities
<!-- 無現有 spec 需異動 -->

## Impact

- **新增專案結構**：全新 Spring Boot 專案（Gradle），無現有程式碼衝突
- **資料庫**：需設計使用者、打卡紀錄、出勤規則、通知設定等資料表
- **外部依賴**：SMTP / Email 服務（遲到通知）、JWT 函式庫
- **API 文件**：透過 Springdoc OpenAPI 自動產生 Swagger UI
- **部署**：Docker + Docker Compose，包含 App 容器與資料庫容器
