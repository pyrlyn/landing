// Ported from the Astro site's docs.css. Kotlin is now the source of truth for these styles.
package dev.pyrlyn.landing.styles.sheets

import dev.pyrlyn.landing.styles.at
import dev.pyrlyn.landing.styles.atMedia
import dev.pyrlyn.landing.styles.prop
import dev.pyrlyn.landing.styles.sel
import dev.pyrlyn.landing.styles.atSupports
import kotlinx.css.CssBuilder

/** Styles from docs.css, in source order. */
internal fun CssBuilder.docsCss() {
    docsCssPart0()
    docsCssPart1()
}

private fun CssBuilder.docsCssPart0() {
    sel("html:has(.dnav)") {
        prop("scroll-padding-top", "156px")
    }
    sel(".dnav") {
        prop("--dnav-top", "80px")
        prop("position", "sticky")
        prop("top", "var(--dnav-top)")
        prop("z-index", "45")
        prop("padding", "var(--s-2) var(--s-4) 0")
        prop("animation", "dnav-in 1.5s var(--ease) both")
    }
    at("keyframes dnav-in") {
        sel("from") {
            prop("opacity", "0")
        }
    }
    atMedia("(prefers-reduced-motion: reduce)") {
        sel(".dnav") {
            prop("animation", "none")
        }
    }
    sel(".dnav__inner") {
        prop("position", "relative")
        prop("display", "flex")
        prop("align-items", "center")
        prop("gap", "var(--s-3)")
        prop("max-width", "calc(var(--container) - 2 * var(--s-5))")
        prop("margin-inline", "auto")
        prop("min-height", "56px")
        prop("padding", "var(--s-1) var(--s-2) var(--s-1) var(--s-3)")
        prop("border-radius", "var(--r-lg)")
        prop("background", "linear-gradient(180deg, rgb(255 255 255 / .06), rgb(255 255 255 / .02)), color-mix(in oklch, var(--bg) 70%, transparent)")
        prop("box-shadow", "var(--glass-edge), var(--shadow-2)")
        prop("-webkit-backdrop-filter", "saturate(180%) blur(20px)")
        prop("backdrop-filter", "saturate(180%) blur(20px)")
    }
    sel(".dnav__brand") {
        prop("display", "inline-flex")
        prop("align-items", "center")
        prop("gap", "var(--s-2)")
        prop("min-height", "44px")
        prop("flex", "none")
        prop("padding-right", "var(--s-1)")
        prop("border-radius", "var(--r-sm)")
    }
    sel(".dnav__mark") {
        prop("display", "grid")
        prop("place-items", "center")
        prop("width", "30px")
        prop("height", "30px")
        prop("border-radius", "9px")
        prop("font-family", "var(--font-mono)")
        prop("font-weight", "700")
        prop("font-size", "var(--fs-xs)")
        prop("color", "var(--accent)")
        prop("background", "radial-gradient(51% 92% at 51% 8%, color-mix(in oklch, var(--accent) 28%, transparent), color-mix(in oklch, var(--accent) 6%, transparent))")
        prop("box-shadow", "inset 0 0 0 1px color-mix(in oklch, var(--accent) 34%, transparent), inset 0 1px #ffffff26")
    }
    sel(".dnav__name") {
        prop("font-family", "var(--font-mono)")
        prop("font-weight", "700")
        prop("font-size", "var(--fs-sm)")
        prop("color", "var(--fg-strong)")
        prop("letter-spacing", "-.02em")
        prop("white-space", "nowrap")
    }
    sel(".dnav__name span") {
        prop("font-family", "var(--font-sans)")
        prop("font-weight", "600")
        prop("color", "var(--muted)")
        prop("letter-spacing", "0")
        prop("margin-left", "2px")
    }
    sel(".dnav__sep") {
        prop("width", "1px")
        prop("height", "22px")
        prop("flex", "none")
        prop("background", "var(--hairline-strong)")
    }
    sel(".dnav__scroller") {
        prop("position", "relative")
        prop("flex", "1")
        prop("min-width", "0")
        prop("overflow-x", "auto")
        prop("overscroll-behavior-x", "contain")
        prop("scroll-snap-type", "x proximity")
        prop("scrollbar-width", "none")
        prop("scroll-padding-inline", "24px")
        prop("--fade-l", "0px")
        prop("--fade-r", "0px")
        prop("mask-image", "linear-gradient(90deg, transparent 0, #000 var(--fade-l), #000 calc(100% - var(--fade-r)), transparent 100%)")
        prop("-webkit-mask-image", "linear-gradient(90deg, transparent 0, #000 var(--fade-l), #000 calc(100% - var(--fade-r)), transparent 100%)")
    }
    sel(".dnav__scroller::-webkit-scrollbar") {
        prop("display", "none")
    }
    sel(".dnav__scroller[data-fade-l]") {
        prop("--fade-l", "40px")
    }
    sel(".dnav__scroller[data-fade-r]") {
        prop("--fade-r", "40px")
    }
    sel(".dnav__tabs") {
        prop("position", "relative")
        prop("display", "flex")
        prop("align-items", "center")
        prop("gap", "2px")
        prop("width", "max-content")
        prop("padding", "4px 2px")
    }
    sel(".dnav__tabs li") {
        prop("scroll-snap-align", "start")
    }
    sel(".dnav__gap") {
        prop("margin-left", "var(--s-3)")
        prop("position", "relative")
    }
    sel(".dnav__gap::before") {
        prop("content", "\"\"")
        prop("position", "absolute")
        prop("left", "calc(-1 * var(--s-2) - 1px)")
        prop("top", "50%")
        prop("width", "1px")
        prop("height", "14px")
        prop("translate", "0 -50%")
        prop("background", "var(--hairline-strong)")
    }
    sel(".dnav__tab") {
        prop("position", "relative")
        prop("display", "inline-flex")
        prop("align-items", "center")
        prop("min-height", "44px")
        prop("padding", "0 var(--s-3)")
        prop("border-radius", "var(--r-sm)")
        prop("font-size", "var(--fs-sm)")
        prop("font-weight", "500")
        prop("color", "var(--muted)")
        prop("white-space", "nowrap")
        prop("transition", "color .18s ease-out, background-color .18s ease-out")
    }
    sel(".dnav__tab:hover") {
        prop("color", "var(--fg-strong)")
        prop("background", "rgb(255 255 255 / .05)")
    }
    sel(".dnav__tab:focus-visible") {
        prop("border-radius", "var(--r-sm)")
        prop("box-shadow", "inset 0 0 0 2px var(--accent)")
    }
    sel(".dnav__tab[aria-current=\"page\"]") {
        prop("color", "var(--fg-strong)")
    }
    sel(".dnav__tab[aria-current=\"page\"]::after") {
        prop("content", "\"\"")
        prop("position", "absolute")
        prop("left", "var(--s-3)")
        prop("right", "var(--s-3)")
        prop("bottom", "3px")
        prop("height", "2px")
        prop("border-radius", "2px")
        prop("background", "var(--accent)")
        prop("box-shadow", "0 0 10px var(--accent-glow)")
    }
    sel(".dnav.is-ready .dnav__tab[aria-current=\"page\"]::after") {
        prop("opacity", "0")
    }
    sel(".dnav__indicator") {
        prop("position", "absolute")
        prop("left", "0")
        prop("bottom", "7px")
        prop("width", "1px")
        prop("height", "2px")
        prop("border-radius", "2px")
        prop("pointer-events", "none")
        prop("opacity", "0")
        prop("background", "var(--accent)")
        prop("box-shadow", "0 0 10px var(--accent-glow)")
        prop("transform-origin", "0 50%")
        prop("transform", "translateX(var(--x, 0)) scaleX(var(--w, 0))")
    }
    sel(".dnav.is-ready .dnav__indicator") {
        prop("opacity", "1")
    }
    atMedia("(prefers-reduced-motion: no-preference)") {
        sel(".dnav.is-ready .dnav__indicator") {
            prop("transition", "transform .2s ease-out, opacity .2s ease-out")
        }
    }
    sel(".dnav__sections") {
        prop("display", "none")
        prop("position", "static")
    }
    sel(".dnav__summary") {
        prop("display", "inline-flex")
        prop("align-items", "center")
        prop("gap", "var(--s-2)")
        prop("min-height", "44px")
        prop("padding", "0 var(--s-3)")
        prop("border-radius", "var(--r-sm)")
        prop("font-size", "var(--fs-sm)")
        prop("color", "var(--fg-strong)")
        prop("cursor", "pointer")
        prop("list-style", "none")
        prop("white-space", "nowrap")
        prop("background", "rgb(255 255 255 / .05)")
        prop("box-shadow", "inset 0 0 0 1px var(--hairline)")
    }
    sel(".dnav__summary::-webkit-details-marker") {
        prop("display", "none")
    }
    sel(".dnav__summary-k") {
        prop("color", "var(--muted)")
        prop("font-size", "var(--fs-xs)")
        prop("font-weight", "600")
        prop("letter-spacing", ".1em")
        prop("text-transform", "uppercase")
    }
    sel(".dnav__summary-v") {
        prop("max-width", "34vw")
        prop("overflow", "hidden")
        prop("text-overflow", "ellipsis")
        prop("font-weight", "600")
    }
    sel(".dnav__summary svg") {
        prop("transition", "transform .2s ease-out")
    }
    sel(".dnav__sections[open] .dnav__summary svg") {
        prop("transform", "rotate(180deg)")
    }
    sel(".dnav__summary:focus-visible") {
        prop("border-radius", "var(--r-sm)")
    }
    sel(".dnav__panel") {
        prop("position", "absolute")
        prop("top", "calc(100% + 8px)")
        prop("left", "0")
        prop("right", "0")
        prop("z-index", "5")
        prop("display", "grid")
        prop("gap", "var(--s-2)")
        prop("max-height", "min(70vh, 560px)")
        prop("overflow-y", "auto")
        prop("padding", "var(--s-3)")
        prop("border-radius", "var(--r-lg)")
        prop("background", "var(--glass-fill), color-mix(in oklch, var(--bg) 80%, #1b2130)")
    }
    sel(".dnav__group") {
        prop("display", "grid")
        prop("gap", "2px")
        prop("padding-top", "var(--s-2)")
        prop("border-top", "1px solid var(--hairline)")
    }
    sel(".dnav__group-label") {
        prop("padding", "var(--s-1) var(--s-3)")
        prop("font-size", "var(--fs-xs)")
        prop("font-weight", "600")
        prop("letter-spacing", ".12em")
        prop("text-transform", "uppercase")
        prop("color", "var(--subtle)")
    }
    sel(".dnav__item") {
        prop("display", "flex")
        prop("align-items", "center")
        prop("min-height", "44px")
        prop("padding", "0 var(--s-3)")
        prop("border-radius", "var(--r-sm)")
        prop("font-size", "var(--fs-sm)")
        prop("color", "var(--muted)")
    }
    sel(".dnav__item:hover") {
        prop("color", "var(--fg-strong)")
        prop("background", "rgb(255 255 255 / .05)")
    }
    sel(".dnav__item[aria-current=\"page\"]") {
        prop("color", "var(--fg-strong)")
        prop("background", "color-mix(in oklch, var(--accent) 12%, transparent)")
        prop("box-shadow", "inset 2px 0 0 var(--accent)")
    }
    sel(".dnav__item:focus-visible") {
        prop("border-radius", "var(--r-sm)")
    }
    atMedia("(max-width: 767px)") {
        sel(".dnav") {
            prop("--dnav-top", "76px")
            prop("padding-inline", "var(--s-3)")
        }
        sel(".dnav__inner") {
            prop("min-height", "52px")
        }
        sel(".dnav__name span") {
            prop("display", "none")
        }
        sel(".dnav__scroller--compact") {
            prop("display", "none")
        }
        sel(".dnav__scroller--compact ~ .dnav__sections") {
            prop("display", "block")
            prop("margin-left", "auto")
            prop("min-width", "0")
        }
        sel(".dnav__inner:has(.dnav__scroller--compact) .dnav__sep") {
            prop("display", "none")
        }
        sel(".prose--doc .doc-anchor") {
            prop("display", "none")
        }
    }
    sel(".docs") {
        prop("display", "grid")
        prop("gap", "var(--s-7)")
        prop("padding-block", "var(--s-7) var(--s-8)")
        prop("grid-template-columns", "minmax(0, 1fr)")
    }
    sel(".docs__main") {
        prop("min-width", "0")
        prop("display", "grid")
        prop("grid-template-columns", "minmax(0, 1fr)")
        prop("gap", "var(--s-7)")
    }
    sel(".docs:not(.docs--wide) .docs__main") {
        prop("width", "100%")
        prop("max-width", "76ch")
        prop("margin-inline", "auto")
    }
    atMedia("(min-width: 1280px)") {
        sel(".docs--toc") {
            prop("grid-template-columns", "minmax(0, 1fr) 220px")
            prop("column-gap", "var(--s-8)")
        }
        sel(".docs--toc .docs__main") {
            prop("margin-inline", "auto 0")
            prop("max-width", "76ch")
        }
    }
    sel(".toc") {
        prop("display", "none")
    }
    atMedia("(min-width: 1280px)") {
        sel(".toc") {
            prop("display", "block")
            prop("position", "sticky")
            prop("top", "164px")
            prop("align-self", "start")
            prop("max-height", "calc(100vh - 190px)")
            prop("overflow-y", "auto")
            prop("padding-left", "var(--s-4)")
            prop("border-left", "1px solid var(--hairline)")
        }
    }
    sel(".toc__title") {
        prop("font-size", "var(--fs-xs)")
        prop("font-weight", "600")
        prop("letter-spacing", ".12em")
        prop("text-transform", "uppercase")
        prop("color", "var(--subtle)")
        prop("margin-bottom", "var(--s-2)")
    }
    sel(".toc ul") {
        prop("display", "grid")
        prop("gap", "2px")
    }
    sel(".toc a") {
        prop("position", "relative")
        prop("display", "block")
        prop("padding", "6px var(--s-2)")
        prop("border-radius", "var(--r-xs)")
        prop("font-size", "var(--fs-sm)")
        prop("line-height", "1.4")
        prop("color", "var(--muted)")
        prop("transition", "color .18s ease-out")
    }
    sel(".toc a:hover") {
        prop("color", "var(--fg-strong)")
    }
    sel(".toc a[aria-current=\"true\"]") {
        prop("color", "var(--accent)")
    }
    sel(".toc a[aria-current=\"true\"]::before") {
        prop("content", "\"\"")
        prop("position", "absolute")
        prop("left", "calc(-1 * var(--s-4) - 1px)")
        prop("top", "6px")
        prop("bottom", "6px")
        prop("width", "2px")
        prop("border-radius", "2px")
        prop("background", "var(--accent)")
    }
    sel(".pager") {
        prop("display", "grid")
        prop("gap", "var(--s-3)")
        prop("grid-template-columns", "repeat(2, minmax(0, 1fr))")
        prop("padding-top", "var(--s-5)")
        prop("border-top", "1px solid var(--hairline)")
    }
    sel(".pager__link") {
        prop("display", "grid")
        prop("gap", "2px")
        prop("min-height", "72px")
        prop("align-content", "center")
        prop("padding", "var(--s-3) var(--s-5)")
        prop("border-radius", "var(--r-md)")
    }
    sel(".pager__link small") {
        prop("font-size", "var(--fs-xs)")
        prop("color", "var(--muted)")
    }
    sel(".pager__link strong") {
        prop("color", "var(--fg-strong)")
        prop("font-size", "var(--fs-md)")
    }
    sel(".pager__link--next") {
        prop("grid-column", "2")
        prop("text-align", "right")
    }
    sel(".pager__link:focus-visible") {
        prop("border-radius", "var(--r-md)")
    }
    atMedia("(max-width: 479px)") {
        sel(".pager") {
            prop("grid-template-columns", "minmax(0, 1fr)")
        }
        sel(".pager__link--next") {
            prop("grid-column", "1")
        }
    }
    sel(".doc") {
        prop("display", "grid")
        prop("grid-template-columns", "minmax(0, 1fr)")
        prop("gap", "var(--s-6)")
    }
    sel(".doc__head") {
        prop("display", "grid")
        prop("gap", "var(--s-3)")
        prop("padding-bottom", "var(--s-5)")
        prop("border-bottom", "1px solid var(--hairline)")
    }
    sel(".doc__title") {
        prop("font-size", "clamp(2rem, 1.4rem + 2.2vw, 3rem)")
        prop("font-weight", "700")
        prop("letter-spacing", "-.035em")
        prop("line-height", "1.08")
    }
    sel(".doc__meta") {
        prop("display", "flex")
        prop("flex-wrap", "wrap")
        prop("align-items", "center")
        prop("gap", "var(--s-2) var(--s-4)")
        prop("font-size", "var(--fs-sm)")
        prop("color", "var(--subtle)")
    }
    sel(".doc__meta code") {
        prop("font-size", ".86em")
        prop("color", "var(--muted)")
    }
    sel(".doc__meta a") {
        prop("display", "inline-flex")
        prop("align-items", "center")
        prop("gap", "4px")
        prop("min-height", "44px")
        prop("font-weight", "600")
        prop("color", "var(--accent)")
    }
    sel(".prose--doc") {
        prop("max-width", "70ch")
        prop("font-size", "1.0625rem")
        prop("line-height", "1.75")
        prop("color", "color-mix(in oklch, var(--muted) 70%, var(--fg))")
        prop("gap", "0")
    }
    sel(".prose--doc > * + *") {
        prop("margin-top", "var(--s-4)")
    }
    sel(".prose--doc p, .prose--doc li") {
        prop("text-wrap", "pretty")
    }
    sel(".prose--doc .doc-h") {
        prop("position", "relative")
        prop("scroll-margin-top", "156px")
    }
    sel(".prose--doc h2") {
        prop("margin-top", "var(--s-8)")
        prop("padding-top", "var(--s-2)")
        prop("font-size", "1.6rem")
        prop("letter-spacing", "-.025em")
        prop("line-height", "1.2")
    }
    sel(".prose--doc h3") {
        prop("margin-top", "var(--s-6)")
        prop("font-size", "1.2rem")
        prop("letter-spacing", "-.015em")
        prop("line-height", "1.3")
    }
    sel(".prose--doc h4, .prose--doc h5, .prose--doc h6") {
        prop("margin-top", "var(--s-5)")
        prop("font-size", "1rem")
        prop("color", "var(--fg-strong)")
    }
    sel(".prose--doc > .doc-h:first-child") {
        prop("margin-top", "0")
    }
    sel(".prose--doc h2 + *, .prose--doc h3 + *") {
        prop("margin-top", "var(--s-3)")
    }
    sel(".doc-anchor") {
        prop("position", "absolute")
        prop("left", "-1.1em")
        prop("top", "0")
        prop("bottom", "0")
        prop("width", "1em")
        prop("display", "grid")
        prop("place-items", "center")
        prop("color", "var(--subtle)")
        prop("opacity", "0")
        prop("text-decoration", "none !important")
        prop("transition", "opacity .15s ease-out")
    }
    sel(".doc-anchor::before") {
        prop("content", "\"#\"")
        prop("font-weight", "500")
    }
    sel(".doc-h:hover .doc-anchor, .doc-anchor:focus-visible") {
        prop("opacity", "1")
    }
    sel(".prose--doc a:not(.doc-anchor)") {
        prop("color", "var(--accent)")
        prop("text-decoration", "underline")
        prop("text-underline-offset", "3px")
        prop("text-decoration-color", "var(--accent-line)")
        prop("text-decoration-thickness", "1px")
        prop("transition", "text-decoration-color .18s ease-out")
    }
    sel(".prose--doc a:not(.doc-anchor):hover") {
        prop("text-decoration-color", "currentColor")
    }
    sel(".prose--doc ul, .prose--doc ol") {
        prop("display", "grid")
        prop("gap", "var(--s-2)")
        prop("padding-left", "1.4em")
    }
    sel(".prose--doc ul") {
        prop("list-style", "none")
    }
    sel(".prose--doc ul > li") {
        prop("position", "relative")
    }
    sel(".prose--doc ul > li::before") {
        prop("content", "\"\"")
        prop("position", "absolute")
        prop("left", "-1em")
        prop("top", ".72em")
        prop("width", "6px")
        prop("height", "6px")
        prop("border-radius", "2px")
        prop("background", "color-mix(in oklch, var(--accent) 70%, transparent)")
    }
    sel(".prose--doc ol") {
        prop("list-style", "decimal")
    }
    sel(".prose--doc ol > li::marker") {
        prop("color", "var(--accent)")
        prop("font-weight", "600")
        prop("font-variant-numeric", "tabular-nums")
    }
    sel(".prose--doc li > ul, .prose--doc li > ol") {
        prop("margin-top", "var(--s-2)")
    }
    sel(".prose--doc hr") {
        prop("border", "0")
        prop("height", "1px")
        prop("background", "var(--hairline)")
        prop("margin-block", "var(--s-6)")
    }
    sel(".prose--doc img") {
        prop("max-width", "100%")
        prop("border-radius", "var(--r-md)")
        prop("box-shadow", "var(--glass-edge), var(--shadow-2)")
    }
    sel(".prose--doc strong") {
        prop("color", "var(--fg-strong)")
    }
    sel(".prose--doc :not(pre) > code") {
        prop("font-size", ".84em")
        prop("word-break", "break-word")
    }
    sel(".codeblock--doc .code") {
        prop("font-size", ".8125rem")
        prop("line-height", "1.7")
    }
    sel(".codeblock--doc[data-lang]::before") {
        prop("content", "attr(data-lang)")
        prop("position", "absolute")
        prop("left", "var(--s-4)")
        prop("top", "-9px")
        prop("z-index", "1")
        prop("padding", "0 6px")
        prop("border-radius", "var(--r-xs)")
        prop("font-family", "var(--font-mono)")
        prop("font-size", "10px")
        prop("line-height", "18px")
        prop("letter-spacing", ".06em")
        prop("text-transform", "uppercase")
        prop("color", "var(--muted)")
        prop("background", "color-mix(in oklch, var(--bg) 70%, #1b2130)")
        prop("box-shadow", "inset 0 0 0 1px var(--hairline)")
    }
    sel(".prose--doc .codeblock") {
        prop("margin-top", "var(--s-5)")
    }
    sel(".table-wrap") {
        prop("overflow-x", "auto")
        prop("border-radius", "var(--r-md)")
        prop("box-shadow", "inset 0 0 0 1px var(--hairline)")
        prop("background", "rgb(0 0 0 / .18)")
        prop("scrollbar-width", "thin")
        prop("scrollbar-color", "var(--hairline-strong) transparent")
    }
    sel(".table-wrap:focus-visible") {
        prop("border-radius", "var(--r-md)")
    }
    sel(".table-wrap table") {
        prop("width", "100%")
        prop("border-collapse", "collapse")
        prop("font-size", "var(--fs-sm)")
        prop("line-height", "1.55")
    }
    sel(".table-wrap th") {
        prop("position", "sticky")
        prop("top", "0")
        prop("text-align", "left")
        prop("font-weight", "600")
        prop("color", "var(--fg-strong)")
        prop("background", "rgb(255 255 255 / .04)")
    }
    sel(".table-wrap th, .table-wrap td") {
        prop("padding", "var(--s-2) var(--s-4)")
        prop("border-bottom", "1px solid var(--hairline)")
        prop("vertical-align", "top")
    }
    sel(".table-wrap td") {
        prop("color", "var(--muted)")
        prop("min-width", "8ch")
    }
    sel(".table-wrap tr:last-child td") {
        prop("border-bottom", "0")
    }
    sel(".table-wrap tbody tr") {
        prop("transition", "background-color .15s ease-out")
    }
    sel(".table-wrap tbody tr:hover") {
        prop("background", "rgb(255 255 255 / .025)")
    }
    sel(".table-wrap code") {
        prop("white-space", "nowrap")
    }
    sel(".callout") {
        prop("display", "grid")
        prop("gap", "var(--s-2)")
        prop("padding", "var(--s-4) var(--s-5)")
        prop("border-radius", "var(--r-md)")
        prop("font-size", "var(--fs-sm)")
        prop("line-height", "1.65")
        prop("color", "var(--fg)")
        prop("--c", "var(--accent-2)")
        prop("background", "color-mix(in oklch, var(--c) 8%, transparent)")
        prop("box-shadow", "inset 0 0 0 1px color-mix(in oklch, var(--c) 30%, transparent), inset 3px 0 0 var(--c)")
    }
    sel(".callout p") {
        prop("color", "inherit")
    }
    sel(".callout--tip") {
        prop("--c", "var(--ok)")
    }
    sel(".callout--important") {
        prop("--c", "var(--accent)")
    }
    sel(".callout--warning, .callout--caution") {
        prop("--c", "#ffcf7a")
    }
    sel(".callout__label") {
        prop("display", "inline-flex")
        prop("align-items", "center")
        prop("gap", "var(--s-2)")
        prop("font-size", "var(--fs-xs)")
        prop("font-weight", "700")
        prop("letter-spacing", ".1em")
        prop("text-transform", "uppercase")
        prop("color", "var(--c) !important")
    }
    sel(".dsec") {
        prop("padding-block", "var(--s-6)")
    }
    sel(".dhero") {
        prop("display", "grid")
        prop("grid-template-columns", "minmax(0, 1fr)")
        prop("gap", "var(--s-7)")
        prop("align-items", "center")
        prop("padding-block", "var(--s-5) var(--s-6)")
    }
    atMedia("(min-width: 1024px)") {
        sel(".dhero") {
            prop("grid-template-columns", "minmax(0, 1fr) minmax(0, 1fr)")
            prop("gap", "var(--s-8)")
        }
    }
    sel(".dhero__copy") {
        prop("display", "grid")
        prop("gap", "var(--s-5)")
        prop("justify-items", "start")
        prop("grid-template-columns", "minmax(0, 1fr)")
    }
    sel(".dhero__title") {
        prop("font-size", "clamp(2.2rem, 1.4rem + 3vw, 3.6rem)")
        prop("font-weight", "700")
        prop("letter-spacing", "-.045em")
        prop("line-height", "1.02")
    }
    sel(".dhero__src") {
        prop("font-size", "var(--fs-xs)")
        prop("color", "var(--subtle)")
    }
    sel(".dhero__src a") {
        prop("color", "var(--muted)")
        prop("text-decoration", "underline")
        prop("text-underline-offset", "3px")
        prop("text-decoration-color", "var(--hairline-strong)")
    }
    sel(".dhero__src a:hover") {
        prop("color", "var(--fg-strong)")
    }
}

