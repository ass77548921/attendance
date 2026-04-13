## ADDED Requirements

### Requirement: 遲到事件 Email 通知發送
系統 SHALL 在員工上班打卡被判定為遲到時，立即透過 Email 非同步通知後台設定的收件人清單。通知採事件驅動（Spring ApplicationEvent + @Async），不阻塞打卡 API 回應。通知內容需包含遲到員工姓名、遲到日期、遲到分鐘數。

#### Scenario: 遲到打卡後立即發送通知
- **WHEN** 員工上班打卡完成且系統判定 isLate=true
- **THEN** 系統在打卡 API 回傳前發布 LateArrivalEvent，@Async 監聽器對 notification_recipients 中每位有效收件人發送通知 Email，內容含員工姓名、遲到日期、遲到分鐘數

#### Scenario: 無收件人設定時不發送
- **WHEN** notification_recipients 清單為空
- **THEN** 系統不嘗試發送 Email，記錄警告日誌

#### Scenario: SMTP 發送失敗自動重試
- **WHEN** Email 發送因 SMTP 錯誤失敗
- **THEN** 系統自動重試最多 3 次，每次間隔 2 分鐘，三次均失敗後記錄錯誤日誌

### Requirement: 補打卡審核後觸發通知
系統 SHALL 在補打卡申請核准後，若重新計算結果導致該日出現遲到狀態（原本未遲到），立即發送通知。

#### Scenario: 補打卡核准後觸發遲到通知
- **WHEN** 管理員核准補打卡申請，且重算後 isLate 由 false 變為 true
- **THEN** 系統發布 LateArrivalEvent，觸發 Email 通知

#### Scenario: 補打卡核准後遲到狀態不變
- **WHEN** 管理員核准補打卡申請，重算後 isLate 未發生變化
- **THEN** 系統不發送通知

### Requirement: 手動觸發測試通知
系統 SHALL 提供管理員手動觸發測試通知的 API，用於驗證 SMTP 設定是否正確。

#### Scenario: 發送測試郵件成功
- **WHEN** 管理員呼叫測試通知 API，SMTP 設定正確
- **THEN** 系統向收件人清單的第一位收件人發送測試郵件，回傳 200 OK 並說明已發送

#### Scenario: 發送測試郵件失敗
- **WHEN** SMTP 設定錯誤或連線失敗
- **THEN** 系統回傳 500 Internal Server Error，提供錯誤摘要

### Requirement: 通知記錄保存
系統 SHALL 記錄每次通知發送嘗試，包含發送時間、收件人、關聯遲到紀錄、發送結果（SUCCESS / FAILED）。

#### Scenario: 查詢通知發送紀錄
- **WHEN** 管理員查詢指定日期範圍的通知紀錄
- **THEN** 系統回傳紀錄列表，含發送時間、收件人 email、員工姓名、結果狀態
