import React from "react";
import { View, Text, ScrollView, TouchableOpacity } from "react-native";
import { Link, router } from "expo-router";
import { Card } from "@/components/Card";
import { Badge } from "@/components/Badge";
import { Button } from "@/components/Button";
import { useAuthStore } from "@/store/authStore";

// ─────────────────────── Data ───────────────────────

const REVENUE_STREAMS = [
  {
    icon: "👤",
    title: "Research Membership",
    subtitle: "Subscription — ₹2,500/month + GST",
    desc: "Primary revenue stream. Research members pay a monthly subscription for access to CA-certified financial data, company profiles, and document downloads.",
    highlights: ["Recurring monthly revenue", "50 research profile downloads/month", "Full access to financial statements, videos & contacts"],
  },
  {
    icon: "🏢",
    title: "Company Listing",
    subtitle: "Annual — ₹500/year + GST",
    desc: "Companies pay to list their verified financial profiles on the platform for research visibility. A low-cost annual fee makes it accessible for MSMEs and private enterprises.",
    highlights: ["Annual renewal model", "CA certificate & financial uploads", "Video profile & direct research visibility"],
  },
];

const HOW_IT_WORKS_MEMBERS = [
  { step: "1", title: "Create Account", desc: "Register with email + password. No OTP needed. Free to browse." },
  { step: "2", title: "Accept Research Declaration", desc: "Confirm you're using the platform for research & due-diligence purposes only." },
  { step: "3", title: "Subscribe", desc: "Pay ₹2,500/month + GST via Razorpay. Instant access on payment." },
  { step: "4", title: "Explore & Download", desc: "Search 10,000+ companies, view full profiles, download up to 50 research reports/month." },
];

const HOW_IT_WORKS_COMPANIES = [
  { step: "1", title: "Register as Company", desc: "Create a company account and submit your business details." },
  { step: "2", title: "Upload Financials", desc: "Upload CA-certified financial statements, certificates, and a corporate profile video." },
  { step: "3", title: "Admin Verification", desc: "Our team verifies the documents. Once approved, your profile goes live." },
  { step: "4", title: "Research Visibility", desc: "Your company becomes visible to research members for due-diligence and corporate intelligence." },
];

const WHAT_IS = [
  "Corporate Intelligence Database",
  "Research & Due Diligence Platform",
  "Business Information Service",
  "Subscription-based Information Product",
  "CA-Certified Financial Data Repository",
];
const WHAT_IS_NOT = [
  "Stock Exchange or Securities Marketplace",
  "Broker, Investment Advisor or Research Analyst",
  "NBFC, P2P Lending or Fundraising Platform",
  "Loan Marketplace or Credit Platform",
  "Any form of Transaction Facilitator",
];

