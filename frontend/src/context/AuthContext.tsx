import { createContext, useContext, useState, type ReactNode } from 'react';
import { clearToken, getToken, setToken } from '../lib/apiClient';
import { apiClient } from '../lib/apiClient';
import type { TokenResponse, UserRole } from '../types/api';

interface AuthContextValue {
  token: string | null;
  username: string | null;
  role: UserRole | null;
  isAdmin: boolean;
  isSuperAdmin: boolean;
  login: (username: string, password: string) => Promise<{ mustChangePassword: boolean }>;
  logout: () => void;
  setUsername: (name: string) => void;
}

const AuthContext = createContext<AuthContextValue | null>(null);

function decodePayload(token: string): Record<string, unknown> | null {
  try {
    return JSON.parse(atob(token.split('.')[1]));
  } catch {
    return null;
  }
}

function decodeUsername(token: string): string | null {
  const payload = decodePayload(token);
  return typeof payload?.sub === 'string' ? payload.sub : null;
}

function decodeRole(token: string): UserRole | null {
  const payload = decodePayload(token);
  const role = payload?.role;
  if (role === 'SUPER_ADMIN' || role === 'ADMIN' || role === 'EMPLOYEE') {
    return role;
  }
  return null;
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setTokenState] = useState<string | null>(getToken());
  const [username, setUsername] = useState<string | null>(
    token ? decodeUsername(token) : null
  );
  const [role, setRole] = useState<UserRole | null>(
    token ? decodeRole(token) : null
  );

  const login = async (u: string, password: string): Promise<{ mustChangePassword: boolean }> => {
    const res = await apiClient.post<TokenResponse>('/api/auth/login', {
      username: u,
      password,
    }, { skipAuth: true });
    setToken(res.accessToken);
    setTokenState(res.accessToken);
    setUsername(decodeUsername(res.accessToken) ?? u);
    setRole(res.role ?? decodeRole(res.accessToken));
    if (res.mustChangePassword) {
      localStorage.setItem('attendance_must_change_password', 'true');
    } else {
      localStorage.removeItem('attendance_must_change_password');
    }
    return { mustChangePassword: res.mustChangePassword };
  };

  const logout = () => {
    clearToken();
    setTokenState(null);
    setUsername(null);
    setRole(null);
    localStorage.removeItem('attendance_must_change_password');
  };

  const isSuperAdmin = role === 'SUPER_ADMIN';
  const isAdmin = role === 'ADMIN' || role === 'SUPER_ADMIN';

  return (
    <AuthContext.Provider value={{ token, username, role, isAdmin, isSuperAdmin, login, logout, setUsername }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
