import React from "react";
import { Stack } from "expo-router";
import SignOutButton from "@/components/SignOutButton";

export default function PublicLayout() {
  return (
    <Stack
      screenOptions={{
        headerStyle: { backgroundColor: "#141B47" },
        headerTintColor: "#FFFFFF",
        headerTitleStyle: { fontWeight: "700", fontSize: 17 },
        headerBackTitle: "Back",
        contentStyle: { backgroundColor: "#F6F7FB" },
        animation: "slide_from_right",
        headerShadowVisible: false,
        headerRight: () => <SignOutButton />,
      }}
    >
      <Stack.Screen name="index" options={{ title: "DataOfBusiness", headerTitle: "" }} />
      <Stack.Screen name="pricing" options={{ title: "Pricing Plans", headerShown: true }} />
      <Stack.Screen name="about" options={{ title: "About Us", headerShown: true }} />
      <Stack.Screen name="contact" options={{ title: "Contact Us", headerShown: true }} />
      <Stack.Screen name="privacy" options={{ title: "Privacy Policy", headerShown: true }} />
      <Stack.Screen name="terms" options={{ title: "Terms of Service", headerShown: true }} />
      <Stack.Screen name="refund" options={{ title: "Refund Policy", headerShown: true }} />
      <Stack.Screen name="grievance" options={{ title: "Grievance Redressal", headerShown: true }} />
      <Stack.Screen name="business-model" options={{ title: "Business Model & Sitemap", headerShown: true }} />
    </Stack>
  );
}
