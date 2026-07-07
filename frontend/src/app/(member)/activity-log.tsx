import { useEffect, useState, useCallback } from "react";
import { View, Text, FlatList, TouchableOpacity, ActivityIndicator, TextInput } from "react-native";
import { Card } from "@/components/Card";
import { Badge } from "@/components/Badge";
import { EmptyState } from "@/components/EmptyState";
import { unlockApi } from "@/services/api";
import type { ActivityEntry, ActivityCategory } from "@/types";

// ─────────────────────── Constants ───────────────────────

const DATE_FILTERS = [
  { key: "ALL", label: "All" },
  { key: "TODAY", label: "Today" },
  { key: "7D", label: "7 Days" },
  { key: "30D", label: "30 Days" },
  { key: "90D", label: "90 Days" },
] as const;

const CATEGORY_FILTERS: { key: ActivityCategory; icon: string; label: string }[] = [
  { key: "ALL", icon: "📋", label: "All" },
  { key: "COMPANY", icon: "🏢", label: "Company" },
  { key: "CREDITS", icon: "💳", label: "Credits" },
  { key: "SUBSCRIPTION", icon: "📦", label: "Subscription" },
  { key: "AUTH", icon: "🔐", label: "Auth" },
  { key: "SEARCH", icon: "🔍", label: "Search" },
  { key: "DOWNLOADS", icon: "📥", label: "Downloads" },
];

const ACTIVITY_ICONS: Record<string, string> = {
  COMPANY_UNLOCK: "🔓",
  COMPANY_VIEWED: "👁️",
  COMPANY_BOOKMARKED: "🔖",
  COMPANY_DOWNLOADED: "📄",
  COMPANY_EXPORTED: "📊",
  COMPANY_SHARED: "📤",
  COMPANY_WATCHLIST_ADDED: "⭐",
  COMPANY_WATCHLIST_REMOVED: "⨯",
  COMPANY_UNBOOKMARKED: "🚫",
  COMPANY_REMOVED_BOOKMARK: "🚫",
  COMPANY_SEARCHED: "🔍",
  CREDITS_DEDUCTED: "➖",
  CREDITS_REFUNDED: "💰",
  CREDITS_ADDED: "➕",
  PLAN_UPGRADED: "⬆️",
  PLAN_RENEWED: "🔄",
  LOGIN: "🔑",
  LOGOUT: "🚪",
  PASSWORD_CHANGED: "🔒",
  EMAIL_UPDATED: "✉️",
  PROFILE_UPDATED: "👤",
  SUBSCRIPTION_PURCHASED: "🛒",
  SUBSCRIPTION_RENEWED: "🔄",
  SUBSCRIPTION_EXPIRED: "⏰",
  FILTERS_APPLIED: "🔎",
  SEARCH_SAVED: "💾",
  SUPPORT_TICKET: "🎫",
  FEEDBACK_SUBMITTED: "💬",
  NOTIFICATION_READ: "🔔",
};

function getActivityIcon(action: string | null): string {
  if (!action) return "📋";
  return ACTIVITY_ICONS[action] ?? "📋";
}

function getActivityColor(category: string): string {
  switch (category) {
    case "COMPANY": return "bg-blue/10";
    case "CREDITS": return "bg-gold/10";
    case "AUTH": return "bg-teal/10";
    case "SUBSCRIPTION": return "bg-purple-500/10";
    case "SEARCH": return "bg-navy/10";
    case "DOWNLOADS": return "bg-green/10";
    default: return "bg-faint/10";
  }
}

// ─────────────────────── Helpers ───────────────────────

function getDateRange(filterKey: string): { dateFrom?: string; dateTo?: string } {
  if (filterKey === "ALL") return {};
  const now = new Date();
  const endOfDay = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 23, 59, 59, 999);
  const dateTo = endOfDay.toISOString();

  let start: Date;
  switch (filterKey) {
    case "TODAY":
      start = new Date(now.getFullYear(), now.getMonth(), now.getDate());
      return { dateFrom: start.toISOString(), dateTo };
    case "7D":
      start = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
      return { dateFrom: start.toISOString(), dateTo };
    case "30D":
      start = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000);
      return { dateFrom: start.toISOString(), dateTo };
    case "90D":
      start = new Date(now.getTime() - 90 * 24 * 60 * 60 * 1000);
      return { dateFrom: start.toISOString(), dateTo };
    default:
      return {};
  }
}

