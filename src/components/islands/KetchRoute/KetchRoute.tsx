// ketch route: the package chip travels the offset-path and the stroke draws with scroll; rollback runs back.
// Markup: static Astro children. Logic: initRoute in lib/client/visuals.ts. Reduced motion: final state.
import type { ReactNode } from "react";
import { Enhance } from "../hooks/useEnhance";
import { initRoute } from "../../../lib/client/visuals";

const init = (el: HTMLElement) => {
  const root = el.matches("[data-route]") ? el : el.querySelector<HTMLElement>("[data-route]");
  return root ? initRoute(root) : undefined;
};
export default function KetchRoute({ children }: { children?: ReactNode }) {
  return <Enhance init={init}>{children}</Enhance>;
}
