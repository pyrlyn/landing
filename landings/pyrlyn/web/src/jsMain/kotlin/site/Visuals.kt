// rtok / ketch product visuals. Illustrative only. Reduced motion: final states, no timers;
// the scroll-scrubbed ones render at p = 1.
package site

import kotlinx.browser.window
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.Element
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLElement

/** One rAF-throttled scroll/resize loop shared by the scroll-scrubbed visuals (no scroll-jacking). */
private val scrubSubs = js("[]").unsafeCast<Array<() -> Unit>>()
private var scrubFrame = 0

private fun onScrub(fn: () -> Unit) {
    if (scrubSubs.isEmpty()) {
        val queue: (dynamic) -> Unit = { if (scrubFrame == 0) scrubFrame = window.requestAnimationFrame { scrubFrame = 0; for (f in scrubSubs) f() } }
        window.addEventListener("scroll", queue, passive); window.addEventListener("resize", queue, passive)
    }
    scrubSubs.asDynamic().push(fn)
}

/** 0 → 1 as the element travels from `start`·vh (entering low) to `end`·vh − height. */
private fun progress(el: Element, start: Double, end: Double): Double {
    val r = el.getBoundingClientRect()
    val vh = window.innerHeight.toDouble()
    val a = vh * start; val b = vh * end - r.height
    return mMin(1.0, mMax(0.0, (a - r.top) / (a - b)))
}

/** rtok: MCP code graph draws in once visible. */
fun initGraph(g: HTMLElement) {
    if (reducedMotion()) return
    g.classList.add("is-armed")
    var io: IntersectionObserver? = null
    io = IntersectionObserver({ es, _ -> if (es[0].isIntersecting == true) { io?.disconnect(); window.requestAnimationFrame { g.classList.add("is-drawn") } } }, js("({threshold:0.4})"))
    io?.observe(g)
}

private const val K = 0
private const val T = 1
private const val C = 2

private class Particle(val kind: Int, val sx: Double, val sy: Double, val w: Double) {
    var bx = 0.0
    var by = 0.0
}

/** rtok: token particles scroll-scrubbed into a narrow context band, reversible. */
fun initParticles(root: HTMLElement) {
    val reduce = mq(REDUCE_MOTION)
    val canvas = q("[data-p-canvas]", root)?.unsafeCast<HTMLCanvasElement>() ?: return
    val ctx = canvas.getContext("2d")?.unsafeCast<CanvasRenderingContext2D>() ?: return
    val out = q("[data-p-count]", root)
    val cyan = cssVar("--accent").ifEmpty { "#5CE1FF" }
    val coral = cssVar("--accent-2").ifEmpty { "#FF6B4A" }
    var seed = 7.0
    fun rnd(): Double { seed = (seed * 16807) % 2147483647; return seed / 2147483647 }
    val ps = Array(240) {
        val r = rnd()
        Particle(if (r < 0.55) K else if (r < 0.72) T else C, rnd(), rnd(), 6 + rnd() * 12).also { rnd() }
    }
    var cut = 0
    for (p in ps) if (p.kind == C) cut++
    var w = 0.0; var h = 0.0; var last = -1.0; var rows = 1
    fun layout() {
        val dpr = mMin(2.0, window.devicePixelRatio.let { if (it > 0) it else 1.0 })
        w = canvas.clientWidth.toDouble(); h = canvas.clientHeight.toDouble()
        canvas.width = (w * dpr).trunc(); canvas.height = (h * dpr).trunc(); ctx.setTransform(dpr, 0.0, 0.0, dpr, 0.0, 0.0)
        // Kept + thinned tokens pack into rows inside a narrow band; cut tokens go to the archive corner.
        val bandW = w * 0.62; val x0 = w * 0.08; val rowH = 9
        var x = x0; var row = 0; var ai = 0
        for (p in ps) {
            if (p.kind == C) { p.bx = w * 0.8 + (ai % 12) * 9; p.by = h * 0.72 + mFloor(ai / 12.0) * 8 - 20; ai++; continue }
            val pw = if (p.kind == T) p.w * 0.35 else p.w * 0.8
            if (x + pw > x0 + bandW) { x = x0; row++ }
            p.bx = x; p.by = h / 2 - 18 + row * rowH; x += pw + 3
        }
        rows = row + 1
        last = -1.0
    }
    fun ease(t: Double) = if (t < 0.5) 4 * t * t * t else (-2 * t + 2).let { u -> 1 - u * u * u / 2 }
    fun draw(p0: Double) {
        val t = ease(p0)
        ctx.clearRect(0.0, 0.0, w, h)
        ctx.fillStyle = "rgba(92,225,255,${0.04 + t * 0.06})"
        val bh = rows * 9 + 12.0
        ctx.fillRect(w * 0.06, h / 2 - 24, w * 0.66, bh + (h * 0.5 - bh) * (1 - t))
        for (p in ps) {
            val x = p.sx * (w - 20) + 10; val y = p.sy * (h - 20) + 10
            val px = x + (p.bx - x) * t; val py = y + (p.by - y) * t
            val pw = when (p.kind) { T -> p.w * (1 - 0.65 * t); K -> p.w * (1 - 0.2 * t); else -> p.w * (1 - 0.5 * t) }
            ctx.globalAlpha = when (p.kind) { T -> 0.75 - 0.25 * t; C -> 0.9 - 0.35 * t; else -> 0.95 }
            if (p.kind == C) { ctx.strokeStyle = coral; ctx.lineWidth = 1.2; ctx.strokeRect(px, py, pw, 5.0) }
            else { ctx.fillStyle = if (p.kind == K) cyan else "#7aa3b8"; ctx.fillRect(px, py, pw, if (p.kind == K) 5.0 else 4.0) }
        }
        ctx.globalAlpha = 1.0
        out?.textContent = "−${jsRound(cut * t)}"
    }
    val tick = {
        val p = if (reduce.matches == true) 1.0 else progress(root, 0.95, 0.35)
        if (mAbs(p - last) >= 0.002) { last = p; draw(p) }
    }
    ResizeObserver { _, _ -> layout(); tick() }.observe(canvas)
    onScrub(tick)
    reduce.addEventListener("change", { last = -1.0; tick() })
}