// Sitemap sections
const SITEMAP = [
  {
    role: "Public",
    color: "bg-blue",
    pages: [
      { name: "Landing / Home", route: "/(public)", icon: "🏠" },
      { name: "Company Database", route: "/(authenticated)/companies", icon: "🏢" },
      { name: "Company Profile", route: "/(authenticated)/company/[id]", icon: "📋" },
      { name: "Pricing Plans", route: "/(public)/pricing", icon: "💰" },
      { name: "About Us", route: "/(public)/about", icon: "ℹ️" },
      { name: "Contact", route: "/(public)/contact", icon: "📧" },
      { name: "Privacy Policy", route: "/(public)/privacy", icon: "🔒" },
      { name: "Terms of Service", route: "/(public)/terms", icon: "📜" },
      { name: "Refund Policy", route: "/(public)/refund", icon: "↩️" },
      { name: "Grievance Redressal", route: "/(public)/grievance", icon: "📢" },
    ],
  },
  {
    role: "Authentication",
    color: "bg-navy-2",
    pages: [
      { name: "Login", route: "/(auth)/login", icon: "🔑" },
      { name: "Register", route: "/(auth)/register", icon: "📝" },
      { name: "Forgot Password", route: "/(auth)/forgot-password", icon: "❓" },
    ],
  },
  {
    role: "Research Member",
    color: "bg-gold-dark",
    pages: [
      { name: "Member Dashboard", route: "/(member)/dashboard", icon: "📊" },
      { name: "Download Center", route: "/(member)/downloads", icon: "📥" },
      { name: "My Profile", route: "/(member)/profile", icon: "👤" },
    ],
  },
  {
    role: "Company User",
    color: "bg-teal",
    pages: [
      { name: "Company Dashboard", route: "/(company)/dashboard", icon: "📈" },
      { name: "Create Listing", route: "/(company)/create-listing", icon: "➕" },
      { name: "Listing Status", route: "/(company)/listing-status", icon: "📌" },
    ],
  },
  {
    role: "Admin",
    color: "bg-red",
    pages: [
      { name: "Admin Dashboard", route: "/(admin)/dashboard", icon: "⚙️" },
      { name: "Pending Approvals", route: "/(admin)/pending-approvals", icon: "⏳" },
      { name: "Refund Management", route: "/(admin)/refunds", icon: "💳" },
      { name: "Grievance Management", route: "/(admin)/grievances", icon: "📢" },
      { name: "Audit Logs", route: "/(admin)/audit-logs", icon: "📋" },
    ],
  },
];

const STATS = [
  { value: "10,000+", label: "Listed Companies" },
  { value: "₹2,500/mo", label: "Research Access" },
  { value: "₹500/yr", label: "Company Listing" },
  { value: "50/mo", label: "Downloads Included" },
];

// ─────────────────────── Component ───────────────────────

