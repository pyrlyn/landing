# Lighthouse CI

Lighthouse CI ([`@lhci/cli`](https://github.com/GoogleChrome/lighthouse-ci)) audits the built site on
every pull request to `main`, every push to `main` and on manual dispatch. It runs **only in CI**:
it is not a `package.json` dependency, so `npm install`, `npm run build` and the local workflow
are unchanged.

## How it works

The workflow is `.github/workflows/lighthouse.yaml`, generated from the Kotlin script
`.github/workflows/lighthouse.main.kts` (see [Kotlin workflow config](#kotlin-workflow-config)).
It is self-contained and does not call the shared `pyrlyn/ci` workflows. Jobs:

| Job | What it does |
|---|---|
| `check_yaml_consistency` | Re-runs the Kotlin script and fails if the committed YAML differs from its output |
| `test` | `npm ci`, `npm test`, production build (`SITE_URL=https://pyrlyn.dev`, as in `release.yml`), `npm run check:seo`, `npm run check:docs`. Steps after the tests run even if the tests fail, so one run shows every failing check |
| `lighthouse` | Builds with `SITE_BASE=/`, then runs `npx @lhci/cli@0.15.1 autorun` twice: `lighthouserc.mobile.json` and `lighthouserc.desktop.json` |

Each Lighthouse config serves `dist/` through `staticDistDir` (LHCI's built-in static server),
audits `/`, `/rtok/` and `/rtok/docs/` three times each and asserts on the median run. Reports
(HTML and JSON) are written to `.lighthouseci/mobile` and `.lighthouseci/desktop` and uploaded as
the `lighthouse-reports` artifact (14 days). Nothing goes to `temporary-public-storage` or an LHCI
server. The desktop run executes even if the mobile assertions fail.

## Thresholds

The same thresholds apply to mobile and desktop. A score below the threshold on the median run
fails the job (`error` level).

| Category | Min score | Why |
|---|---|---|
| Performance | 0.90 | Lighthouse's own "good" band starts at 90. The site was at 93–94 on mobile before the external font and analytics were added, so 90 is a reachable target, not an aspiration |
| Accessibility | 0.95 | High bar with room for one or two minor audits that Lighthouse flags on static content (for example a contrast edge case) without blocking every PR |
| Best practices | 0.95 | Same reasoning: third-party scripts (GA, PostHog) can trip one audit such as console errors or third-party cookies |
| SEO | 1.00 | The site is at 100 on every page and `check:seo` already enforces the same basics; any regression is a real bug |

**Expect the job to be red for now.** At the time of writing `main` scores about 0.64–0.67 on mobile
performance (mobile LCP ~7 s, blocked by the external Satoshi stylesheet, GA and PostHog). PR #80
brings it to 0.76–0.94 (`/` 0.76, `/rtok/` 0.78, `/rtok/docs/` 0.94). The `lighthouse` job stays red
until #80 and further LCP work on `/` and `/rtok/` (the h1 hero) land. The threshold is the target
and is deliberately not lowered to match the current scores.

The `test` job is also red on `main` today: the `docs-nav` unit test fails because `docs-nav.json`
is missing 7 new rtok and stator docs. That is a pre-existing issue, not caused by this workflow.

## Kotlin workflow config

The workflow is written with [github-workflows-kt](https://github.com/typesafegithub/github-workflows-kt)
(pinned to `4.0.0`). The script `.github/workflows/lighthouse.main.kts`:

- pulls the library from Maven Central and typed action bindings (`actions/checkout`,
  `actions/setup-node`, `actions/upload-artifact`, major `v7`) from the bindings server
  `https://bindings.krzeminski.it`;
- pins every action to a commit SHA via `_customVersion` (same SHAs as `release.yml`);
- keeps the pinned versions (`lhciVersion`, action SHAs) and the build env at the top;
- shares checkout, Node 22 setup and `npm ci` between jobs through `checkoutAndNode()`;
- writes `lighthouse.yaml` next to itself.

Regenerate the YAML after any change to the script (needs Kotlin and a JDK; GitHub's
`ubuntu-latest` has both):

```sh
kotlin .github/workflows/lighthouse.main.kts
# or, since the script is executable:
.github/workflows/lighthouse.main.kts
```

Commit the script and the YAML together. Never edit `lighthouse.yaml` by hand: the
`check_yaml_consistency` job deletes it, re-runs the script and fails on `git diff --exit-code`,
and the other jobs depend on it.

## Enable and disable

- Disable temporarily: Actions → `lighthouse` → "Disable workflow", or `gh workflow disable lighthouse`.
- Make it non-blocking: change `"error"` to `"warn"` in the `lighthouserc.*.json` assertions.
- Drop desktop: remove the desktop step from the script and regenerate.
- Remove: delete the script, the YAML and both `lighthouserc.*.json` files.
- To make it a required check, add `Lighthouse CI` in the branch protection of `main` (only once it is green).

## How to test

- Open a PR or run the workflow manually (`gh workflow run lighthouse --ref <branch>`), then
  download the `lighthouse-reports` artifact and open the HTML reports.
- Locally, without adding a dependency (optional; CI is the source of truth):
  `SITE_BASE=/ npm run build && npx --yes @lhci/cli@0.15.1 autorun --config=lighthouserc.mobile.json`.
  This needs a local Chrome.

## Limitations

- Scores on shared GitHub runners vary by a few points between runs; three runs with the median
  reduce, but do not remove, the noise.
- `staticDistDir` serves files without the production nginx config, so redirects, compression and
  cache headers are not measured. External requests (Fontshare, GA, PostHog) are real network calls.
- The Lighthouse build uses `SITE_BASE=/` so the audited URLs match production paths.
- Repo variables (Algolia, PostHog key, API URL) are not passed, so search, PostHog and Pro prices
  are off in the audited build. GA is off too (`GA_MEASUREMENT_ID` is not set, so CI does not
  send hits to production analytics), so scores are slightly better than on production.
- Only three pages are audited.

The library-generated `check_yaml_consistency` job checks out without `persist-credentials: false`
(github-workflows-kt 4.0.0 has no option for it). That single jactionlint `artipacked` finding is
recorded in `.github/jactionlint-baseline.json`; the job uploads nothing, so the token is not exposed.
