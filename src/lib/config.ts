// Build-time site config. Values come from env (.env.local locally, build args in Docker); see .env.example.
// Only public values belong here: the Algolia key must be search-only, never the Admin key.
const env = import.meta.env as Record<string, string | undefined>;
const val = (k: string) => (env[k] ?? process.env[k] ?? "").trim();

export const ALGOLIA = {
  appId: val("ALGOLIA_APP_ID"),
  searchKey: val("ALGOLIA_SEARCH_KEY"),
  index: val("ALGOLIA_INDEX") || "pyrlyn",
};
export const ALGOLIA_ENABLED = Boolean(ALGOLIA.appId && ALGOLIA.searchKey);

// Public GA4 ID; override with GA_MEASUREMENT_ID, or set it to "off" to disable.
const ga = val("GA_MEASUREMENT_ID") || "G-61CRNDXXYK";
export const GA_MEASUREMENT_ID = /^G-[A-Z0-9]+$/.test(ga) ? ga : "";

// Public PostHog project key (phc_...); set to "off" to disable. Host defaults to the EU cloud.
const ph = val("POSTHOG_KEY") || "phc_oirHFL4DjcX7uyo2fmEaiXB3KA25Qyv8HoAbVEv32xkH";
export const POSTHOG_KEY = /^phc_[A-Za-z0-9]+$/.test(ph) ? ph : "";
export const POSTHOG_HOST = val("POSTHOG_HOST") || "https://eu.i.posthog.com";

// The backend's public GraphQL endpoint (https://…/graphql); empty = off. Without it the placeholder
// prices stay and the Pro wishlist button stays disabled, so a build without the API still ships.
const api = val("API_URL");
export const API_URL = /^https?:\/\/\S+$/.test(api) ? api : "";
