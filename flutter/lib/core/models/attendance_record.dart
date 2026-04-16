class AttendanceRecord {
  const AttendanceRecord({
    required this.id,
    required this.userId,
    required this.userFullName,
    required this.workDate,
    required this.clockInTime,
    required this.clockOutTime,
    required this.isLate,
    required this.lateMinutes,
    required this.isEarlyLeave,
    required this.shortMinutes,
    required this.workDurationMinutes,
  });

  final int id;
  final int userId;
  final String userFullName;
  final String workDate;
  final String? clockInTime;
  final String? clockOutTime;
  final bool isLate;
  final int lateMinutes;
  final bool isEarlyLeave;
  final int shortMinutes;
  final int? workDurationMinutes;

  factory AttendanceRecord.fromJson(Map<String, dynamic> json) {
    return AttendanceRecord(
      id: json['id'] as int? ?? 0,
      userId: json['userId'] as int? ?? 0,
      userFullName: json['userFullName'] as String? ?? '',
      workDate: json['workDate'] as String? ?? '',
      clockInTime: json['clockInTime'] as String?,
      clockOutTime: json['clockOutTime'] as String?,
      isLate: json['isLate'] as bool? ?? false,
      lateMinutes: json['lateMinutes'] as int? ?? 0,
      isEarlyLeave: json['isEarlyLeave'] as bool? ?? false,
      shortMinutes: json['shortMinutes'] as int? ?? 0,
      workDurationMinutes: json['workDurationMinutes'] as int?,
    );
  }
}