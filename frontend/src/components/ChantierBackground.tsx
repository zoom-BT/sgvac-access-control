"use client";

type Variant = "global" | "portail";

const IMAGES: Record<Variant, string> = {
  global: "/images/chantier-bg.jpg",
  portail: "/images/portail-bg.jpg",
};

export function ChantierBackground({
  variant = "global",
  children,
  className = "",
}: {
  variant?: Variant;
  children: React.ReactNode;
  className?: string;
}) {
  const bg = IMAGES[variant];

  return (
    <div
      className={`relative min-h-screen ${className}`}
      style={{
        backgroundImage: `
          linear-gradient(135deg, rgba(28,28,30,0.88) 0%, rgba(28,28,30,0.72) 50%, rgba(232,93,4,0.15) 100%),
          url('${bg}')
        `,
        backgroundSize: "cover",
        backgroundPosition: "center",
        backgroundColor: "#1C1C1E",
      }}
    >
      <div className="absolute inset-0 bg-[url('/images/texture-overlay.png')] opacity-20 pointer-events-none" />
      <div className="relative z-10">{children}</div>
    </div>
  );
}
