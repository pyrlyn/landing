// Sticky docs nav: edge fades, current tab centred, sliding underline; closes the compact Section menu on
// Esc / outside click / tab-out. Markup: static DocsNav.astro (children). Logic: lib/client/docs.ts.
import type { ReactNode } from "react";
import { Enhance } from "../hooks/useEnhance";
import { initDocsNav } from "../../../lib/client/docs";

export default function DocsNav({ children }: { children?: ReactNode }) {
  return <Enhance init={initDocsNav}>{children}</Enhance>;
}
