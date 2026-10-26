package dev.pyrlyn.landing.markdown

import dev.pyrlyn.landing.content.Names

/** HTML escaping as marked does it (text and attributes). */
object Esc {
    private val entity = Regex("&(?:#\\d+|#x[0-9a-fA-F]+|[A-Za-z][A-Za-z0-9]*);")
    fun text(s: String): String {
        val sb = StringBuilder(s.length + 16)
        var i = 0
        while (i < s.length) {
            val c = s[i]
            when (c) {
                '&' -> { val m = entity.matchAt(s, i); if (m != null) { sb.append(m.value); i += m.value.length; continue } else sb.append("&amp;") }
                '<' -> sb.append("&lt;")
                '>' -> sb.append("&gt;")
                '"' -> sb.append("&quot;")
                '\'' -> sb.append("&#39;")
                else -> sb.append(c)
            }
            i++
        }
        return sb.toString()
    }

    /** The site's own `esc` (src/lib/sections.ts): & < > " only. */
    fun attr(s: String) = s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
}

/**
 * Markdown → HTML with marked-compatible output and overridable hooks (heading, code, link, image,
 * table, blockquote, html), mirroring the custom marked renderers of the Astro site.
 */
open class Renderer {
    /** Leaf text normalisation (the site's walkTokens hook: product names in prose). */
    open fun textHook(s: String): String = Names.normalize(s)

    protected var refs: Map<String, Pair<String, String?>> = emptyMap()

    fun render(src: String): String {
        val parser = BlockParser()
        val blocks = parser.parse(src)
        refs = parser.refs
        return blocks(blocks, tight = false)
    }

    fun renderInline(src: String): String = inline(InlineParser().parse(src))

    fun inline(nodes: List<Inline>): String = nodes.joinToString("") { inlineNode(it) }

    protected fun parseInline(src: String): List<Inline> = InlineParser(refs).parse(src)

    open fun inlineNode(n: Inline): String = when (n) {
        is Inline.Text -> Esc.text(textHook(n.text))
        is Inline.CodeSpan -> "<code>${Esc.text(n.text)}</code>"
        is Inline.Em -> "<em>${inline(n.children)}</em>"
        is Inline.Strong -> "<strong>${inline(n.children)}</strong>"
        is Inline.Del -> "<del>${inline(n.children)}</del>"
        is Inline.Link -> link(n.href, n.title, inline(n.children))
        is Inline.Image -> image(n.href, n.alt)
        is Inline.Html -> html(n.raw)
        Inline.Break -> "<br>"
    }

    open fun blocks(list: List<Block>, tight: Boolean): String = list.joinToString("") { block(it, tight) }

    open fun block(b: Block, tight: Boolean): String = when (b) {
        is Block.Heading -> heading(b.depth, b.text, inline(parseInline(b.text)))
        is Block.Paragraph -> if (tight) inline(parseInline(b.text)) else "<p>${inline(parseInline(b.text))}</p>\n"
        is Block.Code -> code(b.text, b.lang)
        is Block.Rule -> "<hr>\n"
        is Block.Html -> html(b.raw)
        is Block.Quote -> blockquote(blocks(b.blocks, false))
        is Block.Table -> table(b)
        is Block.ListBlock -> {
            val tag = if (b.ordered) "ol" else "ul"
            val start = if (b.ordered && b.start != 1) " start=\"${b.start}\"" else ""
            val items = b.items.joinToString("") { item ->
                val check = item.task?.let { "<input ${if (it) "checked=\"\" " else ""}disabled=\"\" type=\"checkbox\"> " } ?: ""
                val body = blocks(item.blocks, tight = !b.loose)
                "<li>$check${body.let { if (!b.loose) it.trimEnd('\n').let { s -> s } else it }}</li>\n"
            }
            "<$tag$start>\n$items</$tag>\n"
        }
    }

    open fun heading(depth: Int, raw: String, inner: String) = "<h$depth>$inner</h$depth>\n"
    open fun code(text: String, lang: String) = "<pre><code${if (lang.isNotEmpty()) " class=\"language-${Esc.text(lang)}\"" else ""}>${Esc.text(text)}\n</code></pre>\n"
    open fun link(href: String, title: String?, text: String) = "<a href=\"${Esc.text(href)}\"${title?.let { " title=\"${Esc.text(it)}\"" } ?: ""}>$text</a>"
    open fun image(href: String, alt: String) = "<img src=\"${Esc.text(href)}\" alt=\"${Esc.text(alt)}\">"
    open fun html(raw: String) = raw
    open fun blockquote(inner: String) = "<blockquote>\n$inner</blockquote>\n"
    open fun table(t: Block.Table): String {
        fun cell(tag: String, c: String, a: String?) = "<$tag${a?.let { " align=\"$it\"" } ?: ""}>${inline(parseInline(c))}</$tag>\n"
        val head = t.header.mapIndexed { k, c -> cell("th", c, t.aligns.getOrNull(k)) }.joinToString("")
        val rows = t.rows.joinToString("") { r -> "<tr>\n" + r.mapIndexed { k, c -> cell("td", c, t.aligns.getOrNull(k)) }.joinToString("") + "</tr>\n" }
        return "<table>\n<thead>\n<tr>\n$head</tr>\n</thead>\n${if (rows.isNotEmpty()) "<tbody>$rows</tbody>" else ""}</table>\n"
    }

    /** Cell inline HTML for custom table renderers. */
    protected fun cellHtml(src: String) = inline(parseInline(src))
}
