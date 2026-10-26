// Load: a one-off full-screen WebGL light wave (fixed canvas on <body>). Renders nothing; the GL code is
// fetched as its own chunk after first paint, only when motion is allowed and the hero is on screen.
// The sweep is dropped (canvas removed) on hero exit, hashchange, back/forward and tab hide — see
// src/lib/client/intro-gl.ts (landing#54) — and also when the island unmounts.
import { useEffect } from "react";
import { prefersReducedMotion } from "../../../lib/client/media";

export default function IntroRing() {
  useEffect(() => {
    const root = document.documentElement;
    const hero = document.querySelector<HTMLElement>(".hero");
    if (!hero || prefersReducedMotion() || !root.classList.contains("intro") || hero.getBoundingClientRect().bottom < 0) return;
    // Drop the entrance class once it has played, so later style changes never re-trigger it.
    const t = setTimeout(() => root.classList.remove("intro"), 2200);
    const cs = getComputedStyle(root);
    const a = cs.getPropertyValue("--accent").trim(), b = cs.getPropertyValue("--accent-2").trim();
    if (!/^#[0-9a-f]{6}$/i.test(a) || !/^#[0-9a-f]{6}$/i.test(b)) return () => clearTimeout(t);
    let stop: (() => void) | undefined, cancelled = false;
    const raf = requestAnimationFrame(() => {
      import("../../../lib/client/intro-gl").then((m) => { if (!cancelled) stop = m.playIntro(hero, a, b); }).catch(() => {});
    });
    return () => { cancelled = true; cancelAnimationFrame(raf); clearTimeout(t); stop?.(); };
  }, []);
  return null;
}
