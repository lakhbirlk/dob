import { useEffect, useState, useCallback } from "react";
import { View, Text, FlatList, TouchableOpacity, ActivityIndicator } from "react-native";
import { router } from "expo-router";
import { Card } from "@/components/Card";
import { Badge } from "@/components/Badge";
import { EmptyState } from "@/components/EmptyState";
import { unlockApi } from "@/services/api";
import type { UnlockedCompanyItem } from "@/types";

export default function UnlockedCompaniesScreen() {
  const [data, setData] = useState<UnlockedCompanyItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [error, setError] = useState<string | null>(null);

  const fetch = useCallback(async (pageNum = 0, isRefresh = false) => {
    if (isRefresh) setRefreshing(true);
    else if (pageNum === 0) setLoading(true);
    setError(null);

    try {
      const result = await unlockApi.getUnlockedCompanies(pageNum, 20);
      if (pageNum === 0) {
        setData(result.data);
      } else {
        setData((prev) => [...prev, ...result.data]);
      }
      setPage(pageNum);
      setTotalPages(result.pagination.totalPages);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load unlocked companies");
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, []);

  useEffect(() => { fetch(0); }, [fetch]);

  const handleLoadMore = () => {
    if (page < totalPages - 1 && !loading) {
      fetch(page + 1);
    }
  };

  const renderItem = ({ item }: { item: UnlockedCompanyItem }) => (
    <TouchableOpacity
      onPress={() => router.push(`/(authenticated)/company/${item.publicCompanyId ?? item.companyId}`)}
      activeOpacity={0.85}
    >
      <Card variant="elevated" className="mb-3">
        <View className="flex-row items-center gap-x-3">
          <View className="w-10 h-10 rounded-full bg-teal/10 items-center justify-center">
            <Text className="text-lg">🔓</Text>
          </View>
          <View className="flex-1">
            <Text className="text-base font-bold text-ink">{item.companyName}</Text>
            <Text className="text-xs text-faint mt-0.5">
              {[item.sector, item.city, item.state].filter(Boolean).join(" · ") || item.publicCompanyId}
            </Text>
          </View>
        </View>
        <View className="flex-row items-center justify-between mt-3 pt-3 border-t border-line/50">
          <View className="flex-row items-center gap-x-2">
            <Badge variant="success">
              <Text className="text-xs font-extrabold">{item.creditsUsed} Credit{item.creditsUsed !== 1 ? "s" : ""}</Text>
            </Badge>
          </View>
          <Text className="text-xs text-faint">
            Unlocked {new Date(item.unlockedAt).toLocaleDateString("en-IN", { day: "numeric", month: "short", year: "numeric" })}
          </Text>
        </View>
      </Card>
    </TouchableOpacity>
  );

  if (loading && data.length === 0) {
    return (
      <View className="flex-1 bg-bg items-center justify-center">
        <ActivityIndicator size="large" color="#1E2761" />
        <Text className="text-muted text-sm mt-3">Loading unlocked companies...</Text>
      </View>
    );
  }

  return (
    <View className="flex-1 bg-bg">
      {error && data.length === 0 ? (
        <EmptyState
          icon="⚠️"
          title="Could not load data"
          description={error}
          actionLabel="Retry"
          onAction={() => fetch(0)}
        />
      ) : (
        <FlatList
          data={data}
          renderItem={renderItem}
          keyExtractor={(item) => item.companyId}
          contentContainerStyle={{ padding: 16, paddingBottom: 32 }}
          ListEmptyComponent={
            <EmptyState
              icon="🔓"
              title="No unlocked companies yet"
              description="Companies you unlock with your credits will appear here"
              actionLabel="Browse Companies"
              onAction={() => router.push("/(authenticated)/companies" as any)}
            />
          }
          onRefresh={() => fetch(0, true)}
          refreshing={refreshing}
          onEndReached={handleLoadMore}
          onEndReachedThreshold={0.5}
          ListFooterComponent={
            loading && data.length > 0 ? (
              <ActivityIndicator size="small" color="#1E2761" style={{ padding: 16 }} />
            ) : null
          }
        />
      )}
    </View>
  );
}
