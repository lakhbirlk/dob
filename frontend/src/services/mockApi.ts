/**
 * Mock API — intercepts axios requests and returns mock data for development.
 * Remove this file and the interceptor in api.ts when connecting to a real backend.
 */

import type {
  AuthResponse,
  Company,
  PaginatedResponse,
  ApiResponse,
  User,
} from "@/types";
import { UserRole, CompanyStatus, PaymentStatus } from "@/types";

// ─────────────────────── Helpers ───────────────────────

function ok<T>(data: T, message = "Success"): ApiResponse<T> {
  return { success: true, data, message };
}

function paginated<T>(data: T[], page = 1, limit = 12, total = 0): PaginatedResponse<T> {
  return {
    success: true,
    data,
    message: "OK",
    pagination: {
      page,
      limit,
      total: total || data.length,
      totalPages: Math.ceil((total || data.length) / limit),
    },
  };
}

// ─────────────────────── Mock Users ───────────────────────

const MOCK_USERS: Record<string, { user: User; password: string }> = {
  "member@test.com": {
    password: "password123",
    user: {
      id: "u1",
      email: "member@test.com",
      fullName: "Rahul Sharma",
      phone: "9876543210",
      role: UserRole.MEMBER,
      panNumber: null,
      isEmailVerified: true,
      isPhoneVerified: false,
      avatarUrl: null,
      createdAt: "2026-01-15T10:30:00Z",
      updatedAt: "2026-06-01T08:00:00Z",
    },
  },
  "company@test.com": {
    password: "password123",
    user: {
      id: "u2",
      email: "company@test.com",
      fullName: "Priya Patel",
      phone: "9876543211",
      role: UserRole.COMPANY,
      panNumber: "ABCDE1234F",
      isEmailVerified: true,
      isPhoneVerified: true,
      avatarUrl: null,
      createdAt: "2026-02-20T14:00:00Z",
      updatedAt: "2026-05-10T12:00:00Z",
    },
  },
  "admin@test.com": {
    password: "admin123",
    user: {
      id: "u3",
      email: "admin@test.com",
      fullName: "Admin User",
      phone: "9876543212",
      role: UserRole.ADMIN,
      panNumber: null,
      isEmailVerified: true,
      isPhoneVerified: true,
      avatarUrl: null,
      createdAt: "2025-01-01T00:00:00Z",
      updatedAt: "2026-06-01T00:00:00Z",
    },
  },
};

// ─────────────────────── Mock Companies ───────────────────────

