package dev.pyrlyn.landing.markdown

/** Inline Markdown: escapes, code spans, emphasis, strikethrough, links, images, autolinks, raw HTML, breaks. */
class InlineParser(private val refs: Map<String, Pair<String, String?>> = emptyMap()) {
    private val punct = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~"
    private val autolinkRe = Regex("^<([A-Za-z][A-Za-z0-9+.-]{1,31}:[^\\s<>]*)>")
    private val emailRe = Regex("^<([A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?(?:\\.[A-Za-z0-9-]+)+)>")
    private val tagRe = Regex("^(?:<!--[\\s\\S]*?-->|</[A-Za-z][\\w-]*\\s*>|<[A-Za-z][\\w-]*(?:\\s+[A-Za-z_:][\\w.:-]*(?:\\s*=\\s*(?:\"[^\"]*\"|'[^']*'|[^\\s\"'=<>`]+))?)*\\s*/?>)")
    private val urlRe = Regex("^(?:https?://|www\\.)[^\\s<]*[^\\s<?!.,:*_~)'\"]")

    fun parse(src: String): List<Inline> {
        val out = mutableListOf<Inline>()
        val text = StringBuilder()
        fun flush() { if (text.isNotEmpty()) { out += Inline.Text(text.toString()); text.clear() } }
        var i = 0
        while (i < src.length) {
            val c = src[i]
            val rest = src.substring(i)
            when {
                c == '\\' && i + 1 < src.length && src[i + 1] == '\n' -> { flush(); out += Inline.Break; i += 2 }
                c == '\\' && i + 1 < src.length && src[i + 1] in punct -> { text.append(src[i + 1]); i += 2 }
                c == '\n' && text.endsWith("  ") -> { while (text.endsWith(" ")) text.setLength(text.length - 1); flush(); out += Inline.Break; i++ }
                c == '`' -> {
                    val run = rest.takeWhile { it == '`' }
                    val end = findCodeEnd(src, i + run.length, run.length)
                    if (end < 0) { text.append(run); i += run.length } else {
                        var code = src.substring(i + run.length, end).replace('\n', ' ')
                        if (code.length > 2 && code.startsWith(" ") && code.endsWith(" ") && code.isNotBlank()) code = code.substring(1, code.length - 1)
                        flush(); out += Inline.CodeSpan(code); i = end + run.length
                    }
                }
                c == '!' && rest.startsWith("![") -> {
                    val link = parseLink(src, i + 1)
                    if (link != null) { flush(); out += Inline.Image(link.href, plainText(parse(link.label))); i = link.end } else { text.append(c); i++ }
                }
                c == '[' -> {
                    val link = parseLink(src, i)
                    if (link != null) { flush(); out += Inline.Link(link.href, link.title, parse(link.label)); i = link.end } else { text.append(c); i++ }
                }
                c == '<' -> {
                    val a = autolinkRe.find(rest) ?: emailRe.find(rest)
                    val t = if (a == null) tagRe.find(rest) else null
                    when {
                        a != null -> { flush(); val v = a.groupValues[1]; out += Inline.Link(if (a.value.contains('@') && !v.contains(':')) "mailto:$v" else v, null, listOf(Inline.Text(v))); i += a.value.length }
                        t != null -> { flush(); out += Inline.Html(t.value); i += t.value.length }
                        else -> { text.append(c); i++ }
                    }
                }
                (c == 'h' || c == 'w') && (i == 0 || !src[i - 1].isLetterOrDigit()) && urlRe.find(rest) != null -> {
                    var u = urlRe.find(rest)!!.value
                    // Balance a trailing ")" as GFM does.
                    while (u.endsWith(")") && u.count { it == '(' } < u.count { it == ')' }) u = u.dropLast(1)
                    flush(); out += Inline.Link(if (u.startsWith("www.")) "http://$u" else u, null, listOf(Inline.Text(u))); i += u.length
                }
                c == '*' || c == '_' || c == '~' -> {
                    val r = parseEmphasis(src, i)
                    if (r != null) { flush(); out += r.first; i = r.second } else {
                        val run = rest.takeWhile { it == c }; text.append(run); i += run.length
                    }
                }
                else -> { text.append(c); i++ }
            }
        }
        flush()
        return out
    }

    private fun findCodeEnd(s: String, from: Int, n: Int): Int {
        var k = from
        while (k < s.length) {
            if (s[k] == '`') {
                val run = s.substring(k).takeWhile { it == '`' }.length
                if (run == n) return k
                k += run
            } else k++
        }
        return -1
    }

    private data class LinkMatch(val label: String, val href: String, val title: String?, val end: Int)

