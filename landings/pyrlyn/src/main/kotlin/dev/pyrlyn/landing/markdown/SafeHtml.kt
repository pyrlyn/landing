package dev.pyrlyn.landing.markdown

/** Guards for HTML that comes from synced Markdown (port of src/lib/html.ts). */
object SafeHtml {
    /** Raw tags kept as HTML; everything else is shown as text. */
    private val SAFE_TAGS = setOf("br", "kbd", "sub", "sup", "details", "summary", "b", "i", "em", "strong", "code")
    private val comment = Regex("<!--[\\s\\S]*?(?:-->|$)")
    private val namedAnchor = Regex("<a\\s+name=\"([A-Za-z0-9_.:-]+)\"\\s*/?>(\\s*</a>)?")
    private val tag = Regex("</?([A-Za-z][\\w-]*)([^<>]*)>|[<>]")
    private val attrsOk = Regex("^(\\s+open)?\\s*/?$", RegexOption.IGNORE_CASE)
    private val marker = Regex("\u0001([A-Za-z0-9_.:-]+)\u0002")

    fun clean(raw: String): String {
        var s = comment.replace(raw, "")
        s = namedAnchor.replace(s) { "\u0001${it.groupValues[1]}\u0002" }
        s = if (s == "</a>") "" else s
        s = tag.replace(s) { m ->
            val name = m.groups[1]?.value
            val attrs = m.groups[2]?.value ?: ""
            if (name != null && name.lowercase() in SAFE_TAGS && attrsOk.matches(attrs)) m.value.lowercase() else Esc.attr(m.value)
        }
        return marker.replace(s) { "<span id=\"${it.groupValues[1]}\"></span>" }
    }

    /** Link and image targets: drop script-capable schemes (javascript:, vbscript:, data:). */
    fun href(href: String): String {
        val scheme = Regex("^([a-z][a-z0-9+.-]*):", RegexOption.IGNORE_CASE).find(href.replace(Regex("[\\u0000-\\u0020]"), ""))?.groupValues?.get(1)?.lowercase()
        return if (scheme != null && scheme !in setOf("http", "https", "mailto")) "#" else href
    }
}
