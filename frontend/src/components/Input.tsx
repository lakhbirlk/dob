import React, { useState } from "react";
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  type TextInputProps,
  type StyleProp,
  type ViewStyle,
} from "react-native";

interface InputProps extends Omit<TextInputProps, "style"> {
  label?: string;
  error?: string;
  hint?: string;
  leftIcon?: React.ReactNode;
  rightIcon?: React.ReactNode;
  isPassword?: boolean;
  containerStyle?: StyleProp<ViewStyle>;
}

export const Input: React.FC<InputProps> = ({
  label,
  error,
  hint,
  leftIcon,
  rightIcon,
  isPassword = false,
  containerStyle,
  editable = true,
  ...rest
}) => {
  const [isFocused, setIsFocused] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  const borderColor = error
    ? "border-red"
    : isFocused
    ? "border-navy"
    : "border-line";

  return (
    <View className="mb-4" style={containerStyle}>
      {label && (
        <Text className="text-sm font-medium text-ink mb-1.5">{label}</Text>
      )}
      <View
        className={`flex-row items-center bg-card border rounded-default px-4 ${borderColor} ${
          !editable ? "bg-bg opacity-70" : ""
        }`}
      >
        {leftIcon && <View className="mr-3">{leftIcon}</View>}
        <TextInput
          className={`flex-1 py-3.5 text-base text-ink ${
            rest.multiline ? "min-h-[100px]" : ""
          }`}
          placeholderTextColor="#98A1B3"
          onFocus={() => setIsFocused(true)}
          onBlur={() => setIsFocused(false)}
          secureTextEntry={isPassword && !showPassword}
          editable={editable}
          {...rest}
        />
        {isPassword && (
          <TouchableOpacity
            onPress={() => setShowPassword(!showPassword)}
            className="ml-2 py-2"
          >
            <Text className="text-muted text-sm">
              {showPassword ? "Hide" : "Show"}
            </Text>
          </TouchableOpacity>
        )}
        {rightIcon && !isPassword && <View className="ml-3">{rightIcon}</View>}
      </View>
      {error && (
        <Text className="text-red text-xs mt-1 ml-1">{error}</Text>
      )}
      {hint && !error && (
        <Text className="text-faint text-xs mt-1 ml-1">{hint}</Text>
      )}
    </View>
  );
};

export default Input;
