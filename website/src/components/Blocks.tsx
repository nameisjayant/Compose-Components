import type { Block } from "@/lib/data";
import { CodeBlock } from "./CodeBlock";
import { Inline } from "./Inline";

/** Renders README blocks (paragraphs, subheadings, notes, code, tables) pulled out by the generator. */
export function Blocks({ blocks, codeTitle: title }: { blocks: Block[]; codeTitle?: string }) {
  return (
    <>
      {blocks.map((b, i) => {
        switch (b.type) {
          case "p":
            return <p key={i}><Inline text={b.text} /></p>;
          case "h":
            return <h3 key={i}><code>{b.text.replace(/`/g, "")}</code></h3>;
          case "note":
            return <p key={i} className="callout"><Inline text={b.text} /></p>;
          case "code":
            return <CodeBlock key={i} code={b.code} lang={b.lang} title={codeTitle(b.code, b.lang, title)} />;
          case "table":
            return (
              <div key={i} className="table-wrap">
                <table>
                  <thead><tr>{b.head.map((h, k) => <th key={k}><Inline text={h} /></th>)}</tr></thead>
                  <tbody>{b.rows.map((r, j) => <tr key={j}>{r.map((c, k) => <td key={k}><Inline text={c} /></td>)}</tr>)}</tbody>
                </table>
              </div>
            );
        }
      })}
    </>
  );
}

/** Uses a leading `// file.name` comment as the block's title. */
function codeTitle(code: string, lang: string, fallback?: string): string {
  const m = /^\/\/ (\S+\.\w+)/.exec(code);
  if (m) return m[1];
  if (lang === "toml") return "libs.versions.toml";
  if (lang === "bash") return "Terminal";
  return lang === "kotlin" ? (fallback ?? "Kotlin") : lang;
}
