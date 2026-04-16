## MODIFIED Requirements

### Requirement: 員工資料唯讀查看彈窗
系統 SHALL 提供管理員一個唯讀彈窗以查閱員工完整資訊，彈窗由員工列表的「查看」按鈕觸發，風格為置中遮罩 + 圓角卡片。彈窗底部 SHALL 提供「查看出勤紀錄」按鈕與「關閉」按鈕。彈窗 SHALL 顯示 `address`（地址）、`personalPhone`（個人聯絡電話）、`officeExtension`（公司分機電話）三個欄位，若值為空則顯示「—」。

#### Scenario: 開啟查看彈窗
- **WHEN** 管理員點擊員工列表某列的「查看」按鈕
- **THEN** 彈窗開啟，顯示該員工的 ID、帳號、姓名、Email、角色、狀態、建立時間、地址、個人聯絡電話、公司分機電話；所有欄位為唯讀；若 address、personalPhone、officeExtension 為空，則顯示「—」；底部顯示「查看出勤紀錄」與「關閉」按鈕

#### Scenario: 關閉查看彈窗
- **WHEN** 管理員點擊「關閉」按鈕或遮罩外區域
- **THEN** 彈窗關閉

#### Scenario: 跳轉至員工出勤紀錄
- **WHEN** 管理員點擊「查看出勤紀錄」按鈕
- **THEN** 彈窗關閉，頁面導航至出勤紀錄頁並以該員工 userId 自動篩選
