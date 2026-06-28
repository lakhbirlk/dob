import React from "react";
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
import { colors } from "@/theme/colors";
import { router } from "expo-router";

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

/**
 * UpgradeBottomSheet — modal bottom sheet that prompts free users to subscribe.
 * Displays a comparison of what they're missing and the subscription CTA.
 */
export const UpgradeBottomSheet: React.FC<UpgradeBottomSheetProps> = ({
  visible,
  onClose,
  companyId,
}) => {
  const screenHeight = Dimensions.get("window").height;

  const handleUpgrade = () => {
    onClose();
    router.push("/(auth)/register");
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
          style={{ maxHeight: screenHeight * 0.8 }}
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
                Subscribe as a Research Member to unlock the complete company
                profile with CA-certified financial data.
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

            {/* Pricing Card */}
            <Card variant="elevated" className="items-center mb-6 py-5">
              <Text className="text-xs text-faint font-semibold uppercase tracking-wider mb-2">
                Research Membership
              </Text>
              <View className="flex-row items-baseline gap-x-1">
                <Text className="text-3xl font-extrabold text-navy">₹2,500</Text>
                <Text className="text-sm text-muted">/month + GST</Text>
              </View>
              <View className="flex-row items-center gap-x-2 mt-3">
                <Badge variant="success">
                  <Text className="text-xs font-extrabold">50 Downloads/mo</Text>
                </Badge>
                <Badge variant="info">
                  <Text className="text-xs font-extrabold">Full Access</Text>
                </Badge>
              </View>
            </Card>

            {/* CTA */}
            <Button variant="gold" size="xl" onPress={handleUpgrade}>
              Subscribe Now — ₹2,500/month
            </Button>

            {/* Footnote */}
            <Text className="text-xs text-faint text-center mt-4 mb-2 leading-5">
              Cancel anytime. Full report access includes financial statements,
              risk analysis, and downloadable PDFs.
            </Text>

            {/* Close */}
            <TouchableOpacity onPress={onClose} className="items-center py-3">
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
