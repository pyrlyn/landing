import { initSearch } from "./search";
// Progressive enhancement only — every page is complete without JavaScript.
// Theme toggle, products menu, ring carousel, copy feedback and the intro sweep are React islands
// (src/components/islands/); what is left here is phase 2 of that migration.
const reduceMotion = matchMedia("(prefers-reduced-motion: reduce)");
const finePointer = matchMedia("(hover: hover) and (pointer: fine)");
const motionOK = () => !reduceMotion.matches && finePointer.matches;

/* Cursor-follow shine on cards (fine pointers only). */
function initShine() {
  const sync = () => document.documentElement.classList.toggle("has-shine", finePointer.matches);
  sync();
  finePointer.addEventListener("change", sync);
  document.addEventListener("pointermove", (e) => {
    if (!finePointer.matches) return;
    const el = (e.target as Element | null)?.closest<HTMLElement>(".shine");
    if (!el) return;
    const r = el.getBoundingClientRect();
    el.style.setProperty("--mx", `${e.clientX - r.left}px`);
    el.style.setProperty("--my", `${e.clientY - r.top}px`);
  }, { passive: true });
}

/* Hero stage: tilt + parallax layers, lerped, rAF only while settling. */
function initStage() {
  const stage = document.querySelector<HTMLElement>("[data-stage]");
  if (!stage) return;
  const tilt = stage.querySelector<HTMLElement>(".stage__tilt");
  const layers = [...stage.querySelectorAll<HTMLElement>("[data-depth]")];
  let tx = 0, ty = 0, cx = 0, cy = 0, frame = 0, visible = true;

  const tick = () => {
    cx += (tx - cx) * 0.08;
    cy += (ty - cy) * 0.08;
    tilt?.style.setProperty("--ry", `${cx * 8}deg`);
    tilt?.style.setProperty("--rx", `${-cy * 6}deg`);
    for (const l of layers) {
      const d = Number(l.dataset.depth) || 0;
      l.style.translate = `${-cx * d}px ${-cy * d}px`;
    }
    frame = Math.abs(tx - cx) + Math.abs(ty - cy) > 0.0005 ? requestAnimationFrame(tick) : 0;
  };
  const onMove = (e: PointerEvent) => {
    if (!motionOK() || !visible) return;
    tx = e.clientX / innerWidth - 0.5;
    ty = e.clientY / innerHeight - 0.5;
    if (!frame) frame = requestAnimationFrame(tick);
  };
  const reset = () => {
    tx = ty = 0;
    if (!frame) frame = requestAnimationFrame(tick);
  };
  new IntersectionObserver(([en]) => { visible = en.isIntersecting; if (!visible) reset(); }).observe(stage);
  addEventListener("pointermove", onMove, { passive: true });
  document.addEventListener("pointerleave", reset);
  reduceMotion.addEventListener("change", () => {
    if (reduceMotion.matches) {
      cancelAnimationFrame(frame); frame = 0; tx = ty = cx = cy = 0;
      tilt?.style.removeProperty("--rx"); tilt?.style.removeProperty("--ry");
      layers.forEach((l) => (l.style.translate = ""));
    }
  });
}

/* Decorative loops (background drift, hero chips) pause while the hero is off screen.
   Docs pages have no hero and are rendered with .is-still already. */
function initStill() {
  const root = document.documentElement;
  const hero = document.querySelector("[data-stage]");
  if (!hero || root.classList.contains("is-still")) return;
  new IntersectionObserver(([en]) => root.classList.toggle("is-still", !en.isIntersecting)).observe(hero);
}

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

/* Step rails: reveal once in view — only when motion is allowed. */
function initReveal() {
  const steps = [...document.querySelectorAll<HTMLElement>(".gs__step, .rail__step")];
  if (!steps.length || reduceMotion.matches || !("IntersectionObserver" in window)) return;
  document.documentElement.classList.add("js-reveal");
  const io = new IntersectionObserver((entries) => {
    for (const e of entries) if (e.isIntersecting) { e.target.classList.add("is-in"); io.unobserve(e.target); }
  }, { rootMargin: "0px 0px -10% 0px" });
  steps.forEach((s) => io.observe(s));
  reduceMotion.addEventListener("change", () => { if (reduceMotion.matches) steps.forEach((s) => s.classList.add("is-in")); });
}

