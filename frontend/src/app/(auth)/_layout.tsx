import React from "react";
import { Stack } from "expo-router";

export default function AuthLayout() {
  return (
    <Stack
      screenOptions={{
        headerStyle: { backgroundColor: "#1E2761" },
        headerTintColor: "#FFFFFF",
        headerTitleStyle: { fontWeight: "600" },
        contentStyle: { backgroundColor: "#F6F7FB" },
        animation: "slide_from_right",
      }}
    >
      <Stack.Screen
        name="login"
        options={{
          title: "Sign In",
          headerBackVisible: false,
        }}
      />
      <Stack.Screen
        name="register"
        options={{
          title: "Create Account",
          headerStyle: { backgroundColor: "#141B47" },
        }}
      />
      <Stack.Screen
        name="register-company"
        options={{
          title: "Company Registration",
        }}
      />
      <Stack.Screen
        name="register-research"
        options={{
          title: "Research Member Registration",
        }}
      />
      <Stack.Screen
        name="forgot-password"
        options={{
          title: "Forgot Password",
        }}
      />
    </Stack>
  );
}
