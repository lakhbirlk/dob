package com.dob.infrastructure.config.seed;

import com.dob.infrastructure.persistence.entity.*;
import com.dob.infrastructure.persistence.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Seeds the database with realistic Indian business data for development & testing.
 *
 * <p>Execution order (dependency-safe):
 * <ol>
 *   <li>Users + ResearchMember profiles
 *   <li>Companies + CompanyDocuments
 *   <li>Memberships
 *   <li>Payments
 *   <li>Grievances
 *   <li>AuditLogs
 *   <li>CreditTransactions
 *   <li>UnlockedCompanies
 * </ol>
 *
 * <p>Idempotent: skips seeding if any user already exists.
 */
@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    // ─── Repositories ─────────────────────────────────────────
    private final UserJpaRepository userRepo;
    private final ResearchMemberJpaRepository researchMemberRepo;
    private final CompanyJpaRepository companyRepo;
    private final CompanyDocumentJpaRepository documentRepo;
    private final MembershipJpaRepository membershipRepo;
    private final PaymentJpaRepository paymentRepo;
    private final GrievanceJpaRepository grievanceRepo;
    private final AuditLogJpaRepository auditLogRepo;
    private final CreditTransactionJpaRepository creditTxRepo;
    private final UnlockedCompanyJpaRepository unlockedRepo;

    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    // ─── RNG ──────────────────────────────────────────────────
    private static final ThreadLocalRandom RND = ThreadLocalRandom.current();

    // ─── Credentials ──────────────────────────────────────────
    private static final String DEFAULT_PASSWORD = "password123";

    // ─── Fixed UUIDs for well-known users (so login works across restarts) ──
    private static final UUID ADMIN_ID      = UUID.fromString("a0000000-0000-0000-0000-000000000001");
    private static final UUID MEMBER_1_ID   = UUID.fromString("a0000000-0000-0000-0000-000000000002");
    private static final UUID MEMBER_2_ID   = UUID.fromString("a0000000-0000-0000-0000-000000000003");
    private static final UUID COMPANY_1_ID  = UUID.fromString("a0000000-0000-0000-0000-000000000004");
    private static final UUID COMPANY_2_ID  = UUID.fromString("a0000000-0000-0000-0000-000000000006");
    private static final UUID COMPANY_3_ID  = UUID.fromString("a0000000-0000-0000-0000-000000000007");
    private static final UUID COMPANY_4_ID  = UUID.fromString("a0000000-0000-0000-0000-000000000008");
    private static final UUID COMPANY_5_ID  = UUID.fromString("a0000000-0000-0000-0000-000000000009");
    private static final UUID AUDITOR_ID    = UUID.fromString("a0000000-0000-0000-0000-000000000005");

    // ─── Fixed Company UUIDs ──────────────────────────────────
    private static UUID COMPANY_UUID(int idx) {
        return UUID.fromString(String.format("c0000000-0000-0000-0000-0000000%04d", idx));
    }

    // ========================================================================
    //  RUN
    // ========================================================================

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepo.count() > 0) {
            log.info("Database already contains data — skipping seed (found {} users)", userRepo.count());
            return;
        }
        log.info("╔══════════════════════════════════════════════════════════╗");
        log.info("║   Seeding database with development data...              ║");
        log.info("╚══════════════════════════════════════════════════════════╝");

        var hash = passwordEncoder.encode(DEFAULT_PASSWORD);

        // ── 1. Users & Research Members ──
        var admin        = seedAdmin(hash);
        var auditors     = seedAuditors(hash);
        var members      = seedResearchMembers(hash);
        var companyUsers = seedCompanyUsers(hash);
        log.info("✔ Users seeded ({} total) — password: {}", userRepo.count(), DEFAULT_PASSWORD);

        // ── 2. Companies & Documents ──
        var companies = seedCompanies(companyUsers, admin);
        log.info("✔ Companies seeded ({} total)", companyRepo.count());

        seedCompanyDocuments(companies);
        log.info("✔ Company documents seeded ({} total)", documentRepo.count());

        // ── 3. Memberships ──
        var memberships = seedMemberships(members);
        log.info("✔ Memberships seeded ({} total)", membershipRepo.count());

        // ── 4. Payments ──
        seedPayments(members, memberships, companies);
        log.info("✔ Payments seeded ({} total)", paymentRepo.count());

        // ── 5. Grievances ──
        seedGrievances(members, admin);
        log.info("✔ Grievances seeded ({} total)", grievanceRepo.count());

        // ── 6. Audit Logs ──
        seedAuditLogs(members, companies);
        log.info("✔ Audit logs seeded ({} total)", auditLogRepo.count());

        // ── 7. Credit Transactions ──
        var creditTxs = seedCreditTransactions(members, companies);
        log.info("✔ Credit transactions seeded ({} total)", creditTxRepo.count());

        // ── 8. Unlocked Companies ──
        seedUnlockedCompanies(members, companies, creditTxs);
        log.info("✔ Unlocked companies seeded ({} total)", unlockedRepo.count());

        // ── Done ──
        printLoginInfo(admin, auditors, members, companyUsers);
    }

    // ========================================================================
    //  1. USERS
    // ========================================================================

    private UserEntity seedAdmin(String hash) {
        var u = UserEntity.builder()
            .id(ADMIN_ID)
            .email("admin@dataofbusiness.in").passwordHash(hash).pan("ABCDE1234F")
            .fullName("Admin User").phone("9876543210")
            .role(UserEntity.UserRole.ADMIN).emailVerified(true).active(true)
            .createdAt(Instant.now()).updatedAt(Instant.now()).build();
        return userRepo.save(u);
    }

    private List<UserEntity> seedAuditors(String hash) {
        return List.of(userRepo.save(UserEntity.builder()
            .id(AUDITOR_ID)
            .email("auditor@dataofbusiness.in").passwordHash(hash)
            .fullName("Auditor User").phone("9876543214")
            .role(UserEntity.UserRole.AUDITOR).emailVerified(true).active(true)
            .createdAt(Instant.now()).updatedAt(Instant.now()).build()));
    }

    private List<UserEntity> seedResearchMembers(String hash) {
        var list = new ArrayList<UserEntity>();
        record RM(String name, String email, String phone, String occupation, String org, String purpose) {}
        var rms = List.of(
            new RM("Rahul Sharma",  "rahul.sharma@example.com",   "9876543211", "Financial Analyst",    "ICICI Securities",       "INVESTMENT_RESEARCH"),
            new RM("Priya Patel",   "priya.patel@example.com",    "9876543212", "Investment Banker",    "Kotak Investment Bank",  "DUE_DILIGENCE"),
            new RM("Arun Krishnan", "arun.krishnan@example.com",  "9876543220", "Portfolio Manager",    "HDFC Asset Management",  "INVESTMENT_RESEARCH"),
            new RM("Sneha Gupta",   "sneha.gupta@example.com",    "9876543221", "Management Consultant", "McKinsey & Company",     "MARKET_RESEARCH"),
            new RM("Vikram Singh",  "vikram.singh@example.com",   "9876543222", "Private Equity Analyst","Sequoia Capital India",  "DUE_DILIGENCE")
        );
        for (var i = 0; i < rms.size(); i++) {
            var rm = rms.get(i);
            var user = userRepo.save(UserEntity.builder()
                .id(i == 0 ? MEMBER_1_ID : i == 1 ? MEMBER_2_ID : UUID.randomUUID())
                .email(rm.email).passwordHash(hash).pan(generatePAN())
                .fullName(rm.name).phone(rm.phone)
                .role(UserEntity.UserRole.RESEARCH_MEMBER).emailVerified(true).active(true)
                .createdAt(Instant.now()).updatedAt(Instant.now()).build());
            list.add(user);

            // Create ResearchMember profile
            var cs = pick(IndianDataPool.CITIES);
            researchMemberRepo.save(ResearchMemberEntity.builder()
                .id(UUID.randomUUID()).userId(user.getId())
                .fullName(rm.name).occupation(rm.occupation)
                .organization(rm.org).designation(rm.occupation)
                .researchPurpose(rm.purpose)
                .country("India").state(cs.state()).city(cs.city())
                .industriesOfInterest("Technology, Finance, Healthcare, Energy")
                .companySizePreference("ALL")
                .notificationPreferences("{\"email\":true,\"push\":true}")
                .createdAt(Instant.now()).updatedAt(Instant.now()).build());
        }
        return list;
    }

    private List<UserEntity> seedCompanyUsers(String hash) {
        record CU(String name, String email, String phone, String pan) {}
        var cus = List.of(
            new CU("Vikram Singh",    "vikram.singh@company.in",    "9876543213", "XYZAB1234C"),
            new CU("Ananya Gupta",    "ananya.gupta@company.in",    "9876543215", "GHIJK5678L"),
            new CU("Dr. Sanjay Verma","sanjay.verma@company.in",   "9876543216", "MNOPQ9012R"),
            new CU("Meera Krishnan",  "meera.krishnan@company.in", "9876543217", "STUVW3456X"),
            new CU("Rohan Desai",     "rohan.desai@company.in",    "9876543218", "YZABC7890D")
        );
        var ids = List.of(COMPANY_1_ID, COMPANY_2_ID, COMPANY_3_ID, COMPANY_4_ID, COMPANY_5_ID);
        var list = new ArrayList<UserEntity>();
        for (var i = 0; i < cus.size(); i++) {
            var cu = cus.get(i);
            list.add(userRepo.save(UserEntity.builder()
                .id(ids.get(i))
                .email(cu.email).passwordHash(hash).pan(cu.pan)
                .fullName(cu.name).phone(cu.phone)
                .role(UserEntity.UserRole.COMPANY_USER).emailVerified(true).active(true)
                .createdAt(Instant.now()).updatedAt(Instant.now()).build()));
        }
        return list;
    }

    // ========================================================================
    //  2. COMPANIES
    // ========================================================================

    private List<CompanyEntity> seedCompanies(List<UserEntity> companyUsers, UserEntity admin) {
        var companies = new ArrayList<CompanyEntity>();

        // ── 5 Rich companies (detailed profiles) ──
        companies.add(buildRichCompany(1, "TechVentures India Pvt Ltd", "Technology", "Maharashtra", "Mumbai",
            "Private Limited", 2015, companyUsers.get(0).getId(),
            "Enterprise SaaS platform for supply chain management. Serving 500+ clients across India with AI-powered logistics optimization, real-time inventory tracking, and predictive demand forecasting solutions.",
            APPROVED_ACTIVE, "TechVentures", "Information Technology",
            "Revolutionizing supply chain management through AI-powered SaaS solutions",
            "To become India's leading supply chain intelligence platform powering 10,000+ enterprises by 2030",
            "Fast-paced, innovation-first culture with flat hierarchy. Quarterly hackathons, learning budget for every employee, and remote-friendly policies.",
            "Arjun Mehta", "Priya Kulkarni", "Arjun Mehta, Neha Singh",
            "B2B SaaS", "Growth", 1200, "₹85 Cr", "₹12 Cr funded",
            "Sequoia Capital India, Accel Partners",
            "Supply Chain OS, Inventory Optimizer, Logistics Tracker, Demand Forecaster",
            "AI Consulting, Implementation, Training",
            "Python, Go, React, TensorFlow, PostgreSQL, Redis, Kubernetes, AWS",
            "ISO 9001, ISO 27001, SOC 2",
            "Best Enterprise SaaS 2024 (NASSCOM), Top 50 AI Companies 2023"));

        companies.add(buildRichCompany(2, "GreenEnergy Solutions Ltd", "Energy", "Gujarat", "Ahmedabad",
            "Public Limited", 2010, companyUsers.get(1).getId(),
            "Renewable energy company specializing in solar and wind power generation. Operating 200MW capacity across Gujarat and Rajasthan with ambitious expansion plans for 500MW by 2027.",
            APPROVED_ACTIVE, "GreenEnergy", "Renewable Energy",
            "Powering India's sustainable future with clean renewable energy",
            "To achieve 5GW installed renewable capacity by 2030 and become India's most trusted green energy provider",
            "Sustainability-driven culture with focus on innovation and community impact. Strong emphasis on employee well-being and green practices.",
            "Vikram Rathore", "Anjali Sharma", "Vikram Rathore",
            "B2B Energy Supply", "Enterprise", 850, "₹320 Cr", "₹200 Cr funded",
            "Brookfield Renewable, IFC, State Bank of India",
            "Solar Power, Wind Power, Green Certificates, Power Purchase Agreements",
            "EPC Services, Maintenance, Energy Auditing",
            "SCADA, IoT Sensors, AI Predictive Maintenance, SAP, AWS",
            "ISO 9001, ISO 14001, ISO 45001, MNRE Certification",
            "Best Renewable Energy Company 2024, Golden Peacock Environment Award"));

        companies.add(buildRichCompany(3, "Bharat Biotech Labs Pvt Ltd", "Healthcare", "Telangana", "Hyderabad",
            "Private Limited", 2008, companyUsers.get(2).getId(),
            "Biotechnology research and development company focused on vaccines and diagnostic solutions. ISO 13485 certified with WHO GMP compliant manufacturing facilities.",
            APPROVED_ACTIVE, "Bharat Biotech", "Biotechnology",
            "Developing affordable life-saving vaccines and diagnostics for India and the world",
            "To make India self-reliant in vaccine manufacturing and achieve global leadership in biotech innovation",
            "Research-driven culture with world-class labs. Emphasis on scientific excellence, compliance, and patient impact.",
            "Dr. Rajesh Reddy", "Dr. Suman Rao", "Dr. Rajesh Reddy, Dr. Suman Rao, Dr. Prakash Nair",
            "B2G/B2B Biotech", "Enterprise", 1800, "₹520 Cr", "₹450 Cr funded",
            "Krishna Institute, HDFC Bank, US-India VC Fund",
            "Vaccines, Diagnostic Kits, Therapeutics, Research Reagents",
            "Contract Research, Clinical Trials, Regulatory Consulting",
            "CRISPR, ELISA, PCR, Next-Gen Sequencing, AI Drug Discovery, LIMS",
            "ISO 13485, ISO 9001, WHO GMP, USFDA, CDSCO, NABL",
            "National Biotech Award 2024, Best Vaccine Manufacturer 2023, 45+ Patents"));

        companies.add(buildRichCompany(4, "Indus Finance Corp Ltd", "Finance", "Maharashtra", "Mumbai",
            "Public Limited", 2005, companyUsers.get(3).getId(),
            "NBFC providing MSME lending, equipment financing, and working capital solutions across 15 states. Serving over 50,000 small businesses with a loan book of ₹890 Cr.",
            APPROVED_ACTIVE, "Indus Finance", "Financial Services",
            "Empowering Indian MSMEs with accessible and transparent financial solutions",
            "To be India's most trusted non-banking financial institution serving 1 million MSMEs",
            "Performance-driven with strong compliance culture. Meritocratic environment focusing on customer outcomes.",
            "Amit Khanna", null, "Amit Khanna, Sneha Agarwal",
            "B2B Lending", "Enterprise", 3500, "₹890 Cr", "₹600 Cr funded",
            "ICICI Venture, CDC Group, Kotak Mahindra",
            "MSME Loans, Equipment Finance, Working Capital, Supply Chain Finance",
            "Financial Advisory, Credit Assessment, Insurance",
            "Core Banking System, AI Credit Scoring, Loan Management, Salesforce, Tableau",
            "ISO 27001, RBI Registration, CIBIL Accredited, IRDAI Broker License",
            "Best NBFC 2024, MSME Empowerment Award 2023, 5-Star CRISIL Rating"));

        companies.add(buildRichCompany(5, "Skyline Realty Group LLP", "Real Estate", "Karnataka", "Bengaluru",
            "LLP", 2012, companyUsers.get(4).getId(),
            "Premium residential and commercial real estate developer. Delivered 25+ projects across Bangalore and Chennai spanning 6 million sq ft of developed area.",
            APPROVED_ACTIVE, "Skyline Realty", "Real Estate",
            "Building premium living and working spaces that redefine urban landscapes",
            "To become India's most admired real estate brand known for design excellence and on-time delivery",
            "Design-centric culture with focus on craftsmanship and customer delight. RERA compliant with 100% on-time project delivery record.",
            "Karthik Iyer", null, "Karthik Iyer, Ranjit Menon",
            "B2C Real Estate", "Growth", 450, "₹175 Cr", "₹85 Cr funded",
            "HDFC Capital, Kotak Realty Fund",
            "Premium Apartments, Villas, Commercial Spaces, Co-working",
            "Property Management, Interior Design, Facility Maintenance",
            "BIM, AutoCAD, Salesforce CRM, SAP, Project Management Suite",
            "RERA Registered, ISO 9001, CREDAI Member, IGBC Green Building",
            "Best Luxury Developer 2024, CREDAI Design Award 2023"));

        // ── 20 Standard companies with auto-generated data ──
        var standardCompanies = List.of(
            // idSuffix, name, sector, state, city, type, year, creatorIndex, status
            new StdCo(6,  "Ocean Logistics Ltd",          "Logistics",    "Tamil Nadu",   "Chennai",     "Public Limited", 2007, 0, APPROVED_ACTIVE),
            new StdCo(7,  "DigitalPay India Pvt Ltd",     "Technology",   "Karnataka",    "Bengaluru",   "Private Limited", 2018, 1, APPROVED_ACTIVE),
            new StdCo(8,  "AgriFresh Exports Pvt Ltd",    "Agriculture",  "Punjab",       "Ludhiana",    "Private Limited", 2013, 2, APPROVED_ACTIVE),
            new StdCo(9,  "MediCare Hospitals Ltd",       "Healthcare",   "Maharashtra",  "Pune",        "Public Limited", 2003, 3, APPROVED_ACTIVE),
            new StdCo(10, "SteelCraft Industries Ltd",    "Manufacturing","Odisha",       "Rourkela",    "Public Limited", 2000, 4, APPROVED_ACTIVE),
            new StdCo(11, "CloudNine Technologies Pvt Ltd","Technology",   "Telangana",    "Hyderabad",   "Private Limited", 2016, 0, APPROVED_ACTIVE),
            new StdCo(12, "EduPrime Learning Pvt Ltd",    "Education",    "Delhi",        "New Delhi",   "Private Limited", 2017, 1, APPROVED_ACTIVE),
            new StdCo(13, "SafeGuard Insurance Brokers Ltd","Finance",    "Maharashtra",  "Mumbai",      "Public Limited", 2009, 2, APPROVED_ACTIVE),
            new StdCo(14, "FreshMart Retail Chain Pvt Ltd","Retail",      "Tamil Nadu",   "Coimbatore",  "Private Limited", 2014, 3, APPROVED_ACTIVE),
            new StdCo(15, "AutoParts Manufacturing Pvt Ltd","Manufacturing","Haryana",    "Gurugram",    "Private Limited", 2011, 4, APPROVED_ACTIVE),
            new StdCo(16, "Urban Infra Projects Ltd",     "Infrastructure","Maharashtra", "Mumbai",      "Public Limited", 2006, 0, APPROVED_ACTIVE),
            new StdCo(17, "BioAgri Sciences Pvt Ltd",     "Agriculture",  "Maharashtra",  "Nashik",      "Private Limited", 2016, 1, APPROVED_ACTIVE),
            new StdCo(18, "CyberShield Security Pvt Ltd", "Technology",   "Karnataka",    "Bengaluru",   "Private Limited", 2018, 2, APPROVED_ACTIVE),
            new StdCo(19, "Golden Harvest Foods Pvt Ltd", "Food Processing","Madhya Pradesh","Indore",   "Private Limited", 2010, 3, APPROVED_ACTIVE),
            new StdCo(20, "WestWind Textiles Ltd",        "Textiles",     "Gujarat",      "Surat",       "Public Limited", 2001, 4, APPROVED_ACTIVE),
            new StdCo(21, "HealthFirst Diagnostics Ltd",  "Healthcare",   "Delhi",        "New Delhi",   "Public Limited", 2012, 0, APPROVED_ACTIVE),
            new StdCo(22, "SolarMax Energy Pvt Ltd",      "Energy",       "Rajasthan",    "Jaipur",      "Private Limited", 2015, 1, APPROVED_ACTIVE),
            new StdCo(23, "QuickShip Logistics LLP",      "Logistics",    "Maharashtra",  "Pune",        "LLP", 2019, 2, APPROVED_ACTIVE),
            new StdCo(24, "Apex Chemicals Ltd",           "Chemicals",    "Gujarat",      "Vadodara",    "Public Limited", 1998, 3, APPROVED_ACTIVE),
            new StdCo(25, "BuildRight Construction Pvt Ltd","Real Estate", "Karnataka",   "Bengaluru",   "Private Limited", 2011, 4, APPROVED_ACTIVE)
        );

        for (var sc : standardCompanies) {
            var creator = companyUsers.get(sc.creatorIndex() % companyUsers.size());
            companies.add(buildStandardCompany(sc, creator.getId(), admin.getId()));
        }

        // ── Workflow demo companies (non-APPROVED_ACTIVE statuses) ──
        companies.add(buildWorkflowCompany(26, "InnovateTech Solutions Pvt Ltd", "Technology", "Karnataka", "Bengaluru",
            "Private Limited", 2023, companyUsers.get(0).getId(),
            "AI-powered analytics platform for retail businesses in early development stage.", DRAFT));

        companies.add(buildWorkflowCompany(27, "FinFlow Capital Advisors Pvt Ltd", "Finance", "Maharashtra", "Mumbai",
            "Private Limited", 2019, companyUsers.get(1).getId(),
            "Boutique financial advisory firm specializing in M&A and fundraising for mid-market companies.",
            APPROVED_MEMBERSHIP_PENDING));

        companies.add(buildWorkflowCompany(28, "Punjab Tractors & Equipment Ltd", "Manufacturing", "Punjab", "Mohali",
            "Public Limited", 2002, companyUsers.get(2).getId(),
            "Farm equipment manufacturer specializing in tractors and harvesters with 15% North India market share.",
            PENDING_REVIEW));

        companies.add(buildWorkflowCompany(29, "Tranquil Pharma Pvt Ltd", "Pharmaceuticals", "Himachal Pradesh", "Baddi",
            "Private Limited", 2013, companyUsers.get(3).getId(),
            "Pharmaceutical manufacturing with WHO-GMP certified plant producing 50+ generic drug formulations.",
            PENDING_REVIEW));

        companies.add(buildWorkflowCompany(30, "Himalayan Organics Pvt Ltd", "Agriculture", "Uttarakhand", "Dehradun",
            "Private Limited", 2017, companyUsers.get(4).getId(),
            "Organic farming and produce company with certified organic vegetables, pulses, and spices.",
            REJECTED));

        var saved = companyRepo.saveAll(companies);
        log.info("Seeded {} companies", saved.size());
        return saved;
    }

    // ─── Standard company builder ─────────────────────────

    private record StdCo(int id, String name, String sector, String state, String city,
                         String type, int year, int creatorIndex, CompanyEntity.CompanyStatus status) {}

    private CompanyEntity buildStandardCompany(StdCo sc, UUID creatorId, UUID adminId) {
        var now = Instant.now();
        var brand = IndianDataPool.stripSuffix(sc.name());
        var stage = pick(IndianDataPool.COMPANY_STAGES);
        var hq = sc.city() + ", " + sc.state();
        var ceo = indianName();
        var cto = RND.nextBoolean() ? indianName() : null;
        var founder = ceo + (RND.nextBoolean() ? ", " + indianName() : "");

        int createdDays = RND.nextInt(30, 365);
        int approvedDays = createdDays - RND.nextInt(1, 30);
        boolean approved = sc.status() == APPROVED_ACTIVE;
        boolean rejected = sc.status() == REJECTED;
        boolean pending = sc.status() == PENDING_REVIEW;
        boolean mp = sc.status() == APPROVED_MEMBERSHIP_PENDING;

        return CompanyEntity.builder()
            .id(COMPANY_UUID(sc.id()))
            .name(sc.name()).sector(sc.sector()).state(sc.state()).city(sc.city())
            .companyType(sc.type()).incorporationYear(sc.year())
            .description(sc.sector() + " company based in " + hq + ". Established in " + sc.year() + ".")
            .website("https://" + brand.toLowerCase().replaceAll("[^a-z0-9]", "") + ".in")
            .logoUrl(pick(IndianDataPool.LOGO_URLS))
            .cin(generateCIN(sc.state(), sc.year(), sc.type()))
            .gstin(generateGST(sc.state(), sc.sector()))
            .pan(generatePAN())
            .companyRegistrationNumber(brand.substring(0, Math.min(3, brand.length())).toUpperCase()
                + "/REG/" + sc.year() + "/" + RND.nextInt(100, 9999))
            .registeredAddressLine1(hq).registeredCity(sc.city()).registeredState(sc.state())
            .registeredPinCode(String.valueOf(RND.nextInt(100000, 999999))).registeredCountry("India")
            .officialEmail("contact@" + brand.toLowerCase().replaceAll("[^a-z0-9]", "") + ".in")
            .officialPhone("+91 " + randomPhone())
            .phoneNumber("+91 " + randomPhone())
            .headquarter(hq)
            .linkedinProfile("https://linkedin.com/company/" + brand.toLowerCase().replaceAll("[^a-z0-9]", ""))
            .twitterUrl("https://twitter.com/" + brand.toLowerCase().replaceAll("[^a-z0-9]", ""))
            .annualTurnover(RND.nextInt(10, 500) + " Cr")
            .employeeCount(RND.nextInt(50, 2000))
            .totalFunding(RND.nextBoolean() ? "₹" + RND.nextInt(5, 200) + " Cr funded" : null)
            .investors(RND.nextBoolean() ? pickN(IndianDataPool.INVESTORS, RND.nextInt(1, 3)) : null)
            .ceoName(ceo).ctoName(cto).founders(founder)
            .businessModel(pick(IndianDataPool.BUSINESS_MODELS)).companyStage(stage)
            .productsServices(generateProducts(sc.sector()))
            .technologiesUsed(generateTechnologies(sc.sector()))
            .certifications(generateCertOverview(sc.sector()))
            .awards(generateAwards())
            .cultureSummary("Dynamic and inclusive workplace fostering innovation, collaboration, and continuous learning.")
            .mission("To deliver exceptional " + sc.sector().toLowerCase() + " solutions that drive value for our clients.")
            .vision("To be a globally recognized " + sc.sector().toLowerCase() + " leader known for innovation and trust.")
            .dashboardStatus(approved ? "Approved" : rejected ? "Rejected" : pending ? "Pending" : mp ? "Approved" : "Draft")
            .financialDataJson(generateFinancialDataJson(sc.name(), sc.sector(), sc.year()))
            .certificatesDataJson(generateCertificatesJson(sc.name(), sc.sector()))
            .videosDataJson(generateVideosJson(sc.name(), sc.sector()))
            .status(sc.status())
            .createdBy(creatorId)
            .approvedBy(approved || rejected || mp ? adminId : null)
            .approvedAt(approved || rejected || mp ? now.minusSeconds(approvedDays * 86400L) : null)
            .submittedAt(approved || pending || mp ? now.minusSeconds(createdDays * 86400L) : null)
            .listingExpiresAt(approved ? LocalDate.now().plusYears(1) : null)
            .createdAt(now.minusSeconds(createdDays * 86400L))
            .updatedAt(RND.nextBoolean() ? now.minusSeconds(RND.nextInt(1, 30) * 86400L) : now)
            .build();
    }

    // ─── Rich company builder ─────────────────────────────

    private CompanyEntity buildRichCompany(int idx, String name, String sector, String state, String city,
                                            String companyType, int year, UUID creatorId, String description,
                                            CompanyEntity.CompanyStatus status, String brand, String industry,
                                            String mission, String vision, String culture,
                                            String ceo, String cto, String founders,
                                            String businessModel, String stage, int empCount,
                                            String turnover, String funding, String investors,
                                            String products, String services, String technologies,
                                            String certs, String awards) {
        var now = Instant.now();
        boolean approved = status == APPROVED_ACTIVE;
        boolean rejected = status == REJECTED;
        var hq = city + ", " + state;
        var brandLower = brand.toLowerCase().replaceAll("[^a-z0-9]", "");
        int createdDays = RND.nextInt(60, 365);
        int approvedDays = createdDays - RND.nextInt(1, 15);

        return CompanyEntity.builder()
            .id(COMPANY_UUID(idx))
            .name(name).sector(sector).state(state).city(city)
            .companyType(companyType).incorporationYear(year)
            .description(description)
            .website("https://" + brandLower + ".in")
            .logoUrl(pick(IndianDataPool.LOGO_URLS))
            .cin(generateCIN(state, year, companyType))
            .gstin(generateGST(state, sector))
            .pan(generatePAN())
            .companyRegistrationNumber(brand.substring(0, Math.min(3, brand.length())).toUpperCase()
                + "/REG/" + year + "/" + RND.nextInt(100, 9999))
            .registeredAddressLine1(hq).registeredCity(city).registeredState(state)
            .registeredPinCode(String.valueOf(RND.nextInt(100000, 999999))).registeredCountry("India")
            .officialEmail("contact@" + brandLower + ".in")
            .officialPhone("+91 " + randomPhone()).phoneNumber("+91 " + randomPhone())
            .headquarter(hq)
            .linkedinProfile("https://linkedin.com/company/" + brandLower)
            .twitterUrl("https://twitter.com/" + brandLower)
            .annualTurnover(turnover).employeeCount(empCount)
            .totalFunding(funding).investors(investors)
            .ceoName(ceo).ctoName(cto).founders(founders)
            .businessModel(businessModel).companyStage(stage)
            .productsServices(products).technologiesUsed(technologies)
            .certifications(certs).awards(awards)
            .cultureSummary(culture).mission(mission).vision(vision)
            .dashboardStatus(approved ? "Approved" : rejected ? "Rejected" : "Pending")
            .financialDataJson(generateFinancialDataJson(name, sector, year))
            .certificatesDataJson(generateCertificatesJson(name, sector))
            .videosDataJson(generateVideosJson(name, sector))
            .status(status)
            .createdBy(creatorId)
            .approvedBy(approved || rejected ? ADMIN_ID : null)
            .approvedAt(approved || rejected ? now.minusSeconds(approvedDays * 86400L) : null)
            .submittedAt(approved || rejected ? now.minusSeconds(createdDays * 86400L) : null)
            .listingExpiresAt(approved ? LocalDate.now().plusYears(1) : null)
            .createdAt(now.minusSeconds(createdDays * 86400L))
            .updatedAt(RND.nextBoolean() ? now.minusSeconds(RND.nextInt(1, 30) * 86400L) : now)
            .build();
    }

    // ─── Workflow company (non-standard status) ──────────

    private CompanyEntity buildWorkflowCompany(int idx, String name, String sector, String state, String city,
                                                String companyType, int year, UUID creatorId, String description,
                                                CompanyEntity.CompanyStatus status) {
        var now = Instant.now();
        var brand = IndianDataPool.stripSuffix(name);
        boolean approved = status == APPROVED_MEMBERSHIP_PENDING;
        boolean rejected = status == REJECTED;
        boolean submitted = status == PENDING_REVIEW;
        var hq = city + ", " + state;
        int daysAgo = RND.nextInt(5, 60);

        return CompanyEntity.builder()
            .id(COMPANY_UUID(idx))
            .name(name).sector(sector).state(state).city(city)
            .companyType(companyType).incorporationYear(year)
            .description(description)
            .website("https://" + brand.toLowerCase().replaceAll("[^a-z0-9]", "") + ".in")
            .logoUrl(pick(IndianDataPool.LOGO_URLS))
            .status(status)
            .createdBy(creatorId)
            .approvedBy(approved || rejected ? ADMIN_ID : null)
            .approvedAt(approved || rejected ? now.minusSeconds(daysAgo * 86400L) : null)
            .submittedAt(approved || submitted ? now.minusSeconds((daysAgo + 5) * 86400L) : null)
            .createdAt(now.minusSeconds((daysAgo + 10) * 86400L))
            .updatedAt(RND.nextInt(1, 5) > 1 ? now : now.minusSeconds(RND.nextInt(1, 10) * 86400L))
            .dashboardStatus(status == DRAFT ? "Draft" : approved ? "Approved" : rejected ? "Rejected" : "Pending")
            .build();
    }

    // ─── Company Documents ───────────────────────────────

    private void seedCompanyDocuments(List<CompanyEntity> companies) {
        var docs = new ArrayList<CompanyDocumentEntity>();
        var docTypes = List.of("BALANCE_SHEET", "PROFIT_LOSS", "CASH_FLOW", "AUDIT_REPORT", "TAX_FILING", "INCORPORATION", "GST_CERTIFICATE");
        for (var company : companies) {
            if (company.getStatus() != APPROVED_ACTIVE) continue;
            for (int i = 0; i < RND.nextInt(2, 5); i++) {
                var docType = docTypes.get(RND.nextInt(docTypes.size()));
                docs.add(CompanyDocumentEntity.builder()
                    .id(UUID.randomUUID()).companyId(company.getId())
                    .documentType(docType)
                    .fileUrl("https://storage.dataofbusiness.in/documents/"
                        + company.getId().toString().substring(0, 8) + "/" + docType.toLowerCase() + "_"
                        + Year.now().getValue() + ".pdf")
                    .uploadedAt(Instant.now().minusSeconds(RND.nextLong(1, 365) * 86400L))
                    .build());
            }
        }
        documentRepo.saveAll(docs);
    }

    // ========================================================================
    //  3. MEMBERSHIPS
    // ========================================================================

    private List<MembershipEntity> seedMemberships(List<UserEntity> members) {
        if (members.size() < 2) return List.of();
        var now = LocalDate.now();

        var m1 = membershipRepo.save(MembershipEntity.builder()
            .id(UUID.fromString("b0000000-0000-0000-0000-000000000001"))
            .userId(members.get(0).getId()).planType("CREDITS_10")
            .status(MembershipEntity.MembershipStatus.ACTIVE)
            .startDate(now.minusDays(15)).endDate(now.plusDays(15))
            .downloadLimit(10).downloadsUsed(3)
            .createdAt(Instant.now()).updatedAt(Instant.now()).build());

        var m2 = membershipRepo.save(MembershipEntity.builder()
            .id(UUID.fromString("b0000000-0000-0000-0000-000000000002"))
            .userId(members.get(1).getId()).planType("CREDITS_5")
            .status(MembershipEntity.MembershipStatus.ACTIVE)
            .startDate(now.minusDays(30)).endDate(now.plusDays(1))
            .downloadLimit(5).downloadsUsed(5)
            .createdAt(Instant.now()).updatedAt(Instant.now()).build());

        // Additional members with memberships
        var m3 = membershipRepo.save(MembershipEntity.builder()
            .id(UUID.randomUUID())
            .userId(members.get(2).getId()).planType("CREDITS_20")
            .status(MembershipEntity.MembershipStatus.ACTIVE)
            .startDate(now.minusDays(45)).endDate(now.plusDays(45))
            .downloadLimit(20).downloadsUsed(7)
            .createdAt(Instant.now()).updatedAt(Instant.now()).build());

        return List.of(m1, m2, m3);
    }

    // ========================================================================
    //  4. PAYMENTS
    // ========================================================================

    private void seedPayments(List<UserEntity> members, List<MembershipEntity> memberships,
                               List<CompanyEntity> companies) {
        var payments = new ArrayList<PaymentEntity>();

        // Membership payments
        if (memberships.size() >= 2 && members.size() >= 2) {
            payments.add(PaymentEntity.builder()
                .id(UUID.fromString("d0000000-0000-0000-0000-000000000001"))
                .userId(members.get(0).getId()).membershipId(memberships.get(0).getId())
                .planId("CREDITS_10")
                .amount(new BigDecimal("3000.00")).gst(new BigDecimal("540.00")).total(new BigDecimal("3540.00"))
                .razorpayOrderId("order_" + randomTxn()).razorpayPaymentId("pay_" + randomTxn())
                .razorpaySignature("sig_" + randomTxn())
                .status(PaymentEntity.PaymentStatus.PAID).paymentType(PaymentEntity.PaymentType.MEMBERSHIP)
                .createdAt(Instant.now().minusSeconds(15 * 86400L)).build());

            payments.add(PaymentEntity.builder()
                .id(UUID.fromString("d0000000-0000-0000-0000-000000000002"))
                .userId(members.get(1).getId()).membershipId(memberships.get(1).getId())
                .planId("CREDITS_5")
                .amount(new BigDecimal("2000.00")).gst(new BigDecimal("360.00")).total(new BigDecimal("2360.00"))
                .razorpayOrderId("order_" + randomTxn()).razorpayPaymentId("pay_" + randomTxn())
                .razorpaySignature("sig_" + randomTxn())
                .status(PaymentEntity.PaymentStatus.PAID).paymentType(PaymentEntity.PaymentType.MEMBERSHIP)
                .createdAt(Instant.now().minusSeconds(30 * 86400L)).build());

            if (memberships.size() >= 3) {
                payments.add(PaymentEntity.builder()
                    .id(UUID.randomUUID())
                    .userId(members.get(2).getId()).membershipId(memberships.get(2).getId())
                    .planId("CREDITS_20")
                    .amount(new BigDecimal("4000.00")).gst(new BigDecimal("720.00")).total(new BigDecimal("4720.00"))
                    .razorpayOrderId("order_" + randomTxn()).razorpayPaymentId("pay_" + randomTxn())
                    .razorpaySignature("sig_" + randomTxn())
                    .status(PaymentEntity.PaymentStatus.PAID).paymentType(PaymentEntity.PaymentType.MEMBERSHIP)
                    .createdAt(Instant.now().minusSeconds(45 * 86400L)).build());
            }
        }

        // Company listing payments
        for (var company : companies) {
            if (company.getStatus() == APPROVED_ACTIVE && RND.nextDouble() < 0.5) {
                payments.add(PaymentEntity.builder()
                    .id(UUID.randomUUID())
                    .userId(company.getCreatedBy()).companyId(company.getId())
                    .planId("COMPANY")
                    .amount(new BigDecimal("500.00")).gst(new BigDecimal("90.00")).total(new BigDecimal("590.00"))
                    .razorpayOrderId("order_" + randomTxn()).razorpayPaymentId("pay_" + randomTxn())
                    .razorpaySignature("sig_" + randomTxn())
                    .status(PaymentEntity.PaymentStatus.PAID).paymentType(PaymentEntity.PaymentType.LISTING)
                    .createdAt(Instant.now().minusSeconds(RND.nextLong(30, 365) * 86400L)).build());
            }
        }

        paymentRepo.saveAll(payments);
    }

    // ========================================================================
    //  5. GRIEVANCES
    // ========================================================================

    private void seedGrievances(List<UserEntity> members, UserEntity admin) {
        if (members.size() < 2) return;

        var grievances = new ArrayList<GrievanceEntity>();

        grievances.add(GrievanceEntity.builder()
            .id(UUID.randomUUID()).userId(members.get(0).getId())
            .complaintType("Data Discrepancy")
            .description("Found a mismatch in revenue figures for company TechVentures India. The FY2024-25 reported revenue does not match the CA certificate uploaded on the platform.")
            .status(GrievanceEntity.GrievanceStatus.OPEN)
            .priority(GrievanceEntity.GrievancePriority.HIGH)
            .createdAt(Instant.now().minusSeconds(RND.nextLong(1, 10) * 86400L))
            .updatedAt(Instant.now()).build());

        grievances.add(GrievanceEntity.builder()
            .id(UUID.randomUUID()).userId(members.get(1).getId())
            .complaintType("Download Issue")
            .description("Unable to download financial statement PDF for company GreenEnergy Solutions. The download button is not responding when clicked.")
            .status(GrievanceEntity.GrievanceStatus.IN_PROGRESS)
            .priority(GrievanceEntity.GrievancePriority.MEDIUM)
            .assignedTo(admin.getId())
            .createdAt(Instant.now().minusSeconds(RND.nextLong(15, 30) * 86400L))
            .updatedAt(Instant.now()).build());

        if (members.size() >= 3) {
            grievances.add(GrievanceEntity.builder()
                .id(UUID.randomUUID()).userId(members.get(2).getId())
                .complaintType("Account Access")
                .description("Unable to reset my account password. The reset link sent to my email is not working and shows an expired token error.")
                .status(GrievanceEntity.GrievanceStatus.RESOLVED)
                .priority(GrievanceEntity.GrievancePriority.LOW)
                .assignedTo(admin.getId())
                .resolution("Password reset link expiry time has been increased from 15 minutes to 60 minutes. User was guided through manual reset process.")
                .resolvedAt(Instant.now().minusSeconds(RND.nextLong(5, 15) * 86400L))
                .createdAt(Instant.now().minusSeconds(RND.nextLong(20, 45) * 86400L))
                .updatedAt(Instant.now()).build());
        }

        grievanceRepo.saveAll(grievances);
    }

    // ========================================================================
    //  6. AUDIT LOGS
    // ========================================================================

    private void seedAuditLogs(List<UserEntity> members, List<CompanyEntity> companies) {
        var logs = new ArrayList<AuditLogEntity>();
        var actions = List.of("LOGIN", "LOGOUT", "COMPANY_VIEWED", "COMPANY_SEARCHED", "PROFILE_UPDATED");

        for (int i = 0; i < 15; i++) {
            var user = pick(members);
            var action = pick(actions);
            UUID companyId = action.equals("COMPANY_VIEWED") && !companies.isEmpty()
                ? pick(companies).getId() : null;

            logs.add(AuditLogEntity.builder()
                .id(UUID.randomUUID()).userId(user.getId())
                .action(action).companyId(companyId)
                .transactionId("TXN-" + randomTxn())
                .outcome("SUCCESS")
                .details(action + " performed by user " + user.getEmail())
                .ipAddress("192.168." + RND.nextInt(1, 255) + "." + RND.nextInt(1, 255))
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0")
                .createdAt(Instant.now().minusSeconds(RND.nextLong(1, 90) * 86400L))
                .build());
        }
        auditLogRepo.saveAll(logs);
    }

    // ========================================================================
    //  7. CREDIT TRANSACTIONS
    // ========================================================================

    private List<CreditTransactionEntity> seedCreditTransactions(List<UserEntity> members, List<CompanyEntity> companies) {
        var txs = new ArrayList<CreditTransactionEntity>();
        if (members.size() < 2 || companies.isEmpty()) return txs;

        // Get research member user IDs (ResearchMember role)
        var researchMembers = members.stream()
            .filter(u -> u.getRole() == UserEntity.UserRole.RESEARCH_MEMBER)
            .toList();

        if (researchMembers.isEmpty()) return txs;

        // Credit purchases (positive)
        txs.add(CreditTransactionEntity.builder()
            .id(UUID.randomUUID()).memberId(researchMembers.get(0).getId())
            .creditsUsed(10).transactionType("PURCHASE")
            .balanceBefore(0).balanceAfter(10)
            .status("COMPLETED").transactionId("TXN-CR-" + randomTxn())
            .createdAt(Instant.now().minusSeconds(15 * 86400L)).build());

        // Credit deductions (negative — unlocking companies)
        int bal = 10;
        for (int i = 0; i < Math.min(3, companies.size()); i++) {
            int used = 1;
            txs.add(CreditTransactionEntity.builder()
                .id(UUID.randomUUID()).memberId(researchMembers.get(0).getId())
                .companyId(companies.get(i).getId())
                .creditsUsed(used).transactionType("UNLOCK")
                .balanceBefore(bal).balanceAfter(bal - used)
                .status("COMPLETED").transactionId("TXN-UL-" + randomTxn())
                .createdAt(Instant.now().minusSeconds((15 - i) * 86400L)).build());
            bal -= used;
        }

        // Second member with fewer transactions
        if (researchMembers.size() >= 2) {
            txs.add(CreditTransactionEntity.builder()
                .id(UUID.randomUUID()).memberId(researchMembers.get(1).getId())
                .creditsUsed(5).transactionType("PURCHASE")
                .balanceBefore(0).balanceAfter(5)
                .status("COMPLETED").transactionId("TXN-CR-" + randomTxn())
                .createdAt(Instant.now().minusSeconds(30 * 86400L)).build());
        }

        return creditTxRepo.saveAll(txs);
    }

    // ========================================================================
    //  8. UNLOCKED COMPANIES
    // ========================================================================

    private void seedUnlockedCompanies(List<UserEntity> members, List<CompanyEntity> companies,
                                        List<CreditTransactionEntity> creditTxs) {
        var unlocked = new ArrayList<UnlockedCompanyEntity>();
        var researchMembers = members.stream()
            .filter(u -> u.getRole() == UserEntity.UserRole.RESEARCH_MEMBER)
            .toList();

        if (researchMembers.isEmpty() || companies.isEmpty()) return;

        // First member unlocked top 3 approved companies
        var approvedCompanies = companies.stream()
            .filter(c -> c.getStatus() == APPROVED_ACTIVE)
            .limit(3)
            .toList();

        for (int i = 0; i < approvedCompanies.size(); i++) {
            unlocked.add(UnlockedCompanyEntity.builder()
                .id(UUID.randomUUID()).memberId(researchMembers.get(0).getId())
                .companyId(approvedCompanies.get(i).getId())
                .creditsUsed(1)
                .unlockedAt(Instant.now().minusSeconds((15 - i) * 86400L))
                .unlockedBy(researchMembers.get(0).getId())
                .build());
        }

        unlockedRepo.saveAll(unlocked);
    }

    // ========================================================================
    //  DATA GENERATORS (Financial JSON, Certificates JSON, Videos JSON)
    // ========================================================================

    private String generateFinancialDataJson(String companyName, String sector, int foundedYear) {
        int currentYear = Year.now().getValue();
        var statements = new ArrayList<Map<String, Object>>();

        for (int offset = 0; offset < 3; offset++) {
            int fy = currentYear - offset;
            String fyLabel = "FY" + (fy - 1) + "-" + fy;

            double baseRevenue = switch (sector) {
                case "Technology" -> RND.nextDouble(50, 500);
                case "Energy" -> RND.nextDouble(100, 1000);
                case "Healthcare" -> RND.nextDouble(30, 300);
                case "Pharmaceuticals" -> RND.nextDouble(40, 400);
                case "Finance" -> RND.nextDouble(100, 800);
                case "Real Estate" -> RND.nextDouble(40, 400);
                case "Manufacturing" -> RND.nextDouble(60, 600);
                case "Automotive" -> RND.nextDouble(80, 700);
                case "Agriculture" -> RND.nextDouble(10, 100);
                case "Retail" -> RND.nextDouble(20, 200);
                case "Logistics" -> RND.nextDouble(30, 300);
                case "Education" -> RND.nextDouble(10, 80);
                default -> RND.nextDouble(15, 150);
            };

            double revenue = baseRevenue * (1 - offset * 0.15);
            double expenses = revenue * RND.nextDouble(0.6, 0.85);
            double ebitda = revenue - expenses;
            double netProfit = ebitda * RND.nextDouble(0.5, 0.8);
            double assets = revenue * RND.nextDouble(1.5, 3.0);
            double liabilities = assets * RND.nextDouble(0.3, 0.6);
            double equity = assets - liabilities;
            double opCashFlow = netProfit * RND.nextDouble(0.8, 1.2);
            double capex = revenue * RND.nextDouble(0.05, 0.15);
            double debt = liabilities * RND.nextDouble(0.4, 0.7);

            var prefix = companyName.toLowerCase().replaceAll("[^a-z0-9]", "");
            var yearData = new LinkedHashMap<String, Object>();
            yearData.put("financialYear", fyLabel);
            yearData.put("revenue", round1(revenue));
            yearData.put("expenses", round1(expenses));
            yearData.put("ebitda", round1(ebitda));
            yearData.put("netProfit", round1(netProfit));
            yearData.put("totalAssets", round1(assets));
            yearData.put("totalLiabilities", round1(liabilities));
            yearData.put("totalEquity", round1(equity));
            yearData.put("operatingCashFlow", round1(opCashFlow));
            yearData.put("capex", round1(capex));
            yearData.put("totalDebt", round1(debt));
            yearData.put("verified", RND.nextDouble() < 0.8);
            yearData.put("balanceSheetUrl", "https://storage.dataofbusiness.in/financials/" + prefix + "/BS_" + fyLabel + ".pdf");
            yearData.put("profitLossUrl", "https://storage.dataofbusiness.in/financials/" + prefix + "/PL_" + fyLabel + ".pdf");
            yearData.put("cashFlowUrl", "https://storage.dataofbusiness.in/financials/" + prefix + "/CF_" + fyLabel + ".pdf");
            yearData.put("auditorReportUrl", "https://storage.dataofbusiness.in/financials/" + prefix + "/AR_" + fyLabel + ".pdf");
            yearData.put("uploadDate", LocalDate.now().minusDays(RND.nextInt(1, 365)).toString());
            yearData.put("uploadedBy", pick(List.of("CA R. Sharma", "CA A. Gupta", "CA S. Patel", "CA P. Verma")));
            yearData.put("status", RND.nextDouble() < 0.8 ? "Approved" : "Pending");
            yearData.put("fileSize", RND.nextInt(1, 10) + "." + RND.nextInt(1, 9) + " MB");
            statements.add(yearData);
        }
        return toJson(statements);
    }

    private String generateCertificatesJson(String companyName, String sector) {
        var certs = new ArrayList<Map<String, Object>>();
        var usedTypes = new HashSet<String>();
        var sectorCerts = IndianDataPool.SECTOR_CERT_NAMES.getOrDefault(sector, IndianDataPool.SECTOR_CERT_NAMES.get("Technology"));
        var now = Instant.now();

        // Add sector-specific certs first
        for (var certName : sectorCerts) {
            certs.add(buildCertEntry(certName, companyName, now));
            usedTypes.add(certName);
        }

        // Fill remainder with random certs
        int target = RND.nextInt(8, 14);
        while (certs.size() < target) {
            var ct = pick(IndianDataPool.ALL_CERT_TYPES);
            if (usedTypes.add(ct.name())) {
                certs.add(buildCertEntry(ct.name(), companyName, now));
            }
        }
        return toJson(certs);
    }

    private Map<String, Object> buildCertEntry(String certName, String companyName, Instant now) {
        var prefix = companyName.toLowerCase().replaceAll("[^a-z0-9]", "").substring(0, Math.min(5, companyName.length()));
        boolean active = RND.nextDouble() < 0.85;
        return Map.of(
            "certificateName", certName,
            "certificateNumber", certName.substring(0, Math.min(4, certName.length())).toUpperCase()
                + "-" + RND.nextInt(100000, 999999),
            "issuingAuthority", pick(List.of("BIS", "ISO", "AICPA", "DPIIT", "US FDA", "WHO", "FSSAI", "NABL")),
            "issueDate", now.minusSeconds(RND.nextLong(30, 1500) * 86400L).toString(),
            "expiryDate", active
                ? now.plusSeconds(RND.nextLong(30, 1500) * 86400L).toString()
                : now.minusSeconds(RND.nextLong(1, 100) * 86400L).toString(),
            "status", active ? "Active" : "Expired",
            "verificationUrl", "https://verify.dataofbusiness.in/certificates/" + prefix + "/"
                + certName.toLowerCase().replaceAll("[^a-z0-9]", ""),
            "pdfUrl", "https://storage.dataofbusiness.in/certificates/" + prefix + "/"
                + certName.toLowerCase().replaceAll("[^a-z0-9]", "") + ".pdf",
            "description", certName + " certification for " + companyName + ".",
            "verified", RND.nextDouble() < 0.85
        );
    }

    private String generateVideosJson(String companyName, String sector) {
        var videos = new ArrayList<Map<String, Object>>();
        var prefix = companyName.toLowerCase().replaceAll("[^a-z0-9]", "");

        record VidCat(String title, String desc) {}
        var categories = List.of(
            new VidCat("Company Introduction", "A brief introduction to " + companyName + " and our journey in the " + sector + " industry."),
            new VidCat("Product Demo", "Comprehensive product demonstration showcasing key features and benefits."),
            new VidCat("Investor Pitch", "Investment opportunity deck and growth story for potential investors."),
            new VidCat("Customer Success Story", "How we helped our clients achieve their business goals."),
            new VidCat("Technology Overview", "Deep dive into our technology stack and architecture."),
            new VidCat("Manufacturing Tour", "Behind the scenes look at our facilities and operations.")
        );

        for (int i = 0; i < Math.min(categories.size(), RND.nextInt(3, 6)); i++) {
            var cat = categories.get(i);
            int duration = RND.nextInt(60, 600);
            int views = RND.nextInt(1000, 100000);

            var video = new LinkedHashMap<String, Object>();
            video.put("title", companyName + " — " + cat.title());
            video.put("description", cat.desc());
            video.put("duration", (duration / 60) + ":" + String.format("%02d", duration % 60));
            video.put("durationSeconds", duration);
            video.put("videoUrl", "https://storage.dataofbusiness.in/videos/" + prefix + "/"
                + cat.title().toLowerCase().replaceAll("[^a-z0-9]", "") + ".mp4");
            video.put("thumbnailUrl", "https://images.unsplash.com/photo-1536240478700-b869070f9279?w=400");
            video.put("uploadDate", LocalDate.now().minusDays(RND.nextInt(1, 365)).toString());
            video.put("category", cat.title());
            video.put("views", views);
            video.put("likes", RND.nextInt(50, Math.max(51, views / 10)));
            video.put("comments", RND.nextInt(5, 50));
            video.put("shares", RND.nextInt(5, 100));
            video.put("language", RND.nextDouble() < 0.7 ? "English" : "Hindi");
            video.put("resolution", "1920x1080");
            video.put("status", "Published");
            video.put("speaker", pick(List.of("CEO", "CTO", "Founder", "VP Marketing", "Head of Product")));
            videos.add(video);
        }
        return toJson(videos);
    }

    // ─── Small helpers ─────────────────────

    private String generateProducts(String sector) {
        return switch (sector) {
            case "Technology" -> pick(List.of("Cloud Platform, SaaS Suite, API Gateway",
                "Analytics Dashboard, Mobile SDK, Data Pipeline", "AI Assistant, Automation Tools, Dev Platform"));
            case "Healthcare", "Pharmaceuticals" -> pick(List.of("Diagnostic Kit, Medical Device, Health Monitor",
                "Vaccine, Therapeutics, Test Kit", "Hospital Software, Patient Portal, EHR System"));
            case "Finance" -> pick(List.of("Loan Product, Insurance Plan, Investment Fund",
                "Payment Gateway, Wallet, Credit Card", "SME Loan, Working Capital, Equipment Finance"));
            case "Manufacturing", "Automotive" -> pick(List.of("Industrial Equipment, Components, Spare Parts",
                "Consumer Goods, Machinery, Tools", "Steel Products, Fabricated Parts, Assemblies"));
            case "Agriculture" -> pick(List.of("Organic Grains, Spices, Processed Foods",
                "Fertilizers, Pesticides, Seeds", "Fresh Produce, Dairy Products, Pulses"));
            case "Energy" -> pick(List.of("Solar Power, Wind Power, Green Certificates",
                "Power Solutions, Energy Storage, PPA", "Renewable Energy, Battery Storage, Grid Solutions"));
            case "Real Estate" -> pick(List.of("Residential Apartments, Commercial Spaces, Villas",
                "Township Projects, Co-Working, Retail", "Luxury Homes, Office Spaces, Plots"));
            default -> pick(List.of("Core Product Line, Premium Range, Enterprise Solutions",
                "Standard Products, Custom Solutions, Accessories"));
        };
    }

    private String generateTechnologies(String sector) {
        return switch (sector) {
            case "Technology" -> pick(List.of("Python, Go, React, Node.js, PostgreSQL, Kubernetes, AWS, TensorFlow",
                "Java, Spring Boot, Angular, Docker, Redis, GCP, Kafka, PyTorch",
                "TypeScript, Next.js, GraphQL, MongoDB, Terraform, Azure, MLflow"));
            case "Healthcare" -> pick(List.of("CRISPR, ELISA, PCR, NGS, LIMS, AI Diagnostics, Cloud Platform",
                "Bioreactors, Spectrometry, HPLC, Cell Culture, Bioinformatics, IoT"));
            case "Finance" -> pick(List.of("Core Banking System, AI Credit Scoring, Loan Management, Salesforce, Tableau",
                "Payment Gateway, Fraud Detection, Risk Engine, Blockchain, Analytics"));
            case "Manufacturing" -> pick(List.of("SCADA, IoT Sensors, ERP, SAP, AutoCAD, PLC, MES, AI Quality Control",
                "CNC, Robotics, CAD/CAM, PLM, SCM, Industry 4.0, Digital Twin"));
            case "Energy" -> pick(List.of("SCADA Systems, IoT Sensor Networks, ML Forecasting, Battery Management",
                "Solar PV Design, Wind Analytics, Drone Thermography, Power BI"));
            case "Real Estate" -> pick(List.of("BIM, AutoCAD, Salesforce CRM, SAP, Project Management Suite, VR Tools",
                "Revit, Primavera P6, Drone Survey, IoT Building Management, Procore"));
            case "Logistics" -> pick(List.of("Fleet Management System, WMS, TMS, Route Optimization, IoT Trackers",
                "ERP, Real-Time Tracking, Analytics Dashboard, Mobile Apps, RFID"));
            default -> pick(List.of("ERP, CRM, IoT, Cloud Infrastructure, Data Analytics, AI/ML",
                "Automation Tools, Monitoring Systems, Quality Management Software"));
        };
    }

    private String generateCertOverview(String sector) {
        return IndianDataPool.SECTOR_CERT_NAMES.getOrDefault(sector, IndianDataPool.SECTOR_CERT_NAMES.get("Technology"))
            .stream().limit(3).collect(Collectors.joining(", "));
    }

    private String generateAwards() {
        return pick(List.of(
            "Best in Industry 2024, Innovation Award 2023, Customer Excellence 2023",
            "National Excellence Award 2024, Emerging Leader 2023, Quality Award 2023",
            "Industry Leader 2024, Top Employer 2023, Sustainability Award 2023",
            "Golden Trophy 2024, Best Brand 2023, Service Excellence 2023",
            "Most Innovative Company 2024, Best Workplace 2023, Growth Leader 2023"
        ));
    }

    // ========================================================================
    //  REGISTRATION NUMBER GENERATORS
    // ========================================================================

    private String generateCIN(String state, int year, String companyType) {
        String stateCode = IndianDataPool.STATE_CODES.getOrDefault(state, "MH");
        String listing = RND.nextBoolean() ? "U" : "L";
        String typeCode = switch (companyType.toLowerCase()) {
            case "private limited" -> "PTC";
            case "public limited" -> "PLC";
            case "llp" -> "LLP";
            case "limited liability partnership" -> "LLP";
            default -> "PTC";
        };
        return listing + String.format("%05d", RND.nextInt(10000, 99999))
            + stateCode + year + typeCode + String.format("%06d", RND.nextInt(100000, 999999));
    }

    private String generateGST(String state, String sector) {
        String stateCode = IndianDataPool.STATE_CODES.getOrDefault(state, "MH");
        int stateNum = STATE_GST_CODES.getOrDefault(stateCode, 27);
        String pan = generatePAN();
        int entityNum = RND.nextInt(10);
        int checkDigit = RND.nextInt(10);
        return String.format("%02d%s%dZ%d", stateNum, pan, entityNum, checkDigit);
    }

    private static final java.util.Map<String, Integer> STATE_GST_CODES = java.util.Map.ofEntries(
        java.util.Map.entry("MH", 27), java.util.Map.entry("KA", 29),
        java.util.Map.entry("TS", 36), java.util.Map.entry("GJ", 24),
        java.util.Map.entry("TN", 33), java.util.Map.entry("DL", 7),
        java.util.Map.entry("WB", 19), java.util.Map.entry("RJ", 8),
        java.util.Map.entry("UP", 9), java.util.Map.entry("KL", 32),
        java.util.Map.entry("AP", 37), java.util.Map.entry("OD", 21),
        java.util.Map.entry("MP", 23), java.util.Map.entry("PB", 3),
        java.util.Map.entry("HR", 6), java.util.Map.entry("AS", 18),
        java.util.Map.entry("UK", 5), java.util.Map.entry("JH", 20),
        java.util.Map.entry("CG", 22), java.util.Map.entry("CH", 4)
    );

    private String generatePAN() {
        var letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        var sb = new StringBuilder();
        for (int i = 0; i < 5; i++) sb.append(letters.charAt(RND.nextInt(letters.length())));
        sb.append(String.format("%04d", RND.nextInt(1000, 9999)));
        sb.append(letters.charAt(RND.nextInt(letters.length())));
        return sb.toString();
    }

    private String randomPhone() {
        return (RND.nextInt(60000, 99999) + "" + RND.nextInt(100000, 999999));
    }

    private String randomTxn() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // ========================================================================
    //  GENERAL UTILITIES
    // ========================================================================

    private String indianName() {
        return pick(RND.nextBoolean() ? IndianDataPool.MALE_FIRST_NAMES : IndianDataPool.FEMALE_FIRST_NAMES)
            + " " + pick(IndianDataPool.LAST_NAMES);
    }

    private <T> T pick(List<T> list) {
        return list.get(RND.nextInt(list.size()));
    }

    private <T> T pick(T[] array) {
        return array[RND.nextInt(array.length)];
    }

    private String pickN(List<String> list, int n) {
        var shuffled = new ArrayList<>(list);
        java.util.Collections.shuffle(shuffled, RND);
        return shuffled.subList(0, Math.min(n, shuffled.size())).stream()
            .collect(Collectors.joining(", "));
    }

    private double round1(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("JSON serialization failed", e);
            return "[]";
        }
    }

    private void printLoginInfo(UserEntity admin, List<UserEntity> auditors,
                                 List<UserEntity> members, List<UserEntity> companyUsers) {
        log.info("╔══════════════════════════════════════════════════════════╗");
        log.info("║   ✓ DATABASE SEEDING COMPLETE                           ║");
        log.info("╠══════════════════════════════════════════════════════════╣");
        log.info("║   Login credentials (password: {})       ║", DEFAULT_PASSWORD);
        log.info("╠══════════════════════════════════════════════════════════╣");
        log.info("║   Admin:        {}  ║", admin.getEmail());
        log.info("║   Auditor:      {}  ║", auditors.get(0).getEmail());
        for (var m : members) {
            log.info("║   Member:       {}  ║", m.getEmail());
        }
        for (var cu : companyUsers) {
            log.info("║   Company:      {}  ║", cu.getEmail());
        }
        log.info("╚══════════════════════════════════════════════════════════╝");
    }

    // ─── CompanyStatus constants ──────────────────────────
    private static final CompanyEntity.CompanyStatus DRAFT = CompanyEntity.CompanyStatus.DRAFT;
    private static final CompanyEntity.CompanyStatus PENDING_REVIEW = CompanyEntity.CompanyStatus.PENDING_REVIEW;
    private static final CompanyEntity.CompanyStatus APPROVED_ACTIVE = CompanyEntity.CompanyStatus.APPROVED_ACTIVE;
    private static final CompanyEntity.CompanyStatus APPROVED_MEMBERSHIP_PENDING = CompanyEntity.CompanyStatus.APPROVED_MEMBERSHIP_PENDING;
    private static final CompanyEntity.CompanyStatus REJECTED = CompanyEntity.CompanyStatus.REJECTED;
}
