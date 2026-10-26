// Cox scenes (tabs + perspective panels) and the home ring carousel with hotspots.
package site

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLElement

private fun wrap(k: Int, n: Int) = ((k % n) + n) % n

/** Cox scenes: tabs + perspective panels; each panel types its own terminal. */
fun initScenes() {
    for (r in qa("[data-scenes]")) {
        val tabs = qa("[role=tab]", r)
        val panels = qa("[data-scene]", r)
        val terms: Array<Terminal?> = Array(panels.size) { q("[data-term]", panels[it])?.let(::Terminal) }
        var active = 0; var prev = 0; var seen = false
        fun render() {
            q(".scenes__stage", r)?.setStyle("--active", active.toString())
            for (i in tabs.indices) { val on = i == active; tabs[i].setAttribute("aria-selected", on.toString()); tabs[i].tabIndex = if (on) 0 else -1 }
            for (i in panels.indices) {
                val p = panels[i]; val on = i == active
                p.classList.toggle("is-active", on); p.toggleAttr("inert", !on); p.setStyle("--off", "${mAbs((i - active).toDouble())}")
            }
            if (seen) { if (prev != active) terms[prev]?.stop(); terms[active]?.play() }
            prev = active
        }
        fun go(k: Int, focus: Boolean = false) {
            active = wrap(k, if (panels.isEmpty()) 1 else panels.size); render()
            if (focus) tabs.at(active)?.focus()
        }
        r.classList.add("is-ready")
        for (i in tabs.indices) tabs[i].on("click") { go(i) }
        for (i in panels.indices) panels[i].on("click") { go(i) }
        r.on("keydown") { e ->
            val a = document.activeElement
            val onTab = a?.getAttribute("role") == "tab" && r.contains(a)
            if (e.key == "ArrowRight" || e.key == "ArrowLeft") { e.preventDefault(); go(active + if (e.key == "ArrowRight") 1 else -1, onTab) }
            if ((e.key == "Home" || e.key == "End") && e.el?.getAttribute("role") == "tab") { e.preventDefault(); go(if (e.key == "Home") 0 else panels.size - 1, true) }
        }
        onVisible(r, 0.35) { seen = true; render() }
        render()
    }
}

/** Home ring carousel + hotspots. */
fun initRing() {
    for (root in qa("[data-ring]")) {
        val autoMs = 4200
        val stage = q("[data-ring-stage]", root)
        val track = q("[data-ring-track]", root)
        val items = qa("[data-ring-item]", root)
        val status = q("[data-ring-status]", root)
        val controls = q("[data-ring-controls]", root)
        val titles = (root.data("titles") ?: "").asDynamic().split("\u001f").unsafeCast<Array<String>>()
        val n = items.size
        val step = 360.0 / n
        var index = 0; var rot = 0.0; var touched = false; var timer = 0
        fun render() {
            track?.setStyle("--rot", "${rot}deg")
            for (k in items.indices) items[k].classList.toggle("is-active", k == index)
            if (status != null) {
                status.textContent = "${titles.at(index) ?: ""} · ${index + 1} / ${titles.size}"
                status.setAttribute("aria-live", if (touched) "polite" else "off")
            }
        }
        /** Turn to slide `k` (any integer); `rot` accumulates so the ring never spins the long way. */
        fun go(k: Int) {
            val to = wrap(k, n)
            var delta = wrap(to - index, n)
            if (delta > n / 2.0) delta -= n
            rot += delta * step; index = to; render()
        }
        fun takeOver() { touched = true; window.clearInterval(timer) }
        root.classList.add("is-ready")
        controls?.hidden = false
        q("[data-ring-prev]", root)?.on("click") { takeOver(); go(index - 1) }
        q("[data-ring-next]", root)?.on("click") { takeOver(); go(index + 1) }
        root.on("keydown") { e ->
            if (e.closest(".hotspot") != null || (e.key != "ArrowRight" && e.key != "ArrowLeft")) return@on
            e.preventDefault(); takeOver()
            go(index + if (e.key == "ArrowRight") 1 else -1)
            items.at(index)?.let { q(".ring__card", it) }?.asDynamic()?.focus(js("({preventScroll:true})"))
        }
        for (k in items.indices) items[k].on("focusin") { takeOver(); if (k != index) go(k) }
        var startX = 0.0; var startRot = 0.0; var dragRot = 0.0; var dragging = false; var moved = false
        stage?.on("pointerdown") { e ->
            if (mq("(max-width: 767px)").matches == true || e.closest(".hotspot") != null) return@on
            dragging = true; moved = false; startX = e.asDynamic().clientX.unsafeCast<Double>(); startRot = rot; dragRot = rot
        }
        onWin("pointermove") { e ->
            if (!dragging) return@onWin
            val dx = e.asDynamic().clientX.unsafeCast<Double>() - startX
            if (!moved && mAbs(dx) > 6) { moved = true; takeOver(); stage?.classList?.add("is-dragging") }
            if (moved) { dragRot = startRot - dx * 0.35; track?.setStyle("--rot", "${dragRot}deg") }
        }
        val end: (dynamic) -> Unit = {
            if (dragging) {
                dragging = false; stage?.classList?.remove("is-dragging")
                if (moved) {
                    // Snap the free drag angle to the nearest slide.
                    val k = jsRound(dragRot / step)
                    index = wrap(k, n); rot = k * step; render()
                    window.setTimeout({ moved = false }, 0)
                }
            }
        }
        window.addEventListener("pointerup", end); window.addEventListener("pointercancel", end)
        stage?.on("click", true) { e -> if (moved) { e.preventDefault(); e.stopPropagation(); moved = false } }
        val m = mq(REDUCE_MOTION)
        val start = {
            window.clearInterval(timer)
            if (!touched && m.matches != true) timer = window.setInterval({
                if (!(document.asDynamic().hidden == true || root.matches(":hover") || root.contains(document.activeElement) ||
                        q(".hotspot__dot[aria-expanded=\"true\"]", root) != null)) go(index + 1)
            }, autoMs)
        }
        m.addEventListener("change", start)
        start(); render()

        // Hotspots: one open at a time; Esc closes and returns focus; outside click closes.
        val dots = qa(".hotspot__dot", root)
        var open: String? = null
        fun sync() {
            for (b in dots) {
                val id = b.getAttribute("aria-controls"); val on = id == open
                b.setAttribute("aria-expanded", on.toString())
                id?.let { document.getElementById(it) }?.unsafeCast<HTMLElement>()?.hidden = !on
            }
        }
        for (b in dots) b.on("click") { val id = b.getAttribute("aria-controls"); open = if (open == id) null else id; sync() }
        onDoc("keydown") { e ->
            if (e.key != "Escape" || open == null) return@onDoc
            val dot = dots.firstOrNull { it.getAttribute("aria-controls") == open }
            open = null; sync(); dot?.focus()
        }
        onDoc("click") { e -> if (open != null && e.closest(".hotspot") == null) { open = null; sync() } }
    }
}