const MOCK_COMPANIES: Company[] = [
  {
    id: "c1",
    userId: "u2",
    name: "TechVentures India",
    legalName: "TechVentures India Private Limited",
    cin: "U72900MH2020PTC123456",
    gstin: "27AABCT1234C1Z5",
    sector: "Technology",
    subSector: "SaaS",
    state: "Maharashtra",
    city: "Mumbai",
    address: "Bandra Kurla Complex, Mumbai 400051",
    pincode: "400051",
    companyType: "Private Limited",
    incorporationYear: 2020,
    description: "TechVentures India is a leading SaaS company providing enterprise solutions for supply chain management. Trusted by 500+ enterprises across India.",
    status: CompanyStatus.APPROVED_ACTIVE,
    verified: true,
    featured: true,
    profile: {
      id: "p1",
      companyId: "c1",
      foundedYear: 2020,
      employeeCount: 250,
      website: "https://techventures.example.com",
      description: "TechVentures India is a leading SaaS company providing enterprise solutions for supply chain management. Trusted by 500+ enterprises across India.",
      logoUrl: null,
      coverImageUrl: null,
      linkedinUrl: null,
      twitterUrl: null,
      createdAt: "2026-01-01T00:00:00Z",
      updatedAt: "2026-01-01T00:00:00Z",
    },
    financials: [
      {
        id: "f1",
        companyId: "c1",
        year: 2025,
        revenue: 4500000000,
        profit: 820000000,
        assets: 12000000000,
        liabilities: 4500000000,
        documentUrl: null,
        isVerified: true,
        createdAt: "2026-01-01T00:00:00Z",
      },
      {
        id: "f2",
        companyId: "c1",
        year: 2024,
        revenue: 3200000000,
        profit: 550000000,
        assets: 9500000000,
        liabilities: 3800000000,
        documentUrl: null,
        isVerified: true,
        createdAt: "2025-06-01T00:00:00Z",
      },
    ],
    certificates: [
      {
        id: "cert1",
        companyId: "c1",
        name: "Ajay Mehta, CA",
        issuingAuthority: "ICAI",
        issueDate: "2025-03-15",
        expiryDate: "2026-03-15",
        documentUrl: "",
        isVerified: true,
        createdAt: "2025-03-15T00:00:00Z",
      },
    ],
    videos: [
      {
        id: "v1",
        companyId: "c1",
        title: "Company Overview 2026",
        description: "CEO presenting the company vision and growth plans",
        url: "",
        thumbnailUrl: null,
        duration: 180,
        createdAt: "2026-02-01T00:00:00Z",
      },
    ],
    createdAt: "2026-01-01T00:00:00Z",
    updatedAt: "2026-06-01T00:00:00Z",
  },
  // Generate more companies programmatically
  ...Array.from({ length: 15 }, (_, i) => {
    const sector = ["Technology", "Manufacturing", "Finance", "Healthcare", "Retail", "Energy", "Real Estate"][i % 7];
    const state = ["Maharashtra", "Karnataka", "Delhi", "Tamil Nadu", "Gujarat", "Telangana", "Haryana"][i % 7];
    const city = ["Mumbai", "Bengaluru", "Delhi", "Chennai", "Ahmedabad", "Hyderabad", "Gurugram"][i % 7];
    const name = [
      "GreenEnergy Solutions", "Bharat Biotech Labs", "Indus Finance Corp",
      "Skyline Realty Group", "Ocean Logistics Ltd", "Digital Payments India",
      "AgriFood Exports Co", "MediCare Hospitals", "SteelCraft Industries",
      "Urban Infra Projects", "CloudNine Technologies", "EduPrime Learning",
      "SafeGuard Insurance", "FreshMart Retail Chain", "AutoParts Manufacturing",
    ][i];
    const foundedYear = [2018, 2015, 2010, 2022, 2019, 2016, 2021][i % 7];
    return {
      id: `c${i + 2}`,
      userId: `u${(i % 3) + 2}`,
      name,
      legalName: `Company ${i + 2} Pvt Ltd`,
      cin: `U72900MH2020PTC${100000 + i}`,
      gstin: null,
      sector,
      subSector: null,
      state,
      city,
      address: "India",
      pincode: "400001",
      companyType: ["Private Limited", "Public Limited", "LLP", "Partnership", "Proprietorship"][i % 5],
      incorporationYear: foundedYear,
      description: `${name} is a leading company in the ${sector.toLowerCase()} sector providing high-quality products and services across India.`,
      status: [CompanyStatus.APPROVED_ACTIVE, CompanyStatus.PENDING_REVIEW, CompanyStatus.APPROVED_ACTIVE, CompanyStatus.APPROVED_MEMBERSHIP_PENDING, CompanyStatus.DRAFT][i % 5],
      verified: i % 3 !== 0,
      featured: i < 5,
      profile: {
        id: `p${i + 2}`,
        companyId: `c${i + 2}`,
        foundedYear,
        employeeCount: [50, 200, 500, 1000, 75, 300, 150][i % 7],
        website: "https://example.com",
        description: `${name} is a leading company in the ${sector.toLowerCase()} sector providing high-quality products and services across India.`,
        logoUrl: null,
        coverImageUrl: null,
        linkedinUrl: null,
        twitterUrl: null,
        createdAt: "2026-01-01T00:00:00Z",
        updatedAt: "2026-01-01T00:00:00Z",
      },
      financials: [],
      certificates: [],
      videos: [],
      createdAt: "2026-01-01T00:00:00Z",
      updatedAt: "2026-06-01T00:00:00Z",
    };
  }),
];

// ─────────────────────── Mock Grievances ───────────────────────

