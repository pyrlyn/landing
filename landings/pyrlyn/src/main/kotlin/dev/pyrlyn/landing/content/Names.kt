package dev.pyrlyn.landing.content

/**
 * Product names are proper nouns in prose ("Rtok", "Ketch") but stay lowercase as commands, package
 * names, URLs, slugs, paths and logo marks (port of src/lib/names.ts). Synced Markdown is overwritten
 * upstream, so the site normalizes at build time: only Markdown text nodes and front-matter display
 * strings pass through here.
 */
object Names {
    val PRODUCT_NAMES: Map<String, String> = mapOf(
        "aulo" to "Aulo", "bindsmith" to "Bindsmith", "cox" to "Cox", "kerf" to "Kerf", "ketch" to "Ketch", "mailune" to "Mailune",
        "rtok" to "Rtok", "runa" to "Runa", "scull" to "Scull", "stator" to "Stator", "swarfr" to "Swarfr", "weft" to "Weft",
    )

    private val DEFAULT_COMMANDS = listOf(
        "acp", "add", "agents", "bench", "build", "changelog", "chat", "check", "completions", "config", "doctor",
        "expand", "explain", "fmt", "graph", "help", "history", "hook", "hooks", "import", "info", "init",
        "install", "list", "lock", "mcp", "media", "otel", "outdated", "path", "plugin", "plugins", "process",
        "proxy", "registry", "releases", "remove", "rollback", "run", "search", "self", "serve",
        "sessions", "stats", "sync", "task", "tui", "uninstall", "update", "upgrade", "validate", "web",
    )
    private val PROSE_WORDS = setOf(
        "is", "and", "or", "the", "a", "an", "to", "in", "on", "for", "with", "vs", "does", "has", "can", "will", "was", "runs",
        "reads", "needs", "checks", "already", "because", "built", "beat", "why", "also", "keeps", "uses", "only", "itself", "docs",
        "documentation", "pro", "website", "site", "readme", "saved", "installed", "installs", "automatically", "reduces", "cannot",
        "without", "by", "usage", "configuration",
    )

    private val ALT = PRODUCT_NAMES.keys.joinToString("|")
    private val NAME_RE = Regex("(?<![\\w./@`\$~:\\\\-])($ALT)(?![\\w/@\\\\-]|\\.[A-Za-z0-9])")
    private val CMD_BEFORE = Regex("(?:\\b(?:run|type|call|invoke|execute)\\s+|\\$\\s*)$", RegexOption.IGNORE_CASE)
    private val NEXT_WORD = Regex("^ +(--?[A-Za-z]|[a-z][\\w-]*)")
    private val EXEC_WORD = Regex("^ +(command|binary|executable|subcommand)s?\\b")
    private val HARVEST = Regex("(?:^|[\\s|;&(])(?:$ALT)\\s+([a-z][\\w-]*)", RegexOption.MULTILINE)

    private val commands: MutableSet<String> = DEFAULT_COMMANDS.toMutableSet()

    fun addCommands(words: Iterable<String>) { for (w in words) if (w !in PROSE_WORDS) commands += w }

    /** Collect "<name> <subcommand>" words from fenced code and code spans of raw Markdown sources. */
    fun harvestFromMarkdown(sources: Iterable<String>) {
        val found = mutableSetOf<String>()
        val code = Regex("```[^\\n]*\\n([\\s\\S]*?)```|`([^`\\n]+)`")
        for (raw in sources) for (m in code.findAll(raw)) {
            val src = m.groups[1]?.value ?: m.groups[2]?.value ?: continue
            for (h in HARVEST.findAll(src)) if (h.groupValues[1] !in PROSE_WORDS) found += h.groupValues[1]
        }
        addCommands(found)
    }

    /** Capitalize product names in a plain prose string; command mentions stay as they are. */
    fun normalize(text: String): String {
        if (text.isEmpty()) return text
        return NAME_RE.replace(text) { m ->
            val name = m.value
            val after = text.substring(m.range.last + 1)
            val next = NEXT_WORD.find(after)
            when {
                next != null && (next.groupValues[1].startsWith("-") || next.groupValues[1] in commands) -> name
                EXEC_WORD.containsMatchIn(after) -> name
                CMD_BEFORE.containsMatchIn(text.substring(0, m.range.first)) -> name
                else -> PRODUCT_NAMES.getValue(name)
            }
        }
    }

    /** Normalize a raw Markdown inline string, leaving `code spans`, link targets and tags as written. */
    fun normalizeMdInline(src: String): String {
        val re = Regex("(`[^`]*`|\\]\\([^)]*\\)|<[^>]+>)")
        val out = StringBuilder()
        var last = 0
        for (m in re.findAll(src)) {
            out.append(normalize(src.substring(last, m.range.first))).append(m.value)
            last = m.range.last + 1
        }
        return out.append(normalize(src.substring(last))).toString()
    }
}
