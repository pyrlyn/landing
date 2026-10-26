# landing-kotlin

A 1:1 port of the pyrlyn.dev landing (the Astro `pyrlyn/landing`, main) to Kotlin/JVM. The site is
generated statically, with the same routes, HTML structure and CSS as the original.

- **Templates:** kotlinx.html
- **Data:** kotlinx-serialization (JSON copy in `src/main/resources/data`)
- **Dev preview:** Ktor CIO. It is for preview only; production serves the static `build/site`.
- **Content:** `content/projects/*.md` (products) and `content/docs/<product>/` (docs). Markdown is parsed
  only at build time by JetBrains markdown (GFM) behind `markdown/MarkdownRenderer`, and baked into static
  HTML. No Markdown parser ships to the browser (`generateSite` fails if a JS asset contains one), and the
  preview only serves `build/site`.
- **CSS:** written in Kotlin with kotlin-css (JetBrains kotlin-wrappers) in `styles/`, rendered at
  build time, minified and inlined into each page. The sheets use the `sel`/`atMedia`/`atSupports`/`at`/`prop` helpers
  from `styles/Dsl.kt`, which keep the source order and never merge rules.
- **JS:** Kotlin/JS (IR, browser target) in `web/` (site bundle) and `web-intro/` (the WebGL intro sweep).
  Production webpack builds apply DCE and minification. `generateSite` builds both, hashes them into
  `/_assets/` and loads the intro only on hero pages when motion is allowed. Pages are fully pre-rendered
  and work without JS; the scripts only enhance widgets (menus, theme, search, carousels, terminals,
  visuals, docs nav/TOC, wishlist links). The web code sticks to externals and native JS helpers
  (`web/.../Dom.kt`) instead of Kotlin collections, Regex and exceptions, which keeps the stdlib out of the bundle.
- **Font:** Satoshi from the Fontshare CDN, preloaded with `font-display: swap`.

## Commands

```sh
./gradlew generateSite   # build the static site into build/site
./gradlew run            # generateSite, then preview on http://localhost:4321 (PORT overrides)
./gradlew build          # compile + tests
```

Requires JDK 21. Gradle comes from the wrapper; the Kotlin Gradle plugin downloads its own Node/webpack toolchain for the JS build.

## Environment

Same variables as the Astro site (`src/lib/config.ts`). All are optional at build time:

| var | purpose |
|-|-|
| `SITE_URL` | canonical origin (default `https://pyrlyn.dev`) |
| `SITE_BASE` | path prefix (default `/`) |
| `GA_MEASUREMENT_ID` | GA4 (public fallback built in) |
| `POSTHOG_KEY`, `POSTHOG_HOST` | PostHog EU (public fallback built in) |
| `PERF_DEFER_ANALYTICS` | `0` loads analytics immediately (default: after load + idle) |
| `API_URL` | backend for prices and the wishlist (empty means placeholders) |
| `ALGOLIA_APP_ID`, `ALGOLIA_SEARCH_KEY`, `ALGOLIA_INDEX` | docs search (off when unset) |

Search Console is verified via a DNS TXT record, so the site needs no meta tag.

## Layout

```
src/main/kotlin/dev/pyrlyn/landing/
  config/     SiteConfig (env)
  content/    front matter, catalog, sections, docs, names
  markdown/   MarkdownRenderer (JetBrains markdown AST → HTML hooks), escaping, HTML guards
  seo/        JSON-LD, titles
  html/       layout, components, icons
  pages/      home, product, docs, misc
  styles/     kotlin-css stylesheets (Styles.site, Styles.docs)
  build/      CSS/JS pipeline
  Generate.kt static generator, sitemap, robots
  Preview.kt  Ktor dev server
web/          Kotlin/JS site bundle (progressive enhancement)
web-intro/    Kotlin/JS WebGL intro, loaded lazily
kotlin-js-store/ yarn.lock for the Kotlin/JS toolchain
content/      markdown content
public/       static files copied as-is
```
