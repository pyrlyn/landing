// Ported from the Astro site's global.css. Kotlin is now the source of truth for these styles.
package dev.pyrlyn.landing.styles.sheets

import dev.pyrlyn.landing.styles.at
import dev.pyrlyn.landing.styles.atMedia
import dev.pyrlyn.landing.styles.prop
import dev.pyrlyn.landing.styles.sel
import dev.pyrlyn.landing.styles.atSupports
import kotlinx.css.CssBuilder

/** Styles from global.css, in source order. */
internal fun CssBuilder.globalCss() {
    globalCssPart0()
    globalCssPart1()
    globalCssPart2()
    globalCssPart3()
    globalCssPart4()
}

private fun CssBuilder.globalCssPart0() {
    brandTokensCss()
    baseCss()
    brandComponentsCss()
    sel(".badge--placeholder") {
        prop("color", "#ffcf7a")
        prop("background", "rgb(255 191 90 / .1)")
        prop("box-shadow", "inset 0 0 0 1px rgb(255 191 90 / .45)")
    }
    sel(".badge--placeholder::before") {
        prop("content", "\"\"")
        prop("width", "6px")
        prop("height", "6px")
        prop("border-radius", "50%")
        prop("background", "currentColor")
    }
    sel(".display") {
        prop("font-size", "var(--fs-display)")
        prop("font-weight", "700")
        prop("line-height", "1.0")
        prop("letter-spacing", "-.045em")
    }
    sel(".h2") {
        prop("font-size", "var(--fs-h2)")
        prop("font-weight", "680")
        prop("line-height", "1.06")
        prop("letter-spacing", "-.035em")
    }
    sel(".h3") {
        prop("font-size", "var(--fs-h3)")
        prop("font-weight", "620")
        prop("letter-spacing", "-.015em")
        prop("line-height", "1.3")
    }
    sel(".lead") {
        prop("font-size", "clamp(1.0625rem, .95rem + .45vw, 1.25rem)")
        prop("line-height", "1.55")
        prop("color", "var(--muted)")
        prop("max-width", "38rem")
    }
    sel(".muted") {
        prop("color", "var(--muted)")
    }
    sel(".small") {
        prop("font-size", "var(--fs-sm)")
    }
    sel(".gradient-text") {
        prop("background", "linear-gradient(100deg, #fff 0%, color-mix(in oklch, var(--accent) 70%, white) 40%, var(--accent-2) 100%)")
        prop("-webkit-background-clip", "text")
        prop("background-clip", "text")
        prop("color", "transparent")
    }
    sel(".inline-code, .prose :not(pre) > code, .card :not(pre) > code") {
        prop("font-size", ".86em")
        prop("padding", "1px 6px")
        prop("border-radius", "var(--r-xs)")
        prop("background", "rgb(255 255 255 / .06)")
        prop("box-shadow", "inset 0 0 0 1px var(--hairline)")
        prop("color", "var(--fg-strong)")
    }
    sel(".icon") {
        prop("display", "grid")
        prop("place-items", "center")
        prop("width", "40px")
        prop("height", "40px")
        prop("flex", "none")
        prop("border-radius", "var(--r-sm)")
        prop("color", "var(--card-accent, var(--accent))")
        prop("background", "radial-gradient(51% 92% at 51% 8%, color-mix(in oklch, var(--card-accent, var(--accent)) 26%, transparent), color-mix(in oklch, var(--card-accent, var(--accent)) 6%, transparent))")
        prop("box-shadow", "inset 0 0 0 1px color-mix(in oklch, var(--card-accent, var(--accent)) 32%, transparent), inset 0 1px #ffffff26")
    }
    sel(".codeblock") {
        prop("position", "relative")
    }
    sel(".code") {
        prop("margin", "0")
        prop("overflow-x", "auto")
        prop("white-space", "pre")
        prop("padding", "var(--s-4) var(--s-5)")
        prop("padding-right", "96px")
        prop("border-radius", "var(--r-md)")
        prop("background", "linear-gradient(180deg, rgb(0 0 0 / .35), rgb(0 0 0 / .5))")
        prop("box-shadow", "inset 0 0 0 1px var(--hairline), inset 0 1px 0 rgb(255 255 255 / .04)")
        prop("font-size", ".8125rem")
        prop("line-height", "1.75")
        prop("color", "var(--fg)")
        prop("scrollbar-width", "thin")
        prop("scrollbar-color", "var(--hairline-strong) transparent")
    }
    sel(".code:focus-visible") {
        prop("border-radius", "var(--r-md)")
    }
    sel(".codeblock:has(.copy:not([hidden]))::after") {
        prop("content", "\"\"")
        prop("position", "absolute")
        prop("top", "1px")
        prop("right", "1px")
        prop("bottom", "1px")
        prop("width", "120px")
        prop("pointer-events", "none")
        prop("border-radius", "0 var(--r-md) var(--r-md) 0")
        prop("background", "linear-gradient(90deg, transparent, rgb(10 12 17 / .92) 55%)")
    }
    sel(".codeblock .copy") {
        prop("z-index", "1")
    }
    sel(".tok-p") {
        prop("color", "var(--accent)")
        prop("user-select", "none")
    }
    sel(".tok-c") {
        prop("color", "var(--subtle)")
    }
    sel(".copy") {
        prop("position", "absolute")
        prop("top", "6px")
        prop("right", "6px")
        prop("display", "inline-flex")
        prop("align-items", "center")
        prop("gap", "6px")
        prop("min-height", "44px")
        prop("min-width", "44px")
        prop("padding", "0 var(--s-3)")
        prop("border", "0")
        prop("border-radius", "var(--r-sm)")
        prop("cursor", "pointer")
        prop("font", "inherit")
        prop("font-size", "var(--fs-xs)")
        prop("font-weight", "600")
        prop("color", "var(--muted)")
        prop("background", "var(--glass-fill), rgb(20 24 34 / .9)")
        prop("box-shadow", "var(--glass-edge)")
        prop("transition", "color .2s, background-color .2s")
    }
    sel(".copy:hover") {
        prop("color", "var(--fg-strong)")
    }
    sel(".copy[data-state=\"copied\"]") {
        prop("color", "var(--ok)")
    }
    sel(".copy[hidden]") {
        prop("display", "none")
    }
    atMedia("(hover: hover)") {
        sel(".codeblock .copy, .codeblock::after") {
            prop("opacity", "0")
            prop("transition", "opacity .15s ease-out")
        }
        sel(".codeblock .copy") {
            prop("transition", "opacity .15s ease-out, color .18s ease-out, background-color .18s ease-out, scale .12s ease-out")
        }
        sel(".codeblock:hover .copy, .codeblock:focus-within .copy, .codeblock .copy[data-state=\"copied\"], .codeblock:hover::after, .codeblock:focus-within::after, .codeblock:has(.copy[data-state=\"copied\"])::after") {
            prop("opacity", "1")
        }
    }
    atMedia("(hover: none)") {
        sel(".codeblock:has(.copy:not([hidden]))") {
            prop("display", "flex")
            prop("align-items", "center")
            prop("gap", "var(--s-2)")
        }
        sel(".codeblock:has(.copy:not([hidden])) .code") {
            prop("flex", "1")
            prop("min-width", "0")
            prop("padding-right", "var(--s-5)")
        }
        sel(".codeblock:has(.copy:not([hidden]))::after") {
            prop("display", "none")
        }
        sel(".codeblock .copy") {
            prop("position", "static")
            prop("flex", "none")
        }
    }
    atMedia("(prefers-reduced-motion: reduce)") {
        sel(".codeblock .copy, .codeblock::after") {
            prop("transition", "none")
        }
    }
    sel(".container") {
        prop("width", "100%")
        prop("max-width", "var(--container)")
        prop("margin-inline", "auto")
        prop("padding-inline", "var(--s-5)")
    }
    atMedia("(min-width: 768px)") {
        sel(".container") {
            prop("padding-inline", "var(--s-6)")
        }
    }
    sel(".container--narrow") {
        prop("max-width", "880px")
    }
    sel(".section") {
        prop("padding-block", "var(--s-9)")
    }
    atMedia("(min-width: 1024px)") {
        sel(".section") {
            prop("padding-block", "var(--s-10)")
        }
    }
    sel(".section--tight") {
        prop("padding-block", "var(--s-8)")
    }
    sel(".section__head") {
        prop("display", "grid")
        prop("gap", "var(--s-4)")
        prop("max-width", "680px")
        prop("margin-bottom", "var(--s-7)")
    }
    sel(".section__head--center") {
        prop("margin-inline", "auto")
        prop("text-align", "center")
        prop("justify-items", "center")
    }
    sel(".section__head .lead") {
        prop("font-size", "var(--fs-lg)")
    }
    sel(".grid") {
        prop("display", "grid")
        prop("gap", "var(--s-4)")
        prop("grid-template-columns", "minmax(0, 1fr)")
    }
    sel(".grid > *, .install-grid > *, .usage > *, .pricing > *, .hero > *, .stats > *") {
        prop("min-width", "0")
    }
    atMedia("(min-width: 640px)") {
        sel(".grid") {
            prop("gap", "var(--s-5)")
        }
        sel(".grid--2, .grid--3") {
            prop("grid-template-columns", "repeat(2, minmax(0, 1fr))")
        }
    }
    atMedia("(min-width: 1024px)") {
        sel(".grid--3") {
            prop("grid-template-columns", "repeat(3, minmax(0, 1fr))")
        }
    }
    sel(".card") {
        prop("display", "grid")
        prop("grid-template-columns", "minmax(0, 1fr)")
        prop("align-content", "start")
        prop("gap", "var(--s-3)")
        prop("padding", "var(--s-5)")
        prop("border-radius", "var(--r-lg)")
    }
    atMedia("(min-width: 768px)") {
        sel(".card") {
            prop("padding", "var(--s-6)")
        }
    }
    sel(".card p") {
        prop("font-size", "var(--fs-sm)")
        prop("line-height", "1.65")
        prop("color", "var(--muted)")
    }
    sel(".card--lift") {
        prop("transition", "translate .2s ease-out, box-shadow .2s ease-out")
    }
    sel(".card--lift:hover") {
        prop("translate", "0 -2px")
        prop("box-shadow", "var(--glass-edge), var(--shadow-3), 0 0 0 1px color-mix(in oklch, var(--card-accent, var(--accent)) 32%, transparent)")
    }
    sel(".nav") {
        prop("position", "sticky")
        prop("top", "0")
        prop("z-index", "50")
        prop("padding", "var(--s-3) var(--s-4) 0")
    }
    sel(".nav__inner") {
        prop("display", "flex")
        prop("align-items", "center")
        prop("gap", "var(--s-4)")
        prop("max-width", "calc(var(--container) - 2 * var(--s-5))")
        prop("margin-inline", "auto")
        prop("min-height", "60px")
        prop("padding", "var(--s-2) var(--s-2) var(--s-2) var(--s-4)")
        prop("border-radius", "var(--r-full)")
        prop("background", "linear-gradient(180deg, rgb(255 255 255 / .07), rgb(255 255 255 / .02)), color-mix(in oklch, var(--bg) 62%, transparent)")
        prop("box-shadow", "var(--glass-edge), var(--shadow-2)")
        prop("-webkit-backdrop-filter", "saturate(180%) blur(20px)")
        prop("backdrop-filter", "saturate(180%) blur(20px)")
    }
    sel(".nav__links") {
        prop("display", "none")
        prop("align-items", "center")
        prop("gap", "2px")
        prop("margin-right", "auto")
    }
    sel(".nav__links a:not(.menu__item)") {
        prop("display", "inline-flex")
        prop("align-items", "center")
        prop("gap", "6px")
        prop("min-height", "44px")
        prop("padding", "0 var(--s-3)")
        prop("border-radius", "var(--r-full)")
        prop("font-size", "var(--fs-sm)")
        prop("font-weight", "500")
        prop("color", "var(--muted)")
        prop("transition", "color .2s, background-color .2s")
    }
    sel(".nav__links a:hover") {
        prop("color", "var(--fg-strong)")
        prop("background", "rgb(255 255 255 / .05)")
    }
    sel(".nav__links a[aria-current=\"page\"]") {
        prop("color", "var(--fg-strong)")
        prop("background", "rgb(255 255 255 / .07)")
        prop("box-shadow", "inset 0 0 0 1px var(--hairline)")
    }
    sel(".nav__links .dot") {
        prop("width", "6px")
        prop("height", "6px")
        prop("border-radius", "50%")
    }
    sel(".nav__sep") {
        prop("width", "1px")
        prop("height", "20px")
        prop("background", "var(--hairline-strong)")
        prop("margin-inline", "var(--s-2)")
    }
    sel(".nav__actions") {
        prop("display", "flex")
        prop("gap", "var(--s-2)")
        prop("margin-left", "auto")
    }
    sel(".nav__gh") {
        prop("display", "none")
    }
    atMedia("(min-width: 900px)") {
        sel(".nav__links") {
            prop("display", "flex")
        }
    }
    atMedia("(min-width: 600px)") {
        sel(".nav__gh") {
            prop("display", "inline-grid")
        }
    }
    sel(".nav__gh") {
        prop("place-items", "center")
        prop("width", "44px")
        prop("height", "44px")
        prop("border-radius", "var(--r-full)")
        prop("flex", "none")
        prop("color", "var(--muted)")
        prop("background", "transparent")
        prop("box-shadow", "inset 0 0 0 1px var(--hairline)")
        prop("transition", "color .2s, background-color .2s")
    }
    sel(".nav__gh:hover") {
        prop("color", "var(--fg-strong)")
        prop("background", "var(--hover-1, rgb(255 255 255 / .05))")
    }
    sel(".nav__gh:focus-visible") {
        prop("outline", "none")
        prop("box-shadow", "var(--focus)")
    }
    sel(":root") {
        prop("--header-autohide", "4s")
        prop("--nav-strip", "52px")
    }
    sel(".nav--auto") {
        prop("pointer-events", "none")
    }
    sel(".nav--auto .nav__inner, .nav__peek-pill") {
        prop("pointer-events", "auto")
    }
    sel(".nav--auto .nav__inner") {
        prop("transition", "transform .45s var(--ease), opacity .3s var(--ease)")
    }
    sel(".nav--auto[data-collapsed]:not(:hover, :has(:focus-visible), [data-pinned]) .nav__inner") {
        prop("transform", "translateY(calc(-100% - var(--s-4)))")
        prop("opacity", "0")
        prop("transition-delay", "var(--header-autohide)")
    }
    sel(".nav__peek") {
        prop("position", "absolute")
        prop("inset", "var(--s-2) var(--s-4) auto")
        prop("max-width", "calc(var(--container) - 2 * var(--s-5))")
        prop("margin-inline", "auto")
        prop("pointer-events", "none")
    }
    sel(".nav__peek-pill") {
        prop("display", "flex")
        prop("align-items", "center")
        prop("gap", "var(--s-1)")
        prop("width", "fit-content")
        prop("height", "44px")
        prop("padding", "0 0 0 var(--s-3)")
        prop("border-radius", "var(--r-full)")
        prop("opacity", "0")
        prop("transition", "opacity .3s var(--ease)")
    }
    sel(".nav__peek-pill .brand svg") {
        prop("height", "20px")
        prop("width", "auto")
    }
    sel(".nav--auto[data-collapsed]:not(:hover, :has(:focus-visible), [data-pinned]) .nav__peek-pill") {
        prop("opacity", "1")
        prop("transition-delay", "var(--header-autohide)")
    }
    sel(".nav--auto .nav__peek-pill:has(:focus-visible)") {
        prop("opacity", "1")
    }
    sel(".nav__peek-btn") {
        prop("display", "inline-grid")
        prop("place-items", "center")
        prop("width", "44px")
        prop("height", "44px")
        prop("padding", "0")
        prop("border", "0")
        prop("border-radius", "var(--r-full)")
        prop("cursor", "pointer")
        prop("color", "var(--muted)")
        prop("background", "transparent")
        prop("transition", "color .2s, background-color .2s")
    }
    sel(".nav__peek .nav__peek-btn svg") {
        prop("transform", "rotate(180deg)")
    }
    sel(".nav__unpin") {
        prop("display", "none")
        prop("flex", "none")
        prop("box-shadow", "inset 0 0 0 1px var(--hairline)")
    }
    sel(".nav--auto[data-pinned] .nav__unpin") {
        prop("display", "inline-grid")
    }
    atMedia("(max-width: 599px)") {
        sel(".nav--auto[data-pinned] .nav__unpin") {
            prop("display", "none")
        }
    }
    sel(".nav__peek-btn:hover") {
        prop("color", "var(--fg-strong)")
        prop("background", "var(--hover-1, rgb(255 255 255 / .05))")
    }
    sel(".nav__peek-btn:focus-visible") {
        prop("box-shadow", "var(--focus)")
    }
    sel("html:has(.nav--auto) .dnav") {
        prop("transition", "transform .45s var(--ease)")
    }
    sel("html:has(.nav--auto[data-collapsed]) .dnav") {
        prop("transform", "translateY(calc(var(--nav-strip) - var(--dnav-top)))")
        prop("transition-delay", "var(--header-autohide)")
    }
    sel("html:has(.nav--auto:hover, .nav--auto :focus-visible, .nav--auto[data-pinned]) .dnav") {
        prop("transform", "none")
        prop("transition-delay", "0s")
    }
    atMedia("(hover: none)") {
        sel(".nav--auto[data-collapsed]:not(:has(:focus-visible), [data-pinned]) .nav__inner") {
            prop("transform", "translateY(calc(-100% - var(--s-4)))")
            prop("opacity", "0")
            prop("transition-delay", "var(--header-autohide)")
        }
        sel(".nav--auto[data-collapsed]:not(:has(:focus-visible), [data-pinned]) .nav__peek-pill") {
            prop("opacity", "1")
            prop("transition-delay", "var(--header-autohide)")
        }
    }
    atMedia("(prefers-reduced-motion: reduce)") {
        sel(".nav--auto .nav__inner, .nav__peek-pill, html:has(.nav--auto) .dnav") {
            prop("transition", "none !important")
            prop("transition-delay", "0s !important")
        }
    }
    sel(".hero") {
        prop("display", "grid")
        prop("gap", "var(--s-8)")
        prop("align-items", "center")
        prop("padding-block", "var(--s-8) var(--s-8)")
    }
    atMedia("(min-width: 1024px)") {
        sel(".hero") {
            prop("grid-template-columns", "minmax(0, 1.12fr) minmax(0, 1fr)")
            prop("gap", "var(--s-6)")
            prop("padding-block", "var(--s-9)")
            prop("min-height", "min(860px, calc(100vh - 72px))")
        }
    }
    sel(".hero__copy") {
        prop("display", "grid")
        prop("grid-template-columns", "minmax(0, 1fr)")
        prop("gap", "var(--s-5)")
        prop("justify-items", "start")
        prop("position", "relative")
        prop("z-index", "2")
    }
    sel(".hero__ctas") {
        prop("display", "flex")
        prop("flex-wrap", "wrap")
        prop("gap", "var(--s-3)")
        prop("margin-top", "var(--s-2)")
    }
    sel(".hero__note") {
        prop("display", "flex")
        prop("flex-wrap", "wrap")
        prop("gap", "var(--s-2) var(--s-5)")
        prop("font-size", "var(--fs-sm)")
        prop("color", "var(--muted)")
    }
    sel(".hero__note li") {
        prop("display", "inline-flex")
        prop("align-items", "center")
        prop("gap", "var(--s-2)")
    }
    sel(".hero__note svg") {
        prop("color", "var(--accent-2)")
    }
    sel(".stage") {
        prop("position", "relative")
        prop("aspect-ratio", "3 / 2")
        prop("width", "100%")
        prop("perspective", "1400px")
    }
    sel(".stage__tilt") {
        prop("position", "absolute")
        prop("inset", "0")
        prop("transform-style", "preserve-3d")
        prop("transform", "rotateX(var(--rx)) rotateY(var(--ry))")
        prop("transition", "--rx .5s var(--ease), --ry .5s var(--ease)")
    }
    sel(".stage__art") {
        prop("position", "absolute")
        prop("inset", "-8% -14% -8% -10%")
        prop("z-index", "0")
        prop("mix-blend-mode", "screen")
        prop("mask-image", "radial-gradient(closest-side, #000 55%, transparent 100%)")
        prop("-webkit-mask-image", "radial-gradient(closest-side, #000 55%, transparent 100%)")
    }
    sel(".stage__art img") {
        prop("width", "100%")
        prop("height", "100%")
        prop("object-fit", "cover")
    }
    sel(".stage__art picture") {
        prop("height", "100%")
    }
    sel(".layer") {
        prop("position", "absolute")
        prop("z-index", "1")
        prop("will-change", "translate")
        prop("transition", "translate .6s var(--ease)")
    }
    sel(".float") {
        prop("animation", "float 7s ease-in-out infinite")
    }
    sel(".float--slow") {
        prop("animation-duration", "9s")
        prop("animation-delay", "-3s")
    }
    at("keyframes float") {
        sel("0%, 100%") {
            prop("transform", "translate3d(0, 0, 0)")
        }
        sel("50%") {
            prop("transform", "translate3d(0, -12px, 0)")
        }
    }
    sel(".chip") {
        prop("display", "flex")
        prop("align-items", "center")
        prop("gap", "var(--s-3)")
        prop("padding", "var(--s-3) var(--s-4)")
        prop("border-radius", "var(--r-md)")
        prop("font-size", "var(--fs-sm)")
        prop("line-height", "1.3")
        prop("color", "var(--fg)")
    }
    sel(".chip strong") {
        prop("display", "block")
        prop("color", "var(--fg-strong)")
        prop("font-weight", "600")
    }
    sel(".chip small") {
        prop("display", "block")
        prop("color", "var(--muted)")
        prop("font-size", "var(--fs-xs)")
    }
    sel(".toggle-chip") {
        prop("padding", "var(--s-2)")
        prop("gap", "var(--s-2)")
        prop("flex-direction", "column")
        prop("align-items", "stretch")
        prop("border-radius", "var(--r-lg)")
        prop("width", "248px")
    }
    sel(".toggle-chip__label") {
        prop("padding", "var(--s-1) var(--s-2) 0")
        prop("font-size", "var(--fs-xs)")
        prop("color", "var(--muted)")
        prop("letter-spacing", ".12em")
        prop("text-transform", "uppercase")
        prop("font-weight", "600")
    }
    sel(".segmented") {
        prop("display", "grid")
        prop("grid-template-columns", "1fr 1fr")
        prop("gap", "2px")
        prop("padding", "3px")
        prop("border-radius", "var(--r-full)")
        prop("background", "rgb(0 0 0 / .35)")
        prop("box-shadow", "inset 0 0 0 1px var(--hairline)")
    }
    sel(".segmented button") {
        prop("min-height", "44px")
        prop("border", "0")
        prop("border-radius", "var(--r-full)")
        prop("cursor", "pointer")
        prop("font", "inherit")
        prop("font-size", "var(--fs-sm)")
        prop("font-weight", "600")
        prop("color", "var(--muted)")
        prop("background", "transparent")
        prop("transition", "color .2s, background-color .25s var(--ease), box-shadow .25s var(--ease)")
    }
    sel(".segmented button[aria-pressed=\"true\"]") {
        prop("color", "var(--fg-strong)")
        prop("background", "var(--glass-fill), rgb(255 255 255 / .08)")
        prop("box-shadow", "var(--glass-edge), var(--shadow-1)")
    }
    sel(".segmented button:focus-visible") {
        prop("border-radius", "var(--r-full)")
    }
    sel(".toggle-chip__body") {
        prop("display", "grid")
        prop("gap", "2px")
        prop("padding", "var(--s-2) var(--s-2) var(--s-1)")
        prop("font-size", "var(--fs-xs)")
        prop("color", "var(--muted)")
    }
    sel(".toggle-chip__body strong") {
        prop("color", "var(--fg-strong)")
        prop("font-size", "var(--fs-sm)")
    }
    sel("[data-audience=\"pro\"] .for-oss, [data-audience=\"oss\"] .for-pro") {
        prop("display", "none")
    }
    sel(".stage__hotspots") {
        prop("position", "absolute")
        prop("inset", "-8% -14% -8% -10%")
        prop("z-index", "2")
        prop("pointer-events", "none")
    }
    sel(".hotspot") {
        prop("position", "absolute")
        prop("translate", "-50% -50%")
        prop("pointer-events", "auto")
    }
    sel(".stage__hotspots:has([aria-expanded=\"true\"])") {
        prop("z-index", "6")
    }
    sel(".hotspot__dot") {
        prop("position", "relative")
        prop("display", "grid")
        prop("place-items", "center")
        prop("width", "44px")
        prop("height", "44px")
        prop("border", "0")
        prop("border-radius", "50%")
        prop("background", "transparent")
        prop("cursor", "pointer")
    }
    sel(".hotspot__dot::before") {
        prop("content", "\"\"")
        prop("width", "14px")
        prop("height", "14px")
        prop("border-radius", "50%")
        prop("background", "radial-gradient(51% 92% at 51% 8%, #fff, var(--card-accent))")
        prop("box-shadow", "0 0 0 4px color-mix(in oklch, var(--card-accent) 30%, transparent), 0 0 18px var(--card-accent)")
    }
    sel(".hotspot__dot::after") {
        prop("content", "\"\"")
        prop("position", "absolute")
        prop("inset", "8px")
        prop("border-radius", "50%")
        prop("box-shadow", "0 0 0 1.5px color-mix(in oklch, var(--card-accent) 70%, transparent)")
        prop("animation", "ping 2.4s var(--ease) infinite")
    }
    sel(".hotspot__dot[aria-expanded=\"true\"]::after") {
        prop("animation", "none")
        prop("opacity", "0")
    }
    sel(".hotspot__dot:focus-visible") {
        prop("border-radius", "50%")
    }
    at("keyframes ping") {
        sel("0%") {
            prop("transform", "scale(.6)")
            prop("opacity", "1")
        }
        sel("100%") {
            prop("transform", "scale(1.6)")
            prop("opacity", "0")
        }
    }
}

