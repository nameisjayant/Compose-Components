"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { Logo } from "./Logo";
import { REPO_URL } from "@/lib/data";
import { ThemeToggle } from "./ThemeToggle";

export function Header({ version }: { version: string }) {
  const path = usePathname();
  const onDocs = path.startsWith("/docs");
  return (
    <header className="site-header">
      <div className="header-inner">
        <Link href="/" className="brand" aria-label="Compose Components home">
          <Logo size={34} />
          <span className="brand-name">Compose<span> Components</span></span>
          <span className="version-pill">v{version}</span>
        </Link>
        <nav className="tabs" aria-label="Main">
          <Link href="/" className={!onDocs ? "tab active" : "tab"} aria-current={!onDocs ? "page" : undefined}>Home</Link>
          <Link href="/docs/" className={onDocs ? "tab active" : "tab"} aria-current={onDocs ? "page" : undefined}>Docs</Link>
        </nav>
        <div className="header-actions">
          <a className="icon-btn" href={REPO_URL} target="_blank" rel="noreferrer" aria-label="GitHub repository">
            <svg viewBox="0 0 24 24" width="20" height="20" aria-hidden="true">
              <path fill="currentColor" d="M12 2a10 10 0 0 0-3.16 19.49c.5.09.68-.22.68-.48v-1.7c-2.78.6-3.37-1.34-3.37-1.34-.45-1.16-1.11-1.47-1.11-1.47-.91-.62.07-.6.07-.6 1 .07 1.53 1.03 1.53 1.03.9 1.52 2.34 1.08 2.91.83.09-.65.35-1.09.63-1.34-2.22-.25-4.55-1.11-4.55-4.94 0-1.09.39-1.98 1.03-2.68-.1-.25-.45-1.27.1-2.64 0 0 .84-.27 2.75 1.02a9.5 9.5 0 0 1 5 0c1.91-1.29 2.75-1.02 2.75-1.02.55 1.37.2 2.39.1 2.64.64.7 1.03 1.59 1.03 2.68 0 3.84-2.34 4.68-4.57 4.93.36.31.68.92.68 1.85v2.74c0 .27.18.58.69.48A10 10 0 0 0 12 2z" />
            </svg>
          </a>
          <ThemeToggle />
        </div>
      </div>
    </header>
  );
}
