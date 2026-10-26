# landing

<https://github.com/pyrlyn/landing>

Astro static storefront ("listepo tools") marketing rtok, cox and ketch; product pages and docs sections are generated from Markdown synced from each product repository, with post-build CI validators for docs coverage, links, and SEO.

| # | Status | Priority | Complexity | Readiness | Agent |
| --- | --- | --- | --- | --- | --- |
| T1 | todo | P1 | 2 | 0% | |
| T3 | todo | P2 | 2 | 0% | |
| T4 | todo | P3 | 2 | 0% | |
| T5 | todo | P3 | 2 | 0% | |
| T6 | todo | P3 | 1 | 0% | |
| T7 | todo | P3 | 2 | 0% | |
| T8 | todo | P2 | 2 | 50% | |
| T12 | todo | P2 | 2 | 0% | |
| T13 | todo | P3 | 2 | 0% | |
| T14 | todo | P3 | 2 | 0% | |
| T15 | todo | P3 | 1 | 0% | |
| T16 | todo | P3 | 1 | 0% | |

Audit note (2026-10-07): no XSS — every `set:html` sink passes the allowlist/`safeHref` guards; JSON-LD already escapes `<`; no secrets in content (only placeholder strings in docs).

### T1. Home share card advertises the wrong brand ("Tiller")

`public/images/home/og.jpg` — used as `og:image`/`twitter:image` for the home page via `src/layouts/Base.astro:36` — reads "Tiller / Ship with agents. Keep the receipts." while the site is "listepo tools". The old Tiller generator `art/og.mjs` was removed on 2026-10-26; also stale is the comment at `src/styles/global.css:2`. Done means: `public/images/home/og.jpg` regenerated, stale CSS comment fixed.

### T3. Contract violation: product art is mandatory, not optional

`CONTENT_CONTRACT.md:42-46` promises adding a product needs only `content/projects/<slug>.md` and lists art as optional — but a product without art breaks the build (undefined theme images in `Base.astro:36`, `check-seo.mjs:67-68` "og:image file missing", hero `Picture` 404, `check-docs.mjs:112-117`). Done means: either the contract is corrected (art + THEMES entry required) or unknown tools fall back to the home art.

### T4. Deduplicate the copy-button markup and esc()

`src/lib/sections.ts:9-22` and `src/lib/docs.ts:87-103` carry the same code-block template; `esc()` is implemented 4× (`src/lib/html.ts:3`, `docs.ts:9`, `sections.ts:5`, `src/components/Terminal.astro:7`) and Terminal's copy omits `"`-escaping — a trap if reused inside an attribute. Done means: one shared helper for each.

### T5. Share slug/nav logic between the site and its CI checks

`slugOf` and `isExcluded` exist twice with identical semantics (`src/lib/docs.ts:52,127-132` and `scripts/check-docs.mjs:20-22`); any drift silently invalidates the check. Also `firstSentence` is re-implemented locally in `src/pages/index.astro:17` and two different `plain()` implementations exist (`docs.ts:105-113` vs `seo.ts:19-21`). Done means: one shared module both sides import; the doubled text helpers unified or renamed.

### T6. Fix the hardcoded CTA label and dead theme fields

`src/pages/[slug].astro:272` renders the visible label `github.com/pyrlyn/<wbr>{d.title}` regardless of `d.repo` — wrong for any product outside the org or whose title ≠ repo name; derive it from `d.repo`. `Theme.heroAlt` is always `""` (`src/lib/site.ts:50,55-58`) while real alt texts live in a local map (`[slug].astro:49-53`) and the home glyph map duplicates theme knowledge (`index.astro:33`). Done means: the label is derived, and hero alt/glyph live in THEMES with local maps deleted.

### T7. Robustness and cleanup batch

`src/lib/docs.ts:196` takes the first `# ` line anywhere as the title (would misparse inside fenced code; use the fence-aware scan `check-docs.mjs:26-39` already has); `CONTENT_CONTRACT.md:74-76` misdescribes `check:docs` as failing when it only warns; `_prototype-vite/` was removed on 2026-10-26 (done). Done means: all three addressed.

### T8. Land the 2026-10-17 site QA fixes and close the hosting findings

