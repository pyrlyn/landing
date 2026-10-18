import assert from "node:assert/strict";
import test from "node:test";
import { countDocHeadings } from "../src/lib/doc-source.ts";

test("documentation counts secondary titles as rendered level-two headings", () => {
  assert.equal(countDocHeadings("# Title\n\n## Section\n\n# Additional title\n\n### Details\n"), 3);
});

test("documentation heading counts use Markdown syntax", () => {
  const raw = "# Title\n\nSetext section\n---\n\n```md\n## Example only\n```\n\n> ### Nested section\n\n#### Deep section\n";
  assert.equal(countDocHeadings(raw), 2);
});
