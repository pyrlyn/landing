package dev.pyrlyn.landing.html

import dev.pyrlyn.landing.content.Catalog
import dev.pyrlyn.landing.content.Demo
import dev.pyrlyn.landing.content.Flow
import dev.pyrlyn.landing.content.Project
import dev.pyrlyn.landing.content.Scene
import dev.pyrlyn.landing.content.Site
import dev.pyrlyn.landing.markdown.Esc
import kotlinx.html.*
import java.io.File

/** AVIF + WebP picture for generated art in public/images/<dir>/<name>-<w>.{avif,webp}; nothing when the art is missing. */
fun FlowContent.picture(ctx: Ctx, dir: String, name: String, widths: List<Int>, sizes: String, w: Int, h: Int, alt: String = "", cls: String? = null, eager: Boolean = false) {
    val has = widths.all { x -> listOf("avif", "webp").all { File(ctx.publicDir, "images/$dir/$name-$x.$it").exists() } }
    if (!has) return
    fun set(ext: String) = widths.joinToString(", ") { x -> "${ctx.url("/images/$dir/$name-$x.$ext")} ${x}w" }
    val img = buildString {
        append("<img src=\"").append(Esc.attr(ctx.url("/images/$dir/$name-${widths.last()}.webp"))).append("\" alt=\"").append(Esc.attr(alt))
        append("\" srcset=\"").append(Esc.attr(set("webp"))).append("\" sizes=\"").append(Esc.attr(sizes))
        append("\" width=\"$w\" height=\"$h\" loading=\"").append(if (eager) "eager" else "lazy").append("\" decoding=\"async\"")
        if (eager) append(" fetchpriority=\"high\"")
        append(">")
    }
    val clsAttr = if (cls != null) " class=\"${Esc.attr(cls)}\"" else ""
    val hidden = if (alt.isEmpty()) " aria-hidden=\"true\"" else ""
    raw("<picture$clsAttr$hidden><source type=\"image/avif\" srcset=\"${Esc.attr(set("avif"))}\" sizes=\"${Esc.attr(sizes)}\">$img</picture>")
}

/** Animated console demo, server-rendered in its final state (port of Terminal.astro). */
fun FlowContent.terminal(demo: Demo, id: String, cls: String? = null, manual: Boolean = false) {
    fun esc(s: String) = s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
    val lines = mutableListOf<String>()
    for (s in demo.steps) {
        lines += "<span class=\"term__line term__line--cmd\"><span class=\"tok-p\">$ </span><span class=\"term__typed\">${esc(s.cmd)}</span></span>"
        s.note?.let { lines += "<span class=\"term__line term__line--note\">${esc(it)}</span>" }
        s.out.forEach { lines += "<span class=\"term__line term__line--out\">${esc(it)}</span>" }
    }
    lines += "<span class=\"term__line term__line--cmd term__line--idle\"><span class=\"tok-p\">$ </span><span class=\"term__cursor\"></span></span>"
    val transcript = demo.steps.joinToString("\n") { s -> (listOf("$ ${s.cmd}", s.note) + s.out).filterNot { it.isNullOrEmpty() }.joinToString("\n") }
    figure(listOfNotNull("term glass glass--thick", cls).joinToString(" ")) {
        attr("data-term", ""); if (manual) attr("data-term-manual", "")
        if (!manual) attr("data-term-auto", "")
        attr("aria-labelledby", "$id-cap")
        div("term__bar") {
            span("term__dots") { attr("aria-hidden", "true"); i {}; i {}; i {} }
            span("term__title") { this.id = "$id-title"; +demo.title }
            button(classes = "term__ctl", type = ButtonType.button) { attr("data-term-ctl", ""); attr("aria-describedby", "$id-title"); attr("hidden", ""); +"Replay" }
        }
        pre("term__body") { attr("aria-hidden", "true"); code { raw(lines.joinToString("\n")) } }
        pre("sr-only") { +transcript }
        figcaption("term__cap") { this.id = "$id-cap"; +demo.caption }
    }
}

