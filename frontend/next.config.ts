import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  // Static export so the UI can be bundled inside the desktop (Electron) app.
  output: "export",
  trailingSlash: true,
  images: { unoptimized: true },
};

export default nextConfig;
