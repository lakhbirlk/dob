import React from "react";
import { View, Text, ActivityIndicator } from "react-native";
import { Stack, Redirect } from "expo-router";
import { useAuthStore } from "@/store/authStore";
import SignOutButton from "@/components/SignOutButton";

export default function MemberLayout() {
  const { isAuthenticated, isHydrated } = useAuthStore();

  if (!isHydrated) {
    return (
      <View className="flex-1 items-center justify-center bg-bg">
        <ActivityIndicator size="large" color="#1E2761" />
      </View>
    );
  }

  if (!isAuthenticated) {
    return <Redirect href="/(public)/business-model" />;
  }

  return (
    <Stack
      screenOptions={{
        headerStyle: { backgroundColor: "#141B47" },
        headerTintColor: "#FFFFFF",
        headerTitleStyle: { fontWeight: "700", fontSize: 17 },
        headerShadowVisible: false,
        contentStyle: { backgroundColor: "#F6F7FB" },
        animation: "slide_from_right",
        headerRight: () => <SignOutButton />,
      }}
    >
      <Stack.Screen name="dashboard" options={{ title: "Research Dashboard" }} />
      <Stack.Screen name="downloads" options={{ title: "Downloads", headerShown: true }} />
      <Stack.Screen name="profile" options={{ title: "My Profile", headerShown: true }} />
      <Stack.Screen name="unlocked-companies" options={{ title: "Unlocked Companies", headerShown: true }} />
      <Stack.Screen name="credit-history" options={{ title: "Credit History", headerShown: true }} />
      <Stack.Screen name="activity-log" options={{ title: "Activity Log", headerShown: true }} />
    </Stack>
  );
}
