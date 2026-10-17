# landing

If an AGENTS.md or CLAUDE.md exists higher in the tree, follow it too. On conflict, ask the creator.

Astro storefront for rtok, cox, and ketch ("listepo tools"). Product copy is `content/projects/*.md`. Synced docs are `content/docs/<product>/`. Internal links go through `u()` in `src/lib/site.ts`. `astro.config.mjs` reads `SITE_URL` (default `https://pyrlyn.dev`) and `SITE_BASE` (default `/landing/`).

```sh
npm ci
npm run dev
npm run build
npm run check
npm run check:docs
npm run check:seo
npm test
```

`predev` and `prebuild` copy logo files with `pyrlyn-brand-copy`. `npm test` imports `src/lib` and `src/data` under Node. `tests/hooks.mjs` supplies `import.meta.env`, `import.meta.glob`, and `astro:content` because those exist only in the Astro build. Do not test `art/` or `_prototype-vite/`.

jactionlint and hk are pinned in `mise.toml`. `.github/jactionlint.yaml` applies the default profile and the baseline `.github/jactionlint-baseline.json`. `hk.pkl` runs jactionlint as a pre-push hook:

```sh
mise x -- jactionlint                  # lint workflows, composite actions, dependabot.yml
mise x -- jactionlint --baseline-write # refresh the baseline after fixing findings
mise x -- hk install --mise            # install the pre-push hook once per clone
```

Do not invent ratings, reviews, or Pro prices in structured data. Pro prices in `src/lib/site.ts` are placeholders.

Product categories live in `src/lib/site.ts` (`CATEGORIES` and `PRODUCT_CATEGORY`). Every new, renamed or removed product must be put into the right category there. Keep the categories up to date and consistent in the Products menu, the home catalog and pricing cards, and the product-page badge. `tests/categories.test.mjs` enforces it. The site footer lists product names only, with no per-product docs links.
