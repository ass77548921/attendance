import { useState, useEffect } from 'react';
import { apiClient } from '../lib/apiClient';
import type { AttendanceConfigResponse } from '../types/api';

export function AttendanceConfigPage() {
  const [config, setConfig] = useState<AttendanceConfigResponse | null>(null);
  const [form, setForm] = useState({
    workStartTime: '',
    workEndTime: '',
    lateToleranceMinutes: 0,
    lunchBreakMinutes: 60,
    requiredWorkMinutes: 480,
    timezone: 'Asia/Taipei',
  });
  const [error, setError] = useState('');
  const [toast, setToast] = useState('');
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    apiClient.get<AttendanceConfigResponse>('/api/admin/config').then((res) => {
      setConfig(res);
      setForm({
        workStartTime: res.workStartTime,
        workEndTime: res.workEndTime,
        lateToleranceMinutes: res.lateToleranceMinutes,
        lunchBreakMinutes: res.lunchBreakMinutes,
        requiredWorkMinutes: res.requiredWorkMinutes,
        timezone: res.timezone,
      });
    });
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    if (form.workStartTime >= form.workEndTime) {
      setError('上班時間必須早於下班時間');
      return;
    }
    setSaving(true);
    try {
      await apiClient.put('/api/admin/config', form);
      setToast('設定已更新');
      setTimeout(() => setToast(''), 3000);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : '儲存失敗');
    } finally {
      setSaving(false);
    }
  };

  const set = (key: keyof typeof form) =>
    (e: React.ChangeEvent<HTMLInputElement>) =>
      setForm(f => ({ ...f, [key]: key.endsWith('Minutes') ? Number(e.target.value) : e.target.value }));

  if (!config) return <p className="text-sm text-gray-500">載入中…</p>;

  return (
    <div className="max-w-lg">
      <h1 className="text-xl font-semibold mb-4">出勤規則設定</h1>
      {toast && <div className="mb-4 bg-green-100 text-green-800 px-4 py-2 rounded text-sm">{toast}</div>}
      <div className="bg-white rounded shadow p-6">
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium mb-1">上班時間</label>
              <input type="time" value={form.workStartTime} onChange={set('workStartTime')} required
                className="w-full border border-gray-300 rounded px-3 py-2 text-sm" />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">下班時間</label>
              <input type="time" value={form.workEndTime} onChange={set('workEndTime')} required
                className="w-full border border-gray-300 rounded px-3 py-2 text-sm" />
            </div>
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">遲到寬限（分鐘）</label>
            <input type="number" min={0} max={60} value={form.lateToleranceMinutes}
              onChange={set('lateToleranceMinutes')} required
              className="w-full border border-gray-300 rounded px-3 py-2 text-sm" />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">午休時間（分鐘）</label>
            <input type="number" min={0} max={120} value={form.lunchBreakMinutes}
              onChange={set('lunchBreakMinutes')} required
              className="w-full border border-gray-300 rounded px-3 py-2 text-sm" />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">每日應工作時數（分鐘）</label>
            <input type="number" min={60} max={600} value={form.requiredWorkMinutes}
              onChange={set('requiredWorkMinutes')} required
              className="w-full border border-gray-300 rounded px-3 py-2 text-sm" />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">時區</label>
            <input type="text" value={form.timezone} onChange={set('timezone')} required
              className="w-full border border-gray-300 rounded px-3 py-2 text-sm" />
          </div>
          {error && <p className="text-red-600 text-sm">{error}</p>}
          <button type="submit" disabled={saving}
            className="w-full bg-blue-600 text-white py-2 rounded text-sm hover:bg-blue-700 disabled:opacity-50">
            {saving ? '儲存中…' : '儲存設定'}
          </button>
        </form>
      </div>
    </div>
  );
}
