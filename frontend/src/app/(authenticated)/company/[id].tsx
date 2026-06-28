import React, { useState } from "react";
import { View, Text, ScrollView, TouchableOpacity } from "react-native";
import { useLocalSearchParams, router } from "expo-router";
import { Card } from "@/components/Card";
import { Button } from "@/components/Button";
import { Badge } from "@/components/Badge";
import { PremiumBadge } from "@/components/PremiumBadge";
import { UpgradeBottomSheet } from "@/components/UpgradeBottomSheet";
import { Divider } from "@/components/Divider";
import { colors } from "@/theme/colors";
import { useCompanyDetail } from "@/hooks/useCompanies";
import { useAuthStore } from "@/store/authStore";
import type {
  CompanyResponse,
  FreeCompanyResponse,
  PremiumCompanyResponse,
} from "@/types";
import { UserRole } from "@/types";

function formatINR(v?: number) {
  if (v == null) return "—";
  if (v >= 1e7) return `₹${(v / 1e7).toFixed(1)} Cr`;
  if (v >= 1e5) return `₹${(v / 1e5).toFixed(1)} L`;
  return `₹${v.toLocaleString("en-IN")}`;
}

export default function CompanyDetailScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const { data: response, isLoading } = useCompanyDetail(id);
  const [showUpgrade, setShowUpgrade] = useState(false);
  const { isAuthenticated, user } = useAuthStore();
  const isAdmin = user?.role === UserRole.ADMIN || user?.role === UserRole.SUPER_ADMIN;

  if (isLoading || !response) {
    return (
      <View className="flex-1 bg-bg items-center justify-center">
        <Text className="text-muted text-base">Loading company profile...</Text>
      </View>
    );
  }

  // ── ADMIN OVERRIDE ──
  // Admins always see full data regardless of subscription status
  if (isAdmin && response.locked) {
    response.locked = false;
  }

  // ── FREE USER VIEW (Locked) ──
  if (response.locked) {
    const company = response as FreeCompanyResponse;
    return (
      <ScrollView className="flex-1 bg-bg">
        {/* Locked Hero */}
        <View className="bg-navy-deep pt-10 pb-8 px-5" style={{ borderBottomLeftRadius: 24, borderBottomRightRadius: 24 }}>
          <View className="flex-row items-center gap-x-3 mb-3">
            <Text className="text-3xl">🔒</Text>
            <View>
              <Text className="text-2xl font-extrabold text-white font-mono">
                {company.companyId}
              </Text>
              <Text className="text-xs text-faint font-medium mt-1">
                Company ID
              </Text>
            </View>
          </View>
          <Text className="text-base text-faint mb-4">
            {[company.industry, company.city, company.state]
              .filter(Boolean)
              .join(" · ") || "Company information locked"}
          </Text>
          <View className="flex-row gap-x-2 flex-wrap">
            {company.businessType && (
              <Badge variant="info">
                <Text className="text-xs font-extrabold">{company.businessType}</Text>
              </Badge>
            )}
            {company.verified ? (
              <PremiumBadge size="sm" />
            ) : (
              <Badge variant="neutral">
                <Text className="text-xs font-extrabold">Pending Verification</Text>
              </Badge>
            )}
          </View>
        </View>

        {/* Masked Info */}
        <View className="px-5 pt-6 gap-y-5 pb-16">
          {/* Non-identifying details */}
          <Card variant="elevated">
            <Text className="text-lg font-extrabold text-navy mb-4">
              Company Overview
            </Text>
            <View className="flex-row flex-wrap gap-y-4">
              <DetailItem label="Industry" value={company.industry} />
              <DetailItem label="Business Type" value={company.businessType} />
              <DetailItem label="State" value={company.state} />
              <DetailItem label="City" value={company.city} />
              <DetailItem label="Company Age" value={company.companyAge != null ? `${company.companyAge} years` : null} />
              <DetailItem label="Employees" value={company.employeeRange} />
              <DetailItem label="Revenue Range" value={company.revenueRange} />
              <DetailItem label="Risk Score" value={company.riskScore} />
            </View>
            {company.summary && (
              <>
                <Divider />
                <Text className="text-sm text-ink leading-6 mt-3">
                  {company.summary}
                </Text>
              </>
            )}
          </Card>

          {/* Locked sections */}
          <LockedSection icon="📊" title="Financial Statements" />
          <LockedSection icon="👥" title="Directors & Shareholding" />
          <LockedSection icon="📋" title="Compliance & Legal Documents" />
          <LockedSection icon="📍" title="Address & Contact Details" />
          <LockedSection icon="🤖" title="AI Risk Analysis Report" />

          {/* Upgrade CTA */}
          <Card variant="subtle" className="items-center py-6">
            <View className="w-16 h-16 rounded-full bg-gold/15 items-center justify-center mb-4">
              <Text className="text-3xl">🔓</Text>
            </View>
            <Text className="text-xl font-extrabold text-navy text-center mb-2">
              Unlock Full Company Report
            </Text>
            <Text className="text-sm text-muted text-center mb-1 px-4 leading-5">
              Get access to CA-certified financials, director details,
              shareholding patterns, and AI-powered risk analysis.
            </Text>
            <View className="flex-row items-center gap-x-2 mt-2 mb-5">
              <Badge variant="gold">
                <Text className="text-xs font-extrabold">₹2,500/month</Text>
              </Badge>
              <Badge variant="success">
                <Text className="text-xs font-extrabold">50 Downloads/mo</Text>
              </Badge>
            </View>
            <Button
              variant="gold"
              size="xl"
              onPress={() => setShowUpgrade(true)}
            >
              Subscribe Now
            </Button>
          </Card>
        </View>

        {/* Upgrade Sheet */}
        <UpgradeBottomSheet
          visible={showUpgrade}
          onClose={() => setShowUpgrade(false)}
          companyId={company.companyId}
        />
      </ScrollView>
    );
  }

  // ── PREMIUM USER VIEW (Full data) ──
  const company = response as PremiumCompanyResponse;

  return (
    <ScrollView className="flex-1 bg-bg">
      {/* Hero Header */}
      <View className="bg-navy-deep pt-10 pb-8 px-5" style={{ borderBottomLeftRadius: 24, borderBottomRightRadius: 24 }}>
        <View className="flex-row justify-between items-start mb-2">
          <View className="flex-1 mr-3">
            <Text className="text-3xl font-extrabold text-white mb-1">
              {company.companyName}
            </Text>
            <Text className="text-xs text-faint font-mono">
              {company.companyId}
            </Text>
          </View>
          <PremiumBadge size="md" />
        </View>
        <Text className="text-base text-faint mb-4">
          {[company.industry, company.city, company.state]
            .filter(Boolean)
            .join(" · ")}
        </Text>
        <View className="flex-row gap-x-2 flex-wrap">
          {company.businessType && (
            <Badge variant="info">
              <Text className="text-xs font-extrabold">{company.businessType}</Text>
            </Badge>
          )}
          {company.incorporationYear && (
            <Badge variant="neutral">
              <Text className="text-xs font-extrabold">Est. {company.incorporationYear}</Text>
            </Badge>
          )}
          {company.verified && (
            <Badge variant="success">
              <Text className="text-xs font-extrabold">✓ CA Verified</Text>
            </Badge>
          )}
          {company.riskScore && (
            <Badge variant={company.riskScore === "Low" ? "success" : company.riskScore === "High" ? "danger" : "warning"}>
              <Text className="text-xs font-extrabold">Risk: {company.riskScore}</Text>
            </Badge>
          )}
        </View>
      </View>

      <View className="px-5 pt-6 gap-y-5 pb-16">
        {/* Identifiers */}
        {(company.cin || company.gstin || company.pan) && (
          <Card variant="elevated">
            <Text className="text-lg font-extrabold text-navy mb-4">
              Business Identifiers
            </Text>
            {company.cin && <DetailItem label="CIN" value={company.cin} />}
            {company.gstin && <DetailItem label="GSTIN" value={company.gstin} />}
            {company.pan && <DetailItem label="PAN" value={company.pan} />}
          </Card>
        )}

        {/* About */}
        {company.description && (
          <Card variant="elevated">
            <Text className="text-lg font-extrabold text-navy mb-3">
              About the Company
            </Text>
            <Text className="text-sm text-ink leading-6">
              {company.description}
            </Text>
            {company.website && (
              <Text className="text-sm text-blue mt-3 font-medium">
                🌐 {company.website}
              </Text>
            )}
            {company.email && (
              <Text className="text-sm text-blue mt-1 font-medium">
                ✉️ {company.email}
              </Text>
            )}
          </Card>
        )}

        {/* Address */}
        {company.address && (
          <Card variant="elevated">
            <Text className="text-lg font-extrabold text-navy mb-3">
              Registered Address
            </Text>
            <Text className="text-sm text-ink leading-6">
              {company.address}
            </Text>
            <Text className="text-sm text-muted mt-1">
              {[company.city, company.state].filter(Boolean).join(", ")}
            </Text>
          </Card>
        )}

        {/* Company Details */}
        <Card variant="elevated">
          <Text className="text-lg font-extrabold text-navy mb-4">
            Company Details
          </Text>
          <View className="flex-row flex-wrap gap-y-4">
            <DetailItem label="Industry" value={company.industry} />
            <DetailItem label="Sub-Sector" value={company.subSector} />
            <DetailItem label="Business Type" value={company.businessType} />
            <DetailItem label="Incorporation" value={company.incorporationYear?.toString()} />
            <DetailItem label="Company Age" value={company.companyAge != null ? `${company.companyAge} years` : null} />
            <DetailItem label="Employees" value={company.employeeRange} />
            <DetailItem label="Revenue Range" value={company.revenueRange} />
            <DetailItem label="Risk Score" value={company.riskScore} />
          </View>
        </Card>

        {/* Key Executives */}
        {company.keyExecutives && company.keyExecutives.length > 0 && (
          <Card variant="elevated">
            <Text className="text-lg font-extrabold text-navy mb-4">
              👥 Key Executives & Directors
            </Text>
            {company.keyExecutives.map((exec, i) => (
              <View key={i} className="flex-row items-center gap-x-3 py-2">
                <View className="w-10 h-10 rounded-full bg-navy/10 items-center justify-center">
                  <Text className="text-lg">👤</Text>
                </View>
                <View className="flex-1">
                  <Text className="text-sm font-bold text-ink">
                    {exec.name || exec.directorName || "—"}
                  </Text>
                  <Text className="text-xs text-muted">
                    {exec.designation || exec.role || "Director"}
                  </Text>
                </View>
              </View>
            ))}
          </Card>
        )}

        {/* Financials */}
        {company.financials && company.financials.length > 0 && (
          <Card variant="elevated">
            <Text className="text-lg font-extrabold text-navy mb-4">
              📊 Financial Statements
            </Text>
            {company.financials.map((fin, i) => (
              <View key={fin.id ?? i}>
                {i > 0 && <Divider />}
                <View className="flex-row justify-between items-center mb-2">
                  <Text className="text-base font-bold text-ink">
                    FY {fin.year}
                  </Text>
                  {fin.isVerified && (
                    <Badge variant="success">
                      <Text className="text-xs font-extrabold">✓ CA Verified</Text>
                    </Badge>
                  )}
                </View>
                <View className="flex-row gap-x-6">
                  <Finance label="Revenue" value={fin.revenue} />
                  <Finance label="Profit" value={fin.profit} />
                  <Finance label="Assets" value={fin.assets} />
                  <Finance label="Liabilities" value={fin.liabilities} />
                </View>
              </View>
            ))}
          </Card>
        )}

        {/* Certificates */}
        {company.certificates && company.certificates.length > 0 && (
          <Card variant="elevated">
            <Text className="text-lg font-extrabold text-navy mb-4">
              📜 CA Certificates
            </Text>
            {company.certificates.map((cert, i) => (
              <View key={i} className="flex-row items-center gap-x-3 py-2">
                <View className="w-10 h-10 rounded-lg bg-green-light items-center justify-center">
                  <Text className="text-lg">✅</Text>
                </View>
                <View className="flex-1">
                  <Text className="text-sm font-bold text-ink">
                    {cert.name}
                  </Text>
                  <Text className="text-xs text-muted">
                    Authority: {cert.issuingAuthority}
                  </Text>
                </View>
              </View>
            ))}
          </Card>
        )}

        {/* Videos */}
        {company.videos && company.videos.length > 0 && (
          <Card variant="elevated">
            <Text className="text-lg font-extrabold text-navy mb-4">
              🎥 Company Videos
            </Text>
            {company.videos.map((v, i) => (
              <View key={i} className="flex-row items-center gap-x-3 py-2">
                <View className="w-10 h-10 rounded-lg bg-navy/10 items-center justify-center">
                  <Text className="text-lg">▶️</Text>
                </View>
                <View className="flex-1">
                  <Text className="text-sm font-bold text-ink">{v.title}</Text>
                  {v.duration && (
                    <Text className="text-xs text-muted">
                      {Math.floor(v.duration / 60)}:
                      {(v.duration % 60).toString().padStart(2, "0")} min
                    </Text>
                  )}
                </View>
              </View>
            ))}
          </Card>
        )}

        {/* AI Analysis */}
        {company.aiAnalysis && (
          <Card variant="elevated">
            <Text className="text-lg font-extrabold text-navy mb-3">
              🤖 AI Analysis
            </Text>
            <Text className="text-sm text-ink leading-6">
              {company.aiAnalysis}
            </Text>
          </Card>
        )}

        {/* Download CTA */}
        <Card variant="subtle" className="items-center py-6">
          <Text className="text-lg font-extrabold text-navy text-center mb-2">
            Download Full Report
          </Text>
          <Text className="text-sm text-muted text-center mb-5 px-4">
            Get a comprehensive PDF report including all financials, certificates,
            and risk analysis.
          </Text>
          <Button
            variant="gold"
            size="xl"
            disabled={!company.canDownload}
            onPress={() => {
              /* Wire to download service */
            }}
          >
            {company.canDownload
              ? "Download Report (PDF)"
              : "Download limit reached"}
          </Button>
        </Card>
      </View>
    </ScrollView>
  );
}

