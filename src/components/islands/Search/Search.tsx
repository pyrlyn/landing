// Header search (design: Мия; styles src/styles/search.css). Ported from feat/header-search as an island.
// The button widens into a field; hits come from the SearchProvider chosen by pickProvider
// (src/lib/client/search-provider.ts: Algolia with keys, local stub on the dev server, otherwise
// "Search isn't available yet"), rendered as an ARIA combobox listbox. `/` or ⌘K/Ctrl+K opens it.
// The field is focused synchronously on open (not after the width transition), so nothing typed right after
// the click or hotkey is lost; text typed before hydration is picked up on mount.
import { useCallback, useEffect, useRef, useState, type CSSProperties, type KeyboardEvent } from "react";
import { pickProvider, type SearchHit } from "../../../lib/client/search-provider";
import { highlightParts, moveActive } from "../../../lib/client/search-state";
import { useDocumentEvent } from "../hooks/useDocumentEvent";

interface Props { appId?: string; searchKey?: string; index?: string }
type Result = { kind: "idle" } | { kind: "hits"; hits: SearchHit[]; q: string } | { kind: "error"; q: string } | { kind: "off" };

const isTyping = (el: EventTarget | null) =>
  el instanceof HTMLElement && (el.isContentEditable || /^(INPUT|TEXTAREA|SELECT)$/.test(el.tagName));
const DEBOUNCE_MS = 150;

