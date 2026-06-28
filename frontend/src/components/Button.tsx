import React from "react";
import {
  TouchableOpacity, Text, ActivityIndicator,
  type TouchableOpacityProps, type StyleProp, type ViewStyle, type TextStyle,
} from "react-native";

type ButtonVariant = "primary" | "secondary" | "ghost" | "gold" | "danger" | "outline" | "subtle";
type ButtonSize = "sm" | "md" | "lg" | "xl";

interface ButtonProps extends Omit<TouchableOpacityProps, "style"> {
  variant?: ButtonVariant;
  size?: ButtonSize;
  loading?: boolean;
  leftIcon?: React.ReactNode;
  rightIcon?: React.ReactNode;
  style?: StyleProp<ViewStyle>;
  textStyle?: StyleProp<TextStyle>;
  children: React.ReactNode;
}

const variantConfig: Record<ButtonVariant, { bg: string; text: string; border: string; shadow: string }> = {
  primary:  { bg: "bg-navy", text: "text-white", border: "", shadow: "shadow-md" },
  secondary:{ bg: "bg-navy-2", text: "text-white", border: "", shadow: "shadow-md" },
  ghost:    { bg: "bg-transparent", text: "text-navy", border: "border border-line", shadow: "" },
  gold:     { bg: "bg-gold", text: "text-navy-deep", border: "", shadow: "shadow-glow" },
  danger:   { bg: "bg-red", text: "text-white", border: "", shadow: "shadow-md" },
  outline:  { bg: "bg-transparent", text: "text-navy", border: "border-2 border-navy", shadow: "" },
  subtle:   { bg: "bg-navy/5", text: "text-navy", border: "", shadow: "" },
};

const sizeStyles: Record<ButtonSize, string> = {
  sm: "px-4 py-2.5 rounded-sm",
  md: "px-6 py-3.5 rounded-default",
  lg: "px-8 py-4 rounded-default",
  xl: "px-10 py-5 rounded-lg",
};

const textSizes: Record<ButtonSize, string> = {
  sm: "text-sm", md: "text-base", lg: "text-lg", xl: "text-xl",
};

export const Button: React.FC<ButtonProps> = ({
  variant = "primary", size = "md", loading = false, disabled = false,
  leftIcon, rightIcon, style, textStyle, children, ...rest
}) => {
  const cfg = variantConfig[variant];
  const isDisabled = disabled || loading;

  const classes = [
    "flex-row items-center justify-center", cfg.bg, cfg.text,
    sizeStyles[size], cfg.border, cfg.shadow,
    isDisabled ? "opacity-50" : "active:opacity-90 active:scale-[0.98]",
  ].filter(Boolean).join(" ");

  const textClasses = ["font-bold text-center tracking-wide", textSizes[size]].join(" ");

  const indicatorColor = variant === "ghost" || variant === "outline" || variant === "subtle" ? "#1E2761" : "#FFFFFF";

  return (
    <TouchableOpacity
      className={classes}
      disabled={isDisabled}
      activeOpacity={0.85}
      style={style}
      {...rest}
    >
      {loading ? (
        <ActivityIndicator size="small" color={indicatorColor} style={{ marginRight: 8 }} />
      ) : leftIcon ? <>{leftIcon}</> : null}
      <Text className={textClasses} style={textStyle}>{children}</Text>
      {rightIcon && !loading && <>{rightIcon}</>}
    </TouchableOpacity>
  );
};

export default Button;
