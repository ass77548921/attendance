import 'package:attendance_app/core/models/token_response.dart';
import 'package:attendance_app/core/network/auth_storage.dart';
import 'package:attendance_app/core/router/app_router.dart';
import 'package:attendance_app/core/router/app_routes.dart';
import 'package:attendance_app/features/auth/data/auth_repository.dart';
import 'package:attendance_app/features/auth/state/auth_provider.dart';
import 'package:dio/dio.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

class _FakeAuthStorage extends AuthStorage {
  String? _accessToken;
  String? _refreshToken;
  String? _role;
  bool _mustChangePassword = false;

  @override
  Future<void> saveSession({
    required String accessToken,
    required String refreshToken,
    required String role,
    required bool mustChangePassword,
  }) async {
    _accessToken = accessToken;
    _refreshToken = refreshToken;
    _role = role;
    _mustChangePassword = mustChangePassword;
  }

  @override
  Future<String?> readAccessToken() async => _accessToken;

  @override
  Future<String?> readRefreshToken() async => _refreshToken;

  @override
  Future<String?> readRole() async => _role;

  @override
  Future<bool> readMustChangePassword() async => _mustChangePassword;

  @override
  Future<void> clear() async {
    _accessToken = null;
    _refreshToken = null;
    _role = null;
    _mustChangePassword = false;
  }
}

class _FakeAuthRepository extends AuthRepository {
  _FakeAuthRepository() : super(Dio());

  final List<TokenResponse> _loginResponses = <TokenResponse>[];

  void enqueueLoginResponse(TokenResponse response) {
    _loginResponses.add(response);
  }

  @override
  Future<TokenResponse> login({required String username, required String password}) async {
    if (_loginResponses.isEmpty) {
      throw Exception('No fake login response enqueued');
    }
    return _loginResponses.removeAt(0);
  }

  @override
  Future<void> logout(String refreshToken) async {}

  @override
  Future<void> changePassword({required String currentPassword, required String newPassword}) async {}
}

TokenResponse _tokenResponse({required bool mustChangePassword, String suffix = '1'}) {
  return TokenResponse(
    accessToken: 'access-$suffix',
    refreshToken: 'refresh-$suffix',
    tokenType: 'Bearer',
    expiresIn: 900,
    mustChangePassword: mustChangePassword,
    role: 'EMPLOYEE',
  );
}

void main() {
  group('Password-change logout flow', () {
    test('2.4 force-change-password logout and relogin cycle keeps mustChangePassword', () async {
      final fakeRepository = _FakeAuthRepository();
      final fakeStorage = _FakeAuthStorage();
      fakeRepository.enqueueLoginResponse(_tokenResponse(mustChangePassword: true, suffix: 'first'));
      fakeRepository.enqueueLoginResponse(_tokenResponse(mustChangePassword: true, suffix: 'second'));

      final container = ProviderContainer(
        overrides: [
          authRepositoryProvider.overrideWithValue(fakeRepository),
          authStorageProvider.overrideWithValue(fakeStorage),
        ],
      );
      addTearDown(container.dispose);

      final controller = container.read(authProvider.notifier);

      final firstLoginSuccess = await controller.login('user', 'password');
      expect(firstLoginSuccess, isTrue);
      expect(container.read(authProvider).isAuthenticated, isTrue);
      expect(container.read(authProvider).mustChangePassword, isTrue);

      await controller.logout();
      expect(container.read(authProvider).status, AuthStatus.unauthenticated);
      expect(await fakeStorage.readAccessToken(), isNull);

      final secondLoginSuccess = await controller.login('user', 'password');
      expect(secondLoginSuccess, isTrue);
      expect(container.read(authProvider).isAuthenticated, isTrue);
      expect(container.read(authProvider).mustChangePassword, isTrue);
    });

    test('2.5 route guard blocks protected page after logout', () async {
      final fakeRepository = _FakeAuthRepository();
      final fakeStorage = _FakeAuthStorage();
      fakeRepository.enqueueLoginResponse(_tokenResponse(mustChangePassword: false, suffix: 'normal'));

      final container = ProviderContainer(
        overrides: [
          authRepositoryProvider.overrideWithValue(fakeRepository),
          authStorageProvider.overrideWithValue(fakeStorage),
        ],
      );
      addTearDown(container.dispose);

      final controller = container.read(authProvider.notifier);
      await controller.login('user', 'password');
      expect(container.read(authProvider).status, AuthStatus.authenticated);

      await controller.logout();
      final authAfterLogout = container.read(authProvider);
      expect(authAfterLogout.status, AuthStatus.unauthenticated);

      expect(
        resolveAuthRedirect(auth: authAfterLogout, location: AppRoutes.attendance),
        AppRoutes.login,
      );
    });
  });
}
