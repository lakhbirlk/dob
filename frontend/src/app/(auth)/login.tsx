import React, { useState } from "react";
import {
  View, Text, ScrollView, KeyboardAvoidingView, Platform, TouchableOpacity,
} from "react-native";
import { Link, router, useLocalSearchParams } from "expo-router";
import { useAuth } from "@/hooks/useAuth";
import { useAuthStore } from "@/store/authStore";
import { Input } from "@/components/Input";
import { Button } from "@/components/Button";
import { UserRole } from "@/types";

export default function LoginScreen() {
  const { login, isLoading, error, clearError } = useAuth();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [errors, setErrors] = useState<{ email?: string; password?: string }>({});

  // Optional redirect params from pricing page or session expiry
  const params = useLocalSearchParams<{ redirect?: string; plan?: string; reason?: string }>();

  const validate = (): boolean => {
    const e: typeof errors = {};
    if (!email.trim()) e.email = "Email is required";
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/i.test(email.trim())) e.email = "Enter a valid email";
    if (!password) e.password = "Password is required";
    else if (password.length < 6) e.password = "At least 6 characters";
    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const navigateAfterLogin = (role: UserRole) => {
    // If there's a pending redirect (e.g. from pricing page), honour it
    if (params.redirect) {
      const target = params.plan
        ? `${params.redirect}?plan=${params.plan}`
        : params.redirect;
      router.replace(target as any);
      return;
    }
    // Default role-based navigation
    switch (role) {
      case UserRole.ADMIN:
      case UserRole.SUPER_ADMIN:
        router.replace("/(admin)/dashboard");
        break;
      case UserRole.COMPANY:
        router.replace("(company)/dashboard");
        break;
      case UserRole.MEMBER:
      default:
        router.replace("/(member)/dashboard");
        break;
    }
  };

  const handleLogin = async () => {
    clearError();
    if (!validate()) return;
    try {
      await login({ email: email.trim(), password });
      const user = useAuthStore.getState().user;
      if (user) navigateAfterLogin(user.role);
    } catch {}
  };

  return (
    <KeyboardAvoidingView className="flex-1 bg-bg" behavior={Platform.OS === "ios" ? "padding" : "height"}>
      <ScrollView className="flex-1" contentContainerStyle={{ flexGrow: 1 }} keyboardShouldPersistTaps="handled">
        {/* Brand Header */}
        <View className="bg-navy-deep pt-14 pb-10 px-6 items-center" style={{ borderBottomLeftRadius: 24, borderBottomRightRadius: 24 }}>
          <View className="flex-row items-center gap-x-3 mb-4">
            <View className="bg-navy px-3 py-1.5 rounded-lg">
              <Text className="text-gold font-extrabold text-lg tracking-wider">DoB</Text>
            </View>
            <Text className="text-white font-bold text-xl">DataOfBusiness</Text>
          </View>
          {params.plan && (
            <View className="bg-white/10 px-4 py-2 rounded-full">
              <Text className="text-white/90 text-sm font-medium">
                {params.plan === "RESEARCH" ? "🔬 Research Membership" : "🏢 Company Listing"} selected
              </Text>
            </View>
          )}
        </View>

        {/* Form Card */}
        <View className="px-5 -mt-6">
          <View className="bg-card rounded-xl shadow-xl border border-line/30 px-6 py-7">
            <Text className="text-2xl font-extrabold text-ink mb-1">Welcome Back</Text>
            <Text className="text-muted text-base mb-6">Sign in to your account</Text>

            {params.reason === "session_expired" && (
              <View className="bg-amber-light border border-gold/30 rounded-lg px-4 py-3 mb-5 flex-row items-center gap-x-2">
                <Text className="text-base">⏰</Text>
                <Text className="text-ink text-sm flex-1">Your session has expired. Please sign in again.</Text>
              </View>
            )}

            {error && (
              <View className="bg-red-light border border-red/30 rounded-lg px-4 py-3 mb-5 flex-row items-center justify-between">
                <Text className="text-red text-sm flex-1">{error}</Text>
                <TouchableOpacity onPress={clearError}><Text className="text-red font-bold text-sm ml-3">✕</Text></TouchableOpacity>
              </View>
            )}

            <Input
              label="Email Address"
              value={email}
              onChangeText={(t) => { setEmail(t); if (errors.email) setErrors(p => ({ ...p, email: undefined })); }}
              placeholder="you@example.com"
              keyboardType="email-address"
              autoCapitalize="none"
              autoComplete="email"
              error={errors.email}
            />

            <Input
              label="Password"
              value={password}
              onChangeText={(t) => { setPassword(t); if (errors.password) setErrors(p => ({ ...p, password: undefined })); }}
              placeholder="Enter your password"
              isPassword
              autoComplete="password"
              error={errors.password}
            />

            <Link href="/(auth)/forgot-password" asChild>
              <TouchableOpacity className="self-end mb-6">
                <Text className="text-navy font-semibold text-sm">Forgot Password?</Text>
              </TouchableOpacity>
            </Link>

            <Button variant="primary" size="xl" loading={isLoading} onPress={handleLogin} style={{ width: "100%" }}>
              Sign In Securely
            </Button>

            <View className="flex-row justify-center mt-6">
              <Text className="text-muted text-base">Don't have an account? </Text>
              <Link href="/(auth)/register" asChild>
                <TouchableOpacity><Text className="text-navy font-bold text-base">Create One →</Text></TouchableOpacity>
              </Link>
            </View>
          </View>
        </View>

        {/* Footer */}
        <View className="px-6 pb-10 mt-8">
          <Text className="text-faint text-xs text-center">
            Secured with JWT encryption. Your data is protected under India's DPDP Act 2023.
          </Text>
        </View>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}
