import axios, {
  AxiosError,
  AxiosInstance,
  InternalAxiosRequestConfig,
} from "axios";
import { storage } from "@/services/storage";
import type {
  ApiResponse,
  AuthResponse,
  Certificate,
  Company,
  CompanyDetailDto,
  CompanyDto,
  CompanyRegistrationRequest,
  CompanyResponse,
  CreateSubscriptionResponse,
  FinancialStatement,
  FreeCompanyResponse,
  LoginRequest,
  Membership,
  MembershipPlan,
  Notification,
  PaginatedResponse,
  Payment,
  PaymentSuccessResponse,
  PremiumCompanyResponse,
  RegisterRequest,
  ResearchMemberRegistrationRequest,
  ReviewRequest,
  SearchFilters,
  User,
  UserMembershipResponse,
  Video,
  Grievance,
  Refund,
  AuditLog,
  DownloadDocument,
} from "@/types";
import { UserRole, CompanyStatus } from "@/types";

// ─────────────────────── Config ───────────────────────

const API_BASE_URL =
  process.env.EXPO_PUBLIC_API_URL || "http://localhost:8080/api";

const ACCESS_TOKEN_KEY = "dob_access_token";
const REFRESH_TOKEN_KEY = "dob_refresh_token";

// ─────────────────────── Token Helpers ───────────────────────

async function getAccessToken(): Promise<string | null> {
  return storage.getItemAsync(ACCESS_TOKEN_KEY);
}

async function getRefreshToken(): Promise<string | null> {
  return storage.getItemAsync(REFRESH_TOKEN_KEY);
}

export async function setTokens(
  accessToken: string,
  refreshToken: string
): Promise<void> {
  await storage.setItemAsync(ACCESS_TOKEN_KEY, accessToken);
  await storage.setItemAsync(REFRESH_TOKEN_KEY, refreshToken);
}

export async function clearTokens(): Promise<void> {
  await storage.deleteItemAsync(ACCESS_TOKEN_KEY);
  await storage.deleteItemAsync(REFRESH_TOKEN_KEY);
}

// ─────────────────────── Global Auth Failure Handler ───────────────────────

export type AuthFailureReason = "session_expired" | "forbidden";

let authFailureHandler: ((reason: AuthFailureReason) => void) | null = null;

export function setAuthFailureHandler(handler: (reason: AuthFailureReason) => void): void {
  authFailureHandler = handler;
}

// ─────────────────────── Axios Instance ───────────────────────

const api: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
  headers: {
    "Content-Type": "application/json",
  },
});

// ─────────────────────── Request Interceptor ───────────────────────

api.interceptors.request.use(
  async (config: InternalAxiosRequestConfig) => {
    const token = await getAccessToken();
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error: AxiosError) => Promise.reject(error)
);

// ─────────────────────── Response Interceptor ───────────────────────

let isRefreshing = false;
let failedQueue: Array<{
  resolve: (value: unknown) => void;
  reject: (reason: unknown) => void;
}> = [];

function processQueue(error: unknown, token: string | null = null): void {
  failedQueue.forEach((promise) => {
    if (error) {
      promise.reject(error);
    } else {
      promise.resolve(token);
    }
  });
  failedQueue = [];
}

