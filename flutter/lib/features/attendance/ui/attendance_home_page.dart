import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../config/app_config.dart';
import '../../../core/models/attendance_record.dart';
import '../../../core/theme/app_colors.dart';
import '../../../core/theme/attendance_colors.dart';
import '../../../core/utils/date_time_formatter.dart';
import '../../../shared/layout/app_scaffold.dart';
import '../../../shared/widgets/error_retry.dart';
import '../data/attendance_repository.dart';
import '../state/attendance_provider.dart';

class AttendanceHomePage extends ConsumerWidget {
  const AttendanceHomePage({super.key, this.useScaffold = true});

  final bool useScaffold;

  Future<void> _handleAction(BuildContext context, WidgetRef ref, AttendanceRecord? today) async {
    final repository = ref.read(attendanceRepositoryProvider);
    try {
      if (today == null || today.clockInTime == null) {
        // 上班打卡
        await repository.clockIn();
        if (context.mounted) {
          ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('上班打卡成功')));
        }
      } else if (today.clockOutTime == null) {
        // 下班打卡 - 檢查是否早退
        final standardWorkHours = AppConfig.standardWorkHours;
        final workedMinutes = today.workDurationMinutes ?? 0;
        final workedHours = workedMinutes / 60.0;
        
        if (workedHours < standardWorkHours) {
          // 顯示早退確認對話框
          final shouldClockOut = await _showEarlyLeaveConfirmDialog(
            context,
            workedHours,
            standardWorkHours.toDouble(),
          );
          if (shouldClockOut != true) return;
        }
        
        await repository.clockOut();
        if (context.mounted) {
          ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('下班打卡成功')));
        }
      }
      ref.invalidate(todayAttendanceProvider);
      ref.invalidate(attendanceRecordsProvider);
    } catch (error) {
      if (!context.mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(error.toString().replaceFirst('Exception: ', ''))),
      );
    }
  }

  Future<bool?> _showEarlyLeaveConfirmDialog(
    BuildContext context,
    double workedHours,
    double standardHours,
  ) {
    return showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('提早下班確認'),
        content: Text(
          '您的工作時長未滿標準工時（已工作 ${workedHours.toStringAsFixed(1)} 小時，標準 ${standardHours.toStringAsFixed(0)} 小時），確定要下班打卡嗎？',
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(false),
            child: const Text('取消'),
          ),
          ElevatedButton(
            onPressed: () => Navigator.of(context).pop(true),
            style: ElevatedButton.styleFrom(
              backgroundColor: AppColors.warning,
            ),
            child: const Text('確定打卡'),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final todayAsync = ref.watch(todayAttendanceProvider);

    final content = Padding(
      padding: const EdgeInsets.all(24),
      child: todayAsync.when(
        data: (today) {
          final hasClockIn = today?.clockInTime != null;
          final hasClockOut = today?.clockOutTime != null;
          final actionText = !hasClockIn ? '上班打卡' : hasClockOut ? '今日已完成' : '下班打卡';
          final isLate = today?.isLate ?? false;
          
          // 計算是否早退
          final standardWorkHours = AppConfig.standardWorkHours;
          final workedMinutes = today?.workDurationMinutes ?? 0;
          final workedHours = workedMinutes / 60.0;
          // 未下班時顯示預警
          final showEarlyLeaveWarning = hasClockIn && !hasClockOut && workedHours < standardWorkHours;
          // 已下班但工時不足時顯示早退記錄
          final hasLeftEarly = hasClockOut && workedHours < standardWorkHours;

          return Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              Card(
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text('目前時間', style: Theme.of(context).textTheme.labelLarge),
                        const SizedBox(height: 8),
                        Text(
                          DateTimeFormatter.formatFullDateTime(DateTime.now()),
                          style: Theme.of(context).textTheme.headlineSmall,
                        ),
                        const SizedBox(height: 24),
                        _InfoRow(
                          label: '上班時間',
                          value: today?.clockInTime != null
                              ? DateTimeFormatter.formatFullDateTime(DateTime.parse(today!.clockInTime!).toLocal())
                              : '-',
                          isLate: isLate,
                        ),
                        const SizedBox(height: 16),
                        _InfoRow(
                          label: '下班時間',
                          value: today?.clockOutTime != null
                              ? DateTimeFormatter.formatFullDateTime(DateTime.parse(today!.clockOutTime!).toLocal())
                              : '-',
                        ),
                        const SizedBox(height: 16),
                        _InfoRow(
                          label: '工作時長',
                          value: today?.workDurationMinutes == null
                              ? '-'
                              : DateTimeFormatter.formatDuration(
                                  Duration(minutes: today!.workDurationMinutes!),
                                ),
                          isEarlyLeave: hasLeftEarly,
                        ),
                        if (showEarlyLeaveWarning) ...[
                          const SizedBox(height: 16),
                          Row(
                            children: [
                              Icon(Icons.warning, color: AppColors.warning, size: 16),
                              const SizedBox(width: 8),
                              Expanded(
                                child: Text(
                                  '提醒：目前工作未滿標準工時，可能視為早退',
                                  style: TextStyle(
                                    fontSize: 14,
                                    color: AppColors.warning,
                                  ),
                                ),
                              ),
                            ],
                          ),
                        ],
                        const SizedBox(height: 24),
                        FilledButton.icon(
                          onPressed: hasClockIn && hasClockOut ? null : () => _handleAction(context, ref, today),
                          icon: const Icon(Icons.fingerprint),
                          label: Text(actionText),
                        ),
                      ],
                    ),
                  ),
                ),
              ],
            );
          },
          error: (error, _) => ErrorRetry(
            message: error.toString().replaceFirst('Exception: ', ''),
            onRetry: () => ref.invalidate(todayAttendanceProvider),
          ),
          loading: () => const Center(child: CircularProgressIndicator()),
        ),
      );

    if (!useScaffold) {
      return content;
    }

    return AppScaffold(
      currentIndex: 0,
      title: '今日打卡',
      child: content,
    );
  }
}

class _InfoRow extends StatelessWidget {
  const _InfoRow({
    required this.label,
    required this.value,
    this.isLate = false,
    this.isEarlyLeave = false,
  });

  final String label;
  final String value;
  final bool isLate;
  final bool isEarlyLeave;

  @override
  Widget build(BuildContext context) {
    final attendanceColors = AttendanceColors.of(context);
    
    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Expanded(
          flex: 2,
          child: Text(label, style: Theme.of(context).textTheme.bodyMedium),
        ),
        Expanded(
          flex: 3,
          child: Row(
            mainAxisAlignment: MainAxisAlignment.end,
            children: [
              if (isLate) ...[
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                  decoration: BoxDecoration(
                    color: attendanceColors.late,
                    borderRadius: BorderRadius.circular(4),
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
                const SizedBox(width: 8),
              ],
              if (isEarlyLeave) ...[
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                  decoration: BoxDecoration(
                    color: attendanceColors.earlyLeave,
                    borderRadius: BorderRadius.circular(4),
                  ),
                  child: Text(
                    '早退',
                    style: TextStyle(
                      fontSize: 12,
                      fontWeight: FontWeight.w600,
                      color: AppColors.textOnPrimary,
                    ),
                  ),
                ),
                const SizedBox(width: 8),
              ],
              Flexible(
                child: Text(
                  value,
                  textAlign: TextAlign.right,
                  style: Theme.of(context).textTheme.bodyMedium,
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }
}