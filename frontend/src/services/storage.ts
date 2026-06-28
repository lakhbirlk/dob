import { Platform } from "react-native";

/**
 * Platform-adaptive secure storage.
 * - Native (iOS/Android): expo-secure-store (Keychain / Keystore)
 * - Web: localStorage (best available on web; not truly "secure")
 */

const isWeb = Platform.OS === "web";

async function getItemAsync(key: string): Promise<string | null> {
  if (isWeb) {
    return localStorage.getItem(key);
  }
  // Dynamic import avoids loading native module on web
  const SecureStore = require("expo-secure-store");
  return SecureStore.getItemAsync(key);
}

async function setItemAsync(key: string, value: string): Promise<void> {
  if (isWeb) {
    localStorage.setItem(key, value);
    return;
  }
  const SecureStore = require("expo-secure-store");
  return SecureStore.setItemAsync(key, value);
}

async function deleteItemAsync(key: string): Promise<void> {
  if (isWeb) {
    localStorage.removeItem(key);
    return;
  }
  const SecureStore = require("expo-secure-store");
  return SecureStore.deleteItemAsync(key);
}

export const storage = {
  getItemAsync,
  setItemAsync,
  deleteItemAsync,
};
