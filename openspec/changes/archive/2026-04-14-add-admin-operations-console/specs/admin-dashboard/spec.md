## MODIFIED Requirements

### Requirement: 員工出勤紀錄查詢
系統 SHALL 允許管理員查詢所有員工的出勤紀錄，支援依員工、日期範圍、遲到狀態篩選，並支援分頁。出勤紀錄回應物件 SHALL 包含 `latestAdjustment` 欄位，提供最近一次手動調整的摘要（adjustedBy、reason、adjustedAt），若無調整紀錄則為 `null`。

#### Scenario: 查詢指定員工指定日期範圍紀錄
- **WHEN** 管理員提供 userId、startDate、endDate 查詢參數
- **THEN** 系統回傳該員工指定範圍內的所有出勤紀錄，含上下班時間、遲到/早退狀態、工作時長，及 `latestAdjustment` 摘要（無調整時為 `null`）

#### Scenario: 查詢所有員工當月紀錄
- **WHEN** 管理員提供 startDate 與 endDate，不指定 userId
- **THEN** 系統回傳所有員工在該範圍的紀錄，支援分頁（pageSize 預設 20），每筆含 `latestAdjustment` 欄位

#### Scenario: 篩選遲到員工
- **WHEN** 管理員提供 isLate=true 篩選條件
- **THEN** 系統僅回傳 isLate=true 的紀錄，每筆含 `latestAdjustment` 欄位

#### Scenario: 非管理員嘗試查詢他人紀錄
- **WHEN** 員工嘗試查詢其他員工的出勤紀錄
- **THEN** 系統回傳 403 Forbidden
