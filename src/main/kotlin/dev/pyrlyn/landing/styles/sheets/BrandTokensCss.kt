// Ported from the Astro site's brand-tokens.css. Kotlin is now the source of truth for these styles.
package dev.pyrlyn.landing.styles.sheets

import dev.pyrlyn.landing.styles.at
import dev.pyrlyn.landing.styles.atMedia
import dev.pyrlyn.landing.styles.prop
import dev.pyrlyn.landing.styles.sel
import dev.pyrlyn.landing.styles.atSupports
import kotlinx.css.CssBuilder

/** Styles from brand-tokens.css, in source order. */
internal fun CssBuilder.brandTokensCss() {
    brandTokensCssPart0()
}

private fun CssBuilder.brandTokensCssPart0() {
    sel(":root") {
        prop("color-scheme", "dark")
        prop("--accent", "#4c8dff")
        prop("--accent-2", "#3ee6c4")
        prop("--accent-light", "#2f6bdb")
        prop("--bg", "#070910")
        prop("--fg", "#eef1f6")
        prop("--fg-strong", "#ffffff")
        prop("--muted", "#a4adbb")
        prop("--subtle", "#7d8797")
        prop("--on-accent", "#050810")
        prop("--danger", "#ff7a6b")
        prop("--ok", "#5ee3a1")
        prop("--accent-soft", "color-mix(in oklch, var(--accent) 16%, transparent)")
        prop("--accent-line", "color-mix(in oklch, var(--accent) 38%, transparent)")
        prop("--accent-glow", "color-mix(in oklch, var(--accent) 55%, transparent)")
        prop("--bg-elev", "color-mix(in oklch, var(--bg) 88%, white 4%)")
        prop("--surface", "rgb(14 18 27 / .62)")
        prop("--surface-lifted", "rgb(38 46 62 / .62)")
        prop("--surface-fill", "radial-gradient(51% 92% at 51% 8%, var(--surface-lifted), var(--surface))")
        prop("--surface-opaque", "color-mix(in oklch, var(--bg) 80%, #1b2130)")
        prop("--glass-fill", "linear-gradient(180deg, rgb(255 255 255 / .08), rgb(255 255 255 / .03))")
        prop("--glass-edge", "inset 0 0 0 1px #ffffff1a, inset 0 1px #ffffff1a, inset 0 -1px #0003")
        prop("--hairline", "rgb(255 255 255 / .08)")
        prop("--hairline-strong", "rgb(255 255 255 / .14)")
        prop("--blur-1", "2px")
        prop("--blur-2", "10px")
        prop("--blur-3", "36px")
        prop("--shadow-1", "0 0 0 1px rgb(0 0 0 / .2), 0 1px 1px rgb(0 0 0 / .2), 0 2px 3px -1px rgb(0 0 0 / .25)")
        prop("--shadow-2", "0 0 0 1px rgb(0 0 0 / .22), 0 2px 4px rgb(0 0 0 / .18), 0 8px 16px -4px rgb(0 0 0 / .3), 0 16px 32px -8px rgb(0 0 0 / .32)")
        prop("--shadow-3", "0 0 0 1px rgb(0 0 0 / .25), 0 4px 8px rgb(0 0 0 / .2), 0 16px 32px -8px rgb(0 0 0 / .38), 0 40px 80px -16px rgb(0 0 0 / .55)")
        prop("--glow", "0 0 0 1px var(--accent-line), 0 18px 48px -14px var(--accent-glow)")
        prop("--focus", "0 0 0 3px var(--bg), 0 0 0 5px var(--accent)")
        prop("--font-sans", "\"Inter Variable\", \"Inter\", ui-sans-serif, system-ui, -apple-system, \"Segoe UI\", Roboto, sans-serif")
        prop("--font-mono", "ui-monospace, \"SF Mono\", \"JetBrains Mono\", Menlo, Consolas, monospace")
        prop("--fs-xs", ".75rem")
        prop("--fs-sm", ".875rem")
        prop("--fs-md", "1rem")
        prop("--fs-lg", "1.125rem")
        prop("--fs-xl", "1.3rem")
        prop("--fs-h3", "1.125rem")
        prop("--fs-h2", "clamp(2rem, 1.3rem + 2.8vw, 3.25rem)")
        prop("--fs-display", "clamp(2.6rem, 1.5rem + 3.6vw, 4.1rem)")
        prop("--s-1", "4px")
        prop("--s-2", "8px")
        prop("--s-3", "12px")
        prop("--s-4", "16px")
        prop("--s-5", "24px")
        prop("--s-6", "32px")
        prop("--s-7", "48px")
        prop("--s-8", "64px")
        prop("--s-9", "96px")
        prop("--s-10", "128px")
        prop("--r-xs", "6px")
        prop("--r-sm", "10px")
        prop("--r-md", "14px")
        prop("--r-lg", "20px")
        prop("--r-xl", "28px")
        prop("--r-full", "999px")
        prop("--container", "1200px")
        prop("--ease", "cubic-bezier(.22, 1, .36, 1)")
    }
}
