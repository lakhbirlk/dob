import React from "react";
import { View, ActivityIndicator } from "react-native";
import { Stack, Redirect } from "expo-router";
import { useAuthStore } from "@/store/authStore";
import SignOutButton from "@/components/SignOutButton";

export default function AuthenticatedLayout() {
  const { isAuthenticated, isHydrated } = useAuthStore();

  if (!isHydrated) {
    return (
      <View className="flex-1 items-center justify-center bg-bg">
        <ActivityIndicator size="large" color="#1E2761" />
      </View>
    );
  }

  if (!isAuthenticated) {
    return <Redirect href="/(auth)/login?redirect=/(authenticated)/companies" />;
  }

  return (
    <Stack
      screenOptions={{
        headerStyle: { backgroundColor: "#141B47" },
        headerTintColor: "#FFFFFF",
        headerTitleStyle: { fontWeight: "700", fontSize: 17 },
        headerBackTitle: "Back",
        contentStyle: { backgroundColor: "#F6F7FB" },
        animation: "slide_from_right",
        headerRight: () => <SignOutButton />,
      }}
    >
      <Stack.Screen name="companies" options={{ title: "Company Database", headerShown: true }} />
      <Stack.Screen name="company/[id]" options={{ title: "Company Profile", headerShown: true }} />
    </Stack>
  );
}
