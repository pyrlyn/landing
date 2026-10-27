rootProject.name = "landing-kotlin"

dependencyResolutionManagement {
    repositories { mavenCentral() }
}

// Browser code (Kotlin/JS): the site bundle and the lazily loaded WebGL intro.
include(":web", ":web-intro")

// Browser end-to-end tests (Playwright for Java) against the generated build/site; run with `./gradlew e2eTest`.
include(":e2e")
