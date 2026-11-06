package dev.pyrlyn.landing.content

import java.io.File

/** Marketplace helpers over content/projects (port of src/lib/catalog.ts). */
class Catalog(val tools: List<Project>, private val apiPrices: Map<String, String?> = emptyMap()) {
    val featured: Project get() = tools.find { it.featured } ?: tools.first()

    fun byId(id: String) = tools.find { it.id == id }

    /** Tools that sell a Pro tier; free-only products are left out. */
    fun hasPro(t: Project) = !Site.isFreeOnly(t.id)

    /** The API's price wins; front matter `pro_price` and Site.PRO are placeholders. */
    fun proFor(t: Project): Site.Pro {
        val base = Site.PRO[t.id] ?: Site.PRO_FALLBACK
        return Site.Pro(apiPrices[t.id] ?: t.proPrice ?: base.price, base.features)
    }

    /** The API refuses wishlist entries for products it does not know, so only those get the form. */
    fun hasWishlist(t: Project) = apiPrices.containsKey(t.id)

    fun statusFor(t: Project) = t.status ?: Site.RELEASE_STATUS[t.id] ?: Site.RELEASE_STATUS_FALLBACK

    data class Accents(val accent: String, val accent2: String, val accentLight: String)
    fun accentsFor(t: Project): Accents {
        val theme = Site.THEMES[t.id]
        return Accents(t.accent, t.accent2 ?: theme?.accent2 ?: t.accent, t.accentLight ?: theme?.accentLight ?: t.accent)
    }

    fun categoryFor(t: Project) = Site.categoryOf(t.id) ?: Site.CATEGORIES.last()

    data class Group(val category: Site.Category, val tools: List<Project>)
    fun groupByCategory(list: List<Project> = tools) = Site.CATEGORIES.sortedBy { it.order }
        .map { c -> Group(c, list.filter { categoryFor(it).id == c.id }) }
        .filter { it.tools.isNotEmpty() }

    companion object {
        /** Short badge label: every product is in active development. */
        fun versionLabel(@Suppress("UNUSED_PARAMETER") t: Project) = "in development"

        /** Short install for cards: prefer a `ketch install …` alternative. */
        fun cardInstall(t: Project) = t.installAlternatives.find { it.startsWith("ketch ") } ?: t.install

        private val DEFAULT_ORDER = listOf("rtok", "ketch", "mailune") // hidden: cox, runa, weft, swarfr, aulo, scull, kerf, stator, bindsmith

        fun load(dir: File, apiPrices: Map<String, String?> = emptyMap()): Catalog {
            val files = dir.listFiles { f -> f.extension == "md" && f.name != "README.md" }.orEmpty()
            fun rank(p: Project) = p.order ?: DEFAULT_ORDER.indexOf(p.id).takeIf { it >= 0 } ?: 1000
            val tools = files.map { Project.load(it) }
                .filter { it.id in Site.VISIBLE_PRODUCTS }
                .sortedWith(compareBy<Project> { rank(it) }.thenBy { it.title })
            require(tools.isNotEmpty()) { "no visible projects in $dir (VISIBLE_PRODUCTS=${Site.VISIBLE_PRODUCTS})" }
            val missing = Site.VISIBLE_PRODUCTS - tools.map { it.id }.toSet()
            require(missing.isEmpty()) { "VISIBLE_PRODUCTS missing project files: $missing" }
            return Catalog(tools, apiPrices)
        }
    }
}