export default function BusinessModelScreen() {
  const { isAuthenticated } = useAuthStore();

  return (
    <ScrollView className="flex-1 bg-bg" showsVerticalScrollIndicator={false}>
      {/* ===== HERO ===== */}
      <View
        className="bg-navy-deep pt-14 pb-12 px-5"
        style={{ borderBottomLeftRadius: 32, borderBottomRightRadius: 32 }}
      >
        <View className="flex-row justify-center mb-5">
          <View className="flex-row items-center gap-x-2 bg-white/10 border border-white/20 px-4 py-2 rounded-full">
            <View className="w-2 h-2 rounded-full bg-gold" />
            <Text className="text-xs font-bold text-white tracking-wider uppercase">
              Corporate Intelligence Platform
            </Text>
          </View>
        </View>

        <Text className="text-3xl font-extrabold text-white text-center leading-tight mb-3 tracking-tight">
          The Business Model{"\n"}Behind{" "}
          <Text className="text-gold">DataOfBusiness</Text>
        </Text>
        <Text className="text-base text-faint text-center mb-8 leading-7 px-2">
          We are a subscription-based corporate intelligence platform — not a
          marketplace, broker, or financial intermediary. Here is exactly how
          the platform works and generates revenue.
        </Text>

        {/* Stats row */}
        <View className="flex-row flex-wrap justify-center gap-4">
          {STATS.map((s, i) => (
            <View
              key={i}
              className="bg-white/10 rounded-xl px-4 py-3 items-center min-w-[100]"
            >
              <Text className="text-lg font-extrabold text-gold">{s.value}</Text>
              <Text className="text-xs text-faint mt-1 font-medium">{s.label}</Text>
            </View>
          ))}
        </View>
      </View>

      {/* ===== SECTION: What Is DataOfBusiness ===== */}
      <View className="px-5 mt-8">
        <Badge variant="info" className="self-center mb-3">
          Platform Overview
        </Badge>
        <Text className="text-2xl font-extrabold text-ink text-center mb-2">
          A Research Database — Nothing More
        </Text>
        <Text className="text-muted text-center text-base leading-6 mb-6">
          DataOfBusiness is an information service. Indian private companies
          publish CA-certified financial data. Research members subscribe to
          access it. We do not facilitate transactions, investments, or loans.
        </Text>

        <View className="flex-row gap-x-3">
          <View className="flex-1 bg-green-light border border-green/20 rounded-xl p-4">
            <Text className="text-sm font-bold text-green mb-2">✓ What We Are</Text>
            {WHAT_IS.map((item, i) => (
              <Text key={i} className="text-xs text-ink mb-1.5 leading-5">
                {item}
              </Text>
            ))}
          </View>
          <View className="flex-1 bg-red-light border border-red/20 rounded-xl p-4">
            <Text className="text-sm font-bold text-red mb-2">✗ What We're NOT</Text>
            {WHAT_IS_NOT.map((item, i) => (
              <Text key={i} className="text-xs text-ink mb-1.5 leading-5">
                {item}
              </Text>
            ))}
          </View>
        </View>
      </View>

      {/* ===== SECTION: Revenue Model ===== */}
      <View className="px-5 mt-10">
        <Badge variant="gold" className="self-center mb-3">
          Revenue Model
        </Badge>
        <Text className="text-2xl font-extrabold text-ink text-center mb-2 tracking-tight">
          Two Revenue Streams
        </Text>
        <Text className="text-muted text-center text-base leading-6 mb-6">
          The platform generates revenue through two distinct subscription
          streams — no commissions, no success fees, no transaction charges.
        </Text>

        <View className="gap-y-4">
          {REVENUE_STREAMS.map((stream, i) => (
            <Card key={i} variant="elevated" className="p-5">
              <View className="flex-row items-start gap-x-4 mb-3">
                <View className="w-12 h-12 rounded-xl bg-navy/10 items-center justify-center">
                  <Text className="text-2xl">{stream.icon}</Text>
                </View>
                <View className="flex-1">
                  <Text className="text-lg font-bold text-ink">{stream.title}</Text>
                  <Text className="text-sm text-gold-dark font-semibold mt-0.5">
                    {stream.subtitle}
                  </Text>
                </View>
              </View>
              <Text className="text-sm text-muted leading-6 mb-3">
                {stream.desc}
              </Text>
              {stream.highlights.map((h, j) => (
                <View key={j} className="flex-row items-start gap-x-2 mb-1.5">
                  <Text className="text-green text-sm mt-0.5">✓</Text>
                  <Text className="text-sm text-ink flex-1">{h}</Text>
                </View>
              ))}
            </Card>
          ))}
        </View>
      </View>

      {/* ===== SECTION: How It Works ===== */}
      <View className="px-5 mt-10">
        <Badge variant="info" className="self-center mb-3">
          How It Works
        </Badge>
        <Text className="text-2xl font-extrabold text-ink text-center mb-6 tracking-tight">
          Two-Sided Platform Flow
        </Text>

        {/* Companies side */}
        <Card variant="elevated" className="p-5 mb-4">
          <View className="flex-row items-center gap-x-3 mb-4">
            <View className="bg-blue/10 w-10 h-10 rounded-xl items-center justify-center">
              <Text className="text-xl">🏢</Text>
            </View>
            <View>
              <Text className="text-base font-bold text-ink">For Companies</Text>
              <Text className="text-xs text-muted">Publish & get research visibility</Text>
            </View>
          </View>
          <View className="gap-y-3">
            {HOW_IT_WORKS_COMPANIES.map((item, i) => (
              <View key={i} className="flex-row items-start gap-x-3">
                <View className="w-7 h-7 rounded-full bg-navy items-center justify-center">
                  <Text className="text-white text-xs font-bold">{item.step}</Text>
                </View>
                <View className="flex-1">
                  <Text className="text-sm font-bold text-ink">{item.title}</Text>
                  <Text className="text-xs text-muted mt-0.5">{item.desc}</Text>
                </View>
              </View>
            ))}
          </View>
          <Link href="/(auth)/register" asChild>
            <TouchableOpacity className="mt-4 bg-blue/10 rounded-lg py-3 items-center">
              <Text className="text-blue font-bold text-sm">List Your Company →</Text>
            </TouchableOpacity>
          </Link>
        </Card>

        {/* Members side */}
        <Card variant="elevated" className="p-5">
          <View className="flex-row items-center gap-x-3 mb-4">
            <View className="bg-gold/10 w-10 h-10 rounded-xl items-center justify-center">
              <Text className="text-xl">🔎</Text>
            </View>
            <View>
              <Text className="text-base font-bold text-ink">For Research Members</Text>
              <Text className="text-xs text-muted">Subscribe & access verified data</Text>
            </View>
          </View>
          <View className="gap-y-3">
            {HOW_IT_WORKS_MEMBERS.map((item, i) => (
              <View key={i} className="flex-row items-start gap-x-3">
                <View className="w-7 h-7 rounded-full bg-gold items-center justify-center">
                  <Text className="text-navy-deep text-xs font-bold">{item.step}</Text>
                </View>
                <View className="flex-1">
                  <Text className="text-sm font-bold text-ink">{item.title}</Text>
                  <Text className="text-xs text-muted mt-0.5">{item.desc}</Text>
                </View>
              </View>
            ))}
          </View>
          {!isAuthenticated && (
            <Link href="/(auth)/register" asChild>
              <TouchableOpacity className="mt-4 bg-gold/10 rounded-lg py-3 items-center">
                <Text className="text-gold-dark font-bold text-sm">Get Research Access →</Text>
              </TouchableOpacity>
            </Link>
          )}
        </Card>
      </View>

      {/* ===== SECTION: Pricing Summary ===== */}
      <View className="mx-5 mt-10 rounded-2xl overflow-hidden" style={{ backgroundColor: "#1E2761" }}>
        <View className="p-6 items-center">
          <Badge variant="gold" className="mb-3">
            Simple Pricing
          </Badge>
          <Text className="text-xl font-extrabold text-white text-center mb-2">
            No Commissions. No Hidden Fees.
          </Text>
          <Text className="text-faint text-center text-sm leading-6 mb-5">
            We charge for information access only — never for transactions,
            introductions, or successful deals.
          </Text>
          <View className="w-full gap-y-3 mb-5">
            <View className="flex-row justify-between items-center bg-white/10 rounded-xl px-4 py-3">
              <View>
                <Text className="text-white font-bold text-sm">Research Membership</Text>
                <Text className="text-faint text-xs">Monthly subscription</Text>
              </View>
              <Text className="text-gold font-extrabold text-lg">₹2,500 + GST</Text>
            </View>
            <View className="flex-row justify-between items-center bg-white/10 rounded-xl px-4 py-3">
              <View>
                <Text className="text-white font-bold text-sm">Company Listing</Text>
                <Text className="text-faint text-xs">Annual listing fee</Text>
              </View>
              <Text className="text-gold font-extrabold text-lg">₹500 + GST</Text>
            </View>
          </View>
          <Link href="/(public)/pricing" asChild>
            <TouchableOpacity className="bg-gold rounded-xl py-3.5 px-8">
              <Text className="text-navy-deep font-extrabold text-base">
                View Full Pricing →
              </Text>
            </TouchableOpacity>
          </Link>
        </View>
      </View>

      {/* ===== SECTION: Platform Sitemap ===== */}
      <View className="px-5 mt-10">
        <Badge variant="info" className="self-center mb-3">
          Platform Map
        </Badge>
        <Text className="text-2xl font-extrabold text-ink text-center mb-2 tracking-tight">
          Complete Sitemap
        </Text>
        <Text className="text-muted text-center text-base leading-6 mb-6">
          DataOfBusiness has {SITEMAP.reduce((a, s) => a + s.pages.length, 0)} screens
          across {SITEMAP.length} role-based sections.
        </Text>

        <View className="gap-y-4">
          {SITEMAP.map((section, i) => (
            <Card key={i} variant="elevated" className="overflow-hidden">
              {/* Section header */}
              <View className={`${section.color} px-4 py-3`}>
                <Text className="text-white font-extrabold text-base">
                  {section.role}
                </Text>
                <Text className="text-white/70 text-xs mt-0.5">
                  {section.pages.length} {section.pages.length === 1 ? "page" : "pages"}
                </Text>
              </View>

              {/* Page links */}
              <View className="px-2 py-1">
                {section.pages.map((page, j) => (
                  <Link key={j} href={page.route} asChild>
                    <TouchableOpacity className="flex-row items-center gap-x-3 px-3 py-3 border-b border-line/60 last:border-b-0 active:bg-bg">
                      <Text className="text-lg">{page.icon}</Text>
                      <View className="flex-1">
                        <Text className="text-sm font-semibold text-ink">
                          {page.name}
                        </Text>
                        <Text className="text-xs text-faint font-mono">
                          {page.route}
                        </Text>
                      </View>
                      <Text className="text-faint text-sm">→</Text>
                    </TouchableOpacity>
                  </Link>
                ))}
              </View>
            </Card>
          ))}
        </View>
      </View>

      {/* ===== SECTION: Legal Disclaimer ===== */}
      <View className="px-5 mt-10 mb-6">
        <Card variant="subtle" className="p-5 bg-blue-light/30 border border-blue/20">
          <Text className="text-xs font-bold text-blue mb-2 tracking-wide uppercase">
            ⚖️ Legal Position
          </Text>
          <Text className="text-xs text-ink leading-6">
            DataOfBusiness is a corporate intelligence database and research platform.
            It is NOT a stock exchange, securities marketplace, broker, investment
            adviser, research analyst, NBFC, P2P platform, fundraising platform, or
            loan marketplace. The platform facilitates no transactions of any kind.
            All subscription fees are for information access only. Company listings
            are for research visibility purposes only. Users are solely responsible
            for any engagement they pursue with listed companies.
          </Text>
          <Text className="text-xs text-muted mt-3 leading-5">
            Compliant with India's DPDP Act 2023. All financial data is CA-certified.
            Payments processed securely via Razorpay.
          </Text>
        </Card>
      </View>

      {/* ===== FOOTER ===== */}
      <View
        className="bg-navy-deep px-6 py-10"
        style={{ borderTopLeftRadius: 24, borderTopRightRadius: 24 }}
      >
        <View className="flex-row items-center gap-x-3 mb-6">
          <View className="bg-navy px-3 py-1.5 rounded-md">
            <Text className="text-gold font-extrabold text-base tracking-wide">
              DoB
            </Text>
          </View>
          <View>
            <Text className="text-white font-bold text-lg">DataOfBusiness</Text>
            <Text className="text-faint text-xs">
              India's Corporate Intelligence Platform
            </Text>
          </View>
        </View>
        <View className="flex-row flex-wrap gap-x-6 gap-y-3 mb-8">
          {["About", "Pricing", "Companies", "Contact", "Privacy", "Terms", "Refund", "Grievance"].map(
            (l) => (
              <Link key={l} href={`/(public)/${l.toLowerCase()}`} asChild>
                <TouchableOpacity>
                  <Text className="text-white/70 text-sm font-medium">{l}</Text>
                </TouchableOpacity>
              </Link>
            )
          )}
        </View>
        <View className="flex-row flex-wrap gap-x-6 gap-y-2 mb-6">
          <Link href="/(public)/business-model" asChild>
            <TouchableOpacity>
              <Text className="text-gold text-sm font-semibold">Business Model & Sitemap</Text>
            </TouchableOpacity>
          </Link>
        </View>
        <View className="h-px bg-white/10 mb-6" />
        <Text className="text-faint text-xs text-center leading-5">
          DataOfBusiness is NOT a stock exchange, broker, investment advisor,
          NBFC, or P2P platform. It is a corporate research & information
          service.
          {"\n"}© 2026 DataOfBusiness. All rights reserved.
        </Text>
      </View>
    </ScrollView>
  );
}
