// Client behaviour for the static pages: a vanilla port of the Astro site's Preact islands.
// Every page works without this file; it only enhances server-rendered markup.
// The Kotlin build bundles it with ./lib/*.js (imports are inlined) into one module script.
import { FINE_POINTER, REDUCE_MOTION, prefersReducedMotion } from "./lib/media.js";
import { initStage, initShine, initStill, initScrollReveal, initStepReveal } from "./lib/hero.js";
import { createTerminal } from "./lib/terminal.js";
import { initBitset, initGraph, initParticles, initRoute } from "./lib/visuals.js";
import { initDocsNav, initToc } from "./lib/docs.js";
import { snap, statusText, turnTo } from "./lib/ring.js";
import { pickProvider } from "./lib/search-provider.js";
import { highlightParts, moveActive } from "./lib/search-state.js";

const $ = (s, r = document) => r.querySelector(s);
const $$ = (s, r = document) => [...r.querySelectorAll(s)];
const me = document.currentScript || $("script[data-intro]");
const idle = (fn) => (window.requestIdleCallback ? requestIdleCallback(fn, { timeout: 2000 }) : setTimeout(fn, 1));
const onVisible = (el, fn, threshold = 0) => {
  const io = new IntersectionObserver((es) => { if (es.some((e) => e.isIntersecting)) { io.disconnect(); fn(); } }, { threshold });
  io.observe(el);
};

// ---- Products menu and docs section menu: <details>, closed on Esc, outside click, focus leaving.
for (const d of $$("details[data-menu]")) {
  document.addEventListener("click", (e) => { if (d.open && !d.contains(e.target)) d.open = false; });
  document.addEventListener("keydown", (e) => { if (e.key === "Escape" && d.open) { d.open = false; d.querySelector("summary")?.focus(); } });
  d.addEventListener("focusout", (e) => { const to = e.relatedTarget; if (d.open && to && !d.contains(to)) d.open = false; });
}

// ---- Theme toggle (first paint is set by the inline head script).
{
  const KEY = "pyrlyn-theme";
  const btn = $("[data-theme-toggle]");
  const cur = () => (document.documentElement.getAttribute("data-mode") === "light" ? "light" : "dark");
  const apply = (m) => { const r = document.documentElement; r.setAttribute("data-mode", m); r.style.colorScheme = m; window.__applyThemeMeta?.(m); label(); };
  const label = () => btn?.setAttribute("aria-label", cur() === "light" ? "Switch to dark theme" : "Switch to light theme");
  label();
  btn?.addEventListener("click", () => { const n = cur() === "light" ? "dark" : "light"; try { localStorage.setItem(KEY, n); } catch {} apply(n); });
  matchMedia("(prefers-color-scheme: light)").addEventListener("change", (e) => {
    let saved = null; try { saved = localStorage.getItem(KEY); } catch {}
    if (!saved) apply(e.matches ? "light" : "dark");
  });
}

// ---- Auto-collapsing product/docs header with the peek strip.
{
  const nav = $("[data-nav-auto]");
  if (nav) {
    const peek = $(".nav__peek .nav__peek-btn", nav);
    const unpin = $(".nav__unpin", nav);
    let pinned = false;
    const set = (next, keyboard) => {
      const hadFocus = nav.contains(document.activeElement);
      nav.toggleAttribute("data-pinned", next); pinned = next; peek?.setAttribute("aria-expanded", String(next));
      if (keyboard) { if (next && unpin && unpin.offsetParent) unpin.focus({ preventScroll: true }); else if (!next && hadFocus) peek?.focus({ preventScroll: true }); }
      else if (hadFocus) document.activeElement?.blur();
    };
    const ms = (parseFloat(getComputedStyle(document.documentElement).getPropertyValue("--header-autohide")) || 4) * 1000;
    const arm = () => { const inner = $(".nav__inner", nav); if (inner) void getComputedStyle(inner).opacity; nav.setAttribute("data-collapsed", ""); };
    setTimeout(arm, prefersReducedMotion() ? ms : 0);
    peek?.addEventListener("click", (e) => set(true, e.detail === 0));
    unpin?.addEventListener("click", (e) => set(false, e.detail === 0));
    document.addEventListener("pointerdown", (e) => { if (pinned && !nav.contains(e.target)) set(false, false); });
    document.addEventListener("keydown", (e) => { if (e.key === "Escape" && pinned) set(false, true); });
  }
}

