# landing-kotlin

This project lives at `landings/pyrlyn/` in the [pyrlyn/landing](https://github.com/pyrlyn/landing) repository. Run Gradle commands from this directory.

A 1:1 port of the pyrlyn.dev landing (the Astro `pyrlyn/landing`, main) to Kotlin/JVM. The site is
generated statically, with the same routes, HTML structure and CSS as the original.

- **Templates:** kotlinx.html
- **Data:** kotlinx-serialization (JSON copy in `src/main/resources/data`)
- **Dev preview:** Ktor CIO. It is for preview only; production serves the static `build/site`. `./gradlew dev`
  runs `gradlew --continuous generateSite` beside it; after each successful generation the generator writes
  `build/site.stamp`, and the preview tells open pages (Server-Sent Events on `/__livereload`) to reload.
- **Content:** `content/projects/*.md` (products) and `content/docs/<product>/` (docs). Markdown is parsed
  only at build time by JetBrains markdown (GFM) behind `markdown/MarkdownRenderer`, and baked into static
  HTML. No Markdown parser ships to the browser (`generateSite` fails if a JS asset contains one), and the
  preview only serves `build/site`.
- **CSS:** written in Kotlin with kotlin-css (JetBrains kotlin-wrappers) in `styles/`, rendered at
  build time, minified and inlined into each page. The sheets use the `sel`/`atMedia`/`atSupports`/`at`/`prop` helpers
  from `styles/Dsl.kt`, which keep the source order and never merge rules.
- **JS:** Kotlin/JS (IR, browser target) in `web/` (site bundle), `web-intro/` (the WebGL intro sweep) and
  `web-plans/` (product plans and the wishlist form, on the backend client). Production webpack builds apply
  DCE and minification. `generateSite` builds all three and hashes them into `/_assets/`; the intro loads
  only on hero pages when motion is allowed, the plans widget only on product pages of a site built with
  `API_URL`. Pages are fully pre-rendered and work without JS; the scripts only enhance widgets (menus,
  theme, search, carousels, terminals, visuals, docs nav/TOC, plans). The `web/` code sticks to externals
  and native JS helpers (`web/.../Dom.kt`) instead of Kotlin collections, Regex and exceptions, which keeps
  the stdlib out of the bundle.
- **Font:** Satoshi from the Fontshare CDN, preloaded with `font-display: swap`.

## Backend: plans and the wishlist

Plans and the wishlist come from the landing API of [pyrlyn/backend](https://github.com/pyrlyn/backend)
through its generated Kotlin client, `backend:landing-client` (version in `gradle/libs.versions.toml`).
`web-plans/` uses it in the browser:

- With `API_URL` set, each product page with a Pro tier carries `data-plans`/`data-api` and a `<template>` of a
  tier card; `site.js` loads the plans bundle on idle. It calls `GET /v1/projects/{slug}/plans` and swaps the
  placeholder tiers for the API's plans: a waitlist plan gets the join form, a free plan the install link, a
  paid plan on sale neither (the API has no checkout yet).
- The join form sends `POST /v1/projects/{slug}/waitlist` with the email and reports success, an invalid
  address (400), a closed wishlist (404), rate limiting (429) and an unreachable API.
- Without `API_URL`, without JS, when the API fails or does not know the product (404), the placeholder
  tiers stay, with the disabled "Join the Pro wishlist" button. The plans bundle is not shipped at all
  without `API_URL`.

The client is published to the GitHub Packages of the private `pyrlyn/backend`, so every build that
resolves it (`build`, `generateSite`, `e2eTest`) needs a token with `read:packages` that can see that
repository: `GITHUB_TOKEN` (and optionally `GITHUB_ACTOR`) in the environment, or `gpr.key` (and `gpr.user`)
in `~/.gradle/gradle.properties`. Locally, with the GitHub CLI:

```sh
gh auth refresh -s read:packages
GITHUB_TOKEN=$(gh auth token) ./gradlew build
```

CI reads it from the `BACKEND_PACKAGES_TOKEN` secret. To build against a backend checkout instead of the
published client (to try an API change before it is released), pass its path; Gradle then builds
`:shared:landing-client` from it in a composite build and needs no token:

```sh
./gradlew build -PbackendDir=../backend
./gradlew dev -PbackendDir=../backend
```

## Commands

```sh
./gradlew generateSite   # build the static site into build/site
./gradlew run            # generateSite, then preview on http://localhost:4321 (PORT overrides)
./gradlew dev            # the same preview with live reload: any change regenerates the site, open pages reload
./gradlew build          # compile + tests
./gradlew e2eTest        # generateSite + generateApiSite, then Playwright e2e tests in headless Chromium
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
- Kotlin/JS widgets: theme toggle, menu, home ring carousel, OSS/Pro toggle, Cox scenes, docs nav;
- the plans widget, on a second site built by `generateApiSite` into `build/site-api` with the stub
  `API_URL` `https://api.e2e.test`, whose plans and waitlist routes the tests answer themselves: plans replace
  the placeholders, the join form posts the email and reports 204, 429, 400 and network errors, and the
  placeholders stay on a failure or a 404. The main site, built without `API_URL`, ships no plans widget.

Analytics hosts are always aborted, so tests never send events. Install Chromium once (into Playwright's
default cache, outside the repo) with `./gradlew :e2e:playwrightInstall` (add `-PwithDeps` on Linux CI for
OS packages). `-Pheaded` shows the browser. `./gradlew build` does not run the e2e tests. CI runs them nightly only (repository-root `.github/workflows/e2e-nightly.yml`, with `working-directory: landings/pyrlyn`;
03:00 UTC = 05:00 Kyiv in winter, 06:00 in summer; also manual via workflow_dispatch).

## Lighthouse

`The repository-root `.github/workflows/lighthouse-nightly.yaml` (generated from `lighthouse-nightly.main.kts` with
github-workflows-kt) runs Lighthouse CI `@lhci/cli@0.15.1` nightly from this directory (03:00 UTC = 05:00 Kyiv in
winter, 06:00 in summer) and on workflow_dispatch against `/`, `/rtok/`, `/rtok/docs/` with the budgets from
`lighthouserc.{mobile,desktop}.json`; reports are uploaded as artifacts. Locally:

```sh
SITE_URL=https://pyrlyn.dev ./gradlew generateSite
npx --yes --ignore-scripts @lhci/cli@0.15.1 autorun --config=lighthouserc.mobile.json   # or .desktop.json
# From the repository root, after editing the script:
kotlin .github/workflows/lighthouse-nightly.main.kts
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
| `API_URL` | landing API for plans and the wishlist (empty means placeholders); see Backend above |
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
web-plans/    Kotlin/JS plans and wishlist widget on the backend client, loaded lazily
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
  1. Build the site from this directory: `./gradlew generateSite` (output in `landings/pyrlyn/build/site`).
  2. Build the amd64 image from `web-landing-kotlin/` in infra (context = `landings/pyrlyn/build/site`) and save it as a `.tar.gz`.
  3. Check first: `CONTABO_HOST=<host> ANSIBLE_SSH_ARGS='-o IdentitiesOnly=yes' ansible-playbook -i inventory web-landing-production.yml -e confirm_production=production -e landing_image=pyrlyn-landing-kotlin:<sha> -e landing_image_tar=<path-to-tar.gz> --check --diff`
  4. If the check shows `failed=0` and only the landing changes, run the same command without `--check --diff`.
  5. Rollback: the same command with `-e landing_image=pyrlyn-landing:dev -e landing_image_tar=`.

### Credentials (values are never stored in this repo)

- Login (SSH username): set in pyrlyn/infra `inventory` as `ansible_user=deploy` (for the sites playbooks, `deploy_remote_user` in `sites/playbooks/deploy.yml`, default `deploy`).
- SSH: user `deploy` with the private key `~/.ssh/id_ed25519` on Ivan's Mac. Do not try other users: fail2ban bans the IP after a few failed logins.
- Ansible vault password: file `~/Grok/secrets/infra_vault_pass` on Ivan's Mac (mode 600), passed via `ANSIBLE_VAULT_PASSWORD_FILE`.
- `CONTABO_HOST`: see `deploy/deploy-contabo.md` in the cox repo; it is not stored in infra.