fun FlowContent.flowViz(flow: Flow) {
    div("flow") {
        h3("h3 flow__title") { +flow.title }
        ol("flow__nodes") {
            flow.nodes.forEachIndexed { i, n ->
                li("flow__node glass shine") {
                    style = "--i:$i"
                    span("flow__idx") { attr("aria-hidden", "true"); +(i + 1).toString().padStart(2, '0') }
                    strong { +n.label }
                    span("muted small") { +n.detail }
                }
            }
        }
    }
}

/** rtok: the MCP code graph as an SVG (port of CodeGraph.astro). */
fun FlowContent.codeGraph() {
    data class P(val id: String, val x: Int, val y: Int)
    val tools = listOf(P("symbol", 330, 50), P("callers", 455, 120), P("impact", 420, 280), P("outline", 245, 280), P("explore", 205, 90))
    val hub = 330 to 172
    val agent = 70 to 172
    fun f(n: Double): String { val r = Math.round(n * 10) / 10.0; return if (r == Math.floor(r)) r.toLong().toString() else r.toString() }
    fun edge(t: P): String {
        val dx = (t.x - hub.first).toDouble(); val dy = (t.y - hub.second).toDouble(); val len = Math.hypot(dx, dy)
        val k = minOf(44 / Math.abs(if (dx == 0.0) 1e-9 else dx), 17 / Math.abs(if (dy == 0.0) 1e-9 else dy))
        return "M${f(hub.first + dx / len * 36)} ${f(hub.second + dy / len * 36)} L${f(t.x - dx * k)} ${f(t.y - dy * k)}"
    }
    val svg = buildString {
        append("<svg class=\"graph__svg\" viewBox=\"0 0 520 330\" role=\"img\" aria-label=\"An agent queries Rtok&#39;s symbol index through five MCP tools: symbol, callers, impact, outline and explore.\">")
        append("<defs><radialGradient id=\"g-hub\" cx=\"50%\" cy=\"30%\" r=\"70%\"><stop offset=\"0\" stop-color=\"#bff4ff\"></stop><stop offset=\"1\" stop-color=\"#5CE1FF\" stop-opacity=\".35\"></stop></radialGradient></defs>")
        append("<path class=\"graph__edge graph__edge--agent\" pathLength=\"1\" d=\"M${agent.first + 44} ${agent.second} C ${agent.first + 120} ${agent.second - 40}, ${hub.first - 120} ${hub.second - 40}, ${hub.first - 36} ${hub.second}\" style=\"--d:0\"></path>")
        tools.forEachIndexed { i, t -> append("<path class=\"graph__edge\" pathLength=\"1\" d=\"${edge(t)}\" style=\"--d:${f(0.35 + i * 0.12)}\"></path>") }
        append("<g class=\"graph__node graph__node--agent\" style=\"--d:0\"><rect x=\"${agent.first - 48}\" y=\"${agent.second - 20}\" width=\"96\" height=\"40\" rx=\"12\"></rect><text x=\"${agent.first}\" y=\"${agent.second + 5}\">agent</text></g>")
        append("<g class=\"graph__node graph__node--hub\" style=\"--d:.2\"><circle class=\"graph__base\" cx=\"${hub.first}\" cy=\"${hub.second}\" r=\"36\"></circle><circle cx=\"${hub.first}\" cy=\"${hub.second}\" r=\"36\" fill=\"url(#g-hub)\"></circle><text x=\"${hub.first}\" y=\"${hub.second - 2}\">symbol</text><text x=\"${hub.first}\" y=\"${hub.second + 13}\">index</text></g>")
        tools.forEachIndexed { i, t -> append("<g class=\"graph__node\" style=\"--d:${f(0.5 + i * 0.12)}\"><rect x=\"${t.x - 44}\" y=\"${t.y - 17}\" width=\"88\" height=\"34\" rx=\"17\"></rect><text x=\"${t.x}\" y=\"${t.y + 5}\">${t.id}</text></g>") }
        append("</svg>")
    }
    figure("graph glass") {
        attr("data-graph", ""); attr("aria-labelledby", "graph-cap")
        p("flow__title") { +"Code graph over MCP" }
        raw(svg)
        figcaption("muted small") {
            id = "graph-cap"
            code { +"rtok graph index ." }; +" builds a symbol index that agents query as "; code { +"symbol" }; +", "; code { +"callers" }; +", "
            code { +"impact" }; +", "; code { +"outline" }; +" and "; code { +"explore" }; +" instead of a grep-and-read chain."
        }
    }
}

