package dev.pyrlyn.landing.styles

import kotlinx.css.CssBuilder

/*
 * Tiny layer over kotlin-css used by the sheets. Plain `rule()` merges blocks that share a selector,
 * which would move later declarations ahead of the rules in between and change the cascade, so every
 * block here is repeatable and the stylesheet keeps its exact source order.
 */

/** A style rule (also used for nested rules such as `&:hover`). */
internal inline fun CssBuilder.sel(selector: String, crossinline block: CssBuilder.() -> Unit) {
    rule(selector, passStaticClassesToParent = false, repeatable = true) { block() }
}

/** `@media <query> { … }`. */
internal inline fun CssBuilder.atMedia(query: String, crossinline block: CssBuilder.() -> Unit) = sel("@media $query", block)

/** `@supports <condition> { … }`. */
internal inline fun CssBuilder.atSupports(condition: String, crossinline block: CssBuilder.() -> Unit) = sel("@supports $condition", block)

/** Any other block at-rule: `@keyframes`, `@property`, `@layer`, `@font-face`. */
internal inline fun CssBuilder.at(prelude: String, crossinline block: CssBuilder.() -> Unit) = sel("@$prelude", block)

/** One declaration. */
internal fun CssBuilder.prop(name: String, value: String) = put(name, value)
