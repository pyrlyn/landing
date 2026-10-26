import assert from "node:assert/strict";
import { readdirSync, readFileSync } from "node:fs";
import test from "node:test";

import { installLandingHooks } from "./hooks.mjs";

installLandingHooks();

const { isExcluded, isServiceDoc } = await import("../src/lib/docs.ts");
const nav = JSON.parse(readFileSync(new URL("../src/data/docs-nav.json", import.meta.url), "utf8"));

const walk = (url, rel = "") =>
  readdirSync(url, { withFileTypes: true }).flatMap((e) =>
    e.isDirectory() ? walk(new URL(`${e.name}/`, url), `${rel}${e.name}/`) : [`${rel}${e.name}`],
  );

// rtok's comparison/ pages land under "More" on purpose until the
// docs nav gets comparison groups (plan.md, Audit 2026-10-19).
const DEFERRED = /^comparison\//;

test("every English product doc is placed in docs-nav.json or excluded", () => {
  const unplaced = [];
  for (const [product, cfg] of Object.entries(nav)) {
    if (product.startsWith("$")) continue;
    const listed = new Set(cfg.groups.flatMap((g) => g.items.map((i) => i.file).filter(Boolean)));
    const dir = new URL(`../content/docs/${product}/`, import.meta.url);
    let files;
    try {
      files = walk(dir);
    } catch {
      continue;
    }
    for (const f of files) {
      if (!f.endsWith(".md") || isServiceDoc(f) || DEFERRED.test(f)) continue;
      if (!listed.has(f) && !isExcluded(f, cfg.exclude)) unplaced.push(`${product}/${f}`);
    }
  }
  assert.deepEqual(unplaced, []);
});
