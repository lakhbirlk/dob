import React, { useState } from "react";
import { View, Text, TouchableOpacity, ActivityIndicator } from "react-native";

interface DocumentUploadProps {
  label: string;
  required?: boolean;
  accept?: string;
  onUpload?: (file: { name: string; uri: string; type: string }) => void;
}

export default function DocumentUpload({
  label,
  required = false,
  accept = ".pdf,.jpg,.jpeg,.png",
  onUpload,
}: DocumentUploadProps) {
  const [uploading, setUploading] = useState(false);
  const [uploaded, setUploaded] = useState<{ name: string } | null>(null);

  const handlePress = async () => {
    // In a real implementation, this would use expo-document-picker:
    //   const result = await DocumentPicker.getDocumentAsync({ type: accept });
    //   if (!result.canceled && result.assets[0]) {
    //     setUploaded({ name: result.assets[0].name });
    //     onUpload?.(result.assets[0]);
    //   }

    // Placeholder for now:
    setUploading(true);
    setTimeout(() => {
      setUploading(false);
      setUploaded({ name: `${label.replace(/\s+/g, "_")}.pdf` });
    }, 1000);
  };

  return (
    <View className="mb-4">
      <Text className="text-sm font-semibold text-ink mb-1.5">
        {label}
        {required && <Text className="text-red"> *</Text>}
      </Text>
      <TouchableOpacity
        className="border-2 border-dashed border-faint/50 rounded-xl py-4 px-4 items-center active:opacity-70"
        onPress={handlePress}
        disabled={uploading}
        activeOpacity={0.7}
      >
        {uploading ? (
          <ActivityIndicator size="small" color="#1E2761" />
        ) : uploaded ? (
          <View className="items-center">
            <Text className="text-2xl mb-1">📄</Text>
            <Text className="text-navy text-sm font-semibold">{uploaded.name}</Text>
            <TouchableOpacity
              onPress={() => setUploaded(null)}
              className="mt-1"
            >
              <Text className="text-red text-xs">Remove</Text>
            </TouchableOpacity>
          </View>
        ) : (
          <View className="items-center">
            <Text className="text-2xl mb-1">📎</Text>
            <Text className="text-muted text-sm">Tap to upload</Text>
            <Text className="text-faint text-xs mt-0.5">Supports {accept}</Text>
          </View>
        )}
      </TouchableOpacity>
    </View>
  );
}
