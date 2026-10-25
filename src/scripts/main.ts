import { initSearch } from "./search";
// Progressive enhancement only — every page is complete without JavaScript.
// Theme toggle, products menu, ring carousel, copy feedback and the intro sweep are React islands
// (src/components/islands/); what is left here is phase 2 of that migration.
const reduceMotion = matchMedia("(prefers-reduced-motion: reduce)");

initSearch();
