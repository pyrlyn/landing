package dev.pyrlyn.landing.content

import dev.pyrlyn.landing.markdown.Block
import dev.pyrlyn.landing.markdown.BlockParser
import dev.pyrlyn.landing.markdown.Esc
import dev.pyrlyn.landing.markdown.Renderer
import dev.pyrlyn.landing.markdown.SafeHtml

/** Turns a project Markdown body into the structured pieces the page templates render (port of src/lib/sections.ts). */
object Sections {
    const val COPY_ICON = "<svg viewBox=\"0 0 16 16\" width=\"14\" height=\"14\" aria-hidden=\"true\"><rect x=\"5\" y=\"5\" width=\"8.5\" height=\"8.5\" rx=\"2\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.5\"/><path d=\"M3 10.5V4a1.5 1.5 0 0 1 1.5-1.5H10\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.5\" stroke-linecap=\"round\"/></svg>"

    /** Code block with an accessible copy button (the button stays hidden until JS enables it). */
    fun codeBlock(text: String, lang: String = "bash", label: String = "command"): String {
        val src = text.removeSuffix("\n")
        val lines = src.split("\n").map { line ->
            val m = Regex("^(.*?)(\\s+#\\s.*)$").find(line)
            val body = if (m != null) "${Esc.attr(m.groupValues[1])}<span class=\"tok-c\">${Esc.attr(m.groupValues[2])}</span>" else Esc.attr(line)
            if (lang == "text") body else "<span class=\"tok-p\" aria-hidden=\"true\">$ </span>$body"
        }
        return """<div class="codeblock">
  <pre class="code" tabindex="0" role="group" aria-label="${Esc.attr(label)}"><code data-lang="${Esc.attr(lang)}">${lines.joinToString("\n")}</code></pre>
  <button class="copy" type="button" data-copy="${Esc.attr(src)}" hidden>
    $COPY_ICON
    <span class="copy__label">Copy</span><span class="sr-only"> ${Esc.attr(label)}</span>
  </button>
</div>"""
    }

    /** The site renderer for product copy: copyable code blocks, safe links/images/HTML. */
    private object SiteRenderer : Renderer() {
        override fun code(text: String, lang: String) = codeBlock(text, if (lang == "powershell") "powershell" else lang.ifEmpty { "bash" })
        override fun link(href: String, title: String?, text: String): String {
            val out = SafeHtml.href(href)
            val ext = Regex("^https?:").containsMatchIn(out)
            return "<a href=\"${Esc.attr(out)}\"${if (ext) " rel=\"noopener\"" else ""}>$text</a>"
        }
        override fun image(href: String, alt: String) = "<img src=\"${Esc.attr(SafeHtml.href(href))}\" alt=\"${Esc.attr(alt)}\" loading=\"lazy\" decoding=\"async\">"
        override fun html(raw: String) = SafeHtml.clean(raw)
    }

    fun renderMd(src: String): String = SiteRenderer.render(src)
    fun renderInline(src: String): String = SiteRenderer.renderInline(src)

    /** Split the body on H2 headings. Keys are lowercased heading text. */
    fun split(body: String): Map<String, String> {
        val clean = body.replace(Regex("<!--[\\s\\S]*?-->"), "")
        val parts = clean.split(Regex("^##\\s+", RegexOption.MULTILINE))
        return parts.drop(1).associate { part ->
            val nl = part.indexOf('\n').let { if (it < 0) part.length else it }
            part.substring(0, nl).trim().lowercase() to part.substring(nl).trim()
        }
    }

    data class Feature(val name: String, val html: String)

    /** `- **Name.** description` bullets → cards. */
    fun features(src: String?): List<Feature> {
        val items = (src ?: "").split(Regex("^\\s*-\\s+", RegexOption.MULTILINE)).map { it.trim() }.filter { it.isNotEmpty() }
        return items.map { item ->
            val text = item.replace(Regex("\\s*\\n\\s*"), " ")
            val m = Regex("^\\*\\*(.+?)\\*\\*\\s*[—–-]?\\s*(.*)$").find(text)
            val name = (m?.groupValues?.get(1) ?: text.split(".")[0]).replace(Regex("[.:]\\s*$"), "")
            Feature(Names.normalizeMdInline(name), renderInline(m?.groupValues?.get(2) ?: text))
        }
    }

    data class Usage(val caption: String, val code: String, val lang: String)

    /** Paragraph followed by a code fence → captioned usage card. */
    fun usage(src: String?): List<Usage> {
        val out = mutableListOf<Usage>()
        var caption = ""
        for (b in BlockParser().parse(src ?: "")) {
            if (b is Block.Paragraph) caption = renderInline(b.text.replace(Regex("\\s*\\n\\s*"), " "))
            if (b is Block.Code) { out += Usage(caption, b.text, b.lang.ifEmpty { "bash" }); caption = "" }
        }
        return out
    }

    data class LinkItem(val label: String, val href: String)

    /** Links bullets `Label: <url>`; the License bullet is dropped (the site renders one shared license block). */
    fun links(src: String?): List<LinkItem> = (src ?: "")
        .split(Regex("^\\s*-\\s+", RegexOption.MULTILINE))
        .map { it.trim().replace(Regex("\\s*\\n\\s*"), " ") }
        .filter { it.isNotEmpty() && !Regex("^licen[cs]e", RegexOption.IGNORE_CASE).containsMatchIn(it) }
        .mapNotNull { s ->
            val m = Regex("^(.+?):\\s*<?(https?://[^\\s>]+)>?").find(s) ?: Regex("^\\[(.+?)\\]\\((https?://[^)]+)\\)").find(s)
            m?.let { LinkItem(Names.normalizeMdInline(it.groupValues[1].trim()), it.groupValues[2]) }
        }
}
