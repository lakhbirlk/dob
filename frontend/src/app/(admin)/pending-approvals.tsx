import React, { useState, useEffect, useCallback } from 'react';
import { View, Text, FlatList, TouchableOpacity, TextInput, Alert, ScrollView } from 'react-native';
import { Card } from '@/components/Card';
import { Button } from '@/components/Button';
import { Badge } from '@/components/Badge';
import { EmptyState } from '@/components/EmptyState';
import { Modal } from '@/components/Modal';
import { colors } from '@/theme/colors';
import { adminApi } from '@/services/api';
import type { CompanyDetailDto, CompanyDto } from '@/types';
import { CompanyStatus } from '@/types';

type StatusConfig = Record<string, { variant: 'warning' | 'success' | 'danger' | 'info' | 'neutral'; label: string }>;

const STATUS_CONFIG: StatusConfig = {
  [CompanyStatus.DRAFT]: { variant: 'neutral', label: 'Draft' },
  [CompanyStatus.PENDING_REVIEW]: { variant: 'warning', label: 'Pending Review' },
  [CompanyStatus.REJECTED]: { variant: 'danger', label: 'Rejected' },
  [CompanyStatus.APPROVED_MEMBERSHIP_PENDING]: { variant: 'info', label: 'Approved (No Membership)' },
  [CompanyStatus.APPROVED_ACTIVE]: { variant: 'success', label: 'Approved & Active' },
  [CompanyStatus.MEMBERSHIP_EXPIRED]: { variant: 'neutral', label: 'Membership Expired' },
  [CompanyStatus.SUSPENDED]: { variant: 'danger', label: 'Suspended' },
};

