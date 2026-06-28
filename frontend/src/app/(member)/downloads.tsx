import React from 'react';
import { View, Text, FlatList } from 'react-native';
import { Card } from '@/components/Card';
import { EmptyState } from '@/components/EmptyState';
import { colors } from '@/theme/colors';

export default function DownloadsScreen() {
  // In production: use React Query to fetch download history
  const downloads: any[] = [];

  return (
    <View style={{ flex: 1, backgroundColor: colors.bg }}>
      <View style={{ padding: 16 }}>
        <View style={{ flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' }}>
          <Text style={{ fontSize: 20, fontWeight: '800', color: colors.ink }}>Download Center</Text>
          <View style={{ backgroundColor: '#E8F7F1', paddingHorizontal: 12, paddingVertical: 4, borderRadius: 99 }}>
            <Text style={{ fontSize: 12, fontWeight: '700', color: '#0B6B4F' }}>42 / 50 downloads left</Text>
          </View>
        </View>
        <Text style={{ fontSize: 13, color: colors.muted, marginTop: 4 }}>
          Monthly download limit: 50. Resets on the 1st of each month.
        </Text>
      </View>

      <FlatList
        data={downloads}
        renderItem={({ item }) => (
          <Card variant="bordered" style={{ marginHorizontal: 16, marginBottom: 8 }}>
            <Text style={{ fontSize: 14, fontWeight: '600', color: colors.ink }}>{item.documentType}</Text>
            <Text style={{ fontSize: 12, color: colors.muted }}>{item.companyName} · {item.downloadedAt}</Text>
          </Card>
        )}
        keyExtractor={(_, i) => i.toString()}
        ListEmptyComponent={
          <EmptyState icon="📥" title="No downloads yet" description="Search companies and download their financial reports" />
        }
      />
    </View>
  );
}
