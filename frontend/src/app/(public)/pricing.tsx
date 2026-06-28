import React from "react";
import { View, Text, ScrollView } from "react-native";
import { Button } from "@/components/Button";
import { Card } from "@/components/Card";
import { Badge } from "@/components/Badge";
import { Link, router } from "expo-router";
import { useAuthStore } from "@/store/authStore";

export default function PricingScreen() {
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated);

  const handlePlanPress = (plan: "RESEARCH" | "COMPANY") => {
    if (isAuthenticated) {
      // Already logged in → go directly to payment
      router.push(`/payment?plan=${plan}`);
    } else {
      // Not logged in → go to login, then redirect back to payment
      router.push(`/(auth)/login?redirect=/payment&plan=${plan}`);
    }
  };

  return (
    <ScrollView className="flex-1 bg-bg">
      <View className="px-5 pt-8 pb-12">
        <Badge variant="info" className="self-center mb-4">Pricing</Badge>
        <Text className="text-3xl font-extrabold text-ink text-center mb-2">Simple, Transparent Pricing</Text>
        <Text className="text-muted text-center mb-10">All prices inclusive of 18% GST. No hidden fees.</Text>

        <View className="gap-y-6 max-w-md mx-auto">
          {/* Research Membership */}
          <Card variant="elevated" className="border-2 border-gold/50 relative overflow-hidden">
            <View className="absolute top-0 right-0 bg-gold px-5 py-1.5 rounded-bl-xl">
              <Text className="text-navy text-xs font-extrabold uppercase tracking-wider">Most Popular</Text>
            </View>
            <View className="items-center">
              <Text className="text-4xl mb-3">🔬</Text>
              <Text className="text-xl font-extrabold text-navy mb-2">Research Membership</Text>
              <Text className="text-sm text-muted mb-5 text-center leading-5">For investors, analysts, and researchers</Text>
              <View className="flex-row items-baseline mb-6">
                <Text className="text-5xl font-extrabold text-ink">₹2,500</Text>
                <Text className="text-muted text-base ml-1">+ GST / month</Text>
              </View>
            </View>
            <View className="bg-surface rounded-lg p-4 mb-5 gap-y-3">
              {[
                "✓ Access CA-certified financial data",
                "✓ Advanced search with filters",
                "✓ 50 document downloads / month",
                "✓ Due-diligence research reports",
                "✓ Priority email support",
              ].map((f, i) => <Text key={i} className="text-sm text-ink font-medium">{f}</Text>)}
            </View>
            <Button variant="gold" size="xl" onPress={() => handlePlanPress("RESEARCH")} style={{ width: "100%" }}>
              Get Started — ₹2,950/month incl. GST
            </Button>
          </Card>

          {/* Company Listing */}
          <Card variant="elevated">
            <View className="items-center">
              <Text className="text-4xl mb-3">🏢</Text>
              <Text className="text-xl font-extrabold text-navy mb-2">Company Listing</Text>
              <Text className="text-sm text-muted mb-5 text-center leading-5">Get your company listed and verified</Text>
              <View className="flex-row items-baseline mb-6">
                <Text className="text-5xl font-extrabold text-ink">₹500</Text>
                <Text className="text-muted text-base ml-1">+ GST / year</Text>
              </View>
            </View>
            <View className="bg-surface rounded-lg p-4 mb-5 gap-y-3">
              {[
                "✓ Detailed company profile page",
                "✓ Upload financial statements",
                "✓ CA certificate verification badge",
                "✓ Company video introduction",
                "✓ Visible to verified researchers",
              ].map((f, i) => <Text key={i} className="text-sm text-ink font-medium">{f}</Text>)}
            </View>
            <Button variant="primary" size="xl" onPress={() => handlePlanPress("COMPANY")} style={{ width: "100%" }}>
              List Your Company — ₹590/year incl. GST
            </Button>
          </Card>
        </View>

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
