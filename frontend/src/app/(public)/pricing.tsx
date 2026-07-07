import React, { useEffect, useState } from "react";
import { View, Text, ScrollView, ActivityIndicator } from "react-native";
import { Button } from "@/components/Button";
import { Card } from "@/components/Card";
import { Badge } from "@/components/Badge";
import { Link, router } from "expo-router";
import { useAuthStore } from "@/store/authStore";
import { membershipsApi } from "@/services/api";
import type { CreditPlan } from "@/types";

// ─────────────────────── Plan Icons ───────────────────────

const PLAN_META: Record<string, { icon: string; color: string; popular?: boolean }> = {
  CREDITS_3:  { icon: "🌱", color: "bg-green/10 border-green/30" },
  CREDITS_5:  { icon: "🚀", color: "bg-blue/10 border-blue/30" },
  CREDITS_10: { icon: "💎", color: "bg-gold/10 border-gold/30", popular: true },
  CREDITS_20: { icon: "🏆", color: "bg-navy-2/10 border-navy-2/30" },
  CREDITS_30: { icon: "👑", color: "bg-navy-deep/10 border-navy-deep/30" },
};

// ─────────────────────── Component ───────────────────────

export default function PricingScreen() {
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated);
  const [plans, setPlans] = useState<CreditPlan[]>([]);
  const [guestPlan, setGuestPlan] = useState<CreditPlan | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    membershipsApi.getPlans().then((data) => {
      setPlans(data.creditPlans ?? []);
      setGuestPlan(data.guestPlan ?? null);
    }).catch(() => {
      // Fallback static plans if API fails
      setGuestPlan({ id: "GUEST", name: "Guest", credits: 2, amount: 0, gst: 0, total: 0, duration: "ONETIME" });
      setPlans([
        { id: "CREDITS_3",  name: "Starter",    credits: 3,  amount: 1500, gst: 270, total: 1770, duration: "MONTHLY" },
        { id: "CREDITS_5",  name: "Basic",      credits: 5,  amount: 2000, gst: 360, total: 2360, duration: "MONTHLY" },
        { id: "CREDITS_10", name: "Pro",        credits: 10, amount: 3000, gst: 540, total: 3540, duration: "MONTHLY" },
        { id: "CREDITS_20", name: "Business",   credits: 20, amount: 4000, gst: 720, total: 4720, duration: "MONTHLY" },
        { id: "CREDITS_30", name: "Enterprise", credits: 30, amount: 5000, gst: 900, total: 5900, duration: "MONTHLY" },
      ]);
    }).finally(() => setLoading(false));
  }, []);

  const [selecting, setSelecting] = useState<string | null>(null);

  const handlePlanPress = async (planId: string) => {
    if (selecting) return; // Prevent double-clicks
    setSelecting(planId);
    try {
      const route = isAuthenticated
        ? `/payment?plan=${planId}`
        : `/(auth)/login?redirect=/payment&plan=${planId}`;
      await router.push(route as any);
    } catch {
      // Navigation failed silently — user remains on page
    } finally {
      setSelecting(null);
    }
  };

  return (
    <ScrollView className="flex-1 bg-bg">
      <View className="px-5 pt-8 pb-12">
        <Badge variant="info" className="self-center mb-4">Pricing</Badge>
        <Text className="text-3xl font-extrabold text-ink text-center mb-2">
          Choose Your Credit Plan
        </Text>
        <Text className="text-muted text-center mb-2 max-w-xs mx-auto leading-5">
          Pay only for what you need — each credit unlocks one full company profile.
        </Text>
        <Text className="text-faint text-center mb-8 text-sm">
          All prices inclusive of 18% GST. Upgrade anytime.
        </Text>

        {loading ? (
          <View className="py-20 items-center">
            <ActivityIndicator size="large" color="#1E2761" />
          </View>
        ) : (
          <View className="gap-y-4 max-w-md mx-auto">
            {plans.map((plan, index) => {
              const meta = PLAN_META[plan.id] ?? { icon: "📊", color: "bg-surface border-line" };
              const isPopular = meta.popular;
              const perCredit = Math.round(plan.total / plan.credits);

              return (
                <Card
                  key={plan.id}
                  variant="elevated"
                  className={`border-2 relative overflow-hidden ${isPopular ? "border-gold" : "border-line/50"}`}
                >
                  {isPopular && (
                    <View className="absolute top-0 right-0 bg-gold px-5 py-1.5 rounded-bl-xl z-10">
                      <Text className="text-navy text-xs font-extrabold uppercase tracking-wider">Best Value</Text>
                    </View>
                  )}

                  <View className="flex-row items-center gap-x-4">
                    {/* Icon */}
                    <View className={`w-14 h-14 rounded-xl ${meta.color} items-center justify-center`}>
                      <Text className="text-2xl">{meta.icon}</Text>
                    </View>

                    {/* Details */}
                    <View className="flex-1">
                      <View className="flex-row items-center gap-x-2">
                        <Text className="text-lg font-extrabold text-ink">{plan.name}</Text>
                        <Badge variant="info">
                          <Text className="text-xs font-bold">{plan.credits} Credits</Text>
                        </Badge>
                      </View>
                      <Text className="text-xs text-faint mt-1">₹{perCredit}/credit — Unlock {plan.credits} companies</Text>
                    </View>

                    {/* Price */}
                    <View className="items-end">
                      <Text className="text-2xl font-extrabold text-ink">₹{plan.amount.toLocaleString("en-IN")}</Text>
                      <Text className="text-xs text-faint">
                        +₹{plan.gst.toLocaleString("en-IN")} GST
                      </Text>
                    </View>
                  </View>

                  {/* Bottom row: total + CTA */}
                  <View className="flex-row items-center justify-between mt-4 pt-3 border-t border-line/50">
                    <Text className="text-sm text-muted">
                      Total: <Text className="font-extrabold text-ink">₹{plan.total.toLocaleString("en-IN")}/mo</Text>
                    </Text>
                    <Button
                      variant={isPopular ? "gold" : "primary"}
                      size="sm"
                      loading={selecting === plan.id}
                      onPress={() => handlePlanPress(plan.id)}
                    >
                      {isPopular ? "Get Started" : "Choose Plan"}
                    </Button>
                  </View>
                </Card>
              );
            })}

            {/* Free Guest Plan Card */}
            {guestPlan && (
              <Card
                variant="bordered"
                className="border-dashed border-teal/40 bg-teal/5 mt-2"
              >
                <View className="flex-row items-center gap-x-4">
                  <View className="w-14 h-14 rounded-xl bg-teal/10 items-center justify-center">
                    <Text className="text-2xl">🎁</Text>
                  </View>
                  <View className="flex-1">
                    <View className="flex-row items-center gap-x-2">
                      <Text className="text-lg font-extrabold text-ink">Free Trial</Text>
                      <Badge variant="info">
                        <Text className="text-xs font-bold">{guestPlan.credits} Credits</Text>
                      </Badge>
                    </View>
                    <Text className="text-xs text-faint mt-1">One-time — no credit card required</Text>
                  </View>
                  <View className="items-end">
                    <Text className="text-2xl font-extrabold text-teal">Free</Text>
                  </View>
                </View>
                <View className="flex-row items-center justify-between mt-4 pt-3 border-t border-teal/20">
                  <Text className="text-sm text-muted flex-1 mr-4">
                    Get {guestPlan.credits} free credits to explore the platform. Unlock any {guestPlan.credits} companies.
                  </Text>
                  <Button
                    variant="primary"
                    size="sm"
                    onPress={() => {
                      const route = isAuthenticated
                        ? "/(member)/dashboard"
                        : "/(auth)/register-research";
                      router.push(route as any);
                    }}
                  >
                    Join Free
                  </Button>
                </View>
              </Card>
            )}

            {/* Upgrade info */}
            <Card variant="subtle" className="mt-4">
              <Text className="text-sm font-bold text-ink mb-2">📈 Upgrade Anytime</Text>
              <Text className="text-sm text-muted leading-5">
                You can upgrade to a higher credit plan at any time. Your remaining credits from
                the current plan will be prorated and applied to the new plan.
              </Text>
            </Card>
          </View>
        )}

        {/* Legal Note */}
        <View className="bg-gold-pale border border-gold/30 rounded-xl p-5 mt-10">
          <Text className="text-sm text-muted text-center leading-6">
            DataOfBusiness is a corporate intelligence platform for research & due diligence.
            We are NOT a stock exchange, broker, investment advisor, NBFC, or P2P platform.
          </Text>
          <View className="flex-row justify-center gap-x-6 mt-4 flex-wrap">
            <Link href="/(public)/terms"><Text className="text-navy font-semibold text-sm">Terms</Text></Link>
            <Link href="/(public)/privacy"><Text className="text-navy font-semibold text-sm">Privacy</Text></Link>
            <Link href="/(public)/refund"><Text className="text-navy font-semibold text-sm">Refund Policy</Text></Link>
          </View>
        </View>
      </View>
    </ScrollView>
  );
}
