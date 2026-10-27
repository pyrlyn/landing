plugins { alias(libs.plugins.kotlin.jvm) }

kotlin { jvmToolchain(21) }

dependencies {
    testImplementation(libs.playwright)
    testImplementation(kotlin("test-junit5"))
}

val siteDir = rootProject.layout.buildDirectory.dir("site")

// Browser tests are opt-in: `./gradlew build` stays browser-free, `./gradlew e2eTest` runs them.
tasks.test { enabled = false }

/** Downloads Playwright's Chromium into the default cache (~/Library/Caches/ms-playwright, ~/.cache/ms-playwright). */
val playwrightInstall by tasks.registering(JavaExec::class) {
    group = "verification"
    description = "Installs Playwright Chromium (pass -PwithDeps to also install OS packages on Linux)."
    classpath = sourceSets.test.get().runtimeClasspath
    mainClass.set("com.microsoft.playwright.CLI")
    args(listOfNotNull("install", if (project.hasProperty("withDeps")) "--with-deps" else null, "chromium"))
}

val e2eTest by tasks.registering(Test::class) {
    group = "verification"
    description = "Generates the site and runs Playwright e2e tests against it in headless Chromium."
    testClassesDirs = sourceSets.test.get().output.classesDirs
    classpath = sourceSets.test.get().runtimeClasspath
    useJUnitPlatform()
    dependsOn(":generateSite")
    inputs.dir(siteDir)
    systemProperty("e2e.siteDir", siteDir.get().asFile.absolutePath)
    providers.gradleProperty("headed").orNull?.let { systemProperty("e2e.headed", "true") }
    testLogging { events("failed"); exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL }
}
