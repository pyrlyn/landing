---
title: swarfr
tagline: Sweep the swarf out of Cargo target/ directories — compress and dedupe build output without deleting what you still build with.
repo: https://github.com/pyrlyn/swarfr
install: "curl --proto '=https' --tlsv1.2 -LsSf https://github.com/pyrlyn/swarfr/releases/latest/download/swarfr-installer.sh | sh"
install_alternatives:
  - "ketch install pyrlyn/swarfr"
  - "cargo install --locked --path swarfr"
version: "0.1.0"
status: "In development · no stable release yet"
accent: "#E9B45A"
accent2: "#7FA8D6"
accentLight: "#9A6A16"
order: 7
---

<!-- Website copy for the pyrlyn project site, copied to pyrlyn/landing as
content/projects/swarfr.md by hand until swarfr has a `sync-docs.yml`; front matter follows
CONTENT_CONTRACT.md in that repository.
Sources (checked 2026-10-14): README.md, docs/usage.md and docs/bench.md at
pyrlyn/swarfr@bda2dde; version from the v0.1.0 release. swarfr has no brand tokens yet: the
straw and blue temper-colour accents are this site's. -->

## Overview

Swarf is the chips and shavings a lathe or a mill throws off: metal cut away, waste that is not
the part. Much of a Cargo `target/` directory is swarf, and `swarfr` sweeps it out without
deleting what you still build with and without slowing builds.

One planner runs several approaches that reinforce each other: transparent compression of
stable artifacts, copy-on-write dedupe across targets, zero-byte seeding of new worktrees, and
opt-in removal of orphaned and idle targets. Everything runs under cargo's own build lock and
keeps mtimes, so nothing is rebuilt.

swarfr is in active development: v0.1.0 is out, and testing volunteers are welcome. Lossless
`compress` and `dedupe` need a filesystem that can share blocks or compress (APFS, btrfs, XFS
with reflink); on Windows the tool builds and reports, but those two passes plan no work yet.

## Features

- **compress.** Files of 8 KB and more get transparent APFS compression (LZFSE), or btrfs
  compression on Linux; hardlink groups stay groups.
- **dedupe.** Files with equal content become copy-on-write clones of one copy, compared inside a
  family: a repository and its worktrees.
- **seed.** A new worktree's target starts as a zero-byte clone of a sibling's, so third-party
  crates are not rebuilt.
- **orphans and evict.** Opt-in removal of targets whose worktree is gone, idle targets, and the
  least recently built above a global size cap; every removal is printed with its reason.
- **advise.** Read-only: names the manifest and cargo config keys that make targets bigger than
  they need to be, with the file each one belongs in.
- **Beyond cargo.** `GOCACHE` and Go's module cache, SwiftPM `.build`, .NET `obj/` and `bin/`, and
  CMake build dirs go through the same passes with their own safety rules.

## Install

Every release carries binaries for Apple Silicon macOS (signed), x86_64 Linux and x86_64
Windows. The shell installer puts `swarfr` and its updater `swarfr-update` into cargo's bin
directory:

```sh
curl --proto '=https' --tlsv1.2 -LsSf https://github.com/pyrlyn/swarfr/releases/latest/download/swarfr-installer.sh | sh
```

With ketch:

```sh
ketch install pyrlyn/swarfr
```

Or build it from a checkout, with the toolchain the repository pins:

```sh
git clone https://github.com/pyrlyn/swarfr
cargo install --locked --path swarfr
```

## Usage examples

The first five minutes: everything is read-only until the last step.

```sh
swarfr status ~/code
swarfr advise ~/code
swarfr run --dry-run ~/code
swarfr run ~/code
```

Opt in to a lossy pass, on a dry run first.

```sh
swarfr run --dry-run --lossy orphans ~/code
swarfr run --lossy evict --evict-max-total-gib 50 ~/code
```

Run it as a service that visits a build dir once it has gone cold.

```sh
swarfr daemon install
swarfr daemon status
```

## Links

- Repository: <https://github.com/pyrlyn/swarfr>
- Releases: <https://github.com/pyrlyn/swarfr/releases>
- Design: <https://github.com/pyrlyn/swarfr/blob/main/DESIGN.md>
- License: <https://github.com/pyrlyn/swarfr/blob/main/LICENSE>
