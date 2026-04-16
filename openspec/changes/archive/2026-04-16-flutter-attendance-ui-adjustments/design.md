## Context

Flutter 前台應用目前存在多個用戶體驗問題：

1. **密碼修改流程困境**：強制密碼修改頁面缺乏登出機制，用戶被迫修改密碼或關閉應用
2. **打卡資訊不完整**：時間顯示格式簡略，缺少時區與完整日期資訊3. **狀態提示不足**：遲到標記僅在紀錄列表顯示，主頁缺少；無早退提醒機制4. **誤操作風險**：提早下班打卡無確認機制，容易誤觸
5. **導航體驗問題**：補打卡詳情頁無返回按鈕；底部導航切換時整頁轉場
6. **視覺風格不統一**：缺乏統一主題系統，色彩與排版不一致

**技術背景：**
- 使用 Flutter + Riverpod 狀態管理
- 使用 GoRouter 進行路由管理
- 已有基礎 API 與 domain models
- 打卡邏輯包含 `isLate` 判定（後端計算）

**限制：**
- 需保持與現有 API contract 相容
- 需維持現有頁面結構，避免大規模重構
- 遲到與早退判定依賴後端資料或配置

## Goals / Non-Goals

**Goals:**
- 提供強制密碼修改頁面的登出選項，改善用戶體驗
- 統一時間顯示格式為完整的「年-月-日 時:分:秒 時區地點」
- 在打卡主頁顯示遲到標記，並新增早退提示機制
- 新增提早下班打卡的確認對話框，防止誤操作
- 為補打卡詳情頁新增返回導航
- 修復底部導航切換時的整頁轉場問題
- 建立藍色為主色調的統一主題系統，優化排版與視覺一致性

**Non-Goals:**
- 不修改後端 API contract（遲到/早退判定邏輯維持後端提供）
- 不重構整體架構或狀態管理方式
- 不引入新的大型依賴項（如 UI kit 框架）
- 不處理 iOS 特定的打卡定位功能（不在此次範圍）

## Decisions

### Decision 1: 主題系統實現方式
**選擇：**使用 Flutter 標準 `ThemeData` + `ThemeExtension` 機制

**理由：**
- Flutter 內建支援，無需額外依賴
- `ThemeData` 提供完整的 MaterialDesign 組件樣式定義
- `ThemeExtension` 允許擴展自定義色彩（如遲到紅、早退橘）
- 可透過 `MaterialApp` 全域套用，所有組件自動繼承

**替代方案：**
- 自定義 theme provider：增加維護成本，重複造輪
- 直接硬編碼色彩：無法統一管理，未來修改困難

**實現位置：**
- `lib/core/theme/app_theme.dart` - 定義 ThemeData 與 ColorScheme
- `lib/core/theme/app_colors.dart` - 集中管理所有色彩常數
- `lib/core/theme/app_text_styles.dart` - 定義 TextTheme

---

### Decision 2: 時間格式化策略
**選擇：**使用 `intl` package 的 DateFormat + 自定義格式字串

**理由：**
- `intl` 是 Flutter 官方維護的國際化套件
- 支援時區顯示（`z` 或 `zzzz`）與完整格式
- 可定義統一的格式常數避免重複

**格式定義：**
```dart
final dateTimeFormat = DateFormat('yyyy-MM-dd HH:mm:ss z', 'zh_TW');
```

**時區地點顯示：**
- 使用裝置時區（`DateTime.now().timeZoneName`）
- 地點名稱依時區 offset 映射（如 GMT+8 → 台北）

**替代方案：**
- 手動字串拼接：容易出錯，維護困難
- 僅使用 `toString()`：格式不夠靈活，缺少時區資訊

---

### Decision 3: 遲到與早退判定邏輯位置
**選擇：**依賴後端 API 回應的 `isLate` 欄位與標準工時配置

**理由：**
- 避免前端重複業務邏輯，單一真實來源
- 規定上班時間與標準工時可能因公司政策調整，由後端統一管理
- 前端僅負責顯示邏輯，不計算判定結果

**前端需求：**
- API 需提供 `isLate` (boolean) 欄位於打卡紀錄中
- API 需提供 `standardWorkHours` (number) 配置，用於早退判定
- 若後端暫無提供，前端可從環境變數或配置檔讀取（臨時方案）

