// Ported from the Astro site's base.css. Kotlin is now the source of truth for these styles.
package dev.pyrlyn.landing.styles.sheets

import dev.pyrlyn.landing.styles.at
import dev.pyrlyn.landing.styles.atMedia
import dev.pyrlyn.landing.styles.prop
import dev.pyrlyn.landing.styles.sel
import dev.pyrlyn.landing.styles.atSupports
import kotlinx.css.CssBuilder

/** Styles from base.css, in source order. */
internal fun CssBuilder.baseCss() {
    baseCssPart0()
}

private fun CssBuilder.baseCssPart0() {
    at("property --rx") {
        prop("syntax", "\"<angle>\"")
        prop("inherits", "true")
        prop("initial-value", "0deg")
    }
    at("property --ry") {
        prop("syntax", "\"<angle>\"")
        prop("inherits", "true")
        prop("initial-value", "0deg")
    }
    sel(":root") {
        prop("--font-sans", "\"Satoshi\", ui-sans-serif, system-ui, -apple-system, \"Segoe UI\", Roboto, sans-serif")
    }
    sel("*, *::before, *::after") {
        prop("box-sizing", "border-box")
    }
    sel("html") {
        prop("background", "var(--bg)")
        prop("-webkit-text-size-adjust", "100%")
        prop("scroll-behavior", "smooth")
        prop("scroll-padding-top", "96px")
    }
    sel("body") {
        prop("position", "relative")
        prop("margin", "0")
        prop("min-height", "100vh")
        prop("overflow-x", "hidden")
        prop("color", "var(--fg)")
        prop("font-family", "var(--font-sans)")
        prop("font-size", "var(--fs-md)")
        prop("line-height", "1.6")
        prop("-webkit-font-smoothing", "antialiased")
        prop("text-rendering", "optimizeLegibility")
    }
    sel("img, svg, picture") {
        prop("display", "block")
        prop("max-width", "100%")
    }
    sel("img") {
        prop("height", "auto")
    }
    sel("a") {
        prop("color", "inherit")
        prop("text-decoration", "none")
    }
    sel("p, figure, blockquote") {
        prop("margin", "0")
    }
    sel("h1, h2, h3") {
        prop("margin", "0")
        prop("color", "var(--fg-strong)")
        prop("font-weight", "660")
        prop("line-height", "1.1")
        prop("letter-spacing", "-.02em")
        prop("text-wrap", "balance")
    }
    sel("p") {
        prop("text-wrap", "pretty")
    }
    sel("ul, ol") {
        prop("margin", "0")
        prop("padding", "0")
        prop("list-style", "none")
    }
    sel("code, pre, kbd") {
        prop("font-family", "var(--font-mono)")
    }
    sel(":focus") {
        prop("outline", "none")
    }
    sel(":focus-visible") {
        prop("outline", "2px solid transparent")
        prop("outline-offset", "2px")
        prop("box-shadow", "var(--focus)")
        prop("border-radius", "var(--r-xs)")
    }
    sel("main:focus") {
        prop("box-shadow", "none")
    }
    sel("::selection") {
        prop("background", "color-mix(in oklch, var(--accent) 40%, transparent)")
        prop("color", "var(--fg-strong)")
    }
    sel(".skip-link") {
        prop("position", "absolute")
        prop("left", "var(--s-4)")
        prop("top", "-120px")
        prop("z-index", "100")
        prop("display", "inline-flex")
        prop("align-items", "center")
        prop("min-height", "44px")
        prop("padding", "0 var(--s-4)")
        prop("border-radius", "var(--r-sm)")
        prop("background", "var(--accent)")
        prop("color", "var(--on-accent)")
        prop("font-weight", "600")
    }
    sel(".skip-link:focus-visible") {
        prop("top", "var(--s-4)")
    }
    sel(".sr-only") {
        prop("position", "absolute !important")
        prop("width", "1px")
        prop("height", "1px")
        prop("padding", "0")
        prop("margin", "-1px")
        prop("overflow", "hidden")
        prop("clip", "rect(0 0 0 0)")
        prop("white-space", "nowrap")
        prop("border", "0")
    }
    sel(".mesh") {
        prop("position", "absolute")
        prop("inset", "0")
        prop("z-index", "-1")
        prop("overflow", "hidden")
        prop("pointer-events", "none")
        prop("background", "var(--bg)")
    }
    sel(".mesh__blob") {
        prop("position", "absolute")
        prop("border-radius", "50%")
        prop("filter", "blur(var(--blur-3)) blur(40px)")
        prop("will-change", "transform")
    }
    sel(".mesh__blob--1") {
        prop("width", "min(1100px, 130vw)")
        prop("aspect-ratio", "1")
        prop("left", "-22%")
        prop("top", "-520px")
        prop("background", "radial-gradient(closest-side, color-mix(in oklch, var(--accent) 42%, transparent), transparent)")
        prop("animation", "drift-1 28s var(--ease) infinite alternate")
    }
    sel(".mesh__blob--2") {
        prop("width", "min(900px, 110vw)")
        prop("aspect-ratio", "1")
        prop("right", "-24%")
        prop("top", "80px")
        prop("background", "radial-gradient(closest-side, color-mix(in oklch, var(--accent-2) 24%, transparent), transparent)")
        prop("animation", "drift-2 34s var(--ease) infinite alternate")
    }
    sel(".mesh__blob--3") {
        prop("width", "min(1000px, 120vw)")
        prop("aspect-ratio", "1")
        prop("left", "-20%")
        prop("top", "32%")
        prop("background", "radial-gradient(closest-side, color-mix(in oklch, var(--accent) 28%, transparent), transparent)")
        prop("animation", "drift-3 40s var(--ease) infinite alternate")
    }
    sel(".mesh__blob--4") {
        prop("width", "min(900px, 110vw)")
        prop("aspect-ratio", "1")
        prop("right", "-22%")
        prop("top", "58%")
        prop("background", "radial-gradient(closest-side, color-mix(in oklch, var(--accent-2) 22%, transparent), transparent)")
        prop("animation", "drift-1 24s var(--ease) infinite alternate-reverse")
    }
    sel(".mesh__blob--5") {
        prop("width", "min(1000px, 120vw)")
        prop("aspect-ratio", "1")
        prop("left", "10%")
        prop("bottom", "-380px")
        prop("background", "radial-gradient(closest-side, color-mix(in oklch, var(--accent) 26%, transparent), transparent)")
        prop("animation", "drift-2 30s var(--ease) infinite alternate-reverse")
    }
    sel(".mesh__grid") {
        prop("position", "absolute")
        prop("inset", "0")
        prop("background-image", "linear-gradient(rgb(255 255 255 / .035) 1px, transparent 1px), linear-gradient(90deg, rgb(255 255 255 / .035) 1px, transparent 1px)")
        prop("background-size", "64px 64px")
        prop("height", "1100px")
        prop("bottom", "auto")
        prop("mask-image", "radial-gradient(ellipse 70% 70% at 50% 0%, #000 20%, transparent 75%)")
        prop("-webkit-mask-image", "radial-gradient(ellipse 70% 70% at 50% 0%, #000 20%, transparent 75%)")
    }
    sel(".mesh__grain") {
        prop("position", "absolute")
        prop("inset", "0")
        prop("opacity", ".07")
        prop("mix-blend-mode", "overlay")
        prop("background-image", "url(\"data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='160' height='160'><filter id='n'><feTurbulence type='fractalNoise' baseFrequency='.9' numOctaves='3' stitchTiles='stitch'/></filter><rect width='100%' height='100%' filter='url(%23n)'/></svg>\")")
    }
    atMedia("(prefers-reduced-motion: no-preference)") {
        atSupports("(animation-timeline: scroll())") {
            sel(".mesh__blob") {
                prop("animation-name", "var(--drift), par")
                prop("animation-timeline", "auto, scroll(root)")
                prop("animation-duration", "var(--dur), 1ms")
                prop("animation-timing-function", "var(--ease), linear")
                prop("animation-iteration-count", "infinite, 1")
                prop("animation-direction", "alternate, normal")
                prop("animation-fill-mode", "none, both")
            }
            sel(".mesh__blob--1") {
                prop("--drift", "drift-1")
                prop("--dur", "28s")
                prop("--par", "260px")
            }
            sel(".mesh__blob--2") {
                prop("--drift", "drift-2")
                prop("--dur", "34s")
                prop("--par", "420px")
            }
            sel(".mesh__blob--3") {
                prop("--drift", "drift-3")
                prop("--dur", "40s")
                prop("--par", "-320px")
            }
            sel(".mesh__blob--4") {
                prop("--drift", "drift-1")
                prop("--dur", "24s")
                prop("--par", "-220px")
            }
            sel(".mesh__blob--5") {
                prop("--drift", "drift-2")
                prop("--dur", "30s")
                prop("--par", "-380px")
            }
            sel(".mesh__grid") {
                prop("animation", "par linear both")
                prop("animation-timeline", "scroll(root)")
                prop("--par", "180px")
            }
        }
    }
    at("keyframes par") {
        sel("from") {
            prop("translate", "0 0")
        }
        sel("to") {
            prop("translate", "0 var(--par, 0px)")
        }
    }
    at("keyframes drift-1") {
        sel("to") {
            prop("transform", "translate3d(6%, 5%, 0) scale(1.08)")
        }
    }
    at("keyframes drift-2") {
        sel("to") {
            prop("transform", "translate3d(-6%, 7%, 0) scale(.92)")
        }
    }
    at("keyframes drift-3") {
        sel("to") {
            prop("transform", "translate3d(5%, -6%, 0) scale(1.1)")
        }
    }
    sel(".is-still .mesh__blob") {
        prop("animation-play-state", "paused, running")
    }
    sel(".is-still .stage .float, .is-still .stage .hotspot__dot::after") {
        prop("animation-play-state", "paused")
    }
}
