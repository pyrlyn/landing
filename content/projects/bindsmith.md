---
title: bindsmith
tagline: One bindsmith.yaml, six Flutter platforms — Dart bindings for native APIs on Android, iOS, macOS, Windows, Linux and Web.
repo: https://github.com/pyrlyn/bindsmith
install: "dart run bindsmith doctor"
install_alternatives:
  - "dart run bindsmith init"
version: "0.1.0"
status: "In development · no release yet"
accent: "#C87941"
accent2: "#13B9FD"
accentLight: "#A35F2E"
order: 12
---

<!-- Website copy for the pyrlyn project site, written for pyrlyn/landing from README.md and
docs/getting-started.md at pyrlyn/bindsmith@72c38f3 (checked 2026-10-14). No tag and no release;
the README says "Work in progress", hence `status`; version from the `^0.1.0` in getting-started.
accent, accent2 and accentLight are the copper seed, the dark secondary azure and the tertiary
of docs/brand/tokens.css (Material 3 roles). -->

## Overview

One `bindsmith.yaml`, six Flutter platforms. bindsmith generates Dart bindings for native APIs on
Android, iOS, macOS, Windows, Linux and Web by driving the official generators through their
library APIs (ffigen, swiftgen, jnigen, winmd, dart-dbus), with a TypeScript `.d.ts` driver for
Web, a unified IR with fixup passes, and review markers for everything that could not be mapped.

bindsmith is a work in progress: the drivers, the IR, the passes and the emitters are in place, and
the command line generates for the C and Objective-C drivers so far.

## Features

- **One config, six platforms.** Android, iOS, macOS, Windows, Linux and Web from one
  `bindsmith.yaml`, checked against a JSON Schema in your editor.
- **Official generators underneath.** ffigen, swiftgen, jnigen, winmd and dart-dbus, driven
  through their library APIs, plus a `.d.ts` driver for Web.
- **A unified IR with fixups.** Every driver feeds one model; fixup passes shape the API you ship.
- **Review markers.** `@BindsmithVerify` marks what could not be mapped, and `verify` exits 1
  while anything is unresolved.
- **Pinned dependencies.** `resolve` pins every native dependency in `bindsmith.lock` with sha256
  sums; `--offline` resolves from the cache only.
- **Safe in CI.** `dump`, `diff`, `explain` and `generate --check` write nothing into your project.

## Install

Add the generator and the runtime to your package:

```yaml
dev_dependencies:
  bindsmith: ^0.1.0
dependencies:
  bindsmith_runtime: ^0.1.0
```

Then check what this machine is missing:

```bash
dart run bindsmith doctor
```

## Usage examples

Write a starter config, generate, and see what still needs a human.

```bash
dart run bindsmith init
dart run bindsmith generate
dart run bindsmith verify
```

In CI, fail when the native API moved or the checked-in output is stale.

```bash
dart run bindsmith dump > api.json
dart run bindsmith diff api.json
dart run bindsmith generate --check
```

## Links

- Repository: <https://github.com/pyrlyn/bindsmith>
- Plan: <https://github.com/pyrlyn/bindsmith/blob/main/plan.md>
- License: <https://github.com/pyrlyn/bindsmith/blob/main/LICENSE>
