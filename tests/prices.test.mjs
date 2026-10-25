import assert from "node:assert/strict";
import { createServer } from "node:http";
import test from "node:test";

import { SdkError } from "@pyrlyn/sdk";
import { installLandingHooks } from "./hooks.mjs";

installLandingHooks();

const PROJECTS = {
  data: {
    projects: [
      { slug: "cox", price: null },
      { slug: "rtok", price: { amount: 900, currency: "USD", interval: "MONTH" } },
    ],
  },
};

// A stand-in for the backend: it answers the persisted Projects operation and records what it got.
const requests = [];
const server = createServer((req, res) => {
  let body = "";
  req.on("data", (chunk) => (body += chunk));
  req.on("end", () => {
    requests.push({ method: req.method, body: JSON.parse(body) });
    res.setHeader("content-type", "application/json");
    res.end(JSON.stringify(PROJECTS));
  });
});
await new Promise((resolve) => server.listen(0, "127.0.0.1", resolve));
test.after(() => server.close());
// src/lib/api.ts reads API_URL once, when the build first imports it.
process.env.API_URL = `http://127.0.0.1:${server.address().port}/graphql`;

const pricesP = import("../src/lib/prices.ts");
const catalogP = import("../src/lib/catalog.ts");

test("formatPrice reads minor units by currency", async () => {
  const { formatPrice } = await pricesP;
  assert.equal(formatPrice({ amount: 900, currency: "USD", interval: "MONTH" }), "$9 / mo");
  assert.equal(formatPrice({ amount: 1250, currency: "EUR", interval: "YEAR" }), "€12.50 / yr");
  assert.equal(formatPrice({ amount: 1200, currency: "JPY", interval: "MONTH" }), "¥1,200 / mo");
});

test("the build takes the API's prices and wishlist products, placeholders for the rest", async () => {
  const { proFor, hasWishlist } = await catalogP;
  const tool = (id, data = {}) => ({ id, data });
  assert.equal(proFor(tool("rtok", { pro_price: "$12 / mo" })).price, "$9 / mo");
  // cox is known to the API but sells no plan yet, so its placeholder stays.
  assert.equal(proFor(tool("cox")).price, "$0 / mo");
  assert.equal(proFor(tool("ketch", { pro_price: "$5 / mo" })).price, "$5 / mo");
  assert.equal(hasWishlist(tool("cox")), true);
  assert.equal(hasWishlist(tool("ketch")), false);
  // Only the persisted query's hash goes over the wire, never a query text.
  assert.equal(requests[0].method, "POST");
  assert.equal(requests[0].body.query, undefined);
  assert.match(requests[0].body.extensions.persistedQuery.sha256Hash, /^[0-9a-f]{64}$/);
});

test("fetchPrices maps every project and passes a custom fetch", async () => {
  const { fetchPrices } = await pricesP;
  let seen;
  const prices = await fetchPrices("https://api.example.test/graphql", async (input) => {
    seen = String(input);
    return new Response(JSON.stringify(PROJECTS), { headers: { "content-type": "application/json" } });
  });
  assert.equal(seen, "https://api.example.test/graphql");
  assert.deepEqual([...prices], [["cox", null], ["rtok", "$9 / mo"]]);
});

test("wishlistError says what the visitor can do", async () => {
  const { wishlistError } = await pricesP;
  const sdk = (status, code) => new SdkError("x", status, [{ message: "x", extensions: { code } }]);
  assert.equal(wishlistError(sdk(200, "BAD_USER_INPUT")), "Enter a valid email address.");
  assert.equal(wishlistError(sdk(429, "RATE_LIMITED")), "Too many requests. Try again in a minute.");
  assert.match(wishlistError(sdk(200, "SERVICE_UNAVAILABLE")), /cannot be reached/);
  assert.match(wishlistError(new TypeError("Failed to fetch")), /cannot be reached/);
});
