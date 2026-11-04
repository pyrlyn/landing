package dev.pyrlyn.landing.content

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

/** Site-owned copy kept as JSON resources (src/main/resources/data), ported from the Astro site's src/data TS modules. */
@Serializable data class DemoStep(val cmd: String, val note: String? = null, val out: List<String> = emptyList())
@Serializable data class Demo(val title: String, val caption: String, val steps: List<DemoStep>)
@Serializable data class FlowNode(val label: String, val detail: String)
@Serializable data class Flow(val title: String, val nodes: List<FlowNode>)
@Serializable data class Scene(val id: String, val label: String, val sub: String, val demo: Demo)
@Serializable data class Showcase(val demos: Map<String, Demo>, val flows: Map<String, Flow>, val coxScenes: List<Scene>) {
    /** A demo from a product's usage examples when no hand-made demo exists. */
    fun demoFromUsage(title: String, blocks: List<String>, name: String = title): Demo {
        val steps = blocks.flatMap { it.split("\n") }.filter { it.isNotBlank() }.take(6).map { l ->
            val m = Regex("^(.*?)(\\s+#\\s.*)$").find(l)
            if (m != null) DemoStep(m.groupValues[1], m.groupValues[2].trim()) else DemoStep(l)
        }
        return Demo("$title — zsh", "Commands from the $name usage examples.", steps)
    }
}

@Serializable data class StripItem(val label: String, val cmd: String)
@Serializable data class OverviewStep(val title: String, val text: String, val cmd: String? = null)
@Serializable data class Concept(val eyebrow: String, val title: String, val body: String, val code: String, val lang: String, val caption: String)
@Serializable data class OverviewTable(val eyebrow: String, val title: String, val head: List<String>, val rows: List<List<String>>)
@Serializable data class Notice(val label: String, val text: String, val kind: String)
@Serializable data class Faq(val q: String, val a: String)
@Serializable data class Overview(
    val eyebrow: String, val headline: String, val lead: String, val strip: List<StripItem>, val steps: List<OverviewStep>,
    val concept: Concept, val table: OverviewTable, val notice: Notice, val faq: List<Faq>, val next: List<String>, val from: List<String> = emptyList(),
)


@Serializable data class NavItem(val file: String? = null, val page: String? = null, val title: String? = null, val summary: String? = null)
@Serializable data class NavGroup(val label: String, val items: List<NavItem>)
@Serializable data class ProductNav(val groups: List<NavGroup>, val exclude: Map<String, String> = emptyMap())

object Data {
    private val json = Json { ignoreUnknownKeys = true }
    private fun res(name: String) = requireNotNull(Data::class.java.getResource("/data/$name")) { "missing resource data/$name" }.readText()

    val showcase: Showcase by lazy { json.decodeFromString(res("showcase.json")) }
    val overviews: Map<String, Overview> by lazy { json.decodeFromString(res("docs-overview.json")) }
    val docsNav: Map<String, ProductNav> by lazy {
        val all = json.parseToJsonElement(res("docs-nav.json")) as JsonObject
        all.filterKeys { !it.startsWith("$") }.mapValues { json.decodeFromJsonElement(ProductNav.serializer(), it.value) }
    }
}