// ---- Home: Free / Pro audience toggle.
for (const group of $$("[data-audience-toggle]")) {
  const root = group.closest("[data-audience]");
  for (const b of $$("button[data-set-audience]", group)) b.addEventListener("click", () => {
    const apply = () => { if (root) root.dataset.audience = b.dataset.setAudience; for (const o of $$("button", group)) o.setAttribute("aria-pressed", String(o === b)); };
    if (document.startViewTransition && !prefersReducedMotion()) document.startViewTransition(apply); else apply();
  });
}

// ---- Copy buttons with live region + toast.
if (navigator.clipboard) {
  const live = $("[data-copy-live]"), toast = $("[data-toast]"), text = toast?.querySelector("span");
  let t = 0;
  const show = (msg) => { if (live) live.textContent = msg; if (text) text.textContent = msg; toast?.classList.add("is-on"); clearTimeout(t); t = setTimeout(() => toast?.classList.remove("is-on"), 1600); };
  idle(() => $$("button[data-copy]").forEach((b) => (b.hidden = false)));
  document.addEventListener("click", async (e) => {
    const btn = e.target?.closest?.("button[data-copy]");
    if (!btn) return;
    const label = btn.querySelector(".copy__label");
    try {
      await navigator.clipboard.writeText(btn.dataset.copy ?? "");
      btn.dataset.state = "copied"; if (label) label.textContent = "Copied"; show("Copied to clipboard");
      setTimeout(() => { delete btn.dataset.state; if (label) label.textContent = "Copy"; if (live) live.textContent = ""; }, 1800);
    } catch { show("Copy failed — select the text instead"); }
  });
}

// ---- Page motion: scroll reveal, shine, still pages, step reveal.
initScrollReveal(); initShine(); initStill(); initStepReveal();

// ---- Hero stage tilt and the WebGL intro ring (pages with a hero, not docs).
const inDocs = /\/docs(\/|$)/.test(location.pathname);
if (!inDocs) {
  const stage = $("[data-stage]");
  if (stage) initStage(stage);
  const root = document.documentElement, hero = $(".hero");
  if (hero && !prefersReducedMotion() && root.classList.contains("intro") && hero.getBoundingClientRect().bottom >= 0) {
    setTimeout(() => root.classList.remove("intro"), 2200);
    const cs = getComputedStyle(root);
    const a = cs.getPropertyValue("--accent").trim(), b = cs.getPropertyValue("--accent-2").trim();
    if (/^#[0-9a-f]{6}$/i.test(a) && /^#[0-9a-f]{6}$/i.test(b) && me?.dataset.intro) {
      requestAnimationFrame(() => import(me.dataset.intro).then((m) => m.playIntro(hero, a, b)).catch(() => {}));
    }
  } else root.classList.remove("intro");
}

// ---- Terminals: type out once when scrolled into view (cox scenes drive their own).
idle(() => {
  for (const term of $$("[data-term][data-term-auto]")) {
    const t = createTerminal(term);
    const mq = matchMedia(REDUCE_MOTION);
    if (mq.matches) { t.play(); continue; }
    onVisible(term, () => t.play(), 0.35);
    mq.addEventListener("change", () => { if (mq.matches) t.settle(); });
  }
});

// ---- Product visuals.
idle(() => {
  $$("[data-bitset]").forEach(initBitset);
  $$("[data-graph]").forEach(initGraph);
  $$("[data-particles]").forEach(initParticles);
  $$("[data-route]").forEach(initRoute);
});

