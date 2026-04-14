import { NavLink, Outlet, useNavigate, useLocation } from 'react-router-dom';
import { useEffect } from 'react';
import { useAuth } from '../context/AuthContext';

const navItems = [
  { to: '/admin/attendance', label: '出勤紀錄' },
  { to: '/admin/amendments', label: '補打卡審核' },
  { to: '/admin/users', label: '員工帳號' },
  { to: '/admin/config', label: '出勤規則' },
  { to: '/admin/password-policy', label: '密碼規則設定' },
  { to: '/admin/mail-settings', label: '郵件設定' },
  { to: '/admin/notifications', label: '通知收件人' },
];

export function Layout() {
  const { username, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    const current = navItems.find(item => location.pathname.startsWith(item.to));
    document.title = current
      ? `出勤管理後台 - ${current.label}`
      : '出勤管理後台';
  }, [location.pathname]);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="flex h-screen bg-gray-100">
      {/* Sidebar */}
      <aside className="w-56 flex-shrink-0 bg-gray-900 text-white flex flex-col">
        <div className="px-4 py-5 border-b border-gray-700">
          <span className="text-lg font-semibold">出勤管理後台</span>
        </div>
        <nav className="flex-1 px-2 py-4 space-y-1">
          {navItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) =>
                `block px-3 py-2 rounded text-sm transition-colors ${
                  isActive
                    ? 'bg-blue-600 text-white'
                    : 'text-gray-300 hover:bg-gray-700 hover:text-white'
                }`
              }
            >
              {item.label}
            </NavLink>
          ))}
        </nav>
        <div className="px-4 py-4 border-t border-gray-700">
          <p className="text-xs text-gray-400 mb-2 truncate">{username}</p>
          <NavLink
            to="/admin/profile"
            className={({ isActive }) =>
              `block px-3 py-2 rounded text-sm transition-colors mb-1 ${
                isActive
                  ? 'bg-blue-600 text-white'
                  : 'text-gray-300 hover:bg-gray-700 hover:text-white'
              }`
            }
          >
            個人資料
          </NavLink>
          <button
            onClick={handleLogout}
            className="w-full text-left text-sm text-gray-300 hover:text-white"
          >
            登出
          </button>
        </div>
      </aside>

      {/* Main content */}
      <main className="flex-1 overflow-auto p-6">
        <Outlet />
      </main>
    </div>
  );
}
