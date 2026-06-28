import React from "react";
import { View, TextInput, TouchableOpacity, Text } from "react-native";

interface SearchBarProps {
  value: string;
  onChangeText: (text: string) => void;
  onSubmit?: (text: string) => void;
  placeholder?: string;
  containerClassName?: string;
}

export const SearchBar: React.FC<SearchBarProps> = ({
  value, onChangeText, onSubmit, placeholder = "Search...", containerClassName = "",
}) => {
  return (
    <View className={`flex-row items-center bg-white rounded-xl shadow-lg border border-line/50 ${containerClassName}`}>
      <View className="pl-5 pr-2">
        <Text className="text-xl">🔍</Text>
      </View>
      <TextInput
        className="flex-1 py-4 text-base text-ink"
        value={value}
        onChangeText={onChangeText}
        placeholder={placeholder}
        placeholderTextColor="#98A1B3"
        returnKeyType="search"
        onSubmitEditing={() => onSubmit?.(value)}
      />
      {value.length > 0 && (
        <TouchableOpacity onPress={() => onChangeText("")} className="px-4 py-2">
          <Text className="text-faint text-lg font-bold">✕</Text>
        </TouchableOpacity>
      )}
      <TouchableOpacity
        onPress={() => onSubmit?.(value)}
        className="bg-gold rounded-lg px-5 py-3 mr-2"
        activeOpacity={0.8}
      >
        <Text className="text-navy font-bold text-sm">Search</Text>
      </TouchableOpacity>
    </View>
  );
};

export default SearchBar;
