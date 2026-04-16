import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

final authStorageProvider = Provider<AuthStorage>((ref) => const AuthStorage());

class AuthStorage {
  const AuthStorage();

  static const _storage = FlutterSecureStorage();
  static const _accessTokenKey = 'access_token';
  static const _refreshTokenKey = 'refresh_token';
  static const _roleKey = 'role';
  static const _mustChangePasswordKey = 'must_change_password';

  Future<void> saveSession({
    required String accessToken,
    required String refreshToken,
    required String role,
    required bool mustChangePassword,
  }) async {
    await _storage.write(key: _accessTokenKey, value: accessToken);
    await _storage.write(key: _refreshTokenKey, value: refreshToken);
    await _storage.write(key: _roleKey, value: role);
    await _storage.write(key: _mustChangePasswordKey, value: mustChangePassword.toString());
  }

  Future<String?> readAccessToken() => _storage.read(key: _accessTokenKey);
  Future<String?> readRefreshToken() => _storage.read(key: _refreshTokenKey);
  Future<String?> readRole() => _storage.read(key: _roleKey);

  Future<bool> readMustChangePassword() async {
    final value = await _storage.read(key: _mustChangePasswordKey);
    return value == 'true';
  }

  Future<void> clear() => _storage.deleteAll();
}