/** rtok: canvas token particles (port of TokenParticles.astro). */
fun FlowContent.tokenParticles() {
    div("particles glass glass--thick") {
        attr("data-particles", "")
        div("particles__head") {
            div {
                p("flow__title") { +"Context, compressed "; span("badge badge--placeholder") { +"Illustrative" } }
                p("particles__counter") {
                    attr("aria-live", "off")
                    span("bitset__delta") { +"Δtok" }; +" "; span { attr("data-p-count", ""); +"−0" }; +" "
                    span("muted") { +"/ "; span { attr("data-p-total", ""); +"240" }; +" tokens" }
                }
            }
            ul("legend legend--row") {
                li { span("swatch swatch--k") { attr("aria-hidden", "true") }; +" kept" }
                li { span("swatch swatch--t") { attr("aria-hidden", "true") }; +" thinned" }
                li { span("swatch swatch--c") { attr("aria-hidden", "true") }; +" cut → archive" }
            }
        }
        div("particles__stage") {
            canvas { attr("data-p-canvas", ""); attr("aria-hidden", "true") }
            span("particles__band-label") { attr("aria-hidden", "true"); +"context" }
            span("particles__archive-label") { attr("aria-hidden", "true"); +"archive · "; code { +"rtok expand <id>" } }
        }
        p("muted small") {
            +"Scroll to compress, scroll back to expand. An illustration of the idea, not a measurement: in Rtok a saving only counts when a Measurement row in the ledger records it, and archived payloads come back whole with "
            code { +"rtok expand <id>" }; +"."
        }
    }
}

/** cox: three glass panels in perspective (port of CoxScenes.astro). */
fun FlowContent.coxScenes(scenes: List<Scene>) {
    div("scenes") {
        attr("data-scenes", "")
        div("scenes__tabs") {
            attr("role", "tablist"); attr("aria-label", "Ways to run cox")
            scenes.forEachIndexed { i, s ->
                button(classes = "scenes__tab", type = ButtonType.button) {
                    attr("role", "tab"); id = "sc-tab-${s.id}"; attr("aria-controls", "sc-${s.id}")
                    attr("aria-selected", if (i == 0) "true" else "false"); attr("tabindex", if (i == 0) "0" else "-1")
                    strong { +s.label }; small { +s.sub }
                }
            }
        }
        div("scenes__stage") {
            scenes.forEachIndexed { i, s ->
                div("scenes__panel") {
                    attr("role", "tabpanel"); id = "sc-${s.id}"; attr("aria-labelledby", "sc-tab-${s.id}"); style = "--i:$i"; attr("data-scene", "")
                    terminal(s.demo, "sc-term-${s.id}", manual = true)
                }
            }
        }
    }
}

