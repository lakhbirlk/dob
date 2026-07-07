package com.dob.infrastructure.config.seed;

import java.util.List;

/**
 * Centralized repository of realistic Indian data pools used by DataSeeder.
 * All data here is static — no dependencies on Spring beans.
 */
public final class IndianDataPool {

    private IndianDataPool() {}

    // ─── Names ──────────────────────────────────────────────

    public static final List<String> MALE_FIRST_NAMES = List.of(
        "Aarav", "Vihaan", "Vivaan", "Arjun", "Rudra", "Ayaan", "Krishna", "Reyansh",
        "Yuvraj", "Rohan", "Amit", "Vikram", "Sanjay", "Deepak", "Suresh", "Rajesh",
        "Rakesh", "Manish", "Alok", "Ravi", "Anil", "Sunil", "Vijay", "Ajay",
        "Nitin", "Prakash", "Harish", "Mohan", "Ganesh", "Dinesh", "Mahesh", "Shankar",
        "Karthik", "Arun", "Venkatesh", "Sridhar", "Prasad", "Srinivas", "Narayan",
        "Rahul", "Akshay", "Aditya", "Siddharth", "Dhruv", "Kabir", "Tanishq"
    );

    public static final List<String> FEMALE_FIRST_NAMES = List.of(
        "Ananya", "Diya", "Aadhya", "Sara", "Pari", "Anika", "Myra", "Mira",
        "Neha", "Priya", "Sneha", "Kavita", "Sunita", "Meena", "Pooja", "Swati",
        "Divya", "Anita", "Shweta", "Ritu", "Deepika", "Nandini", "Lakshmi",
        "Kiran", "Shalini", "Vandana", "Geeta", "Rekha", "Anjali", "Sangeeta",
        "Megha", "Aditi", "Isha", "Tara", "Riya", "Aarushi", "Shreya", "Tanvi",
        "Kavya", "Nisha", "Sonia", "Preeti", "Jyoti", "Nidhi", "Shivani"
    );

    public static final List<String> LAST_NAMES = List.of(
        "Sharma", "Verma", "Patel", "Reddy", "Singh", "Gupta", "Joshi", "Mehta",
        "Deshmukh", "Iyer", "Khanna", "Agarwal", "Krishnan", "Subramanian", "Nair",
        "Menon", "Pillai", "Bose", "Sen", "Das", "Choudhury", "Saxena", "Mathur",
        "Trivedi", "Pandey", "Mishra", "Dubey", "Tiwari", "Chauhan", "Rawat",
        "Bisht", "Thakur", "Yadav", "Kumar", "Malhotra", "Kapoor", "Arora",
        "Bhatia", "Sachdev", "Tandon", "Bajaj", "Sethi", "Chopra", "Wadhwa",
        "Chandra", "Prasad", "Rao", "Murthy", "Hegde", "Shetty", "Kamath"
    );

    // ─── Places ────────────────────────────────────────────

    public static record CityState(String city, String state) {}

    public static final List<CityState> CITIES = List.of(
        new CityState("Mumbai", "Maharashtra"),
        new CityState("Delhi", "Delhi"),
        new CityState("Bengaluru", "Karnataka"),
        new CityState("Hyderabad", "Telangana"),
        new CityState("Ahmedabad", "Gujarat"),
        new CityState("Chennai", "Tamil Nadu"),
        new CityState("Kolkata", "West Bengal"),
        new CityState("Pune", "Maharashtra"),
        new CityState("Jaipur", "Rajasthan"),
        new CityState("Lucknow", "Uttar Pradesh"),
        new CityState("Nagpur", "Maharashtra"),
        new CityState("Indore", "Madhya Pradesh"),
        new CityState("Bhopal", "Madhya Pradesh"),
        new CityState("Surat", "Gujarat"),
        new CityState("Vadodara", "Gujarat"),
        new CityState("Coimbatore", "Tamil Nadu"),
        new CityState("Chandigarh", "Chandigarh"),
        new CityState("Guwahati", "Assam"),
        new CityState("Thiruvananthapuram", "Kerala"),
        new CityState("Visakhapatnam", "Andhra Pradesh"),
        new CityState("Mysuru", "Karnataka"),
        new CityState("Nashik", "Maharashtra"),
        new CityState("Aurangabad", "Maharashtra"),
        new CityState("Ranchi", "Jharkhand"),
        new CityState("Bhubaneswar", "Odisha"),
        new CityState("Gurugram", "Haryana"),
        new CityState("Noida", "Uttar Pradesh"),
        new CityState("Kochi", "Kerala"),
        new CityState("Dehradun", "Uttarakhand"),
        new CityState("Udaipur", "Rajasthan")
    );

