// Resolves the optional distDir CLI argument of the post-build check scripts.
// Only a relative path inside the repo is accepted (no "..", no absolute path); otherwise exit 1.
import path from "node:path";

export function resolveDistDir(root, arg = "dist") {
  const ok = /^[\w.-]+(?:\/[\w.-]+)*$/.test(arg) && !arg.split("/").includes("..");
  const resolved = path.resolve(root, ok ? arg : "dist");
  if (!ok || !resolved.startsWith(path.resolve(root) + path.sep)) {
    console.error(`invalid dist dir: ${arg} (must be a relative path inside the repo)`);
    process.exit(1);
  }
  return resolved;
}
