## 1. 專案初始化

- [x] 1.1 在 `frontend/` 目錄以 `npm create vite@latest . -- --template react-ts` 初始化 React + TypeScript + Vite 專案
- [x] 1.2 安裝依賴：`react-router-dom`、`tailwindcss`、`@tailwindcss/forms`，並初始化 Tailwind 設定
- [x] 1.3 設定 Vite dev server proxy：`/api` → `${VITE_API_BASE_URL:-http://localhost:8080}`，支援環境變數覆蓋
- [x] 1.4 在 `frontend/` 目錄建立獨立 `docker-compose.yml`，定義前端 dev service（Node 20 image，`npm run dev`，port 5173），透過 `VITE_API_BASE_URL` 環境變數指向後端；不修改根目錄 `docker-compose.yml`

## 2. 基礎架構

- [x] 2.1 建立 `src/lib/apiClient.ts`：封裝 `fetch`，自動附加 `Authorization: Bearer <token>` header，統一 401 → 登出跳轉處理
- [x] 2.2 建立 `src/context/AuthContext.tsx`：提供 `token`、`username`、`login()`、`logout()` 到全域，token 讀寫 localStorage
- [x] 2.3 建立 `src/components/ProtectedRoute.tsx`：無 token 時導向 `/login`
- [x] 2.4 建立 `src/components/Layout.tsx`：固定側邊欄 + 主內容區，側邊欄含頁面導航連結及登出按鈕，高亮目前頁
- [x] 2.5 設定 `src/App.tsx` 路由結構：`/login`、`/admin/attendance`、`/admin/amendments`、`/admin/users`、`/admin/config`、`/admin/mail-settings`、`/admin/notifications`，所有 `/admin/*` 使用 ProtectedRoute

## 3. 登入頁

- [x] 3.1 建立 `src/pages/LoginPage.tsx`：帳號/密碼 form，呼叫 `POST /api/auth/login`，成功後儲存 token 並導向 `/admin/attendance`
- [x] 3.2 處理登入失敗提示（帳號或密碼錯誤）及已登入自動導向

## 4. 出勤紀錄管理頁

- [x] 4.1 建立 `src/pages/AttendancePage.tsx`：篩選表單（userId、startDate、endDate、isLate）+ 分頁資料表格
- [x] 4.2 表格欄位：員工姓名、日期、上班時間、下班時間、遲到/早退狀態、最近調整摘要（`latestAdjustment`）
- [x] 4.3 實作手動調整 Modal：輸入新的上/下班時間與原因（必填），呼叫 `PATCH /api/admin/attendance/{id}/adjust`
- [x] 4.4 實作調整歷史 Modal：呼叫 `GET /api/admin/attendance/{id}/adjustments`，列表顯示調整紀錄

## 5. 補打卡申請審核頁

- [x] 5.1 建立 `src/pages/AmendmentsPage.tsx`：status 篩選器（PENDING / APPROVED / REJECTED / ALL）+ 表格
- [x] 5.2 表格欄位：申請人、日期、類型、補登時間、原因，附「核准」/「駁回」按鈕
- [x] 5.3 呼叫審核 API 更新 status，操作後重新載入列表並顯示提示

## 6. 員工帳號管理頁

- [x] 6.1 建立 `src/pages/UsersPage.tsx`：搜尋欄 + 帳號列表表格（userId、username、fullName、email、role、status）
- [x] 6.2 實作新增員工 Modal：username、fullName、email、密碼欄位，呼叫 `POST /api/admin/users`
- [x] 6.3 實作帳號狀態切換：「停用」/「啟用」按鈕，呼叫 `PATCH /api/admin/users/{id}/status`

## 7. 出勤規則設定頁

- [x] 7.1 建立 `src/pages/AttendanceConfigPage.tsx`：載入 `GET /api/admin/config`，顯示上班時間、下班時間、遲到寬限分鐘數
- [x] 7.2 實作儲存：前端驗證上班時間 < 下班時間，呼叫 `PUT /api/admin/config`，顯示成功/失敗提示

## 8. 郵件 SMTP 設定頁

- [x] 8.1 建立 `src/pages/MailSettingsPage.tsx`：載入 `GET /api/admin/mail-settings`，顯示所有欄位；密碼欄以 `hasPassword` 顯示「已設定 / 未設定」
- [x] 8.2 實作儲存設定：呼叫 `PUT /api/admin/mail-settings`
- [x] 8.3 實作寄送測試信：測試收件 email 輸入 + 「寄送測試」按鈕，呼叫 `POST /api/admin/mail-settings/test`；502 時顯示後端 `detail` 錯誤訊息

## 9. 通知收件人設定頁

- [x] 9.1 建立 `src/pages/NotificationsPage.tsx`：載入 `GET /api/admin/notification/recipients`，顯示收件人列表
- [x] 9.2 實作新增收件人：email 表單，呼叫 `POST /api/admin/notification/recipients`
- [x] 9.3 實作刪除收件人：確認後呼叫 `DELETE /api/admin/notification/recipients/{id}`

## 10. 驗收與整合測試

- [x] 10.1 驗證 docker-compose 啟動前後端服務，前端可正常訪問 `http://localhost:5173`，API proxy 正常運作
- [x] 10.2 手動走查所有頁面功能（登入、出勤查詢與調整、補打卡審核、帳號管理、規則設定、郵件設定、通知設定）
- [x] 10.3 確認 401 → 登出導向、表單驗證錯誤提示、API 錯誤顯示等邊界行為正常
