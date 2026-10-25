// Marketplace helpers over the projects collection (works for any number of tools).
import { getCollection, type CollectionEntry } from "astro:content";
import { API_PROJECTS } from "./api";
import { CATEGORIES, categoryOf, PRO, PRO_FALLBACK, RELEASE_STATUS, RELEASE_STATUS_FALLBACK, THEMES, type ThemeKey } from "./site";

export type Tool = CollectionEntry<"projects">;

const DEFAULT_ORDER = ["rtok", "cox", "ketch", "runa", "mailune", "weft", "swarfr", "aulo", "scull", "kerf", "stator", "bindsmith"];
const rank = (t: Tool) => t.data.order ?? (DEFAULT_ORDER.includes(t.id) ? DEFAULT_ORDER.indexOf(t.id) : 1000);

export async function getTools(): Promise<Tool[]> {
  const all = await getCollection("projects");
  return all.sort((a, b) => rank(a) - rank(b) || a.data.title.localeCompare(b.data.title));
}

export function featuredTool(tools: Tool[]): Tool {
  return tools.find((t) => t.data.featured) ?? tools[0];
}

/** The API's price wins; front matter `pro_price` and PRO are placeholders for products it does not price. */
export function proFor(t: Tool) {
  const base = PRO[t.id] ?? PRO_FALLBACK;
  return { price: API_PROJECTS.get(t.id) ?? t.data.pro_price ?? base.price, features: base.features };
}

/** The API refuses wishlist entries for products it does not know, so only those get the form. */
export const hasWishlist = (t: Tool) => API_PROJECTS.has(t.id);

export function accentsFor(t: Tool) {
  const theme = THEMES[t.id as ThemeKey];
  return {
    accent: t.data.accent,
    accent2: t.data.accent2 ?? theme?.accent2 ?? t.data.accent,
    accentLight: t.data.accentLight ?? theme?.accentLight ?? t.data.accent,
  };
}

/** Full release status for the product hero badge, e.g. "In development · no stable release yet".
 *  Front matter `status` wins, then RELEASE_STATUS in site.ts, then the fallback. */
export const statusFor = (t: Tool) => t.data.status ?? RELEASE_STATUS[t.id] ?? RELEASE_STATUS_FALLBACK;

/** Short badge label for cards, the carousel, the featured block and the Products menu.
 *  Every product is in active development, so no `v<version>` pill implies a stable release. */
export const versionLabel = (_t: Tool) => "in development";

/** Short one-line install for cards: prefer a `ketch install …` alternative, else the primary command. */
export const cardInstall = (t: Tool) => t.data.install_alternatives?.find((c) => c.startsWith("ketch ")) ?? t.data.install;

/** Category of a tool (falls back to the last category so an unmapped tool is still shown). */
export const categoryFor = (t: Tool) => categoryOf(t.id) ?? CATEGORIES[CATEGORIES.length - 1];

/** Tools grouped by category in CATEGORIES order; empty categories are dropped. */
export function groupByCategory(tools: Tool[]) {
  return [...CATEGORIES]
    .sort((a, b) => a.order - b.order)
    .map((category) => ({ category, tools: tools.filter((t) => categoryFor(t).id === category.id) }))
    .filter((g) => g.tools.length > 0);
}
