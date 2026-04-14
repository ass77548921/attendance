import { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { apiClient } from '../lib/apiClient';
import { useAuth } from '../context/AuthContext';
import type { UserResponse, UserRole, UserStatus, UpdateUserRequest, ResetPasswordRequest, Page } from '../types/api';

const STATUS_LABELS: Record<string, string> = { ACTIVE: '啟用', INACTIVE: '停用' };
const ROLE_LABELS: Record<string, string> = { SUPER_ADMIN: '總管理', ADMIN: '管理員', EMPLOYEE: '員工' };

function emptyToNull(value: string): string | null {
  const trimmed = value.trim();
  return trimmed === '' ? null : trimmed;
}

function CreateUserModal({
  onClose,
  onSuccess,
  title,
  defaultRole,
  allowRoleSelection,
}: {
  onClose: () => void;
  onSuccess: () => void;
  title: string;
  defaultRole: UserRole;
  allowRoleSelection: boolean;
}) {
  const [form, setForm] = useState({
    username: '',
    password: '',
    fullName: '',
    email: '',
    address: '',
    personalPhone: '',
    officeExtension: '',
    role: defaultRole,
  });
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);

  const set = (key: keyof typeof form) => (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) =>
    setForm(f => ({ ...f, [key]: e.target.value }));

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setError('');
    try {
      await apiClient.post('/api/admin/users', {
        username: form.username,
        password: form.password,
        fullName: form.fullName,
        email: form.email,
        role: form.role,
        address: emptyToNull(form.address),
        personalPhone: emptyToNull(form.personalPhone),
        officeExtension: emptyToNull(form.officeExtension),
      });
      onSuccess();
      onClose();
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : '建立失敗');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div className="bg-white rounded shadow-lg w-full max-w-md p-6">
        <h2 className="text-lg font-semibold mb-4">{title}</h2>
        <form onSubmit={handleSubmit} className="space-y-3">
          {([
            { key: 'username', label: '帳號', type: 'text', minLength: 3 },
            { key: 'password', label: '密碼（至少 8 字元）', type: 'password', minLength: 8 },
            { key: 'fullName', label: '姓名', type: 'text', minLength: undefined },
            { key: 'email', label: 'Email', type: 'email', minLength: undefined },
            { key: 'address', label: '地址（選填）', type: 'text', minLength: undefined },
            { key: 'personalPhone', label: '個人聯絡電話（選填）', type: 'text', minLength: undefined },
            { key: 'officeExtension', label: '公司分機電話（選填）', type: 'text', minLength: undefined },
          ] as { key: keyof typeof form; label: string; type: string; minLength?: number }[]).map(({ key, label, type, minLength }) => (
            <div key={key}>
              <label className="block text-sm font-medium mb-1">{label}</label>
              <input type={type} value={form[key]} onChange={set(key)} required={key !== 'address' && key !== 'personalPhone' && key !== 'officeExtension'}
                minLength={minLength}
                className="w-full border border-gray-300 rounded px-3 py-2 text-sm" />
            </div>
          ))}
          {allowRoleSelection && (
            <div>
              <label className="block text-sm font-medium mb-1">角色</label>
              <select value={form.role} onChange={set('role')}
                className="w-full border border-gray-300 rounded px-3 py-2 text-sm">
                <option value="EMPLOYEE">員工</option>
                <option value="ADMIN">管理員</option>
              </select>
            </div>
          )}
          {error && <p className="text-red-600 text-sm">{error}</p>}
          <div className="flex justify-end gap-2 pt-2">
            <button type="button" onClick={onClose}
              className="px-4 py-2 text-sm border border-gray-300 rounded hover:bg-gray-50">取消</button>
            <button type="submit" disabled={saving}
              className="px-4 py-2 text-sm bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50">
              {saving ? '建立中…' : '建立'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

function ViewUserModal({ user, onClose }: { user: UserResponse; onClose: () => void }) {
  const navigate = useNavigate();
  const display = (value: string | null) => (value && value.trim() ? value : '—');

  const handleViewAttendance = () => {
    onClose();
    navigate(`/admin/attendance?userId=${user.id}`);
  };

  return (
    <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-50"
         onClick={onClose}>
      <div className="bg-white rounded-2xl shadow-xl w-full max-w-md p-6"
           onClick={e => e.stopPropagation()}>
        <h2 className="text-lg font-semibold mb-5">員工帳號詳情</h2>
        <dl className="space-y-3 text-sm">
          {([
            { label: 'ID', value: String(user.id) },
            { label: '帳號', value: user.username },
            { label: '姓名', value: user.fullName },
            { label: 'Email', value: user.email },
            { label: '角色', value: ROLE_LABELS[user.role] ?? user.role },
            { label: '狀態', value: STATUS_LABELS[user.status] ?? user.status },
            { label: '地址', value: display(user.address) },
            { label: '個人電話', value: display(user.personalPhone) },
            { label: '公司分機', value: display(user.officeExtension) },
            { label: '建立時間', value: new Date(user.createdAt).toLocaleString('zh-TW') },
          ] as { label: string; value: string }[]).map(({ label, value }) => (
            <div key={label} className="flex gap-3">
              <dt className="w-20 shrink-0 text-gray-500">{label}</dt>
              <dd className="font-medium">{value}</dd>
            </div>
          ))}
        </dl>
        <div className="flex justify-between items-center pt-5">
          <button onClick={handleViewAttendance}
            className="px-4 py-2 text-sm text-blue-600 border border-blue-300 rounded-lg hover:bg-blue-50">查看出勤紀錄</button>
          <button onClick={onClose}
            className="px-5 py-2 text-sm bg-gray-100 rounded-lg hover:bg-gray-200">關閉</button>
        </div>
      </div>
    </div>
  );
}

function EditUserModal({
  user,
  onClose,
  onSuccess,
}: {
  user: UserResponse;
  onClose: () => void;
  onSuccess: () => void;
}) {
  const [form, setForm] = useState<{
    username: string;
    fullName: string;
    email: string;
    address: string;
    personalPhone: string;
    officeExtension: string;
    password: string;
    status: UserStatus;
  }>({
    username: user.username,
    fullName: user.fullName,
    email: user.email,
    address: user.address ?? '',
    personalPhone: user.personalPhone ?? '',
    officeExtension: user.officeExtension ?? '',
    password: '',
    status: user.status,
  });
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);

  const set =
    <K extends keyof typeof form>(key: K) =>
    (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) =>
      setForm(f => ({ ...f, [key]: e.target.value as (typeof form)[K] }));

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setError('');
    try {
      const payload: UpdateUserRequest = {
        username: form.username,
        fullName: form.fullName,
        email: form.email,
        address: emptyToNull(form.address),
        personalPhone: emptyToNull(form.personalPhone),
        officeExtension: emptyToNull(form.officeExtension),
        status: form.status,
      };
      if (form.password.trim() !== '') {
        payload.password = form.password;
      }
      await apiClient.put(`/api/admin/users/${user.id}`, payload);
      onSuccess();
      onClose();
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : '儲存失敗');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-50"
         onClick={onClose}>
      <div className="bg-white rounded-2xl shadow-xl w-full max-w-md p-6"
           onClick={e => e.stopPropagation()}>
        <h2 className="text-lg font-semibold mb-5">編輯員工帳號</h2>
        <form onSubmit={handleSubmit} className="space-y-3">
          <div>
            <label className="block text-sm font-medium mb-1">帳號</label>
            <input type="text" value={form.username} onChange={set('username')} required minLength={3}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">姓名</label>
            <input type="text" value={form.fullName} onChange={set('fullName')} required
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Email</label>
            <input type="email" value={form.email} onChange={set('email')} required
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">地址（選填）</label>
            <input type="text" value={form.address} onChange={set('address')}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">個人聯絡電話（選填）</label>
            <input type="text" value={form.personalPhone} onChange={set('personalPhone')}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">公司分機電話（選填）</label>
            <input type="text" value={form.officeExtension} onChange={set('officeExtension')}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">密碼（選填，留空不變更）</label>
            <input type="password" value={form.password} onChange={set('password')}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">狀態</label>
            <select value={form.status} onChange={set('status')}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm">
              <option value="ACTIVE">啟用</option>
              <option value="INACTIVE">停用</option>
            </select>
          </div>
          {error && <p className="text-red-600 text-sm">{error}</p>}
          <div className="flex justify-end gap-2 pt-2">
            <button type="button" onClick={onClose}
              className="px-4 py-2 text-sm border border-gray-300 rounded-lg hover:bg-gray-50">取消</button>
            <button type="submit" disabled={saving}
              className="px-4 py-2 text-sm bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50">
              {saving ? '儲存中…' : '儲存'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

function ResetPasswordModal({
  user,
  onClose,
  onSuccess,
}: {
  user: UserResponse;
  onClose: () => void;
  onSuccess: () => void;
}) {
  const [form, setForm] = useState<ResetPasswordRequest>({ newPassword: '', reason: '' });
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setError('');
    try {
      await apiClient.post(`/api/admin/users/${user.id}/reset-password`, form);
      onSuccess();
      onClose();
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : '重設失敗');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-50"
         onClick={onClose}>
      <div className="bg-white rounded-2xl shadow-xl w-full max-w-md p-6"
           onClick={e => e.stopPropagation()}>
        <h2 className="text-lg font-semibold mb-1">重設密碼</h2>
        <p className="text-sm text-gray-500 mb-5">為 <strong>{user.fullName}</strong>（{user.username}）重設密碼，該帳號下次登入將被要求變更密碼。</p>
        <form onSubmit={handleSubmit} className="space-y-3">
          <div>
            <label className="block text-sm font-medium mb-1">新密碼</label>
            <input type="password" value={form.newPassword}
              onChange={e => setForm(f => ({ ...f, newPassword: e.target.value }))}
              required
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">重設原因 / 備注</label>
            <input type="text" value={form.reason}
              onChange={e => setForm(f => ({ ...f, reason: e.target.value }))}
              required placeholder="請說明重設原因"
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" />
          </div>
          {error && <p className="text-red-600 text-sm">{error}</p>}
          <div className="flex justify-end gap-2 pt-2">
            <button type="button" onClick={onClose}
              className="px-4 py-2 text-sm border border-gray-300 rounded-lg hover:bg-gray-50">取消</button>
            <button type="submit" disabled={saving}
              className="px-4 py-2 text-sm bg-orange-600 text-white rounded-lg hover:bg-orange-700 disabled:opacity-50">
              {saving ? '重設中…' : '確認重設'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export function UsersPage() {
  const { isSuperAdmin } = useAuth();
  const [search, setSearch] = useState('');
  const [searchId, setSearchId] = useState('');
  const [searchRole, setSearchRole] = useState<UserRole | ''>('');
  const [searchStatus, setSearchStatus] = useState<UserStatus | ''>('');
  const [users, setUsers] = useState<UserResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [toast, setToast] = useState('');
  const [createMode, setCreateMode] = useState<'EMPLOYEE' | 'ADMIN' | null>(null);
  const [viewUser, setViewUser] = useState<UserResponse | null>(null);
  const [editUser, setEditUser] = useState<UserResponse | null>(null);
  const [resetPasswordUser, setResetPasswordUser] = useState<UserResponse | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const params = new URLSearchParams();
      if (search.trim()) params.set('search', search.trim());
      if (searchId.trim()) params.set('id', searchId.trim());
      if (isSuperAdmin) {
        if (searchRole) params.set('role', searchRole);
      } else {
        params.set('role', 'EMPLOYEE');
      }
      if (searchStatus) params.set('status', searchStatus);
      const res = await apiClient.get<Page<UserResponse>>(`/api/admin/users?${params}`);
      setUsers(res.content);
    } finally {
      setLoading(false);
    }
  }, [isSuperAdmin, search, searchId, searchRole, searchStatus]);

  useEffect(() => { load(); }, [load]);

  const showToast = (msg: string) => {
    setToast(msg);
    setTimeout(() => setToast(''), 3000);
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-xl font-semibold">員工帳號管理</h1>
        <div className="flex gap-2">
          <button onClick={() => setCreateMode('EMPLOYEE')}
            className="bg-blue-600 text-white px-4 py-2 rounded text-sm hover:bg-blue-700">
            新增員工
          </button>
          {isSuperAdmin && (
            <button onClick={() => setCreateMode('ADMIN')}
              className="bg-indigo-600 text-white px-4 py-2 rounded text-sm hover:bg-indigo-700">
              新增管理員
            </button>
          )}
        </div>
      </div>

      {/* Search */}
      <div className="bg-white p-4 rounded shadow mb-4 flex gap-3 items-end flex-wrap">
        <div>
          <label className="block text-xs font-medium text-gray-600 mb-1">ID</label>
          <input value={searchId} onChange={e => setSearchId(e.target.value)} placeholder="輸入使用者 ID"
            className="border border-gray-300 rounded px-2 py-1.5 text-sm w-44" />
        </div>
        <div>
          <label className="block text-xs font-medium text-gray-600 mb-1">搜尋（姓名 / Email）</label>
          <input value={search} onChange={e => setSearch(e.target.value)} placeholder="輸入關鍵字"
            className="border border-gray-300 rounded px-2 py-1.5 text-sm w-64" />
        </div>
        {isSuperAdmin && (
          <div>
            <label className="block text-xs font-medium text-gray-600 mb-1">角色</label>
            <select value={searchRole} onChange={e => setSearchRole(e.target.value as UserRole | '')}
              className="border border-gray-300 rounded px-2 py-1.5 text-sm w-40">
              <option value="">全部</option>
              <option value="EMPLOYEE">員工</option>
              <option value="ADMIN">管理員</option>
            </select>
          </div>
        )}
        <div>
          <label className="block text-xs font-medium text-gray-600 mb-1">狀態</label>
          <select value={searchStatus} onChange={e => setSearchStatus(e.target.value as UserStatus | '')}
            className="border border-gray-300 rounded px-2 py-1.5 text-sm w-40">
            <option value="">全部</option>
            <option value="ACTIVE">啟用</option>
            <option value="INACTIVE">停用</option>
          </select>
        </div>
        <button onClick={load}
          className="bg-blue-600 text-white px-4 py-1.5 rounded text-sm hover:bg-blue-700">
          搜尋
        </button>
      </div>

      {toast && (
        <div className="mb-3 bg-green-100 text-green-800 px-4 py-2 rounded text-sm">{toast}</div>
      )}

      <div className="bg-white rounded shadow overflow-x-auto">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 border-b">
            <tr>
              <th className="px-4 py-3 text-left">ID</th>
              <th className="px-4 py-3 text-left">帳號</th>
              <th className="px-4 py-3 text-left">姓名</th>
              <th className="px-4 py-3 text-left">Email</th>
              <th className="px-4 py-3 text-left">角色</th>
              <th className="px-4 py-3 text-left">狀態</th>
              <th className="px-4 py-3 text-left">操作</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan={7} className="px-4 py-8 text-center text-gray-400">載入中…</td></tr>
            ) : users.length === 0 ? (
              <tr><td colSpan={7} className="px-4 py-8 text-center text-gray-400">無資料</td></tr>
            ) : (
              users.map((u) => (
                <tr key={u.id} className="border-b hover:bg-gray-50">
                  <td className="px-4 py-3 text-gray-500">{u.id}</td>
                  <td className="px-4 py-3">{u.username}</td>
                  <td className="px-4 py-3">{u.fullName}</td>
                  <td className="px-4 py-3">{u.email}</td>
                  <td className="px-4 py-3">{ROLE_LABELS[u.role] ?? u.role}</td>
                  <td className="px-4 py-3">
                    <span className={`inline-block px-2 py-0.5 rounded text-xs ${
                      u.status === 'ACTIVE' ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-600'
                    }`}>
                      {STATUS_LABELS[u.status] ?? u.status}
                    </span>
                  </td>
                  <td className="px-4 py-3">
                    <div className="flex items-center gap-3">
                      <button onClick={() => setViewUser(u)}
                        className="text-xs text-gray-600 hover:underline">查看</button>
                      <button onClick={() => setEditUser(u)}
                        className="text-xs text-blue-600 hover:underline">編輯</button>
                      <button onClick={() => setResetPasswordUser(u)}
                        className="text-xs text-orange-600 hover:underline">重設密碼</button>
                    </div>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {createMode && (
        <CreateUserModal
          title={createMode === 'ADMIN' ? '新增管理員帳號' : '新增員工帳號'}
          defaultRole={createMode}
          allowRoleSelection={isSuperAdmin}
          onClose={() => setCreateMode(null)}
          onSuccess={() => { load(); showToast('帳號已建立'); }}
        />
      )}

      {viewUser && (
        <ViewUserModal user={viewUser} onClose={() => setViewUser(null)} />
      )}

      {editUser && (
        <EditUserModal
          user={editUser}
          onClose={() => setEditUser(null)}
          onSuccess={() => { load(); showToast('帳號已更新'); }}
        />
      )}

      {resetPasswordUser && (
        <ResetPasswordModal
          user={resetPasswordUser}
          onClose={() => setResetPasswordUser(null)}
          onSuccess={() => { load(); showToast('密碼已重設'); }}
        />
      )}
    </div>
  );
}
