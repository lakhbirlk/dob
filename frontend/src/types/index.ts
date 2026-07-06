// ─────────────────────── Enums ───────────────────────

export enum UserRole {
  SUPER_ADMIN = "SUPER_ADMIN",
  ADMIN = "ADMIN",
  COMPANY = "COMPANY",
  MEMBER = "MEMBER",
}

export enum CompanyStatus {
  DRAFT = "DRAFT",
  PENDING_REVIEW = "PENDING_REVIEW",
  REJECTED = "REJECTED",
  APPROVED_MEMBERSHIP_PENDING = "APPROVED_MEMBERSHIP_PENDING",
  APPROVED_ACTIVE = "APPROVED_ACTIVE",
  MEMBERSHIP_EXPIRED = "MEMBERSHIP_EXPIRED",
  SUSPENDED = "SUSPENDED",
}

export enum MembershipStatus {
  ACTIVE = "ACTIVE",
  EXPIRED = "EXPIRED",
  CANCELLED = "CANCELLED",
  PENDING = "PENDING",
}

export enum PaymentStatus {
  CREATED = "CREATED",
  VERIFIED = "VERIFIED",
  FAILED = "FAILED",
  REFUNDED = "REFUNDED",
}

export enum GrievanceStatus {
  OPEN = "OPEN",
  IN_PROGRESS = "IN_PROGRESS",
  RESOLVED = "RESOLVED",
  CLOSED = "CLOSED",
}

export enum GrievancePriority {
  LOW = "LOW",
  MEDIUM = "MEDIUM",
  HIGH = "HIGH",
  CRITICAL = "CRITICAL",
}

export enum RefundStatus {
  REQUESTED = "REQUESTED",
  PROCESSING = "PROCESSING",
  APPROVED = "APPROVED",
  REJECTED = "REJECTED",
  COMPLETED = "COMPLETED",
}

export enum NotificationType {
  INFO = "INFO",
  WARNING = "WARNING",
  SUCCESS = "SUCCESS",
  ERROR = "ERROR",
}

// ─────────────────────── User ───────────────────────

export interface User {
  id: string;
  email: string;
  fullName: string;
  phone: string | null;
  role: UserRole;
  panNumber: string | null;
  isEmailVerified: boolean;
  isPhoneVerified: boolean;
  avatarUrl: string | null;
  createdAt: string;
  updatedAt: string;
}

// ─────────────────────── Company ───────────────────────

