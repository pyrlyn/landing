plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    application
}

kotlin { jvmToolchain(21) }

dependencies {
    implementation(libs.kotlinx.html)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.server.cio)
    implementation(libs.kotlin.css)
    testImplementation(kotlin("test"))
}

application {
    // `./gradlew run`: generate the site, then serve build/site with the Ktor dev preview.
    mainClass.set("dev.pyrlyn.landing.PreviewKt")
}

val siteDir = layout.buildDirectory.dir("site")

/** Static site generation: renders every page into build/site. */
val generateSite by tasks.registering(JavaExec::class) {
    group = "site"
    description = "Generates the static site into build/site."
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("dev.pyrlyn.landing.GenerateKt")
    workingDir = projectDir
    args(siteDir.get().asFile.absolutePath)
    inputs.dir("content")
    inputs.dir("public")
    inputs.dir("src/main/web")
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
