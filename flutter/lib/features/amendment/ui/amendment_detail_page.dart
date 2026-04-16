import 'package:flutter/material.dart';

import '../../../core/models/amendment.dart';
import '../../../core/theme/app_colors.dart';

class AmendmentDetailPage extends StatelessWidget {
  const AmendmentDetailPage({super.key, required this.amendment});

  final Amendment amendment;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('補打卡申請詳情')),
      body: ListView(
        padding: const EdgeInsets.all(24),
        children: [
          ListTile(title: const Text('日期'), subtitle: Text(amendment.targetDate), contentPadding: EdgeInsets.zero),
          ListTile(
            title: const Text('類型'),
            subtitle: Text(amendment.amendmentType == AmendmentType.clockIn ? '上班' : '下班'),
            contentPadding: EdgeInsets.zero,
          ),
          ListTile(title: const Text('補登時間'), subtitle: Text(amendment.amendedTime), contentPadding: EdgeInsets.zero),
          ListTile(title: const Text('原因'), subtitle: Text(amendment.reason), contentPadding: EdgeInsets.zero),
          if (amendment.reviewNote != null)
            ListTile(title: const Text('審核備註'), subtitle: Text(amendment.reviewNote!), contentPadding: EdgeInsets.zero),
          const SizedBox(height: 16),
          Text('附件', style: Theme.of(context).textTheme.titleMedium),
          const SizedBox(height: 16),
          if (amendment.attachments.isEmpty)
            const Text('沒有附件')
          else
            Wrap(
              spacing: 16,
              runSpacing: 16,
              children: amendment.attachments
                  .map(
                    (attachment) => SizedBox(
                      width: 180,
                      child: Card(
                        child: Padding(
                          padding: const EdgeInsets.all(16),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Container(
                                height: 88,
                                decoration: BoxDecoration(
                                  color: attachment.isImage
                                      ? AppColors.info.withValues(alpha: 0.12)
                                      : AppColors.surfaceVariant,
                                  borderRadius: BorderRadius.circular(12),
                                ),
                                alignment: Alignment.center,
                                child: Icon(
                                  attachment.isImage ? Icons.image_outlined : Icons.picture_as_pdf_outlined,
                                  size: 36,
                                  color: AppColors.textSecondary,
                                ),
                              ),
                              const SizedBox(height: 16),
                              Text(
                                attachment.originalFilename,
                                maxLines: 2,
                                overflow: TextOverflow.ellipsis,
                              ),
                              const SizedBox(height: 8),
                              Text(
                                _formatSize(attachment.fileSize),
                                style: Theme.of(context).textTheme.bodySmall,
                              ),
                            ],
                          ),
                        ),
                      ),
                    ),
                  )
                  .toList(),
            ),
        ],
      ),
    );
  }

  String _formatSize(int bytes) {
    if (bytes < 1024) return '$bytes B';
    if (bytes < 1024 * 1024) return '${(bytes / 1024).toStringAsFixed(1)} KB';
    return '${(bytes / (1024 * 1024)).toStringAsFixed(1)} MB';
  }
}