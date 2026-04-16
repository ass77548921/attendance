import { describe, it, expect, vi, beforeEach } from 'vitest';
import { afterEach } from 'vitest';
import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { LoginPage } from './LoginPage';
import { ApiError } from '../lib/apiClient';

const navigateMock = vi.fn();
const useAuthMock = vi.fn();

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual<typeof import('react-router-dom')>('react-router-dom');
  return {
    ...actual,
    useNavigate: () => navigateMock,
  };
});

vi.mock('../context/AuthContext', () => ({
  useAuth: () => useAuthMock(),
}));

describe('LoginPage', () => {
  afterEach(() => {
    cleanup();
  });

  beforeEach(() => {
    navigateMock.mockReset();
    useAuthMock.mockReset();
  });

  it('shows employee account message and does not navigate on admin-console rejection', async () => {
    const loginMock = vi.fn().mockRejectedValue(
      new ApiError('此帳號為員工帳號，無法管理後台', 403, 'EMPLOYEE_ACCOUNT_NO_ADMIN_ACCESS')
    );

    useAuthMock.mockReturnValue({
      login: loginMock,
      token: null,
    });

    render(
      <MemoryRouter>
        <LoginPage />
      </MemoryRouter>
    );

    const usernameInput = document.querySelector('input[type="text"]');
    const passwordInput = document.querySelector('input[type="password"]');
    if (!usernameInput || !passwordInput) {
      throw new Error('Login inputs not found');
    }
    fireEvent.change(usernameInput, { target: { value: 'employee1' } });
    fireEvent.change(passwordInput, { target: { value: 'Password@123' } });
    fireEvent.click(screen.getByRole('button', { name: '登入' }));

    await waitFor(() => {
      expect(screen.getByText('此帳號為員工帳號，無法管理後台')).toBeTruthy();
    });
    expect(navigateMock).not.toHaveBeenCalled();
  });

  it('keeps wrong-credentials message behavior', async () => {
    const loginMock = vi.fn().mockRejectedValue(new ApiError('Unauthorized', 401));

    useAuthMock.mockReturnValue({
      login: loginMock,
      token: null,
    });

    render(
      <MemoryRouter>
        <LoginPage />
      </MemoryRouter>
    );

    const usernameInput = document.querySelector('input[type="text"]');
    const passwordInput = document.querySelector('input[type="password"]');
    if (!usernameInput || !passwordInput) {
      throw new Error('Login inputs not found');
    }
    fireEvent.change(usernameInput, { target: { value: 'admin' } });
    fireEvent.change(passwordInput, { target: { value: 'wrong' } });
    fireEvent.click(screen.getByRole('button', { name: '登入' }));

    await waitFor(() => {
      expect(screen.getByText('帳號或密碼錯誤')).toBeTruthy();
    });
  });
});
