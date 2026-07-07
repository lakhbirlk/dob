import { useEffect, useState, useCallback } from "react";
import { View, Text, ScrollView, TouchableOpacity } from "react-native";
import { router } from "expo-router";
import { Card } from "@/components/Card";
import { Button } from "@/components/Button";
import { Badge } from "@/components/Badge";
import { useAuthStore } from "@/store/authStore";
import { membershipsApi, unlockApi } from "@/services/api";
import type { CreditPlan, CreditSummary, ActivityEntry } from "@/types";

// ─────────────────────── Helpers ───────────────────────

function formatDate(dateStr: string | null | undefined): string {
  if (!dateStr) return "—";
  const d = new Date(dateStr);
  return d.toLocaleDateString("en-IN", { day: "numeric", month: "short", year: "numeric" });
}

function getUsageColor(pct: number): string {
  if (pct >= 85) return "#DC2626"; // red
  if (pct >= 60) return "#E8B84B"; // gold
  return "#16A34A";                 // green
}

function getUsageLabel(pct: number): string {
  if (pct >= 100) return "Exhausted";
  if (pct >= 85) return "Almost gone";
  if (pct >= 60) return "Running low";
  return "Good";
}

const PLAN_META: Record<string, { icon: string }> = {
  GUEST:     { icon: "🎁" },
  CREDITS_3:  { icon: "🌱" },
  CREDITS_5:  { icon: "🚀" },
  CREDITS_10: { icon: "💎" },
  CREDITS_20: { icon: "🏆" },
  CREDITS_30: { icon: "👑" },
};

const FALLBACK_PLANS: CreditPlan[] = [
  { id: "CREDITS_3",  name: "Starter",    credits: 3,  amount: 1500, gst: 270, total: 1770, duration: "MONTHLY" },
  { id: "CREDITS_5",  name: "Basic",      credits: 5,  amount: 2000, gst: 360, total: 2360, duration: "MONTHLY" },
  { id: "CREDITS_10", name: "Pro",        credits: 10, amount: 3000, gst: 540, total: 3540, duration: "MONTHLY" },
  { id: "CREDITS_20", name: "Business",   credits: 20, amount: 4000, gst: 720, total: 4720, duration: "MONTHLY" },
  { id: "CREDITS_30", name: "Enterprise", credits: 30, amount: 5000, gst: 900, total: 5900, duration: "MONTHLY" },
];

const FALLBACK_GUEST: CreditPlan = { id: "GUEST", name: "Guest", credits: 2, amount: 0, gst: 0, total: 0, duration: "ONETIME" };

// ─────────────────────── Subscription Data Type ───────────────────────

interface SubscriptionData {
  planType: string;
  planName: string;
  status: string;
  totalCredits: number;
  creditsUsed: number;
  remainingCredits: number;
  renewalDate: string | null;
  startDate: string | null;
  isGuest: boolean;
  isExpired: boolean;
  isTopPlan: boolean;
}

// ─────────────────────── Component ───────────────────────

