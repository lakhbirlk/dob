import React from "react";
import {
  View, Text, ScrollView, TouchableOpacity, Dimensions,
} from "react-native";
import { Link, router } from "expo-router";
import { Button } from "@/components/Button";
import { Card } from "@/components/Card";
import { SearchBar } from "@/components/SearchBar";
import { Badge } from "@/components/Badge";
import { IconBox } from "@/components/IconBox";
import { useAuthStore } from "@/store/authStore";
import { colors } from "@/theme/colors";

const { width } = Dimensions.get("window");

const TRUST_ITEMS = [
  { icon: "🔒", text: "CA-Verified Data" },
  { icon: "🏛️", text: "Compliant with DPDP Act 2023" },
  { icon: "⭐", text: "10,000+ Listed Companies" },
  { icon: "⚡", text: "Instant Document Access" },
];

const FEATURES = [
  { icon: "📊", title: "Verified Financial Data", desc: "Access CA-certified financial statements, balance sheets, and P&L reports verified by our compliance team." },
  { icon: "🔍", title: "Powerful Search & Filters", desc: "Filter by sector, state, revenue range, company type. Full-text search across all company profiles." },
  { icon: "📥", title: "Secure Document Downloads", desc: "Download financial reports with enterprise-grade security. Track your download history and limits." },
  { icon: "🎥", title: "Company Video Pitches", desc: "Watch video introductions from company leadership. Get direct insight into business operations." },
  { icon: "🛡️", title: "Compliance & Due Diligence", desc: "Built for serious research. Every listing is verified. Every document is traceable." },
  { icon: "🤝", title: "Direct Engagement", desc: "Connect with verified businesses. Our platform bridges researchers and companies seamlessly." },
];

const WHAT_IS = [
  "Corporate Intelligence Database",
  "Research & Due Diligence Platform",
  "Business Information Service",
  "Subscription-based Information Product",
];
const WHAT_IS_NOT = [
  "Stock Exchange", "Securities Marketplace",
  "Broker or Investment Advisor", "NBFC / P2P Platform",
  "Fundraising Platform", "Loan Marketplace",
];

