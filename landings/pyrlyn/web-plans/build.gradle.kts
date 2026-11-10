plugins { alias(libs.plugins.kotlin.multiplatform) }

// The plans and wishlist widget, loaded by site.js only on product pages when API_URL is set: it carries
// the generated backend client (Ktor, coroutines, kotlinx.serialization), which the main bundle must not.
kotlin {
    js {
        outputModuleName.set("web-plans")
        browser {
            commonWebpackConfig { outputFileName = "web-plans.js" }
        }
        // ES modules let webpack scope-hoist the stdlib into one function (no UMD wrappers per module).
        useEsModules()
        binaries.executable()
    }
    sourceSets {
        jsMain.dependencies { implementation(libs.landing.client) }
    }
}
