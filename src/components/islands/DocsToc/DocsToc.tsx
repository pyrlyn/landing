// Docs "On this page": highlights the section being read. Markup: static aside (children); logic: lib/client/docs.ts.
import type { ReactNode } from "react";
import { Enhance } from "../hooks/useEnhance";
import { initToc } from "../../../lib/client/docs";

export default function DocsToc({ children }: { children?: ReactNode }) {
  return <Enhance init={initToc}>{children}</Enhance>;
}