function formatTimestamp(ts: string): { date: string; time: string } {
  const d = new Date(ts);
  const date = d.toLocaleDateString("en-IN", { day: "numeric", month: "short", year: "numeric" });
  const time = d.toLocaleTimeString("en-IN", { hour: "2-digit", minute: "2-digit" });
  return { date, time };
}

// ─────────────────────── Component ───────────────────────

export default function ActivityLogScreen() {
  const [data, setData] = useState<ActivityEntry[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [error, setError] = useState<string | null>(null);

  // Filters
  const [dateFilter, setDateFilter] = useState<string>("ALL");
  const [categoryFilter, setCategoryFilter] = useState<ActivityCategory>("ALL");
  const [searchText, setSearchText] = useState("");
  const [searchQuery, setSearchQuery] = useState("");

  const buildParams = useCallback((pageNum: number, cat: ActivityCategory, date: string, search: string) => {
    const dateRange = getDateRange(date);
    return {
      category: cat,
      search: search || undefined,
      page: pageNum,
      size: 20,
      ...dateRange,
    };
  }, []);

  const fetch = useCallback(async (
    pageNum = 0,
    isRefresh = false,
    cat = categoryFilter,
    date = dateFilter,
    search = searchQuery
  ) => {
    if (isRefresh) setRefreshing(true);
    else if (pageNum === 0) setLoading(true);
    setError(null);

    try {
      const params = buildParams(pageNum, cat, date, search);
      const result = await unlockApi.getActivityLog(params);
      if (pageNum === 0) {
        setData(result.data);
      } else {
        setData((prev) => [...prev, ...result.data]);
      }
      setPage(pageNum);
      setTotalPages(result.pagination.totalPages);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load activity log");
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, [categoryFilter, dateFilter, searchQuery, buildParams]);

  useEffect(() => { fetch(0); }, [fetch]);

  const handleFilterChange = (cat: ActivityCategory, date: string) => {
    setCategoryFilter(cat);
    setDateFilter(date);
    setPage(0);
    // Fetch will be triggered by the effect
  };

  const handleSearch = () => {
    setSearchQuery(searchText);
    setPage(0);
  };

  const handleLoadMore = () => {
    if (page < totalPages - 1 && !loading) {
      fetch(page + 1);
    }
  };

  // ──────────── Render Item ────────────

  const renderItem = ({ item }: { item: ActivityEntry }) => {
    const { date, time } = formatTimestamp(item.timestamp);
    const isSuccess = item.status === "SUCCESS";
    const icon = getActivityIcon(item.activityType);
    const iconBg = getActivityColor(item.category);

    return (
      <Card variant="elevated" className="mb-2.5">
        <View className="flex-row items-start gap-x-3">
          {/* Icon */}
          <View className={`w-10 h-10 rounded-xl ${iconBg} items-center justify-center`}>
            <Text className="text-lg">{icon}</Text>
          </View>

          {/* Content */}
          <View className="flex-1">
            <View className="flex-row items-center gap-x-2">
              <Text className="text-sm font-bold text-ink flex-1">{item.description}</Text>
              <Badge variant={isSuccess ? "success" : "danger"}>
                <Text className="text-[10px] font-extrabold">{item.status}</Text>
              </Badge>
            </View>

            {/* Company name */}
            {item.companyName && (
              <Text className="text-xs text-muted mt-0.5 font-medium">{item.companyName}</Text>
            )}

            {/* Time & Credits row */}
            <View className="flex-row items-center justify-between mt-1.5">
              <View className="flex-row items-center gap-x-2">
                <Text className="text-[10px] text-faint">{date}</Text>
                <Text className="text-[10px] text-faint">{time}</Text>
              </View>
              {item.creditsUsed != null && (
                <Text className={`text-xs font-bold ${item.creditsUsed > 0 ? "text-red" : "text-green"}`}>
                  {item.creditsUsed > 0 ? `-${item.creditsUsed}` : `${item.creditsUsed}`} credits
                </Text>
              )}
            </View>
          </View>
        </View>
      </Card>
    );
  };

  // ──────────── Render ────────────

  return (
    <View className="flex-1 bg-bg">
      {/* Search Bar */}
      <View className="px-4 pt-3 pb-2">
        <View className="flex-row items-center gap-x-2 bg-white rounded-xl border border-line px-3 py-2">
          <Text className="text-faint text-lg">🔍</Text>
          <TextInput
            className="flex-1 text-sm text-ink"
            placeholder="Search by company or activity..."
            placeholderTextColor="#98A1B3"
            value={searchText}
            onChangeText={setSearchText}
            onSubmitEditing={handleSearch}
            returnKeyType="search"
          />
          {searchText.length > 0 && (
            <TouchableOpacity onPress={() => { setSearchText(""); setSearchQuery(""); }}>
              <Text className="text-faint text-lg">✕</Text>
            </TouchableOpacity>
          )}
        </View>
      </View>

      {/* Category Filters */}
      <View className="px-4 pb-1">
        <FlatList
          horizontal
          data={CATEGORY_FILTERS}
          keyExtractor={(f) => f.key}
          showsHorizontalScrollIndicator={false}
          contentContainerStyle={{ gap: 6 }}
          renderItem={({ item: f }) => (
            <TouchableOpacity
              onPress={() => handleFilterChange(f.key, dateFilter)}
              className={`flex-row items-center gap-x-1.5 px-3 py-1.5 rounded-full border ${
                categoryFilter === f.key
                  ? "bg-navy border-navy"
                  : "bg-white border-line"
              }`}
            >
              <Text className="text-xs">{f.icon}</Text>
              <Text className={`text-xs font-bold ${
                categoryFilter === f.key ? "text-white" : "text-ink"
              }`}>
                {f.label}
              </Text>
            </TouchableOpacity>
          )}
        />
      </View>

      {/* Date Filters */}
      <View className="px-4 pb-3">
        <FlatList
          horizontal
          data={DATE_FILTERS}
          keyExtractor={(f) => f.key}
          showsHorizontalScrollIndicator={false}
          contentContainerStyle={{ gap: 6 }}
          renderItem={({ item: f }) => (
            <TouchableOpacity
              onPress={() => handleFilterChange(categoryFilter, f.key)}
              className={`px-3 py-1.5 rounded-full border ${
                dateFilter === f.key
                  ? "bg-gold border-gold"
                  : "bg-white border-line"
              }`}
            >
              <Text className={`text-xs font-bold ${
                dateFilter === f.key ? "text-navy" : "text-muted"
              }`}>
                {f.label}
              </Text>
            </TouchableOpacity>
          )}
        />
      </View>

      {/* List */}
      {loading && data.length === 0 ? (
        <View className="flex-1 items-center justify-center">
          <ActivityIndicator size="large" color="#1E2761" />
          <Text className="text-muted text-sm mt-3">Loading activities...</Text>
        </View>
      ) : error && data.length === 0 ? (
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
          contentContainerStyle={{ paddingHorizontal: 16, paddingBottom: 32 }}
          ListEmptyComponent={
            <View className="items-center py-16">
              <Text className="text-5xl mb-4">📋</Text>
              <Text className="text-lg font-extrabold text-ink text-center mb-1">No activities found</Text>
              <Text className="text-sm text-muted text-center leading-5 px-8">
                {searchQuery || categoryFilter !== "ALL" || dateFilter !== "ALL"
                  ? "Try adjusting your filters or search query."
                  : "Your recent actions will appear here once you start using the platform."}
              </Text>
            </View>
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
