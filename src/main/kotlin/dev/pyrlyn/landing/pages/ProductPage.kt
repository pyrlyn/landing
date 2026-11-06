package dev.pyrlyn.landing.pages

import dev.pyrlyn.landing.content.Data
import dev.pyrlyn.landing.content.Project
import dev.pyrlyn.landing.content.Sections
import dev.pyrlyn.landing.content.Site
import dev.pyrlyn.landing.html.*
import dev.pyrlyn.landing.seo.Seo
import kotlinx.html.*

private val ICON_SETS = mapOf(
    "rtok" to listOf("plug", "terminal", "undo", "graph", "layers", "chart", "box", "eye"),
    "cox" to listOf("stream", "shield", "undo", "coin", "layers", "terminal", "editor", "eye"),
    "ketch" to listOf("download", "shield", "app", "tree", "history", "lock", "plug", "box"),
    "runa" to listOf("chart", "cloud", "graph", "plug", "eye", "stream", "layers", "box"),
    "mailune" to listOf("shield", "sparkle", "lock", "app", "cloud", "key", "eye", "box"),
    "weft" to listOf("editor", "layers", "tree", "key", "undo", "plug", "eye", "box"),
    "swarfr" to listOf("box", "layers", "tree", "history", "eye", "graph", "lock", "chart"),
    "aulo" to listOf("stream", "sparkle", "terminal", "plug", "cloud", "layers", "eye", "box"),
    "scull" to listOf("terminal", "app", "editor", "eye", "box", "layers", "plug", "tree"),
    "kerf" to listOf("editor", "graph", "shield", "plug", "box", "layers", "tree", "eye"),
    "stator" to listOf("layers", "bolt", "eye", "box", "key", "plug", "tree", "chart"),
    "bindsmith" to listOf("app", "plug", "layers", "eye", "lock", "shield", "box", "tree"),
)

private val HERO_ALT = mapOf(
    "rtok" to "Abstract 4 by 4 grid of glass cubes: cyan cubes kept, dim cubes thinned, dashed coral outlines cut.",
    "cox" to "Abstract interlocking glass rings glowing lime green on a dark background.",
    "ketch" to "Abstract floating glass app window listing verified packages, with glass spheres around it.",
    "runa" to "The runa mark: a geometric rune stave with an ember glowing at its base, on warm charcoal glass.",
    "mailune" to "Abstract glass envelope sealed with a blue dot, floating over stacked glass message rows.",
    "weft" to "Abstract woven glass: indigo warp threads crossed by gold weft threads, with a screen tree of nodes pinned on the crossings.",
    "swarfr" to "Abstract helical metal shavings in straw-to-blue temper colours, curling off a glass block.",
    "aulo" to "Abstract rose and mint sound waves rippling out of a glass reed pipe.",
    "scull" to "Abstract ivory terminal window with a phosphor-green cursor, crossed by an oar stroke rippling on dark water.",
    "kerf" to "Abstract laser-cut plywood panels with chartreuse cut lines and slotted plugin tabs.",
    "stator" to "The stator mark: an indigo stator ring with a type bracket on the left and machine-code bit ticks on the right.",
    "bindsmith" to "The bindsmith multi-tool mark on its ink tile, with six platform chips fanned around it in copper and azure.",
)

