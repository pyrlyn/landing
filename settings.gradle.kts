rootProject.name = "landing-kotlin"

dependencyResolutionManagement {
    repositories { mavenCentral() }
}

// Browser code (Kotlin/JS): the site bundle and the lazily loaded WebGL intro.
include(":web", ":web-intro")
