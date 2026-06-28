import React from "react";
import { TouchableOpacity, Text, Alert, Platform } from "react-native";
import { useAuthStore } from "@/store/authStore";

function confirmAsync(title: string, message: string): Promise<boolean> {
  return new Promise((resolve) => {
    if (Platform.OS === "web") {
      resolve(window.confirm(`${title}\n\n${message}`));
    } else {
      Alert.alert(title, message, [
        { text: "Cancel", style: "cancel", onPress: () => resolve(false) },
        { text: "Sign Out", style: "destructive", onPress: () => resolve(true) },
      ]);
    }
  });
}

export default function SignOutButton() {
  const { isAuthenticated, logout } = useAuthStore();

  if (!isAuthenticated) return null;

  const handleSignOut = async () => {
    const confirmed = await confirmAsync(
      "Sign Out",
      "Are you sure you want to sign out?"
    );
    if (confirmed) {
      await logout();
      // The layout's auth guard (<Redirect>) handles navigation
    }
  };

  return (
    <TouchableOpacity
      onPress={handleSignOut}
      className="px-3 py-1.5 rounded-lg border border-gold/60 active:bg-gold/20"
    >
      <Text className="text-gold font-bold text-xs">Sign Out</Text>
    </TouchableOpacity>
  );
}