export interface CompanyProfile {
  id: string;
  companyId: string;
  foundedYear: number | null;
  employeeCount: number | null;
  website: string | null;
  description: string | null;
  logoUrl: string | null;
  coverImageUrl: string | null;
  linkedinUrl: string | null;
  twitterUrl: string | null;
  phoneNumber: string | null;
  headquarter: string | null;
  businessModel: string | null;
  companyStage: string | null;
  mission: string | null;
  vision: string | null;
  cultureSummary: string | null;
  ceoName: string | null;
  ctoName: string | null;
  founders: string | null;
  totalFunding: string | null;
  investors: string | null;
  technologiesUsed: string | null;
  awards: string | null;
  certificationsOverview: string | null;
  products: string | null;
  services: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface FinancialStatement {
  id: string;
  companyId: string;
  financialYear: string;
  // Documents
  balanceSheetUrl: string | null;
  profitLossUrl: string | null;
  cashFlowUrl: string | null;
  auditorReportUrl: string | null;
  taxFilingUrl: string | null;
  // Metadata
  uploadDate: string | null;
  uploadedBy: string | null;
  status: string | null;
  version: string | null;
  fileSize: string | null;
  fileType: string | null;
  downloadUrl: string | null;
  // Financial metrics
  revenue: number;
  expenses: number;
  ebitda: number;
  netProfit: number;
  assets: number;
  liabilities: number;
  equity: number;
  operatingCashFlow: number;
  capex: number;
  debt: number;
  // Legacy fields
  year: number;
  profit: number;
  documentUrl: string | null;
  isVerified: boolean;
  createdAt: string;
}

export interface Certificate {
  id: string;
  companyId: string;
  certificateName: string | null;
  certificateNumber: string | null;
  issuingAuthority: string;
  issueDate: string;
  expiryDate: string | null;
  status: string | null;
  verificationUrl: string | null;
  pdfUrl: string | null;
  thumbnailUrl: string | null;
  description: string | null;
  name: string;
  documentUrl: string;
  isVerified: boolean;
  createdAt: string;
}

export interface Video {
  id: string;
  companyId: string;
  title: string;
  description: string | null;
  duration: string | null;
  videoUrl: string;
  thumbnailUrl: string | null;
  uploadDate: string | null;
  category: string | null;
  views: number | null;
  likes: number | null;
  comments: number | null;
  shares: number | null;
  language: string | null;
  resolution: string | null;
  status: string | null;
  transcriptSummary: string | null;
  speaker: string | null;
  url: string;
  durationSeconds: number | null;
  createdAt: string;
}

export interface Company {
  id: string;
  userId: string;
  name: string;
  legalName: string;
  cin: string | null;
  gstin: string | null;
  sector: string;
  subSector: string | null;
  state: string;
  city: string;
  address: string;
  pincode: string;
  companyType: string;
  incorporationYear?: number | null;
  description?: string | null;
  status: CompanyStatus;
  verified: boolean;
  featured: boolean;
  profile: CompanyProfile | null;
  financials: FinancialStatement[];
  certificates: Certificate[];
  videos: Video[];
  createdAt: string;
  updatedAt: string;
}

// ─────────────────────── Membership ───────────────────────

export interface MembershipPlan {
  id: string;
  name: string;
  description: string;
  price: number;
  durationMonths: number;
  features: string[];
  isActive: boolean;
}

export interface Membership {
  id: string;
  userId: string;
  planId: string;
  plan: MembershipPlan;
  status: MembershipStatus;
  startDate: string;
  endDate: string;
  autoRenew: boolean;
  createdAt: string;
}

// ─────────────────────── Payment ───────────────────────

export interface Payment {
  id: string;
  userId: string;
  membershipId: string | null;
  orderId: string;
  amount: number;
  currency: string;
  status: PaymentStatus;
  razorpayPaymentId: string | null;
  razorpaySignature: string | null;
  createdAt: string;
}

// ─────────────────────── Subscription (Simplified Flow) ───────────────────────

export enum PlanType {
  RESEARCH = "RESEARCH",
  COMPANY = "COMPANY",
}

export interface CreateSubscriptionResponse {
  subscriptionId: string;
  amount: number;
  status: string;
}

export interface PaymentSuccessResponse {
  membershipId: string;
  plan: string;
  status: string;
  startDate: string;
  expiryDate: string;
  transactionId: string;
  amountPaid: number;
}

export interface UserMembershipResponse {
  plan: string;
  status: string;
  startDate: string;
  expiryDate: string;
}

// ─────────────────────── Grievance ───────────────────────

export interface Grievance {
  id: string;
  userId: string;
  companyId: string | null;
  subject: string;
  description: string;
  status: GrievanceStatus;
  priority: GrievancePriority;
  assignedTo: string | null;
  resolution: string | null;
  createdAt: string;
  updatedAt: string;
}

// ─────────────────────── Refund ───────────────────────

export interface Refund {
  id: string;
  userId: string;
  paymentId: string;
  amount: number;
  reason: string;
  status: RefundStatus;
  processedBy: string | null;
  processedAt: string | null;
  adminNote: string | null;
  createdAt: string;
  updatedAt: string;
}

// ─────────────────────── Notification ───────────────────────

export interface Notification {
  id: string;
  userId: string;
  type: NotificationType;
  title: string;
  message: string;
  isRead: boolean;
  link: string | null;
  createdAt: string;
}

// ─────────────────────── Audit Log ───────────────────────

export interface AuditLog {
  id: string;
  userId: string;
  action: string;
  resource: string;
  resourceId: string;
  details: string | null;
  ipAddress: string | null;
  createdAt: string;
}

// ─────────────────────── Auth ───────────────────────

export interface AuthResponse {
  user: User;
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  fullName: string;
  phone: string;
  role?: UserRole;
}

// ─────────────────────── Registration Types ───────────────────────

export interface CompanyRegistrationRequest {
  email: string;
  mobile: string;
  password: string;
  confirmPassword: string;
  legalCompanyName: string;
  brandName?: string;
  companyType: string;
  industry: string;
  businessCategory: string;
  dateOfIncorporation: string;
  cin?: string;
  gstNumber?: string;
  pan?: string;
  tan?: string;
  msmeRegistration?: string;
  startupIndiaRegistration?: string;
  addressLine1: string;
  addressLine2?: string;
  city: string;
  state: string;
  pinCode: string;
  country: string;
  officialEmail?: string;
  officialPhone?: string;
  website?: string;
  linkedinProfile?: string;
  socialMediaLinks?: string;
  authorizedRepName: string;
  authorizedRepDesignation: string;
  authorizedRepMobile: string;
  authorizedRepEmail: string;
  authorizedRepIdentityProofUrl?: string;
  authorizedRepDigitalSignatureUrl?: string;
  annualTurnover?: string;
  paidUpCapital?: string;
  authorizedCapital?: string;
  employeeCount?: number;
  financialYear?: string;
  balanceSheetUrl?: string;
  auditorDetails?: string;
  productsServices: string;
  businessDescription?: string;
  exportImportStatus?: string;
  numBranches?: number;
  operationalStates?: string;
  certifications?: string;
  acceptTerms: boolean;
  acceptPrivacy: boolean;
}

export interface ResearchMemberRegistrationRequest {
  fullName: string;
  email: string;
  mobile: string;
  password: string;
  confirmPassword: string;
  occupation: string;
  organization?: string;
  designation?: string;
  researchPurpose: string;
  country: string;
  state: string;
  city: string;
  industriesOfInterest?: string;
  companySizePreference?: string;
  notificationPreferences?: string;
  acceptTerms: boolean;
  acceptPrivacy: boolean;
}

// ─────────────────────── API ───────────────────────

export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message: string;
}

