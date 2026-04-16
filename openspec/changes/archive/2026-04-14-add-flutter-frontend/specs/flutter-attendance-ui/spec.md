## ADDED Requirements

### Requirement: 打卡主頁
系統 SHALL 提供打卡主頁，顯示今日打卡狀態（已上班/已下班/尚未打卡），並依狀態呈現可操作的上班打卡或下班打卡按鈕。

#### Scenario: 今日尚未打卡顯示上班打卡按鈕
- **WHEN** 使用者進入打卡主頁，且今日無任何打卡紀錄
- **THEN** 顯示「上班打卡」按鈕，並顯示目前時間

#### Scenario: 已上班打卡顯示下班打卡按鈕
- **WHEN** 今日已有 clockInTime 但無 clockOutTime
- **THEN** 顯示上班打卡時間、「下班打卡」按鈕

#### Scenario: 已完成今日打卡顯示結果卡片
- **WHEN** 今日同時有 clockInTime 與 clockOutTime
- **THEN** 顯示上班、下班時間及工作時長，按鈕不可點擊

#### Scenario: 打卡成功顯示確認回饋
- **WHEN** 使用者點擊打卡按鈕且 API 回傳 201/200
- **THEN** 頁面即時更新打卡狀態，顯示成功 Snackbar

#### Scenario: 重複打卡顯示錯誤訊息
- **WHEN** API 回傳 409 Conflict
- **THEN** 顯示錯誤 Snackbar「今日已完成上班/下班打卡」

---

### Requirement: 打卡紀錄列表
系統 SHALL 提供打卡紀錄頁面，以月份為單位顯示個人打卡紀錄列表，包含日期、上班時間、下班時間、是否遲到、工作時長。支援切換月份查詢。

#### Scenario: 載入當月紀錄
- **WHEN** 使用者進入打卡紀錄頁
- **THEN** 預設顯示當月所有打卡紀錄，依日期降序排列

#### Scenario: 切換上個月紀錄
- **WHEN** 使用者點擊「上個月」
- **THEN** 更新列表顯示上個月的打卡紀錄

#### Scenario: 遲到標記顯示
- **WHEN** 某筆紀錄 `isLate = true`
- **THEN** 該紀錄卡片顯示醒目的「遲到」標籤

#### Scenario: 無紀錄顯示空狀態
- **WHEN** 所選月份無任何打卡紀錄
- **THEN** 顯示空狀態插圖與文字「本月無打卡紀錄」

#### Scenario: 載入中顯示 Skeleton Loader
- **WHEN** API 請求進行中
- **THEN** 列表區域顯示 Skeleton 載入動畫，不顯示空白頁面
