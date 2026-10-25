// Build-time view of the backend: the projects it knows and their Pro prices.
import { API_URL } from "./config";
import { fetchPrices } from "./prices";

// A hung API must not hang the build; the client refresh catches up later.
const TIMEOUT_MS = 10_000;

async function load(): Promise<Map<string, string | null>> {
  if (!API_URL) return new Map();
  try {
    return await fetchPrices(API_URL, (input, init) => fetch(input, { ...init, signal: AbortSignal.timeout(TIMEOUT_MS) }));
  } catch (error) {
    // Fail open: the site still builds with the placeholder prices and a disabled wishlist.
    console.warn(`landing: no prices from ${API_URL}, placeholders stay: ${error instanceof Error ? error.message : String(error)}`);
    return new Map();
  }
}

/** Slug → Pro price (null while the project sells none); empty when the API is off or unreachable. */
export const API_PROJECTS = await load();
