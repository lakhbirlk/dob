import { View, Text, ScrollView, TouchableOpacity } from "react-native";
import { useState, useCallback } from "react";
import { useLocalSearchParams, router } from "expo-router";
import { Card } from "@/components/Card";
import { Button } from "@/components/Button";
import { Badge } from "@/components/Badge";
import { PremiumBadge } from "@/components/PremiumBadge";
import { UpgradeBottomSheet } from "@/components/UpgradeBottomSheet";
import { Divider } from "@/components/Divider";
import { useCompanyDetail } from "@/hooks/useCompanies";
import { useAuthStore } from "@/store/authStore";
import { companiesApi } from "@/services/api";
import type {
  FreeCompanyResponse,
  PremiumCompanyResponse,
} from "@/types";
import { UserRole } from "@/types";

function formatINR(v?: number) {
  if (v == null) return "—";
  if (v >= 1e7) return `₹${(v / 1e7).toFixed(1)} Cr`;
  if (v >= 1e5) return `₹${(v / 1e5).toFixed(1)} L`;
  return `₹${v.toLocaleString("en-IN")}`;
}

function formatNumber(v?: number | null) {
  if (v == null) return "—";
  if (v >= 10000000) return (v / 10000000).toFixed(1) + " Cr";
  if (v >= 100000) return (v / 100000).toFixed(1) + " L";
  return v.toLocaleString("en-IN");
}

