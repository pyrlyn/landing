// rtok / ketch product visuals (browser only). Each init takes its server-rendered root and returns cleanup.
// Illustrative only. Reduced motion: final states, no timers; the scroll-scrubbed ones render at p = 1.
import { REDUCE_MOTION } from "./media";
import { onScrub, progress } from "./scrub";

const rm = () => matchMedia(REDUCE_MOTION);

/* rtok bitset: blocks settle into their target state; Δtok counts removed blocks. */
export function initBitset(root: HTMLElement) {
  if (rm().matches) return;
  const timers: number[] = [];
  const bits = [...root.querySelectorAll<HTMLElement>(".bit")];
  const out = root.querySelector<HTMLElement>("[data-bitset-count]");
  const run = () => {
    bits.forEach((b) => (b.dataset.state = "K"));
    if (out) out.textContent = "−0";
    let removed = 0;
    const order = bits.map((b, k) => ({ b, k, r: Math.random() })).sort((a, b) => a.r - b.r);
    order.forEach(({ b }, n) => {
      timers.push(window.setTimeout(() => {
        b.dataset.state = b.dataset.target!;
        if (b.dataset.target !== "K") { removed++; if (out) out.textContent = `−${removed}`; }
      }, 500 + n * 140));
    });
  };
  const io = new IntersectionObserver(([en]) => { if (en.isIntersecting) { io.disconnect(); run(); } }, { threshold: 0.4 });
  io.observe(root);
  return () => { io.disconnect(); timers.forEach(clearTimeout); };
}
/* rtok: MCP code graph draws in once visible. */
export function initGraph(g: HTMLElement) {
  if (rm().matches) return;
  g.classList.add("is-armed");
  const io = new IntersectionObserver(([en]) => { if (en.isIntersecting) { io.disconnect(); requestAnimationFrame(() => g.classList.add("is-drawn")); } }, { threshold: 0.4 });
  io.observe(g);
  return () => io.disconnect();
}
/* rtok: token particles scroll-scrubbed into a narrow context band, reversible. Illustrative only. */
export function initParticles(root: HTMLElement) {
  const reduceMotion = rm();
  const canvas = root.querySelector<HTMLCanvasElement>("[data-p-canvas]");
  const ctx = canvas?.getContext("2d");
  if (!canvas || !ctx) return;
  const out = root.querySelector<HTMLElement>("[data-p-count]");
  const css = getComputedStyle(document.documentElement);
  const cyan = css.getPropertyValue("--accent").trim() || "#5CE1FF";
  const coral = css.getPropertyValue("--accent-2").trim() || "#FF6B4A";
  const N = 240;
  let seed = 7; const rnd = () => ((seed = (seed * 16807) % 2147483647) / 2147483647);
  type P = { kind: "K" | "T" | "C"; sx: number; sy: number; w: number; bx: number; by: number; ph: number };
  const ps: P[] = [];
  for (let i = 0; i < N; i++) {
    const r = rnd();
    ps.push({ kind: r < 0.55 ? "K" : r < 0.72 ? "T" : "C", sx: rnd(), sy: rnd(), w: 6 + rnd() * 12, bx: 0, by: 0, ph: rnd() * Math.PI * 2 });
  }
  const cut = ps.filter((p) => p.kind === "C").length;
  let W = 0, H = 0, last = -1, rows = 1;
  const layout = () => {
    const dpr = Math.min(2, devicePixelRatio || 1);
    W = canvas.clientWidth; H = canvas.clientHeight;
    canvas.width = W * dpr; canvas.height = H * dpr; ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
    // band targets: kept + thinned pack into rows inside a narrow band; cut tokens go to the archive corner
    const bandW = W * 0.62, x0 = W * 0.08, rowH = 9;
    let x = x0, row = 0;
    const archive = { x: W * 0.8, y: H * 0.72 };
    let ai = 0;
    for (const p of ps) {
      if (p.kind === "C") { p.bx = archive.x + (ai % 12) * 9; p.by = archive.y + Math.floor(ai / 12) * 8 - 20; ai++; continue; }
      const w = p.kind === "T" ? p.w * 0.35 : p.w * 0.8;
      if (x + w > x0 + bandW) { x = x0; row++; }
      p.bx = x; p.by = H / 2 - 18 + row * rowH; x += w + 3;
    }
    rows = row + 1;
    last = -1;
  };
  const ease = (t: number) => t < 0.5 ? 4 * t * t * t : 1 - Math.pow(-2 * t + 2, 3) / 2;
  const draw = (p0: number) => {
    const t = ease(p0);
    ctx.clearRect(0, 0, W, H);
    // the context band
    ctx.fillStyle = `rgba(92,225,255,${0.04 + t * 0.06})`;
    const bh = rows * 9 + 12;
    ctx.fillRect(W * 0.06, H / 2 - 24, W * 0.66, bh + (H * 0.5 - bh) * (1 - t));
    for (const p of ps) {
      const x = p.sx * (W - 20) + 10, y = p.sy * (H - 20) + 10;
      const px = x + (p.bx - x) * t, py = y + (p.by - y) * t;
      const w = p.kind === "T" ? p.w * (1 - 0.65 * t) : p.kind === "K" ? p.w * (1 - 0.2 * t) : p.w * (1 - 0.5 * t);
      ctx.globalAlpha = p.kind === "T" ? 0.75 - 0.25 * t : p.kind === "C" ? 0.9 - 0.35 * t : 0.95;
      if (p.kind === "C") { ctx.strokeStyle = coral; ctx.lineWidth = 1.2; ctx.strokeRect(px, py, w, 5); }
      else { ctx.fillStyle = p.kind === "K" ? cyan : "#7aa3b8"; ctx.fillRect(px, py, w, p.kind === "K" ? 5 : 4); }
    }
    ctx.globalAlpha = 1;
    if (out) out.textContent = `−${Math.round(cut * t)}`;
  };
  const tick = () => {
    const p = reduceMotion.matches ? 1 : progress(root, 0.95, 0.35);
    if (Math.abs(p - last) < 0.002) return;
    last = p; draw(p);
  };
  const ro = new ResizeObserver(() => { layout(); tick(); });
  ro.observe(canvas);
  const off = onScrub(tick);
  const onMq = () => { last = -1; tick(); };
  reduceMotion.addEventListener("change", onMq);
  return () => { ro.disconnect(); off(); reduceMotion.removeEventListener("change", onMq); };
}
/* ketch: package chip travels the route on offset-path, stroke draws with scroll; rollback runs back. */
export function initRoute(route: HTMLElement) {
  const reduceMotion = rm();
  const fit = route.querySelector<HTMLElement>("[data-route-fit]")!;
  const box = route.querySelector<HTMLElement>("[data-route-box]")!;
  const path = route.querySelector<SVGPathElement>("[data-route-path]")!;
  const tips = [...route.querySelectorAll<HTMLElement>("[data-route-stop]")];
  tips.forEach((t, k) => t.style.setProperty("--k", String(k)));
  // fraction of the route at which each stop is reached (nearest sample on the path)
  const len = path.getTotalLength();
  const stopsAt = tips.map((t) => {
    const x = parseFloat(t.style.left), y = parseFloat(t.style.top);
    let best = 0, bd = Infinity;
    for (let i = 0; i <= 400; i++) {
      const pt = path.getPointAtLength((i / 400) * len);
      const d = (pt.x - x) ** 2 + (pt.y - y) ** 2;
      if (d < bd - 1) { bd = d; best = i / 400; }
    }
    return best;
  });
  // the install stop is revisited at the very end (rollback leg returns there)
  const backStart = stopsAt[3];
  const scale = () => { const s = Math.min(1, fit.clientWidth / 1000); route.style.setProperty("--s", String(s)); };
  const ro = new ResizeObserver(scale); ro.observe(fit); scale();
  const tick = () => {
    const p = reduceMotion.matches ? 1 : progress(route, 0.85, 0.2);
    box.style.setProperty("--dist", `${p * 100}%`);
    route.style.setProperty("--dash", String(1 - p));
    path.style.strokeDashoffset = String(1 - p);
    route.classList.toggle("is-back", p > backStart + 0.02 && p < 1.01);
    tips.forEach((t, k) => t.classList.toggle("is-on", p + 0.005 >= stopsAt[k]));
  };
  const off = onScrub(tick); tick();
  reduceMotion.addEventListener("change", tick);
  return () => { ro.disconnect(); off(); reduceMotion.removeEventListener("change", tick); };
}