    public static final List<String> COMPANY_SUFFIXES = List.of(
        "Pvt Ltd", "Ltd", "LLP", "Private Limited", "Limited"
    );

    // ─── Sectors & Business ───────────────────────────────

    public static final List<String> SECTORS = List.of(
        "Technology", "Healthcare", "Finance", "Manufacturing", "Energy",
        "Real Estate", "Agriculture", "Retail", "Logistics", "Education",
        "Pharmaceuticals", "Food Processing", "Textiles", "Chemicals",
        "Hospitality", "Media", "Environment", "Legal", "Beverages",
        "Infrastructure", "Automotive", "Aerospace", "Telecom"
    );

    public static final List<String> BUSINESS_MODELS = List.of(
        "B2B SaaS", "B2C Marketplace", "D2C Brand", "Manufacturing",
        "B2B Services", "Platform", "Consulting", "Retail",
        "Wholesale", "B2G", "B2B2C", "Subscription"
    );

    public static final List<String> COMPANY_STAGES = List.of(
        "Startup", "Early Growth", "Growth", "Established", "Mature"
    );

    // ─── Company names ─────────────────────────────────────

    public static final List<String> COMPANY_NAMES_TECH = List.of(
        "CloudPeak Technologies", "DataForge Systems", "NexaSoft Solutions",
        "QuantumByte Technologies", "Stellaris AI Labs", "CodeCraft Innovations",
        "InnoVent Solutions", "Digitron India", "WebSphere Technologies",
        "Syntellect Systems", "Orbital Tech Solutions", "Prism Analytics",
        "FusionLogic Software", "ApexaTech", "Velocis Digital"
    );

    public static final List<String> COMPANY_NAMES_MFG = List.of(
        "Precision Engineers India", "Bharat Forge & Tools", "Om Industrial Works",
        "Ashwin Manufacturing", "Surya Components", "Metro Engineers Ltd",
        "National Precision Works", "Gujarat Steel Fabricators", "Technocraft Industries",
        "Radiant Auto Parts", "Arun Machine Tools", "Dwarka Engineering Works"
    );

    public static final List<String> COMPANY_NAMES_FINANCE = List.of(
        "Sahaj Capital Advisors", "Bharat FinCorp", "Apex Wealth Managers",
        "Pragati Investment Advisors", "Samriddh Finance", "Suvidha Loans Pvt Ltd",
        "Capital Trust Financials", "Everest Insurance Brokers", "Swastik Securities"
    );

    public static final List<String> COMPANY_NAMES_HEALTHCARE = List.of(
        "HealWell Hospitals", "Swasthya Diagnostics", "LifeCare Medical Systems",
        "MediPlus Healthcare", "CareFirst Clinics", "Sparsh Surgical",
        "Navjeevan Pharmaceuticals", "Dhanwantari Ayurveda", "Wellness Pathology Labs"
    );

    public static final List<String> COMPANY_NAMES_ENERGY = List.of(
        "Suryodaya Energy", "Pawan Shakti Energy", "HariOM Power Solutions",
        "Saket Renewables", "Aarohi Solar Systems", "GreenGrid Power",
        "Urja Infra Projects", "Bharat BioEnergy", "NeoGreen Energy"
    );

