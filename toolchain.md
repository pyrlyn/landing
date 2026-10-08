# Toolchain

| Program | How to install | Why here | Source |
| --- | --- | --- | --- |
| node | nodejs.org. CI requests Node 22 | Run Astro, the doc and SEO checks, and the tests | https://nodejs.org/ |
| npm | bundled with node | Install packages and run scripts | https://github.com/npm/cli |

| Package | Where | Source | Why here |
| --- | --- | --- | --- |
| astro | local | https://github.com/withastro/astro | Static site |
| @astrojs/sitemap | local | https://github.com/withastro/astro | Sitemap integration |
| @astrojs/check | local | https://github.com/withastro/astro | `astro check` |
| typescript | local | https://github.com/microsoft/TypeScript | Typecheck via `astro check` |
| tailwindcss | local | https://github.com/tailwindlabs/tailwindcss | Utilities |
| @tailwindcss/vite | local | https://github.com/tailwindlabs/tailwindcss | Tailwind Vite plugin |
| marked | local | https://github.com/markedjs/marked | Render synced Markdown |
| @fontsource-variable/inter | local | https://github.com/fontsource/fontsource | UI font |
| @pyrlyn/brand | local | https://github.com/pyrlyn/brand | Tokens, components, and logos. `package.json` installs `github:pyrlyn/brand#v0.2.0` |

`art/` can shell out to other tools when someone regenerates images. The site build and the tests do not.
