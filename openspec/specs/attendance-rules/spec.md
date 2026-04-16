## MODIFIED Requirements

### Requirement: 出勤規則設定管理
系統 SHALL 提供全局出勤規則設定，管理員可透過 API 查詢與更新。設定包含：上班時間、下班時間、遲到容忍分鐘數、午休扣除分鐘數、最低工作分鐘數。

#### Scenario: 查詢目前出勤規則
- **WHEN** 管理員或員工呼叫取得出勤規則 API
- **THEN** 系統回傳目前設定值，含 workStartTime、workEndTime、lateToleranceMinutes、lunchBreakMinutes、requiredWorkMinutes

#### Scenario: 管理員更新出勤規則
- **WHEN** 管理員提交更新後的規則（如將 lunchBreakMinutes 從 60 改為 30）
- **THEN** 系統更新設定並回傳 200 OK，以及更新後的完整規則

#### Scenario: 規則時間邏輯驗證
- **WHEN** 管理員提交 workStartTime 晚於或等於 workEndTime 的設定
- **THEN** 系統拒絕更新並回傳欄位驗證錯誤

#### Scenario: 非管理員嘗試更新規則
- **WHEN** 角色為 EMPLOYEE 的使用者嘗試更新出勤規則
- **THEN** 系統回傳 403 Forbidden
