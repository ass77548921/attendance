## ADDED Requirements

### Requirement: 固定底部導航列避免轉場動畫
系統 SHALL 確保底部導航列（Bottom Navigation Bar）在切換頁面時保持固定不動，僅主內容區域進行頁面轉場，避免整頁包含導航列的不必要動畫。

#### Scenario: 切換 nav item 僅內容區轉場
- **WHEN** 使用者點擊底部導航列的不同項目（如從「打卡」切換至「紀錄」）
- **THEN** 底部導航列 SHALL 維持固定位置與狀態，僅主內容區域（Scaffold body）進行頁面切換動畫

#### Scenario: 導航列選中狀態即時更新
- **WHEN** 使用者點擊導航項目
- **THEN** 導航列的選中指示器（如底線、顏色變化）即時更新，不參與轉場動畫

#### Scenario: 使用 IndexedStack 保持頁面狀態
- **WHEN** 使用者在不同 nav item 間切換
- **THEN** 使用 IndexedStack 或類似機制保持各頁面狀態，避免每次切換重新載入

---

### Requirement: 導航架構避免整頁重建
系統 SHALL 採用合適的導航架構（如 Scaffold 包含固定 bottomNavigationBar + IndexedStack body），確保切換頁面時不會觸發整個 Scaffold 的重建與動畫。

#### Scenario: Scaffold 層級結構正確
- **WHEN** 應用程式渲染主頁面架構
- **THEN** Scaffold 的 bottomNavigationBar 為固定組件，body 使用 IndexedStack 或 PageView，切換時僅更新 body 內容

#### Scenario: 路由設定不影響導航列
- **WHEN** 使用路由導航（如 GoRouter）切換底部導航對應頁面
- **THEN** 路由變更 SHALL 僅改變 body 顯示的頁面，不重新建構整個 Scaffold

#### Scenario: 避免 Navigator push 覆蓋導航列
- **WHEN** 從底部導航頁面 push 至子頁面（如詳情頁）
- **THEN** 子頁面覆蓋整頁包含導航列，返回時恢復原導航結構
