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
    implementation(libs.kotlin.css)
    implementation(libs.jetbrains.markdown)
    testImplementation(kotlin("test"))
}

application {
    // `./gradlew run`: generate the site, then serve build/site with the Ktor dev preview.
    mainClass.set("dev.pyrlyn.landing.PreviewKt")
}

val siteDir = layout.buildDirectory.dir("site")

// Kotlin/JS bundles (production webpack: DCE + terser) that generateSite hashes into /_assets/.
val webBundle = project(":web").layout.buildDirectory.file("kotlin-webpack/js/productionExecutable/web.js")
val introBundle = project(":web-intro").layout.buildDirectory.file("kotlin-webpack/js/productionExecutable/web-intro.js")

/** Static site generation: renders every page into build/site. */
val generateSite by tasks.registering(JavaExec::class) {
    group = "site"
    description = "Generates the static site into build/site."
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("dev.pyrlyn.landing.GenerateKt")
    workingDir = projectDir
    dependsOn(":web:jsBrowserProductionWebpack", ":web-intro:jsBrowserProductionWebpack")
    args(siteDir.get().asFile.absolutePath, webBundle.get().asFile.absolutePath, introBundle.get().asFile.absolutePath)
    inputs.dir("content")
    inputs.dir("public")
    inputs.file(webBundle)
    inputs.file(introBundle)
    inputs.files(sourceSets.main.get().runtimeClasspath)
    // Build-time config from env; declared so a change re-runs the task.
    listOf("SITE_URL", "SITE_BASE", "GA_MEASUREMENT_ID", "POSTHOG_KEY", "POSTHOG_HOST", "ALGOLIA_APP_ID", "ALGOLIA_SEARCH_KEY", "ALGOLIA_INDEX", "API_URL")
        .forEach { inputs.property("env.$it", providers.environmentVariable(it).orElse("")) }
    outputs.dir(siteDir)
}

tasks.named<JavaExec>("run") {
    dependsOn(generateSite)
    workingDir = projectDir
    args(siteDir.get().asFile.absolutePath)
}

tasks.test { useJUnitPlatform() }
