package dev.pyrlyn.landing.seo

import dev.pyrlyn.landing.config.SiteConfig
import dev.pyrlyn.landing.content.Project
import dev.pyrlyn.landing.content.Site.BRAND
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * SEO helpers: meta text and schema.org JSON-LD nodes (port of src/lib/seo.ts). Every value comes from
 * the site's content; nothing invents ratings, reviews or prices. The only offer is the free edition ($0).
 */
class Seo(private val cfg: SiteConfig) {
    fun abs(path: String = "/") = cfg.abs(path)
    val homeUrl get() = abs("/")
    val orgId get() = "${abs("/")}#organization"
    val websiteId get() = "${abs("/")}#website"
    fun appId(slug: String) = "${abs("/$slug/")}#software"

    fun organization() = buildJsonObject {
        put("@type", "Organization"); put("@id", orgId); put("name", "Pyrlyn"); put("url", homeUrl)
        put("logo", abs("/pyrlyn/pyrlyn-mark-on-dark-1000.png"))
        put("sameAs", buildJsonArray { add(JsonPrimitive("https://github.com/pyrlyn")) })
    }

    fun website(description: String) = buildJsonObject {
        put("@type", "WebSite"); put("@id", websiteId); put("name", BRAND); put("url", homeUrl)
        put("description", description); put("inLanguage", "en")
        put("publisher", buildJsonObject { put("@id", orgId) })
    }

    /** Operating systems named in the project's own Install section. */
    fun operatingSystems(installMd: String?): String {
        val os = listOf("macOS" to Regex("macos", RegexOption.IGNORE_CASE), "Linux" to Regex("linux", RegexOption.IGNORE_CASE), "Windows" to Regex("windows|powershell", RegexOption.IGNORE_CASE))
        return os.filter { it.second.containsMatchIn(installMd ?: "") }.joinToString(", ") { it.first }.ifEmpty { "macOS, Linux" }
    }

    fun softwareApplication(t: Project, description: String, installMd: String?, features: List<String>): JsonObject {
        val url = abs("/${t.id}/")
        return buildJsonObject {
            put("@type", "SoftwareApplication"); put("@id", appId(t.id)); put("name", t.title); put("description", description); put("url", url)
            put("applicationCategory", "DeveloperApplication"); put("applicationSubCategory", "Command-line tool")
            put("operatingSystem", operatingSystems(installMd)); put("softwareVersion", t.version)
            put("image", abs("/images/${t.id}/og.jpg")); put("downloadUrl", "${t.repo}/releases"); put("installUrl", "$url#install")
            if (features.isNotEmpty()) put("featureList", JsonArray(features.map(::JsonPrimitive)))
            put("isAccessibleForFree", true)
            put("offers", buildJsonObject { put("@type", "Offer"); put("name", "Free · open source"); put("price", "0"); put("priceCurrency", "USD"); put("url", "$url#pricing") })
            put("publisher", buildJsonObject { put("@id", orgId) })
            put("sameAs", JsonArray(listOfNotNull(t.repo, t.homepage).map(::JsonPrimitive)))
            put("mainEntityOfPage", url)
        }
    }

    fun sourceCode(t: Project) = buildJsonObject {
        put("@type", "SoftwareSourceCode"); put("@id", "${abs("/${t.id}/")}#source"); put("name", "${t.title} source code")
        put("codeRepository", t.repo); put("programmingLanguage", "Rust")
        put("targetProduct", buildJsonObject { put("@id", appId(t.id)) })
    }

    fun breadcrumbs(items: List<Pair<String, String>>) = buildJsonObject {
        put("@type", "BreadcrumbList")
        put("itemListElement", JsonArray(items.mapIndexed { i, (name, url) -> buildJsonObject { put("@type", "ListItem"); put("position", i + 1); put("name", name); put("item", url) } }))
    }

    fun techArticle(t: Project, headline: String, description: String, url: String, sourceUrl: String?, section: String?) = buildJsonObject {
        put("@type", "TechArticle"); put("@id", "$url#article"); put("headline", headline.take(110)); put("description", description)
        put("url", url); put("mainEntityOfPage", url); put("inLanguage", "en"); put("image", abs("/images/${t.id}/og.jpg"))
        if (!section.isNullOrEmpty()) put("articleSection", section)
        put("about", buildJsonObject { put("@type", "SoftwareApplication"); put("@id", appId(t.id)); put("name", t.title); put("url", abs("/${t.id}/")) })
        put("isPartOf", buildJsonObject { put("@id", websiteId) })
        put("publisher", buildJsonObject { put("@id", orgId) })
        if (!sourceUrl.isNullOrEmpty()) put("isBasedOn", sourceUrl)
    }

    /** One schema.org graph, safe to inline in a <script>. */
    fun graph(nodes: List<JsonElement>): String =
        JsonObject(mapOf("@context" to JsonPrimitive("https://schema.org"), "@graph" to JsonArray(nodes))).toString().replace("<", "\\u003c")

    data class ProductSeo(val title: String, val description: String)

