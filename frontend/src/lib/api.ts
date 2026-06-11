const API_URL = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

export type LoginResponse = {
  token: string;
  username: string;
  role: string;
};

export type AccessEvent = {
  id: number;
  badgeCode: string;
  decision: "AUTHORIZED" | "DENIED";
  reason: string;
  checkpoint: string;
  eventTime: string;
  agentUsername: string;
};

async function apiFetch<T>(
  path: string,
  options: RequestInit = {},
  token?: string | null
): Promise<T> {
  const headers: Record<string, string> = {
    "Content-Type": "application/json",
    ...(options.headers as Record<string, string>),
  };
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  const res = await fetch(`${API_URL}${path}`, { ...options, headers });
  if (!res.ok) {
    const body = await res.json().catch(() => ({ message: "Erreur réseau" }));
    throw new Error(body.message ?? `HTTP ${res.status}`);
  }
  return res.json();
}

export async function login(username: string, password: string) {
  return apiFetch<LoginResponse>("/api/v1/auth/login", {
    method: "POST",
    body: JSON.stringify({ username, password }),
  });
}

export async function evaluateAccess(
  token: string,
  badgeCode: string,
  checkpoint = "Portail principal"
) {
  return apiFetch<AccessEvent>(
    "/api/v1/access/evaluate",
    {
      method: "POST",
      body: JSON.stringify({ badgeCode, checkpoint }),
    },
    token
  );
}

export async function fetchEvents(
  token: string,
  params?: { badgeCode?: string; decision?: string }
) {
  const query = new URLSearchParams();
  if (params?.badgeCode) query.set("badgeCode", params.badgeCode);
  if (params?.decision) query.set("decision", params.decision);
  const qs = query.toString();
  return apiFetch<AccessEvent[]>(
    `/api/v1/access/events${qs ? `?${qs}` : ""}`,
    {},
    token
  );
}
