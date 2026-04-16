## MODIFIED Requirements

### Requirement: 登出
系統 SHALL 提供登出功能，呼叫後端登出 API 撤銷 Refresh Token，並清除本地端存儲的 Token，invalidate 所有使用者相關 Riverpod provider 快取，導向登入頁。

#### Scenario: 登出成功
- **WHEN** 使用者點擊登出
- **THEN** 呼叫 `POST /api/auth/logout`，清除 Secure Storage 中所有 Token，invalidate 使用者相關 provider 快取，導向 `/login`

#### Scenario: 登出時 API 失敗仍清除本地 Token 與快取
- **WHEN** 登出 API 請求失敗（網路問題）
- **THEN** 仍清除本地 Token，invalidate 使用者相關 provider 快取，導向 `/login`，不阻塞使用者
