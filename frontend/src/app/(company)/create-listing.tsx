import React, { useState } from 'react';
import { View, Text, TextInput, ScrollView } from 'react-native';
import { Button } from '@/components/Button';
import { Card } from '@/components/Card';
import { colors } from '@/theme/colors';
import { router } from 'expo-router';
import { useCreateCompany } from '@/hooks/useCompanies';

export default function CreateListingScreen() {
  const [form, setForm] = useState({
    name: '', sector: '', state: '', city: '', companyType: '',
    incorporationYear: '', description: '', website: ''
  });
  const [error, setError] = useState('');
  const createCompany = useCreateCompany();

  const handleSubmit = () => {
    if (!form.name) {
      setError('Company name is required');
      return;
    }
    createCompany.mutate({
      ...form,
      incorporationYear: form.incorporationYear ? parseInt(form.incorporationYear) : undefined,
    } as any, {
      onSuccess: () => router.back(),
      onError: (err: any) => setError(err.message || 'Failed to create listing'),
    });
  };

  const update = (key: string, value: string) => {
    setForm(prev => ({ ...prev, [key]: value }));
    setError('');
  };

  const inputStyle = { borderWidth: 1, borderColor: colors.line, borderRadius: 8, padding: 10, fontSize: 15, backgroundColor: '#fff' };

  return (
    <ScrollView style={{ flex: 1, backgroundColor: colors.bg }}>
      <View style={{ padding: 16, gap: 16 }}>
        <Text style={{ fontSize: 20, fontWeight: '800', color: colors.ink }}>Create Company Listing</Text>
        <Text style={{ fontSize: 13, color: colors.muted }}>All listings are reviewed before publication. Listing fee: ₹500 + GST / year.</Text>

        {error ? <Text style={{ color: colors.red, fontSize: 14 }}>{error}</Text> : null}

        <Card variant="default">
          <Field label="Company Name *" value={form.name} onChange={(v) => update('name', v)} placeholder="Enter company name" style={inputStyle} />
          <Field label="Sector" value={form.sector} onChange={(v) => update('sector', v)} placeholder="e.g. Technology" style={inputStyle} />
          <Field label="State" value={form.state} onChange={(v) => update('state', v)} placeholder="e.g. Maharashtra" style={inputStyle} />
          <Field label="City" value={form.city} onChange={(v) => update('city', v)} placeholder="e.g. Mumbai" style={inputStyle} />
          <Field label="Company Type" value={form.companyType} onChange={(v) => update('companyType', v)} placeholder="e.g. Private Limited" style={inputStyle} />
          <Field label="Incorporation Year" value={form.incorporationYear} onChange={(v) => update('incorporationYear', v)} placeholder="e.g. 2020" keyboardType="numeric" style={inputStyle} />
          <Field label="Description" value={form.description} onChange={(v) => update('description', v)} placeholder="Brief description of the company" multiline style={{ ...inputStyle, height: 80, textAlignVertical: 'top' }} />
          <Field label="Website" value={form.website} onChange={(v) => update('website', v)} placeholder="https://example.com" keyboardType="url" style={inputStyle} />

          <Button variant="gold" size="lg" onPress={handleSubmit} loading={createCompany.isPending} style={{ marginTop: 12 }}>
            Submit for Review
          </Button>
        </Card>
      </View>
    </ScrollView>
  );
}

function Field({ label, ...props }: { label: string; [key: string]: any }) {
  return (
    <View style={{ marginBottom: 12 }}>
      <Text style={{ fontSize: 13, fontWeight: '600', color: colors.muted, marginBottom: 4 }}>{label}</Text>
      <TextInput {...props} />
    </View>
  );
}
