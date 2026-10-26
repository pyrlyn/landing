# landing-kotlin

A 1:1 port of the pyrlyn.dev landing (the Astro `pyrlyn/landing`, main) to Kotlin/JVM. The site is
generated statically, with the same routes, HTML structure and CSS as the original.

- **Templates:** kotlinx.html
- **Data:** kotlinx-serialization (JSON copy in `src/main/resources/data`)
- **Dev preview:** Ktor CIO. It is for preview only; production serves the static `build/site`.
- **Content:** `content/projects/*.md` (products) and `content/docs/<product>/` (docs), rendered by a
  built-in GFM-subset Markdown renderer (`markdown/`).
- **CSS:** written in Kotlin with kotlin-css (JetBrains kotlin-wrappers) in `styles/`, rendered at
  build time, minified and inlined into each page. The sheets use the `sel`/`atMedia`/`atSupports`/`at`/`prop` helpers
  from `styles/Dsl.kt`, which keep the source order and never merge rules.
- **JS:** self-hosted from `src/main/web/js`. It is one hashed ES module plus a lazily loaded WebGL intro. There is no framework runtime.
- **Font:** Satoshi from the Fontshare CDN, preloaded with `font-display: swap`.

## Commands

```sh
./gradlew generateSite   # build the static site into build/site
./gradlew run            # generateSite, then preview on http://localhost:4321 (PORT overrides)
./gradlew build          # compile + tests
```

Requires JDK 21. Gradle comes from the wrapper.

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
  markdown/   parser + renderer
  seo/        JSON-LD, titles
  html/       layout, components, icons
  pages/      home, product, docs, misc
  styles/     kotlin-css stylesheets (Styles.site, Styles.docs)
  build/      CSS/JS pipeline
  Generate.kt static generator, sitemap, robots
  Preview.kt  Ktor dev server
src/main/web/ js sources
content/      markdown content
public/       static files copied as-is
```
