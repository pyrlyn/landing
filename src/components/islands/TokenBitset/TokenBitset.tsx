// rtok bitset: blocks settle into their target state once 40% in view; Δtok counts removed blocks.
// Markup: static Astro children. Logic: initBitset in lib/client/visuals.ts. Reduced motion: final state.
import type { ReactNode } from "react";
import { Enhance } from "../hooks/useEnhance";
import { initBitset } from "../../../lib/client/visuals";

const init = (el: HTMLElement) => {
  const root = el.matches("[data-bitset]") ? el : el.querySelector<HTMLElement>("[data-bitset]");
  return root ? initBitset(root) : undefined;
};
export default function TokenBitset({ children }: { children?: ReactNode }) {
  return <Enhance init={init}>{children}</Enhance>;
}
