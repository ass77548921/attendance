// ── Auth ──────────────────────────────────────────────────
export interface TokenResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  mustChangePassword: boolean;
  role: UserRole;
}

// ── Attendance ───────────────────────────────────────────
export interface AttendanceAdjustmentSummaryResponse {
  auditId: number;
  modifiedByUserId: number;
  modifiedByUsername: string;
  reason: string;
  modifiedAt: string;
}

export interface AttendanceRecordResponse {
  id: number;
  userId: number;
  userFullName: string;
  workDate: string;
  clockInTime: string | null;
  clockOutTime: string | null;
  isLate: boolean;
  lateMinutes: number;
  isEarlyLeave: boolean;
  shortMinutes: number;
  workDurationMinutes: number | null;
  latestAdjustment: AttendanceAdjustmentSummaryResponse | null;
}

export interface AttendanceAdjustmentAuditResponse {
  auditId: number;
  attendanceRecordId: number;
  modifiedByUserId: number;
  modifiedByUsername: string;
  reason: string;
  beforeClockInTime: string | null;
  beforeClockOutTime: string | null;
  beforeIsLate: boolean;
  beforeLateMinutes: number;
  beforeIsEarlyLeave: boolean;
  beforeShortMinutes: number;
  afterClockInTime: string | null;
  afterClockOutTime: string | null;
  afterIsLate: boolean;
  afterLateMinutes: number;
  afterIsEarlyLeave: boolean;
  afterShortMinutes: number;
  modifiedAt: string;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

// ── Amendment ────────────────────────────────────────────
export type AmendmentType = 'CLOCK_IN' | 'CLOCK_OUT';
export type AmendmentStatus = 'PENDING' | 'APPROVED' | 'REJECTED';

export interface AmendmentResponse {
  id: number;
  userId: number;
  userFullName: string;
  targetDate: string;
  amendmentType: AmendmentType;
  amendedTime: string;
  reason: string;
  status: AmendmentStatus;
  reviewNote: string | null;
  reviewedBy: number | null;
  reviewedAt: string | null;
  createdAt: string;
}

// ── User ─────────────────────────────────────────────────
export type UserRole = 'SUPER_ADMIN' | 'ADMIN' | 'EMPLOYEE';
export type UserStatus = 'ACTIVE' | 'INACTIVE';

export interface UserResponse {
  id: number;
  username: string;
  fullName: string;
  email: string;
  address: string | null;
  personalPhone: string | null;
  officeExtension: string | null;
  role: UserRole;
  status: UserStatus;
  createdAt: string;
}

export interface UpdateUserRequest {
  username: string;
  fullName: string;
  email: string;
  address: string | null;
  personalPhone: string | null;
  officeExtension: string | null;
  status: UserStatus;
  password?: string;
}

export interface UpdateMyProfileRequest {
  fullName?: string;
  email?: string;
  address?: string | null;
  personalPhone?: string | null;
  officeExtension?: string | null;
  password?: string;
}

export interface ResetPasswordRequest {
  newPassword: string;
  reason: string;
}

// ── Password Policy ───────────────────────────────────────
export interface PasswordPolicyResponse {
  id: number;
  minLength: number;
  requireUppercase: boolean;
  requireLowercase: boolean;
  requireNumber: boolean;
  requireSpecialChar: boolean;
  expiryDays: number;
  historyCount: number;
}

export interface UpdatePasswordPolicyRequest {
  minLength: number;
  requireUppercase: boolean;
  requireLowercase: boolean;
  requireNumber: boolean;
  requireSpecialChar: boolean;
  expiryDays: number;
  historyCount: number;
}

// ── Attendance Config ─────────────────────────────────────
export interface AttendanceConfigResponse {
  id: number;
  workStartTime: string;
  workEndTime: string;
  lateToleranceMinutes: number;
  lunchBreakMinutes: number;
  requiredWorkMinutes: number;
  timezone: string;
}

// ── Mail Settings ─────────────────────────────────────────
export interface MailSettingsResponse {
  id: number;
  smtpHost: string;
  smtpPort: number;
  smtpUsername: string;
  hasPassword: boolean;
  fromEmail: string;
  fromName: string;
  subjectPrefix: string;
  enabled: boolean;
  updatedAt: string;
}

// ── Notification Recipient ────────────────────────────────
export interface NotificationRecipient {
  id: number;
  email: string;
  active: boolean;
  createdAt: string;
}
