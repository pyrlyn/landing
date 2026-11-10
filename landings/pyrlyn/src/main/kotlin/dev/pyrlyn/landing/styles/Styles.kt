package dev.pyrlyn.landing.styles

import dev.pyrlyn.landing.build.AssetPipeline
import dev.pyrlyn.landing.styles.sheets.*
import kotlinx.css.CssBuilder

/**
 * The site's stylesheets, written in the kotlin-css DSL (styles/sheets). The rest of the code base
 * only sees these minified strings; kotlinx.css stays an implementation detail of this package.
 */
object Styles {
    /** Every page: global (with tokens, base, brand components), utilities, products, motion, search, light theme. */
    val site: String by lazy {
        sheet { globalCss(); tailwindUtilitiesCss(); productsCss(); motionCss(); searchCss(); lightCss(); wideCss() }
    }

    /** Docs pages only, appended after [site]. */
    val docs: String by lazy { sheet { docsCss() } }

    private fun sheet(block: CssBuilder.() -> Unit): String =
        AssetPipeline.minifyCss(CssBuilder().apply(block).toString())
}
