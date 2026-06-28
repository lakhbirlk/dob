import { useState } from "react";
import {
  View, Text, ScrollView, KeyboardAvoidingView, Platform, TouchableOpacity,
} from "react-native";
import { Link, router } from "expo-router";
import { useAuthStore } from "@/store/authStore";
import { Input } from "@/components/Input";
import { Button } from "@/components/Button";
import StepProgress from "@/components/StepProgress";
import DocumentUpload from "@/components/DocumentUpload";
import { authApi } from "@/services/api";

const STEPS = [
  { label: "Account", key: "account" },
  { label: "Company", key: "company" },
  { label: "Office & Contact", key: "office" },
  { label: "Rep & Finance", key: "rep-finance" },
  { label: "Submit", key: "submit" },
];

const COMPANY_TYPES = ["Private Limited", "Public Limited", "LLP", "Sole Proprietorship", "Partnership", "Other"];
const EXPORT_IMPORT_OPTIONS = ["Export", "Import", "Both", "None"];

type FormData = Record<string, string | number | boolean | undefined>;

const INITIAL_FORM: FormData = {
  // Account
  email: "", mobile: "", password: "", confirmPassword: "",
  // Company Info
  legalCompanyName: "", brandName: "", companyType: "", industry: "", businessCategory: "",
  dateOfIncorporation: "", cin: "", gstNumber: "", pan: "", tan: "",
  msmeRegistration: "", startupIndiaRegistration: "",
  // Registered Office
  addressLine1: "", addressLine2: "", city: "", state: "", pinCode: "", country: "India",
  // Contact
  officialEmail: "", officialPhone: "", website: "", linkedinProfile: "", socialMediaLinks: "",
  // Authorized Representative
  authorizedRepName: "", authorizedRepDesignation: "", authorizedRepMobile: "", authorizedRepEmail: "",
  // Financial
  annualTurnover: "", paidUpCapital: "", authorizedCapital: "", employeeCount: "",
  financialYear: "", auditorDetails: "",
  // Business
  productsServices: "", businessDescription: "", exportImportStatus: "",
  numBranches: "", operationalStates: "", certifications: "",
};

