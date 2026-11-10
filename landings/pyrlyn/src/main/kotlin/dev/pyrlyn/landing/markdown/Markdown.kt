package dev.pyrlyn.landing.markdown

import dev.pyrlyn.landing.content.Names
import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes as E
import org.intellij.markdown.MarkdownTokenTypes as T
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.impl.ListCompositeNode
import org.intellij.markdown.flavours.gfm.GFMElementTypes as GE
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.flavours.gfm.GFMTokenTypes as GT
import org.intellij.markdown.parser.MarkdownParser

/** A GFM table with cells already rendered to inline HTML. */
class Table(val header: List<String>, val aligns: List<String?>, val rows: List<List<String>>)

/** Top-level blocks for callers that need structure rather than HTML (usage cards). */
sealed interface Outline {
    /** A paragraph, as raw Markdown source. */
    data class Paragraph(val source: String) : Outline
    data class Code(val text: String, val lang: String) : Outline
}

/**
 * Build-time Markdown → HTML. Parsing is done by JetBrains markdown (GFM flavour); this class only
 * walks its AST and emits HTML through overridable hooks, so the site keeps its own output for headings,
 * code, links, images, tables, quotes and raw HTML. Never used at request time or in the browser.
 */
open class MarkdownRenderer {
    /** Leaf text normalisation (product names in prose). */
    open fun textHook(s: String): String = Names.normalize(s)

