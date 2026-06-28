import React from "react";
import { View, Text } from "react-native";

interface IconBoxProps {
  icon: string;
  bg?: string;
  size?: "sm" | "md" | "lg";
}

const sizeMap = { sm: "w-10 h-10 rounded-sm text-xl", md: "w-12 h-12 rounded-default text-2xl", lg: "w-16 h-16 rounded-lg text-3xl" };

export const IconBox: React.FC<IconBoxProps> = ({ icon, bg = "bg-navy/10", size = "md" }) => (
  <View className={`items-center justify-center ${sizeMap[size]} ${bg}`}>
    <Text>{icon}</Text>
  </View>
);

export default IconBox;
