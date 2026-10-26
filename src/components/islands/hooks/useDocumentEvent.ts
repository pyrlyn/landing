import { useEffect, useRef } from "react";
/** Document-level listener with the latest handler and cleanup on unmount. */
export function useDocumentEvent<K extends keyof DocumentEventMap>(type: K, handler: (e: DocumentEventMap[K]) => void) {
  const ref = useRef(handler);
  ref.current = handler;
  useEffect(() => {
    const fn = (e: DocumentEventMap[K]) => ref.current(e);
    document.addEventListener(type, fn);
    return () => document.removeEventListener(type, fn);
  }, [type]);
}