    open fun heading(depth: Int, raw: String, inner: String) = "<h$depth>$inner</h$depth>\n"
    open fun code(text: String, lang: String) = "<pre><code${if (lang.isNotEmpty()) " class=\"language-${Esc.text(lang)}\"" else ""}>${Esc.text(text)}\n</code></pre>\n"
    open fun link(href: String, title: String?, text: String) = "<a href=\"${Esc.text(href)}\"${title?.let { " title=\"${Esc.text(it)}\"" } ?: ""}>$text</a>"
    open fun image(href: String, alt: String) = "<img src=\"${Esc.text(href)}\" alt=\"${Esc.text(alt)}\">"
    open fun html(raw: String) = raw
    open fun blockquote(inner: String) = "<blockquote>\n$inner</blockquote>\n"
    open fun table(t: Table): String {
        fun cell(tag: String, c: String, a: String?) = "<$tag${a?.let { " align=\"$it\"" } ?: ""}>$c</$tag>\n"
        val head = t.header.mapIndexed { k, c -> cell("th", c, t.aligns.getOrNull(k)) }.joinToString("")
        val rows = t.rows.joinToString("") { r -> "<tr>\n" + r.mapIndexed { k, c -> cell("td", c, t.aligns.getOrNull(k)) }.joinToString("") + "</tr>\n" }
        return "<table>\n<thead>\n<tr>\n$head</tr>\n</thead>\n${if (rows.isNotEmpty()) "<tbody>$rows</tbody>" else ""}</table>\n"
    }

    /** Render a whole document. */
    fun render(src: String): String = Walk(src).run { blocks(root.children, tight = false) }

    /** Render inline Markdown (no block structure expected), like marked.parseInline. */
    fun renderInline(src: String): String = Walk(src).run {
        root.children.filter { it.type != T.EOL }.joinToString("") { if (it.type == E.PARAGRAPH) inline(it.children) else block(it, tight = true) }.trimEnd('\n')
    }

    /** Paragraphs and code blocks at the top level, in order. */
    fun outline(src: String): List<Outline> = Walk(src).run {
        root.children.mapNotNull {
            when (it.type) {
                E.PARAGRAPH -> Outline.Paragraph(text(it))
                E.CODE_FENCE -> fence(it).let { (t, l) -> Outline.Code(t, l) }
                E.CODE_BLOCK -> Outline.Code(indented(it), "")
                else -> null
            }
        }
    }

    private inner class Walk(val src: String) {
        val root: ASTNode = MarkdownParser(FLAVOUR).buildMarkdownTreeFromString(src)
        /** GFM: `\\|` inside a table cell is a literal pipe, code spans included. */
        var inCell = false
        val defs = HashMap<String, Pair<String, String?>>().also { collectDefs(root, it) }

        fun text(n: ASTNode) = src.substring(n.startOffset, n.endOffset)

        private fun collectDefs(n: ASTNode, out: MutableMap<String, Pair<String, String?>>) {
            if (n.type == E.LINK_DEFINITION) {
                val label = n.children.firstOrNull { it.type == E.LINK_LABEL }?.let { normLabel(text(it)) }
                val dest = n.children.firstOrNull { it.type == E.LINK_DESTINATION }?.let { dest(text(it)) } ?: ""
                val title = n.children.firstOrNull { it.type == E.LINK_TITLE }?.let { title(text(it)) }
                if (label != null) out.putIfAbsent(label, dest to title)
                return
            }
            n.children.forEach { collectDefs(it, out) }
        }

        fun blocks(list: List<ASTNode>, tight: Boolean): String = list.joinToString("") { block(it, tight) }

        fun block(n: ASTNode, tight: Boolean): String = when (n.type) {
            E.ATX_1, E.ATX_2, E.ATX_3, E.ATX_4, E.ATX_5, E.ATX_6 -> {
                val depth = n.type.toString().last().digitToInt()
                val content = n.children.firstOrNull { it.type == T.ATX_CONTENT }
                val raw = content?.let { text(it).trim().replace(Regex("\\s+#+\\s*$"), "") } ?: ""
                heading(depth, raw, content?.let { inline(it.children.trimWs()) } ?: "")
            }
            E.SETEXT_1, E.SETEXT_2 -> {
                val content = n.children.first { it.type == T.SETEXT_CONTENT }
                heading(if (n.type == E.SETEXT_1) 1 else 2, text(content).trim(), inline(content.children.trimWs()))
            }
            E.PARAGRAPH -> if (tight) inline(n.children) else "<p>${inline(n.children)}</p>\n"
            E.CODE_FENCE -> fence(n).let { (t, l) -> code(t, l) }
            E.CODE_BLOCK -> code(indented(n), "")
            T.HORIZONTAL_RULE -> "<hr>\n"
            E.HTML_BLOCK -> html(text(n).trimEnd('\n') + "\n").let { if (it.endsWith("\n")) it else "$it\n" }
            E.BLOCK_QUOTE -> blockquote(blocks(n.children.filter { it.type !in QUOTE_NOISE }, tight = false))
            GE.ALERT -> {
                // Keep `[!KIND]` at the start of the first paragraph; the site's blockquote hook turns it into a callout.
                val title = n.children.firstOrNull { it.type == GT.ALERT_TITLE }?.let { text(it).trim() } ?: ""
                val inner = blocks(n.children.filter { it.type !in QUOTE_NOISE && it.type != GT.ALERT_TITLE }, tight = false)
                blockquote(if (inner.startsWith("<p>")) "<p>$title " + inner.removePrefix("<p>") else "<p>$title</p>\n$inner")
            }
            E.UNORDERED_LIST, E.ORDERED_LIST -> list(n)
            GE.TABLE -> table(table(n))
            E.LINK_DEFINITION, T.EOL, T.WHITE_SPACE, T.BLOCK_QUOTE -> ""
            else -> if (n.children.isEmpty()) Esc.text(text(n)) else blocks(n.children, tight)
        }

        fun list(n: ASTNode): String {
            val ordered = n.type == E.ORDERED_LIST
            val loose = (n as? ListCompositeNode)?.loose ?: false
            val items = n.children.filter { it.type == E.LIST_ITEM }
            val start = if (ordered) items.firstOrNull()?.children?.firstOrNull { it.type == T.LIST_NUMBER }?.let { Regex("\\d+").find(text(it))?.value?.toInt() } ?: 1 else 1
            val body = items.joinToString("") { item ->
                val check = item.children.firstOrNull { it.type == GT.CHECK_BOX }?.let { c ->
                    "<input ${if (text(c).contains(Regex("\\[[xX]\\]"))) "checked=\"\" " else ""}disabled=\"\" type=\"checkbox\"> "
                } ?: ""
                val inner = blocks(item.children.filter { it.type !in ITEM_NOISE }, tight = !loose)
                "<li>$check${if (!loose) inner.trimEnd('\n') else inner}</li>\n"
            }
            val tag = if (ordered) "ol" else "ul"
            return "<$tag${if (ordered && start != 1) " start=\"$start\"" else ""}>\n$body</$tag>\n"
        }

        fun fence(n: ASTNode): Pair<String, String> {
            val lang = n.children.firstOrNull { it.type == T.FENCE_LANG }?.let { text(it).trim() } ?: ""
            val startCol = n.startOffset - (src.lastIndexOf('\n', n.startOffset - 1) + 1)
            val firstEol = n.children.indexOfFirst { it.type == T.EOL }
            val body = if (firstEol < 0) "" else n.children.drop(firstEol + 1).takeWhile { it.type != T.CODE_FENCE_END }
                .joinToString("") { if (it.type == T.EOL) "\n" else text(it) }
            val lines = body.removeSuffix("\n").split("\n").map { l -> l.drop(minOf(startCol, l.length - l.trimStart(' ').length)) }
            return lines.joinToString("\n").let { if (body.isEmpty()) "" else it } to lang
        }

        fun indented(n: ASTNode): String =
            n.children.joinToString("") { if (it.type == T.EOL) "\n" else text(it).removePrefix("    ") }.trimEnd('\n')

        fun table(n: ASTNode): Table {
            fun cells(row: ASTNode) = row.children.filter { it.type == GT.CELL }.map { inCell = true; inline(it.children).trim().also { inCell = false } }
            val sep = n.children.firstOrNull { it.type == GT.TABLE_SEPARATOR }?.let { text(it) } ?: ""
            val aligns = sep.trim().removePrefix("|").removeSuffix("|").split("|").map { s ->
                val c = s.trim()
                when { c.startsWith(":") && c.endsWith(":") -> "center"; c.endsWith(":") -> "right"; c.startsWith(":") -> "left"; else -> null }
            }
            val header = n.children.firstOrNull { it.type == GE.HEADER }?.let(::cells) ?: emptyList()
            val rows = n.children.filter { it.type == GE.ROW }.map { r -> cells(r).let { c -> List(header.size) { c.getOrElse(it) { "" } } } }
            return Table(header, aligns, rows)
        }

        /** Inline content: consecutive plain tokens are merged so the text hook sees whole phrases. */
        fun inline(nodes: List<ASTNode>): String {
            val out = StringBuilder()
            val run = StringBuilder()
            fun flush() { if (run.isNotEmpty()) { out.append(Esc.text(textHook(unescape(run.toString())))); run.clear() } }
            var afterEol = false
            for (n in nodes) {
                val t = n.type
                if (t in QUOTE_NOISE && afterEol) continue
                if (t == T.WHITE_SPACE && afterEol) continue
                afterEol = t == T.EOL
                when (t) {
                    T.EOL -> run.append('\n')
                    T.HARD_LINE_BREAK -> { flush(); out.append("<br>") }
                    E.CODE_SPAN -> { flush(); out.append("<code>").append(Esc.text(codeSpan(n))).append("</code>") }
                    E.EMPH -> { flush(); out.append("<em>").append(inline(n.children.drop(1).dropLast(1))).append("</em>") }
                    E.STRONG -> { flush(); out.append("<strong>").append(inline(n.children.drop(2).dropLast(2))).append("</strong>") }
                    GE.STRIKETHROUGH -> { flush(); out.append("<del>").append(inline(n.children.dropWhile { it.type == GT.TILDE }.dropLastWhile { it.type == GT.TILDE })).append("</del>") }
                    E.INLINE_LINK, E.FULL_REFERENCE_LINK, E.SHORT_REFERENCE_LINK -> { flush(); out.append(linkNode(n)) }
                    E.IMAGE -> { flush(); out.append(imageNode(n)) }
                    E.AUTOLINK -> { flush(); val u = text(n).removePrefix("<").removeSuffix(">"); out.append(link(u, null, Esc.text(u))) }
                    GT.GFM_AUTOLINK, T.EMAIL_AUTOLINK -> { flush(); val u = text(n); out.append(link(if (t == T.EMAIL_AUTOLINK) "mailto:$u" else u, null, Esc.text(u))) }
                    T.HTML_TAG -> { flush(); out.append(html(text(n))) }
                    else -> if (n.children.isEmpty()) run.append(text(n)) else { flush(); out.append(inline(n.children)) }
                }
            }
            flush()
            return out.toString().trimEnd('\n')
        }

        fun codeSpan(n: ASTNode): String {
            val parts = n.children.drop(1).dropLast(1)
            // Continuation lines: drop the indentation and any `>` quote markers that follow a line break.
            var lineStart = false
            val inner = parts.filter { val skip = lineStart && (it.type == T.WHITE_SPACE || it.type == T.BLOCK_QUOTE); if (!skip) lineStart = it.type == T.EOL; !skip }
                .joinToString("") { if (it.type == T.EOL) " " else text(it) }.let { if (inCell) it.replace("\\|", "|") else it }
            return if (inner.length > 2 && inner.startsWith(" ") && inner.endsWith(" ") && inner.isNotBlank()) inner.substring(1, inner.length - 1) else inner
        }

        fun linkParts(n: ASTNode): Triple<ASTNode?, String, String?>? {
            val label = n.children.firstOrNull { it.type == E.LINK_TEXT }
            return when (n.type) {
                E.INLINE_LINK -> Triple(label, n.children.firstOrNull { it.type == E.LINK_DESTINATION }?.let { dest(text(it)) } ?: "",
                    n.children.firstOrNull { it.type == E.LINK_TITLE }?.let { title(text(it)) })
                else -> {
                    val key = n.children.firstOrNull { it.type == E.LINK_LABEL } ?: label
                    val d = key?.let { defs[normLabel(text(it))] } ?: return null
                    Triple(label ?: key, d.first, d.second)
                }
            }
        }

        fun linkNode(n: ASTNode): String {
            val (label, href, title) = linkParts(n) ?: return Esc.text(textHook(text(n)))
            return link(href, title, label?.let { inline(it.children.drop(1).dropLast(1)) } ?: "")
        }

        fun imageNode(n: ASTNode): String {
            val l = n.children.firstOrNull { it.type in LINKS } ?: return Esc.text(text(n))
            val (label, href, _) = linkParts(l) ?: return Esc.text(text(n))
            return image(href, label?.let { plain(it.children.drop(1).dropLast(1)) } ?: "")
        }

        fun plain(nodes: List<ASTNode>): String = nodes.joinToString("") {
            when {
                it.type == E.CODE_SPAN -> codeSpan(it)
                it.children.isEmpty() -> unescape(text(it))
                it.type in LINKS -> plain(it.children.firstOrNull { c -> c.type == E.LINK_TEXT }?.children?.drop(1)?.dropLast(1) ?: emptyList())
                else -> plain(it.children.filter { c -> c.type != T.EMPH && c.type != GT.TILDE })
            }
        }
    }

    private companion object {
        fun List<ASTNode>.trimWs() = dropWhile { it.type == T.WHITE_SPACE }.dropLastWhile { it.type == T.WHITE_SPACE }
        val FLAVOUR = GFMFlavourDescriptor()
        val LINKS: Set<IElementType> = setOf(E.INLINE_LINK, E.FULL_REFERENCE_LINK, E.SHORT_REFERENCE_LINK)
        val QUOTE_NOISE: Set<IElementType> = setOf(T.BLOCK_QUOTE)
        val ITEM_NOISE: Set<IElementType> = setOf(T.LIST_BULLET, T.LIST_NUMBER, GT.CHECK_BOX, T.EOL, T.WHITE_SPACE)
        val escapable = Regex("\\\\([!-/:-@\\[-`{-~])")
        fun unescape(s: String) = escapable.replace(s) { it.groupValues[1] }
        fun normLabel(s: String) = s.trim().removePrefix("[").removeSuffix("]").trim().replace(Regex("\\s+"), " ").lowercase()
        fun dest(s: String) = unescape(s.trim().removePrefix("<").removeSuffix(">"))
        fun title(s: String) = unescape(s.trim().let { if (it.length >= 2) it.substring(1, it.length - 1) else it })
    }
}