// ──────────── Sub-components ────────────

function DetailItem({
  label,
  value,
}: {
  label: string;
  value?: string | null;
}) {
  if (!value) return null;
  return (
    <View className="w-1/2 pr-3">
      <Text className="text-xs text-faint font-semibold uppercase tracking-wider">
        {label}
      </Text>
      <Text className="text-sm font-bold text-ink mt-0.5">{value}</Text>
    </View>
  );
}

function Finance({
  label,
  value,
}: {
  label: string;
  value?: number;
}) {
  return (
    <View className="flex-1">
      <Text className="text-xs text-faint font-medium">{label}</Text>
      <Text className="text-sm font-bold text-ink mt-1">
        {formatINR(value)}
      </Text>
    </View>
  );
}

function LockedSection({
  icon,
  title,
}: {
  icon: string;
  title: string;
}) {
  return (
    <Card variant="bordered" className="flex-row items-center gap-x-4 py-4 opacity-60">
      <Text className="text-2xl">{icon}</Text>
      <View className="flex-1">
        <Text className="text-base font-bold text-navy">{title}</Text>
        <Text className="text-xs text-muted mt-0.5">
          🔒 Subscribe to unlock
        </Text>
      </View>
      <Text className="text-gold text-lg">🔒</Text>
    </Card>
  );
}
