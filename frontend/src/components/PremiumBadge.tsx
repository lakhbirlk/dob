import React from "react";
import { View, Text } from "react-native";

interface PremiumBadgeProps {
  size?: "sm" | "md" | "lg";
  className?: string;
}

const sizeStyles: Record<
  NonNullable<PremiumBadgeProps["size"]>,
  { container: string; text: string; icon: string }
> = {
  sm: {
    container: "px-2 py-0.5 rounded-full",
    text: "text-[9px]",
    icon: "text-[10px]",
  },
  md: {
    container: "px-2.5 py-1 rounded-full",
    text: "text-[10px]",
    icon: "text-xs",
  },
  lg: {
    container: "px-3 py-1.5 rounded-full",
    text: "text-xs",
    icon: "text-sm",
  },
};

/**
 * PremiumBadge — small gold badge indicating premium/premium-locked content.
 * Use on locked cards, premium-only sections, and upgrade CTAs.
 */
export const PremiumBadge: React.FC<PremiumBadgeProps> = ({
  size = "md",
  className = "",
}) => {
  const s = sizeStyles[size];
  return (
    <View
      className={`${s.container} bg-gold/15 border border-gold/30 flex-row items-center gap-x-1 ${className}`}
    >
      <Text className={s.icon}>⭐</Text>
      <Text className={`${s.text} font-extrabold text-gold-dark`}>
        PREMIUM
      </Text>
    </View>
  );
};

export default PremiumBadge;
