# landing

If an AGENTS.md or CLAUDE.md exists higher in the tree, follow it too. On conflict, ask the creator.

Astro storefront for rtok, cox, and ketch ("listepo tools"). Product copy is `content/projects/*.md`. Synced docs are `content/docs/<product>/`. Internal links go through `u()` in `src/lib/site.ts`. `astro.config.mjs` reads `SITE_URL` (default `https://pyrlyn.github.io`) and `SITE_BASE` (default `/landing/`).

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

Do not invent ratings, reviews, or Pro prices in structured data. Pro prices in `src/lib/site.ts` are placeholders.