// ---- Cox scenes: tabs + perspective panels, each panel types its own terminal.
for (const r of $$("[data-scenes]")) {
  const tabs = $$("[role=tab]", r), panels = $$("[data-scene]", r);
  const terms = panels.map((p) => { const t = $("[data-term]", p); return t ? createTerminal(t) : null; });
  let active = 0, prev = 0, seen = false;
  const render = () => {
    $(".scenes__stage", r)?.style.setProperty("--active", String(active));
    tabs.forEach((t, i) => { const on = i === active; t.setAttribute("aria-selected", String(on)); t.tabIndex = on ? 0 : -1; });
    panels.forEach((p, i) => { const on = i === active; p.classList.toggle("is-active", on); p.toggleAttribute("inert", !on); p.style.setProperty("--off", String(Math.abs(i - active))); });
    if (seen) { if (prev !== active) terms[prev]?.stop(); terms[active]?.play(); }
    prev = active;
  };
  const go = (k, focus = false) => { const n = panels.length || 1; active = ((k % n) + n) % n; render(); if (focus) tabs[active]?.focus(); };
  r.classList.add("is-ready");
  tabs.forEach((t, i) => t.addEventListener("click", () => go(i)));
  panels.forEach((p, i) => p.addEventListener("click", () => go(i)));
  r.addEventListener("keydown", (e) => {
    const onTab = document.activeElement?.getAttribute("role") === "tab" && r.contains(document.activeElement);
    if (e.key === "ArrowRight" || e.key === "ArrowLeft") { e.preventDefault(); go(active + (e.key === "ArrowRight" ? 1 : -1), onTab); }
    if ((e.key === "Home" || e.key === "End") && e.target.getAttribute("role") === "tab") { e.preventDefault(); go(e.key === "Home" ? 0 : panels.length - 1, true); }
  });
  onVisible(r, () => { seen = true; render(); }, 0.35);
  render();
}

// ---- Home ring carousel + hotspots.
for (const root of $$("[data-ring]")) {
  const AUTO_MS = 4200;
  const stage = $("[data-ring-stage]", root), track = $("[data-ring-track]", root), items = $$("[data-ring-item]", root);
  const status = $("[data-ring-status]", root), controls = $("[data-ring-controls]", root);
  const titles = (root.dataset.titles ?? "").split("\u001f");
  const n = items.length;
  let state = { index: 0, rot: 0 }, touched = false, timer = 0;
  const render = () => {
    track?.style.setProperty("--rot", `${state.rot}deg`);
    items.forEach((el, k) => el.classList.toggle("is-active", k === state.index));
    if (status) { status.textContent = statusText(titles, state.index); status.setAttribute("aria-live", touched ? "polite" : "off"); }
  };
  const setState = (s) => { state = s; render(); };
  const go = (k) => setState(turnTo(state, k, n));
  const takeOver = () => { touched = true; clearInterval(timer); };
  root.classList.add("is-ready");
  if (controls) controls.hidden = false;
  $("[data-ring-prev]", root)?.addEventListener("click", () => { takeOver(); go(state.index - 1); });
  $("[data-ring-next]", root)?.addEventListener("click", () => { takeOver(); go(state.index + 1); });
  root.addEventListener("keydown", (e) => {
    if (e.target.closest(".hotspot") || (e.key !== "ArrowRight" && e.key !== "ArrowLeft")) return;
    e.preventDefault(); takeOver();
    go(state.index + (e.key === "ArrowRight" ? 1 : -1));
    items[state.index]?.querySelector(".ring__card")?.focus({ preventScroll: true });
  });
  items.forEach((el, k) => el.addEventListener("focusin", () => { takeOver(); if (k !== state.index) go(k); }));
  let startX = 0, startRot = 0, dragRot = 0, dragging = false, moved = false;
  stage?.addEventListener("pointerdown", (e) => {
    if (matchMedia("(max-width: 767px)").matches || e.target.closest(".hotspot")) return;
    dragging = true; moved = false; startX = e.clientX; startRot = dragRot = state.rot;
  });
  addEventListener("pointermove", (e) => {
    if (!dragging) return;
    const dx = e.clientX - startX;
    if (!moved && Math.abs(dx) > 6) { moved = true; takeOver(); stage.classList.add("is-dragging"); }
    if (moved) { dragRot = startRot - dx * 0.35; track?.style.setProperty("--rot", `${dragRot}deg`); }
  });
  const end = () => { if (!dragging) return; dragging = false; stage.classList.remove("is-dragging"); if (!moved) return; setState(snap(dragRot, n)); setTimeout(() => { moved = false; }, 0); };
  addEventListener("pointerup", end); addEventListener("pointercancel", end);
  stage?.addEventListener("click", (e) => { if (moved) { e.preventDefault(); e.stopPropagation(); moved = false; } }, true);
  const mq = matchMedia(REDUCE_MOTION);
  const start = () => {
    clearInterval(timer);
    if (touched || mq.matches) return;
    timer = setInterval(() => {
      if (document.hidden || root.matches(":hover") || root.contains(document.activeElement) || $('.hotspot__dot[aria-expanded="true"]', root)) return;
      go(state.index + 1);
    }, AUTO_MS);
  };
  mq.addEventListener("change", start);
  start(); render();

  // Hotspots: one open at a time; Esc closes and returns focus; outside click closes.
  const dots = $$(".hotspot__dot", root);
  let open = null;
  const sync = () => dots.forEach((b) => { const id = b.getAttribute("aria-controls"), on = id === open; b.setAttribute("aria-expanded", String(on)); const tip = document.getElementById(id); if (tip) tip.hidden = !on; });
  dots.forEach((b) => b.addEventListener("click", () => { const id = b.getAttribute("aria-controls"); open = open === id ? null : id; sync(); }));
  document.addEventListener("keydown", (e) => { if (e.key !== "Escape" || !open) return; const dot = dots.find((b) => b.getAttribute("aria-controls") === open); open = null; sync(); dot?.focus(); });
  document.addEventListener("click", (e) => { if (open && !e.target.closest(".hotspot")) { open = null; sync(); } });
}

