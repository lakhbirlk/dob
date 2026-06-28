import React from "react";
import { View, Text } from "react-native";
import { colors } from "@/theme/colors";

export const Divider: React.FC<{ className?: string }> = ({ className = "" }) => (
  <View className={`h-px bg-line my-4 ${className}`} />
);

export const OrDivider: React.FC = () => (
  <View className="flex-row items-center my-6">
    <View className="flex-1 h-px bg-line" />
    <Text className="mx-4 text-sm text-faint font-medium">or</Text>
    <View className="flex-1 h-px bg-line" />
  </View>
);
