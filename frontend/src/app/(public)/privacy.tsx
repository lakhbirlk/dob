import React from "react";
import { ScrollView, Text, View } from "react-native";

export default function PrivacyScreen() {
  return (
    <ScrollView className="flex-1 bg-card">
      <View className="p-6">
        <Text className="text-2xl font-extrabold text-ink">Privacy Policy</Text>
        <Text className="text-sm text-muted mt-1">Last updated: June 2026</Text>

        <View className="bg-bg rounded-lg p-4 mt-5 mb-6">
          <Text className="text-sm text-muted leading-5">
            This Privacy Policy complies with India's Digital Personal Data Protection Act, 2023 (DPDP Act). It explains what data we collect, why we collect it, and your rights.
          </Text>
        </View>

        {[
          { title: "Data We Collect", body: "Email address, full name, phone number, PAN (for verification), IP address, device information, and download/access history. Company users additionally provide business information, financial statements, and CA certificates." },
          { title: "Purpose of Collection", body: "To provide corporate intelligence services, verify user identity, process payments, comply with legal obligations, and improve platform security." },
          { title: "Data Retention", body: "Personal data is retained for the duration of your account plus 3 years for compliance purposes. Financial data is retained as long as the company listing is active. Consent records are retained per DPDP Act requirements." },
          { title: "Your Rights", body: "Right to access, correct, and delete your personal data. Right to withdraw consent. Right to grievance redressal. Contact our Grievance Officer for any data-related requests." },
          { title: "Data Security", body: "Data is encrypted at rest and in transit. We use industry-standard security measures including encryption, access controls, and regular security audits." },
          { title: "Third-Party Sharing", body: "We do not sell personal data. Data may be shared with Razorpay for payment processing and AWS/MinIO for file storage, under strict data processing agreements." },
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