const MOCK_GRIEVANCES = [
  {
    id: "g1",
    userId: "u1",
    companyId: "c1",
    subject: "Incorrect financial data",
    description: "The revenue figures listed for TechVentures India show ₹450 Cr for FY25 but our records indicate ₹420 Cr. Please review and correct.",
    status: "OPEN" as const,
    priority: "HIGH" as const,
    assignedTo: null,
    resolution: null,
    createdAt: "2026-06-20T10:30:00Z",
    updatedAt: "2026-06-20T10:30:00Z",
  },
  {
    id: "g2",
    userId: "u2",
    companyId: null,
    subject: "Payment not reflected",
    description: "I made a payment of ₹2,500 on 18 June via Razorpay but my membership is still showing as inactive. Transaction ID: rzp_test_12345.",
    status: "IN_PROGRESS" as const,
    priority: "CRITICAL" as const,
    assignedTo: "u3",
    resolution: null,
    createdAt: "2026-06-18T14:15:00Z",
    updatedAt: "2026-06-22T09:00:00Z",
  },
  {
    id: "g3",
    userId: "u1",
    companyId: "c2",
    subject: "Outdated certificate",
    description: "The CA certificate for GreenEnergy Solutions expires next month. Requesting renewal instructions.",
    status: "OPEN" as const,
    priority: "LOW" as const,
    assignedTo: null,
    resolution: null,
    createdAt: "2026-06-22T16:45:00Z",
    updatedAt: "2026-06-22T16:45:00Z",
  },
  {
    id: "g4",
    userId: "u2",
    companyId: null,
    subject: "Download limit not resetting",
    description: "My download count shows 48/50 but it's the 26th of the month. The limit should have reset by now.",
    status: "RESOLVED" as const,
    priority: "MEDIUM" as const,
    assignedTo: "u3",
    resolution: "The download limit resets on the 1st of each month. Your plan started mid-cycle, so the first month is prorated. Normal monthly resets from next month.",
    createdAt: "2026-06-15T09:00:00Z",
    updatedAt: "2026-06-19T11:30:00Z",
  },
];

// ─────────────────────── Filter Helper ───────────────────────

function filterCompanies(companies: Company[], params: Record<string, string>): Company[] {
  let filtered = [...companies];

  if (params.search) {
    const q = params.search.toLowerCase();
    filtered = filtered.filter(
      (c) =>
        c.name.toLowerCase().includes(q) ||
        c.sector.toLowerCase().includes(q) ||
        c.city.toLowerCase().includes(q) ||
        c.state.toLowerCase().includes(q) ||
        (c.description?.toLowerCase().includes(q) ?? false)
    );
  }

  if (params.sector) {
    filtered = filtered.filter((c) => c.sector === params.sector);
  }

  if (params.state) {
    filtered = filtered.filter((c) => c.state === params.state);
  }

  if (params.companyType) {
    filtered = filtered.filter((c) => c.companyType === params.companyType);
  }

  // Sort
  if (params.sortBy) {
    const order = params.sortOrder === "asc" ? 1 : -1;
    filtered.sort((a: any, b: any) => {
      const aVal = a[params.sortBy] ?? "";
      const bVal = b[params.sortBy] ?? "";
      if (aVal < bVal) return -1 * order;
      if (aVal > bVal) return 1 * order;
      return 0;
    });
  }

  return filtered;
}

// ─────────────────────── Handler ───────────────────────

function generateTokens(userId: string) {
  return {
    accessToken: `mock_access_${userId}_${Date.now()}`,
    refreshToken: `mock_refresh_${userId}_${Date.now()}`,
    expiresIn: 3600,
  };
}

