import React from "react";
import { View, Text, ActivityIndicator } from "react-native";
import { Stack, Redirect } from "expo-router";
import { useAuthStore } from "@/store/authStore";
import SignOutButton from "@/components/SignOutButton";

export default function CompanyLayout() {
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
        headerStyle: { backgroundColor: "#1E2761" },
        headerTintColor: "#FFFFFF",
        headerTitleStyle: { fontWeight: "600" },
        contentStyle: { backgroundColor: "#F6F7FB" },
        animation: "slide_from_right",
        headerRight: () => <SignOutButton />,
      }}
    >
      <Stack.Screen
        name="dashboard"
        options={{
          title: "Company Dashboard",
          headerShown: true,
        }}
      />
      <Stack.Screen
        name="profile"
        options={{
          title: "Company Profile",
          headerShown: true,
        }}
      />
      <Stack.Screen
        name="financials"
        options={{
          title: "Financial Statements",
          headerShown: true,
        }}
      />
    </Stack>
  );
}
