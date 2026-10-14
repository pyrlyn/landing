---
title: Stator
tagline: TypeScript and JavaScript to native binaries — an ahead-of-time compiler where typed code becomes unboxed machine values.
repo: https://github.com/pyrlyn/stator
install: "mise install && pnpm install --frozen-lockfile && pnpm run runtime"
install_alternatives:
  - "git clone https://github.com/pyrlyn/stator && cd stator && mise install && pnpm install --frozen-lockfile && pnpm run runtime"
version: "0.0.0"
status: "Research compiler · no release yet"
accent: "#5B7CFF"
accent2: "#9AA3B5"
accentLight: "#3352D6"
order: 11
---

<!-- Website copy for the pyrlyn project site, written for pyrlyn/landing from README.md and
docs/HOW-IT-WORKS.md at pyrlyn/stator@2f94229 (checked 2026-10-14). No tag and no release; the
README calls it "a research compiler under active construction", hence `status` and version 0.0.0.
accent and accent2 are the indigo and fg-muted of docs/brand (DESIGN.md, dark). -->

## Overview

Stator is an ahead-of-time compiler from TypeScript and JavaScript to native binaries.

Typed code compiles to unboxed machine values. Untyped code compiles too, through a dynamic
representation (NaN-boxed values, shape tables, inline caches), instead of being rejected or
heroically analyzed. One pipeline; the mode is a policy layer on top.

Stator is a research compiler under active construction. It is not an npm-ecosystem drop-in, and
it is not a production JS runtime.

## Features

- **Two modes.** `--mode=ts` (the default) takes `.ts` only and makes `any`, `eval`, `var` and
  `Proxy` compile errors; `--mode=js` takes `.js` and `.ts` mixed and compiles untyped code as dynamic.
- **Unboxed values.** Inside checked code, values compile to machine values wherever the checker
  can infer a type.
- **explain.** `stator explain` reports a per-construct verdict: `static`, `dynamic`, `error` or
  `not-yet`.
- **A bundler for js mode.** `vite-stator` bundles `node_modules` into one tree-shaken module, and
  its `stator()` Vite plugin makes `vite build` write the native binary.
- **Config with a schema.** Every option can live in `stator.config.json`, validated against a
  generated JSON Schema; the command line overrides it.
- **Calling C.** An extern surface with an ABI table and explicit ownership rules for FFI.

## Install

There is no release yet. Set up the pinned toolchain (Node, pnpm, LLVM clang, just) and build the
runtime from a checkout:

```bash
git clone https://github.com/pyrlyn/stator
cd stator
mise install
pnpm install --frozen-lockfile
pnpm run runtime
```

## Usage examples

Build a native binary from TypeScript, straight from the sources on the pinned Node.

```bash
node packages/compiler/src/cli/main.ts build app.ts -o app
```

Compile mixed JavaScript in js mode.

```bash
node packages/compiler/src/cli/main.ts build app.js -o app --mode=js
```

Ask what each construct compiles to.

```bash
node packages/compiler/src/cli/main.ts explain app.ts --json
```

## Links

- Repository: <https://github.com/pyrlyn/stator>
- Plan: <https://github.com/pyrlyn/stator/blob/main/plan.md>
- License: <https://github.com/pyrlyn/stator/blob/main/LICENSE>
