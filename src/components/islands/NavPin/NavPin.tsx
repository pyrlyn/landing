// Product / docs pages: auto-collapsing header (ported from feat/product-header-motion). The collapse is CSS
// (global.css "Auto-hiding header"): this island arms it with [data-collapsed] on mount, the header slides up
// --header-autohide (4s) later and comes back on hover or keyboard focus; without JS it simply stays. This island renders the slim peek strip (logo = static children + chevron
// pointing down: "Show menu") and pins the header open; the static .nav__unpin button in the header
// (chevron up: "Hide menu", ≥600px) unpins. Below 600px a tap outside or Escape unpins.
// Mobile focus fix: focus is moved between the two chevrons only for keyboard use; after a tap it is
// dropped instead, so a focused (hidden) button can't keep the header open or flash a focus ring.
import { useCallback, useEffect, useRef, useState, type MouseEvent, type ReactNode } from "react";
import { useDocumentEvent } from "../hooks/useDocumentEvent";
import { prefersReducedMotion } from "../../../lib/client/media";

export default function NavPin({ children }: { children?: ReactNode }) {
  const ref = useRef<HTMLDivElement>(null);
  const peekRef = useRef<HTMLButtonElement>(null);
  const [pinned, setPinned] = useState(false);
  const pinnedRef = useRef(pinned);
  pinnedRef.current = pinned;
  const nav = () => ref.current?.closest<HTMLElement>("[data-nav-auto]") ?? null;
  const unpinBtn = () => nav()?.querySelector<HTMLButtonElement>(".nav__unpin") ?? null;

  const set = useCallback((next: boolean, keyboard: boolean) => {
    const n = nav();
    if (!n) return;
    const hadFocus = n.contains(document.activeElement);
    n.toggleAttribute("data-pinned", next);
    pinnedRef.current = next;
    setPinned(next);
    const unpin = unpinBtn();
    if (keyboard) {
      if (next && unpin && unpin.offsetParent) unpin.focus({ preventScroll: true });
      else if (!next && hadFocus) peekRef.current?.focus({ preventScroll: true });
    } else if (hadFocus) (document.activeElement as HTMLElement | null)?.blur();
  }, []);

  // Arm the auto-collapse. With motion the CSS transition-delay times it; under reduced motion transitions
  // are off, so wait the same --header-autohide here instead of hiding the header at once.
  useEffect(() => {
    const n = nav();
    if (!n) return;
    const ms = (parseFloat(getComputedStyle(document.documentElement).getPropertyValue("--header-autohide")) || 4) * 1000;
    // With cached JS the island can mount before the header has a computed style; then the change would not
    // transition and the header would be hidden at once. Reading the style first gives the transition its start.
    const arm = () => { const inner = n.querySelector(".nav__inner"); if (inner) void getComputedStyle(inner).opacity; n.setAttribute("data-collapsed", ""); };
    const t = window.setTimeout(arm, prefersReducedMotion() ? ms : 0);
    return () => { clearTimeout(t); n.removeAttribute("data-collapsed"); };
  }, []);

  useEffect(() => {
    const unpin = unpinBtn();
    if (!unpin) return;
    const f = (e: globalThis.MouseEvent) => set(false, e.detail === 0);
    unpin.addEventListener("click", f);
    return () => unpin.removeEventListener("click", f);
  }, [set]);
  useDocumentEvent("pointerdown", (e) => { if (pinnedRef.current && !nav()?.contains(e.target as Node)) set(false, false); });
  useDocumentEvent("keydown", (e) => { if (e.key === "Escape" && pinnedRef.current) set(false, true); });

  // detail === 0: activated from the keyboard (Enter / Space), not a pointer.
  const onPeek = (e: MouseEvent) => set(true, e.detail === 0);
  return (
    <div className="nav__peek" ref={ref}>
      <div className="nav__peek-pill glass">
        {children}
        <button ref={peekRef} className="nav__peek-btn" type="button" aria-label="Show menu" aria-expanded={pinned} data-nav-pin onClick={onPeek}>
          <svg viewBox="0 0 12 12" width="14" height="14" aria-hidden="true"><path d="m3 7.5 3-3 3 3" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round" /></svg>
        </button>
      </div>
    </div>
  );
}