export default function PendingApprovalsScreen() {
  const [pendingCompanies, setPendingCompanies] = useState<CompanyDetailDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedCompany, setSelectedCompany] = useState<CompanyDetailDto | null>(null);
  const [rejectComment, setRejectComment] = useState('');
  const [showRejectModal, setShowRejectModal] = useState(false);
  const [actionLoading, setActionLoading] = useState(false);

  const fetchPending = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await adminApi.getPendingCompaniesWithDetails();
      setPendingCompanies(data || []);
    } catch (err: any) {
      setError(err?.message || 'Failed to load pending companies');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchPending();
  }, [fetchPending]);

  const handleApprove = useCallback(async (companyId: string) => {
    setActionLoading(true);
    try {
      await adminApi.approveCompany(companyId);
      setPendingCompanies(prev => prev.filter(c => c.id !== companyId));
      setSelectedCompany(null);
    } catch (err: any) {
      const message = err?.response?.data?.detail || err?.response?.data?.message || err?.message || 'Failed to approve company';
      Alert.alert('Error', message);
    } finally {
      setActionLoading(false);
    }
  }, []);

  const handleReject = useCallback(async () => {
    if (!selectedCompany) return;
    if (!rejectComment.trim()) {
      Alert.alert('Comment Required', 'Please provide a rejection comment to help the company improve their listing.');
      return;
    }
    setActionLoading(true);
    try {
      await adminApi.rejectCompany(selectedCompany.id, rejectComment.trim());
      setPendingCompanies(prev => prev.filter(c => c.id !== selectedCompany.id));
      setShowRejectModal(false);
      setSelectedCompany(null);
      setRejectComment('');
    } catch (err: any) {
      const message = err?.response?.data?.detail || err?.response?.data?.message || err?.message || 'Failed to reject company';
      Alert.alert('Error', message);
    } finally {
      setActionLoading(false);
    }
  }, [selectedCompany, rejectComment]);

  if (loading) {
    return (
      <View style={{ flex: 1, backgroundColor: colors.bg, justifyContent: 'center', alignItems: 'center' }}>
        <Text style={{ color: colors.muted, fontSize: 15 }}>Loading pending approvals...</Text>
      </View>
    );
  }

  if (error) {
    return (
      <View style={{ flex: 1, backgroundColor: colors.bg, justifyContent: 'center', alignItems: 'center', padding: 16 }}>
        <Text style={{ color: colors.red, fontSize: 15, textAlign: 'center', marginBottom: 12 }}>{error}</Text>
        <Button variant="primary" size="sm" onPress={fetchPending}>Retry</Button>
      </View>
    );
  }

  return (
    <View style={{ flex: 1, backgroundColor: colors.bg }}>
      <View style={{ padding: 16, flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' }}>
        <View>
          <Text style={{ fontSize: 20, fontWeight: '800', color: colors.ink }}>Pending Approvals</Text>
          <Text style={{ fontSize: 13, color: colors.muted, marginTop: 2 }}>
            {pendingCompanies.length} company{pendingCompanies.length !== 1 ? 'ies' : 'y'} awaiting review
          </Text>
        </View>
        <TouchableOpacity onPress={fetchPending}>
          <Text style={{ color: colors.blue, fontSize: 14, fontWeight: '600' }}>Refresh</Text>
        </TouchableOpacity>
      </View>

      <FlatList
        data={pendingCompanies}
        renderItem={({ item }) => (
          <Card variant="bordered" style={{ marginHorizontal: 16, marginBottom: 12 }}>
            <View style={{ flexDirection: 'row', justifyContent: 'space-between', alignItems: 'flex-start' }}>
              <View style={{ flex: 1, marginRight: 12 }}>
                <Text style={{ fontSize: 16, fontWeight: '700', color: colors.navy }}>{item.name}</Text>
                <Text style={{ fontSize: 12, color: colors.muted, marginTop: 2 }}>
                  {item.sector} · {item.city}, {item.state}
                </Text>
                <Text style={{ fontSize: 12, color: colors.faint, marginTop: 1 }}>
                  ID: {item.publicCompanyId}
                </Text>
              </View>
              <Badge variant={STATUS_CONFIG[item.status]?.variant || 'neutral'}>
                <Text style={{ fontSize: 11, fontWeight: '700' }}>
                  {STATUS_CONFIG[item.status]?.label || item.status}
                </Text>
              </Badge>
            </View>

            {item.description ? (
              <Text style={{ fontSize: 13, color: colors.ink, marginTop: 10, lineHeight: 18 }} numberOfLines={3}>
                {item.description}
              </Text>
            ) : null}

            {item.rejectionComment ? (
              <View style={{ marginTop: 8, backgroundColor: '#FEF2F2', borderRadius: 8, padding: 10 }}>
                <Text style={{ fontSize: 11, fontWeight: '700', color: colors.red, marginBottom: 2 }}>Previous Rejection Reason:</Text>
                <Text style={{ fontSize: 12, color: '#991B1B' }}>{item.rejectionComment}</Text>
              </View>
            ) : null}

            <View style={{ flexDirection: 'row', gap: 8, marginTop: 14 }}>
              <Button
                variant="primary"
                size="sm"
                onPress={() => handleApprove(item.id)}
                loading={actionLoading}
                style={{ flex: 1 }}
              >
                Approve
              </Button>
              <Button
                variant="danger"
                size="sm"
                onPress={() => {
                  setSelectedCompany(item);
                  setRejectComment('');
                  setShowRejectModal(true);
                }}
                style={{ flex: 1 }}
              >
                Reject
              </Button>
            </View>
          </Card>
        )}
        keyExtractor={(item) => item.id}
        contentContainerStyle={{ paddingBottom: 24 }}
        ListEmptyComponent={
          <EmptyState icon="✅" title="All caught up" description="No companies pending review" />
        }
      />

      {/* Reject Modal */}
      <Modal visible={showRejectModal} onClose={() => setShowRejectModal(false)} title="Reject Company Listing">
        <View style={{ padding: 4 }}>
          {selectedCompany && (
            <Text style={{ fontSize: 14, fontWeight: '600', color: colors.ink, marginBottom: 8 }}>
              {selectedCompany.name}
            </Text>
          )}
          <Text style={{ fontSize: 13, color: colors.muted, marginBottom: 12 }}>
            Provide feedback to help the company understand why their listing was rejected and what needs to be improved.
          </Text>
          <TextInput
            style={{
              borderWidth: 1, borderColor: colors.line, borderRadius: 8, padding: 12,
              fontSize: 14, color: colors.ink, backgroundColor: '#fff', minHeight: 100,
              textAlignVertical: 'top',
            }}
            placeholder="Enter rejection reason / feedback..."
            multiline
            value={rejectComment}
            onChangeText={setRejectComment}
          />
          <View style={{ flexDirection: 'row', gap: 8, marginTop: 16 }}>
            <Button
              variant="ghost"
              size="md"
              onPress={() => setShowRejectModal(false)}
              style={{ flex: 1 }}
            >
              Cancel
            </Button>
            <Button
              variant="danger"
              size="md"
              onPress={handleReject}
              loading={actionLoading}
              style={{ flex: 1 }}
            >
              Reject & Send Feedback
            </Button>
          </View>
        </View>
      </Modal>
    </View>
  );
}
