// Terminal demo controller. The markup is server-rendered in its final state; this types it out
// line by line with Pause / Replay.
package site

import kotlinx.browser.window
import org.w3c.dom.HTMLElement

class Terminal(term: HTMLElement) {
    private val lines = qa(".term__line", term)
    private val ctl = q("[data-term-ctl]", term)
    private val typed: Array<HTMLElement?> = Array(lines.size) { q(".term__typed", lines[it]) }
    private val full: Array<String> = Array(lines.size) { typed[it]?.textContent ?: "" }
    private var timer = 0
    private var i = 0
    private var c = 0
    private var playing = false
    private var done = false

    init {
        ctl?.let { it.hidden = false; setCtl(); it.on("click") { onCtl() } }
    }

    private fun showAll() {
        for (k in lines.indices) { lines[k].removeAttribute("data-hide"); typed[k]?.textContent = full[k] }
    }

    private fun setCtl() { ctl?.textContent = if (playing) "Pause" else if (done) "Replay" else "Play" }

    private fun step() {
        if (!playing) return
        if (i >= lines.size) { playing = false; done = true; setCtl(); return }
        val t = typed[i]
        lines[i].removeAttribute("data-hide")
        if (t != null && c <= full[i].length) {
            t.textContent = full[i].jsSlice(0, c++)
            timer = window.setTimeout({ step() }, if (c == 1) 380 else (24 + rand() * 38).trunc())
            return
        }
        i++; c = 0
        timer = window.setTimeout({ step() }, if (t != null) 260 else 110)
    }

    private fun start() {
        window.clearTimeout(timer); i = 0; c = 0; done = false; playing = true
        for (k in lines.indices) { lines[k].setAttribute("data-hide", ""); typed[k]?.textContent = "" }
        setCtl(); step()
    }

    private fun onCtl() {
        when {
            playing -> { playing = false; window.clearTimeout(timer); setCtl() }
            done || i == 0 -> start()
            else -> { playing = true; setCtl(); step() }
        }
    }

    /** Start, or show the final state under reduced motion. */
    fun play() { if (reducedMotion()) { showAll(); done = true; setCtl() } else if (!playing) start() }

    /** If typing, jump to the end. */
    fun stop() { if (playing) settle() }

    /** Always jump to the end. */
    fun settle() { playing = false; window.clearTimeout(timer); showAll(); done = true; setCtl() }
}

/** Terminals type out once when scrolled into view (cox scenes drive their own). */
fun initTerminals() = idle {
    for (el in qa("[data-term][data-term-auto]")) {
        val t = Terminal(el)
        val m = mq(REDUCE_MOTION)
        if (m.matches == true) { t.play(); continue }
        onVisible(el, 0.35) { t.play() }
        m.addEventListener("change", { if (m.matches == true) t.settle() })
    }
}
