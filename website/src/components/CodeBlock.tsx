import { highlight } from "@/lib/highlight";
import { CopyButton } from "./CopyButton";

export function CodeBlock({ code, lang = "kotlin", title }: { code: string; lang?: string; title?: string }) {
  return (
    <figure className="code">
      <figcaption>
        <span className="code-dots" aria-hidden="true"><i /><i /><i /></span>
        <span className="code-title">{title ?? lang}</span>
        <CopyButton text={code} />
      </figcaption>
      <pre>
        <code>
          {highlight(code, lang).map((t, i) =>
            t.kind ? <span key={i} className={`tk-${t.kind}`}>{t.text}</span> : t.text,
          )}
        </code>
      </pre>
    </figure>
  );
}
