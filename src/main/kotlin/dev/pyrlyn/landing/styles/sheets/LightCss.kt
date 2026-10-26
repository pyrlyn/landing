// Ported from the Astro site's light.css. Kotlin is now the source of truth for these styles.
package dev.pyrlyn.landing.styles.sheets

import dev.pyrlyn.landing.styles.at
import dev.pyrlyn.landing.styles.atMedia
import dev.pyrlyn.landing.styles.prop
import dev.pyrlyn.landing.styles.sel
import dev.pyrlyn.landing.styles.atSupports
import kotlinx.css.CssBuilder

/** Styles from light.css, in source order. */
internal fun CssBuilder.lightCss() {
    lightCssPart0()
}

private fun CssBuilder.lightCssPart0() {
    sel("html[data-mode=\"light\"]") {
        prop("color-scheme", "light")
        prop("--bg", "#f4f6f9 !important")
        prop("--accent", "var(--accent-light) !important")
        prop("--fg", "#121821")
        prop("--fg-strong", "#05080d")
        prop("--muted", "#4c5868")
        prop("--subtle", "#5f6b7d")
        prop("--on-accent", "#ffffff")
        prop("--danger", "#b42318")
        prop("--ok", "#15803d")
        prop("--bg-elev", "#ffffff")
        prop("--surface", "rgb(255 255 255 / .74)")
        prop("--surface-lifted", "rgb(255 255 255 / .92)")
        prop("--surface-opaque", "#ffffff")
        prop("--glass-fill", "linear-gradient(180deg, rgb(255 255 255 / .72), rgb(255 255 255 / .5))")
        prop("--glass-edge", "inset 0 0 0 1px rgb(10 20 35 / .08), inset 0 1px #ffffffcc, inset 0 -1px rgb(10 20 35 / .04)")
        prop("--hairline", "rgb(10 20 35 / .1)")
        prop("--hairline-strong", "rgb(10 20 35 / .18)")
        prop("--hover-1", "rgb(10 20 35 / .05)")
        prop("--shadow-1", "0 0 0 1px rgb(15 25 40 / .06), 0 1px 2px rgb(15 25 40 / .08)")
        prop("--shadow-2", "0 0 0 1px rgb(15 25 40 / .06), 0 2px 4px rgb(15 25 40 / .06), 0 8px 16px -4px rgb(15 25 40 / .1)")
        prop("--shadow-3", "0 0 0 1px rgb(15 25 40 / .06), 0 4px 8px rgb(15 25 40 / .06), 0 16px 32px -8px rgb(15 25 40 / .14)")
        prop("--focus", "0 0 0 3px var(--bg), 0 0 0 5px var(--accent)")
    }
    sel("html[data-mode=\"light\"] .intro-gl") {
        prop("mix-blend-mode", "normal")
    }
    sel("html[data-mode=\"light\"] .mesh__grid") {
        prop("opacity", ".5")
        prop("filter", "invert(1)")
    }
    sel("html[data-mode=\"light\"] :is(.code, .term, .particles__stage)") {
        prop("--fg", "#eef1f6")
        prop("--fg-strong", "#ffffff")
        prop("--muted", "#a4adbb")
        prop("--subtle", "#8a94a5")
        prop("--accent", "var(--accent-dark)")
        prop("--hairline", "rgb(255 255 255 / .1)")
        prop("--hairline-strong", "rgb(255 255 255 / .18)")
        prop("color", "var(--fg)")
        prop("background", "#0d1220")
    }
    sel("html[data-mode=\"light\"] .term__body") {
        prop("background", "#0d1220")
    }
    sel("html[data-mode=\"light\"] .term") {
        prop("box-shadow", "0 0 0 1px rgb(10 20 35 / .35), 0 18px 40px -16px rgb(15 25 40 / .45)")
    }
    sel("html[data-mode=\"light\"] .term__bar") {
        prop("background", "#151b2b")
        prop("--fg", "#eef1f6")
        prop("--fg-strong", "#fff")
        prop("--muted", "#a4adbb")
        prop("--hairline", "rgb(255 255 255 / .1)")
    }
    sel("html[data-mode=\"light\"] .term__ctl") {
        prop("background", "rgb(255 255 255 / .1)")
    }
    sel("html[data-mode=\"light\"] :is(.segmented, .scenes__tabs)") {
        prop("background", "rgb(10 20 35 / .06)")
    }
    sel("html[data-mode=\"light\"] .price-tier") {
        prop("background", "rgb(10 20 35 / .035)")
    }
    sel("html[data-mode=\"light\"] .price-tier--pro") {
        prop("background", "linear-gradient(180deg, color-mix(in oklch, var(--card-accent) 10%, transparent), rgb(10 20 35 / .03))")
    }
    sel("html[data-mode=\"light\"] .segmented button[aria-pressed=\"true\"]") {
        prop("background", "#fff")
    }
    sel("html[data-mode=\"light\"] :is(.nav__links a:hover, .menu__item:hover, .menu__item[aria-current=\"page\"], .menu__trigger:hover, .menu__details[open] .menu__trigger)") {
        prop("background", "var(--hover-1)")
    }
    sel("html[data-mode=\"light\"] .nav__links a[aria-current=\"page\"]") {
        prop("background", "rgb(10 20 35 / .07)")
    }
    sel("html[data-mode=\"light\"] .menu__panel") {
        prop("background", "#ffffff")
    }
    sel("html[data-mode=\"light\"] :is(.oss-card__link, .oss-card__title span, .menu__ver, .menu__mark, .hotspot__kicker, .hotspot__tip a, .featured__title span, .featured__list svg, .ring__title span, .ring__foot, .badge--version)") {
        prop("color", "color-mix(in oklch, var(--card-accent) 50%, #0b1220)")
    }
    sel("html[data-mode=\"light\"] .badge--placeholder") {
        prop("color", "#8a5a00")
        prop("background", "rgb(255 191 90 / .18)")
        prop("box-shadow", "inset 0 0 0 1px rgb(180 120 0 / .45)")
    }
    sel("html[data-mode=\"light\"] .gradient-text") {
        prop("background-image", "linear-gradient(100deg, var(--accent) 0%, color-mix(in oklch, var(--accent-2) 60%, #0b1220) 100%)")
    }
    sel("html[data-mode=\"light\"] .ring__card") {
        prop("background", "radial-gradient(80% 60% at 50% 0%, color-mix(in oklch, var(--card-accent) 14%, transparent), transparent 70%), #ffffff")
        prop("box-shadow", "var(--shadow-3), 0 0 0 1px color-mix(in oklch, var(--card-accent) 25%, transparent)")
    }
    sel("html[data-mode=\"light\"] :is(.inline-code, .prose :not(pre) > code, .card :not(pre) > code)") {
        prop("background", "rgb(10 20 35 / .06)")
    }
    sel("html[data-mode=\"light\"] .ring__card") {
        prop("background", "radial-gradient(80% 60% at 50% 0%, color-mix(in oklch, var(--card-accent) 12%, #ffffff), #ffffff 70%), #ffffff")
        prop("box-shadow", "0 0 0 1px rgb(10 20 35 / .14), 0 1px 2px rgb(15 25 40 / .08), 0 14px 32px -12px rgb(15 25 40 / .28)")
    }
    sel("html[data-mode=\"light\"] .ring__item:not(.is-active) .ring__face") {
        prop("opacity", "1")
        prop("filter", "saturate(.75) brightness(.95)")
        prop("scale", ".94")
    }
    sel("html[data-mode=\"light\"] .ring__item.is-active .ring__card") {
        prop("box-shadow", "0 0 0 1px color-mix(in oklch, var(--card-accent) 45%, rgb(10 20 35 / .2)), 0 2px 4px rgb(15 25 40 / .08), 0 22px 44px -14px rgb(15 25 40 / .38)")
    }
    sel("html[data-mode=\"light\"] .ring__btn") {
        prop("background", "#ffffff")
        prop("box-shadow", "0 0 0 1px rgb(10 20 35 / .14), var(--shadow-1)")
    }
    sel("html[data-mode=\"light\"] .wishlist__email") {
        prop("background", "#ffffff")
    }
    sel("html[data-mode=\"light\"] .btn[aria-disabled=\"true\"]") {
        prop("opacity", "1")
        prop("filter", "none")
        prop("background", "#e7eaf0")
        prop("color", "#3f4b5c")
        prop("box-shadow", "inset 0 0 0 1px rgb(10 20 35 / .16)")
        prop("text-shadow", "none")
    }
    sel("html[data-mode=\"light\"] .ring__art") {
        prop("mix-blend-mode", "normal")
        prop("width", "132px")
        prop("margin", "var(--s-2) auto var(--s-3)")
        prop("filter", "invert(1) hue-rotate(180deg) saturate(.9) brightness(1.04)")
        prop("mask-image", "radial-gradient(closest-side, #000 55%, transparent 100%)")
        prop("-webkit-mask-image", "radial-gradient(closest-side, #000 55%, transparent 100%)")
    }
    sel("html[data-mode=\"light\"] .stage--ring .stage__art") {
        prop("display", "none")
    }
    sel("html[data-mode=\"light\"] .stage--ring::before") {
        prop("content", "\"\"")
        prop("position", "absolute")
        prop("inset", "4% -8% 0 -8%")
        prop("z-index", "0")
        prop("pointer-events", "none")
        prop("background", "radial-gradient(closest-side, color-mix(in oklch, var(--accent) 12%, transparent), transparent 100%)")
    }
    sel("html[data-mode=\"light\"] .ring__item:not(.is-active) .ring__card") {
        prop("box-shadow", "0 0 0 1px rgb(10 20 35 / .12), 0 6px 14px -8px rgb(15 25 40 / .22)")
    }
    sel("html[data-mode=\"light\"] :is(.dnav__panel, .hotspot__tip, .term-chip, .route__chip, .scenes.is-ready .scenes__panel .term)") {
        prop("background", "#ffffff")
    }
    sel("html[data-mode=\"light\"] .dnav__panel, html[data-mode=\"light\"] .hotspot__tip") {
        prop("box-shadow", "0 0 0 1px rgb(10 20 35 / .12), var(--shadow-3)")
    }
    sel("html[data-mode=\"light\"] .scenes.is-ready .scenes__panel .term") {
        prop("background", "#0d1220")
    }
    sel("html[data-mode=\"light\"] .codeblock::after") {
        prop("background", "#ffffff")
    }
    sel("html[data-mode=\"light\"] .table-wrap") {
        prop("background", "#ffffff")
        prop("box-shadow", "0 0 0 1px rgb(10 20 35 / .12)")
    }
    sel("html[data-mode=\"light\"] .bitset__grid") {
        prop("background", "rgb(10 20 35 / .04)")
    }
    sel("html[data-mode=\"light\"] :is(.price-tier, .scenes__tabs)") {
        prop("box-shadow", "inset 0 0 0 1px rgb(10 20 35 / .1)")
    }
    sel("html[data-mode=\"light\"] :is(.card, .featured, .install-card, .faq__item, .license, .cta, .tier)") {
        prop("box-shadow", "0 0 0 1px rgb(10 20 35 / .09), 0 1px 2px rgb(15 25 40 / .05), 0 12px 28px -14px rgb(15 25 40 / .16)")
    }
    sel("html[data-mode=\"light\"] .eyebrow") {
        prop("color", "color-mix(in oklch, var(--accent) 72%, #0b1220)")
    }
    sel("html[data-mode=\"light\"] :is(.step__n, .nextcard__n)") {
        prop("background-image", "linear-gradient(180deg, var(--accent), color-mix(in oklch, var(--accent) 35%, transparent) 120%)")
    }
    sel("html[data-mode=\"light\"] .rail__n") {
        prop("background", "var(--accent)")
        prop("box-shadow", "0 0 0 6px color-mix(in oklch, var(--accent) 14%, transparent)")
    }
    sel("html[data-mode=\"light\"] .callout:not(.callout--tip):not(.callout--important)") {
        prop("--c", "color-mix(in oklch, var(--accent-2) 55%, #0b1220)")
    }
    sel("html[data-mode=\"light\"] :is(.callout--warning, .callout--caution)") {
        prop("--c", "#8a5a00")
    }
    sel("html[data-mode=\"light\"] .callout--tip") {
        prop("--c", "#15803d")
    }
    sel("html[data-mode=\"light\"] .menu__current") {
        prop("color", "color-mix(in oklch, var(--accent) 80%, #0b1220)")
    }
    sel("html[data-mode=\"light\"] :is(.menu__mark, .menu__ver)") {
        prop("color", "color-mix(in oklch, var(--card-accent) 38%, #0b1220)")
    }
    sel("html[data-mode=\"light\"] .btn--primary") {
        prop("color", "#ffffff")
        prop("text-shadow", "none")
    }
    sel("html[data-mode=\"light\"] .btn--primary:hover") {
        prop("box-shadow", "inset 0 1px 0 rgb(255 255 255 / .35), 0 8px 22px -8px color-mix(in oklch, var(--accent) 70%, transparent)")
    }
    sel("html[data-mode=\"light\"] .btn--glass") {
        prop("background", "#ffffff")
        prop("box-shadow", "0 0 0 1px rgb(10 20 35 / .14), var(--shadow-1)")
        prop("color", "var(--fg-strong)")
    }
    sel("html[data-mode=\"light\"] .btn--glass:hover") {
        prop("background", "#f1f4f8")
    }
    sel("html[data-mode=\"light\"] .stage:not(.stage--ring) .stage__art") {
        prop("mix-blend-mode", "normal")
        prop("opacity", ".9")
    }
    sel("html[data-mode=\"light\"] .stage:not(.stage--ring) .stage__art img") {
        prop("filter", "invert(1) hue-rotate(180deg) saturate(.9) brightness(1.04)")
    }
    sel("html[data-mode=\"light\"][data-theme=\"bindsmith\"]") {
        prop("--bs-container", "#f6e6d3")
        prop("--bs-secondary-container", "#d9edf9")
    }
    sel("html[data-mode=\"light\"][data-theme=\"bindsmith\"] .eyebrow") {
        prop("color", "#5a3410")
    }
    sel("html[data-mode=\"light\"][data-theme=\"bindsmith\"] :is(.feature, .install-card)") {
        prop("background", "#ffffff")
        prop("box-shadow", "0 0 0 1px rgb(10 20 35 / .1), var(--shadow-1)")
    }
    sel("html[data-mode=\"light\"][data-theme=\"bindsmith\"] .dnav__item[aria-current=\"page\"]") {
        prop("color", "#0d3a57")
    }
    sel("html[data-mode=\"light\"] .bs-chips__row b") {
        prop("box-shadow", "inset 0 0 0 1px rgb(10 20 35 / .2)")
    }
    sel("html[data-mode=\"light\"] .bs-chips__row b:first-child") {
        prop("color", "#0d3a57")
    }
}
