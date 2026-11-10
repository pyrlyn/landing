// The plans widget: loaded by site.js on product pages built with API_URL. It asks the backend for the
// product's plans, swaps the placeholder tiers for them and wires each waitlist plan's join form.
// When the API fails or does not know the product, the placeholder tiers stay as they are.
package plans

import backend.landing.client.api.DefaultApi
import backend.landing.client.model.PlanView
import backend.landing.client.model.PriceView
import backend.landing.client.model.WaitlistRequest
import kotlinx.browser.document
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.w3c.dom.Element
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLFormElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLTemplateElement
import org.w3c.dom.asList
import org.w3c.dom.get
import org.w3c.dom.set

private val scope = MainScope()

fun main() {
    val section = document.querySelector("[data-plans][data-api]") as? HTMLElement ?: return
    val slug = section.dataset["plans"] ?: return
    val api = DefaultApi(baseUrl = section.dataset["api"].orEmpty().trimEnd('/'))
    scope.launch {
        val plans = runCatching {
            val response = api.v1ProjectsSlugPlansGet(slug)
            // 404: the backend does not sell this product; the placeholders are the right answer then.
            if (response.status == 404) emptyList() else if (response.success) response.body().plans else error("HTTP ${response.status}")
        }.onFailure { console.warn("plans: ${it.message}") }.getOrNull()
        if (plans.isNullOrEmpty()) return@launch
        runCatching { render(section, slug, plans, api) }.onFailure { console.warn("plans: ${it.message}") }
    }
}

private fun render(section: HTMLElement, slug: String, plans: List<PlanView>, api: DefaultApi) {
    val template = section.querySelector("[data-plan-tpl]") as HTMLTemplateElement
    val grid = section.querySelector("[data-plans-grid]") as HTMLElement
    // Every card is built before the page changes, so a bad plan leaves the placeholders whole.
    val cards = plans.map { card(template, slug, it, api) }
    plans.indexOfFirst { it.price.minor > 0 }.takeIf { it >= 0 }?.let { cards[it].classList.add("tier--featured") }
    grid.className = when (cards.size) {
        1 -> "grid pricing-pair pricing-pair--free"
        2 -> "grid pricing-pair grid--2"
        else -> "grid grid--3"
    }
    grid.textContent = ""
    cards.forEach { grid.appendChild(it) }
    section.querySelectorAll("[data-plans-placeholder]").asList().forEach { (it as Element).remove() }
    section.setAttribute("data-plans-ready", "")
}

private fun card(template: HTMLTemplateElement, slug: String, plan: PlanView, api: DefaultApi): HTMLElement {
    val card = template.content.firstElementChild!!.cloneNode(true) as HTMLElement
    fun part(name: String) = card.querySelector("[data-plan-$name]") as HTMLElement
    val titleId = "plan-${plan.code}"
    card.setAttribute("aria-labelledby", titleId)
    card.dataset["plan"] = plan.code
    part("name").apply { id = titleId; textContent = plan.name }
    part("price").textContent = money(plan.price)
    part("interval").textContent = when (plan.interval) {
        PlanView.Interval.MONTH -> "per month"
        PlanView.Interval.YEAR -> "per year"
        PlanView.Interval.LIFETIME -> "one-time"
        PlanView.Interval.NONE -> "forever"
    }
    part("desc").let { if (plan.description.isBlank()) it.remove() else it.textContent = plan.description }

    val list = part("features")
    val row = part("feature").parentElement!!
    val lines = plan.features.map(::label) + plan.parameters.map { (k, v) -> "${label(k)}: $v" }
    lines.forEach { text ->
        val li = row.cloneNode(true) as HTMLElement
        (li.querySelector("[data-plan-feature]") as HTMLElement).textContent = text
        list.appendChild(li)
    }
    row.remove()
    if (lines.isEmpty()) list.remove()

    if (!plan.waitlist) part("badge").remove()
    val form = part("join") as HTMLFormElement
    val install = part("install")
    when {
        plan.waitlist -> { install.remove(); join(form, slug, plan, api) }
        plan.price.minor == 0 -> form.remove()
        else -> { form.remove(); install.remove() }
    }
    return card
}

/** Feature and parameter codes (`team_sync`, `devices`) as display text. */
private fun label(code: String): String =
    code.replace('_', ' ').replace('-', ' ').replaceFirstChar { it.uppercaseChar() }

private fun money(price: PriceView): String {
    val options: dynamic = js("({style: 'currency', trailingZeroDisplay: 'stripIfInteger'})")
    options.currency = price.currency
    val format: dynamic = js("new Intl.NumberFormat('en', options)")
    val digits = format.resolvedOptions().maximumFractionDigits as Int
    var amount = price.minor.toDouble()
    repeat(digits) { amount /= 10 }
    return format.format(amount) as String
}

private fun join(form: HTMLFormElement, slug: String, plan: PlanView, api: DefaultApi) {
    val email = form.querySelector("input") as HTMLInputElement
    val button = form.querySelector("button") as HTMLButtonElement
    val status = form.querySelector("[role=status]") as HTMLElement
    email.setAttribute("aria-label", "Email for the ${plan.name} wishlist")
    // The browser checks `required` and `type=email` before it fires submit.
    form.addEventListener("submit", { event ->
        event.preventDefault()
        if (button.disabled) return@addEventListener
        button.disabled = true
        status.textContent = "Sending…"
        scope.launch {
            val code = runCatching { api.v1ProjectsSlugWaitlistPost(slug, WaitlistRequest(email.value.trim())).status }.getOrNull()
            status.textContent = when (code) {
                in 200..299 -> "You are on the ${plan.name} wishlist. We will tell you when it goes on sale."
                400 -> "Enter a valid email address."
                404 -> "This wishlist is closed."
                429 -> "Too many tries. Wait a minute and try again."
                else -> "The wishlist cannot be reached right now. Try again later."
            }
            if (code in 200..299) {
                email.disabled = true
                form.dataset["joined"] = ""
            } else {
                button.disabled = false
            }
        }
    })
}
