import { Redirect } from "expo-router";
import { View, Text, ActivityIndicator } from "react-native";
import { useAuthStore } from "@/store/authStore";
import { UserRole } from "@/types";

export default function Index() {
  const { isAuthenticated, isHydrated, user } = useAuthStore();

  // Show loading while auth state is being hydrated
  if (!isHydrated) {
    return (
      <View className="flex-1 items-center justify-center bg-bg">
        <ActivityIndicator size="large" color="#1E2761" />
        <Text className="text-muted mt-4 text-base">Loading...</Text>
      </View>
    );
  }

  // Redirect based on auth state and role
  if (isAuthenticated && user) {
    switch (user.role) {
      case UserRole.ADMIN:
      case UserRole.SUPER_ADMIN:
        return <Redirect href="/(admin)/dashboard" />;
      case UserRole.COMPANY:
        return <Redirect href="/(company)/dashboard" />;
      case UserRole.MEMBER:
        return <Redirect href="/(member)/dashboard" />;
      default:
        return <Redirect href="/(public)" />;
    }
  }

  // Unauthenticated - go to business model landing page
  return <Redirect href="/(public)/business-model" />;
}
