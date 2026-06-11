"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { Navbar } from "@/components/Navbar";
import { ChantierBackground } from "@/components/ChantierBackground";
import { getToken } from "@/lib/auth";

export default function AppLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const router = useRouter();
  const [ready, setReady] = useState(false);

  useEffect(() => {
    if (!getToken()) {
      router.replace("/login");
      return;
    }
    setReady(true);
  }, [router]);

  if (!ready) {
    return (
      <div className="flex min-h-screen items-center justify-center text-stone-400">
        Chargement…
      </div>
    );
  }

  return (
    <ChantierBackground variant="global">
      <Navbar />
      <main className="mx-auto max-w-6xl px-4 py-8">{children}</main>
    </ChantierBackground>
  );
}
