## 1. 主題系統建立

- [x] 1.1 創建 `lib/core/theme/` 目錄結構
- [x] 1.2 實作 `app_colors.dart` 定義藍色主色調與所有色彩常數
- [x] 1.3 實作 `app_text_styles.dart` 定義 TextTheme（標題、內文、輔助文字）
- [x] 1.4 實作 `app_theme.dart` 整合 ThemeData 與 ColorScheme
- [x] 1.5 新增 ThemeExtension 支援自定義色彩（遲到紅、早退橘）
- [x] 1.6 在 MaterialApp 套用 AppTheme
- [x] 1.7 測試主題載入與基本組件樣式變化

## 2. 密碼修改頁面登出功能

- [x] 2.1 在 ChangePasswordPage 的 AppBar 新增「登出」按鈕（左上角）
- [x] 2.2 實作登出確認對話框元件（標題、內容、取消/確定按鈕）
- [x] 2.3 實作登出流程：清除 token、重置 providers、導航至登入頁
- [x] 2.4 測試強制修改密碼情境下的登出與重新登入循環
- [x] 2.5 測試登出後無法返回受保護頁面

## 3. 時間格式化工具

- [x] 3.1 新增 `lib/core/utils/date_time_formatter.dart`
- [x] 3.2 實作格式化函式，使用 intl DateFormat 產生「年-月-日 時:分:秒 時區」格式
- [x] 3.3 實作時區地點名稱映射邏輯（GMT+8 → 台北）
- [x] 3.4 撰寫單元測試驗證不同時區下的格式輸出

## 4. 打卡主頁時間與狀態顯示

- [x] 4.1 更新 AttendancePage 使用新的時間格式化工具顯示上下班時間
- [x] 4.2 實作遲到標記顯示邏輯（依據 API 的 isLate 欄位）
- [x] 4.3 設計遲到標記 UI（紅色標籤或圖示，位於上班時間旁）
- [x] 4.4 實作早退提示邏輯（比較工作時長與標準工時）
- [x] 4.5 在下班打卡按鈕附近顯示早退警告文字（黃色）
- [x] 4.6 測試準時、遲到、早退三種情境的 UI 顯示

## 5. 提早下班打卡確認對話框

- [x] 5.1 實作早退確認對話框元件（顯示已工作時長與標準工時）
- [x] 5.2 在下班打卡按鈕點擊時加入工作時長檢查邏輯
- [x] 5.3 若未滿標準工時，彈出確認對話框
- [x] 5.4 處理使用者確認或取消的流程
- [x] 5.5 測試滿足工時直接打卡、未滿工時彈出確認的流程

## 6. 打卡紀錄列表時間格式更新

- [x] 6.1 更新 AttendanceRecordsPage 的時間顯示使用新格式
- [x] 6.2 確保列表中的遲到標記顯示正常（已有功能，驗證相容性）
- [x] 6.3 測試不同月份、多筆紀錄的時間顯示

## 7. 補打卡頁面返回導航

- [x] 7.1 檢查 AmendmentDetailPage 的 AppBar 配置
- [x] 7.2 確保 AppBar 有 leading 返回按鈕（可能需調整 automaticallyImplyLeading）
- [x] 7.3 調整 GoRouter 路由配置，確認詳情頁有正確的 parent route
- [x] 7.4 測試從列表進入詳情頁再返回的流程
- [x] 7.5 測試深度連結進入詳情頁的返回行為

## 8. 底部導航固定架構重構

- [x] 8.1 重構主頁面架構為 Scaffold + bottomNavigationBar + IndexedStack
- [x] 8.2 將 AttendancePage、RecordsPage、AmendmentPage、ProfilePage 整合至 IndexedStack
- [x] 8.3 實作底部導航 selectedIndex 與路由狀態同步
- [x] 8.4 調整 GoRouter ShellRoute 配置支援固定導航列
- [x] 8.5 測試頁面切換時導航列保持固定，僅內容區轉場
- [x] 8.6 測試各頁面狀態保持（切換後再返回，資料不重新載入）
- [x] 8.7 測試子頁面（如詳情頁）覆蓋整頁包含導航列

## 9. 視覺樣式優化與統一

- [x] 9.1 調整 AttendancePage 使用新主題色彩與 8px 倍數間距
- [x] 9.2 調整 ChangePasswordPage 使用新主題色彩與樣式
- [x] 9.3 調整 AmendmentPage 與 AmendmentDetailPage 使用新主題
- [x] 9.4 調整其他主要頁面（ProfilePage、RecordsPage）使用新主題
- [x] 9.5 統一按鈕樣式（高度 48px、圓角 8px、使用 primary color）
- [x] 9.6 統一卡片樣式（圓角 12px、elevation 2/4、padding 16px）
- [x] 9.7 統一輸入框樣式（OutlinedBorder、高度 56px）
- [x] 9.8 檢查所有對話框樣式符合標準（標題 20sp、內文 16sp）

## 10. API 資料確認與調整

- [x] 10.1 確認後端 API 回應包含 `isLate` 欄位（AttendanceRecord model）
- [x] 10.2 確認後端 API 提供 `standardWorkHours` 配置（或前端配置檔定義）
- [x] 10.3 更新 domain models 包含新欄位（若有變更）
- [x] 10.4 更新 API service 與 repository（若有新 endpoint）

## 11. 整合測試與驗證

- [x] 11.1 完整流程測試：登入 → 強制修改密碼 → 登出 → 重新登入
- [x] 11.2 完整流程測試：登入 → 打卡（準時/遲到）→ 查看紀錄
- [x] 11.3 完整流程測試：登入 → 提早下班打卡 → 確認對話框 → 完成打卡
- [x] 11.4 完整流程測試：登入 → 補打卡 → 進入詳情 → 返回列表
- [x] 11.5 導航測試：在各 tab 間切換，驗證轉場動畫與狀態保持
- [x] 11.6 時區測試：調整裝置時區，驗證時間格式顯示正確
- [x] 11.7 視覺回歸測試：比對新舊 UI，確認無意外破壞
- [x] 11.8 錯誤情境測試：API 失敗、網路斷線、異常資料

