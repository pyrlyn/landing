// SEO helpers: absolute URLs, meta text, and schema.org JSON-LD nodes.
// Every value comes from the site's content (project front matter, synced Markdown, docs pages);
// nothing here invents ratings, reviews or prices. Pro prices are placeholders and never reach
// structured data; the only offer is the free open-source edition ($0), which is real.
import { u, BRAND } from "./site";
import type { Tool } from "./catalog";

const SITE = import.meta.env.SITE;

/** Absolute URL of an internal path, under the configured base. */
export const abs = (path = "/") => new URL(u(path), SITE).href;

export const HOME_URL = () => abs("/");
export const ORG_ID = () => `${abs("/")}#organization`;
export const WEBSITE_ID = () => `${abs("/")}#website`;
export const appId = (slug: string) => `${abs(`/${slug}/`)}#software`;

/** Markdown to plain text (comments, link targets and emphasis marks dropped; literal `<id>` text kept). */
export const plain = (s = "") =>
  s.replace(/<!--[\s\S]*?-->/g, "").replace(/\[([^\]]*)\]\([^)]*\)/g, "$1")
    .replace(/[`*_]/g, "").replace(/&lt;/g, "<").replace(/&gt;/g, ">").replace(/&amp;/g, "&").replace(/\s+/g, " ").trim();

/** Cut text to `max` characters at a sentence end if one is late enough, else at a word boundary. */
export function clip(text: string, max = 160): string {
  const t = text.replace(/\s+/g, " ").trim();
  if (t.length <= max) return t;
  const cut = t.slice(0, max);
  const dot = cut.search(/[.!?](?=\s)[^.!?]*$/);
  return dot > max * 0.5 ? cut.slice(0, dot + 1) : `${cut.slice(0, max - 1).replace(/[\s,;:—-]+\S*$/, "")}…`;
}

/** First sentence of a Markdown block. */
export const firstSentence = (md = "") => plain(md).match(/^(.*?[.!?])(\s|$)/)?.[1] ?? "";

/** Copywriter-approved product <title> and meta description, keyed by project id. Front matter in
 * content/projects is synced from each product repo, so site SEO copy lives here instead. */
export const PRODUCT_SEO: Record<string, { title: string; description: string }> = {
  rtok: { title: "rtok: token-reduction CLI for AI coding agents | Pyrlyn", description: "rtok cuts tokens for AI coding agents with hooks, an MCP server and an API proxy, all in one Rust binary. Install guides, commands and examples." },
  ketch: { title: "ketch: install CLI tools from GitHub releases | Pyrlyn", description: "ketch is a single-binary package manager for macOS, Linux and Windows: it downloads GitHub release assets, verifies checksums and puts them on your PATH." },
  cox: { title: "cox: modular terminal coding agent in Rust | Pyrlyn", description: "cox is a terminal coding agent in Rust with a safe, event-driven core: interactive TUI, headless runs, editor integration via ACP and MCP tools." },
  runa: { title: "runa: local-first AI CLI for GGUF models | Pyrlyn", description: "runa checks whether a GGUF model fits your hardware before you download it, runs it locally via ggml / llama.cpp, or calls OpenAI and Anthropic." },
  mailune: { title: "Mailune: local-first mail client with private AI | Pyrlyn", description: "Mailune is a local-first mail client with private AI for macOS, iOS, Windows, Linux, Android and the web. In development, no release yet." },
  swarfr: { title: "swarfr: shrink Cargo target/ directories safely | Pyrlyn", description: "swarfr compresses and dedupes Cargo target/ build dirs without deleting what you still build with. Runs under cargo's build lock and keeps mtimes." },
  weft: { title: "Weft: agent-friendly UI description format | Pyrlyn", description: "Weft is an open UI format: strict markup that AI agents read and write, canonical JSON that tools validate and patch. CLI, MCP and editor plugins." },
  stator: { title: "Stator: compile TypeScript to native binaries | Pyrlyn", description: "Stator compiles TypeScript and JavaScript ahead of time to native binaries: unboxed values for typed code, a dynamic path for untyped. Research compiler." },
  aulo: { title: "aulo: local-first voice agent for your computer | Pyrlyn", description: "aulo is a planned voice agent that drives your shell, apps and browser, with MCP both ways and local or remote models. Specification stage." },
  scull: { title: "Scull: Rust terminal emulator with native UI | Pyrlyn", description: "Scull is a terminal emulator with a Rust core and a native interface per platform: SwiftUI on macOS, WinUI on Windows. Early scaffold." },
  kerf: { title: "Kerf: Rust IDE core with sandboxed WASM plugins | Pyrlyn", description: "Kerf is an IDE with a small Rust core, a tree-sitter semantic index and sandboxed WASM plugins; LSP and DAP run as separate processes. Research stage." },
  bindsmith: { title: "bindsmith: Dart bindings for native APIs | Pyrlyn", description: "bindsmith generates Dart bindings for native APIs on six Flutter platforms from one bindsmith.yaml: Android, iOS, macOS, Windows, Linux and Web." },
};

/** Product <title>: approved copy, else `{Title}: {tagline} | Pyrlyn`. */
export function productTitle(t: Tool): string {
  return PRODUCT_SEO[t.id]?.title ?? `${t.data.title}: ${t.data.tagline.replace(/\.$/, "")} | ${BRAND}`;
}

/** Product meta description: approved copy, else the tagline plus the overview's first sentence when it fits. */
export function productDescription(t: Tool, overviewMd = ""): string {
  const own = PRODUCT_SEO[t.id]?.description;
  if (own) return own;
  const tag = t.data.tagline;
  const more = firstSentence(overviewMd);
  return more && tag.length + 1 + more.length <= 155 ? `${tag} ${more}` : clip(tag, 155);
}

/** Cut to `max` characters at a word boundary, ending with "…" when cut. */
export function wordClip(text: string, max: number): string {
  const t = text.replace(/\s+/g, " ").trim();
  if (t.length <= max) return t;
  return `${t.slice(0, max - 1).replace(/\s+\S*$/, "").replace(/[\s,;:—-]+$/, "")}…`;
}

/** Docs overview <title>. */
export const docsIndexTitle = (product: string) => `${product} docs: guides, commands and config | ${BRAND}`;

/** Docs overview meta description: tagline, page count and group names, ≤155 chars. */
export const docsIndexDescription = (product: string, tagline: string, pages: number, groups: string[]) =>
  wordClip(`${product} documentation: ${tagline.replace(/\.$/, "")}. ${pages} pages: ${groups.join(", ")}.`, 155);

export const organization = () => ({
  "@type": "Organization",
  "@id": ORG_ID(),
  name: "Pyrlyn",
  url: HOME_URL(),
  logo: abs("/pyrlyn/pyrlyn-mark-on-dark-1000.png"),
  sameAs: ["https://github.com/pyrlyn"],
});

export const website = (description: string) => ({
  "@type": "WebSite",
  "@id": WEBSITE_ID(),
  name: BRAND,
  url: HOME_URL(),
  description,
  inLanguage: "en",
  publisher: { "@id": ORG_ID() },
});

/** Operating systems named in the project's own Install section. */
export function operatingSystems(installMd = ""): string {
  const os = [["macOS", /macos/i], ["Linux", /linux/i], ["Windows", /windows|powershell/i]] as const;
  return os.filter(([, re]) => re.test(installMd)).map(([n]) => n).join(", ") || "macOS, Linux";
}

export function softwareApplication(t: Tool, o: { description: string; installMd?: string; features?: string[] }) {
  const d = t.data;
  const url = abs(`/${t.id}/`);
  return {
    "@type": "SoftwareApplication",
    "@id": appId(t.id),
    name: d.title,
    description: o.description,
    url,
    applicationCategory: "DeveloperApplication",
    applicationSubCategory: "Command-line tool",
    operatingSystem: operatingSystems(o.installMd),
    softwareVersion: d.version,
    image: abs(`/images/${t.id}/og.jpg`),
    downloadUrl: `${d.repo}/releases`,
    installUrl: `${url}#install`,
    ...(o.features?.length ? { featureList: o.features } : {}),
    isAccessibleForFree: true,
    // The free open-source edition is a real $0 offer; Pro prices are placeholders and are left out.
    offers: { "@type": "Offer", name: "Free · open source", price: "0", priceCurrency: "USD", url: `${url}#pricing` },
    publisher: { "@id": ORG_ID() },
    sameAs: [d.repo, ...(d.homepage ? [d.homepage] : [])],
    mainEntityOfPage: url,
  };
}

