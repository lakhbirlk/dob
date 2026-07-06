import { useState } from "react";
import {
  View, Text, ScrollView, TouchableOpacity, TextInput,
  ActivityIndicator, Alert, Modal as RNModal,
} from "react-native";
import { useLocalSearchParams } from "expo-router";
import { Card } from "@/components/Card";
import { Badge } from "@/components/Badge";
import { Button } from "@/components/Button";
import { Divider } from "@/components/Divider";
import { colors } from "@/theme/colors";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { adminApi } from "@/services/api";

const STATUS_CONFIG: Record<string, { label: string; color: string; bg: string }> = {
  APPROVED_ACTIVE: { label: "Active", color: colors.green, bg: colors.greenLight },
  APPROVED_MEMBERSHIP_PENDING: { label: "Approved", color: colors.green, bg: colors.greenLight },
  PENDING_REVIEW: { label: "Pending Review", color: colors.goldDark, bg: colors.goldPale },
  DRAFT: { label: "Draft", color: colors.muted, bg: colors.line },
  REJECTED: { label: "Rejected", color: colors.red, bg: colors.redLight },
  SUSPENDED: { label: "Suspended", color: colors.red, bg: colors.redLight },
  MEMBERSHIP_EXPIRED: { label: "Expired", color: colors.muted, bg: colors.line },
};

interface SectionProps {
  title: string;
  children: React.ReactNode;
  defaultOpen?: boolean;
}

function CollapsibleSection({ title, children, defaultOpen = false }: SectionProps) {
  const [open, setOpen] = useState(defaultOpen);
  return (
    <Card variant="elevated" className="overflow-hidden">
      <TouchableOpacity
        onPress={() => setOpen(!open)}
        className="flex-row justify-between items-center px-5 py-4"
        activeOpacity={0.7}
      >
        <Text className="text-base font-extrabold text-navy">{title}</Text>
        <Text className="text-faint text-lg">{open ? "▲" : "▼"}</Text>
      </TouchableOpacity>
      {open && (
        <View className="px-5 pb-5 pt-1 border-t border-line/50">
          {children}
        </View>
      )}
    </Card>
  );
}

function DetailRow({ label, value }: { label: string; value?: string | number | null | boolean }) {
  if (value === null || value === undefined || value === "") return null;
  const display = typeof value === "boolean" ? (value ? "Yes" : "No") : String(value);
  return (
    <View className="py-2">
      <Text className="text-[10px] text-faint font-semibold uppercase tracking-wider">{label}</Text>
      <Text className="text-sm font-bold text-ink mt-0.5" selectable>{display}</Text>
    </View>
  );
}

function EditField({
  label, value, onChange, placeholder, multiline = false, keyboardType,
}: {
  label: string; value: string; onChange: (v: string) => void;
  placeholder?: string; multiline?: boolean; keyboardType?: "default" | "numeric" | "url" | "email-address" | "phone-pad";
}) {
  return (
    <View>
      <Text className="text-xs font-semibold text-faint uppercase tracking-wider mb-1.5">{label}</Text>
      <TextInput
        className={`bg-bg rounded-xl px-4 py-3 text-sm text-ink border border-line ${multiline ? "min-h-[80px]" : ""}`}
        value={value}
        onChangeText={onChange}
        placeholder={placeholder || `Enter ${label.toLowerCase()}`}
        placeholderTextColor={colors.faint}
        multiline={multiline}
        keyboardType={keyboardType || "default"}
        autoCapitalize="none"
      />
    </View>
  );
}

