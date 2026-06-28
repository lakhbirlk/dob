/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{ts,tsx}",
    "./src/app/**/*.{ts,tsx}",
    "./src/components/**/*.{ts,tsx}",
    "./src/screens/**/*.{ts,tsx}",
  ],
  presets: [require("nativewind/preset")],
  theme: {
    extend: {
      colors: {
        navy: { DEFAULT: "#1E2761", 2: "#2A3580", deep: "#141B47", light: "#3A4590" },
        gold: { DEFAULT: "#E8B84B", dark: "#C49A35", light: "#F0D06B", pale: "#FFF8E7" },
        teal: { DEFAULT: "#0D9488", light: "#14B8A6" },
        blue: { DEFAULT: "#2563EB", light: "#3B82F6" },
        red: { DEFAULT: "#DC2626", light: "#FEF2F2" },
        green: { DEFAULT: "#16A34A", light: "#F0FDF6" },
        bg: "#F6F7FB",
        card: "#FFFFFF",
        surface: "#F8FAFC",
        ink: "#1A2238",
        muted: "#5A6478",
        faint: "#98A1B3",
        line: "#E3E7F0",
        overlay: "rgba(20,27,71,0.4)",
      },
      borderRadius: {
        DEFAULT: "12px", sm: "8px", lg: "16px", xl: "20px",
        full: "9999px",
      },
      fontFamily: {
        sans: ["System", "-apple-system", "sans-serif"],
        heading: ["System", "-apple-system", "sans-serif"],
      },
      fontSize: {
        xs: "12px", sm: "14px", base: "16px", lg: "18px",
        xl: "20px", "2xl": "24px", "3xl": "30px", "4xl": "38px",
        "5xl": "44px",
      },
      spacing: {
        0.5: "2px", 1: "4px", 1.5: "6px", 2: "8px", 2.5: "10px",
        3: "12px", 3.5: "14px", 4: "16px", 5: "20px", 6: "24px",
        7: "28px", 8: "32px", 9: "36px", 10: "40px", 12: "48px",
        14: "56px", 16: "64px", 18: "72px", 20: "80px",
      },
      boxShadow: {
        sm: "0 1px 3px rgba(20,27,71,0.06)",
        md: "0 4px 12px rgba(20,27,71,0.08)",
        lg: "0 8px 24px rgba(20,27,71,0.10)",
        xl: "0 12px 32px rgba(20,27,71,0.14)",
        glow: "0 0 20px rgba(232,184,75,0.25)",
      },
    },
  },
  plugins: [],
};
