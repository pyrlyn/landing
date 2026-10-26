# Loading performance

Four build-level settings keep the first paint from waiting on resources it does not need.

## 1. Only the hero image loads eagerly

- `src/components/Picture.astro` defaults to `loading="lazy"` + `decoding="async"` and adds
  `fetchpriority="high"` only when a caller passes `loading="eager"`.
- Only the page hero (`src/pages/index.astro`, `src/pages/[slug].astro`) passes `loading="eager"`.
- The ring carousel art (`src/components/RingCarousel.astro`, 12 `prop-400` images on the home page)
  uses the default lazy loading, so it no longer competes with the hero for bandwidth.

To make an image eager again, pass `loading="eager"` to `<Picture>`; use it for at most one
above-the-fold image per page.

## 2. Page CSS is inlined

`astro.config.mjs` sets `build.inlineStylesheets: "always"`: Astro writes each page's CSS into a
`<style>` tag instead of a `<link rel="stylesheet">` to `/_astro/*.css`, so first paint does not wait
for an extra request. No new dependency, no change in the CSS itself.

- Disable: `PERF_INLINE_CSS=0 npm run build` (back to Astro's default `"auto"`: external files,
  inlined only when tiny).
- Cost: the CSS (~95 KB raw, ~15 KB gzip) is repeated in every HTML page and not cached across
  navigations; HTML of the home page grows from ~111 KB to ~205 KB raw.

## 3. Satoshi is self-hosted and preloaded

- `scripts/fetch-font.mjs` (`npm run font`, run by `predev`/`prebuild`) downloads the Satoshi variable
  woff2 (one file, wght 300–900, ~42 KB) from Fontshare into `public/fonts/` (gitignored).
- `src/layouts/Base.astro` then emits `<link rel="preload" as="font">` for it and an inline
  `@font-face` with `font-display: block` (no flash of the fallback font). The external
  `api.fontshare.com` stylesheet and its preconnects are gone.
- License: Satoshi is under the ITF Free Font License (v2.0). It allows self-hosting on our own site
  via `@font-face`, but forbids redistributing the files, including in public repositories. This repo
  is public, so the file is fetched at build time and never committed.
- Fallback: if the download fails (offline build), the script only warns and `Base.astro` falls back
  to the Fontshare CDN stylesheet, as before.
- Disable: `PERF_SELF_HOST_FONT=0 npm run build` (CDN stylesheet again).

## 4. Analytics load after the page

- The `gtag` queue (`dataLayer`, `gtag("js")`, `gtag("config", id)`) and the PostHog stub with the same
  `posthog.init(key, { api_host, person_profiles: "identified_only" })` are set up immediately, so
  calls made early are queued, not lost.
- `gtag.js` and PostHog `array.js` are injected after the `load` event, in `requestIdleCallback`
  (3 s timeout; `setTimeout` where unsupported).
- Disable: `PERF_DEFER_ANALYTICS=0 npm run build` (libraries injected at once).

## Testing

- `npm run build && npm run check:seo && npm run check:docs && npm test`.
- Visual parity: full-page screenshots of `/`, `/rtok/`, `/rtok/docs/` at 375 and 1280 px with
  reduced motion, before vs after, pixel diff at threshold 0 (0 px different when this was added).
- Lighthouse: mobile and desktop presets, 3 runs per page, compare medians.

## Limitations

- Visitors who leave before `load` + idle are not counted by GA/PostHog (bounces under ~1 s).
- The self-hosted font does not update automatically when Fontshare ships a new version; delete
  `public/fonts/` and rebuild to refresh it. CI builds always fetch a fresh copy.
- Home and `/rtok/` mobile LCP is still ~6 s in Lighthouse: the LCP element (hero `<h1>`) spends
  ~93% in render delay, not in loading the font or analytics. `/rtok/docs/` reaches ~2.9 s.
- Inlining helps the first visit; repeat navigations re-download the CSS inside each HTML page.
