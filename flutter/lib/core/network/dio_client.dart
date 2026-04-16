import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../config/app_config.dart';
import '../auth/session_expired_notifier.dart';
import '../models/token_response.dart';
import 'auth_storage.dart';

final dioProvider = Provider<Dio>((ref) {
  final storage = ref.watch(authStorageProvider);
  final dio = Dio(
    BaseOptions(
      baseUrl: AppConfig.apiBaseUrl,
      connectTimeout: const Duration(seconds: 15),
      receiveTimeout: const Duration(seconds: 15),
    ),
  );

  dio.interceptors.add(
    InterceptorsWrapper(
      onRequest: (options, handler) async {
        final token = await storage.readAccessToken();
        if (token != null && token.isNotEmpty) {
          options.headers['Authorization'] = 'Bearer $token';
        }
        handler.next(options);
      },
      onError: (error, handler) async {
        if (error.response?.statusCode != 401 || error.requestOptions.path.contains('/api/auth/')) {
          handler.next(error);
          return;
        }

        final refreshToken = await storage.readRefreshToken();
        if (refreshToken == null || refreshToken.isEmpty) {
          await storage.clear();
          ref.read(sessionExpiredProvider.notifier).state = true;
          handler.next(error);
          return;
        }

        try {
          final refreshDio = Dio(BaseOptions(baseUrl: AppConfig.apiBaseUrl));
          final refreshResponse = await refreshDio.post<Map<String, dynamic>>(
            '/api/auth/refresh',
            data: {'refreshToken': refreshToken},
          );

          final tokenResponse = TokenResponse.fromJson(refreshResponse.data ?? <String, dynamic>{});
          await storage.saveSession(
            accessToken: tokenResponse.accessToken,
            refreshToken: tokenResponse.refreshToken,
            role: tokenResponse.role,
            mustChangePassword: tokenResponse.mustChangePassword,
          );

          final cloned = await dio.fetch<dynamic>(
            error.requestOptions.copyWith(
              headers: {
                ...error.requestOptions.headers,
                'Authorization': 'Bearer ${tokenResponse.accessToken}',
              },
            ),
          );
          handler.resolve(cloned);
        } catch (_) {
          await storage.clear();
          ref.read(sessionExpiredProvider.notifier).state = true;
          handler.next(error);
        }
      },
    ),
  );

  return dio;
});