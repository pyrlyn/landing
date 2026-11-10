// Header and page chrome: menus, theme toggle, auto-collapsing nav, audience toggle, copy buttons.
package site

import kotlinx.browser.document
import kotlinx.browser.localStorage
import kotlinx.browser.window
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDetailsElement

/** Products menu and docs section menu: <details>, closed on Esc, outside click, focus leaving. */
fun initMenus() {
    for (el in qa("details[data-menu]")) {
        val d = el.unsafeCast<HTMLDetailsElement>()
        onDoc("click") { e -> if (d.open && !d.contains(e.el)) d.open = false }
        onDoc("keydown") { e -> if (e.key == "Escape" && d.open) { d.open = false; q("summary", d)?.focus() } }
        d.on("focusout") { e -> val to = e.related; if (d.open && to != null && !d.contains(to)) d.open = false }
    }
}

/** Theme toggle; first paint is set by the inline head script. */
fun initTheme() {
    val key = "pyrlyn-theme"
    val root = html
    val btn = q("[data-theme-toggle]")
    fun cur() = if (root.getAttribute("data-mode") == "light") "light" else "dark"
    fun label() = btn?.setAttribute("aria-label", if (cur() == "light") "Switch to dark theme" else "Switch to light theme")
    fun apply(m: String) {
        root.setAttribute("data-mode", m); root.setStyle("color-scheme", m)
        window.asDynamic().__applyThemeMeta?.call(null, m); label()
    }
    label()
    btn?.on("click") {
        val n = if (cur() == "light") "dark" else "light"
        try { localStorage.setItem(key, n) } catch (_: dynamic) {}
        apply(n)
    }
    mq("(prefers-color-scheme: light)").addEventListener("change", { e: dynamic ->
        var saved: String? = null
        try { saved = localStorage.getItem(key) } catch (_: dynamic) {}
        if (saved == null) apply(if (e.matches == true) "light" else "dark")
    })
}

/** Auto-collapsing product/docs header with the peek strip. */
fun initNavPin() {
    val nav = q("[data-nav-auto]") ?: return
    val peek = q(".nav__peek .nav__peek-btn", nav)
    val unpin = q(".nav__unpin", nav)
    var pinned = false
    fun set(next: Boolean, keyboard: Boolean) {
        val hadFocus = nav.contains(document.activeElement)
        nav.toggleAttr("data-pinned", next); pinned = next; peek?.setAttribute("aria-expanded", next.toString())
        val noScroll = js("({preventScroll:true})")
        if (keyboard) {
            if (next && unpin != null && unpin.offsetParent != null) unpin.asDynamic().focus(noScroll)
            else if (!next && hadFocus) peek?.asDynamic()?.focus(noScroll)
        } else if (hadFocus) document.activeElement?.asDynamic()?.blur()
    }
    val secs = jsFloat(window.getComputedStyle(html).getPropertyValue("--header-autohide"))
    val ms = (if (secs > 0) secs else 4.0) * 1000
    val arm = {
        q(".nav__inner", nav)?.let { window.getComputedStyle(it).opacity }
        nav.setAttribute("data-collapsed", "")
    }
    window.setTimeout(arm, if (reducedMotion()) ms.trunc() else 0)
    peek?.on("click") { e -> set(true, e.asDynamic().detail == 0) }
    unpin?.on("click") { e -> set(false, e.asDynamic().detail == 0) }
    onDoc("pointerdown") { e -> if (pinned && !nav.contains(e.el)) set(false, false) }
    onDoc("keydown") { e -> if (e.key == "Escape" && pinned) set(false, true) }
}

/** Home: Free / Pro audience toggle. */
fun initAudience() {
    for (group in qa("[data-audience-toggle]")) {
        val root = group.closest("[data-audience]")
        for (b in qa("button[data-set-audience]", group)) b.on("click") {
            val apply = {
                if (root != null) root.asDynamic().dataset.audience = b.data("setAudience")
                for (o in qa("button", group)) o.setAttribute("aria-pressed", (o == b).toString())
            }
            val d = document.asDynamic()
            if (d.startViewTransition && !reducedMotion()) d.startViewTransition(apply) else apply()
        }
    }
}

/** Copy buttons with a live region and a toast. */
fun initCopy() {
    val clip = window.navigator.asDynamic().clipboard ?: return
    val live = q("[data-copy-live]")
    val toast = q("[data-toast]")
    val text = toast?.let { q("span", it) }
    var t = 0
    fun show(msg: String) {
        live?.textContent = msg; text?.textContent = msg; toast?.classList?.add("is-on")
        window.clearTimeout(t); t = window.setTimeout({ toast?.classList?.remove("is-on") }, 1600)
    }
    idle { for (b in qa("button[data-copy]")) b.hidden = false }
    onDoc("click") { e ->
        val btn = e.closest("button[data-copy]").unsafeCast<HTMLButtonElement?>() ?: return@onDoc
        val label = q(".copy__label", btn)
        clip.writeText(btn.data("copy") ?: "").then({
            btn.asDynamic().dataset.state = "copied"; label?.textContent = "Copied"; show("Copied to clipboard")
            window.setTimeout({ js("delete btn.dataset.state"); label?.textContent = "Copy"; live?.textContent = "" }, 1800)
        }, { show("Copy failed — select the text instead") })
    }
}
