# Done

The storefront, the three product pages, and the docs section already build. Run the Astro build, `check`, `check:docs`, and `check:seo` locally.

### T8. Add the required project files

The project was missing `AGENTS.md`, `done.md`, `roadmap.md`, `ideas.md`, and `toolchain.md`. Done means: all files exist, with the real toolchain (Node, npm, Astro) in `toolchain.md`.

Those files are in the repo root. `toolchain.md` lists Node, npm, and Astro.

### T9. Test storefront text and catalog helpers

`src/lib` and `src/data` turn Markdown and front matter into titles, HTML, and catalog order, and nothing checked those functions. Art generation and the Vite prototype stay out of the suite. Done means: `npm test` imports the real modules (with a small hook so Node can load Astro's `import.meta.env` and extension-less paths) and checks the branching helpers.

`node --test tests/*.test.mjs` passed 9 tests: HTML allowlist, section parsing, site paths, SEO text, catalog order, and doc slugs. The test loader rewrites `import.meta.env`, extension-less imports, and JSON imports that Vite accepts without an import attribute. Art generation and `_prototype-vite/` stay untested.

### T8. Upgrade vulnerable site dependencies

The creator approved dependency updates after npm audit reported high and critical advisories. Preserve static routes, content and assets while upgrading Astro and the affected dependency graph.

Execution plan: capture existing tests/build output; update package manifests and lockfile using npm; preserve HTML whitespace behavior; verify tests, build, package audit and site-specific PDF/docs/SEO checks. Update toolchain documentation. Rollback is a revert of this PR; no runtime data migration is involved.

Validation: npm audit reports 0 vulnerabilities; all 160 routes and extracted HTML text match the Astro 5 baseline; unit tests and astro check pass. Preserved inline whitespace. Migration sources: https://docs.astro.build/en/guides/upgrade-to/v6/ and https://docs.astro.build/en/guides/upgrade-to/v7/ (checked 2026-10-15). The existing duplicate translated-title SEO failure is addressed separately by T9.

### T9. Distinguish translated documentation titles

Russian and Ukrainian lockfile pages have the same H1, so the SEO gate reports duplicate titles. Execution plan: add language labels to translated document metadata titles using the existing SEO module; preserve visible headings; add a title regression test and rerun the built-site SEO gate.

Validation: the duplicate-title SEO failure was reproduced; the new unit test failed before the helper was added. All 10 tests, build, Astro check (0 errors/warnings), docs check and SEO check pass. SEO retains 3 existing non-fatal metadata length warnings. Reused the SEO module and existing document source paths; no new dependency.

### T9. Validate documentation headings with the Markdown parser

The docs gate incorrectly rejects pages with a second level-one heading because the renderer promotes it to level two, while the check counts only literal ## and ### lines.

Execution plan: share source preparation between renderer and checker; count heading tokens with the existing marked dependency; add regression cases for repeated titles, Setext headings and fenced code; run tests, Astro checks, build, docs and SEO gates. No shared package offers this site-specific rendering contract; reuse marked rather than extend the handwritten Markdown scanner.

### T10. Distinguish documentation descriptions with shared introductions

Comparison documents reuse the same benchmark introduction and produce duplicate meta descriptions, failing the SEO gate.

Execution plan: add a metadata regression for distinct titles with identical summaries; use existing plain/clip helpers to include the page subject in the description; run tests, build and SEO checks.

Validation: regression tests failed with the old heading counter and metadata rule. All 19 tests pass, Astro check has zero errors/warnings (20 existing hints), the site builds, docs checks pass, and SEO checks pass with 17 existing warnings. Source preparation and marked are reused; no dependency changes.

### T11. Pin the CodeRabbit issue workflow required by action lint

The mandatory jactionlint check rejects the reusable workflow reference at main.

Execution plan: resolve the current main commit of pyrlyn/ci, pin that exact revision without changing workflow inputs or permissions, and rerun jactionlint. The existing lint gate is the regression test and fails before the pin.

Validation: mise exec -- jactionlint passes after pinning the current main revision 90377c3c3ed260407a1ad27e0cb2355bec9a2045. The unpinned-uses rule failed before the change.
