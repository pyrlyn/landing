// Site-wide constants and helpers.

/** Prefix an internal path with the configured base (`/shop-mvp/`). */
export function u(path = "/"): string {
  const base = import.meta.env.BASE_URL.replace(/\/$/, "");
  const p = path.startsWith("/") ? path : `/${path}`;
  return `${base}${p}`;
}

/** Storefront name (neutral; the marketplace sells several tools). */
export const BRAND = "listepo tools";

/**
 * PLACEHOLDER Pro tiers per tool. Frontmatter `pro_price` overrides the price.
 * Nothing here is a real offer: prices and Pro features are invented for the MVP.
 */
export const PRO: Record<string, { price: string; features: string[] }> = {
  rtok: { price: "$9 / mo", features: ["Team-wide ledger sync", "Hosted savings dashboard", "Priority support"] },
  cox: { price: "$15 / mo", features: ["Shared session history", "Org permission policies", "Priority support"] },
  ketch: { price: "$6 / mo", features: ["Private package registry", "Fleet lockfile sync", "Priority support"] },
};
export const PRO_FALLBACK = { price: "$9 / mo", features: ["Team features", "Hosted dashboard", "Priority support"] };

/** Short real "free" highlights per tool for the pricing table (from each project's site copy). */
export const FREE_HIGHLIGHTS: Record<string, string[]> = {
  rtok: ["Hooks, MCP server and API proxy", "Lossless archive with rtok expand", "Local stats and doctor"],
  cox: ["Terminal UI and headless runs", "Sandboxed tools by default", "Local cost ledger"],
  ketch: ["Install from any GitHub release", "SHA-256 verification", "Rollback, lock and sync"],
  runa: ["Fit check before any download", "Local GGUF and cloud APIs, same controls", "OpenAI-compatible runa serve"],
  mailune: ["Direct to IMAP, JMAP, Gmail and Graph", "On-device AI by default", "Encrypted mail stays local"],
};

/** One license wording for every project (rtok, cox, ketch, runa and Mailune share the same triple license). */
export const LICENSE = {
  short: "GPLv3, royalty-free or commercial — your choice",
  items: [
    { name: "GNU GPLv3", text: "free for open-source applications on any platform, including embedded systems." },
    { name: "Royalty-free license", text: "free for proprietary desktop, mobile and web applications, as long as you disclose that your application uses the project. Embedded systems are not covered." },
    { name: "Commercial license", text: "for proprietary applications, including embedded systems, without the attribution requirement." },
  ],
};

export type ThemeKey = "home" | "rtok" | "cox" | "ketch" | "runa" | "mailune";

export interface Theme {
  accent: string;
  accent2: string;
  accentLight: string;
  bg: string;
  /** Image folder under public/images/ */
  images: string;
  heroAlt: string;
}

/** Page themes. Frontmatter `accent`, `accent2`, `accentLight` override these for project pages. */
export const THEMES: Record<ThemeKey, Theme> = {
  home: { accent: "#4C8DFF", accent2: "#3EE6C4", accentLight: "#2F6BDB", bg: "#070910", images: "home", heroAlt: "" },
  rtok: { accent: "#5CE1FF", accent2: "#FF6B4A", accentLight: "#0B7FA0", bg: "#06101A", images: "rtok", heroAlt: "" },
  cox: { accent: "#A8E06C", accent2: "#6FD6B4", accentLight: "#3D8B3A", bg: "#070B09", images: "cox", heroAlt: "" },
  ketch: { accent: "#3DDCB0", accent2: "#5CC8FF", accentLight: "#0F6F5C", bg: "#06100F", images: "ketch", heroAlt: "" },
  runa: { accent: "#E85A3C", accent2: "#FFB088", accentLight: "#C94830", bg: "#120E0C", images: "runa", heroAlt: "" },
  mailune: { accent: "#8FB4D9", accent2: "#B7D0E8", accentLight: "#335C8C", bg: "#171714", images: "mailune", heroAlt: "" },
};

/**
 * Release status per product, owned by this site so the next docs sync cannot drop it.
 * Every product is in active development. rtok, ketch and cox publish 0.x builds on GitHub, but none
 * is a stable release yet; runa has a tag and no published release; Mailune has neither. A `status` in a product's synced
 * front matter wins over this table; products missing from both get RELEASE_STATUS_FALLBACK.
 */
export const RELEASE_STATUS: Record<string, string> = {
  rtok: "In development · no stable release yet",
  ketch: "In development · no stable release yet",
  cox: "In development · no stable release yet",
  runa: "In development · no release yet",
  mailune: "In development · no release yet",
};
export const RELEASE_STATUS_FALLBACK = "In development";

/** README facts the synced site copy does not carry but the page must keep, worded as a plain note (no "From the README:" prefix).
 *  Source: each repo's README.md (rtok: the limitations list; runa: the status paragraph its synced Overview carries).
 *  (cox's "under active development, APIs not stable" status is already in its synced Overview and the hero status card.) */
export const CAVEATS: Partial<Record<ThemeKey, string>> = {
  runa: "runa has no published release yet, so the installer above works from the first GitHub release. Until then, build from source (see Install below).",
  rtok: "Token savings are estimates from a chars-per-token heuristic until matched against provider usage. No live A/B cost reduction has been established yet.",
};