api.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const status = error.response?.status;
    const originalRequest = error.config as InternalAxiosRequestConfig & {
      _retry?: boolean;
    };

    // 403 Forbidden — authenticated but insufficient role
    if (status === 403) {
      if (authFailureHandler) authFailureHandler("forbidden");
      return Promise.reject(error);
    }

    // Only attempt refresh on 401 and if we haven't already retried
    if (status !== 401 || originalRequest._retry) {
      return Promise.reject(error);
    }

    if (isRefreshing) {
      // Queue this request while another refresh is in progress
      return new Promise((resolve, reject) => {
        failedQueue.push({ resolve, reject });
      }).then((token) => {
        if (originalRequest.headers) {
          originalRequest.headers.Authorization = `Bearer ${token}`;
        }
        return api(originalRequest);
      });
    }

    originalRequest._retry = true;
    isRefreshing = true;

    try {
      const refreshToken = await getRefreshToken();
      if (!refreshToken) {
        throw new Error("No refresh token available");
      }

      const tokenRes = await authApi.refreshToken(refreshToken);

      const newAccessToken = tokenRes.data.accessToken;
      const newRefreshToken = tokenRes.data.refreshToken;

      await setTokens(newAccessToken, newRefreshToken);

      processQueue(null, newAccessToken);

      if (originalRequest.headers) {
        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
      }

      return api(originalRequest);
    } catch (refreshError) {
      processQueue(refreshError, null);
      await clearTokens();
      if (authFailureHandler) authFailureHandler("session_expired");
      return Promise.reject(refreshError);
    } finally {
      isRefreshing = false;
    }
  }
);

// ─────────────────────── User DTO Mapper ───────────────────────
// Maps backend UserDto (snake_case, different field names) to frontend User interface

function mapUserDto(raw: Record<string, unknown>): User {
  return {
    id: raw.id as string,
    email: raw.email as string,
    fullName: raw.fullName as string,
    phone: (raw.phone as string) ?? null,
    role: mapRole(raw.role as string),
    panNumber: (raw.pan as string) ?? null,
    isEmailVerified: (raw.emailVerified as boolean) ?? false,
    isPhoneVerified: false,
    avatarUrl: (raw.avatarUrl as string) ?? null,
    createdAt: (raw.createdAt as string) ?? new Date().toISOString(),
    updatedAt: (raw.updatedAt as string) ?? new Date().toISOString(),
  };
}

function mapRole(role: string): UserRole {
  switch (role) {
    case "SUPER_ADMIN": return UserRole.SUPER_ADMIN;
    case "ADMIN":        return UserRole.ADMIN;
    case "COMPANY_USER": return UserRole.COMPANY;
    case "RESEARCH_MEMBER": return UserRole.MEMBER;
    case "AUDITOR":      return UserRole.ADMIN;
    default:             return UserRole.MEMBER;
  }
}

// ─────────────────────── Company DTO Mapper ───────────────────────
// Maps backend CompanyDetailDto (nested structure) to frontend Company interface

function mapCompanyDetail(raw: Record<string, unknown>): Company {
  const c = (raw.company ?? raw) as Record<string, unknown>;
  const profileRaw = raw.profile as Record<string, unknown> | null | undefined;

  return {
    id: c.id as string,
    userId: (c.createdBy as string) ?? "",
    name: c.name as string,
    legalName: c.name as string,
    cin: (c.cin as string) ?? null,
    gstin: (c.gstin as string) ?? null,
    sector: c.sector as string,
    subSector: null,
    state: c.state as string,
    city: c.city as string,
    address: "",
    pincode: "",
    companyType: c.companyType as string,
    incorporationYear: (c.incorporationYear as number) ?? null,
    description: (c.description as string) ?? null,
    status: (c.status as CompanyStatus) ?? CompanyStatus.PENDING,
    verified: c.status === "APPROVED",
    featured: false,
    profile: {
      id: "",
      companyId: c.id as string,
      foundedYear: null,
      employeeCount: (c.employeeCount as number) ?? null,
      website: (c.website as string) ?? null,
      description: (profileRaw?.about as string) ?? (c.description as string) ?? null,
      logoUrl: (c.logoUrl as string) ?? null,
      coverImageUrl: null,
      linkedinUrl: (c.linkedinProfile as string) ?? (c.linkedinUrl as string) ?? null,
      twitterUrl: (c.twitterUrl as string) ?? null,
      phoneNumber: (c.phoneNumber as string) ?? null,
      headquarter: (c.headquarter as string) ?? null,
      businessModel: (c.businessModel as string) ?? null,
      companyStage: (c.companyStage as string) ?? null,
      mission: (c.mission as string) ?? null,
      vision: (c.vision as string) ?? null,
      cultureSummary: (c.cultureSummary as string) ?? null,
      ceoName: (c.ceoName as string) ?? null,
      ctoName: (c.ctoName as string) ?? null,
      founders: (c.founders as string) ?? null,
      totalFunding: (c.totalFunding as string) ?? null,
      investors: (c.investors as string) ?? null,
      technologiesUsed: (c.technologiesUsed as string) ?? null,
      awards: (c.awards as string) ?? null,
      certificationsOverview: (c.certifications as string) ?? null,
      products: (c.productsServices as string) ?? (c.products as string) ?? null,
      services: (c.services as string) ?? null,
      createdAt: (c.createdAt as string) ?? "",
      updatedAt: (c.updatedAt as string) ?? "",
    },
    financials: ((raw.financials ?? []) as Array<Record<string, unknown>>).map(mapFinancial),
    certificates: ((raw.certificates ?? []) as Array<Record<string, unknown>>).map(mapCertificate),
    videos: ((raw.videos ?? []) as Array<Record<string, unknown>>).map(mapVideo),
    createdAt: (c.createdAt as string) ?? "",
    updatedAt: (c.updatedAt as string) ?? "",
  };
}

