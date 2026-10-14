---
title: Kerf
tagline: An IDE with a small Rust core and sandboxed WASM plugins; LSP and DAP run as processes the core starts.
repo: https://github.com/pyrlyn/kerf
install: "git clone https://github.com/pyrlyn/kerf"
version: "0.0.0"
status: "Research · no code yet"
accent: "#C6F24E"
accent2: "#FF6BD6"
accentLight: "#5E7A12"
order: 10
---

<!-- Website copy for the pyrlyn project site, written for pyrlyn/landing from README.md at
pyrlyn/kerf@92d5fc3 (checked 2026-10-14). No tag and no release; the README status is "research
only, no code yet", hence `status` and version 0.0.0. Kerf has no docs/ folder, so it has no docs
pages on this site. Laser chartreuse and cut-line magenta are this site's. -->

## Overview

Kerf is an IDE with a small Rust core and sandboxed WASM plugins.

The core draws the editor, holds the text buffer, runs the event loop and keeps a semantic index
of the code (tree-sitter plus an incremental index). Languages and debugging come in through LSP
and DAP: separate processes the core starts on a plugin's request. Plugins declare their
permissions in a manifest and cannot spawn processes themselves.

Kerf is at the research stage, with no code yet. The research studies how Zed, VS Code, IntelliJ,
Visual Studio, Lapce, Neovim, Lite XL, Pragtical, Zellij and others work, and what Kerf takes from
each.

## Features

- **A small Rust core.** The editor, the text buffer, the event loop and the semantic index.
- **A semantic index.** tree-sitter plus an incremental index of the code.
- **Sandboxed WASM plugins.** Plugins declare their permissions in a manifest and cannot spawn
  processes themselves.
- **LSP and DAP as processes.** Language servers and debug adapters run as separate processes the
  core starts on a plugin's request.

## Install

There is nothing to build yet. The repository holds the research, the ideas and the plan:

```bash
git clone https://github.com/pyrlyn/kerf
```

## Usage examples

Get the research and the plan.

```bash
git clone https://github.com/pyrlyn/kerf
```

## Links

- Repository: <https://github.com/pyrlyn/kerf>
- Research: <https://github.com/pyrlyn/kerf/blob/main/research.md>
- Plan: <https://github.com/pyrlyn/kerf/blob/main/plan.md>
- License: <https://github.com/pyrlyn/kerf/blob/main/LICENSE>