    public static final List<String> COMPANY_NAMES_REALESTATE = List.of(
        "Shubham Realty Ventures", "Vastu Developers", "Krishna Infraprojects",
        "Navkar Builders", "Samanvay Developers", "Anandam Realty",
        "Trident Infrastructure", "Suyash Housing", "Mangalam Properties"
    );

    // ─── Certificate types ─────────────────────────────────

    public static record CertTemplate(String name, String authority, String description) {}

    public static final List<CertTemplate> ALL_CERT_TYPES = List.of(
        new CertTemplate("ISO 9001:2015", "BIS", "Quality Management System certification"),
        new CertTemplate("ISO 27001:2022", "BIS", "Information Security Management System"),
        new CertTemplate("ISO 14001:2015", "BIS", "Environmental Management System"),
        new CertTemplate("ISO 45001:2018", "BIS", "Occupational Health and Safety"),
        new CertTemplate("SOC 2 Type II", "AICPA", "Security, availability and confidentiality controls"),
        new CertTemplate("PCI DSS", "PCI Security Council", "Payment card industry data security standard"),
        new CertTemplate("CE Marking", "European Commission", "European conformity for products"),
        new CertTemplate("FDA Registration", "US FDA", "US Food and Drug Administration registration"),
        new CertTemplate("Startup India", "DPIIT", "DPIIT recognition for startups"),
        new CertTemplate("MSME Registration", "Ministry of MSME", "Micro, Small and Medium Enterprises registration"),
        new CertTemplate("GST Registration", "GST Council", "Goods and Services Tax registration"),
        new CertTemplate("Import Export Code", "DGFT", "Directorate General of Foreign Trade code"),
        new CertTemplate("Trademark Certificate", "IPO India", "Registered trademark protection"),
        new CertTemplate("Patent Certificate", "IPO India", "Granted patent for innovation"),
        new CertTemplate("Business License", "Municipal Corporation", "Local business operation license"),
        new CertTemplate("RERA Registration", "RERA Authority", "Real Estate Regulatory Authority registration"),
        new CertTemplate("FSSAI License", "FSSAI", "Food safety and standards license"),
        new CertTemplate("ISO 13485:2016", "BIS", "Medical devices quality management system"),
        new CertTemplate("WHO GMP", "WHO", "Good Manufacturing Practices certification"),
        new CertTemplate("NABL Accreditation", "NABL", "National Accreditation Board for Testing and Calibration"),
        new CertTemplate("APEDA Registration", "APEDA", "Agricultural export certification"),
        new CertTemplate("BIS Hallmark", "BIS", "Hallmarking certification for jewellery"),
        new CertTemplate("IRDAI License", "IRDAI", "Insurance Regulatory and Development Authority license"),
        new CertTemplate("CRISIL Rating", "CRISIL", "Credit rating certification"),
        new CertTemplate("IATF 16949:2016", "IATF", "Automotive quality management system"),
        new CertTemplate("IGBC Green Building", "IGBC", "Green building certification"),
        new CertTemplate("DPDP Act Compliance", "DSCI", "Data protection compliance certification")
    );

