## Why

登出後以不同帳號重新登入時，Riverpod `FutureProvider`（`profileProvider`、`todayAttendanceProvider`、`attendanceRecordsProvider`、`amendmentsProvider`）仍持有前一位使用者的快取資料，導致新帳號進入系統後看到舊帳號的個人資訊與出勤紀錄。這些 provider 不依賴 auth state，logout 流程也未主動 invalidate 它們，是安全與正確性問題，需立即修正。

## What Changes

- 在 `AuthController.logout()` 與 `AuthController.forceLogout()` 執行後，invalidate 所有依賴使用者身份的 Riverpod providers
- 受影響的 providers：`profileProvider`、`todayAttendanceProvider`、`attendanceRecordsProvider`、`amendmentsProvider`
- 確保 `attendanceRecordsMonthProvider` 也在登出時重設為當月

## Capabilities

### New Capabilities

- `auth-logout-state-cleanup`: 登出時清除所有使用者相關的 Riverpod provider 快取，確保下一位使用者登入時取得全新資料

### Modified Capabilities

- `flutter-auth-flow`: 登出行為新增 requirement — 登出成功後 SHALL invalidate 所有使用者相關 provider 的快取狀態

## Impact

- `flutter/lib/features/auth/state/auth_provider.dart` — `logout()` 與 `forceLogout()` 需透過 `Ref` 呼叫 `invalidate()`
- `flutter/lib/features/profile/ui/profile_page.dart` — `profileProvider` 定義處（可考慮移至獨立檔案）
- `flutter/lib/features/attendance/state/attendance_provider.dart` — 無需改動 provider 本身，由 auth 層 invalidate
- `flutter/lib/features/amendment/state/amendment_provider.dart` — 無需改動 provider 本身，由 auth 層 invalidate
