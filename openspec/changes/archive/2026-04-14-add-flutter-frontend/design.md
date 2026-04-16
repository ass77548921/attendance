## Context

現有專案已有 React Admin 前台（`frontend/`）與 Spring Boot 後端（`backend/`）。後端 REST API 已定義並實作完整的打卡、認證、補打卡流程。本次新增 Flutter 員工前台（`flutter/`），與 `backend/`、`frontend/` 並列，以相同 API 提供 iOS、Android、Web 三平台原生/網頁體驗。

## Goals / Non-Goals

**Goals:**
- 建立可在 Web、iOS、Android 運行的 Flutter 員工應用程式
- 透過 Flavor 機制支援 `dev` / `staging` / `prod` 三環境設定
- 實作 RWD：手機優先，平板/桌面自動適配寬螢幕佈局
- 涵蓋員工所有核心流程：登入、打卡、補打卡申請、個人資料、改密碼

**Non-Goals:**
- 管理員功能（Admin Console 留在現有 React 前台）
- 推播通知（Push Notification）—— 可在後續 change 擴充
- 離線模式/本地打卡快取
- 現有 React `frontend/` 的任何修改

## Decisions

### D1：Flutter + Riverpod + GoRouter（而非其他跨平台框架）

**選擇**：Flutter 3.x stable + Riverpod 2.x + GoRouter 14.x

**理由**：
- Flutter 是唯一同時支援 Web、iOS、Android 且 pixel-perfect 一致的框架
- Riverpod 提供編譯期安全的 Provider 宣告，無需 BuildContext 即可存取狀態
- GoRouter 已成 Flutter 官方推薦路由方案，支援 redirect/guard 方便實作認證守衛

**放棄的替代方案**：
- React Native：Web 支援差，需要額外 react-native-web
- Ionic/Capacitor：效能及 UI 一致性不如原生 Flutter widget

---

### D2：Flavor 多環境架構

**選擇**：Flutter Flavor（`--flavor dev/staging/prod`）搭配 `dart-define-from-file` 注入環境變數

**結構**：
```
flutter/
  lib/
    config/
      app_config.dart        # 讀取 dart-define 注入的環境值
  flavors/
    dev.json                 # API_BASE_URL, APP_NAME etc.
    staging.json
    prod.json
  android/
    app/src/{dev,staging,prod}/  # 各 Flavor 的 google-services / strings
  ios/
    Flutter/{Debug-dev,Release-prod,...}.xcconfig  # 設定即可，不執行 iOS 測試
```

**理由**：`dart-define-from-file` 不需在 IDE / CI 手動設定每個變數，以 JSON 管理更易維護。

---

### D3：JWT 儲存策略

**選擇**：`flutter_secure_storage` 儲存 Access Token 與 Refresh Token

**理由**：
- iOS → Keychain，Android → EncryptedSharedPreferences，Web → sessionStorage（降級）
- 避免 localStorage 明文儲存 token（OWASP A02）

**風險**：Web 端 `flutter_secure_storage` 回退至 localStorage（加密）。若需更高安全性可改用 httpOnly cookie，但需後端配合。

---

### D4：RWD 策略

**選擇**：使用 `LayoutBuilder` + 斷點 helper（mobile < 600, tablet 600-1200, desktop > 1200）；寬螢幕使用兩欄佈局（Navigation Rail + Content）

**放棄的替代方案**：`responsive_framework` 套件 —— 增加依賴，但核心邏輯不複雜，自行實作更可控。

---

### D5：HTTP 層

**選擇**：`dio` + Interceptor 自動刷新 Token

**理由**：dio 的 Interceptor 可在 token 過期時自動呼叫 refresh API 並 retry，無需在每個 API 呼叫處理 401。

---

### D6：專案目錄位置

**選擇**：`flutter/` 置於 repo 根目錄，與 `backend/`、`frontend/` 並列

**理由**：保持 monorepo 結構一致（`backend/`、`frontend/`、`flutter/` 三者命名對稱），Flutter 與 React Admin 為獨立部署單元，不互相依賴。

## Risks / Trade-offs

- **Flutter Web 效能**：複雜動畫在低階裝置上可能有效能損耗 → 避免過度動畫，使用 `CanvasKit` renderer。
- **Web Secure Storage**：`flutter_secure_storage` 在 Web 端安全性較原生弱 → 可接受，員工內網使用為主；必要時改用後端 httpOnly session。
- **Flavor CI 設定複雜度**：需在 GitHub Actions / Fastlane 分別設定三個 Flavor 的 build 指令 → 在 `Makefile` 或 `justfile` 中封裝常用指令降低認知負擔。
- **與現有 React Admin 的 Token 共享**：兩個前台使用相同 API，但 Cookie/Storage 互不相通，不影響各自安全邊界。

## Migration Plan

1. 在 repo 根目錄建立 `flutter/`，以 `flutter create` 初始化
2. 設定 Flavor（Android `productFlavors` + iOS Scheme/Configuration）；iOS 僅完成設定，不安排 iOS 裝置/模擬器測試
3. 依 capability 分 PR 交付（foundation → auth → attendance → amendment）
4. Web build 輸出至 `flutter/build/web/`，可獨立部署或整合進現有 docker-compose

**Rollback**：Flutter App 為全新目錄，不影響現有 React 及後端，任何時間點刪除 `flutter/` 均可完全還原。

## Open Questions

- Web 部署方式：獨立 nginx container，或整合進現有 `frontend/` nginx？
- ~~iOS Build：是否需要立即設定 Provisioning Profile / Apple Developer 帳號，或先以 Simulator 驗收？~~ **已解決**：假設已有 Apple Developer 帳號，僅完成 iOS Flavor/Scheme 設定；iOS 裝置/模擬器測試留待後續 change 補完，本 change 以 Web 與 Android 為主要驗收目標。
- 是否需要 i18n（多語系 zh/en）？目前先以中文為主。
