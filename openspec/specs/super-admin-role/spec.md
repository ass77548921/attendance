## ADDED Requirements

### Requirement: SUPER_ADMIN 角色定義
系統 SHALL 支援 SUPER_ADMIN 角色，為最高權限管理者，層級高於 ADMIN 與 EMPLOYEE。SUPER_ADMIN 帳號只能透過後端初始化（data migration / seeder）建立，不開放透過 API 自助建立。

#### Scenario: SUPER_ADMIN 呼叫需要 ADMIN 權限的端點
- **WHEN** 具備 SUPER_ADMIN 角色的使用者呼叫任何需要 ADMIN 以上權限的端點
- **THEN** 系統 SHALL 允許存取，回傳正常結果

#### Scenario: SUPER_ADMIN 登入後取得 role 資訊
- **WHEN** SUPER_ADMIN 使用者成功登入
- **THEN** 系統回傳 JWT，token payload 中 `role` 欄位值為 `SUPER_ADMIN`；登入回應 body 中亦包含 `role: "SUPER_ADMIN"`

#### Scenario: SUPER_ADMIN 建立 ADMIN 帳號
- **WHEN** SUPER_ADMIN 提交包含 `role=ADMIN` 的建立帳號請求至 `POST /api/admin/users`
- **THEN** 系統成功建立 ADMIN 帳號並回傳 201 Created

#### Scenario: SUPER_ADMIN 建立 EMPLOYEE 帳號
- **WHEN** SUPER_ADMIN 提交包含 `role=EMPLOYEE` 的建立帳號請求至 `POST /api/admin/users`
- **THEN** 系統成功建立 EMPLOYEE 帳號並回傳 201 Created

#### Scenario: EMPLOYEE 呼叫 SUPER_ADMIN 專用功能
- **WHEN** 角色為 EMPLOYEE 的使用者嘗試呼叫 SUPER_ADMIN 專用端點
- **THEN** 系統回傳 403 Forbidden

#### Scenario: ADMIN 嘗試建立 ADMIN 帳號
- **WHEN** 角色為 ADMIN 的使用者提交包含 `role=ADMIN` 的建立帳號請求
- **THEN** 系統回傳 403 Forbidden，拒絕建立

#### Scenario: ADMIN 嘗試建立 SUPER_ADMIN 帳號
- **WHEN** 角色為 ADMIN 的使用者提交包含 `role=SUPER_ADMIN` 的建立帳號請求
- **THEN** 系統回傳 403 Forbidden，拒絕建立
