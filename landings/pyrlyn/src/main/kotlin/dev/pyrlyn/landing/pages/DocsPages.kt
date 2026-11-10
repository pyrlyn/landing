package dev.pyrlyn.landing.pages

import dev.pyrlyn.landing.content.Catalog
import dev.pyrlyn.landing.content.Data
import dev.pyrlyn.landing.content.Docs
import dev.pyrlyn.landing.content.Project
import dev.pyrlyn.landing.content.Sections
import dev.pyrlyn.landing.content.Site
import dev.pyrlyn.landing.html.*
import dev.pyrlyn.landing.seo.Seo
import kotlinx.html.*

/** Docs shell: sticky docs nav, centred column, optional TOC and prev/next (port of DocsLayout.astro). */
private fun Ctx.docsLayout(
    tool: Project, current: String?, title: String, description: String, toc: List<Docs.TocEntry> = emptyList(), wide: Boolean = false,
    headline: String? = null, section: String? = null, sourceUrl: String? = null, body: DIV.() -> Unit,
): String {
    val pd = docs.get(tool.id)
    val a = catalog.accentsFor(tool)
    val order = listOf<Pair<String?, String>>(null to "Overview") + pd.pages.map { it.slug to it.navTitle }
    val idx = order.indexOfFirst { it.first == current }
    val prev = if (idx > 0) order[idx - 1] else null
    val next = if (idx >= 0 && idx < order.size - 1) order[idx + 1] else null
    val path = if (current != null) "/${tool.id}/docs/$current/" else "/${tool.id}/docs/"
    val url = seo.abs(path)
    val metaDescription = Seo.clip(description, 160)
    val crumbs = listOf(Site.BRAND to seo.abs("/"), tool.title to seo.abs("/${tool.id}/"), "${tool.title} docs" to seo.abs("/${tool.id}/docs/")) +
        (if (current != null) listOf((order.getOrNull(idx)?.second ?: title) to url) else emptyList())
    val jsonLd = listOf(seo.techArticle(tool, headline ?: title, metaDescription, url, sourceUrl, section), seo.breadcrumbs(crumbs))
    val showToc = !wide && toc.count { it.depth == 2 } >= 3
    val meta = PageMeta(
        path = path, title = title, description = metaDescription, theme = tool.id, accent = a.accent, accent2 = a.accent2, accentLight = a.accentLight,
        ogType = "article", imageAlt = "${tool.title} — ${tool.tagline}", jsonLd = jsonLd, extraCss = assets.docsCss,
    )
    return page(meta) {
        docsNav(this@docsLayout, pd, tool.title, current)
        div(listOfNotNull("docs container", if (wide) "docs--wide" else null, if (showToc) "docs--toc" else null).joinToString(" ")) {
            div("docs__main") {
                body()
                nav("pager") {
                    attr("aria-label", "Previous and next page")
                    if (prev != null) a(docs.href(tool.id, prev.first), classes = "pager__link glass shine") { attr("rel", "prev"); small { +"← Previous" }; strong { +prev.second } } else span {}
                    if (next != null) a(docs.href(tool.id, next.first), classes = "pager__link pager__link--next glass shine") { attr("rel", "next"); small { +"Next →" }; strong { +next.second } }
                }
            }
            if (showToc) aside("toc") {
                attr("aria-labelledby", "toc-title"); attr("data-toc", "")
                p("toc__title") { id = "toc-title"; +"On this page" }
                ul { attr("role", "list"); toc.filter { it.depth == 2 }.forEach { t -> li { a("#${t.id}") { attr("data-toc-link", ""); +t.text } } } }
            }
        }
    }
}

