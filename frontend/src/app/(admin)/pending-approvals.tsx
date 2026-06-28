import React from 'react';
import { View, Text, FlatList, TouchableOpacity } from 'react-native';
import { Card } from '@/components/Card';
import { Button } from '@/components/Button';
import { EmptyState } from '@/components/EmptyState';
import { colors } from '@/theme/colors';

export default function PendingApprovalsScreen() {
  // In production: fetch from adminApi.getPendingCompanies()
  const pending: any[] = [];

  return (
    <View style={{ flex: 1, backgroundColor: colors.bg }}>
      <View style={{ padding: 16 }}>
        <Text style={{ fontSize: 20, fontWeight: '800', color: colors.ink }}>Pending Company Approvals</Text>
      </View>
      <FlatList
        data={pending}
        renderItem={({ item }) => (
          <Card variant="bordered" style={{ marginHorizontal: 16, marginBottom: 8 }}>
            <Text style={{ fontSize: 15, fontWeight: '700', color: colors.navy }}>{item.name}</Text>
            <Text style={{ fontSize: 13, color: colors.muted }}>{item.sector} · {item.city}, {item.state}</Text>
            <Text style={{ fontSize: 13, color: colors.ink, marginTop: 8 }} numberOfLines={2}>{item.description}</Text>
            <View style={{ flexDirection: 'row', gap: 8, marginTop: 12 }}>
              <Button variant="primary" size="sm" onPress={() => {}}>Approve</Button>
              <Button variant="danger" size="sm" onPress={() => {}}>Reject</Button>
            </View>
          </Card>
        )}
        keyExtractor={(_, i) => i.toString()}
        ListEmptyComponent={
          <EmptyState icon="✅" title="All caught up" description="No pending company approvals" />
        }
      />
    </View>
  );
}