// ---- Docs: sticky nav strip and on-page TOC.
$$("[data-dnav]").forEach(initDocsNav);
idle(() => $$("[data-toc]").forEach(initToc));

// ---- Header search (Algolia with a search-only key; otherwise "not available yet").
for (const root of $$("[data-search]")) {
  const input = $("#search-input", root), btn = $(".search__btn", root), cancel = $(".search__cancel", root), panel = $("#search-results", root);
  const provider = pickProvider({ appId: root.dataset.appId, searchKey: root.dataset.searchKey, index: root.dataset.index }, false);
  let open = false, result = { kind: "idle" }, active = -1, ctrl = null, timer = 0;
  const esc = (s) => s.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;");
  const hits = () => (result.kind === "hits" ? result.hits : []);
  const draw = () => {
    const shown = open && result.kind !== "idle";
    root.dataset.open = String(open);
    btn.setAttribute("aria-expanded", String(open));
    input.setAttribute("aria-expanded", String(shown));
    input.tabIndex = cancel.tabIndex = open ? 0 : -1;
    if (shown && active >= 0) input.setAttribute("aria-activedescendant", `search-opt-${active}`); else input.removeAttribute("aria-activedescendant");
    panel.hidden = !shown;
    if (!shown) panel.dataset.visible = "false"; else requestAnimationFrame(() => (panel.dataset.visible = "true"));
    const empty = (t, err) => `<p class="search__empty${err ? " search__empty--error" : ""}" role="option" aria-disabled="true">${t}</p>`;
    panel.innerHTML = result.kind === "off" ? empty("Search isn’t available yet.")
      : result.kind === "error" ? empty("Search is unavailable right now. Try again later.", true)
      : result.kind === "hits" ? (result.hits.length ? result.hits.map((h, i) => `<a id="search-opt-${i}" class="search__row" role="option" aria-selected="${i === active}" href="${esc(h.url)}"${h.accent ? ` style="--row-accent:${esc(h.accent)}"` : ""}><span class="search__icon" aria-hidden="true"></span><span class="search__title">${highlightParts(h.title, result.q).map((p) => (p.match ? `<mark>${esc(p.text)}</mark>` : esc(p.text))).join("")}</span><span class="search__section">${esc(h.section ?? "")}</span></a>`).join("") : empty(`No results for “${esc(result.q)}”`))
      : "";
    if (active >= 0) document.getElementById(`search-opt-${active}`)?.scrollIntoView({ block: "nearest" });
  };
  const reset = () => { clearTimeout(timer); ctrl?.abort(); result = { kind: "idle" }; active = -1; draw(); };
  const query = async (q) => {
    ctrl?.abort();
    if (!provider) { result = { kind: "off" }; active = -1; return draw(); }
    const c = (ctrl = new AbortController());
    try { const res = await provider(q, c.signal); if (c.signal.aborted || !open) return; result = { kind: "hits", hits: res, q }; active = res.length ? 0 : -1; }
    catch (e) { if (c.signal.aborted) return; result = { kind: "error", q }; active = -1; console.warn("[search]", e); }
    draw();
  };
  const schedule = (raw) => { clearTimeout(timer); const q = raw.trim(); if (!q) return reset(); timer = setTimeout(() => query(q), 150); };
  const doOpen = () => { input.focus({ preventScroll: true }); if (open) return; open = true; if (matchMedia("(max-width: 639px)").matches) document.documentElement.classList.add("search-lock"); draw(); };
  const close = (focusBtn = true) => { if (!open) return; open = false; reset(); document.documentElement.classList.remove("search-lock"); if (focusBtn) btn.focus(); };
  btn.addEventListener("click", doOpen);
  cancel.addEventListener("click", () => { input.value = ""; close(); });
  input.addEventListener("focus", () => { if (!open) doOpen(); });
  input.addEventListener("input", () => schedule(input.value));
  input.addEventListener("keydown", (e) => {
    const h = hits();
    if (e.key === "Escape") { e.preventDefault(); if (input.value) { input.value = ""; reset(); } else close(); }
    else if ((e.key === "ArrowDown" || e.key === "ArrowUp") && h.length) { e.preventDefault(); active = moveActive(active, e.key === "ArrowDown" ? 1 : -1, h.length); draw(); }
    else if (e.key === "Enter" && active >= 0 && h[active]?.url) { e.preventDefault(); location.href = h[active].url; }
  });
  root.addEventListener("focusout", (e) => { const next = e.relatedTarget; if (next && root.contains(next)) return; if (!input.value) close(false); });
  document.addEventListener("pointerdown", (e) => { if (open && !root.contains(e.target) && !input.value) close(false); });
  const isTyping = (el) => el instanceof HTMLElement && (el.isContentEditable || /^(INPUT|TEXTAREA|SELECT)$/.test(el.tagName));
  document.addEventListener("keydown", (e) => {
    const k = e.key.toLowerCase();
    const hot = (k === "/" && !e.metaKey && !e.ctrlKey && !e.altKey) || (k === "k" && (e.metaKey || e.ctrlKey));
    if (!hot || isTyping(e.target)) return;
    e.preventDefault(); doOpen();
  });
  if (input.value) { doOpen(); schedule(input.value); }
}

