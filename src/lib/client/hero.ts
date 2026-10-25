// Page-wide motion helpers (browser only); each returns its cleanup.
import { FINE_POINTER, REDUCE_MOTION } from "./media";

const reduce = () => matchMedia(REDUCE_MOTION);
const fine = () => matchMedia(FINE_POINTER);

/** Pure: one lerp step of the stage tilt toward the pointer; `settling` is false once close enough. */
export function lerpTilt(cx: number, cy: number, tx: number, ty: number, k = 0.08) {
  const nx = cx + (tx - cx) * k, ny = cy + (ty - cy) * k;
  return { cx: nx, cy: ny, settling: Math.abs(tx - nx) + Math.abs(ty - ny) > 0.0005 };
}

/** Hero stage: tilt + parallax layers ([data-depth]), lerped, rAF only while settling. Fine pointer + motion only. */
export function initStage(stage: HTMLElement) {
  const reduceMotion = reduce(), finePointer = fine();
  const motionOK = () => !reduceMotion.matches && finePointer.matches;
  const tilt = stage.querySelector<HTMLElement>(".stage__tilt");
  const layers = [...stage.querySelectorAll<HTMLElement>("[data-depth]")];
  let tx = 0, ty = 0, cx = 0, cy = 0, frame = 0, visible = true;
  const tick = () => {
    const r = lerpTilt(cx, cy, tx, ty); cx = r.cx; cy = r.cy;
    tilt?.style.setProperty("--ry", `${cx * 8}deg`);
    tilt?.style.setProperty("--rx", `${-cy * 6}deg`);
    for (const l of layers) {
      const d = Number(l.dataset.depth) || 0;
      l.style.translate = `${-cx * d}px ${-cy * d}px`;
    }
    frame = r.settling ? requestAnimationFrame(tick) : 0;
  };
  const onMove = (e: PointerEvent) => {
    if (!motionOK() || !visible) return;
    tx = e.clientX / innerWidth - 0.5;
    ty = e.clientY / innerHeight - 0.5;
    if (!frame) frame = requestAnimationFrame(tick);
  };
  const reset = () => { tx = ty = 0; if (!frame) frame = requestAnimationFrame(tick); };
  const io = new IntersectionObserver(([en]) => { visible = en.isIntersecting; if (!visible) reset(); });
  io.observe(stage);
  const onMq = () => {
    if (!reduceMotion.matches) return;
    cancelAnimationFrame(frame); frame = 0; tx = ty = cx = cy = 0;
    tilt?.style.removeProperty("--rx"); tilt?.style.removeProperty("--ry");
    layers.forEach((l) => (l.style.translate = ""));
  };
  addEventListener("pointermove", onMove, { passive: true });
  document.addEventListener("pointerleave", reset);
  reduceMotion.addEventListener("change", onMq);
  return () => {
    io.disconnect(); cancelAnimationFrame(frame);
    removeEventListener("pointermove", onMove); document.removeEventListener("pointerleave", reset);
    reduceMotion.removeEventListener("change", onMq);
  };
}

/** Cursor-follow shine on `.shine` cards (fine pointers only): sets --mx/--my, toggles html.has-shine. */
export function initShine() {
  const finePointer = fine();
  const sync = () => document.documentElement.classList.toggle("has-shine", finePointer.matches);
  const onMove = (e: PointerEvent) => {
    if (!finePointer.matches) return;
    const el = (e.target as Element | null)?.closest<HTMLElement>(".shine");
    if (!el) return;
    const r = el.getBoundingClientRect();
    el.style.setProperty("--mx", `${e.clientX - r.left}px`);
    el.style.setProperty("--my", `${e.clientY - r.top}px`);
  };
  sync();
  finePointer.addEventListener("change", sync);
  document.addEventListener("pointermove", onMove, { passive: true });
  return () => { finePointer.removeEventListener("change", sync); document.removeEventListener("pointermove", onMove); };
}

/** Decorative loops (background drift, hero chips) pause (html.is-still) while the hero stage is off screen.
 *  Docs pages have no stage and are rendered with .is-still already. */
export function initStill() {
  const root = document.documentElement;
  const hero = document.querySelector("[data-stage]");
  if (!hero || root.classList.contains("is-still")) return;
  const io = new IntersectionObserver(([en]) => root.classList.toggle("is-still", !en.isIntersecting));
  io.observe(hero);
  return () => io.disconnect();
}

export const REVEAL_SELECTOR = "main .section__head, main .grid > li, main .grid > article, main .featured, main .showcase, main .prose, main .install-card, main .usage > .card, main .links > li, main .license, main .faq__item, main .steps > li, main .cta";

/** Scroll reveals: blocks below the fold rise in once as they enter. Elements already on screen at load
 *  are never hidden, so nothing flashes; under reduced motion nothing is touched. Not on docs pages. */
export function initScrollReveal() {
  const reduceMotion = reduce();
  if (reduceMotion.matches || !("IntersectionObserver" in window) || document.querySelector(".docs__main")) return;
  const vh = innerHeight;
  const els = [...document.querySelectorAll<HTMLElement>(REVEAL_SELECTOR)].filter((el) => !el.closest(".hero") && el.getBoundingClientRect().top > vh * 0.92);
  if (!els.length) return;
  const timers: number[] = [];
  const io = new IntersectionObserver((entries) => {
    for (const e of entries) if (e.isIntersecting) {
      const el = e.target as HTMLElement;
      el.classList.add("is-in");
      io.unobserve(el);
      // Hand the element back to its own hover transforms (e.g. .card--lift) once it has landed.
      timers.push(window.setTimeout(() => { el.classList.remove("rv", "is-in"); el.style.removeProperty("--rv-i"); }, 1100));
    }
  }, { rootMargin: "0px 0px -8% 0px" });
  for (const el of els) {
    const sib = el.parentElement ? [...el.parentElement.children].indexOf(el) : 0;
    el.style.setProperty("--rv-i", String(Math.min(sib, 5)));
    el.classList.add("rv");
    io.observe(el);
  }
  const onMq = () => { if (reduceMotion.matches) els.forEach((el) => el.classList.add("is-in")); };
  reduceMotion.addEventListener("change", onMq);
  return () => { io.disconnect(); timers.forEach(clearTimeout); reduceMotion.removeEventListener("change", onMq); };
}

/** Step rails (.gs__step, .rail__step): reveal once in view — only when motion is allowed. */
export function initStepReveal() {
  const reduceMotion = reduce();
  const steps = [...document.querySelectorAll<HTMLElement>(".gs__step, .rail__step")];
  if (!steps.length || reduceMotion.matches || !("IntersectionObserver" in window)) return;
  document.documentElement.classList.add("js-reveal");
  const io = new IntersectionObserver((entries) => {
    for (const e of entries) if (e.isIntersecting) { e.target.classList.add("is-in"); io.unobserve(e.target); }
  }, { rootMargin: "0px 0px -10% 0px" });
  steps.forEach((s) => io.observe(s));
  const onMq = () => { if (reduceMotion.matches) steps.forEach((s) => s.classList.add("is-in")); };
  reduceMotion.addEventListener("change", onMq);
  return () => { io.disconnect(); reduceMotion.removeEventListener("change", onMq); };
}
