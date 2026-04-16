import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/models/attendance_record.dart';
import '../data/attendance_repository.dart';

final todayAttendanceProvider = FutureProvider.autoDispose<AttendanceRecord?>((ref) async {
  return ref.watch(attendanceRepositoryProvider).getTodayStatus();
});

final attendanceRecordsMonthProvider = StateProvider.autoDispose<DateTime>((ref) {
  final now = DateTime.now();
  return DateTime(now.year, now.month);
});

final attendanceRecordsProvider = FutureProvider.autoDispose<List<AttendanceRecord>>((ref) async {
  final month = ref.watch(attendanceRecordsMonthProvider);
  return ref.watch(attendanceRepositoryProvider).getAttendanceList(month: month);
});