import React from 'react';
import { View, Text, FlatList } from 'react-native';
import { Card } from '@/components/Card';
import { Button } from '@/components/Button';
import { EmptyState } from '@/components/EmptyState';
import { colors } from '@/theme/colors';

export default function RefundsManagementScreen() {
  // In production: fetch from adminApi.getRefunds()
  const refunds: any[] = [];

  return (
    <View style={{ flex: 1, backgroundColor: colors.bg }}>
      <View style={{ padding: 16 }}>
        <Text style={{ fontSize: 20, fontWeight: '800', color: colors.ink }}>Refund Requests</Text>
      </View>
      <FlatList
        data={refunds}
        renderItem={({ item }) => (
          <Card variant="bordered" style={{ marginHorizontal: 16, marginBottom: 8 }}>
            <View style={{ flexDirection: 'row', justifyContent: 'space-between' }}>
              <View>
                <Text style={{ fontSize: 14, fontWeight: '700', color: colors.ink }}>{item.type} Refund</Text>
                <Text style={{ fontSize: 12, color: colors.muted }}>Amount: ₹{item.amount} · User: {item.userName}</Text>
                {item.reason && <Text style={{ fontSize: 12, color: colors.muted, marginTop: 4 }}>Reason: {item.reason}</Text>}
              </View>
              <Button variant="gold" size="sm" onPress={() => {}}>Process</Button>
            </View>
          </Card>
        )}
        keyExtractor={(_, i) => i.toString()}
        ListEmptyComponent={
          <EmptyState icon="💰" title="No refund requests" description="No pending refund requests to process" />
        }
      />
    </View>
  );
}
