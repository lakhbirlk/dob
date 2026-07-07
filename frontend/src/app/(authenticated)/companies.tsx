import React, { useState, useCallback } from "react";
import { View, Text, FlatList, TouchableOpacity, Alert } from "react-native";
import { Card } from "@/components/Card";
import { SearchBar } from "@/components/SearchBar";
import { EmptyState } from "@/components/EmptyState";
import { Badge } from "@/components/Badge";
import { LockedCompanyCard } from "@/components/LockedCompanyCard";
import { PremiumCompanyCard } from "@/components/PremiumCompanyCard";
import { UpgradeBottomSheet } from "@/components/UpgradeBottomSheet";
import { colors } from "@/theme/colors";
import { useCompanySearch } from "@/hooks/useCompanies";
import { useSearchStore } from "@/store/searchStore";
import { useAuthStore } from "@/store/authStore";
import { unlockApi } from "@/services/api";
import { router } from "expo-router";
import type { CompanyResponse, FreeCompanyResponse, PremiumCompanyResponse } from "@/types";
import { UserRole } from "@/types";

const SECTORS = ["Technology", "Manufacturing", "Finance", "Healthcare", "Retail", "Energy", "Real Estate"];
const STATES = ["Maharashtra", "Karnataka", "Delhi", "Tamil Nadu", "Gujarat", "Telangana", "Haryana"];
const TYPES = ["Private Limited", "Public Limited", "LLP", "Partnership", "Proprietorship"];

export default function CompaniesScreen() {
  const [searchText, setSearchText] = useState("");
  const { filters, setFilters, setSearch, resetFilters } = useSearchStore();
  const [showFilters, setShowFilters] = useState(false);
  const [upgradeTarget, setUpgradeTarget] = useState<string | undefined>(undefined);
  const [showUpgrade, setShowUpgrade] = useState(false);
  const [unlockingId, setUnlockingId] = useState<string | null>(null);

  const { data, isLoading, error, refetch } = useCompanySearch();
  const { isAuthenticated, user } = useAuthStore();
  const isAdmin = user?.role === UserRole.ADMIN || user?.role === UserRole.SUPER_ADMIN;

  const handleUnlock = useCallback(async (companyId: string) => {
    if (isAdmin) return; // Admins never see upgrade prompts
    if (!isAuthenticated) {
      setUpgradeTarget(companyId);
      setShowUpgrade(true);
      return;
    }

    setUnlockingId(companyId);
    try {
      const result = await unlockApi.unlockCompany(companyId);
      if (result.status === "SUCCESS") {
        // Refetch to update the card from locked → unlocked
        refetch();
        Alert.alert("Company Unlocked!", result.message);
      } else if (result.status === "INSUFFICIENT_CREDITS") {
        // Show upgrade prompt
        setUpgradeTarget(companyId);
        setShowUpgrade(true);
      } else if (result.status === "ALREADY_UNLOCKED") {
        // Navigate directly to company detail
        router.push(`/(authenticated)/company/${companyId}`);
      }
    } catch (err) {
      const message = err instanceof Error ? err.message : "Failed to unlock company";
      Alert.alert("Error", message);
    } finally {
      setUnlockingId(null);
    }
  }, [isAuthenticated, isAdmin, refetch]);

  const handleCompanyPress = useCallback((companyId: string) => {
    router.push(`/(authenticated)/company/${companyId}`);
  }, []);

  const renderCompany = ({ item }: { item: CompanyResponse }) => {
    if (item.locked) {
      // Free user — show masked card
      return (
        <LockedCompanyCard
          company={item as FreeCompanyResponse}
          onUnlock={handleUnlock}
          onPress={handleCompanyPress}
          isUnlocking={unlockingId === item.companyId}
        />
      );
    }
    // Premium user — show full details
    return (
      <PremiumCompanyCard
        company={item as PremiumCompanyResponse}
        onPress={handleCompanyPress}
      />
    );
  };

  return (
    <View className="flex-1 bg-bg">
      <View className="bg-navy-deep px-4 pt-6 pb-5" style={{ borderBottomLeftRadius: 20, borderBottomRightRadius: 20 }}>
        <Text className="text-2xl font-extrabold text-white mb-4">Company Database</Text>
        <SearchBar
          value={searchText}
          onChangeText={setSearchText}
          onSubmit={(q) => { if (q.trim()) { setSearch(q.trim()); } }}
          placeholder="Search by name, sector, or location..."
        />
        <TouchableOpacity onPress={() => setShowFilters(!showFilters)} className="flex-row items-center gap-x-2 mt-3">
          <Text className="text-white/80 font-semibold text-sm">Filters</Text>
          <Text className="text-faint text-xs">{showFilters ? "▲" : "▼"}</Text>
          {Object.values(filters).some(Boolean) && (
            <TouchableOpacity onPress={resetFilters}>
              <Text className="text-gold font-bold text-xs ml-2">Clear All</Text>
            </TouchableOpacity>
          )}
        </TouchableOpacity>

        {showFilters && (
          <View className="mt-4 gap-y-3">
            <FilterRow title="Sector" options={SECTORS} selected={filters.sector || ""} onSelect={(v) => setFilters({ sector: v })} />
            <FilterRow title="State" options={STATES} selected={filters.state || ""} onSelect={(v) => setFilters({ state: v })} />
            <FilterRow title="Type" options={TYPES} selected={filters.companyType || ""} onSelect={(v) => setFilters({ companyType: v })} />
          </View>
        )}
      </View>

      <FlatList
        data={data?.data ?? []}
        renderItem={renderCompany}
        keyExtractor={(item) => item.companyId}
        contentContainerStyle={{ padding: 16 }}
        ListEmptyComponent={
          isLoading ? (
            <View className="items-center py-16">
              <Text className="text-muted text-base">Loading companies...</Text>
            </View>
          ) : error ? (
            <EmptyState
              icon="⚠️"
              title="Could not load companies"
              description={error instanceof Error ? error.message : "Unable to connect to the server. Is the backend running on port 8080?"}
              actionLabel="Retry"
              onAction={() => refetch()}
            />
          ) : (
            <EmptyState
              icon="🏢"
              title="No companies found"
              description="Try adjusting your search or filters"
            />
          )
        }
      />

      {/* Upgrade Bottom Sheet */}
      <UpgradeBottomSheet
        visible={showUpgrade}
        onClose={() => setShowUpgrade(false)}
        companyId={upgradeTarget}
      />
    </View>
  );
}

function FilterRow({ title, options, selected, onSelect }: {
  title: string; options: string[]; selected: string; onSelect: (v: string) => void;
}) {
  return (
    <View>
      <Text className="text-xs font-bold text-white/60 uppercase tracking-wider mb-2">{title}</Text>
      <View className="flex-row flex-wrap gap-1.5">
        {options.map((opt) => (
          <TouchableOpacity
            key={opt}
            onPress={() => onSelect(selected === opt ? "" : opt)}
            className={`px-3 py-1.5 rounded-full border ${selected === opt ? "bg-gold border-gold" : "bg-white/10 border-white/20"}`}
          >
            <Text className={`text-xs font-semibold ${selected === opt ? "text-navy" : "text-white"}`}>{opt}</Text>
          </TouchableOpacity>
        ))}
      </View>
    </View>
  );
}
