import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../core/models/amendment.dart';
import '../../../core/router/app_routes.dart';
import '../../../core/theme/app_colors.dart';
import '../../../shared/layout/app_scaffold.dart';
import '../../../shared/widgets/error_retry.dart';
import '../state/amendment_provider.dart';

class AmendmentListPage extends ConsumerWidget {
  const AmendmentListPage({super.key, this.useScaffold = true});

  final bool useScaffold;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final selectedStatus = ref.watch(amendmentStatusFilterProvider);
    final amendmentsAsync = ref.watch(amendmentsProvider);

    final content = Padding(
      padding: const EdgeInsets.all(24),
      child: Column(
          children: [
            Wrap(
              spacing: 8,
              children: [
                ChoiceChip(
                  label: const Text('全部'),
                  selected: selectedStatus == null,
                  onSelected: (_) => ref.read(amendmentStatusFilterProvider.notifier).state = null,
                ),
                for (final status in AmendmentStatus.values)
                  ChoiceChip(
                    label: Text(_statusText(status)),
                    selected: selectedStatus == status,
                    onSelected: (_) => ref.read(amendmentStatusFilterProvider.notifier).state = status,
                  ),
              ],
            ),
            const SizedBox(height: 16),
            Expanded(
              child: amendmentsAsync.when(
                data: (items) {
                  final filtered = selectedStatus == null
                      ? items
                      : items.where((item) => item.status == selectedStatus).toList();
                  if (filtered.isEmpty) return const Center(child: Text('目前沒有補打卡申請'));
                  return ListView.separated(
                    itemCount: filtered.length,
                    separatorBuilder: (context, index) => const SizedBox(height: 16),
                    itemBuilder: (context, index) {
                      final amendment = filtered[index];
                      return Card(
                        child: ListTile(
                          onTap: () => context.go(AppRoutes.amendmentDetail, extra: amendment),
                          title: Text('${amendment.targetDate} · ${amendment.amendmentType == AmendmentType.clockIn ? '上班' : '下班'}'),
                          subtitle: Text(amendment.reason),
                          trailing: Chip(
                            label: Text(_statusText(amendment.status)),
                            backgroundColor: _statusColor(context, amendment.status),
                          ),
                        ),
                      );
                    },
                  );
                },
                error: (error, _) => ErrorRetry(
                  message: error.toString().replaceFirst('Exception: ', ''),
                  onRetry: () => ref.invalidate(amendmentsProvider),
                ),
                loading: () => const Center(child: CircularProgressIndicator()),
              ),
            ),
          ],
        ),
      );

    if (!useScaffold) {
      return Stack(
        children: [
          content,
          Positioned(
            right: 16,
            bottom: 16,
            child: FloatingActionButton.extended(
              onPressed: () => context.go(AppRoutes.amendmentNew),
              label: const Text('新增申請'),
              icon: const Icon(Icons.add),
            ),
          ),
        ],
      );
    }

    return AppScaffold(
      currentIndex: 2,
      title: '補打卡申請',
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () => context.go(AppRoutes.amendmentNew),
        label: const Text('新增申請'),
        icon: const Icon(Icons.add),
      ),
      child: content,
    );
  }

  String _statusText(AmendmentStatus status) {
    return switch (status) {
      AmendmentStatus.pending => '待審核',
      AmendmentStatus.approved => '已核准',
      AmendmentStatus.rejected => '已駁回',
    };
  }

  Color _statusColor(BuildContext context, AmendmentStatus status) {
    return switch (status) {
      AmendmentStatus.pending => AppColors.warning.withValues(alpha: 0.18),
      AmendmentStatus.approved => AppColors.success.withValues(alpha: 0.18),
      AmendmentStatus.rejected => AppColors.error.withValues(alpha: 0.18),
    };
  }
}