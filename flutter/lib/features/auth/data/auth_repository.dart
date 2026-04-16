import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/models/token_response.dart';
import '../../../core/network/dio_client.dart';
import '../../../core/network/network_exception_utils.dart';

final authRepositoryProvider = Provider<AuthRepository>((ref) {
  return AuthRepository(ref.watch(dioProvider));
});

class AuthRepository {
  AuthRepository(this._dio);

  final Dio _dio;

  Future<TokenResponse> login({required String username, required String password}) async {
    try {
      final response = await _dio.post<Map<String, dynamic>>(
        '/api/auth/login',
        data: {'username': username, 'password': password},
      );
      return TokenResponse.fromJson(response.data ?? <String, dynamic>{});
    } on DioException catch (error) {
      throw Exception(extractDioErrorMessage(error, fallback: '登入失敗'));
    }
  }

  Future<void> logout(String refreshToken) async {
    try {
      await _dio.post<void>('/api/auth/logout', data: {'refreshToken': refreshToken});
    } on DioException catch (error) {
      throw Exception(extractDioErrorMessage(error, fallback: '登出失敗'));
    }
  }

  Future<void> changePassword({required String currentPassword, required String newPassword}) async {
    try {
      await _dio.post<void>(
        '/api/user/change-password',
        data: {'currentPassword': currentPassword, 'newPassword': newPassword},
      );
    } on DioException catch (error) {
      throw Exception(extractDioErrorMessage(error, fallback: '修改密碼失敗'));
    }
  }
}