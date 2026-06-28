import React from "react";
import { ScrollView, Text, View } from "react-native";
import { Card } from "@/components/Card";

export default function AboutScreen() {
  return (
    <ScrollView className="flex-1 bg-bg">
      <View className="p-6">
        <Text className="text-3xl font-extrabold text-ink text-center">
          About DataOfBusiness
        </Text>
        <Text className="text-base text-muted text-center mt-2">
          India's Private Company Intelligence Platform
        </Text>

        <Card variant="default" className="mt-6">
          <Text className="text-base text-ink leading-6">
            DataOfBusiness (DoB) is a corporate intelligence and research platform that provides CA-certified financial information, company profiles, and business data of Indian companies. Our platform serves researchers, investors, and businesses conducting due-diligence on Indian private companies.
          </Text>
        </Card>

        <View className="gap-y-4 mt-6">
          <View className="bg-green-light border border-green/20 rounded-xl p-5">
            <Text className="text-lg font-bold text-green mb-3">What We Are</Text>
            {["Corporate intelligence database", "Research platform", "Due diligence platform", "Business information service", "Subscription-based information product"].map((item, i) => (
              <Text key={i} className="text-sm text-ink mb-1.5">✓ {item}</Text>
            ))}
          </View>

          <View className="bg-red-light border border-red/20 rounded-xl p-5">
            <Text className="text-lg font-bold text-red mb-3">What We Are NOT</Text>
            {["Stock exchange", "Securities marketplace", "Broker", "Investment advisor", "NBFC", "P2P platform", "Fundraising platform", "Loan marketplace"].map((item, i) => (
              <Text key={i} className="text-sm text-ink mb-1.5">✗ {item}</Text>
            ))}
          </View>
        </View>
      </View>
    </ScrollView>
  );
}
