// Ported from the Astro site's products.css. Kotlin is now the source of truth for these styles.
package dev.pyrlyn.landing.styles.sheets

import dev.pyrlyn.landing.styles.at
import dev.pyrlyn.landing.styles.atMedia
import dev.pyrlyn.landing.styles.prop
import dev.pyrlyn.landing.styles.sel
import dev.pyrlyn.landing.styles.atSupports
import kotlinx.css.CssBuilder

/** Styles from products.css, in source order. */
internal fun CssBuilder.productsCss() {
    productsCssPart0()
}

private fun CssBuilder.productsCssPart0() {
    sel("[data-theme=\"weft\"]") {
        prop("--weave", "repeating-linear-gradient(90deg, color-mix(in oklch, var(--accent) 70%, transparent) 0 6px, transparent 6px 12px),\n           repeating-linear-gradient(0deg, color-mix(in oklch, var(--accent-2) 55%, transparent) 0 2px, transparent 2px 4px)")
    }
    sel("[data-theme=\"weft\"] .p-title span") {
        prop("display", "inline-block")
        prop("width", ".28em")
        prop("height", ".28em")
        prop("margin-left", ".06em")
        prop("vertical-align", "baseline")
        prop("color", "transparent")
        prop("overflow", "hidden")
        prop("border-radius", "2px")
        prop("background", "linear-gradient(90deg, transparent 38%, var(--accent) 38% 62%, transparent 62%), linear-gradient(0deg, transparent 38%, var(--accent-2) 38% 62%, transparent 62%), color-mix(in oklch, var(--accent) 22%, transparent)")
        prop("box-shadow", "inset 0 0 0 1px color-mix(in oklch, var(--accent) 50%, transparent)")
    }
    sel("[data-theme=\"weft\"] .eyebrow") {
        prop("font-family", "var(--font-mono)")
        prop("letter-spacing", ".06em")
        prop("text-transform", "none")
    }
    sel("[data-theme=\"weft\"] .eyebrow::before") {
        prop("content", "\"<\"")
        prop("color", "var(--accent-2)")
    }
    sel("[data-theme=\"weft\"] .eyebrow::after") {
        prop("content", "\"/>\"")
        prop("color", "var(--accent-2)")
    }
    sel("[data-theme=\"weft\"] .section__head::after") {
        prop("content", "\"\"")
        prop("display", "block")
        prop("width", "96px")
        prop("height", "6px")
        prop("border-radius", "2px")
        prop("background", "var(--weave)")
        prop("opacity", ".8")
    }
    sel("[data-theme=\"weft\"] .section__head--center::after") {
        prop("margin-inline", "auto")
    }
    sel("[data-theme=\"weft\"] .feature .icon") {
        prop("border-radius", "6px")
        prop("outline", "1px dashed color-mix(in oklch, var(--accent-2) 55%, transparent)")
        prop("outline-offset", "3px")
    }
    sel("[data-theme=\"weft\"] .weft-chip__attr") {
        prop("color", "var(--accent)")
    }
    sel("[data-theme=\"weft\"] .weft-chip__val") {
        prop("color", "var(--accent-2)")
    }
    sel("[data-theme=\"weft\"] .dnav__item[aria-current=\"page\"]") {
        prop("box-shadow", "none")
        prop("background", "linear-gradient(0deg, var(--accent-2) 50%, transparent 50%) left / 2px 6px repeat-y, color-mix(in oklch, var(--accent) 12%, transparent)")
    }
    sel("[data-theme=\"weft\"] .prose--doc h2::before") {
        prop("content", "\"\"")
        prop("display", "inline-block")
        prop("width", ".5em")
        prop("height", ".5em")
        prop("margin-right", ".45em")
        prop("vertical-align", ".08em")
        prop("border-radius", "2px")
        prop("background", "linear-gradient(90deg, transparent 35%, var(--accent) 35% 65%, transparent 65%), linear-gradient(0deg, transparent 35%, var(--accent-2) 35% 65%, transparent 65%)")
    }
    sel("[data-theme=\"weft\"] .prose--doc :not(pre) > code") {
        prop("color", "color-mix(in oklch, var(--accent-2) 80%, white)")
    }
    sel("[data-theme=\"swarfr\"]") {
        prop("--temper", "linear-gradient(90deg, var(--accent) 0%, #C9824A 38%, #8E6FB8 68%, var(--accent-2) 100%)")
        prop("--ticks", "repeating-linear-gradient(90deg, color-mix(in oklch, var(--accent) 80%, transparent) 0 1px, transparent 1px 8px)")
    }
    sel("[data-theme=\"swarfr\"] .p-title span") {
        prop("background", "var(--temper)")
        prop("-webkit-background-clip", "text")
        prop("background-clip", "text")
        prop("color", "transparent")
    }
    sel("[data-theme=\"swarfr\"] body") {
        prop("font-variant-numeric", "tabular-nums")
    }
    sel("[data-theme=\"swarfr\"] .eyebrow::before") {
        prop("content", "\"\"")
        prop("width", "33px")
        prop("height", "8px")
        prop("background", "var(--ticks), linear-gradient(var(--accent), var(--accent)) left bottom / 100% 1px no-repeat")
        prop("opacity", ".85")
    }
    sel("[data-theme=\"swarfr\"] .section__head::after") {
        prop("content", "\"\"")
        prop("display", "block")
        prop("width", "100%")
        prop("max-width", "220px")
        prop("height", "2px")
        prop("background", "var(--temper)")
        prop("opacity", ".7")
    }
    sel("[data-theme=\"swarfr\"] .section__head--center::after") {
        prop("margin-inline", "auto")
    }
    sel("[data-theme=\"swarfr\"] .feature") {
        prop("clip-path", "polygon(0 0, calc(100% - 18px) 0, 100% 18px, 100% 100%, 0 100%)")
    }
    sel("[data-theme=\"swarfr\"] .feature .icon") {
        prop("border-radius", "4px")
    }
    sel(".swarf-gauge") {
        prop("flex-direction", "column")
        prop("align-items", "stretch")
        prop("gap", "8px")
        prop("width", "280px")
        prop("font-size", ".8rem")
    }
    sel(".swarf-gauge__label") {
        prop("color", "var(--muted)")
        prop("font-size", ".72rem")
        prop("letter-spacing", ".04em")
    }
    sel(".swarf-gauge__row") {
        prop("display", "grid")
        prop("grid-template-columns", "44px 1fr auto")
        prop("align-items", "center")
        prop("gap", "8px")
        prop("font-family", "var(--font-mono)")
    }
    sel(".swarf-gauge__row span") {
        prop("color", "var(--muted)")
    }
    sel(".swarf-gauge__row i") {
        prop("display", "block")
        prop("height", "8px")
        prop("width", "var(--w)")
        prop("border-radius", "2px")
        prop("background", "color-mix(in oklch, var(--fg) 30%, transparent)")
    }
    sel(".swarf-gauge__row--after i") {
        prop("background", "var(--temper, var(--accent))")
    }
    sel(".swarf-gauge__row--after b") {
        prop("color", "var(--accent)")
    }
    sel(".swarf-gauge small") {
        prop("color", "var(--muted)")
        prop("font-size", ".68rem")
    }
    sel("[data-theme=\"swarfr\"] .dnav__item[aria-current=\"page\"]") {
        prop("box-shadow", "none")
        prop("background", "var(--temper) left / 2px 100% no-repeat, color-mix(in oklch, var(--accent) 10%, transparent)")
    }
    sel("[data-theme=\"swarfr\"] .prose--doc h2") {
        prop("padding-top", "var(--s-4)")
        prop("background", "var(--ticks) left top / 64px 6px no-repeat")
    }
    atMedia("(max-width: 640px)") {
        sel(".swarf-gauge") {
            prop("width", "min(260px, 88vw)")
        }
    }
    sel("[data-theme=\"aulo\"]") {
        prop("--wave", "radial-gradient(circle at 50% 50%, var(--accent) 0 2px, transparent 3px) 0 0 / 10px 6px repeat-x")
    }
    sel("[data-theme=\"aulo\"] .p-title span") {
        prop("display", "inline-block")
        prop("width", ".2em")
        prop("height", ".2em")
        prop("margin-left", ".12em")
        prop("border-radius", "50%")
        prop("color", "transparent")
        prop("overflow", "hidden")
        prop("vertical-align", "baseline")
        prop("background", "var(--accent)")
        prop("box-shadow", "0 0 0 .05em var(--bg), 0 0 0 .08em color-mix(in oklch, var(--accent) 60%, transparent), 0 0 0 .14em var(--bg), 0 0 0 .165em color-mix(in oklch, var(--accent-2) 45%, transparent)")
    }
    sel("[data-theme=\"aulo\"] .eyebrow") {
        prop("text-transform", "lowercase")
        prop("letter-spacing", ".08em")
    }
    sel("[data-theme=\"aulo\"] .eyebrow::before") {
        prop("content", "\"\"")
        prop("width", "18px")
        prop("height", "12px")
        prop("background", "linear-gradient(var(--accent), var(--accent)) 0 50% / 2px 40% no-repeat, linear-gradient(var(--accent), var(--accent)) 5px 50% / 2px 100% no-repeat, linear-gradient(var(--accent), var(--accent)) 10px 50% / 2px 65% no-repeat, linear-gradient(var(--accent-2), var(--accent-2)) 15px 50% / 2px 30% no-repeat")
    }
    sel("[data-theme=\"aulo\"] .section__head::after") {
        prop("content", "\"\"")
        prop("display", "block")
        prop("width", "120px")
        prop("height", "6px")
        prop("background", "var(--wave)")
        prop("opacity", ".7")
    }
    sel("[data-theme=\"aulo\"] .section__head--center::after") {
        prop("margin-inline", "auto")
    }
    sel("[data-theme=\"aulo\"] .feature .icon") {
        prop("border-radius", "50%")
    }
    sel("[data-theme=\"aulo\"] .feature") {
        prop("border-radius", "28px")
    }
    sel("[data-theme=\"aulo\"] .dnav__item[aria-current=\"page\"]") {
        prop("border-radius", "999px")
        prop("box-shadow", "inset 0 0 0 1px color-mix(in oklch, var(--accent) 45%, transparent)")
    }
    sel(".aulo-chip") {
        prop("align-items", "center")
        prop("gap", "12px")
    }
    sel(".aulo-chip__wave") {
        prop("display", "inline-flex")
        prop("align-items", "center")
        prop("gap", "3px")
        prop("height", "28px")
    }
    sel(".aulo-chip__wave i") {
        prop("width", "3px")
        prop("border-radius", "2px")
        prop("background", "var(--accent)")
        prop("height", "30%")
    }
    sel(".aulo-chip__wave i:nth-child(2), .aulo-chip__wave i:nth-child(6)") {
        prop("height", "60%")
    }
    sel(".aulo-chip__wave i:nth-child(3), .aulo-chip__wave i:nth-child(5)") {
        prop("height", "85%")
        prop("background", "var(--accent-2)")
    }
    sel(".aulo-chip__wave i:nth-child(4)") {
        prop("height", "100%")
    }
    sel(".aulo-chip strong") {
        prop("display", "block")
        prop("font-family", "var(--font-mono)")
    }
    sel(".aulo-chip small") {
        prop("color", "var(--muted)")
        prop("font-size", ".72rem")
    }
    sel("[data-theme=\"scull\"] .p-title span") {
        prop("display", "inline-block")
        prop("width", ".42em")
        prop("height", ".78em")
        prop("margin-left", ".06em")
        prop("vertical-align", "-.06em")
        prop("background", "var(--accent-2)")
        prop("color", "transparent")
        prop("overflow", "hidden")
        prop("box-shadow", "0 0 .3em color-mix(in oklch, var(--accent-2) 60%, transparent)")
    }
    sel("[data-theme=\"scull\"] .h2, [data-theme=\"scull\"] .prose--doc h2") {
        prop("font-family", "var(--font-mono)")
        prop("letter-spacing", "-.03em")
    }
    sel("[data-theme=\"scull\"] .eyebrow") {
        prop("font-family", "var(--font-mono)")
        prop("color", "var(--accent-2)")
    }
    sel("[data-theme=\"scull\"] .eyebrow::before") {
        prop("content", "\">\"")
    }
    sel("[data-theme=\"scull\"] .stage__art::after") {
        prop("content", "\"\"")
        prop("position", "absolute")
        prop("inset", "0")
        prop("pointer-events", "none")
        prop("background", "repeating-linear-gradient(0deg, rgb(0 0 0 / .22) 0 1px, transparent 1px 3px)")
        prop("border-radius", "inherit")
    }
    sel("[data-theme=\"scull\"] .stage__art") {
        prop("position", "relative")
    }
    sel("html[data-mode=\"light\"] [data-theme=\"scull\"] .stage__art::after, html[data-mode=\"light\"][data-theme=\"scull\"] .stage__art::after") {
        prop("background", "repeating-linear-gradient(0deg, rgb(10 20 35 / .06) 0 1px, transparent 1px 3px)")
    }
    sel("[data-theme=\"scull\"] .feature, [data-theme=\"scull\"] .install-card, [data-theme=\"scull\"] .feature .icon") {
        prop("border-radius", "4px")
    }
    sel("[data-theme=\"scull\"] .btn--primary") {
        prop("color", "#0A0C0B")
    }
    sel("[data-theme=\"kerf\"] .p-title span") {
        prop("display", "inline-block")
        prop("width", ".1em")
        prop("height", ".7em")
        prop("margin-left", ".1em")
        prop("vertical-align", "-.02em")
        prop("background", "var(--accent-2)")
        prop("color", "transparent")
        prop("overflow", "hidden")
        prop("border-radius", "1px")
    }
    sel("[data-theme=\"kerf\"] .eyebrow::after") {
        prop("content", "\"\"")
        prop("width", "48px")
        prop("border-top", "1px dashed var(--accent-2)")
    }
    sel("[data-theme=\"kerf\"] .h2") {
        prop("font-weight", "600")
        prop("letter-spacing", "-.035em")
    }
    sel("[data-theme=\"kerf\"] .feature") {
        prop("position", "relative")
        prop("border-radius", "6px")
    }
    sel("[data-theme=\"kerf\"] .feature::after") {
        prop("content", "\"\"")
        prop("position", "absolute")
        prop("inset", "8px")
        prop("pointer-events", "none")
        prop("background", "linear-gradient(var(--accent), var(--accent)) 0 0 / 12px 1px no-repeat, linear-gradient(var(--accent), var(--accent)) 0 0 / 1px 12px no-repeat, linear-gradient(var(--accent), var(--accent)) 100% 100% / 12px 1px no-repeat, linear-gradient(var(--accent), var(--accent)) 100% 100% / 1px 12px no-repeat")
        prop("opacity", ".6")
    }
    sel("[data-theme=\"kerf\"] .section__head::after") {
        prop("content", "\"\"")
        prop("display", "block")
        prop("width", "160px")
        prop("border-top", "2px dashed color-mix(in oklch, var(--accent-2) 70%, transparent)")
    }
    sel("[data-theme=\"kerf\"] .btn--primary") {
        prop("color", "#0B0C08")
    }
    sel("[data-theme=\"kerf\"] .kerf-chip .ok") {
        prop("color", "var(--accent)")
    }
    sel("[data-theme=\"stator\"] .p-title span") {
        prop("color", "var(--accent)")
        prop("margin-left", ".02em")
    }
    sel("[data-theme=\"stator\"] .p-title span::before") {
        prop("content", "\"⟩\"")
    }
    sel("[data-theme=\"stator\"] .p-title span") {
        prop("font-size", "0")
    }
    sel("[data-theme=\"stator\"] .p-title span::before") {
        prop("font-size", "clamp(3.25rem, 2rem + 6vw, 6rem)")
    }
    sel("[data-theme=\"stator\"] .eyebrow") {
        prop("font-family", "var(--font-mono)")
        prop("letter-spacing", ".1em")
    }
    sel("[data-theme=\"stator\"] .eyebrow::after") {
        prop("content", "\"\"")
        prop("width", "30px")
        prop("height", "8px")
        prop("background", "linear-gradient(90deg, var(--accent) 0 3px, transparent 3px 6px, var(--accent) 6px 9px, var(--accent) 9px 12px, transparent 12px 15px, var(--accent-2) 15px 18px, transparent 18px 21px, var(--accent) 21px 24px, var(--accent) 24px 27px, transparent 27px)")
        prop("opacity", ".85")
    }
    sel("[data-theme=\"stator\"] .section__head::after") {
        prop("content", "\"\"")
        prop("display", "block")
        prop("width", "100%")
        prop("max-width", "280px")
        prop("height", "1px")
        prop("background", "linear-gradient(90deg, var(--accent), transparent)")
    }
    sel("[data-theme=\"stator\"] .section__head--center::after") {
        prop("margin-inline", "auto")
    }
    sel("[data-theme=\"stator\"] .feature") {
        prop("border-radius", "10px")
        prop("box-shadow", "inset 0 0 0 1px color-mix(in oklch, var(--accent-2) 22%, transparent)")
    }
    sel("[data-theme=\"stator\"] .feature .icon") {
        prop("border-radius", "4px")
    }
    sel("[data-theme=\"stator\"] .dnav__item[aria-current=\"page\"]") {
        prop("box-shadow", "inset 2px 0 0 var(--accent), inset 0 -1px 0 color-mix(in oklch, var(--accent) 30%, transparent)")
    }
    sel("[data-theme=\"stator\"] .prose--doc h2::before") {
        prop("content", "\"§ \"")
        prop("font-family", "var(--font-mono)")
        prop("color", "var(--accent)")
        prop("font-weight", "500")
    }
    sel("[data-theme=\"bindsmith\"]") {
        prop("--bs-container", "#2A2218")
        prop("--bs-secondary-container", "#0D2A3A")
    }
    sel("[data-theme=\"bindsmith\"] .p-title span") {
        prop("display", "inline-block")
        prop("width", ".3em")
        prop("height", ".3em")
        prop("margin-left", ".08em")
        prop("vertical-align", "baseline")
        prop("border-radius", ".07em")
        prop("color", "transparent")
        prop("overflow", "hidden")
        prop("background", "radial-gradient(circle at 50% 55%, #E8A05C 0 22%, transparent 23%), #1A1C1F")
        prop("box-shadow", "inset 0 0 0 .025em var(--accent)")
    }
    sel("[data-theme=\"bindsmith\"] .btn") {
        prop("border-radius", "999px")
    }
    sel("[data-theme=\"bindsmith\"] .btn--primary") {
        prop("color", "#fff")
        prop("box-shadow", "none")
    }
    sel("[data-theme=\"bindsmith\"] .eyebrow") {
        prop("padding", "4px 12px")
        prop("border-radius", "8px")
        prop("background", "var(--bs-container)")
        prop("color", "#F5E6D4")
        prop("letter-spacing", ".06em")
        prop("text-transform", "none")
        prop("font-weight", "500")
    }
    sel("[data-theme=\"bindsmith\"] .feature, [data-theme=\"bindsmith\"] .install-card") {
        prop("border-radius", "16px")
        prop("background", "color-mix(in oklch, #22252A 70%, transparent)")
        prop("box-shadow", "inset 0 0 0 1px #3A3F47")
        prop("-webkit-backdrop-filter", "none")
        prop("backdrop-filter", "none")
    }
    sel("[data-theme=\"bindsmith\"] .feature .icon") {
        prop("border-radius", "12px")
        prop("background", "var(--bs-container)")
        prop("box-shadow", "none")
    }
    sel("[data-theme=\"bindsmith\"] .dnav__item[aria-current=\"page\"]") {
        prop("border-radius", "999px")
        prop("background", "var(--bs-secondary-container)")
        prop("box-shadow", "none")
        prop("color", "#E3F2FD")
    }
    sel("[data-theme=\"bindsmith\"] .badge--version") {
        prop("border-radius", "8px")
    }
    sel(".bs-chips") {
        prop("flex-direction", "column")
        prop("align-items", "stretch")
        prop("gap", "8px")
        prop("max-width", "340px")
    }
    sel(".bs-chips small") {
        prop("color", "var(--muted)")
        prop("font-size", ".72rem")
    }
    sel(".bs-chips__row") {
        prop("display", "flex")
        prop("flex-wrap", "wrap")
        prop("gap", "6px")
    }
    sel(".bs-chips__row b") {
        prop("font-weight", "500")
        prop("font-size", ".74rem")
        prop("padding", "4px 10px")
        prop("border-radius", "8px")
        prop("box-shadow", "inset 0 0 0 1px #4A4F57")
    }
    sel(".bs-chips__row b:first-child") {
        prop("background", "var(--bs-secondary-container, #0D2A3A)")
        prop("color", "#E3F2FD")
        prop("box-shadow", "none")
    }
}
