#!/usr/bin/env kotlin
// Source of truth for .github/workflows/lighthouse.yaml. Regenerate the YAML with:
//   kotlin .github/workflows/lighthouse.main.kts
// The generated workflow has a consistency-check job that re-runs this script in CI and fails
// when the committed YAML drifts from it. See docs/lighthouse-ci.md.
@file:Repository("https://repo.maven.apache.org/maven2/")
@file:DependsOn("io.github.typesafegithub:github-workflows-kt:4.0.0")

@file:Repository("https://bindings.krzeminski.it")
@file:DependsOn("actions:checkout___major:v7")
@file:DependsOn("actions:setup-node___major:v7")
@file:DependsOn("actions:upload-artifact___major:v7")

import io.github.typesafegithub.workflows.actions.actions.Checkout
import io.github.typesafegithub.workflows.actions.actions.SetupNode
import io.github.typesafegithub.workflows.actions.actions.UploadArtifact
import io.github.typesafegithub.workflows.domain.RunnerType.UbuntuLatest
import io.github.typesafegithub.workflows.domain.triggers.PullRequest
import io.github.typesafegithub.workflows.domain.triggers.Push
import io.github.typesafegithub.workflows.domain.triggers.WorkflowDispatch
import io.github.typesafegithub.workflows.dsl.JobBuilder
import io.github.typesafegithub.workflows.dsl.workflow
import io.github.typesafegithub.workflows.domain.Concurrency
import io.github.typesafegithub.workflows.domain.Mode
import io.github.typesafegithub.workflows.domain.Permission
import io.github.typesafegithub.workflows.yaml.CheckoutActionVersionSource
import io.github.typesafegithub.workflows.yaml.ConsistencyCheckJobConfig

// Actions are pinned to commit SHAs, like the other workflows in this repo.
val checkoutSha = "3d3c42e5aac5ba805825da76410c181273ba90b1" // v7.0.1
val setupNodeSha = "949feb2413d6458794dcd2491c4babbbce0c15c1" // v7.1.0
val uploadArtifactSha = "cf430e030ddbb5b0abf93d22962f4752f3646cd9" // v7.0.2

// Pinned Lighthouse CI CLI. Only CI runs it (npx); it is not a package.json dependency.
val lhciVersion = "0.15.1"

// Production build env, as in release.yml (public values only).
val releaseEnv = mapOf("SITE_URL" to "https://pyrlyn.dev")

// Lighthouse build: served from the root so the audited URLs are /, /rtok/, /rtok/docs/.
val buildEnv = mapOf(
    "SITE_URL" to "https://pyrlyn.dev",
    "SITE_BASE" to "/",
)

fun JobBuilder<*>.checkoutAndNode() {
    uses(
        name = "Checkout",
        action = Checkout(persistCredentials = false, _customVersion = checkoutSha),
    )
    uses(
        name = "Set up Node",
        action = SetupNode(nodeVersion = "22", cache = SetupNode.PackageManager.Npm, _customVersion = setupNodeSha),
    )
    run(
        name = "Install dependencies",
        command = """
            git config --global url."https://github.com/".insteadOf "ssh://git@github.com/"
            npm ci --no-audit --no-fund --ignore-scripts
        """.trimIndent(),
    )
}

workflow(
    name = "lighthouse",
    on = listOf(
        Push(branches = listOf("main")),
        PullRequest(branches = listOf("main")),
        WorkflowDispatch(),
    ),
    sourceFile = __FILE__,
    // Re-runs this script in CI and fails if the committed YAML differs from its output.
    consistencyCheckJobConfig = ConsistencyCheckJobConfig.Configuration(
        condition = null,
        env = emptyMap(),
        checkoutActionVersion = CheckoutActionVersionSource.Given(checkoutSha),
        additionalSteps = null,
        useLocalBindingsServerAsFallback = false,
    ),
    concurrency = Concurrency(
        group = "lighthouse-\${{ github.event.pull_request.number || github.ref }}",
        cancelInProgress = true,
    ),
    permissions = mapOf(Permission.Contents to Mode.Read),
) {
    job(id = "test", name = "Tests and site checks", runsOn = UbuntuLatest, timeoutMinutes = 15) {
        // The docs-nav unit test fails on main today (docs-nav.json is missing 7 docs).
        checkoutAndNode()
        run(name = "Unit tests", command = "npm test")
        // Later steps run even if the unit tests fail, so one run reports every failing check.
        val always = "\${{ !cancelled() }}"
        run(name = "Build", command = "npm run build", env = releaseEnv, condition = always)
        run(name = "check:seo", command = "npm run check:seo", env = releaseEnv, condition = always)
        run(name = "check:docs", command = "npm run check:docs", condition = always)
    }

    job(id = "lighthouse", name = "Lighthouse CI", runsOn = UbuntuLatest, timeoutMinutes = 20) {
        checkoutAndNode()
        run(name = "Build", command = "npm run build", env = buildEnv)
        run(
            name = "Lighthouse (mobile)",
            command = "npx --yes @lhci/cli@$lhciVersion autorun --config=lighthouserc.mobile.json",
        )
        run(
            name = "Lighthouse (desktop)",
            command = "npx --yes @lhci/cli@$lhciVersion autorun --config=lighthouserc.desktop.json",
            condition = "\${{ !cancelled() }}",
        )
        uses(
            name = "Upload Lighthouse reports",
            action = UploadArtifact(
                name = "lighthouse-reports",
                path = listOf(".lighthouseci/"),
                ifNoFilesFound = UploadArtifact.BehaviorIfNoFilesFound.Warn,
                retentionDays = UploadArtifact.RetentionPeriod.Value(14),
                _customVersion = uploadArtifactSha,
            ),
            condition = "\${{ !cancelled() }}",
        )
    }
}
