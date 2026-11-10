package dev.pyrlyn.landing.e2e

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.fail

/** Markdown is baked to HTML, analytics config survives the build, third-party assets are pinned and non-blocking. */
class ContentTest {
    @Test
    fun `markdown pages render HTML, not raw markdown`() {
        val docs = Site.pages.filter { "/docs/" in it }
        assertTrue(docs.size > 10, "expected docs pages, found ${docs.size}")
        val rawMd = listOf(
            Regex("(?m)^#{1,6} \\S") to "heading marker",
            Regex("```") to "code fence",
            Regex("\\[[^\\]\\n]+]\\((?:https?:|/|\\.)[^)\\s]*\\)") to "markdown link",
            Regex("\\*\\*[^*\\n]+\\*\\*") to "bold marker",
        )
        val problems = mutableListOf<String>()
        val page = Site.newPage(blockExternal = true)
        try {
            for (path in docs) {
                page.navigate(Site.server.origin + path)
                // Docs index pages are card overviews; hand-built pages (no .prose) are checked via .docs__main.
                val prose = page.locator(if (page.locator(".prose").count() > 0) ".prose" else ".docs__main").first()
                if (prose.count() == 0) { if (!path.endsWith("/docs/")) problems += "$path: no doc body"; continue }
                if (prose.locator("p, ul, ol, pre, table, h2").count() == 0) problems += "$path: prose has no HTML blocks"
                // Code blocks may legitimately show markdown, so only prose text outside <pre>/<code> is checked.
                val text = prose.evaluate(
                    "el => { const c = el.cloneNode(true); c.querySelectorAll('pre, code').forEach(n => n.remove()); return c.innerText }",
                ) as String
                for ((re, what) in rawMd) re.find(text)?.let { problems += "$path: raw $what: ${it.value.take(60)}" }
            }
        } finally { Site.close(page) }
        if (problems.isNotEmpty()) fail("Raw markdown found (${problems.size}):\n" + problems.take(60).joinToString("\n"))
    }

    @Test
    fun `analytics config is present in the output`() {
        // Values are matched by shape only and never printed.
        for (path in listOf("/", "/rtok/", "/rtok/docs/")) {
            val h = Site.html(path)
            assertTrue(h.contains("googletagmanager.com/gtag/js"), "$path: GA gtag loader missing")
            assertTrue(Regex("['\"]G-[A-Z0-9]{6,}['\"]").containsMatchIn(h), "$path: GA measurement id missing")
            assertTrue(h.contains("posthog.init("), "$path: PostHog init missing")
            assertTrue(Regex("['\"]phc_[A-Za-z0-9]{10,}['\"]").containsMatchIn(h), "$path: PostHog project key missing")
            assertTrue(Regex("https://[a-z.]*posthog\\.com").containsMatchIn(h), "$path: PostHog host missing")
        }
    }

    @Test
    fun `third-party assets are pinned and non-blocking`() {
        val problems = mutableListOf<String>()
        for (path in Site.pages) {
            val h = Site.html(path)
            for (tag in Regex("<script[^>]* src=\"https?://[^\"]+\"[^>]*>").findAll(h).map { it.value }) {
                if (!Regex("\\b(async|defer)\\b|type=\"module\"").containsMatchIn(tag)) problems += "$path: blocking script $tag"
            }
            for (tag in Regex("<link[^>]* rel=\"stylesheet\"[^>]*>").findAll(h).map { it.value }) {
                if (Regex("href=\"https?://").containsMatchIn(tag)) problems += "$path: render-blocking third-party stylesheet $tag"
            }
            for (m in Regex("href=\"(https://cdn\\.fontshare\\.com/[^\"]+)\"").findAll(h)) {
                // Pinned = content-addressed /wf/<hash>/... file, not a moving CSS API URL.
                if (!m.groupValues[1].contains("/wf/")) problems += "$path: unpinned fontshare URL ${m.groupValues[1]}"
            }
        }
        if (problems.isNotEmpty()) fail(problems.take(40).joinToString("\n"))

        // With every third-party request aborted the page still loads, renders and runs its script.
        val page = Site.newPage(blockExternal = true)
        try {
            val errors = mutableListOf<String>()
            page.onPageError { errors += it.toString() }
            page.navigate(Site.server.origin + "/")
            assertTrue(page.locator("h1").first().isVisible, "home h1 should render offline")
            page.locator("[data-ring].is-ready").first().waitFor()
            assertTrue(errors.isEmpty(), "page errors offline: $errors")
        } finally { Site.close(page) }
    }

    @Test
    fun `pinned CDN font loads`() {
        val url = Regex("href=\"(https://cdn\\.fontshare\\.com/wf/[^\"]+\\.woff2)\"").find(Site.html("/"))?.groupValues?.get(1)
            ?: fail("no preloaded fontshare woff2 on home")
        val page = Site.newPage()
        try {
            val r = runCatching { page.request().get(url) }.getOrElse {
                org.junit.jupiter.api.Assumptions.abort<Unit>("network unavailable: ${it.message?.lineSequence()?.first()}"); return
            }
            assertTrue(r.status() == 200, "font CDN returned ${r.status()}")
        } finally { Site.close(page) }
    }
}