private fun CssBuilder.globalCssPart1() {
    sel(".hotspot__tip") {
        prop("position", "absolute")
        prop("top", "calc(100% + 6px)")
        prop("right", "-12px")
        prop("z-index", "5")
        prop("width", "260px")
        prop("background", "var(--glass-fill), color-mix(in oklch, var(--bg) 70%, #1e2536 30%)")
        prop("display", "grid")
        prop("gap", "var(--s-2)")
        prop("padding", "var(--s-4)")
        prop("border-radius", "var(--r-md)")
        prop("font-size", "var(--fs-sm)")
        prop("line-height", "1.5")
        prop("color", "var(--fg)")
    }
    sel(".hotspot__tip[hidden]") {
        prop("display", "none")
    }
    sel(".hotspot__kicker") {
        prop("font-size", "var(--fs-xs)")
        prop("font-weight", "600")
        prop("letter-spacing", ".12em")
        prop("text-transform", "uppercase")
        prop("color", "var(--card-accent)")
    }
    sel(".hotspot__tip a") {
        prop("display", "inline-flex")
        prop("align-items", "center")
        prop("gap", "6px")
        prop("min-height", "44px")
        prop("margin-bottom", "-10px")
        prop("font-weight", "600")
        prop("color", "var(--card-accent)")
    }
    atMedia("(max-width: 639px)") {
        sel(".hotspot__tip") {
            prop("width", "220px")
            prop("right", "-40px")
        }
    }
    sel(".oss-card") {
        prop("--card-accent", "var(--accent)")
        prop("gap", "var(--s-4)")
    }
    sel(".oss-card > :not(.oss-card__preview)") {
        prop("position", "relative")
        prop("z-index", "1")
    }
    sel(".oss-card__preview") {
        prop("position", "absolute")
        prop("top", "-36px")
        prop("right", "-36px")
        prop("width", "200px")
        prop("z-index", "0")
        prop("pointer-events", "none")
        prop("mix-blend-mode", "screen")
        prop("opacity", ".28")
        prop("mask-image", "radial-gradient(closest-side, #000 45%, transparent 100%)")
        prop("-webkit-mask-image", "radial-gradient(closest-side, #000 45%, transparent 100%)")
        prop("transform", "perspective(600px) rotateX(0) rotateY(0) scale(.9)")
        prop("transition", "opacity .5s var(--ease), transform .6s var(--ease)")
    }
    sel(".oss-card:hover .oss-card__preview, .oss-card:focus-within .oss-card__preview") {
        prop("opacity", ".75")
        prop("transform", "perspective(600px) rotateX(8deg) rotateY(-14deg) translate3d(-8px, 10px, 0) scale(1.05)")
    }
    sel(".oss-card:focus-within") {
        prop("translate", "0 -2px")
        prop("box-shadow", "var(--glass-edge), var(--shadow-3)")
    }
    sel(".oss-card .oss-card__top .badge") {
        prop("margin-right", "0")
    }
    sel(".oss-card__top") {
        prop("display", "flex")
        prop("align-items", "center")
        prop("justify-content", "space-between")
        prop("gap", "var(--s-3)")
    }
    sel(".oss-card__title") {
        prop("font-family", "var(--font-mono)")
        prop("font-size", "1.5rem")
        prop("font-weight", "700")
        prop("letter-spacing", "-.03em")
    }
    sel(".oss-card__title span") {
        prop("color", "var(--card-accent)")
    }
    sel(".oss-card .code") {
        prop("font-size", ".75rem")
        prop("padding", "var(--s-3) var(--s-4)")
        prop("overflow", "hidden")
        prop("text-overflow", "ellipsis")
    }
    sel(".oss-card__link") {
        prop("display", "inline-flex")
        prop("align-items", "center")
        prop("gap", "var(--s-2)")
        prop("min-height", "44px")
        prop("margin-top", "auto")
        prop("font-size", "var(--fs-sm)")
        prop("font-weight", "600")
        prop("color", "var(--card-accent)")
    }
    sel(".oss-card__link::after") {
        prop("content", "\"\"")
        prop("position", "absolute")
        prop("inset", "0")
        prop("border-radius", "inherit")
        prop("z-index", "1")
    }
    sel(".oss-card__link:focus-visible") {
        prop("box-shadow", "none")
    }
    sel(".oss-card:has(.oss-card__link:focus-visible)") {
        prop("box-shadow", "var(--glass-edge), var(--focus)")
    }
    sel(".oss-card .icon") {
        prop("font-family", "var(--font-mono)")
        prop("font-weight", "700")
        prop("font-size", "var(--fs-sm)")
    }
    sel(".section-flag") {
        prop("display", "flex")
        prop("flex-wrap", "wrap")
        prop("align-items", "center")
        prop("gap", "var(--s-3)")
    }
    sel(".logos") {
        prop("display", "flex")
        prop("flex-wrap", "wrap")
        prop("justify-content", "center")
        prop("gap", "var(--s-4) var(--s-7)")
        prop("padding-block", "var(--s-5) var(--s-7)")
    }
    sel(".logos li") {
        prop("font-size", "var(--fs-lg)")
        prop("font-weight", "700")
        prop("letter-spacing", "-.02em")
        prop("color", "var(--subtle)")
        prop("display", "inline-flex")
        prop("align-items", "center")
        prop("gap", "var(--s-2)")
    }
    sel(".logos li::before") {
        prop("content", "\"\"")
        prop("width", "18px")
        prop("height", "18px")
        prop("border-radius", "5px")
        prop("background", "currentColor")
        prop("opacity", ".5")
    }
    sel(".stats") {
        prop("display", "grid")
        prop("gap", "var(--s-4)")
        prop("margin-bottom", "var(--s-5)")
        prop("grid-template-columns", "1fr")
    }
    atMedia("(min-width: 640px)") {
        sel(".stats") {
            prop("grid-template-columns", "repeat(3, 1fr)")
            prop("gap", "var(--s-5)")
        }
    }
    sel(".stat") {
        prop("display", "grid")
        prop("gap", "var(--s-1)")
        prop("padding", "var(--s-5) var(--s-6)")
        prop("border-radius", "var(--r-lg)")
    }
    sel(".stat__num") {
        prop("font-size", "2.25rem")
        prop("font-weight", "700")
        prop("letter-spacing", "-.04em")
        prop("line-height", "1.1")
        prop("color", "var(--fg-strong)")
        prop("font-variant-numeric", "tabular-nums")
    }
    sel(".quote") {
        prop("height", "100%")
        prop("gap", "var(--s-5)")
        prop("align-content", "space-between")
    }
    sel(".quote blockquote p") {
        prop("font-size", "var(--fs-md)")
        prop("color", "var(--fg)")
        prop("line-height", "1.6")
    }
    sel(".quote figcaption") {
        prop("display", "flex")
        prop("align-items", "center")
        prop("gap", "var(--s-3)")
        prop("font-size", "var(--fs-sm)")
        prop("line-height", "1.35")
    }
    sel(".quote figcaption strong") {
        prop("display", "block")
        prop("color", "var(--fg-strong)")
    }
    sel(".quote figcaption small") {
        prop("color", "var(--muted)")
        prop("font-size", "var(--fs-xs)")
    }
    sel(".avatar") {
        prop("display", "grid")
        prop("place-items", "center")
        prop("width", "40px")
        prop("height", "40px")
        prop("flex", "none")
        prop("border-radius", "50%")
        prop("font-size", "var(--fs-xs)")
        prop("font-weight", "700")
        prop("color", "var(--on-accent)")
        prop("background", "radial-gradient(51% 92% at 51% 8%, color-mix(in oklch, var(--accent) 70%, white), var(--accent))")
        prop("box-shadow", "inset 0 1px #fff6")
    }
    sel(".avatar--2") {
        prop("background", "radial-gradient(51% 92% at 51% 8%, color-mix(in oklch, var(--accent-2) 70%, white), var(--accent-2))")
    }
    sel(".avatar--3") {
        prop("background", "radial-gradient(51% 92% at 51% 8%, #e8ecf3, #9aa6b8)")
    }
    sel(".footnote") {
        prop("margin-top", "var(--s-5)")
        prop("font-size", "var(--fs-xs)")
        prop("color", "var(--subtle)")
    }
    sel(".pricing") {
        prop("display", "grid")
        prop("gap", "var(--s-5)")
        prop("grid-template-columns", "1fr")
        prop("align-items", "stretch")
    }
    atMedia("(min-width: 960px)") {
        sel(".pricing") {
            prop("grid-template-columns", "repeat(3, 1fr)")
            prop("align-items", "center")
        }
    }
    sel(".tier") {
        prop("gap", "var(--s-4)")
        prop("height", "100%")
    }
    sel(".tier__head") {
        prop("display", "flex")
        prop("align-items", "center")
        prop("justify-content", "space-between")
        prop("gap", "var(--s-3)")
        prop("flex-wrap", "wrap")
    }
    sel(".price") {
        prop("display", "flex")
        prop("align-items", "baseline")
        prop("gap", "var(--s-2)")
        prop("flex-wrap", "wrap")
    }
    sel(".price__num") {
        prop("font-size", "2.75rem")
        prop("font-weight", "700")
        prop("letter-spacing", "-.045em")
        prop("line-height", "1")
        prop("color", "var(--fg-strong)")
    }
    sel(".checklist") {
        prop("display", "grid")
        prop("gap", "var(--s-3)")
        prop("padding-top", "var(--s-4)")
        prop("margin-block", "var(--s-2) var(--s-4)")
        prop("border-top", "1px solid var(--hairline)")
        prop("font-size", "var(--fs-sm)")
    }
    sel(".checklist li") {
        prop("display", "flex")
        prop("gap", "var(--s-3)")
        prop("align-items", "flex-start")
    }
    sel(".checklist svg") {
        prop("flex", "none")
        prop("margin-top", "3px")
        prop("color", "var(--accent-2)")
    }
    sel(".tier .btn") {
        prop("margin-top", "auto")
    }
    sel(".tier--featured") {
        prop("background", "linear-gradient(180deg, color-mix(in oklch, var(--accent) 14%, transparent), rgb(255 255 255 / .02) 60%), var(--surface-fill)")
        prop("box-shadow", "var(--glass-edge), var(--shadow-3), var(--glow)")
    }
    atMedia("(min-width: 960px)") {
        sel(".tier--featured") {
            prop("padding-block", "var(--s-8)")
        }
    }
    sel(".faq") {
        prop("display", "grid")
        prop("gap", "var(--s-3)")
    }
    sel(".faq__item") {
        prop("border-radius", "var(--r-md)")
        prop("box-shadow", "var(--glass-edge), var(--shadow-1)")
    }
    sel(".faq__item summary") {
        prop("display", "flex")
        prop("align-items", "center")
        prop("justify-content", "space-between")
        prop("gap", "var(--s-4)")
        prop("min-height", "60px")
        prop("padding", "var(--s-3) var(--s-5)")
        prop("border-radius", "var(--r-md)")
        prop("font-weight", "600")
        prop("color", "var(--fg-strong)")
        prop("cursor", "pointer")
        prop("list-style", "none")
    }
    sel(".faq__item summary::-webkit-details-marker") {
        prop("display", "none")
    }
    sel(".faq__item summary::after") {
        prop("content", "\"\"")
        prop("flex", "none")
        prop("width", "20px")
        prop("height", "20px")
        prop("background", "var(--muted)")
        prop("-webkit-mask", "url(\"data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 20 20'%3E%3Cpath d='M10 4v12M4 10h12' stroke='black' stroke-width='1.8' stroke-linecap='round'/%3E%3C/svg%3E\") center / contain no-repeat")
        prop("mask", "url(\"data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 20 20'%3E%3Cpath d='M10 4v12M4 10h12' stroke='black' stroke-width='1.8' stroke-linecap='round'/%3E%3C/svg%3E\") center / contain no-repeat")
        prop("transition", "transform .3s var(--ease)")
    }
    sel(".faq__item[open] summary::after") {
        prop("transform", "rotate(45deg)")
    }
    sel(".faq__item .faq__body") {
        prop("padding", "0 var(--s-5) var(--s-5)")
        prop("color", "var(--muted)")
        prop("font-size", "var(--fs-sm)")
        prop("line-height", "1.7")
        prop("max-width", "68ch")
        prop("display", "grid")
        prop("gap", "var(--s-3)")
    }
    sel(".cta") {
        prop("position", "relative")
        prop("overflow", "hidden")
        prop("isolation", "isolate")
        prop("display", "grid")
        prop("gap", "var(--s-6)")
        prop("align-items", "center")
        prop("margin-block", "var(--s-8) var(--s-9)")
        prop("padding", "var(--s-7) var(--s-5)")
        prop("border-radius", "var(--r-xl)")
    }
    sel(".cta::before") {
        prop("content", "\"\"")
        prop("position", "absolute")
        prop("inset", "-60% -20% auto 30%")
        prop("aspect-ratio", "1")
        prop("z-index", "-1")
        prop("background", "radial-gradient(closest-side, color-mix(in oklch, var(--accent) 34%, transparent), transparent)")
        prop("filter", "blur(40px)")
    }
    sel(".cta__copy") {
        prop("display", "grid")
        prop("gap", "var(--s-3)")
        prop("max-width", "560px")
        prop("position", "relative")
        prop("z-index", "1")
    }
    sel(".cta__actions") {
        prop("display", "flex")
        prop("flex-wrap", "wrap")
        prop("gap", "var(--s-3)")
        prop("position", "relative")
        prop("z-index", "1")
    }
    sel(".cta__prop") {
        prop("position", "absolute")
        prop("right", "-40px")
        prop("top", "50%")
        prop("width", "360px")
        prop("translate", "0 -50%")
        prop("z-index", "0")
        prop("opacity", ".5")
        prop("mix-blend-mode", "screen")
        prop("mask-image", "radial-gradient(closest-side, #000 40%, transparent 100%)")
        prop("-webkit-mask-image", "radial-gradient(closest-side, #000 40%, transparent 100%)")
        prop("pointer-events", "none")
    }
    atMedia("(min-width: 960px)") {
        sel(".cta") {
            prop("grid-template-columns", "1fr auto")
            prop("padding", "var(--s-8)")
        }
        sel(".cta--prop .cta__actions") {
            prop("margin-right", "220px")
        }
    }
    atMedia("(max-width: 959px)") {
        sel(".cta__prop") {
            prop("width", "260px")
            prop("right", "-80px")
            prop("top", "auto")
            prop("bottom", "-120px")
            prop("translate", "0 0")
            prop("opacity", ".35")
        }
    }
    sel(".footer") {
        prop("display", "grid")
        prop("gap", "var(--s-5)")
        prop("padding-block", "var(--s-7) var(--s-8)")
        prop("border-top", "1px solid var(--hairline)")
    }
    sel(".footer__row") {
        prop("display", "flex")
        prop("flex-wrap", "wrap")
        prop("align-items", "center")
        prop("justify-content", "space-between")
        prop("gap", "var(--s-4)")
    }
    sel(".footer__links") {
        prop("display", "flex")
        prop("flex-wrap", "wrap")
        prop("gap", "2px var(--s-1)")
    }
    sel(".footer__links a") {
        prop("display", "inline-flex")
        prop("align-items", "center")
        prop("min-height", "44px")
        prop("padding", "0 var(--s-3)")
        prop("border-radius", "var(--r-full)")
        prop("font-size", "var(--fs-sm)")
        prop("color", "var(--muted)")
    }
    sel(".footer__links a:hover") {
        prop("color", "var(--fg-strong)")
    }
    sel(".footer__links a, .breadcrumb a") {
        prop("position", "relative")
    }
    sel(".footer__links a::after, .breadcrumb a::after") {
        prop("content", "\"\"")
        prop("position", "absolute")
        prop("left", "var(--s-3)")
        prop("right", "var(--s-3)")
        prop("bottom", "10px")
        prop("height", "1px")
        prop("background", "currentColor")
        prop("transform", "scaleX(0)")
        prop("transform-origin", "left")
        prop("transition", "transform .2s ease-out")
    }
    sel(".breadcrumb a::after") {
        prop("left", "0")
        prop("right", "0")
    }
    sel(".footer__links a:hover::after, .breadcrumb a:hover::after") {
        prop("transform", "scaleX(1)")
    }
    sel(".footer p") {
        prop("font-size", "var(--fs-xs)")
        prop("color", "var(--subtle)")
        prop("max-width", "80ch")
    }
    sel(".p-title") {
        prop("font-family", "var(--font-mono)")
        prop("font-size", "clamp(3.25rem, 2rem + 6vw, 6rem)")
        prop("font-weight", "700")
        prop("letter-spacing", "-.06em")
        prop("line-height", ".95")
    }
    sel(".p-title span") {
        prop("color", "var(--accent)")
        prop("margin-left", "-.18em")
    }
    sel(".p-meta") {
        prop("display", "flex")
        prop("flex-wrap", "wrap")
        prop("align-items", "center")
        prop("gap", "var(--s-2)")
    }
    sel(".breadcrumb") {
        prop("display", "flex")
        prop("align-items", "center")
        prop("gap", "var(--s-2)")
        prop("font-size", "var(--fs-sm)")
        prop("color", "var(--muted)")
    }
    sel(".breadcrumb a") {
        prop("display", "inline-flex")
        prop("align-items", "center")
        prop("min-height", "44px")
        prop("color", "var(--muted)")
    }
    sel(".breadcrumb a:hover") {
        prop("color", "var(--fg-strong)")
    }
    sel(".note") {
        prop("display", "flex")
        prop("gap", "var(--s-3)")
        prop("align-items", "flex-start")
        prop("max-width", "64ch")
        prop("padding", "var(--s-3) var(--s-4)")
        prop("border-radius", "var(--r-md)")
        prop("font-size", "var(--fs-sm)")
        prop("line-height", "1.6")
        prop("color", "var(--muted)")
        prop("background", "var(--surface)")
        prop("box-shadow", "inset 0 0 0 1px var(--hairline-strong)")
    }
    sel(".note svg") {
        prop("flex", "none")
        prop("margin-top", "3px")
        prop("color", "var(--accent)")
    }
    sel(".install-hero") {
        prop("width", "100%")
        prop("max-width", "560px")
    }
    sel(".install-hero .code") {
        prop("font-size", ".78rem")
    }
    sel(".prose") {
        prop("display", "grid")
        prop("grid-template-columns", "minmax(0, 1fr)")
        prop("gap", "var(--s-4)")
        prop("max-width", "68ch")
        prop("font-size", "var(--fs-lg)")
        prop("line-height", "1.7")
        prop("color", "var(--muted)")
    }
    sel(".prose strong") {
        prop("color", "var(--fg-strong)")
    }
    sel(".prose a") {
        prop("color", "var(--accent)")
        prop("text-decoration", "underline")
        prop("text-underline-offset", "3px")
        prop("text-decoration-color", "var(--accent-line)")
    }
    sel(".feature") {
        prop("--card-accent", "var(--accent)")
    }
    sel(".feature .icon") {
        prop("margin-bottom", "var(--s-1)")
    }
    sel(".install-grid") {
        prop("display", "grid")
        prop("gap", "var(--s-5)")
    }
    atMedia("(min-width: 960px)") {
        sel(".install-grid") {
            prop("grid-template-columns", "minmax(0, 1.15fr) minmax(0, .85fr)")
            prop("align-items", "start")
        }
    }
    sel(".install-card") {
        prop("gap", "var(--s-4)")
    }
    sel(".install-card .prose") {
        prop("font-size", "var(--fs-sm)")
        prop("gap", "var(--s-3)")
        prop("max-width", "none")
    }
    sel(".install-card .prose p") {
        prop("color", "var(--muted)")
    }
    sel(".usage") {
        prop("display", "grid")
        prop("gap", "var(--s-5)")
        prop("grid-template-columns", "1fr")
    }
    atMedia("(min-width: 960px)") {
        sel(".usage") {
            prop("grid-template-columns", "repeat(2, minmax(0, 1fr))")
        }
    }
    sel(".usage .card") {
        prop("gap", "var(--s-3)")
    }
    sel(".usage .card > p") {
        prop("color", "var(--muted)")
        prop("font-size", "var(--fs-sm)")
    }
    sel(".links") {
        prop("display", "grid")
        prop("gap", "var(--s-2)")
        prop("grid-template-columns", "1fr")
    }
    atMedia("(min-width: 640px)") {
        sel(".links") {
            prop("grid-template-columns", "repeat(2, 1fr)")
        }
    }
    atMedia("(min-width: 1024px)") {
        sel(".links") {
            prop("grid-template-columns", "repeat(3, 1fr)")
        }
    }
    sel(".links a") {
        prop("display", "flex")
        prop("align-items", "center")
        prop("justify-content", "space-between")
        prop("gap", "var(--s-3)")
        prop("min-height", "56px")
        prop("padding", "var(--s-3) var(--s-5)")
        prop("border-radius", "var(--r-md)")
        prop("font-weight", "600")
        prop("color", "var(--fg-strong)")
        prop("font-size", "var(--fs-sm)")
    }
    sel(".links a span") {
        prop("color", "var(--accent)")
    }
    sel(".links a:focus-visible") {
        prop("border-radius", "var(--r-md)")
    }
    sel(".links a") {
        prop("transition", "translate .2s ease-out, box-shadow .2s ease-out")
    }
    sel(".links a:hover") {
        prop("translate", "0 -2px")
        prop("box-shadow", "var(--glass-edge), var(--shadow-3), 0 0 0 1px var(--accent-line)")
    }
    sel(".nf-links") {
        prop("margin-top", "var(--s-7)")
    }
    sel(".links a span") {
        prop("transition", "transform .2s ease-out")
    }
    sel(".links a:hover span") {
        prop("transform", "translate(2px, -2px)")
    }
    sel(".license") {
        prop("display", "grid")
        prop("gap", "var(--s-4)")
        prop("margin-top", "var(--s-6)")
    }
    sel(".license ol") {
        prop("display", "grid")
        prop("gap", "var(--s-3)")
        prop("counter-reset", "l")
    }
    sel(".license li") {
        prop("display", "flex")
        prop("gap", "var(--s-3)")
        prop("font-size", "var(--fs-sm)")
        prop("color", "var(--muted)")
    }
    sel(".license li::before") {
        prop("counter-increment", "l")
        prop("content", "counter(l)")
        prop("display", "grid")
        prop("place-items", "center")
        prop("flex", "none")
        prop("width", "24px")
        prop("height", "24px")
        prop("border-radius", "50%")
        prop("font-size", "var(--fs-xs)")
        prop("font-weight", "700")
        prop("color", "var(--accent)")
        prop("background", "var(--accent-soft)")
    }
    sel(".license strong") {
        prop("color", "var(--fg-strong)")
    }
    sel(".delta-pill") {
        prop("border-radius", "var(--r-full)")
        prop("padding", "var(--s-2) var(--s-4) var(--s-2) var(--s-2)")
    }
    sel(".delta-pill__sym") {
        prop("display", "grid")
        prop("place-items", "center")
        prop("width", "36px")
        prop("height", "36px")
        prop("border-radius", "50%")
        prop("font-weight", "700")
        prop("color", "var(--on-accent)")
        prop("background", "radial-gradient(51% 92% at 51% 8%, color-mix(in oklch, var(--accent) 70%, white), var(--accent))")
        prop("box-shadow", "inset 0 1px #fff8")
    }
    sel(".delta-pill__num") {
        prop("font-family", "var(--font-mono)")
        prop("font-variant-numeric", "tabular-nums")
    }
    sel(".legend") {
        prop("display", "grid")
        prop("gap", "var(--s-2)")
        prop("font-size", "var(--fs-xs)")
        prop("color", "var(--muted)")
    }
    sel(".legend li") {
        prop("display", "flex")
        prop("align-items", "center")
        prop("gap", "var(--s-2)")
    }
    sel(".legend i") {
        prop("width", "12px")
        prop("height", "12px")
        prop("border-radius", "3px")
    }
    sel(".legend .k") {
        prop("background", "var(--accent)")
        prop("box-shadow", "0 0 12px var(--accent-glow)")
    }
}

