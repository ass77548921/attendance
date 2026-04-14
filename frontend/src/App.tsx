import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { ProtectedRoute } from './components/ProtectedRoute';
import { Layout } from './components/Layout';
import { LoginPage } from './pages/LoginPage';
import { AttendancePage } from './pages/AttendancePage';
import { AmendmentsPage } from './pages/AmendmentsPage';
import { UsersPage } from './pages/UsersPage';
import { AttendanceConfigPage } from './pages/AttendanceConfigPage';
import { MailSettingsPage } from './pages/MailSettingsPage';
import { NotificationsPage } from './pages/NotificationsPage';
import { ProfilePage } from './pages/ProfilePage';
import { ChangePasswordPage } from './pages/ChangePasswordPage';
import { PasswordPolicyPage } from './pages/PasswordPolicyPage';

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route
            path="/change-password"
            element={
              <ProtectedRoute>
                <ChangePasswordPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin"
            element={
              <ProtectedRoute>
                <Layout />
              </ProtectedRoute>
            }
          >
            <Route index element={<Navigate to="attendance" replace />} />
            <Route path="attendance" element={<AttendancePage />} />
            <Route path="amendments" element={<AmendmentsPage />} />
            <Route path="users" element={<UsersPage />} />
            <Route path="config" element={<AttendanceConfigPage />} />
            <Route path="mail-settings" element={<MailSettingsPage />} />
            <Route path="notifications" element={<NotificationsPage />} />
            <Route path="profile" element={<ProfilePage />} />
            <Route path="password-policy" element={<PasswordPolicyPage />} />
          </Route>
          <Route path="*" element={<Navigate to="/admin/attendance" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
