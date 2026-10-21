import assert from "node:assert/strict";
import test from "node:test";

import { installLandingHooks } from "./hooks.mjs";

installLandingHooks();

const { projectSchema } = await import("../src/lib/project-schema.ts");

const base = {
  title: "demo",
  tagline: "A demo tool",
  repo: "https://github.com/pyrlyn/demo",
  install: "ketch install pyrlyn/demo",
  version: "1.2.3",
  accent: "#5CE1FF",
};

test("homepage accepts http(s) and refuses script-capable schemes", () => {
  assert.ok(projectSchema.safeParse({ ...base, homepage: "https://demo.example/docs" }).success);
  assert.ok(projectSchema.safeParse(base).success);
  for (const bad of ["javascript:alert(1)", "JavaScript:alert(1)", "data:text/html,<script>x</script>"]) {
    assert.equal(projectSchema.safeParse({ ...base, homepage: bad }).success, false, bad);
  }
});
