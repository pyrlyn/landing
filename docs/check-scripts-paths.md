# Post-build check scripts: dist path validation

## How it works
`scripts/check-seo.mjs` and `scripts/check-docs.mjs` always check `<repo>/dist`
(`scripts/lib/dist-dir.mjs`). They take no path argument; any argument exits with code 1.
The path never comes from user input, which fixes SonarCloud `jssecurity:S8707`.

## Enable
Always on; no configuration.

## Disable
Not configurable. To check another build, copy it to `dist/` first.

## Test
- `npm run build && npm run check:seo && npm run check:docs` passes.
- `node scripts/check-seo.mjs x` exits 1 with "unexpected argument".

## Limitations
Only `dist/` can be checked.

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

## undici bumped to 8.12.3 (lockfile only)
`npm audit` reported one high-severity issue in `undici` 8.11.2 (transitive via `astro` → `unifont`):
GHSA-4qv8-2p9j-2685, GHSA-f72v-6w45-q472, GHSA-xjj6-f33q-929c. `npm audit fix` moved it to 8.12.3.
Test: `npm audit` reports 0 vulnerabilities. Limitation: build-time dependency only; not shipped to the browser.
