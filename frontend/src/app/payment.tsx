import React, { useState, useEffect, useCallback } from "react";
import {
  View, Text, ScrollView, ActivityIndicator, TouchableOpacity,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { router, useLocalSearchParams, Redirect } from "expo-router";
import { Button } from "@/components/Button";
import { Card } from "@/components/Card";
import { Badge } from "@/components/Badge";
import { useAuthStore } from "@/store/authStore";
import { subscriptionsApi, paymentsApi, membershipsApi } from "@/services/api";
import type { CreditPlan } from "@/types";

// ─────────────────────── Plan Metadata ───────────────────────

const PLAN_META: Record<string, { icon: string; color: string; badge: string; badgeText: string }> = {
  CREDITS_3:  { icon: "🌱", color: "bg-green", badge: "bg-white/20", badgeText: "text-white" },
  CREDITS_5:  { icon: "🚀", color: "bg-blue", badge: "bg-white/20", badgeText: "text-white" },
  CREDITS_10: { icon: "💎", color: "bg-gold", badge: "bg-navy", badgeText: "text-gold" },
  CREDITS_20: { icon: "🏆", color: "bg-navy-2", badge: "bg-white/20", badgeText: "text-white" },
  CREDITS_30: { icon: "👑", color: "bg-navy-deep", badge: "bg-gold", badgeText: "text-navy" },
  COMPANY:    { icon: "🏢", color: "bg-navy", badge: "bg-teal", badgeText: "text-white" },
};

const CREDIT_BENEFITS = [
  "Full Company Reports with Identifiers",
  "Company Names, CIN, GST, PAN Details",
  "CA-Certified Financial Statements",
  "Director & Shareholding Information",
  "Download Reports (PDF)",
  "AI-Powered Risk Analysis",
  "Premium Search & Filters",
  "Saved Companies & Watchlists",
  "Priority Email Support",
];

const COMPANY_BENEFITS = [
  "Company Profile Published",
  "Company Dashboard Access",
  "Financial Statement Upload",
  "CA Certificate Verification",
  "Business Analytics",
  "Profile Editing",
  "Company Visibility to Researchers",
  "Video Introduction",
];

// ─────────────────────── Plan details helper ───────────────────────

interface PlanDetail {
  title: string;
  icon: string;
  color: string;
  badge: string;
  badgeText: string;
  amount: number;
  gst: number;
  total: number;
  benefits: string[];
  dashboardRoute: string;
  periodLabel: string;
  credits?: number;
}

function buildPlanDetail(planId: string, plans: CreditPlan[]): PlanDetail | null {
  if (planId === "COMPANY") {
    return {
      title: "Company Listing",
      icon: "🏢",
      color: "bg-navy",
      badge: "bg-teal",
      badgeText: "text-white",
      amount: 500,
      gst: 90,
      total: 590,
      benefits: COMPANY_BENEFITS,
      dashboardRoute: "/(company)/dashboard",
      periodLabel: "/year",
    };
  }

  const plan = plans.find((p) => p.id === planId);
  if (!plan) return null;

  return {
    title: `${plan.name} — ${plan.credits} Credits`,
    icon: PLAN_META[planId]?.icon ?? "📊",
    color: PLAN_META[planId]?.color ?? "bg-navy",
    badge: PLAN_META[planId]?.badge ?? "bg-white/20",
    badgeText: PLAN_META[planId]?.badgeText ?? "text-white",
    amount: plan.amount,
    gst: plan.gst,
    total: plan.total,
    benefits: CREDIT_BENEFITS,
    dashboardRoute: "/(member)/dashboard",
    periodLabel: "/month",
    credits: plan.credits,
  };
}

type PaymentState = "idle" | "processing" | "success";

// ─────────────────────── Component ───────────────────────

export default function PaymentScreen() {
  const { isAuthenticated, isHydrated } = useAuthStore();
  const params = useLocalSearchParams<{ plan?: string }>();
  const planKey = params.plan?.toUpperCase() ?? "CREDITS_10";

  const [plans, setPlans] = useState<CreditPlan[]>([]);
  const [plansLoaded, setPlansLoaded] = useState(false);
  const [paymentState, setPaymentState] = useState<PaymentState>("idle");
  const [error, setError] = useState<string | null>(null);
  const [transactionId, setTransactionId] = useState<string | null>(null);

  // Load plans for pricing details
  useEffect(() => {
    membershipsApi.getPlans().then((data) => {
      setPlans(data.creditPlans ?? []);
    }).catch(() => {
      setPlans([
        { id: "CREDITS_3",  name: "Starter",    credits: 3,  amount: 1500, gst: 270, total: 1770, duration: "MONTHLY" },
        { id: "CREDITS_5",  name: "Basic",      credits: 5,  amount: 2000, gst: 360, total: 2360, duration: "MONTHLY" },
        { id: "CREDITS_10", name: "Pro",        credits: 10, amount: 3000, gst: 540, total: 3540, duration: "MONTHLY" },
        { id: "CREDITS_20", name: "Business",   credits: 20, amount: 4000, gst: 720, total: 4720, duration: "MONTHLY" },
        { id: "CREDITS_30", name: "Enterprise", credits: 30, amount: 5000, gst: 900, total: 5900, duration: "MONTHLY" },
      ]);
    }).finally(() => setPlansLoaded(true));
  }, []);

  // Derived value — computed before useCallback so it can be referenced, and before
  // conditional returns so hook ordering is stable across renders.
  const plan = buildPlanDetail(planKey, plans);

  // Dashboard route for post-payment redirect (derived from plan, used in callback deps)
  const dashboardRoute = plan?.dashboardRoute ?? "/(member)/dashboard";

  const handlePayNow = useCallback(async () => {
    setPaymentState("processing");
    setError(null);

    try {
      // Step 1: Create subscription on backend
      const sub = await subscriptionsApi.create(planKey);

      // Step 2: Simulate payment processing delay (1.5s)
      await new Promise((resolve) => setTimeout(resolve, 1500));

      // Step 3: Complete payment — activate membership
      const result = await paymentsApi.simulateSuccess(sub.subscriptionId);
      setTransactionId(result.transactionId);
      setPaymentState("success");

      // Step 4: Auto-redirect after 2 seconds
      setTimeout(() => {
        router.replace(dashboardRoute as any);
      }, 2000);
    } catch (err: unknown) {
      let message = "Payment failed. Please try again.";
      if (err instanceof Error) {
        // Extract backend error detail from axios response if available
        const axiosErr = (err as Record<string, unknown>).response as Record<string, unknown> | undefined;
        if (axiosErr?.data) {
          const data = axiosErr.data as Record<string, unknown>;
          if (typeof data.detail === "string") message = data.detail;
          else if (typeof data.message === "string") message = data.message;
        } else {
          message = err.message;
        }
      }
      setError(message);
      setPaymentState("idle");
    }
  }, [planKey, dashboardRoute]);

  // ──────────── ALL HOOKS ABOVE HERE — CONDITIONAL GUARDS BELOW ────────────

  // Guard: not authenticated → redirect to login
  if (isHydrated && !isAuthenticated) {
    return <Redirect href={`/(auth)/login?redirect=/payment&plan=${planKey}`} />;
  }

  // Guard: still hydrating or loading plans
  if (!isHydrated || !plansLoaded) {
    return (
      <View className="flex-1 items-center justify-center bg-bg">
        <ActivityIndicator size="large" color="#1E2761" />
      </View>
    );
  }

  // Guard: invalid plan (or GUEST which is not purchasable)
  if (!plan) {
    if (planKey === "GUEST") {
      return <Redirect href="/(public)/pricing" />;
    }
    return (
      <SafeAreaView className="flex-1 bg-bg" edges={["bottom"]}>
        <View className="flex-1 items-center justify-center px-6">
          <Text className="text-2xl font-extrabold text-ink mb-2">Invalid Plan</Text>
          <Text className="text-muted text-center mb-6">The selected plan "{planKey}" was not found.</Text>
          <Button variant="primary" onPress={() => router.back()}>Go Back</Button>
        </View>
      </SafeAreaView>
    );
  }

  // ──────────────────── SUCCESS STATE ────────────────────

  if (paymentState === "success") {
    return (
      <SafeAreaView className="flex-1 bg-bg" edges={["bottom"]}>
        <View className="flex-1 items-center justify-center px-6">
          <View className="w-20 h-20 rounded-full bg-green/10 items-center justify-center mb-6">
            <Text className="text-4xl">✓</Text>
          </View>
          <Text className="text-2xl font-extrabold text-ink text-center mb-2">
            Payment Successful
          </Text>
          <Text className="text-lg text-muted text-center mb-6">
            Your {plan.title} has been activated.
          </Text>

          <Card variant="elevated" className="w-full mb-6">
            <View className="gap-y-3">
              <View className="flex-row justify-between">
                <Text className="text-muted text-sm">Plan</Text>
                <Text className="text-ink font-semibold text-sm">
                  {plan.icon} {plan.title}
                </Text>
              </View>
              {plan.credits && (
                <View className="flex-row justify-between">
                  <Text className="text-muted text-sm">Credits</Text>
                  <Text className="text-navy font-extrabold text-sm">{plan.credits} / month</Text>
                </View>
              )}
              <View className="flex-row justify-between">
                <Text className="text-muted text-sm">Amount Paid</Text>
                <Text className="text-green font-bold text-sm">
                  ₹{plan.total.toLocaleString("en-IN")}
                </Text>
              </View>
              {transactionId && (
                <View className="flex-row justify-between">
                  <Text className="text-muted text-sm">Transaction ID</Text>
                  <Text className="text-ink font-mono text-xs">{transactionId}</Text>
                </View>
              )}
              <View className="flex-row justify-between">
                <Text className="text-muted text-sm">Status</Text>
                <View className="bg-green/10 px-2 py-0.5 rounded">
                  <Text className="text-green text-xs font-bold">ACTIVE</Text>
                </View>
              </View>
            </View>
          </Card>

          <Text className="text-faint text-sm text-center">
            Redirecting to your dashboard...
          </Text>
          <ActivityIndicator size="small" color="#98A1B3" style={{ marginTop: 12 }} />
        </View>
      </SafeAreaView>
    );
  }

  // ──────────────────── MAIN PAYMENT SCREEN ────────────────────

  return (
    <SafeAreaView className="flex-1 bg-bg" edges={["bottom"]}>
      <ScrollView className="flex-1" showsVerticalScrollIndicator={false}>
        {/* Header */}
        <View
          className={`${plan.color} pt-8 pb-10 px-6 items-center`}
          style={{ borderBottomLeftRadius: 32, borderBottomRightRadius: 32 }}
        >
          <Text className="text-5xl mb-3">{plan.icon}</Text>
          <Text className="text-white font-extrabold text-2xl mb-1">
            {plan.title}
          </Text>
          <View className={`${plan.badge} px-4 py-1 rounded-full`}>
            <Text className={`${plan.badgeText} text-sm font-bold`}>
              {plan.credits ? `${plan.credits} Credits / month` : "Annual Listing"}
            </Text>
          </View>
        </View>

        <View className="px-5 -mt-6">
          {/* Price Card */}
          <Card variant="elevated" className="mb-5">
            <View className="items-center py-2">
              <Text className="text-faint text-sm mb-1">Total Amount</Text>
              <View className="flex-row items-baseline">
                <Text className="text-5xl font-extrabold text-ink">
                  ₹{plan.total.toLocaleString("en-IN")}
                </Text>
                <Text className="text-muted text-base ml-1">{plan.periodLabel}</Text>
              </View>
              <Text className="text-faint text-xs mt-1">
                ₹{plan.amount.toLocaleString("en-IN")} + ₹{plan.gst} GST
              </Text>
              {plan.credits && (
                <View className="flex-row items-center gap-x-2 mt-2">
                  <Badge variant="success">
                    <Text className="text-xs font-extrabold">{plan.credits} Company Credits</Text>
                  </Badge>
                  <Badge variant="info">
                    <Text className="text-xs font-extrabold">Full Access</Text>
                  </Badge>
                </View>
              )}
            </View>
          </Card>

          {/* Benefits */}
          <Card variant="elevated" className="mb-5">
            <Text className="text-lg font-extrabold text-ink mb-4">
              What's Included
            </Text>
            <View className="gap-y-2.5">
              {plan.benefits.map((benefit, i) => (
                <View key={i} className="flex-row items-center gap-x-3">
                  <View className="w-6 h-6 rounded-full bg-teal/10 items-center justify-center">
                    <Text className="text-teal text-xs font-bold">✓</Text>
                  </View>
                  <Text className="text-ink text-base">{benefit}</Text>
                </View>
              ))}
            </View>
          </Card>

          {/* Error */}
          {error && (
            <View className="bg-red-light border border-red/30 rounded-xl px-4 py-3 mb-5 flex-row items-center justify-between">
              <Text className="text-red text-sm flex-1">{error}</Text>
              <TouchableOpacity onPress={() => setError(null)}>
                <Text className="text-red font-bold text-sm ml-3">✕</Text>
              </TouchableOpacity>
            </View>
          )}

          {/* Pay Now Button */}
          <Button
            variant={planKey.startsWith("CREDITS") ? "gold" : "primary"}
            size="xl"
            loading={paymentState === "processing"}
            onPress={handlePayNow}
            disabled={paymentState === "processing"}
            style={{ width: "100%", marginBottom: 16 }}
          >
            {paymentState === "processing"
              ? "Processing Payment..."
              : `Pay Now — ₹${plan.total.toLocaleString("en-IN")}`}
          </Button>

          {/* Info */}
          <Card variant="subtle" padding="sm" className="mb-8">
            <Text className="text-faint text-xs text-center leading-5">
              This is a simulated payment for demo purposes. No real payment will be charged.
              {"\n"}Your membership will be activated immediately after payment.
            </Text>
          </Card>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}
