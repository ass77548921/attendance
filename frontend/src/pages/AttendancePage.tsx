import { useState, useEffect, useCallback } from 'react';
import { useSearchParams } from 'react-router-dom';
import { apiClient } from '../lib/apiClient';
import type {
  AttendanceRecordResponse,
  AttendanceAdjustmentAuditResponse,
  Page,
} from '../types/api';

function fmtDateTime(iso: string | null): string {
  if (!iso) return '-';
  return new Date(iso).toLocaleString('zh-TW', { hour12: false });
}

function fmtDate(iso: string): string {
  return iso;
}

// ── Adjust Modal ─────────────────────────────────────────
function AdjustModal({
  record,
  onClose,
  onSuccess,
}: {
  record: AttendanceRecordResponse;
  onClose: () => void;
  onSuccess: () => void;
}) {
  const toLocalInput = (iso: string | null) => {
    if (!iso) return '';
    return new Date(iso).toISOString().slice(0, 16);
  };

  const [clockIn, setClockIn] = useState(toLocalInput(record.clockInTime));
  const [clockOut, setClockOut] = useState(toLocalInput(record.clockOutTime));
  const [reason, setReason] = useState('');
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!reason.trim()) { setError('原因為必填'); return; }
    setSaving(true);
    setError('');
    try {
      await apiClient.patch(`/api/admin/attendance/${record.id}/adjust`, {
        clockInTime: clockIn ? new Date(clockIn).toISOString() : null,
        clockOutTime: clockOut ? new Date(clockOut).toISOString() : null,
        reason: reason.trim(),
      });
      onSuccess();
      onClose();
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : '儲存失敗');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div className="bg-white rounded shadow-lg w-full max-w-md p-6">
        <h2 className="text-lg font-semibold mb-4">手動調整出勤紀錄</h2>
        <p className="text-sm text-gray-500 mb-4">
          {record.userFullName} · {fmtDate(record.workDate)}
        </p>
        <form onSubmit={handleSubmit} className="space-y-3">
          <div>
            <label className="block text-sm font-medium mb-1">上班時間</label>
            <input type="datetime-local" value={clockIn} onChange={e => setClockIn(e.target.value)}
              className="w-full border border-gray-300 rounded px-3 py-2 text-sm" />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">下班時間</label>
            <input type="datetime-local" value={clockOut} onChange={e => setClockOut(e.target.value)}
              className="w-full border border-gray-300 rounded px-3 py-2 text-sm" />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">原因 <span className="text-red-500">*</span></label>
            <textarea value={reason} onChange={e => setReason(e.target.value)} rows={2}
              className="w-full border border-gray-300 rounded px-3 py-2 text-sm" />
          </div>
          {error && <p className="text-red-600 text-sm">{error}</p>}
          <div className="flex justify-end gap-2 pt-2">
            <button type="button" onClick={onClose}
              className="px-4 py-2 text-sm border border-gray-300 rounded hover:bg-gray-50">取消</button>
            <button type="submit" disabled={saving}
              className="px-4 py-2 text-sm bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50">
              {saving ? '儲存中…' : '儲存'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

// ── History Modal ─────────────────────────────────────────
function HistoryModal({
  record,
  onClose,
}: {
  record: AttendanceRecordResponse;
  onClose: () => void;
}) {
  const [history, setHistory] = useState<AttendanceAdjustmentAuditResponse[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    apiClient
      .get<AttendanceAdjustmentAuditResponse[]>(`/api/admin/attendance/${record.id}/adjustments`)
      .then(setHistory)
      .finally(() => setLoading(false));
  }, [record.id]);

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div className="bg-white rounded shadow-lg w-full max-w-2xl p-6 overflow-y-auto max-h-[80vh]">
        <h2 className="text-lg font-semibold mb-2">調整歷史</h2>
        <p className="text-sm text-gray-500 mb-4">
          {record.userFullName} · {fmtDate(record.workDate)}
        </p>
        {loading ? (
          <p className="text-sm text-gray-500">載入中…</p>
        ) : history.length === 0 ? (
          <p className="text-sm text-gray-500">無調整紀錄</p>
        ) : (
          <table className="w-full text-sm border-collapse">
            <thead>
              <tr className="bg-gray-100 text-left">
                <th className="px-3 py-2 border">調整時間</th>
                <th className="px-3 py-2 border">調整人</th>
                <th className="px-3 py-2 border">原因</th>
                <th className="px-3 py-2 border">調前上班</th>
                <th className="px-3 py-2 border">調後上班</th>
                <th className="px-3 py-2 border">調前下班</th>
                <th className="px-3 py-2 border">調後下班</th>
              </tr>
            </thead>
            <tbody>
              {history.map((h) => (
                <tr key={h.auditId} className="hover:bg-gray-50">
                  <td className="px-3 py-2 border">{fmtDateTime(h.modifiedAt)}</td>
                  <td className="px-3 py-2 border">{h.modifiedByUsername}</td>
                  <td className="px-3 py-2 border">{h.reason}</td>
                  <td className="px-3 py-2 border">{fmtDateTime(h.beforeClockInTime)}</td>
                  <td className="px-3 py-2 border">{fmtDateTime(h.afterClockInTime)}</td>
                  <td className="px-3 py-2 border">{fmtDateTime(h.beforeClockOutTime)}</td>
                  <td className="px-3 py-2 border">{fmtDateTime(h.afterClockOutTime)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
        <div className="flex justify-end mt-4">
          <button onClick={onClose} className="px-4 py-2 text-sm border border-gray-300 rounded hover:bg-gray-50">
            關閉
          </button>
        </div>
      </div>
    </div>
  );
}

// ── Main Page ─────────────────────────────────────────────
export function AttendancePage() {
  const [searchParams] = useSearchParams();
  const [userId, setUserId] = useState(() => searchParams.get('userId') ?? '');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [isLate, setIsLate] = useState('');
  const [page, setPage] = useState(0);
  const [data, setData] = useState<Page<AttendanceRecordResponse> | null>(null);
  const [loading, setLoading] = useState(false);
  const [toast, setToast] = useState('');
  const [adjustTarget, setAdjustTarget] = useState<AttendanceRecordResponse | null>(null);
  const [historyTarget, setHistoryTarget] = useState<AttendanceRecordResponse | null>(null);

  const buildQuery = useCallback(() => {
    const params = new URLSearchParams();
    if (userId) params.set('userId', userId);
    if (startDate) params.set('startDate', startDate);
    if (endDate) params.set('endDate', endDate);
    if (isLate) params.set('isLate', isLate);
    params.set('page', String(page));
    params.set('size', '20');
    return params.toString();
  }, [userId, startDate, endDate, isLate, page]);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const res = await apiClient.get<Page<AttendanceRecordResponse>>(
        `/api/admin/attendance?${buildQuery()}`
      );
      setData(res);
    } finally {
      setLoading(false);
    }
  }, [buildQuery]);

  useEffect(() => { load(); }, [load]);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setPage(0);
  };

  const showToast = (msg: string) => {
    setToast(msg);
    setTimeout(() => setToast(''), 3000);
  };

  return (
    <div>
      <h1 className="text-xl font-semibold mb-4">出勤紀錄管理</h1>

      {/* Filter form */}
      <form onSubmit={handleSearch} className="bg-white p-4 rounded shadow mb-4 flex flex-wrap gap-3 items-end">
        <div>
          <label className="block text-xs font-medium text-gray-600 mb-1">員工 ID</label>
          <input value={userId} onChange={e => setUserId(e.target.value)} placeholder="全部"
            className="border border-gray-300 rounded px-2 py-1.5 text-sm w-28" />
        </div>
        <div>
          <label className="block text-xs font-medium text-gray-600 mb-1">開始日期</label>
          <input type="date" value={startDate} onChange={e => setStartDate(e.target.value)}
            className="border border-gray-300 rounded px-2 py-1.5 text-sm" />
        </div>
        <div>
          <label className="block text-xs font-medium text-gray-600 mb-1">結束日期</label>
          <input type="date" value={endDate} onChange={e => setEndDate(e.target.value)}
            className="border border-gray-300 rounded px-2 py-1.5 text-sm" />
        </div>
        <div>
          <label className="block text-xs font-medium text-gray-600 mb-1">遲到狀態</label>
          <select value={isLate} onChange={e => setIsLate(e.target.value)}
            className="border border-gray-300 rounded px-2 py-1.5 text-sm">
            <option value="">全部</option>
            <option value="true">遲到</option>
            <option value="false">正常</option>
          </select>
        </div>
        <button type="submit" className="bg-blue-600 text-white px-4 py-1.5 rounded text-sm hover:bg-blue-700">
          查詢
        </button>
      </form>

      {/* Toast */}
      {toast && (
        <div className="mb-3 bg-green-100 text-green-800 px-4 py-2 rounded text-sm">{toast}</div>
      )}

      {/* Table */}
      <div className="bg-white rounded shadow overflow-x-auto">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 border-b">
            <tr>
              <th className="px-4 py-3 text-left">員工</th>
              <th className="px-4 py-3 text-left">日期</th>
              <th className="px-4 py-3 text-left">上班時間</th>
              <th className="px-4 py-3 text-left">下班時間</th>
              <th className="px-4 py-3 text-left">遲到</th>
              <th className="px-4 py-3 text-left">早退</th>
              <th className="px-4 py-3 text-left">最近調整</th>
              <th className="px-4 py-3 text-left">操作</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan={8} className="px-4 py-8 text-center text-gray-400">載入中…</td></tr>
            ) : data?.content.length === 0 ? (
              <tr><td colSpan={8} className="px-4 py-8 text-center text-gray-400">無資料</td></tr>
            ) : (
              data?.content.map((r) => (
                <tr key={r.id} className="border-b hover:bg-gray-50">
                  <td className="px-4 py-3">{r.userFullName}</td>
                  <td className="px-4 py-3">{r.workDate}</td>
                  <td className="px-4 py-3">{fmtDateTime(r.clockInTime)}</td>
                  <td className="px-4 py-3">{fmtDateTime(r.clockOutTime)}</td>
                  <td className="px-4 py-3">
                    {r.isLate ? <span className="text-red-600">遲到 {r.lateMinutes} 分</span> : <span className="text-green-600">正常</span>}
                  </td>
                  <td className="px-4 py-3">
                    {r.isEarlyLeave ? <span className="text-yellow-600">早退 {r.shortMinutes} 分</span> : '-'}
                  </td>
                  <td className="px-4 py-3 text-xs text-gray-500">
                    {r.latestAdjustment
                      ? `${r.latestAdjustment.modifiedByUsername}: ${r.latestAdjustment.reason}`
                      : '-'}
                  </td>
                  <td className="px-4 py-3 flex gap-2">
                    <button onClick={() => setAdjustTarget(r)}
                      className="text-xs text-blue-600 hover:underline">調整</button>
                    <button onClick={() => setHistoryTarget(r)}
                      className="text-xs text-gray-600 hover:underline">歷史</button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Pagination */}
      {data && data.totalPages > 1 && (
        <div className="flex items-center gap-2 mt-3 text-sm">
          <button disabled={page === 0} onClick={() => setPage(p => p - 1)}
            className="px-3 py-1 border rounded disabled:opacity-40 hover:bg-gray-50">上一頁</button>
          <span>第 {page + 1} / {data.totalPages} 頁（共 {data.totalElements} 筆）</span>
          <button disabled={page >= data.totalPages - 1} onClick={() => setPage(p => p + 1)}
            className="px-3 py-1 border rounded disabled:opacity-40 hover:bg-gray-50">下一頁</button>
        </div>
      )}

      {/* Modals */}
      {adjustTarget && (
        <AdjustModal
          record={adjustTarget}
          onClose={() => setAdjustTarget(null)}
          onSuccess={() => { load(); showToast('調整已儲存'); }}
        />
      )}
      {historyTarget && (
        <HistoryModal record={historyTarget} onClose={() => setHistoryTarget(null)} />
      )}
    </div>
  );
}