[pyrlyn/landing#37](https://github.com/pyrlyn/landing/pull/37) (merged 2026-10-17) added a real `public/favicon.ico` with a test and the report `docs/qa/2026-10-17-site-qa.md`; `build`, `check` and `npm test` pass. The QA found no functional bugs. Done means: the report's remaining findings are resolved or tracked: security headers on the live host (the Docker image sends them since the 2026-10-19 audit; Cloudflare does not) and the `www.pyrlyn.dev` vs `pyrlyn.dev` duplicate (hosting; needs a 301 redirect rule), the `rtok-plugin-sdk` link that returns 404 (upstream repo), and the swarfr page title length.

## Audit 2026-10-19

Three passes over `main` at 42f4d2d: (1) module walk, (2) entry points and trust boundaries,
(3) docs against code and tests. Overlap with earlier work: the XSS guards (`html.ts`, `safeHref`,
JSON-LD escaping, Terminal `esc`) noted on 2026-10-07 and in #6 still hold; the security headers
and title length from the #37 QA report stay under T8 for the live host.

Fixed in the audit PR (branch `fix/landing-audit-2026-10-19`):

- medium (pass 2): `homepage` accepted `javascript:`/`data:` URLs (`z.string().url()`) and was
  rendered as `href` in `src/pages/[slug].astro`; the schema now lives in
  `src/lib/project-schema.ts` and requires http(s), tested in `tests/project-schema.test.mjs`.
- low (passes 1+2): `docker/nginx.conf` sent no security headers and the nginx version;
  `docker/security-headers.conf` is included in the server and in `/_astro/`, tested in
  `tests/nginx.test.mjs` and with `nginx -t`.
- low (pass 3): 14 English docs fell into "More" (closes T2); `tests/docs-nav.test.mjs` keeps it so.
- low (pass 1): `scripts/check-docs.mjs` and `scripts/check-seo.mjs` built the root from
  `URL.pathname`; they use `fileURLToPath` now.
- low (pass 3): README and `CONTENT_CONTRACT.md` said the checks run in CI; no build job exists.

### T12. Restore a CI job for build, tests and post-build checks

The build/test workflow was removed in #25 (969fbb5); `.github/workflows` only runs actionlint and
CodeRabbit, so `npm run build`, `check`, `test`, `check:docs` and `check:seo` gate nothing and a
synced `homepage: javascript:` or broken link is only caught when someone builds locally. Severity:
medium. Done means: a pinned-SHA workflow with `permissions: contents: read` runs all five on
pull requests and pushes to `main` (the shared `pyrlyn/ci` route or a local file; confirm with the
creator because #25 removed it on purpose), and README says so again.

### T13. Add a script CSP once the inline scripts are hashed

`docker/security-headers.conf` sets only `frame-ancestors`, `object-src` and `base-uri`, because
`src/layouts/Base.astro:49,54` inline the intro and theme bootstraps (the theme one embeds
`define:vars`, so its hash changes per theme) and line 83 inlines JSON-LD. Without `script-src` a
future injection sink has no second line of defence. Severity: low. Done means: the bootstraps move
to a static file or get build-time hashes, and `script-src 'self' 'sha256-…'` ships in the snippet
and on the live host (T8), with a test that the hashes match the built HTML.

### T14. Decide placement for comparison pages and translations

`check:docs` still warns 113 times: 43 `content/docs/rtok/comparison/*` pages and the `ru/`/`uk/`
translations of rtok, cox, ketch and runa render under "More", while weft and swarfr exclude their
translations in `src/data/docs-nav.json`. The comparison titles run 73–178 characters, past the
70-character title budget `scripts/check-seo.mjs` warns on. Severity: low. Done means: one policy
(e.g. a "Comparisons" group with short titles, translations excluded or grouped per language),
applied to every product, and `tests/docs-nav.test.mjs` extended to cover those folders.

### T15. Shorten product page titles over 70 characters

`check:seo` warns on 17 pages; `/swarfr/` and `/stator/` titles are built from long `tagline`
front matter (`src/lib/seo.ts`). The tagline comes from the product repos through sync-docs, so the
fix is either a shorter upstream tagline or an optional short `seo_title` field in
`src/lib/project-schema.ts`. Severity: low. Done means: no title warning for product pages.

### T16. Reset the copy-button timer on repeated clicks

`src/scripts/main.ts:100` starts a new 1.8 s reset timer per click without clearing the previous
one, so a second click shortly after the first gets its "Copied" state cleared early. Severity:
low (cosmetic). Done means: one timer per button (store it and `clearTimeout`), as `showToast`
already does.
