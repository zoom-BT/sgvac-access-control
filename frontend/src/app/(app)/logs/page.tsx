"use client";

import { useCallback, useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { fetchEvents, type AccessEvent } from "@/lib/api";
import { clearAuth, getToken } from "@/lib/auth";

const REASON_LABELS: Record<string, string> = {
  OK: "Badge valide",
  BADGE_INCONNU: "Badge inconnu",
  BADGE_INACTIF: "Badge inactif",
  BADGE_EXPIRE: "Badge expiré",
  HORS_PLAGE_HORAIRE: "Hors plage horaire",
};

export default function LogsPage() {
  const router = useRouter();
  const [events, setEvents] = useState<AccessEvent[]>([]);
  const [badgeCode, setBadgeCode] = useState("");
  const [decision, setDecision] = useState("");
  const [loading, setLoading] = useState(true);

  const load = useCallback(
    async (params?: { badgeCode?: string; decision?: string }) => {
      const token = getToken();
      if (!token) {
        router.replace("/login");
        return;
      }
      setLoading(true);
      try {
        const data = await fetchEvents(token, params);
        setEvents(data);
      } catch (err) {
        if (err instanceof Error && err.message.includes("401")) {
          clearAuth();
          router.replace("/login");
        }
      } finally {
        setLoading(false);
      }
    },
    [router]
  );

  useEffect(() => {
    load();
  }, [load]);

  function onFilter(e: React.FormEvent) {
    e.preventDefault();
    load({
      badgeCode: badgeCode.trim() || undefined,
      decision: decision || undefined,
    });
  }

  function reset() {
    setBadgeCode("");
    setDecision("");
    load();
  }

  return (
    <div className="space-y-6">
      <h1 className="font-display text-3xl font-bold tracking-tight">
        Journal des accès
      </h1>

      <form
        onSubmit={onFilter}
        className="flex flex-wrap items-end gap-3 rounded-xl border border-white/10 bg-white/5 p-4"
      >
        <div>
          <label className="mb-1 block text-xs font-medium text-stone-400">
            Badge
          </label>
          <input
            type="text"
            value={badgeCode}
            onChange={(e) => setBadgeCode(e.target.value)}
            placeholder="Ex. B-001"
            className="rounded-lg border border-white/15 bg-white/5 px-3 py-2 text-sm text-stone-100 outline-none focus:border-[#E85D04]"
          />
        </div>
        <div>
          <label className="mb-1 block text-xs font-medium text-stone-400">
            Décision
          </label>
          <select
            value={decision}
            onChange={(e) => setDecision(e.target.value)}
            className="rounded-lg border border-white/15 bg-white/5 px-3 py-2 text-sm text-stone-100 outline-none focus:border-[#E85D04]"
          >
            <option value="">— Toutes —</option>
            <option value="AUTHORIZED">Autorisé</option>
            <option value="DENIED">Refusé</option>
          </select>
        </div>
        <button
          type="submit"
          className="rounded-lg bg-[#E85D04] px-4 py-2 text-sm font-semibold text-white transition hover:bg-[#d35400]"
        >
          Filtrer
        </button>
        <button
          type="button"
          onClick={reset}
          className="rounded-lg border border-white/20 px-4 py-2 text-sm text-stone-300 transition hover:bg-white/10"
        >
          Réinitialiser
        </button>
      </form>

      <div className="overflow-x-auto rounded-xl border border-white/10">
        <table className="w-full text-left text-sm">
          <thead className="bg-white/5 text-xs uppercase tracking-wide text-stone-400">
            <tr>
              <th className="px-4 py-3">Date / heure</th>
              <th className="px-4 py-3">Badge</th>
              <th className="px-4 py-3">Décision</th>
              <th className="px-4 py-3">Motif</th>
              <th className="px-4 py-3">Point de contrôle</th>
              <th className="px-4 py-3">Agent</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-white/5">
            {loading ? (
              <tr>
                <td colSpan={6} className="px-4 py-8 text-center text-stone-500">
                  Chargement…
                </td>
              </tr>
            ) : events.length === 0 ? (
              <tr>
                <td colSpan={6} className="px-4 py-8 text-center text-stone-500">
                  Aucun événement.
                </td>
              </tr>
            ) : (
              events.map((e) => (
                <tr key={e.id} className="hover:bg-white/5">
                  <td className="whitespace-nowrap px-4 py-3 text-stone-300">
                    {new Date(e.eventTime).toLocaleString("fr-FR")}
                  </td>
                  <td className="px-4 py-3 font-medium text-stone-100">
                    {e.badgeCode}
                  </td>
                  <td className="px-4 py-3">
                    <span
                      className="rounded-full px-2.5 py-0.5 text-xs font-semibold"
                      style={{
                        backgroundColor:
                          e.decision === "AUTHORIZED"
                            ? "rgba(45,106,79,0.25)"
                            : "rgba(155,34,38,0.25)",
                        color:
                          e.decision === "AUTHORIZED" ? "#52b788" : "#e5616b",
                      }}
                    >
                      {e.decision === "AUTHORIZED" ? "Autorisé" : "Refusé"}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-stone-300">
                    {REASON_LABELS[e.reason] ?? e.reason}
                  </td>
                  <td className="px-4 py-3 text-stone-300">{e.checkpoint}</td>
                  <td className="px-4 py-3 text-stone-300">{e.agentUsername}</td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
