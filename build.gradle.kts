plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.multiplatform) apply false
    application
}

kotlin { jvmToolchain(21) }

dependencies {
    implementation(libs.kotlinx.html)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.server.cio)
    implementation(libs.ktor.server.sse)
    implementation(libs.kotlin.css)
    implementation(libs.jetbrains.markdown)
    testImplementation(kotlin("test"))
}

application {
    // `./gradlew run`: generate the site, then serve build/site with the Ktor dev preview.
    mainClass.set("dev.pyrlyn.landing.PreviewKt")
}

val siteDir = layout.buildDirectory.dir("site")
// Written by the generator after every successful run; `./gradlew dev` reloads the browser when it changes.
// Kept outside build/site so it never ships.
val siteStamp = layout.buildDirectory.file("site.stamp")

// Kotlin/JS bundles (production webpack: DCE + terser) that generateSite hashes into /_assets/.
val webBundle = project(":web").layout.buildDirectory.file("kotlin-webpack/js/productionExecutable/web.js")
val introBundle = project(":web-intro").layout.buildDirectory.file("kotlin-webpack/js/productionExecutable/web-intro.js")
val plansBundle = project(":web-plans").layout.buildDirectory.file("kotlin-webpack/js/productionExecutable/web-plans.js")

/** Renders every page into [out]; [apiUrl] replaces the API_URL from the environment. */
fun JavaExec.renderSite(out: Provider<Directory>, stamp: Provider<RegularFile>?, apiUrl: String? = null) {
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("dev.pyrlyn.landing.GenerateKt")
    workingDir = projectDir
    dependsOn(":web:jsBrowserProductionWebpack", ":web-intro:jsBrowserProductionWebpack", ":web-plans:jsBrowserProductionWebpack")
    args(listOfNotNull(out, webBundle, introBundle, plansBundle, stamp).map { it.get().asFile.absolutePath })
    inputs.dir("content")
    inputs.dir("public")
    inputs.file(webBundle)
    inputs.file(introBundle)
    inputs.file(plansBundle)
    inputs.files(sourceSets.main.get().runtimeClasspath)
    // Build-time config from env; declared so a change re-runs the task.
    listOf("SITE_URL", "SITE_BASE", "GA_MEASUREMENT_ID", "POSTHOG_KEY", "POSTHOG_HOST", "ALGOLIA_APP_ID", "ALGOLIA_SEARCH_KEY", "ALGOLIA_INDEX", "API_URL")
        .forEach { inputs.property("env.$it", providers.environmentVariable(it).orElse("")) }
    if (apiUrl != null) {
        environment("API_URL", apiUrl)
        inputs.property("apiUrl", apiUrl)
    }
    outputs.dir(out)
    stamp?.let { outputs.file(it) }
}

/** Static site generation: renders every page into build/site. */
val generateSite = tasks.register<JavaExec>("generateSite") {
    group = "site"
    description = "Generates the static site into build/site."
    renderSite(siteDir, siteStamp)
}

// The plans widget runs only on a site built with an API URL; the e2e tests answer this host themselves.
tasks.register<JavaExec>("generateApiSite") {
    group = "site"
    description = "Generates the site with a stub API_URL into build/site-api, for the e2e tests of the plans widget."
    renderSite(layout.buildDirectory.dir("site-api"), null, apiUrl = "https://api.e2e.test")
}

tasks.named<JavaExec>("run") {
    dependsOn(generateSite)
    workingDir = projectDir
    args(siteDir.get().asFile.absolutePath)
}

/**
 * `./gradlew dev`: the preview with live reload. The preview starts `gradlew --continuous generateSite`
 * as a child, so any change to Kotlin (site, styles, web), content or public regenerates the site and
 * open pages reload. A single Gradle build cannot both serve forever and rebuild on change.
 */
tasks.register<JavaExec>("dev") {
    group = "site"
    description = "Serves build/site on http://localhost:4321 and regenerates + reloads it on every change."
    dependsOn(generateSite)
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("dev.pyrlyn.landing.PreviewKt")
    workingDir = projectDir
    args(
        siteDir.get().asFile.absolutePath,
        siteStamp.get().asFile.absolutePath,
        layout.projectDirectory.file(if (System.getProperty("os.name").startsWith("Windows")) "gradlew.bat" else "gradlew").asFile.absolutePath,
    )
    // The child build must use the same client source as this one.
    providers.gradleProperty("backendDir").orNull?.let { args("-PbackendDir=${file(it).absolutePath}") }
}

tasks.test { useJUnitPlatform() }
