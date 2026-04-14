## 1. UsersPage — ViewUserModal 新增導航按鈕

- [x] 1.1 在 `ViewUserModal` 元件中引入 `useNavigate`（`react-router-dom`）
- [x] 1.2 在彈窗底部新增「查看出勤紀錄」按鈕，點擊時呼叫 `navigate('/admin/attendance?userId=<user.id>')` 並呼叫 `onClose()`

## 2. AttendancePage — 支援 URL userId 初始化

- [x] 2.1 在 `AttendancePage` 引入 `useSearchParams`（`react-router-dom`）
- [x] 2.2 將 `userId` state 的初始值改為讀取 `searchParams.get('userId') ?? ''`，使頁面 mount 時自動套用篩選並觸發初始查詢

## 3. 驗證

- [x] 3.1 從員工列表點擊「查看」，確認彈窗底部出現「查看出勤紀錄」按鈕
- [x] 3.2 點擊「查看出勤紀錄」，確認彈窗關閉並跳轉至 `/admin/attendance?userId=<id>`
- [x] 3.3 確認出勤紀錄頁載入後員工 ID 篩選欄位已填入正確值，且出勤紀錄已自動查詢
