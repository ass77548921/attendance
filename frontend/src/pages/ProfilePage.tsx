import { useState, useEffect } from 'react';
import { apiClient } from '../lib/apiClient';
import { useAuth } from '../context/AuthContext';
import type { UserResponse } from '../types/api';

export function ProfilePage() {
  const { setUsername } = useAuth();
  const [form, setForm] = useState({ fullName: '', email: '', password: '' });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [successMsg, setSuccessMsg] = useState('');
  const [errorMsg, setErrorMsg] = useState('');

  useEffect(() => {
    apiClient.get<UserResponse>('/api/users/me')
      .then(data => setForm(f => ({ ...f, fullName: data.fullName, email: data.email })))
      .catch((err: unknown) => setErrorMsg(err instanceof Error ? err.message : '無法載入個人資料'))
      .finally(() => setLoading(false));
  }, []);

  const set = (key: keyof typeof form) => (e: React.ChangeEvent<HTMLInputElement>) =>
    setForm(f => ({ ...f, [key]: e.target.value }));

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setSuccessMsg('');
    setErrorMsg('');

    const body: Record<string, string> = {
      fullName: form.fullName,
      email: form.email,
    };
    if (form.password !== '') {
      body.password = form.password;
    }

    try {
      const updated = await apiClient.put<UserResponse>('/api/users/me', body);
      setSuccessMsg('個人資料已更新');
      setForm(f => ({ ...f, password: '' }));
      setUsername(updated.username);
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : '更新失敗';
      if (msg.toLowerCase().includes('email')) {
        setErrorMsg('Email 已被使用，請換一個 Email');
      } else {
        setErrorMsg(msg);
      }
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return <div className="p-6 text-sm text-gray-500">載入中...</div>;
  }

  return (
    <div className="max-w-lg">
      <h1 className="text-xl font-semibold mb-6">個人資料</h1>

      {successMsg && (
        <div className="mb-4 px-4 py-2 bg-green-50 border border-green-200 text-green-700 rounded text-sm">
          {successMsg}
        </div>
      )}
      {errorMsg && (
        <div className="mb-4 px-4 py-2 bg-red-50 border border-red-200 text-red-700 rounded text-sm">
          {errorMsg}
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-4 bg-white rounded shadow p-6">
        <div>
          <label className="block text-sm font-medium mb-1">姓名</label>
          <input
            type="text"
            value={form.fullName}
            onChange={set('fullName')}
            required
            className="w-full border border-gray-300 rounded px-3 py-2 text-sm"
          />
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Email</label>
          <input
            type="email"
            value={form.email}
            onChange={set('email')}
            required
            className="w-full border border-gray-300 rounded px-3 py-2 text-sm"
          />
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">
            新密碼
            <span className="text-gray-400 font-normal ml-1">（留空則不修改）</span>
          </label>
          <input
            type="password"
            value={form.password}
            onChange={set('password')}
            className="w-full border border-gray-300 rounded px-3 py-2 text-sm"
          />
        </div>
        <div className="pt-2">
          <button
            type="submit"
            disabled={saving}
            className="px-4 py-2 bg-blue-600 text-white text-sm rounded hover:bg-blue-700 disabled:opacity-50"
          >
            {saving ? '儲存中...' : '儲存'}
          </button>
        </div>
      </form>
    </div>
  );
}
