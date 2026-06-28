import React from 'react';
import { View, Text, FlatList } from 'react-native';
import { Card } from '@/components/Card';
import { EmptyState } from '@/components/EmptyState';
import { colors } from '@/theme/colors';

export default function AuditLogsScreen() {
  const logs: any[] = [];

  return (
    <View style={{ flex: 1, backgroundColor: colors.bg }}>
      <View style={{ padding: 16 }}>
        <Text style={{ fontSize: 20, fontWeight: '800', color: colors.ink }}>Audit Logs</Text>
        <Text style={{ fontSize: 13, color: colors.muted, marginTop: 4 }}>
          All administrative actions are logged and immutable
        </Text>
      </View>

      <FlatList
        data={logs}
        renderItem={({ item }) => (
          <Card variant="bordered" style={{ marginHorizontal: 16, marginBottom: 6 }}>
            <View style={{ flexDirection: 'row', justifyContent: 'space-between' }}>
              <Text style={{ fontSize: 13, fontWeight: '600', color: colors.ink }}>{item.action}</Text>
              <Text style={{ fontSize: 11, color: colors.faint }}>{item.createdAt}</Text>
            </View>
            <Text style={{ fontSize: 12, color: colors.muted }}>
              {item.entityType} · {item.entityId} · By: {item.userName}
            </Text>
          </Card>
        )}
        keyExtractor={(_, i) => i.toString()}
        ListEmptyComponent={<EmptyState icon="📊" title="No audit logs" description="Audit trail will appear here" />}
      />
    </View>
  );
}
