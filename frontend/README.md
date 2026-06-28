# DataOfBusiness Frontend

React Native app built with Expo and TypeScript.

## Tech Stack

- **Framework**: Expo SDK (managed workflow)
- **Navigation**: Expo Router (file-based)
- **State**: Zustand + TanStack Query (React Query)
- **HTTP**: Axios with JWT interceptor
- **Forms**: React Hook Form + Yup
- **Styling**: NativeWind (Tailwind CSS)
- **Auth Tokens**: expo-secure-store

## Project Structure

```
src/
├── app/                 # Expo Router file-based routes
│   ├── (public)/        # Home, Pricing, Companies, legal pages
│   ├── (auth)/          # Login, Register (no OTP)
│   ├── (member)/        # Research member dashboard
│   ├── (company)/       # Company user dashboard
│   └── (admin)/         # Admin panel
├── components/          # Reusable UI components
├── services/            # Axios API client
├── store/               # Zustand auth & search stores
├── hooks/               # useAuth, useCompanies
├── theme/               # Design system colors
└── types/               # TypeScript interfaces
```

## Setup

```bash
npm install
npx expo start
```

## Design System

- Navy: `#1E2761` — Primary brand color
- Gold: `#E8B84B` — Accent / CTA
- Background: `#F6F7FB`
- Border radius: 12px (default), 8px (small)
