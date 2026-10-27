#!/usr/bin/env kotlin
// Source of truth for .github/workflows/lighthouse-nightly.yaml (same approach as pyrlyn/landing's lighthouse.main.kts).
// Regenerate the YAML with:  kotlin .github/workflows/lighthouse-nightly.main.kts
// The generated workflow has a consistency-check job that fails when the committed YAML drifts from this script.
@file:Repository("https://repo.maven.apache.org/maven2/")
@file:DependsOn("io.github.typesafegithub:github-workflows-kt:4.0.0")

@file:Repository("https://bindings.krzeminski.it")
@file:DependsOn("actions:checkout___major:v7")
@file:DependsOn("actions:setup-java___major:v6")
@file:DependsOn("actions:setup-node___major:v7")
@file:DependsOn("gradle:actions__setup-gradle___major:v6")
@file:DependsOn("actions:upload-artifact___major:v7")

import io.github.typesafegithub.workflows.actions.actions.Checkout
import io.github.typesafegithub.workflows.actions.actions.SetupJava
import io.github.typesafegithub.workflows.actions.actions.SetupNode
import io.github.typesafegithub.workflows.actions.actions.UploadArtifact
import io.github.typesafegithub.workflows.actions.gradle.ActionsSetupGradle
import io.github.typesafegithub.workflows.domain.Concurrency
import io.github.typesafegithub.workflows.domain.Mode
import io.github.typesafegithub.workflows.domain.Permission
import io.github.typesafegithub.workflows.domain.RunnerType.UbuntuLatest
import io.github.typesafegithub.workflows.domain.triggers.Cron
import io.github.typesafegithub.workflows.domain.triggers.Schedule
import io.github.typesafegithub.workflows.domain.triggers.WorkflowDispatch
import io.github.typesafegithub.workflows.dsl.workflow
import io.github.typesafegithub.workflows.yaml.CheckoutActionVersionSource
import io.github.typesafegithub.workflows.yaml.ConsistencyCheckJobConfig

// Actions are pinned to commit SHAs, like the other pyrlyn workflows.
val checkoutSha = "3d3c42e5aac5ba805825da76410c181273ba90b1" // v7.0.1
val setupJavaSha = "b843ebf9b913146b38da2eaa7aeddf76074ae1ad" // v6.1.0
val setupNodeSha = "949feb2413d6458794dcd2491c4babbbce0c15c1" // v7.1.0
val setupGradleSha = "3f5f9adaf7d9fecd50b5935e54106014257a94e6" // v6.4.0
val uploadArtifactSha = "cf430e030ddbb5b0abf93d22962f4752f3646cd9" // v7.0.2

// Pinned Lighthouse CI CLI, same as pyrlyn/landing. Only CI runs it (npx); it is not a project dependency.
val lhciVersion = "0.15.1"

// Production build env, served from the root so the audited URLs are /, /rtok/, /rtok/docs/.
val buildEnv = mapOf("SITE_URL" to "https://pyrlyn.dev", "SITE_BASE" to "/")

workflow(
    name = "lighthouse-nightly",
    on = listOf(
        // 03:00 UTC = 05:00 Kyiv in winter (UTC+2). GitHub cron is UTC-only, so in summer (UTC+3) it runs at 06:00 Kyiv.
        Schedule(listOf(Cron(minute = "0", hour = "3"))),
        WorkflowDispatch(),
    ),
    sourceFile = __FILE__,
    consistencyCheckJobConfig = ConsistencyCheckJobConfig.Configuration(
        condition = null,
        env = emptyMap(),
        checkoutActionVersion = CheckoutActionVersionSource.Given(checkoutSha),
        additionalSteps = null,
        useLocalBindingsServerAsFallback = false,
    ),
    concurrency = Concurrency(group = "lighthouse-nightly", cancelInProgress = false),
    permissions = mapOf(Permission.Contents to Mode.Read),
) {
    job(id = "lighthouse", name = "Lighthouse CI", runsOn = UbuntuLatest, timeoutMinutes = 25) {
        uses(name = "Checkout", action = Checkout(persistCredentials = false, _customVersion = checkoutSha))
        uses(
            name = "Set up JDK",
            action = SetupJava(distribution = SetupJava.Distribution.Temurin, javaVersion = "21", _customVersion = setupJavaSha),
        )
        uses(name = "Set up Gradle", action = ActionsSetupGradle(_customVersion = setupGradleSha))
        uses(name = "Set up Node", action = SetupNode(nodeVersion = "22", _customVersion = setupNodeSha))
        run(name = "Build site", command = "./gradlew generateSite", env = buildEnv)
        run(
            name = "Lighthouse (mobile)",
            command = "npx --yes --ignore-scripts @lhci/cli@$lhciVersion autorun --config=lighthouserc.mobile.json",
        )
        run(
            name = "Lighthouse (desktop)",
            command = "npx --yes --ignore-scripts @lhci/cli@$lhciVersion autorun --config=lighthouserc.desktop.json",
            condition = "\${{ !cancelled() }}",
        )
        uses(
            name = "Upload Lighthouse reports",
            action = UploadArtifact(
                name = "lighthouse-reports",
                path = listOf(".lighthouseci/"),
                ifNoFilesFound = UploadArtifact.BehaviorIfNoFilesFound.Warn,
                // .lighthouseci is a dot-directory; upload-artifact skips hidden files by default.
                includeHiddenFiles = true,
                retentionDays = UploadArtifact.RetentionPeriod.Value(14),
                _customVersion = uploadArtifactSha,
            ),
            condition = "\${{ !cancelled() }}",
        )
    }
}