    companion object {
        /** Copywriter-approved product <title> and meta description, keyed by project id. */
        val PRODUCT_SEO: Map<String, ProductSeo> = mapOf(
            "rtok" to ProductSeo("Rtok: token-reduction CLI for AI coding agents | Pyrlyn", "Rtok cuts tokens for AI coding agents with hooks, an MCP server and an API proxy, all in one Rust binary. Install guides, commands and examples."),
            "ketch" to ProductSeo("Ketch: install CLI tools from GitHub releases | Pyrlyn", "Ketch is a single-binary package manager for macOS, Linux and Windows: it downloads GitHub release assets, verifies checksums and puts them on your PATH."),
            "cox" to ProductSeo("cox: modular terminal coding agent in Rust | Pyrlyn", "cox is a terminal coding agent in Rust with a safe, event-driven core: interactive TUI, headless runs, editor integration via ACP and MCP tools."),
            "runa" to ProductSeo("Runa: local-first AI CLI for GGUF models | Pyrlyn", "Runa checks whether a GGUF model fits your hardware before you download it, runs it locally via ggml / llama.cpp, or calls OpenAI and Anthropic."),
            "mailune" to ProductSeo("Mailune: local-first mail client with private AI | Pyrlyn", "Mailune is a local-first mail client with private AI for macOS, iOS, Windows, Linux, Android and the web. In development, no release yet."),
            "swarfr" to ProductSeo("Swarfr: shrink Cargo target/ directories safely | Pyrlyn", "Swarfr compresses and dedupes Cargo target/ build dirs without deleting what you still build with. Runs under cargo's build lock and keeps mtimes."),
            "weft" to ProductSeo("Weft: agent-friendly UI description format | Pyrlyn", "Weft is an open UI format: strict markup that AI agents read and write, canonical JSON that tools validate and patch. CLI, MCP and editor plugins."),
            "stator" to ProductSeo("Stator: compile TypeScript to native binaries | Pyrlyn", "Stator compiles TypeScript and JavaScript ahead of time to native binaries: unboxed values for typed code, a dynamic path for untyped. Research compiler."),
            "aulo" to ProductSeo("Aulo: local-first voice agent for your computer | Pyrlyn", "Aulo is a planned voice agent that drives your shell, apps and browser, with MCP both ways and local or remote models. Specification stage."),
            "scull" to ProductSeo("Scull: Rust terminal emulator with native UI | Pyrlyn", "Scull is a terminal emulator with a Rust core and a native interface per platform: SwiftUI on macOS, WinUI on Windows. Early scaffold."),
            "kerf" to ProductSeo("Kerf: Rust IDE core with sandboxed WASM plugins | Pyrlyn", "Kerf is an IDE with a small Rust core, a tree-sitter semantic index and sandboxed WASM plugins; LSP and DAP run as separate processes. Research stage."),
            "bindsmith" to ProductSeo("Bindsmith: Dart bindings for native APIs | Pyrlyn", "Bindsmith generates Dart bindings for native APIs on six Flutter platforms from one bindsmith.yaml: Android, iOS, macOS, Windows, Linux and Web."),
        )

        /** Markdown to plain text (comments, link targets and emphasis marks dropped). */
        fun plain(s: String = "") = s.replace(Regex("<!--[\\s\\S]*?-->"), "").replace(Regex("\\[([^\\]]*)\\]\\([^)]*\\)"), "$1")
            .replace(Regex("[`*_]"), "").replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&").replace(Regex("\\s+"), " ").trim()

        /** Cut at a sentence end if one is late enough, else at a word boundary. */
        fun clip(text: String, max: Int = 160): String {
            val t = text.replace(Regex("\\s+"), " ").trim()
            if (t.length <= max) return t
            val cut = t.substring(0, max)
            val dot = Regex("[.!?](?=\\s)[^.!?]*$").find(cut)?.range?.first ?: -1
            return if (dot > max * 0.5) cut.substring(0, dot + 1) else "${cut.substring(0, max - 1).replace(Regex("[\\s,;:—-]+\\S*$"), "")}…"
        }

        fun firstSentence(md: String = "") = Regex("^(.*?[.!?])(\\s|$)").find(plain(md))?.groupValues?.get(1) ?: ""

        fun wordClip(text: String, max: Int): String {
            val t = text.replace(Regex("\\s+"), " ").trim()
            if (t.length <= max) return t
            return "${t.substring(0, max - 1).replace(Regex("\\s+\\S*$"), "").replace(Regex("[\\s,;:—-]+$"), "")}…"
        }

        fun productTitle(t: Project): String {
            PRODUCT_SEO[t.id]?.let { return it.title }
            val lead = "${t.title}: ${t.tagline.removeSuffix(".").split(" — ")[0]}"
            return if (lead.length + BRAND.length + 3 <= 60) "$lead | $BRAND" else wordClip(lead, 60)
        }

        fun productDescription(t: Project, overviewMd: String?): String {
            PRODUCT_SEO[t.id]?.let { return it.description }
            val more = firstSentence(overviewMd ?: "")
            return if (more.isNotEmpty() && t.tagline.length + 1 + more.length <= 155) "${t.tagline} $more" else clip(t.tagline, 155)
        }

        fun docsIndexTitle(product: String) = "$product docs: guides, commands and config | $BRAND"
        fun docsIndexDescription(product: String, tagline: String, pages: Int, groups: List<String>) =
            wordClip("$product documentation: ${tagline.removeSuffix(".")}. $pages pages: ${groups.joinToString(", ")}.", 155)

        fun documentationTitle(title: String, product: String, file: String?): String {
            val language = file?.let { Regex("^(ru|uk)/").find(it)?.groupValues?.get(1) }
            val tail = " · $product docs${language?.let { " ($it)" } ?: ""}"
            val doc = plain(title)
            val full = "$doc$tail | $BRAND"
            if (full.length <= 60) return full
            if (doc.length + tail.length <= 60) return "$doc$tail"
            return "${wordClip(doc, 60 - tail.length)}$tail"
        }

        fun documentationDescription(title: String, product: String, summary: String, tagline: String, shared: Boolean): String {
            val own = plain(summary)
            val fallback = "${plain(title)} in the $product docs: ${tagline.removeSuffix(".")}."
            val text = when { own.isEmpty() -> fallback; shared -> "${plain(title)}: $own"; own.length < 50 -> "$own $fallback"; else -> own }
            return wordClip(text, 155)
        }
    }
}
