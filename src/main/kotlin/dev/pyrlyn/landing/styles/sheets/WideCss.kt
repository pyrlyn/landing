package dev.pyrlyn.landing.styles.sheets

import dev.pyrlyn.landing.styles.atMedia
import dev.pyrlyn.landing.styles.prop
import dev.pyrlyn.landing.styles.sel
import kotlinx.css.CssBuilder

/*
 * Large-monitor layer, loaded last. Below 1600px nothing changes (the container stays 1200px).
 * Above that the container grows continuously with the viewport, max(1200px, 75vw), so it never
 * jumps at a breakpoint and never reaches full width. Root type grows fluidly from 1920px and is
 * capped at 22px (≈3840px). Gutters are fluid so text and hairlines stay off the edges of curved
 * screens. Spacing tokens (4/8pt) are untouched; only rem-based type and widths scale.
 */
internal fun CssBuilder.wideCss() {
    sel(":root") {
        prop("--container", "max(1200px, 75vw)")
        prop("--gutter", "clamp(24px, 1.2rem + 1.5vw, 96px)")
    }
    sel("img, video, canvas, svg") {
        prop("max-width", "100%")
    }
    sel("img, video") {
        prop("height", "auto")
        prop("object-fit", "contain")
    }
    atMedia("(min-width: 1536px)") {
        sel(".container") {
            prop("padding-inline", "var(--gutter)")
        }
        sel(".container--narrow") {
            prop("max-width", "min(100%, 56rem)")
        }
        sel(".section__head") {
            prop("max-width", "min(100%, 44rem)")
        }
        sel(".nav") {
            prop("padding-inline", "var(--gutter)")
        }
    }
    atMedia("(min-width: 1920px)") {
        sel("html") {
            // 16px at 1920, ~18.9px at 2560, 22px cap from ~3640.
            prop("font-size", "clamp(16px, 7.36px + .45vw, 22px)")
        }
        sel(".grid") {
            prop("gap", "var(--s-6)")
        }
    }
    atMedia("(min-width: 2560px)") {
        sel(".section") {
            prop("padding-block", "calc(var(--s-10) + var(--s-6))")
        }
    }
}
