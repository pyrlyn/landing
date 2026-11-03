// Team group photo motion: one-time entrance (fade, scale .96 -> 1, blur out) and a scroll parallax of the
// photo layer inside its clipped frame. CSS view timeline when supported, else rAF only while on screen.
// Reduced motion: no entrance, no parallax (static photo, final state).
package site

import kotlinx.browser.window

fun initTeamPhoto() {
    val root = q("[data-team-photo]") ?: return
    val frame = q("[data-team-frame]", root) ?: return
    val layer = q("[data-team-layer]", root) ?: return
    val ds = root.asDynamic().dataset
    if (reducedMotion()) { ds.state = "in"; return }
    ds.state = "armed"
    var reveal: IntersectionObserver? = null
    reveal = IntersectionObserver({ es, _ ->
        if (es.any { it.isIntersecting == true }) { window.requestAnimationFrame { ds.state = "in" }; reveal?.disconnect() }
    }, js("({threshold:0.15})"))
    reveal.observe(root)
    if (js("typeof CSS!=='undefined'&&CSS.supports('animation-timeline: view()')") == true) { ds.parallax = "css"; return }
    var visible = false; var raf = 0
    val tick = {
        if (raf == 0) raf = window.requestAnimationFrame {
            raf = 0
            val r = frame.getBoundingClientRect(); val h = window.innerHeight.toDouble()
            val p = (r.top + r.height / 2 - h / 2) / (h / 2 + r.height / 2)
            layer.asDynamic().style.transform = "translate3d(0, ${-p * 0.12 * r.height}px, 0)"
        }
    }
    IntersectionObserver({ es, _ ->
        visible = es[0].isIntersecting == true
        layer.asDynamic().style.willChange = if (visible) "transform" else ""
        if (visible) tick()
    }).observe(frame)
    onWin("scroll", passive) { if (visible) tick() }
}
