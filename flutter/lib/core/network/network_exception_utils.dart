import 'package:dio/dio.dart';

String extractDioErrorMessage(DioException error, {required String fallback}) {
  if (error.response == null) {
    return '伺服器無回應，請稍後再試';
  }
  final data = error.response?.data;
  if (data is Map<String, dynamic>) {
    return (data['message'] ?? data['detail'] ?? data['error'] ?? fallback).toString();
  }
  return fallback;
}
