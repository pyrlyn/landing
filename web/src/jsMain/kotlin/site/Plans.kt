// Product-page pricing: loads the plans widget (web-plans) only where a page asks for it, because it
// carries the backend client, which this bundle must not.
package site

import kotlinx.browser.document
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLScriptElement

fun initPlans(me: HTMLScriptElement?) {
    if (q("[data-plans][data-api]") == null) return
    val src = me?.data("plans") ?: return
    idle {
        val s = document.createElement("script").unsafeCast<HTMLScriptElement>()
        s.src = src; s.async = true
        document.head.unsafeCast<HTMLElement>().appendChild(s)
    }
}
