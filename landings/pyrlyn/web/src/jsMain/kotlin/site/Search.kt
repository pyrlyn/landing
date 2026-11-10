// Header search: Algolia over REST with the build's search-only key; without keys it shows
// "not available yet" and makes no requests.
package site

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLInputElement
import kotlin.js.Promise

private class Hit(val title: String, val section: String, val url: String, val accent: String?)

private fun esc(s: String): String = s.jsReplace("&", "&amp;").jsReplace("<", "&lt;").jsReplace(">", "&gt;").jsReplace("\"", "&quot;")

/** Title with every case-insensitive occurrence of `q` wrapped in <mark>. */
private fun highlight(text: String, q: String): String {
    val n = q.jsTrim().jsLower()
    if (n.isEmpty()) return esc(text)
    val low = text.jsLower()
    var out = ""
    var i = 0
    var j = low.jsIndex(n)
    while (j != -1) {
        if (j > i) out += esc(text.jsSlice(i, j))
        out += "<mark>" + esc(text.jsSlice(j, j + n.length)) + "</mark>"
        i = j + n.length
        j = low.jsIndex(n, i)
    }
    if (i < text.length) out += esc(text.jsSlice(i))
    return out
}

private fun algolia(appId: String, key: String, index: String): (String, dynamic) -> Promise<Array<Hit>> {
    val endpoint = "https://$appId-dsn.algolia.net/1/indexes/${js("encodeURIComponent")(index)}/query"
    return { q, signal ->
        val init = js("({method:'POST'})")
        init.signal = signal
        val headers = obj(); headers["X-Algolia-Application-Id"] = appId; headers["X-Algolia-API-Key"] = key
        init.headers = headers
        val body = obj(); body.query = q; body.hitsPerPage = 8
        init.body = JSON.stringify(body)
        window.asDynamic().fetch(endpoint, init).then { res: dynamic ->
            if (res.ok != true) js("Promise").reject("Algolia " + res.status) else res.json()
        }.then { data: dynamic ->
            val hits = data.hits.unsafeCast<Array<dynamic>>()
            Array(hits.size) {
                val h = hits[it]
                Hit((h.title ?: h.objectID).unsafeCast<String>(), (h.section ?: h.product ?: "").unsafeCast<String>(), (h.url ?: "#").unsafeCast<String>(), h.accent.unsafeCast<String?>())
            }
        }.unsafeCast<Promise<Array<Hit>>>()
    }
}

