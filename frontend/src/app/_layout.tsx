import React, { useEffect } from "react";
import { Stack } from "expo-router";
import { StatusBar } from "expo-status-bar";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { useAuthStore } from "@/store/authStore";
import { storage } from "@/services/storage";
import { setAuthFailureHandler } from "@/services/api";
import { router } from "expo-router";
import type { User } from "@/types";
import "../../global.css";

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 2,
      staleTime: 1000 * 60,
      refetchOnWindowFocus: false,
    },
    mutations: {
      retry: 1,
    },
  },
});

function AuthHydration({ children }: { children: React.ReactNode }) {
  const { setHydrated, login } = useAuthStore();

  useEffect(() => {
    async function hydrate() {
      try {
        const accessToken = await storage.getItemAsync("dob_access_token");
        const refreshToken = await storage.getItemAsync("dob_refresh_token");
        const userJson = await storage.getItemAsync("dob_user");

        if (accessToken && refreshToken && userJson) {
          const user: User = JSON.parse(userJson);
          await login(user, accessToken, refreshToken);
        }
      } catch {
        // Hydration failed - user stays logged out
      }
      setHydrated();
    }

    hydrate();
  }, [login, setHydrated]);

  return <>{children}</>;
}

// ─────────────────────── Global Auth Failure Listener ───────────────────────

function AuthFailureListener({ children }: { children: React.ReactNode }) {
  const logout = useAuthStore((s) => s.logout);

  useEffect(() => {
    setAuthFailureHandler(async (reason) => {
      if (reason === "session_expired") {
        await logout();
        router.replace("/(auth)/login?reason=session_expired");
      } else if (reason === "forbidden") {
        router.replace("/access-denied");
      }
    });
  }, [logout]);

  return <>{children}</>;
}

export default function RootLayout() {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthHydration>
        <AuthFailureListener>
          <StatusBar style="dark" />
        <Stack
          screenOptions={{
            headerShown: false,
            contentStyle: { backgroundColor: "#F6F7FB" },
            animation: "slide_from_right",
          }}
        >
          <Stack.Screen name="index" />
          <Stack.Screen name="payment" />
          <Stack.Screen name="access-denied" />
          <Stack.Screen name="(public)" />
          <Stack.Screen name="(auth)" />
          <Stack.Screen name="(member)" />
          <Stack.Screen name="(company)" />
          <Stack.Screen name="(admin)" />
          <Stack.Screen name="(authenticated)" />
        </Stack>
        </AuthFailureListener>
      </AuthHydration>
    </QueryClientProvider>
  );
}
