package dev.pyrlyn.landing.content

/**
 * Minimal YAML front matter reader for content/projects markdown files: scalar `key: value` (plain, single or
 * double quoted), booleans, integers and `- item` lists. That is all CONTENT_CONTRACT.md allows.
 */
object FrontMatter {
    data class Doc(val data: Map<String, Any>, val body: String)

    fun parse(raw: String): Doc {
        val m = Regex("^---\\r?\\n([\\s\\S]*?)\\r?\\n---\\r?\\n?").find(raw) ?: return Doc(emptyMap(), raw)
        val data = linkedMapOf<String, Any>()
        var listKey: String? = null
        for (line in m.groupValues[1].lines()) {
            if (line.isBlank() || line.trimStart().startsWith("#")) continue
            val item = Regex("^\\s+-\\s+(.*)$").find(line) ?: Regex("^-\\s+(.*)$").find(line)
            if (item != null && listKey != null) {
                @Suppress("UNCHECKED_CAST")
                (data.getOrPut(listKey) { mutableListOf<String>() } as MutableList<String>) += scalar(item.groupValues[1])
                continue
            }
            val kv = Regex("^([A-Za-z_][\\w-]*):\\s*(.*)$").find(line) ?: continue
            val (k, v) = kv.destructured
            if (v.isEmpty()) { listKey = k; data[k] = mutableListOf<String>(); continue }
            listKey = null
            data[k] = when {
                v == "true" -> true
                v == "false" -> false
                Regex("^-?\\d+$").matches(v) -> v.toInt()
                else -> scalar(v)
            }
        }
        return Doc(data, raw.substring(m.range.last + 1))
    }

    private fun scalar(v: String): String {
        val t = v.trim()
        return when {
            t.length >= 2 && t.startsWith("\"") && t.endsWith("\"") -> t.substring(1, t.length - 1).replace("\\\"", "\"").replace("\\\\", "\\")
            t.length >= 2 && t.startsWith("'") && t.endsWith("'") -> t.substring(1, t.length - 1).replace("''", "'")
            else -> t.replace(Regex("\\s+#.*$"), "")
        }
    }
}
