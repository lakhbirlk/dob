import React, { useState, useEffect, useCallback } from 'react';
import { View, Text, FlatList, TouchableOpacity, Alert, ScrollView } from 'react-native';
import { Card } from '@/components/Card';
import { Badge } from '@/components/Badge';
import { Button } from '@/components/Button';
import { EmptyState } from '@/components/EmptyState';
import { colors } from '@/theme/colors';
import { companiesApi } from '@/services/api';
import type { CompanyDetailDto } from '@/types';
import { CompanyStatus } from '@/types';
import { router } from 'expo-router';

type StatusConfig = Record<string, { variant: 'warning' | 'success' | 'danger' | 'info' | 'neutral'; label: string; description: string }>;

const STATUS_CONFIG: StatusConfig = {
  [CompanyStatus.DRAFT]: {
    variant: 'neutral', label: 'Draft',
    description: 'Complete your company profile and submit for review.',
  },
  [CompanyStatus.PENDING_REVIEW]: {
    variant: 'warning', label: 'Pending Review',
    description: 'Submitted for admin review. You will be notified once reviewed.',
  },
  [CompanyStatus.REJECTED]: {
    variant: 'danger', label: 'Rejected',
    description: 'Review the feedback, make corrections, and resubmit.',
  },
  [CompanyStatus.APPROVED_MEMBERSHIP_PENDING]: {
    variant: 'info', label: 'Approved',
    description: 'Approved by admin! Purchase a listing membership to publish your company.',
  },
  [CompanyStatus.APPROVED_ACTIVE]: {
    variant: 'success', label: 'Published',
    description: 'Your company is publicly visible in the Company Database.',
  },
  [CompanyStatus.MEMBERSHIP_EXPIRED]: {
    variant: 'neutral', label: 'Expired',
    description: 'Listing membership has expired. Renew to restore public visibility.',
  },
  [CompanyStatus.SUSPENDED]: {
    variant: 'danger', label: 'Suspended',
    description: 'Your listing has been suspended by an admin.',
  },
};

