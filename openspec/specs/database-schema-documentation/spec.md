## ADDED Requirements

### Requirement: 建立資料表時必須提供 table description
系統的 Flyway migration 在新增任何資料表時，`CREATE TABLE` 定義 SHALL 包含可讀的 table description，用於說明該資料表的業務用途與保存資料類型。

#### Scenario: 新建資料表包含 table description
- **WHEN** 開發者新增一個包含 `CREATE TABLE` 的 migration
- **THEN** 該資料表定義必須包含 table-level description，且 description 可以表達該表的業務用途

#### Scenario: 新建資料表缺少 table description
- **WHEN** migration 新增資料表但未提供 table-level description
- **THEN** 該 migration 不符合 schema 文件化要求，不能視為完成

### Requirement: 建立資料表時每個欄位都必須提供 column description
系統的 Flyway migration 在新增任何資料表時，`CREATE TABLE` 內的每個欄位 SHALL 包含對應的 column description，用於說明欄位意義、用途，必要時補充單位、格式或狀態語意。

#### Scenario: 新建資料表的所有欄位皆有 description
- **WHEN** 開發者新增一個包含多個欄位的 `CREATE TABLE` migration
- **THEN** 每個欄位定義都必須包含 column description，而不是只描述部分欄位

#### Scenario: 任一欄位缺少 description
- **WHEN** `CREATE TABLE` 中任一欄位未提供 column description
- **THEN** 該 migration 不符合 schema 文件化要求，不能視為完成

### Requirement: 新建服務的 migration 基線必須反映最終正確 schema
系統若以新建服務方式交付，初始 Flyway migration 基線 SHALL 直接反映最終正確的 schema 與 seed 資料，不得依賴僅因 debug 過程產生的後續修補型 migration 才能得到正確結果。

#### Scenario: schema 修補需回收到基礎建表版本
- **WHEN** 現有 migration 中有僅用於修正先前建表錯誤的版本
- **THEN** 這些修正必須整理回對應的基礎 `CREATE TABLE` migration，使新建服務初始化後立即得到最終正確 schema

#### Scenario: seed 修補需回收到原始 seed 版本
- **WHEN** 現有 migration 中有僅用於修正預設 seed 資料的版本
- **THEN** 該修正必須整理回原始 seed migration，而不是保留為 debug 後補版本