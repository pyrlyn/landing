# listepo tools — AI developer tool storefront

A marketplace landing for AI developer tools, built with [Astro](https://astro.build). The home page is the storefront (catalog, featured tool, how it works, per-tool pricing,
FAQ); every product gets its own showcase page generated from one Markdown file, and a docs section
generated from the product's own `docs/` folder.

Showcase pages `/landing/rtok/`, `/landing/cox/`,
`/landing/ketch/`; docs under `/landing/<product>/docs/` (overview) and `/landing/<product>/docs/<slug>/`.

> The previous site in this repository is preserved in branch and tag `archive/toha-landing-2026-09-27`.

## Run

```sh
npm i
npm run dev       # http://localhost:4321/landing/
npm run build     # static site in dist/
npm run check:docs  # after a build: docs coverage, headings, internal links under the base
npm run check:seo   # after a build: title/description/canonical/OG per page, one h1, alt, JSON-LD, sitemap
npm run preview   # serve dist
```

`astro.config.mjs` reads `SITE_URL` (default `https://pyrlyn.github.io`) and `SITE_BASE` (default
`/landing/`). Every internal link and asset goes through `u()` in `src/lib/site.ts`, so moving the site is a
one-line change. There is no hosted deploy: run `npm run build`, `check:docs` and `check:seo` locally.

## Run with Docker

The `Dockerfile` builds the site for the domain root (`SITE_URL=https://pyrlyn.dev`, `SITE_BASE=/`) in a
Node 22 stage and serves `dist/` with nginx (`docker/nginx.conf`: static files, `404.html` for unknown
paths, gzip, long-lived cache headers for `/_astro/`). The container listens on port 80.

```sh
docker build -t pyrlyn-landing .
docker run -d --name pyrlyn-landing -p 127.0.0.1:8080:80 pyrlyn-landing   # http://localhost:8080/
```

Build args: `SITE_URL`, `SITE_BASE` (e.g. `--build-arg SITE_BASE=/landing/` for the GitHub Pages layout),
`NODE_VERSION` (22), `NGINX_VERSION` (`1.30-alpine`). The build stage needs network access to GitHub:
`@pyrlyn/brand` is a GitHub dependency, fetched over HTTPS inside the image.

## Structure

- `content/projects/*.md` — one file per product (frontmatter + README-derived sections). See
  [`CONTENT_CONTRACT.md`](CONTENT_CONTRACT.md). Adding a file adds the product to the catalog, the
  Products menu, the footer, pricing and a new `/<slug>/` page.
- `content/docs/<product>/**/*.md` — the product's `docs/` folder, synced from its repository together
  with `_source.json` (repo, commit). See [`content/docs/README.md`](content/docs/README.md).
- `src/pages/index.astro` — storefront; `src/pages/[slug].astro` — product showcase template (text from
  the README-derived `content/projects/<slug>.md`).
