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
    assert.doesNotMatch(src, /<form/i, p);
  }
  assert.match(read("src/pages/team.astro"), /https:\/\/github\.com\/listepo/);
  assert.match(read("src/pages/support.astro"), /https:\/\/github\.com\/pyrlyn\/\.github\/issues/);
});