/** The same product as source code, so the GitHub repository is linked as `codeRepository`. */
export const sourceCode = (t: Tool) => ({
  "@type": "SoftwareSourceCode",
  "@id": `${abs(`/${t.id}/`)}#source`,
  name: `${t.data.title} source code`,
  codeRepository: t.data.repo,
  programmingLanguage: "Rust",
  targetProduct: { "@id": appId(t.id) },
});

export const breadcrumbs = (items: { name: string; url: string }[]) => ({
  "@type": "BreadcrumbList",
  itemListElement: items.map((it, i) => ({ "@type": "ListItem", position: i + 1, name: it.name, item: it.url })),
});

export function techArticle(t: Tool, o: { headline: string; description: string; url: string; sourceUrl?: string | null; section?: string }) {
  return {
    "@type": "TechArticle",
    "@id": `${o.url}#article`,
    headline: o.headline.slice(0, 110),
    description: o.description,
    url: o.url,
    mainEntityOfPage: o.url,
    inLanguage: "en",
    image: abs(`/images/${t.id}/og.jpg`),
    ...(o.section ? { articleSection: o.section } : {}),
    about: { "@type": "SoftwareApplication", "@id": appId(t.id), name: t.data.title, url: abs(`/${t.id}/`) },
    isPartOf: { "@id": WEBSITE_ID() },
    publisher: { "@id": ORG_ID() },
    ...(o.sourceUrl ? { isBasedOn: o.sourceUrl } : {}),
  };
}

/** Wrap nodes in one schema.org graph. */
export const graph = (nodes: object[]) => ({ "@context": "https://schema.org", "@graph": nodes });


// Doc page <title>: `{Doc} · {Product} docs | Pyrlyn`; over 60 chars drop the brand, then shorten the doc title.
// ru/uk translations stay English for now but keep a language mark so titles remain unique.
export function documentationTitle(title: string, product: string, file: string | null): string {
  const language = file?.match(/^(ru|uk)\//)?.[1];
  const tail = ` · ${product} docs${language ? ` (${language})` : ""}`;
  const doc = plain(title);
  const full = `${doc}${tail} | ${BRAND}`;
  if (full.length <= 60) return full;
  if (doc.length + tail.length <= 60) return `${doc}${tail}`;
  return `${wordClip(doc, 60 - tail.length)}${tail}`;
}

/** Doc page meta description: the doc's summary (front matter / first paragraph), else a template; ≤155 chars. */
// A summary shared with sibling pages, or too short to describe the page, is prefixed with the doc title.
export function documentationDescription(title: string, product: string, summary: string, tagline = "", shared = false): string {
  const own = plain(summary);
  const fallback = `${plain(title)} in the ${product} docs: ${tagline.replace(/\.$/, "")}.`;
  const text = !own ? fallback : shared ? `${plain(title)}: ${own}` : own.length < 50 ? `${own} ${fallback}` : own;
  return wordClip(text, 155);
}
