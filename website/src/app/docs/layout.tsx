import { DocsSidebar } from "@/components/DocsSidebar";
import { components } from "@/lib/data";

export default function DocsLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="docs">
      <DocsSidebar components={components.map(({ slug, name, composable, tags }) => ({ slug, name, composable, tags }))} />
      <main id="main" className="docs-main">{children}</main>
    </div>
  );
}
