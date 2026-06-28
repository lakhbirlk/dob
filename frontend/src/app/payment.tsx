import React, { useState, useEffect, useCallback } from "react";
import {
  View, Text, ScrollView, ActivityIndicator, TouchableOpacity,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { router, useLocalSearchParams, Redirect } from "expo-router";
import { Button } from "@/components/Button";
import { Card } from "@/components/Card";
import { useAuthStore } from "@/store/authStore";
import { subscriptionsApi, paymentsApi } from "@/services/api";
import { UserRole } from "@/types";

// ─────────────────────── Plan Config ───────────────────────

const PLAN_CONFIG = {
  RESEARCH: {
    title: "Research Membership",
    icon: "🔬",
    amount: 2500,
    gst: 450,
    total: 2950,
    color: "bg-gold",
    badge: "bg-gold",
    badgeText: "text-navy",
    benefits: [
      "Full Company Reports",
      "Company Names & Identifiers",
      "Financial Statements",
      "Director Information",
      "Download Reports (50/month)",
      "Premium Search & Filters",
      "AI-Powered Insights",
      "Saved Companies & Watchlists",
      "Unlimited Company Viewing",
      "Priority Email Support",
    ],
    dashboardRoute: "/(member)/dashboard" as const,
  },
  COMPANY: {
    title: "Company Listing",
    icon: "🏢",
    amount: 500,
    gst: 90,
    total: 590,
    color: "bg-navy",
    badge: "bg-teal",
    badgeText: "text-white",
    benefits: [
      "Company Profile Published",
      "Company Dashboard Access",
      "Financial Statement Upload",
      "CA Certificate Verification",
      "Business Analytics",
      "Profile Editing",
      "Company Visibility to Researchers",
      "Video Introduction",
    ],
    dashboardRoute: "/(company)/dashboard" as const,
  },
} as const;

type PlanKey = keyof typeof PLAN_CONFIG;
type PaymentState = "idle" | "processing" | "success";

// ─────────────────────── Component ───────────────────────

export default function PaymentScreen() {
  const { isAuthenticated, isHydrated, user } = useAuthStore();
  const params = useLocalSearchParams<{ plan?: string }>();
  const planKey = (params.plan?.toUpperCase() === "COMPANY" ? "COMPANY" : "RESEARCH") as PlanKey;
  const plan = PLAN_CONFIG[planKey];

  const [paymentState, setPaymentState] = useState<PaymentState>("idle");
  const [error, setError] = useState<string | null>(null);
  const [transactionId, setTransactionId] = useState<string | null>(null);

  // Guard: not authenticated → redirect to login
  if (isHydrated && !isAuthenticated) {
    return <Redirect href={`/(auth)/login?redirect=/payment&plan=${planKey}`} />;
  }

  // Guard: still hydrating
  if (!isHydrated) {
    return (
      <View className="flex-1 items-center justify-center bg-bg">
        <ActivityIndicator size="large" color="#1E2761" />
      </View>
    );
  }

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
        router.replace(plan.dashboardRoute);
      }, 2000);
    } catch (err: unknown) {
      const message =
        err instanceof Error ? err.message : "Payment failed. Please try again.";
      setError(message);
      setPaymentState("idle");
    }
  }, [planKey, plan.dashboardRoute]);

  // ──────────────────── Render States ────────────────────

  // SUCCESS STATE
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
        <View className={`${plan.color} pt-8 pb-10 px-6 items-center`}
          style={{ borderBottomLeftRadius: 32, borderBottomRightRadius: 32 }}
        >
          <Text className="text-5xl mb-3">{plan.icon}</Text>
          <Text className="text-white font-extrabold text-2xl mb-1">
            {plan.title}
          </Text>
          <View className={`${plan.badge} px-4 py-1 rounded-full`}>
            <Text className={`${plan.badgeText} text-sm font-bold`}>
              {planKey === "RESEARCH" ? "Monthly Subscription" : "Annual Listing"}
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
                <Text className="text-muted text-base ml-1">
                  /{planKey === "RESEARCH" ? "month" : "year"}
                </Text>
              </View>
              <Text className="text-faint text-xs mt-1">
                ₹{plan.amount.toLocaleString("en-IN")} + ₹{plan.gst} GST
              </Text>
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
            variant={planKey === "RESEARCH" ? "gold" : "primary"}
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