**替代方案：**
- 前端自行計算：需要同步規則，容易出現不一致
- 混合模式（前端計算 + 後端驗證）：複雜度高，維護困難

---

### Decision 4: 底部導航固定實現方式
**選擇：**使用單一 `Scaffold` + `IndexedStack` + `bottomNavigationBar`

**理由：**
- `IndexedStack` 保持所有頁面狀態，不會重新載入
- `bottomNavigationBar` 作為 Scaffold 的固定組件，不參與頁面切換
- 與 GoRouter 配合良好，可透過路由狀態同步 selectedIndex

**架構：**
```
Scaffold
├─ bottomNavigationBar: BottomNavigationBar(固定)
└─ body: IndexedStack
    ├─ AttendancePage (index 0)
    ├─ RecordsPage (index 1)
    ├─ AmendmentPage (index 2)
    └─ ProfilePage (index 3)
```

**GoRouter 整合：**
- 定義 `ShellRoute` 包裝底部導航頁面
- 子頁面（如詳情頁）使用 standard push，覆蓋整頁

**替代方案：**
- PageView：會有滑動手勢副作用，不適合 tab 導航
- 每次切換 push/pop：破壞頁面狀態，體驗差
- Navigator 2.0 複雜嵌套：過度設計，維護成本高

---

### Decision 5: 強制密碼修改頁面登出流程
**選擇：**彈出確認對話框 → 清除 token → 導航至登入頁

**理由：**
- 確認對話框避免誤觸，提醒用戶後果
- 清除 token 確保安全，防止後續 API 呼叫失敗
- 導航回登入頁符合標準流程

**狀態清理：**
- 呼叫 AuthNotifier 的 logout() 方法
- 清除 SecureStorage 中的 access/refresh token
- 重置 Riverpod 相關 providers

**UI 設計：**
- AppBar 左上角顯示「登出」文字按鈕（次要動作）
- 對話框標題：「確定要登出嗎？」
- 內容：「您尚未完成密碼修改，下次登入時仍需修改密碼。」
- 動作：「取消」（TextButton）、「確定登出」（ElevatedButton，警告色）

---

### Decision 6: 早退確認對話框觸發時機
**選擇：**下班打卡按鈕點擊時，若工作時長 < 標準工時則彈出

**理由：**
- 即時攔截，防止誤操作
- 提供工作時長資訊，讓用戶決定
- 不影響正常下班打卡流程（滿足工時時不彈出）

**實現邏輯：**
```dart
Future<void> _onClockOut() async {
  final workedHours = _calculateWorkedHours();
  final standardHours = ref.read(attendanceConfigProvider).standardWorkHours;
  
  if (workedHours < standardHours) {
    final confirmed = await _showEarlyLeaveDialog(workedHours, standardHours);
    if (!confirmed) return; // 用戶取消
  }
  
  await _executeClockOut();
}
```

**UI 設計：**
- 對話框標題：「提早下班確認」
- 內容：「您的工作時長未滿標準工時（已工作 {X} 小時，標準 {Y} 小時），確定要下班打卡嗎？」
- 動作：「取消」、「確定打卡」（警告色）

## Risks / Trade-offs

### Risk 1: 後端 API 資料不足
**風險：**若後端未提供 `isLate`、`standardWorkHours` 等欄位，前端無法實現功能

**緩解：**
- 優先與後端確認 API 資料結構
- 若欄位缺失，提前協調後端新增或前端改用配置檔臨時方案
- 記錄 API contract 變更於 migration plan

---

### Risk 2: IndexedStack 記憶體使用
**風險：**IndexedStack 同時保持所有頁面實例，可能增加記憶體佔用

**緩解：**
- Flutter 的 Widget tree 輕量，4-5 個頁面影響有限
- 若未來頁面增多或單頁過重，考慮改用 AutomaticKeepAliveClientMixin 選擇性保持
- 監控實際記憶體使用，必要時調整策略

---

### Risk 3: 時區格式在不同裝置顯示不一致
**風險：**不同裝置時區設定可能導致顯示格式差異（如 GMT+8 vs CST）

