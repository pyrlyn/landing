// Ported from the Astro site's search.css. Kotlin is now the source of truth for these styles.
package dev.pyrlyn.landing.styles.sheets

import dev.pyrlyn.landing.styles.at
import dev.pyrlyn.landing.styles.atMedia
import dev.pyrlyn.landing.styles.prop
import dev.pyrlyn.landing.styles.sel
import dev.pyrlyn.landing.styles.atSupports
import kotlinx.css.CssBuilder

/** Styles from search.css, in source order. */
internal fun CssBuilder.searchCss() {
    searchCssPart0()
}

private fun CssBuilder.searchCssPart0() {
    sel(".search") {
        prop("position", "relative")
        prop("display", "flex")
        prop("align-items", "center")
        prop("width", "44px")
        prop("height", "44px")
        prop("transition", "width .2s var(--ease)")
        prop("container-type", "inline-size")
    }
    sel(".search[data-open=\"true\"]") {
        prop("width", "280px")
    }
    atMedia("(min-width:1024px)") {
        sel(".search[data-open=\"true\"]") {
            prop("width", "320px")
        }
    }
    sel(".search__btn") {
        prop("position", "absolute")
        prop("right", "0")
        prop("z-index", "1")
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
        prop("transition", "transform .2s var(--ease),color .2s,background-color .2s,box-shadow .2s")
    }
    sel(".search__btn:hover") {
        prop("color", "var(--fg-strong)")
        prop("background", "var(--hover-1)")
    }
    sel(".search__btn:focus-visible") {
        prop("box-shadow", "var(--focus)")
    }
    sel(".search[data-open=\"true\"] .search__btn") {
        prop("transform", "translateX(calc(-1 * (100cqw - 44px)))")
        prop("color", "var(--subtle)")
        prop("background", "transparent")
        prop("box-shadow", "none")
        prop("pointer-events", "none")
    }
    sel(".search__input") {
        prop("width", "100%")
        prop("height", "44px")
        prop("padding", "0 48px 0 40px")
        prop("border", "0")
        prop("border-radius", "var(--r-full)")
        prop("font", "inherit")
        prop("font-size", "var(--fs-sm)")
        prop("color", "var(--fg)")
        prop("background", "var(--surface-opaque)")
        prop("box-shadow", "inset 0 0 0 1px var(--hairline-strong)")
        prop("opacity", "0")
        prop("transition", "opacity .16s var(--ease) .12s,box-shadow .2s")
        prop("-webkit-appearance", "none")
        prop("appearance", "none")
    }
    sel(".search__input::-webkit-search-cancel-button") {
        prop("display", "none")
    }
    sel(".search__input::placeholder") {
        prop("color", "var(--subtle)")
    }
    sel(".search__input:focus-visible") {
        prop("outline", "none")
        prop("box-shadow", "var(--focus)")
    }
    sel(".search[data-open=\"false\"] .search__input") {
        prop("pointer-events", "none")
        prop("transition-delay", "0s")
        prop("transition-duration", ".22s")
    }
    sel(".search[data-open=\"true\"] .search__input") {
        prop("opacity", "1")
    }
    sel(".search__btn svg") {
        prop("transition", "transform .2s var(--ease)")
    }
    sel(".search[data-open=\"true\"] .search__btn svg") {
        prop("transform", "translateX(-2px)")
    }
    sel(".search__kbd") {
        prop("position", "absolute")
        prop("right", "12px")
        prop("font", "inherit")
        prop("font-size", "var(--fs-xs)")
        prop("color", "var(--subtle)")
        prop("padding", "2px 6px")
        prop("border-radius", "var(--r-sm)")
        prop("box-shadow", "inset 0 0 0 1px var(--hairline)")
        prop("opacity", "0")
        prop("pointer-events", "none")
        prop("transition", "opacity .16s var(--ease)")
    }
    sel(".search[data-open=\"true\"] .search__kbd") {
        prop("opacity", "1")
        prop("transition-delay", ".12s")
    }
    sel(".search__cancel") {
        prop("display", "none")
    }
    sel(".search__panel") {
        prop("position", "absolute")
        prop("top", "calc(100% + var(--s-2))")
        prop("right", "0")
        prop("width", "420px")
        prop("max-height", "60vh")
        prop("overflow", "auto")
        prop("padding", "var(--s-2)")
        prop("border-radius", "var(--r-lg)")
        prop("background", "var(--surface-opaque)")
        prop("box-shadow", "var(--shadow-2),inset 0 0 0 1px var(--hairline)")
        prop("opacity", "0")
        prop("transform", "translateY(-4px)")
        prop("transition", "opacity .16s var(--ease),transform .16s var(--ease)")
    }
    sel(".search__panel[data-visible=\"true\"]") {
        prop("opacity", "1")
        prop("transform", "none")
    }
    sel(".search__row") {
        prop("display", "flex")
        prop("align-items", "center")
        prop("gap", "var(--s-3)")
        prop("min-height", "44px")
        prop("padding", "0 var(--s-3)")
        prop("border-radius", "var(--r-md)")
        prop("color", "var(--fg)")
        prop("text-decoration", "none")
        prop("cursor", "pointer")
    }
    sel(".search__row[aria-selected=\"true\"],.search__row:hover") {
        prop("background", "var(--hover-1)")
    }
    sel(".search__icon") {
        prop("flex", "none")
        prop("width", "20px")
        prop("height", "20px")
        prop("border-radius", "6px")
        prop("background", "var(--row-accent,var(--hairline-strong))")
    }
    sel(".search__title") {
        prop("flex", "1")
        prop("min-width", "0")
        prop("overflow", "hidden")
        prop("text-overflow", "ellipsis")
        prop("white-space", "nowrap")
        prop("color", "var(--fg)")
    }
    sel(".search__section") {
        prop("flex", "none")
        prop("font-size", "var(--fs-xs)")
        prop("color", "var(--muted)")
    }
    sel(".search__empty") {
        prop("padding", "var(--s-3)")
        prop("font-size", "var(--fs-sm)")
        prop("color", "var(--muted)")
    }
    atMedia("(max-width:639px)") {
        sel(".search[data-open=\"true\"]") {
            prop("position", "absolute")
            prop("inset", "var(--s-3) var(--s-4) auto")
            prop("width", "auto")
            prop("z-index", "60")
            prop("background", "var(--surface-opaque)")
            prop("box-shadow", "0 0 0 var(--s-3) var(--surface-opaque)")
            prop("border-radius", "var(--r-full)")
            prop("padding-right", "72px")
        }
        sel(".search[data-open=\"true\"] .search__kbd") {
            prop("display", "none")
        }
        sel(".search[data-open=\"true\"] .search__cancel") {
            prop("display", "inline-grid")
            prop("place-items", "center")
            prop("position", "absolute")
            prop("right", "0")
            prop("height", "44px")
            prop("min-width", "44px")
            prop("padding", "0 var(--s-2)")
            prop("border", "0")
            prop("border-radius", "var(--r-full)")
            prop("background", "var(--surface-opaque)")
            prop("font", "inherit")
            prop("font-size", "var(--fs-sm)")
            prop("color", "var(--fg)")
            prop("cursor", "pointer")
        }
        sel(".search[data-open=\"true\"] .search__cancel:focus-visible") {
            prop("box-shadow", "var(--focus)")
        }
        sel(".search[data-open=\"true\"] .search__btn") {
            prop("transform", "none")
            prop("left", "0")
            prop("right", "auto")
        }
        sel(".search__panel") {
            prop("position", "fixed")
            prop("top", "72px")
            prop("left", "0")
            prop("right", "0")
            prop("width", "auto")
            prop("max-height", "calc(100dvh - 72px)")
            prop("border-radius", "0")
        }
        sel("html.search-lock") {
            prop("overflow", "hidden")
        }
    }
    atMedia("(prefers-reduced-motion:reduce)") {
        sel(".search,.search__btn,.search__btn svg") {
            prop("transition", "none")
        }
        sel(".search__input,.search__kbd,.search__panel") {
            prop("transition", "opacity .12s linear")
            prop("transition-delay", "0s")
        }
        sel(".search__panel") {
            prop("transform", "none")
        }
    }
    sel(".search__title mark") {
        prop("background", "none")
        prop("color", "var(--fg-strong)")
        prop("font-weight", "600")
        prop("text-decoration", "underline")
        prop("text-decoration-color", "var(--accent)")
        prop("text-underline-offset", "3px")
    }
    sel(".search__empty--error") {
        prop("color", "var(--danger)")
    }
}
