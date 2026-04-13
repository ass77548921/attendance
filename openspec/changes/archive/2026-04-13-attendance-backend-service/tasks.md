## 1. 專案初始化與基礎設定

- [x] 1.1 建立 Spring Boot 3.x + Gradle (Kotlin DSL) 專案結構（src/main/java 分層：controller / service / repository / domain / config / exception）
- [x] 1.2 設定 build.gradle.kts：加入 Spring Web、Spring Security、Spring Data JPA、Spring Mail、Spring Scheduler、Springdoc OpenAPI、JWT（jjwt）、PostgreSQL Driver、Flyway、Lombok 依賴
- [x] 1.3 建立 application.yml（含多環境 profile：dev / prod），設定資料庫連線、JWT secret、SMTP、時區等環境變數佔位符
- [x] 1.4 建立 Dockerfile（multi-stage build：Gradle build stage + JRE runtime stage）
- [x] 1.5 建立 docker-compose.yml（app 容器 + PostgreSQL 容器，含健康檢查與環境變數注入）
- [x] 1.6 建立 .env.example 範本，說明所有必要環境變數

## 2. 資料庫 Schema 設計（Flyway Migration）

- [x] 2.1 建立 V1__create_users.sql：users 表（id, username, password_hash, full_name, email, role, status, created_at）
- [x] 2.2 建立 V2__create_refresh_tokens.sql：refresh_tokens 表（id, user_id, token_hash, expires_at, created_at）
- [x] 2.3 建立 V3__create_attendance_records.sql：attendance_records 表（id, user_id, work_date, clock_in_time, clock_out_time, is_late, late_minutes, is_early_leave, short_minutes, notification_sent, created_at, updated_at）
- [x] 2.4 建立 V4__create_attendance_amendments.sql：attendance_amendments 表（id, user_id, target_date, amendment_type, amended_time, reason, status, review_note, reviewed_by, reviewed_at, created_at）
- [x] 2.5 建立 V5__create_attendance_config.sql：attendance_config 表（id, work_start_time, work_end_time, late_tolerance_minutes, lunch_break_minutes, required_work_minutes, timezone, updated_at）並插入預設值（09:00, 18:00, 10, 60, 480, Asia/Taipei）
- [x] 2.6 建立 V6__create_notification_recipients.sql：notification_recipients 表（id, name, email, active, created_at）
- [x] 2.7 建立 V7__create_notification_logs.sql：notification_logs 表（id, recipient_email, user_id, work_date, result, error_message, sent_at）
- [x] 2.8 建立 V8__insert_default_admin.sql：插入預設管理員帳號（密碼以 BCrypt 雜湊）- [x] 2.9 建立 V9__create_amendment_attachments.sql：amendment_attachments 表（id, amendment_id FK, original_filename, stored_path, mime_type, file_size, created_at）
## 3. 身分驗證與權限（user-auth）

