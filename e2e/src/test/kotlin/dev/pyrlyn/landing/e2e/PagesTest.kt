package dev.pyrlyn.landing.e2e

import com.microsoft.playwright.Page
import com.microsoft.playwright.options.LoadState
import java.net.URI
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.fail

/** Crawls every generated page in Chromium: status, console, same-origin requests, SEO basics, links. */
class PagesTest {
    private fun failIf(problems: List<String>, what: String) {
        if (problems.isNotEmpty()) fail("$what (${problems.size}):\n" + problems.take(60).joinToString("\n"))
    }

    @Test
    fun `every page loads with 200, no console errors and no broken same-origin requests`() {
        val problems = mutableListOf<String>()
        val page = Site.newPage()
        var current = ""
        page.onConsoleMessage { m ->
            val loc = m.location() ?: ""
            // Aborted analytics/third-party requests log "Failed to load resource" against their own URL.
            if (m.type() == "error" && (loc.isEmpty() || Site.isInternal(loc))) problems += "$current console: ${m.text()}"
        }
        page.onPageError { e -> problems += "$current pageerror: $e" }
        page.onResponse { r -> if (Site.isInternal(r.url()) && r.status() >= 400) problems += "$current -> ${r.status()} ${r.url()}" }
        page.onRequestFailed { r -> if (Site.isInternal(r.url())) problems += "$current -> failed ${r.url()}: ${r.failure()}" }
        try {
            for (path in Site.pages) {
                current = path
                val res = page.navigate(Site.server.origin + path)
                if (res?.status() != 200) problems += "$path status ${res?.status()}"
                page.waitForLoadState(LoadState.LOAD)
            }
        } finally { Site.close(page) }
        failIf(problems, "Page load problems")
    }

    @Test
    fun `every page has title, description, canonical, og tags, lang and one h1`() {
        val problems = mutableListOf<String>()
        for (path in Site.pages) {
            val h = Site.html(path)
            fun need(ok: Boolean, what: String) { if (!ok) problems += "$path: $what" }
            need(Regex("<title>[^<]{5,}</title>").containsMatchIn(h), "title")
            need(Regex("<meta name=\"description\" content=\"[^\"]{20,}\"").containsMatchIn(h), "meta description")
            val noindex = Regex("<meta name=\"robots\" content=\"[^\"]*noindex").containsMatchIn(h)
            need(noindex || Regex("<link rel=\"canonical\" href=\"https://[^\"]+\"").containsMatchIn(h), "canonical")
            need(h.contains("property=\"og:title\"") && h.contains("property=\"og:image\""), "og:title/og:image")
            need(h.contains("name=\"viewport\""), "viewport")
            need(Regex("<html[^>]* lang=\"[a-z]").containsMatchIn(h), "html lang")
            need(Regex("<h1[ >]").findAll(h).count() == 1, "exactly one h1")
        }
        failIf(problems, "SEO problems")
        val robots = Site.root.resolve("robots.txt").readText()
        assertTrue(robots.contains("Sitemap:"), "robots.txt must reference the sitemap")
        assertTrue(Site.root.resolve("sitemap-index.xml").isFile, "sitemap-index.xml missing")
    }

    @Test
    fun `every internal link and asset resolves, and sitemap URLs exist`() {
        val attr = Regex("""(?:href|src|data-intro)="([^"]+)"""")
        val problems = mutableListOf<String>()
        val targets = sortedSetOf<String>()
        for (path in Site.pages) for (m in attr.findAll(Site.html(path))) {
            val raw = m.groupValues[1].replace("&amp;", "&")
            if (raw.startsWith("#") || raw.startsWith("mailto:") || raw.startsWith("data:") || raw.contains("://") || raw.startsWith("//")) continue
            val resolved = URI(Site.server.origin + path).resolve(raw.replace(" ", "%20"))
            targets += resolved.rawPath
        }
        for (srcset in Regex("""srcset="([^"]+)"""").findAll(Site.pages.joinToString("") { Site.html(it) })) {
            srcset.groupValues[1].split(",").map { it.trim().substringBefore(" ") }.filter { it.startsWith("/") }.forEach { targets += it }
        }
        val sitemap = Site.root.walkTopDown().filter { it.name.startsWith("sitemap-") && it.name != "sitemap-index.xml" }
            .flatMap { Regex("<loc>https?://[^/<]+(/[^<]*)</loc>").findAll(it.readText()).map { m -> m.groupValues[1] } }.toList()
        assertTrue(sitemap.isNotEmpty(), "sitemap has no URLs")
        targets += sitemap
        val page = Site.newPage()
        try {
            for (t in targets) {
                val r = page.request().get(Site.server.origin + t)
                if (r.status() != 200) problems += "${r.status()} $t"
            }
        } finally { Site.close(page) }
        failIf(problems, "Broken internal URLs out of ${targets.size}")
    }

    @Test
    fun `unknown URL serves the 404 page with status 404`() {
        val page = Site.newPage()
        try {
            val res = page.navigate(Site.server.origin + "/definitely-not-a-page/")
            assertTrue(res?.status() == 404, "expected 404, got ${res?.status()}")
            assertTrue(page.locator("h1").count() == 1, "404 page should render a heading")
        } finally { Site.close(page) }
    }

    @Test
    fun `header navigation from home reaches product pages`() {
        val page: Page = Site.newPage()
        try {
            page.navigate(Site.server.origin + "/")
            val link = page.locator("a[href='/rtok/']:visible").first()
            link.click()
            page.waitForURL("**/rtok/")
            assertTrue(page.locator("h1").first().innerText().isNotBlank(), "rtok page h1")
            page.goBack()
            page.waitForURL(Site.server.origin + "/")
        } finally { Site.close(page) }
    }
}
