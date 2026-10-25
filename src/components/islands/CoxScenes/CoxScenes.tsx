// cox scenes: tabs + ←/→ (Home/End on a tab), click a back panel to bring it forward. The active panel's
// terminal plays once the widget has been 35% in view; switching stops the previous one. The terminals are
// manual (no Terminal island of their own) — this island drives them through lib/client/terminal.
// Server render = first tab selected, all panels present; without JS they simply stack.
import { useCallback, useEffect, useRef, useState, type KeyboardEvent, type ReactNode } from "react";
import { createTerminal, type TerminalController } from "../../../lib/client/terminal";

export default function CoxScenes({ children }: { children?: ReactNode }) {
  const ref = useRef<HTMLDivElement>(null);
  const [active, setActive] = useState(0);
  const [seen, setSeen] = useState(false);
  const terms = useRef<(TerminalController | null)[]>([]);
  const prev = useRef(0);
  const count = useRef(0);

  const root = () => ref.current?.querySelector<HTMLElement>("[data-scenes]") ?? null;
  const tabs = () => [...(root()?.querySelectorAll<HTMLButtonElement>("[role=tab]") ?? [])];
  const panels = () => [...(root()?.querySelectorAll<HTMLElement>("[data-scene]") ?? [])];
  const go = useCallback((k: number, focus = false) => {
    const n = count.current || 1;
    const next = (k + n) % n;
    setActive(next);
    if (focus) tabs()[next]?.focus();
  }, []);

  // Wire up the static markup once.
  useEffect(() => {
    const r = root();
    if (!r) return;
    const ps = panels();
    count.current = ps.length;
    terms.current = ps.map((p) => { const t = p.querySelector<HTMLElement>("[data-term]"); return t ? createTerminal(t) : null; });
    r.classList.add("is-ready");
    const onTab = tabs().map((t, i) => { const f = () => go(i); t.addEventListener("click", f); return f; });
    const onPanel = ps.map((p, i) => { const f = () => go(i); p.addEventListener("click", f); return f; });
    const io = new IntersectionObserver(([en]) => { if (en.isIntersecting) { io.disconnect(); setSeen(true); } }, { threshold: 0.35 });
    io.observe(r);
    return () => {
      io.disconnect();
      tabs().forEach((t, i) => t.removeEventListener("click", onTab[i]));
      ps.forEach((p, i) => p.removeEventListener("click", onPanel[i]));
      terms.current.forEach((t) => t?.destroy());
    };
  }, [go]);

  // Derived DOM from { active, seen }.
  useEffect(() => {
    const r = root();
    if (!r) return;
    r.querySelector<HTMLElement>(".scenes__stage")?.style.setProperty("--active", String(active));
    tabs().forEach((t, i) => { const on = i === active; t.setAttribute("aria-selected", String(on)); t.tabIndex = on ? 0 : -1; });
    panels().forEach((p, i) => {
      const on = i === active;
      p.classList.toggle("is-active", on);
      p.toggleAttribute("inert", !on);
      p.style.setProperty("--off", String(Math.abs(i - active)));
    });
    if (seen) {
      if (prev.current !== active) terms.current[prev.current]?.stop();
      terms.current[active]?.play();
    }
    prev.current = active;
  }, [active, seen]);

  const onKey = (e: KeyboardEvent<HTMLDivElement>) => {
    const onTab = (document.activeElement as HTMLElement | null)?.getAttribute("role") === "tab" && !!root()?.contains(document.activeElement);
    if (e.key === "ArrowRight" || e.key === "ArrowLeft") { e.preventDefault(); go(active + (e.key === "ArrowRight" ? 1 : -1), onTab); }
    if ((e.key === "Home" || e.key === "End") && (e.target as Element).getAttribute("role") === "tab") {
      e.preventDefault(); go(e.key === "Home" ? 0 : count.current - 1, true);
    }
  };
  return <div style={{ display: "contents" }} ref={ref} onKeyDown={onKey}>{children}</div>;
}
