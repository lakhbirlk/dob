import React from "react";
import { View, Text, FlatList, TouchableOpacity, ActivityIndicator, TextInput } from "react-native";
import { router } from "expo-router";
import { Card } from "@/components/Card";
import { Badge } from "@/components/Badge";
import { EmptyState } from "@/components/EmptyState";
import { colors } from "@/theme/colors";
import { useQuery } from "@tanstack/react-query";
import { adminApi } from "@/services/api";

const STATUS_TABS = ["ALL", "APPROVED", "PENDING", "SUSPENDED"] as const;

const STATUS_CONFIG: Record<string, { label: string; color: string; bg: string }> = {
  APPROVED: { label: "APPROVED", color: colors.green, bg: colors.greenLight },
  PENDING: { label: "PENDING", color: colors.goldDark, bg: colors.goldPale },
  REJECTED: { label: "REJECTED", color: colors.red, bg: colors.redLight },
  SUSPENDED: { label: "SUSPENDED", color: colors.red, bg: colors.redLight },
};

export default function AdminCompanyMembersScreen() {
  const [activeTab, setActiveTab] = React.useState<string>("ALL");
  const [search, setSearch] = React.useState("");
  const [page, setPage] = React.useState(1);
  const PAGE_SIZE = 20;

  const { data, isLoading } = useQuery({
    queryKey: ["admin", "company-members", page],
    queryFn: async () => {
      const [membersRes, countRes] = await Promise.all([
        adminApi.getCompanyMembers(page - 1, PAGE_SIZE),
        adminApi.getCompanyMemberCount(),
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
    const companyMatch = m.companyName?.toLowerCase().includes(search.toLowerCase());
    const matchesSearch = !search || nameMatch || emailMatch || companyMatch;

    if (!matchesSearch) return false;
    if (activeTab === "ALL") return true;
    return m.companyStatus === activeTab;
  });

  const renderMember = ({ item }: { item: any }) => {
    const statusCfg = STATUS_CONFIG[item.companyStatus] || STATUS_CONFIG.PENDING;

    return (
      <TouchableOpacity
        onPress={() => router.push(`/(admin)/company-members/${item.id}` as any)}
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
                  {item.companyName || "No company"}
                </Text>
                {!item.active && (
                  <Badge variant="danger">
                    <Text className="text-[10px] font-extrabold">INACTIVE</Text>
                  </Badge>
                )}
              </View>
              <Text className="text-xs text-muted mt-0.5">{item.fullName || "Unnamed"}</Text>
              <Text className="text-xs text-faint mt-0.5">{item.email}</Text>
            </View>

            {/* Status badge */}
            <View className="items-end">
              <View className="px-2.5 py-1 rounded-full" style={{ backgroundColor: statusCfg.bg }}>
                <Text className="text-[10px] font-bold" style={{ color: statusCfg.color }}>
                  {statusCfg.label}
                </Text>
              </View>
              {item.sector && (
                <Text className="text-[10px] text-faint mt-1">{item.sector}</Text>
              )}
            </View>
          </View>

          {/* Company details */}
          <View className="flex-row gap-x-4 mt-3 pt-3 border-t border-line/50">
            <View className="flex-1">
              <Text className="text-[10px] text-faint font-semibold uppercase tracking-wider">Type</Text>
              <Text className="text-sm font-bold text-ink">{item.companyType || "—"}</Text>
            </View>
            <View className="flex-1">
              <Text className="text-[10px] text-faint font-semibold uppercase tracking-wider">Location</Text>
              <Text className="text-sm font-bold text-ink">
                {[item.city, item.state].filter(Boolean).join(", ") || "—"}
              </Text>
            </View>
            <View className="flex-1">
              <Text className="text-[10px] text-faint font-semibold uppercase tracking-wider">Listings</Text>
              <Text className="text-sm font-bold text-ink">{item.totalCompanies}</Text>
            </View>
          </View>
        </Card>
      </TouchableOpacity>
    );
  };

  return (
    <View className="flex-1 bg-bg">
      {/* Header */}
      <View className="px-4 pt-4 pb-2">
        <Text className="text-xl font-extrabold text-ink">Company Members</Text>
        <Text className="text-sm text-muted mt-1">{total} total company accounts</Text>
      </View>

      {/* Search */}
      <View className="px-4 pb-3">
        <View className="flex-row items-center bg-white rounded-xl px-4 py-3 border border-line/60">
          <Text className="text-faint mr-2">🔍</Text>
          <TextInput
            className="flex-1 text-sm text-ink"
            placeholder="Search by name, email or company..."
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
        {STATUS_TABS.map((tab) => (
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

      {/* Company members list */}
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
              icon="🏢"
              title={search ? "No matching companies" : "No company members"}
              description={
                search
                  ? "Try a different search term"
                  : activeTab !== "ALL"
                  ? `No ${activeTab.toLowerCase()} company listings`
                  : "No company users have registered yet"
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
                <Text className="text-xs text-muted">Page {page} of {totalPages}</Text>
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
