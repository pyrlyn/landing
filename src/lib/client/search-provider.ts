/* Search data source. The UI (the Search island) only knows this interface, so the backend can change freely. */
export type SearchHit = {
  id: string;
  title: string;
  /** Short context shown on the right, e.g. product or docs section. */
  section?: string;
  /** Absolute or site-relative URL to open. */
  url: string;
  /** Optional CSS colour for the row marker (product accent). */
  accent?: string;
};

export type SearchProvider = (q: string, signal: AbortSignal) => Promise<SearchHit[]>;

// Local sample data so every UI state can be reviewed without a backend.
const SAMPLE: SearchHit[] = [
  { id: "rtok", title: "rtok — token-efficient CLI output", section: "Product", url: "/rtok/", accent: "var(--accent)" },
  { id: "rtok-docs", title: "rtok: Getting started", section: "Docs", url: "/rtok/docs/" },
  { id: "cox", title: "cox — context orchestration", section: "Product", url: "/cox/" },
  { id: "cox-docs", title: "cox: Configuration", section: "Docs", url: "/cox/docs/" },
  { id: "ketch", title: "ketch — request routing", section: "Product", url: "/ketch/" },
  { id: "ketch-docs", title: "ketch: Routes and fallbacks", section: "Docs", url: "/ketch/docs/" },
  { id: "claude", title: "Claude Code setup", section: "Docs", url: "/rtok/docs/" },
];

/** Stub provider: filters SAMPLE locally. Type "error" to preview the error state. No network. */
export const stubProvider: SearchProvider = (q, signal) =>
  new Promise((resolve, reject) => {
    const t = setTimeout(() => {
      if (q.trim().toLowerCase() === "error") return reject(new Error("stub error"));
      const n = q.trim().toLowerCase();
      resolve(SAMPLE.filter((h) => `${h.title} ${h.section ?? ""}`.toLowerCase().includes(n)).slice(0, 8));
    }, 80);
    signal.addEventListener("abort", () => { clearTimeout(t); reject(new DOMException("aborted", "AbortError")); }, { once: true });
  });

export type AlgoliaConfig = { appId?: string; searchKey?: string; index?: string };
type AlgoliaRecord = { objectID: string; title?: string; section?: string; product?: string; url?: string; accent?: string };

/** Algolia over REST (search-only key from build config, see src/lib/config.ts). Toha's request code from #49. */
export function algoliaProvider({ appId, searchKey, index = "pyrlyn" }: Required<Pick<AlgoliaConfig, "appId" | "searchKey">> & AlgoliaConfig): SearchProvider {
  const endpoint = `https://${appId}-dsn.algolia.net/1/indexes/${encodeURIComponent(index)}/query`;
  return async (q, signal) => {
    const res = await fetch(endpoint, {
      method: "POST",
      signal,
      headers: { "X-Algolia-Application-Id": appId, "X-Algolia-API-Key": searchKey },
      body: JSON.stringify({ query: q, hitsPerPage: 8 }),
    });
    if (!res.ok) throw new Error(`Algolia ${res.status}`);
    const data = (await res.json()) as { hits: AlgoliaRecord[] };
    return data.hits.map((h) => ({
      id: h.objectID,
      title: h.title ?? h.objectID,
      section: h.section ?? h.product,
      url: h.url ?? "#",
      accent: h.accent,
    }));
  };
}

/**
 * Picks the data source:
 * - Algolia keys configured -> Algolia (dev and prod);
 * - no keys, dev server     -> local stub, so the UI can be reviewed;
 * - no keys, production     -> null: the UI shows "Search isn't available yet" and makes no requests.
 * TODO(Toha): the Algolia index (default "pyrlyn") still has to be created and filled with records
 * { objectID, title, url, section?, accent? } (admin key script or Algolia Crawler); until then queries return nothing.
 */
export function pickProvider(cfg: AlgoliaConfig, dev: boolean): SearchProvider | null {
  if (cfg.appId && cfg.searchKey) return algoliaProvider({ ...cfg, appId: cfg.appId, searchKey: cfg.searchKey });
  return dev ? stubProvider : null;
}
