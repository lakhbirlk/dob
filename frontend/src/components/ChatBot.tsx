import React, { useState, useRef, useEffect } from "react";
import {
  View,
  Text,
  TouchableOpacity,
  TextInput,
  FlatList,
  KeyboardAvoidingView,
  Platform,
  Dimensions,
} from "react-native";
import { useAuthStore } from "@/store/authStore";

// ─────────────────────── Types ───────────────────────

interface Message {
  id: string;
  role: "bot" | "user";
  text: string;
}

interface FaqEntry {
  question: string;
  answer: string;
}

// ─────────────────────── FAQ Data ───────────────────────

const FAQS: FaqEntry[] = [
  {
    question: "What is DataOfBusiness?",
    answer:
      "DataOfBusiness is a corporate intelligence database and research platform for Indian private companies. We provide CA-certified financial data, company profiles, and due-diligence information to research members on a subscription basis. We are NOT a stock exchange, broker, investment advisor, or NBFC.",
  },
  {
    question: "How does the credit system work?",
    answer:
      "Research members purchase monthly credit plans (3–30 credits/month from ₹1,500–₹5,000 + GST). Each credit unlocks one full company profile including its name, CIN, GST, PAN, financial statements, director details, and downloadable reports. Unused credits expire at month-end.",
  },
  {
    question: "What membership plans are available?",
    answer:
      "We offer 5 credit plans: Starter (3 credits, ₹1,500+ GST), Basic (5 credits, ₹2,000+ GST), Pro (10 credits, ₹3,000+ GST), Business (20 credits, ₹4,000+ GST), and Enterprise (30 credits, ₹5,000+ GST). All plans are monthly subscriptions. Upgrade anytime and remaining credits from your current plan are prorated.",
  },
  {
    question: "Is DataOfBusiness a stock exchange or investment platform?",
    answer:
      "No. DataOfBusiness is strictly a corporate intelligence and research platform. We do NOT facilitate any transactions, investments, loans, or fundraising. We do NOT provide investment advice, recommendations, or research analyst services. Our platform is for information access only.",
  },
  {
    question: "How do I become a research member?",
    answer:
      "Register with your email and password, complete PAN verification (required by Indian law), accept the research declaration confirming you'll use the platform for research and due-diligence purposes only, and choose a credit plan. New users get 2 free credits to explore the platform.",
  },
  {
    question: "How do I list my company on the platform?",
    answer:
      "Register as a company user, submit your business details, upload CA-certified financial statements and certificates, and optionally add a corporate profile video. Our admin team verifies the documents. Once approved, your profile goes live and becomes visible to research members. The annual listing fee is ₹500 + GST.",
  },
  {
    question: "What data is collected and how is it protected?",
    answer:
      "We collect only what's necessary for platform operation: name, email, phone, PAN, and company details. We comply fully with India's DPDP Act 2023. Your data is encrypted in transit and at rest. We never share personal data with third parties without your explicit consent. You can request data deletion or portability anytime.",
  },
  {
    question: "What payment methods are accepted?",
    answer:
      "All payments are processed securely through Razorpay. We accept UPI, credit/debit cards, net banking, and popular Indian payment wallets. All prices are listed with 18% GST. You receive a GST invoice for every payment.",
  },
  {
    question: "What is your refund policy?",
    answer:
      "Membership subscriptions have a cooling-off period for refunds under Indian consumer law. Company listing fees are non-refundable once the profile is approved and published. For detailed refund terms, please see our Refund Policy page. All refunds are processed through Razorpay within 5-7 business days of approval.",
  },
  {
    question: "How do I file a grievance or complaint?",
    answer:
      "You can submit a grievance through our Grievance Redressal page. Our grievance officer acknowledges receipt within 24 hours and resolves complaints within 30 days as per Indian law. You can also escalate an unresolved grievance to a senior officer for review.",
  },
  {
    question: "Can I upgrade or downgrade my plan?",
    answer:
      "You can upgrade to a higher credit plan at any time. Remaining credits from your current plan are prorated and applied to the new plan. Downgrades take effect at the next billing cycle. There are no cancellation fees.",
  },
  {
    question: "Are the financial statements verified?",
    answer:
      "Yes. All financial data published on the platform is CA (Chartered Accountant) certified. Companies upload audited financial statements along with CA certificates. Our admin team verifies the documents before the profile is approved and made visible to research members.",
  },
];

