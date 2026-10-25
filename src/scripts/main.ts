import { initSearch } from "./search";
// Progressive enhancement only — every page is complete without JavaScript.
// Theme toggle, products menu, ring carousel, copy feedback and the intro sweep are React islands
// (src/components/islands/); what is left here is phase 2 of that migration.
const reduceMotion = matchMedia("(prefers-reduced-motion: reduce)");

/* Docs nav: edge fades while the strip overflows, current tab scrolled into view, and a
   sliding underline that follows hover/focus and returns to the current page. */
function initDocsNav() {
  const nav = document.querySelector<HTMLElement>("[data-dnav]");
  const scroller = nav?.querySelector<HTMLElement>("[data-dnav-scroller]");
  if (!nav || !scroller) return;
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
    const target = left - (scroller.clientWidth - current.offsetWidth) / 2;
    scroller.scrollLeft = Math.max(0, target);
  }
  moveTo(current);
  nav.classList.add("is-ready");
  fades();
  scroller.addEventListener("scroll", fades, { passive: true });
  addEventListener("resize", () => { fades(); moveTo(current); }, { passive: true });
  for (const t of tabs) {
    t.addEventListener("pointerenter", () => moveTo(t));
    t.addEventListener("focus", () => moveTo(t));
  }
  scroller.addEventListener("pointerleave", () => moveTo(current));
  scroller.addEventListener("focusout", (e) => { if (!scroller.contains(e.relatedTarget as Node)) moveTo(current); });
  document.fonts?.ready.then(() => { moveTo(current); fades(); });
}

/* Docs TOC: highlight the section being read. */
function initToc() {
  const links = [...document.querySelectorAll<HTMLAnchorElement>("[data-toc-link]")];
  if (!links.length) return;
  const byId = new Map(links.map((a) => [decodeURIComponent(a.hash.slice(1)), a]));
  const heads = [...byId.keys()].map((id) => document.getElementById(id)).filter(Boolean) as HTMLElement[];
  const set = (id: string) => {
    for (const a of links) {
      if (byId.get(id) === a) a.setAttribute("aria-current", "true");
      else a.removeAttribute("aria-current");
    }
  };
  const onScroll = () => {
    let active = heads[0]?.id;
    for (const h of heads) if (h.getBoundingClientRect().top < 200) active = h.id;
    if (active) set(active);
  };
  addEventListener("scroll", onScroll, { passive: true });
  onScroll();
}

initSearch();
initDocsNav();
initToc();
