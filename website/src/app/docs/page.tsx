import type { Metadata } from "next";
import { Blocks } from "@/components/Blocks";
import { CodeBlock } from "@/components/CodeBlock";
import { ComponentGrid } from "@/components/ComponentGrid";
import { Logo } from "@/components/Logo";
import { Toc } from "@/components/Toc";
import { components, guide } from "@/lib/data";

export const metadata: Metadata = { title: "Getting started" };

const SECTIONS = [
  { id: "installation", title: "Installation" },
  { id: "version-catalog", title: "Version catalog" },
  { id: "imports", title: "Imports" },
  { id: "theming", title: "Theming" },
  { id: "conventions", title: "Shared conventions" },
  { id: "components", title: "All components" },
];

const IMPORTS = components.map((c) => `import ${c.package}.*`).join("\n");

const THEMING = `MaterialTheme(colorScheme = yourColorScheme) {
    // Every component picks up primary, surface, outline… from here.
    ComposeRatingBar(rating = rating, onRatingChange = { rating = it })
}

// One brand color for the highlights
ComposeRatingBar(
    rating = rating,
    onRatingChange = { rating = it },
    colors = ComposeRatingBarDefaults.accentColors(Color(0xFF0F8A66)),
)

// Or start from the theme's colors and change just one
ComposeRatingBar(
    rating = rating,
    onRatingChange = { rating = it },
    colors = ComposeRatingBarDefaults.colors().copy(inactiveColor = Color.LightGray),
)`;

export default function GettingStarted() {
  return (
    <div className="doc-with-toc">
      <article className="doc">
        <p className="crumbs">Docs / Overview</p>
        <h1>Getting started</h1>
        <p className="lead">
          Compose Components adds the Jetpack Compose components Material 3 doesn&apos;t ship. Each one takes its colors,
          shapes and typography from <code>MaterialTheme</code> and follows the same patterns, so learn them here once,
          then pick a component from the sidebar.
        </p>

        <div className="compose-card">
          <Logo size={48} />
          <p>
            A <strong>Jetpack Compose</strong> library on top of Material 3. Every component is a plain{" "}
            <code>@Composable</code>: no Views, no XML, nothing to initialise.
          </p>
        </div>

        <h2 id="installation">Installation</h2>
        <Blocks blocks={guide.installation.filter((b) => b.type !== "table")} />
        <Blocks blocks={guide.installation.filter((b) => b.type === "table")} />

        <h2 id="version-catalog">Version catalog</h2>
        <p>If your project uses <code>libs.versions.toml</code>, add the library there instead:</p>
        <Blocks blocks={guide.catalog} codeTitle="build.gradle.kts" />

        <h2 id="imports">Imports</h2>
        <p>
          Each component has its own package under <code>com.nameisjayant.components</code>. Import the ones you use:
        </p>
        <CodeBlock code={IMPORTS} title="Imports" />

        <h2 id="theming">Theming</h2>
        <p>
          There&apos;s nothing to set up. The components read <code>MaterialTheme.colorScheme</code>,{" "}
          <code>shapes</code> and <code>typography</code>, so they match your theme, dynamic color and dark theme
          included. To change colors, every component has a <code>XxxDefaults.colors(...)</code> to override single
          colors, an <code>accentColors(accent)</code> to repaint the highlights in one color, and a{" "}
          <code>copy(...)</code> on the colors class.
        </p>
        <CodeBlock code={THEMING} title="Theming.kt" />

        <h2 id="conventions">Shared conventions</h2>
        <Blocks blocks={guide.conventions} />

        <h2 id="components">All components</h2>
        <ComponentGrid items={components} />
      </article>
      <Toc sections={SECTIONS} />
    </div>
  );
}
