enum AmendmentType { clockIn, clockOut }

enum AmendmentStatus { pending, approved, rejected }

class AmendmentAttachment {
  const AmendmentAttachment({
    required this.id,
    required this.originalFilename,
    required this.mimeType,
    required this.fileSize,
  });

  final int id;
  final String originalFilename;
  final String mimeType;
  final int fileSize;

  bool get isImage => mimeType.startsWith('image/');

  factory AmendmentAttachment.fromJson(Map<String, dynamic> json) {
    return AmendmentAttachment(
      id: json['id'] as int? ?? 0,
      originalFilename: json['originalFilename'] as String? ?? '',
      mimeType: json['mimeType'] as String? ?? 'application/octet-stream',
      fileSize: json['fileSize'] as int? ?? 0,
    );
  }
}

class Amendment {
  const Amendment({
    required this.id,
    required this.userId,
    required this.userFullName,
    required this.targetDate,
    required this.amendmentType,
    required this.amendedTime,
    required this.reason,
    required this.status,
    required this.reviewNote,
    required this.reviewedBy,
    required this.reviewedAt,
    required this.createdAt,
    required this.attachments,
  });

  final int id;
  final int userId;
  final String userFullName;
  final String targetDate;
  final AmendmentType amendmentType;
  final String amendedTime;
  final String reason;
  final AmendmentStatus status;
  final String? reviewNote;
  final int? reviewedBy;
  final String? reviewedAt;
  final String createdAt;
  final List<AmendmentAttachment> attachments;

  factory Amendment.fromJson(Map<String, dynamic> json) {
    return Amendment(
      id: json['id'] as int? ?? 0,
      userId: json['userId'] as int? ?? 0,
      userFullName: json['userFullName'] as String? ?? '',
      targetDate: json['targetDate'] as String? ?? '',
      amendmentType: (json['amendmentType'] as String? ?? 'CLOCK_IN') == 'CLOCK_OUT'
          ? AmendmentType.clockOut
          : AmendmentType.clockIn,
      amendedTime: json['amendedTime'] as String? ?? '',
      reason: json['reason'] as String? ?? '',
      status: switch (json['status'] as String? ?? 'PENDING') {
        'APPROVED' => AmendmentStatus.approved,
        'REJECTED' => AmendmentStatus.rejected,
        _ => AmendmentStatus.pending,
      },
      reviewNote: json['reviewNote'] as String?,
      reviewedBy: json['reviewedBy'] as int?,
      reviewedAt: json['reviewedAt'] as String?,
      createdAt: json['createdAt'] as String? ?? '',
      attachments: (json['attachments'] as List<dynamic>? ?? <dynamic>[])
          .map((item) => AmendmentAttachment.fromJson(item as Map<String, dynamic>))
          .toList(),
    );
  }
}