private fun CssBuilder.globalCssPart2() {
    sel(".legend .t") {
        prop("background", "#7aa3b8")
        prop("opacity", ".6")
    }
    sel(".legend .c") {
        prop("border", "1.5px dashed var(--accent-2)")
    }
    sel(".status-card") {
        prop("flex-direction", "column")
        prop("align-items", "stretch")
        prop("gap", "var(--s-2)")
        prop("width", "240px")
    }
    sel(".status-row") {
        prop("display", "flex")
        prop("align-items", "center")
        prop("justify-content", "space-between")
        prop("gap", "var(--s-3)")
        prop("font-size", "var(--fs-xs)")
        prop("color", "var(--muted)")
    }
    sel(".status-row b") {
        prop("color", "var(--fg-strong)")
        prop("font-weight", "600")
    }
    sel(".status-led") {
        prop("width", "8px")
        prop("height", "8px")
        prop("border-radius", "50%")
        prop("background", "#ffcf7a")
        prop("box-shadow", "0 0 0 4px rgb(255 207 122 / .18)")
    }
    sel(".term-chip") {
        prop("font-family", "var(--font-mono)")
        prop("font-size", ".78rem")
        prop("flex-direction", "column")
        prop("align-items", "stretch")
        prop("gap", "4px")
        prop("min-width", "280px")
    }
    sel(".term-chip .ok") {
        prop("color", "var(--accent)")
    }
    sel(".term-chip .dim") {
        prop("color", "var(--muted)")
    }
    sel(".caret") {
        prop("display", "inline-block")
        prop("width", ".55em")
        prop("height", "1.1em")
        prop("vertical-align", "-2px")
        prop("background", "var(--accent)")
        prop("animation", "blink 1.1s steps(1) infinite")
    }
    at("keyframes blink") {
        sel("50%") {
            prop("opacity", "0")
        }
    }
    sel(".pos-a") {
        prop("left", "0")
        prop("top", "10%")
    }
    sel(".pos-b") {
        prop("right", "2%")
        prop("bottom", "8%")
    }
    sel(".pos-c") {
        prop("left", "2%")
        prop("bottom", "-2%")
    }
    atMedia("(max-width: 639px)") {
        sel(".stage") {
            prop("aspect-ratio", "4 / 3.4")
        }
        sel(".pos-a") {
            prop("left", "0")
            prop("top", "2%")
        }
        sel(".pos-b") {
            prop("right", "0")
            prop("bottom", "0")
        }
        sel(".pos-c") {
            prop("left", "0")
            prop("bottom", "0")
        }
        sel(".toggle-chip") {
            prop("width", "244px")
        }
        sel(".segmented button") {
            prop("font-size", "var(--fs-xs)")
        }
        sel(".chip") {
            prop("font-size", "var(--fs-xs)")
        }
        sel(".term-chip") {
            prop("min-width", "0")
            prop("max-width", "92%")
        }
    }
    atSupports("not ((backdrop-filter: blur(1px)) or (-webkit-backdrop-filter: blur(1px)))") {
        sel(".glass, .btn--glass, .nav__inner") {
            prop("background", "var(--surface-opaque)")
        }
        sel(".tier--featured") {
            prop("background", "linear-gradient(180deg, color-mix(in oklch, var(--accent) 14%, var(--surface-opaque)), var(--surface-opaque))")
        }
    }
    atMedia("(prefers-reduced-transparency: reduce)") {
        sel(".glass, .btn--glass, .nav__inner, .copy") {
            prop("background", "var(--surface-opaque)")
            prop("-webkit-backdrop-filter", "none")
            prop("backdrop-filter", "none")
        }
        sel(".tier--featured") {
            prop("background", "linear-gradient(180deg, color-mix(in oklch, var(--accent) 14%, var(--surface-opaque)), var(--surface-opaque))")
        }
        sel(".mesh__blob") {
            prop("opacity", ".5")
        }
        sel(".stage__art") {
            prop("opacity", ".7")
        }
    }
    atMedia("(prefers-reduced-motion: reduce)") {
        sel("html") {
            prop("scroll-behavior", "auto")
        }
        sel("*, *::before, *::after") {
            prop("animation-duration", ".01ms !important")
            prop("animation-iteration-count", "1 !important")
            prop("transition-duration", ".01ms !important")
        }
        sel(".stage__tilt") {
            prop("transform", "none")
        }
        sel(".layer") {
            prop("translate", "none !important")
        }
    }
    atMedia("(forced-colors: active)") {
        sel(".glass, .btn, .copy, .faq__item") {
            prop("border", "1px solid CanvasText")
        }
        sel(".gradient-text") {
            prop("color", "CanvasText")
            prop("background", "none")
        }
    }
    sel(".menu") {
        prop("position", "relative")
    }
    sel(".menu__details") {
        prop("position", "relative")
    }
    sel(".menu__trigger") {
        prop("display", "inline-flex")
        prop("align-items", "center")
        prop("gap", "6px")
        prop("min-height", "44px")
        prop("padding", "0 var(--s-3)")
        prop("border-radius", "var(--r-full)")
        prop("font-size", "var(--fs-sm)")
        prop("font-weight", "500")
        prop("color", "var(--muted)")
        prop("cursor", "pointer")
        prop("list-style", "none")
        prop("user-select", "none")
        prop("transition", "color .2s, background-color .2s")
    }
    sel(".menu__trigger::-webkit-details-marker") {
        prop("display", "none")
    }
    sel(".menu__trigger:hover, .menu__details[open] .menu__trigger") {
        prop("color", "var(--fg-strong)")
        prop("background", "rgb(255 255 255 / .05)")
    }
    sel(".menu__trigger[aria-current=\"page\"]") {
        prop("color", "var(--fg-strong)")
    }
    sel(".menu__trigger:focus-visible") {
        prop("border-radius", "var(--r-full)")
    }
    sel(".menu__trigger svg") {
        prop("transition", "transform .25s var(--ease)")
    }
    sel(".menu__details[open] .menu__trigger svg") {
        prop("transform", "rotate(180deg)")
    }
    sel(".menu__current") {
        prop("color", "var(--accent)")
        prop("font-weight", "600")
    }
    sel(".menu__panel") {
        prop("position", "absolute")
        prop("top", "calc(100% + 12px)")
        prop("left", "-12px")
        prop("z-index", "60")
        prop("width", "min(744px, calc(100vw - 32px))")
        prop("display", "grid")
        prop("gap", "var(--s-2)")
        prop("padding", "var(--s-3)")
        prop("border-radius", "var(--r-lg)")
        prop("background", "var(--glass-fill), color-mix(in oklch, var(--bg) 78%, #1b2130)")
        prop("max-height", "calc(100dvh - 96px)")
        prop("overflow-y", "auto")
        prop("overscroll-behavior", "contain")
    }
    sel(".menu__label") {
        prop("padding", "var(--s-1) var(--s-3)")
        prop("font-size", "var(--fs-xs)")
        prop("font-weight", "600")
        prop("letter-spacing", ".12em")
        prop("text-transform", "uppercase")
        prop("color", "var(--subtle)")
    }
    sel(".menu__grid") {
        prop("display", "grid")
        prop("gap", "2px")
    }
    sel(".menu__item") {
        prop("position", "relative")
        prop("display", "grid")
        prop("grid-template-columns", "40px minmax(0, 1fr) 124px")
        prop("align-items", "center")
        prop("gap", "var(--s-3)")
        prop("min-height", "86px")
        prop("padding", "var(--s-3)")
        prop("border-radius", "var(--r-md)")
        prop("color", "var(--fg)")
        prop("transition", "background-color .2s")
    }
    sel(".menu__item:hover, .menu__item[aria-current=\"page\"]") {
        prop("background", "rgb(255 255 255 / .05)")
    }
    sel(".menu__item:focus-visible") {
        prop("border-radius", "var(--r-md)")
    }
    sel(".menu__mark") {
        prop("display", "grid")
        prop("place-items", "center")
        prop("flex", "none")
        prop("width", "40px")
        prop("height", "40px")
        prop("border-radius", "var(--r-sm)")
        prop("font-family", "var(--font-mono)")
        prop("font-weight", "700")
        prop("font-size", "var(--fs-sm)")
        prop("color", "var(--card-accent)")
        prop("background", "radial-gradient(51% 92% at 51% 8%, color-mix(in oklch, var(--card-accent) 26%, transparent), color-mix(in oklch, var(--card-accent) 6%, transparent))")
        prop("box-shadow", "inset 0 0 0 1px color-mix(in oklch, var(--card-accent) 32%, transparent), inset 0 1px #ffffff26")
    }
    sel(".menu__text") {
        prop("display", "grid")
        prop("gap", "2px")
        prop("min-width", "0")
        prop("align-content", "center")
    }
    sel(".menu__text strong") {
        prop("display", "flex")
        prop("align-items", "baseline")
        prop("flex-wrap", "wrap")
        prop("gap", "0 var(--s-2)")
        prop("font-family", "var(--font-mono)")
        prop("color", "var(--fg-strong)")
        prop("font-size", "var(--fs-md)")
        prop("line-height", "24px")
    }
    sel(".menu__ver") {
        prop("font-size", "var(--fs-xs)")
        prop("color", "var(--card-accent)")
        prop("font-weight", "600")
    }
    sel(".menu__text small") {
        prop("color", "var(--muted)")
        prop("font-size", "var(--fs-xs)")
        prop("line-height", "1.45")
    }
    sel(".menu__price") {
        prop("align-self", "center")
        prop("justify-self", "stretch")
        prop("text-align", "right")
        prop("font-size", "var(--fs-xs)")
        prop("color", "var(--muted)")
        prop("white-space", "nowrap")
        prop("font-variant-numeric", "tabular-nums")
    }
    sel(".menu__foot") {
        prop("display", "flex")
        prop("justify-content", "space-between")
        prop("align-items", "center")
        prop("gap", "var(--s-3)")
        prop("padding", "var(--s-2) var(--s-3) var(--s-1)")
        prop("border-top", "1px solid var(--hairline)")
        prop("font-size", "var(--fs-xs)")
        prop("color", "var(--subtle)")
    }
    sel(".menu__foot a") {
        prop("display", "inline-flex")
        prop("align-items", "center")
        prop("min-height", "44px")
        prop("font-weight", "600")
        prop("color", "var(--accent)")
        prop("font-size", "var(--fs-sm)")
    }
    atMedia("(max-width: 899px)") {
        sel(".nav__links") {
            prop("display", "flex")
            prop("margin-right", "auto")
        }
        sel(".nav__links > li:not(.menu)") {
            prop("display", "none")
        }
        sel(".nav__links .nav__sep") {
            prop("display", "none")
        }
        sel(".menu__panel") {
            prop("position", "fixed")
            prop("top", "80px")
            prop("left", "16px")
            prop("right", "16px")
            prop("width", "auto")
            prop("max-height", "calc(100dvh - 104px)")
        }
        sel(".menu__price") {
            prop("display", "none")
        }
        sel(".menu__item") {
            prop("grid-template-columns", "40px minmax(0, 1fr)")
            prop("min-height", "64px")
        }
        sel(".menu__current") {
            prop("display", "none")
        }
    }
    sel(".catalog-chip__dots") {
        prop("display", "flex")
    }
    sel(".catalog-chip__dots i") {
        prop("width", "14px")
        prop("height", "14px")
        prop("border-radius", "50%")
        prop("margin-left", "-4px")
        prop("box-shadow", "0 0 0 2px var(--bg), 0 0 10px currentColor")
    }
    sel(".catalog-chip__dots i:first-child") {
        prop("margin-left", "0")
    }
    sel(".featured") {
        prop("position", "relative")
        prop("overflow", "hidden")
        prop("isolation", "isolate")
        prop("display", "grid")
        prop("gap", "var(--s-7)")
        prop("align-items", "center")
        prop("padding", "var(--s-6) var(--s-5)")
        prop("border-radius", "var(--r-xl)")
    }
    sel(".featured::before") {
        prop("content", "\"\"")
        prop("position", "absolute")
        prop("inset", "-30% auto auto -10%")
        prop("width", "60%")
        prop("aspect-ratio", "1")
        prop("z-index", "-1")
        prop("background", "radial-gradient(closest-side, color-mix(in oklch, var(--card-accent) 26%, transparent), transparent)")
        prop("filter", "blur(30px)")
    }
    atMedia("(min-width: 1024px)") {
        sel(".featured") {
            prop("grid-template-columns", "minmax(0, .95fr) minmax(0, 1.05fr)")
            prop("padding", "var(--s-8)")
        }
    }
    sel(".featured__copy") {
        prop("display", "grid")
        prop("gap", "var(--s-5)")
        prop("justify-items", "start")
    }
    sel(".featured__title") {
        prop("font-family", "var(--font-mono)")
        prop("font-size", "clamp(2.75rem, 2rem + 3vw, 4.25rem)")
        prop("font-weight", "700")
        prop("letter-spacing", "-.06em")
        prop("line-height", "1")
    }
    sel(".featured__title span") {
        prop("color", "var(--card-accent)")
        prop("margin-left", "-.18em")
    }
    sel(".featured__list") {
        prop("display", "grid")
        prop("gap", "var(--s-3)")
        prop("font-size", "var(--fs-sm)")
        prop("color", "var(--muted)")
        prop("max-width", "60ch")
    }
    sel(".featured__list li") {
        prop("display", "flex")
        prop("gap", "var(--s-3)")
        prop("align-items", "flex-start")
    }
    sel(".featured__list svg") {
        prop("flex", "none")
        prop("margin-top", "3px")
        prop("color", "var(--card-accent)")
    }
    sel(".featured__list strong") {
        prop("color", "var(--fg-strong)")
    }
    sel(".featured__list code") {
        prop("font-size", ".86em")
        prop("padding", "1px 6px")
        prop("border-radius", "var(--r-xs)")
        prop("background", "rgb(255 255 255 / .06)")
        prop("color", "var(--fg-strong)")
    }
    sel(".featured__demo") {
        prop("position", "relative")
        prop("min-width", "0")
    }
    sel(".featured__prop") {
        prop("position", "absolute")
        prop("width", "300px")
        prop("right", "-70px")
        prop("top", "-120px")
        prop("z-index", "0")
        prop("opacity", ".7")
        prop("mix-blend-mode", "screen")
        prop("pointer-events", "none")
        prop("mask-image", "radial-gradient(closest-side, #000 40%, transparent 100%)")
        prop("-webkit-mask-image", "radial-gradient(closest-side, #000 40%, transparent 100%)")
        prop("animation", "float 9s ease-in-out infinite")
    }
    sel(".featured__demo .term") {
        prop("position", "relative")
        prop("z-index", "1")
    }
    sel(".price-chips") {
        prop("display", "flex")
        prop("flex-wrap", "wrap")
        prop("gap", "var(--s-2)")
    }
    sel(".price-chip") {
        prop("display", "inline-flex")
        prop("align-items", "center")
        prop("min-height", "28px")
        prop("padding", "0 var(--s-3)")
        prop("border-radius", "var(--r-full)")
        prop("font-size", "var(--fs-xs)")
        prop("font-weight", "600")
        prop("color", "var(--fg)")
        prop("box-shadow", "inset 0 0 0 1px var(--hairline-strong)")
    }
    sel(".price-chip--pro") {
        prop("color", "#ffcf7a")
        prop("box-shadow", "inset 0 0 0 1px rgb(255 191 90 / .4)")
    }
    sel(".soon-card") {
        prop("border-radius", "var(--r-lg)")
        prop("border", "1.5px dashed var(--hairline-strong)")
        prop("background", "rgb(255 255 255 / .015)")
        prop("align-content", "center")
        prop("justify-items", "start")
        prop("min-height", "240px")
    }
    atMedia("(min-width: 1024px)") {
        sel(".grid--3 .soon-card") {
            prop("grid-column", "span 3")
            prop("min-height", "0")
            prop("grid-template-columns", "auto 1fr")
            prop("column-gap", "var(--s-4)")
        }
        sel(".grid--3 .soon-card .icon") {
            prop("grid-row", "span 2")
            prop("margin", "0")
        }
    }
    sel(".grid--4") {
        prop("grid-template-columns", "minmax(0, 1fr)")
    }
    atMedia("(min-width: 640px)") {
        sel(".grid--4") {
            prop("grid-template-columns", "repeat(2, minmax(0, 1fr))")
        }
    }
    atMedia("(min-width: 1100px)") {
        sel(".grid--4") {
            prop("grid-template-columns", "repeat(4, minmax(0, 1fr))")
        }
    }
    sel(".steps") {
        prop("display", "grid")
        prop("gap", "var(--s-5)")
        prop("grid-template-columns", "minmax(0, 1fr)")
        prop("counter-reset", "s")
    }
    atMedia("(min-width: 900px)") {
        sel(".steps") {
            prop("grid-template-columns", "repeat(3, minmax(0, 1fr))")
        }
    }
    sel(".step") {
        prop("position", "relative")
    }
    sel(".step__n") {
        prop("font-family", "var(--font-mono)")
        prop("font-size", "2.5rem")
        prop("font-weight", "700")
        prop("letter-spacing", "-.06em")
        prop("line-height", "1")
        prop("background", "linear-gradient(180deg, color-mix(in oklch, var(--accent) 80%, white), transparent 110%)")
        prop("-webkit-background-clip", "text")
        prop("background-clip", "text")
        prop("color", "transparent")
    }
    sel(".price-card") {
        prop("gap", "var(--s-4)")
    }
    sel(".price-tier") {
        prop("display", "grid")
        prop("gap", "var(--s-3)")
        prop("padding", "var(--s-4)")
        prop("border-radius", "var(--r-md)")
        prop("background", "rgb(0 0 0 / .18)")
        prop("box-shadow", "inset 0 0 0 1px var(--hairline)")
    }
    sel(".price-tier .checklist") {
        prop("margin", "0")
        prop("padding-top", "var(--s-3)")
    }
    sel(".price-tier--pro") {
        prop("background", "linear-gradient(180deg, color-mix(in oklch, var(--card-accent) 10%, transparent), rgb(0 0 0 / .18))")
        prop("box-shadow", "inset 0 0 0 1px color-mix(in oklch, var(--card-accent) 30%, transparent)")
    }
    sel(".price-tier .tier__head strong") {
        prop("display", "inline-flex")
        prop("align-items", "center")
        prop("gap", "var(--s-2)")
        prop("color", "var(--fg-strong)")
        prop("font-size", "var(--fs-sm)")
    }
    sel(".price__num--sm") {
        prop("font-size", "1.5rem")
    }
    sel(".pricing-pair") {
        prop("align-items", "stretch")
    }
    sel(".wishlist") {
        prop("display", "grid")
        prop("gap", "var(--s-2)")
    }
    sel(".wishlist__email") {
        prop("width", "100%")
        prop("min-height", "44px")
        prop("padding", "0 var(--s-3)")
        prop("border", "0")
        prop("border-radius", "var(--r-md)")
        prop("font", "inherit")
        prop("color", "var(--fg-strong)")
        prop("background", "rgb(0 0 0 / .25)")
        prop("box-shadow", "inset 0 0 0 1px var(--hairline-strong)")
    }
    sel(".wishlist__email:focus-visible") {
        prop("outline", "2px solid var(--accent)")
        prop("outline-offset", "2px")
    }
    sel(".wishlist__status:empty") {
        prop("display", "none")
    }
    atMedia("(min-width: 640px)") {
        sel(".pricing-pair") {
            prop("max-width", "880px")
        }
    }
    sel(".pricing-pair--free") {
        prop("max-width", "440px")
    }
    sel(".price-card:not(:has(.price-tier--pro))") {
        prop("grid-template-rows", "auto auto 1fr")
    }
    sel(".price-card:not(:has(.price-tier--pro)) .price-tier") {
        prop("grid-template-rows", "auto 1fr auto")
        prop("align-content", "stretch")
    }
    sel(".price-card:not(:has(.price-tier--pro)) .price-tier .checklist") {
        prop("align-content", "start")
    }
    sel(".term") {
        prop("margin", "0")
        prop("border-radius", "var(--r-lg)")
        prop("overflow", "hidden")
        prop("display", "flex")
        prop("flex-direction", "column")
    }
    sel(".term__body") {
        prop("flex", "1")
    }
    sel(".term__bar") {
        prop("display", "flex")
        prop("align-items", "center")
        prop("gap", "var(--s-3)")
        prop("min-height", "48px")
        prop("padding", "var(--s-1) var(--s-2) var(--s-1) var(--s-4)")
        prop("border-bottom", "1px solid var(--hairline)")
    }
    sel(".term__dots") {
        prop("display", "flex")
        prop("gap", "6px")
    }
    sel(".term__dots i") {
        prop("width", "11px")
        prop("height", "11px")
        prop("border-radius", "50%")
        prop("background", "rgb(255 255 255 / .16)")
    }
    sel(".term__dots i:nth-child(1)") {
        prop("background", "#ff6b6b99")
    }
    sel(".term__dots i:nth-child(2)") {
        prop("background", "#ffcc6699")
    }
    sel(".term__dots i:nth-child(3)") {
        prop("background", "#5ee39a99")
    }
    sel(".term__title") {
        prop("font-family", "var(--font-mono)")
        prop("font-size", "var(--fs-xs)")
        prop("color", "var(--muted)")
        prop("margin-right", "auto")
    }
    sel(".term__ctl") {
        prop("min-height", "36px")
        prop("min-width", "44px")
        prop("padding", "0 var(--s-3)")
        prop("border", "0")
        prop("border-radius", "var(--r-full)")
        prop("cursor", "pointer")
        prop("font", "inherit")
        prop("font-size", "var(--fs-xs)")
        prop("font-weight", "600")
        prop("color", "var(--muted)")
        prop("background", "rgb(255 255 255 / .06)")
        prop("box-shadow", "inset 0 0 0 1px var(--hairline)")
        prop("position", "relative")
    }
    sel(".term__ctl::after") {
        prop("content", "\"\"")
        prop("position", "absolute")
        prop("inset", "-4px")
    }
    sel(".term__ctl:hover") {
        prop("color", "var(--fg-strong)")
    }
    sel(".term__ctl:focus-visible") {
        prop("border-radius", "var(--r-full)")
    }
    sel(".term__ctl[hidden]") {
        prop("display", "none")
    }
    sel(".term__body") {
        prop("margin", "0")
        prop("padding", "var(--s-5)")
        prop("min-height", "300px")
        prop("overflow-x", "auto")
        prop("font-size", ".8125rem")
        prop("line-height", "1.8")
        prop("color", "var(--fg)")
        prop("background", "linear-gradient(180deg, rgb(0 0 0 / .25), rgb(0 0 0 / .45))")
        prop("scrollbar-width", "thin")
    }
    sel(".term__line") {
        prop("display", "inline")
    }
    sel(".term__line--note") {
        prop("color", "var(--subtle)")
    }
    sel(".term__line--out") {
        prop("color", "color-mix(in oklch, var(--accent) 45%, var(--fg))")
    }
    sel(".term__line[data-hide]") {
        prop("visibility", "hidden")
    }
    sel(".term__cursor") {
        prop("display", "inline-block")
        prop("width", ".55em")
        prop("height", "1.15em")
        prop("vertical-align", "-3px")
        prop("background", "var(--accent)")
        prop("animation", "blink 1.1s steps(1) infinite")
    }
    sel(".term__cap") {
        prop("padding", "var(--s-3) var(--s-5)")
        prop("border-top", "1px solid var(--hairline)")
        prop("font-size", "var(--fs-xs)")
        prop("color", "var(--subtle)")
    }
    sel(".showcase") {
        prop("display", "grid")
        prop("gap", "var(--s-5)")
        prop("grid-template-columns", "minmax(0, 1fr)")
        prop("align-items", "stretch")
    }
    atMedia("(min-width: 1024px)") {
        sel(".showcase") {
            prop("grid-template-columns", "minmax(0, 1.15fr) minmax(0, .85fr)")
        }
        sel(".showcase--stack") {
            prop("grid-template-columns", "minmax(0, 1fr)")
        }
    }
    sel(".bitset") {
        prop("display", "grid")
        prop("gap", "var(--s-4)")
        prop("align-content", "start")
        prop("padding", "var(--s-5)")
        prop("border-radius", "var(--r-lg)")
    }
    atMedia("(min-width: 768px)") {
        sel(".bitset") {
            prop("padding", "var(--s-6)")
        }
    }
    sel(".bitset__head") {
        prop("display", "flex")
        prop("flex-wrap", "wrap")
        prop("align-items", "baseline")
        prop("justify-content", "space-between")
        prop("gap", "var(--s-3)")
    }
    sel(".bitset__counter") {
        prop("font-family", "var(--font-mono)")
        prop("font-size", "1.75rem")
        prop("font-weight", "700")
        prop("letter-spacing", "-.04em")
        prop("color", "var(--fg-strong)")
        prop("font-variant-numeric", "tabular-nums")
    }
    sel(".bitset__counter .muted") {
        prop("font-size", "var(--fs-sm)")
        prop("font-weight", "500")
        prop("letter-spacing", "0")
    }
    sel(".bitset__delta") {
        prop("color", "var(--accent)")
    }
    sel(".bitset__grid") {
        prop("width", "100%")
        prop("max-width", "340px")
        prop("justify-self", "center")
        prop("display", "grid")
        prop("grid-template-columns", "repeat(4, 1fr)")
        prop("gap", "var(--s-3)")
        prop("padding", "var(--s-4)")
        prop("border-radius", "var(--r-md)")
        prop("background", "rgb(0 0 0 / .25)")
        prop("box-shadow", "inset 0 0 0 1px var(--hairline)")
        prop("perspective", "800px")
    }
    sel(".bit") {
        prop("aspect-ratio", "1")
        prop("border-radius", "var(--r-sm)")
        prop("transform-style", "preserve-3d")
        prop("transition", "background .5s var(--ease), box-shadow .5s var(--ease), transform .6s var(--ease), opacity .5s var(--ease)")
    }
}

