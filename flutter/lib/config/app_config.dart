/// App configuration injected via --dart-define-from-file using files under the flavors directory.
///
/// Usage:
///   flutter run --flavor stage --dart-define-from-file=flavors/stage.json
///   flutter build apk --flavor pro --dart-define-from-file=flavors/pro.json
class AppConfig {
  AppConfig._();

  static const Set<String> _approvedApiBaseUrls = {
    'https://dev.for-sky.com',
    'http://localhost:8080',
    'https://for-sky.com',
  };

  static String _validatedApiBaseUrl() {
    const configuredApiBaseUrl = String.fromEnvironment(
      'API_BASE_URL',
      defaultValue: '',
    );

    if (configuredApiBaseUrl.isEmpty) {
      throw StateError('Missing required dart-define: API_BASE_URL');
    }

    if (!_approvedApiBaseUrls.contains(configuredApiBaseUrl)) {
      throw StateError(
        'API_BASE_URL is not in approved CORS matrix: $configuredApiBaseUrl',
      );
    }

    return configuredApiBaseUrl;
  }

  static String get apiBaseUrl => _validatedApiBaseUrl();

  static const String appName = String.fromEnvironment(
    'APP_NAME',
    defaultValue: '打卡',
  );

  static const String appSuffix = String.fromEnvironment(
    'APP_SUFFIX',
    defaultValue: '',
  );

  static int get standardWorkHours {
    const configured = int.fromEnvironment(
      'STANDARD_WORK_HOURS',
      defaultValue: 8,
    );
    if (configured <= 0 || configured > 24) {
      throw StateError('STANDARD_WORK_HOURS must be between 1 and 24');
    }
    return configured;
  }
}
