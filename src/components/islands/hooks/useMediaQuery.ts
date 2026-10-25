import { useEffect, useState } from "react";
/** Live media-query match. Server render and first client render return `initial`, so hydration matches. */
export function useMediaQuery(query: string, initial = false) {
  const [matches, setMatches] = useState(initial);
  useEffect(() => {
    const mq = matchMedia(query);
    const sync = () => setMatches(mq.matches);
    sync();
    mq.addEventListener("change", sync);
    return () => mq.removeEventListener("change", sync);
  }, [query]);
  return matches;
}
