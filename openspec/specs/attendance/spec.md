## ADDED Requirements

### Requirement: 上班打卡
系統 SHALL 允許員工執行上班打卡，每日僅允許一筆有效上班打卡紀錄。打卡時間以伺服器接收請求的 UTC 時間為準。

#### Scenario: 正常上班打卡
- **WHEN** 員工在尚未打過上班卡的工作日呼叫上班打卡 API
- **THEN** 系統建立打卡紀錄，設定 `clockInTime`，回傳 201 Created

#### Scenario: 重複上班打卡
- **WHEN** 員工當日已有上班打卡紀錄，再次嘗試上班打卡
- **THEN** 系統回傳 409 Conflict，說明今日已打過上班卡

### Requirement: 下班打卡
系統 SHALL 允許員工執行下班打卡，下班打卡必須在上班打卡之後，每日僅允許一筆有效下班打卡紀錄。

#### Scenario: 正常下班打卡
- **WHEN** 員工當日已有上班打卡紀錄，且尚未打過下班卡
- **THEN** 系統更新打卡紀錄，設定 `clockOutTime`，計算工作時長，回傳 200 OK

#### Scenario: 未上班打卡直接下班打卡
- **WHEN** 員工當日無上班打卡紀錄，嘗試下班打卡
- **THEN** 系統回傳 422 Unprocessable Entity，說明未找到對應上班打卡紀錄

#### Scenario: 重複下班打卡
- **WHEN** 員工當日已有下班打卡紀錄，再次嘗試下班打卡
- **THEN** 系統回傳 409 Conflict，說明今日已打過下班卡

### Requirement: 查詢個人打卡紀錄
系統 SHALL 允許員工查詢自己的打卡紀錄，支援依日期範圍篩選。

#### Scenario: 查詢指定日期範圍的紀錄
- **WHEN** 員工提供 `startDate` 與 `endDate` 查詢參數
- **THEN** 系統回傳該範圍內的打卡紀錄列表，包含上下班時間、遲到狀態、工作時長

#### Scenario: 查詢今日打卡狀態
- **WHEN** 員工呼叫今日打卡狀態 API
- **THEN** 系統回傳今日的打卡紀錄（含 null 欄位表示尚未打卡）

### Requirement: 補打卡申請
系統 SHALL 允許員工針對特定日期提交補打卡申請，說明原因、補登時間，並可選擇性上傳附件（截圖、PDF）作為佐證。補打卡申請需經管理員審核。API 採 `multipart/form-data`，附件欄位名稱為 `attachments`，最多 5 個檔案，每檔限 10 MB，僅允許 JPEG、PNG、GIF、PDF 格式。

#### Scenario: 提交補打卡申請（無附件）
- **WHEN** 員工提交含有目標日期、打卡類型（CLOCK_IN / CLOCK_OUT）、補登時間、原因的申請，不含附件
- **THEN** 系統建立申請紀錄，狀態設為 PENDING，回傳 201 Created

#### Scenario: 提交補打卡申請（含附件）
- **WHEN** 員工提交申請並同時上傳 1–5 個符合格式限制的附件
- **THEN** 系統建立申請紀錄及 amendment_attachments 紀錄，附件儲存至本地 Volume，回傳 201 Created

#### Scenario: 附件格式不符
- **WHEN** 上傳的附件 MIME type 不屬於允許清單（jpeg/png/gif/pdf）
- **THEN** 系統回傳 422 Unprocessable Entity，說明不支援的檔案格式，不建立申請

#### Scenario: 附件超過數量或大小限制
- **WHEN** 上傳附件超過 5 個，或單一附件檔案大小超過 10 MB
- **THEN** 系統回傳 422 Unprocessable Entity，說明超出限制，不建立申請

#### Scenario: 重複提交相同日期相同類型的補打卡
- **WHEN** 該日期同類型補打卡申請已有 PENDING 或 APPROVED 紀錄
- **THEN** 系統回傳 409 Conflict，說明已有待審核或已核准的申請

### Requirement: 補打卡審核
系統 SHALL 允許管理員審核補打卡申請，APPROVED 後自動更新對應日期的打卡紀錄並重新計算出勤狀態。管理員可在審核前下載附件查閱。

#### Scenario: 管理員下載補打卡附件
- **WHEN** 管理員呼叫 GET /api/admin/amendments/{id}/attachments/{fileId}
- **THEN** 系統驗證附件屬於該申請，回傳檔案串流（含正確 Content-Type 與 Content-Disposition）

#### Scenario: 管理員核准補打卡申請
- **WHEN** 管理員將補打卡申請狀態改為 APPROVED
- **THEN** 系統更新對應日期的 `attendance_records`，重新計算遲到與早退狀態，回傳 200 OK

#### Scenario: 管理員駁回補打卡申請
- **WHEN** 管理員將補打卡申請狀態改為 REJECTED，並附上原因
- **THEN** 系統更新申請狀態為 REJECTED，不修改出勤紀錄，回傳 200 OK
