## MODIFIED Requirements

### Requirement: 管理員登入
系統 SHALL 提供管理員登入頁面，輸入帳號密碼後向後端 `POST /api/auth/login` 進行驗證。僅具後台管理權限的帳號可完成登入並取得 JWT；若為員工帳號（EMPLOYEE），系統 MUST 拒絕其後台登入並在登入頁顯示「此帳號為員工帳號，無法管理後台」。

#### Scenario: 正常登入（管理帳號）
- **WHEN** 管理員輸入正確的 username 與 password 並提交登入
- **THEN** 系統取得 JWT token，儲存後導向 `/admin/attendance` 頁面

#### Scenario: 員工帳號嘗試登入後台
- **WHEN** 員工帳號（EMPLOYEE）輸入正確的 username 與 password 並提交登入
- **THEN** 系統拒絕登入且不導向管理頁，登入頁顯示「此帳號為員工帳號，無法管理後台」

#### Scenario: 帳號或密碼錯誤
- **WHEN** 使用者輸入錯誤的帳號或密碼
- **THEN** 系統在登入頁顯示「帳號或密碼錯誤」提示，不導向

#### Scenario: 已登入使用者訪問登入頁
- **WHEN** 已有有效 JWT 的管理帳號訪問登入頁
- **THEN** 系統自動導向 `/admin/attendance`

#### Scenario: JWT 過期或 401 回應
- **WHEN** 使用者的 JWT 已失效，對任一 API 呼叫收到 401 回應
- **THEN** 系統清除 localStorage token 並導向登入頁
