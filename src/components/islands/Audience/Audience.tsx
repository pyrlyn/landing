// Home hero: Free / Pro audience toggle. Owns the segmented buttons; the chosen audience is written to the
// closest [data-audience] (CSS shows .for-oss / .for-pro), inside a view transition when motion is allowed.
// Server render = "oss" pressed, matching the static data-audience="oss".
import { useRef, useState } from "react";
import { prefersReducedMotion } from "../../../lib/client/media";

type Audience = "oss" | "pro";
const OPTIONS: [Audience, string][] = [["oss", "Free"], ["pro", "Pro"]];

export default function AudienceToggle({ labelledBy }: { labelledBy: string }) {
  const ref = useRef<HTMLDivElement>(null);
  const [value, setValue] = useState<Audience>("oss");
  const pick = (a: Audience) => {
    const apply = () => {
      const root = ref.current?.closest<HTMLElement>("[data-audience]");
      if (root) root.dataset.audience = a;
      setValue(a);
    };
    const vt = (document as Document & { startViewTransition?: (cb: () => void) => void }).startViewTransition;
    // flushSync is not needed: the CSS reads data-audience, which apply() sets synchronously.
    if (vt && !prefersReducedMotion()) vt.call(document, apply);
    else apply();
  };
  return (
    <div className="segmented" role="group" aria-labelledby={labelledBy} ref={ref}>
      {OPTIONS.map(([a, label]) => (
        <button key={a} type="button" data-set-audience={a} aria-pressed={value === a} onClick={() => pick(a)}>{label}</button>
      ))}
    </div>
  );
}
