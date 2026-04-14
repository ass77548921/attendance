## Context

目前「員工帳號管理」頁面（`UsersPage.tsx`）的查看彈窗（`ViewUserModal`）只展示員工基本資料，無任何導航能力。「出勤紀錄管理」頁面（`AttendancePage.tsx`）已有 `userId` 篩選欄位，但僅支援手動輸入，不支援從 URL 初始化。應用使用 `react-router-dom` 的 `BrowserRouter`，路由路徑為 `/admin/attendance`。

## Goals / Non-Goals

**Goals:**
- 在 `ViewUserModal` 底部新增「查看出勤紀錄」連結按鈕，可導航至出勤紀錄頁並帶入 `userId`
- `AttendancePage` 在初始化時讀取 URL query parameter `userId`，自動填入篩選欄位並觸發查詢

**Non-Goals:**
- 不修改後端 API
- 不改動路由結構
- 不處理跨頁面狀態管理（使用 URL 而非 context/store）

## Decisions

### 1. 使用 URL Query Parameter 傳遞 userId

**選擇**：導航時使用 `/admin/attendance?userId=<id>`，`AttendancePage` 使用 `useSearchParams` 讀取初始值。

**理由**：
- 無需引入全域狀態管理（context、zustand 等）
- URL 可書籤化，符合現有路由風格
- 實作最小、副作用最少

**替代方案**：React Router location state (`navigate('/admin/attendance', { state: { userId } })`)。缺點：重新整理後 state 消失，無法書籤化，不選。

### 2. 導航方式：useNavigate

**選擇**：在 `ViewUserModal` 中呼叫 `useNavigate()`，在按鈕 `onClick` 時 `navigate('/admin/attendance?userId=...')`，同時呼叫 `onClose()` 關閉彈窗。

**理由**：最符合現有程式碼風格，`ViewUserModal` 已是 React component，直接使用 hook 即可。

### 3. AttendancePage 初始化邏輯

**選擇**：使用 `useSearchParams`，在 component mount 時讀取 `userId` 並設為 state 初始值。`useEffect` 依賴 `[load]`，load 依賴 `buildQuery`，buildQuery 依賴所有篩選 state，因此初始化後自動觸發查詢，無需額外改動。

**注意**：`useState` 的初始值只在 mount 時讀取一次（`useState(() => searchParams.get('userId') ?? '')`），這是最小改動。

## Risks / Trade-offs

- **[Risk] `useSearchParams` 初始值只讀一次** → 如果使用者從查看彈窗多次跳轉（同頁面不同員工），後續跳轉因組件不重新 mount 可能不更新。緩解：目前場景為首次導航，跨頁面皆為 mount，可接受。
- **[Trade-off] userId 為數字但 URL / input 皆為字串** → `userId` state 目前是 `string`，API 接受字串型 userId，無型別衝突。