**緩解：**
- 統一使用 `intl` 的 `z` 格式（短時區名稱）
- 在 UI 設計時預留足夠寬度，避免時區名稱過長截斷
- 測試不同時區設定下的顯示效果

---

### Risk 4: 主題系統遷移影響現有頁面
**風險：**引入新主題可能破壞現有頁面的硬編碼色彩或樣式

**緩解：**
- 採用漸進式遷移：先定義主題，再逐頁面調整
- 使用 Theme.of(context) 替換硬編碼色彩
- 保留舊色彩定義作為過渡期 fallback
- 進行視覺回歸測試確保無破壞性變更

---

### Risk 5: 登出功能可能被濫用延後密碼修改
**風險：**用戶可能反覆登出以規避密碼修改要求

**緩解：**
- 這是產品決策，技術上已實現限制（下次登入仍強制）
- 可在對話框中強調「下次登入時仍需修改」
- 若需進一步限制，可考慮增加登出次數追蹤或強制時限（需後端配合）

## Migration Plan

### Phase 1: 主題系統建立（不影響現有功能）
1. 創建 `lib/core/theme/` 目錄結構
2. 定義 `app_theme.dart`、`app_colors.dart`、`app_text_styles.dart`
3. 在 `MaterialApp` 套用新主題
4. 測試主題載入與組件樣式變化

**驗證：**應用程式啟動正常，色彩開始向藍色主題過渡

---

### Phase 2: 功能增強與 Bug 修復（並行開發）
**2.1 密碼修改頁面登出功能**
- 修改 `ChangePasswordPage`，新增登出按鈕
- 實現確認對話框與登出流程
- 測試強制修改與登出循環

**2.2 打卡頁面時間格式調整**
- 新增 `DateTimeFormatter` utility class
- 更新 `AttendancePage` 與 `AttendanceRecordsPage` 的時間顯示
- 測試不同時區設定

**2.3 遲到/早退功能**
- 確認 API 回應包含 `isLate`、`standardWorkHours`
- 更新 `AttendancePage` 顯示遲到標記與早退提示
- 實現早退確認對話框
- 測試邊界條件（準時、遲到、早退）

**2.4 補打卡頁面返回導航**
- 修改 `AmendmentDetailPage`，確保 AppBar 有 leading 返回按鈕
- 調整路由配置（若使用 GoRouter，確保有 parent route）

**2.5 底部導航固定**
- 重構主頁面架構為 Scaffold + IndexedStack
- 調整 GoRouter ShellRoute 配置
- 測試頁面切換與狀態保持

**驗證：**每個功能獨立測試通過，無互相影響

---

### Phase 3: 視覺優化與整合測試
1. 逐頁面套用新主題色彩
2. 調整 padding、margin 符合 8px 基數規範
3. 統一按鈕、卡片、輸入框樣式
4. 全流程測試（登入 → 強制修改 → 打卡 → 補打卡 → 登出）
5. 進行視覺回歸測試，比對新舊 UI 差異

**驗證：**整體 UI 一致性高，所有功能正常運作

---

### Phase 4: 發布與監控
1. 部署至 staging 環境進行 UAT
2. 收集用戶回饋，調整細節
3. 發布至 production
4. 監控 Sentry / Crashlytics 是否有新 errors
5. 追蹤用戶登出率與早退確認對話框點擊率

**Rollback 策略：**
- 若出現嚴重 UI 錯誤，可快速回退至前一版本
- 主題系統與功能變更相對獨立，可分別回退
- 保留舊版 APK 供緊急降級

## Open Questions

1. **標準工時配置來源**：後端是否已提供 `standardWorkHours` API？若否，預計何時提供？前端是否需臨時使用配置檔？
2. **時區地點名稱映射**：是否需要完整的時區 offset → 地點名稱映射表？還是僅顯示 GMT offset？
3. **遲到早退記錄**：早退打卡是否需要記錄在後端（如新增 `isEarlyLeave` 欄位）？還是僅前端提示？
4. **主題切換功能**：未來是否需要支援深色模式或多主題切換？目前僅實現單一藍色主題。
5. **登出次數限制**：產品是否需要追蹤用戶在強制密碼修改時的登出次數？需要後端支援。