/** Sticky horizontal docs navigation (port of DocsNav.astro). */
private fun FlowContent.docsNav(ctx: Ctx, pd: Docs.ProductDocs, title: String, current: String?) {
    data class It(val slug: String?, val navTitle: String, val group: String)
    val items = listOf(It(null, "Overview", "")) + pd.pages.map { It(it.slug, it.navTitle, it.group) }
    val currentItem = items.find { it.slug == current } ?: items[0]
    val compact = items.size > 7
    nav("dnav") {
        attr("aria-label", "$title documentation"); attr("data-dnav", "")
        div("dnav__inner") {
            a(ctx.docs.href(pd.product), classes = "dnav__brand") {
                span("dnav__mark") { attr("aria-hidden", "true"); +title.take(2) }
                span("dnav__name") { +"$title "; span { +"Docs" } }
            }
            span("dnav__sep") { attr("aria-hidden", "true") }
            div(if (compact) "dnav__scroller dnav__scroller--compact" else "dnav__scroller") {
                attr("data-dnav-scroller", "")
                ul("dnav__tabs") {
                    attr("role", "list")
                    items.forEachIndexed { i, it ->
                        li(if (i > 1 && it.group != items[i - 1].group) "dnav__gap" else null) {
                            a(ctx.docs.href(pd.product, it.slug), classes = "dnav__tab") { if (it.slug == current) attr("aria-current", "page"); +it.navTitle }
                        }
                    }
                }
                span("dnav__indicator") { attr("aria-hidden", "true") }
            }
            if (compact) details("dnav__sections") {
                attr("data-menu", "")
                summary("dnav__summary") {
                    span("dnav__summary-k") { +"Section" }; +" "; span("dnav__summary-v") { +currentItem.navTitle }
                    raw("<svg viewBox=\"0 0 12 12\" width=\"12\" height=\"12\" aria-hidden=\"true\"><path d=\"m3 4.5 3 3 3-3\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.6\" stroke-linecap=\"round\" stroke-linejoin=\"round\"></path></svg>")
                }
                div("dnav__panel glass glass--thick") {
                    a(ctx.docs.href(pd.product), classes = "dnav__item") { if (current == null) attr("aria-current", "page"); +"Overview" }
                    pd.groups.forEach { g ->
                        div("dnav__group") {
                            p("dnav__group-label") { +g.label }
                            g.pages.forEach { p -> a(ctx.docs.href(pd.product, p.slug), classes = "dnav__item") { if (p.slug == current) attr("aria-current", "page"); +p.navTitle } }
                        }
                    }
                }
            }
        }
    }
}

private data class Tile(var w: Int, val h: Int, var r: Int = 0, var c: Int = 0)

/** Bento packing: dense first-fit; tiles grow right into free cells so no row ends with a hole. */
private fun pack(want: List<Pair<Int, Int>>, cols: Int): List<Tile> {
    val grid = mutableMapOf<Pair<Int, Int>, Boolean>()
    fun free(r: Int, c: Int) = grid[r to c] != true
    val out = want.map { Tile(it.first, it.second) }
    for (t in out) {
        t.w = minOf(t.w, cols)
        var r = 0
        placing@ while (true) {
            for (c in 0..cols - t.w) {
                var ok = true
                for (dr in 0 until t.h) for (dc in 0 until t.w) if (ok) ok = free(r + dr, c + dc)
                if (ok) {
                    for (dr in 0 until t.h) for (dc in 0 until t.w) grid[(r + dr) to (c + dc)] = true
                    t.r = r; t.c = c
                    break@placing
                }
            }
            r++
        }
    }
    for (t in out) while (t.c + t.w < cols && (0 until t.h).all { free(t.r + it, t.c + t.w) }) {
        for (dr in 0 until t.h) grid[(t.r + dr) to (t.c + t.w)] = true
        t.w++
    }
    return out
}

