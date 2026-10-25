/* Pure open/close rules for the header search, kept DOM-free so they can be unit-tested. */
export type SearchState = { open: boolean; value: string; focused: boolean };

/** The field stays open while it has text or focus. */
export const shouldStayOpen = (s: SearchState) => s.open && (s.focused || s.value.trim() !== "");

/** Escape: first clears text, then closes. */
export const onEscape = (s: SearchState): SearchState =>
  s.value ? { ...s, value: "" } : { ...s, open: false, focused: false };

/** Wrap-around move through results; -1 when there are none. */
export const moveActive = (active: number, delta: number, count: number) =>
  count ? (active + delta + count) % count : -1;

/** Split text into plain/match parts for highlighting (case-insensitive, all occurrences). */
export function highlightParts(text: string, q: string): { text: string; match: boolean }[] {
  const n = q.trim().toLowerCase();
  if (!n) return [{ text, match: false }];
  const out: { text: string; match: boolean }[] = [];
  const low = text.toLowerCase();
  let i = 0;
  for (let j = low.indexOf(n); j !== -1; j = low.indexOf(n, i)) {
    if (j > i) out.push({ text: text.slice(i, j), match: false });
    out.push({ text: text.slice(j, j + n.length), match: true });
    i = j + n.length;
  }
  if (i < text.length) out.push({ text: text.slice(i), match: false });
  return out;
}
