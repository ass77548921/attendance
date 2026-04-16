import 'package:flutter/material.dart';
import 'app_colors.dart';

/// 自定義色彩擴展 - 用於業務邏輯特定的色彩
@immutable
class AttendanceColors extends ThemeExtension<AttendanceColors> {
  /// 遲到標記色彩
  final Color late;

  /// 早退警告色彩
  final Color earlyLeave;

  /// 準時/正常色彩
  final Color onTime;

  const AttendanceColors({
    required this.late,
    required this.earlyLeave,
    required this.onTime,
  });

  /// 預設打卡主題色彩
  factory AttendanceColors.defaultColors() {
    return const AttendanceColors(
      late: AppColors.late,
      earlyLeave: AppColors.earlyLeave,
      onTime: AppColors.onTime,
    );
  }

  @override
  ThemeExtension<AttendanceColors> copyWith({
    Color? late,
    Color? earlyLeave,
    Color? onTime,
  }) {
    return AttendanceColors(
      late: late ?? this.late,
      earlyLeave: earlyLeave ?? this.earlyLeave,
      onTime: onTime ?? this.onTime,
    );
  }

  @override
  ThemeExtension<AttendanceColors> lerp(
    ThemeExtension<AttendanceColors>? other,
    double t,
  ) {
    if (other is! AttendanceColors) {
      return this;
    }

    return AttendanceColors(
      late: Color.lerp(late, other.late, t) ?? late,
      earlyLeave: Color.lerp(earlyLeave, other.earlyLeave, t) ?? earlyLeave,
      onTime: Color.lerp(onTime, other.onTime, t) ?? onTime,
    );
  }

  /// Helper method to get AttendanceColors from context
  static AttendanceColors of(BuildContext context) {
    return Theme.of(context).extension<AttendanceColors>() ??
        AttendanceColors.defaultColors();
  }
}
