// Product names are proper nouns in prose ("Rtok", "Ketch") but stay lowercase as commands,
// package/crate names, URLs, slugs, paths and logo marks. Synced Markdown (content/projects,
// content/docs) is overwritten upstream, so the site normalizes at build time instead of
// editing those files: only Markdown text nodes and front-matter display strings pass through
// here. Code (fenced and inline), raw HTML, link targets, heading ids and logos never do.
import type { Token } from "marked";

/** Display spelling for every product slug (all 12 in content/projects/*.md). */
export const PRODUCT_NAMES: Record<string, string> = {
  aulo: "Aulo", bindsmith: "Bindsmith", cox: "Cox", kerf: "Kerf", ketch: "Ketch", mailune: "Mailune",
  rtok: "Rtok", runa: "Runa", scull: "Scull", stator: "Stator", swarfr: "Swarfr", weft: "Weft",
};

/** Words that make "<name> <word>" a CLI command, e.g. "rtok install", "cox run". Merged with
 * subcommands harvested from code blocks in the synced content (see harvestCommands). */
export const DEFAULT_COMMANDS = [
  "acp", "add", "agents", "bench", "build", "changelog", "chat", "check", "completions", "config", "doctor",
  "expand", "explain", "fmt", "graph", "help", "history", "hook", "hooks", "import", "info", "init",
  "install", "list", "lock", "mcp", "media", "otel", "outdated", "path", "plugin", "plugins", "process",
  "proxy", "registry", "releases", "remove", "rollback", "run", "search", "self", "serve",
  "sessions", "stats", "sync", "task", "tui", "uninstall", "update", "upgrade", "validate", "web",
];
// Never treat ordinary English as a subcommand, even if a code block happens to use it.
const PROSE_WORDS = new Set(["is", "and", "or", "the", "a", "an", "to", "in", "on", "for", "with", "vs", "does", "has", "can", "will", "was", "runs", "reads", "needs", "checks", "already", "because", "built", "beat", "why", "also", "keeps", "uses", "only", "itself", "docs", "documentation", "pro", "website", "site", "readme", "saved", "installed", "installs", "automatically", "reduces", "cannot", "without", "by", "usage", "configuration"]);

const ALT = Object.keys(PRODUCT_NAMES).join("|");
// Not part of a path, URL, package (rtok-cli, @x/rtok, rtok.md), identifier or shell prompt.
const NAME_RE = new RegExp(`(?<![\\w./@\`$~:\\\\-])(${ALT})(?![\\w/@\\\\-]|\\.[A-Za-z0-9])`, "g");
const CMD_BEFORE = /(?:\b(?:run|type|call|invoke|execute)\s+|\$\s*)$/i;

export function harvestCommands(code: string, into = new Set<string>()): Set<string> {
  const re = new RegExp(`(?:^|[\\s|;&(])(?:${ALT})\\s+([a-z][\\w-]*)`, "gm");
  for (const m of code.matchAll(re)) if (!PROSE_WORDS.has(m[1])) into.add(m[1]);
  return into;
}

let commands = new Set(DEFAULT_COMMANDS);
export const addCommands = (words: Iterable<string>) => { for (const w of words) if (!PROSE_WORDS.has(w)) commands.add(w); };

/** Capitalize product names in a plain prose string; command mentions stay as they are. */
export function normalizeNames(text: string, cmds: Set<string> = commands): string {
  if (!text) return text;
  return text.replace(NAME_RE, (name, _n, offset: number) => {
    const after = text.slice(offset + name.length);
    const next = after.match(/^ +(--?[A-Za-z]|[a-z][\w-]*)/);
    if (next && (next[1].startsWith("-") || cmds.has(next[1]))) return name;
    // "the weft command", "a ketch binary": the executable itself, not the product.
    if (/^ +(command|binary|executable|subcommand)s?\b/.test(after)) return name;
    if (CMD_BEFORE.test(text.slice(0, offset))) return name;
    return PRODUCT_NAMES[name];
  });
}

/** marked walkTokens hook: rewrite leaf text tokens only (code, codespan, html, links' href untouched). */
export function normalizeToken(token: Token): void {
  if (token.type === "text" && !("tokens" in token && token.tokens?.length)) {
    token.text = normalizeNames(token.text);
  }
}

/** Normalize a raw Markdown inline string, leaving `code spans` and [link](targets) as written. */
export function normalizeMdInline(src: string): string {
  return src
    .split(/(`[^`]*`|\]\([^)]*\)|<[^>]+>)/)
    .map((s, i) => (i % 2 ? s : normalizeNames(s)))
    .join("");
}

/** Collect "<name> <subcommand>" words from fenced code and code spans of raw Markdown sources. */
export function harvestFromMarkdown(sources: Iterable<string>): void {
  const found = new Set<string>();
  for (const raw of sources) {
    for (const m of raw.matchAll(/```[^\n]*\n([\s\S]*?)```|`([^`\n]+)`/g)) harvestCommands(m[1] ?? m[2], found);
  }
  addCommands(found);
}

// Subcommands used anywhere in the synced copy (fenced code and code spans), so a future
// "ketch frobnicate" in prose is recognized as a command without editing DEFAULT_COMMANDS.
harvestFromMarkdown(Object.values(import.meta.glob<string>(["/content/projects/**/*.md", "/content/docs/*/**/*.md"], { query: "?raw", import: "default", eager: true })));