function mapFinancial(f: Record<string, unknown>): FinancialStatement {
  const netProfit = toNumber(f.netProfit) || toNumber(f.profit);
  return {
    id: (f.id as string) ?? "",
    companyId: (f.companyId as string) ?? "",
    financialYear: (f.financialYear as string) ?? "",
    balanceSheetUrl: (f.balanceSheetUrl as string) ?? null,
    profitLossUrl: (f.profitLossUrl as string) ?? null,
    cashFlowUrl: (f.cashFlowUrl as string) ?? null,
    auditorReportUrl: (f.auditorReportUrl as string) ?? null,
    taxFilingUrl: (f.taxFilingUrl as string) ?? null,
    uploadDate: (f.uploadDate as string) ?? null,
    uploadedBy: (f.uploadedBy as string) ?? null,
    status: (f.status as string) ?? null,
    version: (f.version as string) ?? null,
    fileSize: (f.fileSize as string) ?? null,
    fileType: (f.fileType as string) ?? null,
    downloadUrl: (f.downloadUrl as string) ?? null,
    revenue: toNumber(f.revenue),
    expenses: toNumber(f.expenses),
    ebitda: toNumber(f.ebitda),
    netProfit: netProfit,
    assets: toNumber(f.assets),
    liabilities: toNumber(f.liabilities),
    equity: toNumber(f.equity),
    operatingCashFlow: toNumber(f.operatingCashFlow),
    capex: toNumber(f.capex),
    debt: toNumber(f.debt),
    // Legacy fields
    year: parseFinancialYear(f.financialYear as string),
    profit: netProfit,
    documentUrl: (f.fileUrl as string) ?? null,
    isVerified: (f.verified as boolean) ?? false,
    createdAt: (f.uploadedAt as string) ?? "",
  };
}

function mapCertificate(c: Record<string, unknown>): Certificate {
  return {
    id: (c.id as string) ?? "",
    companyId: (c.companyId as string) ?? "",
    certificateName: (c.certificateName as string) ?? null,
    certificateNumber: (c.certificateNumber as string) ?? null,
    issuingAuthority: (c.issuingAuthority as string) ?? "Chartered Accountant",
    issueDate: (c.issueDate as string) ?? "",
    expiryDate: (c.expiryDate as string) ?? null,
    status: (c.status as string) ?? null,
    verificationUrl: (c.verificationUrl as string) ?? null,
    pdfUrl: (c.pdfUrl as string) ?? null,
    thumbnailUrl: (c.thumbnailUrl as string) ?? null,
    description: (c.description as string) ?? null,
    // Legacy fields
    name: (c.certificateName as string) ?? "CA Certificate",
    documentUrl: (c.certificateUrl as string) ?? (c.pdfUrl as string) ?? "",
    isVerified: (c.verified as boolean) ?? false,
    createdAt: (c.uploadedAt as string) ?? "",
  };
}

