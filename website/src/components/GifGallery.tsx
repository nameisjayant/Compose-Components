import type { Gif } from "@/lib/data";
import { gif } from "@/lib/data";
import { Inline } from "./Inline";

/** The component's README recordings, framed like the demo app's screen they were recorded on. */
export function GifGallery({ gifs }: { gifs: Gif[] }) {
  return (
    <div className={`gif-gallery count-${gifs.length}`}>
      {gifs.map((g) => (
        <figure key={g.file} className="gif-frame">
          <div className="gif-media">
            {/* eslint-disable-next-line @next/next/no-img-element -- animated GIFs, served as-is */}
            <img src={gif(g.file)} alt={g.alt} loading="lazy" decoding="async" />
          </div>
          {gifs.length > 1 && <figcaption><Inline text={g.caption} /></figcaption>}
        </figure>
      ))}
    </div>
  );
}
