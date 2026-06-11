"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { fetchEvents, type AccessEvent } from "@/lib/api";
import { clearAuth, getToken, getUser } from "@/lib/auth";

export default function DashboardPage() {
  const router = useRouter();
  const [events, setEvents] = useState<AccessEvent[] | null>(null);
  const user = getUser();

  useEffect(() => {
    const token = getToken();
    if (!token) return;
    fetchEvents(token)
      .then(setEvents)
      .catch(() => {
        clearAuth();
        router.replace("/login");
      });
  }, [router]);

  const total = events?.length ?? 0;
  const authorized = events?.filter((e) => e.decision === "AUTHORIZED").length ?? 0;
  const denied = events?.filter((e) => e.decision === "DENIED").length ?? 0;

  return (
    <div className="space-y-8">
      <div>
        <h1 className="font-display text-3xl font-bold tracking-tight">
          Tableau de bord
        </h1>
        <p className="mt-1 text-stone-400">
          Connecté en tant que{" "}
          <span className="font-semibold text-[#E85D04]">
            {user?.username}
          </span>{" "}
          · rôle {user?.role}
        </p>
      </div>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
        <StatCard label="Tentatives enregistrées" value={total} accent="#E85D04" />
        <StatCard label="Accès autorisés" value={authorized} accent="#2D6A4F" />
        <StatCard label="Accès refusés" value={denied} accent="#9B2226" />
      </div>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        <ActionCard
          href="/access"
          title="Poste de contrôle"
          desc="Vérifier un badge à un point de contrôle et obtenir la décision d'accès."
        />
        <ActionCard
          href="/logs"
          title="Journal des accès"
          desc="Consulter et filtrer l'historique immuable des tentatives d'accès."
        />
      </div>
    </div>
  );
}

function StatCard({
  label,
  value,
  accent,
}: {
  label: string;
  value: number;
  accent: string;
}) {
  return (
    <div className="rounded-xl border border-white/10 bg-white/5 p-5">
      <div
        className="font-display text-4xl font-bold"
        style={{ color: accent }}
      >
        {value}
      </div>
      <div className="mt-1 text-sm text-stone-400">{label}</div>
    </div>
  );
}

function ActionCard({
  href,
  title,
  desc,
}: {
  href: string;
  title: string;
  desc: string;
}) {
  return (
    <Link
      href={href}
      className="group rounded-xl border border-white/10 bg-white/5 p-6 transition hover:border-[#E85D04]/60 hover:bg-white/10"
    >
      <h2 className="font-display text-xl font-semibold text-stone-100 group-hover:text-[#E85D04]">
        {title}
      </h2>
      <p className="mt-2 text-sm text-stone-400">{desc}</p>
    </Link>
  );
}
