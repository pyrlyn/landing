// Wishlist mail links: the token is sent only on a click (needs API_URL at build time).
package site

import kotlinx.browser.window
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLScriptElement

fun initWishlist(me: HTMLScriptElement?) {
    val sec = q("[data-wishlist-link]") ?: return
    val api = me?.data("api")?.ifEmpty { null } ?: return
    val action = sec.data("wishlistLink")
    val button = qq("[data-wishlist-action]", sec).unsafeCast<HTMLButtonElement>()
    val status = qq("[role=status]", sec)
    val token = js("new URLSearchParams(location.search)").get("token").unsafeCast<String?>() ?: ""
    if (window.location.search.isNotEmpty()) window.history.replaceState(null, "", window.location.pathname)
    if (token.isEmpty()) status.textContent = "This link is incomplete. Open the link from the mail again." else button.disabled = false
    fun gql(query: String): dynamic {
        val init = js("({method:'POST',headers:{'content-type':'application/json'}})")
        val vars = obj(); vars.token = token
        val body = obj(); body.query = query; body.variables = vars
        init.body = JSON.stringify(body)
        return window.asDynamic().fetch(api, init).then { r: dynamic -> r.json() }
    }
    button.on("click") {
        button.disabled = true; status.textContent = "Sending…"
        val done = if (action == "confirm") {
            gql("mutation ConfirmWishlist(\$token: String!) { confirmWishlist(token: \$token) { __typename ... on MutationConfirmWishlistSuccess { data } } }").then { r: dynamic ->
                val c = r?.data?.confirmWishlist
                status.textContent = if (c?.__typename == "MutationConfirmWishlistSuccess") "Confirmed: you are on the ${c.data} Pro wishlist." else "This link is invalid, already used or expired."
            }
        } else {
            gql("mutation UnsubscribeWishlist(\$token: String!) { unsubscribeWishlist(token: \$token) }").then { _: dynamic ->
                status.textContent = "Unsubscribed: no more mail about this wishlist."
            }
        }
        done.catch { _: dynamic -> status.textContent = "The wishlist cannot be reached right now. Try again later."; button.disabled = false }
    }
}
