// Sidebar order of doc pages (paths under /docs, "" is Getting started), so the page
// transition can slide forward or back depending on where the reader came from.
let order: string[] = [];
let lastPage: string | null = null;
let lastDir: "forward" | "back" = "forward";

export function setDocsOrder(pages: string[]) {
  order = pages;
}

export function navDirection(page: string): "forward" | "back" {
  // Same page again means a re-run of the effect (Strict Mode), not a new navigation.
  if (page !== lastPage) {
    if (lastPage !== null) lastDir = order.indexOf(page) < order.indexOf(lastPage) ? "back" : "forward";
    lastPage = page;
  }
  return lastDir;
}

/** "/docs/components/timeline/" → "components/timeline"; "/docs/" → "". */
export function docsPage(pathname: string): string {
  return pathname.split("/").filter(Boolean).slice(1).join("/");
}
