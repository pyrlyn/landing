// One rAF-throttled scroll/resize loop shared by the scroll-scrubbed visuals (no scroll-jacking).
const subs = new Set            ();
let frame = 0, bound = false;
const run = () => { frame = 0; subs.forEach((f) => f()); };
const queue = () => { if (!frame) frame = requestAnimationFrame(run); };

/** Subscribe `fn` to the scroll loop; returns the unsubscribe. */
export function onScrub(fn            ) {
  if (!bound) { addEventListener("scroll", queue, { passive: true }); addEventListener("resize", queue, { passive: true }); bound = true; }
  subs.add(fn);
  return () => { subs.delete(fn); };
}

/** Pure: 0 → 1 as a box of height `h` at `top` travels from `start`·vh (entering low) to `end`·vh − h. */
export function scrubProgress(top        , h        , vh        , start = 0.9, end = 0.25) {
  const a = vh * start, b = vh * end - h;
  return Math.min(1, Math.max(0, (a - top) / (a - b)));
}
export const progress = (el         , start         , end         ) => {
  const r = el.getBoundingClientRect();
  return scrubProgress(r.top, r.height, innerHeight, start, end);
};
