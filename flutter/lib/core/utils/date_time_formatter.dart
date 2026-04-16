import 'package:intl/intl.dart';

/// 日期時間格式化工具
class DateTimeFormatter {
  DateTimeFormatter._();

  /// 完整日期時間格式：年-月-日 時:分:秒 時區地點
  /// 例如：2026-04-16 09:05:23 GMT+8 台北
  static String formatFullDateTime(DateTime dateTime) {
    // 基本格式：年-月-日 時:分:秒 時區
    final formatter = DateFormat('yyyy-MM-dd HH:mm:ss');
    final formatted = formatter.format(dateTime);
    
    // 取得時區資訊
    final timeZone = _getTimeZoneName(dateTime);
    final location = _getLocationFromTimeZone(dateTime);
    
    return '$formatted $timeZone $location';
  }

  /// 只格式化日期：年-月-日
  static String formatDate(DateTime dateTime) {
    final formatter = DateFormat('yyyy-MM-dd');
    return formatter.format(dateTime);
  }

  /// 只格式化時間：時:分:秒
  static String formatTime(DateTime dateTime) {
    final formatter = DateFormat('HH:mm:ss');
    return formatter.format(dateTime);
  }

  /// 格式化日期時間不含時區：年-月-日 時:分:秒
  static String formatDateTime(DateTime dateTime) {
    final formatter = DateFormat('yyyy-MM-dd HH:mm:ss');
    return formatter.format(dateTime);
  }

  /// 取得時區名稱（如 GMT+8）
  static String _getTimeZoneName(DateTime dateTime) {
    // 取得 UTC 偏移量（分鐘）
    final offset = dateTime.timeZoneOffset;
    final hours = offset.inHours;
    final minutes = offset.inMinutes % 60;
    
    if (hours >= 0) {
      if (minutes == 0) {
        return 'GMT+$hours';
      } else {
        return 'GMT+$hours:${minutes.toString().padLeft(2, '0')}';
      }
    } else {
      final absHours = hours.abs();
      if (minutes == 0) {
        return 'GMT-$absHours';
      } else {
        return 'GMT-$absHours:${minutes.abs().toString().padLeft(2, '0')}';
      }
    }
  }

  /// 根據時區偏移量映射地點名稱
  static String _getLocationFromTimeZone(DateTime dateTime) {
    final offset = dateTime.timeZoneOffset.inHours;
    
    // 時區映射表（常見亞洲時區）
    switch (offset) {
      case 8:
        return '台北'; // GMT+8: 台北、香港、新加坡、北京
      case 9:
        return '東京'; // GMT+9: 東京、首爾
      case 7:
        return '曼谷'; // GMT+7: 曼谷、雅加達
      case 5:
        return '伊斯蘭馬巴德'; // GMT+5: 巴基斯坦
      case 6:
        return '達卡'; // GMT+6: 達卡
      case 0:
        return '倫敦'; // GMT+0: 倫敦
      case -5:
        return '紐約'; // GMT-5: 紐約
      case -8:
        return '洛杉磯'; // GMT-8: 洛杉磯
      default:
        // 若無法映射，返回 UTC+X 格式
        if (offset >= 0) {
          return 'UTC+$offset';
        } else {
          return 'UTC$offset';
        }
    }
  }

  /// 計算兩個時間之間的時長（小時）
  static double calculateWorkHours(DateTime clockIn, DateTime clockOut) {
    final duration = clockOut.difference(clockIn);
    return duration.inMinutes / 60.0;
  }

  /// 格式化工作時長為 HH:mm 格式
  static String formatDuration(Duration duration) {
    final hours = duration.inHours;
    final minutes = duration.inMinutes.remainder(60);
    return '${hours.toString().padLeft(2, '0')}:${minutes.toString().padLeft(2, '0')}';
  }
}