export default function CompanyMemberDetailScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const queryClient = useQueryClient();

  // Profile editing
  const [editing, setEditing] = useState(false);
  const [editName, setEditName] = useState("");
  const [editPhone, setEditPhone] = useState("");
  const [editPan, setEditPan] = useState("");

  // Company editing
  const [editingCompany, setEditingCompany] = useState(false);
  const [editFields, setEditFields] = useState<Record<string, string>>({});

  // Reject state
  const [showRejectModal, setShowRejectModal] = useState(false);
  const [rejectComment, setRejectComment] = useState("");

  const { data: member, isLoading } = useQuery({
    queryKey: ["admin", "company-member", id],
    queryFn: () => adminApi.getCompanyMember(id!),
    enabled: !!id,
  });

  const updateMutation = useMutation({
    mutationFn: (body: any) => adminApi.updateCompanyMember(id!, body),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["admin", "company-member", id] });
      queryClient.invalidateQueries({ queryKey: ["admin", "company-members"] });
      setEditing(false);
    },
    onError: (err: any) => {
      const message = err?.response?.data?.detail || err?.response?.data?.message || err?.message || "Failed to update member";
      Alert.alert("Error", message);
    },
  });

  const updateCompanyMutation = useMutation({
    mutationFn: ({ companyId, body }: { companyId: string; body: any }) =>
      adminApi.updateCompanyListing(id!, companyId, body),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["admin", "company-member", id] });
      queryClient.invalidateQueries({ queryKey: ["admin", "company-members"] });
      setEditingCompany(false);
    },
    onError: (err: any) => {
      const message = err?.response?.data?.detail || err?.response?.data?.message || err?.message || "Failed to update company";
      Alert.alert("Error", message);
    },
  });

  const suspendMutation = useMutation({
    mutationFn: (companyId: string) => adminApi.suspendCompanyListing(id!, companyId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["admin", "company-member", id] });
      queryClient.invalidateQueries({ queryKey: ["admin", "company-members"] });
      Alert.alert("Success", "Company listing has been suspended.");
    },
    onError: (err: any) => {
      const message = err?.response?.data?.detail || err?.response?.data?.message || err?.message || "Failed to suspend listing";
      Alert.alert("Error", message);
    },
  });

  const approveMutation = useMutation({
    mutationFn: (companyId: string) => adminApi.approveCompany(companyId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["admin", "company-member", id] });
      queryClient.invalidateQueries({ queryKey: ["admin", "company-members"] });
      Alert.alert("Success", "Company listing has been approved.");
    },
    onError: (err: any) => {
      const message = err?.response?.data?.detail || err?.response?.data?.message || err?.message || "Failed to approve listing";
      Alert.alert("Error", message);
    },
  });

  const rejectMutation = useMutation({
    mutationFn: ({ companyId, comment }: { companyId: string; comment: string }) =>
      adminApi.rejectCompany(companyId, comment),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["admin", "company-member", id] });
      queryClient.invalidateQueries({ queryKey: ["admin", "company-members"] });
      setShowRejectModal(false);
      setRejectComment("");
      Alert.alert("Success", "Company listing has been rejected.");
    },
    onError: (err: any) => {
      const message = err?.response?.data?.detail || err?.response?.data?.message || err?.message || "Failed to reject listing";
      Alert.alert("Error", message);
    },
  });

  if (isLoading || !member) {
    return (
      <View className="flex-1 bg-bg items-center justify-center">
        <ActivityIndicator size="large" color={colors.navy} />
      </View>
    );
  }

  const company = member.companies?.[0] ?? null;

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
    if (Object.keys(body).length === 0) { setEditing(false); return; }
    updateMutation.mutate(body);
  };

  const handleStartEditCompany = () => {
    if (!company) return;
    const fields: Record<string, string> = {};
    const editableFields = [
      "name", "brandName", "sector", "city", "state", "companyType", "incorporationYear",
      "description", "businessDescription", "website", "logoUrl",
      "cin", "gstin", "pan", "tan", "msmeRegistration", "startupIndiaRegistration",
      "companyRegistrationNumber",
      "registeredAddressLine1", "registeredAddressLine2", "registeredCity",
      "registeredState", "registeredPinCode", "registeredCountry",
      "officialEmail", "officialPhone", "phoneNumber", "headquarter",
      "linkedinProfile", "twitterUrl", "socialMediaLinks",
      "annualTurnover", "paidUpCapital", "authorizedCapital", "employeeCount",
      "financialYear", "totalFunding", "investors",
      "productsServices", "exportImportStatus", "numBranches", "operationalStates",
      "certifications", "technologiesUsed",
      "ceoName", "ctoName", "founders", "businessModel", "companyStage",
      "awards", "cultureSummary", "mission", "vision",
      "authorizedRepName", "authorizedRepDesignation", "authorizedRepMobile",
      "authorizedRepEmail", "auditorDetails",
    ];
    for (const f of editableFields) {
      fields[f] = company[f] != null ? String(company[f]) : "";
    }
    setEditFields(fields);
    setEditingCompany(true);
  };

  const handleSaveCompany = () => {
    if (!company) return;
    const body: any = {};
    for (const [key, val] of Object.entries(editFields)) {
      const orig = company[key] != null ? String(company[key]) : "";
      if (val !== orig) {
        // Convert numeric fields back to number
        if (key === "incorporationYear" || key === "employeeCount" || key === "numBranches") {
          body[key] = val ? Number(val) : null;
        } else {
          body[key] = val || null;
        }
      }
    }
    if (Object.keys(body).length === 0) { setEditingCompany(false); return; }
    updateCompanyMutation.mutate({ companyId: company.id, body });
  };

  const handleSuspend = () => {
    if (!company) return;
    Alert.alert(
      "Suspend Listing",
      `Are you sure you want to suspend "${company.name}"? It will be hidden from search results.`,
      [
        { text: "No", style: "cancel" },
        { text: "Yes, Suspend", style: "destructive", onPress: () => suspendMutation.mutate(company.id) },
      ]
    );
  };

  const handleApprove = () => {
    if (!company) return;
    Alert.alert(
      "Approve Listing",
      `Approve "${company.name}" for public listing?`,
      [
        { text: "No", style: "cancel" },
        { text: "Yes, Approve", onPress: () => approveMutation.mutate(company.id) },
      ]
    );
  };

  const handleReject = () => {
    if (!company) return;
    if (!rejectComment.trim()) {
      Alert.alert("Comment Required", "Please provide a rejection reason to help the company improve their listing.");
      return;
    }
    rejectMutation.mutate({ companyId: company.id, comment: rejectComment.trim() });
  };

  const cfg = company ? STATUS_CONFIG[company.status] || { label: company?.status || "Unknown", color: colors.muted, bg: colors.line } : null;

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
                <Badge variant="danger"><Text className="text-[10px] font-extrabold">INACTIVE</Text></Badge>
              )}
            </View>
            <Text className="text-sm text-faint mt-0.5">{member.email}</Text>
            <Text className="text-xs text-faint mt-0.5 font-mono">ID: {member.id}</Text>
          </View>
        </View>
      </View>

      <View className="px-5 pt-6 gap-y-5 pb-10">
        {/* Profile Details Section */}
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
              <EditField label="Full Name" value={editName} onChange={setEditName} />
              <EditField label="Phone" value={editPhone} onChange={setEditPhone} keyboardType="phone-pad" />
              <EditField label="PAN" value={editPan} onChange={setEditPan} />
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
              <DetailRow label="Email" value={member.email} />
              <DetailRow label="Phone" value={member.phone} />
              <DetailRow label="PAN" value={member.pan} />
              <DetailRow label="Role" value={member.role} />
              <DetailRow label="Status" value={member.active ? "Active" : "Inactive"} />
              <DetailRow label="Email Verified" value={member.emailVerified ? "Yes" : "No"} />
              <DetailRow label="Joined" value={member.createdAt ? new Date(member.createdAt).toLocaleDateString("en-IN", { day: "numeric", month: "long", year: "numeric" }) : "—"} />
            </View>
          )}
        </Card>

        {/* Company Listing */}
        <Text className="text-lg font-extrabold text-ink -mb-1">Company Listing</Text>

        {!company ? (
          <Card variant="subtle" className="py-6 items-center">
            <Text className="text-3xl mb-3">🏢</Text>
            <Text className="text-sm text-muted text-center">No company listing</Text>
            <Text className="text-xs text-faint mt-1 text-center">This user has not created a company listing yet</Text>
          </Card>
        ) : (
          <>
            {/* Status Bar */}
            <Card variant="elevated" className="pb-4">
              <View className="flex-row justify-between items-start mb-3">
                <View className="flex-1 mr-2">
                  <View className="flex-row items-center gap-x-2 flex-wrap">
                    <Text className="text-xl font-extrabold text-navy">{company.name}</Text>
                    {company.status === "SUSPENDED" && (
                      <Badge variant="danger"><Text className="text-[10px] font-extrabold">SUSPENDED</Text></Badge>
                    )}
                  </View>
                  <Text className="text-xs text-faint font-mono mt-0.5">{company.publicCompanyId}</Text>
                </View>
                {cfg && (
                  <View className="px-3 py-1.5 rounded-full" style={{ backgroundColor: cfg.bg }}>
                    <Text className="text-[10px] font-bold" style={{ color: cfg.color }}>{cfg.label}</Text>
                  </View>
                )}
              </View>

              {/* Action buttons */}
              <View className="flex-row gap-x-2">
                {(company.status === "PENDING_REVIEW" || company.status === "DRAFT") && (
                  <>
                    <Button variant="primary" size="sm" loading={approveMutation.isPending} onPress={handleApprove} className="flex-1">
                      Approve
                    </Button>
                    <Button variant="danger" size="sm" onPress={() => { setShowRejectModal(true); setRejectComment(""); }} className="flex-1">
                      Reject
                    </Button>
                  </>
                )}
                {company.status !== "SUSPENDED" && company.status !== "REJECTED" && company.status !== "DRAFT" && company.status !== "PENDING_REVIEW" ? (
                  <TouchableOpacity className="py-2 px-3" onPress={handleSuspend}>
                    <Text className="text-xs font-bold text-red">Suspend Listing</Text>
                  </TouchableOpacity>
                ) : null}
              </View>
            </Card>

            {/* Edit mode */}
            {editingCompany ? (
              <Card variant="elevated">
                <Text className="text-base font-extrabold text-navy mb-4">Edit All Listing Details</Text>
                <ScrollView className="max-h-[500px]">
                  <View className="gap-y-3">
                    <Text className="text-xs font-bold text-blue uppercase tracking-wider mt-2">Basic Info</Text>
                    <EditField label="Company Name" value={editFields.name || ""} onChange={(v) => setEditFields({ ...editFields, name: v })} />
                    <EditField label="Brand Name" value={editFields.brandName || ""} onChange={(v) => setEditFields({ ...editFields, brandName: v })} />
                    <EditField label="Sector" value={editFields.sector || ""} onChange={(v) => setEditFields({ ...editFields, sector: v })} />
                    <EditField label="Company Type" value={editFields.companyType || ""} onChange={(v) => setEditFields({ ...editFields, companyType: v })} />
                    <EditField label="City" value={editFields.city || ""} onChange={(v) => setEditFields({ ...editFields, city: v })} />
                    <EditField label="State" value={editFields.state || ""} onChange={(v) => setEditFields({ ...editFields, state: v })} />
                    <EditField label="Incorporation Year" value={editFields.incorporationYear || ""} onChange={(v) => setEditFields({ ...editFields, incorporationYear: v })} keyboardType="numeric" />
                    <EditField label="Headquarter" value={editFields.headquarter || ""} onChange={(v) => setEditFields({ ...editFields, headquarter: v })} />
                    <EditField label="Description" value={editFields.description || ""} onChange={(v) => setEditFields({ ...editFields, description: v })} multiline />
                    <EditField label="Business Description" value={editFields.businessDescription || ""} onChange={(v) => setEditFields({ ...editFields, businessDescription: v })} multiline />
                    <EditField label="Website" value={editFields.website || ""} onChange={(v) => setEditFields({ ...editFields, website: v })} keyboardType="url" />
                    <EditField label="Logo URL" value={editFields.logoUrl || ""} onChange={(v) => setEditFields({ ...editFields, logoUrl: v })} keyboardType="url" />

                    <Text className="text-xs font-bold text-blue uppercase tracking-wider mt-3">Registration</Text>
                    <EditField label="CIN" value={editFields.cin || ""} onChange={(v) => setEditFields({ ...editFields, cin: v })} />
                    <EditField label="GSTIN" value={editFields.gstin || ""} onChange={(v) => setEditFields({ ...editFields, gstin: v })} />
                    <EditField label="PAN" value={editFields.pan || ""} onChange={(v) => setEditFields({ ...editFields, pan: v })} />
                    <EditField label="TAN" value={editFields.tan || ""} onChange={(v) => setEditFields({ ...editFields, tan: v })} />
                    <EditField label="MSME Registration" value={editFields.msmeRegistration || ""} onChange={(v) => setEditFields({ ...editFields, msmeRegistration: v })} />
                    <EditField label="Startup India Reg." value={editFields.startupIndiaRegistration || ""} onChange={(v) => setEditFields({ ...editFields, startupIndiaRegistration: v })} />
                    <EditField label="Company Reg. Number" value={editFields.companyRegistrationNumber || ""} onChange={(v) => setEditFields({ ...editFields, companyRegistrationNumber: v })} />

                    <Text className="text-xs font-bold text-blue uppercase tracking-wider mt-3">Registered Office</Text>
                    <EditField label="Address Line 1" value={editFields.registeredAddressLine1 || ""} onChange={(v) => setEditFields({ ...editFields, registeredAddressLine1: v })} />
                    <EditField label="Address Line 2" value={editFields.registeredAddressLine2 || ""} onChange={(v) => setEditFields({ ...editFields, registeredAddressLine2: v })} />
                    <EditField label="City" value={editFields.registeredCity || ""} onChange={(v) => setEditFields({ ...editFields, registeredCity: v })} />
                    <EditField label="State" value={editFields.registeredState || ""} onChange={(v) => setEditFields({ ...editFields, registeredState: v })} />
                    <EditField label="Pincode" value={editFields.registeredPinCode || ""} onChange={(v) => setEditFields({ ...editFields, registeredPinCode: v })} />
                    <EditField label="Country" value={editFields.registeredCountry || ""} onChange={(v) => setEditFields({ ...editFields, registeredCountry: v })} />

                    <Text className="text-xs font-bold text-blue uppercase tracking-wider mt-3">Contact Details</Text>
                    <EditField label="Official Email" value={editFields.officialEmail || ""} onChange={(v) => setEditFields({ ...editFields, officialEmail: v })} keyboardType="email-address" />
                    <EditField label="Official Phone" value={editFields.officialPhone || ""} onChange={(v) => setEditFields({ ...editFields, officialPhone: v })} keyboardType="phone-pad" />
                    <EditField label="Phone Number" value={editFields.phoneNumber || ""} onChange={(v) => setEditFields({ ...editFields, phoneNumber: v })} keyboardType="phone-pad" />
                    <EditField label="LinkedIn Profile" value={editFields.linkedinProfile || ""} onChange={(v) => setEditFields({ ...editFields, linkedinProfile: v })} keyboardType="url" />
                    <EditField label="Twitter URL" value={editFields.twitterUrl || ""} onChange={(v) => setEditFields({ ...editFields, twitterUrl: v })} keyboardType="url" />
                    <EditField label="Social Media Links (JSON)" value={editFields.socialMediaLinks || ""} onChange={(v) => setEditFields({ ...editFields, socialMediaLinks: v })} multiline />

                    <Text className="text-xs font-bold text-blue uppercase tracking-wider mt-3">Financial Information</Text>
                    <EditField label="Annual Turnover" value={editFields.annualTurnover || ""} onChange={(v) => setEditFields({ ...editFields, annualTurnover: v })} />
                    <EditField label="Paid-up Capital" value={editFields.paidUpCapital || ""} onChange={(v) => setEditFields({ ...editFields, paidUpCapital: v })} />
                    <EditField label="Authorized Capital" value={editFields.authorizedCapital || ""} onChange={(v) => setEditFields({ ...editFields, authorizedCapital: v })} />
                    <EditField label="Employee Count" value={editFields.employeeCount || ""} onChange={(v) => setEditFields({ ...editFields, employeeCount: v })} keyboardType="numeric" />
                    <EditField label="Financial Year" value={editFields.financialYear || ""} onChange={(v) => setEditFields({ ...editFields, financialYear: v })} />
                    <EditField label="Total Funding" value={editFields.totalFunding || ""} onChange={(v) => setEditFields({ ...editFields, totalFunding: v })} />
                    <EditField label="Investors (JSON)" value={editFields.investors || ""} onChange={(v) => setEditFields({ ...editFields, investors: v })} multiline />
                    <EditField label="Auditor Details" value={editFields.auditorDetails || ""} onChange={(v) => setEditFields({ ...editFields, auditorDetails: v })} multiline />

                    <Text className="text-xs font-bold text-blue uppercase tracking-wider mt-3">Business Information</Text>
                    <EditField label="Products & Services" value={editFields.productsServices || ""} onChange={(v) => setEditFields({ ...editFields, productsServices: v })} multiline />
                    <EditField label="Export/Import Status" value={editFields.exportImportStatus || ""} onChange={(v) => setEditFields({ ...editFields, exportImportStatus: v })} />
                    <EditField label="Number of Branches" value={editFields.numBranches || ""} onChange={(v) => setEditFields({ ...editFields, numBranches: v })} keyboardType="numeric" />
                    <EditField label="Operational States" value={editFields.operationalStates || ""} onChange={(v) => setEditFields({ ...editFields, operationalStates: v })} />
                    <EditField label="Certifications" value={editFields.certifications || ""} onChange={(v) => setEditFields({ ...editFields, certifications: v })} multiline />
                    <EditField label="Technologies Used" value={editFields.technologiesUsed || ""} onChange={(v) => setEditFields({ ...editFields, technologiesUsed: v })} multiline />

                    <Text className="text-xs font-bold text-blue uppercase tracking-wider mt-3">Extended Profile</Text>
                    <EditField label="CEO Name" value={editFields.ceoName || ""} onChange={(v) => setEditFields({ ...editFields, ceoName: v })} />
                    <EditField label="CTO Name" value={editFields.ctoName || ""} onChange={(v) => setEditFields({ ...editFields, ctoName: v })} />
                    <EditField label="Founders (JSON)" value={editFields.founders || ""} onChange={(v) => setEditFields({ ...editFields, founders: v })} multiline />
                    <EditField label="Business Model" value={editFields.businessModel || ""} onChange={(v) => setEditFields({ ...editFields, businessModel: v })} />
                    <EditField label="Company Stage" value={editFields.companyStage || ""} onChange={(v) => setEditFields({ ...editFields, companyStage: v })} />
                    <EditField label="Awards (JSON)" value={editFields.awards || ""} onChange={(v) => setEditFields({ ...editFields, awards: v })} multiline />
                    <EditField label="Culture Summary" value={editFields.cultureSummary || ""} onChange={(v) => setEditFields({ ...editFields, cultureSummary: v })} multiline />
                    <EditField label="Mission" value={editFields.mission || ""} onChange={(v) => setEditFields({ ...editFields, mission: v })} multiline />
                    <EditField label="Vision" value={editFields.vision || ""} onChange={(v) => setEditFields({ ...editFields, vision: v })} multiline />

                    <Text className="text-xs font-bold text-blue uppercase tracking-wider mt-3">Authorized Representative</Text>
                    <EditField label="Name" value={editFields.authorizedRepName || ""} onChange={(v) => setEditFields({ ...editFields, authorizedRepName: v })} />
                    <EditField label="Designation" value={editFields.authorizedRepDesignation || ""} onChange={(v) => setEditFields({ ...editFields, authorizedRepDesignation: v })} />
                    <EditField label="Mobile" value={editFields.authorizedRepMobile || ""} onChange={(v) => setEditFields({ ...editFields, authorizedRepMobile: v })} keyboardType="phone-pad" />
                    <EditField label="Email" value={editFields.authorizedRepEmail || ""} onChange={(v) => setEditFields({ ...editFields, authorizedRepEmail: v })} keyboardType="email-address" />
                  </View>
                </ScrollView>
                <View className="flex-row gap-x-3 mt-4 pt-3 border-t border-line/50">
                  <Button variant="primary" size="md" loading={updateCompanyMutation.isPending} onPress={handleSaveCompany} className="flex-1">
                    Save All Changes
                  </Button>
                  <Button variant="ghost" size="md" onPress={() => setEditingCompany(false)} className="flex-1">
                    Cancel
                  </Button>
                </View>
              </Card>
            ) : (
              /* View mode with collapsible sections */
              <View className="gap-y-4">
                {/* Basic Info */}
                <CollapsibleSection title="Basic Information" defaultOpen>
                  <View className="flex-row flex-wrap">
                    <DetailRow label="Company Name" value={company.name} />
                    <DetailRow label="Brand Name" value={company.brandName} />
                    <DetailRow label="Public ID" value={company.publicCompanyId} />
                    <DetailRow label="Sector" value={company.sector} />
                    <DetailRow label="Company Type" value={company.companyType} />
                    <DetailRow label="City" value={company.city} />
                    <DetailRow label="State" value={company.state} />
                    <DetailRow label="Headquarter" value={company.headquarter} />
                    <DetailRow label="Incorporation Year" value={company.incorporationYear} />
                    <DetailRow label="Company Stage" value={company.companyStage} />
                    <DetailRow label="Business Model" value={company.businessModel} />
                  </View>
                  <Divider />
                  <DetailRow label="Description" value={company.description} />
                  <DetailRow label="Business Description" value={company.businessDescription} />
                  <DetailRow label="Website" value={company.website} />
                  <DetailRow label="Logo URL" value={company.logoUrl} />
                </CollapsibleSection>

                {/* Registration */}
                <CollapsibleSection title="Registration Details">
                  <View className="flex-row flex-wrap">
                    <DetailRow label="CIN" value={company.cin} />
                    <DetailRow label="GSTIN" value={company.gstin} />
                    <DetailRow label="PAN" value={company.pan} />
                    <DetailRow label="TAN" value={company.tan} />
                    <DetailRow label="MSME Registration" value={company.msmeRegistration} />
                    <DetailRow label="Startup India Reg." value={company.startupIndiaRegistration} />
                    <DetailRow label="Company Reg. Number" value={company.companyRegistrationNumber} />
                  </View>
                </CollapsibleSection>

                {/* Registered Office */}
                <CollapsibleSection title="Registered Office">
                  <View className="flex-row flex-wrap">
                    <DetailRow label="Address Line 1" value={company.registeredAddressLine1} />
                    <DetailRow label="Address Line 2" value={company.registeredAddressLine2} />
                    <DetailRow label="City" value={company.registeredCity} />
                    <DetailRow label="State" value={company.registeredState} />
                    <DetailRow label="Pincode" value={company.registeredPinCode} />
                    <DetailRow label="Country" value={company.registeredCountry} />
                  </View>
                </CollapsibleSection>

                {/* Contact */}
                <CollapsibleSection title="Contact Details">
                  <View className="flex-row flex-wrap">
                    <DetailRow label="Official Email" value={company.officialEmail} />
                    <DetailRow label="Official Phone" value={company.officialPhone} />
                    <DetailRow label="Phone Number" value={company.phoneNumber} />
                    <DetailRow label="LinkedIn" value={company.linkedinProfile} />
                    <DetailRow label="Twitter" value={company.twitterUrl} />
                    <DetailRow label="Social Media Links" value={company.socialMediaLinks} />
                  </View>
                </CollapsibleSection>

                {/* Financial */}
                <CollapsibleSection title="Financial Information">
                  <View className="flex-row flex-wrap">
                    <DetailRow label="Annual Turnover" value={company.annualTurnover} />
                    <DetailRow label="Paid-up Capital" value={company.paidUpCapital} />
                    <DetailRow label="Authorized Capital" value={company.authorizedCapital} />
                    <DetailRow label="Employee Count" value={company.employeeCount} />
                    <DetailRow label="Financial Year" value={company.financialYear} />
                    <DetailRow label="Total Funding" value={company.totalFunding} />
                    <DetailRow label="Investors" value={company.investors} />
                    <DetailRow label="Auditor Details" value={company.auditorDetails} />
                  </View>
                </CollapsibleSection>

                {/* Business */}
                <CollapsibleSection title="Business Information">
                  <DetailRow label="Products & Services" value={company.productsServices} />
                  <DetailRow label="Export/Import Status" value={company.exportImportStatus} />
                  <DetailRow label="Number of Branches" value={company.numBranches} />
                  <DetailRow label="Operational States" value={company.operationalStates} />
                  <DetailRow label="Certifications" value={company.certifications} />
                  <DetailRow label="Technologies Used" value={company.technologiesUsed} />
                </CollapsibleSection>

                {/* Extended Profile */}
                <CollapsibleSection title="Extended Profile">
                  <View className="flex-row flex-wrap">
                    <DetailRow label="CEO" value={company.ceoName} />
                    <DetailRow label="CTO" value={company.ctoName} />
                    <DetailRow label="Founders" value={company.founders} />
                    <DetailRow label="Awards" value={company.awards} />
                    <DetailRow label="Culture Summary" value={company.cultureSummary} />
                    <DetailRow label="Mission" value={company.mission} />
                    <DetailRow label="Vision" value={company.vision} />
                  </View>
                </CollapsibleSection>

                {/* Authorized Representative */}
                <CollapsibleSection title="Authorized Representative">
                  <View className="flex-row flex-wrap">
                    <DetailRow label="Name" value={company.authorizedRepName} />
                    <DetailRow label="Designation" value={company.authorizedRepDesignation} />
                    <DetailRow label="Mobile" value={company.authorizedRepMobile} />
                    <DetailRow label="Email" value={company.authorizedRepEmail} />
                  </View>
                </CollapsibleSection>

                {/* Timestamps */}
                <CollapsibleSection title="Timestamps & Audit">
                  <View className="flex-row flex-wrap">
                    <DetailRow label="Submitted At" value={company.submittedAt ? new Date(company.submittedAt).toLocaleString("en-IN") : "—"} />
                    <DetailRow label="Approved At" value={company.approvedAt ? new Date(company.approvedAt).toLocaleString("en-IN") : "—"} />
                    <DetailRow label="Listing Expires" value={company.listingExpiresAt ? new Date(company.listingExpiresAt).toLocaleDateString("en-IN") : "—"} />
                    <DetailRow label="Rejection Comment" value={company.rejectionComment} />
                    <DetailRow label="Created At" value={company.createdAt ? new Date(company.createdAt).toLocaleString("en-IN") : "—"} />
                    <DetailRow label="Updated At" value={company.updatedAt ? new Date(company.updatedAt).toLocaleString("en-IN") : "—"} />
                  </View>
                </CollapsibleSection>

                {/* Edit button */}
                <TouchableOpacity onPress={handleStartEditCompany} className="py-3">
                  <Text className="text-base font-bold text-blue text-center">✏️ Edit All Listing Details →</Text>
                </TouchableOpacity>
              </View>
            )}
          </>
        )}
      </View>

      {/* Reject Modal */}
      <RNModal visible={showRejectModal} transparent animationType="fade" onRequestClose={() => setShowRejectModal(false)}>
        <View className="flex-1 items-center justify-center bg-black/50 px-6">
          <View className="bg-white rounded-2xl w-full max-w-sm p-6">
            <Text className="text-lg font-extrabold text-navy mb-1">Reject Company Listing</Text>
            {company && (
              <Text className="text-sm font-semibold text-ink mb-4">{company.name}</Text>
            )}
            <Text className="text-xs text-muted mb-3">
              Provide feedback to help the company understand why their listing was rejected.
            </Text>
            <TextInput
              className="bg-bg rounded-xl px-4 py-3 text-sm text-ink border border-line min-h-[100px]"
              placeholder="Enter rejection reason / feedback..."
              placeholderTextColor={colors.faint}
              multiline
              value={rejectComment}
              onChangeText={setRejectComment}
              textAlignVertical="top"
            />
            <View className="flex-row gap-x-3 mt-5">
              <Button variant="ghost" size="md" onPress={() => setShowRejectModal(false)} className="flex-1">
                Cancel
              </Button>
              <Button variant="danger" size="md" loading={rejectMutation.isPending} onPress={handleReject} className="flex-1">
                Reject & Send
              </Button>
            </View>
          </View>
        </View>
      </RNModal>
    </ScrollView>
  );
}
