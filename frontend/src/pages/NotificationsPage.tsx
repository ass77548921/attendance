import { useState, useEffect } from 'react';
import { apiClient } from '../lib/apiClient';
import type { NotificationRecipient } from '../types/api';

export function NotificationsPage() {
  const [recipients, setRecipients] = useState<NotificationRecipient[]>([]);
  const [newEmail, setNewEmail] = useState('');
  const [adding, setAdding] = useState(false);
  const [toast, setToast] = useState('');
  const [toastType, setToastType] = useState<'success' | 'error'>('success');

  const showToast = (msg: string, type: 'success' | 'error' = 'success') => {
    setToast(msg);
    setToastType(type);
    setTimeout(() => setToast(''), 4000);
  };

  const load = () => {
    apiClient.get<NotificationRecipient[]>('/api/admin/notification/recipients').then(setRecipients);
  };

  useEffect(() => {
    load();
  }, []);

  const handleAdd = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newEmail.trim()) return;
    setAdding(true);
    try {
      await apiClient.post('/api/admin/notification/recipients', { email: newEmail.trim() });
      setNewEmail('');
      showToast('已新增收件人');
      load();
    } catch (err: unknown) {
      showToast(err instanceof Error ? err.message : '新增失敗', 'error');
    } finally {
      setAdding(false);
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm('確定刪除此收件人？')) return;
    try {
      await apiClient.delete(`/api/admin/notification/recipients/${id}`);
      showToast('已刪除收件人');
      load();
    } catch (err: unknown) {
      showToast(err instanceof Error ? err.message : '刪除失敗', 'error');
    }
  };

  return (
    <div className="max-w-lg">
      <h1 className="text-xl font-semibold mb-4">通知收件人管理</h1>
      {toast && (
        <div className={`mb-4 px-4 py-2 rounded text-sm ${
          toastType === 'error' ? 'bg-red-100 text-red-800' : 'bg-green-100 text-green-800'
        }`}>{toast}</div>
      )}

      {/* Add form */}
      <div className="bg-white rounded shadow p-5 mb-6">
        <h2 className="text-sm font-semibold mb-3">新增收件人</h2>
        <form onSubmit={handleAdd} className="flex gap-3 items-end">
          <div className="flex-1">
            <label className="block text-xs font-medium mb-1">Email</label>
            <input type="email" value={newEmail} onChange={e => setNewEmail(e.target.value)} required
              placeholder="user@example.com"
              className="w-full border border-gray-300 rounded px-3 py-2 text-sm" />
          </div>
          <button type="submit" disabled={adding}
            className="bg-blue-600 text-white px-4 py-2 rounded text-sm hover:bg-blue-700 disabled:opacity-50">
            {adding ? '新增中…' : '新增'}
          </button>
        </form>
      </div>

      {/* Recipients table */}
      <div className="bg-white rounded shadow overflow-hidden">
        {recipients.length === 0 ? (
          <p className="text-center text-sm text-gray-500 py-8">尚無收件人</p>
        ) : (
          <table className="min-w-full text-sm">
            <thead className="bg-gray-50">
              <tr>
                <th className="text-left px-4 py-2 font-medium text-gray-700">Email</th>
                <th className="text-left px-4 py-2 font-medium text-gray-700">狀態</th>
                <th className="text-left px-4 py-2 font-medium text-gray-700">建立時間</th>
                <th className="px-4 py-2"></th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {recipients.map(r => (
                <tr key={r.id}>
                  <td className="px-4 py-3">{r.email}</td>
                  <td className="px-4 py-3">
                    <span className={`px-2 py-0.5 rounded-full text-xs font-medium ${
                      r.active ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-600'
                    }`}>
                      {r.active ? '啟用' : '停用'}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-gray-500">
                    {r.createdAt ? new Date(r.createdAt).toLocaleDateString('zh-TW') : '-'}
                  </td>
                  <td className="px-4 py-3 text-right">
                    <button onClick={() => handleDelete(r.id)}
                      className="text-red-600 hover:text-red-800 text-xs">
                      刪除
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