// ---- Wishlist mail links: the token is sent only on a click (needs API_URL at build time).
{
  const sec = $("[data-wishlist-link]");
  const api = me?.dataset.api;
  if (sec && api) {
    const action = sec.dataset.wishlistLink;
    const button = $("[data-wishlist-action]", sec), status = $("[role=status]", sec);
    const token = new URLSearchParams(location.search).get("token") ?? "";
    if (location.search) history.replaceState(null, "", location.pathname);
    if (!token) status.textContent = "This link is incomplete. Open the link from the mail again.";
    else button.disabled = false;
    const gql = (query, variables) => fetch(api, { method: "POST", headers: { "content-type": "application/json" }, body: JSON.stringify({ query, variables }) }).then((r) => r.json());
    button.addEventListener("click", async () => {
      button.disabled = true; status.textContent = "Sending…";
      try {
        if (action === "confirm") {
          const r = await gql("mutation ConfirmWishlist($token: String!) { confirmWishlist(token: $token) { __typename ... on MutationConfirmWishlistSuccess { data } } }", { token });
          const c = r?.data?.confirmWishlist;
          status.textContent = c?.__typename === "MutationConfirmWishlistSuccess" ? `Confirmed: you are on the ${c.data} Pro wishlist.` : "This link is invalid, already used or expired.";
        } else {
          await gql("mutation UnsubscribeWishlist($token: String!) { unsubscribeWishlist(token: $token) }", { token });
          status.textContent = "Unsubscribed: no more mail about this wishlist.";
        }
      } catch { status.textContent = "The wishlist cannot be reached right now. Try again later."; button.disabled = false; }
    });
  }
}
void FINE_POINTER;
