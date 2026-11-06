package dev.pyrlyn.landing.build

import java.security.MessageDigest

/**
 * Asset helpers: CSS minification, content hashing and the client-bundle guard.
 */
object AssetPipeline {
    fun hash(s: String): String = MessageDigest.getInstance("SHA-256").digest(s.toByteArray()).joinToString("") { "%02x".format(it) }.take(10)

    /** Conservative CSS minifier: comments, whitespace runs, spaces around braces/semicolons/commas. */
    fun minifyCss(css: String): String {
        val out = StringBuilder(css.length)
        var i = 0
        var quote: Char? = null
        while (i < css.length) {
            val c = css[i]
            if (quote != null) { out.append(c); if (c == '\\' && i + 1 < css.length) { out.append(css[i + 1]); i += 2; continue }; if (c == quote) quote = null; i++; continue }
            if (c == '"' || c == '\'') { quote = c; out.append(c); i++; continue }
            if (c == '/' && i + 1 < css.length && css[i + 1] == '*') { val e = css.indexOf("*/", i + 2); i = if (e < 0) css.length else e + 2; continue }
            if (c.isWhitespace()) {
                while (i < css.length && css[i].isWhitespace()) i++
                val prev = out.lastOrNull()
                val next = css.getOrNull(i)
                if (prev != null && next != null && prev !in "{};,>:" && next !in "{};,>!") out.append(' ')
                continue
            }
            if (c in "{};," && out.lastOrNull() == ' ') out.setLength(out.length - 1)
            if (c == '}' && out.lastOrNull() == ';') out.setLength(out.length - 1)
            out.append(c); i++
        }
        return out.toString().trim()
    }

    /**
     * Names of Markdown parsers/renderers; none of them may reach the client. `marked` matches only as the
     * library is called, because the bare word is English ("is marked as '@Serializable'" in
     * kotlinx.serialization's messages, which the plans bundle ships).
     */
    private val markdownParserMarkers = Regex(
        "\\b(markdown-it|micromark|commonmark|remark|showdown|snarkdown|MarkdownParser|intellij\\.markdown|parseMarkdown)\\b" +
            "|\\bmarked(\\(\\)|\\.(parse|parseInline|use|lexer|setOptions)\\b)",
        RegexOption.IGNORE_CASE,
    )

    /**
     * Markdown is parsed only at build time (generateSite) into static HTML. Fails the build if a shipped
     * JS asset contains a Markdown parser.
     */
    fun assertNoMarkdownParser(name: String, js: String) {
        val hit = markdownParserMarkers.find(js) ?: return
        error("$name contains a Markdown parser marker '${hit.value}': Markdown must be rendered at build time only")
    }
}
