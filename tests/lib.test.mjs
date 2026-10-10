import assert from "node:assert/strict";
import test from "node:test";

import { installLandingHooks } from "./hooks.mjs";

installLandingHooks();

const htmlP = import("../src/lib/html.ts");
const sectionsP = import("../src/lib/sections.ts");
const siteP = import("../src/lib/site.ts");
const seoP = import("../src/lib/seo.ts");
const catalogP = import("../src/lib/catalog.ts");
const docsP = import("../src/lib/docs.ts");

function tool(id, data) {
  return { id, data };
}

test("safeHtml keeps allowlisted tags and escapes placeholders", async () => {
  const { safeHtml, safeHref } = await htmlP;
  assert.equal(safeHtml("use <name> here"), "use &lt;name&gt; here");
  assert.equal(safeHtml("a<br>b"), "a<br>b");
  assert.equal(safeHtml("<details open>x</details>"), "<details open>x</details>");
  // A closing tag has no attributes, so an allowlisted closer stays even when the opener is escaped for carrying a class.
  assert.equal(safeHtml('<code class="x">y</code>'), "&lt;code class=&quot;x&quot;&gt;y</code>");
  assert.equal(safeHtml("<!-- secret -->visible"), "visible");
  assert.equal(safeHref("https://github.com/pyrlyn/rtok"), "https://github.com/pyrlyn/rtok");
  assert.equal(safeHref("mailto:listepo@gmail.com"), "mailto:listepo@gmail.com");
  assert.equal(safeHref("javascript:alert(1)"), "#");
  assert.equal(safeHref("java\nscript:alert(1)"), "#");
  assert.equal(safeHref("/rtok/"), "/rtok/");
});

test("section helpers split headings, features, usage, and links", async () => {
  const { codeBlock, parseFeatures, parseLinks, parseUsage, splitSections } = await sectionsP;
  const body = "intro\n\n## Install\n\nRun it.\n\n## Usage\n\nketch install pyrlyn/rtok";
  assert.deepEqual(Object.keys(splitSections(body)), ["install", "usage"]);
  assert.match(splitSections(body).install, /Run it/);
  const features = parseFeatures("- **Hooks.** Claude Code hooks\n- plain item");
  assert.equal(features[0].name, "Hooks");
  assert.match(features[0].html, /Claude Code hooks/);
  assert.equal(features[1].name, "plain item");
  const usage = parseUsage("Install the binary.\n\n```bash\nketch install pyrlyn/rtok\n```");
  assert.equal(usage.length, 1);
  assert.match(usage[0].caption, /Install the binary/);
  assert.equal(usage[0].lang, "bash");
  assert.match(usage[0].code, /ketch install/);
  const links = parseLinks("- Repo: https://github.com/pyrlyn/rtok\n- License: https://example.com/license\n- [Docs](https://github.com/pyrlyn/rtok/tree/main/docs)");
  assert.deepEqual(links, [
    { label: "Repo", href: "https://github.com/pyrlyn/rtok" },
    { label: "Docs", href: "https://github.com/pyrlyn/rtok/tree/main/docs" },
  ]);
  const block = codeBlock("echo hi # note\n<file>", "bash", "install");
  assert.match(block, /tok-c/);
  assert.match(block, /\$ /);
  assert.match(block, /&lt;file&gt;/);
  assert.match(block, /data-copy="echo hi # note\n&lt;file&gt;"/);
});

test("internal paths pick up the pages base", async () => {
  const { BRAND, PRO, PRO_FALLBACK, THEMES, u } = await siteP;
  assert.equal(BRAND, "listepo tools");
  assert.equal(u(), "/landing/");
  assert.equal(u("/rtok/"), "/landing/rtok/");
  assert.equal(u("cox"), "/landing/cox");
  assert.equal(PRO.rtok.price, "$9 / mo");
  assert.equal(PRO_FALLBACK.price, "$9 / mo");
  assert.equal(THEMES.rtok.accent, "#5CE1FF");
  assert.equal(THEMES.ketch.bg, "#06100F");
});

