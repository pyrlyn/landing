# Toolchain

| Program | How to install | Why here | Source |
| --- | --- | --- | --- |
| node | mise (`mise.toml` pins 26.12.1); CI and Docker use Node 22 | Run Astro, the doc and SEO checks, and the tests | https://nodejs.org/ |
| npm | bundled with node | Install packages and run scripts | https://github.com/npm/cli |
| jactionlint | mise (`mise.toml` pins `github:jdx/jactionlint` 2.0.2) | Lint the workflows (the CI `jactionlint` job, the pre-push hook and `mise x -- jactionlint`) | https://github.com/jdx/jactionlint |
| hk | mise (`mise.toml` pins 2.5.0) | Runs jactionlint as a pre-push hook (`hk.pkl`) | https://github.com/jdx/hk |
| shellcheck | mise (`mise.toml` pins 0.11.0) | Checks `run:` scripts inside jactionlint | https://github.com/koalaman/shellcheck |
| agent-browser | mise (`mise.toml` pins 0.39.2) | Browser automation for checking the storefront | https://www.npmjs.com/package/agent-browser |
| Docker | any engine (Docker Engine, OrbStack, Colima). Optional | `docker build` / `docker run` of the `Dockerfile` (images `node:22-bookworm-slim`, `nginx:1.30-alpine`) | https://docs.docker.com/ |

| Package | Where | Source | Why here |
| --- | --- | --- | --- |
| astro | local | https://github.com/withastro/astro | Static site (Astro 7; Node >=22.12) |
| @astrojs/sitemap | local | https://github.com/withastro/astro | Sitemap integration |
| @astrojs/preact, preact | local | https://github.com/preactjs/preact | Islands (React API via preact/compat); served from jsDelivr in the browser |
| typescript | local | https://github.com/microsoft/TypeScript | Types for islands and `src/lib` |
| tailwindcss | local | https://github.com/tailwindlabs/tailwindcss | Utilities |
| @tailwindcss/vite | local | https://github.com/tailwindlabs/tailwindcss | Tailwind Vite plugin |
| marked | local | https://github.com/markedjs/marked | Render synced Markdown |
| Satoshi (variable woff2) | Fontshare CDN | https://www.fontshare.com/fonts/satoshi (ITF FFL) | UI font, loaded from cdn.fontshare.com (preloaded `@font-face`) |
| @pyrlyn/sdk | local | https://github.com/pyrlyn/backend/tree/main/packages/sdk | Backend client for Pro prices and the wishlist (persisted GraphQL operations). `package.json` installs `vendor/pyrlyn-sdk-0.1.0.tgz` until it is on npm |
| @pyrlyn/brand | local | https://github.com/pyrlyn/brand | Tokens, components, and logos. `package.json` installs `github:pyrlyn/brand#v0.5.0` |

`art/` can shell out to other tools when someone regenerates images. The site build and the tests do not.
