// Theme toggle: light <-> dark. The saved choice lives in localStorage ("pyrlyn-theme"); without one the page
// follows the system. The first paint is set by the inline head script in Base.astro, not here.
import { useCallback, useEffect, useState, type ReactNode } from "react";

type Mode = "light" | "dark";
const KEY = "pyrlyn-theme";
const current = (): Mode => (document.documentElement.getAttribute("data-mode") === "light" ? "light" : "dark");
const applyMode = (m: Mode) => {
  const root = document.documentElement;
  root.setAttribute("data-mode", m);
  root.style.colorScheme = m;
  (window as unknown as { __applyThemeMeta?: (m: string) => void }).__applyThemeMeta?.(m);
};

export default function ThemeToggle({ children }: { children?: ReactNode }) {
  // Server render assumes dark (matches the old static label); the real mode is read after hydration.
  const [mode, setMode] = useState<Mode>("dark");

  useEffect(() => {
    setMode(current());
    const mq = matchMedia("(prefers-color-scheme: light)");
    const onSystem = (e: MediaQueryListEvent) => {
      let saved: string | null = null;
      try { saved = localStorage.getItem(KEY); } catch {}
      if (!saved) { const m: Mode = e.matches ? "light" : "dark"; applyMode(m); setMode(m); }
    };
    mq.addEventListener("change", onSystem);
    return () => mq.removeEventListener("change", onSystem);
  }, []);

  const toggle = useCallback(() => {
    const next: Mode = current() === "light" ? "dark" : "light";
    try { localStorage.setItem(KEY, next); } catch {}
    applyMode(next);
    setMode(next);
  }, []);

  return (
    <button className="theme-toggle" type="button" data-theme-toggle onClick={toggle}
      aria-label={mode === "light" ? "Switch to dark theme" : "Switch to light theme"}>
      {children}
    </button>
  );
}
