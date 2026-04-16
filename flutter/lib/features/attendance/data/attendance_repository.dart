import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/models/attendance_record.dart';
import '../../../core/network/dio_client.dart';
import '../../../core/network/network_exception_utils.dart';

final attendanceRepositoryProvider = Provider<AttendanceRepository>((ref) {
  return AttendanceRepository(ref.watch(dioProvider));
});

class AttendanceRepository {
  AttendanceRepository(this._dio);

  final Dio _dio;

  Future<AttendanceRecord?> getTodayStatus() async {
    try {
      final response = await _dio.get<Map<String, dynamic>>('/api/attendance/today');
      return AttendanceRecord.fromJson(response.data ?? <String, dynamic>{});
    } on DioException catch (error) {
      if (error.response?.statusCode == 204) return null;
      throw Exception(extractDioErrorMessage(error, fallback: '取得今日打卡狀態失敗'));
    }
  }

  Future<AttendanceRecord> clockIn() async {
    try {
      final response = await _dio.post<Map<String, dynamic>>('/api/attendance/clock-in');
      return AttendanceRecord.fromJson(response.data ?? <String, dynamic>{});
    } on DioException catch (error) {
      throw Exception(extractDioErrorMessage(error, fallback: '上班打卡失敗'));
    }
  }

  Future<AttendanceRecord> clockOut() async {
    try {
      final response = await _dio.post<Map<String, dynamic>>('/api/attendance/clock-out');
      return AttendanceRecord.fromJson(response.data ?? <String, dynamic>{});
    } on DioException catch (error) {
      throw Exception(extractDioErrorMessage(error, fallback: '下班打卡失敗'));
    }
  }

  Future<List<AttendanceRecord>> getAttendanceList({required DateTime month}) async {
    final start = DateTime(month.year, month.month, 1);
    final end = DateTime(month.year, month.month + 1, 0);
    try {
      final response = await _dio.get<List<dynamic>>(
        '/api/attendance',
        queryParameters: {
          'startDate': _date(start),
          'endDate': _date(end),
        },
      );
      return (response.data ?? <dynamic>[])
          .map((item) => AttendanceRecord.fromJson(item as Map<String, dynamic>))
          .toList();
    } on DioException catch (error) {
      throw Exception(extractDioErrorMessage(error, fallback: '取得打卡紀錄失敗'));
    }
  }

  String _date(DateTime date) => date.toIso8601String().split('T').first;
}