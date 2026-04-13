## Context

目前專案使用 MySQL Flyway migration 建立 schema，但所有 `CREATE TABLE` 腳本都只定義結構，沒有在資料庫層保留 table 與 column 的語意說明。MySQL 支援 table comment 與 column comment，因此可以直接在 migration 中補上 description，而不需要引入額外 metadata table 或應用程式層對照檔。

這個 change 會影響目前所有建立資料表的 migration，包含使用者、打卡紀錄、補卡申請、設定、通知收件人、通知紀錄與附件等 schema。

另外，現有版本序列中包含數個明顯是 debug 後補修正的 migration：`V10__fix_enum_columns.sql`、`V11__fix_admin_password.sql`、`V12__fix_notification_recipients_name.sql`。若系統以新建服務方式初始化，這些修正應直接反映在基礎 migration，而不是保留為先建錯再修正的歷史。

## Goals / Non-Goals

**Goals:**
- 為所有 `CREATE TABLE` migration 補上 table description
- 為每個欄位補上 column description，讓 schema 在資料庫層即可讀懂用途
- 建立之後新增資料表時必須附帶 description 的明確規範
- 整理 migration 版本內容，將 debug 型修補整併回對應的基礎建表或 seed migration，讓新服務 bootstrap 直接得到最終正確 schema

**Non-Goals:**
- 不變更欄位型別、索引、constraint 或既有業務邏輯
- 不引入額外 schema registry、metadata table 或第三方 migration lint 工具
- 不處理非 `CREATE TABLE` 類型的 description 補齊，例如 index 或 constraint 命名優化
- 不保留「為了重現舊 debug 歷史」而存在的 migration 版本脈絡

## Decisions

### Decision 1: 使用 MySQL 原生 COMMENT 語法保存 description

**選擇**：在 `CREATE TABLE` 語句中使用 column-level `COMMENT '...'` 與 table-level `COMMENT='...'`。

**理由**：
- 可直接跟 schema 一起版本化，與 Flyway migration 保持同一來源
- 建表後可透過 MySQL metadata 直接查詢，不需依賴額外文件
- 不需新增任何 runtime 程式碼或自訂同步機制

**替代方案**：
- 以 README 或外部文件維護欄位說明：容易與實際 schema 漂移，排除
- 另建 metadata table 儲存 description：增加維護成本且無必要，排除

### Decision 2: 既有所有建表 migration 一次補齊 description

**選擇**：更新目前所有 `CREATE TABLE` migration，讓既有 schema 與未來規範一致。

**理由**：
- 若只約束未來 migration，現有主要資料表仍然缺少 description，規範會處於半套狀態
- 目前 migration 數量有限，集中補齊成本可控

**替代方案**：
- 僅在未來新表強制 description：無法解決既有 schema 可讀性問題，排除

### Decision 3: 將 debug 修補版內容回收進基礎 migration

**選擇**：把 `V10`、`V11`、`V12` 這類僅用於修正先前 schema/seed 問題的內容，整理回對應基礎版本，例如：
- `V10` 的 enum 型別與 `notification_logs` 欄位修正，回收到 `V1`、`V4`、`V7`
- `V11` 的 admin 密碼修正，回收到 `V8`
- `V12` 的 `notification_recipients.name` nullable 設定，回收到 `V6`

**理由**：
- 新建服務不需要保留 debug 過程形成的補丁歷史，應直接交付可初始化的最終版本
- 讓 migration 序列更容易理解，避免出現「先建立錯誤 schema 再靠後續版本修正」的閱讀成本

**替代方案**：
- 保留 `V10`–`V12`：適合既有正式上線系統的增量升級，但不適合目前以新服務方式交付的目標，排除

### Decision 4: 驗證以 migration 審查與整合啟動為主

**選擇**：以 migration 檔案檢查與 Flyway 啟動驗證為主要驗證手段，確保 comment 語法可成功建表。

**理由**：
- 這次變更只調整 schema 註解，不涉及 runtime 行為
- 主要風險在 SQL 語法正確性與是否有遺漏欄位 description

**替代方案**：
- 新增自動化 SQL lint：若目前專案沒有既有 lint 基礎，導入成本偏高，可作為後續強化而非本次必要範圍

## Risks / Trade-offs

- [Risk] 某些 migration 補 comment 時遺漏個別欄位 → Mitigation：逐一盤點所有 `CREATE TABLE` 檔案，依表檢查每個欄位是否帶有 `COMMENT`
- [Risk] comment 文案不一致或過度簡略 → Mitigation：以「業務意圖 + 單位/格式（若重要）」為撰寫原則，避免只重複欄位名稱
- [Risk] 回收 `V10`–`V12` 內容時遺漏既有修補邏輯 → Mitigation：逐檔比對修補版與原始建表/seed migration，確認最終 schema 與預設資料一致
- [Trade-off] schema 會變得更冗長 → Mitigation：接受較長的 migration，以換取資料庫層可讀性與交接成本下降