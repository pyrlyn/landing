import { z } from "astro/zod";
import { normalizeNames } from "./names";

// Contract: CONTENT_CONTRACT.md. Front matter is synced from each product repository, so it is
// untrusted input: link targets must be http(s), because z.string().url() alone accepts
// `javascript:` and `data:` URLs that would run script from an href on the product page.
const hex = z.string().regex(/^#[0-9a-fA-F]{6}$/, "6-digit hex, e.g. #5CE1FF");
const webUrl = z.string().url().regex(/^https?:\/\//i, "http(s) URL");

export const projectFields = z.object({
  title: z.string().min(1),
  tagline: z.string().min(1).max(160),
  repo: z.string().url().startsWith("https://github.com/"),
  homepage: webUrl.optional(),
  install: z.string().min(1),
  install_alternatives: z.array(z.string()).optional(),
  version: z.string().regex(/^\d+\.\d+\.\d+(-[\w.]+)?$/, "semver without a leading v"),
  /** Optional release status shown instead of the version badge (e.g. no release published yet). */
  status: z.string().max(48).optional(),
  accent: hex,
  /** Optional second accent (e.g. rtok's coral). Falls back to the site theme table. */
  accent2: hex.optional(),
  /** Optional darker accent for light surfaces. Falls back to the site theme table. */
  accentLight: hex.optional(),
  /** PLACEHOLDER Pro price label, e.g. "$9 / mo". Falls back to PRO in src/lib/site.ts. */
  pro_price: z.string().max(24).optional(),
  /** Feature this tool on the marketplace home (first featured wins; else first by order). */
  featured: z.boolean().optional(),
  /** Sort order on the marketplace (ascending); unsorted tools follow alphabetically. */
  order: z.number().int().optional(),
});

/**
 * `title` and `tagline` are display text: product names are capitalized here (see src/lib/names.ts),
 * so the sync can keep writing `title: rtok`. `rawTitle` keeps the synced spelling for logo marks
 * (the two-letter menu tile, card glyphs), which stay exactly as designed.
 */
export const projectSchema = projectFields.transform((d) => ({
  ...d,
  rawTitle: d.title,
  title: normalizeNames(d.title),
  tagline: normalizeNames(d.tagline),
}));
