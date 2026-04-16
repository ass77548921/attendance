## ADDED Requirements

### Requirement: 登出時清除使用者相關 Provider 快取
系統 SHALL 在登出（含正常登出與強制登出）完成後，invalidate 所有依賴使用者身份的 Riverpod provider，確保下一位使用者取得全新資料。

#### Scenario: 正常登出後 provider 快取被清除
- **WHEN** 使用者點擊登出，`AuthController.logout()` 完成
- **THEN** `profileProvider`、`todayAttendanceProvider`、`attendanceRecordsProvider`、`attendanceRecordsMonthProvider`、`amendmentsProvider` 均被 invalidate

#### Scenario: 強制登出（Session 過期）後 provider 快取被清除
- **WHEN** Refresh Token 過期觸發 `AuthController.forceLogout()`
- **THEN** `profileProvider`、`todayAttendanceProvider`、`attendanceRecordsProvider`、`attendanceRecordsMonthProvider`、`amendmentsProvider` 均被 invalidate

#### Scenario: 不同帳號重新登入後顯示新帳號資料
- **WHEN** 帳號 A 登出後，帳號 B 登入進系統
- **THEN** 個人資料頁顯示帳號 B 的資訊，出勤頁顯示帳號 B 的紀錄，補簽申請頁顯示帳號 B 的申請列表

#### Scenario: 相同帳號重新登入後資料正常顯示
- **WHEN** 同一帳號登出後再次登入
- **THEN** 頁面正常顯示該帳號最新資料（重新 fetch，非舊快取）