private fun CssBuilder.docsCssPart1() {
    sel(".cmdstrip") {
        prop("display", "grid")
        prop("grid-template-columns", "minmax(0, 1fr)")
        prop("gap", "var(--s-2)")
        prop("padding", "var(--s-3)")
        prop("border-radius", "var(--r-xl)")
        prop("counter-reset", "c")
        prop("min-width", "0")
    }
    sel(".cmdstrip__row") {
        prop("display", "grid")
        prop("grid-template-columns", "auto minmax(0, 1fr)")
        prop("align-items", "center")
        prop("column-gap", "var(--s-3)")
        prop("row-gap", "6px")
        prop("padding", "var(--s-3)")
        prop("border-radius", "var(--r-md)")
        prop("transition", "background-color .18s ease-out")
    }
    sel(".cmdstrip__row:hover") {
        prop("background", "rgb(255 255 255 / .03)")
    }
    sel(".cmdstrip__n") {
        prop("grid-row", "span 2")
        prop("align-self", "start")
        prop("padding-top", "2px")
        prop("font-family", "var(--font-mono)")
        prop("font-size", "var(--fs-xs)")
        prop("font-weight", "700")
        prop("color", "var(--accent)")
    }
    sel(".cmdstrip__label") {
        prop("font-size", "var(--fs-xs)")
        prop("font-weight", "600")
        prop("letter-spacing", ".12em")
        prop("text-transform", "uppercase")
        prop("color", "var(--muted)")
    }
    sel(".cmdstrip .code") {
        prop("font-size", ".78rem")
        prop("padding-block", "16px")
        prop("white-space", "pre")
    }
    sel(".cmdstrip .codeblock--doc::before, .rail__body .codeblock--doc::before, .gs__body .codeblock--doc::before") {
        prop("display", "none")
    }
    sel(".rail") {
        prop("position", "relative")
        prop("display", "grid")
        prop("gap", "var(--s-5)")
        prop("grid-template-columns", "minmax(0, 1fr)")
        prop("counter-reset", "r")
    }
    atMedia("(min-width: 1024px)") {
        sel(".rail") {
            prop("grid-template-columns", "repeat(4, minmax(0, 1fr))")
        }
    }
    sel(".rail__step") {
        prop("position", "relative")
        prop("display", "grid")
        prop("grid-template-columns", "minmax(0, 1fr)")
        prop("gap", "var(--s-4)")
        prop("align-content", "start")
    }
    sel(".rail__n") {
        prop("position", "relative")
        prop("z-index", "1")
        prop("display", "grid")
        prop("place-items", "center")
        prop("width", "44px")
        prop("height", "44px")
        prop("border-radius", "50%")
        prop("font-family", "var(--font-mono)")
        prop("font-weight", "700")
        prop("color", "var(--on-accent)")
        prop("background", "radial-gradient(51% 92% at 51% 8%, color-mix(in oklch, var(--accent) 70%, white), var(--accent))")
        prop("box-shadow", "inset 0 1px #fff8, 0 0 0 6px color-mix(in oklch, var(--accent) 14%, transparent), 0 8px 22px -8px var(--accent-glow)")
    }
    sel(".rail__step::before") {
        prop("content", "\"\"")
        prop("position", "absolute")
        prop("background", "linear-gradient(90deg, var(--accent-line), transparent)")
    }
    atMedia("(min-width: 1024px)") {
        sel(".rail__step::before") {
            prop("left", "56px")
            prop("right", "calc(-1 * var(--s-5))")
            prop("top", "22px")
            prop("height", "1px")
        }
    }
    atMedia("(max-width: 1023px)") {
        sel(".rail__step") {
            prop("grid-template-columns", "44px minmax(0, 1fr)")
            prop("column-gap", "var(--s-4)")
        }
        sel(".rail__step::before") {
            prop("left", "22px")
            prop("top", "52px")
            prop("bottom", "calc(-1 * var(--s-5))")
            prop("width", "1px")
            prop("background", "linear-gradient(180deg, var(--accent-line), transparent)")
        }
    }
    sel(".rail__step:last-child::before") {
        prop("display", "none")
    }
    sel(".rail__body") {
        prop("display", "grid")
        prop("grid-template-columns", "minmax(0, 1fr)")
        prop("gap", "var(--s-2)")
        prop("min-width", "0")
    }
    sel(".rail__body p") {
        prop("font-size", "var(--fs-sm)")
        prop("color", "var(--muted)")
        prop("line-height", "1.65")
    }
    sel(".rail__body .codeblock") {
        prop("margin-top", "var(--s-2)")
    }
    sel(".rail__body .code") {
        prop("font-size", ".75rem")
        prop("padding", "16px var(--s-4)")
        prop("padding-right", "88px")
    }
    sel(".split") {
        prop("display", "grid")
        prop("grid-template-columns", "minmax(0, 1fr)")
        prop("gap", "var(--s-6)")
        prop("align-items", "center")
    }
    atMedia("(min-width: 1024px)") {
        sel(".split") {
            prop("grid-template-columns", "minmax(0, .95fr) minmax(0, 1.05fr)")
            prop("gap", "var(--s-8)")
        }
    }
    sel(".split__copy") {
        prop("display", "grid")
        prop("grid-template-columns", "minmax(0, 1fr)")
        prop("gap", "var(--s-4)")
        prop("min-width", "0")
    }
    sel(".prose--tight") {
        prop("font-size", "var(--fs-md)")
        prop("gap", "var(--s-3)")
    }
    sel(".prose--tight ol") {
        prop("display", "grid")
        prop("gap", "var(--s-2)")
        prop("padding-left", "1.3em")
        prop("list-style", "decimal")
    }
    sel(".prose--tight ol li::marker") {
        prop("color", "var(--accent)")
        prop("font-weight", "700")
    }
    sel(".split__code") {
        prop("margin", "0")
        prop("display", "grid")
        prop("grid-template-columns", "minmax(0, 1fr)")
        prop("gap", "var(--s-3)")
        prop("padding", "var(--s-3)")
        prop("border-radius", "var(--r-lg)")
        prop("min-width", "0")
    }
    sel(".split__code figcaption") {
        prop("padding", "0 var(--s-2) var(--s-1)")
        prop("font-size", "var(--fs-xs)")
        prop("color", "var(--subtle)")
    }
    sel(".split__code .code") {
        prop("font-size", ".78rem")
    }
    sel(".bento") {
        prop("display", "grid")
        prop("gap", "var(--s-4)")
        prop("grid-template-columns", "minmax(0, 1fr)")
    }
    atMedia("(min-width: 768px)") {
        sel(".bento") {
            prop("grid-template-columns", "repeat(2, minmax(0, 1fr))")
            prop("grid-auto-flow", "dense")
        }
    }
    atMedia("(min-width: 1100px)") {
        sel(".bento") {
            prop("grid-template-columns", "repeat(3, minmax(0, 1fr))")
        }
    }
    sel(".bento__tile") {
        prop("--card-accent", "var(--accent)")
        prop("display", "grid")
        prop("gap", "var(--s-3)")
        prop("align-content", "start")
        prop("padding", "var(--s-5)")
        prop("border-radius", "var(--r-lg)")
        prop("transition", "translate .2s ease-out, box-shadow .2s ease-out")
    }
    atMedia("(min-width: 768px) and (max-width: 1099px)") {
        sel(".bento__tile") {
            prop("grid-column", "var(--c2)")
        }
    }
    atMedia("(min-width: 1100px)") {
        sel(".bento__tile") {
            prop("grid-column", "var(--c3)")
            prop("grid-row", "var(--r3)")
        }
        sel(".bento__tile--cols .bento__list") {
            prop("grid-template-columns", "repeat(2, minmax(0, 1fr))")
            prop("column-gap", "var(--s-6)")
        }
    }
    sel(".bento__tile--lead") {
        prop("background", "radial-gradient(80% 70% at 0% 0%, color-mix(in oklch, var(--accent) 16%, transparent), transparent 70%), var(--glass-fill), var(--surface-fill)")
    }
    sel(".bento__label") {
        prop("display", "flex")
        prop("justify-content", "space-between")
        prop("align-items", "baseline")
        prop("gap", "var(--s-3)")
        prop("font-size", "var(--fs-xs)")
        prop("font-weight", "700")
        prop("letter-spacing", ".12em")
        prop("text-transform", "uppercase")
        prop("color", "var(--accent)")
    }
    sel(".bento__label span") {
        prop("color", "var(--subtle)")
        prop("font-weight", "500")
        prop("letter-spacing", ".02em")
        prop("text-transform", "none")
    }
    sel(".bento__list") {
        prop("display", "grid")
        prop("grid-template-columns", "minmax(0, 1fr)")
        prop("gap", "2px")
    }
    sel(".bento__link") {
        prop("position", "relative")
        prop("z-index", "1")
        prop("display", "grid")
        prop("gap", "2px")
        prop("min-height", "44px")
        prop("align-content", "center")
        prop("padding", "var(--s-2) var(--s-3)")
        prop("margin-inline", "calc(-1 * var(--s-3))")
        prop("border-radius", "var(--r-sm)")
        prop("transition", "background-color .18s ease-out")
    }
    sel(".bento__link strong") {
        prop("color", "var(--fg-strong)")
        prop("font-size", "var(--fs-md)")
        prop("font-weight", "600")
    }
    sel(".bento__link strong span") {
        prop("display", "inline-block")
        prop("color", "var(--accent)")
        prop("opacity", "0")
        prop("transform", "translateX(-4px)")
        prop("transition", "opacity .18s ease-out, transform .18s ease-out")
    }
    sel(".bento__link:hover") {
        prop("background", "rgb(255 255 255 / .05)")
    }
    sel(".bento__link:hover strong span, .bento__link:focus-visible strong span") {
        prop("opacity", "1")
        prop("transform", "none")
    }
    sel(".bento__link small") {
        prop("font-size", "var(--fs-sm)")
        prop("line-height", "1.55")
        prop("color", "var(--muted)")
        prop("display", "-webkit-box")
        prop("-webkit-line-clamp", "3")
        prop("-webkit-box-orient", "vertical")
        prop("overflow", "hidden")
    }
    sel(".bento__link:focus-visible") {
        prop("border-radius", "var(--r-sm)")
    }
    sel(".bento__tile--lead .bento__link strong") {
        prop("font-size", "var(--fs-lg)")
    }
    sel(".duo") {
        prop("display", "grid")
        prop("grid-template-columns", "minmax(0, 1fr)")
        prop("gap", "var(--s-5)")
        prop("align-items", "start")
    }
    atMedia("(min-width: 1024px)") {
        sel(".duo") {
            prop("grid-template-columns", "minmax(0, 1.4fr) minmax(0, .9fr)")
            prop("gap", "var(--s-7)")
        }
    }
    sel(".duo__table") {
        prop("display", "grid")
        prop("grid-template-columns", "minmax(0, 1fr)")
        prop("gap", "var(--s-3)")
        prop("min-width", "0")
    }
    sel(".duo__title") {
        prop("font-size", "1.5rem")
        prop("letter-spacing", "-.02em")
        prop("margin-bottom", "var(--s-2)")
    }
    sel(".duo__notice") {
        prop("align-self", "stretch")
        prop("align-content", "start")
        prop("padding", "var(--s-5) var(--s-6)")
        prop("font-size", "var(--fs-md)")
    }
    atMedia("(min-width: 1024px)") {
        sel(".duo__notice") {
            prop("margin-top", "56px")
        }
    }
    sel(".faq--docs") {
        prop("max-width", "820px")
    }
    sel(".faq--docs .faq__item") {
        prop("transition", "box-shadow .2s ease-out")
    }
    sel(".faq--docs .faq__item:hover") {
        prop("box-shadow", "var(--glass-edge), var(--shadow-2), 0 0 0 1px var(--accent-line)")
    }
    sel(".faq--docs .faq__item summary:focus-visible") {
        prop("border-radius", "var(--r-md)")
    }
    sel(".nextcards") {
        prop("display", "grid")
        prop("gap", "var(--s-4)")
        prop("grid-template-columns", "minmax(0, 1fr)")
    }
    atMedia("(min-width: 900px)") {
        sel(".nextcards") {
            prop("grid-template-columns", "repeat(3, minmax(0, 1fr))")
        }
    }
    sel(".nextcard") {
        prop("--card-accent", "var(--accent)")
        prop("display", "grid")
        prop("gap", "var(--s-2)")
        prop("height", "100%")
        prop("align-content", "start")
        prop("padding", "var(--s-5)")
        prop("border-radius", "var(--r-lg)")
    }
    sel(".nextcard__n") {
        prop("font-family", "var(--font-mono)")
        prop("font-size", "2rem")
        prop("font-weight", "700")
        prop("letter-spacing", "-.06em")
        prop("line-height", "1")
        prop("background", "linear-gradient(180deg, color-mix(in oklch, var(--accent) 80%, white), transparent 120%)")
        prop("-webkit-background-clip", "text")
        prop("background-clip", "text")
        prop("color", "transparent")
    }
    sel(".nextcard strong") {
        prop("color", "var(--fg-strong)")
        prop("font-size", "var(--fs-lg)")
    }
    sel(".nextcard small") {
        prop("color", "var(--muted)")
        prop("font-size", "var(--fs-sm)")
        prop("line-height", "1.55")
        prop("display", "-webkit-box")
        prop("-webkit-line-clamp", "3")
        prop("-webkit-box-orient", "vertical")
        prop("overflow", "hidden")
    }
    sel(".nextcard__go") {
        prop("margin-top", "var(--s-2)")
        prop("font-size", "var(--fs-sm)")
        prop("font-weight", "600")
        prop("color", "var(--accent)")
    }
    sel(".nextcard:focus-visible") {
        prop("border-radius", "var(--r-lg)")
    }
    sel(".gs") {
        prop("display", "grid")
        prop("grid-template-columns", "minmax(0, 1fr)")
        prop("gap", "var(--s-7)")
        prop("max-width", "960px")
        prop("margin-inline", "auto")
        prop("width", "100%")
    }
    sel(".gs__head") {
        prop("display", "grid")
        prop("gap", "var(--s-4)")
        prop("justify-items", "start")
    }
    sel(".gs__rail") {
        prop("position", "relative")
        prop("display", "grid")
        prop("grid-template-columns", "minmax(0, 1fr)")
        prop("gap", "var(--s-6)")
    }
    sel(".gs__step") {
        prop("position", "relative")
        prop("display", "grid")
        prop("grid-template-columns", "88px minmax(0, 1fr)")
        prop("column-gap", "var(--s-5)")
    }
    sel(".gs__step::before") {
        prop("content", "\"\"")
        prop("position", "absolute")
        prop("left", "43px")
        prop("top", "92px")
        prop("bottom", "calc(-1 * var(--s-6))")
        prop("width", "2px")
        prop("border-radius", "2px")
        prop("background", "linear-gradient(180deg, var(--accent), color-mix(in oklch, var(--accent) 10%, transparent))")
        prop("opacity", ".55")
    }
    sel(".gs__step:last-child::before") {
        prop("display", "none")
    }
    sel(".gs__n") {
        prop("font-family", "var(--font-mono)")
        prop("font-size", "5rem")
        prop("font-weight", "700")
        prop("line-height", "1")
        prop("letter-spacing", "-.08em")
        prop("text-align", "center")
        prop("background", "linear-gradient(180deg, color-mix(in oklch, var(--accent) 85%, white), color-mix(in oklch, var(--accent) 30%, transparent))")
        prop("-webkit-background-clip", "text")
        prop("background-clip", "text")
        prop("color", "transparent")
        prop("filter", "drop-shadow(0 8px 24px color-mix(in oklch, var(--accent) 35%, transparent))")
    }
    sel(".gs__body") {
        prop("display", "grid")
        prop("grid-template-columns", "minmax(0, 1fr)")
        prop("gap", "var(--s-3)")
        prop("padding", "var(--s-5)")
        prop("border-radius", "var(--r-lg)")
        prop("min-width", "0")
    }
    atMedia("(min-width: 768px)") {
        sel(".gs__body") {
            prop("padding", "var(--s-6)")
        }
    }
    sel(".gs__title") {
        prop("font-size", "1.35rem")
    }
    sel(".gs__body > p") {
        prop("color", "var(--muted)")
        prop("font-size", "var(--fs-md)")
        prop("line-height", "1.65")
    }
    sel(".gs__body code:not(pre code)") {
        prop("font-size", ".86em")
        prop("padding", "1px 6px")
        prop("border-radius", "var(--r-xs)")
        prop("background", "rgb(255 255 255 / .06)")
        prop("box-shadow", "inset 0 0 0 1px var(--hairline)")
        prop("color", "var(--fg-strong)")
    }
    sel(".gs__alt summary") {
        prop("display", "inline-flex")
        prop("align-items", "center")
        prop("min-height", "44px")
        prop("font-size", "var(--fs-sm)")
        prop("font-weight", "600")
        prop("color", "var(--accent)")
        prop("cursor", "pointer")
        prop("border-radius", "var(--r-xs)")
    }
    sel(".gs__alt[open]") {
        prop("display", "grid")
        prop("grid-template-columns", "minmax(0, 1fr)")
        prop("gap", "var(--s-2)")
    }
    sel(".gs__term") {
        prop("margin-top", "var(--s-2)")
    }
    sel(".gs__out") {
        prop("min-height", "0")
        prop("white-space", "pre")
        prop("font-size", ".84rem")
        prop("line-height", "1.85")
    }
    sel(".gs__p") {
        prop("color", "var(--accent)")
        prop("user-select", "none")
    }
    sel(".gs__dim") {
        prop("color", "var(--muted)")
    }
    sel(".gs__warn") {
        prop("color", "#ff8f7a")
    }
    sel(".gs__ok") {
        prop("color", "var(--accent)")
    }
    sel(".gs__next") {
        prop("display", "grid")
        prop("gap", "var(--s-4)")
    }
    sel(".gs__links") {
        prop("display", "grid")
        prop("gap", "var(--s-3)")
        prop("grid-template-columns", "minmax(0, 1fr)")
    }
    atMedia("(min-width: 768px)") {
        sel(".gs__links") {
            prop("grid-template-columns", "repeat(3, minmax(0, 1fr))")
        }
    }
    sel(".gs__links a") {
        prop("--card-accent", "var(--accent)")
        prop("display", "grid")
        prop("gap", "4px")
        prop("height", "100%")
        prop("padding", "var(--s-4) var(--s-5)")
        prop("border-radius", "var(--r-md)")
        prop("transition", "translate .2s ease-out, box-shadow .2s ease-out")
    }
    sel(".gs__links strong") {
        prop("color", "var(--fg-strong)")
    }
    sel(".gs__links small") {
        prop("color", "var(--muted)")
        prop("font-size", "var(--fs-sm)")
        prop("line-height", "1.5")
    }
    sel(".gs__links a:focus-visible") {
        prop("border-radius", "var(--r-md)")
    }
    atMedia("(max-width: 639px)") {
        sel(".gs__step") {
            prop("grid-template-columns", "minmax(0, 1fr)")
            prop("row-gap", "var(--s-2)")
            prop("padding-left", "52px")
        }
        sel(".gs__n") {
            prop("position", "absolute")
            prop("left", "0")
            prop("top", "0")
            prop("width", "40px")
            prop("font-size", "2.6rem")
        }
        sel(".gs__step::before") {
            prop("left", "19px")
            prop("top", "56px")
        }
    }
    atMedia("(prefers-reduced-motion: no-preference)") {
        sel(".js-reveal .gs__step, .js-reveal .rail__step") {
            prop("opacity", "0")
            prop("transform", "translateY(12px)")
            prop("transition", "opacity .45s ease-out, transform .45s ease-out")
            prop("transition-delay", "calc(var(--i, 0) * 70ms)")
        }
        sel(".js-reveal .gs__step.is-in, .js-reveal .rail__step.is-in") {
            prop("opacity", "1")
            prop("transform", "none")
        }
    }
    atMedia("(prefers-reduced-transparency: reduce)") {
        sel(".dnav__inner") {
            prop("background", "var(--surface-opaque)")
            prop("-webkit-backdrop-filter", "none")
            prop("backdrop-filter", "none")
        }
    }
    atSupports("not ((backdrop-filter: blur(1px)) or (-webkit-backdrop-filter: blur(1px)))") {
        sel(".dnav__inner") {
            prop("background", "var(--surface-opaque)")
        }
    }
    atMedia("(forced-colors: active)") {
        sel(".dnav__inner, .callout, .table-wrap") {
            prop("border", "1px solid CanvasText")
        }
        sel(".dnav__tab[aria-current=\"page\"]") {
            prop("text-decoration", "underline")
        }
    }
}
