/* Header search: opens an animated field, asks the SearchProvider (search-provider.ts) for hits,
   renders an ARIA combobox listbox. Styles: src/styles/search.css. */
import { provider, type SearchHit } from "./search-provider";
import { highlightParts, moveActive } from "./search-state";

const esc = (s: string) => s.replace(/[&<>"']/g, (c) => `&#${c.charCodeAt(0)};`);
const reduced = () => matchMedia("(prefers-reduced-motion: reduce)").matches;
const isTyping = (el: EventTarget | null) =>
  el instanceof HTMLElement && (el.isContentEditable || /^(INPUT|TEXTAREA|SELECT)$/.test(el.tagName));

export function initSearch() {
  const found = document.querySelector<HTMLElement>("[data-search]");
  if (!found) return;
  const root: HTMLElement = found;
  const btn = root.querySelector<HTMLButtonElement>(".search__btn")!;
  const input = root.querySelector<HTMLInputElement>(".search__input")!;
  const panel = root.querySelector<HTMLElement>(".search__panel")!;
  const cancel = root.querySelector<HTMLButtonElement>(".search__cancel");
  let hits: SearchHit[] = [];
  let ctrl: AbortController | null = null;
  let lastQ = "";
  let active = -1;
  let timer = 0;
  const isOpen = () => root.dataset.open === "true";

  const setExpanded = (v: boolean) => {
    btn.setAttribute("aria-expanded", String(v));
    input.setAttribute("aria-expanded", String(v && !panel.hidden));
  };
  const hidePanel = () => {
    panel.dataset.visible = "false";
    panel.hidden = true;
    input.removeAttribute("aria-activedescendant");
    input.setAttribute("aria-expanded", "false");
  };
  const showPanel = () => {
    panel.hidden = false;
    input.setAttribute("aria-expanded", "true");
    requestAnimationFrame(() => (panel.dataset.visible = "true"));
  };

  function open() {
    if (isOpen()) return input.focus();
    root.dataset.open = "true";
    setExpanded(true);
    input.tabIndex = 0;
    cancel && (cancel.tabIndex = 0);
    if (matchMedia("(max-width: 639px)").matches) document.documentElement.classList.add("search-lock");
    if (reduced()) input.focus();
    else {
      const done = (e: TransitionEvent) => { if (e.target === root && e.propertyName === "width") { root.removeEventListener("transitionend", done); input.focus(); } };
      root.addEventListener("transitionend", done);
      setTimeout(() => document.activeElement !== input && isOpen() && input.focus(), 320); // fallback (mobile: no width transition)
    }
  }
  function close(focusBtn = true) {
    if (!isOpen()) return;
    root.dataset.open = "false";
    hidePanel();
    setExpanded(false);
    input.tabIndex = -1;
    cancel && (cancel.tabIndex = -1);
    document.documentElement.classList.remove("search-lock");
    if (focusBtn) btn.focus();
  }

  const mark = (t: string) => highlightParts(t, lastQ).map((p) => (p.match ? `<mark>${esc(p.text)}</mark>` : esc(p.text))).join("");
  function render(error = false) {
    active = hits.length ? 0 : -1;
    panel.innerHTML = error
      ? `<p class="search__empty search__empty--error" role="option" aria-disabled="true">Search is unavailable right now. Try again later.</p>`
      : hits.length
      ? hits.map((h, i) => `<a id="search-opt-${i}" class="search__row" role="option" aria-selected="${i === active}" href="${esc(h.url)}"${h.accent ? ` style="--row-accent:${esc(h.accent)}"` : ""}><span class="search__icon" aria-hidden="true"></span><span class="search__title">${mark(h.title)}</span><span class="search__section">${esc(h.section ?? "")}</span></a>`).join("")
      : `<p class="search__empty" role="option" aria-disabled="true">No results for “${esc(lastQ)}”</p>`;
    syncActive();
    showPanel();
  }
  function syncActive() {
    panel.querySelectorAll<HTMLElement>(".search__row").forEach((r, i) => r.setAttribute("aria-selected", String(i === active)));
    if (active >= 0) {
      input.setAttribute("aria-activedescendant", `search-opt-${active}`);
      document.getElementById(`search-opt-${active}`)?.scrollIntoView({ block: "nearest" });
    } else input.removeAttribute("aria-activedescendant");
  }

  async function query(q: string) {
    ctrl?.abort();
    const c = (ctrl = new AbortController());
    try {
      const res = await provider(q, c.signal);
      if (c.signal.aborted || !isOpen()) return;
      lastQ = q;
      hits = res;
      render();
    } catch (e) {
      if (c.signal.aborted) return;
      lastQ = q;
      hits = [];
      render(true);
      console.warn("[search]", e);
    }
  }
  const reset = () => { clearTimeout(timer); ctrl?.abort(); hits = []; hidePanel(); };

  btn.addEventListener("click", open);
  cancel?.addEventListener("click", () => { input.value = ""; reset(); close(); });
  input.addEventListener("input", () => {
    clearTimeout(timer);
    const q = input.value.trim();
    if (!q) return reset();
    timer = window.setTimeout(() => query(q), 150);
  });
  input.addEventListener("keydown", (e) => {
    if (e.key === "Escape") {
      e.preventDefault();
      if (input.value) { input.value = ""; reset(); } else close();
    } else if ((e.key === "ArrowDown" || e.key === "ArrowUp") && hits.length) {
      e.preventDefault();
      active = moveActive(active, e.key === "ArrowDown" ? 1 : -1, hits.length);
      syncActive();
    } else if (e.key === "Enter" && active >= 0 && hits[active]?.url) {
      e.preventDefault();
      location.href = hits[active].url;
    }
  });
  root.addEventListener("focusout", (e) => {
    const next = e.relatedTarget as Node | null;
    if (next && root.contains(next)) return;
    if (!input.value) close(false);
  });
  document.addEventListener("pointerdown", (e) => {
    if (isOpen() && !root.contains(e.target as Node) && !input.value) close(false);
  });
  document.addEventListener("keydown", (e) => {
    const k = e.key.toLowerCase();
    const hot = (k === "/" && !e.metaKey && !e.ctrlKey && !e.altKey) || (k === "k" && (e.metaKey || e.ctrlKey));
    if (!hot || isTyping(e.target)) return;
    e.preventDefault();
    open();
  });
}
