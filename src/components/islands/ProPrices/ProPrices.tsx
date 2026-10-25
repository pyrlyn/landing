// Catches up with Pro price changes since the build: refetches the API's prices and rewrites the static
// [data-pro-price] spans (cards, menu, pricing). Renders nothing; mounted only when API_URL is set.
// On failure the build-time prices stay.
import { useEffect } from "react";
import { fetchPrices } from "../../../lib/prices";

export default function ProPrices({ api }: { api: string }) {
  useEffect(() => {
    const nodes = document.querySelectorAll<HTMLElement>("[data-pro-price]");
    if (!nodes.length) return;
    let alive = true;
    fetchPrices(api).then((prices) => {
      if (!alive) return;
      for (const node of nodes) {
        const price = prices.get(node.dataset.proPrice ?? "");
        if (price) node.textContent = price;
      }
    }).catch((error) => console.warn("pro: price refresh failed, build-time prices stay", error));
    return () => { alive = false; };
  }, [api]);
  return null;
}
