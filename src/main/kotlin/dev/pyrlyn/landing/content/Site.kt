package dev.pyrlyn.landing.content

/** Site-wide constants (port of src/lib/site.ts). Product facts that the docs sync must not overwrite live here. */
object Site {
    /** Site brand: the site, nav, footer and GitHub org are all Pyrlyn. */
    const val BRAND = "Pyrlyn"

    data class Pro(val price: String, val features: List<String>)

    /** PLACEHOLDER Pro tiers per tool. Front matter `pro_price` overrides the price. Nothing here is a real offer. */
    val PRO: Map<String, Pro> = mapOf(
        "rtok" to Pro("$0 / mo", listOf("Team-wide ledger sync", "Hosted savings dashboard", "Priority support")),
        "cox" to Pro("$0 / mo", listOf("Shared session history", "Org permission policies", "Priority support")),
        "ketch" to Pro("$0 / mo", listOf("Private package registry", "Fleet lockfile sync", "Priority support")),
    )
    val PRO_FALLBACK = Pro("$0 / mo", listOf("Team features", "Hosted dashboard", "Priority support"))

    /** Products that are completely free: no Pro tier, no price, no paid options anywhere on the site. */
    val FREE_ONLY: Set<String> = setOf("stator", "weft")
    fun isFreeOnly(slug: String) = slug in FREE_ONLY

    /** Footnote for every Pro price shown on the site. */
    const val PRO_PRICE_NOTE = "* Free during the preview (beta) version."

    /** Short real "free" highlights per tool for the pricing table. */
    val FREE_HIGHLIGHTS: Map<String, List<String>> = mapOf(
        "rtok" to listOf("Hooks, MCP server and API proxy", "Lossless archive with rtok expand", "Local stats and doctor"),
        "cox" to listOf("Terminal UI and headless runs", "Sandboxed tools by default", "Local cost ledger"),
        "ketch" to listOf("Install from any GitHub release", "SHA-256 verification", "Rollback, lock and sync"),
        "runa" to listOf("Fit check before any download", "Local GGUF and cloud APIs, same controls", "OpenAI-compatible runa serve"),
        "mailune" to listOf("Direct to IMAP, JMAP, Gmail and Graph", "On-device AI by default", "Encrypted mail stays local"),
        "weft" to listOf("The weft command: validate, fmt, explain", "MCP server and editor plugins", "Render to HTML, React, SwiftUI and Slint"),
        "swarfr" to listOf("Lossless compress and dedupe", "Seed new worktrees from a sibling", "Read-only status and advise"),
        "aulo" to listOf("The full specification and plan", "gRPC API reference", "Free, open-source edition when it ships"),
        "scull" to listOf("Rust core, native UI per platform", "Research on six terminals", "Open development from day one"),
        "kerf" to listOf("Research on ten editors", "Sandboxed WASM plugin design", "Open development from day one"),
        "stator" to listOf("ts and js modes", "stator explain verdicts", "vite-stator bundler for js mode"),
        "bindsmith" to listOf("Six platforms from one YAML", "Review markers and verify", "CI-safe dump, diff and --check"),
    )

    data class LicenseItem(val name: String, val text: String)
    data class License(val short: String, val lead: String, val items: List<LicenseItem>)

    /** One license wording for every project; Weft and Stator override it. */
    val LICENSE = License(
        short = "GPLv3 or commercial — your choice",
        lead = "",
        items = listOf(
            LicenseItem("GNU GPLv3", "free for open-source applications on any platform, including embedded systems."),
            LicenseItem("Commercial license", "for proprietary applications, including embedded systems."),
        ),
    )
    val LICENSE_OVERRIDES: Map<String, License> = mapOf(
        "stator" to License(
            "Free · GNU GPLv3", "Stator is free and licensed under the GNU GPLv3:",
            listOf(LicenseItem("GNU GPLv3", "use, modify and distribute it under the terms in LICENSE.")),
        ),
        "weft" to License(
            "Apache License 2.0", "Weft is licensed under the Apache License 2.0:",
            listOf(LicenseItem("Apache License 2.0", "use, modify and distribute it, including in proprietary software, under the terms in LICENSE.")),
        ),
    )
    fun licenseFor(slug: String) = LICENSE_OVERRIDES[slug] ?: LICENSE

