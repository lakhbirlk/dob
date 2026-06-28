import React from "react";
import {
  View,
  Text,
  ScrollView,
  SafeAreaView,
  TouchableOpacity,
  ActivityIndicator,
} from "react-native";
import { useAuth } from "@/hooks/useAuth";
import { Card } from "@/components/Card";
import { CompanyStatus } from "@/types";

export default function CompanyDashboard() {
  const { user, logout } = useAuth();

  if (!user) {
    return (
      <SafeAreaView className="flex-1 bg-bg items-center justify-center">
        <ActivityIndicator size="large" color="#1E2761" />
        <Text className="text-muted mt-4">Loading profile...</Text>
      </SafeAreaView>
    );
  }

  const companyStatus: CompanyStatus = CompanyStatus.PENDING; // TODO: Fetch actual company data

  const statusColors: Record<CompanyStatus, string> = {
    [CompanyStatus.APPROVED]: "text-green",
    [CompanyStatus.PENDING]: "text-gold",
    [CompanyStatus.REJECTED]: "text-red",
    [CompanyStatus.SUSPENDED]: "text-red",
  };

  const dashboardCards = [
    {
      title: "Company Profile",
      description: "Manage your company information and branding",
      icon: "🏢",
      link: "/(company)/profile",
    },
    {
      title: "Financial Statements",
      description: "Upload and manage financial documents",
      icon: "📊",
      link: "/(company)/financials",
    },
    {
      title: "Certificates",
      description: "Upload business certificates and licenses",
      icon: "📜",
      link: null,
    },
    {
      title: "Video Pitches",
      description: "Upload company introduction videos",
      icon: "🎥",
      link: null,
    },
  ];

  return (
    <SafeAreaView className="flex-1 bg-bg">
      <ScrollView className="flex-1" showsVerticalScrollIndicator={false}>
        {/* Header */}
        <View className="bg-navy px-6 pt-8 pb-10">
          <Text className="text-white text-2xl font-bold">
            Company Dashboard
          </Text>
          <Text className="text-faint mt-1 text-base">{user.email}</Text>

          {/* Status Badge */}
          <View className="flex-row items-center mt-3">
            <View className="bg-white/20 rounded-full px-3 py-1">
              <Text className={`text-sm font-semibold ${statusColors[companyStatus]}`}>
                Status: {companyStatus}
              </Text>
            </View>
          </View>
        </View>

        {/* Quick Actions */}
        <View className="px-4 mt-6">
          <Text className="text-lg font-semibold text-ink mb-4">
            Manage Your Company
          </Text>
          <View className="gap-y-3">
            {dashboardCards.map((card) => (
              <TouchableOpacity
                key={card.title}
                activeOpacity={0.7}
              >
                <Card variant="bordered" padding="md">
                  <View className="flex-row items-center">
                    <View className="w-12 h-12 rounded-full bg-bg items-center justify-center mr-4">
                      <Text className="text-2xl">{card.icon}</Text>
                    </View>
                    <View className="flex-1">
                      <Text className="text-base font-semibold text-ink">
                        {card.title}
                      </Text>
                      <Text className="text-sm text-muted mt-0.5">
                        {card.description}
                      </Text>
                    </View>
                    <Text className="text-navy font-semibold ml-2">
                      &rarr;
                    </Text>
                  </View>
                </Card>
              </TouchableOpacity>
            ))}
          </View>
        </View>

        {/* Pending Approval Notice */}
        {companyStatus === CompanyStatus.PENDING && (
          <View className="mx-4 mt-6">
            <Card variant="bordered" padding="lg">
              <View className="items-center">
                <Text className="text-3xl mb-2">⏳</Text>
                <Text className="text-lg font-semibold text-ink text-center mb-2">
                  Profile Under Review
                </Text>
                <Text className="text-muted text-center leading-6">
                  Your company profile is being reviewed by our admin team. This
                  usually takes 1-2 business days. We&apos;ll notify you once
                  the review is complete.
                </Text>
              </View>
            </Card>
          </View>
        )}

        {/* Account */}
        <View className="px-4 mt-8 mb-10">
          <Card variant="bordered">
            <TouchableOpacity className="py-3 border-b border-line">
              <Text className="text-base text-ink">Account Settings</Text>
            </TouchableOpacity>
            <TouchableOpacity className="py-3" onPress={logout}>
              <Text className="text-base text-red">Sign Out</Text>
            </TouchableOpacity>
          </Card>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}
