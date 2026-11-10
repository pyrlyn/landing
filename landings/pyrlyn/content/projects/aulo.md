---
title: aulo
tagline: A local-first voice agent that controls your computer — shell, apps and browser, MCP both ways, local or remote models.
repo: https://github.com/pyrlyn/aulo
install: "git clone https://github.com/pyrlyn/aulo && cd aulo && mise exec -- just test"
version: "0.0.0"
status: "Specification only · no code yet"
accent: "#FF7AA2"
accent2: "#7EF2D0"
accentLight: "#C2386A"
order: 8
---

<!-- Website copy for the pyrlyn project site, written for pyrlyn/landing from README.md, spec.md
and docs/api/aulo.v1.md at pyrlyn/aulo@0950448 (checked 2026-10-14). No tag and no release; the
README status is "specification and plan only. No code yet", hence `status` and version 0.0.0.
aulo has no brand tokens yet: the rose and mint accents are this site's. -->

## Overview

aulo is a local-first voice agent that controls the computer it runs on. You talk to it and it
talks back. It runs shell commands, opens and drives apps, works in a browser, and reads the screen
when it has to.

aulo is planned as one daemon, `aulod`, with a gRPC API. Native desktop apps with chats (SwiftUI on
macOS, WinUI 3 on Windows), the `aulo` CLI and remote gRPC clients all reach it, so it can also run
headless on a server.

aulo is at the specification stage: the spec, the research, the plan and the gRPC API reference
exist; there is no code yet. The name comes from the Greek *aulos*, a reed pipe: breath turned into
voice.

## Features

- **Voice first.** Hands-free listening with a wake word or push-to-talk, end-of-turn detection and
  barge-in, and replies that stream as speech.
- **Swappable speech.** Every speech-to-text and text-to-speech engine can be swapped at runtime.
- **Computer control.** Shell, apps, browser and desktop accessibility; screenshots plus synthetic
  input only as the last resort.
- **MCP both ways.** A built-in MCP client, and aulo as an MCP server for other agents.
- **Any model.** Local models (runa, Ollama, LM Studio, llama.cpp) and remote providers (OpenAI,
  Anthropic, Gemini, xAI, OpenRouter, any OpenAI-compatible endpoint), set per tier and per chat.
- **Plugins.** Tools, speech engines, LLM providers, skills and hooks.

## Install

There is nothing to install yet. The repository holds the specification, the plan and a Rust
workspace pinned with mise:

```bash
git clone https://github.com/pyrlyn/aulo
cd aulo
mise exec -- just test
```

## Usage examples

Run the workspace tests (`just test` runs `cargo test --workspace`).

```bash
mise exec -- just test
```

## Links

- Repository: <https://github.com/pyrlyn/aulo>
- Specification: <https://github.com/pyrlyn/aulo/blob/main/spec.md>
- Plan: <https://github.com/pyrlyn/aulo/blob/main/plan.md>
- License: <https://github.com/pyrlyn/aulo/blob/main/LICENSE>
