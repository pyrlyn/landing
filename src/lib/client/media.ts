// Shared media queries for islands (browser only).
export const REDUCE_MOTION = "(prefers-reduced-motion: reduce)";
export const FINE_POINTER = "(hover: hover) and (pointer: fine)";
export const prefersReducedMotion = () => typeof matchMedia === "function" && matchMedia(REDUCE_MOTION).matches;