function mapVideo(v: Record<string, unknown>): Video {
  return {
    id: (v.id as string) ?? "",
    companyId: (v.companyId as string) ?? "",
    title: (v.title as string) ?? "Company Video",
    description: (v.description as string) ?? null,
    duration: (v.duration as string) ?? null,
    videoUrl: (v.videoUrl as string) ?? "",
    thumbnailUrl: (v.thumbnailUrl as string) ?? null,
    uploadDate: (v.uploadDate as string) ?? null,
    category: (v.category as string) ?? null,
    views: (v.views as number) ?? null,
    likes: (v.likes as number) ?? null,
    comments: (v.comments as number) ?? null,
    shares: (v.shares as number) ?? null,
    language: (v.language as string) ?? null,
    resolution: (v.resolution as string) ?? null,
    status: (v.status as string) ?? null,
    transcriptSummary: (v.transcriptSummary as string) ?? null,
    speaker: (v.speaker as string) ?? null,
    // Legacy fields
    url: (v.videoUrl as string) ?? "",
    durationSeconds: (v.durationSeconds as number) ?? null,
    createdAt: (v.uploadedAt as string) ?? "",
  };
}

function parseFinancialYear(fy: string): number {
  const m = /^(\d{4})/.exec(fy ?? "");
  return m ? parseInt(m[1], 10) : new Date().getFullYear();
}

function toNumber(v: unknown): number {
  if (typeof v === "number") return v;
  if (typeof v === "string") return parseFloat(v);
  return 0;
}

// ─────────────────────── Auth API ───────────────────────

export const authApi = {
  login: async (payload: LoginRequest): Promise<ApiResponse<AuthResponse>> => {
    const { data: raw } = await api.post("/auth/login", payload);
    const data: AuthResponse = {
      user: mapUserDto(raw.user),
      accessToken: raw.accessToken,
      refreshToken: raw.refreshToken,
      expiresIn: raw.expiresIn,
    };
    return { success: true, data, message: "OK" };
  },

  register: async (
    payload: RegisterRequest
  ): Promise<ApiResponse<AuthResponse>> => {
    const { data: raw } = await api.post("/auth/register", payload);
    const data: AuthResponse = {
      user: mapUserDto(raw.user),
      accessToken: raw.accessToken,
      refreshToken: raw.refreshToken,
      expiresIn: raw.expiresIn,
    };
    return { success: true, data, message: "OK" };
  },

  refreshToken: async (
    refreshToken: string
  ): Promise<ApiResponse<AuthResponse>> => {
    const { data: raw } = await api.post("/auth/refresh", { refreshToken });
    const data: AuthResponse = {
      user: mapUserDto(raw.user),
      accessToken: raw.accessToken,
      refreshToken: raw.refreshToken,
      expiresIn: raw.expiresIn,
    };
    return { success: true, data, message: "OK" };
  },

  logout: async (): Promise<ApiResponse<null>> => {
    const { data } = await api.post("/auth/logout");
    return { success: true, data, message: "OK" };
  },

  registerCompany: async (
    payload: CompanyRegistrationRequest
  ): Promise<ApiResponse<AuthResponse>> => {
    const { data: raw } = await api.post("/auth/register/company", payload);
    const data: AuthResponse = {
      user: mapUserDto(raw.user),
      accessToken: raw.accessToken,
      refreshToken: raw.refreshToken,
      expiresIn: raw.expiresIn,
    };
    return { success: true, data, message: "OK" };
  },

  registerResearchMember: async (
    payload: ResearchMemberRegistrationRequest
  ): Promise<ApiResponse<AuthResponse>> => {
    const { data: raw } = await api.post("/auth/register/research", payload);
    const data: AuthResponse = {
      user: mapUserDto(raw.user),
      accessToken: raw.accessToken,
      refreshToken: raw.refreshToken,
      expiresIn: raw.expiresIn,
    };
    return { success: true, data, message: "OK" };
  },
};