export default function MemberDashboard() {
  const { user } = useAuthStore();

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [subscription, setSubscription] = useState<SubscriptionData | null>(null);
  const [nextPlan, setNextPlan] = useState<CreditPlan | null>(null);
  const [creditSummary, setCreditSummary] = useState<CreditSummary | null>(null);
  const [recentActivities, setRecentActivities] = useState<ActivityEntry[]>([]);

  // ──────── Fetch Data ────────

  const fetchData = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const [membershipData, plansData, summaryData, activityData] = await Promise.all([
        membershipsApi.getMyMembership(),
        membershipsApi.getPlans(),
        unlockApi.getCreditSummary().catch(() => null),
        unlockApi.getRecentActivities().catch(() => [] as ActivityEntry[]),
      ]);

      setCreditSummary(summaryData);
      setRecentActivities(activityData);

      const rawMembership = membershipData as unknown as Record<string, unknown> | null;
      const creditPlans: CreditPlan[] = (plansData?.creditPlans ?? FALLBACK_PLANS) as CreditPlan[];
      const guestPlan: CreditPlan = (plansData?.guestPlan ?? FALLBACK_GUEST) as CreditPlan;

      if (!rawMembership) {
        // No membership at all
        setSubscription(null);
        setNextPlan(creditPlans[0] ?? null);
        setLoading(false);
        return;
      }

      const planType = (rawMembership.planType as string) ?? "";
      const status = (rawMembership.status as string) ?? "ACTIVE";
      const totalCredits = (rawMembership.downloadLimit as number) ?? 0;
      const creditsUsed = (rawMembership.downloadsUsed as number) ?? 0;
      const remainingCredits = Math.max(0, totalCredits - creditsUsed);
      const startDate = (rawMembership.startDate as string) ?? null;
      const endDate = (rawMembership.endDate as string) ?? null;
      const isGuest = planType === "GUEST";

      // Resolve plan name
      let planName: string;
      if (isGuest) {
        planName = guestPlan.name;
      } else {
        const found = creditPlans.find((p) => p.id === planType);
        planName = found?.name ?? planType;
      }

      // Next higher plan (skip GUEST)
      const next = creditPlans.find((p) => p.credits > totalCredits);
      setNextPlan(next ?? null);

      const isTopPlan = !isGuest && !next;

      setSubscription({
        planType,
        planName,
        status,
        totalCredits,
        creditsUsed,
        remainingCredits,
        renewalDate: endDate,
        startDate,
        isGuest,
        isExpired: status === "EXPIRED",
        isTopPlan,
      });
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : "Failed to load subscription data";
      setError(msg);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  // ──────── Derived Values ────────

  const usagePct = subscription && subscription.totalCredits > 0
    ? Math.min(100, Math.round((subscription.creditsUsed / subscription.totalCredits) * 100))
    : 0;
  const usageColor = getUsageColor(usagePct);
  const usageLabel = getUsageLabel(usagePct);
  const planIcon = PLAN_META[subscription?.planType ?? ""]?.icon ?? "📊";

  // ──────── Loading State ────────

  if (loading) {
    return (
      <ScrollView className="flex-1 bg-bg">
        {/* Header skeleton */}
        <View className="bg-navy-deep px-5 pt-10 pb-8" style={{ borderBottomLeftRadius: 28, borderBottomRightRadius: 28 }}>
          <View className="mb-2">
            <View className="h-5 w-32 bg-white/20 rounded mb-1" />
            <View className="h-7 w-44 bg-white/20 rounded" />
          </View>
          {/* Card skeleton */}
          <View className="bg-white/10 rounded-2xl p-5 mt-6 gap-y-4">
            <View className="h-5 w-28 bg-white/10 rounded" />
            <View className="h-4 w-36 bg-white/10 rounded" />
            <View className="h-px bg-white/10 my-1" />
            <View className="flex-row justify-between">
              <View className="h-4 w-24 bg-white/10 rounded" />
              <View className="h-4 w-16 bg-white/10 rounded" />
            </View>
            <View className="flex-row justify-between">
              <View className="h-4 w-24 bg-white/10 rounded" />
              <View className="h-4 w-16 bg-white/10 rounded" />
            </View>
            <View className="flex-row justify-between">
              <View className="h-4 w-28 bg-white/10 rounded" />
              <View className="h-4 w-16 bg-white/10 rounded" />
            </View>
            <View className="h-2.5 bg-white/10 rounded-full" />
          </View>
        </View>
        {/* Quick actions skeleton */}
        <View className="px-5 mt-6 gap-y-4 pb-10">
          <View className="h-5 w-28 bg-faint/20 rounded" />
          {[1, 2, 3].map((i) => (
            <View key={i} className="h-16 bg-card rounded-xl border border-line/30" />
          ))}
        </View>
      </ScrollView>
    );
  }

  // ──────── Error State ────────

  if (error && !subscription) {
    return (
      <ScrollView className="flex-1 bg-bg">
        <View className="bg-navy-deep px-5 pt-10 pb-12" style={{ borderBottomLeftRadius: 28, borderBottomRightRadius: 28 }}>
          <View className="flex-row justify-between items-start">
            <View>
              <Text className="text-2xl font-extrabold text-white">Welcome back,</Text>
              <Text className="text-gold text-2xl font-extrabold">{user?.fullName ?? "Researcher"}</Text>
            </View>
          </View>
        </View>
        <View className="px-5 -mt-6">
          <Card variant="elevated" className="items-center py-8 px-6">
            <Text className="text-4xl mb-4">⚠️</Text>
            <Text className="text-base font-extrabold text-ink text-center mb-1">Something went wrong</Text>
            <Text className="text-sm text-muted text-center mb-5 leading-5">{error}</Text>
            <Button variant="primary" size="md" onPress={fetchData}>
              Retry
            </Button>
          </Card>
        </View>
      </ScrollView>
    );
  }

  // ──────── Empty State (No Membership) ────────

  if (!subscription) {
    return (
      <ScrollView className="flex-1 bg-bg">
        <View className="bg-navy-deep px-5 pt-10 pb-12" style={{ borderBottomLeftRadius: 28, borderBottomRightRadius: 28 }}>
          <View className="flex-row justify-between items-start">
            <View>
              <Text className="text-2xl font-extrabold text-white">Welcome back,</Text>
              <Text className="text-gold text-2xl font-extrabold">{user?.fullName ?? "Researcher"}</Text>
            </View>
          </View>
        </View>
        <View className="px-5 -mt-6">
          <Card variant="elevated" className="items-center py-8 px-6">
            <Text className="text-5xl mb-4">🔍</Text>
            <Text className="text-lg font-extrabold text-ink text-center mb-1">No Active Subscription</Text>
            <Text className="text-sm text-muted text-center mb-6 leading-5">
              No active subscription found.{'\n'}Upgrade your plan to access research features.
            </Text>
            <Button variant="gold" size="lg" onPress={() => router.push("/(public)/pricing" as any)}>
              View Plans
            </Button>
          </Card>
        </View>

        {/* Quick Actions */}
        <View className="px-5 mt-6 gap-y-4 pb-10">
          <Text className="text-lg font-extrabold text-ink">Quick Actions</Text>
          <QuickActionButton icon="🔍" title="Search Companies" subtitle="Browse 10,000+ verified companies" onPress={() => router.push("/(authenticated)/companies" as any)} />
          <QuickActionButton icon="👤" title="My Profile" subtitle="Manage your account & PAN verification" onPress={() => router.push("/(member)/profile" as any)} />
        </View>
      </ScrollView>
    );
  }

  // ──────── Main Dashboard ────────

  return (
    <ScrollView className="flex-1 bg-bg">
      {/* ──────── HEADER ──────── */}
      <View className="bg-navy-deep px-5 pt-10 pb-30" style={{ borderBottomLeftRadius: 28, borderBottomRightRadius: 28 }}>
        <View className="flex-row justify-between items-start">
          <View>
            <Text className="text-2xl font-extrabold text-white">Welcome back,</Text>
            <Text className="text-gold text-2xl font-extrabold">{user?.fullName ?? "Researcher"}</Text>
          </View>
          <Badge variant={subscription.isGuest ? "info" : "gold"}>
            <Text className="text-xs font-extrabold text-navy">
              {subscription.isGuest ? "FREE" : subscription.status === "EXPIRED" ? "EXPIRED" : "MEMBER"}
            </Text>
          </Badge>
        </View>
      </View>

      {/* ──────── SUBSCRIPTION SUMMARY CARD ──────── */}
      <View className="px-5 -mt-28">

        {subscription.isGuest ? (
          /* ── GUEST / FREE CARD ── */
          <Card variant="elevated" className="overflow-hidden">
            <View className="bg-teal/10 px-5 py-4 flex-row items-center gap-x-3">
              <Text className="text-3xl">{planIcon}</Text>
              <View className="flex-1">
                <Text className="text-lg font-extrabold text-ink">Free Trial — {subscription.planName}</Text>
                <Text className="text-xs text-muted mt-0.5">
                  {subscription.remainingCredits > 0
                    ? `${subscription.remainingCredits} one-time credit${subscription.remainingCredits !== 1 ? "s" : ""} remaining`
                    : "All free credits used"}
                </Text>
              </View>
              <Badge variant="info">
                <Text className="text-xs font-bold">Trial</Text>
              </Badge>
            </View>

            {/* Credits */}
            <View className="px-5 py-4 gap-y-3">
              <CreditRow label="Total Credits" value={subscription.totalCredits} />
              <CreditRow label="Credits Used" value={subscription.creditsUsed} />
              <CreditRow label="Remaining Credits" value={subscription.remainingCredits} bold />
              <View className="mt-1">
                <ProgressBar pct={usagePct} color={usageColor} />
                <Text className="text-xs text-faint text-right mt-1">{usagePct}% Used</Text>
              </View>
            </View>

            {/* CTA */}
            <View className="px-5 pb-5">
              {subscription.remainingCredits === 0 ? (
                <Button variant="gold" size="md" onPress={() => router.push("/(public)/pricing" as any)}>
                  Subscribe to Continue
                </Button>
              ) : (
                <Button variant="gold" size="md" onPress={() => router.push("/(public)/pricing" as any)}>
                  Upgrade for More Credits
                </Button>
              )}
            </View>
          </Card>
        ) : (
          /* ── PAID PLAN CARD ── */
          <Card variant="elevated" className="overflow-hidden">
            {/* Plan Header */}
            <View className="bg-navy-2/10 px-5 py-4 flex-row items-center gap-x-3">
              <Text className="text-3xl">{planIcon}</Text>
              <View className="flex-1">
                <Text className="text-lg font-extrabold text-ink">{subscription.planName}</Text>
                <Text className="text-xs text-muted mt-0.5">
                  {subscription.isExpired
                    ? "Plan has expired"
                    : `Renews on ${formatDate(subscription.renewalDate)}`}
                </Text>
              </View>
              <StatusBadge status={subscription.status} />
            </View>

            {/* Credits Section */}
            <View className="px-5 pt-4 pb-2">
              <Text className="text-xs font-bold text-faint uppercase tracking-wider mb-3">Credits</Text>
              <View className="gap-y-3">
                <CreditRow label="Total Credits" value={subscription.totalCredits} />
                <CreditRow label="Credits Used" value={subscription.creditsUsed} />
                <CreditRow label="Remaining Credits" value={subscription.remainingCredits} bold />
              </View>
            </View>

            {/* Progress Bar */}
            <View className="px-5 py-3">
              <ProgressBar pct={usagePct} color={usageColor} />
              <View className="flex-row justify-between mt-1">
                <Text style={{ color: usageColor }} className="text-xs font-semibold">
                  {usageLabel}
                </Text>
                <Text className="text-xs text-faint">{usagePct}% Used</Text>
              </View>
            </View>

            {/* Upgrade / Top Plan message */}
            <View className="px-5 pb-5 pt-1">
              {subscription.isTopPlan ? (
                <View className="bg-gold/10 border border-gold/20 rounded-xl px-4 py-3 flex-row items-center gap-x-2">
                  <Text className="text-lg">🏆</Text>
                  <Text className="text-sm text-ink font-semibold flex-1">
                    You're on our top plan with {subscription.totalCredits} monthly credits.
                  </Text>
                </View>
              ) : nextPlan ? (
                <Button variant="gold" size="sm" onPress={() => router.push("/(public)/pricing" as any)}>
                  Upgrade to {nextPlan.name} — {nextPlan.credits} Credits
                </Button>
              ) : null}
            </View>

            {/* Expired banner */}
            {subscription.isExpired && (
              <View className="bg-red/10 px-5 py-3 border-t border-red/20">
                <View className="flex-row items-center gap-x-2">
                  <Text className="text-base">⚠️</Text>
                  <Text className="text-sm text-red font-semibold flex-1">Your subscription has expired.</Text>
                  <Button variant="primary" size="sm" onPress={() => router.push("/(public)/pricing" as any)}>
                    Renew
                  </Button>
                </View>
              </View>
            )}
          </Card>
        )}

        {/* ──────── ERROR BANNER (non-blocking) ──────── */}
        {error && (
          <View className="bg-red-light border border-red/30 rounded-xl px-4 py-3 mt-3 flex-row items-center gap-x-2">
            <Text className="text-base">⚠️</Text>
            <Text className="text-sm text-red flex-1">{error}</Text>
            <TouchableOpacity onPress={fetchData}>
              <Text className="text-red font-bold text-sm">Retry</Text>
            </TouchableOpacity>
          </View>
        )}
      </View>

      {/* ──────── RECENT ACTIVITIES ──────── */}
      {recentActivities.length > 0 && (
        <View className="px-5 mt-6">
          <View className="flex-row items-center justify-between mb-3">
            <Text className="text-lg font-extrabold text-ink">Recent Activities</Text>
            <TouchableOpacity onPress={() => router.push("/(member)/activity-log" as any)}>
              <Text className="text-sm text-navy font-bold">View All →</Text>
            </TouchableOpacity>
          </View>

          <Card variant="elevated" className="overflow-hidden">
            {recentActivities.slice(0, 5).map((activity, idx) => {
              const ts = new Date(activity.timestamp);
              const timeStr = ts.toLocaleTimeString("en-IN", { hour: "2-digit", minute: "2-digit" });
              const isSuccess = activity.status === "SUCCESS";
              return (
                <TouchableOpacity
                  key={activity.id}
                  onPress={() => router.push("/(member)/activity-log" as any)}
                  activeOpacity={0.7}
                >
                  <View className={`flex-row items-center gap-x-3 px-4 py-3 ${idx > 0 ? "border-t border-line/40" : ""}`}>
                    <View className={`w-8 h-8 rounded-lg ${
                      activity.category === "COMPANY" ? "bg-blue/10" :
                      activity.category === "CREDITS" ? "bg-gold/10" :
                      activity.category === "AUTH" ? "bg-teal/10" : "bg-faint/10"
                    } items-center justify-center`}>
                      <Text className="text-sm">{activity.activityType === "COMPANY_UNLOCK" ? "🔓" : "📋"}</Text>
                    </View>
                    <View className="flex-1">
                      <Text className="text-sm font-semibold text-ink" numberOfLines={1}>
                        {activity.description}
                      </Text>
                      <View className="flex-row items-center gap-x-2 mt-0.5">
                        {activity.companyName && (
                          <Text className="text-xs text-muted" numberOfLines={1}>{activity.companyName}</Text>
                        )}
                        <Text className="text-[10px] text-faint">{timeStr}</Text>
                      </View>
                    </View>
                    <View className="items-end">
                      {activity.creditsUsed != null && activity.creditsUsed > 0 && (
                        <Text className="text-xs font-bold text-red">-{activity.creditsUsed}</Text>
                      )}
                      <Badge variant={isSuccess ? "success" : "danger"}>
                        <Text className="text-[9px] font-extrabold">{activity.status}</Text>
                      </Badge>
                    </View>
                  </View>
                </TouchableOpacity>
              );
            })}
          </Card>
        </View>
      )}

      {/* ──────── QUICK ACTIONS ──────── */}
      <View className="px-5 mt-6 gap-y-4 pb-10">
        <Text className="text-lg font-extrabold text-ink">Quick Actions</Text>

        <QuickActionButton
          icon="🔍"
          title="Search Companies"
          subtitle="Browse 10,000+ verified companies"
          onPress={() => router.push("/(authenticated)/companies" as any)}
        />
        <QuickActionButton
          icon="🔓"
          title="Unlocked Companies"
          subtitle={creditSummary ? `${creditSummary.totalUnlocked} company${creditSummary.totalUnlocked !== 1 ? "ies" : "y"} unlocked` : "View unlocked companies"}
          onPress={() => router.push("/(member)/unlocked-companies" as any)}
        />
        <QuickActionButton
          icon="💳"
          title="Credit History"
          subtitle="View your credit usage and transactions"
          onPress={() => router.push("/(member)/credit-history" as any)}
        />
        <QuickActionButton
          icon="📋"
          title="Activity Log"
          subtitle="Audit trail of your account activity"
          onPress={() => router.push("/(member)/activity-log" as any)}
        />
        <QuickActionButton
          icon="📥"
          title="Download Center"
          subtitle="View your downloaded documents"
          onPress={() => router.push("/(member)/downloads" as any)}
        />
        <QuickActionButton
          icon="👤"
          title="My Profile"
          subtitle="Manage your account & PAN verification"
          onPress={() => router.push("/(member)/profile" as any)}
        />
      </View>
    </ScrollView>
  );
}

