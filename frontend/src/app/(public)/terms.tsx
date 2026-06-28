import React from "react";
import { ScrollView, Text, View } from "react-native";

export default function TermsScreen() {
  return (
    <ScrollView className="flex-1 bg-card">
      <View className="p-6">
        <Text className="text-2xl font-extrabold text-ink">Terms of Service</Text>
        <Text className="text-sm text-muted mt-1">Last updated: June 2026</Text>

        <View className="bg-gold-pale border-l-4 border-gold rounded-lg p-4 mt-5 mb-6">
          <Text className="text-sm text-ink font-semibold leading-5">
            DataOfBusiness is a corporate intelligence platform. It is NOT a stock exchange, securities marketplace, broker, investment advisor, NBFC, P2P platform, fundraising platform, or loan marketplace.
          </Text>
        </View>

        {[
          { title: "Platform Purpose", body: "DataOfBusiness provides CA-certified financial information and company profiles of Indian businesses for research and due-diligence purposes only." },
          { title: "Membership Rules", body: "Research membership costs ₹2,500 + GST per month. Members get 50 document downloads per month. Downloads beyond the limit are not permitted. Membership is non-transferable." },
          { title: "Company Listing Rules", body: "Company listing costs ₹500 + GST per year. All information provided must be accurate and verifiable. Financial statements must be certified by a practicing Chartered Accountant. The platform reserves the right to reject or suspend listings." },
          { title: "Download Restrictions", body: "Documents downloaded from the platform are for personal research use only. Redistribution, resale, or republication of downloaded documents is strictly prohibited." },
          { title: "User Obligations", body: "Users must provide accurate information during registration. PAN verification may be required. Users must not misuse the platform for unauthorized purposes. Violation may result in account suspension." },
          { title: "Legal Disclaimer", body: "Information provided on the platform is sourced from company submissions and public records. While we verify information where possible, we do not guarantee accuracy. Users should conduct independent verification for critical decisions." },
        ].map((section, i) => (
          <View key={i} className="mb-6">
            <Text className="text-lg font-bold text-navy mb-2">{section.title}</Text>
            <Text className="text-base text-ink leading-6">{section.body}</Text>
          </View>
        ))}
      </View>
    </ScrollView>
  );
}
