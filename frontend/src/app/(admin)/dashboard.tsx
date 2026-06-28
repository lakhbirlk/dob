import React from "react";
import { View, Text, ScrollView, TouchableOpacity } from "react-native";
import { router } from "expo-router";
import { Card } from "@/components/Card";
import { Badge } from "@/components/Badge";
import { colors } from "@/theme/colors";

const MODULES = [
  { title: "Pending Approvals", icon: "📋", desc: "Review and approve company listings", count: "5", route: "/(admin)/pending-approvals", color: "#E8B84B" },
  { title: "Refund Management", icon: "💰", desc: "Process refund requests", count: "2", route: "/(admin)/refunds", color: "#DC2626" },
  { title: "Grievance Management", icon: "📝", desc: "Handle user grievances", count: "3", route: "/(admin)/grievances", color: "#2563EB" },
  { title: "Audit Logs", icon: "📊", desc: "View all admin actions", route: "/(admin)/audit-logs", color: "#5A6478" },
];

const STATS = [
  { label: "Total Users", value: "1,248", icon: "👥" },
  { label: "Total Companies", value: "10,450", icon: "🏢" },
  { label: "Active Members", value: "892", icon: "⭐" },
  { label: "Revenue (MTD)", value: "₹2.4L", icon: "📈" },
];

export default function AdminDashboard() {
  return (
    <ScrollView className="flex-1 bg-bg">
      <View className="bg-navy-deep px-5 pt-10 pb-8" style={{ borderBottomLeftRadius: 28, borderBottomRightRadius: 28 }}>
        <Text className="text-2xl font-extrabold text-white mb-1">Admin Panel</Text>
        <Text className="text-faint text-sm">Manage platform operations</Text>

        <View className="flex-row flex-wrap gap-3 mt-6">
          {STATS.map((s, i) => (
            <View key={i} className="bg-white/10 rounded-xl px-4 py-3 flex-1 min-w-[45%]">
              <Text className="text-2xl">{s.icon}</Text>
              <Text className="text-xl font-extrabold text-white mt-1">{s.value}</Text>
              <Text className="text-xs text-faint mt-0.5">{s.label}</Text>
            </View>
          ))}
        </View>
      </View>

      <View className="px-5 mt-6 gap-y-4 pb-10">
        <Text className="text-lg font-extrabold text-ink">Quick Actions</Text>
        {MODULES.map((m, i) => (
          <TouchableOpacity key={i} onPress={() => router.push(m.route as any)} activeOpacity={0.9}>
            <Card variant="elevated" className="flex-row items-center gap-x-4">
              <View className="w-12 h-12 rounded-xl items-center justify-center" style={{ backgroundColor: m.color + "15" }}>
                <Text className="text-2xl">{m.icon}</Text>
              </View>
              <View className="flex-1">
                <View className="flex-row items-center gap-x-2">
                  <Text className="text-base font-bold text-ink">{m.title}</Text>
                  {m.count && <Badge variant="warning"><Text className="text-xs font-extrabold">{m.count}</Text></Badge>}
                </View>
                <Text className="text-sm text-muted mt-0.5">{m.desc}</Text>
              </View>
              <Text className="text-faint text-lg">→</Text>
            </Card>
          </TouchableOpacity>
        ))}
      </View>
    </ScrollView>
  );
}