/** Per-product floating chip on the hero stage (static markup from [slug].astro). */
private val HERO_CHIPS: Map<String, String> = mapOf(
    "rtok" to """<div class="layer pos-a" data-depth="40" aria-hidden="true"><div class="chip glass glass--thick delta-pill float"><span class="delta-pill__sym">Δ</span><span><strong>Δtok <span class="delta-pill__num">−7 / 16</span></strong><small>illustration · every saving is a ledger row</small></span></div></div><div class="layer pos-b" data-depth="56" aria-hidden="true"><div class="chip glass glass--thick float float--slow"><ul class="legend"><li><i class="k"></i>kept</li><li><i class="t"></i>thinned</li><li><i class="c"></i>cut · <code>rtok expand &lt;id&gt;</code></li></ul></div></div>""",
    "cox" to """<div class="layer pos-b" data-depth="48"><div class="chip glass glass--thick status-card float"><div class="status-row"><span>Status</span><span style="display:inline-flex;align-items:center;gap:8px"><span class="status-led" aria-hidden="true"></span><b>Active development</b></span></div><div class="status-row"><span>APIs</span><b>Not yet stable</b></div><div class="status-row"><span>Release</span><b>No stable release yet</b></div><div class="status-row"><span>Surfaces</span><b>TUI · headless · ACP · MCP</b></div></div></div>""",
    "runa" to """<div class="layer pos-c" data-depth="48" aria-hidden="true"><div class="chip glass glass--thick term-chip float"><span><span class="dim">$</span> runa fit --recommend --use code</span><span class="ok">✓ memory + speed forecast · before any download</span><span><span class="dim">$</span> <span class="caret"></span></span></div></div>""",
    "weft" to """<div class="layer pos-c" data-depth="48" aria-hidden="true"><div class="chip glass glass--thick term-chip weft-chip float"><span><span class="dim">&lt;</span>button <span class="weft-chip__attr">id</span>=<span class="weft-chip__val">"go"</span> <span class="weft-chip__attr">variant</span>=<span class="weft-chip__val">"primary"</span><span class="dim">/&gt;</span></span><span class="ok">✓ valid against the catalog · patch by id</span><span><span class="dim">$</span> weft validate --strict <span class="caret"></span></span></div></div>""",
    "swarfr" to """<div class="layer pos-b" data-depth="48" aria-hidden="true"><div class="chip glass glass--thick swarf-gauge float"><span class="swarf-gauge__label">Two fresh targets · 587-crate workspace</span><span class="swarf-gauge__row"><span>before</span><i style="--w:100%"></i><b>3.63 GiB</b></span><span class="swarf-gauge__row swarf-gauge__row--after"><span>after</span><i style="--w:25.6%"></i><b>0.93 GiB</b></span><small>blocks on disk · 0 units rebuilt (docs/bench.md)</small></div></div>""",
    "aulo" to """<div class="layer pos-c" data-depth="48" aria-hidden="true"><div class="chip glass glass--thick aulo-chip float"><span class="aulo-chip__wave"><i></i><i></i><i></i><i></i><i></i><i></i><i></i></span><span><strong>aulod</strong><small>voice · shell · apps · browser · MCP</small></span></div></div>""",
    "scull" to """<div class="layer pos-c" data-depth="48" aria-hidden="true"><div class="chip glass glass--thick term-chip float"><span><span class="dim">$</span> just check</span><span class="ok">✓ builds · lints · tests the workspace</span><span><span class="dim">$</span> <span class="caret"></span></span></div></div>""",
    "kerf" to """<div class="layer pos-c" data-depth="48" aria-hidden="true"><div class="chip glass glass--thick term-chip kerf-chip float"><span><span class="dim">[plugin]</span> permissions = manifest</span><span class="ok">✓ sandboxed WASM · cannot spawn processes</span><span><span class="dim">core →</span> LSP · DAP</span></div></div>""",
    "stator" to """<div class="layer pos-c" data-depth="48" aria-hidden="true"><div class="chip glass glass--thick term-chip float"><span><span class="dim">$</span> stator explain app.ts</span><span class="ok">static · dynamic · error · not-yet</span><span><span class="dim">$</span> <span class="caret"></span></span></div></div>""",
    "bindsmith" to """<div class="layer pos-c" data-depth="48" aria-hidden="true"><div class="chip glass glass--thick bs-chips float"><small>bindsmith.yaml → six platforms</small><span class="bs-chips__row"><b>Android</b><b>iOS</b><b>macOS</b><b>Windows</b><b>Linux</b><b>Web</b></span></div></div>""",
    "ketch" to """<div class="layer pos-c" data-depth="48" aria-hidden="true"><div class="chip glass glass--thick term-chip float"><span><span class="dim">$</span> ketch install BurntSushi/ripgrep</span><span class="ok">✓ SHA-256 verified · linked onto PATH</span><span><span class="dim">$</span> <span class="caret"></span></span></div></div>""",
)

