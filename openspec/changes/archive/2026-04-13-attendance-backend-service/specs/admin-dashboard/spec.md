## ADDED Requirements

### Requirement: 員工出勤紀錄查詢
系統 SHALL 允許管理員查詢所有員工的出勤紀錄，支援依員工、日期範圍、遲到狀態篩選，並支援分頁。

#### Scenario: 查詢指定員工指定日期範圍紀錄
- **WHEN** 管理員提供 userId、startDate、endDate 查詢參數
- **THEN** 系統回傳該員工指定範圍內的所有出勤紀錄，含上下班時間、遲到/早退狀態及工作時長

#### Scenario: 查詢所有員工當月紀錄
- **WHEN** 管理員提供 startDate 與 endDate，不指定 userId
- **THEN** 系統回傳所有員工在該範圍的紀錄，支援分頁（pageSize 預設 20）

#### Scenario: 篩選遲到員工
- **WHEN** 管理員提供 isLate=true 篩選條件
- **THEN** 系統僅回傳 isLate=true 的紀錄

#### Scenario: 非管理員嘗試查詢他人紀錄
- **WHEN** 員工嘗試查詢其他員工的出勤紀錄
- **THEN** 系統回傳 403 Forbidden

### Requirement: 補打卡申請管理
系統 SHALL 允許管理員查詢所有待審核的補打卡申請，並執行審核操作。

#### Scenario: 查詢待審核補打卡申請列表
- **WHEN** 管理員查詢 status=PENDING 的補打卡申請
- **THEN** 系統回傳所有待審核申請，含申請人、日期、類型、補登時間、原因

#### Scenario: 審核補打卡申請
- **WHEN** 管理員對申請執行 APPROVED 或 REJECTED 操作
- **THEN** 系統更新申請狀態，APPROVED 時同步更新出勤紀錄，回傳 200 OK

### Requirement: 員工帳號管理
系統 SHALL 允許管理員查詢員工列表、查看帳號詳情、建立帳號及停用帳號。

#### Scenario: 查詢員工列表
- **WHEN** 管理員查詢員工列表（可依姓名或 email 搜尋）
- **THEN** 系統回傳員工列表，含 userId、username、fullName、email、role、status

#### Scenario: 停用員工帳號
- **WHEN** 管理員將某員工帳號狀態設為 INACTIVE
- **THEN** 系統更新帳號狀態，該員工後續登入請求回傳 401，回傳 200 OK

### Requirement: 遲到通知收件人設定
系統 SHALL 允許管理員設定遲到通知 Email 收件人清單，可新增或移除收件人。

#### Scenario: 新增通知收件人
- **WHEN** 管理員提交包含 email 與 name 的新增請求
- **THEN** 系統儲存收件人資訊並回傳 201 Created

#### Scenario: 移除通知收件人
- **WHEN** 管理員刪除指定收件人記錄
- **THEN** 系統移除該收件人，後續通知不再發送給此地址，回傳 200 OK

#### Scenario: 查詢收件人清單
- **WHEN** 管理員查詢通知收件人列表
- **THEN** 系統回傳所有已設定的收件人

### Requirement: 出勤統計摘要
系統 SHALL 提供管理員查詢指定時間區間的出勤統計，包含遲到次數、缺席次數、補打卡申請數量。

#### Scenario: 查詢指定月份出勤統計
- **WHEN** 管理員查詢某年某月的統計資料
- **THEN** 系統回傳該月各員工的遲到次數、正常出勤天數、補打卡申請次數
