import assert from "node:assert/strict";
import test from "node:test";

import { installLandingHooks } from "./hooks.mjs";

installLandingHooks();

const { normalizeNames, normalizeMdInline, PRODUCT_NAMES } = await import("../src/lib/names.ts");
const { renderMd, renderInline } = await import("../src/lib/sections.ts");
const { projectSchema } = await import("../src/lib/project-schema.ts");

test("covers all 12 products", () => {
  assert.equal(Object.keys(PRODUCT_NAMES).length, 12);
});

test("capitalizes product names in prose", () => {
  assert.equal(normalizeNames("rtok cuts tokens; ketch is a package manager."), "Rtok cuts tokens; Ketch is a package manager.");
  assert.equal(normalizeNames("Does cox break the cache? Not with runa, swarfr or bindsmith."), "Does Cox break the cache? Not with Runa, Swarfr or Bindsmith.");
  assert.equal(normalizeNames("rtok's ledger"), "Rtok's ledger");
  assert.equal(normalizeNames("Rtok and Stator"), "Rtok and Stator");
});

test("leaves CLI commands lowercase", () => {
  for (const s of ["rtok install", "cox run --x", "ketch self uninstall", "rtok --version", "run rtok", "$ ketch", "the weft command", "rtok stats"]) {
    assert.equal(normalizeNames(s), s, s);
  }
  assert.equal(normalizeNames("Then run rtok install to finish."), "Then run rtok install to finish.");
});

test("leaves paths, URLs, packages and identifiers lowercase", () => {
  for (const s of ["https://github.com/pyrlyn/rtok", "pyrlyn/rtok", "rtok-cli", "ketch.lock", "bindsmith.yaml", "~/.ketch", "rtok/comparison/README.md", "@pyrlyn/cox", "rtok_core"]) {
    assert.equal(normalizeNames(s), s, s);
  }
});

test("Markdown: inline code, code blocks and links stay as written", () => {
  assert.equal(normalizeMdInline("Use `ketch` with [rtok](https://github.com/pyrlyn/rtok)"), "Use `ketch` with [Rtok](https://github.com/pyrlyn/rtok)");
  const inline = renderInline("`ketch` installs rtok");
  assert.match(inline, /<code>ketch<\/code> installs Rtok/);
  const html = renderMd("rtok is fast.\n\n```bash\nrtok install\ncox run --x\n```\n\nSee [ketch docs](https://github.com/pyrlyn/ketch).");
  assert.match(html, /<p>Rtok is fast\.<\/p>/);
  assert.match(html, /rtok install/);
  assert.match(html, /cox run --x/);
  assert.match(html, /href="https:\/\/github\.com\/pyrlyn\/ketch"/);
  assert.match(html, />Ketch docs</);
  assert.doesNotMatch(html, /Rtok install|Cox run/);
});

test("project schema: display title capitalized, rawTitle kept for logo marks", () => {
  const base = { title: "rtok", tagline: "rtok cuts tokens.", repo: "https://github.com/pyrlyn/rtok", install: "rtok install", version: "1.0.0", accent: "#5CE1FF" };
  const d = projectSchema.parse(base);
  assert.equal(d.title, "Rtok");
  assert.equal(d.rawTitle, "rtok");
  assert.equal(d.tagline, "Rtok cuts tokens.");
  assert.equal(d.install, "rtok install");
});