// ─────────────────────── User API ───────────────────────

export const userApi = {
  getProfile: async (): Promise<ApiResponse<User>> => {
    const { data } = await api.get("/user/profile");
    return data;
  },

  updateProfile: async (
    payload: Partial<Pick<User, "fullName" | "phone" | "avatarUrl">>
  ): Promise<ApiResponse<User>> => {
    const { data } = await api.put("/user/profile", payload);
    return data;
  },

  updatePan: async (panNumber: string): Promise<ApiResponse<User>> => {
    const { data } = await api.put("/user/pan", { panNumber });
    return data;
  },
};

// ─────────────────────── Companies API ───────────────────────

/**
 * Check if a CompanyResponse is a FreeCompanyResponse (masked).
 */
export function isFreeResponse(
  resp: CompanyResponse
): resp is FreeCompanyResponse {
  return resp.locked === true;
}

/**
 * Check if a CompanyResponse is a PremiumCompanyResponse (full access).
 */
export function isPremiumResponse(
  resp: CompanyResponse
): resp is PremiumCompanyResponse {
  return resp.locked === false;
}

export const companiesApi = {
  search: async (
    params: SearchFilters
  ): Promise<PaginatedResponse<CompanyResponse>> => {
    const { data } = await api.get("/companies", {
      params: {
        query: params.search,
        sector: params.sector,
        state: params.state,
        companyType: params.companyType,
        revenueRange: params.revenueRange,
        membershipFilter: params.membershipFilter,
        page: params.page ? params.page - 1 : 0,
        size: params.limit ?? 12,
      },
    });
    return {
      success: true,
      data: data.content ?? [],
      message: "OK",
      pagination: {
        page: data.page + 1,
        limit: data.size,
        total: data.totalElements,
        totalPages: data.totalPages,
      },
    };
  },

  getById: async (id: string): Promise<ApiResponse<CompanyResponse>> => {
    // Detect DoB ID (DOB-XXXXXXXX) vs UUID
    const endpoint = /^DOB-/i.test(id)
      ? `/companies/public/${id}`
      : `/companies/${id}`;
    const { data: raw } = await api.get(endpoint);
    return { success: true, data: raw as CompanyResponse, message: "OK" };
  },

  create: async (
    payload: FormData | Record<string, unknown>
  ): Promise<ApiResponse<Company>> => {
    const isFormData = payload instanceof FormData;
    const { data } = await api.post("/companies", payload, {
      headers: isFormData ? { "Content-Type": "multipart/form-data" } : {},
    });
    return data;
  },

  update: async (
    id: string,
    payload: FormData | Record<string, unknown>
  ): Promise<ApiResponse<Company>> => {
    const isFormData = payload instanceof FormData;
    const { data } = await api.put(`/companies/${id}`, payload, {
      headers: isFormData ? { "Content-Type": "multipart/form-data" } : {},
    });
    return data;
  },

  uploadFinancials: async (
    companyId: string,
    payload: FormData
  ): Promise<ApiResponse<Company>> => {
    const { data } = await api.post(
      `/companies/${companyId}/financials`,
      payload,
      {
        headers: { "Content-Type": "multipart/form-data" },
      }
    );
    return data;
  },

  uploadCertificate: async (
    companyId: string,
    payload: FormData
  ): Promise<ApiResponse<Company>> => {
    const { data } = await api.post(
      `/companies/${companyId}/certificates`,
      payload,
      {
        headers: { "Content-Type": "multipart/form-data" },
      }
    );
    return data;
  },

  uploadVideo: async (
    companyId: string,
    payload: FormData
  ): Promise<ApiResponse<Company>> => {
    const { data } = await api.post(
      `/companies/${companyId}/videos`,
      payload,
      {
        headers: { "Content-Type": "multipart/form-data" },
      }
    );
    return data;
  },

  /**
   * Download a company report as a PDF.
   * Uses fetch directly (not axios) to handle blob responses without
   * interference from the JSON response interceptor.
   */
  downloadReport: async (companyId: string): Promise<void> => {
    const token = await getAccessToken();
    if (!token) {
      throw new Error("Authentication required. Please sign in.");
    }

    const response = await fetch(`${API_BASE_URL}/companies/${companyId}/report`, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (!response.ok) {
      let errorMessage = "Failed to download report";
      try {
        const errorBody = await response.text();
        const parsed = JSON.parse(errorBody);
        errorMessage = parsed.detail || parsed.message || errorMessage;
      } catch {
        // Use default error message
      }
      throw new Error(errorMessage);
    }

    const blob = await response.blob();
    const blobUrl = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = blobUrl;
    a.download = `company-report-${companyId}.pdf`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);

    // Clean up the blob URL after a short delay
    setTimeout(() => URL.revokeObjectURL(blobUrl), 1000);
  },

  // ──────── Company Workflow Endpoints ────────

  /**
   * Get the current user's companies with full workflow details.
   */
  getMyCompanyDetails: async (): Promise<CompanyDetailDto[]> => {
    const { data } = await api.get("/companies/my/details");
    return data;
  },

  /**
   * Get company detail for owner view — shows all statuses.
   */
  getOwnerView: async (id: string): Promise<CompanyDetailDto> => {
    const { data } = await api.get(`/companies/${id}/owner-view`);
    return data;
  },

  /**
   * Submit a DRAFT company for admin review.
   */
  submitForReview: async (companyId: string): Promise<CompanyDto> => {
    const { data } = await api.post(`/companies/${companyId}/submit`);
    return data;
  },

  /**
   * Resubmit a REJECTED company for admin review after making corrections.
   */
  resubmitForReview: async (companyId: string): Promise<CompanyDto> => {
    const { data } = await api.post(`/companies/${companyId}/resubmit`);
    return data;
  },

  /**
   * Activate listing membership (after ₹500+GST payment).
   */
  activateListingMembership: async (companyId: string): Promise<CompanyDto> => {
    const { data } = await api.post(`/companies/${companyId}/activate-listing`);
    return data;
  },
};

