# Pyrlyn logo

Parent brand of [github.com/pyrlyn](https://github.com/pyrlyn). Concept "Prompt": the shell prompt
`>_` reduced to a chevron and a blue cursor bar. Pyrlyn's tools are CLIs, so the parent mark is
the place they all run. Product logos stay primary; this one is for "by Pyrlyn" credits and
org-level use.

## Files

| File | Use |
|---|---|
| `pyrlyn-mark-on-light.svg` / `-1000.png` | Mark, ink chevron + `#2F6BDB` cursor, for light backgrounds |
| `pyrlyn-mark-on-dark.svg` / `-1000.png` | Mark, paper chevron + `#4C8DFF` cursor, for dark backgrounds |
| `pyrlyn-mark-mono.svg` | Mark in `currentColor` (one path): inline SVG, masks, one-colour print |
| `pyrlyn-lockup-on-light.svg` / `-2000.png` | Mark + `pyrlyn` wordmark, for light backgrounds |
| `pyrlyn-lockup-on-dark.svg` / `-2000.png` | Mark + `pyrlyn` wordmark, for dark backgrounds |
| `pyrlyn-lockup-mono.svg` | Lockup in `currentColor` (one path) |
| `pyrlyn-favicon.svg` | Favicon, heavier 16 px geometry; follows the **browser** `prefers-color-scheme`: ink chevron + `#2F6BDB` cursor on light UI, paper chevron + `#4C8DFF` on dark UI |
| `pyrlyn-favicon-16.png` / `-32.png` / `-64.png` | PNG fallbacks (can't follow the scheme): light-UI version, ink chevron + `#2F6BDB`, transparent |
| `pyrlyn-github-avatar-1000.png` | GitHub org avatar: opaque ink square, mark at 62.5% |
| `pyrlyn-apple-touch-icon.png` | 180×180 opaque ink square, paper chevron + `#4C8DFF` (iOS adds the rounding) |
| `pyrlyn-mark-on-dark-{32,64,128,256,512}.png` | Static mark on solid ink, paper chevron + `#4C8DFF`; 32/64 use the heavier geometry |
| `pyrlyn-mark-on-light-{32,64,128,256,512}.png` | Static mark on solid paper, ink chevron + `#2F6BDB`; 32/64 heavier |
| `pyrlyn-mark-blink-on-dark.svg` / `-on-light.svg` | Mark with the cursor blinking (CSS, 1.06 s, `steps(1)`, as the landing `cursor-blink`); solid under `prefers-reduced-motion` |
| `pyrlyn-mark-blink-on-dark.gif` / `.png` (APNG), same `-on-light` | 512 px looping blink on solid ink / paper |

Mark and lockup PNGs are RGBA with a transparent background: marks 1000×1000, lockups 2000 px
wide. The wordmark is outlined to paths, so no font is needed.

## Colours

| Token | Hex | Use |
|---|---|---|
| Ink | `#0C0E11` | Mark and wordmark on light backgrounds; dark background |
| Paper | `#F4F2ED` | Mark and wordmark on dark backgrounds; light background |
| Accent (on dark) | `#4C8DFF` | Cursor bar on dark backgrounds (`themes/landing` `--accent`) |
| Accent (on light) | `#2F6BDB` | Cursor bar on light backgrounds (`themes/landing` `--accent-light`) |

## Usage

- The cursor is the only accent: the landing blue in every file (amber until v0.4.0). Do not recolour the chevron. Animate the cursor only via the
  `-blink` files.
- Clear space: at least the cursor height (4 grid units, 1/8 of the mark box) on every side; the
  lockup SVGs already include 2 units.
- Minimum size: mark 16 px; lockup 16 px high.
- Themed pages: switch `on-light` / `on-dark` with the theme, use `<picture>` with
  `prefers-color-scheme`, or inline the `mono` file and set `color`.

## Geometry

32×32 grid, 2 units = 1 px at 16 px. Chevron `M4 6H10L20 16L10 26H4L14 16Z`: 45° arms, 6 units
thick horizontally, flat ends on even rows (y 6 and 26). Cursor 10×4 at (18, 22), bottom-aligned
with the chevron, so both land on whole pixels at 16 and 32 px.

Lockup: the `l` ascender sits on the chevron top and the baseline on the cursor top, so the cursor
reads as the underscore and the chevron centre matches the x-height centre. Wordmark: JetBrains
Mono (OFL) weight 600, hand-kerned for even gaps (font units, 1000 upm): py −21, yr −35, rl +12,
ly −5, yn −22. Cursor-to-wordmark gap: 7 units.

Favicon, blink and sized PNGs: `node _build/gen-mark-assets.mjs` (needs `sharp`, `ffmpeg`).
The rest: regenerate with `python3 _build/build.py [OUT_DIR]` (needs `fonttools`, `cairosvg`; point
`JBMONO_VF` at `JetBrainsMono-VariableFont_wght.ttf`).