/** ketch: package chip travels the route on offset-path, stroke draws with scroll; rollback runs back. */
fun initRoute(route: HTMLElement) {
    val reduce = mq(REDUCE_MOTION)
    val fit = qq("[data-route-fit]", route)
    val box = qq("[data-route-box]", route)
    val path = route.querySelector("[data-route-path]").asDynamic()
    val tips = qa("[data-route-stop]", route)
    for (k in tips.indices) tips[k].setStyle("--k", k.toString())
    // Fraction of the route at which each stop is reached (nearest sample on the path).
    val len = path.getTotalLength().unsafeCast<Double>()
    val stopsAt = DoubleArray(tips.size) { k ->
        val x = jsFloat(tips[k].style.left); val y = jsFloat(tips[k].style.top)
        var best = 0.0; var bd = 1e300
        for (i in 0..400) {
            val pt = path.getPointAtLength(i / 400.0 * len)
            val dx = pt.x.unsafeCast<Double>() - x; val dy = pt.y.unsafeCast<Double>() - y
            val d = dx * dx + dy * dy
            if (d < bd - 1) { bd = d; best = i / 400.0 }
        }
        best
    }
    // The install stop is revisited at the very end (the rollback leg returns there).
    val backStart = stopsAt[3]
    val scale = { route.setStyle("--s", mMin(1.0, fit.clientWidth / 1000.0).toString()) }
    ResizeObserver { _, _ -> scale() }.observe(fit); scale()
    val tick = {
        val p = if (reduce.matches == true) 1.0 else progress(route, 0.85, 0.2)
        box.setStyle("--dist", "${p * 100}%")
        route.setStyle("--dash", (1 - p).toString())
        path.style.strokeDashoffset = (1 - p).toString()
        route.classList.toggle("is-back", p > backStart + 0.02 && p < 1.01)
        for (k in tips.indices) tips[k].classList.toggle("is-on", p + 0.005 >= stopsAt[k])
    }
    onScrub(tick); tick()
    reduce.addEventListener("change", tick)
}

fun initVisuals() = idle {
    for (g in qa("[data-graph]")) initGraph(g)
    for (p in qa("[data-particles]")) initParticles(p)
    for (r in qa("[data-route]")) initRoute(r)
}
