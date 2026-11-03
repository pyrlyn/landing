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
./gradlew e2eTest        # generateSite, then Playwright e2e tests in headless Chromium
```

Requires JDK 21. Gradle comes from the wrapper; the Kotlin Gradle plugin downloads its own Node/webpack toolchain for the JS build.

## E2E tests

`e2e/` holds browser tests written in Kotlin with [Playwright for Java](https://playwright.dev/java/).
`./gradlew e2eTest` generates the site, serves `build/site` on a free local port (a tiny JDK
`HttpServer`, `/x/` → `/x/index.html`, misses → `404.html`) and checks, in headless Chromium:

- every page returns 200 with no console errors, page errors or failing same-origin requests;
- SEO basics on every page (title, description, canonical unless `noindex`, OG, lang, one `h1`), robots and sitemap;
- every internal link, asset and sitemap URL resolves; unknown URLs get the 404 page;
- docs pages render Markdown as HTML (no raw `#`, fences, `**`, `[x](y)` in prose);
- GA and PostHog config is still in the output (matched by shape, values never printed);
- third-party scripts are async/module, no third-party stylesheets, the Fontshare font is a pinned `/wf/` URL
  that loads, and pages still work with all third-party requests blocked;
- Kotlin/JS widgets: theme toggle, menu, home ring carousel, OSS/Pro toggle, Cox scenes, docs nav.

Analytics hosts are always aborted, so tests never send events. Install Chromium once (into Playwright's
default cache, outside the repo) with `./gradlew :e2e:playwrightInstall` (add `-PwithDeps` on Linux CI for
OS packages). `-Pheaded` shows the browser. `./gradlew build` does not run the e2e tests. CI runs them nightly only (`.github/workflows/e2e-nightly.yml`,
03:00 UTC = 05:00 Kyiv in winter, 06:00 in summer; also manual via workflow_dispatch).

## Lighthouse

`.github/workflows/lighthouse-nightly.yaml` (generated from `lighthouse-nightly.main.kts` with
github-workflows-kt, like pyrlyn/landing) runs Lighthouse CI `@lhci/cli@0.15.1` nightly (03:00 UTC = 05:00 Kyiv in
winter, 06:00 in summer) and on workflow_dispatch against `/`, `/rtok/`, `/rtok/docs/` with the budgets from
`lighthouserc.{mobile,desktop}.json`; reports are uploaded as artifacts. Locally:

```sh
SITE_URL=https://pyrlyn.dev ./gradlew generateSite
npx --yes --ignore-scripts @lhci/cli@0.15.1 autorun --config=lighthouserc.mobile.json   # or .desktop.json
kotlin .github/workflows/lighthouse-nightly.main.kts   # regenerate the YAML after editing the script
```

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
e2e/          Playwright (Java API, Kotlin) browser tests: ./gradlew e2eTest
kotlin-js-store/ yarn.lock for the Kotlin/JS toolchain
content/      markdown content
public/       static files copied as-is
```

## Production deploy notes (2026-11-03)

1. The first deploy attempt failed: the production site runs in a Docker Compose stack, not where the sites playbook expected.
2. The rollout was redone for that stack via Ansible ([pyrlyn/infra#22](https://github.com/pyrlyn/infra/pull/22)): first a `--check --diff` run, then the live run as the `deploy` user.
3. Canonical tags on the Kotlin landing pages were already correct (each page points to itself), so no code changes were needed.
4. Other variants (the Astro landing, the CV) were left untouched; only the Kotlin landing was changed.

### Site and how to deploy

- Site: https://pyrlyn.dev (production, Contabo host, Docker Compose stack in `/opt/sites/web/web`, service `landing`).
- Deploy is done with Ansible from [pyrlyn/infra](https://github.com/pyrlyn/infra) (playbook `web-landing-production.yml`, added in pyrlyn/infra#22):
  1. Build the site: `./gradlew generateSite` (output in `build/site`).
  2. Build the amd64 image from `web-landing-kotlin/` in infra and save it as a `.tar.gz`.
  3. Check first: `CONTABO_HOST=<host> ANSIBLE_SSH_ARGS='-o IdentitiesOnly=yes' ansible-playbook -i inventory web-landing-production.yml -e confirm_production=production -e landing_image=pyrlyn-landing-kotlin:<sha> -e landing_image_tar=<path-to-tar.gz> --check --diff`
  4. If the check shows `failed=0` and only the landing changes, run the same command without `--check --diff`.
  5. Rollback: the same command with `-e landing_image=pyrlyn-landing:dev -e landing_image_tar=`.

### Credentials (values are never stored in this repo)

- SSH: user `deploy` with the private key `~/.ssh/id_ed25519` on Ivan's Mac. Do not try other users: fail2ban bans the IP after a few failed logins.
- Ansible vault password: file `~/Grok/secrets/infra_vault_pass` on Ivan's Mac (mode 600), passed via `ANSIBLE_VAULT_PASSWORD_FILE`.
- `CONTABO_HOST`: see `deploy/deploy-contabo.md` in the cox repo; it is not stored in infra.
