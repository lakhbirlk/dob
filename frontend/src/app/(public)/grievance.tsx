import React from "react";
import { ScrollView, Text, View } from "react-native";

export default function GrievanceScreen() {
  return (
    <ScrollView className="flex-1 bg-card">
      <View className="p-6">
        <Text className="text-2xl font-extrabold text-ink">Grievance Redressal</Text>
        <Text className="text-sm text-muted mt-1">
          As required under the IT Act and DPDP Act 2023
        </Text>

        <View className="bg-bg rounded-xl p-5 mt-6 mb-6">
          <Text className="text-lg font-bold text-navy mb-2">Grievance Officer</Text>
          <Text className="text-base text-ink mb-1">Name: [Grievance Officer Name]</Text>
          <Text className="text-base text-ink mb-1">Email: grievance@dataofbusiness.in</Text>
          <Text className="text-base text-ink">Address: [Registered Office Address]</Text>
        </View>

        {[
          {
            title: "Complaint Lifecycle",
            body: "1. File a complaint via email or in-app grievance form.\n2. Acknowledgment within 24 hours with a ticket ID.\n3. Initial review and assignment within 48 hours.\n4. Resolution within 15 days from acknowledgment.\n5. Escalation available if not satisfied with resolution.",
          },
          {
            title: "Escalation Process",
            body: "Level 1: Grievance Officer (resolution within 15 days).\nLevel 2: Senior Management (resolution within 7 additional days).\nLevel 3: Nodal Officer — contact details provided upon Level 2 escalation.",
          },
          {
            title: "Resolution SLA",
            body: "General queries: 3 business days. Account-related issues: 5 business days. Payment/refund issues: 10 business days. Data privacy complaints: 15 days as per DPDP Act. Content-related grievances: 7 business days.",
          },
          {
            title: "Compliance",
            body: "This grievance mechanism complies with the Information Technology Act, 2000 and the Digital Personal Data Protection Act, 2023. Users have the right to approach the Data Protection Board of India if not satisfied with the resolution.",
          },
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
