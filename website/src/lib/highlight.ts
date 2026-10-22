// A small Kotlin / Gradle / TOML tokenizer, enough for the snippets on this site.
// Runs at build time, so pages ship as plain HTML with no highlighting library.

export type Token = { kind: string | null; text: string };

const KEYWORDS = new Set([
  "val", "var", "fun", "class", "object", "data", "private", "public", "internal", "if", "else",
  "when", "return", "by", "import", "package", "true", "false", "null", "it", "this", "for", "in",
]);

const RULES: [string, RegExp][] = [
  ["comment", /^(\/\/[^\n]*|#[^\n]*|\/\*[\s\S]*?\*\/)/],
  ["string", /^"(?:\\.|[^"\\])*"/],
  ["number", /^(0x[0-9A-Fa-f]+|\d+(\.\d+)?)(\.dp|\.sp|f|L)?\b/],
  ["annotation", /^@\w+/],
  ["word", /^[A-Za-z_]\w*/],
  ["space", /^\s+/],
  ["punct", /^[\s\S]/],
];

export function highlight(code: string, lang: string): Token[] {
  const out: Token[] = [];
  let rest = code;
  while (rest) {
    for (const [kind, re] of RULES) {
      const m = re.exec(rest);
      if (!m) continue;
      const text = m[0];
      rest = rest.slice(text.length);
      if (kind === "comment" && text.startsWith("#") && lang !== "toml" && lang !== "properties" && lang !== "bash") {
        // '#' is not a comment in Kotlin; emit it as punctuation and re-scan the rest.
        rest = text.slice(1) + rest;
        push(out, "punct", "#");
      } else if (kind === "word") {
        const next = rest.trimStart()[0];
        if (KEYWORDS.has(text)) push(out, "keyword", text);
        else if (/^[A-Z]/.test(text)) push(out, "type", text);
        else if (next === "(" || next === "{") push(out, "fn", text);
        else if (next === "=" && rest.trimStart()[1] !== "=") push(out, "param", text);
        else push(out, null, text);
      } else {
        push(out, kind === "space" || kind === "punct" ? null : kind, text);
      }
      break;
    }
  }
  return out;
}

function push(out: Token[], kind: string | null, text: string) {
  const last = out[out.length - 1];
  if (last && last.kind === kind && kind === null) last.text += text;
  else out.push({ kind, text });
}
