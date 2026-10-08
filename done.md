# Done

The storefront, the three product pages, and the docs section already build. CI (`.github/workflows/pages.yml`) runs the Astro build, `check`, `check:docs`, and `check:seo`, then deploys `main` to GitHub Pages.

### T8. Add the required project files

The project was missing `AGENTS.md`, `done.md`, `roadmap.md`, `ideas.md`, and `toolchain.md`. Done means: all files exist, with the real toolchain (Node, npm, Astro) in `toolchain.md`.

Those files are in the repo root. `toolchain.md` lists Node, npm, and Astro.

### T9. Test storefront text and catalog helpers

`src/lib` and `src/data` turn Markdown and front matter into titles, HTML, and catalog order, and nothing checked those functions. Art generation and the Vite prototype stay out of the suite. Done means: `npm test` imports the real modules (with a small hook so Node can load Astro's `import.meta.env` and extension-less paths) and checks the branching helpers.

`node --test tests/*.test.mjs` passed 9 tests: HTML allowlist, section parsing, site paths, SEO text, catalog order, and doc slugs. The test loader rewrites `import.meta.env`, extension-less imports, and JSON imports that Vite accepts without an import attribute. Art generation and `_prototype-vite/` stay untested.
