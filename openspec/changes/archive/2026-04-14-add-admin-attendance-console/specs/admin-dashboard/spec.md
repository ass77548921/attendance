## MODIFIED Requirements

### Requirement: 員工出勤紀錄查詢
系統 SHALL 允許管理員查詢所有員工的出勤紀錄，支援依員工、日期範圍、遲到狀態篩選，並支援分頁；回傳內容需包含最近一次人工修正資訊（若有）。

#### Scenario: 查詢指定員工指定日期範圍紀錄
- **WHEN** 管理員提供 userId、startDate、endDate 查詢參數
- **THEN** 系統回傳該員工指定範圍內的所有出勤紀錄，含上下班時間、遲到/早退狀態及工作時長

#### Scenario: 查詢所有員工當月紀錄
- **WHEN** 管理員提供 startDate 與 endDate，不指定 userId
- **THEN** 系統回傳所有員工在該範圍的紀錄，支援分頁（pageSize 預設 20）

#### Scenario: 篩選遲到員工
- **WHEN** 管理員提供 isLate=true 篩選條件
- **THEN** 系統僅回傳 isLate=true 的紀錄

#### Scenario: 回傳人工修正摘要
- **WHEN** 出勤紀錄曾被管理員人工修正
- **THEN** 查詢結果包含最近修正時間、修正人與修正理由摘要

#### Scenario: 非管理員嘗試查詢他人紀錄
- **WHEN** 員工嘗試查詢其他員工的出勤紀錄
- **THEN** 系統回傳 403 Forbidden

### Requirement: 員工帳號管理
系統 SHALL 允許管理員查詢員工列表、查看帳號詳情、建立帳號及停用帳號，並支援角色與狀態管理。

#### Scenario: 查詢員工列表
- **WHEN** 管理員查詢員工列表（可依姓名或 email 搜尋）
- **THEN** 系統回傳員工列表，含 userId、username、fullName、email、role、status

#### Scenario: 停用員工帳號
- **WHEN** 管理員將某員工帳號狀態設為 INACTIVE
- **THEN** 系統更新帳號狀態，該員工後續登入請求回傳 401，回傳 200 OK

#### Scenario: 建立後台管理員帳號
- **WHEN** 具權限管理員建立 role=ADMIN 的帳號
- **THEN** 系統建立帳號並套用管理端權限邊界
