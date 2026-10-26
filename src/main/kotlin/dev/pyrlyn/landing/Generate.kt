package dev.pyrlyn.landing

import dev.pyrlyn.landing.build.AssetPipeline
import dev.pyrlyn.landing.config.SiteConfig
import dev.pyrlyn.landing.content.Catalog
import dev.pyrlyn.landing.content.Docs
import dev.pyrlyn.landing.content.Names
import dev.pyrlyn.landing.html.Assets
import dev.pyrlyn.landing.html.Ctx
import dev.pyrlyn.landing.pages.*
import java.io.File
import kotlin.system.measureTimeMillis

/** Static site generator: `./gradlew generateSite` → build/site (port of `astro build`). */
fun main(args: Array<String>) {
    val out = File(args.getOrNull(0) ?: "build/site")
    val ms = measureTimeMillis { generate(File("."), out, SiteConfig.fromEnv()) }
    println("landing: generated ${out.walkTopDown().count { it.extension == "html" }} pages into $out in ${ms}ms")
}

fun generate(root: File, out: File, cfg: SiteConfig) {
    val content = File(root, "content")
    val publicDir = File(root, "public")
    val web = File(root, "src/main/web")

    // Product subcommands used anywhere in the synced copy, so "<name> <cmd>" stays lowercase.
    Names.harvestFromMarkdown(content.walkTopDown().filter { it.isFile && it.extension == "md" }.map { it.readText() }.toList())

    out.deleteRecursively()
    out.mkdirs()
    publicDir.copyRecursively(out, overwrite = true)

    // Assets: CSS is inlined into each page (like the Astro build) to keep first paint request-free;
    // JS ships as one hashed, cacheable module plus the lazily imported WebGL intro.
    val css = web.resolve("css")
    val siteCss = AssetPipeline.minifyCss(listOf("global.css", "tailwind-utilities.css", "products.css", "motion.css", "search.css", "light.css").joinToString("\n") { AssetPipeline.cssWithImports(css.resolve(it)) })
    val docsCss = AssetPipeline.minifyCss(AssetPipeline.cssWithImports(css.resolve("docs.css")))
    val assetDir = File(out, "_assets").apply { mkdirs() }
    fun emit(name: String, ext: String, body: String): String {
        val file = "$name.${AssetPipeline.hash(body)}.$ext"
        File(assetDir, file).writeText(body)
        return "/_assets/$file"
    }
    val js = emit("site", "js", AssetPipeline.minifyJs(AssetPipeline.bundleJs(web.resolve("js/site.js"))))
    val intro = emit("intro-gl", "js", AssetPipeline.minifyJs(web.resolve("js/lib/intro-gl.js").readText()))

    val catalog = Catalog.load(File(content, "projects"))
    val docs = Docs(File(content, "docs"), cfg)
    val ctx = Ctx(cfg, catalog, docs, publicDir, Assets(siteCss, docsCss, js, intro))

    val pages = linkedMapOf<String, String>()
    pages["index.html"] = ctx.homePage()
    pages["404.html"] = ctx.notFoundPage()
    pages["500.html"] = ctx.serverErrorPage()
    pages["support/index.html"] = ctx.supportPage()
    pages["team/index.html"] = ctx.teamPage()
    pages["wishlist/confirm/index.html"] = ctx.wishlistPage("confirm")
    pages["wishlist/unsubscribe/index.html"] = ctx.wishlistPage("unsubscribe")
    val withDocs = docs.productsWithDocs().toSet()
    for (t in catalog.tools) {
        pages["${t.id}/index.html"] = ctx.productPage(t)
        if (t.id in withDocs) {
            pages["${t.id}/docs/index.html"] = ctx.docsIndexPage(t)
            for (p in docs.get(t.id).pages) pages["${t.id}/docs/${p.slug}/index.html"] = ctx.docPage(t, p)
        }
    }
    for ((path, html) in pages) File(out, path).apply { parentFile.mkdirs() }.writeText(html)

    // robots.txt + sitemap (wishlist pages are noindex mail-link targets, not content).
    val urls = pages.keys.filter { it.endsWith("index.html") && !it.startsWith("wishlist/") }
        .map { cfg.abs("/" + it.removeSuffix("index.html")) }
    File(out, "sitemap-0.xml").writeText(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">" +
            urls.joinToString("") { "<url><loc>$it</loc></url>" } + "</urlset>",
    )
    File(out, "sitemap-index.xml").writeText(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"><sitemap><loc>${cfg.abs("/sitemap-0.xml")}</loc></sitemap></sitemapindex>",
    )
    File(out, "robots.txt").writeText("User-agent: *\nAllow: /\n\nSitemap: ${cfg.abs("/sitemap-index.xml")}\n")
}