/** ketch: GitHub release → checksum → install → lockfile → rollback route (port of KetchRoute.astro). */
fun FlowContent.ketchRoute(flow: Flow) {
    val d = "M80 150 H800 C 910 150, 910 300, 790 300 H 650 C 570 300, 560 230, 560 150"
    data class Stop(val id: String, val x: Int, val y: Int, val label: String, val text: String, val pos: String, val back: Boolean = false)
    val stops = listOf(
        Stop("release", 80, 150, "GitHub release", "The asset built for your machine — no formula, no tap.", "up"),
        Stop("checksum", 320, 150, "Checksum", "Published SHA-256 checked against what landed on disk.", "down"),
        Stop("install", 560, 150, "Install", "Unpacked into a versioned store under ~/.ketch, linked onto your PATH.", "up"),
        Stop("lockfile", 800, 150, "Lockfile", "ketch lock writes ketch.lock; ketch sync installs exactly those versions elsewhere.", "up"),
        Stop("rollback", 720, 300, "Rollback", "ketch rollback <pkg> relinks the previous version, no re-download.", "down", back = true),
    )
    div("route-wrap") {
        div("route glass") {
            attr("data-route", "")
            p("flow__title") { +flow.title }
            div("route__fit") {
                attr("data-route-fit", "")
                div("route__box") {
                    attr("data-route-box", ""); style = "--route:path('$d')"
                    raw(buildString {
                        append("<svg class=\"route__svg\" viewBox=\"0 0 1000 360\" width=\"1000\" height=\"360\" aria-hidden=\"true\"><path class=\"route__track\" d=\"$d\"></path><path class=\"route__draw\" d=\"$d\" pathLength=\"1\" data-route-path></path><path class=\"route__back\" d=\"M800 150 C 910 150, 910 300, 790 300 H 650 C 570 300, 560 230, 560 150\"></path>")
                        stops.forEach { s -> append("<circle class=\"route__stop${if (s.back) " route__stop--back" else ""}\" cx=\"${s.x}\" cy=\"${s.y}\" r=\"9\"></circle>") }
                        append("</svg>")
                    })
                    stops.forEach { s ->
                        div("route__tip glass glass--thick route__tip--${s.pos}${if (s.back) " route__tip--back" else ""}") {
                            style = "left:${s.x}px;top:${s.y}px"; attr("data-route-stop", s.id)
                            strong { +s.label }; span { +s.text }
                        }
                    }
                    div("route__chip glass glass--thick") { attr("data-route-chip", ""); attr("aria-hidden", "true"); span("route__chip-dot") {}; +"sharkdp/fd "; small { +"v10.2.0" } }
                }
            }
            p("muted small") { +"Scroll to follow one package through install, lock and rollback. Steps and commands are from the Ketch README; "; code { +"sharkdp/fd@v10.2.0" }; +" is its install example." }
        }
        div("route-fallback") { flowViz(flow) }
    }
}

/** Home hero: 3D ring carousel of product cards (port of RingCarousel.astro + its island's markup). */
data class Hotspot(val x: Int, val y: Int, val tool: String, val label: String, val accent: String, val href: String, val text: String)

fun FlowContent.ringCarousel(ctx: Ctx, tools: List<Project>, hotspots: List<Hotspot>) {
    val n = tools.size
    val cat = ctx.catalog
    div("ring") {
        attr("data-ring", ""); style = "--n:$n"; attr("role", "region"); attr("aria-roledescription", "carousel"); attr("aria-label", "Products")
        attr("data-titles", tools.joinToString("\u001f") { it.title })
        div("ring__stage") {
            attr("data-ring-stage", "")
            ul("ring__track") {
                attr("data-ring-track", ""); attr("data-hotspots", "")
                tools.forEachIndexed { i, t ->
                    val hs = hotspots.find { it.tool == t.id }
                    li("ring__item") {
                        style = "--i:$i;--card-accent:${t.accent}"; attr("data-ring-item", ""); attr("aria-roledescription", "slide"); attr("aria-label", "${i + 1} of $n: ${t.title}")
                        div("ring__face") {
                            a(ctx.url("/${t.id}/"), classes = "ring__card glass glass--thick") {
                                attr("draggable", "false")
                                picture(ctx, t.id, "prop", listOf(400), "240px", 400, 400, cls = "ring__art")
                                span("ring__meta") { span("badge badge--version") { style = "--accent:${t.accent}"; +Catalog.versionLabel(t) } }
                                strong("ring__title") { +t.title; span { +"." } }
                                span("ring__tag") { +t.tagline }
                                span("ring__foot") {
                                    span { proLabel(cat, t) }
                                    icon("arrow", 16)
                                }
                            }
                            if (hs != null) div("hotspot ring__hs") {
                                button(classes = "hotspot__dot", type = ButtonType.button) {
                                    attr("aria-expanded", "false"); attr("aria-controls", "hs-${t.id}"); style = "--card-accent:${t.accent}"
                                    span("sr-only") { +"${hs.label}: show a feature" }
                                }
                                div("hotspot__tip glass glass--thick") {
                                    id = "hs-${t.id}"; attr("role", "note"); attr("hidden", ""); style = "--card-accent:${t.accent}"
                                    p("hotspot__kicker") { +hs.label }
                                    p { +hs.text }
                                }
                            }
                        }
                    }
                }
            }
        }
        div("ring__controls") {
            attr("data-ring-controls", ""); attr("hidden", "")
            button(classes = "ring__btn", type = ButtonType.button) { attr("data-ring-prev", ""); attr("aria-label", "Previous product"); raw("<svg viewBox=\"0 0 16 16\" width=\"16\" height=\"16\" aria-hidden=\"true\"><path d=\"M10 3 5 8l5 5\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.8\" stroke-linecap=\"round\" stroke-linejoin=\"round\"></path></svg>") }
            p("ring__status") { attr("aria-live", "off"); attr("data-ring-status", ""); +"${tools.firstOrNull()?.title ?: ""} · 1 / $n" }
            button(classes = "ring__btn", type = ButtonType.button) { attr("data-ring-next", ""); attr("aria-label", "Next product"); raw("<svg viewBox=\"0 0 16 16\" width=\"16\" height=\"16\" aria-hidden=\"true\"><path d=\"m6 3 5 5-5 5\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.8\" stroke-linecap=\"round\" stroke-linejoin=\"round\"></path></svg>") }
        }
    }
}

