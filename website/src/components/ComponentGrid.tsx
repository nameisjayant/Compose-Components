import Link from "next/link";
import { componentHref, gif, type Component } from "@/lib/data";

/** A card per component: its first recording, name and one-line description. */
export function ComponentGrid({ items, headingLevel = "h3" }: { items: Component[]; headingLevel?: "h2" | "h3" }) {
  const Heading = headingLevel;
  return (
    <ul className="comp-grid">
      {items.map((c) => (
        <li key={c.slug}>
          <Link href={componentHref(c.slug)} className="comp-card">
            <span className="comp-card-media">
              {/* eslint-disable-next-line @next/next/no-img-element */}
              <img src={gif(c.gifs[0].file)} alt="" loading="lazy" decoding="async" />
            </span>
            <span className="comp-card-body">
              <Heading className="comp-card-name">{c.name}</Heading>
              <code className="comp-card-api">{c.composable}</code>
              <span className="comp-card-text">{c.card}</span>
            </span>
          </Link>
        </li>
      ))}
    </ul>
  );
}
