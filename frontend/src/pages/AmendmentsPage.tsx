import { useState, useEffect, useCallback } from 'react';
import { apiClient } from '../lib/apiClient';
import type { AmendmentResponse, AmendmentStatus, Page } from '../types/api';

function fmtDateTime(iso: string | null): string {
  if (!iso) return '-';
  return new Date(iso).toLocaleString('zh-TW', { hour12: false });
}

const STATUS_LABELS: Record<string, string> = {
  PENDING: '待審核',
  APPROVED: '已核准',
  REJECTED: '已駁回',
};

const TYPE_LABELS: Record<string, string> = {
  CLOCK_IN: '上班打卡',
  CLOCK_OUT: '下班打卡',
};

export function AmendmentsPage() {
  const [statusFilter, setStatusFilter] = useState<string>('PENDING');
  const [items, setItems] = useState<AmendmentResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [toast, setToast] = useState('');

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const params = new URLSearchParams();
      if (statusFilter !== 'ALL') params.set('status', statusFilter);
      const res = await apiClient.get<Page<AmendmentResponse>>(`/api/admin/amendments?${params}`);
      setItems(res.content);
    } finally {
      setLoading(false);
    }
  }, [statusFilter]);

  useEffect(() => { load(); }, [load]);

  const showToast = (msg: string) => {
    setToast(msg);
    setTimeout(() => setToast(''), 3000);
  };

  const review = async (id: number, status: AmendmentStatus) => {
    try {
      await apiClient.post(`/api/admin/amendments/${id}/review`, { status });
      showToast(status === 'APPROVED' ? '已核准' : '已駁回');
      load();
    } catch (err: unknown) {
      showToast(err instanceof Error ? err.message : '操作失敗');
    }
  };

  return (
    <div>
      <h1 className="text-xl font-semibold mb-4">補打卡申請審核</h1>

      {/* Filter */}
      <div className="bg-white p-4 rounded shadow mb-4 flex items-center gap-3">
        <label className="text-sm font-medium text-gray-700">狀態篩選</label>
        <select value={statusFilter} onChange={e => setStatusFilter(e.target.value)}
          className="border border-gray-300 rounded px-2 py-1.5 text-sm">
          <option value="PENDING">待審核</option>
          <option value="APPROVED">已核准</option>
          <option value="REJECTED">已駁回</option>
          <option value="ALL">全部</option>
        </select>
      </div>

      {toast && (
        <div className="mb-3 bg-green-100 text-green-800 px-4 py-2 rounded text-sm">{toast}</div>
      )}

      <div className="bg-white rounded shadow overflow-x-auto">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 border-b">
            <tr>
              <th className="px-4 py-3 text-left">申請人</th>
              <th className="px-4 py-3 text-left">目標日期</th>
              <th className="px-4 py-3 text-left">類型</th>
              <th className="px-4 py-3 text-left">補登時間</th>
              <th className="px-4 py-3 text-left">原因</th>
              <th className="px-4 py-3 text-left">狀態</th>
              <th className="px-4 py-3 text-left">操作</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan={7} className="px-4 py-8 text-center text-gray-400">載入中…</td></tr>
            ) : items.length === 0 ? (
              <tr><td colSpan={7} className="px-4 py-8 text-center text-gray-400">無資料</td></tr>
            ) : (
              items.map((item) => (
                <tr key={item.id} className="border-b hover:bg-gray-50">
                  <td className="px-4 py-3">{item.userFullName}</td>
                  <td className="px-4 py-3">{item.targetDate}</td>
                  <td className="px-4 py-3">{TYPE_LABELS[item.amendmentType] ?? item.amendmentType}</td>
                  <td className="px-4 py-3">{fmtDateTime(item.amendedTime)}</td>
                  <td className="px-4 py-3 max-w-xs truncate">{item.reason}</td>
                  <td className="px-4 py-3">
                    <span className={`inline-block px-2 py-0.5 rounded text-xs ${
                      item.status === 'APPROVED' ? 'bg-green-100 text-green-700' :
                      item.status === 'REJECTED' ? 'bg-red-100 text-red-700' :
                      'bg-yellow-100 text-yellow-700'
                    }`}>
                      {STATUS_LABELS[item.status] ?? item.status}
                    </span>
                  </td>
                  <td className="px-4 py-3 flex gap-2">
                    {item.status === 'PENDING' && (
                      <>
                        <button onClick={() => review(item.id, 'APPROVED')}
                          className="text-xs text-green-600 hover:underline">核准</button>
                        <button onClick={() => review(item.id, 'REJECTED')}
                          className="text-xs text-red-600 hover:underline">駁回</button>
                      </>
                    )}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