- [x] 3.1 建立 User entity、UserRepository、UserRole enum（EMPLOYEE / ADMIN）、UserStatus enum（ACTIVE / INACTIVE）
- [x] 3.2 實作 UserDetailsService 整合 Spring Security，依 username 載入使用者
- [x] 3.3 實作 JwtService：產生 Access Token（15min）、驗證 Token、解析 Claims
- [x] 3.4 建立 RefreshToken entity、RefreshTokenRepository，實作 Refresh Token 儲存、驗證與撤銷
- [x] 3.5 實作 JwtAuthenticationFilter（OncePerRequestFilter）：從 Authorization header 解析並驗證 JWT
- [x] 3.6 設定 SecurityFilterChain：公開路徑（/api/auth/**、/swagger-ui/**）、角色保護路徑
- [x] 3.7 實作 AuthController：POST /api/auth/login、POST /api/auth/refresh、POST /api/auth/logout
- [x] 3.8 實作 UserController（Admin only）：POST /api/admin/users（建立帳號）、GET /api/admin/users（列表）、PATCH /api/admin/users/{id}/status（停用 / 啟用）
- [x] 3.9 撰寫 AuthController 整合測試：登入成功、登入失敗、Token 刷新、登出

## 4. 打卡功能（attendance）

- [x] 4.1 建立 AttendanceRecord entity 與 AttendanceRecordRepository（含依 userId + workDate 查詢）
- [x] 4.2 實作 AttendanceService：上班打卡邏輯（重複打卡檢查、時間記錄、觸發規則計算）
- [x] 4.3 實作 AttendanceService：下班打卡邏輯（需先有上班打卡、計算工作時長、觸發規則計算）
- [x] 4.4 實作 AttendanceController：POST /api/attendance/clock-in、POST /api/attendance/clock-out、GET /api/attendance/today
- [x] 4.5 實作 AttendanceController：GET /api/attendance（員工查詢個人紀錄，支援 startDate/endDate 參數）
- [x] 4.6 建立 AttendanceAmendment entity、AmendmentAttachment entity、對應 Repository
- [x] 4.7 實作 FileStorageService：驗證 MIME type（JPEG/PNG/GIF/PDF）、驗證檔案數附大小限制、防止路徑穿越（Path Traversal）、將附件存至 `UPLOAD_DIR/{amendmentId}/` Volume 路徑
- [x] 4.8 實作 AmendmentService：提交補打卡申請（multipart/form-data，重複申請檢查、附件儲存）、審核（APPROVED 觸發重算）、查詢列表
- [x] 4.9 實作 AmendmentController：POST /api/attendance/amendments（multipart）、GET /api/attendance/amendments（員工查詢自身）
- [x] 4.10 實作附件下載 API：GET /api/admin/amendments/{id}/attachments/{fileId}（驗證所屬申請、回傳檔案串流）
- [x] 4.11 撰寫打卡功能整合測試：上班打卡、下班打卡、重複打卡衝突、補打卡附件上傳與下載

## 5. 出勤規則引擎（attendance-rules）

- [x] 5.1 建立 AttendanceConfig entity 與 AttendanceConfigRepository（確保表中永遠只有一筆設定）
- [x] 5.2 實作 AttendanceRuleEngine：計算遲到（isLate、lateMinutes）
- [x] 5.3 實作 AttendanceRuleEngine：計算過早下班（isEarlyLeave、shortMinutes），扣除午休時間
- [x] 5.4 整合 AttendanceRuleEngine 至上下班打卡流程（打卡後即時計算）
- [x] 5.5 實作補打卡核准後重新計算邏輯（AmendmentService.approve() 中呼叫 ruleEngine.recalculate()）
- [x] 5.6 實作 AttendanceConfigController（Admin only）：GET /api/admin/config、PUT /api/admin/config
- [x] 5.7 撰寫規則引擎單元測試：遲到邊界（含容忍時間）、過早下班邊界、午休時長扣除

## 6. 後台管理（admin-dashboard）

- [x] 6.1 實作 AdminAttendanceController：GET /api/admin/attendance（支援 userId、startDate、endDate、isLate 篩選，分頁）
- [x] 6.2 實作 AdminAttendanceController：GET /api/admin/attendance/summary（月度統計：遲到次數、正常天數）
- [x] 6.3 實作 AdminAmendmentController：GET /api/admin/amendments（查詢待審核申請）、POST /api/admin/amendments/{id}/review（審核）
- [x] 6.4 建立 NotificationRecipient entity、NotificationRecipientRepository
- [x] 6.5 實作 NotificationRecipientController（Admin only）：GET /api/admin/notification/recipients、POST、DELETE /{id}

## 7. 遲到通知機制（notification — Event-Driven）

- [x] 7.1 設定 Spring Mail（JavaMailSender）：SMTP host/port/username/password 透過環境變數注入
- [x] 7.2 建立 Email 通知模板（HTML，含員工姓名、遲到日期、遲到分鐘數）
- [x] 7.3 建立 NotificationLog entity 與 NotificationLogRepository
- [x] 7.4 建立 `LateArrivalEvent`（Spring ApplicationEvent 子類別，含 userId、workDate、lateMinutes）
- [x] 7.5 實作 NotificationService：以 `@Async` + `@EventListener` 接收 LateArrivalEvent，對所有 active 收件人發送 Email、記錄 NotificationLog
- [x] 7.6 實作 NotificationService：發送失敗 @Retryable 重試（最多 3 次，間隔 2 分鐘）
- [x] 7.7 在 AttendanceService.clockIn() 判定遲到後，透過 ApplicationEventPublisher 發布 LateArrivalEvent
- [x] 7.8 在 AmendmentService.approve() 重算後，若遲到狀態發生變化則發布 LateArrivalEvent
- [x] 7.9 設定 `@EnableAsync` 與 TaskExecutor Bean（AsyncConfig）
- [x] 7.10 實作管理員手動測試通知 API：POST /api/admin/notification/test
- [x] 7.11 實作 AdminNotificationController：GET /api/admin/notification/logs（查詢通知發送紀錄）

## 8. API 文件（Springdoc OpenAPI）

- [x] 8.1 加入 Springdoc OpenAPI 依賴並設定 API 基本資訊（title、version、description）
- [x] 8.2 設定 SecurityScheme（BearerAuth / JWT）至 OpenAPI 設定，使 Swagger UI 支援 Token 填入
- [x] 8.3 為所有 Controller 加入 @Operation、@ApiResponse 等 Swagger 註解
- [x] 8.4 驗證 /swagger-ui.html 可正常存取並顯示所有 API 端點

## 9. 整合測試與品質

- [x] 9.1 設定 Testcontainers（PostgreSQL）供整合測試使用
- [x] 9.2 撰寫 user-auth 整合測試：登入流程、JWT 驗證、權限控制
- [x] 9.3 撰寫 attendance 整合測試：完整打卡流程（上班→下班→查詢）
- [x] 9.4 撰寫 notification 整合測試（Mock SMTP）：排程觸發通知發送
- [x] 9.5 驗證 Docker Compose 啟動流程：執行 docker-compose up 確認服務正常運行並可存取 Swagger UI
