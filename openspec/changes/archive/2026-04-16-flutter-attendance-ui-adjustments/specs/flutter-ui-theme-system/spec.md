## ADDED Requirements

### Requirement: 藍色主題配色方案
系統 SHALL 採用藍色為主色調的配色方案，包含主色、次要色、背景色、文字色、狀態色等完整色彩定義。所有 UI 組件 SHALL 統一使用此主題配色。

#### Scenario: 主色定義為藍色系
- **WHEN** 應用程式啟動並載入主題配置
- **THEN** primary color 設定為藍色（建議 #2196F3 或接近色值），primary variant 使用深藍色

#### Scenario: 狀態色保持語義化
- **WHEN** 顯示成功、警告、錯誤狀態
- **THEN** 成功使用綠色（#4CAF50），警告使用橘色（#FF9800），錯誤使用紅色（#F44336），遲到使用警告色

#### Scenario: 背景與卡片層次分明
- **WHEN** 渲染頁面與卡片組件
- **THEN** 背景色使用淺灰（#F5F5F5），卡片背景為白色（#FFFFFF），提供清晰視覺層次

#### Scenario: 文字色符合對比度規範
- **WHEN** 顯示主要文字與次要文字
- **THEN** 主要文字使用深灰（#212121），次要文字使用中灰（#757575），確保 WCAG AA 對比度標準

---

### Requirement: 統一排版與間距規範
系統 SHALL 定義統一的文字排版階層（標題、內文、輔助文字）及間距系統（8px 基數），所有頁面與組件 SHALL 遵循此規範以確保視覺一致性。

#### Scenario: 文字大小階層明確
- **WHEN** 渲染不同層級的文字內容
- **THEN** 標題使用 20-24sp，內文使用 16sp，輔助文字使用 14sp，按鈕文字使用 16sp medium weight

#### Scenario: 間距使用 8px 倍數
- **WHEN** 設定 padding、margin、gap 等間距屬性
- **THEN** 所有間距值為 8 的倍數（8px、16px、24px、32px），確保視覺韻律一致

#### Scenario: 卡片圓角統一
- **WHEN** 渲染卡片、按鈕、輸入框等組件
- **THEN** 圓角使用 8px（小組件）或 12px（卡片），創造柔和視覺效果

---

### Requirement: 組件樣式標準化
系統 SHALL 提供標準化的按鈕、輸入框、卡片、對話框等常用組件樣式，確保整體應用視覺風格統一。

#### Scenario: 按鈕樣式統一
- **WHEN** 渲染主要動作按鈕
- **THEN** 使用 ElevatedButton with primary color，高度 48px，文字 16sp medium，圓角 8px

#### Scenario: 輸入框樣式一致
- **WHEN** 渲染文字輸入欄位
- **THEN** 使用 OutlinedBorder，未聚焦時邊框灰色，聚焦時邊框 primary color，高度 56px

#### Scenario: 卡片陰影層次
- **WHEN** 渲染資訊卡片
- **THEN** 使用 elevation 2（靜止）或 4（互動），白色背景，圓角 12px，padding 16px

#### Scenario: 對話框樣式標準
- **WHEN** 顯示確認或提示對話框
- **THEN** 標題使用 20sp，內文 16sp，按鈕位於右下方，主要動作按鈕使用 primary color

---

### Requirement: 主題配置可維護性
系統 SHALL 將主題配置集中於 `lib/core/theme/` 目錄，使用 ThemeData 與自定義 ColorScheme、TextTheme 定義，便於未來調整與擴展。

#### Scenario: 主題定義集中管理
- **WHEN** 開發者需要調整主題參數
- **THEN** 所有色彩、文字、間距定義位於 `lib/core/theme/app_theme.dart` 單一檔案

#### Scenario: 全域應用主題
- **WHEN** 應用程式啟動
- **THEN** MaterialApp 的 theme 參數使用集中定義的 AppTheme，所有頁面自動套用

#### Scenario: 自定義色彩可擴展
- **WHEN** 需要新增特定用途色彩（如遲到紅、早退橘）
- **THEN** 透過 ThemeExtension 機制擴展，避免與標準 ColorScheme 衝突
