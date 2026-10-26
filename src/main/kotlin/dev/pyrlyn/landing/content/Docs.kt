package dev.pyrlyn.landing.content

import dev.pyrlyn.landing.config.SiteConfig
import dev.pyrlyn.landing.markdown.Esc
import dev.pyrlyn.landing.markdown.MarkdownRenderer
import dev.pyrlyn.landing.markdown.Table
import dev.pyrlyn.landing.markdown.SafeHtml
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.net.URLDecoder

/**
 * Product documentation pages built from content/docs/<product>/ — a copy of each product repo's
 * docs/ folder kept up to date by its sync workflow (port of src/lib/docs.ts). Navigation, titles
 * and exclusions: data/docs-nav.json.
 */
class Docs(private val root: File, private val cfg: SiteConfig) {
    @Serializable data class Source(val repo: String, val sha: String, val ref: String)
    data class TocEntry(val id: String, val text: String, val depth: Int)
    data class Page(
        val product: String, val slug: String, val title: String, val navTitle: String, val group: String,
        /** Path inside docs/, or null for a site-authored page. */
        val file: String?, var summary: String, val html: String, val toc: List<TocEntry>, val sourceUrl: String?,
    )
    data class Group(val label: String, val pages: List<Page>)
    data class ProductDocs(val product: String, val source: Source?, val pages: List<Page>, val groups: List<Group>, val excluded: List<Pair<String, String>>)

    /** Raw Markdown of every synced doc, keyed `<product>/<path>.md`. */
    val raw: Map<String, String> = root.walkTopDown().filter { it.isFile && it.extension == "md" && it.parentFile != root }
        .associate { it.relativeTo(root).invariantSeparatorsPath to it.readText() }

    private val cache = mutableMapOf<String, ProductDocs>()

    fun productsWithDocs(): List<String> = Data.docsNav.keys.filter { p ->
        raw.keys.any { it.startsWith("$p/") } || Data.docsNav.getValue(p).groups.any { g -> g.items.any { it.page != null } }
    }

    fun get(product: String): ProductDocs = cache.getOrPut(product) { build(product) }

    fun href(product: String, slug: String? = null) = cfg.url(if (slug != null) "/$product/docs/$slug/" else "/$product/docs/")

    private fun build(product: String): ProductDocs {
        val nav = Data.docsNav[product] ?: ProductNav(emptyList())
        val prefix = "$product/"
        val files = raw.keys.filter { it.startsWith(prefix) }.map { it.removePrefix(prefix) }.filter { !isServiceDoc(it) }.sorted()
        val source = File(root, "$product/_source.json").takeIf { it.exists() }?.let { Json { ignoreUnknownKeys = true }.decodeFromString<Source>(it.readText()) }
        val excluded = mutableListOf<Pair<String, String>>()
        val listed = nav.groups.flatMap { g -> g.items.mapNotNull { it.file } }.toSet()
        val pageFiles = linkedSetOf<String>()
        for (f in files) {
            val reason = isExcluded(f, nav.exclude)
            if (reason != null && f !in listed) excluded += f to reason else pageFiles += f
        }
        val fileToSlug = pageFiles.associateWith { slugOf(it) }
        val groups = mutableListOf<Group>()
        val pages = mutableListOf<Page>()
        fun addGroup(label: String, items: List<NavItem>) {
            val gp = mutableListOf<Page>()
            for (it in items) {
                val page = when {
                    it.page != null -> Page(product, it.page, it.title ?: it.page, it.title ?: it.page, label, null, it.summary ?: "", "", emptyList(), null)
                    it.file != null && it.file in pageFiles -> renderDoc(product, it.file, raw.getValue(prefix + it.file), label, it.title, fileToSlug, source)
                        .also { p -> it.summary?.let { s -> p.summary = s } }
                    else -> null
                }
                if (page != null) { gp += page; pages += page }
            }
            if (gp.isNotEmpty()) groups += Group(label, gp)
        }
        nav.groups.forEach { addGroup(it.label, it.items) }
        val unplaced = pageFiles.filter { it !in listed }.map { NavItem(file = it) }
        if (unplaced.isNotEmpty()) addGroup("More", unplaced)
        return ProductDocs(product, source, pages, groups, excluded)
    }

