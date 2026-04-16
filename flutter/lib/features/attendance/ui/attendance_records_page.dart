import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/theme/app_colors.dart';
import '../../../core/theme/attendance_colors.dart';
import '../../../core/utils/date_time_formatter.dart';
import '../../../shared/layout/app_scaffold.dart';
import '../../../shared/widgets/error_retry.dart';
import '../state/attendance_provider.dart';

class AttendanceRecordsPage extends ConsumerWidget {
  const AttendanceRecordsPage({super.key, this.useScaffold = true});

  final bool useScaffold;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final month = ref.watch(attendanceRecordsMonthProvider);
    final recordsAsync = ref.watch(attendanceRecordsProvider);

    final content = Padding(
      padding: const EdgeInsets.all(24),
      child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Row(
              children: [
                IconButton(
                  onPressed: () => ref.read(attendanceRecordsMonthProvider.notifier).state = DateTime(month.year, month.month - 1),
                  icon: const Icon(Icons.chevron_left),
                ),
                Expanded(
                  child: Text('${month.year}年${month.month}月', textAlign: TextAlign.center),
                ),
                IconButton(
                  onPressed: () => ref.read(attendanceRecordsMonthProvider.notifier).state = DateTime(month.year, month.month + 1),
                  icon: const Icon(Icons.chevron_right),
                ),
              ],
            ),
            const SizedBox(height: 16),
            Expanded(
              child: recordsAsync.when(
                skipLoadingOnRefresh: false,
                data: (records) {
                  if (records.isEmpty) {
                    return const Center(child: Text('本月無打卡紀錄'));
                  }
                  return ListView.separated(
                    itemCount: records.length,
                    separatorBuilder: (context, index) => const SizedBox(height: 16),
                    itemBuilder: (context, index) {
                      final record = records[index];
                      final attendanceColors = AttendanceColors.of(context);
                      
                      // 格式化時間
                      final clockInFormatted = record.clockInTime != null
                          ? DateTimeFormatter.formatFullDateTime(DateTime.parse(record.clockInTime!).toLocal())
                          : '-';
                      final clockOutFormatted = record.clockOutTime != null
                          ? DateTimeFormatter.formatFullDateTime(DateTime.parse(record.clockOutTime!).toLocal())
                          : '-';
                      final durationFormatted = record.workDurationMinutes != null
                          ? DateTimeFormatter.formatDuration(Duration(minutes: record.workDurationMinutes!))
                          : '-';
                      
                      return Card(
                        child: Padding(
                          padding: const EdgeInsets.all(16),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Row(
                                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                children: [
                                  Text(
                                    record.workDate,
                                    style: Theme.of(context).textTheme.titleMedium,
                                  ),
                                  if (record.isLate)
                                    Container(
                                      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                                      decoration: BoxDecoration(
                                        color: attendanceColors.late,
                                        borderRadius: BorderRadius.circular(8),
                                      ),
                                      child: Text(
                                        '遲到',
                                        style: TextStyle(
                                          fontSize: 12,
                                          fontWeight: FontWeight.w600,
                                          color: AppColors.textOnPrimary,
                                        ),
                                      ),
                                    ),
                                ],
                              ),
                              const SizedBox(height: 8),
                              Text(
                                '上班：$clockInFormatted',
                                style: Theme.of(context).textTheme.bodySmall,
                              ),
                              const SizedBox(height: 4),
                              Text(
                                '下班：$clockOutFormatted',
                                style: Theme.of(context).textTheme.bodySmall,
                              ),
                              const SizedBox(height: 4),
                              Text(
                                '工時：$durationFormatted',
                                style: Theme.of(context).textTheme.bodySmall,
                              ),
                            ],
                          ),
                        ),
                      );
                    },
                  );
                },
                error: (error, _) => ErrorRetry(
                  message: error.toString().replaceFirst('Exception: ', ''),
                  onRetry: () => ref.invalidate(attendanceRecordsProvider),
                ),
                loading: () => const Center(child: CircularProgressIndicator()),
              ),
            ),
          ],
        ),
      );

    if (!useScaffold) {
      return content;
    }

    return AppScaffold(
      currentIndex: 1,
      title: '打卡紀錄',
      child: content,
    );
  }
}