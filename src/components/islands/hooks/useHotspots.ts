import { useEffect, useState, type RefObject } from "react";
import { useDocumentEvent } from "./useDocumentEvent";
/** Hotspots inside `rootRef` (static `.hotspot__dot` buttons + their `aria-controls` tips): one open at a
 *  time; Esc closes and returns focus to the dot; a click outside any hotspot closes. */
export function useHotspots(rootRef: RefObject<HTMLElement | null>) {
  const [open, setOpen] = useState<string | null>(null);
  const dots = () => [...(rootRef.current?.querySelectorAll<HTMLButtonElement>(".hotspot__dot") ?? [])];
  useEffect(() => {
    const list = dots();
    const fns = list.map((b) => { const id = b.getAttribute("aria-controls")!; const f = () => setOpen((o) => (o === id ? null : id)); b.addEventListener("click", f); return f; });
    return () => list.forEach((b, k) => b.removeEventListener("click", fns[k]));
  }, []);
  useEffect(() => {
    for (const b of dots()) {
      const id = b.getAttribute("aria-controls")!, on = id === open;
      b.setAttribute("aria-expanded", String(on));
      const tip = document.getElementById(id);
      if (tip) tip.hidden = !on;
    }
  }, [open]);
  useDocumentEvent("keydown", (e) => {
    if (e.key !== "Escape" || !open) return;
    const dot = dots().find((b) => b.getAttribute("aria-controls") === open);
    setOpen(null); dot?.focus();
  });
  useDocumentEvent("click", (e) => { if (open && !(e.target as Element).closest(".hotspot")) setOpen(null); });
}
