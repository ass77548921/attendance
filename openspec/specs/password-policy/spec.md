## ADDED Requirements

### Requirement: 密碼規則設定管理
系統 SHALL 提供管理員一個全域密碼規則設定介面，設定結果影響所有帳號的密碼驗證行為。系統中永遠只有一筆規則（單例），透過 `GET /api/admin/password-policy` 查詢、`PUT /api/admin/password-policy` 更新。

#### Scenario: 查詢密碼規則
- **WHEN** 管理員呼叫 `GET /api/admin/password-policy`
- **THEN** 系統回傳 200 及當前規則：minLength、requireUppercase、requireLowercase、requireNumber、requireSpecialChar、expiryDays（0=永不過期）、historyCount（0=不限）

#### Scenario: 更新密碼規則
- **WHEN** 管理員呼叫 `PUT /api/admin/password-policy` 並提交合法的規則設定
- **THEN** 系統更新並回傳 200 及更新後規則

#### Scenario: 非管理員存取規則設定
- **WHEN** 角色為 EMPLOYEE 的使用者嘗試呼叫密碼規則 API
- **THEN** 系統回傳 403 Forbidden

### Requirement: 密碼複雜度驗證
系統 SHALL 在所有密碼變更入口（建立帳號、強制密碼強更、管理員重設）強制執行 PasswordPolicy 中定義的複雜度規則。

#### Scenario: 密碼長度不足
- **WHEN** 提交的密碼字元數小於 minLength
- **THEN** 系統回傳 400 Bad Request，說明長度不足

#### Scenario: 缺少必要字元類型
- **WHEN** 規則要求大寫字母但提交的密碼不含大寫
- **THEN** 系統回傳 400 Bad Request，說明缺少必要字元類型（大寫/小寫/數字/特殊字元）

#### Scenario: 密碼重複使用禁止
- **WHEN** historyCount > 0，且提交的新密碼與最近 historyCount 筆舊密碼中任一筆相同
- **THEN** 系統回傳 400 Bad Request，提示「密碼不可與最近 N 次相同」

#### Scenario: 符合所有規則的密碼
- **WHEN** 提交的密碼符合所有 PasswordPolicy 規則且不在歷史禁止清單中
- **THEN** 系統接受此密碼並繼續後續流程