// ─────────────────────── Sub-Components ───────────────────────

function CreditRow({ label, value, bold = false }: { label: string; value: number; bold?: boolean }) {
  return (
    <View className="flex-row items-center justify-between">
      <Text className="text-sm text-muted">{label}</Text>
      <Text className={`text-sm ${bold ? "font-extrabold text-ink" : "font-semibold text-ink"}`}>
        {value.toLocaleString("en-IN")}
      </Text>
    </View>
  );
}

function ProgressBar({ pct, color }: { pct: number; color: string }) {
  return (
    <View className="h-2.5 bg-faint/20 rounded-full overflow-hidden">
      <View
        className="h-full rounded-full"
        style={{
          width: `${pct}%`,
          backgroundColor: color,
        }}
      />
    </View>
  );
}

function StatusBadge({ status }: { status: string }) {
  switch (status) {
    case "ACTIVE":
      return (
        <Badge variant="success">
          <Text className="text-xs font-bold">Active</Text>
        </Badge>
      );
    case "EXPIRED":
      return (
        <Badge variant="warning">
          <Text className="text-xs font-bold">Expired</Text>
        </Badge>
      );
    case "CANCELLED":
      return (
        <Badge variant="warning">
          <Text className="text-xs font-bold">Cancelled</Text>
        </Badge>
      );
    case "REFUNDED":
      return (
        <Badge variant="danger">
          <Text className="text-xs font-bold">Refunded</Text>
        </Badge>
      );
    default:
      return (
        <Badge variant="info">
          <Text className="text-xs font-bold">{status}</Text>
        </Badge>
      );
  }
}

function QuickActionButton({
  icon,
  title,
  subtitle,
  onPress,
}: {
  icon: string;
  title: string;
  subtitle: string;
  onPress: () => void;
}) {
  return (
    <TouchableOpacity onPress={onPress} activeOpacity={0.9}>
      <Card variant="elevated" className="flex-row items-center gap-x-4">
        <View className="w-12 h-12 rounded-xl bg-blue/10 items-center justify-center">
          <Text className="text-2xl">{icon}</Text>
        </View>
        <View className="flex-1">
          <Text className="text-base font-bold text-ink">{title}</Text>
          <Text className="text-sm text-muted mt-0.5">{subtitle}</Text>
        </View>
        <Text className="text-faint text-lg">→</Text>
      </Card>
    </TouchableOpacity>
  );
}