    public static final java.util.Map<String, List<String>> SECTOR_CERT_NAMES = java.util.Map.ofEntries(
        java.util.Map.entry("Technology", List.of("ISO 27001:2022", "SOC 2 Type II", "Startup India", "ISO 9001:2015", "PCI DSS")),
        java.util.Map.entry("Healthcare", List.of("ISO 13485:2016", "WHO GMP", "FDA Registration", "NABL Accreditation", "ISO 9001:2015")),
        java.util.Map.entry("Pharmaceuticals", List.of("WHO GMP", "ISO 13485:2016", "FDA Registration", "ISO 9001:2015", "ISO 14001:2015")),
        java.util.Map.entry("Finance", List.of("ISO 27001:2022", "IRDAI License", "CRISIL Rating", "PCI DSS", "ISO 9001:2015")),
        java.util.Map.entry("Food Processing", List.of("FSSAI License", "ISO 9001:2015", "APEDA Registration", "MSME Registration")),
        java.util.Map.entry("Beverages", List.of("FSSAI License", "ISO 9001:2015", "BIS Hallmark", "MSME Registration")),
        java.util.Map.entry("Dairy", List.of("FSSAI License", "ISO 9001:2015", "APEDA Registration", "MSME Registration")),
        java.util.Map.entry("Real Estate", List.of("RERA Registration", "ISO 9001:2015", "IGBC Green Building", "MSME Registration")),
        java.util.Map.entry("Manufacturing", List.of("ISO 9001:2015", "ISO 14001:2015", "IATF 16949:2016", "MSME Registration", "CE Marking")),
        java.util.Map.entry("Automotive", List.of("IATF 16949:2016", "ISO 9001:2015", "ISO 14001:2015", "ISO 45001:2018", "CE Marking")),
        java.util.Map.entry("Agriculture", List.of("APEDA Registration", "ISO 9001:2015", "FSSAI License", "MSME Registration")),
        java.util.Map.entry("Energy", List.of("ISO 14001:2015", "ISO 45001:2018", "ISO 9001:2015", "MSME Registration")),
        java.util.Map.entry("Textiles", List.of("ISO 9001:2015", "ISO 14001:2015", "MSME Registration", "IEC")),
        java.util.Map.entry("Chemicals", List.of("ISO 9001:2015", "ISO 14001:2015", "ISO 45001:2018", "MSME Registration")),
        java.util.Map.entry("Hospitality", List.of("ISO 9001:2015", "ISO 14001:2015", "FSSAI License", "MSME Registration")),
        java.util.Map.entry("Media", List.of("ISO 9001:2015", "ISO 27001:2022", "MSME Registration", "Trademark Certificate")),
        java.util.Map.entry("Environment", List.of("ISO 14001:2015", "ISO 9001:2015", "MSME Registration")),
        java.util.Map.entry("Infrastructure", List.of("ISO 9001:2015", "ISO 14001:2015", "ISO 45001:2018", "MSME Registration")),
        java.util.Map.entry("Logistics", List.of("ISO 9001:2015", "ISO 27001:2022", "ISO 14001:2015", "MSME Registration")),
        java.util.Map.entry("Education", List.of("ISO 9001:2015", "MSME Registration", "Startup India", "Trademark Certificate")),
        java.util.Map.entry("Legal", List.of("ISO 9001:2015", "ISO 27001:2022", "MSME Registration")),
        java.util.Map.entry("Telecom", List.of("ISO 27001:2022", "ISO 9001:2015", "MSME Registration", "Trademark Certificate"))
    );

    // ─── Investors ─────────────────────────────────────────

    public static final List<String> INVESTORS = List.of(
        "Sequoia Capital India", "Accel Partners", "Elevation Capital", "Peak XV Partners",
        "Matrix Partners India", "Nexus Venture Partners", "Blume Ventures",
        "Tiger Global", "SoftBank Vision Fund", "Kalaari Capital",
        "ICICI Venture", "HDFC Capital", "Kotak Private Equity", "State Bank of India",
        "IFC", "CDC Group", "Brookfield Asset Management",
        "ICICI Prudential Real Estate Fund", "Kotak Realty Fund",
        "Omnivore Partners", "AgFunder", "Nabard Innovation Fund",
        "Responsible Energy Fund", "Breakthrough Energy Ventures",
        "SIDBI Venture Capital", "IndiaMART Ventures",
        "Mirae Asset Venture Investments", "Stellaris Venture Partners"
    );

    // ─── Occupations for Research Members ──────────────────

    public static final List<String> OCCUPATIONS = List.of(
        "Financial Analyst", "Investment Banker", "Portfolio Manager",
        "Equity Researcher", "Management Consultant", "Business Journalist",
        "Mergers & Acquisitions Advisor", "Private Equity Analyst",
        "Venture Capitalist", "Corporate Strategist", "Market Researcher",
        "Due Diligence Specialist", "Credit Analyst", "Academic Researcher",
        "Business Professor", "Chartered Accountant", "Company Secretary",
        "Risk Analyst", "Data Scientist", "Strategy Consultant"
    );

