# Loading performance

Two build-level settings keep the first paint from waiting on resources it does not need.

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

## Testing

- `npm run build && npm run check:seo && npm run check:docs && npm test`.
- Visual parity: full-page screenshots of `/`, `/rtok/`, `/rtok/docs/` at 375 and 1280 px with
  reduced motion, before vs after, pixel diff at threshold 0 (0 px different when this was added).
- Lighthouse: mobile and desktop presets, 3 runs per page, compare medians.

## Limitations

- The page still loads one render-blocking third-party stylesheet (Satoshi from
  `api.fontshare.com`, `src/layouts/Base.astro`) and third-party analytics scripts (Google Analytics
  `gtag.js`, ~180 KB; PostHog `array.js`, ~107 KB). Lighthouse attributes most remaining mobile
  render-blocking time and LCP (the hero `<h1>`, which waits for the font) to these. Changing them
  is out of scope here: self-hosting the font or deferring analytics are separate decisions.
- Inlining helps the first visit; repeat navigations re-download the CSS inside each HTML page.
