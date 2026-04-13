## Why

目前的 Flyway migration 會建立資料表與欄位，但沒有要求在 schema 層提供 description，導致新表結構的業務用途只能靠欄位命名或額外文件推測。當資料庫持續演進時，缺少 table 與 column description 會提高維護、交接與查核成本。

此外，目前 migration 版本中仍保留一些先前 debug 過程產生的修補型版本，像是 schema 欄位修正、預設資料修正與 nullable 調整。若此專案要以「新建服務」方式交付，初始 migration 應直接反映最終正確 schema，而不是依賴後續 debug patch 疊加。

## What Changes

- 新增一項資料庫 schema 文件化能力，要求所有新建資料表在建立時必須包含 table description
- 要求 `CREATE TABLE` 內的每個欄位都必須包含對應的 column description，而不是只描述部分欄位
- 將現有建立資料表的 Flyway migration 補齊 description，讓既有 schema 與新規範一致
- 整理現有 migration 版本內容，將僅因 debug 產生的 schema 修補回收進對應的基礎建表或 seed migration，讓新建服務時的版本序列保持乾淨
- 補充 migration 撰寫準則或驗證方式，避免未來新增資料表時遺漏 description

## Capabilities

### New Capabilities
- `database-schema-documentation`: 定義資料表建立時必須提供 table 與 column description 的 schema 文件化要求

### Modified Capabilities

（無）

## Impact

- `src/main/resources/db/migration/` 內所有建立資料表的 migration 將需要加入 table-level 與 column-level description
- 既有 debug/修補型 migration 的內容需要重新分配回對應基礎版本，例如 enum 型別、欄位命名與 nullable 設定、預設 admin seed 資料
- 可能需要更新資料庫 migration 撰寫規範、開發文件或驗證流程，以確保後續 migration 持續符合要求
- 不影響既有 API、Service、Controller 或 domain 行為，影響範圍限於 schema 定義與其驗證方式