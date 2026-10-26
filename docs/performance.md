# Loading performance

Five build-level settings keep the first paint from waiting on resources it does not need.

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

## 3. Satoshi from the Fontshare CDN, without a blocking stylesheet

- `src/layouts/Base.astro` has an inline `@font-face` (`font-display: block`, no flash of the
  fallback font) whose `src` is the Satoshi variable woff2 on `cdn.fontshare.com` (one file, wght
  300–900, ~42 KB), plus `<link rel="preconnect">` to that host and `<link rel="preload" as="font"
  crossorigin>` for the file. The render-blocking `api.fontshare.com` stylesheet is gone.
- The URL is the exact file `api.fontshare.com/v2/css?f[]=satoshi@1` references (same for every
  user agent, `last-modified` 2021, `access-control-allow-origin: *`). The path is a content hash,
  so a new Satoshi release on Fontshare will not update the site automatically: re-check the
  stylesheet and update `fontUrl` in `Base.astro`.
- License: Satoshi is under the ITF Free Font License; the files are not committed to this public repo.
- Revert: put back `<link rel="stylesheet" href="https://api.fontshare.com/v2/css?f[]=satoshi@1&display=swap">`.

## 4. Analytics load after the page

- The `gtag` queue (`dataLayer`, `gtag("js")`, `gtag("config", id)`) and the PostHog stub with the same
  `posthog.init(key, { api_host, person_profiles: "identified_only" })` are set up immediately, so
  calls made early are queued, not lost.
- `gtag.js` and PostHog `array.js` are injected after the `load` event, in `requestIdleCallback`
  (3 s timeout; `setTimeout` where unsupported).
- Disable: `PERF_DEFER_ANALYTICS=0 npm run build` (libraries injected at once).

## 5. Third-party client modules from jsDelivr

`src/lib/cdn-modules.mjs` keeps `preact`, `preact/hooks`, `preact/compat`, `preact/jsx-runtime`,
`@preact/signals` and `@preact/signals-core` (and the `react`/`react-dom` names that alias to
`preact/compat`) out of the client bundle. `Base.astro` emits an import map at the top of `<head>`
pointing them at `cdn.jsdelivr.net/npm/<pkg>@<version>/<file>` and a preconnect to jsDelivr.

- Versions come from `node_modules` (that is, `package-lock.json`), so updating a dependency
  updates the URL.
- SRI: the import map's `integrity` field has a sha384 for every URL, computed from the same file in
  `node_modules` (jsDelivr serves npm files byte-for-byte). Browsers without import-map integrity
  support still load the modules, just without the check.
- Only the client build is affected; server rendering still uses `node_modules`.
- Disable: `PERF_CDN_MODULES=0 npm run build` (Preact bundled again).

### Inventory: what is on a CDN and what is not

| Asset | Where | Why |
|---|---|---|
| Satoshi font | Fontshare CDN | Vendor CDN; the license does not allow committing the files here |
| Preact 11, hooks, compat, jsx-runtime | jsDelivr | npm package with a public CDN, pinned + SRI |
| @preact/signals, @preact/signals-core | jsDelivr | Same |
| Google Analytics `gtag.js`, PostHog | Vendor CDNs (already) | Loaded after `load` + idle (section 4) |
| Tailwind CSS v4 utilities | Bundled (inlined CSS) | Built at compile time from only the classes used; the CDN build is the full JIT runtime, larger and render-blocking |
| `@pyrlyn/brand` tokens/components CSS, logos, favicons | Own | Our own package, not on a public CDN |
| `@pyrlyn/sdk` | Own | Vendored tarball, not published |
| `marked`, Astro runtime | Build time only | Not shipped to the browser (Astro's island loader is inlined in the HTML) |
| Icons (`Icon.astro`) | Inline SVG | Own icon set rendered into the HTML; no icon font or library |

## Testing

- `npm run build && npm run check:seo && npm run check:docs && npm test`.
- Visual parity: full-page screenshots of `/`, `/rtok/`, `/rtok/docs/` at 375 and 1280 px with
  reduced motion, before vs after, pixel diff at threshold 0 (0 px different when this was added).
- Lighthouse: mobile and desktop presets, 3 runs per page, compare medians.

## Limitations

- Visitors who leave before `load` + idle are not counted by GA/PostHog (bounces under ~1 s).
- The page depends on two more third-party origins (`cdn.fontshare.com`, `cdn.jsdelivr.net`); if one is
  down, text waits up to ~3 s (`font-display: block`) and then uses the fallback font, and islands do
  not hydrate (static HTML still renders).
- Inlining helps the first visit; repeat navigations re-download the CSS inside each HTML page.