private fun CssBuilder.globalCssPart3() {
    sel(".bit[data-state=\"K\"]") {
        prop("background", "radial-gradient(51% 92% at 51% 8%, color-mix(in oklch, var(--accent) 75%, white), color-mix(in oklch, var(--accent) 55%, transparent))")
        prop("box-shadow", "inset 0 1px #ffffff99, inset 0 -2px #0004, 0 8px 22px -8px var(--accent-glow)")
        prop("transform", "translateZ(10px) rotateX(12deg)")
    }
    sel(".bit[data-state=\"T\"]") {
        prop("background", "linear-gradient(180deg, #7aa3b8aa, #7aa3b833)")
        prop("box-shadow", "inset 0 1px #ffffff44")
        prop("opacity", ".6")
        prop("transform", "scale(.72)")
    }
    sel(".bit[data-state=\"C\"]") {
        prop("background", "transparent")
        prop("box-shadow", "inset 0 0 0 1.5px color-mix(in oklch, var(--accent-2) 80%, transparent)")
        prop("outline", "1.5px dashed color-mix(in oklch, var(--accent-2) 60%, transparent)")
        prop("outline-offset", "-6px")
        prop("transform", "scale(.9)")
    }
    sel(".legend--row") {
        prop("display", "flex")
        prop("flex-wrap", "wrap")
        prop("gap", "var(--s-2) var(--s-5)")
    }
    sel(".legend--row code") {
        prop("font-size", ".9em")
    }
    sel(".flow") {
        prop("display", "grid")
        prop("gap", "var(--s-4)")
    }
    sel(".flow__title") {
        prop("color", "var(--muted)")
        prop("font-size", "var(--fs-sm)")
        prop("font-weight", "600")
        prop("letter-spacing", ".12em")
        prop("text-transform", "uppercase")
    }
    sel(".flow__nodes") {
        prop("position", "relative")
        prop("display", "grid")
        prop("gap", "var(--s-3)")
        prop("grid-template-columns", "minmax(0, 1fr)")
    }
    atMedia("(min-width: 900px)") {
        sel(".flow__nodes") {
            prop("grid-auto-flow", "column")
            prop("grid-auto-columns", "minmax(0, 1fr)")
            prop("gap", "var(--s-5)")
        }
    }
    sel(".flow__node") {
        prop("position", "relative")
        prop("display", "grid")
        prop("gap", "var(--s-1)")
        prop("align-content", "start")
        prop("padding", "var(--s-4) var(--s-5)")
        prop("border-radius", "var(--r-md)")
    }
    sel(".flow__node strong") {
        prop("color", "var(--fg-strong)")
        prop("font-size", "var(--fs-md)")
    }
    sel(".flow__idx") {
        prop("font-family", "var(--font-mono)")
        prop("font-size", "var(--fs-xs)")
        prop("color", "var(--accent)")
        prop("font-weight", "700")
    }
    sel(".flow__node::after") {
        prop("content", "\"\"")
        prop("position", "absolute")
        prop("z-index", "-1")
        prop("background", "linear-gradient(90deg, var(--accent-line), var(--accent), var(--accent-line))")
        prop("background-size", "200% 100%")
        prop("animation", "flow-pulse 3s linear infinite")
        prop("animation-delay", "calc(var(--i) * .4s)")
    }
    sel(".flow__node:last-child::after") {
        prop("display", "none")
    }
    atMedia("(min-width: 900px)") {
        sel(".flow__node::after") {
            prop("top", "50%")
            prop("right", "calc(-1 * var(--s-5))")
            prop("width", "var(--s-5)")
            prop("height", "2px")
        }
    }
    atMedia("(max-width: 899px)") {
        sel(".flow__node::after") {
            prop("left", "28px")
            prop("bottom", "calc(-1 * var(--s-3))")
            prop("width", "2px")
            prop("height", "var(--s-3)")
        }
    }
    at("keyframes flow-pulse") {
        sel("from") {
            prop("background-position", "200% 0")
        }
        sel("to") {
            prop("background-position", "0 0")
        }
    }
    sel(".brand") {
        prop("white-space", "nowrap")
    }
    at("property --rot") {
        prop("syntax", "\"<angle>\"")
        prop("inherits", "true")
        prop("initial-value", "0deg")
    }
    sel(".stage--ring") {
        prop("aspect-ratio", "auto")
        prop("min-height", "600px")
    }
    sel(".stage--ring .stage__art") {
        prop("opacity", ".55")
        prop("inset", "-6% -16% 4% -12%")
    }
    sel(".stage--ring .pos-a") {
        prop("left", "-2%")
        prop("top", "0")
        prop("z-index", "4")
    }
    sel(".stage__ring") {
        prop("inset", "118px 0 0 0")
        prop("display", "grid")
    }
    sel(".ring") {
        prop("position", "relative")
        prop("display", "grid")
        prop("gap", "var(--s-4)")
        prop("align-content", "start")
    }
    sel(".ring__stage") {
        prop("position", "relative")
        prop("perspective", "1400px")
        prop("perspective-origin", "50% 40%")
        prop("touch-action", "pan-y")
        prop("cursor", "grab")
        prop("overflow-x", "clip")
        prop("height", "472px")
        prop("padding-bottom", "72px")
        prop("margin-bottom", "-72px")
        prop("-webkit-mask-image", "linear-gradient(90deg, transparent, #000 9%, #000 91%, transparent), linear-gradient(#000 calc(100% - 56px), transparent)")
        prop("mask-image", "linear-gradient(90deg, transparent, #000 9%, #000 91%, transparent), linear-gradient(#000 calc(100% - 56px), transparent)")
        prop("-webkit-mask-composite", "source-in")
        prop("mask-composite", "intersect")
    }
    sel(".ring__stage.is-dragging") {
        prop("cursor", "grabbing")
    }
    sel(".ring__track") {
        prop("--a", "calc(360deg / var(--n))")
        prop("--r", "360px")
        prop("position", "absolute")
        prop("inset", "0")
        prop("transform-style", "preserve-3d")
        prop("transform", "translateZ(calc(-1 * var(--r))) rotateY(calc(-1 * var(--rot)))")
        prop("transition", "--rot .9s var(--ease)")
    }
    sel(".ring__stage.is-dragging .ring__track") {
        prop("transition", "none")
    }
    sel(".ring__item") {
        prop("position", "absolute")
        prop("left", "50%")
        prop("top", "12px")
        prop("width", "240px")
        prop("margin-left", "-120px")
        prop("transform-style", "preserve-3d")
        prop("transform", "rotateY(calc(var(--i) * var(--a))) translateZ(var(--r))")
    }
    sel(".ring__face") {
        prop("position", "relative")
        prop("transform", "rotateY(calc(var(--rot) - var(--i) * var(--a)))")
        prop("transition", "opacity .6s var(--ease), filter .6s var(--ease)")
    }
    sel(".ring__item:not(.is-active) .ring__face") {
        prop("opacity", ".5")
        prop("filter", "saturate(.7) blur(.4px)")
    }
    sel(".ring__item.is-active") {
        prop("z-index", "2")
    }
    sel(".ring__track, .ring__item, .ring__face") {
        prop("pointer-events", "none")
    }
    sel(".ring__card, .ring__hs") {
        prop("pointer-events", "auto")
    }
    sel(".ring__card") {
        prop("position", "relative")
        prop("display", "grid")
        prop("gap", "var(--s-2)")
        prop("padding", "var(--s-5)")
        prop("border-radius", "var(--r-lg)")
        prop("color", "var(--fg)")
        prop("overflow", "hidden")
        prop("background", "radial-gradient(80% 60% at 50% 0%, color-mix(in oklch, var(--card-accent) 22%, transparent), transparent 70%), var(--glass-fill), color-mix(in oklch, var(--bg) 70%, #182030)")
        prop("box-shadow", "var(--glass-edge), 0 30px 60px -24px rgb(0 0 0 / .7), 0 0 0 1px color-mix(in oklch, var(--card-accent) 20%, transparent)")
        prop("user-select", "none")
        prop("-webkit-user-drag", "none")
    }
    sel(".ring__card:focus-visible") {
        prop("border-radius", "var(--r-lg)")
        prop("box-shadow", "var(--glass-edge), var(--focus)")
    }
    sel(".ring__art") {
        prop("display", "block")
        prop("width", "150px")
        prop("margin", "-18px auto -6px")
        prop("mix-blend-mode", "screen")
        prop("pointer-events", "none")
        prop("mask-image", "radial-gradient(closest-side, #000 45%, transparent 100%)")
        prop("-webkit-mask-image", "radial-gradient(closest-side, #000 45%, transparent 100%)")
    }
    sel(".ring__art img") {
        prop("width", "100%")
        prop("height", "auto")
    }
    sel(".ring__meta") {
        prop("display", "flex")
    }
    sel(".ring__title") {
        prop("font-family", "var(--font-mono)")
        prop("font-size", "1.75rem")
        prop("font-weight", "700")
        prop("letter-spacing", "-.05em")
        prop("color", "var(--fg-strong)")
        prop("line-height", "1")
    }
    sel(".ring__title span") {
        prop("color", "var(--card-accent)")
        prop("margin-left", "-.12em")
    }
    sel(".ring__tag") {
        prop("font-size", "var(--fs-xs)")
        prop("line-height", "1.5")
        prop("color", "var(--muted)")
        prop("display", "-webkit-box")
        prop("-webkit-line-clamp", "3")
        prop("-webkit-box-orient", "vertical")
        prop("overflow", "hidden")
    }
    sel(".ring__foot") {
        prop("display", "flex")
        prop("justify-content", "space-between")
        prop("align-items", "center")
        prop("padding-top", "var(--s-2)")
        prop("margin-top", "var(--s-1)")
        prop("border-top", "1px solid var(--hairline)")
        prop("font-size", "var(--fs-xs)")
        prop("font-weight", "600")
        prop("color", "var(--card-accent)")
    }
    sel(".ring__hs") {
        prop("left", "auto")
        prop("right", "6px")
        prop("top", "6px")
        prop("translate", "none")
        prop("z-index", "3")
    }
    sel(".ring__hs .hotspot__tip") {
        prop("right", "0")
        prop("width", "250px")
    }
    sel(".ring__item:not(.is-active) .ring__hs") {
        prop("visibility", "hidden")
    }
    sel(".ring__controls") {
        prop("position", "relative")
        prop("z-index", "1")
        prop("display", "flex")
        prop("align-items", "center")
        prop("justify-content", "center")
        prop("gap", "var(--s-3)")
    }
    sel(".ring__controls[hidden]") {
        prop("display", "none")
    }
    sel(".ring__btn") {
        prop("display", "grid")
        prop("place-items", "center")
        prop("width", "44px")
        prop("height", "44px")
        prop("border", "0")
        prop("border-radius", "50%")
        prop("cursor", "pointer")
        prop("color", "var(--fg-strong)")
        prop("background", "var(--glass-fill), rgb(255 255 255 / .04)")
        prop("box-shadow", "var(--glass-edge), var(--shadow-1)")
    }
    sel(".ring__btn:hover") {
        prop("background", "var(--glass-fill), rgb(255 255 255 / .1)")
    }
    sel(".ring__btn:focus-visible") {
        prop("border-radius", "50%")
    }
    sel(".ring__status") {
        prop("min-width", "12ch")
        prop("text-align", "center")
        prop("font-size", "var(--fs-sm)")
        prop("color", "var(--muted)")
        prop("font-variant-numeric", "tabular-nums")
    }
    sel(".ring:not(.is-ready) .ring__stage, .ring:not(.is-ready) .ring__track, .ring:not(.is-ready) .ring__item, .ring:not(.is-ready) .ring__face") {
        prop("all", "unset")
        prop("display", "block")
    }
    sel(".ring:not(.is-ready) .ring__track") {
        prop("display", "grid")
        prop("grid-template-columns", "repeat(auto-fit, minmax(200px, 1fr))")
        prop("gap", "var(--s-3)")
    }
    sel(".ring:not(.is-ready) .ring__face") {
        prop("position", "relative")
    }
    atMedia("(max-width: 767px)") {
        sel(".stage--ring") {
            prop("min-height", "0")
            prop("aspect-ratio", "auto")
        }
        sel(".stage--ring .stage__art") {
            prop("inset", "-10% -30% auto")
            prop("height", "70%")
            prop("opacity", ".35")
        }
        sel(".stage--ring .pos-a") {
            prop("position", "relative")
            prop("left", "0")
            prop("top", "0")
            prop("margin-bottom", "var(--s-4)")
        }
        sel(".stage--ring .stage__tilt") {
            prop("position", "relative")
        }
        sel(".stage__ring") {
            prop("position", "relative")
            prop("inset", "auto")
        }
        sel(".ring__stage, .ring__track, .ring__item, .ring__face") {
            prop("all", "unset")
            prop("display", "block")
        }
        sel(".ring__face") {
            prop("position", "relative")
        }
        sel(".ring__track") {
            prop("display", "grid")
            prop("gap", "var(--s-3)")
        }
        sel(".ring__item:not(.is-active) .ring__face") {
            prop("opacity", "1")
            prop("filter", "none")
        }
        sel(".ring__item:not(.is-active) .ring__hs") {
            prop("visibility", "visible")
        }
        sel(".ring__card") {
            prop("grid-template-columns", "88px 1fr")
            prop("column-gap", "var(--s-3)")
            prop("align-items", "center")
        }
        sel(".ring__art") {
            prop("grid-row", "span 4")
            prop("width", "96px")
            prop("margin", "-8px")
        }
        sel(".ring__controls") {
            prop("display", "none")
        }
    }
    sel(".oss-card__mesh") {
        prop("position", "absolute")
        prop("inset", "0")
        prop("z-index", "0")
        prop("border-radius", "inherit")
        prop("overflow", "hidden")
        prop("opacity", "0")
        prop("pointer-events", "none")
        prop("transition", "opacity .5s var(--ease)")
    }
    sel(".oss-card__mesh::before, .oss-card__mesh::after") {
        prop("content", "\"\"")
        prop("position", "absolute")
        prop("width", "70%")
        prop("aspect-ratio", "1")
        prop("border-radius", "50%")
        prop("filter", "blur(36px)")
    }
    sel(".oss-card__mesh::before") {
        prop("right", "-20%")
        prop("top", "-30%")
        prop("background", "color-mix(in oklch, var(--card-accent) 40%, transparent)")
        prop("animation", "drift-1 14s ease-in-out infinite alternate")
    }
    sel(".oss-card__mesh::after") {
        prop("left", "-25%")
        prop("bottom", "-35%")
        prop("background", "color-mix(in oklch, var(--card-accent) 22%, #3a4bff 10%)")
        prop("animation", "drift-2 18s ease-in-out infinite alternate")
    }
    sel(".oss-card:hover .oss-card__mesh, .oss-card:focus-within .oss-card__mesh") {
        prop("opacity", "1")
    }
    sel(".oss-card__mesh::before, .oss-card__mesh::after") {
        prop("animation-play-state", "paused")
    }
    sel(".oss-card:is(:hover, :focus-within) .oss-card__mesh::before, .oss-card:is(:hover, :focus-within) .oss-card__mesh::after") {
        prop("animation-play-state", "running")
    }
    sel(".oss-card .oss-card__preview") {
        prop("width", "300px")
        prop("top", "-60px")
        prop("right", "-80px")
    }
    sel(".graph") {
        prop("display", "grid")
        prop("gap", "var(--s-3)")
        prop("align-content", "start")
        prop("margin", "0")
        prop("padding", "var(--s-5)")
        prop("border-radius", "var(--r-lg)")
    }
    sel(".graph__svg") {
        prop("width", "100%")
        prop("height", "auto")
        prop("overflow", "visible")
    }
    sel(".graph__edge") {
        prop("fill", "none")
        prop("stroke", "color-mix(in oklch, var(--accent) 70%, transparent)")
        prop("stroke-width", "1.5")
        prop("stroke-dasharray", "1")
        prop("stroke-dashoffset", "0")
    }
    sel(".graph__edge--agent") {
        prop("stroke", "color-mix(in oklch, var(--accent-2) 70%, transparent)")
        prop("stroke-dasharray", "1")
    }
    sel(".graph__node rect") {
        prop("fill", "rgb(10 22 34)")
        prop("stroke", "color-mix(in oklch, var(--accent) 45%, transparent)")
    }
    sel(".graph__base") {
        prop("fill", "rgb(10 22 34)")
    }
    sel("html[data-mode=\"light\"] .graph__node rect, html[data-mode=\"light\"] .graph__base") {
        prop("fill", "var(--surface-opaque)")
    }
    sel(".graph__node--agent rect") {
        prop("stroke", "color-mix(in oklch, var(--accent-2) 60%, transparent)")
    }
    sel(".graph__node text") {
        prop("fill", "var(--fg-strong)")
        prop("font-family", "var(--font-mono)")
        prop("font-size", "13px")
        prop("text-anchor", "middle")
    }
    sel(".graph__node--hub text") {
        prop("fill", "#04121c")
        prop("font-weight", "700")
        prop("font-size", "11px")
    }
    sel(".graph.is-armed .graph__edge") {
        prop("stroke-dashoffset", "1")
    }
    sel(".graph.is-armed .graph__node") {
        prop("opacity", "0")
    }
    sel(".graph.is-drawn .graph__edge") {
        prop("stroke-dashoffset", "0")
        prop("transition", "stroke-dashoffset 1s var(--ease) calc(var(--d) * 1s)")
    }
    sel(".graph.is-drawn .graph__node") {
        prop("opacity", "1")
        prop("transition", "opacity .5s var(--ease) calc(var(--d) * 1s)")
    }
    sel(".graph code, .particles code, .route code") {
        prop("font-size", ".86em")
        prop("padding", "1px 5px")
        prop("border-radius", "var(--r-xs)")
        prop("background", "rgb(255 255 255 / .06)")
        prop("color", "var(--fg-strong)")
    }
    sel(".particles") {
        prop("display", "grid")
        prop("gap", "var(--s-4)")
        prop("padding", "var(--s-5)")
        prop("border-radius", "var(--r-xl)")
    }
    atMedia("(min-width: 768px)") {
        sel(".particles") {
            prop("padding", "var(--s-6)")
        }
    }
    sel(".particles__head") {
        prop("display", "flex")
        prop("flex-wrap", "wrap")
        prop("justify-content", "space-between")
        prop("align-items", "end")
        prop("gap", "var(--s-4)")
    }
    sel(".particles__head .flow__title") {
        prop("display", "flex")
        prop("align-items", "center")
        prop("gap", "var(--s-2)")
    }
    sel(".particles__counter") {
        prop("font-family", "var(--font-mono)")
        prop("font-size", "1.75rem")
        prop("font-weight", "700")
        prop("letter-spacing", "-.04em")
        prop("color", "var(--fg-strong)")
        prop("font-variant-numeric", "tabular-nums")
    }
    sel(".particles__counter .muted") {
        prop("font-size", "var(--fs-sm)")
        prop("font-weight", "500")
        prop("letter-spacing", "0")
    }
    sel(".particles__stage") {
        prop("position", "relative")
        prop("height", "clamp(260px, 36vw, 380px)")
        prop("border-radius", "var(--r-md)")
        prop("background", "radial-gradient(80% 60% at 50% 50%, rgb(92 225 255 / .06), transparent), rgb(0 0 0 / .3)")
        prop("box-shadow", "inset 0 0 0 1px var(--hairline)")
        prop("overflow", "hidden")
    }
    sel(".particles__stage canvas") {
        prop("position", "absolute")
        prop("inset", "0")
        prop("width", "100%")
        prop("height", "100%")
    }
    sel(".particles__band-label, .particles__archive-label") {
        prop("position", "absolute")
        prop("font-size", "var(--fs-xs)")
        prop("color", "var(--subtle)")
        prop("letter-spacing", ".1em")
        prop("text-transform", "uppercase")
    }
    sel(".particles__band-label") {
        prop("left", "var(--s-4)")
        prop("top", "calc(50% - 34px)")
    }
    sel(".particles__archive-label") {
        prop("right", "var(--s-4)")
        prop("bottom", "var(--s-3)")
        prop("color", "color-mix(in oklch, var(--accent-2) 70%, var(--muted))")
        prop("text-transform", "none")
        prop("letter-spacing", "0")
    }
    sel(".swatch") {
        prop("display", "inline-block")
        prop("width", "10px")
        prop("height", "10px")
        prop("border-radius", "3px")
        prop("margin-right", "4px")
        prop("vertical-align", "-1px")
    }
    sel(".swatch--k") {
        prop("background", "var(--accent)")
        prop("box-shadow", "0 0 8px var(--accent)")
    }
    sel(".swatch--t") {
        prop("background", "#7aa3b8")
        prop("opacity", ".7")
    }
    sel(".swatch--c") {
        prop("background", "transparent")
        prop("box-shadow", "inset 0 0 0 1.5px var(--accent-2)")
    }
    sel(".scenes") {
        prop("display", "grid")
        prop("grid-template-columns", "minmax(0, 1fr)")
        prop("gap", "var(--s-5)")
    }
    sel(".scenes__tabs") {
        prop("display", "flex")
        prop("flex-wrap", "wrap")
        prop("gap", "var(--s-2)")
        prop("padding", "4px")
        prop("border-radius", "var(--r-lg)")
        prop("background", "rgb(0 0 0 / .3)")
        prop("box-shadow", "inset 0 0 0 1px var(--hairline)")
        prop("justify-self", "start")
    }
    sel(".scenes__tab") {
        prop("display", "grid")
        prop("gap", "2px")
        prop("text-align", "left")
        prop("min-height", "52px")
        prop("padding", "var(--s-2) var(--s-4)")
        prop("border", "0")
        prop("border-radius", "var(--r-md)")
        prop("cursor", "pointer")
        prop("font", "inherit")
        prop("color", "var(--muted)")
        prop("background", "transparent")
        prop("transition", "background-color .25s var(--ease), color .2s")
    }
    sel(".scenes__tab strong") {
        prop("font-size", "var(--fs-sm)")
        prop("color", "inherit")
    }
    sel(".scenes__tab small") {
        prop("font-family", "var(--font-mono)")
        prop("font-size", "var(--fs-xs)")
        prop("color", "var(--subtle)")
    }
    sel(".scenes__tab[aria-selected=\"true\"]") {
        prop("color", "var(--fg-strong)")
        prop("background", "var(--glass-fill), color-mix(in oklch, var(--accent) 12%, transparent)")
        prop("box-shadow", "var(--glass-edge), inset 0 0 0 1px color-mix(in oklch, var(--accent) 35%, transparent)")
    }
    sel(".scenes__tab[aria-selected=\"true\"] small") {
        prop("color", "var(--accent)")
    }
    sel(".scenes__tab:focus-visible") {
        prop("border-radius", "var(--r-md)")
    }
    sel(".scenes__stage") {
        prop("display", "grid")
        prop("grid-template-columns", "minmax(0, 1fr)")
        prop("gap", "var(--s-4)")
    }
    sel(".scenes.is-ready .scenes__stage") {
        prop("position", "relative")
        prop("display", "block")
        prop("height", "420px")
        prop("perspective", "1200px")
        prop("perspective-origin", "50% 30%")
        prop("transform-style", "preserve-3d")
    }
    sel(".scenes.is-ready .scenes__panel") {
        prop("--d", "calc(var(--i) - var(--active, 0))")
        prop("position", "absolute")
        prop("left", "50%")
        prop("top", "0")
        prop("width", "min(760px, 88%)")
        prop("transform", "translateX(calc(-50% + var(--d) * 22%)) translateZ(calc(var(--off, 1) * -260px)) rotateY(calc(var(--d) * -14deg))")
        prop("opacity", "calc(1 - var(--off, 1) * .55)")
        prop("filter", "saturate(calc(1 - var(--off, 1) * .4))")
        prop("transition", "transform .8s var(--ease), opacity .6s var(--ease), filter .6s var(--ease)")
        prop("z-index", "calc(10 - var(--off, 1))")
    }
    sel(".scenes.is-ready .scenes__panel.is-active") {
        prop("--off", "0")
    }
    sel(".scenes.is-ready .scenes__panel:not(.is-active)") {
        prop("cursor", "pointer")
    }
    sel(".scenes .term__body") {
        prop("min-height", "230px")
    }
    atMedia("(max-width: 899px)") {
        sel(".scenes.is-ready .scenes__stage") {
            prop("height", "auto")
            prop("perspective", "none")
        }
        sel(".scenes.is-ready .scenes__panel") {
            prop("position", "static")
            prop("width", "auto")
            prop("transform", "none")
            prop("opacity", "1")
            prop("filter", "none")
        }
        sel(".scenes.is-ready .scenes__panel:not(.is-active)") {
            prop("display", "none")
        }
    }
    sel(".route") {
        prop("display", "grid")
        prop("gap", "var(--s-4)")
        prop("padding", "var(--s-5) var(--s-5) var(--s-5)")
        prop("border-radius", "var(--r-xl)")
        prop("overflow", "hidden")
    }
    sel(".route__fit") {
        prop("position", "relative")
        prop("height", "calc(450px * var(--s, 1))")
    }
    sel(".route__box") {
        prop("position", "absolute")
        prop("left", "50%")
        prop("top", "0")
        prop("width", "1000px")
        prop("height", "360px")
        prop("margin-left", "-500px")
        prop("transform", "scale(var(--s, 1))")
        prop("transform-origin", "50% 0")
    }
    sel(".route__svg") {
        prop("position", "absolute")
        prop("inset", "0")
        prop("overflow", "visible")
    }
    sel(".route__track") {
        prop("fill", "none")
        prop("stroke", "rgb(255 255 255 / .1)")
        prop("stroke-width", "2")
    }
    sel(".route__draw") {
        prop("fill", "none")
        prop("stroke", "var(--accent)")
        prop("stroke-width", "3")
        prop("stroke-linecap", "round")
        prop("stroke-dasharray", "1")
        prop("stroke-dashoffset", "var(--dash, 0)")
        prop("filter", "drop-shadow(0 0 6px color-mix(in oklch, var(--accent) 60%, transparent))")
    }
    sel(".route__back") {
        prop("fill", "none")
        prop("stroke", "rgb(255 255 255 / .35)")
        prop("stroke-width", "1.5")
        prop("stroke-dasharray", "4 8")
    }
    sel(".route__stop") {
        prop("fill", "#06201a")
        prop("stroke", "var(--accent)")
        prop("stroke-width", "2")
    }
    sel(".route__stop--back") {
        prop("stroke", "#ffb86b")
    }
    sel(".route__tip") {
        prop("position", "absolute")
        prop("width", "210px")
        prop("padding", "var(--s-3) var(--s-4)")
        prop("border-radius", "var(--r-md)")
        prop("display", "grid")
        prop("gap", "2px")
        prop("font-size", "13px")
        prop("line-height", "1.45")
        prop("color", "var(--muted)")
        prop("translate", "-50% 0")
        prop("opacity", ".45")
        prop("transition", "opacity .4s var(--ease), box-shadow .4s var(--ease)")
        prop("animation", "float 8s ease-in-out infinite")
        prop("animation-delay", "calc(var(--k, 0) * -2s)")
    }
    sel(".route__tip strong") {
        prop("color", "var(--fg-strong)")
        prop("font-size", "14px")
    }
}

