// Ported from the Astro site's brand-components.css. Kotlin is now the source of truth for these styles.
package dev.pyrlyn.landing.styles.sheets

import dev.pyrlyn.landing.styles.at
import dev.pyrlyn.landing.styles.atMedia
import dev.pyrlyn.landing.styles.prop
import dev.pyrlyn.landing.styles.sel
import dev.pyrlyn.landing.styles.atSupports
import kotlinx.css.CssBuilder

/** Styles from brand-components.css, in source order. */
internal fun CssBuilder.brandComponentsCss() {
    brandComponentsCssPart0()
}

private fun CssBuilder.brandComponentsCssPart0() {
    sel(".glass") {
        prop("position", "relative")
        prop("background", "var(--glass-fill), var(--surface-fill)")
        prop("box-shadow", "var(--glass-edge), var(--shadow-2)")
        prop("-webkit-backdrop-filter", "blur(var(--blur-2)) saturate(160%)")
        prop("backdrop-filter", "blur(var(--blur-2)) saturate(160%)")
    }
    sel(".glass--thin") {
        prop("-webkit-backdrop-filter", "blur(var(--blur-1)) saturate(140%)")
        prop("backdrop-filter", "blur(var(--blur-1)) saturate(140%)")
    }
    sel(".glass--thick") {
        prop("-webkit-backdrop-filter", "blur(var(--blur-3)) saturate(180%)")
        prop("backdrop-filter", "blur(var(--blur-3)) saturate(180%)")
        prop("box-shadow", "var(--glass-edge), var(--shadow-3)")
    }
    sel(".shine") {
        prop("isolation", "isolate")
        prop("overflow", "hidden")
    }
    sel(".shine::before") {
        prop("content", "\"\"")
        prop("position", "absolute")
        prop("inset", "0")
        prop("z-index", "-1")
        prop("border-radius", "inherit")
        prop("pointer-events", "none")
        prop("background", "radial-gradient(360px circle at var(--mx, 50%) var(--my, 0%), color-mix(in oklch, var(--card-accent, var(--accent)) 16%, transparent), transparent 55%)")
        prop("opacity", "0")
        prop("transition", "opacity .4s var(--ease)")
    }
    sel(".shine::after") {
        prop("content", "\"\"")
        prop("position", "absolute")
        prop("inset", "0")
        prop("border-radius", "inherit")
        prop("padding", "1px")
        prop("pointer-events", "none")
        prop("background", "radial-gradient(260px circle at var(--mx, 50%) var(--my, 0%), color-mix(in oklch, var(--card-accent, var(--accent)) 70%, white 10%), transparent 60%)")
        prop("-webkit-mask", "linear-gradient(#000 0 0) content-box, linear-gradient(#000 0 0)")
        prop("-webkit-mask-composite", "xor")
        prop("mask", "linear-gradient(#000 0 0) content-box exclude, linear-gradient(#000 0 0)")
        prop("opacity", "0")
        prop("transition", "opacity .4s var(--ease)")
    }
    sel(".has-shine .shine:hover::before, .has-shine .shine:hover::after") {
        prop("opacity", "1")
    }
    sel(".btn") {
        prop("position", "relative")
        prop("display", "inline-flex")
        prop("align-items", "center")
        prop("justify-content", "center")
        prop("gap", "var(--s-2)")
        prop("min-height", "44px")
        prop("padding", "0 var(--s-5)")
        prop("border", "0")
        prop("border-radius", "var(--r-full)")
        prop("font", "inherit")
        prop("font-size", "var(--fs-sm)")
        prop("font-weight", "600")
        prop("letter-spacing", "-.005em")
        prop("white-space", "nowrap")
        prop("cursor", "pointer")
        prop("transition", "translate .18s ease-out, scale .12s ease-out, box-shadow .18s ease-out, background-color .18s ease-out")
    }
    atMedia("(prefers-reduced-motion: no-preference)") {
        sel(".btn:active") {
            prop("scale", ".98")
        }
    }
    sel(".btn:focus-visible") {
        prop("border-radius", "var(--r-full)")
    }
    sel(".btn--lg") {
        prop("min-height", "52px")
        prop("padding", "0 var(--s-6)")
        prop("font-size", "var(--fs-md)")
    }
    sel(".btn--block") {
        prop("width", "100%")
    }
    sel(".btn--primary") {
        prop("color", "var(--on-accent)")
        prop("background", "linear-gradient(180deg, color-mix(in oklch, var(--accent) 78%, white), var(--accent))")
        prop("box-shadow", "inset 0 1px 0 rgb(255 255 255 / .55), inset 0 -1px 0 rgb(0 0 0 / .18), 0 0 0 1px color-mix(in oklch, var(--accent) 60%, black), 0 8px 24px -8px var(--accent-glow)")
    }
    sel(".btn--primary:hover") {
        prop("translate", "0 -1px")
        prop("box-shadow", "inset 0 1px 0 rgb(255 255 255 / .55), inset 0 -1px 0 rgb(0 0 0 / .18), 0 0 0 1px color-mix(in oklch, var(--accent) 60%, black), 0 14px 36px -10px var(--accent-glow)")
    }
    sel(".btn--primary:focus-visible") {
        prop("box-shadow", "var(--focus)")
    }
    sel(".btn[aria-disabled=\"true\"]") {
        prop("opacity", ".55")
        prop("cursor", "not-allowed")
        prop("filter", "saturate(.6)")
    }
    sel(".btn[aria-disabled=\"true\"]:hover") {
        prop("translate", "none")
    }
    sel(".btn[aria-disabled=\"true\"]:active") {
        prop("scale", "none")
    }
    sel(".btn--glass") {
        prop("color", "var(--fg-strong)")
        prop("background", "var(--glass-fill), rgb(255 255 255 / .03)")
        prop("box-shadow", "var(--glass-edge), var(--shadow-1)")
        prop("-webkit-backdrop-filter", "blur(var(--blur-2))")
        prop("backdrop-filter", "blur(var(--blur-2))")
    }
    sel(".btn--glass:hover") {
        prop("translate", "0 -1px")
        prop("background", "var(--glass-fill), rgb(255 255 255 / .07)")
    }
    sel(".btn--glass:focus-visible") {
        prop("box-shadow", "var(--focus)")
    }
    sel(".btn svg") {
        prop("flex", "none")
    }
    sel(".eyebrow") {
        prop("display", "inline-flex")
        prop("align-items", "center")
        prop("gap", "var(--s-2)")
        prop("width", "fit-content")
        prop("font-size", "var(--fs-xs)")
        prop("font-weight", "600")
        prop("letter-spacing", ".14em")
        prop("text-transform", "uppercase")
        prop("color", "var(--accent)")
    }
    sel(".pill") {
        prop("display", "inline-flex")
        prop("align-items", "center")
        prop("gap", "var(--s-2)")
        prop("width", "fit-content")
        prop("min-height", "32px")
        prop("padding", "0 var(--s-3) 0 var(--s-2)")
        prop("border-radius", "var(--r-full)")
        prop("background", "var(--glass-fill), rgb(255 255 255 / .02)")
        prop("box-shadow", "var(--glass-edge)")
        prop("font-size", "var(--fs-xs)")
        prop("font-weight", "500")
        prop("color", "var(--muted)")
    }
    sel(".pill__dot") {
        prop("width", "8px")
        prop("height", "8px")
        prop("border-radius", "50%")
        prop("background", "var(--accent-2)")
        prop("box-shadow", "0 0 0 4px color-mix(in oklch, var(--accent-2) 22%, transparent)")
    }
    sel(".badge") {
        prop("display", "inline-flex")
        prop("align-items", "center")
        prop("gap", "var(--s-1)")
        prop("width", "fit-content")
        prop("padding", "2px var(--s-2)")
        prop("border-radius", "var(--r-full)")
        prop("font-size", "var(--fs-xs)")
        prop("font-weight", "600")
        prop("line-height", "1.5")
        prop("letter-spacing", ".01em")
    }
    sel(".badge--accent") {
        prop("color", "var(--on-accent)")
        prop("background", "var(--accent)")
    }
    sel(".badge--line") {
        prop("color", "var(--muted)")
        prop("box-shadow", "inset 0 0 0 1px var(--hairline-strong)")
    }
    sel(".badge--version") {
        prop("font-family", "var(--font-mono)")
        prop("color", "var(--accent)")
        prop("background", "var(--accent-soft)")
        prop("box-shadow", "inset 0 0 0 1px var(--accent-line)")
    }
    sel(".brand") {
        prop("display", "inline-flex")
        prop("align-items", "center")
        prop("justify-content", "center")
        prop("gap", "var(--s-2)")
        prop("min-height", "44px")
        prop("min-width", "44px")
        prop("padding-right", "var(--s-2)")
        prop("font-weight", "700")
        prop("letter-spacing", "-.02em")
        prop("color", "var(--fg-strong)")
    }
    sel(".brand__mark") {
        prop("width", "26px")
        prop("height", "26px")
        prop("border-radius", "8px")
        prop("flex", "none")
        prop("background", "conic-gradient(from 210deg, var(--accent), var(--accent-2), var(--accent))")
        prop("box-shadow", "inset 0 1px #ffffff66, inset 0 -1px #0004, 0 4px 12px -4px var(--accent-glow)")
        prop("position", "relative")
    }
    sel(".brand__mark::after") {
        prop("content", "\"\"")
        prop("position", "absolute")
        prop("inset", "7px")
        prop("border-radius", "3px")
        prop("background", "var(--bg)")
        prop("opacity", ".8")
    }
    sel(".pyrlyn-by") {
        prop("display", "inline-flex")
        prop("align-items", "center")
        prop("gap", "var(--s-2)")
        prop("min-height", "44px")
        prop("padding-inline", "var(--s-3) var(--s-2)")
        prop("border-radius", "var(--r-full)")
        prop("font-size", "var(--fs-xs)")
        prop("font-weight", "500")
        prop("color", "var(--subtle)")
        prop("position", "relative")
        prop("opacity", ".88")
        prop("transition", "opacity .2s, color .2s")
    }
    sel(".pyrlyn-by::before") {
        prop("content", "\"\"")
        prop("position", "absolute")
        prop("left", "0")
        prop("top", "50%")
        prop("width", "1px")
        prop("height", "20px")
        prop("translate", "0 -50%")
        prop("background", "var(--hairline-strong)")
    }
    sel(".pyrlyn-by:hover, .pyrlyn-by:focus-visible") {
        prop("opacity", "1")
        prop("color", "var(--muted)")
    }
    sel(".pyrlyn-by img") {
        prop("display", "block")
        prop("width", "auto")
        prop("height", "20px")
    }
    sel(".pyrlyn-by--nav") {
        prop("margin-left", "calc(-1 * var(--s-1))")
    }
    sel(".pyrlyn-by--hero") {
        prop("padding-left", "var(--s-4)")
    }
    sel(".pyrlyn-by--hero img") {
        prop("height", "28px")
    }
    sel(".pyrlyn-by--footer img") {
        prop("height", "26px")
    }
}
