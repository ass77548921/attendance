## 1. 補齊建表 migration description

- [x] 1.1 盤點 `src/main/resources/db/migration/` 中所有包含 `CREATE TABLE` 的 migration，確認需要補齊 description 的資料表與欄位清單
- [x] 1.2 更新 `V1__create_users.sql`、`V2__create_refresh_tokens.sql`、`V3__create_attendance_records.sql`、`V4__create_attendance_amendments.sql`，為 table 與所有欄位加入 description
- [x] 1.3 更新 `V5__create_attendance_config.sql`、`V6__create_notification_recipients.sql`、`V7__create_notification_logs.sql`、`V9__create_amendment_attachments.sql`，為 table 與所有欄位加入 description

## 2. 整理 migration 版本內容

- [x] 2.1 盤點 `V10__fix_enum_columns.sql`、`V11__fix_admin_password.sql`、`V12__fix_notification_recipients_name.sql` 的修補內容，對應回原始建表或 seed migration
- [x] 2.2 更新 `V1`、`V4`、`V6`、`V7`、`V8` 等基礎 migration，直接反映目前實際需要的最終 schema 與 seed 狀態
- [x] 2.3 移除、清空或以其他一致方式淘汰僅為 debug 修補而存在的 migration 版本，讓新建服務的版本序列保持乾淨

## 3. 驗證 schema 文件化規範與版本整理結果

- [x] 3.1 檢查每個 `CREATE TABLE` 是否同時包含 table-level 與 column-level description，確認沒有遺漏任何欄位
- [x] 3.2 啟動 Flyway migration 驗證整理後的版本序列可成功建表，且不再依賴 debug patch 才得到正確 schema
- [x] 3.3 執行既有測試或最小必要驗證，確認本次變更未影響既有資料庫相關行為