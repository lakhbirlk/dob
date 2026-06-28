import React from 'react';
import { View, Text, FlatList } from 'react-native';
import { Card } from '@/components/Card';
import { EmptyState } from '@/components/EmptyState';
import { colors } from '@/theme/colors';

export default function ListingStatusScreen() {
  // In production: fetch from companiesApi.getMyCompanies()
  const myCompanies: any[] = [];

  return (
    <View style={{ flex: 1, backgroundColor: colors.bg }}>
      <View style={{ padding: 16 }}>
        <Text style={{ fontSize: 20, fontWeight: '800', color: colors.ink }}>My Listings</Text>
      </View>
      <FlatList
        data={myCompanies}
        renderItem={({ item }) => (
          <Card variant="bordered" style={{ marginHorizontal: 16, marginBottom: 8 }}>
            <View style={{ flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' }}>
              <View style={{ flex: 1 }}>
                <Text style={{ fontSize: 15, fontWeight: '700', color: colors.navy }}>{item.name}</Text>
                <Text style={{ fontSize: 12, color: colors.muted }}>{item.sector} · {item.city}</Text>
              </View>
              <View style={{
                paddingHorizontal: 10, paddingVertical: 4, borderRadius: 99,
                backgroundColor: item.status === 'APPROVED' ? '#E8F7F1' : item.status === 'REJECTED' ? '#FEF2F2' : '#FFFBF1'
              }}>
                <Text style={{
                  fontSize: 11, fontWeight: '700',
                  color: item.status === 'APPROVED' ? '#0B6B4F' : item.status === 'REJECTED' ? '#B91C1C' : '#C49A35'
                }}>
                  {item.status}
                </Text>
              </View>
            </View>
          </Card>
        )}
        keyExtractor={(_, i) => i.toString()}
        ListEmptyComponent={
          <EmptyState icon="📋" title="No listings yet" description="Create your first company listing to get started" />
        }
      />
    </View>
  );
}
