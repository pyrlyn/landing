// Hero stage tilt + parallax (fine pointer, motion allowed). Renders nothing: the stage holds other islands
// (ring carousel, audience toggle), so it stays static Astro markup and this island finds [data-stage].
import { useEffect } from "react";
import { initStage } from "../../../lib/client/hero";

export default function HeroStage() {
  useEffect(() => {
    const stage = document.querySelector<HTMLElement>("[data-stage]");
    return stage ? initStage(stage) : undefined;
  }, []);
  return null;
}
