import React from "react";
import { View, type ViewProps, type StyleProp, type ViewStyle } from "react-native";

interface CardProps extends ViewProps {
  children: React.ReactNode;
  padding?: "none" | "sm" | "md" | "lg" | "xl";
  variant?: "default" | "flat" | "bordered" | "elevated" | "subtle";
  style?: StyleProp<ViewStyle>;
}

const paddingStyles: Record<NonNullable<CardProps["padding"]>, string> = {
  none: "p-0", sm: "p-3", md: "p-5", lg: "p-7", xl: "p-9",
};

const variantStyles: Record<NonNullable<CardProps["variant"]>, string> = {
  default: "bg-card rounded-default shadow-md border border-line/50",
  flat: "bg-card rounded-default",
  bordered: "bg-card rounded-default border border-line",
  elevated: "bg-card rounded-xl shadow-xl border border-line/30",
  subtle: "bg-surface rounded-default border border-line/50",
};

export const Card: React.FC<CardProps> = ({
  children, padding = "md", variant = "default", style, className = "", ...rest
}) => {
  const classes = [variantStyles[variant], paddingStyles[padding], className].filter(Boolean).join(" ");
  return <View className={classes} style={style} {...rest}>{children}</View>;
};

export default Card;
