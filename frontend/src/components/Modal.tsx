import React from "react";
import {
  Modal as RNModal,
  View,
  Text,
  TouchableOpacity,
  ScrollView,
  Pressable,
  type StyleProp,
  type ViewStyle,
} from "react-native";

interface ModalProps {
  visible: boolean;
  onClose: () => void;
  title?: string;
  subtitle?: string;
  children: React.ReactNode;
  footer?: React.ReactNode;
  showCloseButton?: boolean;
  closeOnBackdrop?: boolean;
  scrollable?: boolean;
  style?: StyleProp<ViewStyle>;
  size?: "sm" | "md" | "lg";
}

const sizeStyles: Record<NonNullable<ModalProps["size"]>, string> = {
  sm: "w-[85%] max-w-[340px]",
  md: "w-[90%] max-w-[420px]",
  lg: "w-[95%] max-w-[600px]",
};

export const Modal: React.FC<ModalProps> = ({
  visible,
  onClose,
  title,
  subtitle,
  children,
  footer,
  showCloseButton = true,
  closeOnBackdrop = true,
  scrollable = false,
  style,
  size = "md",
}) => {
  const ContentWrapper = scrollable ? ScrollView : View;

  return (
    <RNModal
      visible={visible}
      transparent
      animationType="fade"
      onRequestClose={closeOnBackdrop ? onClose : undefined}
    >
      <Pressable
        className="flex-1 bg-black/50 justify-center items-center"
        onPress={closeOnBackdrop ? onClose : undefined}
      >
        <Pressable
          className={`bg-card rounded-default shadow-lg ${sizeStyles[size]}`}
          style={style}
          onPress={() => {
            /* prevent backdrop close when pressing modal content */
          }}
        >
          {/* Header */}
          {(title || showCloseButton) && (
            <View className="flex-row items-center justify-between px-6 pt-5 pb-2 border-b border-line">
              <View className="flex-1 mr-4">
                {title && (
                  <Text className="text-xl font-bold text-ink">{title}</Text>
                )}
                {subtitle && (
                  <Text className="text-sm text-muted mt-0.5">{subtitle}</Text>
                )}
              </View>
              {showCloseButton && (
                <TouchableOpacity
                  onPress={onClose}
                  className="w-8 h-8 rounded-full bg-bg items-center justify-center"
                >
                  <Text className="text-muted text-lg font-bold">&times;</Text>
                </TouchableOpacity>
              )}
            </View>
          )}

          {/* Content */}
          <ContentWrapper className="px-6 py-4" scrollEnabled={scrollable}>
            {children}
          </ContentWrapper>

          {/* Footer */}
          {footer && (
            <View className="px-6 py-4 border-t border-line">{footer}</View>
          )}
        </Pressable>
      </Pressable>
    </RNModal>
  );
};

export default Modal;
