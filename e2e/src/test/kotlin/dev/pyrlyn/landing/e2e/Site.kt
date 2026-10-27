package dev.pyrlyn.landing.e2e

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import java.io.File

/** Shared fixture: one static server and one headless Chromium for the whole run. */
object Site {
    val root: File = File(System.getProperty("e2e.siteDir") ?: error("e2e.siteDir is not set; run ./gradlew e2eTest"))
        .also { require(File(it, "index.html").isFile) { "No generated site in $it; run ./gradlew generateSite" } }
    val server = StaticServer(root)
    private val playwright: Playwright = Playwright.create()
    val browser: Browser = playwright.chromium().launch(
        BrowserType.LaunchOptions().setHeadless(System.getProperty("e2e.headed") != "true"),
    )

    /** Analytics must never be hit from tests; these hosts are aborted at the network layer. */
    val analyticsHosts = listOf("googletagmanager.com", "google-analytics.com", "posthog.com")

    /** Every generated page as a URL path (`/rtok/docs/install/`), excluding the error pages. */
    val pages: List<String> by lazy {
        root.walkTopDown().filter { it.isFile && it.name.endsWith(".html") }
            .map { "/" + it.relativeTo(root).invariantSeparatorsPath }
            .filter { it != "/404.html" && it != "/500.html" }
            .map { it.removeSuffix("index.html") }
            .sorted().toList()
    }

    fun html(path: String): String = server.resolve(path)!!.readText()

    fun isInternal(url: String) = url.startsWith(server.origin)

    /** New page with analytics blocked; [blockExternal] also aborts every third-party request (offline mode). */
    fun newPage(blockExternal: Boolean = false, width: Int = 1280, height: Int = 900): Page {
        val ctx = browser.newContext(Browser.NewContextOptions().setViewportSize(width, height)
                // Reduced motion keeps animated widgets stable for clicks; the scripts honour it.
                .setReducedMotion(com.microsoft.playwright.options.ReducedMotion.REDUCE))
        ctx.route("**/*") { route ->
            val url = route.request().url()
            when {
                isInternal(url) || url.startsWith("data:") -> route.resume()
                blockExternal || analyticsHosts.any { url.contains(it) } -> route.abort()
                else -> route.resume()
            }
        }
        return ctx.newPage()
    }

    fun close(page: Page) = page.context().close()

    init {
        Runtime.getRuntime().addShutdownHook(Thread { runCatching { browser.close(); playwright.close(); server.close() } })
    }
}
