package dev.pyrlyn.landing.e2e

import com.microsoft.playwright.Page
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/** The Kotlin/JS bundle (`:web`) enhances the pre-rendered markup; each widget is exercised once. */
class BehaviorTest {
    private fun <T> onPage(path: String, width: Int = 1280, block: (Page) -> T): T {
        val page = Site.newPage(blockExternal = true, width = width)
        return try { page.navigate(Site.server.origin + path); block(page) } finally { Site.close(page) }
    }

    @Test
    fun `theme toggle switches and persists the mode`() = onPage("/") { p ->
        val html = p.locator("html")
        val before = html.getAttribute("data-mode") ?: "dark"
        p.locator("[data-theme-toggle]").first().click()
        val after = html.getAttribute("data-mode")
        assertNotEquals(before, after, "data-mode should flip")
        assertEquals(after, p.evaluate("localStorage.getItem('pyrlyn-theme')"))
        p.reload()
        assertEquals(after, p.locator("html").getAttribute("data-mode"), "saved theme applies on reload")
    }

    @Test
    fun `menu opens and closes on Escape and outside click`() = onPage("/", width = 390) { p ->
        val menu = p.locator("details[data-menu]").first()
        menu.locator("summary").click()
        assertTrue(menu.getAttribute("open") != null, "menu should open")
        p.keyboard().press("Escape")
        assertTrue(menu.getAttribute("open") == null, "Escape should close the menu")
        menu.locator("summary").click()
        p.mouse().click(5.0, 600.0)
        assertTrue(menu.getAttribute("open") == null, "outside click should close the menu")
    }

    @Test
    fun `home ring carousel moves with next and prev`() = onPage("/") { p ->
        val ring = p.locator("[data-ring]").first()
        ring.scrollIntoViewIfNeeded()
        val status = ring.locator("[data-ring-status]")
        assertTrue(status.innerText().contains("1 /"), "starts at slide 1: ${status.innerText()}")
        ring.locator("[data-ring-next]").click()
        assertTrue(status.innerText().contains("2 /"), "next goes to slide 2: ${status.innerText()}")
        ring.locator("[data-ring-prev]").click()
        ring.locator("[data-ring-prev]").click()
        val total = status.innerText().substringAfterLast("/ ").trim()
        assertTrue(status.innerText().contains("$total / $total"), "prev wraps to last: ${status.innerText()}")
        assertEquals(1, ring.locator("[data-ring-item].is-active").count())
    }

    @Test
    fun `audience toggle switches OSS and Pro`() = onPage("/") { p ->
        val pro = p.locator("button[data-set-audience=pro]").first()
        pro.scrollIntoViewIfNeeded()
        pro.click()
        p.waitForCondition { pro.getAttribute("aria-pressed") == "true" }
        assertEquals("false", p.locator("button[data-set-audience=oss]").first().getAttribute("aria-pressed"))
        assertEquals("pro", p.locator("[data-audience]").first().getAttribute("data-audience"))
    }

    @Test
    fun `cox scenes switch tabs`() = onPage("/cox/") { p ->
        val scenes = p.locator("[data-scenes].is-ready").first()
        scenes.scrollIntoViewIfNeeded()
        val tabs = scenes.locator("[role=tab]")
        assertTrue(tabs.count() > 1, "expected several scene tabs")
        tabs.nth(1).click()
        assertEquals("true", tabs.nth(1).getAttribute("aria-selected"))
        assertEquals("false", tabs.nth(0).getAttribute("aria-selected"))
        assertTrue(scenes.locator("[data-scene]").nth(1).getAttribute("class")!!.contains("is-active"))
    }

    @Test
    fun `docs pages get the enhanced docs nav`() = onPage("/rtok/docs/install/") { p ->
        assertTrue(p.locator("[data-dnav]").count() > 0, "docs nav present")
        assertTrue(p.locator(".prose h2, .prose h3").count() > 0, "docs content has headings")
    }

    @Test
    fun `team page shows the photo, six numbered members and reveals the photo`() = onPage("/team/") { p ->
        assertEquals(6, p.locator(".team-pin").count())
        assertEquals(6, p.locator(".team-list > li").count())
        assertEquals(1, p.locator(".team-card--lead").count())
        assertTrue(p.locator(".team-list").innerText().contains("Ivan Tuhai"))
        assertTrue(p.locator(".team-photo__layer img").evaluate("i => i.complete && i.naturalWidth > 0") as Boolean)
        p.locator("[data-team-photo]").scrollIntoViewIfNeeded()
        p.waitForFunction("() => document.querySelector('[data-team-photo]').dataset.state === 'in'")
    }
}
