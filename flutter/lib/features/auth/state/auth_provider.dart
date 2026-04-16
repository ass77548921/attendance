import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/auth/session_expired_notifier.dart';
import '../../../core/network/auth_storage.dart';
import '../data/auth_repository.dart';

enum AuthStatus { initial, loading, authenticated, unauthenticated }

class AuthState {
  const AuthState({
    required this.status,
    this.accessToken,
    this.refreshToken,
    this.role,
    this.mustChangePassword = false,
    this.errorMessage,
    this.sessionExpiredMessage,
  });

  final AuthStatus status;
  final String? accessToken;
  final String? refreshToken;
  final String? role;
  final bool mustChangePassword;
  final String? errorMessage;
  final String? sessionExpiredMessage;

  bool get isAuthenticated => status == AuthStatus.authenticated && accessToken != null;

  AuthState copyWith({
    AuthStatus? status,
    String? accessToken,
    String? refreshToken,
    String? role,
    bool? mustChangePassword,
    String? errorMessage,
    bool clearError = false,
    String? sessionExpiredMessage,
    bool clearSessionExpiredMessage = false,
  }) {
    return AuthState(
      status: status ?? this.status,
      accessToken: accessToken ?? this.accessToken,
      refreshToken: refreshToken ?? this.refreshToken,
      role: role ?? this.role,
      mustChangePassword: mustChangePassword ?? this.mustChangePassword,
      errorMessage: clearError ? null : (errorMessage ?? this.errorMessage),
      sessionExpiredMessage: clearSessionExpiredMessage ? null : (sessionExpiredMessage ?? this.sessionExpiredMessage),
    );
  }

  factory AuthState.initial() => const AuthState(status: AuthStatus.initial);
}

final authProvider = StateNotifierProvider<AuthController, AuthState>((ref) {
  final controller = AuthController(
    ref.watch(authRepositoryProvider),
    ref.watch(authStorageProvider),
  );

  ref.listen<bool>(sessionExpiredProvider, (previous, next) async {
    if (next) {
      await controller.forceLogout();
      ref.read(sessionExpiredProvider.notifier).state = false;
    }
  });

  return controller;
});

class AuthController extends StateNotifier<AuthState> {
  AuthController(this._repository, this._storage) : super(AuthState.initial());

  final AuthRepository _repository;
  final AuthStorage _storage;

  Future<void> loadSession() async {
    final accessToken = await _storage.readAccessToken();
    final refreshToken = await _storage.readRefreshToken();
    final role = await _storage.readRole();
    final mustChangePassword = await _storage.readMustChangePassword();

    if (accessToken == null || refreshToken == null) {
      state = const AuthState(status: AuthStatus.unauthenticated);
      return;
    }

    state = AuthState(
      status: AuthStatus.authenticated,
      accessToken: accessToken,
      refreshToken: refreshToken,
      role: role,
      mustChangePassword: mustChangePassword,
    );
  }

  Future<bool> login(String username, String password) async {
    state = state.copyWith(status: AuthStatus.loading, clearError: true);
    try {
      final response = await _repository.login(username: username, password: password);
      await _storage.saveSession(
        accessToken: response.accessToken,
        refreshToken: response.refreshToken,
        role: response.role,
        mustChangePassword: response.mustChangePassword,
      );
      state = AuthState(
        status: AuthStatus.authenticated,
        accessToken: response.accessToken,
        refreshToken: response.refreshToken,
        role: response.role,
        mustChangePassword: response.mustChangePassword,
      );
      return true;
    } catch (error) {
      state = AuthState(
        status: AuthStatus.unauthenticated,
        errorMessage: error.toString().replaceFirst('Exception: ', ''),
      );
      return false;
    }
  }

  Future<void> logout() async {
    final refreshToken = state.refreshToken ?? await _storage.readRefreshToken();
    try {
      if (refreshToken != null) {
        await _repository.logout(refreshToken);
      }
    } catch (_) {
      // Ignore logout API failures and clear local session anyway.
    }
    await _storage.clear();
    state = const AuthState(status: AuthStatus.unauthenticated);
  }

  Future<void> forceLogout() async {
    if (state.status == AuthStatus.unauthenticated) return;
    await _storage.clear();
    state = const AuthState(
      status: AuthStatus.unauthenticated,
      sessionExpiredMessage: '登入已逾時，請重新登入',
    );
  }

  void clearSessionExpiredMessage() {
    state = state.copyWith(clearSessionExpiredMessage: true);
  }

  Future<bool> changePassword({required String currentPassword, required String newPassword}) async {
    try {
      await _repository.changePassword(currentPassword: currentPassword, newPassword: newPassword);
      await _storage.saveSession(
        accessToken: state.accessToken ?? await _storage.readAccessToken() ?? '',
        refreshToken: state.refreshToken ?? await _storage.readRefreshToken() ?? '',
        role: state.role ?? await _storage.readRole() ?? 'EMPLOYEE',
        mustChangePassword: false,
      );
      state = state.copyWith(mustChangePassword: false, clearError: true, status: AuthStatus.authenticated);
      return true;
    } catch (error) {
      state = state.copyWith(errorMessage: error.toString().replaceFirst('Exception: ', ''));
      return false;
    }
  }
}