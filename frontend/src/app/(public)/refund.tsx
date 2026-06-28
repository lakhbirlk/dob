import React from "react";
import { ScrollView, Text, View } from "react-native";

export default function RefundScreen() {
  return (
    <ScrollView className="flex-1 bg-card">
      <View className="p-6">
        <Text className="text-2xl font-extrabold text-ink">Refund & Cancellation Policy</Text>
        <Text className="text-sm text-muted mt-1">Last updated: June 2026</Text>

        {[
          {
            title: "Membership Refunds",
            body: "Research Membership (₹2,500 + GST): Full refund if requested within 24 hours of payment. Partial refund (50%) if requested within 7 days, provided no downloads have been made. No refund after 7 days or if any downloads have been consumed.",
          },
          {
            title: "Company Listing Refunds",
            body: "Company Listing Fee (₹500 + GST): Full refund if the listing has not been approved yet. No refund after listing approval. If listing is rejected by the platform, a full refund is automatically processed.",
          },
          {
            title: "Cooling-Off Period",
            body: "A 24-hour cooling-off period applies to all purchases. During this period, you may cancel your purchase and receive a full refund, provided no services have been consumed.",
          },
          {
            title: "Refund Process",
            body: "Refund requests can be raised from your account dashboard. Approved refunds are processed within 5-7 business days to the original payment method via Razorpay. GST invoices will be revised accordingly.",
          },
          {
            title: "Non-Refundable Scenarios",
            body: "No refunds are provided for: (a) membership after 7 days, (b) membership with any downloads, (c) approved company listings, (d) violation of terms resulting in account suspension.",
          },
        ].map((section, i) => (
          <View key={i} className="mb-6 mt-6">
            <Text className="text-lg font-bold text-navy mb-2">{section.title}</Text>
            <Text className="text-base text-ink leading-6">{section.body}</Text>
          </View>
        ))}
      </View>
    </ScrollView>
  );
}
