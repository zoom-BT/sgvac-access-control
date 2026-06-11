"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { evaluateAccess, type AccessEvent } from "@/lib/api";
import { clearAuth, getToken } from "@/lib/auth";

const REASON_LABELS: Record<string, string> = {
  OK: "Badge valide",
  BADGE_INCONNU: "Badge inconnu",
  BADGE_INACTIF: "Badge inactif",
  BADGE_EXPIRE: "Badge expiré",
  HORS_PLAGE_HORAIRE: "Hors plage horaire",
};

export default function AccessPage() {
  const router = useRouter();
  const [badgeCode, setBadgeCode] = useState("");
  const [checkpoint, setCheckpoint] = useState("Portail principal");
  const [result, setResult] = useState<AccessEvent | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    const token = getToken();
    if (!token) {
      router.replace("/login");
      return;
    }
    setError(null);
    setLoading(true);
    try {
      const event = await evaluateAccess(token, badgeCode.trim(), checkpoint);
      setResult(event);
      setBadgeCode("");
    } catch (err) {
      if (err instanceof Error && err.message.includes("401")) {
        clearAuth();
        router.replace("/login");
        return;
      }
      setError(err instanceof Error ? err.message : "Erreur lors du contrôle");
    } finally {
      setLoading(false);
    }
  }

  const authorized = result?.decision === "AUTHORIZED";

  return (
    <div className="space-y-6">
      <h1 className="font-display text-3xl font-bold tracking-tight">
        Poste de contrôle
      </h1>

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
        <form
          onSubmit={onSubmit}
          className="space-y-5 rounded-xl border border-white/10 bg-white/5 p-6"
        >
          <div>
            <label className="mb-1 block text-sm font-medium text-stone-300">
              Code badge
            </label>
            <input
              type="text"
              value={badgeCode}
              onChange={(e) => setBadgeCode(e.target.value)}
              required
              autoFocus
              placeholder="Ex. B-001"
              className="w-full rounded-lg border border-white/15 bg-white/5 px-3 py-2.5 text-stone-100 outline-none transition focus:border-[#E85D04] focus:ring-1 focus:ring-[#E85D04]"
            />
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-stone-300">
              Point de contrôle
            </label>
            <input
              type="text"
              value={checkpoint}
              onChange={(e) => setCheckpoint(e.target.value)}
              className="w-full rounded-lg border border-white/15 bg-white/5 px-3 py-2.5 text-stone-100 outline-none transition focus:border-[#E85D04] focus:ring-1 focus:ring-[#E85D04]"
            />
          </div>

          {error && (
            <p className="rounded-lg border border-[#9B2226]/50 bg-[#9B2226]/20 px-3 py-2 text-sm text-red-200">
              {error}
            </p>
          )}

          <button
            type="submit"
            disabled={loading}
            className="w-full rounded-lg bg-[#E85D04] px-4 py-2.5 font-semibold text-white transition hover:bg-[#d35400] disabled:cursor-not-allowed disabled:opacity-60"
          >
            {loading ? "Vérification…" : "Vérifier l'accès"}
          </button>
        </form>

        <div className="flex items-center justify-center rounded-xl border border-white/10 bg-white/5 p-6">
          {!result ? (
            <p className="text-center text-stone-500">
              Scannez ou saisissez un code badge pour afficher la décision.
            </p>
          ) : (
            <div
              className="w-full rounded-xl p-6 text-center"
              style={{
                backgroundColor: authorized
                  ? "rgba(45,106,79,0.18)"
                  : "rgba(155,34,38,0.18)",
                border: `1px solid ${authorized ? "#2D6A4F" : "#9B2226"}`,
              }}
            >
              <div
                className="font-display text-3xl font-bold"
                style={{ color: authorized ? "#52b788" : "#e5616b" }}
              >
                {authorized ? "✔ ACCÈS AUTORISÉ" : "✘ ACCÈS REFUSÉ"}
              </div>
              <dl className="mt-5 space-y-2 text-left text-sm">
                <Row label="Badge" value={result.badgeCode} />
                <Row
                  label="Motif"
                  value={REASON_LABELS[result.reason] ?? result.reason}
                />
                <Row label="Point de contrôle" value={result.checkpoint} />
                <Row label="Agent" value={result.agentUsername} />
                <Row
                  label="Horodatage"
                  value={new Date(result.eventTime).toLocaleString("fr-FR")}
                />
              </dl>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

function Row({ label, value }: { label: string; value: string }) {
  return (
    <div className="flex justify-between gap-4 border-b border-white/10 pb-1">
      <dt className="text-stone-400">{label}</dt>
      <dd className="font-medium text-stone-100">{value}</dd>
    </div>
  );
}
