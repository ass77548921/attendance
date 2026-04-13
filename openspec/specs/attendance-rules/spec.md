## ADDED Requirements

### Requirement: 出勤規則設定管理
系統 SHALL 提供全局出勤規則設定，管理員可透過 API 查詢與更新。設定包含：上班時間、下班時間、遲到容忍分鐘數、午休扣除分鐘數、最低工作分鐘數。

#### Scenario: 查詢目前出勤規則
- **WHEN** 管理員或員工呼叫取得出勤規則 API
- **THEN** 系統回傳目前設定值，含 workStartTime、workEndTime、lateToleranceMinutes、lunchBreakMinutes、requiredWorkMinutes

#### Scenario: 管理員更新出勤規則
- **WHEN** 管理員提交更新後的規則（如將 lunchBreakMinutes 從 60 改為 30）
- **THEN** 系統更新設定並回傳 200 OK，以及更新後的完整規則

#### Scenario: 非管理員嘗試更新規則
- **WHEN** 角色為 EMPLOYEE 的使用者嘗試更新出勤規則
- **THEN** 系統回傳 403 Forbidden

### Requirement: 遲到判斷
系統 SHALL 在員工完成上班打卡時，自動判斷是否遲到。遲到判定條件：打卡時間（以設定時區換算）> 上班時間 + 遲到容忍分鐘數。

#### Scenario: 準時打卡（含容忍區間）
- **WHEN** 員工在 workStartTime + lateToleranceMinutes 以內完成上班打卡
- **THEN** 打卡紀錄 `isLate` 設為 false

#### Scenario: 遲到打卡
- **WHEN** 員工的打卡時間超過 workStartTime + lateToleranceMinutes
- **THEN** 打卡紀錄 `isLate` 設為 true，`lateMinutes` 記錄遲到分鐘數

### Requirement: 過早下班偵測
系統 SHALL 在員工完成下班打卡時，自動計算實際工作時長（下班時間 - 上班時間 - 午休分鐘數），若低於 requiredWorkMinutes 則標記為過早下班。

#### Scenario: 工作時長達標
- **WHEN** 員工下班打卡後，計算所得工作時長 >= requiredWorkMinutes（預設 480 分鐘）
- **THEN** 打卡紀錄 `isEarlyLeave` 設為 false

#### Scenario: 工作時長不足
- **WHEN** 員工下班打卡後，計算所得工作時長 < requiredWorkMinutes
- **THEN** 打卡紀錄 `isEarlyLeave` 設為 true，`shortMinutes` 記錄不足分鐘數

### Requirement: 補打卡後重新計算出勤狀態
系統 SHALL 在補打卡申請核准後，重新依照當前出勤規則計算該日 isLate、lateMinutes、isEarlyLeave、shortMinutes，並更新出勤紀錄。

#### Scenario: 補上班打卡核准後重算遲到狀態
- **WHEN** 補登的上班打卡時間早於原打卡時間
- **THEN** 系統重新計算 isLate，若補登後不再遲到則 isLate = false

#### Scenario: 補下班打卡核准後重算早退狀態
- **WHEN** 補登的下班打卡時間晚於原打卡時間
- **THEN** 系統重新計算 isEarlyLeave 與 shortMinutes

### Requirement: 時區設定
系統 SHALL 支援設定系統時區，所有出勤時間判斷以設定的時區為基準（資料庫儲存 UTC，顯示與判斷依 timezone 設定轉換）。

#### Scenario: 時區設定影響遲到判斷
- **WHEN** attendance_config 的 timezone 設為 Asia/Taipei（UTC+8）
- **THEN** 所有遲到 / 早退判斷以 UTC+8 當地時間為基準
