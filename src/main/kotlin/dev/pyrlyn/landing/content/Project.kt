package dev.pyrlyn.landing.content

import java.io.File

/** One product, read from content/projects/<id>.md (contract: CONTENT_CONTRACT.md; port of project-schema.ts). */
data class Project(
    val id: String,
    /** Display title (product names capitalized). */
    val title: String,
    /** Synced spelling, for logo marks. */
    val rawTitle: String,
    val tagline: String,
    val repo: String,
    val homepage: String?,
    val install: String,
    val installAlternatives: List<String>,
    val version: String,
    val status: String?,
    val accent: String,
    val accent2: String?,
    val accentLight: String?,
    val proPrice: String?,
    val featured: Boolean,
    val order: Int?,
    val body: String,
) {
    companion object {
        private val hex = Regex("^#[0-9a-fA-F]{6}$")
        private val semver = Regex("^\\d+\\.\\d+\\.\\d+(-[\\w.]+)?$")
        private val web = Regex("^https?://", RegexOption.IGNORE_CASE)

        fun load(file: File): Project {
            val fm = FrontMatter.parse(file.readText())
            val d = fm.data
            fun s(k: String) = d[k] as? String
            fun req(k: String) = requireNotNull(s(k)?.takeIf { it.isNotEmpty() }) { "${file.name}: `$k` is required" }
            val title = req("title")
            val tagline = req("tagline")
            require(tagline.length <= 160) { "${file.name}: tagline over 160 chars" }
            val repo = req("repo")
            require(repo.startsWith("https://github.com/")) { "${file.name}: repo must be a GitHub URL" }
            s("homepage")?.let { require(web.containsMatchIn(it)) { "${file.name}: homepage must be http(s)" } }
            val version = req("version")
            require(semver.matches(version)) { "${file.name}: version must be semver without a leading v" }
            val accent = req("accent")
            listOfNotNull(accent, s("accent2"), s("accentLight")).forEach { require(hex.matches(it)) { "${file.name}: accent $it is not #RRGGBB" } }
            @Suppress("UNCHECKED_CAST")
            return Project(
                id = file.nameWithoutExtension,
                title = Names.normalize(title),
                rawTitle = title,
                tagline = Names.normalize(tagline),
                repo = repo,
                homepage = s("homepage"),
                install = req("install"),
                installAlternatives = (d["install_alternatives"] as? List<String>).orEmpty(),
                version = version,
                status = s("status"),
                accent = accent,
                accent2 = s("accent2"),
                accentLight = s("accentLight"),
                proPrice = s("pro_price"),
                featured = d["featured"] == true,
                order = d["order"] as? Int,
                body = fm.body,
            )
        }
    }
}
