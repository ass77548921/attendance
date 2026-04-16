import 'package:flutter_riverpod/flutter_riverpod.dart';

/// Set to `true` by the Dio interceptor when token refresh fails.
/// `authProvider` listens to this and triggers `forceLogout()`.
final sessionExpiredProvider = StateProvider<bool>((ref) => false);
