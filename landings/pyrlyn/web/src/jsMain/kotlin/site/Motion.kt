// Page motion: hero stage tilt, card shine, still pages, scroll and step reveals, the lazy WebGL intro.
package site

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLScriptElement

/** Hero stage: tilt + parallax layers ([data-depth]), lerped, rAF only while settling. Fine pointer + motion only. */
fun initStage(stage: HTMLElement) {
    val reduce = mq(REDUCE_MOTION)
    val fine = mq(FINE_POINTER)
    val tilt = q(".stage__tilt", stage)
    val layers = qa("[data-depth]", stage)
    var tx = 0.0; var ty = 0.0; var cx = 0.0; var cy = 0.0; var frame = 0; var visible = true
    var tick: (Double) -> Unit = {}
    tick = {
        cx += (tx - cx) * 0.08; cy += (ty - cy) * 0.08
        val settling = mAbs(tx - cx) + mAbs(ty - cy) > 0.0005
        tilt?.setStyle("--ry", "${cx * 8}deg")
        tilt?.setStyle("--rx", "${-cy * 6}deg")
        for (l in layers) {
            val d = jsFloat(l.data("depth")).let { if (it != it) 0.0 else it }
            l.asDynamic().style.translate = "${-cx * d}px ${-cy * d}px"
        }
        frame = if (settling) window.requestAnimationFrame(tick) else 0
    }
    fun kick() { if (frame == 0) frame = window.requestAnimationFrame(tick) }
    val reset = { tx = 0.0; ty = 0.0; kick() }
    IntersectionObserver({ es, _ -> visible = es[0].isIntersecting == true; if (!visible) reset() }).observe(stage)
    onWin("pointermove", passive) { e ->
        if (reduce.matches == true || fine.matches != true || !visible) return@onWin
        val p = e.asDynamic()
        tx = p.clientX.unsafeCast<Double>() / window.innerWidth - 0.5
        ty = p.clientY.unsafeCast<Double>() / window.innerHeight - 0.5
        kick()
    }
    onDoc("pointerleave") { reset() }
    reduce.addEventListener("change", {
        if (reduce.matches == true) {
            window.cancelAnimationFrame(frame); frame = 0; tx = 0.0; ty = 0.0; cx = 0.0; cy = 0.0
            tilt?.asDynamic()?.style?.removeProperty("--rx"); tilt?.asDynamic()?.style?.removeProperty("--ry")
            for (l in layers) l.asDynamic().style.translate = ""
        }
    })
}

/** Cursor-follow shine on `.shine` cards (fine pointers only): sets --mx/--my, toggles html.has-shine. */
fun initShine() {
    val fine = mq(FINE_POINTER)
    val sync = { html.classList.toggle("has-shine", fine.matches == true); Unit }
    sync()
    fine.addEventListener("change", sync)
    document.addEventListener("pointermove", { e ->
        if (fine.matches != true) return@addEventListener
        val el = e.closest(".shine") ?: return@addEventListener
        val r = el.getBoundingClientRect()
        val p = e.asDynamic()
        el.setStyle("--mx", "${p.clientX - r.left}px")
        el.setStyle("--my", "${p.clientY - r.top}px")
    }, passive)
}

/** Decorative loops pause (html.is-still) while the hero stage is off screen. */
fun initStill() {
    val root = html
    val hero = q("[data-stage]") ?: return
    if (root.classList.contains("is-still")) return
    IntersectionObserver({ es, _ -> root.classList.toggle("is-still", es[0].isIntersecting != true) }).observe(hero)
}

private const val REVEAL_SELECTOR = "main .section__head, main .grid > li, main .grid > article, main .featured, main .showcase, main .prose, main .install-card, main .usage > .card, main .links > li, main .license, main .faq__item, main .steps > li, main .cta"

/** Scroll reveals: blocks below the fold rise in once; nothing on screen at load is hidden. Not on docs pages. */
fun initScrollReveal() {
    val reduce = mq(REDUCE_MOTION)
    if (reduce.matches == true || window.asDynamic().IntersectionObserver == undefined || q(".docs__main") != null) return
    val vh = window.innerHeight
    val els = qa(REVEAL_SELECTOR).jsFilter { it.closest(".hero") == null && it.getBoundingClientRect().top > vh * 0.92 }
    if (els.isEmpty()) return
    var io: IntersectionObserver? = null
    io = IntersectionObserver({ es, _ ->
        for (e in es) if (e.isIntersecting == true) {
            val el = e.target.unsafeCast<HTMLElement>()
            el.classList.add("is-in"); io?.unobserve(el)
            // Hand the element back to its own hover transforms once it has landed.
            window.setTimeout({ el.classList.remove("rv", "is-in"); el.asDynamic().style.removeProperty("--rv-i") }, 1100)
        }
    }, js("({rootMargin:'0px 0px -8% 0px'})"))
    for (el in els) {
        val sib = el.parentElement?.let { p -> js("Array.prototype.indexOf").call(p.children, el).unsafeCast<Int>() } ?: 0
        el.setStyle("--rv-i", "${mMin(sib.toDouble(), 5.0)}")
        el.classList.add("rv")
        io?.observe(el)
    }
    reduce.addEventListener("change", { if (reduce.matches == true) for (el in els) el.classList.add("is-in") })
}

/** Step rails (.gs__step, .rail__step): reveal once in view, only when motion is allowed. */
fun initStepReveal() {
    val reduce = mq(REDUCE_MOTION)
    val steps = qa(".gs__step, .rail__step")
    if (steps.isEmpty() || reduce.matches == true || window.asDynamic().IntersectionObserver == undefined) return
    html.classList.add("js-reveal")
    var io: IntersectionObserver? = null
    io = IntersectionObserver({ es, _ ->
        for (e in es) if (e.isIntersecting == true) { e.target.classList.add("is-in"); io?.unobserve(e.target) }
    }, js("({rootMargin:'0px 0px -10% 0px'})"))
    for (s in steps) io?.observe(s)
    reduce.addEventListener("change", { if (reduce.matches == true) for (s in steps) s.classList.add("is-in") })
}

private fun isHex(s: String) = js("/^#[0-9a-f]{6}$/i").test(s).unsafeCast<Boolean>()

/** Hero stage tilt and the WebGL intro sweep (pages with a hero, not docs). The intro script loads only here. */
fun initHero(me: HTMLScriptElement?) {
    if (js("/\\/docs(\\/|$)/").test(window.location.pathname).unsafeCast<Boolean>()) return
    q("[data-stage]")?.let { initStage(it) }
    val root = html
    val hero = q(".hero")
    if (hero != null && !reducedMotion() && root.classList.contains("intro") && hero.getBoundingClientRect().bottom >= 0) {
        window.setTimeout({ root.classList.remove("intro") }, 2200)
        val src = me?.data("intro")
        if (isHex(cssVar("--accent")) && isHex(cssVar("--accent-2")) && src != null) {
            window.requestAnimationFrame {
                val s = document.createElement("script").unsafeCast<HTMLScriptElement>()
                s.src = src; s.async = true
                document.head.unsafeCast<HTMLElement>().appendChild(s)
            }
        }
    } else root.classList.remove("intro")
}
