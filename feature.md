# DataOfBusiness — Feature Reference

> **DataOfBusiness** is a corporate intelligence and research platform for Indian private companies. It provides CA-certified financial information, company profiles, and due-diligence data on a subscription basis.

---

## Table of Contents
1. [Company Database (Browse & Search)](#1-company-database-browse--search)
2. [Company Listing (For Businesses)](#2-company-listing-for-businesses)
3. [Research Membership (For Researchers)](#3-research-membership-for-researchers)
4. [Download Research Profiles](#4-download-research-profiles)
5. [Premium Members-Only Data](#5-premium-members-only-data)
6. [Filters & Advanced Search](#6-filters--advanced-search)
7. [36-Month Turnover Tracking](#7-36-month-turnover-tracking)
8. [CA-Certified Financial Statements](#8-ca-certified-financial-statements)
9. [Corporate Profile Video](#9-corporate-profile-video)
10. [Loans & Liabilities Disclosure](#10-loans--liabilities-disclosure)
11. [PAN + OTP Authentication](#11-pan--otp-authentication)
12. [Payment Integration (Razorpay)](#12-payment-integration-razorpay)
13. [Refund & Cancellation System](#13-refund--cancellation-system)
14. [Grievance Redressal](#14-grievance-redressal)
15. [Reporting Incorrect Data](#15-reporting-incorrect-data)
16. [Privacy & DPDP Act 2023 Compliance](#16-privacy--dpdp-act-2023-compliance)
17. [Audit Logging](#17-audit-logging)
18. [Admin Controls](#18-admin-controls)
19. [OnlineCACertificate Partnership](#19-onlinecacertificate-partnership)
20. [Legal & Compliance Framework](#20-legal--compliance-framework)
21. [GST Invoicing](#21-gst-invoicing)
22. [User Roles & Permissions](#22-user-roles--permissions)

---

## 1. Company Database (Browse & Search)

**Source:** `dataofbusiness_website.html` — Homepage hero, Board page

The core of the platform is a searchable database of Indian private companies with CA-certified financial information.

### Public Access (Non-Member)
- View a gallery of **company cards** each displaying:
  - Unique listing code (e.g. `DOB-1042`) instead of the legal name
  - Entity type badge (`Pvt Ltd`, `LLP`, `Firm`, `Proprietorship`, `OPC`, `Public Ltd`)
  - Sector tag
  - City & state
  - Established year
  - **TTM Turnover** (₹ Lakhs)
  - **3-Year Growth** trend percentage (green / red)
  - **Net Profit** (₹ Lakhs)
  - **Net Worth** (₹ Lakhs)
  - Sparkline chart of 36-month turnover trend
- Basic search by sector, state, city, company code, entity type

### Member Access
- Full company **legal name** displayed instead of anonymized code
- All advanced filters unlocked
- Company identity revealed (name, CIN/Reg. number, CA details, email, phone)
- Full financial statements visible
- Profile video playable
- Download enabled

### UX States
- **Loading** — Cards render from local/API data
- **Empty** — "No companies match your filters." shown with context-appropriate messaging
- **Error** — Data-fetch error handling with retry option
- **Edge: Zero listings** — Hero stat shows `0` companies listed, lock-note guides to membership

---

## 2. Company Listing (For Businesses)

**Source:** `dataofbusiness_website.html` — "List Your Company" section

Businesses can publish their verified financials for research visibility. The listing process is a **6-step wizard**:

### Step 1 — Company Profile
- Legal name, entity type, company PAN, CIN/registration number
- Year established, state, city
- Sector/category, employee count
- Mobile (OTP login identity)
- Company description (for research members)
- Contact email & phone (revealed to members only)

### Step 2 — Monthly Turnover (36 Months)
- Enter net monthly turnover (₹ Lakhs, excl. GST) for each of the last 36 months
- **Quick-fill** feature to apply a single value to all months
- Grid layout with month/year labels

### Step 3 — CA-Certified Financial Statements
- Choose certification mode:
  - **"I have my own CA"** — Enter CA name, ICAI membership number, optional UDIN
  - **"I need a CA certificate"** — Redirect to `OnlineCACertificate.in` partner
- Upload Balance Sheet + P&L (PDF, max 10 MB each) for each of the last 3 FYs
- Enter latest FY net profit & net worth

### Step 4 — Loans & Liabilities
- Add liabilities with type, lender/party, outstanding amount
- Supported liability types: Bank Term Loan, Cash Credit/OD, Unsecured Loan, Vehicle/Equipment Loan, Other Liability
- Option to declare "No outstanding loans or liabilities"
- Dynamic add/remove list

### Step 5 — Corporate Profile Video (Optional)
- Upload MP4/MOV/WebM (max 100 MB, 60 seconds recommended)

### Step 6 — Review & Submit
- Full review of all entered data
- Fee breakdown:
  - ₹500 per year of financial data listed (× 3 years = ₹1,500)
  - GST @ 18% (₹270)
  - **Total: ₹1,770**
- Mandatory declarations:
  1. Information is true, accurate, and genuinely CA-certified
  2. NOT using platform to offer securities, raise capital, or solicit investment
  3. Platform is an information service only; company is responsible for its data
- **Pay & Publish** via Razorpay

### UX States
- **Incomplete form** — Per-step validation with toast messages
- **Invalid PAN** — Format validation (10-character PAN)
- **Invalid mobile** — 10-digit validation
- **Missing months** — Step 2 blocks if not all 36 months filled
- **CA mode toggle** — Switches between own-CA form and partner CTA
- **Submission** — Simulated payment in demo; real Razorpay in production; admin verification pending

---

## 3. Research Membership (For Researchers)

**Source:** `dataofbusiness_website.html` — Pricing section, Membership modal

### Pricing
- **₹2,500** for 90 days of access
- **+ 18% GST** = ₹2,950 total

### What's Included
- ✓ Full company names & identity
- ✓ CA-certified financial statements (view & download)
- ✓ Company profile videos & contact details
- ✓ **Download 5 research profiles**

### Purchase Flow
1. User clicks "Get Research Access" → modal opens
2. Pricing breakdown displayed (₹2,500 + ₹450 GST = ₹2,950)
3. Feature list shown
4. Mandatory **research-use consent** checkbox:
   - Indian resident confirmation
   - Research & due-diligence purpose only
   - Acknowledgment that platform is an information service (not stock exchange, broker, or investment adviser)
   - Agreement that any dealing with a company is private, off-platform, at own risk
5. Pay via Razorpay
6. Membership activated for 90 days

### UX States
- **Not logged in** — Prompt to register with PAN + OTP
- **Already member** — Toast: "You already have research access"
- **Consent not checked** — "Please accept the research-use declaration first"
- **Payment processing** — Button disabled with processing state
- **Payment success** — Full data unlocked, 5 downloads available, navigation pill shows "✓ Research Member"
- **Payment failure** — Error message with retry option
- **Expired membership** — Lock data again, prompt to renew

---

## 4. Download Research Profiles

**Source:** `dataofbusiness_website.html` — Detail page download

Members can download up to **5 complete company research profiles** per membership period.

### Download Limits
- **Hard cap:** 5 unique downloads per 90-day membership
- Already-downloaded companies can be re-downloaded without consuming an additional slot
- Each unique company counts once toward the limit
- Visual dots indicator shows used/remaining downloads
- Counter in the sidebar shows remaining downloads

### What's Included in a Download
- Company legal name, type, CIN/reg. number
- Location, established year, employee count
- Contact email & phone
- CA certification details (CA name, ICAI number)
- Company profile description
- Key financials (TTM turnover, net profit, net worth, 3-year growth)
- Loans & liabilities
- Full 36-month turnover data (month-wise)
- Disclaimer & terms of use

### Download Slot Refund
- If a downloaded listing is later **removed for verified incorrect data**, the download slot is **automatically restored**
- The member can then download a replacement company

### UX States
- **Non-member** — "🔒 Download Research Profile (members only)" — triggers membership modal
- **Member, not yet downloaded** — Shows remaining count, download button active
- **Member, already downloaded (slot used)** — "Download Again (already counted)"
- **Member, limit reached (5/5)** — Button disabled, "Download limit reached"
- **Error** — Download failure notification

---

## 5. Premium Members-Only Data

**Source:** `dataofbusiness_website.html` — Detail page, Board page

Data is tiered between **public (anonymized)** and **members-only (unlocked)**.

### Public/Anonymized View
- Company code only (e.g. "DOB-1042")
- Sector + entity type composite name (e.g. "Textiles LLP")
- City, state, established year
- Entity type badge, sector tag
- TTM turnover, 3-year growth, net profit, net worth
- Turnover sparkline chart
- **All identity fields blurred:** Legal name, CIN, email, phone shown as `██████████`

### Members-Only Unlocked
- Legal company name
- CIN / Registration number
- Certifying CA name & ICAI membership number
- Email & phone
- Full Balance Sheet & P&L PDFs (time-limited secure links)
- Corporate profile video
- Full loan/liability details

### UX Patterns
- **Blur-box** with overlay for locked content
- Overlay message: "🔒 Identity locked — Get research access to view..."
- **Member pill** badge on unlocked items: "✓ Identity unlocked"
- **Research banner** on every company page reiterating the purpose

---

## 6. Filters & Advanced Search

**Source:** `dataofbusiness_website.html` — Board page filters

### Available Filters (Members Only)
| Filter | Type | Options |
|--------|------|---------|
| Search | Text input | Sector, state, company code, entity type |
| Sector | Dropdown | All sectors dynamically populated from listings |
| Entity Type | Dropdown | Private Limited, LLP, Partnership Firm, Proprietorship, OPC, Public Limited (Unlisted) |
| State | Dropdown | All states dynamically populated |
| Company Size (TTM) | Dropdown | Under ₹2 Cr, ₹2–5 Cr, ₹5–10 Cr, Above ₹10 Cr |

### Non-Member Experience
- Basic search only (text input)
- Advanced filters are **visually locked** with overlay:
  - "🔒 Category & advanced filters are a member feature"
  - "Unlock Filters — ₹2,500" CTA button
- Filter inputs are disabled

### UX States
- **Filter active** — Results update in real-time via `renderBoard()`
- **No results** — "No companies match your filters." message
- **Loading** — Skeleton while filtered results compute
- **Member upgrade nudge** — Filter lock overlay with CTA

---

## 7. 36-Month Turnover Tracking

**Source:** `dataofbusiness_website.html` — Detail page, Listing form Step 2

### Data Entry (Listing Side)
- 36 individual monthly turnover fields in chronological grid
- Values in ₹ Lakhs (excl. GST)
- **Quick-fill** to populate all months with a base value
- Field-level validation (all 36 months required)

### Visualization (Research Side)
- **Sparkline SVG chart** showing the full 36-month trend
- Card-level sparkline on the board view
- Large sparkline on detail page (600px width)
- Annual aggregation table:
  - FY period label
  - Annual total
  - Monthly average
  - Peak month
- **TTM (Trailing Twelve Months)** turnover prominently displayed
- **3-Year Growth** percentage calculated from first-12-months vs last-12-months comparison

### UX States
- **Data loading** — Chart renders from array data
- **Flat/no growth** — Neutral growth display
- **Negative growth** — Red coloring on percentage
- **Positive growth** — Green coloring with + prefix
- **Incomplete data** — Validation blocks form submission

---

## 8. CA-Certified Financial Statements

**Source:** `dataofbusiness_website.html` — Listing form Step 3, Detail page

### Certification Options
1. **Own CA** — Enter CA name, ICAI membership number (6-digit), optional UDIN (18-character)
2. **OnlineCACertificate.in** — Partner service for UDIN-verified CA certificates in 24–48 hours

### Document Upload
- Balance Sheet PDF (max 10 MB) for each of 3 FYs
- P&L Statement PDF (max 10 MB) for each of 3 FYs
- Visual upload zone with drag-and-drop indication
- File chip showing uploaded filename
- Visual state change (border turns teal) on file selection

### Display (Members)
- "📄 Balance Sheet — latest 3 FYs" with PDF ✓ indicator
- "📄 Profit & Loss — latest 3 FYs" with PDF ✓ indicator
- Time-limited secure download links
- Included in research-profile download

### CA Verification
- ICAI membership number validated before listing goes live
- UDIN verified where available
- CA details displayed on company detail page for member transparency

### UX States
- **No CA entered** — Step validation blocks
- **Online mode selected** — Partner upsell card shown
- **File selected** — Upload zone shows green border + file chip
- **No file** — Dashed border with "click to upload" prompt
- **Member viewing** — Green checkmarks, download links
- **Non-member viewing** — Blur-box overlay

---

## 9. Corporate Profile Video

**Source:** `dataofbusiness_website.html` — Listing form Step 5, Detail page

### Upload Specifications
- Formats: MP4, MOV, WebM
- Max size: 100 MB
- Recommended length: 60 seconds
- Content guidelines: **Corporate profile only** — not an investment pitch

### Display
- 16:9 aspect ratio video shell
- Gold play button overlay
- Company name as video title
- "Corporate profile · Members only" label
- Hosted on the platform

### UX States
- **Non-member** — Video shell shows lock icon + "Profile video locked" + "Get Access" CTA
- **Member** — Playable video shell with play button
- **No video uploaded** — Video section absent from detail page
- **Upload in progress** — Progress indicator
- **Upload error** — Size/format validation message

---

## 10. Loans & Liabilities Disclosure

**Source:** `dataofbusiness_website.html` — Listing form Step 4, Detail page

### Data Entry
- Liability types: Bank Term Loan, Cash Credit/OD, Unsecured Loan, Vehicle/Equipment Loan, Other Liability
- Lender/party name
- Outstanding amount (₹ Lakhs)
- Dynamic add/remove rows
- "No outstanding loans or liabilities" checkbox clears all entries

### Display
- Total liabilities summary on detail page
- Each liability shown as row: type — lender, amount
- Special green row if none declared: "✓ No outstanding loans or liabilities declared"
- Disclaimer: "Declared by the company. Verify independently during due diligence."

### UX States
- **Has liabilities** — Listed individually with amounts
- **None declared** — Green confirmation row
- **Empty/incomplete** — Step validation prevents submission
- **Member view** — Full visibility
- **Non-member view** — Blurred with lock overlay

---

## 11. PAN + OTP Authentication

**Source:** `dataofbusiness_website.html` — Listing form Step 1, Terms of Service

### Registration
- **PAN** (Permanent Account Number) as primary identity marker
  - 10-character format validation: `ABCDE1234F`
  - Case-insensitive, stored uppercase
- **Mobile number** (10-digit Indian mobile)
  - OTP-based authentication
  - No password required

### Security Principles
- Platform **never collects or stores** government portal credentials
- No Income Tax, GST, or MCA portal login details
- PAN used for identity only — not for tax verification
- OTP for session authentication

### UX States
- **Invalid PAN format** — "Enter a valid 10-character company PAN"
- **Invalid mobile** — "Enter a valid 10-digit mobile"
- **OTP sent** — Verification code input
- **OTP expired/incorrect** — Resend option

---

## 12. Payment Integration (Razorpay)

**Source:** `dataofbusiness_website.html` — Membership modal, Review step; `terms.html`; `refund.html`

### Payment Flows
1. **Membership Purchase** — ₹2,950 (incl. GST) for 90-day research access
2. **Company Listing Fee** — ₹1,770 (incl. GST) for 3 years of data listing

### Processing
- Third-party gateway: **Razorpay**
- Platform does **not** store card or banking details
- GST invoices issued for all payments
- Payment confirmation triggers membership activation / listing submission

### Security
- All payments processed through Razorpay's secure infrastructure
- Transaction IDs recorded for audit and refund processing
- No sensitive financial data stored on platform

### UX States
- **Payment Initiated** — Button shows "Processing payment…"
- **Payment Success** — "✓ Payment Successful!" with green background
- **Payment Failed** — Error toast with retry
- **Awaiting verification** — Post-payment admin verification for listings
- **Demo mode** — "Demo mode — payment simulated. Production connects to Razorpay."

---

## 13. Refund & Cancellation System

**Source:** `refund.html`

### Investor Membership Refunds
| Scenario | Refund Policy |
|----------|---------------|
| Within 7 days, no downloads, no identity revealed | **Full refund** |
| After any download or identity reveal | **Non-refundable** |
| Membership active > 7 days | **Non-refundable** |
| Low inventory (< 10 live listings) at payment time | Full refund within 7 days (browsed but not downloaded) |

### Business Listing Fee Refunds
| Scenario | Refund Policy |
|----------|---------------|
| Listing rejected during verification review | **Full refund** |
| Listing approved and published | **Non-refundable** |
| Voluntary removal by business | **No pro-rata refund** |

### Important Rules
- **No refund** for transaction outcomes (investments, loans, negotiations) — these occur entirely off-platform
- Refund requests via email with payment ID and reason
- Response within **3 business days**
- Approved refunds processed within **7–10 business days** via Razorpay
- GST adjusted as per law
- Payment-gateway charges may be deducted if non-recoverable

### Auto-Renewal
- Memberships do **not** auto-renew by default
- Future auto-renewal option cancellable anytime before renewal date

### Download Slot Restoration
- When a downloaded listing is removed for verified incorrect data
- Slot is automatically restored for the member
- Member can download a replacement

---

## 14. Grievance Redressal

**Source:** `grievance.html`

### Grievance Officer
- Designated officer under IT Act, 2000, Consumer Protection Act, 2019, and DPDP Act, 2023
- Contact: email, phone, registered address
- Working hours: Monday–Friday, 10:00 AM – 6:00 PM IST

### Grievance Process
1. **Submit** — Email Grievance Officer with registered name, mobile number, complaint description
2. **Acknowledge** — Within **24 hours**
3. **Investigate** — Resolution within legally prescribed timeframe
4. **Resolve** — Written resolution sent to registered email

### Grievance Categories
| Type | Channel | SLA |
|------|---------|-----|
| Incorrect listing data | In-app Report button or email | Verified → listing removed |
| Data privacy concerns | Email to Grievance Officer | Per DPDP Act timelines |
| Payment/refund issues | Email with payment ID | 3 business days for review |
| General complaints | Email | 24-hour acknowledgment |

### Reporting Incorrect Data
- "Report" feature on every company detail page (members only)
- Reason dropdown: Incorrect turnover, fake CA certificate, incorrect financials, wrong liabilities, other
- Optional details textarea
- Reported listing removed pending verification
- Download slot refunded if applicable

---

## 15. Reporting Incorrect Data

**Source:** `dataofbusiness_website.html` — Report flow; `terms.html` §7

### How Reporting Works
1. Member clicks "⚐ Report incorrect data in this listing" on any detail page
2. Modal opens with reason dropdown and optional details
3. Member submits report
4. Listing is **immediately removed** from public/member view pending verification
5. If the member had downloaded this listing, the **download slot is restored**
6. Admin reviews the report
7. If confirmed incorrect:
   - Listing permanently removed
   - Business account may be suspended
   - Affected members' download slots restored

### Report Reasons
- Incorrect turnover figures
- CA certificate appears fake / invalid
- Incorrect financial statements
- Undisclosed / wrong liabilities
- Other

### UX States
- **Report submitted** — "✓ Report submitted. Listing removed pending verification; download slot refunded."
- **Already reported** — Listing filtered out of board view
- **Admin review** — Pending status in admin panel
- **False report** — Potential penalties for abuse per Terms

---

## 16. Privacy & DPDP Act 2023 Compliance

**Source:** `privacy.html`

### Data Collection Categories
| Category | Examples | Purpose |
|----------|----------|---------|
| Identity & account | Name, PAN, mobile, email | Account creation, OTP auth, communication |
| Business data | Business name, turnover, financials, liabilities, video | Create and display listing |
| Payment data | Transaction IDs, payment status (Razorpay) | Process fees, issue GST invoices |
| Technical data | IP address, browser, device, timestamps | Security, audit logs, fraud prevention |
| Usage data | Pages viewed, listings accessed, downloads, reports | Operate platform, enforce limits |

### What is NOT Collected
- ❌ Government portal credentials (IT, GST, MCA)
- ❌ Card or bank account numbers (handled by Razorpay)
- ❌ Biometric, health, caste, or religious information

### Data Subject Rights (DPDP Act)
- **Access** — View personal data held
- **Correction** — Rectify inaccurate data
- **Erasure** — Delete data (subject to legal retention)
- **Consent withdrawal** — Stop processing
- **Grievance** — Raise complaints with Grievance Officer
- **Nomination** — Appoint nominee for exercise of rights after death/incapacity

### Data Sharing
- With **Investors**: Identity, financials, contacts revealed to active paid members (with explicit consent)
- With **Service providers**: Razorpay, SMS/OTP provider, hosting provider (under confidentiality)
- **As required by law**: Compelled by valid legal order
- **Data is NOT sold** to advertisers or third parties

### Security Measures
- HTTPS/SSL encryption
- Hashed OTP storage
- Non-public document directories (authenticated access only)
- Time-limited access links
- Rate limiting
- Access audit logging

### Cookies
- Essential cookies & local storage only
- No third-party advertising/tracking cookies
- Future analytics deployment would require policy update and consent

---

## 17. Audit Logging

**Source:** `dataofbusiness_website.html` — Trust strip; `privacy.html`

Every access to the platform is audit-logged:
- Login timestamps
- Pages viewed
- Listings accessed and downloaded
- Reports filed
- Admin actions

### Purposes
- Security monitoring
- Fraud prevention
- Download-limit enforcement
- Compliance with data protection regulations
- Accountability and traceability

---

## 18. Admin Controls

**Source:** `CLAUDE.md` — Role definitions

### Administrator Capabilities
- **Company Approval** — Review and approve/reject new listings
- **Membership Management** — Activate, suspend, or expire memberships
- **Refund Processing** — Review and process refund requests
- **Grievance Management** — Investigate and resolve complaints
- **Data Verification** — Verify CA certificates, UDINs, and financial data
- **Listing Moderation** — Remove listings with verified incorrect data
- **User Management** — Suspend accounts for policy violations
- **Audit Trail Access** — View complete audit logs

### Admin Types
| Role | Capabilities |
|------|-------------|
| **SUPER_ADMIN** | Full system access, all admin actions, configuration |
| **ADMIN** | Company approval, membership management, refund/grievance |
| **AUDITOR** | Read-only access to audit logs |

### Verification Workflow
1. Company submits listing with data, CA details, declarations
2. Admin reviews:
   - Data completeness and consistency
   - CA certificate authenticity (ICAI membership verification)
   - Declaration compliance
3. Decision: **Approve** (listing goes live) or **Reject** (fee refunded)
4. Ongoing monitoring via user reports

---

## 19. OnlineCACertificate Partnership

**Source:** `dataofbusiness_website.html` — Listing form Step 3, Cross-sell banner

### Cross-Sell Banner (Homepage)
- Visible to all users on the homepage
- "Don't have a CA to certify your financials?"
- UDIN-verified by practising Chartered Accountants
- 24–48 hour turnaround
- CTA: "OnlineCACertificate.in →"

### Integration in Listing Flow
- Step 3 offers "I need a CA certificate" option
- Partner card with branding
- Direct link to get certified
- When selected, CA details show "OnlineCACertificate.in (pending)" / "UDIN pending"

---

## 20. Legal & Compliance Framework

**Source:** `dataofbusiness_website.html` — What-we-are/not section, Footer disclaimer; `terms.html`

### Platform Classification
| **IS** | **IS NOT** |
|--------|------------|
| Corporate intelligence & research database | Stock exchange or securities marketplace |
| Source of CA-certified company financials | Broker, investment adviser, or research analyst |
| Business-information service for diligence | Fundraising or capital-raising platform |
| Directory of verified company profiles | Entity that offers, prices, or facilitates securities |
| Subscription information product | Entity that solicits investment or arranges transactions |

### Key Legal Disclaimers
- Every company page carries a **research-use banner**
- Footer contains a **comprehensive disclaimer** covering regulatory exclusions
- Listing form requires a **no-securities declaration** before submission
- Membership requires **research-use consent** acknowledgment

---

## 21. GST Invoicing

**Source:** `dataofbusiness_website.html` — Pricing; `terms.html` §6

### Pricing Structure
| Item | Base Price | GST (18%) | Total |
|------|-----------|-----------|-------|
| Research Membership (90 days) | ₹2,500 | ₹450 | ₹2,950 |
| Company Listing (per year of data) | ₹500 | ₹90 | ₹590 |
| Company Listing (3 years) | ₹1,500 | ₹270 | ₹1,770 |

### Invoicing
- GST invoices issued for all payments
- GSTIN displayed on platform (and invoices)
- All fees include/exclude GST as indicated at checkout

---

## 22. User Roles & Permissions

**Source:** `CLAUDE.md`

| Role | Description | Key Permissions |
|------|-------------|-----------------|
| **SUPER_ADMIN** | Full platform administration | All actions, configuration, user management |
| **ADMIN** | Day-to-day administration | Company approval, membership, refund/grievance management |
| **COMPANY_USER** | Business listing management | Create/edit listings, upload financials/CA certs/videos, view listing status |
| **RESEARCH_MEMBER** | Paid subscriber | Search companies, view details, download profiles (limited) |
| **AUDITOR** | Compliance oversight | Read-only audit log access |

---

## Appendix: Feature Cross-Reference

| Feature | dataofbusiness_website.html | terms.html | privacy.html | refund.html | grievance.html |
|---------|----------------------------|------------|-------------|-------------|----------------|
| Company Database | Board page, Detail page | — | — | — | — |
| Company Listing | List page (6-step form) | §4 | §1 (business data) | §3 | — |
| Research Membership | Pricing, Membership modal | §5 | — | §2 | — |
| Download Profiles | Detail page, download fn | §5 (5-max rule) | — | §2 (slot refund) | — |
| Premium Data | Blur-box, lock overlays | §5 | §4 (data sharing) | — | — |
| Filters & Search | Board filters | — | — | — | — |
| Turnover Tracking | Listing Step 2, Detail page | — | — | — | — |
| CA-Certified Financials | Listing Step 3, Detail page | §4 | — | — | — |
| Profile Video | Listing Step 5, Detail page | — | — | — | — |
| Loans & Liabilities | Listing Step 4, Detail page | — | — | — | — |
| PAN + OTP Auth | Listing Step 1 | §3 | §1 | — | — |
| Razorpay Payments | Membership modal, Review step | §6 | §1 (payment data) | §6 | — |
| Refund System | Download slot refund logic | — | — | Full page | — |
| Grievance Redressal | Report listing modal | §16 | §9 | §8 | Full page |
| Report Incorrect Data | Report modal, refund slot | §7 | — | §2 (slot refund) | Reporting section |
| DPDP Privacy | — | — | Full page | — | §2 |
| Audit Logging | Trust strip | — | §5 | — | — |
| Admin Controls | (Backend) | §13 | — | — | — |
| OnlineCACertificate | Listing Step 3, Cross-sell banner | — | — | — | — |
| Legal Framework | IS/IS NOT section, disclaimer | §1 | Intro | §1 | — |
| GST Invoicing | Pricing | §6 | — | §6 | — |
| User Roles | (Backend) | — | — | — | — |
