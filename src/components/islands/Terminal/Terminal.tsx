// Terminal demo (non-manual): types once when 35% in view; reduced motion keeps the server-rendered final
// state and only labels the button "Replay". Markup is static Astro (children); logic in lib/client/terminal.
import { useEffect, useRef, type ReactNode } from "react";
import { createTerminal } from "../../../lib/client/terminal";
import { REDUCE_MOTION } from "../../../lib/client/media";

export default function Terminal({ children }: { children?: ReactNode }) {
  const ref = useRef<HTMLDivElement>(null);
  useEffect(() => {
    const term = ref.current?.querySelector<HTMLElement>("[data-term]");
    if (!term) return;
    const t = createTerminal(term);
    const mq = matchMedia(REDUCE_MOTION);
    if (mq.matches) { t.play(); return () => t.destroy(); }
    const io = new IntersectionObserver(([en]) => { if (en.isIntersecting) { io.disconnect(); t.play(); } }, { threshold: 0.35 });
    io.observe(term);
    const onMq = () => { if (mq.matches) t.settle(); };
    mq.addEventListener("change", onMq);
    return () => { io.disconnect(); mq.removeEventListener("change", onMq); t.destroy(); };
  }, []);
  return <div style={{ display: "contents" }} ref={ref}>{children}</div>;
}
