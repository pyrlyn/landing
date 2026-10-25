// The post-build check scripts always read <repo>/dist. A CLI argument is no longer accepted:
// the path never comes from user input, so it cannot be steered outside the repo (Sonar S8707).
import path from "node:path";

export function resolveDistDir(root, arg) {
  if (arg !== undefined) {
    console.error(`unexpected argument: ${arg} (the scripts always check <repo>/dist)`);
    process.exit(1);
  }
  return path.join(root, "dist");
}