// ─────────────────────── Memberships API ───────────────────────

export const membershipsApi = {
  getMyMembership: async (): Promise<ApiResponse<Membership>> => {
    const { data } = await api.get("/memberships/me");
    return data;
  },

  getPlans: async (): Promise<ApiResponse<MembershipPlan[]>> => {
    const { data } = await api.get("/memberships/plans");
    return data;
  },

  subscribe: async (
    planId: string,
    autoRenew?: boolean
  ): Promise<ApiResponse<Membership>> => {
    const { data } = await api.post("/memberships/subscribe", {
      planId,
      autoRenew: autoRenew ?? false,
    });
    return data;
  },

  /** Get current membership from /api/users/me/membership */
  getCurrent: async (): Promise<UserMembershipResponse> => {
    const { data } = await api.get("/users/me/membership");
    return data;
  },
};

// ─────────────────────── Payments API ───────────────────────

export const paymentsApi = {
  createOrder: async (
    amount: number,
    membershipId?: string
  ): Promise<ApiResponse<{ orderId: string; amount: number; currency: string }>> => {
    const { data } = await api.post("/payments/order", {
      amount,
      membershipId,
    });
    return data;
  },

  verify: async (payload: {
    razorpayOrderId: string;
    razorpayPaymentId: string;
    razorpaySignature: string;
  }): Promise<ApiResponse<Payment>> => {
    const { data } = await api.post("/payments/verify", payload);
    return data;
  },

  getHistory: async (
    page = 1,
    limit = 10
  ): Promise<PaginatedResponse<Payment>> => {
    const { data } = await api.get("/payments/history", {
      params: { page, limit },
    });
    return data;
  },

  /** Simulated payment success for MVP flow */
  simulateSuccess: async (
    subscriptionId: string
  ): Promise<PaymentSuccessResponse> => {
    const { data } = await api.post("/payments/success", { subscriptionId });
    return data;
  },
};

