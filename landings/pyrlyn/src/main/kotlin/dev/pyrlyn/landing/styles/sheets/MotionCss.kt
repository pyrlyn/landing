// Ported from the Astro site's motion.css. Kotlin is now the source of truth for these styles.
package dev.pyrlyn.landing.styles.sheets

import dev.pyrlyn.landing.styles.at
import dev.pyrlyn.landing.styles.atMedia
import dev.pyrlyn.landing.styles.prop
import dev.pyrlyn.landing.styles.sel
import dev.pyrlyn.landing.styles.atSupports
import kotlinx.css.CssBuilder

/** Styles from motion.css, in source order. */
internal fun CssBuilder.motionCss() {
    motionCssPart0()
}

private fun CssBuilder.motionCssPart0() {
    atMedia("(prefers-reduced-motion: no-preference)") {
        sel(".intro .hero__copy > *") {
            prop("animation", "intro-rise .8s var(--ease, cubic-bezier(.2, .7, .2, 1)) both")
            prop("animation-delay", "calc(80ms + var(--i, 0) * 70ms)")
        }
        sel(".intro .hero__copy > :nth-child(1)") {
            prop("--i", "0")
        }
        sel(".intro .hero__copy > :nth-child(2)") {
            prop("--i", "1")
        }
        sel(".intro .hero__copy > :nth-child(3)") {
            prop("--i", "2")
        }
        sel(".intro .hero__copy > :nth-child(4)") {
            prop("--i", "3")
        }
        sel(".intro .hero__copy > :nth-child(5)") {
            prop("--i", "4")
        }
        sel(".intro .hero__copy > :nth-child(n + 6)") {
            prop("--i", "5")
        }
        sel(".intro .hero .stage") {
            prop("animation", "intro-stage 1.3s cubic-bezier(.16, 1, .3, 1) .12s both")
            prop("transform-origin", "50% 70%")
        }
        sel(".intro .hero .stage .layer:not(.stage__art)") {
            prop("animation", "intro-chip .9s cubic-bezier(.16, 1, .3, 1) .55s both")
        }
        sel(".intro .nav") {
            prop("animation", "intro-fade .6s ease-out both")
        }
        sel(".rv") {
            prop("opacity", "0")
            prop("translate", "0 28px")
            prop("scale", ".985")
            prop("transition", "opacity .7s cubic-bezier(.2, .7, .2, 1), translate .7s cubic-bezier(.2, .7, .2, 1), scale .7s cubic-bezier(.2, .7, .2, 1)")
            prop("transition-delay", "calc(var(--rv-i, 0) * 60ms)")
        }
        sel(".rv.is-in") {
            prop("opacity", "1")
            prop("translate", "0 0")
            prop("scale", "1")
        }
    }
    sel(".intro-gl") {
        prop("position", "fixed")
        prop("inset", "0")
        prop("width", "100%")
        prop("height", "100%")
        prop("pointer-events", "none")
        prop("z-index", "3")
        prop("mix-blend-mode", "screen")
    }
    sel(".hero") {
        prop("position", "relative")
    }
    atMedia("(prefers-reduced-motion: no-preference)") {
        sel(".brand__cursor") {
            prop("animation", "cursor-blink 1.06s steps(1, end) infinite")
        }
    }
    at("keyframes cursor-blink") {
        sel("0%, 50%") {
            prop("opacity", "1")
        }
        sel("50.01%, 100%") {
            prop("opacity", "0")
        }
    }
    at("keyframes intro-rise") {
        sel("from") {
            prop("opacity", "0")
            prop("translate", "0 18px")
            prop("filter", "blur(6px)")
        }
        sel("to") {
            prop("opacity", "1")
            prop("translate", "0 0")
            prop("filter", "blur(0)")
        }
    }
    at("keyframes intro-stage") {
        sel("from") {
            prop("opacity", "0")
            prop("transform", "perspective(1200px) rotateX(16deg) rotateY(-10deg) translateY(36px) scale(.94)")
        }
        sel("to") {
            prop("opacity", "1")
            prop("transform", "none")
        }
    }
    at("keyframes intro-chip") {
        sel("from") {
            prop("opacity", "0")
            prop("translate", "0 22px")
        }
        sel("to") {
            prop("opacity", "1")
            prop("translate", "0 0")
        }
    }
    at("keyframes intro-fade") {
        sel("from") {
            prop("opacity", "0")
        }
        sel("to") {
            prop("opacity", "1")
        }
    }
    atMedia("(prefers-reduced-motion: reduce)") {
        sel(".intro *, .intro *::before, .intro *::after") {
            prop("animation", "none !important")
        }
        sel(".rv") {
            prop("opacity", "1 !important")
            prop("translate", "none !important")
            prop("scale", "none !important")
            prop("transition", "none !important")
        }
        sel(".intro-gl") {
            prop("display", "none")
        }
        sel(".brand__cursor") {
            prop("animation", "none !important")
            prop("opacity", "1")
        }
    }
}
