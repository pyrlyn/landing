/* Search data source. The UI (search.ts) only knows this interface, so the backend can change freely. */
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

// TODO(Toha): wire Algolia here — implement a SearchProvider that queries the index (honour `signal`)
// and maps each record to SearchHit { id, title, section?, url, accent? }, then export it as `provider`.
export const provider: SearchProvider = stubProvider;
