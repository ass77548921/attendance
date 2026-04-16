## MODIFIED Requirements

### Requirement: 使用者帳號建立
系統 SHALL 允許管理員建立新使用者帳號，並指定角色（EMPLOYEE 或 ADMIN）。帳號建立後密碼以 BCrypt 雜湊儲存，不得以明文保存。

#### Scenario: 管理員建立員工帳號
- **WHEN** 管理員提交包含 username、password、fullName、email、role=EMPLOYEE 的建立請求
- **THEN** 系統建立帳號並回傳 201 Created，密碼以 BCrypt 儲存

#### Scenario: 建立管理員帳號
- **WHEN** 具管理權限的管理員提交 role=ADMIN 建立請求
- **THEN** 系統建立管理員帳號並套用管理端權限策略

#### Scenario: 使用者名稱重複
- **WHEN** 提交的 username 已存在於系統
- **THEN** 系統回傳 409 Conflict 並說明 username 已被使用

#### Scenario: 非管理員嘗試建立帳號
- **WHEN** 角色為 EMPLOYEE 的使用者嘗試呼叫建立帳號 API
- **THEN** 系統回傳 403 Forbidden

### Requirement: 角色權限存取控制
系統 SHALL 依據使用者角色限制 API 存取，ADMIN 擁有所有權限，EMPLOYEE 僅能存取個人相關功能。

#### Scenario: EMPLOYEE 存取管理員專用 API
- **WHEN** 角色為 EMPLOYEE 的使用者呼叫管理員專用端點
- **THEN** 系統回傳 403 Forbidden

#### Scenario: 未帶 Token 存取受保護 API
- **WHEN** 請求未包含 Authorization header
- **THEN** 系統回傳 401 Unauthorized

#### Scenario: 停用帳號不得存取後台功能
- **WHEN** 狀態為 INACTIVE 的帳號攜帶既有 Token 呼叫後台端點
- **THEN** 系統拒絕請求並回傳未授權或禁止存取錯誤
