## MODIFIED Requirements

### Requirement: 員工資料編輯彈窗
系統 SHALL 提供管理員一個彈窗介面以編輯員工基本資料，彈窗由員工列表的「編輯」按鈕觸發，風格為置中遮罩 + 圓角卡片，與 SweetAlert2 視覺一致。彈窗 SHALL 包含三個可選欄位：`address`（地址）、`personalPhone`（個人聯絡電話）、`officeExtension`（公司分機電話）；此三個欄位均為可選，留空表示不填寫。

#### Scenario: 開啟編輯彈窗
- **WHEN** 管理員點擊員工列表某列的「編輯」按鈕
- **THEN** 彈窗開啟，並預填該員工的帳號、姓名、Email、狀態；密碼欄位為空（留空代表不修改）；address、personalPhone、officeExtension 顯示現有值（若有）或空白

#### Scenario: 成功儲存編輯（含聯絡資料）
- **WHEN** 管理員修改欄位後點擊「儲存」（包含填寫或清空 address、personalPhone、officeExtension）
- **THEN** 系統呼叫 `PUT /api/admin/users/{id}`，request body 包含 address、personalPhone、officeExtension（可為 null），成功後關閉彈窗並重新載入列表，顯示成功提示

#### Scenario: 密碼選填
- **WHEN** 管理員留空密碼欄位並儲存
- **THEN** 系統 SHALL 不修改該員工的現有密碼

#### Scenario: 聯絡資料選填
- **WHEN** 管理員留空 address、personalPhone 或 officeExtension 並儲存
- **THEN** 系統 SHALL 接受空值並儲存為 null，不報錯

#### Scenario: 驗證失敗
- **WHEN** 管理員提交缺少必填欄位或格式錯誤的 Email
- **THEN** 彈窗內顯示錯誤訊息，不關閉彈窗

#### Scenario: 後端回傳錯誤
- **WHEN** 後端回傳 400 或 409（如帳號重複）
- **THEN** 彈窗內顯示後端錯誤訊息，不關閉彈窗

#### Scenario: 取消編輯
- **WHEN** 管理員點擊「取消」或遮罩外區域
- **THEN** 彈窗關閉，不儲存任何變更