// ─────────────────────── Subscriptions API (Simplified Flow) ───────────────────────

export const subscriptionsApi = {
  create: async (plan: string): Promise<CreateSubscriptionResponse> => {
    const { data } = await api.post("/subscriptions/create", { plan });
    return data;
  },
};

// ─────────────────────── Downloads API ───────────────────────

export const downloadsApi = {
  downloadDocument: async (
    companyId: string,
    documentId: string
  ): Promise<ApiResponse<{ url: string }>> => {
    const { data } = await api.get(
      `/downloads/${companyId}/documents/${documentId}`
    );
    return data;
  },

  getHistory: async (
    page = 1,
    limit = 10
  ): Promise<PaginatedResponse<DownloadDocument>> => {
    const { data } = await api.get("/downloads/history", {
      params: { page, limit },
    });
    return data;
  },
};

// ─────────────────────── Refunds API ───────────────────────

export const refundsApi = {
  requestRefund: async (payload: {
    paymentId: string;
    amount: number;
    reason: string;
  }): Promise<ApiResponse<Refund>> => {
    const { data } = await api.post("/refunds", payload);
    return data;
  },

  getStatus: async (refundId: string): Promise<ApiResponse<Refund>> => {
    const { data } = await api.get(`/refunds/${refundId}`);
    return data;
  },
};

// ─────────────────────── Grievances API ───────────────────────

export const grievancesApi = {
  create: async (payload: {
    subject: string;
    description: string;
    companyId?: string;
  }): Promise<ApiResponse<Grievance>> => {
    const { data } = await api.post("/grievances", payload);
    return data;
  },

  list: async (
    page = 1,
    limit = 10
  ): Promise<PaginatedResponse<Grievance>> => {
    const { data } = await api.get("/grievances", {
      params: { page, limit },
    });
    return data;
  },

  getById: async (id: string): Promise<ApiResponse<Grievance>> => {
    const { data } = await api.get(`/grievances/${id}`);
    return data;
  },
};

// ─────────────────────── Admin API ───────────────────────

