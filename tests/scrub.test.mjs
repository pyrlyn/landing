import assert from "node:assert/strict";
import test from "node:test";
import { installLandingHooks } from "./hooks.mjs";
installLandingHooks();
const { scrubProgress } = await import("../src/lib/client/scrub.ts");

test("scrubProgress clamps and is linear between start and end", () => {
  // vh 1000, box 200 high: a = 900, b = 250 - 200 = 50
  assert.equal(scrubProgress(950, 200, 1000), 0);
  assert.equal(scrubProgress(900, 200, 1000), 0);
  assert.equal(scrubProgress(50, 200, 1000), 1);
  assert.equal(scrubProgress(-500, 200, 1000), 1);
  assert.equal(scrubProgress(475, 200, 1000), 0.5);
  assert.equal(scrubProgress(500, 0, 1000, 1, 0), 0.5);
});
