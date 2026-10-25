// Page-wide motion, renders nothing: card shine, offscreen pausing of decorative loops (html.is-still),
// scroll reveals below the fold, and step-rail reveals. client:load so reveals are armed before the user
// scrolls (same timing as the old module script). Logic: lib/client/hero.ts.
import { useEffect } from "react";
import { initScrollReveal, initShine, initStepReveal, initStill } from "../../../lib/client/hero";

export default function PageMotion() {
  useEffect(() => {
    const offs = [initScrollReveal(), initShine(), initStill(), initStepReveal()];
    return () => offs.forEach((f) => f?.());
  }, []);
  return null;
}
