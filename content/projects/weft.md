---
title: Weft
tagline: An agent-friendly UI description format — strict markup that models read and write, canonical JSON that tools validate and patch.
repo: https://github.com/pyrlyn/weft
install: "cargo build -p weft-cli"
install_alternatives:
  - "git clone https://github.com/pyrlyn/weft && cd weft && mise install && pnpm install && moon run root:wasm && cargo build -p weft-cli"
version: "0.1.0"
status: "Prototype · no release yet"
accent: "#A89BFF"
accent2: "#F2C46D"
accentLight: "#5B4BD1"
order: 6
---

<!-- Website copy for the pyrlyn project site, copied to pyrlyn/landing as
content/projects/weft.md by hand until Weft has a `sync-docs.yml`; front matter follows
CONTENT_CONTRACT.md in that repository.
Sources (checked 2026-10-14): README.md, docs/README.md, docs/what-is-weft.md, docs/tour.md and
docs/cli.md at pyrlyn/weft@1ab1443; version from `weft --version` in docs/tour.md (no tag and no
release yet, hence `status`). Weft has no brand tokens yet: the indigo warp and gold weft accents
are this site's. -->

## Overview

Weft is an open format for describing a screen of a user interface so that an AI agent can read
it, write it, check it and change it safely, and so that ordinary tools can check, preview and
convert it too.

A Weft file is a strict XML subset with a closed vocabulary: explicit hierarchy, named slots,
enumerated states and stable ids. The same tree exists as canonical typed JSON, validated
against a component catalog. Roles and states come from WAI-ARIA, component contracts are
modelled on Custom Elements Manifest, and design values come from Design Tokens (DTCG 2025.10).

Weft is a prototype at format version 0.1. There is no installer and no release yet: build the
`weft` command from the repository.

## Features

- **Strict markup for models.** A restricted subset of XML with double-quoted attributes and one
  way to say each thing. Anything not in the catalog is an error, with a "did you mean" hint.
- **Canonical JSON for tools.** Markup and `.weft.json` convert into each other without loss, and
  a screen has exactly one canonical text, so two files that mean the same are byte-for-byte equal.
- **A closed catalog.** 29 components (`button`, `field`, `table`, `dialog` and more), each with its
  props, slots, states and events. The catalog is data you can read, extend and version.
- **Tokens, bindings, actions.** Values are token references such as `{token.space.md}`, data is
  bound with `{$.user.email}`, and behaviour is a named action like `on-press="nav.reset"`. The
  document never carries code.
- **Patches by id.** Every element has an id, and a change is a short patch applied atomically,
  so the diff is exactly what was asked for.
- **Agent tooling.** An MCP server, and plugins for Claude Code, Cursor, Open Design and Xcode;
  render to HTML, React, SolidJS, Lit, SwiftUI and Slint, and import HTML and React back with
  the losses listed.

## Install

There is no installer yet. Build the `weft` command from the repository and put the folder on
your `PATH`:

```bash
cargo build -p weft-cli
export PATH="$PWD/target/debug:$PATH"
```

For the tour and the TypeScript packages, set up the pinned tools and the WebAssembly core first:

```bash
mise install
pnpm install
moon run root:wasm
cargo build -p weft-cli
```

## Usage examples

Check a screen against the component catalog; exit code 0 means it is valid.

```bash
weft validate weft-tour/login.weft --catalog packages/catalog/catalog.json --strict
```

A screen in Weft markup: a sign-in form with a bound email field and a submit action.

```xml
<screen id="login" label="Sign in" weft="0.1">
  <form id="f1" on-submit="auth.submit">
    <field id="email" label="Email" required="true" type="email" value="{$.email}"/>
    <button id="go" submit="true" variant="primary">Sign in</button>
  </form>
</screen>
```

Print the canonical markup of a document, then read back what each binding, token and event means.

```bash
weft fmt weft-tour/login.weft
weft explain weft-tour/login.weft
```

## Links

- Repository: <https://github.com/pyrlyn/weft>
- Specification: <https://github.com/pyrlyn/weft/blob/main/SPEC.md>
- Guide for AI agents: <https://github.com/pyrlyn/weft/blob/main/AGENT-SPEC.md>
- License: <https://github.com/pyrlyn/weft/blob/main/LICENSE>