export default function CompanyDetailScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const { data: response, isLoading } = useCompanyDetail(id);
  const [showUpgrade, setShowUpgrade] = useState(false);
  const [downloading, setDownloading] = useState(false);
  const [downloadError, setDownloadError] = useState<string | null>(null);
  const [downloadSuccess, setDownloadSuccess] = useState(false);
  const [expandedSections, setExpandedSections] = useState<Record<string, boolean>>({
    financials: false,
  });
  const { isAuthenticated, user } = useAuthStore();
  const isAdmin = user?.role === UserRole.ADMIN || user?.role === UserRole.SUPER_ADMIN;

  const toggleSection = useCallback((key: string) => {
    setExpandedSections(prev => ({ ...prev, [key]: !prev[key] }));
  }, []);

  const handleDownload = useCallback(async () => {
    if (downloading || !id) return;
    if (!isAuthenticated) {
      router.push("/(auth)/login?reason=session_expired");
      return;
    }
    setDownloading(true);
    setDownloadError(null);
    setDownloadSuccess(false);
    try {
      await companiesApi.downloadReport(id);
      setDownloadSuccess(true);
      setTimeout(() => setDownloadSuccess(false), 4000);
    } catch (err: any) {
      const message = err?.message || "Failed to download report. Please try again.";
      setDownloadError(message);
      setTimeout(() => setDownloadError(null), 6000);
    } finally {
      setDownloading(false);
    }
  }, [id, downloading, isAuthenticated]);

  if (isLoading || !response) {
    return (
      <View className="flex-1 bg-bg items-center justify-center">
        <Text className="text-muted text-base">Loading company profile...</Text>
      </View>
    );
  }

  // ── ADMIN OVERRIDE ──
  if (isAdmin && response.locked) {
    response.locked = false;
  }

  // ── FREE USER VIEW (Locked) ──
  if (response.locked) {
    const company = response as FreeCompanyResponse;
    return (
      <ScrollView className="flex-1 bg-bg">
        <View className="bg-navy-deep pt-10 pb-8 px-5" style={{ borderBottomLeftRadius: 24, borderBottomRightRadius: 24 }}>
          <View className="flex-row items-center gap-x-3 mb-3">
            <Text className="text-3xl">🔒</Text>
            <View>
              <Text className="text-2xl font-extrabold text-white font-mono">
                {company.companyId}
              </Text>
              <Text className="text-xs text-faint font-medium mt-1">Company ID</Text>
            </View>
          </View>
          <Text className="text-base text-faint mb-4">
            {[company.industry, company.city, company.state].filter(Boolean).join(" · ") || "Company information locked"}
          </Text>
          <View className="flex-row gap-x-2 flex-wrap">
            {company.businessType && (
              <Badge variant="info"><Text className="text-xs font-extrabold">{company.businessType}</Text></Badge>
            )}
            {company.verified ? (
              <PremiumBadge size="sm" />
            ) : (
              <Badge variant="neutral"><Text className="text-xs font-extrabold">Pending Verification</Text></Badge>
            )}
          </View>
        </View>

        <View className="px-5 pt-6 gap-y-5 pb-16">
          <Card variant="elevated">
            <Text className="text-lg font-extrabold text-navy mb-4">Company Overview</Text>
            <View className="flex-row flex-wrap gap-y-4">
              <DetailItem label="Industry" value={company.industry} />
              <DetailItem label="Business Type" value={company.businessType} />
              <DetailItem label="State" value={company.state} />
              <DetailItem label="City" value={company.city} />
              <DetailItem label="Company Age" value={company.companyAge != null ? `${company.companyAge} years` : null} />
              <DetailItem label="Employees" value={company.employeeRange} />
              <DetailItem label="Revenue Range" value={company.revenueRange} />
              <DetailItem label="Risk Score" value={company.riskScore} />
            </View>
            {company.summary && (
              <>
                <Divider />
                <Text className="text-sm text-ink leading-6 mt-3">{company.summary}</Text>
              </>
            )}
          </Card>

          <LockedSection icon="📊" title="Financial Statements" />
          <LockedSection icon="👥" title="Leadership & Team" />
          <LockedSection icon="📋" title="Certificates & Compliance" />
          <LockedSection icon="📍" title="Address & Contact Details" />
          <LockedSection icon="🎯" title="Mission, Vision & Culture" />
          <LockedSection icon="🏆" title="Awards & Recognition" />
          <LockedSection icon="🎥" title="Company Videos" />
          <LockedSection icon="🤖" title="AI Risk Analysis Report" />

          <Card variant="subtle" className="items-center py-6">
            <View className="w-16 h-16 rounded-full bg-gold/15 items-center justify-center mb-4">
              <Text className="text-3xl">🔓</Text>
            </View>
            <Text className="text-xl font-extrabold text-navy text-center mb-2">Unlock Full Company Report</Text>
            <Text className="text-sm text-muted text-center mb-1 px-4 leading-5">
              Get access to CA-certified financials, director details, shareholding patterns, and AI-powered risk analysis.
            </Text>
            <View className="flex-row items-center gap-x-2 mt-2 mb-5">
              <Badge variant="gold"><Text className="text-xs font-extrabold">₹2,500/month</Text></Badge>
              <Badge variant="success"><Text className="text-xs font-extrabold">50 Downloads/mo</Text></Badge>
            </View>
            <Button variant="gold" size="xl" onPress={() => setShowUpgrade(true)}>Subscribe Now</Button>
          </Card>
        </View>

        <UpgradeBottomSheet visible={showUpgrade} onClose={() => setShowUpgrade(false)} companyId={company.companyId} />
      </ScrollView>
    );
  }

  // ── PREMIUM USER VIEW (Full data) ──
  const company = response as PremiumCompanyResponse;

  return (
    <ScrollView className="flex-1 bg-bg">
      {/* ── Hero Header ── */}
      <View className="bg-navy-deep pt-10 pb-8 px-5" style={{ borderBottomLeftRadius: 24, borderBottomRightRadius: 24 }}>
        <View className="flex-row items-start mb-2">
          {company.logoUrl && (
            <View className="w-16 h-16 rounded-xl bg-white/15 items-center justify-center mr-3 overflow-hidden">
              <Text className="text-3xl">{company.companyName?.charAt(0) || "🏢"}</Text>
            </View>
          )}
          <View className="flex-1">
            <View className="flex-row justify-between items-start">
              <View className="flex-1 mr-3">
                <Text className="text-3xl font-extrabold text-white mb-1">{company.companyName}</Text>
                <Text className="text-xs text-faint font-mono">{company.companyId}</Text>
              </View>
              <PremiumBadge size="md" />
            </View>
          </View>
        </View>
        <Text className="text-base text-faint mb-3 mt-1">
          {[company.industry, company.city, company.state].filter(Boolean).join(" · ")}
        </Text>
        <View className="flex-row gap-x-2 flex-wrap">
          {company.businessType && (
            <Badge variant="info"><Text className="text-xs font-extrabold">{company.businessType}</Text></Badge>
          )}
          {company.incorporationYear && (
            <Badge variant="neutral"><Text className="text-xs font-extrabold">Est. {company.incorporationYear}</Text></Badge>
          )}
          {company.verified && (
            <Badge variant="success"><Text className="text-xs font-extrabold">✓ CA Verified</Text></Badge>
          )}
          {company.companyStage && (
            <Badge variant="warning"><Text className="text-xs font-extrabold">{company.companyStage}</Text></Badge>
          )}
          {company.riskScore && (
            <Badge variant={company.riskScore === "Low" ? "success" : company.riskScore === "High" ? "danger" : "warning"}>
              <Text className="text-xs font-extrabold">Risk: {company.riskScore}</Text>
            </Badge>
          )}
        </View>
        {company.dashboardStatus && (
          <View className="mt-3">
            <DashboardStatusBadge status={company.dashboardStatus} />
          </View>
        )}
      </View>

      <View className="px-5 pt-6 gap-y-5 pb-16">
        {/* ── Business Identifiers ── */}
        {(company.cin || company.gstin || company.pan || company.companyRegistrationNumber) && (
          <Card variant="elevated">
            <Text className="text-lg font-extrabold text-navy mb-4">📋 Business Identifiers</Text>
            <DetailItem label="CIN" value={company.cin} />
            <DetailItem label="GSTIN" value={company.gstin} />
            <DetailItem label="PAN" value={company.pan} />
            <DetailItem label="Registration Number" value={company.companyRegistrationNumber || company.registrationNumber} />
          </Card>
        )}

        {/* ── About ── */}
        {company.description && (
          <Card variant="elevated">
            <Text className="text-lg font-extrabold text-navy mb-3">📄 About the Company</Text>
            <Text className="text-sm text-ink leading-6">{company.description}</Text>
          </Card>
        )}

        {/* ── Mission & Vision ── */}
        {(company.mission || company.vision) && (
          <Card variant="elevated">
            <Text className="text-lg font-extrabold text-navy mb-4">🎯 Mission & Vision</Text>
            {company.mission && (
              <View className="mb-3">
                <Text className="text-xs text-faint font-semibold uppercase tracking-wider mb-1">Mission</Text>
                <Text className="text-sm text-ink leading-6 italic">"{company.mission}"</Text>
              </View>
            )}
            {company.vision && (
              <View>
                <Text className="text-xs text-faint font-semibold uppercase tracking-wider mb-1">Vision</Text>
                <Text className="text-sm text-ink leading-6 italic">"{company.vision}"</Text>
              </View>
            )}
          </Card>
        )}

        {/* ── Company Culture ── */}
        {company.cultureSummary && (
          <Card variant="elevated">
            <Text className="text-lg font-extrabold text-navy mb-3">💡 Company Culture</Text>
            <Text className="text-sm text-ink leading-6">{company.cultureSummary}</Text>
          </Card>
        )}

        {/* ── Company Details ── */}
        <Card variant="elevated">
          <Text className="text-lg font-extrabold text-navy mb-4">📊 Company Details</Text>
          <View className="flex-row flex-wrap gap-y-4">
            <DetailItem label="Industry" value={company.industry} />
            <DetailItem label="Sub-Sector" value={company.subSector} />
            <DetailItem label="Business Type" value={company.businessType} />
            <DetailItem label="Business Model" value={company.businessModel} />
            <DetailItem label="Company Stage" value={company.companyStage} />
            <DetailItem label="Incorporation" value={company.incorporationYear?.toString()} />
            <DetailItem label="Company Age" value={company.companyAge != null ? `${company.companyAge} years` : null} />
            <DetailItem label="Employees" value={company.employeeCount != null ? formatNumber(company.employeeCount) : company.employeeRange} />
            <DetailItem label="Annual Revenue" value={company.annualRevenue ? `₹${company.annualRevenue}` : company.revenueRange} />
            <DetailItem label="Risk Score" value={company.riskScore} />
            <DetailItem label="Headquarters" value={company.headquarter} />
            <DetailItem label="Branch Offices" value={company.numBranches != null ? `${company.numBranches} offices` : null} />
          </View>
        </Card>

        {/* ── Contact & Social ── */}
        {(company.website || company.email || company.phoneNumber || company.linkedinUrl || company.twitterUrl) && (
          <Card variant="elevated">
            <Text className="text-lg font-extrabold text-navy mb-4">📞 Contact & Social</Text>
            <DetailItem label="Website" value={company.website} />
            <DetailItem label="Email" value={company.email} />
            <DetailItem label="Phone" value={company.phoneNumber} />
            {company.linkedinUrl && (
              <Text className="text-sm text-blue mt-2 font-medium">🔗 LinkedIn: {company.linkedinUrl}</Text>
            )}
            {company.twitterUrl && (
              <Text className="text-sm text-blue mt-1 font-medium">🐦 Twitter/X: {company.twitterUrl}</Text>
            )}
          </Card>
        )}

        {/* ── Address ── */}
        {company.address && (
          <Card variant="elevated">
            <Text className="text-lg font-extrabold text-navy mb-3">📍 Registered Address</Text>
            <Text className="text-sm text-ink leading-6">{company.address}</Text>
            <Text className="text-sm text-muted mt-1">{[company.city, company.state].filter(Boolean).join(", ")}</Text>
          </Card>
        )}

        {/* ── Leadership ── */}
        {(company.ceoName || company.ctoName || company.founders || (company.keyExecutives && company.keyExecutives.length > 0)) && (
          <Card variant="elevated">
            <Text className="text-lg font-extrabold text-navy mb-4">👥 Leadership</Text>
            {company.ceoName && (
              <View className="flex-row items-center gap-x-3 py-2">
                <View className="w-10 h-10 rounded-full bg-navy/10 items-center justify-center">
                  <Text className="text-lg">👤</Text>
                </View>
                <View className="flex-1">
                  <Text className="text-sm font-bold text-ink">{company.ceoName}</Text>
                  <Text className="text-xs text-muted">Chief Executive Officer</Text>
                </View>
              </View>
            )}
            {company.ctoName && (
              <View className="flex-row items-center gap-x-3 py-2">
                <View className="w-10 h-10 rounded-full bg-navy/10 items-center justify-center">
                  <Text className="text-lg">👤</Text>
                </View>
                <View className="flex-1">
                  <Text className="text-sm font-bold text-ink">{company.ctoName}</Text>
                  <Text className="text-xs text-muted">Chief Technology Officer</Text>
                </View>
              </View>
            )}
            {company.founders && (
              <View className="mt-2 pt-2 border-t border-line">
                <Text className="text-xs text-faint font-semibold uppercase tracking-wider mb-2">Founder(s)</Text>
                <Text className="text-sm text-ink">{company.founders}</Text>
              </View>
            )}
            {company.keyExecutives?.map((exec, i) => (
              <View key={i} className="flex-row items-center gap-x-3 py-2">
                <View className="w-10 h-10 rounded-full bg-navy/10 items-center justify-center">
                  <Text className="text-lg">👤</Text>
                </View>
                <View className="flex-1">
                  <Text className="text-sm font-bold text-ink">{exec.name || exec.directorName || "—"}</Text>
                  <Text className="text-xs text-muted">{exec.designation || exec.role || "Director"}</Text>
                </View>
              </View>
            ))}
          </Card>
        )}

        {/* ── Products, Services & Technologies ── */}
        {(company.products || company.technologiesUsed) && (
          <Card variant="elevated">
            <Text className="text-lg font-extrabold text-navy mb-4">🛠️ Products & Services</Text>
            {company.products && (
              <View className="mb-3">
                <Text className="text-xs text-faint font-semibold uppercase tracking-wider mb-1">Products</Text>
                <Text className="text-sm text-ink leading-6">{company.products}</Text>
              </View>
            )}
            {company.services && (
              <View className="mb-3">
                <Text className="text-xs text-faint font-semibold uppercase tracking-wider mb-1">Services</Text>
                <Text className="text-sm text-ink leading-6">{company.services}</Text>
              </View>
            )}
            {company.technologiesUsed && (
              <View>
                <Text className="text-xs text-faint font-semibold uppercase tracking-wider mb-1">Technologies</Text>
                <View className="flex-row flex-wrap gap-1.5 mt-1">
                  {company.technologiesUsed.split(",").map((tech, i) => (
                    <View key={i} className="bg-navy/8 rounded-full px-3 py-1">
                      <Text className="text-xs text-navy font-medium">{tech.trim()}</Text>
                    </View>
                  ))}
                </View>
              </View>
            )}
          </Card>
        )}

        {/* ── Funding & Investors ── */}
        {(company.totalFunding || company.investors) && (
          <Card variant="elevated">
            <Text className="text-lg font-extrabold text-navy mb-4">💰 Funding & Investors</Text>
            <DetailItem label="Total Funding" value={company.totalFunding} />
            {company.investors && (
              <View className="mt-2">
                <Text className="text-xs text-faint font-semibold uppercase tracking-wider mb-1">Investors</Text>
                <Text className="text-sm text-ink">{company.investors}</Text>
              </View>
            )}
            {company.totalFunding && (
              <View className="mt-3 bg-green-light/30 rounded-xl px-4 py-3">
                <Text className="text-xs text-green font-semibold">✓ Funded Company</Text>
                <Text className="text-sm text-ink mt-1">
                  {company.companyName} has raised {company.totalFunding} from notable investors.
                </Text>
              </View>
            )}
          </Card>
        )}

        {/* ── Certifications Overview ── */}
        {company.certificationsOverview && (
          <Card variant="elevated">
            <Text className="text-lg font-extrabold text-navy mb-3">✅ Certifications</Text>
            <View className="flex-row flex-wrap gap-1.5">
              {company.certificationsOverview.split(",").map((cert, i) => (
                <View key={i} className="bg-green-light/40 rounded-full px-3 py-1.5">
                  <Text className="text-xs text-green font-bold">{cert.trim()}</Text>
                </View>
              ))}
            </View>
          </Card>
        )}

        {/* ── Awards ── */}
        {company.awards && (
          <Card variant="elevated">
            <Text className="text-lg font-extrabold text-navy mb-3">🏆 Awards & Recognition</Text>
            <Text className="text-sm text-ink leading-6">{company.awards}</Text>
          </Card>
        )}

        {/* ── Financial Statements (Enhanced) ── */}
        {company.financials && company.financials.length > 0 && (
          <Card variant="elevated">
            <Text className="text-lg font-extrabold text-navy mb-4">📊 Financial Statements</Text>
            {company.financials.map((fin, i) => (
              <View key={fin.id ?? i}>
                {i > 0 && <Divider />}
                {/* FY Header */}
                <TouchableOpacity
                  onPress={() => toggleSection(`fin_${i}`)}
                  className="flex-row justify-between items-center py-2"
                >
                  <View className="flex-row items-center gap-x-2">
                    <Text className="text-base font-bold text-ink">{fin.financialYear}</Text>
                    {fin.status && (
                      <Badge variant={fin.status === "Approved" ? "success" : fin.status === "Rejected" ? "danger" : "warning"}>
                        <Text className="text-xs font-extrabold">{fin.status}</Text>
                      </Badge>
                    )}
                  </View>
                  <Text className="text-faint text-lg">
                    {expandedSections[`fin_${i}`] ? "▲" : "▼"}
                  </Text>
                </TouchableOpacity>

                {/* Summary metrics always visible */}
                <View className="flex-row flex-wrap gap-y-3 mt-1">
                  <Finance label="Revenue" value={fin.revenue} />
                  <Finance label="Net Profit" value={fin.netProfit} />
                  <Finance label="Assets" value={fin.assets} />
                  <Finance label="EBITDA" value={fin.ebitda} />
                </View>

                {/* Expanded details */}
                {expandedSections[`fin_${i}`] && (
                  <View className="mt-4 bg-surface rounded-xl p-4">
                    <Text className="text-xs text-faint font-semibold uppercase tracking-wider mb-3">Full Financial Breakdown</Text>
                    <View className="flex-row flex-wrap gap-y-3">
                      <Finance label="Revenue" value={fin.revenue} />
                      <Finance label="Expenses" value={fin.expenses} />
                      <Finance label="EBITDA" value={fin.ebitda} />
                      <Finance label="Net Profit" value={fin.netProfit} />
                      <Finance label="Total Assets" value={fin.assets} />
                      <Finance label="Liabilities" value={fin.liabilities} />
                      <Finance label="Equity" value={fin.equity} />
                      <Finance label="Op. Cash Flow" value={fin.operatingCashFlow} />
                      <Finance label="CapEx" value={fin.capex} />
                      <Finance label="Debt" value={fin.debt} />
                    </View>

                    {/* Documents */}
                    {(fin.balanceSheetUrl || fin.profitLossUrl || fin.cashFlowUrl || fin.auditorReportUrl || fin.taxFilingUrl) && (
                      <View className="mt-4 pt-3 border-t border-line">
                        <Text className="text-xs text-faint font-semibold uppercase tracking-wider mb-2">Documents</Text>
                        {fin.balanceSheetUrl && <DocLink label="Balance Sheet" />}
                        {fin.profitLossUrl && <DocLink label="P&L Statement" />}
                        {fin.cashFlowUrl && <DocLink label="Cash Flow Statement" />}
                        {fin.auditorReportUrl && <DocLink label="Auditor Report" />}
                        {fin.taxFilingUrl && <DocLink label="Tax Filing" />}
                        {fin.uploadedBy && (
                          <Text className="text-xs text-muted mt-2">
                            Uploaded by {fin.uploadedBy} | Size: {fin.fileSize || "—"}
                          </Text>
                        )}
                      </View>
                    )}
                  </View>
                )}
              </View>
            ))}
          </Card>
        )}

        {/* ── Certificates (Enhanced) ── */}
        {company.certificates && company.certificates.length > 0 && (
          <Card variant="elevated">
            <Text className="text-lg font-extrabold text-navy mb-4">📜 Certifications & Compliance</Text>
            {company.certificates.map((cert, i) => (
              <View key={i} className="flex-row items-center gap-x-3 py-2.5">
                <View className="w-10 h-10 rounded-lg bg-green-light items-center justify-center">
                  <Text className="text-lg">✅</Text>
                </View>
                <View className="flex-1">
                  <Text className="text-sm font-bold text-ink">{cert.certificateName || cert.name}</Text>
                  {cert.issuingAuthority && (
                    <Text className="text-xs text-muted">{cert.issuingAuthority}</Text>
                  )}
                  <View className="flex-row items-center gap-x-2 mt-1">
                    {cert.status && (
                      <Text className={`text-xs font-bold ${cert.status === "Active" ? "text-green" : "text-red"}`}>
                        {cert.status}
                      </Text>
                    )}
                    {cert.certificateNumber && (
                      <Text className="text-xs text-faint">{cert.certificateNumber}</Text>
                    )}
                  </View>
                </View>
                {cert.thumbnailUrl && <Text className="text-xl">📜</Text>}
              </View>
            ))}
          </Card>
        )}

        {/* ── Videos (Enhanced) ── */}
        {company.videos && company.videos.length > 0 && (
          <Card variant="elevated">
            <Text className="text-lg font-extrabold text-navy mb-4">🎥 Company Videos</Text>
            {company.videos.map((v, i) => (
              <View key={i} className="py-2.5">
                {i > 0 && <Divider />}
                <View className="flex-row items-center gap-x-3 pt-2">
                  <View className="w-12 h-12 rounded-lg bg-navy/10 items-center justify-center">
                    <Text className="text-lg">▶️</Text>
                  </View>
                  <View className="flex-1">
                    <Text className="text-sm font-bold text-ink">{v.title}</Text>
                    {v.category && <Text className="text-xs text-muted">{v.category}</Text>}
                    <View className="flex-row items-center gap-x-3 mt-1">
                      {v.duration && <Text className="text-xs text-faint">{v.duration}</Text>}
                      {v.views != null && <Text className="text-xs text-faint">{formatNumber(v.views)} views</Text>}
                      {v.language && <Text className="text-xs text-faint">{v.language}</Text>}
                    </View>
                  </View>
                </View>
                {v.description && (
                  <Text className="text-xs text-muted leading-5 mt-1 ml-[60px]">{v.description}</Text>
                )}
                {(v.transcriptSummary) && (
                  <TouchableOpacity onPress={() => toggleSection(`vid_${i}`)}>
                    <Text className="text-xs text-blue mt-1 ml-[60px]">
                      {expandedSections[`vid_${i}`] ? "Hide transcript" : "View transcript"}
                    </Text>
                    {expandedSections[`vid_${i}`] && (
                      <Text className="text-xs text-muted leading-5 mt-1 ml-[60px]">{v.transcriptSummary}</Text>
                    )}
                  </TouchableOpacity>
                )}
              </View>
            ))}
          </Card>
        )}

        {/* ── AI Analysis ── */}
        {company.aiAnalysis && (
          <Card variant="elevated">
            <Text className="text-lg font-extrabold text-navy mb-3">🤖 AI Analysis</Text>
            <Text className="text-sm text-ink leading-6">{company.aiAnalysis}</Text>
          </Card>
        )}

        {/* ── Download CTA ── */}
        <Card variant="subtle" className="items-center py-6">
          <Text className="text-lg font-extrabold text-navy text-center mb-2">Download Full Report</Text>
          <Text className="text-sm text-muted text-center mb-5 px-4">
            Get a comprehensive PDF report including all financials, certificates, and risk analysis.
          </Text>

          {downloadError && (
            <View className="bg-red-light border border-red/30 rounded-lg px-4 py-3 mb-4 flex-row items-center w-full mx-4">
              <Text className="text-red text-sm flex-1">{downloadError}</Text>
              <TouchableOpacity onPress={() => setDownloadError(null)}>
                <Text className="text-red font-bold text-sm ml-3">✕</Text>
              </TouchableOpacity>
            </View>
          )}

          {downloadSuccess && (
            <View className="bg-green-light border border-green/30 rounded-lg px-4 py-3 mb-4 flex-row items-center w-full mx-4">
              <Text className="text-green text-sm flex-1">✓ Report downloaded successfully</Text>
            </View>
          )}

          <Button
            variant="gold"
            size="xl"
            loading={downloading}
            disabled={!company.canDownload || downloading}
            onPress={handleDownload}
          >
            {downloading ? "Preparing Report..." : company.canDownload ? "Download Report (PDF)" : "Download limit reached"}
          </Button>
          {!company.canDownload && !isAdmin && (
            <Text className="text-xs text-red mt-2 text-center">
              Please upgrade your subscription to download reports.
            </Text>
          )}
        </Card>
      </View>
    </ScrollView>
  );
}

