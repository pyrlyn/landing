import assert from "node:assert/strict";
import { readFileSync } from "node:fs";
import test from "node:test";

test("public/favicon.ico is a real ICO for crawlers that request it directly", () => {
  const ico = readFileSync(new URL("../public/favicon.ico", import.meta.url));
  assert.equal(ico.readUInt16LE(0), 0); // reserved
  assert.equal(ico.readUInt16LE(2), 1); // type: icon
  assert.ok(ico.readUInt16LE(4) >= 1); // at least one image
});
