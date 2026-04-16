## ADDED Requirements

### Requirement: 補打卡詳情頁面返回導航
系統 SHALL 在補打卡申請詳情頁面提供返回導航機制，使用者可透過左側 AppBar 返回按鈕或手勢返回至申請列表頁面。

#### Scenario: 顯示 AppBar 返回按鈕
- **WHEN** 使用者從補打卡列表點擊某筆申請進入詳情頁
- **THEN** 詳情頁的 AppBar 左側 SHALL 顯示返回按鈕（<- 圖示）

#### Scenario: 點擊返回按鈕導航回列表
- **WHEN** 使用者點擊 AppBar 返回按鈕
- **THEN** 導航返回補打卡申請列表頁面，保留之前的篩選與捲動狀態

#### Scenario: 手勢返回導航
- **WHEN** 使用者在詳情頁執行返回手勢（iOS 左滑、Android 返回鍵）
- **THEN** 導航返回補打卡申請列表頁面

#### Scenario: 詳情頁支援深度連結
- **WHEN** 使用者透過深度連結或直接 URL 進入詳情頁
- **THEN** 返回按鈕仍可正常顯示，點擊後導航至列表頁（或應用程式首頁，若無列表上下文）
