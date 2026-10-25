import { useEffect, useRef, type ReactNode } from "react";
/** Wraps server-rendered children in a box-less div and runs `init(firstElementChild)` after hydration.
 *  `init` returns its cleanup. Islands using this hydrate client:idle/load (a display:contents wrapper has no
 *  box for client:visible to observe); the init itself waits for visibility where it needs to. */
export function Enhance({ init, children }: { init: (el: HTMLElement) => (() => void) | void; children?: ReactNode }) {
  const ref = useRef<HTMLDivElement>(null);
  const initRef = useRef(init);
  useEffect(() => {
    const host = ref.current?.querySelector("astro-slot") ?? ref.current;
    const el = host?.firstElementChild as HTMLElement | null | undefined;
    return el ? initRef.current(el) ?? undefined : undefined;
  }, []);
  return <div style={{ display: "contents" }} ref={ref}>{children}</div>;
}