test("seo text stays inside the meta limits and omits pro prices", async () => {
  const { abs, breadcrumbs, clip, firstSentence, graph, operatingSystems, organization, plain, productDescription, productTitle, softwareApplication } = await seoP;
  assert.equal(plain("See [rtok](https://github.com/pyrlyn/rtok) and **bold**."), "See rtok and bold.");
  assert.equal(plain("keep &lt;id&gt;"), "keep <id>");
  assert.equal(firstSentence("Hello world. More text."), "Hello world.");
  assert.equal(clip("Short line.", 160), "Short line.");
  const cut = clip(`${"A".repeat(90)}. ${"B".repeat(80)}`, 160);
  assert.equal(cut, `${"A".repeat(90)}.`);
  assert.ok(clip("word ".repeat(40), 160).endsWith("…"));

  const short = tool("rtok", { title: "rtok", tagline: "Cut the context.", version: "1.0.0", repo: "https://github.com/pyrlyn/rtok" });
  assert.equal(productTitle(short), "rtok — Cut the context · listepo tools");
  const longLead = `${"Long lead ".repeat(6)}with the rest of the sentence that should not be the title.`;
  const long = tool("rtok", { ...short.data, tagline: longLead });
  const title = productTitle(long);
  assert.equal(title.includes("with the rest"), false);
  assert.equal(title.includes("listepo tools"), false);
  assert.equal(productDescription(short, "First sentence here. Second."), "Cut the context. First sentence here.");
  assert.equal(operatingSystems(""), "macOS, Linux");
  assert.equal(operatingSystems("Windows PowerShell and Linux"), "Linux, Windows");

  const app = softwareApplication(short, { description: "Cut the context.", installMd: "macOS" });
  assert.equal(app.offers.price, "0");
  assert.equal(app.offers.priceCurrency, "USD");
  assert.equal("aggregateRating" in app, false);
  assert.equal(app.operatingSystem, "macOS");
  assert.equal(organization().name, "listepo");
  assert.equal(abs("/rtok/"), "https://pyrlyn.dev/landing/rtok/");
  assert.deepEqual(breadcrumbs([{ name: "Home", url: abs("/") }]).itemListElement[0].position, 1);
  assert.equal(graph([organization()])["@context"], "https://schema.org");
});

test("catalog rank, pro price, accents, and the card install line", async () => {
  globalThis.__astroCollection = async () => [
    tool("ketch", { title: "ketch", featured: false, accent: "#3DDCB0", install: "curl ketch" }),
    tool("aaa", { title: "Aaa", order: 5, accent: "#111111", install: "curl aaa", install_alternatives: ["brew aaa", "ketch install aaa"] }),
    tool("rtok", { title: "rtok", featured: true, accent: "#5CE1FF", accent2: "#FF6B4A", install: "curl rtok", pro_price: "$12 / mo" }),
  ];
  const { accentsFor, cardInstall, featuredTool, getTools, proFor } = await catalogP;
  const tools = await getTools();
  assert.deepEqual(tools.map((item) => item.id), ["rtok", "ketch", "aaa"]);
  assert.equal(featuredTool(tools).id, "rtok");
  assert.equal(featuredTool([tools[1]]).id, "ketch");
  assert.deepEqual(proFor(tools[0]), { price: "$12 / mo", features: ["Team-wide ledger sync", "Hosted savings dashboard", "Priority support"] });
  assert.equal(proFor(tool("other", { title: "Other", accent: "#000", install: "x" })).price, "$9 / mo");
  assert.equal(accentsFor(tools[0]).accent2, "#FF6B4A");
  assert.equal(accentsFor(tool("other", { title: "Other", accent: "#abcdef", install: "x" })).accent2, "#abcdef");
  assert.equal(cardInstall(tools[2]), "ketch install aaa");
  assert.equal(cardInstall(tools[1]), "curl ketch");
});

test("doc slugs, heading ids, and code blocks", async () => {
  const { docCodeBlock, docsHref, makeSlugger, slugOf } = await docsP;
  assert.equal(slugOf("design/loop.md"), "design-loop");
  assert.equal(slugOf("COMMANDS.md"), "commands");
  const slug = makeSlugger();
  assert.equal(slug("Hello World"), "hello-world");
  assert.equal(slug("Hello World"), "hello-world-1");
  assert.equal(slug("Use `<NAME>` here"), "use-name-here");
  assert.equal(slug("Hello <span>World</span>"), "hello-world-2");
  const shell = docCodeBlock("echo hi # note\n# full line", "bash");
  assert.match(shell, /data-lang="bash"/);
  assert.match(shell, /tok-c/);
  assert.equal(shell.includes("$ "), false);
  const rust = docCodeBlock('println!("hi")', "rust");
  assert.match(rust, /data-lang="rust"/);
  assert.match(rust, /&quot;/);
  assert.equal(docsHref("rtok"), "/landing/rtok/docs/");
  assert.equal(docsHref("rtok", "commands"), "/landing/rtok/docs/commands/");
});
