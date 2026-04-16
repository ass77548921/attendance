## 1. 修改使用者相關 Provider — 改用 autoDispose

- [x] 1.1 `auth_provider.dart`：移除 `onLoggedOut` callback 機制（改用 autoDispose 方案）
- [x] 1.2 `profileProvider`、`todayAttendanceProvider`、`attendanceRecordsProvider`、`attendanceRecordsMonthProvider`、`amendmentsProvider` 改用 `.autoDispose`，確保登出後 shell route unmount 時自動清除快取
- [x] 1.3 確認 `logout()` 與 `forceLogout()` 均能觸發此機制（login/logout 導向 `/login` → shell unmount → autoDispose 觸發）

## 2. 驗證

- [x] 2.1 手動測試：帳號 A 登入 → 登出 → 帳號 B 登入，確認個人資料頁、出勤頁、補簽頁均顯示帳號 B 的資料
- [x] 2.2 手動測試：同一帳號登出再登入，確認資料正常顯示（非舊快取）
- [x] 2.3 手動測試：模擬 session 過期強制登出（forceLogout），再以新帳號登入，確認無殘留資料
- [x] 2.4 確認 `attendanceRecordsMonthProvider` 月份選擇在重新登入後重設為當月
