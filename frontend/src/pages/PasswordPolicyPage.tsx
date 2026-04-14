import { useState, useEffect } from 'react';
import { apiClient } from '../lib/apiClient';
import type { PasswordPolicyResponse, UpdatePasswordPolicyRequest } from '../types/api';

export function PasswordPolicyPage() {
  const [policy, setPolicy] = useState<PasswordPolicyResponse | null>(null);
  const [form, setForm] = useState<UpdatePasswordPolicyRequest>({
    minLength: 8,
    requireUppercase: true,
    requireLowercase: true,
    requireNumber: true,
    requireSpecialChar: false,
    expiryDays: 0,
    historyCount: 5,
  });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    apiClient.get<PasswordPolicyResponse>('/api/admin/password-policy')
      .then((data) => {
        setPolicy(data);
        setForm({
          minLength: data.minLength,
          requireUppercase: data.requireUppercase,
          requireLowercase: data.requireLowercase,
          requireNumber: data.requireNumber,
          requireSpecialChar: data.requireSpecialChar,
          expiryDays: data.expiryDays,
          historyCount: data.historyCount,
        });
      })
      .catch(() => setError('載入密碼規則失敗'))
      .finally(() => setLoading(false));
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setMessage('');
    setError('');
    setSaving(true);
    try {
      await apiClient.put('/api/admin/password-policy', form);
      setMessage('密碼規則已儲存');
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : '';
      setError(msg || '儲存失敗，請稍後再試');
    } finally {
      setSaving(false);
    }
  };

  const handleNumberChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: parseInt(value, 10) || 0 }));
  };

  const handleCheckboxChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, checked } = e.target;
    setForm((prev) => ({ ...prev, [name]: checked }));
  };

  if (loading) return <p className="text-gray-500">載入中…</p>;

  return (
    <div className="max-w-lg">
      <h1 className="text-xl font-semibold mb-6">密碼規則設定</h1>

      {policy && (
        <form onSubmit={handleSubmit} className="bg-white rounded shadow p-6 space-y-5">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              最短密碼長度
            </label>
            <input
              type="number"
              name="minLength"
              min={4}
              max={128}
              value={form.minLength}
              onChange={handleNumberChange}
              className="w-32 border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <fieldset className="space-y-2">
            <legend className="text-sm font-medium text-gray-700 mb-1">密碼複雜度要求</legend>
            {([
              { name: 'requireUppercase', label: '必須包含大寫字母' },
              { name: 'requireLowercase', label: '必須包含小寫字母' },
              { name: 'requireNumber', label: '必須包含數字' },
              { name: 'requireSpecialChar', label: '必須包含特殊字元' },
            ] as const).map(({ name, label }) => (
              <label key={name} className="flex items-center gap-2 text-sm text-gray-700">
                <input
                  type="checkbox"
                  name={name}
                  checked={form[name]}
                  onChange={handleCheckboxChange}
                  className="rounded"
                />
                {label}
              </label>
            ))}
          </fieldset>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              密碼有效天數（0 表示永久有效）
            </label>
            <input
              type="number"
              name="expiryDays"
              min={0}
              value={form.expiryDays}
              onChange={handleNumberChange}
              className="w-32 border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              禁止重複使用最近幾次密碼（0 表示不限制）
            </label>
            <input
              type="number"
              name="historyCount"
              min={0}
              max={24}
              value={form.historyCount}
              onChange={handleNumberChange}
              className="w-32 border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          {message && <p className="text-green-600 text-sm">{message}</p>}
          {error && <p className="text-red-600 text-sm">{error}</p>}

          <button
            type="submit"
            disabled={saving}
            className="bg-blue-600 text-white px-4 py-2 rounded text-sm hover:bg-blue-700 disabled:opacity-50"
          >
            {saving ? '儲存中…' : '儲存設定'}
          </button>
        </form>
      )}

      {error && !policy && <p className="text-red-600 text-sm">{error}</p>}
    </div>
  );
}
