## Why

目前專案的儲存庫根目錄同時放置後端程式與未來前台/後台可能新增的模組，容易造成新成員理解成本增加，也會讓模組邊界不清楚。為了避免後續擴充時路徑混淆與不必要耦合，需先建立明確的後端獨立資料夾。

## What Changes

- 新增專用後端容器資料夾（例如 `backend/`），並將既有後端資產移入，同時維持現有執行行為不變。
- 更新建置、執行與本地開發的入口路徑，使其可從新後端位置正常運作。
- 更新開發文件，讓貢獻者可以清楚區分後端與未來前台/後台模組。
- API 行為、資料庫 schema 與商業邏輯維持不變。

## Capabilities

### New Capabilities
- `repository-structure-separation`: 定義並落實後端與未來前台/後台模組的儲存庫結構邊界。

### Modified Capabilities
- `local-config-guide`: 更新本地設定與執行指引，反映後端移入資料夾後的路徑。
- `readme-doc`: 更新儲存庫使用說明與指令範例，改為以新後端根目錄為準。

## Impact

- 受影響範圍：專案根目錄結構、Gradle wrapper 呼叫路徑、Docker/Compose 參考路徑、CI workflow 路徑與環境設定腳本。
- APIs：不變更任何 endpoint 契約。
- Dependencies：預期不新增執行期相依套件，主要為路徑層級調整。
- Systems：本地開發、容器啟動與 pipeline job 需在路徑遷移後持續正常。
