import assert from "node:assert/strict";
import test from "node:test";
import { installLandingHooks } from "./hooks.mjs";
installLandingHooks();
const { activeHeading } = await import("../src/lib/client/docs.ts");

test("activeHeading picks the last heading above the reading line", () => {
  const h = (top) => [{ id: "a", top }, { id: "b", top: top + 400 }, { id: "c", top: top + 800 }];
  assert.equal(activeHeading(h(500)), "a");
  assert.equal(activeHeading(h(-300)), "b");
  assert.equal(activeHeading(h(-700)), "c");
  assert.equal(activeHeading([]), undefined);
});
