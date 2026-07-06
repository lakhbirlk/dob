import React from "react";
import { View, Text, FlatList, TouchableOpacity, ActivityIndicator, TextInput } from "react-native";
import { router } from "expo-router";
import { Card } from "@/components/Card";
import { Badge } from "@/components/Badge";
import { EmptyState } from "@/components/EmptyState";
import { colors } from "@/theme/colors";
import { useQuery } from "@tanstack/react-query";
import { adminApi } from "@/services/api";

const MEMBER_STATUS_TABS = ["ALL", "ACTIVE", "EXPIRED", "INACTIVE"] as const;

export default function AdminMembersScreen() {
  const [activeTab, setActiveTab] = React.useState<string>("ALL");
  const [search, setSearch] = React.useState("");
  const [page, setPage] = React.useState(1);
  const PAGE_SIZE = 20;

  const { data, isLoading } = useQuery({
    queryKey: ["admin", "members", page],
    queryFn: async () => {
      const [membersRes, countRes] = await Promise.all([
        adminApi.getMembers(page - 1, PAGE_SIZE),
        adminApi.getMemberCount(),
      ]);
      return { members: membersRes.data ?? [], total: countRes.count ?? 0 };
    },
  });

  const members = data?.members ?? [];
  const total = data?.total ?? 0;
  const totalPages = Math.ceil(total / PAGE_SIZE);

  const filtered = members.filter((m: any) => {
    const nameMatch = m.fullName?.toLowerCase().includes(search.toLowerCase());
    const emailMatch = m.email?.toLowerCase().includes(search.toLowerCase());
    const matchesSearch = !search || nameMatch || emailMatch;

    if (!matchesSearch) return false;
    if (activeTab === "ALL") return true;
    if (activeTab === "ACTIVE") return m.membershipStatus === "ACTIVE";
    if (activeTab === "EXPIRED") return m.membershipStatus === "EXPIRED" || m.membershipStatus === "CANCELLED";
    if (activeTab === "INACTIVE") return !m.active || !m.membershipStatus;
    return true;
  });

  const renderMember = ({ item }: { item: any }) => {
    const statusColor = item.membershipStatus === "ACTIVE" ? colors.green
      : item.membershipStatus === "EXPIRED" ? colors.red
      : item.membershipStatus ? colors.goldDark
      : colors.faint;

    return (
      <TouchableOpacity
        onPress={() => router.push(`/(admin)/members/${item.id}` as any)}
        activeOpacity={0.8}
      >
        <Card variant="elevated" className="mx-4 mb-3">
          <View className="flex-row items-center gap-x-4">
            {/* Avatar */}
            <View className="w-12 h-12 rounded-full bg-navy/10 items-center justify-center">
              <Text className="text-xl">
                {item.fullName ? item.fullName.charAt(0).toUpperCase() : "?"}
              </Text>
            </View>

            {/* Info */}
            <View className="flex-1">
              <View className="flex-row items-center gap-x-2">
                <Text className="text-base font-bold text-ink" numberOfLines={1}>
                  {item.fullName || "Unnamed"}
                </Text>
                {!item.active && (
                  <Badge variant="danger">
                    <Text className="text-[10px] font-extrabold">INACTIVE</Text>
                  </Badge>
                )}
              </View>
              <Text className="text-xs text-muted mt-0.5">{item.email}</Text>
              {item.phone && (
                <Text className="text-xs text-faint mt-0.5">{item.phone}</Text>
              )}
            </View>

            {/* Status */}
            <View className="items-end">
              <View className="flex-row items-center gap-x-1.5">
                <View className="w-2 h-2 rounded-full" style={{ backgroundColor: statusColor }} />
                <Text className="text-xs font-bold" style={{ color: statusColor }}>
                  {item.membershipStatus || "NO PLAN"}
                </Text>
              </View>
              {item.planType && (
                <Text className="text-[10px] text-faint mt-1">{item.planType}</Text>
              )}
            </View>
          </View>

          {/* Membership details */}
          {item.membershipStatus && (
            <View className="flex-row gap-x-4 mt-3 pt-3 border-t border-line/50">
              <View className="flex-1">
                <Text className="text-[10px] text-faint font-semibold uppercase tracking-wider">Downloads</Text>
                <Text className="text-sm font-bold text-ink">{item.downloadsUsed}/{item.downloadLimit}</Text>
              </View>
              {item.membershipEndDate && (
                <View className="flex-1">
                  <Text className="text-[10px] text-faint font-semibold uppercase tracking-wider">Expires</Text>
                  <Text className="text-sm font-bold text-ink">
                    {new Date(item.membershipEndDate + "T00:00:00").toLocaleDateString("en-IN", {
                      day: "numeric", month: "short", year: "numeric",
                    })}
                  </Text>
                </View>
              )}
              <View className="flex-1">
                <Text className="text-[10px] text-faint font-semibold uppercase tracking-wider">Joined</Text>
                <Text className="text-sm font-bold text-ink">
                  {new Date(item.createdAt).toLocaleDateString("en-IN", {
                    day: "numeric", month: "short", year: "numeric",
                  })}
                </Text>
              </View>
            </View>
          )}
        </Card>
      </TouchableOpacity>
    );
  };

  return (
    <View className="flex-1 bg-bg">
      {/* Header */}
      <View className="px-4 pt-4 pb-2">
        <Text className="text-xl font-extrabold text-ink">Research Members</Text>
        <Text className="text-sm text-muted mt-1">{total} total members</Text>
      </View>

      {/* Search */}
      <View className="px-4 pb-3">
        <View className="flex-row items-center bg-white rounded-xl px-4 py-3 border border-line/60">
          <Text className="text-faint mr-2">🔍</Text>
          <TextInput
            className="flex-1 text-sm text-ink"
            placeholder="Search by name or email..."
            placeholderTextColor={colors.faint}
            value={search}
            onChangeText={setSearch}
          />
          {search ? (
            <TouchableOpacity onPress={() => setSearch("")}>
              <Text className="text-faint text-base">✕</Text>
            </TouchableOpacity>
          ) : null}
        </View>
      </View>

      {/* Tabs */}
      <View className="flex-row gap-2 px-4 pb-3">
        {MEMBER_STATUS_TABS.map((tab) => (
          <TouchableOpacity
            key={tab}
            onPress={() => setActiveTab(tab)}
            className={`px-3.5 py-1.5 rounded-full border ${
              activeTab === tab ? "bg-navy border-navy" : "bg-white border-line"
            }`}
          >
            <Text className={`text-xs font-bold ${activeTab === tab ? "text-white" : "text-ink"}`}>
              {tab === "ALL" ? "All" : tab.charAt(0) + tab.slice(1).toLowerCase()}
            </Text>
          </TouchableOpacity>
        ))}
      </View>

      {/* Members list */}
      {isLoading ? (
        <View className="flex-1 items-center justify-center">
          <ActivityIndicator size="large" color={colors.navy} />
        </View>
      ) : (
        <FlatList
          data={filtered}
          renderItem={renderMember}
          keyExtractor={(item: any) => item.id}
          contentContainerStyle={{ paddingBottom: 24 }}
          ListEmptyComponent={
            <EmptyState
              icon="👥"
              title={search ? "No matching members" : "No members found"}
              description={
                search
                  ? "Try a different search term"
                  : activeTab !== "ALL"
                  ? `No ${activeTab.toLowerCase()} members`
                  : "No research members have registered yet"
              }
            />
          }
          ListFooterComponent={
            totalPages > 1 ? (
              <View className="flex-row justify-center items-center gap-x-4 px-4 py-4">
                <TouchableOpacity
                  onPress={() => setPage((p) => Math.max(1, p - 1))}
                  disabled={page <= 1}
                  className={`px-4 py-2 rounded-lg ${page <= 1 ? "bg-line" : "bg-navy"}`}
                >
                  <Text className={`text-xs font-bold ${page <= 1 ? "text-faint" : "text-white"}`}>
                    ← Previous
                  </Text>
                </TouchableOpacity>
                <Text className="text-xs text-muted">
                  Page {page} of {totalPages}
                </Text>
                <TouchableOpacity
                  onPress={() => setPage((p) => Math.min(totalPages, p + 1))}
                  disabled={page >= totalPages}
                  className={`px-4 py-2 rounded-lg ${page >= totalPages ? "bg-line" : "bg-navy"}`}
                >
                  <Text className={`text-xs font-bold ${page >= totalPages ? "text-faint" : "text-white"}`}>
                    Next →
                  </Text>
                </TouchableOpacity>
              </View>
            ) : null
          }
        />
      )}
    </View>
  );
}