/** "Free · Pro $X*" or "Free". */
fun FlowOrPhrasingContent.proLabel(cat: Catalog, t: Project) {
    if (cat.hasPro(t)) { +"Free · Pro "; span { attr("data-pro-price", t.id); +cat.proFor(t).price }; +"*" } else +"Free"
}

/** The Pro wishlist action: an email form for products the backend knows, else the disabled button. */
fun FlowContent.proWishlist(ctx: Ctx, t: Project, describedBy: String) {
    if (ctx.catalog.hasWishlist(t)) {
        form(classes = "wishlist") {
            attr("data-wishlist", t.id); attr("data-api", ctx.cfg.apiUrl)
            input(InputType.email, name = "email", classes = "wishlist__email") {
                required = true; maxLength = "254"; attr("autocomplete", "email"); placeholder = "you@example.com"; attr("aria-label", "Email for the ${t.title} Pro wishlist")
            }
            button(classes = "btn btn--primary btn--block", type = ButtonType.submit) { disabled = true; attr("aria-describedby", describedBy); +"Join the Pro wishlist" }
            p("wishlist__status muted small") { attr("role", "status"); attr("aria-live", "polite") }
        }
    } else {
        button(classes = "btn btn--primary btn--block", type = ButtonType.button) {
            attr("aria-disabled", "true"); attr("aria-describedby", describedBy); style = "--accent:${t.accent}"; +"Join the Pro wishlist"
        }
    }
}

/** Pyrlyn Prompt logo from @pyrlyn/brand, inlined so it takes the page's brand tokens. */
object Logo {
    private fun inner(svg: String) = svg.trim()
        .replace(Regex("^<svg[^>]*>|</svg>$"), "")
        .replace(Regex("#F4F2ED", RegexOption.IGNORE_CASE), "currentColor")
        .replace(Regex("<path fill=\"(#(?:F2B33D|4C8DFF))\"", RegexOption.IGNORE_CASE)) { "<path class=\"brand__cursor\" fill=\"${it.groupValues[1]}\"" }
        .replace(Regex("#(?:F2B33D|4C8DFF)", RegexOption.IGNORE_CASE), "var(--accent)")
    private fun res(n: String) = requireNotNull(Logo::class.java.getResource("/brand/$n")).readText()
    val lockup by lazy { inner(res("pyrlyn-lockup-on-dark.svg")) }
    val mark by lazy { inner(res("pyrlyn-mark-on-dark.svg")) }
}

fun FlowContent.pyrlynLogo(ctx: Ctx) {
    a(ctx.url("/"), classes = "brand tw:max-[459px]:pr-0!") {
        attr("aria-label", "Pyrlyn")
        raw("<svg class=\"tw:block tw:max-[459px]:hidden!\" viewBox=\"-2 4 109 26\" width=\"101\" height=\"24\" aria-hidden=\"true\" focusable=\"false\">${Logo.lockup}</svg>")
        raw("<svg class=\"tw:block tw:min-[460px]:hidden!\" viewBox=\"0 0 32 32\" width=\"28\" height=\"28\" aria-hidden=\"true\" focusable=\"false\">${Logo.mark}</svg>")
    }
}

fun esc(s: String) = Esc.attr(s)
