import React from "react";
import { View, Text } from "react-native";
import { Stack, router } from "expo-router";
import { Button } from "@/components/Button";
import { Card } from "@/components/Card";

export default function AccessDeniedScreen() {
  return (
    <>
      <Stack.Screen options={{ title: "Access Denied", headerShown: true }} />
      <View className="flex-1 bg-bg items-center justify-center px-6">
        <Card variant="elevated" className="items-center py-10 px-8 max-w-sm w-full">
          <View className="w-20 h-20 rounded-full bg-red/10 items-center justify-center mb-6">
            <Text className="text-5xl">🚫</Text>
          </View>

          <Text className="text-2xl font-extrabold text-ink text-center mb-2">
            Access Denied
          </Text>
          <Text className="text-base text-muted text-center leading-6 mb-2">
            You do not have the required permissions to access this area.
          </Text>
          <Text className="text-sm text-faint text-center mb-8">
            This section is restricted to administrators only. If you believe
            this is a mistake, please contact support.
          </Text>

          <View className="flex-row gap-x-3">
            <Button
              variant="outline"
              size="lg"
              onPress={() => router.back()}
            >
              Go Back
            </Button>
            <Button
              variant="gold"
              size="lg"
              onPress={() => router.replace("/(public)")}
            >
              Home
            </Button>
          </View>
        </Card>
      </View>
    </>
  );
}
