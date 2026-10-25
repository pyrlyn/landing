import assert from "node:assert/strict";
import test from "node:test";

import { installLandingHooks } from "./hooks.mjs";

installLandingHooks();

const ringP = import("../src/lib/client/ring.ts");
const titles = ["rtok", "cox", "ketch", "runa", "Mailune", "swarfr", "a", "b", "c", "d", "e", "f"];

test("statusText names the active slide and its position", async () => {
  const { statusText } = await ringP;
  assert.equal(statusText(titles, 2), "ketch · 3 / 12");
  assert.equal(statusText(titles, 0), "rtok · 1 / 12");
});

test("turnTo wraps the index and turns the shortest way", async () => {
  const { turnTo } = await ringP;
  const n = titles.length, step = 360 / n;
  assert.deepEqual(turnTo({ index: 0, rot: 0 }, -1, n), { index: 11, rot: -step });
  assert.deepEqual(turnTo({ index: 11, rot: -step }, 12, n), { index: 0, rot: 0 });
  assert.deepEqual(turnTo({ index: 0, rot: 0 }, 2, n), { index: 2, rot: 2 * step });
  assert.deepEqual(turnTo({ index: 1, rot: step }, 10, n), { index: 10, rot: -2 * step });
});

test("snap rounds a drag angle to the nearest slide and keeps the index in range", async () => {
  const { snap } = await ringP;
  assert.deepEqual(snap(31, 12), { index: 1, rot: 30 });
  assert.deepEqual(snap(-44, 12), { index: 11, rot: -30 });
  assert.deepEqual(snap(370, 12), { index: 0, rot: 360 });
});

test("counter derived from state always matches the highlighted slide", async () => {
  const { turnTo, statusText } = await ringP;
  let s = { index: 0, rot: 0 };
  for (const k of [1, 2, 7, -3, 25, 0, 11]) {
    s = turnTo(s, k, titles.length);
    assert.equal(statusText(titles, s.index).split(" · ")[0], titles[s.index]);
  }
});
