import React from "react";
import { View, Text, ActivityIndicator } from "react-native";
import { Stack, Redirect } from "expo-router";
import { useAuthStore } from "@/store/authStore";
import SignOutButton from "@/components/SignOutButton";
import { UserRole } from "@/types";

const ADMIN_ROLES = [UserRole.ADMIN, UserRole.SUPER_ADMIN];

export default function AdminLayout() {
  const { isAuthenticated, isHydrated, user } = useAuthStore();

  if (!isHydrated) {
    return (
      <View className="flex-1 items-center justify-center bg-bg">
        <ActivityIndicator size="large" color="#1E2761" />
      </View>
    );
  }

  if (!isAuthenticated) {
    return <Redirect href="/(auth)/login?redirect=/(admin)/dashboard" />;
  }

  if (!user || !ADMIN_ROLES.includes(user.role)) {
    return <Redirect href="/access-denied" />;
  }

  return (
    <Stack
      screenOptions={{
        headerStyle: { backgroundColor: "#141B47" },
        headerTintColor: "#E8B84B",
        headerTitleStyle: { fontWeight: "800", fontSize: 17, color: "#FFFFFF" },
        headerShadowVisible: false,
        contentStyle: { backgroundColor: "#F6F7FB" },
        animation: "slide_from_right",
        headerRight: () => <SignOutButton />,
      }}
    >
      <Stack.Screen name="dashboard" options={{ title: "Admin Panel" }} />
      <Stack.Screen name="members" options={{ title: "Research Members", headerShown: true }} />
      <Stack.Screen name="members/[id]" options={{ title: "Member Details", headerShown: true }} />
      <Stack.Screen name="company-members" options={{ title: "Company Members", headerShown: true }} />
      <Stack.Screen name="company-members/[id]" options={{ title: "Company Details", headerShown: true }} />
      <Stack.Screen name="pending-approvals" options={{ title: "Pending Approvals", headerShown: true }} />
      <Stack.Screen name="refunds" options={{ title: "Refund Management", headerShown: true }} />
      <Stack.Screen name="grievances" options={{ title: "Grievance Management", headerShown: true }} />
      <Stack.Screen name="audit-logs" options={{ title: "Audit Logs", headerShown: true }} />
    </Stack>
  );
}
