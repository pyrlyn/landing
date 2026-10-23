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

export const GA_MEASUREMENT_ID = /^G-[A-Z0-9]+$/.test(val("GA_MEASUREMENT_ID")) ? val("GA_MEASUREMENT_ID") : "";
