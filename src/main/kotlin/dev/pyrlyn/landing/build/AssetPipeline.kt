package dev.pyrlyn.landing.build

import java.io.File
import java.security.MessageDigest

/**
 * Own CSS/JS pipeline, no third-party tooling: CSS minification, a small ES-module
 * bundler for src/main/web/js (relative imports inlined in dependency order), content hashing.
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

    /** Bundle an entry module with its relative imports (each module once, dependencies first). */
    fun bundleJs(entry: File): String {
        val done = hashSetOf<String>()
        val ordered = mutableListOf<String>()
        fun visit(f: File) {
            val key = f.canonicalPath
            if (!done.add(key)) return
            val src = f.readText()
            val importRe = Regex("^import\\s*\\{[^}]*\\}\\s*from\\s*\"(\\.[^\"]+)\";?\\s*$", RegexOption.MULTILINE)
            for (m in importRe.findAll(src)) {
                val p = m.groupValues[1].let { if (it.endsWith(".js")) it else "$it.js" }
                visit(File(f.parentFile, p))
            }
            val exportRe = Regex("^export\\s+((?:async\\s+)?function\\*?|const|let|class)\\s+([A-Za-z0-9_$]+)", RegexOption.MULTILINE)
            val names = exportRe.findAll(src).map { it.groupValues[2] }.toList()
            val body = src.replace(importRe, "").replace(exportRe) { "${it.groupValues[1]} ${it.groupValues[2]}" }
            // Each module gets its own scope so private top-level names cannot collide; only exports are lifted.
            ordered += if (names.isEmpty()) "{\n$body\n}" else "const {${names.joinToString(",")}}=(()=>{\n$body\nreturn {${names.joinToString(",")}};\n})();"
        }
        visit(entry)
        return ordered.joinToString("\n")
    }

    /** Light JS minification: drop full-line // comments, block comments at line start, indentation and blank lines. */
    fun minifyJs(js: String): String {
        val noBlock = Regex("^\\s*/\\*[\\s\\S]*?\\*/\\s*$", RegexOption.MULTILINE).replace(js, "")
        return noBlock.lines().map { it.trim() }.filter { it.isNotEmpty() && !it.startsWith("//") }.joinToString("\n")
    }

    /** Names of Markdown parsers/renderers; none of them may reach the client. */
    private val markdownParserMarkers = Regex("\\b(marked|markdown-it|micromark|commonmark|remark|showdown|snarkdown|MarkdownParser|intellij\\.markdown|parseMarkdown)\\b", RegexOption.IGNORE_CASE)

    /**
     * Markdown is parsed only at build time (generateSite) into static HTML. Fails the build if a shipped
     * JS asset contains a Markdown parser.
     */
    fun assertNoMarkdownParser(name: String, js: String) {
        val hit = markdownParserMarkers.find(js) ?: return
        error("$name contains a Markdown parser marker '${hit.value}': Markdown must be rendered at build time only")
    }
}
