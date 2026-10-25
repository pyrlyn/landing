import { defineConfig } from "astro/config";
import sitemap from "@astrojs/sitemap";
import tailwindcss from "@tailwindcss/vite";

// Islands are Preact with compat: the React API (hooks, JSX types) via preact/compat, at a fraction of the size.
import preact from "@astrojs/preact";

// Static site served under a subpath. The base path follows the repository name; change the default
// below (or set SITE_BASE at build time) when the repo is renamed. Every internal link and
// asset goes through `u()` in src/lib/site.ts, so nothing else needs to change.
const base = process.env.SITE_BASE ?? "/landing/";

export default defineConfig({
  // Keep pre-Astro-7 spacing between inline elements.
  compressHTML: true,
  site: process.env.SITE_URL ?? "https://pyrlyn.dev",
  base,
  trailingSlash: "ignore",
  output: "static",
  // sitemap-index.xml + sitemap-0.xml under the base; robots.txt (src/pages/robots.txt.ts) points to it.
  // The wishlist pages are noindex mail-link targets (src/pages/wishlist/), not content.
  integrations: [sitemap({ filter: (page) => !/\/wishlist\//.test(page) }), preact({ compat: true })],
  // Tailwind v4 utilities only (no preflight), see src/styles/tailwind.css.
  vite: { plugins: [tailwindcss()] },
});