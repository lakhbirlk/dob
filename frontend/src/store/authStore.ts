import { create } from "zustand";
import { storage } from "@/services/storage";
import type { User } from "@/types";
import { setTokens, clearTokens } from "@/services/api";

const USER_KEY = "dob_user";

interface AuthState {
  user: User | null;
  accessToken: string | null;
  refreshToken: string | null;
  isAuthenticated: boolean;
  isHydrated: boolean;
  isLoading: boolean;
  error: string | null;

  // Actions
  login: (user: User, accessToken: string, refreshToken: string) => Promise<void>;
  logout: () => Promise<void>;
  setUser: (user: User) => void;
  setLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
  setHydrated: () => void;
  updateToken: (accessToken: string, refreshToken: string) => Promise<void>;
}

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  accessToken: null,
  refreshToken: null,
  isAuthenticated: false,
  isHydrated: false,
  isLoading: false,
  error: null,

  login: async (user: User, accessToken: string, refreshToken: string) => {
    await setTokens(accessToken, refreshToken);
    await storage.setItemAsync(USER_KEY, JSON.stringify(user));
    set({
      user,
      accessToken,
      refreshToken,
      isAuthenticated: true,
      error: null,
      isLoading: false,
    });
  },

  logout: async () => {
    await clearTokens();
    await storage.deleteItemAsync(USER_KEY);
    set({
      user: null,
      accessToken: null,
      refreshToken: null,
      isAuthenticated: false,
      error: null,
      isLoading: false,
    });
  },

  setUser: async (user: User) => {
    await storage.setItemAsync(USER_KEY, JSON.stringify(user));
    set({ user });
  },

  setLoading: (isLoading: boolean) => {
    set({ isLoading });
  },

  setError: (error: string | null) => {
    set({ error, isLoading: false });
  },

  setHydrated: () => {
    set({ isHydrated: true });
  },

  updateToken: async (accessToken: string, refreshToken: string) => {
    await setTokens(accessToken, refreshToken);
    set({ accessToken, refreshToken });
  },
}));
