import raw from "@/data/components.json";

export type Block =
  | { type: "p" | "note" | "h"; text: string }
  | { type: "code"; lang: string; code: string }
  | { type: "table"; head: string[]; rows: string[][] };

export interface Gif { file: string; alt: string; caption: string }

export interface Section { id: string; title: string; blocks: Block[] }

export interface Component {
  composable: string;
  slug: string;
  name: string;
  card: string;
  tags: string[];
  concepts: string[];
  package: string;
  summary: string;
  gifs: Gif[];
  sections: Section[];
  params: Block[];
  signature: string;
}

interface Data {
  version: string;
  guide: { installation: Block[]; catalog: Block[]; conventions: Block[] };
  components: Component[];
}

const data = raw as Data;

export const version = data.version;
export const components = data.components;
export const guide = data.guide;

export const REPO_URL = "https://github.com/nameisjayant/Compose-Components";
export const MAVEN_URL = "https://central.sonatype.com/artifact/io.github.nameisjayant/compose-components";
export const DEPENDENCY = `implementation("io.github.nameisjayant:compose-components:${version}")`;

export function componentHref(slug: string): string {
  return `/docs/components/${slug}/`;
}

export function getComponent(slug: string): Component | undefined {
  return components.find((c) => c.slug === slug);
}

/** Prefixes a public/ path with the deploy base path (empty unless BASE_PATH is set). */
export function asset(path: string): string {
  return `${process.env.NEXT_PUBLIC_BASE_PATH ?? ""}${path}`;
}

export function gif(file: string): string {
  return asset(`/gifs/${file}.gif`);
}
