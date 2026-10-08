import { existsSync, readFileSync } from "node:fs";
import { registerHooks } from "node:module";
import { fileURLToPath } from "node:url";

const EXTENSIONS = [".ts", ".tsx", ".mjs", ".js", ".json"];

// Astro rewrites `import.meta.env` and `import.meta.glob` at build time. Node does not,
// and several lib modules import each other without a file extension.
export function installLandingHooks() {
  if (globalThis.__landingHooks) return;
  globalThis.__landingHooks = true;
  globalThis.__viteEnv ??= {
    SITE: "https://pyrlyn.github.io",
    BASE_URL: "/landing/",
  };
  globalThis.__viteGlob ??= () => ({});

  registerHooks({
    resolve(specifier, context, nextResolve) {
      if (specifier === "astro:content") {
        return {
          url: new URL("./stubs/astro-content.mjs", import.meta.url).href,
          shortCircuit: true,
        };
      }
      const parent = context.parentURL ?? "";
      if (
        parent.includes("/src/") &&
        (specifier.startsWith("./") || specifier.startsWith("../")) &&
        !/\.[a-z0-9]+$/i.test(specifier)
      ) {
        const base = new URL(specifier, parent);
        for (const ext of EXTENSIONS) {
          const url = new URL(`${base.pathname}${ext}`, base);
          if (existsSync(fileURLToPath(url))) return nextResolve(url.href, context);
        }
      }
      return nextResolve(specifier, context);
    },
    load(url, context, nextLoad) {
      // Vite accepts `import x from "./file.json"` without an import attribute. Node rejects that,
      // so the test loader turns those JSON files into a default export itself.
      if (url.includes("/src/") && url.endsWith(".json")) {
        const source = `export default ${readFileSync(fileURLToPath(url), "utf8")}`;
        return { format: "module", source, shortCircuit: true };
      }
      // Node's default TypeScript loader does not hand `source` back to a wrapping
      // hook, and a missing source is rejected. Read the file and let Node strip types.
      if (url.includes("/src/") && url.endsWith(".ts")) {
        const source = readFileSync(fileURLToPath(url), "utf8")
          .replaceAll("import.meta.env", "globalThis.__viteEnv")
          .replaceAll("import.meta.glob", "globalThis.__viteGlob");
        return { format: "module-typescript", source, shortCircuit: true };
      }
      return nextLoad(url, context);
    },
  });
}
