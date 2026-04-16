import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../core/models/user_profile.dart';
import '../../../core/network/dio_client.dart';
import '../../../core/network/network_exception_utils.dart';
import '../../../core/router/app_routes.dart';
import '../../../shared/layout/app_scaffold.dart';
import '../../../shared/widgets/error_retry.dart';
import '../../auth/state/auth_provider.dart';

final profileProvider = FutureProvider.autoDispose<UserProfile>((ref) async {
  final dio = ref.watch(dioProvider);
  final response = await dio.get<Map<String, dynamic>>('/api/users/me');
  return UserProfile.fromJson(response.data ?? <String, dynamic>{});
});

class ProfilePage extends ConsumerWidget {
  const ProfilePage({super.key, this.useScaffold = true});

  final bool useScaffold;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final profileAsync = ref.watch(profileProvider);

    final content = Padding(
      padding: const EdgeInsets.all(24),
      child: profileAsync.when(
          data: (profile) => ListView(
            children: [
              Card(
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      _ProfileRow(label: '帳號', value: profile.username),
                      _ProfileRow(label: '姓名', value: profile.fullName),
                      _ProfileRow(label: 'Email', value: profile.email),
                      _ProfileRow(label: '地址', value: profile.address ?? '-'),
                      _ProfileRow(label: '手機', value: profile.personalPhone ?? '-'),
                      _ProfileRow(label: '分機', value: profile.officeExtension ?? '-'),
                      const SizedBox(height: 16),
                      Wrap(
                        spacing: 16,
                        children: [
                          OutlinedButton(
                            onPressed: () async {
                              final updated = await context.push(AppRoutes.editProfile, extra: profile);
                              if (updated == true) {
                                ref.invalidate(profileProvider);
                              }
                            },
                            child: const Text('編輯'),
                          ),
                          OutlinedButton(
                            onPressed: () => context.push(AppRoutes.changePassword),
                            child: const Text('修改密碼'),
                          ),
                          FilledButton(
                            onPressed: () async {
                              await ref.read(authProvider.notifier).logout();
                              if (context.mounted) context.go(AppRoutes.login);
                            },
                            child: const Text('登出'),
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
              ),
            ],
          ),
          error: (error, _) => ErrorRetry(
            message: _message(error),
            onRetry: () => ref.invalidate(profileProvider),
          ),
          loading: () => const Center(child: CircularProgressIndicator()),
        ),
      );

    if (!useScaffold) {
      return content;
    }

    return AppScaffold(
      currentIndex: 3,
      title: '個人資料',
      child: content,
    );
  }

  String _message(Object error) {
    if (error is DioException) {
      return extractDioErrorMessage(error, fallback: '取得個人資料失敗');
    }
    return error.toString();
  }
}

class _ProfileRow extends StatelessWidget {
  const _ProfileRow({required this.label, required this.value});

  final String label;
  final String value;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 16),
      child: Row(
        children: [
          SizedBox(width: 88, child: Text(label)),
          Expanded(child: Text(value)),
        ],
      ),
    );
  }
}