import React from "react";
import { View, Text, FlatList, TouchableOpacity, ActivityIndicator } from "react-native";
import { Card } from "@/components/Card";
import { EmptyState } from "@/components/EmptyState";
import { Badge } from "@/components/Badge";
import { colors } from "@/theme/colors";
import { useQuery } from "@tanstack/react-query";
import { adminApi } from "@/services/api";
import type { Grievance } from "@/types";

const STATUS_TABS = ["ALL", "OPEN", "IN_PROGRESS", "RESOLVED"] as const;

const STATUS_COLORS: Record<string, string> = {
  OPEN: colors.navy,
  IN_PROGRESS: colors.gold,
  RESOLVED: colors.green,
};

const PRIORITY_BG: Record<string, string> = {
  LOW: "#F6F7FB",
  MEDIUM: "#FFFBF1",
  HIGH: "#FEF2F2",
  CRITICAL: "#FEF2F2",
};

const PRIORITY_TEXT: Record<string, string> = {
  LOW: "#5A6478",
  MEDIUM: "#C49A35",
  HIGH: "#B91C1C",
  CRITICAL: "#B91C1C",
};

export default function GrievancesManagementScreen() {
  const [activeTab, setActiveTab] = React.useState<string>("ALL");

  const { data, isLoading } = useQuery({
    queryKey: ["admin", "grievances", activeTab],
    queryFn: async () => {
      const response = await adminApi.getGrievances();
      return response;
    },
  });

  const grievances = data?.data ?? [];

  const filtered = activeTab === "ALL"
    ? grievances
    : grievances.filter((g: Grievance) => g.status === activeTab);

  return (
    <View className="flex-1 bg-bg">
      {/* Header */}
      <View className="px-4 pt-4 pb-2">
        <Text className="text-xl font-extrabold text-ink">Grievance Management</Text>
      </View>

      {/* Tabs */}
      <View className="flex-row gap-2 px-4 pb-3">
        {STATUS_TABS.map((tab) => (
          <TouchableOpacity
            key={tab}
            onPress={() => setActiveTab(tab)}
            className={`px-3.5 py-1.5 rounded-full border ${
              activeTab === tab
                ? "bg-navy border-navy"
                : "bg-white border-line"
            }`}
          >
            <Text
              className={`text-xs font-bold ${
                activeTab === tab ? "text-white" : "text-ink"
              }`}
            >
              {tab === "ALL" ? "All" : tab.replace("_", " ")}
            </Text>
          </TouchableOpacity>
        ))}
      </View>

      {/* List */}
      {isLoading ? (
        <View className="flex-1 items-center justify-center">
          <ActivityIndicator size="large" color={colors.navy} />
        </View>
      ) : (
        <FlatList
          data={filtered}
          renderItem={({ item }: { item: Grievance }) => (
            <Card variant="bordered" className="mx-4 mb-3 p-4">
              {/* Header row */}
              <View className="flex-row justify-between items-start mb-2">
                <View className="flex-1 mr-2">
                  <Text className="text-sm font-bold text-ink" numberOfLines={1}>
                    {item.subject}
                  </Text>
                  {item.companyId && (
                    <Text className="text-xs text-muted mt-0.5">
                      Company ID: {item.companyId}
                    </Text>
                  )}
                </View>
                <View
                  className="px-2.5 py-1 rounded-full"
                  style={{ backgroundColor: PRIORITY_BG[item.priority] }}
                >
                  <Text
                    className="text-[10px] font-bold"
                    style={{ color: PRIORITY_TEXT[item.priority] }}
                  >
                    {item.priority}
                  </Text>
                </View>
              </View>

              {/* Description */}
              <Text className="text-xs text-muted leading-5 mb-3" numberOfLines={2}>
                {item.description}
              </Text>

              {/* Footer */}
              <View className="flex-row justify-between items-center">
                <View className="flex-row items-center gap-x-2">
                  <Badge variant={item.status === "RESOLVED" ? "success" : item.status === "IN_PROGRESS" ? "warning" : "neutral"}>
                    {item.status.replace("_", " ")}
                  </Badge>
                </View>
                <Text className="text-xs text-faint">
                  {new Date(item.createdAt).toLocaleDateString("en-IN", {
                    day: "numeric",
                    month: "short",
                    year: "numeric",
                  })}
                </Text>
              </View>

              {/* Resolution (if resolved) */}
              {item.resolution && (
                <View className="mt-3 pt-3 border-t border-line/60">
                  <Text className="text-[11px] font-bold text-green mb-1">Resolution</Text>
                  <Text className="text-[11px] text-muted leading-5">{item.resolution}</Text>
                </View>
              )}

              {/* Assigned to */}
              {item.assignedTo && (
                <Text className="text-[10px] text-faint mt-2">
                  Assigned to: {item.assignedTo}
                </Text>
              )}
            </Card>
          )}
          keyExtractor={(item) => item.id}
          contentContainerStyle={{ paddingBottom: 24 }}
          ListEmptyComponent={
            <EmptyState
              icon="📝"
              title="No grievances"
              description={
                activeTab === "ALL"
                  ? "No grievances have been submitted yet"
                  : `No ${activeTab.toLowerCase().replace("_", " ")} grievances`
              }
            />
          }
        />
      )}
    </View>
  );
}
