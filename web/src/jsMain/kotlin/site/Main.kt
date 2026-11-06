// Site bundle entry. Every page works without JS; this only enhances the server-rendered markup.
package site

import kotlinx.browser.document
import org.w3c.dom.HTMLScriptElement

fun main() {
    initErrorRoute()
    val me = (document.currentScript ?: document.querySelector("script[data-intro]")).unsafeCast<HTMLScriptElement?>()
    initMenus(); initTheme(); initNavPin(); initAudience(); initCopy()
    initScrollReveal(); initShine(); initStill(); initStepReveal()
    initHero(me)
    initTerminals(); initVisuals(); initScenes(); initRing(); initTeamPhoto()
    for (n in qa("[data-dnav]")) initDocsNav(n)
    idle { for (t in qa("[data-toc]")) initToc(t) }
    initSearch(); initPlans(me)
}