export default function Search({ appId, searchKey, index }: Props) {
  const rootRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<HTMLInputElement>(null);
  const btnRef = useRef<HTMLButtonElement>(null);
  const [provider] = useState(() => pickProvider({ appId, searchKey, index }, import.meta.env.DEV));
  const [open, setOpen] = useState(false);
  const [value, setValue] = useState("");
  const [result, setResult] = useState<Result>({ kind: "idle" });
  const [active, setActive] = useState(-1);
  const [visible, setVisible] = useState(false);
  const ctrl = useRef<AbortController | null>(null);
  const timer = useRef(0);
  const openRef = useRef(open);
  openRef.current = open;

  const hits = result.kind === "hits" ? result.hits : [];
  const panelShown = open && result.kind !== "idle";

  const reset = useCallback(() => { clearTimeout(timer.current); ctrl.current?.abort(); setResult({ kind: "idle" }); setActive(-1); }, []);

  const query = useCallback(async (q: string) => {
    ctrl.current?.abort();
    if (!provider) { setResult({ kind: "off" }); setActive(-1); return; }
    const c = (ctrl.current = new AbortController());
    try {
      const res = await provider(q, c.signal);
      if (c.signal.aborted || !openRef.current) return;
      setResult({ kind: "hits", hits: res, q }); setActive(res.length ? 0 : -1);
    } catch (e) {
      if (c.signal.aborted) return;
      setResult({ kind: "error", q }); setActive(-1);
      console.warn("[search]", e);
    }
  }, [provider]);

  const schedule = useCallback((raw: string) => {
    clearTimeout(timer.current);
    const q = raw.trim();
    if (!q) return reset();
    timer.current = window.setTimeout(() => query(q), DEBOUNCE_MS);
  }, [query, reset]);

  const doOpen = useCallback(() => {
    // Focus first, in the same task as the click / hotkey: typing that follows lands in the field.
    inputRef.current?.focus({ preventScroll: true });
    if (openRef.current) return;
    openRef.current = true;
    setOpen(true);
    if (matchMedia("(max-width: 639px)").matches) document.documentElement.classList.add("search-lock");
  }, []);

  const close = useCallback((focusBtn = true) => {
    if (!openRef.current) return;
    openRef.current = false;
    setOpen(false);
    reset();
    document.documentElement.classList.remove("search-lock");
    if (focusBtn) btnRef.current?.focus();
  }, [reset]);

  // Text typed into the field before hydration (or restored by the browser) is not dropped.
  useEffect(() => {
    const v = inputRef.current?.value ?? "";
    if (v) { setValue(v); doOpen(); schedule(v); }
    return () => { clearTimeout(timer.current); ctrl.current?.abort(); document.documentElement.classList.remove("search-lock"); };
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  // Panel fade-in a frame after it is unhidden (CSS transition on data-visible).
  useEffect(() => {
    if (!panelShown) { setVisible(false); return; }
    const f = requestAnimationFrame(() => setVisible(true));
    return () => cancelAnimationFrame(f);
  }, [panelShown]);

  useEffect(() => {
    if (active >= 0) document.getElementById(`search-opt-${active}`)?.scrollIntoView({ block: "nearest" });
  }, [active]);

  useDocumentEvent("pointerdown", (e) => {
    if (openRef.current && !rootRef.current?.contains(e.target as Node) && !inputRef.current?.value) close(false);
  });
  useDocumentEvent("keydown", (e) => {
    const k = e.key.toLowerCase();
    const hot = (k === "/" && !e.metaKey && !e.ctrlKey && !e.altKey) || (k === "k" && (e.metaKey || e.ctrlKey));
    if (!hot || isTyping(e.target)) return;
    e.preventDefault();
    doOpen();
  });

  const onKeyDown = (e: KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Escape") {
      e.preventDefault();
      if (value) { setValue(""); reset(); } else close();
    } else if ((e.key === "ArrowDown" || e.key === "ArrowUp") && hits.length) {
      e.preventDefault();
      setActive((a) => moveActive(a, e.key === "ArrowDown" ? 1 : -1, hits.length));
    } else if (e.key === "Enter" && active >= 0 && hits[active]?.url) {
      e.preventDefault();
      location.href = hits[active].url;
    }
  };

  const mark = (t: string, q: string) => highlightParts(t, q).map((p, i) => (p.match ? <mark key={i}>{p.text}</mark> : p.text));

  return (
    <div className="search" data-open={String(open)} data-search ref={rootRef}
      onBlur={(e) => { const next = e.relatedTarget as Node | null; if (next && rootRef.current?.contains(next)) return; if (!inputRef.current?.value) close(false); }}>
      <button ref={btnRef} className="search__btn" type="button" aria-label="Search" aria-expanded={open} aria-controls="search-input" title="Search (/ or ⌘K)" onClick={doOpen}>
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" aria-hidden="true"><circle cx="11" cy="11" r="7" /><path d="m20 20-3.5-3.5" /></svg>
      </button>
      <input ref={inputRef} id="search-input" className="search__input" type="search" role="combobox" aria-expanded={panelShown}
        aria-controls="search-results" aria-autocomplete="list" aria-label="Search tools and docs" autoComplete="off" placeholder="Search tools and docs"
        aria-activedescendant={panelShown && active >= 0 ? `search-opt-${active}` : undefined}
        tabIndex={open ? 0 : -1} value={value}
        onFocus={() => { if (!openRef.current) doOpen(); }}
        onChange={(e) => { setValue(e.target.value); schedule(e.target.value); }} onKeyDown={onKeyDown} />
      <kbd className="search__kbd">Esc</kbd>
      <button className="search__cancel" type="button" tabIndex={open ? 0 : -1} aria-label="Close search" onClick={() => { setValue(""); close(); }}>Cancel</button>
      <div id="search-results" className="search__panel" role="listbox" aria-label="Search results" hidden={!panelShown} data-visible={String(visible)}>
        {result.kind === "off" && <p className="search__empty" role="option" aria-disabled="true">Search isn’t available yet.</p>}
        {result.kind === "error" && <p className="search__empty search__empty--error" role="option" aria-disabled="true">Search is unavailable right now. Try again later.</p>}
        {result.kind === "hits" && (hits.length
          ? hits.map((h, i) => (
            <a key={h.id} id={`search-opt-${i}`} className="search__row" role="option" aria-selected={i === active} href={h.url}
              style={h.accent ? ({ "--row-accent": h.accent } as CSSProperties) : undefined}>
              <span className="search__icon" aria-hidden="true"></span>
              <span className="search__title">{mark(h.title, result.q)}</span>
              <span className="search__section">{h.section ?? ""}</span>
            </a>))
          : <p className="search__empty" role="option" aria-disabled="true">No results for “{result.q}”</p>)}
      </div>
    </div>
  );
}
