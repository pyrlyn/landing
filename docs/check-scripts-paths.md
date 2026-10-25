# Post-build check scripts: dist path validation

## How it works
`scripts/check-seo.mjs` and `scripts/check-docs.mjs` take an optional `distDir` CLI argument.
It must be a relative path made of `[A-Za-z0-9_.-]` segments, with no `..`, and must resolve
inside the repository root. Anything else exits with code 1 before any file is read.
This fixes SonarCloud `jssecurity:S8707` (path traversal from CLI arguments).

## Enable
Always on; no configuration.

## Disable
Not configurable. To check a build outside the repo, copy or symlink it under the repo first.

## Test
- `npm run build && npm run check:seo && npm run check:docs` passes.
- `node scripts/check-seo.mjs ../x` and `node scripts/check-seo.mjs /etc` exit 1 with "invalid dist dir".

## Limitations
A symlink inside the repo that points outside is still followed.

# Related fixes in the same change

## check-docs skips 500.html
nginx serves `dist/500.html` for 5xx at any path, so its home link is a literal `/` on purpose
(see `src/pages/500.astro`). `check-docs.mjs` no longer applies the base-path link rule to it.
Disable: remove the `500.html` filter in `scripts/check-docs.mjs`. Test: `npm run check:docs` prints `OK`.
Limitation: other links on the 500 page are not link-checked.

## docs-nav entries for newly synced docs
Content sync added `rtok/ci-platforms.md`, `cox/plugins/jev.md` and `stator/THREADS.md` without
nav entries, which failed `tests/docs-nav.test.mjs`. They are now listed in `src/data/docs-nav.json`.
Test: `npm test` (26/26). Limitation: every future synced doc still needs a nav entry or an exclude.