export default function ListingStatusScreen() {
  const [myCompanies, setMyCompanies] = useState<CompanyDetailDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [actionLoading, setActionLoading] = useState<string | null>(null);

  const fetchCompanies = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await companiesApi.getMyCompanyDetails();
      setMyCompanies(data || []);
    } catch (err: any) {
      setError(err?.message || 'Failed to load companies');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchCompanies();
  }, [fetchCompanies]);

  const handleSubmit = useCallback(async (companyId: string) => {
    setActionLoading(companyId);
    try {
      await companiesApi.submitForReview(companyId);
      Alert.alert('Submitted', 'Your company has been submitted for admin review.');
      fetchCompanies();
    } catch (err: any) {
      Alert.alert('Error', err?.message || 'Failed to submit for review');
    } finally {
      setActionLoading(null);
    }
  }, [fetchCompanies]);

  const handleResubmit = useCallback(async (companyId: string) => {
    setActionLoading(companyId);
    try {
      await companiesApi.resubmitForReview(companyId);
      Alert.alert('Resubmitted', 'Your company has been resubmitted for admin review.');
      fetchCompanies();
    } catch (err: any) {
      Alert.alert('Error', err?.message || 'Failed to resubmit');
    } finally {
      setActionLoading(null);
    }
  }, [fetchCompanies]);

  const handleActivateListing = useCallback(async (companyId: string) => {
    setActionLoading(companyId);
    try {
      await companiesApi.activateListingMembership(companyId);
      Alert.alert('Success', 'Listing membership activated! Your company is now publicly visible.');
      fetchCompanies();
    } catch (err: any) {
      Alert.alert('Error', err?.message || 'Failed to activate listing membership');
    } finally {
      setActionLoading(null);
    }
  }, [fetchCompanies]);

  const renderStatusActions = (company: CompanyDetailDto) => {
    switch (company.status) {
      case CompanyStatus.DRAFT:
        return (
          <Button
            variant="gold"
            size="md"
            onPress={() => handleSubmit(company.id)}
            loading={actionLoading === company.id}
          >
            Submit for Review
          </Button>
        );

      case CompanyStatus.PENDING_REVIEW:
        return (
          <View style={{ backgroundColor: '#FFFBF1', borderRadius: 8, padding: 10 }}>
            <Text style={{ fontSize: 12, color: '#C49A35', textAlign: 'center', fontWeight: '600' }}>
              ⏳ Awaiting admin review. This typically takes 1-2 business days.
            </Text>
          </View>
        );

      case CompanyStatus.REJECTED:
        return (
          <View style={{ gap: 8 }}>
            {company.rejectionComment && (
              <View style={{ backgroundColor: '#FEF2F2', borderRadius: 8, padding: 10 }}>
                <Text style={{ fontSize: 11, fontWeight: '700', color: colors.red, marginBottom: 2 }}>
                  Feedback from Admin:
                </Text>
                <Text style={{ fontSize: 12, color: '#991B1B' }}>{company.rejectionComment}</Text>
              </View>
            )}
            <Button
              variant="gold"
              size="md"
              onPress={() => handleResubmit(company.id)}
              loading={actionLoading === company.id}
            >
              Make Changes & Resubmit
            </Button>
          </View>
        );

      case CompanyStatus.APPROVED_MEMBERSHIP_PENDING:
        return (
          <View style={{ gap: 8 }}>
            <View style={{ backgroundColor: '#E8F7F1', borderRadius: 8, padding: 10 }}>
              <Text style={{ fontSize: 12, color: '#0B6B4F', textAlign: 'center', fontWeight: '600' }}>
                ✓ Approved by admin! Activate your listing membership to go public.
              </Text>
            </View>
            <Button
              variant="gold"
              size="md"
              onPress={() => handleActivateListing(company.id)}
              loading={actionLoading === company.id}
            >
              Purchase Listing Membership (₹500/yr)
            </Button>
          </View>
        );

      case CompanyStatus.APPROVED_ACTIVE:
        return (
          <View style={{ gap: 8 }}>
            <View style={{ backgroundColor: '#E8F7F1', borderRadius: 8, padding: 10 }}>
              <Text style={{ fontSize: 12, color: '#0B6B4F', textAlign: 'center', fontWeight: '600' }}>
                ✓ Publicly visible in Company Database
              </Text>
              {company.listingExpiresAt && (
                <Text style={{ fontSize: 11, color: '#0B6B4F', textAlign: 'center', marginTop: 2 }}>
                  Membership valid until {new Date(company.listingExpiresAt).toLocaleDateString()}
                </Text>
              )}
            </View>
          </View>
        );

      case CompanyStatus.MEMBERSHIP_EXPIRED:
        return (
          <Button
            variant="gold"
            size="md"
            onPress={() => handleActivateListing(company.id)}
            loading={actionLoading === company.id}
          >
            Renew Listing Membership (₹500/yr)
          </Button>
        );

      case CompanyStatus.SUSPENDED:
        return (
          <View style={{ backgroundColor: '#FEF2F2', borderRadius: 8, padding: 10 }}>
            <Text style={{ fontSize: 12, color: '#B91C1C', textAlign: 'center', fontWeight: '600' }}>
              ⚠️ This listing has been suspended by an admin. Contact support for details.
            </Text>
          </View>
        );

      default:
        return null;
    }
  };

  if (loading) {
    return (
      <View style={{ flex: 1, backgroundColor: colors.bg, justifyContent: 'center', alignItems: 'center' }}>
        <Text style={{ color: colors.muted, fontSize: 15 }}>Loading your listings...</Text>
      </View>
    );
  }

  if (error) {
    return (
      <View style={{ flex: 1, backgroundColor: colors.bg, justifyContent: 'center', alignItems: 'center', padding: 16 }}>
        <Text style={{ color: colors.red, fontSize: 14, textAlign: 'center', marginBottom: 12 }}>{error}</Text>
        <Button variant="primary" size="sm" onPress={fetchCompanies}>Retry</Button>
      </View>
    );
  }

  return (
    <View style={{ flex: 1, backgroundColor: colors.bg }}>
      <View style={{ padding: 16 }}>
        <Text style={{ fontSize: 20, fontWeight: '800', color: colors.ink }}>My Company Listings</Text>
        <Text style={{ fontSize: 13, color: colors.muted, marginTop: 2 }}>
          Track the status of your company listings through the approval workflow.
        </Text>
      </View>

      <FlatList
        data={myCompanies}
        renderItem={({ item: company }) => {
          const config = STATUS_CONFIG[company.status] || { variant: 'neutral' as const, label: company.status, description: '' };
          return (
            <Card variant="bordered" style={{ marginHorizontal: 16, marginBottom: 12 }}>
              {/* Header */}
              <View style={{ flexDirection: 'row', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                <View style={{ flex: 1, marginRight: 12 }}>
                  <Text style={{ fontSize: 16, fontWeight: '700', color: colors.navy }}>{company.name}</Text>
                  <Text style={{ fontSize: 12, color: colors.faint, marginTop: 2 }}>
                    ID: {company.publicCompanyId}
                  </Text>
                  {company.sector && (
                    <Text style={{ fontSize: 12, color: colors.muted, marginTop: 1 }}>
                      {company.sector}{company.city ? ` · ${company.city}` : ''}
                    </Text>
                  )}
                </View>
                <Badge variant={config.variant}>
                  <Text style={{ fontSize: 11, fontWeight: '700' }}>{config.label}</Text>
                </Badge>
              </View>

              {/* Status Description */}
              <Text style={{ fontSize: 13, color: colors.ink, marginTop: 10, lineHeight: 18 }}>
                {config.description}
              </Text>

              {/* Action Buttons */}
              <View style={{ marginTop: 12 }}>
                {renderStatusActions(company)}
              </View>

              {/* Timeline / Info */}
              <View style={{ marginTop: 12, paddingTop: 10, borderTopWidth: 1, borderTopColor: colors.line }}>
                {company.submittedAt && (
                  <Text style={{ fontSize: 11, color: colors.faint }}>
                    Submitted: {new Date(company.submittedAt).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' })}
                  </Text>
                )}
                {company.approvedAt && (
                  <Text style={{ fontSize: 11, color: colors.faint, marginTop: 1 }}>
                    {company.status === CompanyStatus.REJECTED ? 'Rejected' : 'Reviewed'}: {new Date(company.approvedAt).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' })}
                  </Text>
                )}
                {company.listingExpiresAt && (
                  <Text style={{ fontSize: 11, color: colors.faint, marginTop: 1 }}>
                    Listing valid until: {new Date(company.listingExpiresAt).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' })}
                  </Text>
                )}
                <View style={{ flexDirection: 'row', gap: 6, marginTop: 6 }}>
                  {company.isPubliclyVisible && (
                    <Badge variant="success">
                      <Text style={{ fontSize: 10, fontWeight: '700' }}>✓ Public</Text>
                    </Badge>
                  )}
                  {company.hasActiveListingMembership && (
                    <Badge variant="info">
                      <Text style={{ fontSize: 10, fontWeight: '700' }}>Active Membership</Text>
                    </Badge>
                  )}
                </View>
              </View>
            </Card>
          );
        }}
        keyExtractor={(item) => item.id}
        contentContainerStyle={{ paddingBottom: 24 }}
        ListEmptyComponent={
          <EmptyState
            icon="📋"
            title="No listings yet"
            description="Create your first company listing to get started with the approval workflow."
            action={
              <Button variant="gold" size="md" onPress={() => router.push('/(company)/create-listing')}>
                Create Listing
              </Button>
            }
          />
        }
      />
    </View>
  );
}
