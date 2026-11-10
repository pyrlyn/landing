// Docs pages: the sticky nav strip (edge fades, sliding underline, section menu) and the on-page TOC.
package site

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLDetailsElement
import org.w3c.dom.HTMLElement

/** Docs nav: edge fades while the strip overflows, the current tab scrolled into view, and an underline that
 * follows hover/focus and returns to the current page. Section menus close like the header menu. */
fun initDocsNav(nav: HTMLElement) {
    val scroller = q("[data-dnav-scroller]", nav) ?: return
    val indicator = qq(".dnav__indicator", scroller)
    val tabs = qa(".dnav__tab", scroller)
    val current = tabs.firstOrNull { it.getAttribute("aria-current") == "page" }
    fun fades() {
        val maxL = scroller.scrollWidth - scroller.clientWidth
        scroller.toggleAttr("data-fade-l", scroller.scrollLeft > 4)
        scroller.toggleAttr("data-fade-r", scroller.scrollLeft < maxL - 4)
    }
    fun moveTo(el: HTMLElement?) {
        if (el == null) { indicator.setStyle("--w", "0"); return }
        val pad = 12
        val left = el.getBoundingClientRect().left - scroller.getBoundingClientRect().left + scroller.scrollLeft
        indicator.setStyle("--x", "${left + pad}px")
        indicator.setStyle("--w", "${mMax((el.offsetWidth - pad * 2).toDouble(), 0.0)}")
    }
    if (current != null) {
        val left = current.getBoundingClientRect().left - scroller.getBoundingClientRect().left
        scroller.scrollLeft = mMax(0.0, left - (scroller.clientWidth - current.offsetWidth) / 2.0)
    }
    moveTo(current)
    nav.classList.add("is-ready")
    fades()
    for (t in tabs) { t.on("pointerenter") { moveTo(t) }; t.on("focus") { moveTo(t) } }
    scroller.on("scroll", passive) { fades() }
    onWin("resize", passive) { fades(); moveTo(current) }
    scroller.on("pointerleave") { moveTo(current) }
    scroller.on("focusout") { e -> if (!scroller.contains(e.related)) moveTo(current) }
    document.asDynamic().fonts?.ready?.then({ moveTo(current); fades() }, { })

    val menus = qa("details[data-menu]", nav)
    onDoc("click") { e -> for (m in menus) { val d = m.unsafeCast<HTMLDetailsElement>(); if (d.open && !d.contains(e.el)) d.open = false } }
    onDoc("keydown") { e ->
        if (e.key == "Escape") for (m in menus) { val d = m.unsafeCast<HTMLDetailsElement>(); if (d.open) { d.open = false; q("summary", d)?.focus() } }
    }
    for (m in menus) m.on("focusout") { e -> val d = m.unsafeCast<HTMLDetailsElement>(); val to = e.related; if (d.open && to != null && !d.contains(to)) d.open = false }
}

/** Docs TOC: aria-current on the link of the section being read (the last heading above 200 px). */
fun initToc(root: HTMLElement) {
    val links = qa("[data-toc-link]", root)
    if (links.isEmpty()) return
    val ids = Array(links.size) { js("decodeURIComponent")(links[it].unsafeCast<HTMLAnchorElement>().hash.jsSlice(1)).unsafeCast<String>() }
    val onScroll = {
        var active: String? = null
        for (id in ids) {
            val h = document.getElementById(id) ?: continue
            if (active == null || h.getBoundingClientRect().top < 200) active = id
        }
        if (active != null) {
            var marked = false
            for (k in links.indices) {
                // Like the Map in the original, only the last link for an id is marked.
                val isIt = ids[k] == active && ids.jsLastIndexOf(active) == k
                if (isIt && !marked) { links[k].setAttribute("aria-current", "true"); marked = true } else links[k].removeAttribute("aria-current")
            }
        }
    }
    window.addEventListener("scroll", { onScroll() }, passive)
    onScroll()
}

private fun Array<String>.jsLastIndexOf(x: String): Int = asDynamic().lastIndexOf(x).unsafeCast<Int>()
