## ADDED Requirements

### Requirement: 補打卡申請表單
系統 SHALL 提供補打卡申請表單，員工可選擇目標日期、打卡類型（上班/下班）、補登時間、填寫原因，並可選擇性上傳最多 5 個附件（JPEG、PNG、GIF、PDF，每檔限 10 MB）。

#### Scenario: 成功提交補打卡申請（無附件）
- **WHEN** 員工填寫所有必填欄位並點擊送出
- **THEN** 呼叫 API，成功後顯示成功訊息，導向申請列表頁

#### Scenario: 成功提交補打卡申請（含附件）
- **WHEN** 員工選擇 1–5 個符合格式的附件並送出
- **THEN** 以 multipart/form-data 上傳，成功後顯示「申請已送出」Snackbar

#### Scenario: 附件格式不符顯示錯誤
- **WHEN** 使用者選擇非允許格式的檔案
- **THEN** 即時顯示「不支援此檔案格式，請上傳 JPEG、PNG、GIF 或 PDF」，不加入上傳清單

#### Scenario: 附件超過 5 個時阻止選取
- **WHEN** 已選取 5 個附件，使用者繼續選取
- **THEN** 顯示「最多上傳 5 個附件」，不加入超出的檔案

#### Scenario: 必填欄位未填寫時阻止送出
- **WHEN** 目標日期、打卡類型、補登時間或原因有任何未填
- **THEN** 對應欄位顯示紅色驗證錯誤文字，按鈕保持不可點擊

#### Scenario: API 回傳 422 顯示後端錯誤
- **WHEN** API 回傳 422（例如該日期同類型申請已存在）
- **THEN** 顯示後端回傳的錯誤訊息 Snackbar，不關閉表單

---

### Requirement: 補打卡申請列表
系統 SHALL 提供個人補打卡申請列表頁面，顯示所有申請紀錄及目前狀態（PENDING / APPROVED / REJECTED），並可依狀態篩選。

#### Scenario: 載入個人申請列表
- **WHEN** 使用者進入補打卡申請列表頁
- **THEN** 顯示所有個人申請紀錄，依申請時間降序排列，每筆顯示申請日期、類型、補登時間、狀態

#### Scenario: 依狀態篩選
- **WHEN** 使用者選擇「待審核」篩選條件
- **THEN** 列表只顯示 PENDING 狀態的申請

#### Scenario: 狀態 Chip 顏色區分
- **WHEN** 列表顯示不同狀態的申請
- **THEN** PENDING 顯示橘色 Chip，APPROVED 顯示綠色 Chip，REJECTED 顯示紅色 Chip

#### Scenario: 點擊申請查看詳情
- **WHEN** 使用者點擊列表中某筆申請
- **THEN** 顯示該申請的完整詳情，包含原因、附件縮圖（如有）及審核備註（如有）
