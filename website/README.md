# Compose Components website

Docs site for [Compose Components](https://github.com/nameisjayant/Compose-Components). It lives in the library repo's `website/` folder and is built with Next.js (App Router) and exported as static HTML.

- **Home**: what the library is, the install line, a phone mockup cycling through each component's GIF, the shared conventions, a card per component and step-by-step installation.
- **Docs**: a sidebar with *Getting started* (installation, version catalog, imports, theming, shared conventions), then **Components** (one page each with GIFs, a "How it works" explanation, every README example, all parameters and the API signatures) and **Modifiers** (coming soon).

## Develop

```bash
npm install
npm run dev        # http://localhost:3000
```

## Sync with the library

Component content isn't written by hand, and the generated files (`src/data/`, `public/gifs/`) aren't committed: `npm run dev` and `npm run build` regenerate them first. `scripts/generate_data.py` reads the library repo (the README's `## <emoji> ComposeXxx` sections for usage, GIFs and parameter tables, the sources for signatures, `gradle.properties` for the version) and writes `src/data/components.json` and `public/gifs/`. The short card text, tags and "How it works" notes for each component live in `COMPONENTS` at the top of the script.

After adding a component to the library (with its README section and GIFs), add it to `COMPONENTS` and run:

```bash
npm run sync          # reads the parent folder; or: npm run sync -- /path/to/ComposeComponents
```

The sync fails if the README has a component that `COMPONENTS` doesn't know about, or if a listed API can't be found in the sources.

## Build and deploy

```bash
npm run build      # static site in out/
npm start          # preview out/ locally
```

For a GitHub Pages project site (`<user>.github.io/Compose-Components`), build with the base path:

```bash
BASE_PATH=/Compose-Components npm run build
```

`.github/workflows/docs.yml` in the library repo does this whenever `website/`, the README or the GIFs change on `main`. `npm run build` runs the sync first, so the deployed site always matches the README. Turn it on once under **Settings → Pages → Source: GitHub Actions**.
