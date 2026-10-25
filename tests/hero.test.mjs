import assert from "node:assert/strict";
import test from "node:test";
import { installLandingHooks } from "./hooks.mjs";
installLandingHooks();
const { lerpTilt } = await import("../src/lib/client/hero.ts");

test("lerpTilt moves 8% toward the target and stops settling when close", () => {
  const r = lerpTilt(0, 0, 0.5, -0.5);
  assert.ok(Math.abs(r.cx - 0.04) < 1e-12 && Math.abs(r.cy + 0.04) < 1e-12 && r.settling);
  assert.equal(lerpTilt(0.1, 0.1, 0.1002, 0.1).settling, false);
});