    public static final List<String> RESEARCH_PURPOSES = List.of(
        "INVESTMENT_RESEARCH", "DUE_DILIGENCE", "MARKET_RESEARCH",
        "ACADEMIC_RESEARCH", "COMPETITOR_ANALYSIS", "PARTNERSHIP_EVALUATION"
    );

    // ─── Grievance types ───────────────────────────────────

    public static final List<String> COMPLAINT_TYPES = List.of(
        "Data Discrepancy", "Download Issue", "Payment Issue",
        "Account Access", "Incorrect Information", "Technical Issue",
        "Billing Problem", "Content Mismatch", "Profile Issue",
        "Search Functionality", "Report Error", "Other"
    );

    // ─── Activity/Audit action types ───────────────────────

    public static final List<String> AUDIT_ACTIONS = List.of(
        "LOGIN", "LOGOUT", "COMPANY_VIEWED", "COMPANY_SEARCHED",
        "COMPANY_DOWNLOADED", "COMPANY_BOOKMARKED", "COMPANY_UNLOCKED",
        "SUBSCRIPTION_PURCHASED", "PLAN_UPGRADED", "PROFILE_UPDATED",
        "PASSWORD_CHANGED", "REGISTER", "GRIEVANCE_SUBMITTED"
    );

    // ─── State codes for CIN/GST ──────────────────────────

    public static final java.util.Map<String, String> STATE_CODES = java.util.Map.ofEntries(
        java.util.Map.entry("Maharashtra", "MH"), java.util.Map.entry("Karnataka", "KA"),
        java.util.Map.entry("Telangana", "TS"), java.util.Map.entry("Gujarat", "GJ"),
        java.util.Map.entry("Tamil Nadu", "TN"), java.util.Map.entry("Delhi", "DL"),
        java.util.Map.entry("West Bengal", "WB"), java.util.Map.entry("Rajasthan", "RJ"),
        java.util.Map.entry("Uttar Pradesh", "UP"), java.util.Map.entry("Kerala", "KL"),
        java.util.Map.entry("Andhra Pradesh", "AP"), java.util.Map.entry("Odisha", "OD"),
        java.util.Map.entry("Madhya Pradesh", "MP"), java.util.Map.entry("Punjab", "PB"),
        java.util.Map.entry("Haryana", "HR"), java.util.Map.entry("Assam", "AS"),
        java.util.Map.entry("Uttarakhand", "UK"), java.util.Map.entry("Jharkhand", "JH"),
        java.util.Map.entry("Chhattisgarh", "CG"), java.util.Map.entry("Chandigarh", "CH")
    );

    // ─── Plan types ────────────────────────────────────────

    public static final List<String> PLAN_IDS = List.of(
        "CREDITS_3", "CREDITS_5", "CREDITS_10", "CREDITS_20", "CREDITS_30"
    );

    // ─── Logo URLs (realistic placeholder) ─────────────────

    public static final List<String> LOGO_URLS = List.of(
        "https://images.unsplash.com/photo-1560179707-f14e90ef3623?w=200",
        "https://images.unsplash.com/photo-1556761175-b413da4baf72?w=200",
        "https://images.unsplash.com/photo-1559136555-9303baea8ebd?w=200",
        "https://images.unsplash.com/photo-1552581234-26160f608093?w=200",
        "https://images.unsplash.com/photo-1549421263-5ec394a5ad4c?w=200",
        "https://images.unsplash.com/photo-1563986768609-322da13575f2?w=200"
    );

    // ─── Brand / display name suffixes ─────────────────

    public static String stripSuffix(String companyName) {
        return companyName
            .replaceAll("\\s+(Pvt\\s+Ltd|Ltd|LLP|Private\\s+Limited|Limited)$", "")
            .trim();
    }
}