/* Home: Pro/OSS audience toggle. */
function initAudience() {
  const root = document.querySelector<HTMLElement>("[data-audience]");
  if (!root) return;
  const buttons = [...document.querySelectorAll<HTMLButtonElement>("[data-set-audience]")];
  for (const b of buttons) {
    b.addEventListener("click", () => {
      const apply = () => {
        root.dataset.audience = b.dataset.setAudience!;
        buttons.forEach((x) => x.setAttribute("aria-pressed", String(x === b)));
      };
      const vt = (document as Document & { startViewTransition?: (cb: () => void) => void }).startViewTransition;
      if (vt && !reduceMotion.matches) vt.call(document, apply);
      else apply();
    });
  }
}

/* Home hero hotspots: one open at a time; Esc / outside click closes and returns focus. */
function initHotspots() {
  const dots = [...document.querySelectorAll<HTMLButtonElement>("[data-hotspots] .hotspot__dot")];
  if (!dots.length) return;
  const tipOf = (b: HTMLButtonElement) => document.getElementById(b.getAttribute("aria-controls")!)!;
  const close = (b: HTMLButtonElement) => { b.setAttribute("aria-expanded", "false"); tipOf(b).hidden = true; };
  for (const b of dots) {
    b.addEventListener("click", () => {
      const open = b.getAttribute("aria-expanded") === "true";
      dots.forEach(close);
      if (!open) { b.setAttribute("aria-expanded", "true"); tipOf(b).hidden = false; }
    });
  }
  document.addEventListener("keydown", (e) => {
    if (e.key !== "Escape") return;
    const openDot = dots.find((b) => b.getAttribute("aria-expanded") === "true");
    if (openDot) { close(openDot); openDot.focus(); }
  });
  document.addEventListener("click", (e) => {
    if (!(e.target as Element).closest(".hotspot")) dots.forEach(close);
  });
}

/* Scroll reveals: blocks below the fold rise in once as they enter. Elements already on screen at
   load are never hidden, so nothing flashes; under reduced motion nothing is touched. */
function initScrollReveal() {
  if (reduceMotion.matches || !("IntersectionObserver" in window) || document.querySelector(".docs__main")) return;
  const sel = "main .section__head, main .grid > li, main .grid > article, main .featured, main .showcase, main .prose, main .install-card, main .usage > .card, main .links > li, main .license, main .faq__item, main .steps > li, main .cta";
  const vh = innerHeight;
  const els = [...document.querySelectorAll<HTMLElement>(sel)].filter((el) => !el.closest(".hero") && el.getBoundingClientRect().top > vh * 0.92);
  if (!els.length) return;
  const io = new IntersectionObserver((entries) => {
    for (const e of entries) if (e.isIntersecting) {
      const el = e.target as HTMLElement;
      el.classList.add("is-in");
      io.unobserve(el);
      // Hand the element back to its own hover transforms (e.g. .card--lift) once it has landed.
      setTimeout(() => { el.classList.remove("rv", "is-in"); el.style.removeProperty("--rv-i"); }, 1100);
    }
  }, { rootMargin: "0px 0px -8% 0px" });
  for (const el of els) {
    const sib = el.parentElement ? [...el.parentElement.children].indexOf(el) : 0;
    el.style.setProperty("--rv-i", String(Math.min(sib, 5)));
    el.classList.add("rv");
    io.observe(el);
  }
  reduceMotion.addEventListener("change", () => { if (reduceMotion.matches) els.forEach((el) => el.classList.add("is-in")); });
}

initSearch();
initScrollReveal();
initHotspots();
initShine();
initStage();
initStill();
initAudience();
initDocsNav();
initToc();
initReveal();
