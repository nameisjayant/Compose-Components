import Link from "next/link";
import { Blocks } from "@/components/Blocks";
import { CodeBlock } from "@/components/CodeBlock";
import { ComponentGrid } from "@/components/ComponentGrid";
import { CopyButton } from "@/components/CopyButton";
import { Logo } from "@/components/Logo";
import { Showcase } from "@/components/Showcase";
import { componentHref, components, DEPENDENCY, guide, MAVEN_URL, REPO_URL, version, type Block } from "@/lib/data";

const FEATURES = [
  { icon: "M12 3a9 9 0 1 0 0 18V3z", title: "Theme first", text: "Colors, shapes and type come from MaterialTheme, so every component matches your app, dynamic color included." },
  { icon: "M12 4a8 8 0 1 0 8 8c0-1.5-1.2-2-2.4-2H15a2 2 0 0 1-2-2V6.4C13 5.2 13.5 4 12 4z", title: "One accent", text: "accentColors(accent) repaints the highlights in your brand color. Every colors class also has copy(...)." },
  { icon: "M4 6h16M4 12h10M4 18h16", title: "Slots for custom drawing", text: "dayContent, cellContent, itemContent and indicator hand you the full state of each item to draw it yourself." },
  { icon: "M4 17c3-8 6-10 8-10s5 2 8 10", title: "Tuned motion", text: "Animations you can tune with XxxAnimation(...) or switch off with XxxAnimation.None. Haptics where they help." },
  { icon: "M12 5.5a1.5 1.5 0 1 0 0-.01M6 9h12M12 9v11M8 20l4-6 4 6", title: "Accessible and RTL", text: "Screen-reader semantics on every part, adjustable controls for pickers and sliders, and right-to-left layouts." },
  { icon: "M7 17L17 7M9 7h8v8", title: "minSdk 24, no desugaring", text: "The date picker uses its own CalendarDate type, so nothing needs java.time or core library desugaring." },
];

function firstCode(blocks: Block[]): string | undefined {
  const b = blocks.find((x) => x.type === "code");
  return b && b.type === "code" ? b.code : undefined;
}

export default function HomePage() {
  const showcase = components.map((c) => ({ href: componentHref(c.slug), name: c.composable, file: c.gifs[0].file }));
  const example = components.find((c) => c.slug === "rating-bar") ?? components[0];
  const exampleCode = firstCode(example.sections[0]?.blocks ?? []);
  const [installText, ...installRest] = guide.installation;
  const requirements = installRest.filter((b) => b.type === "table");

  return (
    <main id="main" className="home">
      <section className="hero" aria-labelledby="hero-title">
        <div className="hero-glow" aria-hidden="true" />
        <div className="hero-grid">
          <div className="hero-copy">
            <div className="hero-logo">
              <Logo size={72} />
              <span>Built for<strong>Jetpack Compose</strong></span>
            </div>
            <p className="eyebrow">
              <span className="eyebrow-dot" aria-hidden="true" />
              {components.length} components for Jetpack Compose
            </p>
            <h1 id="hero-title">
              The components Material&nbsp;3 <span className="grad">doesn&apos;t ship</span>.
            </h1>
            <p className="lead">
              A date picker, wheel picker, OTP input, rating bar, timeline and color picker, built to feel like they
              always belonged in Material 3. They read your <code>MaterialTheme</code>, animate, and work with screen
              readers.
            </p>
            <div className="install">
              <code>
                <span className="tk-fn">implementation</span>(<span className="tk-string">&quot;io.github.nameisjayant:compose-components:{version}&quot;</span>)
              </code>
              <CopyButton text={DEPENDENCY} label="Copy dependency" />
            </div>
            <div className="cta-row">
              <Link href={componentHref(components[0].slug)} className="btn btn-primary">
                Browse components
                <svg viewBox="0 0 24 24" width="18" height="18" aria-hidden="true"><path d="M5 12h14M13 6l6 6-6 6" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" /></svg>
              </Link>
              <a href="#installation" className="btn btn-ghost">Installation</a>
            </div>
            <ul className="stats">
              <li><strong>{components.length}</strong><span>components</span></li>
              <li><strong>M3</strong><span>themed</span></li>
              <li><strong>24</strong><span>min SDK</span></li>
              <li><a href={MAVEN_URL} target="_blank" rel="noreferrer"><strong>v{version}</strong><span>Maven Central</span></a></li>
            </ul>
          </div>
          <Showcase items={showcase} />
        </div>
      </section>

      <section className="home-section" aria-labelledby="features-title">
        <h2 id="features-title" className="section-title">Learn one, know them all</h2>
        <p className="section-lead">Every component is put together the same way, so the second one takes minutes.</p>
        <ul className="features">
          {FEATURES.map((f) => (
            <li key={f.title} className="feature">
              <span className="feature-icon" aria-hidden="true">
                <svg viewBox="0 0 24 24" width="20" height="20"><path d={f.icon} fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" /></svg>
              </span>
              <h3>{f.title}</h3>
              <p>{f.text}</p>
            </li>
          ))}
        </ul>
      </section>

      <section className="home-section" aria-labelledby="components-title">
        <h2 id="components-title" className="section-title">Components</h2>
        <p className="section-lead">Each one has its own page with explanations, code you can copy and every parameter.</p>
        <ComponentGrid items={components} />
      </section>

      <section className="home-section install-section" id="installation" aria-labelledby="installation-title">
        <h2 id="installation-title" className="section-title">Installation</h2>
        <p className="section-lead">Three steps from an empty project to your first component.</p>
        <ol className="steps">
          <li className="step">
            <h3>Add the dependency</h3>
            {installText && <Blocks blocks={[installText]} />}
            <Blocks blocks={guide.installation.filter((b) => b.type === "code")} />
            <details className="disclosure">
              <summary>Using a version catalog?</summary>
              <Blocks blocks={guide.catalog} />
            </details>
          </li>
          <li className="step">
            <h3>Check the requirements</h3>
            <Blocks blocks={requirements} />
          </li>
          <li className="step">
            <h3>Drop in a component</h3>
            <p>
              Here&apos;s the {example.name.toLowerCase()}. Every component is imported from{" "}
              <code>com.nameisjayant.components.&lt;component&gt;</code>.
            </p>
            {exampleCode && <CodeBlock code={exampleCode} title={`${example.composable}.kt`} />}
            <Link href="/docs/" className="btn btn-ghost">Read the getting started guide</Link>
          </li>
        </ol>
      </section>

      <footer className="home-foot">
        Apache 2.0 · Made with Jetpack Compose by{" "}
        <a href="https://github.com/nameisjayant" target="_blank" rel="noreferrer">Jayant Kumar</a> ·{" "}
        <a href={REPO_URL} target="_blank" rel="noreferrer">GitHub</a>
      </footer>
    </main>
  );
}
