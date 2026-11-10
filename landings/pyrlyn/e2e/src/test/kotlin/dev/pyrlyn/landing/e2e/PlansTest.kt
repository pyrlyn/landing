package dev.pyrlyn.landing.e2e

import com.microsoft.playwright.Page
import com.microsoft.playwright.Route
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.jupiter.api.Assumptions.assumeTrue
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * The plans widget (`:web-plans`, the backend client) on the site built with the stub API_URL. The tests
 * play the backend: they answer the plans and waitlist routes, including its errors.
 */
class PlansTest {
    private val cors = mapOf(
        "access-control-allow-origin" to "*",
        "access-control-allow-methods" to "GET, POST",
        "access-control-allow-headers" to "*",
    )

    private fun Route.json(status: Int, body: String = "") =
        fulfill(Route.FulfillOptions().setStatus(status).setHeaders(cors + ("content-type" to "application/json")).setBody(body))

    /** Opens rtok's page with [api] answering every request to the stub API but CORS preflights. */
    private fun <T> onProduct(api: (Route) -> Unit, block: (Page) -> T): T {
        val page = Site.newPage(blockExternal = true)
        page.route("${Site.API}/**") { route ->
            if (route.request().method() == "OPTIONS") route.fulfill(Route.FulfillOptions().setStatus(204).setHeaders(cors)) else api(route)
        }
        return try {
            page.navigate(Site.apiServer.origin + "/rtok/")
            block(page)
        } finally {
            Site.close(page)
        }
    }

    /** Answers the plans route with [PLANS] and the waitlist route with [waitlist]. */
    private fun backend(waitlist: (Route) -> Unit): (Route) -> Unit = { route ->
        if (route.request().url().endsWith("/v1/projects/rtok/plans")) route.json(200, PLANS) else waitlist(route)
    }

    private fun Page.plansReady() = waitForSelector("#pricing[data-plans-ready]")

    @Test
    fun `plans from the API replace the placeholder tiers`() = onProduct(backend { it.abort() }) { p ->
        p.plansReady()
        val cards = p.locator("[data-plans-grid] > article")
        assertEquals(2, cards.count())
        assertEquals(0, p.locator("#pricing [data-plans-placeholder]").count(), "placeholder badge should go")

        val free = p.locator("[data-plan=free]")
        assertEquals("$0", free.locator("[data-plan-price]").textContent())
        assertEquals(1, free.locator("[data-plan-install]").count(), "a free plan offers the install")
        assertEquals(0, free.locator("form").count())

        val pro = p.locator("[data-plan=pro]")
        assertEquals("$9.90", pro.locator("[data-plan-price]").textContent())
        assertEquals("per month", pro.locator("[data-plan-interval]").textContent())
        assertEquals(listOf("Team sync", "Seats: 5"), pro.locator("[data-plan-feature]").allTextContents())
        assertTrue(pro.getAttribute("class")!!.contains("tier--featured"))
        assertEquals(1, pro.locator("form[data-plan-join]").count(), "a waitlist plan offers the join form")
    }

    @Test
    fun `join form posts the email and confirms`() {
        var sent: String? = null
        onProduct(backend { route -> sent = route.request().postData(); route.json(204) }) { p ->
            p.plansReady()
            val form = p.locator("[data-plan=pro] form")
            form.locator("input[type=email]").fill(" ann@example.com ")
            form.locator("button[type=submit]").click()
            p.waitForSelector("[data-plan=pro] form[data-joined]")
            assertTrue(form.locator("[role=status]").textContent().orEmpty().startsWith("You are on the Pro wishlist"))
            assertTrue(form.locator("input").isDisabled(), "a joined form takes no second address")
        }
        val email = Json.parseToJsonElement(sent!!).jsonObject.getValue("email").jsonPrimitive.content
        assertEquals("ann@example.com", email)
    }

    @Test
    fun `join form reports the API's errors and lets the user retry`() {
        var answer: (Route) -> Unit = { it.json(429, """{"error":"rate_limited","message":"slow down"}""") }
        onProduct(backend { answer(it) }) { p ->
            p.plansReady()
            val form = p.locator("[data-plan=pro] form")
            val status = form.locator("[role=status]")
            val submit = form.locator("button[type=submit]")
            form.locator("input[type=email]").fill("ann@example.com")

            fun expect(text: String) {
                submit.click()
                p.waitForFunction("t => document.querySelector('[data-plan=pro] [role=status]').textContent.startsWith(t)", text)
                assertFalse(submit.isDisabled(), "the user can try again after: $text")
            }
            expect("Too many tries")
            answer = { it.json(400, """{"error":"validation_failed","message":"request is invalid"}""") }
            expect("Enter a valid email")
            answer = { it.abort() }
            expect("The wishlist cannot be reached")
            assertTrue(status.isVisible())
        }
    }

    @Test
    fun `placeholders stay when the API fails or does not know the product`() {
        for (answer in listOf<(Route) -> Unit>({ it.abort() }, { it.json(404, """{"error":"not_found","message":"no project"}""") })) {
            var asked = false
            onProduct({ asked = true; answer(it) }) { p ->
                p.waitForCondition { asked }
                p.waitForTimeout(300.0)
                assertEquals(0, p.locator("#pricing[data-plans-ready]").count())
                assertEquals(1, p.locator("#pricing [data-plans-placeholder]").count())
                assertEquals("true", p.locator("#pricing button[aria-disabled]").getAttribute("aria-disabled"))
            }
        }
    }

    @Test
    fun `without API_URL the site ships no plans widget`() {
        assumeTrue(System.getenv("API_URL").isNullOrBlank(), "the main site was built with API_URL")
        val html = Site.html("/rtok/")
        assertFalse(html.contains("data-plans="), "no live plans without an API")
        assertFalse(html.contains("data-plan-tpl"))
        assertTrue(File(Site.root, "_assets").list()!!.none { it.startsWith("plans.") }, "no plans bundle without an API")
        assertTrue(html.contains("Join the Pro wishlist"), "the placeholder action stays")
    }

    private companion object {
        val PLANS = """
            {"plans": [
              {"code": "free", "name": "Free", "description": "", "price": {"minor": 0, "currency": "USD"},
               "interval": "NONE", "features": ["hooks"], "parameters": {}, "waitlist": false},
              {"code": "pro", "name": "Pro", "description": "For teams.", "price": {"minor": 990, "currency": "USD"},
               "interval": "MONTH", "features": ["team_sync"], "parameters": {"seats": "5"}, "waitlist": true}
            ]}
        """.trimIndent()
    }
}