export default function HomeScreen() {
  const [search, setSearch] = React.useState("");
  const { isAuthenticated } = useAuthStore();

  return (
    <ScrollView className="flex-1 bg-bg" showsVerticalScrollIndicator={false}>

      {/* ===== HERO ===== */}
      <View className="bg-navy-deep pt-14 pb-10 px-5" style={{
        borderBottomLeftRadius: 32, borderBottomRightRadius: 32,
      }}>
        {/* Trust Badge */}
        <View className="flex-row justify-center mb-6">
          <View className="flex-row items-center gap-x-2 bg-white/10 border border-white/20 px-4 py-2 rounded-full">
            <View className="w-2 h-2 rounded-full bg-gold" />
            <Text className="text-xs font-bold text-white tracking-wider uppercase">India's Trusted Corporate Intelligence Platform</Text>
          </View>
        </View>

        <Text className="text-4xl font-extrabold text-white text-center leading-tight mb-3 tracking-tight">
          Discover{" "}
          <Text className="text-gold">Indian Companies</Text>
          {" "}Like Never Before
        </Text>
        <Text className="text-base text-faint text-center mb-8 leading-7 px-2">
          Access CA-certified financials, company profiles, and due-diligence data on India's premier corporate intelligence platform.
        </Text>

        {/* Search */}
        <View className="mb-8">
          <SearchBar
            value={search}
            onChangeText={setSearch}
            onSubmit={(q) => { if (q.trim()) router.push({ pathname: "/(authenticated)/companies", params: { search: q.trim() } }); }}
            placeholder="Search by company name, sector, or location..."
            containerClassName="shadow-xl"
          />
        </View>

        {/* CTAs for non-auth */}
        {!isAuthenticated && (
          <View className="flex-row justify-center gap-x-4 mb-6">
            <Link href="/(auth)/register" asChild>
              <Button variant="gold" size="lg" style={{ minWidth: 140 }}>Get Started Free</Button>
            </Link>
            <Link href="/(auth)/login" asChild>
              <Button variant="ghost" size="lg" textStyle={{ color: "#FFFFFF" }} style={{ borderColor: "rgba(255,255,255,0.3)" }}>Sign In</Button>
            </Link>
          </View>
        )}

        {/* Hero Stats */}
        <View className="flex-row justify-center gap-x-12 mt-2">
          {[{ v: "10,000+", l: "Companies" }, { v: "₹500Cr+", l: "Data Tracked" }, { v: "28", l: "States Covered" }].map((s, i) => (
            <View key={i} className="items-center">
              <Text className="text-2xl font-extrabold text-white">{s.v}</Text>
              <Text className="text-xs text-faint mt-1 font-medium">{s.l}</Text>
            </View>
          ))}
        </View>
      </View>

      {/* ===== TRUST STRIP ===== */}
      <View className="bg-card border-b border-line">
        <View className="flex-row flex-wrap justify-center gap-x-8 gap-y-3 px-4 py-4">
          {TRUST_ITEMS.map((item, i) => (
            <View key={i} className="flex-row items-center gap-x-2">
              <Text className="text-base">{item.icon}</Text>
              <Text className="text-xs font-semibold text-muted uppercase tracking-wide">{item.text}</Text>
            </View>
          ))}
        </View>
      </View>

      {/* ===== WHAT WE ARE / NOT ===== */}
      <View className="px-5 mt-10">
        <View className="flex-row gap-x-4">
          <View className="flex-1 bg-green-light border border-green/20 rounded-xl p-5">
            <Text className="text-base font-bold text-green mb-3">✓ What We Are</Text>
            {WHAT_IS.map((item, i) => (
              <Text key={i} className="text-sm text-ink mb-1.5 leading-5">{item}</Text>
            ))}
          </View>
          <View className="flex-1 bg-red-light border border-red/20 rounded-xl p-5">
            <Text className="text-base font-bold text-red mb-3">✗ What We're NOT</Text>
            {WHAT_IS_NOT.map((item, i) => (
              <Text key={i} className="text-sm text-ink mb-1.5 leading-5">{item}</Text>
            ))}
          </View>
        </View>
      </View>

      {/* ===== FEATURES ===== */}
      <View className="px-5 mt-12">
        <Badge variant="info" className="self-center mb-3">Why DataOfBusiness</Badge>
        <Text className="text-3xl font-extrabold text-ink text-center mb-3 tracking-tight">
          Everything you need for{"\n"}company research
        </Text>
        <Text className="text-muted text-center mb-8 text-base leading-6">
          Purpose-built tools for corporate intelligence, due diligence, and business research.
        </Text>

        <View className="gap-y-4">
          {FEATURES.map((f, i) => (
            <Card key={i} variant="elevated" className="flex-row items-start gap-x-4">
              <IconBox icon={f.icon} bg="bg-navy/8" size="md" />
              <View className="flex-1">
                <Text className="text-lg font-bold text-ink mb-1.5">{f.title}</Text>
                <Text className="text-sm text-muted leading-6">{f.desc}</Text>
              </View>
            </Card>
          ))}
        </View>
      </View>

      {/* ===== PRICING CTA ===== */}
      <View className="mx-5 my-12 rounded-2xl overflow-hidden" style={{ backgroundColor: "#1E2761" }}>
        <View className="p-8 items-center">
          <Badge variant="gold" className="mb-4">Simple Pricing</Badge>
          <Text className="text-2xl font-extrabold text-white text-center mb-3">Start Your Research Today</Text>
          <Text className="text-faint text-center mb-7 leading-6">
            Research Membership at ₹2,500/month + GST. Company Listing at ₹500/year + GST.
          </Text>
          <View className="flex-row gap-x-3">
            <Link href="/(public)/pricing" asChild>
              <Button variant="gold" size="lg">View Pricing</Button>
            </Link>
            <Link href="/(auth)/register" asChild>
              <Button variant="ghost" size="lg" textStyle={{ color: "#FFFFFF" }}>Register Free</Button>
            </Link>
          </View>
        </View>
      </View>

      {/* ===== FOOTER ===== */}
      <View className="bg-navy-deep px-6 py-10" style={{ borderTopLeftRadius: 24, borderTopRightRadius: 24 }}>
        <View className="flex-row items-center gap-x-3 mb-6">
          <View className="bg-navy px-3 py-1.5 rounded-md">
            <Text className="text-gold font-extrabold text-base tracking-wide">DoB</Text>
          </View>
          <View>
            <Text className="text-white font-bold text-lg">DataOfBusiness</Text>
            <Text className="text-faint text-xs">India's Corporate Intelligence Platform</Text>
          </View>
        </View>
        <View className="flex-row flex-wrap gap-x-6 gap-y-3 mb-8">
          {["About", "Pricing", "Companies", "Contact", "Privacy", "Terms", "Refund", "Grievance"].map((l) => (
            <Link key={l} href={`/(public)/${l.toLowerCase()}`} asChild>
              <TouchableOpacity><Text className="text-white/70 text-sm font-medium">{l}</Text></TouchableOpacity>
            </Link>
          ))}
        </View>
        <View className="flex-row flex-wrap gap-x-6 gap-y-2 mb-6">
          <Link href="/(public)/business-model" asChild>
            <TouchableOpacity>
              <Text className="text-gold text-sm font-semibold">Business Model & Sitemap</Text>
            </TouchableOpacity>
          </Link>
        </View>
        <View className="h-px bg-white/10 mb-6" />
        <Text className="text-faint text-xs text-center">
          DataOfBusiness is NOT a stock exchange, broker, investment advisor, NBFC, or P2P platform.
          {"\n"}© 2026 DataOfBusiness. All rights reserved.
        </Text>
      </View>

    </ScrollView>
  );
}
