export function docSource(raw: string): { title: string | undefined; body: string } {
  const body = raw.replace(/^---\n[\s\S]*?\n---\n/, "");
  const h1 = body.match(/^#\s+(.+)$/m);
  return { title: h1?.[1], body: h1 ? body.replace(h1[0], "") : body };
}

export function countDocHeadings(raw: string): number {
  const md = new Marked({ gfm: true });
  let count = 0;
  md.walkTokens(md.lexer(docSource(raw).body), (token) => {
    if (token.type === "heading" && token.depth <= 3) count++;
  });
  return count;
}
import { Marked } from "marked";

