// Home: 3D ring carousel. The cards are static Astro markup passed in as children (real links, no JS
// needed: without JS / <768px it is a plain grid). The island owns one piece of state, { index, rot }:
// the highlighted card, the counter text and the track angle are all derived from it, so the counter
// can never disagree with the card (it used to keep showing the first title while auto-rotate moved on).
// Buttons, ←/→, drag, focus-follow; auto-rotate unless reduced motion, never after the user takes over.
import { useCallback, useEffect, useRef, useState, type CSSProperties, type ReactNode } from "react";
import { snap, statusText, turnTo, type RingState } from "../../../lib/client/ring";
import { REDUCE_MOTION } from "../../../lib/client/media";
import { useHotspots } from "../hooks/useHotspots";

interface Props { titles: string[]; children?: ReactNode }
const AUTO_MS = 4200;

export default function RingCarousel({ titles, children }: Props) {
  const n = titles.length;
  const rootRef = useRef<HTMLDivElement>(null);
  const stageRef = useRef<HTMLDivElement>(null);
  const [ready, setReady] = useState(false);
  const [state, setState] = useState<RingState>({ index: 0, rot: 0 });
  // The counter is announced only once the user drives the ring; auto-rotate updates it silently.
  const [userTouched, setUserTouched] = useState(false);
  const stateRef = useRef(state);
  stateRef.current = state;

  const items = useCallback(() => [...(stageRef.current?.querySelectorAll<HTMLElement>("[data-ring-item]") ?? [])], []);
  const track = useCallback(() => stageRef.current?.querySelector<HTMLElement>("[data-ring-track]") ?? null, []);
  const go = useCallback((k: number) => setState((s) => turnTo(s, k, n)), [n]);
  const takeOver = useCallback(() => setUserTouched(true), []);

  useEffect(() => setReady(true), []);
  // Card hotspots (one open at a time, Esc / outside click closes); an open one pauses auto-rotate.
  useHotspots(rootRef);

  // Derived DOM: angle + highlighted card follow the single state.
  useEffect(() => {
    track()?.style.setProperty("--rot", `${state.rot}deg`);
    items().forEach((el, k) => el.classList.toggle("is-active", k === state.index));
  }, [state, items, track]);

  // Keyboard, focus-follow and drag on the static card markup.
  useEffect(() => {
    const root = rootRef.current, stage = stageRef.current;
    if (!root || !stage) return;
    const list = items();
    const focusCard = (k: number) => list[k]?.querySelector<HTMLElement>(".ring__card")?.focus({ preventScroll: true });
    const onKey = (e: KeyboardEvent) => {
      if ((e.target as Element).closest(".hotspot")) return;
      if (e.key !== "ArrowRight" && e.key !== "ArrowLeft") return;
      e.preventDefault(); takeOver();
      const next = turnTo(stateRef.current, stateRef.current.index + (e.key === "ArrowRight" ? 1 : -1), n);
      stateRef.current = next; setState(next); focusCard(next.index);
    };
    const onFocus = list.map((el, k) => { const f = () => { takeOver(); if (k !== stateRef.current.index) go(k); }; el.addEventListener("focusin", f); return f; });

    let startX = 0, startRot = 0, dragRot = 0, dragging = false, moved = false;
    const down = (e: PointerEvent) => {
      if (matchMedia("(max-width: 767px)").matches || (e.target as Element).closest(".hotspot")) return;
      dragging = true; moved = false; startX = e.clientX; startRot = dragRot = stateRef.current.rot;
    };
    const move = (e: PointerEvent) => {
      if (!dragging) return;
      const dx = e.clientX - startX;
      if (!moved && Math.abs(dx) > 6) { moved = true; takeOver(); stage.classList.add("is-dragging"); }
      if (moved) { dragRot = startRot - dx * 0.35; track()?.style.setProperty("--rot", `${dragRot}deg`); }
    };
    const end = () => {
      if (!dragging) return;
      dragging = false; stage.classList.remove("is-dragging");
      if (!moved) return;
      setState(snap(dragRot, n));
      // The click that ends a drag (if any) fires right after pointerup; forget the drag after it,
      // so a drag released outside the stage cannot swallow the next click.
      setTimeout(() => { moved = false; }, 0);
    };
    const click = (e: MouseEvent) => { if (moved) { e.preventDefault(); e.stopPropagation(); moved = false; } };
    root.addEventListener("keydown", onKey);
    stage.addEventListener("pointerdown", down);
    stage.addEventListener("click", click, true);
    addEventListener("pointermove", move);
    // A touch that turns into a vertical scroll is cancelled: snap instead of staying half-turned.
    addEventListener("pointerup", end);
    addEventListener("pointercancel", end);
    return () => {
      root.removeEventListener("keydown", onKey);
      list.forEach((el, k) => el.removeEventListener("focusin", onFocus[k]));
      stage.removeEventListener("pointerdown", down);
      stage.removeEventListener("click", click, true);
      removeEventListener("pointermove", move);
      removeEventListener("pointerup", end);
      removeEventListener("pointercancel", end);
    };
  }, [n, go, items, takeOver, track]);

  // Auto-rotate: motion allowed, user has not taken over; paused on hover/focus/open hotspot/hidden tab.
  useEffect(() => {
    if (userTouched) return;
    const mq = matchMedia(REDUCE_MOTION);
    let timer = 0;
    const start = () => {
      clearInterval(timer);
      if (mq.matches) return;
      timer = window.setInterval(() => {
        const root = rootRef.current;
        if (!root || document.hidden || root.matches(":hover") || root.contains(document.activeElement)) return;
        if (root.querySelector('.hotspot__dot[aria-expanded="true"]')) return;
        setState((s) => turnTo(s, s.index + 1, n));
      }, AUTO_MS);
    };
    start();
    mq.addEventListener("change", start);
    return () => { clearInterval(timer); mq.removeEventListener("change", start); };
  }, [userTouched, n]);

  return (
    <div ref={rootRef} className={ready ? "ring is-ready" : "ring"} data-ring style={{ "--n": n } as CSSProperties}
      role="region" aria-roledescription="carousel" aria-label="Products">
      <div className="ring__stage" data-ring-stage ref={stageRef}>{children}</div>
      <div className="ring__controls" data-ring-controls hidden={!ready}>
        <button className="ring__btn" type="button" data-ring-prev aria-label="Previous product" onClick={() => { takeOver(); go(state.index - 1); }}>
          <svg viewBox="0 0 16 16" width="16" height="16" aria-hidden="true"><path d="M10 3 5 8l5 5" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" /></svg>
        </button>
        <p className="ring__status" aria-live={userTouched ? "polite" : "off"} data-ring-status>{statusText(titles, state.index)}</p>
        <button className="ring__btn" type="button" data-ring-next aria-label="Next product" onClick={() => { takeOver(); go(state.index + 1); }}>
          <svg viewBox="0 0 16 16" width="16" height="16" aria-hidden="true"><path d="m6 3 5 5-5 5" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" /></svg>
        </button>
      </div>
    </div>
  );
}
