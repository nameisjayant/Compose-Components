"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { gif } from "@/lib/data";

interface Item { href: string; name: string; file: string }

const INTERVAL = 5200;

/** A phone-shaped frame that cycles through one recording of each component. */
export function Showcase({ items }: { items: Item[] }) {
  const [index, setIndex] = useState(0);

  // Always advances; a dot click restarts the timer since it changes `index`.
  useEffect(() => {
    const t = setTimeout(() => setIndex((i) => (i + 1) % items.length), INTERVAL);
    return () => clearTimeout(t);
  }, [index, items.length]);

  const item = items[index];
  return (
    <div className="showcase">
      <div className="phone">
        <div className="phone-notch" aria-hidden="true" />
        <div className="phone-screen">
          {/* eslint-disable-next-line @next/next/no-img-element */}
          <img key={item.file} className="phone-gif" src={gif(item.file)} alt={`${item.name} demo`} />
        </div>
      </div>
      <div className="showcase-caption" aria-live="polite">
        <Link href={item.href} className="showcase-name">{item.name}</Link>
        <div className="showcase-dots" role="tablist" aria-label="Components">
          {items.map((it, i) => (
            <button
              key={it.file}
              type="button"
              role="tab"
              aria-selected={i === index}
              aria-label={it.name}
              className={i === index ? "dot active" : "dot"}
              onClick={() => setIndex(i)}
            />
          ))}
        </div>
      </div>
    </div>
  );
}
