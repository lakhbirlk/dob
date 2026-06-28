import React from "react";
import { View, Text } from "react-native";

type BadgeVariant = "success" | "warning" | "danger" | "info" | "neutral" | "gold";

const variantStyles: Record<BadgeVariant, string> = {
  success: "bg-green-light border-green/20 text-green",
  warning: "bg-gold-pale border-gold/30 text-gold-dark",
  danger: "bg-red-light border-red/20 text-red",
  info: "bg-navy/10 border-navy/20 text-navy",
  neutral: "bg-bg border-line text-muted",
  gold: "bg-gold/15 border-gold/40 text-gold-dark",
};

interface BadgeProps { variant?: BadgeVariant; children: React.ReactNode; className?: string }

export const Badge: React.FC<BadgeProps> = ({ variant = "neutral", children, className = "" }) => (
  <View className={`px-3 py-1 rounded-full border ${variantStyles[variant]} ${className}`}>
    <Text className={`text-xs font-bold ${variantStyles[variant].split(" ")[2]}`}>{children}</Text>
  </View>
);

export default Badge;
