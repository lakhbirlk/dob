package com.dob.infrastructure.config;

import com.dob.infrastructure.persistence.entity.*;
import com.dob.infrastructure.persistence.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.time.Year;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserJpaRepository userRepo;
    private final MembershipJpaRepository membershipRepo;
    private final CompanyJpaRepository companyRepo;
    private final PaymentJpaRepository paymentRepo;
    private final GrievanceJpaRepository grievanceRepo;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    private static final ThreadLocalRandom RND = ThreadLocalRandom.current();
    private static final String[] DASHBOARD_STATUSES = {"Approved", "Under Review", "Pending", "Rejected", "Needs Changes"};
    private static final String[] COMPANY_STAGES = {"Startup", "Growth", "Enterprise"};
    private static final String[] BUSINESS_MODELS = {
        "B2B SaaS", "B2C Marketplace", "D2C Brand", "Manufacturing", "Services",
        "Platform", "Consulting", "Retail", "Wholesale", "Hybrid"
    };

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepo.count() > 0) {
            log.info("Database already seeded — skipping");
            return;
        }
        log.info("Seeding database with development data...");

        String hash = passwordEncoder.encode("password123");

        // ── Users ──
        var admin = userRepo.save(UserEntity.builder()
            .id(UUID.fromString("a0000000-0000-0000-0000-000000000001"))
            .email("admin@dataofbusiness.in").passwordHash(hash).pan("ABCDE1234F")
            .fullName("Admin User").phone("9876543210")
            .role(UserEntity.UserRole.ADMIN).emailVerified(true).active(true)
            .createdAt(Instant.now()).updatedAt(Instant.now()).build());

        var member1 = userRepo.save(UserEntity.builder()
            .id(UUID.fromString("a0000000-0000-0000-0000-000000000002"))
            .email("member1@example.com").passwordHash(hash)
            .fullName("Rahul Sharma").phone("9876543211")
            .role(UserEntity.UserRole.RESEARCH_MEMBER).emailVerified(true).active(true)
            .createdAt(Instant.now()).updatedAt(Instant.now()).build());

        var member2 = userRepo.save(UserEntity.builder()
            .id(UUID.fromString("a0000000-0000-0000-0000-000000000003"))
            .email("member2@example.com").passwordHash(hash)
            .fullName("Priya Patel").phone("9876543212")
            .role(UserEntity.UserRole.RESEARCH_MEMBER).emailVerified(true).active(true)
            .createdAt(Instant.now()).updatedAt(Instant.now()).build());

        var companyUser = userRepo.save(UserEntity.builder()
            .id(UUID.fromString("a0000000-0000-0000-0000-000000000004"))
            .email("company@example.com").passwordHash(hash).pan("XYZAB1234C")
            .fullName("Vikram Singh").phone("9876543213")
            .role(UserEntity.UserRole.COMPANY_USER).emailVerified(true).active(true)
            .createdAt(Instant.now()).updatedAt(Instant.now()).build());

        var companyUser2 = userRepo.save(UserEntity.builder()
            .id(UUID.fromString("a0000000-0000-0000-0000-000000000006"))
            .email("greenenergy@example.com").passwordHash(hash).pan("GHIJK5678L")
            .fullName("Ananya Gupta").phone("9876543215")
            .role(UserEntity.UserRole.COMPANY_USER).emailVerified(true).active(true)
            .createdAt(Instant.now()).updatedAt(Instant.now()).build());

        var companyUser3 = userRepo.save(UserEntity.builder()
            .id(UUID.fromString("a0000000-0000-0000-0000-000000000007"))
            .email("bharatbiotech@example.com").passwordHash(hash).pan("MNOPQ9012R")
            .fullName("Dr. Sanjay Verma").phone("9876543216")
            .role(UserEntity.UserRole.COMPANY_USER).emailVerified(true).active(true)
            .createdAt(Instant.now()).updatedAt(Instant.now()).build());

        var companyUser4 = userRepo.save(UserEntity.builder()
            .id(UUID.fromString("a0000000-0000-0000-0000-000000000008"))
            .email("skyline@example.com").passwordHash(hash).pan("STUVW3456X")
            .fullName("Meera Krishnan").phone("9876543217")
            .role(UserEntity.UserRole.COMPANY_USER).emailVerified(true).active(true)
            .createdAt(Instant.now()).updatedAt(Instant.now()).build());

        var companyUser5 = userRepo.save(UserEntity.builder()
            .id(UUID.fromString("a0000000-0000-0000-0000-000000000009"))
            .email("nexgen@example.com").passwordHash(hash).pan("YZABC7890D")
            .fullName("Rohan Desai").phone("9876543218")
            .role(UserEntity.UserRole.COMPANY_USER).emailVerified(true).active(true)
            .createdAt(Instant.now()).updatedAt(Instant.now()).build());

        userRepo.save(UserEntity.builder()
            .id(UUID.fromString("a0000000-0000-0000-0000-000000000005"))
            .email("auditor@dataofbusiness.in").passwordHash(hash)
            .fullName("Auditor User").phone("9876543214")
            .role(UserEntity.UserRole.AUDITOR).emailVerified(true).active(true)
            .createdAt(Instant.now()).updatedAt(Instant.now()).build());

        log.info("Seeded 9 users (password: password123)");

        // ── Memberships ──
        membershipRepo.save(MembershipEntity.builder()
            .id(UUID.fromString("b0000000-0000-0000-0000-000000000001"))
            .userId(member1.getId()).planType("MONTHLY").status(MembershipEntity.MembershipStatus.ACTIVE)
            .startDate(LocalDate.now().minusDays(15)).endDate(LocalDate.now().plusDays(15))
            .downloadLimit(50).downloadsUsed(3).createdAt(Instant.now()).updatedAt(Instant.now()).build());

        membershipRepo.save(MembershipEntity.builder()
            .id(UUID.fromString("b0000000-0000-0000-0000-000000000002"))
            .userId(member2.getId()).planType("MONTHLY").status(MembershipEntity.MembershipStatus.ACTIVE)
            .startDate(LocalDate.now().minusDays(30)).endDate(LocalDate.now().plusDays(1))
            .downloadLimit(50).downloadsUsed(12).createdAt(Instant.now()).updatedAt(Instant.now()).build());

        log.info("Seeded memberships");

        // ── Companies ──
        var companyIdx = new int[]{0};

        // Named companies with rich profiles
        saveRichCompany(companyRepo, "c0000000-0000-0000-0000-000000000001",
            "TechVentures India Pvt Ltd", "Technology", "Maharashtra", "Mumbai", "Private Limited", 2015, companyUser.getId(),
            "Enterprise SaaS platform for supply chain management. Serving 500+ clients across India with AI-powered logistics optimization.",
            30, 60, 2, "APPROVED_ACTIVE",
            "TechVentures India Pvt Ltd", "TechVentures", "Information Technology", "Private Limited", 2015,
            "Revolutionizing supply chain management through AI-powered SaaS solutions", "To become India's leading supply chain intelligence platform powering 10,000+ enterprises by 2030",
            "Fast-paced, innovation-first culture with flat hierarchy. Hackathons every quarter, learning budget for every employee.",
            "Arjun Mehta", "Priya Kulkarni", "Arjun Mehta, Neha Singh", "B2B SaaS", "Growth",
            1200, "₹85 Cr", "₹12 Cr funded", "Sequoia Capital India, Accel Partners",
            "Supply Chain OS, Inventory Optimizer, Logistics Tracker, Demand Forecaster", "AI Consulting, Implementation, Training",
            "Python, Go, React, TensorFlow, PostgreSQL, Redis, Kubernetes, AWS", "ISO 9001, ISO 27001, SOC 2",
            "Best Enterprise SaaS 2024 (NASSCOM), Top 50 AI Companies 2023", TECH_INDUSTRY_CEOS, TECH_INDUSTRY_CTOS,
            "DOB-TV-IN-001", "TVPL/REG/2015/0421", "TechVentures HQ, 4th Floor, Galaxy Tower, BKC, Mumbai");

        saveRichCompany(companyRepo, "c0000000-0000-0000-0000-000000000002",
            "GreenEnergy Solutions Ltd", "Energy", "Gujarat", "Ahmedabad", "Public Limited", 2010, companyUser2.getId(),
            "Renewable energy company specializing in solar and wind power generation. Operating 200MW capacity across Gujarat and Rajasthan.",
            25, 55, 1, "APPROVED_ACTIVE",
            "GreenEnergy Solutions Ltd", "GreenEnergy", "Renewable Energy", "Public Limited", 2010,
            "Powering India's sustainable future with clean renewable energy", "To achieve 5GW installed renewable capacity by 2030 and become India's most trusted green energy provider",
            "Sustainability-driven culture with focus on innovation and community impact. Strong emphasis on employee well-being and green practices.",
            "Vikram Rathore", "Anjali Sharma", "Vikram Rathore", "B2B Energy Supply", "Enterprise",
            850, "₹320 Cr", "₹200 Cr funded", "Brookfield Renewable, IFC, State Bank of India",
            "Solar Power, Wind Power, Green Certificates, Power Purchase Agreements", "EPC Services, Maintenance, Energy Auditing",
            "SCADA, IoT Sensors, AI Predictive Maintenance, SAP, AWS",
            "ISO 9001, ISO 14001, ISO 45001, MNRE Certification",
            "Best Renewable Energy Company 2024, Golden Peacock Environment Award",
            ENERGY_CEOS, ENERGY_CTOS, "DOB-EN-IN-001", "GESL/REG/2010/0189", "GreenEnergy Tower, SG Highway, Ahmedabad, Gujarat");

        saveRichCompany(companyRepo, "c0000000-0000-0000-0000-000000000003",
            "Bharat Biotech Labs Pvt Ltd", "Healthcare", "Telangana", "Hyderabad", "Private Limited", 2008, companyUser3.getId(),
            "Biotechnology research and development company focused on vaccines and diagnostic solutions. ISO 13485 certified.",
            28, 58, 3, "APPROVED_ACTIVE",
            "Bharat Biotech Labs Pvt Ltd", "Bharat Biotech", "Biotechnology", "Private Limited", 2008,
            "Developing affordable life-saving vaccines and diagnostics for India and the world", "To make India self-reliant in vaccine manufacturing and achieve global leadership in biotech innovation",
            "Research-driven culture with world-class labs. Emphasis on scientific excellence, compliance, and patient impact.",
            "Dr. Rajesh Reddy", "Dr. Suman Rao", "Dr. Rajesh Reddy, Dr. Suman Rao, Dr. Prakash Nair", "B2G/B2B Biotech", "Enterprise",
            1800, "₹520 Cr", "₹450 Cr funded", "Krishna Institute, HDFC Bank, US-India VC Fund",
            "Vaccines, Diagnostic Kits, Therapeutics, Research Reagents", "Contract Research, Clinical Trials, Regulatory Consulting",
            "CRISPR, ELISA, PCR, Next-Gen Sequencing, AI Drug Discovery, LIMS",
            "ISO 13485, ISO 9001, WHO GMP, USFDA, CDSCO, DCGI, NABL",
            "National Biotech Award 2024, Best Vaccine Manufacturer 2023, Patents: 45+",
            HEALTHCARE_CEOS, HEALTHCARE_CTOS, "DOB-HL-IN-001", "BBL/REG/2008/0092", "Genome Valley, Shameerpet, Hyderabad, Telangana");

        saveRichCompany(companyRepo, "c0000000-0000-0000-0000-000000000004",
            "Indus Finance Corp Ltd", "Finance", "Maharashtra", "Mumbai", "Public Limited", 2005, companyUser4.getId(),
            "NBFC providing MSME lending, equipment financing and working capital solutions across 15 states.",
            20, 50, 4, "APPROVED_ACTIVE",
            "Indus Finance Corp Ltd", "Indus Finance", "Financial Services", "Public Limited", 2005,
            "Empowering Indian MSMEs with accessible and transparent financial solutions", "To be India's most trusted non-banking financial institution serving 1 million MSMEs",
            "Performance-driven with strong compliance culture. Meritocratic environment with focus on customer outcomes.",
            "Amit Khanna", null, "Amit Khanna, Sneha Agarwal", "B2B Lending", "Enterprise",
            3500, "₹890 Cr", "₹600 Cr funded", "ICICI Venture, CDC Group, Kotak Mahindra",
            "MSME Loans, Equipment Finance, Working Capital, Supply Chain Finance", "Financial Advisory, Credit Assessment, Insurance",
            "Core Banking System, AI Credit Scoring, Loan Management System, Salesforce, Tableau",
            "ISO 27001, RBI Registration, CIBIL Accredited, IRDAI Broker License",
            "Best NBFC 2024, MSME Empowerment Award 2023, 5-Star CRISIL Rating",
            FINANCE_CEOS, FINANCE_CTOS, "DOB-FN-IN-001", "IFCL/REG/2005/0332", "Indus House, Lower Parel, Mumbai, Maharashtra");

        saveRichCompany(companyRepo, "c0000000-0000-0000-0000-000000000005",
            "Skyline Realty Group LLP", "Real Estate", "Karnataka", "Bengaluru", "LLP", 2012, companyUser5.getId(),
            "Premium residential and commercial real estate developer. Delivered 25+ projects across Bangalore and Chennai.",
            22, 52, 5, "APPROVED_ACTIVE",
            "Skyline Realty Group LLP", "Skyline Realty", "Real Estate", "LLP", 2012,
            "Building premium living and working spaces that redefine urban landscapes", "To become India's most admired real estate brand known for design excellence and on-time delivery",
            "Design-centric culture with focus on craftsmanship and customer delight. RERA compliant with 100% on-time project delivery record.",
            "Karthik Iyer", null, "Karthik Iyer, Ranjit Menon", "B2C Real Estate", "Growth",
            450, "₹175 Cr", "₹85 Cr funded", "HDFC Capital, Kotak Realty Fund",
            "Premium Apartments, Villas, Commercial Spaces, Co-working", "Property Management, Interior Design, Facility Maintenance",
            "BIM, AutoCAD, Salesforce CRM, SAP, Project Management Suite",
            "RERA Registered, ISO 9001, CREDAI Member, IGBC Green Building",
            "Best Luxury Developer 2024, CREDAI Design Award 2023, Timely Delivery Excellence 2023",
            REALESTATE_CEOS, REALESTATE_CTOS, "DOB-RE-IN-001", "SRG/REG/2012/0156", "Skyline Tower, MG Road, Bengaluru, Karnataka");

        // Remaining 35 companies
        String[][] companyData = {
            {"c0000000-0000-0000-0000-000000000006", "Ocean Logistics Ltd", "Logistics", "Tamil Nadu", "Chennai", "Public Limited", "2007",
                "End-to-end logistics and supply chain solutions. Operates 3 major warehouses and a fleet of 200+ vehicles.", "18", "48", "6", "APPROVED_ACTIVE"},
            {"c0000000-0000-0000-0000-000000000007", "DigitalPay India Pvt Ltd", "Technology", "Karnataka", "Bengaluru", "Private Limited", "2018",
                "Digital payment gateway and fintech platform. Processing 500Cr+ monthly transaction volume.", "15", "45", "7", "APPROVED_ACTIVE"},
            {"c0000000-0000-0000-0000-000000000008", "AgriFresh Exports Pvt Ltd", "Agriculture", "Punjab", "Ludhiana", "Private Limited", "2013",
                "Agricultural produce exporter with APEDA certification. Exporting grains, spices and processed foods to 12 countries.", "12", "42", "8", "APPROVED_ACTIVE"},
            {"c0000000-0000-0000-0000-000000000009", "MediCare Hospitals Ltd", "Healthcare", "Maharashtra", "Pune", "Public Limited", "2003",
                "Multi-specialty hospital chain with 8 facilities across Western India. NABH accredited with 2000+ bed capacity.", "10", "40", "9", "APPROVED_ACTIVE"},
            {"c0000000-0000-0000-0000-000000000010", "SteelCraft Industries Ltd", "Manufacturing", "Odisha", "Rourkela", "Public Limited", "2000",
                "Steel fabrication and heavy engineering company. Supplies to major infrastructure projects.", "8", "38", "10", "APPROVED_ACTIVE"},
            {"c0000000-0000-0000-0000-000000000011", "CloudNine Technologies Pvt Ltd", "Technology", "Telangana", "Hyderabad", "Private Limited", "2016",
                "Cloud infrastructure and DevOps consulting. AWS Advanced Partner with 100+ successful migrations.", "6", "36", "11", "APPROVED_ACTIVE"},
            {"c0000000-0000-0000-0000-000000000012", "EduPrime Learning Pvt Ltd", "Education", "Delhi", "New Delhi", "Private Limited", "2017",
                "EdTech platform offering K-12 and competitive exam preparation. 500K+ active students.", "35", "65", "12", "APPROVED_ACTIVE"},
            {"c0000000-0000-0000-0000-000000000013", "SafeGuard Insurance Brokers Ltd", "Finance", "Maharashtra", "Mumbai", "Public Limited", "2009",
                "Insurance broking services for corporate and retail clients. IRDAI licensed with 50K+ policies.", "33", "63", "13", "APPROVED_ACTIVE"},
            {"c0000000-0000-0000-0000-000000000014", "FreshMart Retail Chain Pvt Ltd", "Retail", "Tamil Nadu", "Coimbatore", "Private Limited", "2014",
                "Organized retail chain for fresh groceries and household goods. 35+ stores in TN and Kerala.", "31", "61", "14", "APPROVED_ACTIVE"},
            {"c0000000-0000-0000-0000-000000000015", "AutoParts Manufacturing Pvt Ltd", "Manufacturing", "Haryana", "Gurugram", "Private Limited", "2011",
                "Automotive components manufacturer supplying to OEMs. ISO/TS 16949 certified.", "29", "59", "15", "APPROVED_ACTIVE"},
            {"c0000000-0000-0000-0000-000000000016", "Urban Infra Projects Ltd", "Infrastructure", "Maharashtra", "Mumbai", "Public Limited", "2006",
                "Infrastructure development company specializing in roads, bridges and metro rail projects.", "27", "57", "16", "APPROVED_ACTIVE"},
            {"c0000000-0000-0000-0000-000000000017", "BioAgri Sciences Pvt Ltd", "Agriculture", "Maharashtra", "Nashik", "Private Limited", "2016",
                "Bio-pesticides and organic fertilizer manufacturer. Serving 50K+ farmers.", "24", "54", "17", "APPROVED_ACTIVE"},
            {"c0000000-0000-0000-0000-000000000018", "CyberShield Security Pvt Ltd", "Technology", "Karnataka", "Bengaluru", "Private Limited", "2018",
                "Cybersecurity solutions provider offering VAPT, SOC and compliance. 200+ enterprise clients.", "21", "51", "18", "APPROVED_ACTIVE"},
            {"c0000000-0000-0000-0000-000000000019", "Golden Harvest Foods Pvt Ltd", "Food Processing", "Madhya Pradesh", "Indore", "Private Limited", "2010",
                "Processed food manufacturer with FSSAI certification. Exporting snacks and spices.", "19", "49", "19", "APPROVED_ACTIVE"},
            {"c0000000-0000-0000-0000-000000000020", "WestWind Textiles Ltd", "Textiles", "Gujarat", "Surat", "Public Limited", "2001",
                "Synthetic textile manufacturer with 500+ looms. Exporting to 20+ countries.", "17", "47", "20", "APPROVED_ACTIVE"},
            {"c0000000-0000-0000-0000-000000000021", "HealthFirst Diagnostics Ltd", "Healthcare", "Delhi", "New Delhi", "Public Limited", "2012",
                "Chain of diagnostic centres with NABL accreditation. 40+ centres across North India.", "14", "44", "21", "APPROVED_ACTIVE"},
            {"c0000000-0000-0000-0000-000000000022", "SolarMax Energy Pvt Ltd", "Energy", "Rajasthan", "Jaipur", "Private Limited", "2015",
                "Solar panel manufacturing and EPC services. 100MW installed capacity.", "11", "41", "22", "APPROVED_ACTIVE"},
            {"c0000000-0000-0000-0000-000000000023", "QuickShip Logistics LLP", "Logistics", "Maharashtra", "Pune", "LLP", "2019",
                "Last-mile delivery and e-commerce logistics. 50K+ packages daily across 30 cities.", "9", "39", "23", "APPROVED_ACTIVE"},
            {"c0000000-0000-0000-0000-000000000024", "Apex Chemicals Ltd", "Chemicals", "Gujarat", "Vadodara", "Public Limited", "1998",
                "Specialty chemicals manufacturer with 4 units. Exports to 25+ countries. ISO 9001:2015.", "7", "37", "24", "APPROVED_ACTIVE"},
            {"c0000000-0000-0000-0000-000000000025", "BuildRight Construction Pvt Ltd", "Real Estate", "Karnataka", "Bengaluru", "Private Limited", "2011",
                "Residential and commercial construction. Completed 15+ RERA registered premium projects.", "5", "35", "25", "APPROVED_ACTIVE"},
            {"c0000000-0000-0000-0000-000000000026", "DataMatrix Analytics Pvt Ltd", "Technology", "Telangana", "Hyderabad", "Private Limited", "2017",
                "Big data analytics and AI consulting. Custom ML solutions for healthcare, finance and retail.", "3", "33", "26", "APPROVED_ACTIVE"},
            {"c0000000-0000-0000-0000-000000000027", "Pristine Hotels & Resorts Ltd", "Hospitality", "Goa", "Panaji", "Public Limited", "2004",
                "Chain of boutique hotels across 8 tourist destinations. 600+ rooms. Award-winning.", "1", "31", "27", "APPROVED_ACTIVE"},
            {"c0000000-0000-0000-0000-000000000028", "EcoWaste Solutions Pvt Ltd", "Environment", "Maharashtra", "Navi Mumbai", "Private Limited", "2016",
                "Waste management and recycling. Processing 500 tonnes/day with 80% recovery rate.", "30", "30", "28", "APPROVED_ACTIVE"},
            {"c0000000-0000-0000-0000-000000000029", "BlueWave Aquatech Pvt Ltd", "Fisheries", "Kerala", "Kochi", "Private Limited", "2014",
                "Aquaculture and seafood processing. MPEDA approved with EU and Middle East exports.", "28", "28", "29", "APPROVED_ACTIVE"},
            {"c0000000-0000-0000-0000-000000000030", "NexGen Electronics Pvt Ltd", "Electronics", "Karnataka", "Bengaluru", "Private Limited", "2015",
                "Electronic manufacturing services (EMS). SMT assembly, PCB fabrication and product design.", "26", "26", "30", "APPROVED_ACTIVE"},
            {"c0000000-0000-0000-0000-000000000031", "Punjab Tractors & Equipment Ltd", "Manufacturing", "Punjab", "Mohali", "Public Limited", "2002",
                "Farm equipment manufacturer specializing in tractors and harvesters. 15% North India market share.", "5", "5", null, "PENDING_REVIEW"},
            {"c0000000-0000-0000-0000-000000000032", "Tranquil Pharma Pvt Ltd", "Pharmaceuticals", "Himachal Pradesh", "Baddi", "Private Limited", "2013",
                "Pharma manufacturing with WHO-GMP certified plant. 50+ generic drug formulations.", "3", "3", null, "PENDING_REVIEW"},
            {"c0000000-0000-0000-0000-000000000033", "NorthEast Tea Traders Pvt Ltd", "Tea", "Assam", "Guwahati", "Private Limited", "2009",
                "Premium tea plantation and export. 500 acres of tea gardens producing Orthodox and CTC varieties.", "20", "50", "1", "APPROVED_ACTIVE"},
            {"c0000000-0000-0000-0000-000000000034", "PrimeLegal Advisors LLP", "Legal", "Delhi", "New Delhi", "LLP", "2018",
                "Corporate law firm specializing in M&A, IP and commercial litigation. 25+ advocates.", "16", "46", "2", "APPROVED_ACTIVE"},
            {"c0000000-0000-0000-0000-000000000035", "GardenFresh Dairy Pvt Ltd", "Dairy", "Gujarat", "Anand", "Private Limited", "2012",
                "Milk and dairy products. 50K+ litre/day processing. Supplying fresh milk, curd, cheese and butter.", "14", "44", "3", "APPROVED_ACTIVE"},
            {"c0000000-0000-0000-0000-000000000036", "RubyStone Jewellery Ltd", "Jewellery", "Maharashtra", "Mumbai", "Public Limited", "2006",
                "Fine jewellery manufacturer and retailer. BIS hallmarked gold with 20+ showrooms.", "12", "42", "4", "APPROVED_ACTIVE"},
            {"c0000000-0000-0000-0000-000000000037", "DeltaTech Solutions Ltd", "Technology", "Tamil Nadu", "Chennai", "Public Limited", "2008",
                "IT services and consulting with 2000+ employees. Cloud, blockchain and enterprise apps.", "10", "40", "5", "APPROVED_ACTIVE"},
            {"c0000000-0000-0000-0000-000000000038", "Sapphire Media Networks Pvt Ltd", "Media", "Maharashtra", "Mumbai", "Private Limited", "2013",
                "Digital media and entertainment. 3 news portals and a YouTube network with 5M+ subscribers.", "8", "38", "6", "APPROVED_ACTIVE"},
            {"c0000000-0000-0000-0000-000000000039", "Himalayan Organics Pvt Ltd", "Agriculture", "Uttarakhand", "Dehradun", "Private Limited", "2017",
                "Organic farming and produce. Certified organic vegetables, pulses and spices.", "2", "10", "2", "REJECTED"},
            {"c0000000-0000-0000-0000-000000000040", "CrystalClear Waters Pvt Ltd", "Beverages", "Rajasthan", "Jodhpur", "Private Limited", "2019",
                "Packaged drinking water and beverage company. BIS and FSSAI certified with 5 bottling plants.", "1", "1", null, "PENDING_REVIEW"},
        };

        // Map of registered COMPANY_USER IDs to use as creators for all seeded companies
        var companyUserIds = List.of(
            companyUser.getId(),
            companyUser2.getId(),
            companyUser3.getId(),
            companyUser4.getId(),
            companyUser5.getId()
        );

        for (String[] cd : companyData) {
            String cid = cd[0], name = cd[1], sector = cd[2], state = cd[3], city = cd[4];
            String ctype = cd[5], yearStr = cd[6], desc = cd[7];
            int aDays = Integer.parseInt(cd[8]), cDays = Integer.parseInt(cd[9]);
            Integer uDays = cd[10] != null ? Integer.parseInt(cd[10]) : null;
            String status = cd[11];
            UUID creatorId = companyUserIds.get(companyIdx[0] % companyUserIds.size());
            companyIdx[0]++;

            int year = Integer.parseInt(yearStr);
            String brand = name.replaceAll("\\s+(Pvt Ltd|Ltd|LLP).*$", "");
            String stage = pickOne(COMPANY_STAGES);
            String dashStatus = pickOneForStatus(status);

            // Generate CEOs, CTOs for auto companies
            String ceo = pickOne(AUTO_CEOS);
            String cto = RND.nextBoolean() ? pickOne(AUTO_CTOS) : null;
            String founder = ceo + (RND.nextBoolean() ? ", " + pickOne(AUTO_FOUNDERS) : "");

            String hq = city + ", " + state;
            String website = "https://" + name.toLowerCase().replaceAll("[^a-z0-9]", "") + ".in";

            String products = generateProducts(sector);
            String services = generateServices(sector);
            String technologies = generateTechnologies(sector);
            String awards = generateAwards();
            String mission = "To deliver exceptional " + sector.toLowerCase() + " solutions that drive value for our clients and stakeholders";
            String vision = "To be a globally recognized " + sector.toLowerCase() + " leader known for innovation, quality, and trust";
            String culture = "Dynamic and inclusive workplace fostering innovation, collaboration, and continuous learning. Employee-first approach with strong emphasis on professional growth.";
            String turnover = (RND.nextInt(10, 500)) + " Cr";
            String funding = RND.nextBoolean() ? "₹" + RND.nextInt(5, 200) + " Cr funded" : null;
            String investors = RND.nextBoolean() ? pickOne(INVESTOR_NAMES) + (RND.nextBoolean() ? ", " + pickOne(INVESTOR_NAMES) : "") : null;

            String cin = "U" + RND.nextInt(10000, 99999) + sector.substring(0, 2).toUpperCase() + year + "PTC" + RND.nextInt(100000, 999999);
            String gstin = "27" + "ABCDE" + String.format("%04d", RND.nextInt(1000, 9999)) + "1Z" + RND.nextInt(10, 99);
            String pan = "ABCDE" + RND.nextInt(1000, 9999) + (RND.nextBoolean() ? "C" : "P");
            String regNum = String.format("%s/REG/%d/%04d", sector.substring(0, 3).toUpperCase(), year, RND.nextInt(100, 9999));
            String crn = brand.substring(0, Math.min(3, brand.length())).toUpperCase() + "/" + RND.nextInt(10000, 99999);

            String email = "contact@" + name.toLowerCase().replaceAll("[^a-z0-9]", "") + ".in";
            String phone = "+91 " + (RND.nextInt(60000, 99999) + "" + RND.nextInt(100000, 999999));
            String linkedin = "https://linkedin.com/company/" + name.toLowerCase().replaceAll("[^a-z0-9]", "");
            String twitter = "https://twitter.com/" + brand.toLowerCase().replaceAll("[^a-z0-9]", "");

            String finJson = generateFinancialDataJson(name, sector, year);
            String certJson = generateCertificatesJson(name, sector);
            String vidJson = generateVideosJson(name, sector);

            String dashboardStatus = dashStatus;

            int employeeCount = RND.nextInt(50, 2000);

            var company = CompanyEntity.builder()
                .id(UUID.fromString(cid)).name(name).sector(sector).state(state).city(city)
                .companyType(ctype).incorporationYear(year).description(desc)
                .website(website).logoUrl("https://images.unsplash.com/photo-1560179707-f14e90ef3623?w=200")
                .cin(cin).gstin(gstin).pan(pan)
                .companyRegistrationNumber(crn)
                .registeredAddressLine1(hq).registeredCity(city).registeredState(state).registeredPinCode(String.valueOf(RND.nextInt(100000, 999999))).registeredCountry("India")
                .officialEmail(email).officialPhone(phone).linkedinProfile(linkedin).twitterUrl(twitter).phoneNumber(phone).headquarter(hq)
                .annualTurnover(turnover).employeeCount(employeeCount).totalFunding(funding).investors(investors)
                .ceoName(ceo).ctoName(cto).founders(founder)
                .businessModel(pickOne(BUSINESS_MODELS)).companyStage(stage)
                .productsServices(products).technologiesUsed(technologies).certifications(generateCertOverview(sector)).awards(awards)
                .cultureSummary(culture).mission(mission).vision(vision)
                .dashboardStatus(dashboardStatus)
                .financialDataJson(finJson).certificatesDataJson(certJson).videosDataJson(vidJson)
                .status(CompanyEntity.CompanyStatus.valueOf(status))
                .createdBy(creatorId)
                .approvedBy("APPROVED_ACTIVE".equals(status) || "REJECTED".equals(status)
                    ? UUID.fromString("a0000000-0000-0000-0000-000000000001") : null)
                .approvedAt("APPROVED_ACTIVE".equals(status) || "REJECTED".equals(status)
                    ? Instant.now().minusSeconds(aDays * 86400L) : null)
                .submittedAt("APPROVED_ACTIVE".equals(status) || "PENDING_REVIEW".equals(status)
                    ? Instant.now().minusSeconds(cDays > 10 ? cDays * 86400L : 86400L) : null)
                .listingExpiresAt("APPROVED_ACTIVE".equals(status)
                    ? LocalDate.now().plusYears(1) : null)
                .createdAt(Instant.now().minusSeconds(cDays * 86400L))
                .updatedAt(uDays != null ? Instant.now().minusSeconds(uDays * 86400L) : Instant.now())
                .build();
            companyRepo.save(company);
        }

        log.info("Seeded 42 companies with rich profiles, financials, certificates & videos");

        // ── Additional workflow demo companies ──

        // Company in DRAFT status (not yet submitted)
        var draftCompany = CompanyEntity.builder()
            .id(UUID.fromString("c0000000-0000-0000-0000-000000000041"))
            .name("InnovateTech Solutions Pvt Ltd").sector("Technology").state("Karnataka").city("Bengaluru")
            .companyType("Private Limited").incorporationYear(2023)
            .description("AI-powered analytics platform for retail businesses")
            .website("https://innovatetech.in")
            .logoUrl("https://images.unsplash.com/photo-1560179707-f14e90ef3623?w=200")
            .status(CompanyEntity.CompanyStatus.DRAFT)
            .createdBy(companyUser.getId())
            .createdAt(Instant.now().minusSeconds(2 * 86400L))
            .updatedAt(Instant.now())
            .dashboardStatus("Draft")
            .build();
        companyRepo.save(draftCompany);

        // Company in APPROVED_MEMBERSHIP_PENDING status (approved but no listing membership)
        var approvedNoMembership = CompanyEntity.builder()
            .id(UUID.fromString("c0000000-0000-0000-0000-000000000042"))
            .name("FinFlow Capital Advisors Pvt Ltd").sector("Finance").state("Maharashtra").city("Mumbai")
            .companyType("Private Limited").incorporationYear(2019)
            .description("Boutique financial advisory firm specializing in M&A and fundraising for mid-market companies")
            .website("https://finflow.in")
            .logoUrl("https://images.unsplash.com/photo-1560179707-f14e90ef3623?w=200")
            .status(CompanyEntity.CompanyStatus.APPROVED_MEMBERSHIP_PENDING)
            .createdBy(companyUser2.getId())
            .approvedBy(UUID.fromString("a0000000-0000-0000-0000-000000000001"))
            .approvedAt(Instant.now().minusSeconds(5 * 86400L))
            .submittedAt(Instant.now().minusSeconds(10 * 86400L))
            .createdAt(Instant.now().minusSeconds(20 * 86400L))
            .updatedAt(Instant.now().minusSeconds(5 * 86400L))
            .dashboardStatus("Approved")
            .build();
        companyRepo.save(approvedNoMembership);

        log.info("Seeded 2 workflow demo companies (DRAFT + APPROVED_MEMBERSHIP_PENDING)");

        // ── Payments ──
        paymentRepo.save(PaymentEntity.builder()
            .id(UUID.fromString("d0000000-0000-0000-0000-000000000001"))
            .userId(member1.getId()).membershipId(UUID.fromString("b0000000-0000-0000-0000-000000000001"))
            .amount(new BigDecimal("2500.00")).gst(new BigDecimal("450.00")).total(new BigDecimal("2950.00"))
            .razorpayOrderId("order_mem_001").razorpayPaymentId("pay_mem_001").razorpaySignature("sig_mem_001")
            .status(PaymentEntity.PaymentStatus.PAID).paymentType(PaymentEntity.PaymentType.MEMBERSHIP)
            .createdAt(Instant.now()).build());

        paymentRepo.save(PaymentEntity.builder()
            .id(UUID.fromString("d0000000-0000-0000-0000-000000000002"))
            .userId(member2.getId()).membershipId(UUID.fromString("b0000000-0000-0000-0000-000000000002"))
            .amount(new BigDecimal("2500.00")).gst(new BigDecimal("450.00")).total(new BigDecimal("2950.00"))
            .razorpayOrderId("order_mem_002").razorpayPaymentId("pay_mem_002").razorpaySignature("sig_mem_002")
            .status(PaymentEntity.PaymentStatus.PAID).paymentType(PaymentEntity.PaymentType.MEMBERSHIP)
            .createdAt(Instant.now()).build());

        log.info("Seeded 2 payments");

        // ── Grievances ──
        grievanceRepo.save(GrievanceEntity.builder()
            .id(UUID.randomUUID()).userId(member1.getId())
            .complaintType("Data Discrepancy")
            .description("Found a mismatch in revenue figures for company TechVentures India. The 2024-25 reported revenue does not match the CA certificate.")
            .status(GrievanceEntity.GrievanceStatus.OPEN).priority(GrievanceEntity.GrievancePriority.HIGH)
            .createdAt(Instant.now()).updatedAt(Instant.now()).build());

        grievanceRepo.save(GrievanceEntity.builder()
            .id(UUID.randomUUID()).userId(member2.getId())
            .complaintType("Download Issue")
            .description("Unable to download financial statement for a company. The download button is not responding.")
            .status(GrievanceEntity.GrievanceStatus.IN_PROGRESS).priority(GrievanceEntity.GrievancePriority.MEDIUM)
            .createdAt(Instant.now()).updatedAt(Instant.now()).build());

        log.info("Seeded 2 grievances");

        log.info("✓ Database seeding complete! Login credentials:");
        log.info("  Admin:       admin@dataofbusiness.in / password123");
        log.info("  Member 1:    member1@example.com / password123");
        log.info("  Member 2:    member2@example.com / password123");
        log.info("  Company:     company@example.com / password123");
        log.info("  Auditor:     auditor@dataofbusiness.in / password123");
    }

    // ── Rich company helper for first 5 named companies ──

    private void saveRichCompany(CompanyJpaRepository repo, String id, String name, String sector, String state,
                                  String city, String companyType, int year, UUID companyUserId, String description,
                                  int approvedDaysAgo, int createdDaysAgo, Integer updatedDaysAgo, String status,
                                  String brandName, String displayName, String industry, String busType, int foundedYear,
                                  String mission, String vision, String culture, String ceo, String cto,
                                  String founders, String businessModel, String companyStage, int empCount,
                                  String turnover, String funding, String investors, String products, String services,
                                  String technologies, String certs, String awards,
                                  String[] ceoPool, String[] ctoPool,
                                  String dobId, String regNum, String hq) {
        Instant now = Instant.now();
        boolean approved = "APPROVED_ACTIVE".equals(status);
        boolean rejected = "REJECTED".equals(status);

        String email = "contact@" + name.toLowerCase().replaceAll("[^a-z0-9]", "") + ".in";
        String phone = "+91 " + (90000 + RND.nextInt(10000)) + "" + (RND.nextInt(10000));
        String linkedin = "https://linkedin.com/company/" + name.toLowerCase().replaceAll("[^a-z0-9]", "");
        String twitter = "https://twitter.com/" + brandName.toLowerCase().replaceAll("[^a-z0-9]", "");

        String cin = "U" + RND.nextInt(10000, 99999) + sector.substring(0, 2).toUpperCase() + year + "PTC" + RND.nextInt(100000, 999999);
        String gstin = "27" + "ABCDE" + String.format("%04d", RND.nextInt(1000, 9999)) + "1Z" + RND.nextInt(10, 99);
        String pan = "ABCDE" + RND.nextInt(1000, 9999) + (RND.nextBoolean() ? "C" : "P");
        String crn = brandName.substring(0, Math.min(3, brandName.length())).toUpperCase() + "/" + RND.nextInt(10000, 99999);

        String dashStatus = pickOneForStatus(status);
        String finJson = generateFinancialDataJson(name, sector, year);
        String certJson = generateCertificatesJson(name, sector);
        String vidJson = generateVideosJson(name, sector);

        var company = CompanyEntity.builder()
            .id(UUID.fromString(id)).name(name).sector(sector).state(state).city(city)
            .companyType(companyType).incorporationYear(year).description(description)
            .website("https://" + name.toLowerCase().replaceAll("[^a-z0-9]", "") + ".in")
            .logoUrl("https://images.unsplash.com/photo-1560179707-f14e90ef3623?w=200")
            .status(CompanyEntity.CompanyStatus.valueOf(status))
            .createdBy(companyUserId)
            .approvedBy(approved || rejected ? UUID.fromString("a0000000-0000-0000-0000-000000000001") : null)
            .approvedAt(approved || rejected ? now.minusSeconds(approvedDaysAgo * 86400L) : null)
            .submittedAt(approved || rejected ? now.minusSeconds(createdDaysAgo * 86400L) : null)
            .listingExpiresAt(approved ? LocalDate.now().plusYears(1) : null)
            .createdAt(now.minusSeconds(createdDaysAgo * 86400L))
            .updatedAt(updatedDaysAgo != null ? now.minusSeconds(updatedDaysAgo * 86400L) : now)
            // Registration
            .cin(cin).gstin(gstin).pan(pan).companyRegistrationNumber(crn)
            .registeredAddressLine1(hq).registeredCity(city).registeredState(state)
            .registeredPinCode(String.valueOf(RND.nextInt(100000, 999999))).registeredCountry("India")
            // Contact
            .officialEmail(email).officialPhone(phone).linkedinProfile(linkedin).twitterUrl(twitter)
            .phoneNumber(phone).headquarter(hq)
            // Financial
            .annualTurnover(turnover).employeeCount(empCount).totalFunding(funding).investors(investors)
            // Extended profile
            .ceoName(ceo).ctoName(cto).founders(founders)
            .businessModel(businessModel).companyStage(companyStage)
            .productsServices(products).technologiesUsed(technologies)
            .certifications(certs).awards(awards)
            .cultureSummary(culture).mission(mission).vision(vision)
            .dashboardStatus(dashStatus)
            // JSON aggregate data
            .financialDataJson(finJson).certificatesDataJson(certJson).videosDataJson(vidJson)
            .build();
        repo.save(company);
    }

    // ── Financial Data JSON Generator ──

    private String generateFinancialDataJson(String companyName, String sector, int foundedYear) {
        int currentYear = Year.now().getValue();
        var statements = new ArrayList<Map<String, Object>>();

        for (int offset = 0; offset < 3; offset++) {
            int fy = currentYear - offset;
            String fyLabel = "FY" + (fy - 1) + "-" + fy;

            // Generate realistic financials based on sector
            double baseRevenue = switch (sector) {
                case "Technology" -> RND.nextDouble(50, 500);
                case "Energy" -> RND.nextDouble(100, 1000);
                case "Healthcare" -> RND.nextDouble(30, 300);
                case "Finance" -> RND.nextDouble(100, 800);
                case "Real Estate" -> RND.nextDouble(40, 400);
                case "Manufacturing" -> RND.nextDouble(60, 600);
                case "Agriculture" -> RND.nextDouble(10, 100);
                case "Retail" -> RND.nextDouble(20, 200);
                default -> RND.nextDouble(15, 150);
            };

            // Earlier years have slightly lower revenue (growth)
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

            String fileSize = RND.nextInt(1, 10) + "." + RND.nextInt(1, 9) + " MB";
            String uploadDate = LocalDate.now().minusDays(RND.nextInt(1, 365)).toString();
            String status = RND.nextDouble() < 0.8 ? "Approved" : (RND.nextDouble() < 0.5 ? "Pending" : "Rejected");

            var yearData = new LinkedHashMap<String, Object>();
            yearData.put("financialYear", fyLabel);
            yearData.put("balanceSheetUrl", "https://storage.dataofbusiness.in/financials/" + companyName.toLowerCase().replaceAll("[^a-z0-9]", "") + "/BS_" + fyLabel + ".pdf");
            yearData.put("profitLossUrl", "https://storage.dataofbusiness.in/financials/" + companyName.toLowerCase().replaceAll("[^a-z0-9]", "") + "/PL_" + fyLabel + ".pdf");
            yearData.put("cashFlowUrl", "https://storage.dataofbusiness.in/financials/" + companyName.toLowerCase().replaceAll("[^a-z0-9]", "") + "/CF_" + fyLabel + ".pdf");
            yearData.put("auditorReportUrl", "https://storage.dataofbusiness.in/financials/" + companyName.toLowerCase().replaceAll("[^a-z0-9]", "") + "/AR_" + fyLabel + ".pdf");
            yearData.put("taxFilingUrl", "https://storage.dataofbusiness.in/financials/" + companyName.toLowerCase().replaceAll("[^a-z0-9]", "") + "/ITR_" + fyLabel + ".pdf");
            yearData.put("uploadDate", uploadDate);
            yearData.put("uploadedBy", pickOne(new String[]{"CA R. Sharma", "CA A. Gupta", "CA S. Patel", "CA P. Verma"}));
            yearData.put("status", status);
            yearData.put("version", "v1.0");
            yearData.put("fileSize", fileSize);
            yearData.put("fileType", "application/pdf");
            yearData.put("downloadUrl", "https://storage.dataofbusiness.in/downloads/" + fyLabel + "/" + companyName.toLowerCase().replaceAll("[^a-z0-9]", ""));
            yearData.put("revenue", Math.round(revenue * 100) / 100.0);
            yearData.put("expenses", Math.round(expenses * 100) / 100.0);
            yearData.put("ebitda", Math.round(ebitda * 100) / 100.0);
            yearData.put("netProfit", Math.round(netProfit * 100) / 100.0);
            yearData.put("assets", Math.round(assets * 100) / 100.0);
            yearData.put("liabilities", Math.round(liabilities * 100) / 100.0);
            yearData.put("equity", Math.round(equity * 100) / 100.0);
            yearData.put("operatingCashFlow", Math.round(opCashFlow * 100) / 100.0);
            yearData.put("capex", Math.round(capex * 100) / 100.0);
            yearData.put("debt", Math.round(debt * 100) / 100.0);
            yearData.put("verified", RND.nextDouble() < 0.8);
            statements.add(yearData);
        }
        return toJson(statements);
    }

    // ── Certificates JSON Generator ──

    private String generateCertificatesJson(String companyName, String sector) {
        int numCerts = RND.nextInt(8, 16);
        var certs = new ArrayList<Map<String, Object>>();
        var usedTypes = new HashSet<String>();

        String[][] allCertTypes = {
            {"ISO 9001:2015", "BIS", "Quality Management System certification for " + sector.toLowerCase() + " operations"},
            {"ISO 27001:2022", "BIS", "Information Security Management System certification"},
            {"ISO 14001:2015", "BIS", "Environmental Management System certification"},
            {"ISO 45001:2018", "BIS", "Occupational Health and Safety Management System"},
            {"SOC 2 Type II", "AICPA", "Security, availability, and confidentiality controls"},
            {"PCI DSS", "PCI Security Council", "Payment card industry data security standard"},
            {"CE Marking", "European Commission", "European conformity for products"},
            {"FDA Registration", "US FDA", "US Food and Drug Administration registration"},
            {"Startup India", "DPIIT", "Department for Promotion of Industry and Internal Trade recognition"},
            {"MSME Registration", "Ministry of MSME", "Micro, Small and Medium Enterprises registration"},
            {"GST Registration", "GST Council", "Goods and Services Tax registration"},
            {"Import Export Code", "DGFT", "Directorate General of Foreign Trade code"},
            {"Trademark Certificate", "IPO India", "Registered trademark protection"},
            {"Patent Certificate", "IPO India", "Granted patent for innovation"},
            {"Business License", "Municipal Corporation", "Local business operation license"},
            {"RERA Registration", "RERA Authority", "Real Estate Regulatory Authority registration"},
            {"FSSAI License", "FSSAI", "Food safety and standards license"},
            {"ISO 13485:2016", "BIS", "Medical devices quality management system"},
            {"WHO GMP", "WHO", "Good Manufacturing Practices certification"},
            {"NABL Accreditation", "NABL", "National Accreditation Board for Testing and Calibration"},
            {"APEDA Registration", "APEDA", "Agricultural export certification"},
            {"BIS Hallmark", "BIS", "Hallmarking certification for jewellery and precious metals"},
            {"IRDAI License", "IRDAI", "Insurance Regulatory and Development Authority license"},
            {"CRISIL Rating", "CRISIL", "Credit rating certification"},
        };

        // First add sector-appropriate certs
        String[][] sectorCerts = switch (sector) {
            case "Healthcare", "Pharmaceuticals" -> new String[][]{{"ISO 13485:2016"}, {"WHO GMP"}, {"FDA Registration"}, {"CDSCO"}, {"NABL Accreditation"}};
            case "Technology" -> new String[][]{{"ISO 27001:2022"}, {"SOC 2 Type II"}, {"Startup India"}, {"ISO 9001:2015"}};
            case "Finance" -> new String[][]{{"ISO 27001:2022"}, {"IRDAI License"}, {"CRISIL Rating"}, {"PCI DSS"}};
            case "Food Processing", "Beverages", "Dairy" -> new String[][]{{"FSSAI License"}, {"ISO 22000"}, {"ISO 9001:2015"}, {"APEDA Registration"}};
            case "Real Estate" -> new String[][]{{"RERA Registration"}, {"ISO 9001:2015"}, {"IGBC Green Building"}, {"CREDAI Member"}};
            case "Manufacturing" -> new String[][]{{"ISO 9001:2015"}, {"ISO 14001:2015"}, {"CE Marking"}, {"MSME Registration"}};
            case "Agriculture" -> new String[][]{{"APEDA Registration"}, {"Organic Certification"}, {"ISO 9001:2015"}, {"MSME Registration"}};
            case "Energy" -> new String[][]{{"ISO 14001:2015"}, {"ISO 45001:2018"}, {"MNRE Certification"}, {"ISO 9001:2015"}};
            default -> new String[][]{{"ISO 9001:2015"}, {"MSME Registration"}, {"GST Registration"}, {"Business License"}};
        };

        for (var sc : sectorCerts) {
            Map<String, Object> cert = buildCert(sc[0], companyName);
            certs.add(cert);
            usedTypes.add(sc[0]);
        }

        // Fill remaining with random certs
        while (certs.size() < numCerts) {
            var ct = allCertTypes[RND.nextInt(allCertTypes.length)];
            if (usedTypes.add(ct[0])) {
                certs.add(buildCert(ct[0], companyName));
            }
        }

        return toJson(certs);
    }

    private Map<String, Object> buildCert(String certName, String companyName) {
        String prefix = companyName.toLowerCase().replaceAll("[^a-z0-9]", "").substring(0, Math.min(5, companyName.length()));
        Instant issueDate = Instant.now().minusSeconds(RND.nextLong(30, 1500) * 86400L);
        Instant expiryDate = Instant.now().plusSeconds(RND.nextLong(30, 1500) * 86400L);
        boolean active = RND.nextDouble() < 0.85;

        var cert = new LinkedHashMap<String, Object>();
        cert.put("certificateName", certName);
        cert.put("certificateNumber", certName.substring(0, Math.min(4, certName.length())).toUpperCase() + "-" + RND.nextInt(100000, 999999));
        cert.put("issuingAuthority", pickOne(new String[]{"BIS", "ISO", "AICPA", "DPIIT", "US FDA", "WHO", "FSSAI", "NABL", "DGFT", "IPO India"}));
        cert.put("issueDate", java.time.Instant.now().minusSeconds(RND.nextLong(30, 1500) * 86400L).toString());
        cert.put("expiryDate", active ? java.time.Instant.now().plusSeconds(RND.nextLong(30, 1500) * 86400L).toString() : java.time.Instant.now().minusSeconds(RND.nextLong(1, 100) * 86400L).toString());
        cert.put("status", active ? "Active" : "Expired");
        cert.put("verificationUrl", "https://verify.dataofbusiness.in/certificates/" + prefix + "/" + certName.toLowerCase().replaceAll("[^a-z0-9]", ""));
        cert.put("pdfUrl", "https://storage.dataofbusiness.in/certificates/" + prefix + "/" + certName.toLowerCase().replaceAll("[^a-z0-9]", "") + ".pdf");
        cert.put("thumbnailUrl", "https://images.unsplash.com/photo-1589829085413-56de8ae18c73?w=100");
        cert.put("description", certName + " certification for " + companyName + ".");
        cert.put("verified", RND.nextDouble() < 0.85);
        return cert;
    }

    // ── Videos JSON Generator ──

    private String generateVideosJson(String companyName, String sector) {
        int numVideos = RND.nextInt(3, 9);
        var videos = new ArrayList<Map<String, Object>>();

        String[][] categories = {
            {"Company Introduction", "A brief introduction to " + companyName + " and our journey"},
            {"Product Demo", "Comprehensive product demonstration showcasing key features and benefits"},
            {"Investor Pitch", "Investment opportunity deck and growth story for potential investors"},
            {"Customer Success Story", "How we helped our clients achieve their business goals"},
            {"Manufacturing Tour", "Behind the scenes look at our manufacturing facilities"},
            {"Technology Overview", "Deep dive into our technology stack and architecture"},
            {"Sustainability", "Our commitment to environmental sustainability and green practices"},
            {"Recruitment", "Life at " + companyName + " — culture, values, and career opportunities"},
        };

        for (int i = 0; i < numVideos && i < categories.length; i++) {
            var cat = categories[i];
            String prefix = companyName.toLowerCase().replaceAll("[^a-z0-9]", "");
            int duration = RND.nextInt(60, 600);
            int views = RND.nextInt(1000, 100000);
            int likes = RND.nextInt(50, views / 10);

            var video = new LinkedHashMap<String, Object>();
            video.put("title", companyName + " — " + cat[0]);
            video.put("description", cat[1]);
            video.put("duration", (duration / 60) + ":" + String.format("%02d", duration % 60));
            video.put("videoUrl", "https://storage.dataofbusiness.in/videos/" + prefix + "/" + cat[0].toLowerCase().replaceAll("[^a-z0-9]", "") + ".mp4");
            video.put("thumbnailUrl", "https://images.unsplash.com/photo-1536240478700-b869070f9279?w=400");
            video.put("uploadDate", LocalDate.now().minusDays(RND.nextInt(1, 365)).toString());
            video.put("category", cat[0]);
            video.put("views", views);
            video.put("likes", likes);
            int maxComments = Math.max(11, likes / 5);
            int maxShares = Math.max(6, likes / 10);
            video.put("comments", RND.nextInt(10, maxComments));
            video.put("shares", RND.nextInt(5, maxShares));
            video.put("language", RND.nextDouble() < 0.7 ? "English" : "Hindi");
            video.put("resolution", "1920x1080");
            video.put("status", "Published");
            video.put("transcriptSummary", cat[1] + ". Key highlights include our unique approach, market impact, and future roadmap.");
            video.put("speaker", pickOne(new String[]{"CEO", "CTO", "Founder", "VP Marketing", "Head of Product"}));
            video.put("durationSeconds", duration);
            videos.add(video);
        }
        return toJson(videos);
    }

    // ── Helper methods ──

    private String pickOne(String[] arr) {
        return arr[RND.nextInt(arr.length)];
    }

    private String pickOneForStatus(String companyStatus) {
        return switch (companyStatus) {
            case "APPROVED_ACTIVE" -> RND.nextDouble() < 0.6 ? "Approved" : (RND.nextDouble() < 0.5 ? "Under Review" : "Needs Changes");
            case "APPROVED_MEMBERSHIP_PENDING" -> "Approved";
            case "PENDING_REVIEW" -> "Pending";
            case "REJECTED" -> "Rejected";
            case "DRAFT" -> "Draft";
            case "MEMBERSHIP_EXPIRED" -> "Expired";
            case "SUSPENDED" -> "Suspended";
            default -> "Pending";
        };
    }

    private String generateProducts(String sector) {
        return switch (sector) {
            case "Technology" -> pickOne(new String[]{"Cloud Platform, SaaS Suite, API Gateway", "Analytics Dashboard, Mobile SDK, Data Pipeline", "AI Assistant, Automation Tools, Dev Platform"});
            case "Healthcare" -> pickOne(new String[]{"Diagnostic Kit, Medical Device, Health Monitor", "Vaccine, Therapeutics, Test Kit", "Hospital Software, Patient Portal, EHR System"});
            case "Finance" -> pickOne(new String[]{"Loan Product, Insurance Plan, Investment Fund", "Payment Gateway, Wallet, Credit Card", "SME Loan, Working Capital, Equipment Finance"});
            case "Manufacturing" -> pickOne(new String[]{"Industrial Equipment, Components, Spare Parts", "Consumer Goods, Machinery, Tools", "Steel Products, Fabricated Parts, Assemblies"});
            case "Agriculture" -> pickOne(new String[]{"Organic Grains, Spices, Processed Foods", "Fertilizers, Pesticides, Seeds", "Fresh Produce, Dairy Products, Pulses"});
            default -> pickOne(new String[]{"Core Product Line, Premium Range, Enterprise Solutions", "Standard Products, Custom Solutions, Accessories"});
        };
    }

    private String generateServices(String sector) {
        return switch (sector) {
            case "Technology" -> pickOne(new String[]{"Cloud Consulting, DevOps, Managed IT", "AI/ML Consulting, Data Analytics, Training", "Cybersecurity Audit, Compliance, SOC Services"});
            case "Healthcare" -> pickOne(new String[]{"Diagnostic Services, Health Checkups, Lab Testing", "Clinical Trials, Regulatory Consulting, R&D", "Telemedicine, Home Healthcare, Pharmacy"});
            case "Finance" -> pickOne(new String[]{"Financial Advisory, Credit Assessment, Insurance", "Wealth Management, Tax Planning, Audit", "Risk Assessment, Portfolio Management, Underwriting"});
            case "Manufacturing" -> pickOne(new String[]{"Maintenance Services, Installation, Training", "Design Services, Prototyping, Quality Testing", "Logistics Support, After-Sales, Technical Support"});
            default -> pickOne(new String[]{"Consulting, Training, Support", "Maintenance, Installation, After-Sales Service"});
        };
    }

    private String generateTechnologies(String sector) {
        return switch (sector) {
            case "Technology" -> pickOne(new String[]{"Python, Go, React, Node.js, PostgreSQL, Kubernetes, AWS, TensorFlow",
                "Java, Spring Boot, Angular, Docker, Redis, GCP, PyTorch, Kafka",
                "TypeScript, Next.js, GraphQL, MongoDB, Terraform, Azure, MLflow"});
            case "Healthcare" -> pickOne(new String[]{"CRISPR, ELISA, PCR, NGS, LIMS, AI Diagnostics, Cloud Platform",
                "Bioreactors, Spectrometry, HPLC, Cell Culture, Bioinformatics, IoT"});
            case "Finance" -> pickOne(new String[]{"Core Banking System, AI Credit Scoring, Loan Management, Salesforce, Tableau",
                "Payment Gateway, Fraud Detection, Risk Engine, Blockchain, Analytics"});
            case "Manufacturing" -> pickOne(new String[]{"SCADA, IoT Sensors, ERP, SAP, AutoCAD, PLC, MES, AI Quality Control",
                "CNC, Robotics, CAD/CAM, PLM, SCM, Industry 4.0, Digital Twin"});
            default -> pickOne(new String[]{"ERP, CRM, IoT, Cloud Infrastructure, Data Analytics, AI/ML",
                "Automation Tools, Monitoring Systems, Quality Management Software"});
        };
    }

    private String generateCertOverview(String sector) {
        return switch (sector) {
            case "Technology" -> "ISO 27001, SOC 2, ISO 9001";
            case "Healthcare" -> "ISO 13485, WHO GMP, FDA, NABL, GLP";
            case "Finance" -> "ISO 27001, PCI DSS, IRDAI, CRISIL";
            case "Manufacturing" -> "ISO 9001, ISO 14001, ISO 45001, IATF 16949";
            case "Food Processing", "Beverages" -> "FSSAI, ISO 22000, BRC, APEDA";
            case "Real Estate" -> "RERA, ISO 9001, IGBC, CREDAI";
            case "Agriculture" -> "APEDA, Organic Certification, ISO 9001, GLOBALG.A.P";
            default -> "ISO 9001, ISO 14001, MSME, GST";
        };
    }

    private String generateAwards() {
        return pickOne(new String[]{
            "Best in Industry 2024, Innovation Award 2023, Customer Excellence 2023",
            "National Excellence Award 2024, Emerging Leader 2023, Quality Award 2023",
            "Industry Leader 2024, Top Employer 2023, Sustainability Award 2023",
            "Golden Trophy 2024, Best Brand 2023, Service Excellence 2023"
        });
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    // ── Data pools ──

    private static final String[] TECH_INDUSTRY_CEOS = {"Arjun Mehta", "Rahul Sharma", "Vikram Singh", "Neha Patel"};
    private static final String[] TECH_INDUSTRY_CTOS = {"Priya Kulkarni", "Suresh Reddy", "Amit Joshi", "Deepa Nair"};
    private static final String[] ENERGY_CEOS = {"Vikram Rathore", "Sunita Devi", "Rajesh Kumar", "Amitabh Sinha"};
    private static final String[] ENERGY_CTOS = {"Anjali Sharma", "Prakash Rao", "Meena Iyer", "Sandeep Gupta"};
    private static final String[] HEALTHCARE_CEOS = {"Dr. Rajesh Reddy", "Dr. Meera Krishnan", "Dr. Sanjay Verma", "Dr. Anita Desai"};
    private static final String[] HEALTHCARE_CTOS = {"Dr. Suman Rao", "Dr. Karthik Iyer", "Dr. Nandini Gupta", "Dr. Rohan Menon"};
    private static final String[] FINANCE_CEOS = {"Amit Khanna", "Sneha Agarwal", "Vivek Jain", "Pooja Malhotra"};
    private static final String[] FINANCE_CTOS = {"Ravi Shankar", "Kavita Singh", "Ankur Verma", "Nisha Gupta"};
    private static final String[] REALESTATE_CEOS = {"Karthik Iyer", "Ranjit Menon", "Deepa Nair", "Mohan Raj"};
    private static final String[] REALESTATE_CTOS = {"Suresh Babu", "Lakshmi Narayan", "Arun Prasad", "Divya Krishnan"};

    private static final String[] AUTO_CEOS = {
        "Rajesh Kumar", "Anita Sharma", "Suresh Reddy", "Meena Gupta", "Amit Verma",
        "Pooja Singh", "Vivek Malhotra", "Deepa Iyer", "Rohan Joshi", "Nandini Rao",
        "Arun Prakash", "Kavita Nair", "Manish Agarwal", "Shweta Patel", "Vikas Desai"
    };
    private static final String[] AUTO_CTOS = {
        "Sandeep Rao", "Anjali Mehta", "Prakash Sinha", "Kiran Verma", "Ravi Kumar",
        "Neha Gupta", "Ashok Reddy", "Divya Sharma", "Mohan Iyer", "Sonia Patel"
    };
    private static final String[] AUTO_FOUNDERS = {
        "Rajesh Kumar", "Anita Sharma", "Suresh Reddy", "Meena Gupta", "Amit Verma",
        "Pooja Singh", "Vivek Malhotra", "Deepa Iyer", "Rohan Joshi", "Nandini Rao"
    };
    private static final String[] INVESTOR_NAMES = {
        "Sequoia Capital India", "Accel Partners", "Elevation Capital", "Peak XV Partners",
        "Matrix Partners India", "Nexus Venture Partners", "Blume Ventures",
        "Tiger Global", "SoftBank Vision Fund", "Kalaari Capital",
        "ICICI Venture", "HDFC Capital", "Kotak Private Equity", "State Bank of India",
        "IFC", "CDC Group", "Brookfield Asset Management"
    };
}
