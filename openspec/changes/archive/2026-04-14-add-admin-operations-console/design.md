## Context

後端 Spring Boot 管理 API 已完整實作（出勤手動調整、補打卡審核、帳號管理、郵件 SMTP 設定、出勤規則設定等），目前管理員只能透過直接呼叫 REST API 操作，缺乏視覺化操作介面。本設計目標是在不異動後端的前提下，新增一個輕量網頁型 SPA 後台系統。

現有後端已提供：
- JWT 認證（`POST /api/auth/login` 回傳 `token`）
- 全部管理 API 在 `/api/admin/**`（需 ROLE_ADMIN）
- 出勤規則設定 `/api/admin/config`
- 郵件設定 `/api/admin/mail-settings`
- 通知收件人 `/api/admin/notification-recipients`
- 員工出勤查詢 `/api/admin/attendance`（支援分頁、篩選）
- 手動調整 `PATCH /api/admin/attendance/{id}/adjust`
- 補打卡申請審核 `/api/admin/amendments`
- 帳號管理 `/api/admin/users`

## Goals / Non-Goals

**Goals:**
- 建立 `frontend/` 目錄，含完整前端 SPA 專案（React + Vite）
- 涵蓋全部管理功能頁面：登入、出勤紀錄、補打卡審核、帳號管理、郵件設定、出勤規則、通知設定
- 建立 `frontend/docker-compose.yml`，作為前端獨立的 compose 部署設定，dev 模式透過 proxy 存取後端 API
- 使用 JWT Bearer Token 與現有後端溝通，認證流程完整（token 儲存、過期處理、登出）

**Non-Goals:**
- 不新增後端 endpoint
- 不實做 i18n / 多語系
- 不修改根目錄 `docker-compose.yml`（後端 compose 維持獨立，不與前端耦合）
- 不建立員工自助入口（非管理員功能）
- 不做 PWA / 行動裝置優化（桌面優先）

## Decisions

### D1：技術棧選用 React + Vite + Tailwind CSS

**選擇**: React 18 + Vite 5 + Tailwind CSS v3

**理由**:
- Vite 提供極快的 dev server 與 HMR，適合單人快速迭代
- React 生態豐富，元件組合彈性高
- Tailwind CSS 省去 CSS 模組命名負擔，與元件共存清晰
- 無需框架 SSR，純 SPA 即可；後端 CORS 已允許 localhost

**放棄選項**:
- Vue 3 / Nuxt：可行但團隊如為 Java/Backend 背景，React 文件資源更多
- Next.js：SSR 不必要，反增複雜度
- Thymeleaf server-side render：無法實現動態表格、分頁、表單互動的流暢體驗

---

### D2：HTTP 層使用原生 fetch + 封裝 apiClient

**選擇**: 自製 `src/lib/apiClient.ts`，封裝 `fetch`，自動附加 `Authorization: Bearer <token>` header，統一錯誤處理（401 → 自動登出並導向登入頁）

**理由**:
- 無需引入 axios 等依賴，減少套件數量
- fetch 在現代瀏覽器及 Vite 環境下完全支援
- 集中 token 注入邏輯，避免各頁重複

**放棄選項**:
- React Query（TanStack Query）：功能強大但較重，MVP 階段過度工程
- SWR：類似，同樣過度

---

### D3：路由使用 React Router v6

**選擇**: `react-router-dom` v6，定義 protected routes（需登入才能存取 Admin 頁面）

**理由**: 業界標準，與 React 18 搭配成熟

---

### D4：狀態管理使用 React Context

**選擇**: 僅用 React Context 儲存 `authToken`、`currentUser`

**理由**: 狀態範圍小（只有 auth 全域共享），無需 Redux / Zustand；頁面讀取資料均以 local `useState + useEffect` 在各頁處理

---

### D5：Docker 分離部署策略

**選擇**: 前端使用獨立的 `frontend/docker-compose.yml`，不修改根目錄的後端 compose 設定

**理由**:
- 前後端可獨立啟動、停止、部署，降低耦合
- 後端 `docker-compose.yml` 不因前端需求而異動，維持穩定
- 前端 compose 設定 `VITE_API_BASE_URL` 環境變數，指向後端 API（dev: `http://localhost:8080`，可覆蓋）
- 未來替換前端框架或遷移至其他部署平台時，後端完全不受影響

**放棄選項**:
- 根目錄 compose 加入 frontend service：前後端生命週期耦合，需同時啟停
- `docker-compose.override.yml`：合併機制隱含，不易理解；分離的獨立檔案意圖更清晰

---

### D6：分頁與表格元件自製輕量元件

**選擇**: 自製 `<DataTable>`, `<Pagination>` 元件，不引入 AG Grid / react-table

**理由**: 管理頁面資料量不大（預設 pageSize 20），自製輕量元件可完全掌控樣式且減少依賴

## Risks / Trade-offs

- **無型別安全 API 契約** → 手動對齊後端欄位；建議日後考慮 openapi-generator 自動生成 client types
- **JWT 儲存在 localStorage** → XSS 風險，但本系統為內部管理後台、無敏感財務資料，可接受；若需強化可改用 httpOnly cookie，但需後端配合
- **開發 proxy 依賴 Vite devServer** → production 環境需另行設定 nginx reverse proxy（Non-Goal，日後補）
- **無 refresh token 機制** → 短效 token 到期後需重新登入；對管理後台操作頻率可接受

## Migration Plan

1. 新增 `frontend/` 目錄並初始化 Vite React 專案
2. 建立路由結構與 Layout 骨架
3. 實作登入頁與 auth context
4. 依序實作各管理功能頁面
5. 建立 `frontend/docker-compose.yml`，定義獨立 frontend service（`npm run dev`，port 5173），透過 `VITE_API_BASE_URL` 環境變數指向後端
6. 驗證：管理員可正常登入並操作所有功能

**Rollback**: 前端為獨立目錄，刪除 `frontend/` 及 compose service 即完全回退，不影響後端。

## Open Questions

- （已確認）後端是否需 CORS 設定？→ 由後端 `WebSecurityConfig` 處理，確認允許 `http://localhost:5173`
- （已決定）Docker 分離策略：使用 `frontend/docker-compose.yml` 獨立部署，不使用 override 機制，不修改根目錄 compose