private fun CssBuilder.globalCssPart4() {
    sel(".route__tip--up") {
        prop("margin-top", "-118px")
    }
    sel(".route__tip--down") {
        prop("margin-top", "28px")
    }
    sel(".route__tip--back strong") {
        prop("color", "#ffcf9a")
    }
    sel(".route__tip.is-on") {
        prop("opacity", "1")
        prop("box-shadow", "var(--glass-edge), var(--shadow-2), 0 0 0 1px color-mix(in oklch, var(--accent) 40%, transparent)")
    }
    sel(".route__chip") {
        prop("position", "absolute")
        prop("left", "0")
        prop("top", "0")
        prop("offset-path", "var(--route)")
        prop("offset-distance", "var(--dist, 100%)")
        prop("offset-rotate", "0deg")
        prop("offset-anchor", "50% 50%")
        prop("display", "inline-flex")
        prop("align-items", "center")
        prop("gap", "6px")
        prop("padding", "6px 12px")
        prop("border-radius", "var(--r-full)")
        prop("white-space", "nowrap")
        prop("font-family", "var(--font-mono)")
        prop("font-size", "12px")
        prop("font-weight", "600")
        prop("color", "var(--fg-strong)")
        prop("box-shadow", "var(--glass-edge), 0 10px 30px -8px rgb(0 0 0 / .7), 0 0 0 1px color-mix(in oklch, var(--accent) 50%, transparent)")
        prop("z-index", "3")
    }
    sel(".route__chip small") {
        prop("color", "var(--accent)")
        prop("font-size", "11px")
    }
    sel(".route__chip-dot") {
        prop("width", "8px")
        prop("height", "8px")
        prop("border-radius", "2px")
        prop("background", "var(--accent)")
        prop("box-shadow", "0 0 10px var(--accent)")
    }
    sel(".route.is-back .route__chip") {
        prop("box-shadow", "var(--glass-edge), 0 10px 30px -8px rgb(0 0 0 / .7), 0 0 0 1px #ffb86b99")
    }
    sel(".route.is-back .route__chip-dot") {
        prop("background", "#ffb86b")
        prop("box-shadow", "0 0 10px #ffb86b")
    }
    sel(".route-fallback") {
        prop("display", "none")
    }
    atMedia("(max-width: 759px)") {
        sel(".route") {
            prop("display", "none")
        }
        sel(".route-fallback") {
            prop("display", "block")
        }
    }
    sel(".scenes.is-ready .scenes__panel .term") {
        prop("background", "linear-gradient(180deg, color-mix(in oklch, var(--bg) 60%, #1a2418), color-mix(in oklch, var(--bg) 80%, #111))")
        prop("-webkit-backdrop-filter", "none")
        prop("backdrop-filter", "none")
    }
    atMedia("(max-width: 767px)") {
        sel(".stage--ring .stage__tilt") {
            prop("display", "flex")
            prop("flex-direction", "column")
        }
        sel(".stage--ring .pos-a") {
            prop("order", "-1")
        }
        sel(".stage--ring .toggle-chip") {
            prop("width", "100%")
        }
    }
    sel(".copy") {
        prop("transition", "color .18s ease-out, background-color .18s ease-out, scale .12s ease-out")
    }
    atMedia("(prefers-reduced-motion: no-preference)") {
        sel(".copy:active") {
            prop("scale", ".96")
        }
    }
    sel(".toast") {
        prop("position", "fixed")
        prop("left", "50%")
        prop("bottom", "var(--s-6)")
        prop("z-index", "90")
        prop("display", "inline-flex")
        prop("align-items", "center")
        prop("gap", "var(--s-2)")
        prop("padding", "var(--s-2) var(--s-4)")
        prop("border-radius", "var(--r-full)")
        prop("pointer-events", "none")
        prop("font-size", "var(--fs-sm)")
        prop("font-weight", "600")
        prop("color", "var(--fg-strong)")
        prop("background", "var(--glass-fill), color-mix(in oklch, var(--bg) 70%, #1e2536)")
        prop("box-shadow", "var(--glass-edge), var(--shadow-3), 0 0 0 1px var(--accent-line)")
        prop("-webkit-backdrop-filter", "blur(var(--blur-2))")
        prop("backdrop-filter", "blur(var(--blur-2))")
        prop("opacity", "0")
        prop("transform", "translate(-50%, 8px)")
        prop("transition", "opacity .18s ease-out, transform .18s ease-out")
    }
    sel(".toast svg") {
        prop("color", "var(--ok)")
    }
    sel(".toast.is-on") {
        prop("opacity", "1")
        prop("transform", "translate(-50%, 0)")
    }
    sel(".pager__link, .bento__tile, .gs__links a") {
        prop("transition", "translate .2s ease-out, box-shadow .2s ease-out")
    }
    sel(".pager__link:hover, .bento__tile:hover, .gs__links a:hover") {
        prop("translate", "0 -2px")
        prop("box-shadow", "var(--glass-edge), var(--shadow-3), 0 0 0 1px var(--accent-line)")
    }
    sel(".menu__group + .menu__group") {
        prop("margin-top", "var(--s-2)")
    }
    sel(".menu__cat") {
        prop("display", "flex")
        prop("align-items", "center")
        prop("gap", "var(--s-2)")
        prop("padding", "var(--s-2) var(--s-3) var(--s-1)")
        prop("font-size", "var(--fs-xs)")
        prop("font-weight", "600")
        prop("color", "var(--accent)")
    }
    sel(".menu__cat span") {
        prop("color", "var(--subtle)")
        prop("font-weight", "500")
    }
    sel(".cat-chips") {
        prop("display", "flex")
        prop("flex-wrap", "wrap")
        prop("gap", "var(--s-2)")
        prop("margin-bottom", "var(--s-6)")
    }
    sel(".cat-chip") {
        prop("display", "inline-flex")
        prop("align-items", "center")
        prop("gap", "var(--s-2)")
        prop("min-height", "44px")
        prop("padding", "0 var(--s-4)")
        prop("border-radius", "var(--r-full)")
        prop("font-size", "var(--fs-sm)")
        prop("font-weight", "600")
        prop("color", "var(--fg)")
        prop("background", "rgb(255 255 255 / .05)")
        prop("box-shadow", "inset 0 0 0 1px var(--hairline)")
        prop("transition", "background .2s var(--ease), color .2s var(--ease)")
    }
    sel(".cat-chip span") {
        prop("color", "var(--subtle)")
        prop("font-weight", "500")
    }
    sel(".cat-chip:hover") {
        prop("color", "var(--fg-strong)")
        prop("background", "rgb(255 255 255 / .09)")
    }
    sel(".cat-group") {
        prop("scroll-margin-top", "96px")
    }
    sel(".cat-group + .cat-group, .cat-group + .grid") {
        prop("margin-top", "var(--s-8)")
    }
    sel(".cat-group__head") {
        prop("display", "flex")
        prop("flex-wrap", "wrap")
        prop("align-items", "baseline")
        prop("gap", "var(--s-1) var(--s-4)")
        prop("margin-bottom", "var(--s-4)")
    }
    sel(".cat-group__title") {
        prop("font-size", "var(--fs-xl)")
        prop("font-weight", "600")
        prop("color", "var(--fg-strong)")
    }
    sel(".cat-group__head p") {
        prop("color", "var(--muted)")
        prop("font-size", "var(--fs-sm)")
    }
    sel(".cat-tag") {
        prop("margin", "calc(var(--s-1) * -1) 0 var(--s-3)")
        prop("font-size", "var(--fs-xs)")
        prop("font-weight", "600")
        prop("letter-spacing", ".08em")
        prop("text-transform", "uppercase")
        prop("color", "var(--subtle)")
    }
    sel(".badge--cat") {
        prop("text-decoration", "none")
        prop("color", "var(--fg)")
        prop("background", "rgb(255 255 255 / .06)")
        prop("box-shadow", "inset 0 0 0 1px var(--hairline)")
    }
    sel(".badge--cat:hover") {
        prop("color", "var(--fg-strong)")
    }
    atMedia("(prefers-reduced-motion: reduce)") {
        sel(".cat-chip") {
            prop("transition", "none")
        }
    }
    sel(".theme-toggle") {
        prop("display", "inline-grid")
        prop("place-items", "center")
        prop("width", "44px")
        prop("height", "44px")
        prop("padding", "0")
        prop("border", "0")
        prop("border-radius", "var(--r-full)")
        prop("cursor", "pointer")
        prop("color", "var(--muted)")
        prop("background", "transparent")
        prop("box-shadow", "inset 0 0 0 1px var(--hairline)")
        prop("transition", "color .2s, background-color .2s")
    }
    sel(".theme-toggle:hover") {
        prop("color", "var(--fg-strong)")
        prop("background", "var(--hover-1, rgb(255 255 255 / .05))")
    }
    sel(".theme-toggle:focus-visible") {
        prop("box-shadow", "var(--focus)")
        prop("border-radius", "var(--r-full)")
    }
    sel(".theme-toggle__sun, .theme-toggle__moon") {
        prop("display", "none")
    }
    sel("html[data-mode=\"light\"] .theme-toggle__moon, html:not([data-mode=\"light\"]) .theme-toggle__sun") {
        prop("display", "inline-grid")
    }
    sel(".nav__actions") {
        prop("align-items", "center")
    }
    sel(".nav__cta-icon") {
        prop("display", "none")
    }
    atMedia("(max-width: 399px)") {
        sel(".nav__cta") {
            prop("width", "44px")
            prop("height", "44px")
            prop("padding", "0 !important")
            prop("justify-content", "center")
            prop("flex", "none")
        }
        sel(".nav__cta-icon") {
            prop("display", "inline-grid")
            prop("place-items", "center")
        }
        sel(".nav__cta-label") {
            prop("position", "absolute")
            prop("width", "1px")
            prop("height", "1px")
            prop("overflow", "hidden")
            prop("clip", "rect(0 0 0 0)")
            prop("clip-path", "inset(50%)")
            prop("white-space", "nowrap")
        }
    }
    sel(".err") {
        prop("display", "grid")
        prop("place-content", "center")
        prop("justify-items", "center")
        prop("gap", "var(--s-3)")
        prop("min-height", "80dvh")
        prop("padding", "var(--s-8) var(--s-4)")
        prop("text-align", "center")
    }
    sel(".err__code") {
        prop("background", "none")
        prop("user-select", "none")
        prop("-webkit-tap-highlight-color", "transparent")
        prop("margin", "0")
        prop("font-family", "var(--font-mono)")
        prop("font-weight", "700")
        prop("font-size", "clamp(72px, 14vw, 144px)")
        prop("line-height", "1")
        prop("letter-spacing", "-.05em")
        prop("color", "var(--accent)")
    }
    sel(".err--500 .err__code") {
        prop("color", "var(--danger)")
    }
    sel(".err h1") {
        prop("margin", "0")
        prop("font-size", "var(--fs-2xl)")
        prop("color", "var(--fg-strong)")
    }
    sel(".err__text") {
        prop("margin", "0 0 var(--s-4)")
        prop("color", "var(--muted)")
    }
}