export interface PaginatedResponse<T> {
  success: boolean;
  data: T[];
  message: string;
  pagination: {
    page: number;
    limit: number;
    total: number;
    totalPages: number;
  };
}

// ─────────────────────── Company Data Masking / Premium Access ───────────────────────

/**
 * FreeCompanyResponse — returned to non-subscribed users.
 * Never includes company name, CIN, GST, PAN, directors, or contact details.
 */
export interface FreeCompanyResponse {
  companyId: string;          // DOB-XXXXXXXX
  industry: string | null;
  businessType: string | null;
  state: string | null;
  city: string | null;
  companyAge: number | null;
  employeeRange: string | null;
  revenueRange: string | null;
  riskScore: string | null;
  verified: boolean;
  locked: boolean;            // Always true for free users
  summary: string | null;
}

/**
 * PremiumCompanyResponse — returned to subscribed users.
 * Contains the complete company profile including identifying information.
 */
export interface PremiumCompanyResponse {
  companyId: string;
  companyName: string;
  logoUrl: string | null;
  // Identifiers
  cin: string | null;
  gstin: string | null;
  pan: string | null;
  registrationNumber: string | null;
  companyRegistrationNumber: string | null;
  // Industry & Classification
  industry: string | null;
  subSector: string | null;
  businessType: string | null;
  businessModel: string | null;
  companyStage: string | null;
  // Incorporation & Age
  incorporationYear: number | null;
  companyAge: number | null;
  // Location
  state: string | null;
  city: string | null;
  address: string | null;
  headquarter: string | null;
  numBranches: number | null;
  // Contact
  email: string | null;
  phoneNumber: string | null;
  website: string | null;
  linkedinUrl: string | null;
  twitterUrl: string | null;
  // Size & Revenue
  employeeRange: string | null;
  employeeCount: number | null;
  annualRevenue: string | null;
  revenueRange: string | null;
  totalFunding: string | null;
  investors: string | null;
  // Risk & Verification
  riskScore: string | null;
  verified: boolean;
  locked: boolean;
  // Description & Brand
  description: string | null;
  mission: string | null;
  vision: string | null;
  cultureSummary: string | null;
  // Leadership
  keyExecutives: Array<Record<string, string>>;
  ceoName: string | null;
  ctoName: string | null;
  founders: string | null;
  // Products & Services
  products: string | null;
  services: string | null;
  technologiesUsed: string | null;
  // Certifications & Awards
  certificationsOverview: string | null;
  awards: string | null;
  // Shareholding
  shareholding: Array<Record<string, unknown>>;
  // Data
  financials: FinancialStatement[];
  certificates: Certificate[];
  videos: Video[];
  aiAnalysis: string | null;
  riskReportUrl: string | null;
  canDownload: boolean;
  companyUrl: string | null;
  status: string;
  dashboardStatus: string | null;
  createdAt: string;
  updatedAt: string;
}

