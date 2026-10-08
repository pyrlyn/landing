# landing

<https://github.com/pyrlyn/landing>

Astro static storefront ("listepo tools", https://pyrlyn.github.io/landing/) marketing rtok, cox and ketch; product pages and docs sections are generated from Markdown synced from each product repository, with post-build CI validators for docs coverage, links, and SEO.

| # | Status | Priority | Complexity | Readiness | Agent |
| --- | --- | --- | --- | --- | --- |
| T1 | todo | P1 | 2 | 0% | |
| T2 | todo | P2 | 1 | 0% | |
| T3 | todo | P2 | 2 | 0% | |
| T4 | todo | P3 | 2 | 0% | |
| T5 | todo | P3 | 2 | 0% | |
| T6 | todo | P3 | 1 | 0% | |
| T7 | todo | P3 | 2 | 0% | |

Audit note (2026-10-07): no XSS — every `set:html` sink passes the allowlist/`safeHref` guards; JSON-LD already escapes `<`; no secrets in content (only placeholder strings in docs).

### T1. Home share card advertises the wrong brand ("Tiller")

`public/images/home/og.jpg` — used as `og:image`/`twitter:image` for the home page via `src/layouts/Base.astro:36` — reads "Tiller / Ship with agents. Keep the receipts." while the site is "listepo tools". The generator is stale too (`art/og.mjs:3,24` hardcodes Tiller), as is the comment at `src/styles/global.css:2`. Done means: `art/og.mjs` cards and brand updated, `public/images/home/og.jpg` regenerated, stale CSS comment fixed.

### T2. rtok's newest doc falls into the uncurated "More" tab

`content/docs/rtok/agents-and-worktrees.md` appears in neither `groups` nor `exclude` for rtok in `src/data/docs-nav.json:3-37`, so it renders under the trailing "More" group (`src/lib/docs.ts:175-176`) and `check:docs` warns on every CI run (`scripts/check-docs.mjs:65`). Done means: the page sits in a proper rtok group (e.g. Guides) with a short title and the warning is gone.

### T3. Contract violation: product art is mandatory, not optional

`CONTENT_CONTRACT.md:42-46` promises adding a product needs only `content/projects/<slug>.md` and lists art as optional — but a product without art breaks the build (undefined theme images in `Base.astro:36`, `check-seo.mjs:67-68` "og:image file missing", hero `Picture` 404, `check-docs.mjs:112-117`). Done means: either the contract is corrected (art + THEMES entry required) or unknown tools fall back to the home art.

### T4. Deduplicate the copy-button markup and esc()

`src/lib/sections.ts:9-22` and `src/lib/docs.ts:87-103` carry the same code-block template; `esc()` is implemented 4× (`src/lib/html.ts:3`, `docs.ts:9`, `sections.ts:5`, `src/components/Terminal.astro:7`) and Terminal's copy omits `"`-escaping — a trap if reused inside an attribute. Done means: one shared helper for each.

### T5. Share slug/nav logic between the site and its CI checks

`slugOf` and `isExcluded` exist twice with identical semantics (`src/lib/docs.ts:52,127-132` and `scripts/check-docs.mjs:20-22`); any drift silently invalidates the check. Also `firstSentence` is re-implemented locally in `src/pages/index.astro:17` and two different `plain()` implementations exist (`docs.ts:105-113` vs `seo.ts:19-21`). Done means: one shared module both sides import; the doubled text helpers unified or renamed.

### T6. Fix the hardcoded CTA label and dead theme fields

`src/pages/[slug].astro:272` renders the visible label `github.com/pyrlyn/<wbr>{d.title}` regardless of `d.repo` — wrong for any product outside the org or whose title ≠ repo name; derive it from `d.repo`. `Theme.heroAlt` is always `""` (`src/lib/site.ts:50,55-58`) while real alt texts live in a local map (`[slug].astro:49-53`) and the home glyph map duplicates theme knowledge (`index.astro:33`). Done means: the label is derived, and hero alt/glyph live in THEMES with local maps deleted.

### T7. Robustness and cleanup batch

`src/lib/docs.ts:196` takes the first `# ` line anywhere as the title (would misparse inside fenced code; use the fence-aware scan `check-docs.mjs:26-39` already has); `CONTENT_CONTRACT.md:74-76` misdescribes `check:docs` as failing when it only warns; `_prototype-vite/` is dead weight already excluded everywhere — archive it to a branch like the previous site. Done means: all three addressed.
