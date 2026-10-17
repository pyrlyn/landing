import assert from "node:assert/strict";
import test from "node:test";
import { readdirSync } from "node:fs";

import { CATEGORIES, PRODUCT_CATEGORY, categoryOf } from "../src/lib/site.ts";

const slugs = readdirSync(new URL("../content/projects/", import.meta.url))
  .filter((f) => f.endsWith(".md") && f !== "README.md")
  .map((f) => f.replace(/\.md$/, ""));

test("category ids are unique and ordered", () => {
  const ids = CATEGORIES.map((c) => c.id);
  assert.equal(new Set(ids).size, ids.length);
  const orders = CATEGORIES.map((c) => c.order);
  assert.equal(new Set(orders).size, orders.length);
  for (const c of CATEGORIES) assert.ok(c.label && c.description);
});

test("every product has a valid category", () => {
  assert.ok(slugs.length >= 12);
  for (const slug of slugs) assert.ok(categoryOf(slug), `${slug} has no valid category in PRODUCT_CATEGORY`);
});

test("no category entry for a removed product", () => {
  for (const slug of Object.keys(PRODUCT_CATEGORY)) assert.ok(slugs.includes(slug), `${slug} is in PRODUCT_CATEGORY but has no content/projects file`);
});

test("every category is non-empty", () => {
  for (const c of CATEGORIES) {
    assert.ok(Object.values(PRODUCT_CATEGORY).includes(c.id), `category ${c.id} has no products`);
  }
});
