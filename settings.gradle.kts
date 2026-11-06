rootProject.name = "landing-kotlin"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        // The landing API's client (backend:landing-client) lives in the GitHub Packages of the private
        // pyrlyn/backend; reading it takes a token with read:packages, even for a public consumer.
        maven("https://maven.pkg.github.com/pyrlyn/backend") {
            name = "backendPackages"
            credentials {
                username = providers.gradleProperty("gpr.user").orElse(providers.environmentVariable("GITHUB_ACTOR")).getOrElse("token")
                password = providers.gradleProperty("gpr.key").orElse(providers.environmentVariable("GITHUB_TOKEN")).orNull
            }
            content { includeGroup("backend") }
        }
    }
}

// `-PbackendDir=../backend` builds the client from a backend checkout instead of the published version,
// so a change to the API can be tried here before it is released; Gradle swaps backend:landing-client
// for the included project by its coordinates.
providers.gradleProperty("backendDir").orNull?.let { includeBuild(it) }

// Browser code (Kotlin/JS): the site bundle, the lazily loaded WebGL intro and the lazily loaded plans
// and wishlist widget, which carries the backend client.
include(":web", ":web-intro", ":web-plans")

// Browser end-to-end tests (Playwright for Java) against the generated build/site; run with `./gradlew e2eTest`.
include(":e2e")
