# landing

If an AGENTS.md or CLAUDE.md exists higher in the tree, follow it too. On conflict, ask the creator.

Astro storefront for rtok, cox, and ketch ("listepo tools"). Product copy is `content/projects/*.md`. Synced docs are `content/docs/<product>/`. Internal links go through `u()` in `src/lib/site.ts`. `astro.config.mjs` reads `SITE_URL` (default `https://pyrlyn.dev`) and `SITE_BASE` (default `/landing/`).

```sh
npm ci
npm run dev
npm run build
npm run check
npm run check:docs
npm run check:seo
npm test
```

`predev` and `prebuild` copy logo files with `pyrlyn-brand-copy`. `npm test` imports `src/lib` and `src/data` under Node. `tests/hooks.mjs` supplies `import.meta.env`, `import.meta.glob`, and `astro:content` because those exist only in the Astro build. Do not test `art/`.

jactionlint and hk are pinned in `mise.toml`. `.github/jactionlint.yaml` applies the default profile and the baseline `.github/jactionlint-baseline.json`. `hk.pkl` runs jactionlint as a pre-push hook:

```sh
mise x -- jactionlint                  # lint workflows, composite actions, dependabot.yml
mise x -- jactionlint --baseline-write # refresh the baseline after fixing findings
mise x -- hk install --mise            # install the pre-push hook once per clone
```

Do not invent ratings, reviews, or Pro prices in structured data. Real Pro prices and the Pro wishlist come from the backend through `@pyrlyn/sdk` when `API_URL` is set (`src/lib/api.ts` at build time, the `ProPrices`, `WishlistForm` and `WishlistLink` islands in the browser; see `.env.example`); `PRO` in `src/lib/site.ts` and front matter `pro_price` are placeholders for products the API does not price. Without `API_URL`, or when the API is unreachable, the build warns and keeps the placeholders. `@pyrlyn/sdk` is installed from `vendor/` (packed from `pyrlyn/backend` `packages/sdk`) until it is published on npm; then depend on the registry version and delete `vendor/`.

Product categories live in `src/lib/site.ts` (`CATEGORIES` and `PRODUCT_CATEGORY`). Every new, renamed or removed product must be put into the right category there. Keep the categories up to date and consistent in the Products menu, the home catalog and pricing cards, and the product-page badge. `tests/categories.test.mjs` enforces it. The site footer lists product names only, with no per-product docs links.

Product names are proper nouns in prose and UI: write Rtok, Cox, Ketch, Runa, Swarfr, Aulo, Bindsmith (and Kerf, Mailune, Scull, Stator, Weft). Commands stay lowercase everywhere: code blocks, inline code, the Terminal component, install snippets, copy buttons, and plain-text command mentions such as `run rtok install` (a name followed by a subcommand or flag). So do package and crate names, URLs, slugs, paths, repo names, front-matter keys and ids, HTML ids, and every logo (`PyrlynLogo.astro`, the menu's two-letter tiles, card glyphs, brand SVGs, favicons). Synced Markdown keeps whatever spelling upstream uses: `src/lib/names.ts` capitalizes names at build time in Markdown text nodes (`walkTokens` in `src/lib/sections.ts` and `src/lib/docs.ts`) and in front-matter `title`/`tagline` (`src/lib/project-schema.ts`; `rawTitle` keeps the synced spelling for logo marks). Hand-written copy in `src/` is spelled correctly at the source. `tests/names.test.mjs` covers the rules; add a subcommand to `DEFAULT_COMMANDS` if prose shows one capitalized.

## Client-side code: Preact islands (React API)

All client-side interactivity is TypeScript islands written against the React API and run on Preact (`@astrojs/preact` with `compat: true`, so `react` / `react-dom` imports resolve to `preact/compat`; `tsconfig.json` sets `jsxImportSource: "preact"` and path aliases for the types). Write them as ordinary React function components with hooks; do not add `react` or `react-dom` back as dependencies. Astro stays for static markup, content and MDX.

- `src/components/islands/<Name>/<Name>.tsx` — one island per folder. Static markup the island does not need to re-render (cards, icons, menu panel) is passed from Astro as children, so it stays server-rendered and works without JS.
- `src/components/islands/hooks/` — shared hooks (`useMediaQuery`, `useDocumentEvent`).
- `src/lib/client/` — browser-side helpers and pure logic (ring maths, media queries, the intro WebGL sweep). Pure logic gets node tests in `tests/`.
- Hydration: `client:load` for above-the-fold controls and page-wide motion (ThemeToggle, Menu, Search, NavPin, IntroRing, HeroStage, Audience, PageMotion, DocsNav, WishlistLink), `client:visible` for below-the-fold widgets with their own box (RingCarousel, WishlistForm), `client:idle` for helpers and for wrappers around static markup (CopyFeedback, ProPrices, Terminal, CoxScenes, TokenParticles, TokenBitset, CodeGraph, KetchRoute, DocsToc). Wrappers made with `Enhance` (`hooks/useEnhance.tsx`) are `display: contents`, which `client:visible` cannot observe — use `client:idle` and let the init wait for visibility itself.
- Exception: the pre-paint theme script in `Base.astro` stays inline — it must run before first paint.
- There is no `src/scripts/` any more; every behaviour lives in an island. Islands that need the DOM of other islands or of the whole page (HeroStage, PageMotion, ProPrices, CopyFeedback) render nothing and query it. Reduced motion is respected in every island (final states, no timers).