export const adminApi = {
  getMembers: async (
    page = 0,
    size = 20
  ): Promise<{ data: any[] }> => {
    const { data } = await api.get("/admin/members", {
      params: { page, size },
    });
    return { data };
  },

  getMemberCount: async (): Promise<{ count: number }> => {
    const { data } = await api.get("/admin/members/count");
    return data;
  },

  getMember: async (id: string): Promise<any> => {
    const { data } = await api.get(`/admin/members/${id}`);
    return data;
  },

  updateMember: async (
    id: string,
    payload: Record<string, unknown>
  ): Promise<any> => {
    const { data } = await api.put(`/admin/members/${id}`, payload);
    return data;
  },

  updateMemberMembership: async (
    id: string,
    payload: Record<string, unknown>
  ): Promise<any> => {
    const { data } = await api.put(`/admin/members/${id}/membership`, payload);
    return data;
  },

  getPendingCompanies: async (
    page = 0,
    size = 20
  ): Promise<CompanyDto[]> => {
    const { data } = await api.get("/admin/companies/pending", {
      params: { page, size },
    });
    return data;
  },

  getPendingCompaniesWithDetails: async (): Promise<CompanyDetailDto[]> => {
    const { data } = await api.get("/admin/companies/pending-with-details");
    return data;
  },

  approveCompany: async (id: string): Promise<CompanyDto> => {
    const { data } = await api.post(`/admin/companies/${id}/approve`);
    return data;
  },

  rejectCompany: async (
    id: string,
    comment?: string
  ): Promise<CompanyDto> => {
    const { data } = await api.post(`/admin/companies/${id}/reject`, {
      comment,
    });
    return data;
  },

  suspendCompany: async (id: string): Promise<CompanyDto> => {
    const { data } = await api.post(`/admin/companies/${id}/suspend`);
    return data;
  },

  reactivateCompany: async (id: string): Promise<CompanyDto> => {
    const { data } = await api.post(`/admin/companies/${id}/reactivate`);
    return data;
  },

  getRefunds: async (
    page = 1,
    limit = 10
  ): Promise<PaginatedResponse<Refund>> => {
    const { data } = await api.get("/admin/refunds", {
      params: { page, limit },
    });
    return data;
  },

  processRefund: async (
    refundId: string,
    action: "APPROVED" | "REJECTED",
    note?: string
  ): Promise<ApiResponse<Refund>> => {
    const { data } = await api.post(`/admin/refunds/${refundId}/process`, {
      action,
      note,
    });
    return data;
  },

  getGrievances: async (
    page = 1,
    limit = 10
  ): Promise<PaginatedResponse<Grievance>> => {
    const { data } = await api.get("/admin/grievances", {
      params: { page, limit },
    });
    return data;
  },

  assignGrievance: async (
    grievanceId: string,
    adminId: string
  ): Promise<ApiResponse<Grievance>> => {
    const { data } = await api.post(
      `/admin/grievances/${grievanceId}/assign`,
      { adminId }
    );
    return data;
  },

  resolveGrievance: async (
    grievanceId: string,
    resolution: string
  ): Promise<ApiResponse<Grievance>> => {
    const { data } = await api.post(
      `/admin/grievances/${grievanceId}/resolve`,
      { resolution }
    );
    return data;
  },

  getAuditLogs: async (
    page = 1,
    limit = 20
  ): Promise<PaginatedResponse<AuditLog>> => {
    const { data } = await api.get("/admin/audit-logs", {
      params: { page, limit },
    });
    return data;
  },

  // ── Company Members ──

  getCompanyMembers: async (
    page = 0,
    size = 20
  ): Promise<{ data: any[] }> => {
    const { data } = await api.get("/admin/company-members", {
      params: { page, size },
    });
    return { data };
  },

  getCompanyMemberCount: async (): Promise<{ count: number }> => {
    const { data } = await api.get("/admin/company-members/count");
    return data;
  },

  getCompanyMember: async (id: string): Promise<any> => {
    const { data } = await api.get(`/admin/company-members/${id}`);
    return data;
  },

  updateCompanyMember: async (
    id: string,
    payload: Record<string, unknown>
  ): Promise<any> => {
    const { data } = await api.put(`/admin/company-members/${id}`, payload);
    return data;
  },

  updateCompanyListing: async (
    userId: string,
    companyId: string,
    payload: Record<string, unknown>
  ): Promise<any> => {
    const { data } = await api.put(
      `/admin/company-members/${userId}/companies/${companyId}`,
      payload
    );
    return data;
  },

  suspendCompanyListing: async (
    userId: string,
    companyId: string
  ): Promise<any> => {
    const { data } = await api.post(
      `/admin/company-members/${userId}/companies/${companyId}/suspend`
    );
    return data;
  },
};

// ─────────────────────── Notifications API ───────────────────────

export const notificationsApi = {
  list: async (
    page = 1,
    limit = 20
  ): Promise<PaginatedResponse<Notification>> => {
    const { data } = await api.get("/notifications", {
      params: { page, limit },
    });
    return data;
  },

  markRead: async (id: string): Promise<ApiResponse<Notification>> => {
    const { data } = await api.put(`/notifications/${id}/read`);
    return data;
  },
};

export default api;
