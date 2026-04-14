## ADDED Requirements

### Requirement: 側邊欄個人資料導航入口
系統 SHALL 在後台左側 Sidebar 底部區塊，顯示「個人資料」Nav Item，位於登入使用者名稱下方、登出按鈕上方。點擊後導航至 `/admin/profile`。

#### Scenario: 使用者點擊個人資料 Nav Item
- **WHEN** 已登入使用者點擊 Sidebar 底部的「個人資料」連結
- **THEN** 系統導航至 `/admin/profile`，並將「個人資料」項目渲染為 active 狀態

#### Scenario: 當前路由為 /admin/profile 時的 Nav 狀態
- **WHEN** 當前路由為 `/admin/profile`
- **THEN** Sidebar 中「個人資料」项目呈現 active 樣式（與其他主功能 Nav Item 的 active 樣式一致）
