import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { companiesApi } from "@/services/api";
import { useSearchStore } from "@/store/searchStore";
import type { Company, CompanyResponse, SearchFilters } from "@/types";

// ─────────────────────── Query Keys ───────────────────────

export const companyKeys = {
  all: ["companies"] as const,
  lists: () => [...companyKeys.all, "list"] as const,
  list: (filters: SearchFilters) => [...companyKeys.lists(), filters] as const,
  details: () => [...companyKeys.all, "detail"] as const,
  detail: (id: string) => [...companyKeys.details(), id] as const,
  publicDetail: (publicId: string) =>
    [...companyKeys.all, "public", publicId] as const,
};

// ─────────────────────── Search Hook ───────────────────────

export function useCompanySearch(enabled = true) {
  const filters = useSearchStore((state) => state.filters);

  return useQuery({
    queryKey: companyKeys.list(filters),
    queryFn: async () => {
      const response = await companiesApi.search(filters);
      return response;
    },
    enabled,
    staleTime: 1000 * 60 * 2, // 2 minutes
    placeholderData: (previousData) => previousData,
  });
}

// ─────────────────────── Detail Hook ───────────────────────

export function useCompanyDetail(id: string | undefined) {
  return useQuery({
    queryKey: companyKeys.detail(id ?? ""),
    queryFn: async () => {
      if (!id) throw new Error("Company ID is required");
      const response = await companiesApi.getById(id);
      return response.data as CompanyResponse;
    },
    enabled: !!id,
    staleTime: 1000 * 60 * 5, // 5 minutes
  });
}

// ─────────────────────── Public Detail Hook (by DoB ID) ───────────────────────

export function useCompanyByPublicId(publicCompanyId: string | undefined) {
  return useQuery({
    queryKey: companyKeys.publicDetail(publicCompanyId ?? ""),
    queryFn: async () => {
      if (!publicCompanyId) throw new Error("Public company ID is required");
      const response = await companiesApi.getByPublicCompanyId(publicCompanyId);
      return response.data as CompanyResponse;
    },
    enabled: !!publicCompanyId,
    staleTime: 1000 * 60 * 5,
  });
}

// ─────────────────────── Create Company Mutation ───────────────────────

export function useCreateCompany() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (payload: FormData | Record<string, unknown>) => {
      const response = await companiesApi.create(payload);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: companyKeys.lists() });
    },
  });
}

// ─────────────────────── Update Company Mutation ───────────────────────

export function useUpdateCompany() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({
      id,
      payload,
    }: {
      id: string;
      payload: FormData | Record<string, unknown>;
    }) => {
      const response = await companiesApi.update(id, payload);
      return response.data;
    },
    onSuccess: (data: Company) => {
      queryClient.invalidateQueries({ queryKey: companyKeys.lists() });
      queryClient.invalidateQueries({ queryKey: companyKeys.detail(data.id) });
    },
  });
}

// ─────────────────────── Upload Financials Mutation ───────────────────────

export function useUploadFinancials() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({
      companyId,
      payload,
    }: {
      companyId: string;
      payload: FormData;
    }) => {
      const response = await companiesApi.uploadFinancials(companyId, payload);
      return response.data;
    },
    onSuccess: (data: Company) => {
      queryClient.invalidateQueries({ queryKey: companyKeys.detail(data.id) });
    },
  });
}

// ─────────────────────── Upload Certificate Mutation ───────────────────────

export function useUploadCertificate() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({
      companyId,
      payload,
    }: {
      companyId: string;
      payload: FormData;
    }) => {
      const response = await companiesApi.uploadCertificate(companyId, payload);
      return response.data;
    },
    onSuccess: (data: Company) => {
      queryClient.invalidateQueries({ queryKey: companyKeys.detail(data.id) });
    },
  });
}

// ─────────────────────── Upload Video Mutation ───────────────────────

export function useUploadVideo() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({
      companyId,
      payload,
    }: {
      companyId: string;
      payload: FormData;
    }) => {
      const response = await companiesApi.uploadVideo(companyId, payload);
      return response.data;
    },
    onSuccess: (data: Company) => {
      queryClient.invalidateQueries({ queryKey: companyKeys.detail(data.id) });
    },
  });
}
