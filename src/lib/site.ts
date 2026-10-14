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
  weft: ["The weft command: validate, fmt, explain", "MCP server and editor plugins", "Render to HTML, React, SwiftUI and Slint"],
  swarfr: ["Lossless compress and dedupe", "Seed new worktrees from a sibling", "Read-only status and advise"],
  aulo: ["The full specification and plan", "gRPC API reference", "Free, open-source edition when it ships"],
  scull: ["Rust core, native UI per platform", "Research on six terminals", "Open development from day one"],
  kerf: ["Research on ten editors", "Sandboxed WASM plugin design", "Open development from day one"],
  stator: ["ts and js modes", "stator explain verdicts", "vite-stator bundler for js mode"],
  bindsmith: ["Six platforms from one YAML", "Review markers and verify", "CI-safe dump, diff and --check"],
};

/** One license wording for every project (rtok, cox, ketch, runa, Mailune, swarfr, aulo, Kerf, Stator and bindsmith share the same triple license; Weft and Scull override it in LICENSE_OVERRIDES). */
export const LICENSE = {
  short: "GPLv3, royalty-free or commercial — your choice",
  items: [
    { name: "GNU GPLv3", text: "free for open-source applications on any platform, including embedded systems." },
    { name: "Royalty-free license", text: "free for proprietary desktop, mobile and web applications, as long as you disclose that your application uses the project. Embedded systems are not covered." },
    { name: "Commercial license", text: "for proprietary applications, including embedded systems, without the attribution requirement." },
  ],
};

/** Products under another license than LICENSE (Weft is Apache-2.0). */
export const LICENSE_OVERRIDES: Record<string, { short: string; lead: string; items: { name: string; text: string }[] }> = {
  weft: {
    short: "Apache License 2.0",
    lead: "Weft is licensed under the Apache License 2.0:",
    items: [{ name: "Apache License 2.0", text: "use, modify and distribute it, including in proprietary software, under the terms in LICENSE." }],
  },
};
export const licenseFor = (slug: string) => {
  const o = LICENSE_OVERRIDES[slug];
  return o ?? { short: LICENSE.short, lead: "", items: LICENSE.items };
};

export type ThemeKey = "home" | "rtok" | "cox" | "ketch" | "runa" | "mailune" | "weft" | "swarfr" | "aulo" | "scull" | "kerf" | "stator" | "bindsmith";

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
  weft: { accent: "#A89BFF", accent2: "#F2C46D", accentLight: "#5B4BD1", bg: "#0C0B16", images: "weft", heroAlt: "" },
  swarfr: { accent: "#E9B45A", accent2: "#7FA8D6", accentLight: "#9A6A16", bg: "#0E0D0B", images: "swarfr", heroAlt: "" },
  aulo: { accent: "#FF7AA2", accent2: "#7EF2D0", accentLight: "#C2386A", bg: "#120B10", images: "aulo", heroAlt: "" },
  scull: { accent: "#EDE6D6", accent2: "#45E39A", accentLight: "#2E7D5B", bg: "#0A0C0B", images: "scull", heroAlt: "" },
  kerf: { accent: "#C6F24E", accent2: "#FF6BD6", accentLight: "#5E7A12", bg: "#0B0C08", images: "kerf", heroAlt: "" },
  stator: { accent: "#5B7CFF", accent2: "#9AA3B5", accentLight: "#3352D6", bg: "#0C0E14", images: "stator", heroAlt: "" },
  bindsmith: { accent: "#C87941", accent2: "#13B9FD", accentLight: "#A35F2E", bg: "#121416", images: "bindsmith", heroAlt: "" },
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
  weft: "Prototype · no release yet",
  swarfr: "In development · no stable release yet",
  aulo: "Specification only · no code yet",
  scull: "Early scaffold · no release yet",
  kerf: "Research · no code yet",
  stator: "Research compiler · no release yet",
  bindsmith: "In development · no release yet",
};
export const RELEASE_STATUS_FALLBACK = "In development";

/** README facts the synced site copy does not carry but the page must keep, worded as a plain note (no "From the README:" prefix).
 *  Source: each repo's README.md (rtok: the limitations list; runa: the status paragraph its synced Overview carries).
 *  (cox's "under active development, APIs not stable" status is already in its synced Overview and the hero status card.) */
export const CAVEATS: Partial<Record<ThemeKey, string>> = {
  runa: "runa has no published release yet, so the installer above works from the first GitHub release. Until then, build from source (see Install below).",
  rtok: "Token savings are estimates from a chars-per-token heuristic until matched against provider usage. No live A/B cost reduction has been established yet.",
};
