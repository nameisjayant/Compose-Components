"use client";

import { usePathname } from "next/navigation";
import { useLayoutEffect, useRef } from "react";
import { docsPage, navDirection } from "@/lib/docsNav";

// A template remounts on every navigation, so the entrance animation replays each time a page is picked.
export default function DocsTemplate({ children }: { children: React.ReactNode }) {
  const ref = useRef<HTMLDivElement>(null);
  const page = docsPage(usePathname());

  useLayoutEffect(() => {
    ref.current?.setAttribute("data-dir", navDirection(page));
  }, [page]);

  return <div ref={ref} className="page-enter">{children}</div>;
}
