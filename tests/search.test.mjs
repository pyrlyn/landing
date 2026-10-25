import assert from "node:assert/strict";
import test from "node:test";
import { installLandingHooks } from "./hooks.mjs";
installLandingHooks();
const s = await import("../src/scripts/search-state.ts");
const p = await import("../src/scripts/search-provider.ts");

test("search stays open with text or focus", () => {
  assert.equal(s.shouldStayOpen({ open: true, value: "", focused: true }), true);
  assert.equal(s.shouldStayOpen({ open: true, value: "rt", focused: false }), true);
  assert.equal(s.shouldStayOpen({ open: true, value: " ", focused: false }), false);
  assert.equal(s.shouldStayOpen({ open: false, value: "rt", focused: true }), false);
});
test("Escape clears, then closes", () => {
  const a = s.onEscape({ open: true, value: "rt", focused: true });
  assert.deepEqual(a, { open: true, value: "", focused: true });
  assert.equal(s.onEscape(a).open, false);
});
test("active index wraps", () => {
  assert.equal(s.moveActive(0, -1, 3), 2);
  assert.equal(s.moveActive(2, 1, 3), 0);
  assert.equal(s.moveActive(0, 1, 0), -1);
});
test("highlight parts", () => {
  assert.deepEqual(s.highlightParts("rtok docs Rtok", "rtok").filter((x) => x.match).length, 2);
  assert.deepEqual(s.highlightParts("abc", ""), [{ text: "abc", match: false }]);
});
test("stub provider filters locally and aborts", async () => {
  const hits = await p.stubProvider("ketch", new AbortController().signal);
  assert.ok(hits.length >= 1 && hits.every((h) => /ketch/i.test(h.title)));
  const c = new AbortController();
  const pr = p.stubProvider("rtok", c.signal); c.abort();
  await assert.rejects(pr, { name: "AbortError" });
  await assert.rejects(p.stubProvider("error", new AbortController().signal));
});

test("pickProvider: algolia with keys, stub in dev, null in prod", () => {
  assert.equal(p.pickProvider({}, true), p.stubProvider);
  assert.equal(p.pickProvider({ appId: "A" }, false), null);
  assert.equal(typeof p.pickProvider({ appId: "A", searchKey: "k" }, false), "function");
  assert.notEqual(p.pickProvider({ appId: "A", searchKey: "k" }, true), p.stubProvider);
});
