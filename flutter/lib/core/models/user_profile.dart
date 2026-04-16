class UserProfile {
  const UserProfile({
    required this.id,
    required this.username,
    required this.fullName,
    required this.email,
    required this.address,
    required this.personalPhone,
    required this.officeExtension,
    required this.role,
    required this.status,
    required this.createdAt,
  });

  final int id;
  final String username;
  final String fullName;
  final String email;
  final String? address;
  final String? personalPhone;
  final String? officeExtension;
  final String role;
  final String status;
  final String createdAt;

  factory UserProfile.fromJson(Map<String, dynamic> json) {
    return UserProfile(
      id: json['id'] as int? ?? 0,
      username: json['username'] as String? ?? '',
      fullName: json['fullName'] as String? ?? '',
      email: json['email'] as String? ?? '',
      address: json['address'] as String?,
      personalPhone: json['personalPhone'] as String?,
      officeExtension: json['officeExtension'] as String?,
      role: json['role'] as String? ?? 'EMPLOYEE',
      status: json['status'] as String? ?? 'ACTIVE',
      createdAt: json['createdAt'] as String? ?? '',
    );
  }
}