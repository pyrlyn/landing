# Team and Support pages

## How it works
- Footer (`src/layouts/Base.astro`) links "Our team" (`/team/`) and "Support" (`/support/`) after "GitHub". Nothing was removed.
- `/team/` (`src/pages/team.astro`): Ivan's photo in the center, eight AI tools around it as small
  figures with an icon head and a caption (Kimi, OpenAI / ChatGPT, Grok, Claude, Gemini, Cursor,
  GitHub Copilot, DeepSeek). Icons are Simple Icons paths (CC0) in `src/data/team-tools.ts`;
  OpenAI and Grok are not in Simple Icons and use neutral monograms. A note says logos belong to their
  owners and imply no endorsement. Below: "About Pyrlyn" (facts from public pyrlyn repo descriptions)
  and "About me".
- Photo: if `public/team/ivan.jpg` exists at build time it is used; otherwise `public/team/ivan-placeholder.svg`.
- `/support/` (`src/pages/support.astro`): a static GET form (`title`, `body`) to
  `https://github.com/pyrlyn/support/issues/new`, which opens a prefilled public issue in a new tab.
  No backend and no email: the site is static nginx and the repo has no real support address.
- `scripts/check-seo.mjs` expects no product JSON-LD on `team/` and `support/`.

## Enable
On by default after build. To add the photo: put a square JPEG (>= 480x480) at `public/team/ivan.jpg` and rebuild.

## Disable
Delete the page file and its footer `<li>`, and its line in `expectTypes` in `scripts/check-seo.mjs`.

## Test
`npm test` (`tests/footer-pages.test.mjs`), `npm run build`, `npm run check:docs`, `npm run check:seo`.
Manual: open `/support/`, submit; GitHub shows a new-issue form in pyrlyn/support with the text filled in.

## Limitations
- English only; non-doc pages have no i18n.
- Sending needs a GitHub account; the message is public.
- Very long messages may exceed URL length limits (form caps body at 5000 chars).
- Under 560px the orbit becomes a stacked grid.
