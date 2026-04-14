## ADDED Requirements

### Requirement: 員工查看彈窗跳轉出勤紀錄
系統 SHALL 在員工查看彈窗底部提供「查看出勤紀錄」按鈕，點擊後關閉彈窗並導航至出勤紀錄頁面，同時以 URL query parameter 帶入該員工的 userId 作為預設篩選條件。

#### Scenario: 點擊查看出勤紀錄
- **WHEN** 管理員在員工查看彈窗中點擊「查看出勤紀錄」按鈕
- **THEN** 彈窗關閉，頁面導航至 `/admin/attendance?userId=<employee_id>`，出勤紀錄頁自動以該員工 ID 篩選並載入近期出勤資料
