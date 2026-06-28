import React, { useState } from 'react';
import { View, Text, TextInput, ScrollView } from 'react-native';
import { Button } from '@/components/Button';
import { Card } from '@/components/Card';
import { colors } from '@/theme/colors';
import { useAuthStore } from '@/store/authStore';

export default function ProfileScreen() {
  const { user } = useAuthStore();
  const [fullName, setFullName] = useState(user?.fullName ?? '');
  const [phone, setPhone] = useState(user?.phone ?? '');
  const [pan, setPan] = useState(user?.panNumber ?? '');
  const [saved, setSaved] = useState(false);

  const handleSave = () => {
    // In production: call userApi.updateProfile({ fullName, phone })
    setSaved(true);
    setTimeout(() => setSaved(false), 3000);
  };

  const handlePanUpdate = () => {
    // In production: call userApi.updatePan(pan)
    setSaved(true);
    setTimeout(() => setSaved(false), 3000);
  };

  return (
    <ScrollView style={{ flex: 1, backgroundColor: colors.bg }}>
      <View style={{ padding: 16, gap: 16 }}>
        <Text style={{ fontSize: 20, fontWeight: '800', color: colors.ink }}>My Profile</Text>

        <Card variant="default">
          <Text style={{ fontSize: 16, fontWeight: '700', color: colors.navy, marginBottom: 16 }}>
            Personal Information
          </Text>

          <View style={{ marginBottom: 12 }}>
            <Text style={{ fontSize: 13, fontWeight: '600', color: colors.muted, marginBottom: 4 }}>Email</Text>
            <Text style={{ fontSize: 15, color: colors.ink }}>{user?.email ?? '—'}</Text>
          </View>

          <View style={{ marginBottom: 12 }}>
            <Text style={{ fontSize: 13, fontWeight: '600', color: colors.muted, marginBottom: 4 }}>Full Name</Text>
            <TextInput value={fullName} onChangeText={setFullName}
              style={{ borderWidth: 1, borderColor: colors.line, borderRadius: 8, padding: 10, fontSize: 15 }} />
          </View>

          <View style={{ marginBottom: 12 }}>
            <Text style={{ fontSize: 13, fontWeight: '600', color: colors.muted, marginBottom: 4 }}>Phone</Text>
            <TextInput value={phone} onChangeText={setPhone} keyboardType="phone-pad"
              style={{ borderWidth: 1, borderColor: colors.line, borderRadius: 8, padding: 10, fontSize: 15 }} />
          </View>

          <Button variant="primary" onPress={handleSave}>
            {saved ? 'Saved ✓' : 'Save Changes'}
          </Button>
        </Card>

        <Card variant="default">
          <Text style={{ fontSize: 16, fontWeight: '700', color: colors.navy, marginBottom: 12 }}>
            PAN Verification
          </Text>
          <Text style={{ fontSize: 13, color: colors.muted, marginBottom: 8 }}>
            PAN is required for full platform access. Your PAN is encrypted and never shared.
          </Text>
          <TextInput value={pan} onChangeText={(t) => setPan(t.toUpperCase())} maxLength={10}
            placeholder="ABCDE1234F" autoCapitalize="characters"
            style={{ borderWidth: 1, borderColor: colors.line, borderRadius: 8, padding: 10, fontSize: 15 }} />
          <Button variant="primary" onPress={handlePanUpdate} style={{ marginTop: 12 }}>
            Update PAN
          </Button>
        </Card>

        <Card variant="default">
          <Text style={{ fontSize: 16, fontWeight: '700', color: colors.navy, marginBottom: 12 }}>Account</Text>
          <View style={{ gap: 8 }}>
            <Text style={{ fontSize: 14, color: colors.ink }}>Role: <Text style={{ fontWeight: '600' }}>{user?.role ?? '—'}</Text></Text>
            <Text style={{ fontSize: 14, color: colors.ink }}>Member since: <Text style={{ fontWeight: '600' }}>2026</Text></Text>
          </View>
        </Card>
      </View>
    </ScrollView>
  );
}
