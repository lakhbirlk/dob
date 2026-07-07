import React from "react";
import { View, Text, TouchableOpacity, ActivityIndicator } from "react-native";
import { Card } from "@/components/Card";
import { Badge } from "@/components/Badge";
import { colors } from "@/theme/colors";
import type { FreeCompanyResponse } from "@/types";

interface LockedCompanyCardProps {
  company: FreeCompanyResponse;
  onUnlock: (companyId: string) => void;
  onPress: (companyId: string) => void;
  isUnlocking?: boolean;
}

/**
 * LockedCompanyCard — displays masked company data for free (non-subscribed) users.
 *
 * Shows:
 *  - DoB Company ID (e.g. DOB-7F92A1BC)
 *  - Industry, Business Type, State/City
 *  - Company Age, Employee Range, Revenue Range
 *  - Risk Score, Verification Status
 *  - Lock icon + "Unlock Full Report" CTA
 *
 * Never displays company name, CIN, GST, PAN, directors, or contact details.
 */
export const LockedCompanyCard: React.FC<LockedCompanyCardProps> = ({
  company,
  onUnlock,
  onPress,
  isUnlocking,
}) => {
  return (
    <TouchableOpacity
      onPress={() => onPress(company.companyId)}
      activeOpacity={0.85}
    >
      <Card variant="elevated" className="mb-3">
        {/* Header: DoB ID + Lock Badge */}
        <View className="flex-row justify-between items-center mb-3">
          <View className="flex-row items-center gap-x-2">
            <Text className="text-lg">🔒</Text>
            <Text className="text-sm font-bold text-navy font-mono">
              {company.companyId}
            </Text>
          </View>
          <Badge variant="gold">
            <Text className="text-[10px] font-extrabold">LOCKED</Text>
          </Badge>
        </View>

        {/* Verification Status */}
        <View className="flex-row items-center gap-x-2 mb-3">
          {company.verified ? (
            <Badge variant="success">
              <Text className="text-xs font-extrabold">✓ Verified</Text>
            </Badge>
          ) : (
            <Badge variant="neutral">
              <Text className="text-xs font-extrabold">Pending Verification</Text>
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
              <Text className="text-xs font-extrabold">
                Risk: {company.riskScore}
              </Text>
            </Badge>
          )}
        </View>

        {/* Masked Info Row */}
        <View className="flex-row flex-wrap gap-y-2">
          <InfoItem label="Industry" value={company.industry} />
          <InfoItem label="Type" value={company.businessType} />
          <InfoItem label="Location" value={[company.city, company.state].filter(Boolean).join(", ")} />
          <InfoItem label="Age" value={company.companyAge != null ? `${company.companyAge} yrs` : null} />
          <InfoItem label="Employees" value={company.employeeRange} />
          <InfoItem label="Revenue" value={company.revenueRange} />
        </View>

        {/* Summary */}
        {company.summary && (
          <Text className="text-sm text-muted mt-3 leading-5" numberOfLines={2}>
            {company.summary}
          </Text>
        )}

        {/* Horizontal Rule */}
        <View className="h-px bg-line my-3" />

        {/* Unlock CTA */}
        <TouchableOpacity
          onPress={() => onUnlock(company.companyId)}
          disabled={isUnlocking}
          className={`flex-row items-center justify-center gap-x-2 rounded-lg py-3 border ${
            isUnlocking ? "bg-gold/5 border-gold/20" : "bg-gold/10 border-gold/30"
          }`}
          activeOpacity={0.7}
        >
          {isUnlocking ? (
            <ActivityIndicator size="small" color="#C49A35" />
          ) : (
            <Text className="text-lg">🔓</Text>
          )}
          <Text className="text-sm font-bold text-gold-dark">
            {isUnlocking ? "Unlocking..." : "Unlock with Credits"}
          </Text>
        </TouchableOpacity>
      </Card>
    </TouchableOpacity>
  );
};

function InfoItem({ label, value }: { label: string; value: string | null | undefined }) {
  if (!value) return null;
  return (
    <View className="w-1/2 pr-2 mb-1">
      <Text className="text-[10px] text-faint font-semibold uppercase tracking-wider">
        {label}
      </Text>
      <Text className="text-xs text-ink font-medium mt-0.5">{value}</Text>
    </View>
  );
}

export default LockedCompanyCard;