export function handleMockRequest(
  method: string,
  url: string,
  data: unknown,
  params?: Record<string, any>
): [number, unknown] | null {
  const fullPath = url.replace("https://api.dataofbusiness.com/api/v1", "");
  // Strip query string for matching
  const path = fullPath.split("?")[0];

  // ── Auth ──
  if (method === "post" && path === "/auth/register") {
    const body = data as { email: string; password: string; fullName: string };
    const tokens = generateTokens(body.email);
    const newUser: User = {
      id: `u_${Date.now()}`,
      email: body.email,
      fullName: body.fullName,
      phone: (data as any).phone || null,
      role: UserRole.MEMBER,
      panNumber: null,
      isEmailVerified: false,
      isPhoneVerified: false,
      avatarUrl: null,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    };
    const response: AuthResponse = { user: newUser, ...tokens };
    return [201, ok(response, "Registration successful")];
  }

  if (method === "post" && path === "/auth/login") {
    const body = data as { email: string; password: string };
    const mock = MOCK_USERS[body.email];
    if (!mock || mock.password !== body.password) {
      return [401, { success: false, data: null, message: "Invalid email or password" }];
    }
    const tokens = generateTokens(mock.user.id);
    return [200, ok({ user: mock.user, ...tokens }, "Login successful")];
  }

  if (method === "post" && path === "/auth/logout") {
    return [200, ok(null, "Logged out")];
  }

  if (method === "post" && path === "/auth/refresh") {
    const tokens = generateTokens("refresh");
    // Return a mock user with the new tokens
    return [200, ok({ user: MOCK_USERS["member@test.com"]?.user, ...tokens })];
  }

  // ── User ──
  if (method === "get" && path === "/user/profile") {
    return [200, ok(MOCK_USERS["member@test.com"]!.user)];
  }

  if (method === "put" && path === "/user/profile") {
    return [200, ok(MOCK_USERS["member@test.com"]!.user, "Profile updated")];
  }

  if (method === "put" && path === "/user/pan") {
    return [200, ok(MOCK_USERS["member@test.com"]!.user, "PAN updated")];
  }

  // ── Companies ──
  if (method === "get" && path === "/companies") {
    const queryParams: Record<string, string> = {};
    // Use directly-passed params (from axios config.params) if available, otherwise parse from URL
    if (params && Object.keys(params).length > 0) {
      for (const [key, value] of Object.entries(params)) {
        if (value !== undefined && value !== null) {
          queryParams[key] = String(value);
        }
      }
    } else {
      Object.assign(queryParams, Object.fromEntries(new URLSearchParams(fullPath.split("?")[1] || "")));
    }
    const page = parseInt(queryParams.page || "1", 10);
    const limit = parseInt(queryParams.limit || "12", 10);
    const filtered = filterCompanies(MOCK_COMPANIES, queryParams);
    return [200, paginated(filtered, page, limit, filtered.length)];
  }

  if (method === "get" && path.startsWith("/companies/")) {
    const id = path.split("/")[2];
    const company = MOCK_COMPANIES.find((c) => c.id === id);
    if (company) return [200, ok(company)];
    return [404, { success: false, data: null, message: "Company not found" }];
  }

  if (method === "post" && path === "/companies") {
    return [201, ok(MOCK_COMPANIES[0]!, "Company created")];
  }

  // ── Memberships ──
  if (method === "get" && path === "/memberships/plans") {
    return [200, ok([
      { id: "plan1", name: "Research Membership", description: "Monthly research access", price: 2500, durationMonths: 1, features: ["50 downloads", "Advanced search", "CA-certified data"], isActive: true },
    ])];
  }

  if (method === "get" && path === "/memberships/me") {
    return [200, ok({
      id: "m1", userId: "u1", planId: "plan1",
      plan: { id: "plan1", name: "Research Membership", description: "Monthly", price: 2500, durationMonths: 1, features: [], isActive: true },
      status: "ACTIVE", startDate: "2026-06-01", endDate: "2026-07-01", autoRenew: true, createdAt: "2026-06-01",
    })];
  }

  // ── Payments ──
  if (method === "post" && path === "/payments/order") {
    return [200, ok({ orderId: `order_${Date.now()}`, amount: 2500, currency: "INR" })];
  }

  if (method === "post" && path === "/payments/verify") {
    return [200, ok({
      id: `pay_${Date.now()}`, userId: "u1", membershipId: "m1", orderId: "order_123",
      amount: 2500, currency: "INR", status: PaymentStatus.VERIFIED,
      razorpayPaymentId: `rzp_${Date.now()}`, razorpaySignature: "mock_sig", createdAt: new Date().toISOString(),
    })];
  }

  // ── Grievances (user-facing) ──
  if (method === "get" && path === "/grievances") {
    return [200, paginated(MOCK_GRIEVANCES)];
  }

  if (method === "get" && path.startsWith("/grievances/")) {
    const id = path.split("/")[2];
    const grievance = MOCK_GRIEVANCES.find((g) => g.id === id);
    if (grievance) return [200, ok(grievance)];
    return [404, { success: false, data: null, message: "Grievance not found" }];
  }

  if (method === "post" && path === "/grievances") {
    return [201, ok(MOCK_GRIEVANCES[0], "Grievance submitted")];
  }

  // ── Admin: Grievances ──
  if (method === "get" && path === "/admin/grievances") {
    return [200, paginated(MOCK_GRIEVANCES)];
  }

  if (method === "post" && path.startsWith("/admin/grievances/") && path.endsWith("/assign")) {
    return [200, ok(MOCK_GRIEVANCES[0], "Grievance assigned")];
  }

  if (method === "post" && path.startsWith("/admin/grievances/") && path.endsWith("/resolve")) {
    return [200, ok(MOCK_GRIEVANCES[0], "Grievance resolved")];
  }

  // ── Notifications ──
  if (method === "get" && path === "/notifications") {
    return [200, paginated([])];
  }

  // ── Downloads ──
  if (method === "get" && path.startsWith("/downloads")) {
    return [200, paginated([])];
  }

  // ── Admin ──
  if (path.startsWith("/admin/")) {
    return [200, paginated([])];
  }

  // ── Refunds ──
  if (path.startsWith("/refunds")) {
    return [200, paginated([])];
  }

  return null; // not handled — let Axios try the real request
}
