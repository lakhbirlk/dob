import { useCallback, useState } from "react";
import { useAuthStore } from "@/store/authStore";
import { authApi, userApi, setTokens } from "@/services/api";
import type { LoginRequest, RegisterRequest, User } from "@/types";
import { UserRole } from "@/types";

interface UseAuthReturn {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  login: (payload: LoginRequest) => Promise<void>;
  register: (payload: RegisterRequest) => Promise<void>;
  logout: () => Promise<void>;
  refreshProfile: () => Promise<void>;
  clearError: () => void;
  hasRole: (role: UserRole) => boolean;
  roles: UserRole[];
}

export function useAuth(): UseAuthReturn {
  const {
    user,
    isAuthenticated,
    isLoading,
    error,
    login: storeLogin,
    logout: storeLogout,
    setUser,
    setLoading,
    setError,
  } = useAuthStore();

  const [roles, setRoles] = useState<UserRole[]>([]);

  const login = useCallback(
    async (payload: LoginRequest) => {
      setLoading(true);
      setError(null);
      try {
        const response = await authApi.login(payload);
        const { user, accessToken, refreshToken } = response.data;
        await storeLogin(user, accessToken, refreshToken);
        setRoles([user.role]);
      } catch (err: unknown) {
        const message =
          err instanceof Error ? err.message : "Login failed. Please try again.";
        setError(message);
        throw err;
      }
    },
    [storeLogin, setLoading, setError]
  );

  const register = useCallback(
    async (payload: RegisterRequest) => {
      setLoading(true);
      setError(null);
      try {
        const response = await authApi.register(payload);
        const { user, accessToken, refreshToken } = response.data;
        await storeLogin(user, accessToken, refreshToken);
        setRoles([user.role]);
      } catch (err: unknown) {
        const message =
          err instanceof Error
            ? err.message
            : "Registration failed. Please try again.";
        setError(message);
        throw err;
      }
    },
    [storeLogin, setLoading, setError]
  );

  const logout = useCallback(async () => {
    try {
      await authApi.logout();
    } catch {
      // Proceed with local logout even if API call fails
    }
    await storeLogout();
    setRoles([]);
  }, [storeLogout]);

  const refreshProfile = useCallback(async () => {
    try {
      const response = await userApi.getProfile();
      setUser(response.data);
      setRoles([response.data.role]);
    } catch {
      // Silently fail - user can still use the app
    }
  }, [setUser]);

  const clearError = useCallback(() => {
    setError(null);
  }, [setError]);

  const hasRole = useCallback(
    (role: UserRole): boolean => {
      return user?.role === role || roles.includes(role);
    },
    [user, roles]
  );

  return {
    user,
    isAuthenticated,
    isLoading,
    error,
    login,
    register,
    logout,
    refreshProfile,
    clearError,
    hasRole,
    roles,
  };
}
