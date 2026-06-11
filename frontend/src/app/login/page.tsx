"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { ChantierBackground } from "@/components/ChantierBackground";
import { login } from "@/lib/api";
import { saveAuth } from "@/lib/auth";

export default function LoginPage() {
  const router = useRouter();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const res = await login(username.trim(), password);
      saveAuth(res.token, { username: res.username, role: res.role });
      router.push("/dashboard");
    } catch (err) {
      setError(
        err instanceof Error ? err.message : "Échec de la connexion"
      );
      setLoading(false);
    }
  }

  return (
    <ChantierBackground variant="portail" className="flex items-center justify-center px-4">
      <div className="w-full max-w-md rounded-2xl border border-white/10 bg-black/55 p-8 shadow-2xl backdrop-blur-md">
        <div className="mb-8 text-center">
          <h1 className="font-display text-4xl font-bold tracking-tight text-[#E85D04]">
            SGVAC
          </h1>
          <p className="mt-1 text-sm text-stone-300">
            Gestion des accès chantier
          </p>
        </div>

        <form onSubmit={onSubmit} className="space-y-5">
          <div>
            <label className="mb-1 block text-sm font-medium text-stone-300">
              Identifiant
            </label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
              autoFocus
              className="w-full rounded-lg border border-white/15 bg-white/5 px-3 py-2.5 text-stone-100 outline-none transition focus:border-[#E85D04] focus:ring-1 focus:ring-[#E85D04]"
            />
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-stone-300">
              Mot de passe
            </label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
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
            {loading ? "Connexion…" : "Se connecter"}
          </button>
        </form>

        <div className="mt-6 rounded-lg border border-white/10 bg-white/5 px-4 py-3 text-xs text-stone-400">
          <p className="mb-1 font-semibold text-stone-300">Comptes de démonstration</p>
          <p>admin / admin123! — agent / agent123!</p>
        </div>
      </div>
    </ChantierBackground>
  );
}
