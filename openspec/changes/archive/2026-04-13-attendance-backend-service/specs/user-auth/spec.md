## ADDED Requirements

### Requirement: 使用者帳號建立
系統 SHALL 允許管理員建立新使用者帳號，並指定角色（EMPLOYEE 或 ADMIN）。帳號建立後密碼以 BCrypt 雜湊儲存，不得以明文保存。

#### Scenario: 管理員建立員工帳號
- **WHEN** 管理員提交包含 username、password、fullName、email、role=EMPLOYEE 的建立請求
- **THEN** 系統建立帳號並回傳 201 Created，密碼以 BCrypt 儲存

#### Scenario: 使用者名稱重複
- **WHEN** 提交的 username 已存在於系統
- **THEN** 系統回傳 409 Conflict 並說明 username 已被使用

#### Scenario: 非管理員嘗試建立帳號
- **WHEN** 角色為 EMPLOYEE 的使用者嘗試呼叫建立帳號 API
- **THEN** 系統回傳 403 Forbidden

### Requirement: 使用者登入與 JWT 發放
系統 SHALL 驗證使用者帳號密碼，驗證通過後發放 Access Token（15 分鐘有效）與 Refresh Token（7 天有效）。

#### Scenario: 有效憑證登入
- **WHEN** 使用者提交正確的 username 與 password
- **THEN** 系統回傳 200 OK，含 accessToken（JWT）與 refreshToken

#### Scenario: 錯誤密碼登入
- **WHEN** 使用者提交錯誤的 password
- **THEN** 系統回傳 401 Unauthorized，不揭露帳號是否存在

#### Scenario: 帳號不存在
- **WHEN** 使用者提交不存在的 username
- **THEN** 系統回傳 401 Unauthorized（與密碼錯誤回應一致，防止帳號枚舉）

### Requirement: Token 刷新
系統 SHALL 允許持有有效 Refresh Token 的使用者取得新的 Access Token。

#### Scenario: 有效 Refresh Token 刷新
- **WHEN** 使用者提交未過期且未被撤銷的 Refresh Token
- **THEN** 系統發放新的 Access Token，並回傳 200 OK

#### Scenario: 過期 Refresh Token
- **WHEN** 使用者提交已過期的 Refresh Token
- **THEN** 系統回傳 401 Unauthorized，要求重新登入

### Requirement: 登出與 Token 撤銷
系統 SHALL 在使用者登出時撤銷 Refresh Token，使其無法再次使用。

#### Scenario: 使用者登出
- **WHEN** 使用者呼叫登出 API（含有效 Access Token）
- **THEN** 系統從 DB 刪除對應 Refresh Token，回傳 200 OK

### Requirement: 角色權限存取控制
系統 SHALL 依據使用者角色限制 API 存取，ADMIN 擁有所有權限，EMPLOYEE 僅能存取個人相關功能。

#### Scenario: EMPLOYEE 存取管理員專用 API
- **WHEN** 角色為 EMPLOYEE 的使用者呼叫管理員專用端點
- **THEN** 系統回傳 403 Forbidden

#### Scenario: 未帶 Token 存取受保護 API
- **WHEN** 請求未包含 Authorization header
- **THEN** 系統回傳 401 Unauthorized

### Requirement: 使用者帳號停用
系統 SHALL 允許管理員停用使用者帳號，停用後該帳號無法登入。

#### Scenario: 停用帳號後嘗試登入
- **WHEN** 管理員停用某帳號後，該帳號嘗試登入
- **THEN** 系統回傳 401 Unauthorized，說明帳號已停用
