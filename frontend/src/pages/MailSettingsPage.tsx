import { useState, useEffect } from 'react';
import { apiClient } from '../lib/apiClient';
import type { MailSettingsResponse } from '../types/api';

export function MailSettingsPage() {
  const [settings, setSettings] = useState<MailSettingsResponse | null>(null);
  const [form, setForm] = useState({
    smtpHost: '',
    smtpPort: 587,
    smtpUsername: '',
    smtpPassword: '',
    fromEmail: '',
    fromName: '',
    subjectPrefix: '',
    enabled: true,
  });
  const [testEmail, setTestEmail] = useState('');
  const [toast, setToast] = useState('');
  const [toastType, setToastType] = useState<'success' | 'error'>('success');
  const [saving, setSaving] = useState(false);
  const [testing, setTesting] = useState(false);

  useEffect(() => {
    apiClient.get<MailSettingsResponse>('/api/admin/mail-settings').then((res) => {
      setSettings(res);
      setForm({
        smtpHost: res.smtpHost,
        smtpPort: res.smtpPort,
        smtpUsername: res.smtpUsername,
        smtpPassword: '',
        fromEmail: res.fromEmail,
        fromName: res.fromName,
        subjectPrefix: res.subjectPrefix,
        enabled: res.enabled,
      });
    });
  }, []);

  const showToast = (msg: string, type: 'success' | 'error' = 'success') => {
    setToast(msg);
    setToastType(type);
    setTimeout(() => setToast(''), 5000);
  };

  const set = (key: keyof typeof form) =>
    (e: React.ChangeEvent<HTMLInputElement>) => {
      const value = key === 'smtpPort' ? Number(e.target.value) :
        key === 'enabled' ? e.target.checked : e.target.value;
      setForm(f => ({ ...f, [key]: value }));
    };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    try {
      const payload = { ...form };
      if (!payload.smtpPassword) delete (payload as Partial<typeof payload>).smtpPassword;
      await apiClient.put('/api/admin/mail-settings', payload);
      showToast('設定已更新');
    } catch (err: unknown) {
      showToast(err instanceof Error ? err.message : '儲存失敗', 'error');
    } finally {
      setSaving(false);
    }
  };

  const handleTest = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!testEmail.trim()) return;
    setTesting(true);
    try {
      await apiClient.post('/api/admin/mail-settings/test', { to: testEmail });
      showToast('測試信已寄出');
    } catch (err: unknown) {
      showToast(err instanceof Error ? err.message : '寄送失敗', 'error');
    } finally {
      setTesting(false);
    }
  };

  if (!settings) return <p className="text-sm text-gray-500">載入中…</p>;

  return (
    <div className="max-w-lg">
      <h1 className="text-xl font-semibold mb-4">郵件 SMTP 設定</h1>
      {toast && (
        <div className={`mb-4 px-4 py-2 rounded text-sm ${
          toastType === 'error' ? 'bg-red-100 text-red-800' : 'bg-green-100 text-green-800'
        }`}>{toast}</div>
      )}

      <div className="bg-white rounded shadow p-6 mb-6">
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-3 gap-3">
            <div className="col-span-2">
              <label className="block text-sm font-medium mb-1">SMTP 主機</label>
              <input type="text" value={form.smtpHost} onChange={set('smtpHost')} required
                className="w-full border border-gray-300 rounded px-3 py-2 text-sm" />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Port</label>
              <input type="number" min={1} max={65535} value={form.smtpPort} onChange={set('smtpPort')} required
                className="w-full border border-gray-300 rounded px-3 py-2 text-sm" />
            </div>
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">SMTP 帳號</label>
            <input type="text" value={form.smtpUsername} onChange={set('smtpUsername')} required
              className="w-full border border-gray-300 rounded px-3 py-2 text-sm" />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">
              SMTP 密碼
              <span className="ml-2 text-xs text-gray-500 font-normal">
                {settings.hasPassword ? '（已設定，留空保持不變）' : '（尚未設定）'}
              </span>
            </label>
            <input type="password" value={form.smtpPassword} onChange={set('smtpPassword')}
              placeholder={settings.hasPassword ? '留空保持現有密碼' : ''}
              className="w-full border border-gray-300 rounded px-3 py-2 text-sm" />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">寄件人 Email</label>
            <input type="email" value={form.fromEmail} onChange={set('fromEmail')} required
              className="w-full border border-gray-300 rounded px-3 py-2 text-sm" />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">寄件人名稱</label>
            <input type="text" value={form.fromName} onChange={set('fromName')} required
              className="w-full border border-gray-300 rounded px-3 py-2 text-sm" />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">信件主旨前綴</label>
            <input type="text" value={form.subjectPrefix} onChange={set('subjectPrefix')} required
              className="w-full border border-gray-300 rounded px-3 py-2 text-sm" />
          </div>
          <div className="flex items-center gap-2">
            <input type="checkbox" id="enabled" checked={form.enabled} onChange={set('enabled')}
              className="rounded border-gray-300" />
            <label htmlFor="enabled" className="text-sm font-medium">啟用郵件發送</label>
          </div>
          <button type="submit" disabled={saving}
            className="w-full bg-blue-600 text-white py-2 rounded text-sm hover:bg-blue-700 disabled:opacity-50">
            {saving ? '儲存中…' : '儲存設定'}
          </button>
        </form>
      </div>

      {/* Test send */}
      <div className="bg-white rounded shadow p-6">
        <h2 className="text-base font-semibold mb-3">寄送測試信</h2>
        <form onSubmit={handleTest} className="flex gap-3 items-end">
          <div className="flex-1">
            <label className="block text-sm font-medium mb-1">測試收件 Email</label>
            <input type="email" value={testEmail} onChange={e => setTestEmail(e.target.value)} required
              placeholder="test@example.com"
              className="w-full border border-gray-300 rounded px-3 py-2 text-sm" />
          </div>
          <button type="submit" disabled={testing}
            className="bg-gray-700 text-white px-4 py-2 rounded text-sm hover:bg-gray-800 disabled:opacity-50">
            {testing ? '寄送中…' : '寄送測試'}
          </button>
        </form>
      </div>
    </div>
  );
}
