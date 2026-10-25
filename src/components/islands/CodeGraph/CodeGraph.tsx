// rtok MCP code graph: edges draw in once 40% in view (CSS .is-armed/.is-drawn).
// Markup: static Astro children. Logic: initGraph in lib/client/visuals.ts. Reduced motion: final state.
import type { ReactNode } from "react";
import { Enhance } from "../hooks/useEnhance";
import { initGraph } from "../../../lib/client/visuals";

const init = (el: HTMLElement) => {
  const root = el.matches("[data-graph]") ? el : el.querySelector<HTMLElement>("[data-graph]");
  return root ? initGraph(root) : undefined;
};
export default function CodeGraph({ children }: { children?: ReactNode }) {
  return <Enhance init={init}>{children}</Enhance>;
}
