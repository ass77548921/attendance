## Context

目前 Flutter app 使用 Riverpod 管理狀態。使用者相關資料由多個 `FutureProvider` 負責：
- `profileProvider`（`profile_page.dart`）：呼叫 `/api/users/me` 取得個人資訊
- `todayAttendanceProvider`、`attendanceRecordsProvider`（`attendance_provider.dart`）：取得出勤資料
- `amendmentsProvider`（`amendment_provider.dart`）：取得補簽申請列表

這些 provider **不依賴** `authProvider` 的 state，因此在使用者登出後，Riverpod container 仍持有舊資料。下一位使用者登入後，若 provider 的快取尚未過期，頁面會直接顯示前一位使用者的資料，不會重新 fetch。

`AuthController` 目前在 `logout()` 與 `forceLogout()` 中僅清除 `SecureStorage` 並將 `authProvider` state 設為 `unauthenticated`，並未 invalidate 其他 providers。

## Goals / Non-Goals

**Goals:**
- 登出（含強制登出）時，invalidate 所有依賴使用者身份的 Riverpod provider
- 確保再次登入的使用者取得全新資料，不殘留前一位使用者快取
- 修改範圍最小化，不更動 provider 架構

**Non-Goals:**
- 重新設計 provider 架構（如將所有 provider 改為依賴 auth state）
- 增加全域 auth 監聽機制
- 修改後端 API

## Decisions

### 決策一：在 `AuthController` 中透過 `Ref` 主動 invalidate providers

**選項 A（採用）**：在 `authProvider` 的建立端（`authProvider` Provider 定義）使用 `ref.invalidate(...)` invalidate 相關 providers，於 `logout()` / `forceLogout()` 完成後呼叫。

實作方式：將 `Ref` 傳入 `AuthController`，或在 `authProvider` 的 Provider 層（`StateNotifierProvider` callback）監聽 auth state 由 authenticated → unauthenticated 時執行 invalidate。

**選項 B（捨棄）**：讓每個 `FutureProvider` 透過 `ref.watch(authProvider)` 建立依賴，token 改變時自動重新 fetch。
- 缺點：每次 auth state 任何變化（如 token 更新）都會觸發所有 provider 重新 fetch，過於侵入且效能影響較大。

**選項 C（捨棄）**：在 `ProfilePage`、`AttendanceHomePage` 等頁面進入時強制 refresh。
- 缺點：需修改多個 UI 檔案，且無法保證所有入口都覆蓋到。

**採用選項 A 的具體方式**：
在 `authProvider` Provider 定義中，透過 `ref.listen` 監聽 auth state，當 status 由 `authenticated` 轉為 `unauthenticated` 時（logout/forceLogout 均會觸發），呼叫 `ref.invalidate()` 清除相關 providers。這是純 provider 層的改動，不影響 UI。

### 決策二：同時重設 `attendanceRecordsMonthProvider`

登出時 `attendanceRecordsMonthProvider`（`StateProvider`）持有月份選擇狀態。若不重設，下一位使用者進入時會看到前一位使用者選擇的月份，需一併 invalidate。

## Risks / Trade-offs

- **[Risk] invalidate 時序問題** → `ref.listen` 在 state 改變後的下一個 frame 觸發，logout 動作完成後 providers 才被 invalidate，中間有極短暫空窗。由於 logout 後立即導向 `/login`，此空窗不影響使用者體驗。
- **[Risk] 未來新增 provider 忘記加入 invalidate 清單** → 建議在 `auth_provider.dart` 附近加入清楚的 TODO 注解，說明新增使用者相關 provider 時需同步更新此處。
- **[Trade-off] 使用 `ref.listen` vs 直接在 logout 方法內 invalidate** → `ref.listen` 方案讓 `AuthController` 不需知道其他 providers，維持關注點分離；缺點是略為間接。考量可維護性，仍優先選用此方式。

## Migration Plan

1. 修改 `authProvider` Provider 定義，加入 `ref.listen` 監聽 logout 事件
2. invalidate 清單：`profileProvider`、`todayAttendanceProvider`、`attendanceRecordsProvider`、`attendanceRecordsMonthProvider`、`amendmentsProvider`
3. 無 DB migration，無 API 變更，直接部署即可
4. Rollback：revert `auth_provider.dart` 一個檔案即可恢復原狀

## Open Questions

- 無。技術方向明確，修改範圍清楚。
