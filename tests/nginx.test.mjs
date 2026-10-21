import assert from "node:assert/strict";
import { readFileSync } from "node:fs";
import test from "node:test";

const conf = readFileSync(new URL("../docker/nginx.conf", import.meta.url), "utf8");
const dockerfile = readFileSync(new URL("../Dockerfile", import.meta.url), "utf8");
const headers = readFileSync(new URL("../docker/security-headers.conf", import.meta.url), "utf8");
const INCLUDE = "include /etc/nginx/security-headers.conf;";

// Innermost `{ ... }` blocks with their header line (`server`, `location /_astro/`, ...).
function blocks(text) {
  const out = [];
  const stack = [];
  let head = 0;
  for (let i = 0; i < text.length; i++) {
    if (text[i] === "{") {
      stack.push({ name: text.slice(head, i).trim().split("\n").pop().trim(), start: i + 1 });
      head = i + 1;
    } else if (text[i] === "}") {
      const b = stack.pop();
      out.push({ name: b.name, body: text.slice(b.start, i) });
      head = i + 1;
    } else if (text[i] === ";") head = i + 1;
  }
  return out;
}

test("every nginx block that sets headers also carries the security headers", () => {
  const server = blocks(conf).find((b) => b.name.startsWith("server"));
  assert.ok(server.body.includes(INCLUDE), "server block");
  for (const b of blocks(conf)) {
    if (b.name.startsWith("location") && /\badd_header\b/.test(b.body)) assert.ok(b.body.includes(INCLUDE), b.name);
  }
  assert.match(conf, /server_tokens off;/);
  assert.ok(dockerfile.includes("COPY docker/security-headers.conf /etc/nginx/security-headers.conf"));
  for (const h of ["X-Content-Type-Options", "Referrer-Policy", "X-Frame-Options", "Content-Security-Policy"]) {
    assert.match(headers, new RegExp(`add_header ${h} .* always;`));
  }
});