/**
 * Union type — use `locked` as the discriminator.
 * locked === true → FreeCompanyResponse (masked)
 * locked === false → PremiumCompanyResponse (full)
 */
export type CompanyResponse = FreeCompanyResponse | PremiumCompanyResponse;

/**
 * CompanyDto — summary DTO returned from CRUD and admin endpoints.
 */
export interface CompanyDto {
  id: string;
  publicCompanyId: string;
  name: string;
  sector: string | null;
  state: string | null;
  city: string | null;
  companyType: string | null;
  incorporationYear: number | null;
  description: string | null;
  website: string | null;
  logoUrl?: string | null;
  status: CompanyStatus;
  createdBy: string;
  createdByName?: string | null;
  createdAt: string;
  updatedAt: string;
}

// ─────────────────────── Company Workflow / Listing Management ───────────────────────

/**
 * CompanyDetailDto — returned for owner/admin views with full workflow details.
 */
export interface CompanyDetailDto {
  id: string;
  publicCompanyId: string;
  name: string;
  sector: string | null;
  state: string | null;
  city: string | null;
  companyType: string | null;
  incorporationYear: number | null;
  description: string | null;
  website: string | null;
  logoUrl: string | null;
  status: CompanyStatus;
  createdBy: string;
  approvedBy: string | null;
  approvedAt: string | null;
  submittedAt: string | null;
  rejectionComment: string | null;
  listingExpiresAt: string | null;
  isPubliclyVisible: boolean;
  hasActiveListingMembership: boolean;
  profile?: CompanyProfile | null;
  financials?: FinancialStatement[];
  certificates?: Certificate[];
  videos?: Video[];
  financialDataJson?: string | null;
  certificatesDataJson?: string | null;
  videosDataJson?: string | null;
  createdAt: string;
  updatedAt: string;
}

/**
 * Review request payload for admin reject.
 */
export interface ReviewRequest {
  comment?: string;
}

// ─────────────────────── Search ───────────────────────

export interface SearchFilters {
  sector?: string;
  state?: string;
  city?: string;
  companyType?: string;
  revenueRange?: string;
  membershipFilter?: string;
  verified?: boolean;
  featured?: boolean;
  search?: string;
  page?: number;
  limit?: number;
  sortBy?: string;
  sortOrder?: "asc" | "desc";
}

export interface RevenueRange {
  label: string;
  min: number;
  max: number | null;
}

export interface DownloadDocument {
  id: string;
  userId: string;
  companyId: string;
  documentType: string;
  documentId: string;
  companyName?: string;
  downloadedAt: string;
}
