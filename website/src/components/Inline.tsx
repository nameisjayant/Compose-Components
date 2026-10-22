import { Fragment, type ReactNode } from "react";
import { REPO_URL } from "@/lib/data";

/** Renders the README's inline markdown: `code`, **bold**, *italic* and [links](url). */
export function Inline({ text }: { text: string }) {
  const parts: ReactNode[] = [];
  const re = /`([^`]+)`|\*\*([^*]+)\*\*|\*([^*\s][^*]*)\*|\[([^\]]+)\]\(([^)]+)\)/g;
  let last = 0;
  let m: RegExpExecArray | null;
  while ((m = re.exec(text))) {
    parts.push(text.slice(last, m.index));
    if (m[1]) parts.push(<code key={m.index}>{m[1]}</code>);
    else if (m[2]) parts.push(<strong key={m.index}><Inline text={m[2]} /></strong>);
    else if (m[3]) parts.push(<em key={m.index}>{m[3]}</em>);
    else {
      const href = /^https?:/.test(m[5]) ? m[5] : `${REPO_URL}/blob/main/${m[5]}`;
      parts.push(
        <a key={m.index} href={href} target="_blank" rel="noreferrer">
          <Inline text={m[4]} />
        </a>,
      );
    }
    last = re.lastIndex;
  }
  parts.push(text.slice(last));
  return <>{parts.map((p, i) => <Fragment key={i}>{p}</Fragment>)}</>;
}
