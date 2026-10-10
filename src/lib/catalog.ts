// Marketplace helpers over the projects collection (works for any number of tools).
import { getCollection, type CollectionEntry } from "astro:content";
import { PRO, PRO_FALLBACK, RELEASE_STATUS, RELEASE_STATUS_FALLBACK, THEMES, type ThemeKey } from "./site";

export type Tool = CollectionEntry<"projects">;

const DEFAULT_ORDER = ["rtok", "cox", "ketch", "runa", "mailune"];
const rank = (t: Tool) => t.data.order ?? (DEFAULT_ORDER.includes(t.id) ? DEFAULT_ORDER.indexOf(t.id) : 1000);

export async function getTools(): Promise<Tool[]> {
  const all = await getCollection("projects");
  return all.sort((a, b) => rank(a) - rank(b) || a.data.title.localeCompare(b.data.title));
}

export function featuredTool(tools: Tool[]): Tool {
  return tools.find((t) => t.data.featured) ?? tools[0];
}

export function proFor(t: Tool) {
  const base = PRO[t.id] ?? PRO_FALLBACK;
  return { price: t.data.pro_price ?? base.price, features: base.features };
}

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
