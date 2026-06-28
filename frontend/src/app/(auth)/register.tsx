import {
  View, Text, ScrollView, TouchableOpacity,
} from "react-native";
import { Link, router } from "expo-router";
import { SafeAreaView } from "react-native-safe-area-context";

export default function RegisterTypeScreen() {
  return (
    <SafeAreaView className="flex-1 bg-bg" edges={["bottom"]}>
      <ScrollView className="flex-1" showsVerticalScrollIndicator={false}>
        {/* Brand Header */}
        <View className="bg-navy-deep pt-8 pb-10 px-6 items-center" style={{ borderBottomLeftRadius: 32, borderBottomRightRadius: 32 }}>
          <View className="flex-row items-center gap-x-3 mb-4">
            <View className="bg-navy px-3 py-1.5 rounded-lg">
              <Text className="text-gold font-extrabold text-lg tracking-wider">DoB</Text>
            </View>
            <Text className="text-white font-bold text-xl">DataOfBusiness</Text>
          </View>
        </View>

        <View className="px-5 -mt-6">
          {/* Title Card */}
          <View className="bg-card rounded-xl shadow-xl border border-line/30 px-6 py-7 mb-5">
            <Text className="text-2xl font-extrabold text-ink text-center mb-2">
              Create Your DoB Account
            </Text>
            <Text className="text-muted text-base text-center">
              Choose how you want to use the DataOfBusiness platform.
            </Text>
          </View>

          {/* Option 1: Register as Company */}
          <TouchableOpacity
            className="bg-card rounded-xl shadow-lg border border-line/30 px-6 py-6 mb-4 active:opacity-80"
            onPress={() => router.push("/(auth)/register-company")}
            activeOpacity={0.7}
          >
            <View className="flex-row items-center gap-x-4 mb-3">
              <View className="w-12 h-12 rounded-xl bg-navy items-center justify-center">
                <Text className="text-2xl">🏢</Text>
              </View>
              <View className="flex-1">
                <Text className="text-lg font-bold text-ink">Register as Company</Text>
                <Text className="text-sm text-muted">Add your business to our database</Text>
              </View>
              <Text className="text-faint text-xl">›</Text>
            </View>
            <View className="bg-bg rounded-lg px-4 py-3">
              {[
                "Register your company",
                "Submit business and financial information",
                "Manage company profile",
                "Maintain compliance records",
                "Update company information",
              ].map((item, i) => (
                <View key={i} className="flex-row items-start gap-x-2 py-0.5">
                  <Text className="text-teal text-sm">✓</Text>
                  <Text className="text-muted text-sm">{item}</Text>
                </View>
              ))}
            </View>
            <View className="mt-4 bg-navy rounded-xl py-3.5 items-center">
              <Text className="text-white font-bold text-base">Register as Company</Text>
            </View>
          </TouchableOpacity>

          {/* Option 2: Register as Research Member */}
          <TouchableOpacity
            className="bg-card rounded-xl shadow-lg border border-line/30 px-6 py-6 mb-5 active:opacity-80"
            onPress={() => router.push("/(auth)/register-research")}
            activeOpacity={0.7}
          >
            <View className="flex-row items-center gap-x-4 mb-3">
              <View className="w-12 h-12 rounded-xl bg-teal items-center justify-center">
                <Text className="text-2xl">🔍</Text>
              </View>
              <View className="flex-1">
                <Text className="text-lg font-bold text-ink">Register as Research Member</Text>
                <Text className="text-sm text-muted">Research companies and access data</Text>
              </View>
              <Text className="text-faint text-xl">›</Text>
            </View>
            <View className="bg-bg rounded-lg px-4 py-3">
              {[
                "Search company database",
                "Purchase premium reports",
                "Access research tools",
                "Save companies",
                "Create watchlists",
                "Receive platform updates",
              ].map((item, i) => (
                <View key={i} className="flex-row items-start gap-x-2 py-0.5">
                  <Text className="text-teal text-sm">✓</Text>
                  <Text className="text-muted text-sm">{item}</Text>
                </View>
              ))}
            </View>
            <View className="mt-4 bg-teal rounded-xl py-3.5 items-center">
              <Text className="text-white font-bold text-base">Register as Research Member</Text>
            </View>
          </TouchableOpacity>

          {/* Sign In Link */}
          <View className="flex-row justify-center mb-8">
            <Text className="text-muted text-base">Already have an account? </Text>
            <Link href="/(auth)/login" asChild>
              <TouchableOpacity>
                <Text className="text-navy font-bold text-base">Sign In</Text>
              </TouchableOpacity>
            </Link>
          </View>

          {/* Footer */}
          <Text className="text-faint text-xs text-center mb-6">
            🔒 Your data is encrypted and protected under India's DPDP Act 2023.
          </Text>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}
