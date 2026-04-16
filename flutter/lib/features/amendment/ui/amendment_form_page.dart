import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../core/models/amendment.dart';
import '../../../core/router/app_routes.dart';
import '../data/amendment_repository.dart';
import '../state/amendment_provider.dart';

class AmendmentFormPage extends ConsumerStatefulWidget {
  const AmendmentFormPage({super.key});

  @override
  ConsumerState<AmendmentFormPage> createState() => _AmendmentFormPageState();
}

class _AmendmentFormPageState extends ConsumerState<AmendmentFormPage> {
  DateTime? _targetDate;
  TimeOfDay? _time;
  AmendmentType _type = AmendmentType.clockIn;
  final TextEditingController _reasonController = TextEditingController();
  final List<PlatformFile> _files = [];
  final ValueNotifier<String?> _error = ValueNotifier<String?>(null);

  @override
  void dispose() {
    _reasonController.dispose();
    _error.dispose();
    super.dispose();
  }

  Future<void> _pickFiles() async {
    final result = await FilePicker.platform.pickFiles(
      allowMultiple: true,
      type: FileType.custom,
      allowedExtensions: const ['jpg', 'jpeg', 'png', 'gif', 'pdf'],
      withData: true,
    );
    if (result == null) return;
    if (_files.length + result.files.length > 5) {
      _error.value = '最多上傳 5 個附件';
      return;
    }
    _error.value = null;
    _files.addAll(result.files);
    setState(() {});
  }

  Future<void> _submit() async {
    if (_targetDate == null || _time == null || _reasonController.text.trim().isEmpty) {
      _error.value = '請完整填寫日期、時間與原因';
      return;
    }
    _error.value = null;
    final amendedAt = DateTime(
      _targetDate!.year,
      _targetDate!.month,
      _targetDate!.day,
      _time!.hour,
      _time!.minute,
    );
    try {
      await ref.read(amendmentRepositoryProvider).submit(
            targetDate: _targetDate!.toIso8601String().split('T').first,
            amendmentType: _type,
            amendedTime: amendedAt,
            reason: _reasonController.text.trim(),
            attachments: _files,
          );
      ref.invalidate(amendmentsProvider);
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('申請已送出')));
      context.go(AppRoutes.amendments);
    } catch (error) {
      _error.value = error.toString().replaceFirst('Exception: ', '');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('新增補打卡申請')),
      body: Center(
        child: ConstrainedBox(
          constraints: const BoxConstraints(maxWidth: 600),
          child: ListView(
            padding: const EdgeInsets.all(24),
            children: [
              ListTile(
                contentPadding: EdgeInsets.zero,
                title: const Text('目標日期'),
                subtitle: Text(_targetDate == null ? '請選擇日期' : _targetDate!.toIso8601String().split('T').first),
                trailing: const Icon(Icons.calendar_today_outlined),
                onTap: () async {
                  final picked = await showDatePicker(
                    context: context,
                    firstDate: DateTime(2020),
                    lastDate: DateTime(2100),
                    initialDate: DateTime.now(),
                  );
                  if (picked == null) return;
                  setState(() => _targetDate = picked);
                },
              ),
              const SizedBox(height: 16),
              SegmentedButton<AmendmentType>(
                segments: const [
                  ButtonSegment(value: AmendmentType.clockIn, label: Text('上班')),
                  ButtonSegment(value: AmendmentType.clockOut, label: Text('下班')),
                ],
                selected: {_type},
                onSelectionChanged: (value) => setState(() => _type = value.first),
              ),
              const SizedBox(height: 16),
              ListTile(
                contentPadding: EdgeInsets.zero,
                title: const Text('補登時間'),
                subtitle: Text(_time == null ? '請選擇時間' : _time!.format(context)),
                trailing: const Icon(Icons.schedule),
                onTap: () async {
                  final picked = await showTimePicker(context: context, initialTime: TimeOfDay.now());
                  if (picked == null) return;
                  setState(() => _time = picked);
                },
              ),
              const SizedBox(height: 16),
              TextField(
                controller: _reasonController,
                minLines: 3,
                maxLines: 5,
                decoration: const InputDecoration(labelText: '原因'),
              ),
              const SizedBox(height: 16),
              OutlinedButton.icon(
                onPressed: _pickFiles,
                icon: const Icon(Icons.attach_file),
                label: const Text('選取附件'),
              ),
              for (final file in _files) ListTile(title: Text(file.name), dense: true),
              const SizedBox(height: 16),
              ValueListenableBuilder<String?>(
                valueListenable: _error,
                builder: (context, value, _) {
                  if (value == null) return const SizedBox.shrink();
                  return Text(value, style: TextStyle(color: Theme.of(context).colorScheme.error));
                },
              ),
              const SizedBox(height: 24),
              FilledButton(onPressed: _submit, child: const Text('送出申請')),
            ],
          ),
        ),
      ),
    );
  }
}