package dev.pyrlyn.landing.markdown

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
