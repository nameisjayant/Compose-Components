import type { Metadata, Viewport } from "next";
import { Inter, JetBrains_Mono } from "next/font/google";
import { Header } from "@/components/Header";
import { asset, version } from "@/lib/data";
import "./globals.css";

const sans = Inter({ subsets: ["latin"], variable: "--font-sans", display: "swap" });
const mono = JetBrains_Mono({ subsets: ["latin"], variable: "--font-mono", display: "swap" });

export const metadata: Metadata = {
  title: { default: "Compose Components", template: "%s · Compose Components" },
  description: "The Jetpack Compose components Material 3 doesn't ship: date picker, wheel picker, OTP input, rating bar, timeline and color picker.",
  icons: { icon: asset("/compose-logo.png") },
};

export const viewport: Viewport = {
  themeColor: [
    { media: "(prefers-color-scheme: light)", color: "#F3F8FC" },
    { media: "(prefers-color-scheme: dark)", color: "#04161B" },
  ],
};

// Applies the saved theme before first paint, so there is no light flash in dark mode.
const themeScript = `try{var t=localStorage.getItem("theme");if(t==="light"||t==="dark")document.documentElement.dataset.theme=t}catch(e){}`;

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en" className={`${sans.variable} ${mono.variable}`} suppressHydrationWarning>
      <head>
        <script dangerouslySetInnerHTML={{ __html: themeScript }} />
      </head>
      <body>
        <a href="#main" className="skip-link">Skip to content</a>
        <Header version={version} />
        {children}
      </body>
    </html>
  );
}
