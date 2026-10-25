// Terminal demo controller (browser only). The markup is server-rendered in its final state; this types it
// out line by line with Pause / Replay. Used by the Terminal island (auto-play once in view) and by the
// CoxScenes island, which drives its manual terminals directly (play the active panel, stop the others).
import { prefersReducedMotion } from "./media";

/** play: start (or show the final state under reduced motion); stop: if typing, jump to the end; settle: always jump to the end. */
export interface TerminalController { play(): void; stop(): void; settle(): void; destroy(): void }

export function createTerminal(term: HTMLElement): TerminalController {
  const lines = [...term.querySelectorAll<HTMLElement>(".term__line")];
  const ctl = term.querySelector<HTMLButtonElement>("[data-term-ctl]");
  const typed = lines.map((l) => l.querySelector<HTMLElement>(".term__typed"));
  const full = typed.map((t) => t?.textContent ?? "");
  let timer = 0, i = 0, c = 0, playing = false, done = false;
  const showAll = () => { lines.forEach((l, k) => { l.removeAttribute("data-hide"); if (typed[k]) typed[k]!.textContent = full[k]; }); };
  const setCtl = () => { if (ctl) ctl.textContent = playing ? "Pause" : done ? "Replay" : "Play"; };
  const step = () => {
    if (!playing) return;
    if (i >= lines.length) { playing = false; done = true; setCtl(); return; }
    const line = lines[i], t = typed[i];
    line.removeAttribute("data-hide");
    if (t && c <= full[i].length) {
      t.textContent = full[i].slice(0, c++);
      timer = window.setTimeout(step, c === 1 ? 380 : 24 + Math.random() * 38);
      return;
    }
    i++; c = 0;
    timer = window.setTimeout(step, t ? 260 : 110);
  };
  const start = () => {
    clearTimeout(timer); i = 0; c = 0; done = false; playing = true;
    lines.forEach((l, k) => { l.setAttribute("data-hide", ""); if (typed[k]) typed[k]!.textContent = ""; });
    setCtl(); step();
  };
  const finish = () => { playing = false; clearTimeout(timer); showAll(); done = true; setCtl(); };
  const onCtl = () => {
    if (playing) { playing = false; clearTimeout(timer); setCtl(); }
    else if (done || i === 0) start();
    else { playing = true; setCtl(); step(); }
  };
  if (ctl) { ctl.hidden = false; setCtl(); ctl.addEventListener("click", onCtl); }
  return {
    play() { if (prefersReducedMotion()) { showAll(); done = true; setCtl(); } else if (!playing) start(); },
    stop() { if (playing) finish(); },
    settle: finish,
    destroy() { clearTimeout(timer); ctl?.removeEventListener("click", onCtl); },
  };
}
