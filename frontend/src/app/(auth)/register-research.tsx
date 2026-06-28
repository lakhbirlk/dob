import { useState } from "react";
import {
  View, Text, ScrollView, KeyboardAvoidingView, Platform, TouchableOpacity,
} from "react-native";
import { Link, router } from "expo-router";
import { useAuth } from "@/hooks/useAuth";
import { useAuthStore } from "@/store/authStore";
import { Input } from "@/components/Input";
import { Button } from "@/components/Button";
import { UserRole } from "@/types";

const RESEARCH_PURPOSE_OPTIONS = [
  "Investment Research",
  "Market Research",
  "Academic Research",
  "Competitive Intelligence",
  "Vendor Verification",
  "Personal Research",
  "Other",
];

export default function RegisterResearchScreen() {
  const { register, isLoading, error, clearError } = useAuth();
  const [form, setForm] = useState({
    fullName: "",
    email: "",
    mobile: "",
    password: "",
    confirmPassword: "",
    occupation: "",
    organization: "",
    designation: "",
    researchPurpose: "",
    country: "India",
    state: "",
    city: "",
    industriesOfInterest: "",
    companySizePreference: "",
  });
  const [accepted, setAccepted] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [showPurposePicker, setShowPurposePicker] = useState(false);

  const update = (k: string, v: string) => {
    setForm(p => ({ ...p, [k]: v }));
    if (errors[k]) setErrors(p => { const n = { ...p }; delete n[k]; return n; });
  };

  const validate = (): boolean => {
    const e: Record<string, string> = {};
    if (!form.fullName.trim()) e.fullName = "Full name is required";
    if (!form.email.trim()) e.email = "Email is required";
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/i.test(form.email.trim())) e.email = "Enter a valid email";
    if (!form.mobile.trim()) e.mobile = "Mobile number is required";
    else if (!/^[0-9]{10}$/.test(form.mobile.replace(/\D/g, ""))) e.mobile = "Enter a valid 10-digit number";
    if (!form.password) e.password = "Password is required";
    else if (form.password.length < 8) e.password = "At least 8 characters";
    if (form.password !== form.confirmPassword) e.confirmPassword = "Passwords do not match";
    if (!form.occupation.trim()) e.occupation = "Occupation is required";
    if (!form.researchPurpose) e.researchPurpose = "Select a research purpose";
    if (!form.country.trim()) e.country = "Country is required";
    if (!form.state.trim()) e.state = "State is required";
    if (!form.city.trim()) e.city = "City is required";
    if (!accepted) e.accepted = "You must accept the terms";
    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const navigateByRole = (role: UserRole) => {
    switch (role) {
      case UserRole.ADMIN:
      case UserRole.SUPER_ADMIN:
        router.replace("/(admin)/dashboard");
        break;
      case UserRole.COMPANY:
        router.replace("/(company)/dashboard");
        break;
      case UserRole.MEMBER:
      default:
        router.replace("/(member)/dashboard");
        break;
    }
  };

  const handleSubmit = async () => {
    clearError();
    if (!validate()) return;
    try {
      await register({
        fullName: form.fullName.trim(),
        email: form.email.trim(),
        phone: form.mobile.trim(),
        password: form.password,
        role: UserRole.MEMBER,
      });
      const user = useAuthStore.getState().user;
      if (user) navigateByRole(user.role);
    } catch (e) {
      console.error("❌ Registration failed:", e);
    }
  };

  return (
    <KeyboardAvoidingView className="flex-1 bg-bg" behavior={Platform.OS === "ios" ? "padding" : "height"}>
      <ScrollView className="flex-1" keyboardShouldPersistTaps="handled" showsVerticalScrollIndicator={false}>
        {/* Header */}
        <View className="bg-teal pt-8 pb-6 px-6" style={{ borderBottomLeftRadius: 24, borderBottomRightRadius: 24 }}>
          <View className="flex-row items-center gap-x-3 mb-2">
            <Text className="text-2xl">🔍</Text>
            <Text className="text-white font-bold text-xl">Research Member</Text>
          </View>
          <Text className="text-white/80 text-sm">Create your research account to access company data</Text>
        </View>

        <View className="px-5 -mt-4">
          <View className="bg-card rounded-xl shadow-xl border border-line/30 px-6 py-7 mb-5">
            <Text className="text-xl font-extrabold text-ink mb-1">Personal Information</Text>
            <Text className="text-muted text-sm mb-5">Fill in your details to get started</Text>

            {error && (
              <View className="bg-red-light border border-red/30 rounded-lg px-4 py-3 mb-5 flex-row items-center justify-between">
                <Text className="text-red text-sm flex-1">{error}</Text>
                <TouchableOpacity onPress={clearError}><Text className="text-red font-bold text-sm ml-3">✕</Text></TouchableOpacity>
              </View>
            )}

            <Input label="Full Name *" value={form.fullName} onChangeText={(t) => update("fullName", t)} placeholder="Rahul Sharma" autoComplete="name" error={errors.fullName} />
            <Input label="Email Address *" value={form.email} onChangeText={(t) => update("email", t)} placeholder="you@example.com" keyboardType="email-address" autoCapitalize="none" autoComplete="email" error={errors.email} />
            <Input label="Mobile Number *" value={form.mobile} onChangeText={(t) => update("mobile", t)} placeholder="9876543210" keyboardType="phone-pad" error={errors.mobile} />
            <Input label="Password *" value={form.password} onChangeText={(t) => update("password", t)} placeholder="Min. 8 characters" isPassword error={errors.password} />
            <Input label="Confirm Password *" value={form.confirmPassword} onChangeText={(t) => update("confirmPassword", t)} placeholder="Re-enter password" isPassword error={errors.confirmPassword} />
          </View>

          {/* Professional Information */}
          <View className="bg-card rounded-xl shadow-lg border border-line/30 px-6 py-7 mb-5">
            <Text className="text-xl font-extrabold text-ink mb-1">Professional Information</Text>
            <Text className="text-muted text-sm mb-5">Tell us about your work</Text>

            <Input label="Occupation *" value={form.occupation} onChangeText={(t) => update("occupation", t)} placeholder="e.g. Analyst, Researcher" error={errors.occupation} />
            <Input label="Organization / Company" value={form.organization} onChangeText={(t) => update("organization", t)} placeholder="e.g. ABC Corp" />
            <Input label="Designation" value={form.designation} onChangeText={(t) => update("designation", t)} placeholder="e.g. Senior Analyst" />

            {/* Research Purpose */}
            <View className="mb-4">
              <Text className="text-sm font-semibold text-ink mb-1.5">
                Research Purpose * <Text className="text-red">*</Text>
              </Text>
              <TouchableOpacity
                className="border border-line rounded-xl px-4 py-3.5 bg-white flex-row justify-between items-center"
                onPress={() => setShowPurposePicker(!showPurposePicker)}
              >
                <Text className={form.researchPurpose ? "text-ink" : "text-faint"}>
                  {form.researchPurpose || "Select research purpose"}
                </Text>
                <Text className="text-faint">{showPurposePicker ? "▲" : "▼"}</Text>
              </TouchableOpacity>
              {errors.researchPurpose && <Text className="text-red text-xs mt-1">{errors.researchPurpose}</Text>}
              {showPurposePicker && (
                <View className="mt-1 bg-white border border-line rounded-xl overflow-hidden">
                  {RESEARCH_PURPOSE_OPTIONS.map((option) => (
                    <TouchableOpacity
                      key={option}
                      className={`px-4 py-3 border-b border-line/50 ${form.researchPurpose === option ? "bg-teal/10" : ""}`}
                      onPress={() => {
                        update("researchPurpose", option);
                        setShowPurposePicker(false);
                      }}
                    >
                      <Text className={`${form.researchPurpose === option ? "text-teal font-semibold" : "text-ink"}`}>
                        {option}
                      </Text>
                    </TouchableOpacity>
                  ))}
                </View>
              )}
            </View>
          </View>

          {/* Address */}
          <View className="bg-card rounded-xl shadow-lg border border-line/30 px-6 py-7 mb-5">
            <Text className="text-xl font-extrabold text-ink mb-1">Address</Text>
            <Text className="text-muted text-sm mb-5">Where are you located?</Text>

            <Input label="Country *" value={form.country} onChangeText={(t) => update("country", t)} placeholder="India" error={errors.country} />
            <Input label="State *" value={form.state} onChangeText={(t) => update("state", t)} placeholder="e.g. Maharashtra" error={errors.state} />
            <Input label="City *" value={form.city} onChangeText={(t) => update("city", t)} placeholder="e.g. Mumbai" error={errors.city} />
          </View>

          {/* Preferences */}
          <View className="bg-card rounded-xl shadow-lg border border-line/30 px-6 py-7 mb-5">
            <Text className="text-xl font-extrabold text-ink mb-1">Preferences</Text>
            <Text className="text-muted text-sm mb-5">Optional preferences to personalize your experience</Text>

            <Input label="Industries of Interest" value={form.industriesOfInterest} onChangeText={(t) => update("industriesOfInterest", t)} placeholder="e.g. Technology, Healthcare, Finance" hint="Comma-separated" />
            <Input label="Company Size Preference" value={form.companySizePreference} onChangeText={(t) => update("companySizePreference", t)} placeholder="e.g. Startup, SME, All" />
          </View>

          {/* Consent */}
          <View className="bg-card rounded-xl shadow-lg border border-line/30 px-6 py-7 mb-5">
            <TouchableOpacity className="flex-row items-start gap-x-2 mb-6" onPress={() => setAccepted(!accepted)}>
              <View className={`w-5 h-5 rounded border-2 items-center justify-center mt-0.5 ${accepted ? "bg-teal border-teal" : "border-faint"}`}>
                {accepted && <Text className="text-white text-xs font-bold">✓</Text>}
              </View>
              <View className="flex-1">
                <Text className="text-sm text-muted leading-5">
                  I agree to the{" "}
                  <Link href="/(public)/terms"><Text className="text-navy font-semibold">Terms of Service</Text></Link>
                  {" "}and{" "}
                  <Link href="/(public)/privacy"><Text className="text-navy font-semibold">Privacy Policy</Text></Link>
                </Text>
                {errors.accepted && <Text className="text-red text-xs mt-1">{errors.accepted}</Text>}
              </View>
            </TouchableOpacity>

            <Button variant="gold" size="xl" loading={isLoading} onPress={handleSubmit} style={{ width: "100%" }}>
              Create Research Account
            </Button>

            <View className="flex-row justify-center mt-6">
              <Text className="text-muted text-base">Already have an account? </Text>
              <Link href="/(auth)/login" asChild>
                <TouchableOpacity><Text className="text-navy font-bold text-base">Sign In</Text></TouchableOpacity>
              </Link>
            </View>
          </View>

          {/* Footer */}
          <Text className="text-faint text-xs text-center mb-8">
            🔒 Your data is encrypted and protected under India's DPDP Act 2023.
          </Text>
        </View>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}
