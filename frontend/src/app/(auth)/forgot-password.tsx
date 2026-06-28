import React, { useState } from "react";
import { View, Text, TextInput } from "react-native";
import { Button } from "@/components/Button";
import { Card } from "@/components/Card";
import { router } from "expo-router";

export default function ForgotPasswordScreen() {
  const [email, setEmail] = useState("");
  const [sent, setSent] = useState(false);
  const [error, setError] = useState("");

  const handleReset = () => {
    if (!email) {
      setError("Email is required");
      return;
    }
    // In production: call authApi.forgotPassword(email)
    setSent(true);
  };

  const inputClass = `border rounded-lg px-4 py-3 text-base bg-card ${error ? "border-red" : "border-line"}`;

  return (
    <View className="flex-1 bg-bg justify-center px-6">
      <Card variant="default" className="p-6">
        <Text className="text-2xl font-extrabold text-navy text-center">
          Reset Password
        </Text>

        {sent ? (
          <View className="mt-6">
            <Text className="text-base text-green text-center">
              If an account exists with that email, you will receive password reset instructions.
            </Text>
            <Button variant="ghost" onPress={() => router.back()} style={{ marginTop: 20 }}>
              Back to Login
            </Button>
          </View>
        ) : (
          <View className="mt-6">
            <Text className="text-sm text-muted text-center mb-5">
              Enter your email address and we'll send you a link to reset your password.
            </Text>
            {error ? (
              <Text className="text-red text-sm mb-3">{error}</Text>
            ) : null}
            <Text className="text-sm font-semibold text-muted mb-1.5">Email</Text>
            <TextInput
              value={email}
              onChangeText={(t) => { setEmail(t); setError(""); }}
              placeholder="you@example.com"
              keyboardType="email-address"
              autoCapitalize="none"
              className={inputClass}
              placeholderTextColor="#98A1B3"
            />
            <Button variant="primary" size="lg" onPress={handleReset} style={{ marginTop: 20 }}>
              Send Reset Link
            </Button>
            <Button variant="ghost" onPress={() => router.back()} style={{ marginTop: 8 }}>
              Back to Login
            </Button>
          </View>
        )}
      </Card>
    </View>
  );
}