/** Docs overview per product (port of src/pages/[slug]/docs/index.astro). */
fun Ctx.docsIndexPage(tool: Project): String {
    val pd = docs.get(tool.id)
    val o = Data.overviews[tool.id]
    val bySlug = pd.pages.associateBy { it.slug }
    val nextPages = o?.next.orEmpty().mapNotNull { bySlug[it] }
    val first = pd.pages.firstOrNull()
    val lead = o?.lead ?: tool.tagline
    val shortSha = pd.source?.sha?.take(7)
    val description = Seo.docsIndexDescription(tool.title, tool.tagline, pd.pages.size, pd.groups.map { it.label })
    val lay3 = pack(pd.groups.mapIndexed { i, g -> (if (i == 0) 2 else 1) to (if (i > 0 && g.pages.size >= 4) 2 else 1) }, 3)
    val lay2 = pack(pd.groups.mapIndexed { i, _ -> (if (i == 0) 2 else 1) to 1 }, 2)
    val repo = pd.source?.repo ?: "pyrlyn/${tool.id}"
    return docsLayout(tool, null, Seo.docsIndexTitle(tool.title), description, headline = o?.headline ?: "${tool.title} documentation", wide = true) {
        section("dhero") {
            attr("aria-labelledby", "dhero-title")
            div("dhero__copy") {
                p("eyebrow") { +(o?.eyebrow ?: "${tool.title} documentation") }
                h1("dhero__title") { id = "dhero-title"; +(o?.headline ?: tool.tagline) }
                p("lead") { raw(Sections.renderInline(lead)) }
                div("hero__ctas") {
                    if (first != null) a(docs.href(tool.id, first.slug), classes = "btn btn--primary btn--lg") { +"Start with ${first.navTitle} "; span { attr("aria-hidden", "true"); +"→" } }
                    a(url("/${tool.id}/"), classes = "btn btn--glass btn--lg") { +"About ${tool.title}" }
                }
                p("dhero__src") {
                    +"${pd.pages.size} pages from "
                    a("https://github.com/$repo/tree/${pd.source?.ref ?: "main"}/docs") { attr("rel", "noopener"); code { +"$repo/docs" } }
                    if (shortSha != null) { +" at "; code { +shortSha } }
                    +", ${Catalog.versionLabel(tool)}"
                }
            }
            if (o != null) ol("cmdstrip glass glass--thick") {
                attr("aria-label", "Commands to get going")
                o.strip.forEachIndexed { i, s ->
                    li("cmdstrip__row") {
                        span("cmdstrip__n") { attr("aria-hidden", "true"); +(i + 1).toString().padStart(2, '0') }
                        span("cmdstrip__label") { +s.label }
                        raw(Docs.docCodeBlock(s.cmd, "bash"))
                    }
                }
            }
        }
        if (o != null) {
            section("dsec") {
                attr("aria-labelledby", "how-title")
                header("section__head") { p("eyebrow") { +"How it works" }; h2("h2") { id = "how-title"; +"From install to first result." } }
                ol("rail") {
                    o.steps.forEachIndexed { i, s ->
                        li("rail__step") {
                            style = "--i:$i"
                            span("rail__n") { attr("aria-hidden", "true"); +"${i + 1}" }
                            div("rail__body") { h3("h3") { +s.title }; p { raw(Sections.renderInline(s.text)) }; s.cmd?.let { raw(Docs.docCodeBlock(it, "bash")) } }
                        }
                    }
                }
            }
            section("dsec split") {
                attr("aria-labelledby", "concept-title")
                div("split__copy") {
                    p("eyebrow") { +o.concept.eyebrow }
                    h2("h2") { id = "concept-title"; +o.concept.title }
                    div("prose prose--tight") { raw(Sections.renderMd(o.concept.body)) }
                }
                figure("split__code glass") { raw(Docs.docCodeBlock(o.concept.code, o.concept.lang)); figcaption { +o.concept.caption } }
            }
        }
        section("dsec") {
            attr("aria-labelledby", "paths-title")
            header("section__head") { p("eyebrow") { +"Paths into the docs" }; h2("h2") { id = "paths-title"; +"Pick where to go next." } }
            ul("bento") {
                attr("role", "list")
                pd.groups.forEachIndexed { gi, g ->
                    val cls = listOfNotNull("bento__tile glass shine", if (gi == 0) "bento__tile--lead" else null, if (lay3[gi].w >= 2 && g.pages.size >= 4) "bento__tile--cols" else null)
                    li(cls.joinToString(" ")) {
                        style = "--c3:span ${lay3[gi].w};--r3:span ${lay3[gi].h};--c2:span ${lay2[gi].w}"
                        p("bento__label") { +g.label; span { +"${g.pages.size} ${if (g.pages.size == 1) "page" else "pages"}" } }
                        ul("bento__list") {
                            attr("role", "list")
                            g.pages.forEachIndexed { pi, p ->
                                li {
                                    a(docs.href(tool.id, p.slug), classes = "bento__link") {
                                        strong { +p.navTitle; span { attr("aria-hidden", "true"); +" →" } }
                                        if ((gi == 0 || pi < 3) && p.summary.isNotEmpty()) small { +p.summary }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (o != null) {
            section("dsec duo") {
                attr("aria-labelledby", "table-title")
                div("duo__table") {
                    p("eyebrow") { +o.table.eyebrow }
                    h2("h3 duo__title") { id = "table-title"; +o.table.title }
                    div("table-wrap") {
                        attr("tabindex", "0"); attr("role", "region"); attr("aria-labelledby", "table-title")
                        table {
                            thead { tr { th { attr("scope", "col"); +o.table.head[0] }; th { attr("scope", "col"); +o.table.head[1] } } }
                            tbody { o.table.rows.forEach { r -> tr { td { raw(Sections.renderInline(r[0])) }; td { raw(Sections.renderInline(r[1])) } } } }
                        }
                    }
                }
                aside("callout callout--${o.notice.kind} duo__notice") {
                    p("callout__label") { icon("info", 16); +" ${o.notice.label}" }
                    p { raw(Sections.renderInline(o.notice.text)) }
                }
            }
            section("dsec") {
                attr("aria-labelledby", "faq-title")
                header("section__head") { p("eyebrow") { +"Questions the docs answer" }; h2("h2") { id = "faq-title"; +"Good to know." } }
                div("faq faq--docs") { o.faq.forEach { f -> details("faq__item glass") { summary { +f.q }; div("faq__body") { p { raw(Sections.renderInline(f.a)) } } } } }
            }
        }
        if (nextPages.isNotEmpty()) section("dsec") {
            attr("aria-labelledby", "next-title")
            header("section__head") { p("eyebrow") { +"Next steps" }; h2("h2") { id = "next-title"; +"Read these first." } }
            ol("nextcards") {
                attr("role", "list")
                nextPages.forEachIndexed { i, p ->
                    li {
                        a(docs.href(tool.id, p.slug), classes = "nextcard glass shine card--lift") {
                            span("nextcard__n") { attr("aria-hidden", "true"); +(i + 1).toString().padStart(2, '0') }
                            strong { +p.navTitle }
                            if (p.summary.isNotEmpty()) small { +p.summary }
                            span("nextcard__go") { attr("aria-hidden", "true"); +"Read →" }
                        }
                    }
                }
            }
        }
    }
}

/** One page per synced doc plus site-authored pages (port of src/pages/[slug]/docs/[doc].astro). */
fun Ctx.docPage(tool: Project, page: Docs.Page): String {
    val pd = docs.get(tool.id)
    val authored = page.file == null && "${tool.id}/${page.slug}" == "ketch/getting-started"
    val shared = page.summary.isNotEmpty() && pd.pages.any { it !== page && it.summary == page.summary }
    val shortSha = pd.source?.sha?.take(7)
    return docsLayout(
        tool, page.slug, Seo.documentationTitle(page.title, tool.title, page.file),
        Seo.documentationDescription(page.title, tool.title, page.summary, tool.tagline, shared),
        toc = page.toc, wide = authored, headline = page.title, section = page.group, sourceUrl = page.sourceUrl,
    ) {
        if (authored) ketchGettingStarted(this@docPage, tool) else article("doc") {
            attr("aria-labelledby", "doc-title")
            header("doc__head") {
                nav("breadcrumb") { attr("aria-label", "Breadcrumb"); a(docs.href(tool.id)) { +"${tool.title} docs" }; span { attr("aria-hidden", "true"); +"/" }; span { +page.group } }
                h1("doc__title") { id = "doc-title"; +page.title }
                p("doc__meta") {
                    span { +"From "; code { +"docs/${page.file}" }; if (shortSha != null) { +" at "; code { +shortSha } } }
                    page.sourceUrl?.let { a(it) { attr("rel", "noopener"); +"View on GitHub "; span { attr("aria-hidden", "true"); +"↗" } } }
                }
            }
            div("prose prose--doc") { raw(page.html) }
        }
    }
}

/** ketch getting started: a vertical three-step rail (port of docs/KetchGettingStarted.astro). */
private fun FlowContent.ketchGettingStarted(ctx: Ctx, tool: Project) {
    val alternatives = tool.installAlternatives
    val steps = listOf(
        Triple("Install Ketch", "One script on macOS and Linux. It runs `ketch self install` and `ketch path install`, so Ketch ends up in `~/.ketch` as one of its own packages. Then make sure `~/.ketch/bin` is on your `PATH` — `ketch doctor` tells you if it is not.", tool.install),
        Triple("Install your first tool", "Point Ketch at any repository that publishes releases. It picks the asset built for your machine, checks the SHA-256 the release published, and links the binary onto your `PATH`.", "ketch install pyrlyn/rtok"),
    )
    article("gs") {
        attr("aria-labelledby", "gs-title")
        header("gs__head") {
            nav("breadcrumb") { attr("aria-label", "Breadcrumb"); a(ctx.docs.href(tool.id)) { +"${tool.title} docs" }; span { attr("aria-hidden", "true"); +"/" }; span { +"Start" } }
            h1("dhero__title") { id = "gs-title"; +"Getting started with ${tool.title}" }
            p("lead") { +"Three steps from nothing to a verified tool on your "; code("inline-code") { +"PATH" }; +". Ketch is a command-line tool: there is no account to create and nothing to sign up for." }
        }
        ol("gs__rail") {
            steps.forEachIndexed { i, (title, text, code) ->
                li("gs__step") {
                    style = "--i:$i"
                    span("gs__n") { attr("aria-hidden", "true"); +"${i + 1}" }
                    div("gs__body glass") {
                        h2("h3 gs__title") { span("sr-only") { +"Step ${i + 1}: " }; +title }
                        p { raw(Sections.renderInline(text)) }
                        raw(Docs.docCodeBlock(code, "bash"))
                        if (i == 0 && alternatives.isNotEmpty()) details("gs__alt") {
                            summary { +"Windows, Homebrew or mise" }
                            alternatives.forEach { c -> raw(Docs.docCodeBlock(c, if (c.startsWith("irm ")) "powershell" else "bash")) }
                        }
                    }
                }
            }
            li("gs__step") {
                style = "--i:2"
                span("gs__n") { attr("aria-hidden", "true"); +"3" }
                div("gs__body glass") {
                    h2("h3 gs__title") { span("sr-only") { +"Step 3: " }; +"See what it did" }
                    p { +"Ketch reports each stage: resolving the package, downloading the asset for your platform, verifying the checksum, and the installed version. The tool is on your "; code("inline-code") { +"PATH" }; +" straight away." }
                    raw("""<figure class="term glass glass--thick gs__term"><div class="term__bar"><span class="term__dots" aria-hidden="true"><i></i><i></i><i></i></span><span class="term__title">ketch — install &amp; verify</span></div><pre class="term__body gs__out" tabindex="0" aria-label="Example output"><span class="gs__p">$</span> ketch install pyrlyn/rtok
<span class="gs__dim">resolving</span>   rtok (github:pyrlyn/rtok)
<span class="gs__dim">downloading</span> rtok-aarch64-apple-darwin.tar.xz
<span class="gs__ok">verified</span>    sha256 published by the release
<span class="gs__ok">installed</span>   rtok 0.1.0

<span class="gs__p">$</span> rtok --version
rtok 0.1.0</pre><figcaption class="term__cap">Example output from the Ketch README demo (Apple silicon, rtok 0.1.0). Asset name and version depend on your machine and the latest release.</figcaption></figure>""")
                }
            }
        }
        section("gs__next") {
            attr("aria-labelledby", "gs-next")
            h2("h3") { id = "gs-next"; +"Where to go next" }
            ul("gs__links") {
                attr("role", "list")
                listOf(
                    Triple("commands", "Commands", "Every command, its form and one working example."),
                    Triple("lockfile", "Lockfile", "Pin a machine's tools and reproduce them elsewhere."),
                    Triple("troubleshooting", "Troubleshooting", "Failures Ketch already explains, in one place."),
                ).forEach { (s, t, d) -> li { a(ctx.docs.href(tool.id, s), classes = "glass shine") { strong { +t }; small { +d } } } }
            }
        }
    }
}
