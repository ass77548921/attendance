import 'package:flutter/material.dart';

/// 應用程式色彩定義 - 藍色主題
class AppColors {
  // 主色調 - 藍色系
  static const Color primary = Color(0xFF2196F3); // Material Blue
  static const Color primaryDark = Color(0xFF1976D2); // Darker blue
  static const Color primaryLight = Color(0xFF64B5F6); // Lighter blue
  
  // 次要色
  static const Color secondary = Color(0xFF03A9F4); // Light Blue
  static const Color secondaryDark = Color(0xFF0288D1);
  
  // 背景色
  static const Color background = Color(0xFFF5F5F5); // Light grey background
  static const Color surface = Color(0xFFFFFFFF); // White for cards and surfaces
  static const Color surfaceVariant = Color(0xFFFAFAFA);
  
  // 文字色
  static const Color textPrimary = Color(0xFF212121); // Dark grey
  static const Color textSecondary = Color(0xFF757575); // Medium grey
  static const Color textDisabled = Color(0xFFBDBDBD); // Light grey
  static const Color textOnPrimary = Color(0xFFFFFFFF); // White on blue
  
  // 狀態色 - 語義化
  static const Color success = Color(0xFF4CAF50); // Green
  static const Color warning = Color(0xFFFF9800); // Orange
  static const Color error = Color(0xFFF44336); // Red
  static const Color info = Color(0xFF2196F3); // Blue
  
  // 業務邏輯特定色彩
  static const Color late = Color(0xFFF44336); // 遲到紅 (same as error)
  static const Color earlyLeave = Color(0xFFFF9800); // 早退橘 (same as warning)
  static const Color onTime = Color(0xFF4CAF50); // 準時綠 (same as success)
  
  // 分隔線與邊框
  static const Color divider = Color(0xFFE0E0E0);
  static const Color border = Color(0xFFBDBDBD);
  static const Color borderFocused = primary;
  
  // 陰影與覆蓋層
  static const Color shadow = Color(0x1F000000);
  static const Color overlay = Color(0x66000000);
  
  // 禁用建構函式，這是純靜態類別
  AppColors._();
}
