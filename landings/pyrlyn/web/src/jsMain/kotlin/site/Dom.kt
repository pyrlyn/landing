// Small DOM layer for the site bundle. Kotlin/JS externals only: no collections, regex or reflection,
// so production DCE keeps the bundle close to hand-written JS.
package site

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.ParentNode
import org.w3c.dom.events.Event

external class IntersectionObserver(callback: (entries: Array<dynamic>, observer: dynamic) -> Unit, options: dynamic = definedExternally) {
    fun observe(target: Element)
    fun unobserve(target: Element)
    fun disconnect()
}

external class ResizeObserver(callback: (entries: dynamic, observer: dynamic) -> Unit) {
    fun observe(target: Element)
}

external class AbortController {
    val signal: dynamic
    fun abort()
}

const val REDUCE_MOTION = "(prefers-reduced-motion: reduce)"
const val FINE_POINTER = "(hover: hover) and (pointer: fine)"

/** First match, untyped-checked (no runtime cast code). */
fun q(sel: String, root: ParentNode = document): HTMLElement? = root.querySelector(sel).unsafeCast<HTMLElement?>()

/** All matches as a JS array. */
fun qa(sel: String, root: ParentNode = document): Array<HTMLElement> = js("Array.from")(root.querySelectorAll(sel)).unsafeCast<Array<HTMLElement>>()

fun mq(query: String): dynamic = window.matchMedia(query)
fun reducedMotion(): Boolean = window.matchMedia(REDUCE_MOTION).matches

fun Element.on(type: String, options: dynamic = undefined, fn: (Event) -> Unit) = addEventListener(type, fn, options)
fun onDoc(type: String, fn: (Event) -> Unit) = document.addEventListener(type, fn)
fun onWin(type: String, options: dynamic = undefined, fn: (Event) -> Unit) = window.addEventListener(type, fn, options)

val passive: dynamic get() = js("({passive:true})")

/** Run when the browser is idle (or soon). */
fun idle(fn: () -> Unit) {
    val w = window.asDynamic()
    if (w.requestIdleCallback) w.requestIdleCallback(fn, js("({timeout:2000})")) else window.setTimeout(fn, 1)
}

/** Run `fn` once the element is in view. */
fun onVisible(el: Element, threshold: Double = 0.0, fn: () -> Unit) {
    var io: IntersectionObserver? = null
    val opts = obj(); opts.threshold = threshold
    io = IntersectionObserver({ es, _ ->
        if (es.any { e -> e.isIntersecting == true }) { io?.disconnect(); fn() }
    }, opts)
    io?.observe(el)
}

val Event.el: Element? get() = target.unsafeCast<Element?>()
fun Event.closest(sel: String): Element? = el?.asDynamic()?.closest?.call(el, sel).unsafeCast<Element?>()
val Event.key: String get() = asDynamic().key.unsafeCast<String>()
val Event.related: Element? get() = asDynamic().relatedTarget.unsafeCast<Element?>()

fun Element.setStyle(name: String, value: String) = asDynamic().style.setProperty(name, value)
fun HTMLElement.data(name: String): String? = asDynamic().dataset[name].unsafeCast<String?>()

fun cssVar(name: String): String = window.getComputedStyle(html).getPropertyValue(name).jsTrim()

/** A fresh plain JS object. */
fun obj(): dynamic = js("({})")

fun Element.toggleAttr(name: String, on: Boolean) { asDynamic().toggleAttribute(name, on) }

fun jsFloat(s: String?): Double = js("parseFloat")(s).unsafeCast<Double>()
fun rand(): Double = js("Math.random()").unsafeCast<Double>()

/** JS Array.prototype.filter (stays a JS array; Kotlin's filter would pull in collections). */
fun <T> Array<T>.jsFilter(f: (T) -> Boolean): Array<T> = asDynamic().filter(f).unsafeCast<Array<T>>()
fun <T> Array<T>.jsIndexOf(x: T): Int = asDynamic().indexOf(x).unsafeCast<Int>()

// Native string/number helpers: Kotlin's own versions pull Unicode tables, Regex or exceptions into the bundle.
fun String.jsTrim(): String = asDynamic().trim().unsafeCast<String>()
fun String.jsLower(): String = asDynamic().toLowerCase().unsafeCast<String>()
fun String.jsIndex(s: String, from: Int = 0): Int = asDynamic().indexOf(s, from).unsafeCast<Int>()
fun String.jsSlice(a: Int, b: Int = length): String = asDynamic().slice(a, b).unsafeCast<String>()
fun String.jsReplace(a: String, b: String): String = asDynamic().split(a).join(b).unsafeCast<String>()
fun jsRound(x: Double): Int = js("Math.round")(x).unsafeCast<Int>()
/** Array element or null when out of range (no bounds-check code). */
fun <T> Array<T>.at(i: Int): T? = asDynamic()[i].unsafeCast<T?>()
/** Non-null first match, for elements the markup always has. */
fun qq(sel: String, root: ParentNode = document): HTMLElement = root.querySelector(sel).unsafeCast<HTMLElement>()
val html: HTMLElement get() = document.documentElement.unsafeCast<HTMLElement>()
private val M: dynamic get() = js("Math")
fun mMin(a: Double, b: Double): Double = M.min(a, b).unsafeCast<Double>()
fun mMax(a: Double, b: Double): Double = M.max(a, b).unsafeCast<Double>()
fun mAbs(a: Double): Double = M.abs(a).unsafeCast<Double>()
fun mFloor(a: Double): Double = M.floor(a).unsafeCast<Double>()
/** Number → JS int (Math.trunc, no Long-based range checks). */
fun Double.trunc(): Int = M.trunc(this).unsafeCast<Int>()
