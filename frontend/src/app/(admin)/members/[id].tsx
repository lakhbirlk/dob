import { useState } from "react";
import {
  View, Text, ScrollView, TouchableOpacity, TextInput,
  ActivityIndicator, Alert,
} from "react-native";
import { useLocalSearchParams } from "expo-router";
import { Card } from "@/components/Card";
import { Badge } from "@/components/Badge";
import { Button } from "@/components/Button";
import { Divider } from "@/components/Divider";
import { colors } from "@/theme/colors";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { adminApi } from "@/services/api";

export default function MemberDetailScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const queryClient = useQueryClient();

  const [editing, setEditing] = useState(false);
  const [editName, setEditName] = useState("");
  const [editPhone, setEditPhone] = useState("");
  const [editPan, setEditPan] = useState("");

  // Membership editing
  const [editPlan, setEditPlan] = useState(false);
  const [editPlanType, setEditPlanType] = useState("MONTHLY");
  const [editEndDate, setEditEndDate] = useState("");
  const [editDownloadLimit, setEditDownloadLimit] = useState("50");

  const { data: member, isLoading } = useQuery({
    queryKey: ["admin", "member", id],
    queryFn: () => adminApi.getMember(id!),
    enabled: !!id,
  });

  const updateMutation = useMutation({
    mutationFn: (body: any) => adminApi.updateMember(id!, body),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["admin", "member", id] });
      queryClient.invalidateQueries({ queryKey: ["admin", "members"] });
      setEditing(false);
    },
    onError: (err: any) => {
      Alert.alert("Error", err?.message || "Failed to update member");
    },
  });

  const updateMembershipMutation = useMutation({
    mutationFn: (body: any) => adminApi.updateMemberMembership(id!, body),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["admin", "member", id] });
      queryClient.invalidateQueries({ queryKey: ["admin", "members"] });
      setEditPlan(false);
    },
    onError: (err: any) => {
      Alert.alert("Error", err?.message || "Failed to update membership");
    },
  });

  if (isLoading || !member) {
    return (
      <View className="flex-1 bg-bg items-center justify-center">
        <ActivityIndicator size="large" color={colors.navy} />
      </View>
    );
  }

  const activeMembership = member.memberships?.find((m: any) => m.status === "ACTIVE");
  const pastMemberships = member.memberships?.filter((m: any) => m.status !== "ACTIVE") ?? [];

  const handleStartEdit = () => {
    setEditName(member.fullName || "");
    setEditPhone(member.phone || "");
    setEditPan(member.pan || "");
    setEditing(true);
  };

  const handleSaveProfile = () => {
    const body: any = {};
    if (editName !== member.fullName) body.fullName = editName;
    if (editPhone !== (member.phone || "")) body.phone = editPhone;
    if (editPan !== (member.pan || "")) body.pan = editPan;
    updateMutation.mutate(body);
  };

  const handleStartEditPlan = () => {
    if (activeMembership) {
      setEditPlanType(activeMembership.planType || "MONTHLY");
      setEditEndDate(activeMembership.endDate || "");
      setEditDownloadLimit(String(activeMembership.downloadLimit || 50));
    } else {
      setEditPlanType("MONTHLY");
      setEditEndDate("");
      setEditDownloadLimit("50");
    }
    setEditPlan(true);
  };

  const handleSavePlan = () => {
    if (editPlanType || editEndDate || editDownloadLimit) {
      const body: any = {};
      if (editPlanType) body.planType = editPlanType;
      if (editEndDate) body.endDate = editEndDate;
      if (editDownloadLimit) body.downloadLimit = parseInt(editDownloadLimit, 10);
      body.action = activeMembership ? "EXTEND" : "ACTIVATE";
      updateMembershipMutation.mutate(body);
    }
  };

  const handleCancelMembership = () => {
    Alert.alert(
      "Cancel Membership",
      "Are you sure you want to cancel this member's subscription?",
      [
        { text: "No", style: "cancel" },
        { text: "Yes, Cancel", style: "destructive", onPress: () => {
          updateMembershipMutation.mutate({ action: "CANCEL" });
        }},
      ]
    );
  };

  return (
    <ScrollView className="flex-1 bg-bg">
      {/* Profile Header */}
      <View className="bg-navy-deep px-5 pt-8 pb-8" style={{ borderBottomLeftRadius: 24, borderBottomRightRadius: 24 }}>
        <View className="flex-row items-center gap-x-4">
          <View className="w-16 h-16 rounded-full bg-white/15 items-center justify-center">
            <Text className="text-3xl text-white font-bold">
              {member.fullName ? member.fullName.charAt(0).toUpperCase() : "?"}
            </Text>
          </View>
          <View className="flex-1">
            <View className="flex-row items-center gap-x-2">
              <Text className="text-xl font-extrabold text-white">{member.fullName || "Unnamed"}</Text>
              {!member.active && (
                <Badge variant="danger">
                  <Text className="text-[10px] font-extrabold">INACTIVE</Text>
                </Badge>
              )}
            </View>
            <Text className="text-sm text-faint mt-0.5">{member.email}</Text>
            <Text className="text-xs text-faint mt-0.5 font-mono">ID: {member.id}</Text>
          </View>
        </View>
      </View>

      <View className="px-5 pt-6 gap-y-5 pb-10">
        {/* Profile Details */}
        <Card variant="elevated">
          <View className="flex-row justify-between items-center mb-4">
            <Text className="text-lg font-extrabold text-navy">Profile Details</Text>
            {!editing && (
              <TouchableOpacity onPress={handleStartEdit}>
                <Text className="text-sm font-bold text-blue">Edit</Text>
              </TouchableOpacity>
            )}
          </View>

          {editing ? (
            <View className="gap-y-4">
              <View>
                <Text className="text-xs font-semibold text-faint uppercase tracking-wider mb-1.5">Full Name</Text>
                <TextInput
                  className="bg-bg rounded-xl px-4 py-3 text-sm text-ink border border-line"
                  value={editName}
                  onChangeText={setEditName}
                  placeholderTextColor={colors.faint}
                />
              </View>
              <View>
                <Text className="text-xs font-semibold text-faint uppercase tracking-wider mb-1.5">Phone</Text>
                <TextInput
                  className="bg-bg rounded-xl px-4 py-3 text-sm text-ink border border-line"
                  value={editPhone}
                  onChangeText={setEditPhone}
                  placeholder="Add phone number"
                  placeholderTextColor={colors.faint}
                />
              </View>
              <View>
                <Text className="text-xs font-semibold text-faint uppercase tracking-wider mb-1.5">PAN</Text>
                <TextInput
                  className="bg-bg rounded-xl px-4 py-3 text-sm text-ink border border-line"
                  value={editPan}
                  onChangeText={setEditPan}
                  placeholder="Add PAN"
                  placeholderTextColor={colors.faint}
                  autoCapitalize="characters"
                />
              </View>
              <View className="flex-row gap-x-3 mt-2">
                <Button variant="primary" size="md" loading={updateMutation.isPending} onPress={handleSaveProfile} className="flex-1">
                  Save Changes
                </Button>
                <Button variant="ghost" size="md" onPress={() => setEditing(false)} className="flex-1">
                  Cancel
                </Button>
              </View>
            </View>
          ) : (
            <View className="flex-row flex-wrap gap-y-4">
              <DetailItem label="Email" value={member.email} />
              <DetailItem label="Phone" value={member.phone || "—"} />
              <DetailItem label="PAN" value={member.pan || "—"} />
              <DetailItem label="Role" value={member.role} />
              <DetailItem label="Status" value={member.active ? "Active" : "Inactive"} />
              <DetailItem label="Email Verified" value={member.emailVerified ? "Yes" : "No"} />
              <Divider />
              <DetailItem label="Joined" value={new Date(member.createdAt).toLocaleDateString("en-IN", { day: "numeric", month: "long", year: "numeric" })} />
            </View>
          )}
        </Card>

        {/* Active Subscription */}
        <Card variant="elevated">
          <View className="flex-row justify-between items-center mb-4">
            <Text className="text-lg font-extrabold text-navy">Current Subscription</Text>
            {activeMembership && !editPlan && (
              <View className="flex-row gap-x-2">
                <TouchableOpacity onPress={handleStartEditPlan}>
                  <Text className="text-sm font-bold text-blue">Edit</Text>
                </TouchableOpacity>
                <TouchableOpacity onPress={handleCancelMembership}>
                  <Text className="text-sm font-bold text-red">Cancel</Text>
                </TouchableOpacity>
              </View>
            )}
            {!activeMembership && !editPlan && (
              <TouchableOpacity onPress={handleStartEditPlan}>
                <Text className="text-sm font-bold text-green">+ Add Plan</Text>
              </TouchableOpacity>
            )}
          </View>

          {editPlan ? (
            <View className="gap-y-4">
              <View>
                <Text className="text-xs font-semibold text-faint uppercase tracking-wider mb-1.5">Plan Type</Text>
                <View className="flex-row gap-x-2">
                  {["MONTHLY", "YEARLY", "QUARTERLY"].map((plan) => (
                    <TouchableOpacity
                      key={plan}
                      onPress={() => setEditPlanType(plan)}
                      className={`px-4 py-2 rounded-lg border ${
                        editPlanType === plan ? "bg-navy border-navy" : "bg-white border-line"
                      }`}
                    >
                      <Text className={`text-xs font-bold ${editPlanType === plan ? "text-white" : "text-ink"}`}>
                        {plan.charAt(0) + plan.slice(1).toLowerCase()}
                      </Text>
                    </TouchableOpacity>
                  ))}
                </View>
              </View>
              <View>
                <Text className="text-xs font-semibold text-faint uppercase tracking-wider mb-1.5">End Date (YYYY-MM-DD)</Text>
                <TextInput
                  className="bg-bg rounded-xl px-4 py-3 text-sm text-ink border border-line"
                  value={editEndDate}
                  onChangeText={setEditEndDate}
                  placeholder="e.g. 2027-06-28"
                  placeholderTextColor={colors.faint}
                />
              </View>
              <View>
                <Text className="text-xs font-semibold text-faint uppercase tracking-wider mb-1.5">Download Limit</Text>
                <TextInput
                  className="bg-bg rounded-xl px-4 py-3 text-sm text-ink border border-line"
                  value={editDownloadLimit}
                  onChangeText={setEditDownloadLimit}
                  keyboardType="number-pad"
                  placeholder="50"
                  placeholderTextColor={colors.faint}
                />
              </View>
              <View className="flex-row gap-x-3 mt-2">
                <Button variant="primary" size="md" loading={updateMembershipMutation.isPending} onPress={handleSavePlan} className="flex-1">
                  {activeMembership ? "Update Plan" : "Activate Plan"}
                </Button>
                <Button variant="ghost" size="md" onPress={() => setEditPlan(false)} className="flex-1">
                  Cancel
                </Button>
              </View>
            </View>
          ) : activeMembership ? (
            <View className="flex-row flex-wrap gap-y-4">
              <DetailItem label="Plan Type" value={activeMembership.planType} />
              <DetailItem label="Status" value={activeMembership.status} />
              <DetailItem label="Start Date" value={new Date(activeMembership.startDate + "T00:00:00").toLocaleDateString("en-IN", { day: "numeric", month: "short", year: "numeric" })} />
              <DetailItem label="End Date" value={new Date(activeMembership.endDate + "T00:00:00").toLocaleDateString("en-IN", { day: "numeric", month: "short", year: "numeric" })} />
              <DetailItem label="Downloads" value={`${activeMembership.downloadsUsed} / ${activeMembership.downloadLimit}`} />
            </View>
          ) : (
            <View className="py-6 items-center">
              <Text className="text-3xl mb-3">📋</Text>
              <Text className="text-sm text-muted text-center">No active subscription</Text>
              <Text className="text-xs text-faint mt-1 text-center">This member has not subscribed to any plan</Text>
            </View>
          )}
        </Card>

        {/* Membership History */}
        {pastMemberships.length > 0 && (
          <Card variant="elevated">
            <Text className="text-lg font-extrabold text-navy mb-4">Membership History</Text>
            {pastMemberships.map((m: any, i: number) => (
              <View key={m.id}>
                {i > 0 && <Divider />}
                <View className="flex-row justify-between items-center py-2">
                  <View>
                    <Text className="text-sm font-bold text-ink">{m.planType}</Text>
                    <Text className="text-xs text-muted mt-0.5">
                      {new Date(m.startDate + "T00:00:00").toLocaleDateString("en-IN", { day: "numeric", month: "short" })} — {new Date(m.endDate + "T00:00:00").toLocaleDateString("en-IN", { day: "numeric", month: "short", year: "numeric" })}
                    </Text>
                    <Text className="text-xs text-faint mt-0.5">{m.downloadsUsed} / {m.downloadLimit} downloads</Text>
                  </View>
                  <Badge variant={m.status === "EXPIRED" ? "neutral" : m.status === "CANCELLED" ? "warning" : "success"}>
                    <Text className="text-xs font-extrabold">{m.status}</Text>
                  </Badge>
                </View>
              </View>
            ))}
          </Card>
        )}
      </View>
    </ScrollView>
  );
}

// ──────────── Sub-components ────────────

function DetailItem({ label, value }: { label: string; value?: string | null }) {
  if (!value) return null;
  return (
    <View className="w-1/2 pr-3">
      <Text className="text-xs text-faint font-semibold uppercase tracking-wider">{label}</Text>
      <Text className="text-sm font-bold text-ink mt-0.5">{value}</Text>
    </View>
  );
}