/** One product page (port of src/pages/[slug].astro). */
fun Ctx.productPage(entry: Project): String {
    val cat = catalog
    val slug = entry.id
    val theme = Site.theme(slug)
    val s = Sections.split(entry.body)
    val overview = Sections.renderMd(s["overview"] ?: "")
    val features = Sections.features(s["features"])
    val installHtml = Sections.renderMd(s["install"] ?: "")
    val usage = Sections.usage(s["usage examples"] ?: s["usage"])
    val links = Sections.links(s["links"])
    val caveat = Site.CAVEATS[slug]
    val lic = Site.licenseFor(slug)
    val demo = Data.showcase.demos[slug] ?: Data.showcase.demoFromUsage(entry.rawTitle, usage.map { it.code }, entry.title)
    val flow = Data.showcase.flows[slug]
    val pro = cat.proFor(entry)
    val withPro = cat.hasPro(entry)
    val pd = if (slug in docs.productsWithDocs()) docs.get(slug) else null
    val gs = pd?.pages?.find { it.slug == "getting-started" && it.file == null }
    val docsCta = pd?.let { if (gs != null) docs.href(slug, gs.slug) to "Get started" else docs.href(slug) to "Read the docs" }
    val icons = ICON_SETS[slug] ?: ICON_SETS.getValue("rtok")
    val pageTitle = Seo.productTitle(entry)
    val pageDescription = Seo.productDescription(entry, s["overview"])
    val jsonLd = listOf(
        seo.softwareApplication(entry, pageDescription, s["install"], features.map { it.name }),
        seo.sourceCode(entry),
        seo.breadcrumbs(listOf(Site.BRAND to seo.abs("/"), entry.title to seo.abs("/$slug/"))),
    )
    val ctx = this
    val meta = PageMeta(
        path = "/$slug/", title = pageTitle, description = pageDescription, theme = slug,
        accent = entry.accent, accent2 = entry.accent2, accentLight = entry.accentLight, imageAlt = "${entry.title} — ${entry.tagline}", jsonLd = jsonLd,
    )
    return page(meta) {
        section("hero container") {
            attr("aria-labelledby", "p-title")
            div("hero__copy") {
                nav("breadcrumb") { attr("aria-label", "Breadcrumb"); a(url("/#catalog")) { +"Open source" }; span { attr("aria-hidden", "true"); +"/" }; span { attr("aria-current", "page"); +entry.title } }
                div("p-meta") {
                    a(url("/#cat-${cat.categoryFor(entry).id}"), classes = "badge badge--cat") { +cat.categoryFor(entry).label }
                    span("badge badge--version") { +cat.statusFor(entry) }
                    span("badge badge--line") { +lic.short }
                }
                h1("p-title") { id = "p-title"; +entry.title; span { +"." } }
                p("lead") { +entry.tagline }
                div("install-hero") { raw(Sections.codeBlock(entry.install, "bash", "Install ${entry.title}")) }
                div("hero__ctas") {
                    a(entry.repo, classes = "btn btn--primary btn--lg") { icon("github", 18); +" View on GitHub" }
                    if (docsCta != null) a(docsCta.first, classes = "btn btn--glass btn--lg") { +"${docsCta.second} "; icon("arrow", 18) }
                    if (docsCta == null && entry.homepage != null) a(entry.homepage, classes = "btn btn--glass btn--lg") { +"Documentation" }
                }
                if (caveat != null) p("note") { icon("info", 16); span { +caveat } }
            }
            div("stage") {
                attr("data-stage", "")
                div("stage__tilt") {
                    div("stage__art layer") { attr("data-depth", "10"); picture(ctx, theme.images, "hero", listOf(1200, 2400), "(min-width: 1024px) 60vw, 100vw", 2400, 1600, alt = HERO_ALT[slug] ?: "", eager = true) }
                    HERO_CHIPS[slug]?.let { raw(it) }
                }
            }
        }

        // SHOWCASE: animated terminal (real README commands) + product visual
        section("section section--tight container") {
            id = "demo"; attr("aria-labelledby", "demo-title")
            header("section__head") { p("eyebrow") { +"See it run" }; h2("h2") { id = "demo-title"; +"${entry.title}, in the terminal." } }
            when {
                slug == "rtok" -> div("showcase") { terminal(demo, "$slug-term"); codeGraph() }
                slug == "cox" -> div("showcase showcase--stack") { coxScenes(Data.showcase.coxScenes); flow?.let { flowViz(it) } }
                slug == "ketch" && flow != null -> div("showcase showcase--stack") { terminal(demo, "$slug-term"); ketchRoute(flow) }
                else -> div(if (flow != null) "showcase showcase--stack" else "showcase") { terminal(demo, "$slug-term"); flow?.let { flowViz(it) } }
            }
        }

        if (slug == "rtok") section("section section--tight container") {
            attr("aria-labelledby", "ctx-title")
            header("section__head") { p("eyebrow") { +"Reversible by design" }; h2("h2") { id = "ctx-title"; +"Less context in. Nothing lost." } }
            tokenParticles()
        }

        section("section section--tight container") {
            attr("aria-labelledby", "ov-title")
            header("section__head") { p("eyebrow") { +"Overview" }; h2("h2") { id = "ov-title"; +"What ${entry.title} does." } }
            div("prose") { raw(overview) }
        }

        section("section container") {
            attr("aria-labelledby", "feat-title")
            header("section__head") { p("eyebrow") { +"Features" }; h2("h2") { id = "feat-title"; +"Built for real workflows." } }
            ul("grid grid--3") {
                attr("role", "list")
                features.forEachIndexed { i, f ->
                    li("card glass shine card--lift feature") {
                        span("icon") { attr("aria-hidden", "true"); icon(icons[i % icons.size]) }
                        h3("h3") { +f.name }
                        p { raw(f.html) }
                    }
                }
            }
        }

        section("section container") {
            id = "install"; attr("aria-labelledby", "inst-title")
            header("section__head") { p("eyebrow") { +"Install" }; h2("h2") { id = "inst-title"; +"Up and running in a minute." } }
            div("install-grid") {
                div("card glass install-card") { h3("h3") { +"Install" }; div("prose") { raw(installHtml) } }
                div("card glass install-card") {
                    h3("h3") { +"Usage" }
                    usage.take(1).forEach { g -> p { raw(g.caption) }; raw(Sections.codeBlock(g.code, g.lang, g.caption.replace(Regex("<[^>]+>"), "").ifEmpty { "Example" })) }
                    p("muted small") { +"More examples below." }
                }
            }
        }

        if (usage.size > 1) section("section section--tight container") {
            attr("aria-labelledby", "use-title")
            header("section__head") { p("eyebrow") { +"Usage examples" }; h2("h2") { id = "use-title"; +"Real commands, straight from the docs." } }
            div("usage") {
                usage.drop(1).forEach { g ->
                    div("card glass") { p { raw(g.caption) }; raw(Sections.codeBlock(g.code, g.lang, g.caption.replace(Regex("<[^>]+>"), "").ifEmpty { "Example" })) }
                }
            }
        }

        section("section container") {
            attr("aria-labelledby", "links-title")
            header("section__head") { p("eyebrow") { +"Links" }; h2("h2") { id = "links-title"; +"Go deeper." } }
            ul("links") {
                attr("role", "list")
                if (pd != null) li { a(docs.href(slug), classes = "glass shine") { +"${entry.title} docs on this site · ${pd.pages.size} pages"; span { attr("aria-hidden", "true"); +"→" } } }
                links.forEach { l -> li { a(l.href, classes = "glass shine") { attr("rel", "noopener"); +l.label; span { attr("aria-hidden", "true"); +"↗" } } } }
            }
            div("license card glass") {
                h3("h3") { +"License" }
                p("muted small") { +lic.lead.ifEmpty { "You can use ${entry.title} under any of the following licenses, at your choice:" } }
                ol { lic.items.forEach { l -> li { span { strong { +l.name }; +" — ${l.text}" } } } }
            }
        }

        // PRICING: Free/OSS (real) + Pro (PLACEHOLDER). With API_URL the plans widget swaps the tiers for the
        // backend's plans; without JS, without the API or when it fails, the placeholders stay.
        val livePlans = withPro && assets.plansJs != null
        section("section section--tight container") {
            id = "pricing"; attr("aria-labelledby", "pr-title")
            if (livePlans) { attr("data-plans", slug); attr("data-api", cfg.apiUrl) }
            header("section__head") {
                div("section-flag") { p("eyebrow") { +"Pricing" }; if (withPro) span("badge badge--placeholder") { attr("data-plans-placeholder", ""); +"Pro is a placeholder" } }
                h2("h2") { id = "pr-title"; +(if (withPro) "Free forever. Pro when you need it." else "${entry.title} is free.") }
            }
            if (livePlans) planTemplate(entry)
            div(if (withPro) "grid pricing-pair grid--2" else "grid pricing-pair pricing-pair--free") {
                attr("data-plans-grid", "")
                article("card glass tier") {
                    attr("aria-labelledby", "t-free")
                    div("tier__head") { h3("h3") { id = "t-free"; +"Free · open source" }; span("badge badge--line") { +lic.short } }
                    if (withPro) p("price") { span("price__num") { +"$0" }; span("muted small") { +"forever" } }
                    else p("price") { span("price__num") { +"Free" }; span("muted small") { +"no paid plans" } }
                    ul("checklist") { (Site.FREE_HIGHLIGHTS[slug] ?: listOf("The full open-source tool")).forEach { f -> li { icon("check", 16); +" $f" } } }
                    a("#install", classes = "btn btn--glass btn--block") { +"Install free" }
                }
                if (withPro) article("card glass tier tier--featured") {
                    attr("aria-labelledby", "t-pro")
                    div("tier__head") { h3("h3") { id = "t-pro"; +"${entry.title} Pro" }; span("badge badge--placeholder") { +"Placeholder" } }
                    p("price") { span("price__num") { span { attr("data-pro-price", slug); +pro.price }; +"*" }; span("muted small") { +"per seat" } }
                    ul("checklist") { pro.features.forEach { f -> li { icon("check", 16); +" $f" } } }
                    proWishlist(entry, "t-pro-note")
                    p("muted small") { id = "t-pro-note"; +"${Site.PRO_PRICE_NOTE} Placeholder — the Pro wishlist is not open yet." }
                }
            }
        }

        section("container") {
            attr("aria-labelledby", "cta-title")
            div("cta cta--prop glass glass--thick tw:max-[959px]:grid-cols-[minmax(0,1fr)]") {
                picture(ctx, theme.images, "prop", listOf(400, 800), "360px", 800, 800, cls = "cta__prop")
                div("cta__copy") { h2("h2") { id = "cta-title"; +"Try ${entry.title} today." }; p("muted") { +"Free and open source. Star it, file an issue, or send a pull request." } }
                div("cta__actions") {
                    if (docsCta != null) a(docsCta.first, classes = "btn btn--glass btn--lg") { +"${docsCta.second} "; icon("arrow", 18) }
                    a(entry.repo, classes = "btn btn--primary btn--lg tw:max-w-full tw:max-sm:whitespace-normal! tw:max-sm:py-2 tw:max-sm:text-center") {
                        icon("github", 18); +" "
                        span("tw:min-w-0") { +"github.com/"; raw("<wbr>"); +"pyrlyn/"; raw("<wbr>"); +slug }
                    }
                }
            }
        }
    }
}
