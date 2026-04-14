## MODIFIED Requirements

### Requirement: 遲到事件 Email 通知發送
系統 SHALL 在員工上班打卡被判定為遲到時，立即透過 Email 非同步通知後台設定的收件人清單。通知採事件驅動（Spring ApplicationEvent + @Async），不阻塞打卡 API 回應。通知內容需包含遲到員工姓名、遲到日期、遲到分鐘數，且寄件資訊來自可管理的郵件設定。

#### Scenario: 遲到打卡後立即發送通知
- **WHEN** 員工上班打卡完成且系統判定 isLate=true
- **THEN** 系統在打卡 API 回傳前發布 LateArrivalEvent，@Async 監聽器對 notification_recipients 中每位有效收件人發送通知 Email，內容含員工姓名、遲到日期、遲到分鐘數

#### Scenario: 無收件人設定時不發送
- **WHEN** notification_recipients 清單為空
- **THEN** 系統不嘗試發送 Email，記錄警告日誌

#### Scenario: SMTP 發送失敗自動重試
- **WHEN** Email 發送因 SMTP 錯誤失敗
- **THEN** 系統自動重試最多 3 次，每次間隔 2 分鐘，三次均失敗後記錄錯誤日誌

### Requirement: 手動觸發測試通知
系統 SHALL 提供管理員手動觸發測試通知的 API，用於驗證 SMTP 設定是否正確，並允許指定測試收件人。

#### Scenario: 發送測試郵件成功
- **WHEN** 管理員呼叫測試通知 API，SMTP 設定正確
- **THEN** 系統向指定收件人或預設收件人清單發送測試郵件，回傳 200 OK 並說明已發送

#### Scenario: 發送測試郵件失敗
- **WHEN** SMTP 設定錯誤或連線失敗
- **THEN** 系統回傳 500 Internal Server Error，提供錯誤摘要
