import { create } from "zustand";
import type { SearchFilters } from "@/types";

interface SearchState {
  filters: SearchFilters;
  recentSearches: string[];
  savedFilters: Array<{ name: string; filters: SearchFilters }>;

  // Actions
  setFilters: (filters: Partial<SearchFilters>) => void;
  resetFilters: () => void;
  setSearch: (search: string) => void;
  setSector: (sector: string | undefined) => void;
  setState: (state: string | undefined) => void;
  setCity: (city: string | undefined) => void;
  setCompanyType: (companyType: string | undefined) => void;
  setRevenueRange: (revenueRange: string | undefined) => void;
  setMembershipFilter: (membershipFilter: string | undefined) => void;
  setVerified: (verified: boolean | undefined) => void;
  setFeatured: (featured: boolean | undefined) => void;
  setPage: (page: number) => void;
  setSort: (sortBy: string, sortOrder: "asc" | "desc") => void;
  addRecentSearch: (search: string) => void;
  clearRecentSearches: () => void;
  saveFilters: (name: string) => void;
  applySavedFilters: (name: string) => void;
  removeSavedFilters: (name: string) => void;
}

const DEFAULT_FILTERS: SearchFilters = {
  page: 1,
  limit: 12,
  sortBy: "createdAt",
  sortOrder: "desc",
};

export const useSearchStore = create<SearchState>((set, get) => ({
  filters: { ...DEFAULT_FILTERS },
  recentSearches: [],
  savedFilters: [],

  setFilters: (partial: Partial<SearchFilters>) => {
    set((state) => ({
      filters: { ...state.filters, ...partial, page: partial.page ?? 1 },
    }));
  },

  resetFilters: () => {
    set({ filters: { ...DEFAULT_FILTERS } });
  },

  setSearch: (search: string) => {
    set((state) => ({
      filters: { ...state.filters, search, page: 1 },
    }));
    if (search.trim()) {
      get().addRecentSearch(search.trim());
    }
  },

  setSector: (sector: string | undefined) => {
    set((state) => ({
      filters: { ...state.filters, sector, page: 1 },
    }));
  },

  setState: (stateVal: string | undefined) => {
    set((state) => ({
      filters: { ...state.filters, state: stateVal, page: 1 },
    }));
  },

  setCity: (city: string | undefined) => {
    set((state) => ({
      filters: { ...state.filters, city, page: 1 },
    }));
  },

  setCompanyType: (companyType: string | undefined) => {
    set((state) => ({
      filters: { ...state.filters, companyType, page: 1 },
    }));
  },

  setRevenueRange: (revenueRange: string | undefined) => {
    set((state) => ({
      filters: { ...state.filters, revenueRange, page: 1 },
    }));
  },

  setMembershipFilter: (membershipFilter: string | undefined) => {
    set((state) => ({
      filters: { ...state.filters, membershipFilter, page: 1 },
    }));
  },

  setVerified: (verified: boolean | undefined) => {
    set((state) => ({
      filters: { ...state.filters, verified, page: 1 },
    }));
  },

  setFeatured: (featured: boolean | undefined) => {
    set((state) => ({
      filters: { ...state.filters, featured, page: 1 },
    }));
  },

  setPage: (page: number) => {
    set((state) => ({
      filters: { ...state.filters, page },
    }));
  },

  setSort: (sortBy: string, sortOrder: "asc" | "desc") => {
    set((state) => ({
      filters: { ...state.filters, sortBy, sortOrder },
    }));
  },

  addRecentSearch: (search: string) => {
    set((state) => {
      const filtered = state.recentSearches.filter(
        (s) => s.toLowerCase() !== search.toLowerCase()
      );
      return {
        recentSearches: [search, ...filtered].slice(0, 10),
      };
    });
  },

  clearRecentSearches: () => {
    set({ recentSearches: [] });
  },

  saveFilters: (name: string) => {
    set((state) => {
      const filtered = state.savedFilters.filter((f) => f.name !== name);
      return {
        savedFilters: [
          ...filtered,
          { name, filters: { ...state.filters, page: 1 } },
        ],
      };
    });
  },

  applySavedFilters: (name: string) => {
    const saved = get().savedFilters.find((f) => f.name === name);
    if (saved) {
      set({ filters: { ...saved.filters } });
    }
  },

  removeSavedFilters: (name: string) => {
    set((state) => ({
      savedFilters: state.savedFilters.filter((f) => f.name !== name),
    }));
  },
}));
