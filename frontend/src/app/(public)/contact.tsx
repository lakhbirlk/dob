import React from "react";
import { ScrollView, Text, View } from "react-native";
import { Card } from "@/components/Card";
import { Link } from "expo-router";

export default function ContactScreen() {
  return (
    <ScrollView className="flex-1 bg-bg">
      <View className="p-6">
        <Text className="text-3xl font-extrabold text-center text-ink">Contact Us</Text>

        <Card variant="default" className="mt-6">
          <Text className="text-lg font-bold text-navy mb-1">General Inquiries</Text>
          <Text className="text-sm text-ink">Email: support@dataofbusiness.in</Text>
        </Card>

        <Card variant="default" className="mt-3">
          <Text className="text-lg font-bold text-navy mb-1">Grievance Officer</Text>
          <Text className="text-sm text-ink">Email: grievance@dataofbusiness.in</Text>
          <Link href="/(public)/grievance" className="text-sm text-blue mt-2 font-medium">View Grievance Policy →</Link>
        </Card>

        <Card variant="default" className="mt-3">
          <Text className="text-lg font-bold text-navy mb-3">Legal</Text>
          <View className="gap-y-2">
            <Link href="/(public)/terms" className="text-sm text-blue">Terms of Service</Link>
            <Link href="/(public)/privacy" className="text-sm text-blue">Privacy Policy</Link>
            <Link href="/(public)/refund" className="text-sm text-blue">Refund & Cancellation</Link>
          </View>
        </Card>
      </View>
    </ScrollView>
  );
}