- `src/pages/[slug]/docs/index.astro` — docs overview per product (copy in `src/data/docs-overview.ts`,
  facts only from the product's docs/README); `src/pages/[slug]/docs/[doc].astro` — one page per synced
  doc, plus site-authored pages such as `src/components/docs/KetchGettingStarted.astro`.
- `src/lib/docs.ts` — reads the synced docs at build time, renders Markdown (heading ids matching
  GitHub's, internal `.md` links rewritten to site pages, other relative links pinned to the synced
  commit on GitHub), builds the TOC and summaries; `src/data/docs-nav.json` — tab order, titles,
  groups and exclusions; `src/layouts/DocsLayout.astro` + `src/components/DocsNav.astro` — the sticky
  horizontal docs nav, ~70ch column, on-page TOC (≥1280px) and prev/next; `src/styles/docs.css`.
- `scripts/check-docs.mjs`, `scripts/check-seo.mjs` — post-build checks run in CI.
- `src/lib/seo.ts` — titles, meta descriptions and schema.org JSON-LD (Organization/WebSite on the home
  page, SoftwareApplication + BreadcrumbList per product, TechArticle + BreadcrumbList per docs page);
  values come from the content only — no ratings, and Pro placeholder prices never reach structured data.
  `@astrojs/sitemap` writes the sitemap; `src/pages/robots.txt.ts` points to it.
- `src/components/` — `Terminal` (animated demo of real README commands), `TokenBitset` (rtok),
  `Flow` (cox event stream, ketch install/rollback), `Picture`, `Icon`.
- `src/data/showcase.ts` — terminal scripts and flow steps; `src/lib/site.ts` — brand, themes, license,
  caveats, Pro placeholders; `src/lib/catalog.ts` — collection helpers.
- `src/styles/global.css` — the design system (it imports the brand tokens and components from
  `@pyrlyn/brand`, see [Brand tokens](#brand-tokens)); `src/styles/base.css` — reset and background mesh;
  `src/scripts/main.ts` — progressive enhancement.
- `src/styles/tailwind.css` — Tailwind v4 (`@tailwindcss/vite`), theme + utilities only, **no preflight**,
  default theme replaced by the tokens from `global.css`. Responsive fixes go in markup as prefixed
  utilities (`tw:max-sm:px-3!`); the `tw:` prefix keeps them apart from the site's own class names
  (`.container`, `.grid`, `.ring` …), and `!` is needed where a utility must beat a rule in `global.css`
  (unlayered CSS wins over `@layer utilities`). Do not add new hand-written CSS for layout fixes.
- `art/` — scripts that draw the 11 images in `public/images/` (4 hero, 3 props, 4 OG cards) as SVG,
  render them with headless Chrome and encode AVIF/WebP/JPEG with sharp.

## Design system

Dark-first depth: near-black blue-tinted background, CSS gradient mesh from each page accent
(`color-mix`/`oklch`, grain, faint grid, slow drift, scroll-driven parallax where `animation-timeline` is
supported), glass surfaces (hairline + top highlight + bottom edge, blur tiers 2/10/36px), three-level
shadow stacks, cursor-follow shine, two-ring focus. Per page accents: home `#4C8DFF`/`#3EE6C4`, rtok
`#5CE1FF`/`#FF6B4A` on navy `#06101A`, cox `#A8E06C`, ketch `#3DDCB0`.

Docs pages keep the same system: a glass secondary nav stuck under the header (product mark, tabs with an
accent underline that slides to the hovered tab, edge fades and scroll-snap when the tabs overflow, a
"Section" disclosure on phones when there are more than 7 pages), prose at ~70ch, code blocks with copy
buttons and a "Copied" toast, tables in scrollable regions, callouts for blockquotes and GitHub alerts.

Accessibility: AA contrast, skip link, 44px targets, keyboard hotspots and menu (Esc closes), content
visible without JS, opaque fallbacks for `prefers-reduced-transparency` and missing `backdrop-filter`,
`prefers-reduced-motion` stops drift, float, tilt, typing, parallax, step reveals and hover motion.

## Brand tokens

The visual identity comes from the [`pyrlyn/brand`](https://github.com/pyrlyn/brand) package,
installed from git and pinned to a tag in `package.json`:

```json
"@pyrlyn/brand": "github:pyrlyn/brand#v0.2.0"
```

What the site takes from it:

| From `@pyrlyn/brand` | Used in | What |
|---|---|---|
| `tokens.css` | `global.css` (1st import) | rtok brand variables `--rtok-*` (dark default, light via `[data-theme="light"]`/`.light`). Exposed for use; the page CSS does not reference them yet, and no landing variable or selector shares their names. |
| `landing/tokens.css` | `global.css` (2nd import) | This site's tokens under their usual names: `--accent` (home `#4C8DFF`), `--accent-2`, `--accent-light`, `--bg`, `--fg*`, `--muted`, `--subtle`, `--surface*`, `--glass-*`, `--hairline*`, `--blur-*`, `--shadow-*`, `--glow`, `--focus`, `--font-sans`, `--font-mono`, `--fs-*`, `--s-1`…`--s-10`, `--r-*`, `--container`, `--ease`. Page themes still override `--accent` / `--accent-2` / `--accent-light` / `--bg` on `<html>` (`src/lib/site.ts`). |
| `landing/components.css` | `global.css` (after `base.css`) | `.glass`, `.shine`, `.btn*`, `.eyebrow`, `.pill`, `.badge*`, `.brand`, `.brand__mark`, `.pyrlyn-by*` |
| `logo/pyrlyn/pyrlyn-{lockup,mark}-on-dark.svg?raw` | `Base.astro` | The header logo, inlined (paper → `currentColor`, amber cursor → `--accent`) |
| `logo/pyrlyn/*` | `public/pyrlyn/` | Favicons, apple-touch icon and the `<img>` lockups, copied by `npm run brand` |
| `logo/listepo/listepo-favicon.svg` | `public/favicon.svg` | The previous (unlinked) favicon, kept at its old URL |

`npm run brand` (run automatically by `predev` and `prebuild`) calls the package's `pyrlyn-brand-copy`
helper, which copies the logo files into `public/pyrlyn/` and `public/favicon.svg`; both are git-ignored,
so the URLs (`/landing/pyrlyn/pyrlyn-favicon.svg` …) stay the same without keeping copies in this repo.
The import order in `global.css` (brand tokens → `base.css` → brand components → page CSS) is the cascade
order the rules had when they lived in this repo, so moving them changed nothing visually.

Not from the package: Inter is self-hosted through `@fontsource-variable/inter` and the monospace stack
is system fonts; product art, hero images and OG cards in `public/images/` (drawn by `art/`) are content.

To update: tag a new version in `pyrlyn/brand`, then

```sh
npm install github:pyrlyn/brand#vX.Y.Z   # updates package.json and package-lock.json
npm run build                             # check the pages before committing
```

and commit `package.json` and `package-lock.json`. To change a landing token or brand component, edit
`themes/landing/` in `pyrlyn/brand` (not `global.css`) and bump the tag.

## Placeholders

All Pro tiers, Pro prices, Pro features and the waitlist buttons are **placeholders** and are labelled on
the page. Product facts (features, install commands, versions, terminal commands) come only from each
tool's own README, docs and site copy. Licensing for every tool: GNU GPLv3, a royalty-free license, or a
commercial license — your choice.
