---
title: Scull
tagline: A terminal emulator with a Rust core and a native interface on each platform — SwiftUI on macOS, WinUI on Windows.
repo: https://github.com/pyrlyn/scull
install: "git clone https://github.com/pyrlyn/scull && cd scull && just check"
version: "0.0.0"
status: "Early scaffold · no release yet"
accent: "#EDE6D6"
accent2: "#45E39A"
accentLight: "#2E7D5B"
order: 9
---

<!-- Website copy for the pyrlyn project site, written for pyrlyn/landing from README.md at
pyrlyn/scull@7d1a8c0 (checked 2026-10-14). No tag and no release; the README status is "an empty
Cargo workspace", hence `status` and version 0.0.0. The repository has no LICENSE file yet, so the
site shows "No license yet" (LICENSE_OVERRIDES). Ivory and phosphor green are this site's. -->

## Overview

Scull is a terminal emulator with a Rust core and a native interface on each platform: SwiftUI on
macOS, WinUI on Windows. The core parses, keeps state and encodes input; the platform draws text
with its own text stack and handles windows, input methods and accessibility.

Scull is an early scaffold: an empty Cargo workspace that `just check` builds, lints and tests.
The research behind it studies how kitty, WezTerm, Alacritty, foot, Contour and Warp work, and
what Scull takes from each.

## Features

- **A Rust core.** Parsing, terminal state and input encoding live in one core shared by every
  platform.
- **Native text on each platform.** The platform draws text with its own text stack instead of a
  custom renderer.
- **Native windows and input.** Windows, input methods and accessibility come from SwiftUI on
  macOS and WinUI on Windows.
- **Research first.** The design takes from kitty, WezTerm, Alacritty, foot, Contour and Warp, with
  the reasoning written down in the repository.

## Install

There is nothing to install yet. Build, lint and test the workspace:

```bash
git clone https://github.com/pyrlyn/scull
cd scull
just check
```

## Usage examples

Build, lint and test the Cargo workspace.

```bash
just check
```

## Links

- Repository: <https://github.com/pyrlyn/scull>
- Research: <https://github.com/pyrlyn/scull/blob/main/research.md>
- Plan: <https://github.com/pyrlyn/scull/blob/main/plan.md>
