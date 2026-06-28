export const colors = {
  navy: "#1E2761",
  navy2: "#2A3580",
  navyDeep: "#141B47",
  navyLight: "#3A4590",

  gold: "#E8B84B",
  goldDark: "#C49A35",
  goldLight: "#F0D06B",
  goldPale: "#FFF8E7",

  teal: "#0D9488",
  tealLight: "#14B8A6",
  blue: "#2563EB",
  blueLight: "#3B82F6",
  red: "#DC2626",
  redLight: "#FEF2F2",
  green: "#16A34A",
  greenLight: "#F0FDF6",

  bg: "#F6F7FB",
  card: "#FFFFFF",
  surface: "#F8FAFC",
  ink: "#1A2238",
  muted: "#5A6478",
  faint: "#98A1B3",
  line: "#E3E7F0",
  overlay: "rgba(20,27,71,0.4)",
} as const;

export const gradients = {
  heroStart: "#141B47",
  heroMid: "#1E2761",
  heroEnd: "#2E3D8F",
  cardNavy: ["#1E2761", "#2A3580"] as const,
  cardGold: ["#E8B84B", "#C49A35"] as const,
};

export type Colors = typeof colors;
