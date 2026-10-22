export function Toc({ sections }: { sections: { id: string; title: string }[] }) {
  return (
    <nav className="toc" aria-label="On this page">
      <p>On this page</p>
      {sections.map((s) => <a key={s.id} href={`#${s.id}`}>{s.title}</a>)}
    </nav>
  );
}
