import assert from "node:assert/strict";
import test from "node:test";

import { COX_SCENES, DEMOS, FLOWS, demoFromUsage } from "../src/data/showcase.ts";
import { OVERVIEWS } from "../src/data/docs-overview.ts";

test("demoFromUsage keeps a trailing comment off the command", () => {
  const demo = demoFromUsage("cox", [{ code: "cox doctor # green\ncox stats\n" }]);
  assert.equal(demo.title, "cox — zsh");
  assert.deepEqual(demo.steps, [
    { cmd: "cox doctor", note: "# green" },
    { cmd: "cox stats" },
  ]);
  const capped = demoFromUsage("rtok", [{ code: Array.from({ length: 8 }, (_, i) => `cmd ${i}`).join("\n") }]);
  assert.equal(capped.steps.length, 6);
});

test("showcase demos and flows name a command or a node", () => {
  for (const [id, demo] of Object.entries(DEMOS)) {
    assert.ok(demo.title.includes(id));
    assert.ok(demo.steps.length >= 1);
    for (const step of demo.steps) assert.ok(step.cmd);
  }
  for (const flow of Object.values(FLOWS)) {
    assert.ok(flow.nodes.length >= 2);
    for (const node of flow.nodes) assert.ok(node.label && node.detail);
  }
  const ids = COX_SCENES.map((scene) => scene.id);
  assert.equal(new Set(ids).size, ids.length);
  for (const scene of COX_SCENES) assert.ok(scene.demo.steps.length >= 1);
});

test("each docs overview has commands, steps, and a source note", () => {
  for (const id of ["rtok", "cox", "ketch"]) {
    const page = OVERVIEWS[id];
    assert.ok(page);
    assert.ok(page.strip.length >= 1);
    assert.ok(page.steps.length >= 1);
    assert.ok(page.faq.length >= 1);
    assert.ok(page.next.length >= 1);
    assert.ok(page.from.length >= 1);
    assert.ok(["note", "warning", "tip"].includes(page.notice.kind));
    for (const row of page.table.rows) assert.equal(row.length, 2);
  }
});
