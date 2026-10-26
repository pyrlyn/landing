plugins { alias(libs.plugins.kotlin.multiplatform) }

// Browser-only, progressive enhancement: IR, production webpack (DCE + terser), one IIFE script.
kotlin {
    js {
        outputModuleName.set("web-intro")
        browser {
            commonWebpackConfig { outputFileName = "web-intro.js" }
        }
        // ES modules let webpack scope-hoist the stdlib into one function (no UMD wrappers per module).
        useEsModules()
        binaries.executable()
    }
}
