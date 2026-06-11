"use client";

import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { clearAuth, getUser } from "@/lib/auth";

const links = [
  { href: "/dashboard", label: "Tableau de bord" },
  { href: "/access", label: "Poste de contrôle" },
  { href: "/logs", label: "Journal" },
];

export function Navbar() {
  const pathname = usePathname();
  const router = useRouter();
  const user = getUser();

  function logout() {
    clearAuth();
    router.push("/login");
  }

  return (
    <header className="border-b border-white/10 bg-black/40 backdrop-blur-md">
      <div className="mx-auto flex max-w-6xl items-center justify-between px-4 py-3">
        <div className="flex items-center gap-3">
          <span className="text-xl font-bold tracking-tight text-[#E85D04]">
            SGVAC
          </span>
          <span className="hidden text-sm text-stone-400 sm:inline">
            Gestion des accès chantier
          </span>
        </div>
        <nav className="flex items-center gap-1 sm:gap-4">
          {links.map((l) => (
            <Link
              key={l.href}
              href={l.href}
              className={`rounded px-3 py-1.5 text-sm transition ${
                pathname === l.href
                  ? "bg-[#E85D04] text-white"
                  : "text-stone-300 hover:bg-white/10"
              }`}
            >
              {l.label}
            </Link>
          ))}
          {user && (
            <button
              onClick={logout}
              className="ml-2 rounded border border-white/20 px-3 py-1.5 text-sm text-stone-300 hover:bg-white/10"
            >
              {user.username} · Déconnexion
            </button>
          )}
        </nav>
      </div>
    </header>
  );
}
