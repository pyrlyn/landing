// Third-party client modules served from jsDelivr instead of our bundle (see docs/performance.md).
// Versions are pinned to the ones in package-lock.json; integrity (SRI) is computed at build time
// from the same files in node_modules, which jsDelivr serves byte-for-byte for /npm/<pkg>@<ver>/<file>.
// Disable with PERF_CDN_MODULES=0 (everything bundled again).
import { createHash } from "node:crypto";
import fs from "node:fs";
import path from "node:path";

const ROOT = process.cwd(); // the project root: npm scripts and astro build run there
const version = (pkg) => JSON.parse(fs.readFileSync(path.join(ROOT, "node_modules", pkg, "package.json"), "utf8")).version;

// bare specifier -> [package, file inside the package]
const FILES = {
  preact: ["preact", "dist/preact.mjs"],
  "preact/hooks": ["preact", "hooks/dist/hooks.mjs"],
  "preact/compat": ["preact", "compat/dist/compat.mjs"],
  "preact/jsx-runtime": ["preact", "jsx-runtime/dist/jsxRuntime.mjs"],
  "@preact/signals": ["@preact/signals", "dist/signals.module.js"],
  "@preact/signals-core": ["@preact/signals-core", "dist/signals-core.module.js"],
};
// React API names the islands import; they resolve to preact/compat (tsconfig paths + @astrojs/preact compat).
const ALIASES = { react: "preact/compat", "react-dom": "preact/compat", "react/jsx-runtime": "preact/jsx-runtime", "preact/jsx-dev-runtime": "preact/jsx-runtime" };

export const cdnModulesEnabled = process.env.PERF_CDN_MODULES !== "0";

function build() {
  const imports = {}, integrity = {};
  for (const [spec, [pkg, file]] of Object.entries(FILES)) {
    const url = `https://cdn.jsdelivr.net/npm/${pkg}@${version(pkg)}/${file}`;
    imports[spec] = url;
    integrity[url] = "sha384-" + createHash("sha384").update(fs.readFileSync(path.join(ROOT, "node_modules", pkg, file))).digest("base64");
  }
  for (const [a, t] of Object.entries(ALIASES)) imports[a] = imports[t];
  return { imports, integrity };
}

export const importMap = cdnModulesEnabled ? build() : null;
export const externalSpecifiers = new Set([...Object.keys(FILES), ...Object.keys(ALIASES)]);

/** Vite plugin: keep these specifiers as bare imports in the client build; the import map resolves them. */
export function cdnModules() {
  return {
    name: "pyrlyn-cdn-modules",
    enforce: "pre",
    apply: "build",
    resolveId(source) {
      if (!cdnModulesEnabled || this.environment?.name !== "client") return null;
      return externalSpecifiers.has(source) ? { id: source, external: true } : null;
    },
  };
}
