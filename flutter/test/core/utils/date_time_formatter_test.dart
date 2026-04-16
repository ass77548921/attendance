import 'package:flutter_test/flutter_test.dart';
import 'package:attendance_app/core/utils/date_time_formatter.dart';

void main() {
  group('DateTimeFormatter', () {
    group('formatFullDateTime', () {
      test('should format datetime with GMT+8 timezone', () {
        // 創建 GMT+8 時區的時間
        final dateTime = DateTime(2026, 4, 16, 9, 5, 23);
        final formatted = DateTimeFormatter.formatFullDateTime(dateTime);
        
        // 應包含日期、時間和時區資訊
        expect(formatted, contains('2026-04-16'));
        expect(formatted, contains('09:05:23'));
        expect(formatted, contains('GMT'));
      });

      test('should include location name for GMT+8', () {
        final dateTime = DateTime(2026, 4, 16, 9, 5, 23);
        final formatted = DateTimeFormatter.formatFullDateTime(dateTime);
        
        // GMT+8 應該顯示台北
        expect(formatted, contains('台北'));
      });
    });

    group('formatDate', () {
      test('should format date only in yyyy-MM-dd format', () {
        final dateTime = DateTime(2026, 4, 16, 9, 5, 23);
        final formatted = DateTimeFormatter.formatDate(dateTime);
        
        expect(formatted, equals('2026-04-16'));
      });

      test('should pad single digit month and day with zeros', () {
        final dateTime = DateTime(2026, 1, 5, 9, 5, 23);
        final formatted = DateTimeFormatter.formatDate(dateTime);
        
        expect(formatted, equals('2026-01-05'));
      });
    });

    group('formatTime', () {
      test('should format time only in HH:mm:ss format', () {
        final dateTime = DateTime(2026, 4, 16, 9, 5, 23);
        final formatted = DateTimeFormatter.formatTime(dateTime);
        
        expect(formatted, equals('09:05:23'));
      });

      test('should handle midnight correctly', () {
        final dateTime = DateTime(2026, 4, 16, 0, 0, 0);
        final formatted = DateTimeFormatter.formatTime(dateTime);
        
        expect(formatted, equals('00:00:00'));
      });
    });

    group('formatDateTime', () {
      test('should format datetime without timezone', () {
        final dateTime = DateTime(2026, 4, 16, 9, 5, 23);
        final formatted = DateTimeFormatter.formatDateTime(dateTime);
        
        expect(formatted, equals('2026-04-16 09:05:23'));
      });
    });

    group('calculateWorkHours', () {
      test('should calculate 8 hours correctly', () {
        final clockIn = DateTime(2026, 4, 16, 9, 0, 0);
        final clockOut = DateTime(2026, 4, 16, 17, 0, 0);
        
        final hours = DateTimeFormatter.calculateWorkHours(clockIn, clockOut);
        
        expect(hours, equals(8.0));
      });

      test('should calculate fractional hours correctly', () {
        final clockIn = DateTime(2026, 4, 16, 9, 0, 0);
        final clockOut = DateTime(2026, 4, 16, 14, 30, 0);
        
        final hours = DateTimeFormatter.calculateWorkHours(clockIn, clockOut);
        
        expect(hours, equals(5.5));
      });

      test('should handle less than 1 hour', () {
        final clockIn = DateTime(2026, 4, 16, 9, 0, 0);
        final clockOut = DateTime(2026, 4, 16, 9, 45, 0);
        
        final hours = DateTimeFormatter.calculateWorkHours(clockIn, clockOut);
        
        expect(hours, equals(0.75));
      });
    });

    group('formatDuration', () {
      test('should format 8 hours duration', () {
        final duration = const Duration(hours: 8);
        final formatted = DateTimeFormatter.formatDuration(duration);
        
        expect(formatted, equals('08:00'));
      });

      test('should format duration with minutes', () {
        final duration = const Duration(hours: 5, minutes: 30);
        final formatted = DateTimeFormatter.formatDuration(duration);
        
        expect(formatted, equals('05:30'));
      });

      test('should handle less than 1 hour', () {
        final duration = const Duration(minutes: 45);
        final formatted = DateTimeFormatter.formatDuration(duration);
        
        expect(formatted, equals('00:45'));
      });

      test('should handle more than 24 hours', () {
        final duration = const Duration(hours: 25, minutes: 15);
        final formatted = DateTimeFormatter.formatDuration(duration);
        
        expect(formatted, equals('25:15'));
      });
    });
  });
}
