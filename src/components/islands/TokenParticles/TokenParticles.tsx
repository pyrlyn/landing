// rtok token particles: canvas, scroll-scrubbed into a narrow context band, reversible.
// Markup: static Astro children. Logic: initParticles in lib/client/visuals.ts. Reduced motion: final state.
import type { ReactNode } from "react";
import { Enhance } from "../hooks/useEnhance";
import { initParticles } from "../../../lib/client/visuals";

const init = (el: HTMLElement) => {
  const root = el.matches("[data-particles]") ? el : el.querySelector<HTMLElement>("[data-particles]");
  return root ? initParticles(root) : undefined;
};
export default function TokenParticles({ children }: { children?: ReactNode }) {
  return <Enhance init={init}>{children}</Enhance>;
}
