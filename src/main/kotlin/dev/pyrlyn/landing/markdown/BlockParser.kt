package dev.pyrlyn.landing.markdown

/**
 * Line-based block parser for GitHub-flavoured Markdown: ATX/setext headings, fenced and indented
 * code, lists (nested, loose/tight, task items), block quotes, pipe tables, HTML blocks, rules and
 * paragraphs. Link reference definitions are collected into [refs].
 */
class BlockParser(val refs: MutableMap<String, Pair<String, String?>> = mutableMapOf()) {
    private val fenceRe = Regex("^ {0,3}(`{3,}|~{3,})\\s*([^`]*)$")
    private val headingRe = Regex("^ {0,3}(#{1,6})(?:[ \\t]+(.*?))?(?:[ \\t]+#+)?[ \\t]*$")
    private val ruleRe = Regex("^ {0,3}((?:-[ \\t]*){3,}|(?:\\*[ \\t]*){3,}|(?:_[ \\t]*){3,})$")
    private val listRe = Regex("^( {0,3})([*+-]|\\d{1,9}[.)])([ \\t]+|$)")
    private val quoteRe = Regex("^ {0,3}> ?")
    private val htmlRe = Regex("^ {0,3}(?:<!--|<\\?|<![A-Za-z]|<!\\[CDATA\\[|</?[A-Za-z][\\w-]*(?:\\s|/?>|$))")
    private val tableDelimRe = Regex("^ {0,3}\\|?\\s*:?-+:?\\s*(?:\\|\\s*:?-+:?\\s*)*\\|?\\s*$")
    private val refRe = Regex("^ {0,3}\\[([^\\]]+)\\]:\\s*<?(\\S+?)>?(?:\\s+[\"'(](.*)[\"')])?\\s*$")
    private val setextRe = Regex("^ {0,3}(=+|-+)\\s*$")

    fun parse(src: String): List<Block> = parseLines(src.replace("\r\n", "\n").replace("\t", "    ").split("\n"))

    private fun isBlank(s: String) = s.isBlank()

    /** Does [line] start a block that interrupts a paragraph? */
    private fun interrupts(line: String): Boolean =
        fenceRe.containsMatchIn(line) || headingRe.matches(line) || ruleRe.matches(line) || quoteRe.containsMatchIn(line) ||
            htmlRe.containsMatchIn(line) ||
            listRe.find(line)?.let { m -> val mk = m.groupValues[2]; m.groupValues[3].isNotEmpty() && line.substring(m.range.last + 1).isNotBlank() && (mk.length == 1 || mk.startsWith("1")) } == true

    fun parseLines(lines: List<String>): List<Block> {
        val out = mutableListOf<Block>()
        var i = 0
        while (i < lines.size) {
            val line = lines[i]
            if (isBlank(line)) { i++; continue }

            fenceRe.find(line)?.let { m ->
                val fence = m.groupValues[1]
                val indent = line.indexOfFirst { it != ' ' }
                val body = mutableListOf<String>()
                i++
                while (i < lines.size) {
                    val l = lines[i]
                    val t = l.trimStart()
                    if (t.startsWith(fence.substring(0, 1).repeat(fence.length)) && t.trimEnd().all { it == fence[0] } && l.length - t.length < 4) { i++; break }
                    body += if (indent > 0) l.removePrefix(" ".repeat(minOf(indent, l.length - l.trimStart().length))) else l
                    i++
                }
                out += Block.Code(body.joinToString("\n"), m.groupValues[2].trim())
                return@let
            }?.let { continue }

            if (line.startsWith("    ")) {
                val body = mutableListOf<String>()
                while (i < lines.size && (lines[i].startsWith("    ") || isBlank(lines[i]))) { body += lines[i].removePrefix("    "); i++ }
                while (body.isNotEmpty() && body.last().isBlank()) body.removeAt(body.lastIndex)
                out += Block.Code(body.joinToString("\n"), "")
                continue
            }

            headingRe.matchEntire(line)?.let { m ->
                out += Block.Heading(m.groupValues[1].length, m.groupValues[2].trim()); i++
            }?.let { continue }

            if (ruleRe.matches(line)) { out += Block.Rule; i++; continue }

            if (quoteRe.containsMatchIn(line)) {
                val body = mutableListOf<String>()
                while (i < lines.size) {
                    val l = lines[i]
                    if (quoteRe.containsMatchIn(l)) body += l.replaceFirst(quoteRe, "")
                    else if (!isBlank(l) && body.isNotEmpty() && !isBlank(body.last()) && !interrupts(l)) body += l
                    else break
                    i++
                }
                out += Block.Quote(BlockParser(refs).parseLines(body))
                continue
            }

            listRe.find(line)?.let { first ->
                val (blk, next) = parseList(lines, i, first)
                out += blk; i = next
            }?.let { continue }

            if (htmlRe.containsMatchIn(line)) {
                val body = mutableListOf<String>()
                if (line.trimStart().startsWith("<!--")) {
                    while (i < lines.size) { body += lines[i]; i++; if (body.last().contains("-->")) break }
                } else {
                    while (i < lines.size && !isBlank(lines[i])) { body += lines[i]; i++ }
                }
                out += Block.Html(body.joinToString("\n"))
                continue
            }

            if (line.contains('|') && i + 1 < lines.size && tableDelimRe.matches(lines[i + 1]) && lines[i + 1].contains('-')) {
                val header = cells(line)
                val aligns = cells(lines[i + 1]).map { c ->
                    val t = c.trim()
                    when { t.startsWith(":") && t.endsWith(":") -> "center"; t.endsWith(":") -> "right"; t.startsWith(":") -> "left"; else -> null }
                }
                if (aligns.size == header.size) {
                    i += 2
                    val rows = mutableListOf<List<String>>()
                    while (i < lines.size && !isBlank(lines[i]) && !interrupts(lines[i])) {
                        val r = cells(lines[i])
                        rows += List(header.size) { k -> r.getOrElse(k) { "" } }
                        i++
                    }
                    out += Block.Table(header, aligns, rows)
                    continue
                }
            }

            refRe.matchEntire(line)?.let { m ->
                refs.putIfAbsent(m.groupValues[1].lowercase().trim(), m.groupValues[2] to m.groups[3]?.value)
                i++
            }?.let { continue }

            // Paragraph (with setext heading detection).
            val para = mutableListOf(line.trim())
            i++
            var setext: Int? = null
            while (i < lines.size && !isBlank(lines[i])) {
                val l = lines[i]
                setextRe.matchEntire(l)?.let { m -> setext = if (m.groupValues[1].startsWith("=")) 1 else 2 }
                if (setext != null) { i++; break }
                if (interrupts(l)) break
                para += l.trimStart()
                i++
            }
            val text = para.joinToString("\n").trimEnd()
            out += if (setext != null) Block.Heading(setext!!, text) else Block.Paragraph(text)
        }
        return out
    }

