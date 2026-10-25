import { test } from "node:test";
import assert from "node:assert/strict";
import { readFileSync } from "node:fs";

const read = (p) => readFileSync(new URL(`../${p}`, import.meta.url), "utf8");

test("footer links to the team and support pages", () => {
  const base = read("src/layouts/Base.astro");
  assert.match(base, /href=\{u\("\/team"\)\}>Our team</);
  assert.match(base, /href=\{u\("\/support"\)\}>Support</);
});

test("team and support pages use only public GitHub links, no email", () => {
  for (const p of ["src/pages/team.astro", "src/pages/support.astro"]) {
    const src = read(p);
    assert.doesNotMatch(src, /mailto:|@[a-z0-9-]+\.[a-z]{2,}/i, p);
  }
  assert.match(read("src/pages/team.astro"), /https:\/\/github\.com\/listepo/);
  assert.match(read("src/pages/support.astro"), /https:\/\/github\.com\/pyrlyn\/support\/issues/);
});

test("support form opens a prefilled GitHub issue (GET title/body), no backend", () => {
  const src = read("src/pages/support.astro");
  assert.match(src, /action="https:\/\/github\.com\/pyrlyn\/support\/issues\/new" method="get"/);
  assert.match(src, /name="title"/);
  assert.match(src, /name="body"/);
});

test("team tools: eight entries, each with an icon path or a monogram", async () => {
  const src = read("src/data/team-tools.ts");
  for (const n of ["Kimi", "OpenAI / ChatGPT", "Grok", "Claude", "Gemini", "Cursor", "GitHub Copilot", "DeepSeek"]) assert.ok(src.includes(`"${n}"`), n);
  assert.match(read("src/pages/team.astro"), /public\/team\/ivan\.jpg/);
});