    private fun parseLink(s: String, open: Int): LinkMatch? {
        // Matching ] with nesting, skipping code spans.
        var depth = 0
        var k = open
        var close = -1
        while (k < s.length) {
            when (s[k]) {
                '\\' -> k++
                '`' -> { val run = s.substring(k).takeWhile { it == '`' }.length; val e = findCodeEnd(s, k + run, run); if (e > 0) k = e + run - 1 }
                '[' -> depth++
                ']' -> { depth--; if (depth == 0) { close = k; break } }
            }
            k++
        }
        if (close < 0) return null
        val label = s.substring(open + 1, close)
        if (close + 1 < s.length && s[close + 1] == '(') {
            var p = close + 2
            while (p < s.length && s[p] == ' ') p++
            val dest = StringBuilder()
            if (p < s.length && s[p] == '<') {
                p++
                while (p < s.length && s[p] != '>') { dest.append(s[p]); p++ }
                p++
            } else {
                var paren = 0
                while (p < s.length && !s[p].isWhitespace()) {
                    if (s[p] == '\\' && p + 1 < s.length) { dest.append(s[p + 1]); p += 2; continue }
                    if (s[p] == '(') paren++
                    if (s[p] == ')') { if (paren == 0) break; paren-- }
                    dest.append(s[p]); p++
                }
            }
            while (p < s.length && s[p].isWhitespace()) p++
            var title: String? = null
            if (p < s.length && (s[p] == '"' || s[p] == '\'')) {
                val q = s[p]; val e = s.indexOf(q, p + 1)
                if (e < 0) return null
                title = s.substring(p + 1, e); p = e + 1
                while (p < s.length && s[p].isWhitespace()) p++
            }
            if (p >= s.length || s[p] != ')') return null
            return LinkMatch(label, dest.toString(), title, p + 1)
        }
        // Reference links: [text][ref], [text][], [ref]
        val refEnd: Int
        val key: String
        if (close + 1 < s.length && s[close + 1] == '[') {
            val e = s.indexOf(']', close + 2)
            if (e < 0) return null
            key = s.substring(close + 2, e).ifEmpty { label }
            refEnd = e + 1
        } else { key = label; refEnd = close + 1 }
        val ref = refs[key.lowercase().trim()] ?: return null
        return LinkMatch(label, ref.first, ref.second, refEnd)
    }

    private fun parseEmphasis(s: String, i: Int): Pair<Inline, Int>? {
        val c = s[i]
        val run = s.substring(i).takeWhile { it == c }.length
        val before = if (i > 0) s[i - 1] else ' '
        val after = if (i + run < s.length) s[i + run] else ' '
        if (after.isWhitespace()) return null
        if (c == '_' && before.isLetterOrDigit()) return null
        if (c == '~') {
            if (run != 2) return null
            val e = s.indexOf("~~", i + 2)
            if (e < 0 || s[e - 1].isWhitespace()) return null
            return Inline.Del(parse(s.substring(i + 2, e))) to e + 2
        }
        for (n in listOf(minOf(run, 3), 2, 1).distinct().filter { it <= run }) {
            val delim = c.toString().repeat(n)
            var k = i + n
            while (k < s.length) {
                k = findDelim(s, k, delim)
                if (k < 0) break
                val prev = s[k - 1]
                val next = if (k + n < s.length) s[k + n] else ' '
                val runHere = s.substring(k).takeWhile { it == c }.length
                if (!prev.isWhitespace() && k > i + n && (c != '_' || !next.isLetterOrDigit()) && (runHere == n || n == 3 || runHere > n && n == 1 && false || runHere >= n)) {
                    val inner = s.substring(i + n, k)
                    val kids = parse(inner)
                    val node: Inline = when (n) {
                        3 -> Inline.Em(listOf(Inline.Strong(kids)))
                        2 -> Inline.Strong(kids)
                        else -> Inline.Em(kids)
                    }
                    return node to k + n
                }
                k += runHere
            }
        }
        return null
    }

    /** Next position of [delim] outside code spans and links. */
    private fun findDelim(s: String, from: Int, delim: String): Int {
        var k = from
        while (k < s.length) {
            when {
                s[k] == '\\' -> k += 2
                s[k] == '`' -> { val run = s.substring(k).takeWhile { it == '`' }.length; val e = findCodeEnd(s, k + run, run); k = if (e > 0) e + run else k + run }
                s.startsWith(delim, k) -> {
                    // For a single delimiter skip doubled runs that belong to strong emphasis.
                    val run = s.substring(k).takeWhile { it == delim[0] }.length
                    if (delim.length == 1 && run == 2) { k += 2; continue }
                    return k
                }
                else -> k++
            }
        }
        return -1
    }

    companion object {
        fun plainText(nodes: List<Inline>): String = nodes.joinToString("") { n ->
            when (n) {
                is Inline.Text -> n.text
                is Inline.CodeSpan -> n.text
                is Inline.Em -> plainText(n.children)
                is Inline.Strong -> plainText(n.children)
                is Inline.Del -> plainText(n.children)
                is Inline.Link -> plainText(n.children)
                is Inline.Image -> n.alt
                is Inline.Html -> ""
                Inline.Break -> ""
            }
        }
    }
}
