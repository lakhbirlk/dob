import React from "react";
import { View, Text, type StyleProp, type ViewStyle } from "react-native";
import Button from "./Button";

interface EmptyStateProps {
  icon?: string;
  title: string;
  description?: string;
  actionLabel?: string;
  onAction?: () => void;
  style?: StyleProp<ViewStyle>;
}

export const EmptyState: React.FC<EmptyStateProps> = ({
  icon = "📭",
  title,
  description,
  actionLabel,
  onAction,
  style,
}) => {
  return (
    <View
      className="flex-1 items-center justify-center px-6 py-12"
      style={style}
    >
      <Text className="text-5xl mb-4">{icon}</Text>
      <Text className="text-lg font-semibold text-ink text-center mb-2">
        {title}
      </Text>
      {description && (
        <Text className="text-base text-muted text-center mb-6 leading-6">
          {description}
        </Text>
      )}
      {actionLabel && onAction && (
        <Button variant="primary" size="md" onPress={onAction}>
          {actionLabel}
        </Button>
      )}
    </View>
  );
};

export default EmptyState;