    data class Theme(val accent: String, val accent2: String, val accentLight: String, val bg: String, val images: String)

    /** Page themes. Front matter accents override these for project pages. */
    val THEMES: Map<String, Theme> = mapOf(
        "home" to Theme("#4C8DFF", "#3EE6C4", "#2F6BDB", "#070910", "home"),
        "rtok" to Theme("#5CE1FF", "#FF6B4A", "#0B7FA0", "#06101A", "rtok"),
        "cox" to Theme("#A8E06C", "#6FD6B4", "#3D8B3A", "#070B09", "cox"),
        "ketch" to Theme("#3DDCB0", "#5CC8FF", "#0F6F5C", "#06100F", "ketch"),
        "runa" to Theme("#E85A3C", "#FFB088", "#C94830", "#120E0C", "runa"),
        "mailune" to Theme("#8FB4D9", "#B7D0E8", "#335C8C", "#171714", "mailune"),
        "weft" to Theme("#A89BFF", "#F2C46D", "#5B4BD1", "#0C0B16", "weft"),
        "swarfr" to Theme("#E9B45A", "#7FA8D6", "#9A6A16", "#0E0D0B", "swarfr"),
        "aulo" to Theme("#FF7AA2", "#7EF2D0", "#C2386A", "#120B10", "aulo"),
        "scull" to Theme("#EDE6D6", "#45E39A", "#2E7D5B", "#0A0C0B", "scull"),
        "kerf" to Theme("#C6F24E", "#FF6BD6", "#5E7A12", "#0B0C08", "kerf"),
        "stator" to Theme("#5B7CFF", "#9AA3B5", "#3352D6", "#0C0E14", "stator"),
        "bindsmith" to Theme("#C87941", "#13B9FD", "#A35F2E", "#121416", "bindsmith"),
    )
    fun theme(key: String) = THEMES[key] ?: THEMES.getValue("home")

    /** Release status per product; a `status` in synced front matter wins. */
    val RELEASE_STATUS: Map<String, String> = mapOf(
        "rtok" to "In development · no stable release yet",
        "ketch" to "In development · no stable release yet",
        "cox" to "In development · no stable release yet",
        "runa" to "In development · no release yet",
        "mailune" to "In development · no release yet",
        "weft" to "Prototype · no release yet",
        "swarfr" to "In development · no stable release yet",
        "aulo" to "Specification only · no code yet",
        "scull" to "Early scaffold · no release yet",
        "kerf" to "Research · no code yet",
        "stator" to "Research compiler · no release yet",
        "bindsmith" to "In development · no release yet",
    )
    const val RELEASE_STATUS_FALLBACK = "In development"

    /** README facts the synced site copy does not carry but the page must keep. */
    val CAVEATS: Map<String, String> = mapOf(
        "runa" to "Runa has no published release yet, so the installer above works from the first GitHub release. Until then, build from source (see Install below).",
        "rtok" to "Token savings are estimates from a chars-per-token heuristic until matched against provider usage. No live A/B cost reduction has been established yet.",
    )

    data class Category(val id: String, val label: String, val description: String, val order: Int)

    val CATEGORIES = listOf(
        Category("ai", "AI & agents", "Coding agents, local models and token savings.", 1),
        Category("devtools", "Developer tools", "CLI, build, terminal and editor tooling.", 2),
        Category("languages", "Languages & formats", "Compilers and machine-readable formats.", 3),
        Category("apps", "Apps", "End-user applications.", 4),
    )

    /** Category per product slug. A new, renamed or removed product must be updated here. */
    val PRODUCT_CATEGORY: Map<String, String> = mapOf(
        "rtok" to "ai", "cox" to "ai", "runa" to "ai", "aulo" to "ai",
        "ketch" to "devtools", "swarfr" to "devtools", "scull" to "devtools", "kerf" to "devtools", "bindsmith" to "devtools",
        "weft" to "languages", "stator" to "languages",
        "mailune" to "apps",
    )
    fun categoryOf(slug: String) = CATEGORIES.find { it.id == PRODUCT_CATEGORY[slug] }
}