fun initSearch() {
    for (root in qa("[data-search]")) {
        val input = qq("#search-input", root).unsafeCast<HTMLInputElement>()
        val btn = qq(".search__btn", root)
        val cancel = qq(".search__cancel", root)
        val panel = qq("#search-results", root)
        val appId = root.data("appId"); val key = root.data("searchKey")
        val provider = if (!appId.isNullOrEmpty() && !key.isNullOrEmpty()) algolia(appId, key, root.data("index")?.ifEmpty { null } ?: "pyrlyn") else null
        var open = false
        var kind = "idle" // idle | off | error | hits
        var hits: Array<Hit> = arrayOf()
        var lastQ = ""
        var active = -1
        var ctrl: AbortController? = null
        var timer = 0
        fun empty(t: String, err: Boolean = false) = "<p class=\"search__empty${if (err) " search__empty--error" else ""}\" role=\"option\" aria-disabled=\"true\">$t</p>"
        fun draw() {
            val shown = open && kind != "idle"
            root.asDynamic().dataset.open = open.toString()
            btn.setAttribute("aria-expanded", open.toString())
            input.setAttribute("aria-expanded", shown.toString())
            input.tabIndex = if (open) 0 else -1; cancel.tabIndex = input.tabIndex
            if (shown && active >= 0) input.setAttribute("aria-activedescendant", "search-opt-$active") else input.removeAttribute("aria-activedescendant")
            panel.hidden = !shown
            if (!shown) panel.asDynamic().dataset.visible = "false" else window.requestAnimationFrame { panel.asDynamic().dataset.visible = "true" }
            panel.innerHTML = when (kind) {
                "off" -> empty("Search isn’t available yet.")
                "error" -> empty("Search is unavailable right now. Try again later.", true)
                "hits" -> if (hits.isEmpty()) empty("No results for “${esc(lastQ)}”") else {
                    var sb = ""
                    for (i in hits.indices) {
                        val h = hits[i]
                        sb += "<a id=\"search-opt-$i\" class=\"search__row\" role=\"option\" aria-selected=\"${i == active}\" href=\"${esc(h.url)}\""
                        if (!h.accent.isNullOrEmpty()) sb += " style=\"--row-accent:${esc(h.accent)}\""
                        sb += "><span class=\"search__icon\" aria-hidden=\"true\"></span><span class=\"search__title\">${highlight(h.title, lastQ)}</span><span class=\"search__section\">${esc(h.section)}</span></a>"
                    }
                    sb
                }
                else -> ""
            }
            if (active >= 0) document.getElementById("search-opt-$active")?.asDynamic()?.scrollIntoView(js("({block:'nearest'})"))
        }
        fun reset() { window.clearTimeout(timer); ctrl?.abort(); kind = "idle"; active = -1; draw() }
        fun query(qs: String) {
            ctrl?.abort()
            if (provider == null) { kind = "off"; active = -1; draw(); return }
            val c = AbortController(); ctrl = c
            provider(qs, c.signal).then({ res ->
                if (c.signal.aborted != true && open) { kind = "hits"; hits = res; lastQ = qs; active = if (res.isEmpty()) -1 else 0; draw() }
            }, { e ->
                if (c.signal.aborted != true) { kind = "error"; lastQ = qs; active = -1; console.warn("[search]", e); draw() }
            })
        }
        fun schedule(raw: String) {
            window.clearTimeout(timer)
            val qs = raw.jsTrim()
            if (qs.isEmpty()) { reset(); return }
            timer = window.setTimeout({ query(qs) }, 150)
        }
        fun doOpen() {
            input.asDynamic().focus(js("({preventScroll:true})"))
            if (open) return
            open = true
            if (mq("(max-width: 639px)").matches == true) html.classList.add("search-lock")
            draw()
        }
        fun close(focusBtn: Boolean = true) {
            if (!open) return
            open = false; reset(); html.classList.remove("search-lock")
            if (focusBtn) btn.focus()
        }
        btn.on("click") { doOpen() }
        cancel.on("click") { input.value = ""; close() }
        input.on("focus") { if (!open) doOpen() }
        input.on("input") { schedule(input.value) }
        input.on("keydown") { e ->
            val list = if (kind == "hits") hits else arrayOf()
            val k = e.key
            if (k == "Escape") { e.preventDefault(); if (input.value.isNotEmpty()) { input.value = ""; reset() } else close() }
            else if ((k == "ArrowDown" || k == "ArrowUp") && list.isNotEmpty()) {
                e.preventDefault(); active = (active + (if (k == "ArrowDown") 1 else -1) + list.size) % list.size; draw()
            } else if (k == "Enter" && active >= 0 && list.at(active)?.url?.isNotEmpty() == true) {
                e.preventDefault(); window.location.href = list[active].url
            }
        }
        root.on("focusout") { e -> val next = e.related; if (next != null && root.contains(next)) return@on; if (input.value.isEmpty()) close(false) }
        onDoc("pointerdown") { e -> if (open && !root.contains(e.el) && input.value.isEmpty()) close(false) }
        onDoc("keydown") { e ->
            val k = e.key.jsLower(); val m = e.asDynamic()
            val hot = (k == "/" && m.metaKey != true && m.ctrlKey != true && m.altKey != true) || (k == "k" && (m.metaKey == true || m.ctrlKey == true))
            if (!hot || isTyping(e.el)) return@onDoc
            e.preventDefault(); doOpen()
        }
        if (input.value.isNotEmpty()) { doOpen(); schedule(input.value) }
    }
}

private fun isTyping(el: dynamic): Boolean {
    if (el == null || el.tagName == undefined) return false
    val tag = el.tagName.unsafeCast<String>()
    return el.isContentEditable == true || tag == "INPUT" || tag == "TEXTAREA" || tag == "SELECT"
}