// ──────────── Sub-components ────────────

function DetailItem({ label, value }: { label: string; value?: string | null }) {
  if (!value) return null;
  return (
    <View className="w-1/2 pr-3">
      <Text className="text-xs text-faint font-semibold uppercase tracking-wider">{label}</Text>
      <Text className="text-sm font-bold text-ink mt-0.5">{value}</Text>
    </View>
  );
}

function Finance({ label, value }: { label: string; value?: number }) {
  return (
    <View className="w-1/2 mb-1">
      <Text className="text-xs text-faint font-medium">{label}</Text>
      <Text className="text-sm font-bold text-ink mt-0.5">{formatINR(value)}</Text>
    </View>
  );
}

function DocLink({ label }: { label: string }) {
  return (
    <View className="flex-row items-center gap-x-2 py-1">
      <Text className="text-xs text-blue underline">📄 {label}.pdf</Text>
    </View>
  );
}

function LockedSection({ icon, title }: { icon: string; title: string }) {
  return (
    <Card variant="bordered" className="flex-row items-center gap-x-4 py-4 opacity-60">
      <Text className="text-2xl">{icon}</Text>
      <View className="flex-1">
        <Text className="text-base font-bold text-navy">{title}</Text>
        <Text className="text-xs text-muted mt-0.5">🔒 Subscribe to unlock</Text>
      </View>
      <Text className="text-gold text-lg">🔒</Text>
    </Card>
  );
}

function DashboardStatusBadge({ status }: { status: string }) {
  const statusConfig: Record<string, { variant: "success" | "warning" | "danger" | "info" | "neutral"; label: string }> = {
    Approved: { variant: "success", label: "✓ Approved" },
    "Under Review": { variant: "warning", label: "🔄 Under Review" },
    Pending: { variant: "info", label: "⏳ Pending" },
    Rejected: { variant: "danger", label: "✕ Rejected" },
    "Needs Changes": { variant: "neutral", label: "⚠️ Needs Changes" },
  };
  const config = statusConfig[status] || { variant: "neutral" as const, label: status };
  return (
    <Badge variant={config.variant}>
      <Text className="text-xs font-extrabold">{config.label}</Text>
    </Badge>
  );
}
