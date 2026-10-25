// Docs page helpers (browser only, except the pure activeHeading). Each init returns its cleanup.

/** Docs nav: edge fades while the strip overflows, current tab scrolled into view, and a sliding underline
 *  that follows hover/focus and returns to the current page. Also closes the compact "Section" disclosure
 *  on Esc / outside click / tabbing out, like the header products menu. */
export function initDocsNav(nav: HTMLElement) {
  const scroller = nav.querySelector<HTMLElement>("[data-dnav-scroller]");
  if (!scroller) return;
  const indicator = scroller.querySelector<HTMLElement>(".dnav__indicator")!;
  const tabs = [...scroller.querySelectorAll<HTMLAnchorElement>(".dnav__tab")];
  const current = tabs.find((t) => t.getAttribute("aria-current") === "page");
  const fades = () => {
    const max = scroller.scrollWidth - scroller.clientWidth;
    scroller.toggleAttribute("data-fade-l", scroller.scrollLeft > 4);
    scroller.toggleAttribute("data-fade-r", scroller.scrollLeft < max - 4);
  };
  const moveTo = (el: HTMLElement | undefined) => {
    if (!el) { indicator.style.setProperty("--w", "0"); return; }
    const pad = 12;
    const left = el.getBoundingClientRect().left - scroller.getBoundingClientRect().left + scroller.scrollLeft;
    indicator.style.setProperty("--x", `${left + pad}px`);
    indicator.style.setProperty("--w", `${Math.max(el.offsetWidth - pad * 2, 0)}`);
  };
  if (current) {
    const left = current.getBoundingClientRect().left - scroller.getBoundingClientRect().left;
    scroller.scrollLeft = Math.max(0, left - (scroller.clientWidth - current.offsetWidth) / 2);
  }
  moveTo(current);
  nav.classList.add("is-ready");
  fades();
  const onResize = () => { fades(); moveTo(current); };
  const back = () => moveTo(current);
  const onFocusOut = (e: FocusEvent) => { if (!scroller.contains(e.relatedTarget as Node)) moveTo(current); };
  const tabFns = tabs.map((t) => { const f = () => moveTo(t); t.addEventListener("pointerenter", f); t.addEventListener("focus", f); return f; });
  scroller.addEventListener("scroll", fades, { passive: true });
  addEventListener("resize", onResize, { passive: true });
  scroller.addEventListener("pointerleave", back);
  scroller.addEventListener("focusout", onFocusOut);
  let alive = true;
  document.fonts?.ready.then(() => { if (alive) { moveTo(current); fades(); } }).catch(() => { /* fonts failed to load; keep fallback layout */ });

  const menus = [...nav.querySelectorAll<HTMLDetailsElement>("details[data-menu]")];
  const onDocClick = (e: MouseEvent) => { for (const m of menus) if (m.open && !m.contains(e.target as Node)) m.open = false; };
  const onKey = (e: KeyboardEvent) => {
    if (e.key !== "Escape") return;
    for (const m of menus) if (m.open) { m.open = false; m.querySelector("summary")?.focus(); }
  };
  const menuOut = menus.map((m) => { const f = (e: FocusEvent) => { const to = e.relatedTarget as Node | null; if (m.open && to && !m.contains(to)) m.open = false; }; m.addEventListener("focusout", f); return f; });
  document.addEventListener("click", onDocClick);
  document.addEventListener("keydown", onKey);

  return () => {
    alive = false;
    tabs.forEach((t, k) => { t.removeEventListener("pointerenter", tabFns[k]); t.removeEventListener("focus", tabFns[k]); });
    scroller.removeEventListener("scroll", fades);
    removeEventListener("resize", onResize);
    scroller.removeEventListener("pointerleave", back);
    scroller.removeEventListener("focusout", onFocusOut);
    menus.forEach((m, k) => m.removeEventListener("focusout", menuOut[k]));
    document.removeEventListener("click", onDocClick);
    document.removeEventListener("keydown", onKey);
  };
}

/** Pure: the last heading whose top is above `line` (px from the viewport top); the first one otherwise. */
export function activeHeading(heads: { id: string; top: number }[], line = 200): string | undefined {
  let active = heads[0]?.id;
  for (const h of heads) if (h.top < line) active = h.id;
  return active;
}

/** Docs TOC: aria-current on the link of the section being read. */
export function initToc(root: HTMLElement) {
  const links = [...root.querySelectorAll<HTMLAnchorElement>("[data-toc-link]")];
  if (!links.length) return;
  const byId = new Map(links.map((a) => [decodeURIComponent(a.hash.slice(1)), a]));
  const heads = [...byId.keys()].map((id) => document.getElementById(id)).filter(Boolean) as HTMLElement[];
  const onScroll = () => {
    const id = activeHeading(heads.map((h) => ({ id: h.id, top: h.getBoundingClientRect().top })));
    if (!id) return;
    for (const a of links) {
      if (byId.get(id) === a) a.setAttribute("aria-current", "true");
      else a.removeAttribute("aria-current");
    }
  };
  addEventListener("scroll", onScroll, { passive: true });
  onScroll();
  return () => removeEventListener("scroll", onScroll);
}
