import React from "react";
import { View, Text, ScrollView, TouchableOpacity } from "react-native";
import { router } from "expo-router";
import { Card } from "@/components/Card";
import { Button } from "@/components/Button";
import { Badge } from "@/components/Badge";
import { useAuthStore } from "@/store/authStore";
import { colors } from "@/theme/colors";

export default function MemberDashboard() {
  const { user } = useAuthStore();

  return (
    <ScrollView className="flex-1 bg-bg">
      {/* Header */}
      <View className="bg-navy-deep px-5 pt-10 pb-8" style={{ borderBottomLeftRadius: 28, borderBottomRightRadius: 28 }}>
        <View className="flex-row justify-between items-start">
          <View>
            <Text className="text-2xl font-extrabold text-white">Welcome back,</Text>
            <Text className="text-gold text-2xl font-extrabold">{user?.fullName ?? "Researcher"}</Text>
          </View>
          <Badge variant="gold"><Text className="text-xs font-extrabold text-navy">MEMBER</Text></Badge>
        </View>

        <View className="flex-row gap-x-4 mt-6">
          <View className="bg-white/10 rounded-xl px-4 py-3 flex-1">
            <Text className="text-xs text-faint font-semibold uppercase tracking-wide">Downloads Left</Text>
            <Text className="text-2xl font-extrabold text-white mt-1">42 / 50</Text>
          </View>
          <View className="bg-white/10 rounded-xl px-4 py-3 flex-1">
            <Text className="text-xs text-faint font-semibold uppercase tracking-wide">Membership</Text>
            <Text className="text-sm font-extrabold text-gold mt-1">ACTIVE</Text>
          </View>
        </View>
      </View>

      {/* Quick Actions */}
      <View className="px-5 mt-6 gap-y-4 pb-10">
        <Text className="text-lg font-extrabold text-ink">Quick Actions</Text>

        <TouchableOpacity onPress={() => router.push("/(authenticated)/companies" as any)} activeOpacity={0.9}>
          <Card variant="elevated" className="flex-row items-center gap-x-4">
            <View className="w-12 h-12 rounded-xl bg-blue/10 items-center justify-center">
              <Text className="text-2xl">🔍</Text>
            </View>
            <View className="flex-1">
              <Text className="text-base font-bold text-ink">Search Companies</Text>
              <Text className="text-sm text-muted mt-0.5">Browse 10,000+ verified companies</Text>
            </View>
            <Text className="text-faint text-lg">→</Text>
          </Card>
        </TouchableOpacity>

        <TouchableOpacity onPress={() => router.push("/(member)/downloads" as any)} activeOpacity={0.9}>
          <Card variant="elevated" className="flex-row items-center gap-x-4">
            <View className="w-12 h-12 rounded-xl bg-green/10 items-center justify-center">
              <Text className="text-2xl">📥</Text>
            </View>
            <View className="flex-1">
              <Text className="text-base font-bold text-ink">Download Center</Text>
              <Text className="text-sm text-muted mt-0.5">View your downloaded documents</Text>
            </View>
            <Text className="text-faint text-lg">→</Text>
          </Card>
        </TouchableOpacity>

        <TouchableOpacity onPress={() => router.push("/(member)/profile" as any)} activeOpacity={0.9}>
          <Card variant="elevated" className="flex-row items-center gap-x-4">
            <View className="w-12 h-12 rounded-xl bg-navy/10 items-center justify-center">
              <Text className="text-2xl">👤</Text>
            </View>
            <View className="flex-1">
              <Text className="text-base font-bold text-ink">My Profile</Text>
              <Text className="text-sm text-muted mt-0.5">Manage your account & PAN verification</Text>
            </View>
            <Text className="text-faint text-lg">→</Text>
          </Card>
        </TouchableOpacity>

        <Card variant="subtle" className="mt-4">
          <Text className="text-sm font-bold text-ink mb-2">Need more downloads?</Text>
          <Text className="text-sm text-muted mb-4">Your monthly download count resets on the 1st of each month. Upgrading increases your limit.</Text>
          <Button variant="gold" size="md" onPress={() => router.push("/(public)/pricing" as any)}>Upgrade Plan</Button>
        </Card>
      </View>
    </ScrollView>
  );
}