export default function RegisterCompanyScreen() {
  const [step, setStep] = useState(0);
  const [form, setForm] = useState<FormData>({ ...INITIAL_FORM });
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [isLoading, setIsLoading] = useState(false);
  const [apiError, setApiError] = useState<string | null>(null);
  const [accepted, setAccepted] = useState(false);

  const update = (k: string, v: string | boolean) => {
    setForm(p => ({ ...p, [k]: v }));
    if (errors[k]) setErrors(p => { const n = { ...p }; delete n[k]; return n; });
  };

  const validateStep = (): boolean => {
    const e: Record<string, string> = {};

    if (step === 0) {
      if (!form.email) e.email = "Email is required";
      else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/i.test(form.email as string)) e.email = "Enter a valid email";
      if (!form.mobile) e.mobile = "Mobile number is required";
      else if (!/^[0-9]{10}$/.test((form.mobile as string).replace(/\D/g, ""))) e.mobile = "Enter a valid 10-digit number";
      if (!form.password) e.password = "Password is required";
      else if ((form.password as string).length < 8) e.password = "At least 8 characters";
      if (form.password !== form.confirmPassword) e.confirmPassword = "Passwords do not match";
    } else if (step === 1) {
      if (!form.legalCompanyName) e.legalCompanyName = "Company name is required";
      if (!form.companyType) e.companyType = "Company type is required";
      if (!form.industry) e.industry = "Industry is required";
      if (!form.businessCategory) e.businessCategory = "Business category is required";
      if (!form.dateOfIncorporation) e.dateOfIncorporation = "Date of incorporation is required";
    } else if (step === 2) {
      if (!form.addressLine1) e.addressLine1 = "Address is required";
      if (!form.city) e.city = "City is required";
      if (!form.state) e.state = "State is required";
      if (!form.pinCode) e.pinCode = "PIN code is required";
    } else if (step === 3) {
      if (!form.authorizedRepName) e.authorizedRepName = "Representative name is required";
      if (!form.authorizedRepDesignation) e.authorizedRepDesignation = "Designation is required";
      if (!form.authorizedRepMobile) e.authorizedRepMobile = "Mobile is required";
      if (!form.authorizedRepEmail) e.authorizedRepEmail = "Email is required";
      if (!form.productsServices) e.productsServices = "Products/services description is required";
    } else if (step === 4) {
      if (!accepted) e.accepted = "You must accept the terms";
    }

    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const nextStep = () => {
    if (validateStep()) setStep(s => Math.min(s + 1, STEPS.length - 1));
  };

  const prevStep = () => {
    setStep(s => Math.max(s - 1, 0));
    setErrors({});
  };

  const handleSubmit = async () => {
    if (!validateStep()) return;
    setApiError(null);
    setIsLoading(true);

    try {
      const payload = {
        email: form.email as string,
        mobile: form.mobile as string,
        password: form.password as string,
        confirmPassword: form.confirmPassword as string,
        legalCompanyName: form.legalCompanyName as string,
        brandName: (form.brandName as string) || undefined,
        companyType: form.companyType as string,
        industry: form.industry as string,
        businessCategory: form.businessCategory as string,
        dateOfIncorporation: form.dateOfIncorporation as string,
        cin: (form.cin as string) || undefined,
        gstNumber: (form.gstNumber as string) || undefined,
        pan: (form.pan as string) || undefined,
        tan: (form.tan as string) || undefined,
        msmeRegistration: (form.msmeRegistration as string) || undefined,
        startupIndiaRegistration: (form.startupIndiaRegistration as string) || undefined,
        addressLine1: form.addressLine1 as string,
        addressLine2: (form.addressLine2 as string) || undefined,
        city: form.city as string,
        state: form.state as string,
        pinCode: form.pinCode as string,
        country: form.country as string,
        officialEmail: (form.officialEmail as string) || undefined,
        officialPhone: (form.officialPhone as string) || undefined,
        website: (form.website as string) || undefined,
        linkedinProfile: (form.linkedinProfile as string) || undefined,
        socialMediaLinks: (form.socialMediaLinks as string) || undefined,
        authorizedRepName: form.authorizedRepName as string,
        authorizedRepDesignation: form.authorizedRepDesignation as string,
        authorizedRepMobile: form.authorizedRepMobile as string,
        authorizedRepEmail: form.authorizedRepEmail as string,
        annualTurnover: (form.annualTurnover as string) || undefined,
        paidUpCapital: (form.paidUpCapital as string) || undefined,
        authorizedCapital: (form.authorizedCapital as string) || undefined,
        employeeCount: form.employeeCount ? parseInt(form.employeeCount as string, 10) : undefined,
        financialYear: (form.financialYear as string) || undefined,
        auditorDetails: (form.auditorDetails as string) || undefined,
        productsServices: form.productsServices as string,
        businessDescription: (form.businessDescription as string) || undefined,
        exportImportStatus: (form.exportImportStatus as string) || undefined,
        numBranches: form.numBranches ? parseInt(form.numBranches as string, 10) : undefined,
        operationalStates: (form.operationalStates as string) || undefined,
        certifications: (form.certifications as string) || undefined,
        acceptTerms: true,
        acceptPrivacy: true,
      };

      const response = await authApi.registerCompany(payload);
      const user = response.data.user;

      // Store auth state
      const { login: storeLogin } = useAuthStore.getState();
      await storeLogin(user, response.data.accessToken, response.data.refreshToken);

      // Navigate to company dashboard
      router.replace("/(company)/dashboard");
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : "Registration failed. Please try again.";
      setApiError(message);
    } finally {
      setIsLoading(false);
    }
  };

  const renderCompanyTypePicker = () => (
    <View className="mb-4">
      <Text className="text-sm font-semibold text-ink mb-1.5">Company Type *</Text>
      <View className="flex-row flex-wrap gap-2">
        {COMPANY_TYPES.map((type) => (
          <TouchableOpacity
            key={type}
            className={`px-4 py-2.5 rounded-lg border ${form.companyType === type ? "bg-navy border-navy" : "bg-white border-line"}`}
            onPress={() => update("companyType", type)}
          >
            <Text className={`text-sm ${form.companyType === type ? "text-white font-semibold" : "text-ink"}`}>{type}</Text>
          </TouchableOpacity>
        ))}
      </View>
      {errors.companyType && <Text className="text-red text-xs mt-1">{errors.companyType}</Text>}
    </View>
  );

  const renderExportImportPicker = () => (
    <View className="mb-4">
      <Text className="text-sm font-semibold text-ink mb-1.5">Export/Import Status</Text>
      <View className="flex-row flex-wrap gap-2">
        {EXPORT_IMPORT_OPTIONS.map((opt) => (
          <TouchableOpacity
            key={opt}
            className={`px-4 py-2.5 rounded-lg border ${form.exportImportStatus === opt ? "bg-navy border-navy" : "bg-white border-line"}`}
            onPress={() => update("exportImportStatus", opt)}
          >
            <Text className={`text-sm ${form.exportImportStatus === opt ? "text-white font-semibold" : "text-ink"}`}>{opt}</Text>
          </TouchableOpacity>
        ))}
      </View>
    </View>
  );

  const renderStep = () => {
    switch (step) {
      case 0:
        return (
          <View className="bg-card rounded-xl shadow-lg border border-line/30 px-6 py-7">
            <Text className="text-xl font-extrabold text-ink mb-1">Account Information</Text>
            <Text className="text-muted text-sm mb-5">Create your login credentials</Text>
            <Input label="Email Address *" value={form.email as string} onChangeText={(t) => update("email", t)} placeholder="you@company.com" keyboardType="email-address" autoCapitalize="none" error={errors.email} />
            <Input label="Mobile Number *" value={form.mobile as string} onChangeText={(t) => update("mobile", t)} placeholder="9876543210" keyboardType="phone-pad" error={errors.mobile} />
            <Input label="Password *" value={form.password as string} onChangeText={(t) => update("password", t)} placeholder="Min. 8 characters" isPassword error={errors.password} />
            <Input label="Confirm Password *" value={form.confirmPassword as string} onChangeText={(t) => update("confirmPassword", t)} placeholder="Re-enter password" isPassword error={errors.confirmPassword} />
          </View>
        );

      case 1:
        return (
          <View className="bg-card rounded-xl shadow-lg border border-line/30 px-6 py-7">
            <Text className="text-xl font-extrabold text-ink mb-1">Company Information</Text>
            <Text className="text-muted text-sm mb-5">Tell us about your business</Text>
            <Input label="Legal Company Name *" value={form.legalCompanyName as string} onChangeText={(t) => update("legalCompanyName", t)} placeholder="TechVentures India Pvt Ltd" error={errors.legalCompanyName} />
            <Input label="Brand Name (Optional)" value={form.brandName as string} onChangeText={(t) => update("brandName", t)} placeholder="TechVentures" />
            {renderCompanyTypePicker()}
            <Input label="Industry / Sector *" value={form.industry as string} onChangeText={(t) => update("industry", t)} placeholder="Information Technology" error={errors.industry} />
            <Input label="Business Category *" value={form.businessCategory as string} onChangeText={(t) => update("businessCategory", t)} placeholder="Software Development" error={errors.businessCategory} />
            <Input label="Date of Incorporation *" value={form.dateOfIncorporation as string} onChangeText={(t) => update("dateOfIncorporation", t)} placeholder="2015-06-15" hint="YYYY-MM-DD format" error={errors.dateOfIncorporation} />
            <Input label="CIN" value={form.cin as string} onChangeText={(t) => update("cin", t)} placeholder="U72300KA2016PTC123456" hint="21-character CIN" autoCapitalize="characters" />
            <Input label="GST Number" value={form.gstNumber as string} onChangeText={(t) => update("gstNumber", t)} placeholder="29ABCDE1234F1Z5" autoCapitalize="characters" />
            <Input label="PAN" value={form.pan as string} onChangeText={(t) => update("pan", t)} placeholder="AABCT1234E" autoCapitalize="characters" maxLength={10} />
            <Input label="TAN (Optional)" value={form.tan as string} onChangeText={(t) => update("tan", t)} placeholder="BANP12345A" autoCapitalize="characters" />
            <Input label="MSME Registration (Optional)" value={form.msmeRegistration as string} onChangeText={(t) => update("msmeRegistration", t)} placeholder="UDYAM-MH-01-0001234" />
            <Input label="Startup India Registration (Optional)" value={form.startupIndiaRegistration as string} onChangeText={(t) => update("startupIndiaRegistration", t)} placeholder="DIPP12345" />
          </View>
        );

      case 2:
        return (
          <View>
            {/* Registered Office */}
            <View className="bg-card rounded-xl shadow-lg border border-line/30 px-6 py-7 mb-4">
              <Text className="text-xl font-extrabold text-ink mb-1">Registered Office</Text>
              <Text className="text-muted text-sm mb-5">Company's registered address</Text>
              <Input label="Address Line 1 *" value={form.addressLine1 as string} onChangeText={(t) => update("addressLine1", t)} placeholder="123, MG Road" error={errors.addressLine1} />
              <Input label="Address Line 2" value={form.addressLine2 as string} onChangeText={(t) => update("addressLine2", t)} placeholder="Indiranagar" />
              <Input label="City *" value={form.city as string} onChangeText={(t) => update("city", t)} placeholder="Bengaluru" error={errors.city} />
              <Input label="State *" value={form.state as string} onChangeText={(t) => update("state", t)} placeholder="Karnataka" error={errors.state} />
              <Input label="PIN Code *" value={form.pinCode as string} onChangeText={(t) => update("pinCode", t)} placeholder="560038" keyboardType="number-pad" error={errors.pinCode} />
              <Input label="Country" value={form.country as string} onChangeText={(t) => update("country", t)} placeholder="India" />
            </View>

            {/* Contact Details */}
            <View className="bg-card rounded-xl shadow-lg border border-line/30 px-6 py-7">
              <Text className="text-xl font-extrabold text-ink mb-1">Contact Details</Text>
              <Text className="text-muted text-sm mb-5">How customers can reach you</Text>
              <Input label="Official Email" value={form.officialEmail as string} onChangeText={(t) => update("officialEmail", t)} placeholder="contact@company.com" keyboardType="email-address" autoCapitalize="none" />
              <Input label="Official Phone" value={form.officialPhone as string} onChangeText={(t) => update("officialPhone", t)} placeholder="9876543210" keyboardType="phone-pad" />
              <Input label="Website" value={form.website as string} onChangeText={(t) => update("website", t)} placeholder="https://www.company.com" keyboardType="url" autoCapitalize="none" />
              <Input label="LinkedIn Profile" value={form.linkedinProfile as string} onChangeText={(t) => update("linkedinProfile", t)} placeholder="https://linkedin.com/company/..." autoCapitalize="none" />
              <Input label="Social Media Links (Optional)" value={form.socialMediaLinks as string} onChangeText={(t) => update("socialMediaLinks", t)} placeholder="Instagram, Twitter URLs" />
            </View>
          </View>
        );

      case 3:
        return (
          <View>
            {/* Authorized Representative */}
            <View className="bg-card rounded-xl shadow-lg border border-line/30 px-6 py-7 mb-4">
              <Text className="text-xl font-extrabold text-ink mb-1">Authorized Representative</Text>
              <Text className="text-muted text-sm mb-5">Person authorized to represent the company</Text>
              <Input label="Full Name *" value={form.authorizedRepName as string} onChangeText={(t) => update("authorizedRepName", t)} placeholder="Rajesh Kumar" error={errors.authorizedRepName} />
              <Input label="Designation *" value={form.authorizedRepDesignation as string} onChangeText={(t) => update("authorizedRepDesignation", t)} placeholder="Director" error={errors.authorizedRepDesignation} />
              <Input label="Mobile Number *" value={form.authorizedRepMobile as string} onChangeText={(t) => update("authorizedRepMobile", t)} placeholder="9876543211" keyboardType="phone-pad" error={errors.authorizedRepMobile} />
              <Input label="Email Address *" value={form.authorizedRepEmail as string} onChangeText={(t) => update("authorizedRepEmail", t)} placeholder="rajesh@company.com" keyboardType="email-address" autoCapitalize="none" error={errors.authorizedRepEmail} />
              <DocumentUpload label="Identity Proof" required />
              <DocumentUpload label="Digital Signature (Optional)" />
            </View>

            {/* Financial Information */}
            <View className="bg-card rounded-xl shadow-lg border border-line/30 px-6 py-7 mb-4">
              <Text className="text-xl font-extrabold text-ink mb-1">Financial Information</Text>
              <Text className="text-muted text-sm mb-5">Company financial overview</Text>
              <Input label="Annual Turnover" value={form.annualTurnover as string} onChangeText={(t) => update("annualTurnover", t)} placeholder="₹50Cr-100Cr" />
              <Input label="Paid-up Capital" value={form.paidUpCapital as string} onChangeText={(t) => update("paidUpCapital", t)} placeholder="₹1Cr" />
              <Input label="Authorized Capital" value={form.authorizedCapital as string} onChangeText={(t) => update("authorizedCapital", t)} placeholder="₹5Cr" />
              <Input label="Employee Count" value={form.employeeCount as string} onChangeText={(t) => update("employeeCount", t)} placeholder="200" keyboardType="number-pad" />
              <Input label="Financial Year" value={form.financialYear as string} onChangeText={(t) => update("financialYear", t)} placeholder="2024-25" />
              <Input label="Auditor Details (Optional)" value={form.auditorDetails as string} onChangeText={(t) => update("auditorDetails", t)} placeholder="ABC & Co, Chartered Accountants" />
            </View>

            {/* Business Information */}
            <View className="bg-card rounded-xl shadow-lg border border-line/30 px-6 py-7">
              <Text className="text-xl font-extrabold text-ink mb-1">Business Information</Text>
              <Text className="text-muted text-sm mb-5">Tell us about your business operations</Text>
              <Input label="Products & Services *" value={form.productsServices as string} onChangeText={(t) => update("productsServices", t)} placeholder="Enterprise SaaS platform" error={errors.productsServices} />
              <Input label="Business Description" value={form.businessDescription as string} onChangeText={(t) => update("businessDescription", t)} placeholder="Brief description of your business" multiline />
              {renderExportImportPicker()}
              <Input label="Number of Branches" value={form.numBranches as string} onChangeText={(t) => update("numBranches", t)} placeholder="5" keyboardType="number-pad" />
              <Input label="Operational States" value={form.operationalStates as string} onChangeText={(t) => update("operationalStates", t)} placeholder="Karnataka, Maharashtra, Tamil Nadu" />
              <Input label="Certifications" value={form.certifications as string} onChangeText={(t) => update("certifications", t)} placeholder="ISO 9001:2015, ISO 27001" />
            </View>
          </View>
        );

      case 4:
        return (
          <View>
            {/* Summary */}
            <View className="bg-card rounded-xl shadow-lg border border-line/30 px-6 py-7 mb-4">
              <Text className="text-xl font-extrabold text-ink mb-1">Review & Submit</Text>
              <Text className="text-muted text-sm mb-5">Please review your information before submitting</Text>

              <View className="bg-bg rounded-lg p-4 mb-4">
                <Text className="font-bold text-ink mb-2">Account</Text>
                <Text className="text-muted text-sm">Email: {form.email}</Text>
                <Text className="text-muted text-sm">Mobile: {form.mobile}</Text>
              </View>

              <View className="bg-bg rounded-lg p-4 mb-4">
                <Text className="font-bold text-ink mb-2">Company</Text>
                <Text className="text-muted text-sm">Name: {form.legalCompanyName}</Text>
                {form.brandName ? <Text className="text-muted text-sm">Brand: {form.brandName}</Text> : null}
                <Text className="text-muted text-sm">Type: {form.companyType}</Text>
                <Text className="text-muted text-sm">Industry: {form.industry}</Text>
                {form.cin ? <Text className="text-muted text-sm">CIN: {form.cin}</Text> : null}
                {form.gstNumber ? <Text className="text-muted text-sm">GST: {form.gstNumber}</Text> : null}
              </View>

              <View className="bg-bg rounded-lg p-4 mb-4">
                <Text className="font-bold text-ink mb-2">Office & Contact</Text>
                <Text className="text-muted text-sm">{form.addressLine1}, {form.city}, {form.state} - {form.pinCode}</Text>
                {form.officialEmail ? <Text className="text-muted text-sm">Email: {form.officialEmail}</Text> : null}
              </View>

              <View className="bg-bg rounded-lg p-4 mb-4">
                <Text className="font-bold text-ink mb-2">Authorized Representative</Text>
                <Text className="text-muted text-sm">{form.authorizedRepName} ({form.authorizedRepDesignation})</Text>
              </View>

              {form.productsServices && (
                <View className="bg-bg rounded-lg p-4 mb-4">
                  <Text className="font-bold text-ink mb-2">Business Info</Text>
                  <Text className="text-muted text-sm">{form.productsServices}</Text>
                </View>
              )}
            </View>

            {/* Consent */}
            <View className="bg-card rounded-xl shadow-lg border border-line/30 px-6 py-7 mb-4">
              <Text className="text-xl font-extrabold text-ink mb-1">Consent</Text>
              <TouchableOpacity className="flex-row items-start gap-x-2 mt-3" onPress={() => setAccepted(!accepted)}>
                <View className={`w-5 h-5 rounded border-2 items-center justify-center mt-0.5 ${accepted ? "bg-navy border-navy" : "border-faint"}`}>
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
            </View>

            {/* Error banner */}
            {apiError && (
              <View className="bg-red-light border border-red/30 rounded-lg px-4 py-3 mb-4 flex-row items-center justify-between">
                <Text className="text-red text-sm flex-1">{apiError}</Text>
                <TouchableOpacity onPress={() => setApiError(null)}><Text className="text-red font-bold text-sm ml-3">✕</Text></TouchableOpacity>
              </View>
            )}

            {/* Submit button */}
            <Button variant="gold" size="xl" loading={isLoading} onPress={handleSubmit} style={{ width: "100%", marginBottom: 16 }}>
              Register Company
            </Button>
          </View>
        );

      default:
        return null;
    }
  };

  return (
    <KeyboardAvoidingView className="flex-1 bg-bg" behavior={Platform.OS === "ios" ? "padding" : "height"}>
      <ScrollView className="flex-1" keyboardShouldPersistTaps="handled" showsVerticalScrollIndicator={false}>
        {/* Header */}
        <View className="bg-navy-deep pt-4 pb-4 px-6" style={{ borderBottomLeftRadius: 24, borderBottomRightRadius: 24 }}>
          <View className="flex-row items-center gap-x-3 mb-1">
            <Text className="text-2xl">🏢</Text>
            <Text className="text-white font-bold text-xl">Company Registration</Text>
          </View>
          <Text className="text-white/70 text-sm">Step {step + 1} of {STEPS.length}</Text>
        </View>

        {/* Step Progress */}
        <StepProgress steps={STEPS} currentStep={step} />

        <View className="px-5 pb-8">
          {renderStep()}

          {/* Navigation Buttons (not on submit step) */}
          {step < 4 && (
            <View className="flex-row gap-x-3 mt-4 mb-8">
              {step > 0 && (
                <View className="flex-1">
                  <Button variant="outline" size="lg" onPress={prevStep}>
                    ← Back
                  </Button>
                </View>
              )}
              <View className={step > 0 ? "flex-1" : "w-full"}>
                <Button variant="primary" size="lg" onPress={nextStep}>
                  Continue →
                </Button>
              </View>
            </View>
          )}

          {/* Sign In link */}
          {step === 0 && (
            <View className="flex-row justify-center mb-8">
              <Text className="text-muted text-base">Already have an account? </Text>
              <Link href="/(auth)/login" asChild>
                <TouchableOpacity><Text className="text-navy font-bold text-base">Sign In</Text></TouchableOpacity>
              </Link>
            </View>
          )}

          {/* Footer */}
          <Text className="text-faint text-xs text-center">
            🔒 Your data is encrypted and protected under India's DPDP Act 2023.
          </Text>
        </View>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}
