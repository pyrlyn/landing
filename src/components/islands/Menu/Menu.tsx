// Products menu: a native <details> disclosure (works without JS). The island adds: close on Esc
// (focus back to the summary), on an outside click, and when Tab leaves the panel.
import { useRef, type ReactNode } from "react";
import { useDocumentEvent } from "../hooks/useDocumentEvent";

interface Props { currentTitle?: string; triggerClass: string; children?: ReactNode }

export default function Menu({ currentTitle, triggerClass, children }: Props) {
  const ref = useRef<HTMLDetailsElement>(null);
  useDocumentEvent("click", (e) => {
    const d = ref.current;
    if (d?.open && !d.contains(e.target as Node)) d.open = false;
  });
  useDocumentEvent("keydown", (e) => {
    const d = ref.current;
    if (e.key !== "Escape" || !d?.open) return;
    d.open = false;
    d.querySelector("summary")?.focus();
  });
  // A null relatedTarget is a click on a non-focusable spot; outside clicks are handled above.
  const onBlur = (e: React.FocusEvent<HTMLDetailsElement>) => {
    const d = ref.current, to = e.relatedTarget as Node | null;
    if (d?.open && to && !d.contains(to)) d.open = false;
  };
  return (
    <details className="menu__details" data-menu ref={ref} onBlur={onBlur}>
      <summary className={triggerClass} aria-current={currentTitle ? "page" : undefined}>
        Products{currentTitle && <span className="menu__current">· {currentTitle}</span>}
        <svg viewBox="0 0 12 12" width="12" height="12" aria-hidden="true"><path d="m3 4.5 3 3 3-3" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round" /></svg>
      </summary>
      <div className="menu__panel glass glass--thick">{children}</div>
    </details>
  );
}
