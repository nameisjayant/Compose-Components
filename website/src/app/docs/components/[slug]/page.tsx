import type { Metadata } from "next";
import Link from "next/link";
import { notFound } from "next/navigation";
import { Blocks } from "@/components/Blocks";
import { CodeBlock } from "@/components/CodeBlock";
import { GifGallery } from "@/components/GifGallery";
import { Inline } from "@/components/Inline";
import { Toc } from "@/components/Toc";
import { componentHref, components, getComponent } from "@/lib/data";

type Props = { params: Promise<{ slug: string }> };

export const dynamicParams = false;

export function generateStaticParams() {
  return components.map((c) => ({ slug: c.slug }));
}

export async function generateMetadata({ params }: Props): Promise<Metadata> {
  const c = getComponent((await params).slug);
  return c ? { title: c.composable, description: c.card } : {};
}

export default async function ComponentPage({ params }: Props) {
  const c = getComponent((await params).slug);
  if (!c) notFound();

  const i = components.indexOf(c);
  const prev = components[i - 1];
  const next = components[i + 1];
  const toc = [
    { id: "preview", title: "Preview" },
    { id: "how-it-works", title: "How it works" },
    { id: "setup", title: "Setup" },
    ...c.sections.map((s) => ({ id: s.id, title: s.title })),
    ...(c.params.length ? [{ id: "parameters", title: "All parameters" }] : []),
    { id: "api", title: "API" },
  ];

  return (
    <div className="doc-with-toc">
      <article className="doc">
        <p className="crumbs">
          Docs / Components / <span>{String(i + 1).padStart(2, "0")}</span>
        </p>
        <h1>{c.composable}</h1>
        <p className="lead"><Inline text={c.summary} /></p>
        <ul className="tags" aria-label="Traits">
          {c.tags.map((t) => <li key={t}>{t}</li>)}
        </ul>

        <h2 id="preview">Preview</h2>
        <GifGallery gifs={c.gifs} />

        <h2 id="how-it-works">How it works</h2>
        <ul className="concepts">
          {c.concepts.map((t, k) => <li key={k}><Inline text={t} /></li>)}
        </ul>

        <h2 id="setup">Setup</h2>
        <p>
          Add the library once (see <Link href="/docs/">Getting started</Link>), then import from{" "}
          <code>{c.package}</code>:
        </p>
        <CodeBlock code={`import ${c.package}.*`} title="Imports" />

        {c.sections.map((s) => (
          <section key={s.id} className="doc-section" aria-labelledby={s.id}>
            <h2 id={s.id}>{s.title}</h2>
            <Blocks blocks={s.blocks} codeTitle={`${c.composable}.kt`} />
          </section>
        ))}

        {c.params.length > 0 && (
          <>
            <h2 id="parameters">All parameters</h2>
            <Blocks blocks={c.params} />
          </>
        )}

        <h2 id="api">API</h2>
        <p>The public signatures, straight from the library sources:</p>
        <CodeBlock code={c.signature} title={c.package} />

        <nav className="pager" aria-label="More components">
          {prev ? (
            <Link href={componentHref(prev.slug)} className="pager-link">
              <span>Previous</span>
              {prev.name}
            </Link>
          ) : (
            <Link href="/docs/" className="pager-link">
              <span>Previous</span>
              Getting started
            </Link>
          )}
          {next && (
            <Link href={componentHref(next.slug)} className="pager-link next">
              <span>Next</span>
              {next.name}
            </Link>
          )}
        </nav>
      </article>
      <Toc sections={toc} />
    </div>
  );
}
