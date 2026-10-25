// Pro prices and the wishlist from the backend through @pyrlyn/sdk. The build (src/lib/api.ts) and
// the browser (src/scripts/pro.ts) share this module, so it must not read build-time config.
import { createClient, SdkError, type ProjectsQuery } from "@pyrlyn/sdk";

export type ApiPrice = NonNullable<ProjectsQuery["projects"][number]["price"]>;

const INTERVAL: Record<string, string> = { MONTH: "mo", YEAR: "yr" };

/** "$9 / mo" from a price in minor units; the currency says how many minor digits it has (JPY none). */
export function formatPrice({ amount, currency, interval }: ApiPrice): string {
  const digits = new Intl.NumberFormat("en-US", { style: "currency", currency }).resolvedOptions().maximumFractionDigits ?? 2;
  const value = amount / 10 ** digits;
  const money = new Intl.NumberFormat("en-US", {
    style: "currency",
    currency,
    minimumFractionDigits: Number.isInteger(value) ? 0 : digits,
    maximumFractionDigits: digits,
  }).format(value);
  return `${money} / ${INTERVAL[interval] ?? interval.toLowerCase()}`;
}

/** Every project the API knows: slug → formatted Pro price, or null while it sells no plan. */
export async function fetchPrices(url: string, fetch?: typeof globalThis.fetch): Promise<Map<string, string | null>> {
  const { projects } = await createClient({ url, fetch }).Projects();
  return new Map(projects.map((p) => [p.slug, p.price ? formatPrice(p.price) : null]));
}

/** What the wishlist form says when the API refuses or cannot be reached. */
export function wishlistError(error: unknown): string {
  if (error instanceof SdkError) {
    const code = error.errors[0]?.extensions?.["code"];
    if (code === "BAD_USER_INPUT") return "Enter a valid email address.";
    if (code === "RATE_LIMITED" || error.status === 429) return "Too many requests. Try again in a minute.";
  }
  return "The wishlist cannot be reached right now. Try again later.";
}
