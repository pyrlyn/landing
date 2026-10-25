// Copy buttons. The buttons themselves are static HTML inside rendered Markdown (src/lib/docs.ts,
// src/lib/sections.ts), so this island owns the shared feedback instead: it reveals the buttons when the
// Clipboard API exists, handles their clicks (delegated), flips the label to "Copied", shows the toast
// and announces through its own aria-live region.
import { useEffect, useRef, useState } from "react";

export default function CopyFeedback() {
  const [live, setLive] = useState("");
  const [toast, setToast] = useState<{ on: boolean; text: string }>({ on: false, text: "Copied" });
  const toastTimer = useRef(0);

  useEffect(() => {
    if (!navigator.clipboard) return;
    const buttons = [...document.querySelectorAll<HTMLButtonElement>("button[data-copy]")];
    buttons.forEach((b) => (b.hidden = false));
    const show = (text: string) => {
      setToast({ on: true, text });
      clearTimeout(toastTimer.current);
      toastTimer.current = window.setTimeout(() => setToast((t) => ({ ...t, on: false })), 1600);
    };
    const onClick = async (e: MouseEvent) => {
      const btn = (e.target as Element | null)?.closest<HTMLButtonElement>("button[data-copy]");
      if (!btn) return;
      const label = btn.querySelector(".copy__label");
      try {
        await navigator.clipboard.writeText(btn.dataset.copy ?? "");
        btn.dataset.state = "copied";
        if (label) label.textContent = "Copied";
        setLive("Copied to clipboard"); show("Copied to clipboard");
        setTimeout(() => { delete btn.dataset.state; if (label) label.textContent = "Copy"; setLive(""); }, 1800);
      } catch {
        setLive("Copy failed — select the text instead"); show("Copy failed — select the text instead");
      }
    };
    document.addEventListener("click", onClick);
    return () => { document.removeEventListener("click", onClick); clearTimeout(toastTimer.current); };
  }, []);

  return (
    <>
      <p className="sr-only" aria-live="polite">{live}</p>
      <div className={toast.on ? "toast is-on" : "toast"} aria-hidden="true">
        <svg viewBox="0 0 16 16" width="14" height="14"><path d="m3.5 8.5 3 3 6-7" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" /></svg>
        <span>{toast.text}</span>
      </div>
    </>
  );
}
