package com.dob.infrastructure.config;

import com.dob.infrastructure.persistence.entity.*;
import com.dob.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.UUID;

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

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepo.count() > 0) {
            log.info("Database already seeded — skipping");
            return;
        }
        log.info("Seeding database with development data...");

        // ── Users ──
        String hash = passwordEncoder.encode("password123");

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

        userRepo.save(UserEntity.builder()
            .id(UUID.fromString("a0000000-0000-0000-0000-000000000005"))
            .email("auditor@dataofbusiness.in").passwordHash(hash)
            .fullName("Auditor User").phone("9876543214")
            .role(UserEntity.UserRole.AUDITOR).emailVerified(true).active(true)
            .createdAt(Instant.now()).updatedAt(Instant.now()).build());

        log.info("Seeded 5 users (password: password123)");

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
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000001", "TechVentures India Pvt Ltd", "Technology", "Maharashtra", "Mumbai", "Private Limited", 2015,
            "Enterprise SaaS platform for supply chain management. Serving 500+ clients across India with AI-powered logistics optimization.", companyUser.getId(), 30, 60, 2, "APPROVED"));
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000002", "GreenEnergy Solutions Ltd", "Energy", "Gujarat", "Ahmedabad", "Public Limited", 2010,
            "Renewable energy company specializing in solar and wind power generation. Operating 200MW capacity across Gujarat and Rajasthan.", companyUser.getId(), 25, 55, 1, "APPROVED"));
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000003", "Bharat Biotech Labs Pvt Ltd", "Healthcare", "Telangana", "Hyderabad", "Private Limited", 2008,
            "Biotechnology research and development company focused on vaccines and diagnostic solutions. ISO 13485 certified.", companyUser.getId(), 28, 58, 3, "APPROVED"));
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000004", "Indus Finance Corp Ltd", "Finance", "Maharashtra", "Mumbai", "Public Limited", 2005,
            "NBFC providing MSME lending, equipment financing and working capital solutions across 15 states.", companyUser.getId(), 20, 50, 4, "APPROVED"));
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000005", "Skyline Realty Group LLP", "Real Estate", "Karnataka", "Bengaluru", "LLP", 2012,
            "Premium residential and commercial real estate developer. Delivered 25+ projects across Bangalore and Chennai.", companyUser.getId(), 22, 52, 5, "APPROVED"));
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000006", "Ocean Logistics Ltd", "Logistics", "Tamil Nadu", "Chennai", "Public Limited", 2007,
            "End-to-end logistics and supply chain solutions. Operates 3 major warehouses and a fleet of 200+ vehicles.", companyUser.getId(), 18, 48, 6, "APPROVED"));
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000007", "DigitalPay India Pvt Ltd", "Technology", "Karnataka", "Bengaluru", "Private Limited", 2018,
            "Digital payment gateway and fintech platform. Processing 500Cr+ monthly transaction volume.", companyUser.getId(), 15, 45, 7, "APPROVED"));
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000008", "AgriFresh Exports Pvt Ltd", "Agriculture", "Punjab", "Ludhiana", "Private Limited", 2013,
            "Agricultural produce exporter with APEDA certification. Exporting grains, spices and processed foods to 12 countries.", companyUser.getId(), 12, 42, 8, "APPROVED"));
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000009", "MediCare Hospitals Ltd", "Healthcare", "Maharashtra", "Pune", "Public Limited", 2003,
            "Multi-specialty hospital chain with 8 facilities across Western India. NABH accredited with 2000+ bed capacity.", companyUser.getId(), 10, 40, 9, "APPROVED"));
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000010", "SteelCraft Industries Ltd", "Manufacturing", "Odisha", "Rourkela", "Public Limited", 2000,
            "Steel fabrication and heavy engineering company. Supplies to major infrastructure projects.", companyUser.getId(), 8, 38, 10, "APPROVED"));
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000011", "CloudNine Technologies Pvt Ltd", "Technology", "Telangana", "Hyderabad", "Private Limited", 2016,
            "Cloud infrastructure and DevOps consulting. AWS Advanced Partner with 100+ successful migrations.", companyUser.getId(), 6, 36, 11, "APPROVED"));
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000012", "EduPrime Learning Pvt Ltd", "Education", "Delhi", "New Delhi", "Private Limited", 2017,
            "EdTech platform offering K-12 and competitive exam preparation. 500K+ active students.", companyUser.getId(), 35, 65, 12, "APPROVED"));
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000013", "SafeGuard Insurance Brokers Ltd", "Finance", "Maharashtra", "Mumbai", "Public Limited", 2009,
            "Insurance broking services for corporate and retail clients. IRDAI licensed with 50K+ policies.", companyUser.getId(), 33, 63, 13, "APPROVED"));
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000014", "FreshMart Retail Chain Pvt Ltd", "Retail", "Tamil Nadu", "Coimbatore", "Private Limited", 2014,
            "Organized retail chain for fresh groceries and household goods. 35+ stores in TN and Kerala.", companyUser.getId(), 31, 61, 14, "APPROVED"));
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000015", "AutoParts Manufacturing Pvt Ltd", "Manufacturing", "Haryana", "Gurugram", "Private Limited", 2011,
            "Automotive components manufacturer supplying to OEMs. ISO/TS 16949 certified.", companyUser.getId(), 29, 59, 15, "APPROVED"));
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000016", "Urban Infra Projects Ltd", "Infrastructure", "Maharashtra", "Mumbai", "Public Limited", 2006,
            "Infrastructure development company specializing in roads, bridges and metro rail projects.", companyUser.getId(), 27, 57, 16, "APPROVED"));
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000017", "BioAgri Sciences Pvt Ltd", "Agriculture", "Maharashtra", "Nashik", "Private Limited", 2016,
            "Bio-pesticides and organic fertilizer manufacturer. Serving 50K+ farmers.", companyUser.getId(), 24, 54, 17, "APPROVED"));
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000018", "CyberShield Security Pvt Ltd", "Technology", "Karnataka", "Bengaluru", "Private Limited", 2018,
            "Cybersecurity solutions provider offering VAPT, SOC and compliance. 200+ enterprise clients.", companyUser.getId(), 21, 51, 18, "APPROVED"));
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000019", "Golden Harvest Foods Pvt Ltd", "Food Processing", "Madhya Pradesh", "Indore", "Private Limited", 2010,
            "Processed food manufacturer with FSSAI certification. Exporting snacks and spices.", companyUser.getId(), 19, 49, 19, "APPROVED"));
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000020", "WestWind Textiles Ltd", "Textiles", "Gujarat", "Surat", "Public Limited", 2001,
            "Synthetic textile manufacturer with 500+ looms. Exporting to 20+ countries.", companyUser.getId(), 17, 47, 20, "APPROVED"));
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000021", "HealthFirst Diagnostics Ltd", "Healthcare", "Delhi", "New Delhi", "Public Limited", 2012,
            "Chain of diagnostic centres with NABL accreditation. 40+ centres across North India.", companyUser.getId(), 14, 44, 21, "APPROVED"));
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000022", "SolarMax Energy Pvt Ltd", "Energy", "Rajasthan", "Jaipur", "Private Limited", 2015,
            "Solar panel manufacturing and EPC services. 100MW installed capacity.", companyUser.getId(), 11, 41, 22, "APPROVED"));
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000023", "QuickShip Logistics LLP", "Logistics", "Maharashtra", "Pune", "LLP", 2019,
            "Last-mile delivery and e-commerce logistics. 50K+ packages daily across 30 cities.", companyUser.getId(), 9, 39, 23, "APPROVED"));
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000024", "Apex Chemicals Ltd", "Chemicals", "Gujarat", "Vadodara", "Public Limited", 1998,
            "Specialty chemicals manufacturer with 4 units. Exports to 25+ countries. ISO 9001:2015.", companyUser.getId(), 7, 37, 24, "APPROVED"));
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000025", "BuildRight Construction Pvt Ltd", "Real Estate", "Karnataka", "Bengaluru", "Private Limited", 2011,
            "Residential and commercial construction. Completed 15+ RERA registered premium projects.", companyUser.getId(), 5, 35, 25, "APPROVED"));
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000026", "DataMatrix Analytics Pvt Ltd", "Technology", "Telangana", "Hyderabad", "Private Limited", 2017,
            "Big data analytics and AI consulting. Custom ML solutions for healthcare, finance and retail.", companyUser.getId(), 3, 33, 26, "APPROVED"));
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000027", "Pristine Hotels & Resorts Ltd", "Hospitality", "Goa", "Panaji", "Public Limited", 2004,
            "Chain of boutique hotels across 8 tourist destinations. 600+ rooms. Award-winning.", companyUser.getId(), 1, 31, 27, "APPROVED"));
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000028", "EcoWaste Solutions Pvt Ltd", "Environment", "Maharashtra", "Navi Mumbai", "Private Limited", 2016,
            "Waste management and recycling. Processing 500 tonnes/day with 80% recovery rate.", companyUser.getId(), 30, 30, 28, "APPROVED"));
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000029", "BlueWave Aquatech Pvt Ltd", "Fisheries", "Kerala", "Kochi", "Private Limited", 2014,
            "Aquaculture and seafood processing. MPEDA approved with EU and Middle East exports.", companyUser.getId(), 28, 28, 29, "APPROVED"));
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000030", "NexGen Electronics Pvt Ltd", "Electronics", "Karnataka", "Bengaluru", "Private Limited", 2015,
            "Electronic manufacturing services (EMS). SMT assembly, PCB fabrication and product design.", companyUser.getId(), 26, 26, 30, "APPROVED"));
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000031", "Punjab Tractors & Equipment Ltd", "Manufacturing", "Punjab", "Mohali", "Public Limited", 2002,
            "Farm equipment manufacturer specializing in tractors and harvesters. 15% North India market share.", companyUser.getId(), 5, 5, null, "PENDING"));
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000032", "Tranquil Pharma Pvt Ltd", "Pharmaceuticals", "Himachal Pradesh", "Baddi", "Private Limited", 2013,
            "Pharma manufacturing with WHO-GMP certified plant. 50+ generic drug formulations.", companyUser.getId(), 3, 3, null, "PENDING"));
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000033", "NorthEast Tea Traders Pvt Ltd", "Tea", "Assam", "Guwahati", "Private Limited", 2009,
            "Premium tea plantation and export. 500 acres of tea gardens producing Orthodox and CTC varieties.", companyUser.getId(), 20, 50, 1, "APPROVED"));
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000034", "PrimeLegal Advisors LLP", "Legal", "Delhi", "New Delhi", "LLP", 2018,
            "Corporate law firm specializing in M&A, IP and commercial litigation. 25+ advocates.", companyUser.getId(), 16, 46, 2, "APPROVED"));
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000035", "GardenFresh Dairy Pvt Ltd", "Dairy", "Gujarat", "Anand", "Private Limited", 2012,
            "Milk and dairy products. 50K+ litre/day processing. Supplying fresh milk, curd, cheese and butter.", companyUser.getId(), 14, 44, 3, "APPROVED"));
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000036", "RubyStone Jewellery Ltd", "Jewellery", "Maharashtra", "Mumbai", "Public Limited", 2006,
            "Fine jewellery manufacturer and retailer. BIS hallmarked gold with 20+ showrooms.", companyUser.getId(), 12, 42, 4, "APPROVED"));
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000037", "DeltaTech Solutions Ltd", "Technology", "Tamil Nadu", "Chennai", "Public Limited", 2008,
            "IT services and consulting with 2000+ employees. Cloud, blockchain and enterprise apps.", companyUser.getId(), 10, 40, 5, "APPROVED"));
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000038", "Sapphire Media Networks Pvt Ltd", "Media", "Maharashtra", "Mumbai", "Private Limited", 2013,
            "Digital media and entertainment. 3 news portals and a YouTube network with 5M+ subscribers.", companyUser.getId(), 8, 38, 6, "APPROVED"));
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000039", "Himalayan Organics Pvt Ltd", "Agriculture", "Uttarakhand", "Dehradun", "Private Limited", 2017,
            "Organic farming and produce. Certified organic vegetables, pulses and spices.", companyUser.getId(), 2, 10, 2, "REJECTED"));
        companyRepo.save(makeCompany("c0000000-0000-0000-0000-000000000040", "CrystalClear Waters Pvt Ltd", "Beverages", "Rajasthan", "Jodhpur", "Private Limited", 2019,
            "Packaged drinking water and beverage company. BIS and FSSAI certified with 5 bottling plants.", companyUser.getId(), 1, 1, null, "PENDING"));

        log.info("Seeded 40 companies");

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

    private CompanyEntity makeCompany(String id, String name, String sector, String state, String city,
                                       String companyType, int year, String description, UUID companyUserId,
                                       int approvedDaysAgo, int createdDaysAgo, Integer updatedDaysAgo, String status) {
        Instant now = Instant.now();
        boolean approved = "APPROVED".equals(status);
        boolean rejected = "REJECTED".equals(status);
        return CompanyEntity.builder()
            .id(UUID.fromString(id)).name(name).sector(sector).state(state).city(city)
            .companyType(companyType).incorporationYear(year).description(description)
            .website("https://" + name.toLowerCase().replaceAll("[^a-z0-9]", "") + ".in")
            .status(CompanyEntity.CompanyStatus.valueOf(status))
            .createdBy(companyUserId)
            .approvedBy(approved || rejected ? UUID.fromString("a0000000-0000-0000-0000-000000000001") : null)
            .approvedAt(approved || rejected ? now.minusSeconds(approvedDaysAgo * 86400L) : null)
            .createdAt(now.minusSeconds(createdDaysAgo * 86400L))
            .updatedAt(updatedDaysAgo != null ? now.minusSeconds(updatedDaysAgo * 86400L) : now)
            .build();
    }
}
