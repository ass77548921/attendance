import 'package:dio/dio.dart';
import 'package:file_picker/file_picker.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/models/amendment.dart';
import '../../../core/network/dio_client.dart';
import '../../../core/network/network_exception_utils.dart';

final amendmentRepositoryProvider = Provider<AmendmentRepository>((ref) {
  return AmendmentRepository(ref.watch(dioProvider));
});

class AmendmentRepository {
  AmendmentRepository(this._dio);

  final Dio _dio;

  Future<List<Amendment>> getMyAmendments() async {
    try {
      final response = await _dio.get<List<dynamic>>('/api/attendance/amendments');
      return (response.data ?? <dynamic>[])
          .map((item) => Amendment.fromJson(item as Map<String, dynamic>))
          .toList();
    } on DioException catch (error) {
      throw Exception(extractDioErrorMessage(error, fallback: '取得補打卡申請失敗'));
    }
  }

  Future<Amendment> submit({
    required String targetDate,
    required AmendmentType amendmentType,
    required DateTime amendedTime,
    required String reason,
    required List<PlatformFile> attachments,
  }) async {
    final files = <MultipartFile>[];
    for (final file in attachments) {
      if (file.bytes != null) {
        files.add(MultipartFile.fromBytes(file.bytes!, filename: file.name));
      } else if (file.path != null) {
        files.add(await MultipartFile.fromFile(file.path!, filename: file.name));
      }
    }

    final formData = FormData.fromMap({
      'targetDate': targetDate,
      'amendmentType': amendmentType == AmendmentType.clockIn ? 'CLOCK_IN' : 'CLOCK_OUT',
      'amendedTime': amendedTime.toUtc().toIso8601String(),
      'reason': reason,
      if (files.isNotEmpty) 'attachments': files,
    });

    try {
      final response = await _dio.post<Map<String, dynamic>>('/api/attendance/amendments', data: formData);
      return Amendment.fromJson(response.data ?? <String, dynamic>{});
    } on DioException catch (error) {
      throw Exception(extractDioErrorMessage(error, fallback: '提交補打卡申請失敗'));
    }
  }
}