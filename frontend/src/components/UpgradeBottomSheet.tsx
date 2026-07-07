import React, { useEffect, useState } from "react";
import {
  View,
  Text,
  Modal,
  TouchableOpacity,
  ScrollView,
  Dimensions,
} from "react-native";
import { Card } from "@/components/Card";
import { Button } from "@/components/Button";
import { Badge } from "@/components/Badge";
import { router } from "expo-router";
import { membershipsApi } from "@/services/api";
import type { CreditPlan } from "@/types";

interface UpgradeBottomSheetProps {
  visible: boolean;
  onClose: () => void;
  companyId?: string;
}

const BENEFITS = [
  { icon: "🏢", label: "Company Name & Profile" },
  { icon: "🔐", label: "CIN, GST, PAN Details" },
  { icon: "👥", label: "Director & Shareholding Data" },
  { icon: "📊", label: "CA-Certified Financial Statements" },
  { icon: "📜", label: "Compliance & Legal Documents" },
  { icon: "📋", label: "AI-Powered Risk Analysis" },
  { icon: "📥", label: "Download Full Reports (PDF)" },
  { icon: "📍", label: "Registered Address & Contact" },
];

const PLAN_META: Record<string, { icon: string; popular?: boolean }> = {
  CREDITS_3:  { icon: "🌱" },
  CREDITS_5:  { icon: "🚀" },
  CREDITS_10: { icon: "💎", popular: true },
  CREDITS_20: { icon: "🏆" },
  CREDITS_30: { icon: "👑" },
};

/**
 * UpgradeBottomSheet — modal bottom sheet that prompts free users to subscribe.
 * Shows the 5 credit-based plans and a CTA to subscribe.
 */
export const UpgradeBottomSheet: React.FC<UpgradeBottomSheetProps> = ({
  visible,
  onClose,
  companyId,
}) => {
  const screenHeight = Dimensions.get("window").height;
  const [plans, setPlans] = useState<CreditPlan[]>([]);

  useEffect(() => {
    if (visible) {
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
      });
    }
  }, [visible]);

  const handleUpgrade = (planId: string) => {
    onClose();
    router.push(`/payment?plan=${planId}`);
  };

  return (
    <Modal
      visible={visible}
      transparent
      animationType="slide"
      onRequestClose={onClose}
    >
      <View className="flex-1 justify-end bg-black/50">
        <View
          className="bg-white rounded-t-[24px]"
          style={{ maxHeight: screenHeight * 0.85 }}
        >
          {/* Handle */}
          <View className="items-center pt-3 pb-2">
            <View className="w-10 h-1 rounded-full bg-line" />
          </View>

          <ScrollView
            className="px-6 pb-8"
            showsVerticalScrollIndicator={false}
          >
            {/* Header */}
            <View className="items-center mb-6">
              <View className="w-16 h-16 rounded-full bg-gold/15 items-center justify-center mb-4">
                <Text className="text-3xl">🔓</Text>
              </View>
              <Text className="text-2xl font-extrabold text-navy text-center">
                Unlock Premium Access
              </Text>
              {companyId && (
                <Text className="text-sm text-muted mt-2 font-mono">
                  {companyId}
                </Text>
              )}
              <Text className="text-sm text-muted text-center mt-3 px-4 leading-5">
                Subscribe as a Research Member to unlock complete company profiles
                with CA-certified financial data. Choose a credit plan that fits
                your needs.
              </Text>
            </View>

            {/* Benefits Comparison */}
            <Card variant="bordered" className="mb-6">
              <Text className="text-base font-extrabold text-navy mb-4">
                What You'll Get
              </Text>
              {BENEFITS.map((b, i) => (
                <View
                  key={i}
                  className="flex-row items-center gap-x-3 py-2.5"
                >
                  <Text className="text-lg">{b.icon}</Text>
                  <Text className="text-sm text-ink font-medium flex-1">
                    {b.label}
                  </Text>
                  <Text className="text-gold">✓</Text>
                </View>
              ))}
            </Card>

            {/* Credit Plans */}
            <Text className="text-base font-extrabold text-navy mb-3">
              Choose Your Credit Plan
            </Text>

            {plans.map((plan) => {
              const meta = PLAN_META[plan.id] ?? { icon: "📊" };
              const perCredit = Math.round(plan.total / plan.credits);

              return (
                <TouchableOpacity
                  key={plan.id}
                  onPress={() => handleUpgrade(plan.id)}
                  activeOpacity={0.8}
                  className="mb-3"
                >
                  <Card
                    variant="elevated"
                    className={`border ${meta.popular ? "border-gold" : "border-line/30"}`}
                  >
                    <View className="flex-row items-center gap-x-3">
                      <Text className="text-2xl">{meta.icon}</Text>
                      <View className="flex-1">
                        <View className="flex-row items-center gap-x-2">
                          <Text className="font-extrabold text-ink">{plan.name}</Text>
                          <Badge variant="info">
                            <Text className="text-xs font-bold">{plan.credits} Credits</Text>
                          </Badge>
                        </View>
                        <Text className="text-xs text-faint mt-0.5">
                          ₹{perCredit}/credit — ₹{plan.amount.toLocaleString("en-IN")}/mo
                        </Text>
                      </View>
                      <View className="items-end">
                        <Text className="font-extrabold text-navy">
                          ₹{plan.total.toLocaleString("en-IN")}
                        </Text>
                        <Text className="text-[10px] text-faint">incl. GST</Text>
                      </View>
                      <Text className="text-gold text-lg ml-1">→</Text>
                    </View>
                  </Card>
                </TouchableOpacity>
              );
            })}

            {/* Footnote */}
            <Text className="text-xs text-faint text-center mt-2 mb-4 leading-5">
              Cancel anytime. Upgrade to a higher plan anytime — remaining credits
              are prorated.
            </Text>

            {/* Close */}
            <TouchableOpacity onPress={onClose} className="items-center py-3 mb-4">
              <Text className="text-sm text-muted font-semibold">
                Maybe Later
              </Text>
            </TouchableOpacity>
          </ScrollView>
        </View>
      </View>
    </Modal>
  );
};

export default UpgradeBottomSheet;
