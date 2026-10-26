// Downloads the Satoshi variable woff2 from Fontshare into public/fonts/ (gitignored) so the site
// serves it itself. The ITF Free Font License allows self-hosting on our own site but forbids
// putting the font files in a public repository, so the file is fetched at build time instead of
// committed. See docs/performance.md.
// Skips when the file already exists or PERF_SELF_HOST_FONT=0. On a network error it only warns:
// Base.astro then falls back to the Fontshare CDN stylesheet.
import fs from "node:fs";
import path from "node:path";

const OUT = path.resolve("public/fonts/satoshi-variable.woff2");
const CSS_URL = "https://api.fontshare.com/v2/css?f[]=satoshi@1&display=swap";

if (process.env.PERF_SELF_HOST_FONT === "0") process.exit(0);
if (fs.existsSync(OUT) && fs.statSync(OUT).size > 0) process.exit(0);
try {
  const css = await (await fetch(CSS_URL)).text();
  const m = css.match(/url\('([^']+\.woff2)'\)/);
  if (!m) throw new Error("no woff2 url in Fontshare CSS");
  const res = await fetch(m[1].startsWith("//") ? `https:${m[1]}` : m[1]);
  if (!res.ok) throw new Error(`font download: HTTP ${res.status}`);
  fs.mkdirSync(path.dirname(OUT), { recursive: true });
  fs.writeFileSync(OUT, Buffer.from(await res.arrayBuffer()));
  console.log(`fetch-font: wrote ${path.relative(process.cwd(), OUT)}`);
} catch (e) {
  console.warn(`fetch-font: ${e.message}; falling back to the Fontshare CDN`);
}
