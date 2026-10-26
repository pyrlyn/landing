// Ported from the Astro site's tailwind-utilities.css. Kotlin is now the source of truth for these styles.
package dev.pyrlyn.landing.styles.sheets

import dev.pyrlyn.landing.styles.at
import dev.pyrlyn.landing.styles.atMedia
import dev.pyrlyn.landing.styles.prop
import dev.pyrlyn.landing.styles.sel
import dev.pyrlyn.landing.styles.atSupports
import kotlinx.css.CssBuilder

/** Styles from tailwind-utilities.css, in source order. */
internal fun CssBuilder.tailwindUtilitiesCss() {
    tailwindUtilitiesCssPart0()
}

private fun CssBuilder.tailwindUtilitiesCssPart0() {
    at("layer utilities") {
        sel(".tw\\:block") {
            prop("display", "block")
        }
        sel(".tw\\:max-w-full") {
            prop("max-width", "100%")
        }
        sel(".tw\\:min-w-0") {
            prop("min-width", "0")
        }
        sel(".tw\\:overflow-x-clip") {
            prop("overflow-x", "clip")
        }
        atMedia("not all and (width>=959px)") {
            sel(".tw\\:max-\\[959px\\]\\:grid-cols-\\[minmax\\(0\\,1fr\\)\\]") {
                prop("grid-template-columns", "minmax(0,1fr)")
            }
        }
        atMedia("not all and (width>=640px)") {
            sel(".tw\\:max-sm\\:py-2") {
                prop("padding-block", "8px")
            }
            sel(".tw\\:max-sm\\:text-center") {
                prop("text-align", "center")
            }
            sel(".tw\\:max-sm\\:whitespace-normal\\!") {
                prop("white-space", "normal!important")
            }
        }
        atMedia("not all and (width>=459px)") {
            sel(".tw\\:max-\\[459px\\]\\:hidden\\!") {
                prop("display", "none!important")
            }
            sel(".tw\\:max-\\[459px\\]\\:gap-2\\!") {
                prop("gap", "8px!important")
            }
            sel(".tw\\:max-\\[459px\\]\\:px-2\\!") {
                prop("padding-inline", "8px!important")
            }
            sel(".tw\\:max-\\[459px\\]\\:px-4\\!") {
                prop("padding-inline", "16px!important")
            }
            sel(".tw\\:max-\\[459px\\]\\:pr-0\\!") {
                prop("padding-right", "0!important")
            }
            sel(".tw\\:max-\\[459px\\]\\:pl-3\\!") {
                prop("padding-left", "12px!important")
            }
        }
        atMedia("not all and (width>=359px)") {
            sel(".tw\\:max-\\[359px\\]\\:gap-1\\!") {
                prop("gap", "4px!important")
            }
            sel(".tw\\:max-\\[359px\\]\\:px-1\\!") {
                prop("padding-inline", "4px!important")
            }
            sel(".tw\\:max-\\[359px\\]\\:px-3\\!") {
                prop("padding-inline", "12px!important")
            }
        }
        atMedia("(width>=460px)") {
            sel(".tw\\:min-\\[460px\\]\\:hidden\\!") {
                prop("display", "none!important")
            }
        }
    }
}