    private fun renderDoc(product: String, file: String, raw: String, group: String, navTitle: String?, fileToSlug: Map<String, String>, source: Source?): Page {
        val dir = if (file.contains('/')) file.substringBeforeLast('/') else ""
        val repo = source?.repo ?: "pyrlyn/$product"
        val ref = source?.sha ?: "main"
        val (h1, rest) = docSource(raw)
        val title = h1?.let { plain(Names.normalizeMdInline(it)) } ?: navTitle ?: file
        val slugger = slugger()
        val toc = mutableListOf<TocEntry>()
        var section = title
        val tableNames = mutableMapOf<String, Int>()
        fun tableName(): String {
            val n = (tableNames[section] ?: 0) + 1
            tableNames[section] = n
            return if (n > 1) "$section table $n" else "$section table"
        }
        fun resolveHref(rawHref: String): String {
            val h = SafeHtml.href(rawHref)
            if (Regex("^(https?:|mailto:|#)").containsMatchIn(h)) return h
            val pathPart = h.substringBefore('#')
            val hash = if (h.contains('#')) h.substringAfter('#') else ""
            val target = posixJoin(dir, runCatching { URLDecoder.decode(pathPart.replace("+", "%2B"), "UTF-8") }.getOrDefault(pathPart))
            val anchor = if (hash.isNotEmpty()) "#$hash" else ""
            val docTarget = target.trimEnd('/')
            fileToSlug[docTarget]?.let { return cfg.url("/$product/docs/$it/") + anchor }
            val repoPath = if (target.startsWith("..")) target else posixJoin("docs", target)
            val clean = posixJoin("", repoPath)
            val kind = if (pathPart.endsWith("/") || !Regex("\\.[a-z0-9]+$", RegexOption.IGNORE_CASE).containsMatchIn(pathPart)) "tree" else "blob"
            return "https://github.com/$repo/$kind/$ref/$clean$anchor"
        }
        val r = object : MarkdownRenderer() {
            override fun heading(depth: Int, raw: String, inner: String): String {
                val id = slugger(raw)
                val d = depth.coerceIn(2, 6)
                if (d == 2 || d == 3) toc += TocEntry(id, plain(Names.normalizeMdInline(raw)), d)
                section = plain(raw)
                return "<h$d id=\"${Esc.attr(id)}\" class=\"doc-h\"><a class=\"doc-anchor\" href=\"#${Esc.attr(id)}\" aria-label=\"Link to this section\"></a>$inner</h$d>\n"
            }
            override fun code(text: String, lang: String) = docCodeBlock(text, lang.split(Regex("\\s"))[0])
            override fun link(href: String, title: String?, text: String): String {
                val out = resolveHref(href)
                return "<a href=\"${Esc.attr(out)}\"${if (Regex("^https?:").containsMatchIn(out)) " rel=\"noopener\"" else ""}>$text</a>"
            }
            override fun image(href: String, alt: String): String {
                if (SafeHtml.href(href) != href) return Esc.attr(alt)
                val src = if (Regex("^https?:").containsMatchIn(href)) href else "https://raw.githubusercontent.com/$repo/$ref/${posixJoin("docs/$dir", href)}"
                return "<img src=\"${Esc.attr(src)}\" alt=\"${Esc.attr(alt)}\" loading=\"lazy\" decoding=\"async\">"
            }
            override fun table(t: Table): String {
                fun al(a: String?) = a?.let { " style=\"text-align:$it\"" } ?: ""
                val head = t.header.mapIndexed { k, c -> "<th${al(t.aligns.getOrNull(k))}>$c</th>" }.joinToString("")
                val rows = t.rows.joinToString("") { row -> "<tr>" + row.mapIndexed { k, c -> "<td${al(t.aligns.getOrNull(k))}>$c</td>" }.joinToString("") + "</tr>" }
                return "<div class=\"table-wrap\" tabindex=\"0\" role=\"region\" aria-label=\"${Esc.attr(tableName())}\"><table><thead><tr>$head</tr></thead><tbody>$rows</tbody></table></div>"
            }
            override fun html(raw: String) = SafeHtml.clean(raw)
            override fun blockquote(inner: String): String {
                var body = inner
                val m = Regex("^<p>\\[!(NOTE|TIP|IMPORTANT|WARNING|CAUTION)\\]\\s*", RegexOption.IGNORE_CASE).find(body)
                val kind = m?.groupValues?.get(1)?.lowercase() ?: "note"
                if (m != null) body = body.replaceFirst(m.value, "<p>")
                val label = m?.groupValues?.get(1)?.let { it[0] + it.substring(1).lowercase() } ?: ""
                return "<aside class=\"callout callout--$kind\">${if (label.isNotEmpty()) "<p class=\"callout__label\">$label</p>" else ""}$body</aside>"
            }
        }
        val parsed = r.render(rest)
        // An in-page link whose target is not on the page renders as plain text instead of a dead link.
        val ids = Regex("\\sid=\"([^\"]+)\"").findAll(parsed).map { it.groupValues[1] }.toSet()
        val html = Regex("<a href=\"#([^\"]+)\">([\\s\\S]*?)</a>").replace(parsed) { m -> if (m.groupValues[1] in ids) m.value else m.groupValues[2] }
        return Page(product, fileToSlug.getValue(file), title, navTitle ?: title, group, file, summarize(rest), html, toc,
            "https://github.com/$repo/blob/${source?.ref ?: "main"}/docs/$file")
    }

    companion object {
        private val SERVICE_DOC = Regex("^(?:(?:readme|notes|ideas|claude|agency)|(?:plan|todo|done|roadmap|research|next)[^/]*)\\.md$", RegexOption.IGNORE_CASE)
        fun isServiceDoc(file: String): Boolean { val name = file.substringAfterLast('/'); return name == "AGENTS.md" || SERVICE_DOC.matches(name) }

        fun isExcluded(file: String, exclude: Map<String, String>): String? {
            for ((pattern, reason) in exclude) {
                val hit = if (pattern.endsWith("/")) file.startsWith(pattern) else if (pattern.endsWith(".md")) file == pattern else file.startsWith(pattern)
                if (hit) return reason
            }
            return null
        }

        /** `design/loop.md` → `design-loop`, `COMMANDS.md` → `commands`. */
        fun slugOf(file: String) = file.replace(Regex("\\.md$", RegexOption.IGNORE_CASE), "").lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-')

        /** GitHub-style heading ids. */
        fun slugger(): (String) -> String {
            val seen = mutableMapOf<String, Int>()
            return { text ->
                val base = Regex("(`+[^`]*`+)").split(text).let { _ ->
                    val parts = mutableListOf<String>()
                    var last = 0
                    for (m in Regex("`+[^`]*`+").findAll(text)) { parts += text.substring(last, m.range.first).replace(Regex("<[^>]+>"), ""); parts += m.value.replace("`", ""); last = m.range.last + 1 }
                    parts += text.substring(last).replace(Regex("<[^>]+>"), "")
                    parts.joinToString("")
                }.lowercase().replace(Regex("[^\\p{L}\\p{N}\\s_-]"), "").trim().replace(Regex("\\s"), "-")
                val n = seen[base] ?: 0
                seen[base] = n + 1
                if (n > 0) "$base-$n" else base
            }
        }

        fun posixJoin(dir: String, rel: String): String {
            val out = if (dir.isEmpty()) mutableListOf() else dir.split("/").toMutableList()
            for (part in rel.split("/")) when (part) {
                "", "." -> {}
                ".." -> if (out.isNotEmpty()) out.removeAt(out.lastIndex)
                else -> out += part
            }
            return out.joinToString("/")
        }

        private val SHELL = setOf("bash", "sh", "shell", "zsh", "console", "powershell", "ps1", "pwsh")

        /** Docs code block: shell prompts only for shell languages, a language tag, and the shared copy button. */
        fun docCodeBlock(text: String, lang: String = ""): String {
            val shell = lang in SHELL
            val src = text.removeSuffix("\n")
            val lines = src.split("\n").map { line ->
                when {
                    !shell -> Esc.attr(line)
                    Regex("^\\s*#").containsMatchIn(line) -> "<span class=\"tok-c\">${Esc.attr(line)}</span>"
                    else -> Regex("^(.*?)(\\s+#\\s.*)$").find(line)?.let { "${Esc.attr(it.groupValues[1])}<span class=\"tok-c\">${Esc.attr(it.groupValues[2])}</span>" } ?: Esc.attr(line)
                }
            }
            val label = if (lang.isNotEmpty()) "$lang code" else "code"
            return """<div class="codeblock codeblock--doc"${if (lang.isNotEmpty()) " data-lang=\"${Esc.attr(lang)}\"" else ""}>
  <pre class="code" tabindex="0" role="group" aria-label="${Esc.attr(label)}"><code>${lines.joinToString("\n")}</code></pre>
  <button class="copy" type="button" data-copy="${Esc.attr(src)}" hidden>
    ${Sections.COPY_ICON}
    <span class="copy__label">Copy</span><span class="sr-only"> ${Esc.attr(label)}</span>
  </button>
</div>"""
        }

        fun plain(md: String) = md
            .replace(Regex("```[\\s\\S]*?```"), "")
            .replace(Regex("!\\[[^\\]]*\\]\\([^)]*\\)"), "")
            .replace(Regex("\\[([^\\]]+)\\]\\([^)]*\\)"), "$1")
            .replace(Regex("^\\s*>\\s?", RegexOption.MULTILINE), "")
            .replace(Regex("[*`]"), "")
            .replace(Regex("\\s+"), " ")
            .trim()

        /** First prose paragraph, cut at a sentence near 220 chars. */
        fun summarize(body: String): String {
            val blocks = body.replace(Regex("<!--[\\s\\S]*?-->"), "").replace(Regex("```[\\s\\S]*?```"), "\n\n").split(Regex("\\n\\s*\\n"))
            val para = blocks.map { it.trim() }.find { it.isNotEmpty() && !Regex("^(#|\\||-|\\*|\\d+\\.|>|<)").containsMatchIn(it) } ?: return ""
            val text = plain(Names.normalizeMdInline(para)).replace(Regex(":$"), ".")
            if (text.length <= 220) return text
            val cut = text.substring(0, 220)
            val dot = Regex("[.!?](?=\\s)[^.!?]*$").find(cut)?.range?.first ?: -1
            return if (dot > 80) cut.substring(0, dot + 1) else "${cut.replace(Regex("\\s+\\S*$"), "")}…"
        }

        /** Strip front matter and the first H1 (which becomes the page title). */
        fun docSource(raw: String): Pair<String?, String> {
            val body = raw.replaceFirst(Regex("^---\\n[\\s\\S]*?\\n---\\n"), "")
            val h1 = Regex("^#\\s+(.+)$", RegexOption.MULTILINE).find(body)
            return h1?.groupValues?.get(1) to (if (h1 != null) body.replaceFirst(h1.value, "") else body)
        }
    }
}
