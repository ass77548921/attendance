## 1. Backend

- [x] 1.1 在 `UpdateProfileRequest.java` 新增 `address`、`personalPhone`、`officeExtension` 三個選填欄位（`@Size` 長度限制與 `User` 欄位一致）
- [x] 1.2 更新 `UserService.updateProfile()` — 新增 address/personalPhone/officeExtension 的 partial-update 邏輯（null = 保留；空字串可考慮清空）

## 2. Frontend (React)

- [x] 2.1 在 `api.ts` 新增 `UpdateMyProfileRequest` 介面（含 `fullName`、`email`、`address`、`personalPhone`、`officeExtension`、`password` 選填欄位）
- [x] 2.2 更新 `ProfilePage.tsx` form state，新增 `address`、`personalPhone`、`officeExtension` 欄位
- [x] 2.3 在 `ProfilePage.tsx` 表單中新增「地址」、「個人聯絡電話」、「公司分機電話」三個選填輸入欄（位於 email 欄位之後）
- [x] 2.4 更新 `ProfilePage.tsx` 的 `handleSubmit`，將三個新欄位加入 request body，空字串轉 null 後送出

## 3. Flutter

- [x] 3.1 新增 `EditProfilePage`（`flutter/lib/features/profile/ui/edit_profile_page.dart`），包含 fullName、email、address（選填）、personalPhone（選填）、officeExtension（選填）表單，使用 `dioProvider` 呼叫 `PUT /api/users/me`
- [x] 3.2 在 `AppRoutes` 新增 `editProfile` 路由常數
- [x] 3.3 在 `app_router.dart`（或 router 設定檔）新增 `EditProfilePage` 路由
- [x] 3.4 在 `ProfilePage` 的 Card 中新增「編輯」按鈕，點擊後 `context.push(AppRoutes.editProfile)`，並在返回後呼叫 `ref.invalidate(profileProvider)`
