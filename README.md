# landing

Kotlin/JVM static site for [pyrlyn.dev](https://pyrlyn.dev).

The Pyrlyn landing lives in [`landings/pyrlyn/`](landings/pyrlyn/). Further landings can be added next to it under `landings/`. GitHub Actions workflows stay at the repository root and run the Gradle project from that directory.

```sh
cd landings/pyrlyn
gh auth refresh -s read:packages
GITHUB_TOKEN=$(gh auth token) ./gradlew generateSite   # output: landings/pyrlyn/build/site
```

See [`landings/pyrlyn/README.md`](landings/pyrlyn/README.md) for the full project documentation.
