import React from "react";
import { View, Text, TouchableOpacity } from "react-native";
import { Card } from "@/components/Card";
import { Badge } from "@/components/Badge";
import { PremiumBadge } from "@/components/PremiumBadge";
import type { PremiumCompanyResponse } from "@/types";

interface PremiumCompanyCardProps {
  company: PremiumCompanyResponse;
  onPress: (companyId: string) => void;
}

/**
 * PremiumCompanyCard — displays full company details for subscribed users.
 * Shows company name, industry, location, and key identifiers.
 */
export const PremiumCompanyCard: React.FC<PremiumCompanyCardProps> = ({
  company,
  onPress,
}) => {
  return (
    <TouchableOpacity
      onPress={() => onPress(company.companyId)}
      activeOpacity={0.85}
    >
      <Card variant="elevated" className="mb-3">
        {/* Header: Name + Premium Badge */}
        <View className="flex-row justify-between items-start mb-2">
          <View className="flex-1 mr-3">
            <Text className="text-base font-bold text-navy mb-1">
              {company.companyName}
            </Text>
            <Text className="text-xs text-faint font-mono">
              {company.companyId}
            </Text>
          </View>
          <PremiumBadge size="sm" />
        </View>

        {/* Meta */}
        <Text className="text-sm text-muted mb-2">
          {[company.industry, company.city, company.state]
            .filter(Boolean)
            .join(" · ")}
        </Text>

        {/* Badges Row */}
        <View className="flex-row flex-wrap gap-x-2 gap-y-1 mb-3">
          {company.businessType && (
            <Badge variant="info">
              <Text className="text-xs font-extrabold">{company.businessType}</Text>
            </Badge>
          )}
          {company.verified && (
            <Badge variant="success">
              <Text className="text-xs font-extrabold">✓ CA Verified</Text>
            </Badge>
          )}
          {company.companyAge != null && (
            <Badge variant="neutral">
              <Text className="text-xs font-extrabold">{company.companyAge} yrs</Text>
            </Badge>
          )}
          {company.riskScore && (
            <Badge
              variant={
                company.riskScore === "Low"
                  ? "success"
                  : company.riskScore === "High"
                  ? "danger"
                  : "warning"
              }
            >
              <Text className="text-xs font-extrabold">Risk: {company.riskScore}</Text>
            </Badge>
          )}
        </View>

        {/* Business Info */}
        <View className="flex-row flex-wrap gap-y-1.5">
          {company.employeeRange && (
            <MiniStat label="Employees" value={company.employeeRange} />
          )}
          {company.revenueRange && (
            <MiniStat label="Revenue" value={company.revenueRange} />
          )}
          {company.cin && (
            <MiniStat label="CIN" value={company.cin} />
          )}
        </View>

        {/* Description */}
        {company.description && (
          <Text
            className="text-sm text-ink mt-2 leading-5"
            numberOfLines={2}
          >
            {company.description}
          </Text>
        )}

        {/* CIN/GST/PAN tags */}
        <View className="flex-row flex-wrap gap-x-2 gap-y-1 mt-3">
          {company.cin && (
            <Text className="text-[10px] text-faint font-mono">
              CIN: {company.cin}
            </Text>
          )}
          {company.gstin && (
            <Text className="text-[10px] text-faint font-mono">
              GST: {company.gstin}
            </Text>
          )}
        </View>
      </Card>
    </TouchableOpacity>
  );
};

function MiniStat({ label, value }: { label: string; value: string }) {
  return (
    <View className="w-1/2 pr-2 mb-0.5">
      <Text className="text-[9px] text-faint font-semibold uppercase tracking-wider">
        {label}
      </Text>
      <Text className="text-xs text-ink font-medium">{value}</Text>
    </View>
  );
}

export default PremiumCompanyCard;
