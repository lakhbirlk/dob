import { useEffect, useState, useCallback } from "react";
import { View, Text, FlatList, ActivityIndicator } from "react-native";
import { router } from "expo-router";
import { Card } from "@/components/Card";
import { Badge } from "@/components/Badge";
import { EmptyState } from "@/components/EmptyState";
import { unlockApi } from "@/services/api";
import type { CreditTransactionItem } from "@/types";

export default function CreditHistoryScreen() {
  const [data, setData] = useState<CreditTransactionItem[]>([]);
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
      const result = await unlockApi.getCreditHistory(pageNum, 20);
      if (pageNum === 0) {
        setData(result.data);
      } else {
        setData((prev) => [...prev, ...result.data]);
      }
      setPage(pageNum);
      setTotalPages(result.pagination.totalPages);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load credit history");
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

  function formatDate(dateStr: string): string {
    const d = new Date(dateStr);
    return d.toLocaleDateString("en-IN", { day: "numeric", month: "short", year: "numeric", hour: "2-digit", minute: "2-digit" });
  }

  const renderItem = ({ item }: { item: CreditTransactionItem }) => {
    const isSuccess = item.status === "SUCCESS";
    return (
      <Card variant="elevated" className="mb-2.5">
        <View className="flex-row items-center gap-x-3">
          <View className={`w-10 h-10 rounded-full ${isSuccess ? "bg-green/10" : "bg-red/10"} items-center justify-center`}>
            <Text className="text-lg">{isSuccess ? "✅" : "❌"}</Text>
          </View>
          <View className="flex-1">
            <View className="flex-row items-center gap-x-2">
              <Text className="text-sm font-bold text-ink">
                {item.transactionType === "UNLOCK" ? "Company Unlock" : item.transactionType}
              </Text>
              <Badge variant={isSuccess ? "success" : "danger"}>
                <Text className="text-xs font-extrabold">{item.status}</Text>
              </Badge>
            </View>
            {item.companyName && (
              <Text className="text-xs text-muted mt-0.5">{item.companyName}</Text>
            )}
            <Text className="text-xs text-faint mt-0.5">{formatDate(item.createdAt)}</Text>
          </View>
          <View className="items-end">
            <Text className={`text-sm font-extrabold ${isSuccess ? "text-red" : "text-muted"}`}>
              -{item.creditsUsed}
            </Text>
            <Text className="text-[10px] text-faint mt-0.5">
              Balance: {item.balanceAfter}
            </Text>
          </View>
        </View>
        <View className="flex-row justify-between mt-2 pt-2 border-t border-line/40">
          <Text className="text-[10px] text-faint font-mono">ID: {item.transactionId}</Text>
          <Text className="text-[10px] text-faint">
            Before: {item.balanceBefore} → After: {item.balanceAfter}
          </Text>
        </View>
      </Card>
    );
  };

  if (loading && data.length === 0) {
    return (
      <View className="flex-1 bg-bg items-center justify-center">
        <ActivityIndicator size="large" color="#1E2761" />
        <Text className="text-muted text-sm mt-3">Loading credit history...</Text>
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
          keyExtractor={(item) => item.id}
          contentContainerStyle={{ padding: 16, paddingBottom: 32 }}
          ListEmptyComponent={
            <EmptyState
              icon="💳"
              title="No credit transactions yet"
              description="Your credit usage history will appear here"
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