// ─────────────────────── Quick replies ───────────────────────

const QUICK_REPLIES = [
  "What is DataOfBusiness?",
  "How does the credit system work?",
  "What plans are available?",
  "Is this a stock exchange?",
  "What is the refund policy?",
  "How do I list my company?",
];

const { width: SCREEN_WIDTH } = Dimensions.get("window");
const CHAT_WIDTH = Math.min(380, SCREEN_WIDTH - 32);
const BOT_AVATAR = "🤖";

// ─────────────────────── Component ───────────────────────

export default function ChatBot() {
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState<Message[]>([
    {
      id: "welcome",
      role: "bot",
      text:
        "👋 Hi there! I'm the DoB assistant. I can answer your questions about our platform, pricing, credits, and more. Pick a question below or type your own!",
    },
  ]);
  const [inputText, setInputText] = useState("");
  const flatListRef = useRef<FlatList>(null);
  const { isAuthenticated } = useAuthStore();

  // Auto-scroll to bottom when new messages arrive
  useEffect(() => {
    if (isOpen && flatListRef.current && messages.length > 0) {
      setTimeout(() => flatListRef.current?.scrollToEnd({ animated: true }), 100);
    }
  }, [messages, isOpen]);

  // Find best FAQ match
  const findAnswer = (query: string): string | null => {
    const lower = query.toLowerCase().trim();
    if (!lower) return null;

    // Direct match first
    const direct = FAQS.find(
      (f) => f.question.toLowerCase() === lower || f.question.toLowerCase().replace("?", "") === lower,
    );
    if (direct) return direct.answer;

    // Keyword scoring
    const words = lower.split(/\s+/).filter((w) => w.length > 2);
    const scored = FAQS.map((f) => {
      const qWords = f.question.toLowerCase().split(/\s+/);
      const aWords = f.answer.toLowerCase().split(/\s+/);
      let score = 0;
      for (const w of words) {
        if (qWords.some((qw) => qw.includes(w) || w.includes(qw))) score += 3;
        if (aWords.some((aw) => aw.includes(w) || w.includes(aw))) score += 1;
      }
      return { faq: f, score };
    }).sort((a, b) => b.score - a.score);

    if (scored.length > 0 && scored[0].score >= 2) return scored[0].faq.answer;

    // Generic fallback
    return null;
  };

  const addMessage = (role: "bot" | "user", text: string) => {
    const id = `${role}-${Date.now()}-${Math.random().toString(36).slice(2, 6)}`;
    setMessages((prev) => [...prev, { id, role, text }]);
  };

  const handleSend = (text?: string) => {
    const question = (text ?? inputText).trim();
    if (!question) return;

    addMessage("user", question);
    setInputText("");

    // Simulate bot "thinking"
    setTimeout(() => {
      const answer = findAnswer(question);
      if (answer) {
        addMessage("bot", answer);
      } else {
        addMessage(
          "bot",
          "I'm not sure about that. Could you try rephrasing? You can also check our Pricing, Terms, Refund Policy, or Grievance pages for detailed information. Or pick one of the suggested questions above!",
        );
      }
    }, 400);
  };

  const handleFaqTap = (question: string) => {
    handleSend(question);
  };

  // ── Floating Button ──

  if (!isOpen) {
    return (
      <TouchableOpacity
        onPress={() => setIsOpen(true)}
        activeOpacity={0.85}
        className="absolute bottom-6 right-6 z-50 w-14 h-14 rounded-full bg-gold items-center justify-center"
        style={{
          elevation: 8,
          shadowColor: "#E8B84B",
          shadowOffset: { width: 0, height: 4 },
          shadowOpacity: 0.35,
          shadowRadius: 8,
        }}
      >
        <Text className="text-2xl">💬</Text>
      </TouchableOpacity>
    );
  }

  // ── Chat Panel ──

  return (
    <KeyboardAvoidingView
      behavior={Platform.OS === "ios" ? "padding" : undefined}
      className="absolute bottom-24 right-6 z-50"
      style={{ width: CHAT_WIDTH, maxHeight: 560 }}
    >
      <View
        className="bg-white rounded-2xl overflow-hidden flex-1"
        style={{
          elevation: 16,
          shadowColor: "#1E2761",
          shadowOffset: { width: 0, height: 8 },
          shadowOpacity: 0.18,
          shadowRadius: 24,
          maxHeight: 560,
        }}
      >
        {/* ── Header ── */}
        <View className="bg-navy-deep px-4 py-3.5 flex-row items-center justify-between">
          <View className="flex-row items-center gap-x-2.5">
            <View className="w-8 h-8 rounded-full bg-gold/20 items-center justify-center">
              <Text className="text-base">{BOT_AVATAR}</Text>
            </View>
            <View>
              <Text className="text-white font-extrabold text-sm">DoB Assistant</Text>
              <Text className="text-gold/70 text-[10px]">Online • FAQ Bot</Text>
            </View>
          </View>
          <TouchableOpacity onPress={() => setIsOpen(false)} className="w-7 h-7 rounded-full bg-white/10 items-center justify-center">
            <Text className="text-white text-xs font-bold">✕</Text>
          </TouchableOpacity>
        </View>

        {/* ── Messages ── */}
        <FlatList
          ref={flatListRef}
          data={messages}
          keyExtractor={(m) => m.id}
          className="flex-1 bg-bg"
          contentContainerStyle={{ padding: 12, paddingBottom: 4 }}
          renderItem={({ item, index }) => {
            const isBot = item.role === "bot";
            return (
              <View
                className={`mb-3 flex-row ${isBot ? "" : "justify-end"}`}
              >
                {isBot && (
                  <View className="w-7 h-7 rounded-full bg-navy/10 items-center justify-center mr-2 mt-1">
                    <Text className="text-xs">{BOT_AVATAR}</Text>
                  </View>
                )}
                <View
                  className={`rounded-2xl px-3.5 py-2.5 max-w-[80%] ${
                    isBot
                      ? "bg-white border border-line/50 rounded-tl-sm"
                      : "bg-navy rounded-tr-sm"
                  }`}
                >
                  <Text
                    className={`text-sm leading-5 ${
                      isBot ? "text-ink" : "text-white"
                    }`}
                  >
                    {item.text}
                  </Text>
                  {index === 0 && messages.length === 1 && (
                    <View className="flex-row flex-wrap gap-x-1.5 gap-y-1.5 mt-3">
                      {QUICK_REPLIES.slice(0, 3).map((qr) => (
                        <TouchableOpacity
                          key={qr}
                          onPress={() => handleFaqTap(qr)}
                          className="bg-navy/5 rounded-full px-3 py-1.5 border border-line/40"
                        >
                          <Text className="text-xs text-navy font-medium">{qr}</Text>
                        </TouchableOpacity>
                      ))}
                    </View>
                  )}
                  {/* Show more quick replies after response */}
                  {isBot && index === messages.length - 1 && messages.length > 1 && (
                    <View className="flex-row flex-wrap gap-x-1.5 gap-y-1.5 mt-3">
                      {QUICK_REPLIES.filter(
                        (qr) => qr !== messages[messages.length - 2]?.text
                      ).slice(0, 3).map((qr) => (
                        <TouchableOpacity
                          key={qr}
                          onPress={() => handleFaqTap(qr)}
                          className="bg-navy/5 rounded-full px-3 py-1.5 border border-line/40"
                        >
                          <Text className="text-xs text-navy font-medium">{qr}</Text>
                        </TouchableOpacity>
                      ))}
                    </View>
                  )}
                </View>
              </View>
            );
          }}
          ListFooterComponent={<View className="h-2" />}
        />

        {/* ── Input ── */}
        <View className="border-t border-line/60 px-3 py-2.5 bg-white flex-row items-center gap-x-2">
          <TextInput
            value={inputText}
            onChangeText={setInputText}
            placeholder="Ask a question..."
            placeholderTextColor="#98A1B3"
            className="flex-1 bg-bg rounded-xl px-3.5 py-2.5 text-sm text-ink"
            onSubmitEditing={() => handleSend()}
            returnKeyType="send"
          />
          <TouchableOpacity
            onPress={() => handleSend()}
            disabled={!inputText.trim()}
            className={`w-9 h-9 rounded-full items-center justify-center ${
              inputText.trim() ? "bg-gold" : "bg-faint/30"
            }`}
          >
            <Text className={`text-base ${inputText.trim() ? "" : "opacity-40"}`}>
              ➤
            </Text>
          </TouchableOpacity>
        </View>
      </View>
    </KeyboardAvoidingView>
  );
}