    /** Split a pipe-table row into cells, honouring escaped pipes and code spans. */
    private fun cells(row: String): List<String> {
        var s = row.trim()
        if (s.startsWith("|")) s = s.substring(1)
        if (s.endsWith("|") && !s.endsWith("\\|")) s = s.dropLast(1)
        val out = mutableListOf<String>()
        val cur = StringBuilder()
        var k = 0
        var inCode = false
        while (k < s.length) {
            val c = s[k]
            when {
                c == '\\' && k + 1 < s.length && s[k + 1] == '|' -> { cur.append('|'); k++ }
                c == '`' -> { inCode = !inCode; cur.append(c) }
                c == '|' && !inCode -> { out += cur.toString().trim(); cur.clear() }
                else -> cur.append(c)
            }
            k++
        }
        out += cur.toString().trim()
        return out
    }

    private fun parseList(lines: List<String>, start: Int, first: MatchResult): Pair<Block, Int> {
        val marker = first.groupValues[2]
        val ordered = marker.first().isDigit()
        val bullet = if (ordered) marker.last() else marker.first()
        val items = mutableListOf<Block.Item>()
        var loose = false
        var i = start
        while (i < lines.size) {
            val m = listRe.find(lines[i]) ?: break
            val mk = m.groupValues[2]
            if (mk.first().isDigit() != ordered || (if (ordered) mk.last() else mk.first()) != bullet) break
            val spaces = m.groupValues[3].length
            val contentIndent = m.groupValues[1].length + mk.length + (if (spaces in 1..4) spaces else 1)
            val body = mutableListOf(lines[i].substring(minOf(lines[i].length, contentIndent)))
            i++
            var sawBlank = false
            while (i < lines.size) {
                val l = lines[i]
                if (isBlank(l)) { body += ""; sawBlank = true; i++; continue }
                val ind = l.length - l.trimStart().length
                if (ind >= contentIndent) { body += l.substring(contentIndent); sawBlank = false; i++; continue }
                if (!sawBlank && listRe.find(l) == null && !interrupts(l)) { body += l.trimStart(); i++; continue }
                break
            }
            // Trailing blanks belong between items.
            var trailing = 0
            while (body.isNotEmpty() && body.last().isBlank()) { body.removeAt(body.lastIndex); trailing++ }
            val nextIsItem = i < lines.size && listRe.find(lines[i])?.let { n ->
                val nmk = n.groupValues[2]; nmk.first().isDigit() == ordered && (if (ordered) nmk.last() else nmk.first()) == bullet
            } == true
            if (trailing > 0 && nextIsItem) loose = true
            // A blank line between two blocks inside one item makes the list loose too.
            val inner = body.dropWhile { it.isBlank() }
            if (inner.zipWithNext().any { (a, b) -> a.isBlank() && b.isNotBlank() } &&
                inner.indexOfFirst { it.isBlank() }.let { idx -> idx > 0 && inner.subList(idx, inner.size).any { it.isNotBlank() && !it.startsWith(" ") && listRe.find(it) == null } }) loose = true
            var task: Boolean? = null
            if (body.isNotEmpty()) {
                Regex("^\\[([ xX])\\][ \\t]+").find(body[0])?.let { t -> task = t.groupValues[1] != " "; body[0] = body[0].substring(t.range.last + 1) }
            }
            items += Block.Item(BlockParser(refs).parseLines(body), task)
            if (trailing > 0 && !nextIsItem) break
        }
        return Block.ListBlock(ordered, if (ordered) marker.dropLast(1).toInt() else 1, items, loose) to i
    }
}
