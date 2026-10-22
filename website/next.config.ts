import type { NextConfig } from "next";

// Static export, so the site can be hosted on GitHub Pages or any static host.
// For a project page (username.github.io/Compose-Components), build with BASE_PATH=/Compose-Components.
const basePath = process.env.BASE_PATH ?? "";

const nextConfig: NextConfig = {
  output: "export",
  basePath,
  trailingSlash: true,
  images: { unoptimized: true },
  env: { NEXT_PUBLIC_BASE_PATH: basePath },
  // Lets `next dev` hydrate when opened from another device on the LAN (e.g. a phone).
  allowedDevOrigins: ["192.168.*.*", "10.*.*.*"],
};

export default nextConfig;
