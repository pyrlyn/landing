# Team and Support pages

## How it works
- `src/pages/team.astro` renders `/team/` ("Our team"). It lists Ivan Tugay with his public GitHub
  profile (https://github.com/listepo). The content is a placeholder to be filled in.
- `src/pages/support.astro` renders `/support/`. It points to GitHub Issues: the tool's own repo for
  bugs and features, `pyrlyn/.github` issues for general questions, and the FAQ. No email or form.
- The footer in `src/layouts/Base.astro` links both pages after "GitHub". Nothing was removed.
- `scripts/check-seo.mjs` expects no product JSON-LD on `team/` and `support/` (only the site-wide
  Organization). Both pages are indexed and appear in the sitemap automatically.

## Enable
On by default after build.

## Disable
Delete the page file and its footer `<li>`, and drop its line in `expectTypes` in `scripts/check-seo.mjs`.

## Test
`npm test` (`tests/footer-pages.test.mjs`), `npm run build`, `npm run check:docs`, `npm run check:seo`.

## Limitations
- English only; the site has no i18n for non-doc pages.
- Team content is a placeholder.
- `pyrlyn/.github` issues must stay enabled for the general-questions link.
