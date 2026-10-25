// Pure ring-carousel maths, shared by the RingCarousel island and tests/ring.test.mjs.
/** Wrap any integer into 0..n-1. */
export const wrap = (k: number, n: number) => ((k % n) + n) % n;
/** Signed number of steps from `from` to `to` going the shortest way round. */
export function shortestDelta(from: number, to: number, n: number) {
  const delta = wrap(to - from, n);
  return delta > n / 2 ? delta - n : delta;
}
export interface RingState { index: number; rot: number }
/** Turn to slide `k` (any integer); `rot` accumulates so the ring never spins the long way. */
export function turnTo(s: RingState, k: number, n: number): RingState {
  const step = 360 / n;
  return { index: wrap(k, n), rot: s.rot + shortestDelta(s.index, wrap(k, n), n) * step };
}
/** Snap a free drag angle to the nearest slide. */
export function snap(rot: number, n: number): RingState {
  const step = 360 / n;
  const k = Math.round(rot / step);
  return { index: wrap(k, n), rot: k * step };
}
/** Counter text: "ketch · 3 / 12". */
export const statusText = (titles: readonly string[], index: number) => `${titles[index] ?? ""} · ${index + 1} / ${titles.length}`;
