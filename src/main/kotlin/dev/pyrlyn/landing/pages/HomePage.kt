package dev.pyrlyn.landing.pages

import dev.pyrlyn.landing.content.Catalog
import dev.pyrlyn.landing.content.Data
import dev.pyrlyn.landing.content.Sections
import dev.pyrlyn.landing.content.Site
import dev.pyrlyn.landing.html.*
import kotlinx.html.*
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/** Marketplace home (port of src/pages/index.astro). */
fun Ctx.homePage(): String {
    val cat = catalog
    val tools = cat.tools
    val groups = cat.groupByCategory()
    val featured = cat.featured
    val fSections = Sections.split(featured.body)
    fun firstSentence(html: String) = Regex("^(.*?[.!?])(\\s|$)").find(html)?.groupValues?.get(1) ?: html
    val fFeatures = Sections.features(fSections["features"]).take(3).map { it.copy(html = firstSentence(it.html)) }
    val fDemo = Data.showcase.demos[featured.id] ?: Data.showcase.demoFromUsage(
        featured.rawTitle, Sections.usage(fSections["usage examples"] ?: fSections["usage"]).map { it.code }, featured.title,
    )
    val minPro = tools.filter(cat::hasPro).map { cat.proFor(it).price }
        .sortedBy { it.replace(Regex("[^\\d.]"), "").toDoubleOrNull() ?: Double.NaN }.firstOrNull()
    val homeTitle = "${Site.BRAND}: local AI developer tools for agentic coding"
    val homeDescription = "Pyrlyn builds local-first developer tools for agentic coding: Rtok, Ketch and Mailune."
    val jsonLd = listOf(
        seo.organization(),
        seo.website(homeDescription),
        buildJsonObject {
            put("@type", "ItemList"); put("name", "Catalog"); put("url", seo.abs("/#catalog"))
            put("itemListElement", JsonArray(tools.mapIndexed { i, p ->
                buildJsonObject {
                    put("@type", "ListItem"); put("position", i + 1); put("name", p.title); put("url", seo.abs("/${p.id}/"))
                    put("item", buildJsonObject { put("@id", seo.appId(p.id)) })
                }
            }))
        },
    )
    val glyph = mapOf("rtok" to "Δ", "cox" to "◎", "ketch" to "↓", "runa" to "ᛉ", "mailune" to "✉", "weft" to "⋕", "swarfr" to "@", "aulo" to "∿", "scull" to "▌", "kerf" to "⌿", "stator" to "⟨⟩", "bindsmith" to "⚒")
    val hotspots = listOf(
        Hotspot(80, 58, "rtok", "Rtok · code graph", Site.theme("rtok").accent, "/rtok/", "Code graph over MCP: agents query symbol, callers, impact, outline and explore instead of grep-and-read."),
        Hotspot(35, 64, "ketch", "Ketch · verified installs", Site.theme("ketch").accent, "/ketch/", "Installs from any repo that ships releases and checks the published SHA-256 sums against what landed on disk."),
        Hotspot(60, 34, "mailune", "Mailune · private AI mail", Site.theme("mailune").accent, "/mailune/", "Local-first mail with on-device AI for macOS, iOS, Windows, Linux, Android and the web."),
    )
    val ctx = this
    return page(PageMeta(path = "/", title = homeTitle, description = homeDescription, theme = "home", imageAlt = homeTitle, jsonLd = jsonLd)) {
        // HERO
        section("hero container") {
            attr("aria-labelledby", "hero-title"); attr("data-audience", "oss")
            div("hero__copy") {
                div("section-flag") { p("pill") { span("pill__dot") { attr("aria-hidden", "true") }; +" Marketplace · ${tools.size} tools · all open source" } }
                h1("display") { id = "hero-title"; +"Tools for agentic coding."; br; span("gradient-text") { +"Free to start." } }
                p("lead") { +"A curated catalog of AI developer tools — each one a fast, local binary with a free open-source edition. Add Pro to the tools your team relies on." }
                div("hero__ctas") {
                    a(url("/#catalog"), classes = "btn btn--primary btn--lg") { +"Browse the catalog "; icon("arrow", 18) }
                    a(url("/#pricing"), classes = "btn btn--glass btn--lg") { +"How pricing works" }
                }
                ul("hero__note") {
                    li { icon("check", 16); +" Free editions · ${Site.LICENSE.short}" }
                    li { icon("check", 16); +" Pro per tool "; span("badge badge--placeholder") { +"Placeholder" } }
                }
            }
            div("stage stage--ring") {
                attr("data-stage", "")
                div("stage__tilt") {
                    div("stage__art layer") { attr("data-depth", "10"); picture(ctx, "home", "hero", listOf(1200, 2400), "(min-width: 1024px) 60vw, 100vw", 2400, 1600, eager = true) }
                    div("stage__ring layer") { attr("data-depth", "0"); ringCarousel(ctx, tools, hotspots) }
                    div("layer pos-a") {
                        attr("data-depth", "34")
                        div("chip glass glass--thick toggle-chip float") {
                            span("toggle-chip__label") { id = "aud-label"; +"Pricing model" }
                            div("segmented") {
                                attr("role", "group"); attr("aria-labelledby", "aud-label"); attr("data-audience-toggle", "")
                                button(type = ButtonType.button) { attr("data-set-audience", "oss"); attr("aria-pressed", "true"); +"Free" }
                                button(type = ButtonType.button) { attr("data-set-audience", "pro"); attr("aria-pressed", "false"); +"Pro" }
                            }
                            div("toggle-chip__body") {
                                attr("aria-live", "polite")
                                span("for-oss") { strong { +"$0 · every tool" }; +"open-source editions" }
                                span("for-pro") { strong { +"from $minPro*" }; +"per tool · placeholder pricing" }
                            }
                        }
                    }
                }
            }
        }

        // FEATURED PRODUCT
        section("section container") {
            id = "featured"; attr("aria-labelledby", "feat-title")
            div("featured glass glass--thick") {
                style = "--card-accent:${featured.accent};--accent:${featured.accent}"
                div("featured__copy") {
                    div("section-flag") { p("eyebrow") { +"Featured tool" }; span("badge badge--version") { +Catalog.versionLabel(featured) } }
                    h2("featured__title") { id = "feat-title"; +featured.title; span { +"." } }
                    p("lead") { +featured.tagline }
                    ul("featured__list") {
                        fFeatures.forEach { f -> li { icon("check", 16); span { strong { +"${f.name}." }; +" "; span { raw(f.html) } } } }
                    }
                    div("hero__ctas") {
                        a(url("/${featured.id}/"), classes = "btn btn--primary btn--lg") { +"Explore ${featured.title} "; icon("arrow", 18) }
                        a(featured.repo, classes = "btn btn--glass btn--lg") { icon("github", 18); +" Source" }
                    }
                }
                div("featured__demo") {
                    picture(ctx, featured.id, "prop", listOf(400, 800), "320px", 800, 800, cls = "featured__prop")
                    terminal(fDemo, "featured-term")
                }
            }
        }

        // CATALOG — generated from content/projects/*.md
        section("section container") {
            id = "catalog"; attr("aria-labelledby", "cat-title")
            header("section__head") {
                p("eyebrow") { +"Catalog" }
                h2("h2") { id = "cat-title"; +"Every tool, free to start." }
                p("lead") { +"Each tool is a standalone binary with its own page, demo and install command." }
            }
            nav("cat-chips") {
                attr("aria-label", "Product categories")
                groups.forEach { g -> a("#cat-${g.category.id}", classes = "cat-chip") { +g.category.label; span { +"${g.tools.size}" } } }
            }
            groups.forEach { g ->
                div("cat-group") {
                    id = "cat-${g.category.id}"; attr("aria-labelledby", "cat-h-${g.category.id}")
                    header("cat-group__head") { h3("cat-group__title") { id = "cat-h-${g.category.id}"; +g.category.label }; p { +g.category.description } }
                    ul("grid grid--3") {
                        attr("role", "list")
                        g.tools.forEach { p ->
                            li("card glass shine card--lift oss-card") {
                                style = "--card-accent:${p.accent}"
                                span("oss-card__mesh") { attr("aria-hidden", "true") }
                                picture(ctx, p.id, "hero", listOf(800, 1200), "300px", 2400, 1600, cls = "oss-card__preview")
                                div("oss-card__top") {
                                    span("icon") { attr("aria-hidden", "true"); +(glyph[p.id] ?: p.rawTitle.take(1)) }
                                    span("badge badge--version") { style = "--accent:${p.accent}"; +Catalog.versionLabel(p) }
                                }
                                h3("oss-card__title") { +p.title; span { +"." } }
                                p { +p.tagline }
                                pre("code") {
                                    attr("role", "group"); attr("aria-label", "Install ${p.title}")
                                    code { span("tok-p") { attr("aria-hidden", "true"); +"$ " }; +Catalog.cardInstall(p) }
                                }
                                p("price-chips") {
                                    if (cat.hasPro(p)) {
                                        span("price-chip") { +"Free · OSS" }
                                        span("price-chip price-chip--pro") { +"Pro "; span { attr("data-pro-price", p.id); +cat.proFor(p).price }; +"*" }
                                    } else span("price-chip") { +"Free" }
                                }
                                a(url("/${p.id}/"), classes = "oss-card__link") { +"Explore ${p.title} "; icon("arrow", 16) }
                            }
                        }
                    }
                }
            }
            ul("grid grid--3") {
                attr("role", "list")
                li("card soon-card") {
                    attr("aria-label", "More tools coming")
                    span("icon") { attr("aria-hidden", "true"); icon("sparkle") }
                    h3("h3") { +"More tools coming" }
                    p { +"New tools join the catalog by adding one Markdown file — see the content contract." }
                }
            }
            p("footnote") { +Site.PRO_PRICE_NOTE }
        }

        // HOW IT WORKS
        section("section container") {
            id = "how"; attr("aria-labelledby", "how-title")
            header("section__head") { p("eyebrow") { +"How it works" }; h2("h2") { id = "how-title"; +"Pick. Install. Grow." } }
            ol("steps") {
                li("card glass step") { span("step__n") { attr("aria-hidden", "true"); +"01" }; h3("h3") { +"Pick a tool" }; p { +"Browse the catalog; every product page shows a live terminal demo and the exact install command." } }
                li("card glass step") { span("step__n") { attr("aria-hidden", "true"); +"02" }; h3("h3") { +"Install the free edition" }; p { +"One command — installer script, Ketch, Homebrew or your package manager. The open-source edition is the full tool." } }
                li("card glass step") { span("step__n") { attr("aria-hidden", "true"); +"03" }; h3("h3") { +"Add Pro per tool" }; p { +"Only for the tools your team relies on, on top of the same binary. "; span("badge badge--placeholder") { +"Placeholder" } } }
            }
        }

        // WHY THESE TOOLS
        section("section container") {
            attr("aria-labelledby", "why-title")
            header("section__head") { p("eyebrow") { +"Why these tools" }; h2("h2") { id = "why-title"; +"Small, local, accountable." } }
            ul("grid grid--4") {
                attr("role", "list")
                fun card(ic: String, t: String, body: String) = li("card glass shine") { span("icon") { attr("aria-hidden", "true"); icon(ic) }; h3("h3") { +t }; p { +body } }
                card("box", "One binary each", "Written in Rust and shipped as a single native binary for macOS and Linux; ketch runs on Windows too.")
                card("tree", "Local first", "Ledgers, archives and installs live on your machine; export telemetry only if you choose.")
                card("chart", "Measured and verified", "Rtok records every saving; Ketch checks every checksum; Mailune keeps mail and AI on your device.")
                card("lock", "Open licensing", "${Site.LICENSE.short}; Weft is Apache-2.0.")
            }
        }

        // PRICING — per product: Free/OSS (real) + Pro (PLACEHOLDER)
        section("section container") {
            id = "pricing"; attr("aria-labelledby", "pricing-title")
            header("section__head section__head--center") {
                div("section-flag") { p("eyebrow") { +"Pricing" }; span("badge badge--placeholder") { +"Pro is a placeholder" } }
                h2("h2") { id = "pricing-title"; +"Free forever. Pro, per tool." }
                p("lead") { +"Every tool has a free open-source edition. Pro tiers below are illustrative placeholders for this MVP." }
            }
            div("grid grid--3") {
                tools.forEach { p ->
                    article("card glass price-card") {
                        style = "--card-accent:${p.accent}"; attr("aria-labelledby", "pr-${p.id}")
                        h3("oss-card__title") { id = "pr-${p.id}"; +p.title; span { +"." } }
                        p("cat-tag") { +cat.categoryFor(p).label }
                        div("price-tier") {
                            div("tier__head") { strong { +"Free · OSS" }; span("price__num price__num--sm") { +(if (cat.hasPro(p)) "$0" else "Free") } }
                            ul("checklist") { (Site.FREE_HIGHLIGHTS[p.id] ?: listOf("The full open-source tool")).forEach { f -> li { icon("check", 16); +" $f" } } }
                            a(url("/${p.id}/#install"), classes = "btn btn--glass btn--block") { +"Install free" }
                        }
                        if (cat.hasPro(p)) div("price-tier price-tier--pro") {
                            div("tier__head") {
                                strong { +"Pro "; span("badge badge--placeholder") { +"Placeholder" } }
                                span("price__num price__num--sm") { span { attr("data-pro-price", p.id); +cat.proFor(p).price }; +"*" }
                            }
                            ul("checklist") { cat.proFor(p).features.forEach { f -> li { icon("check", 16); +" $f" } } }
                            proWishlist(ctx, p, "pricing-note")
                        }
                    }
                }
            }
            p("footnote") {
                id = "pricing-note"
                +"${Site.PRO_PRICE_NOTE} ${if (tools.any(cat::hasWishlist)) "A Pro wishlist mails you once, when paid plans open; checkout is not wired up yet." else "Placeholder — the Pro waitlist is not open yet; checkout is not wired up in this MVP."}"
            }
        }

        // FAQ
        section("section container container--narrow") {
            id = "faq"; attr("aria-labelledby", "faq-title")
            header("section__head") { p("eyebrow") { +"FAQ" }; h2("h2") { id = "faq-title"; +"Questions, answered." } }
            div("faq") {
                fun item(q: String, body: DIV.() -> Unit) = details("faq__item glass") { summary { +q }; div("faq__body") { body() } }
                item("What is free, and what would I pay for?") { p { +"Every tool’s open-source edition is free — the full binary, no feature gates. Pro tiers (placeholder) would add team features on top, priced per tool." } }
                item("How are the tools licensed?") {
                    p { +"All tools in the catalog except Weft use the same licensing. You can use each one under any of the following, at your choice (Weft is under the Apache License 2.0):" }
                    ol("license") { Site.LICENSE.items.forEach { l -> li { span { strong { +l.name }; +" — ${l.text}" } } } }
                }
                item("What data leaves my machine?") { p { +"The open-source tools run locally and keep their ledgers on disk; Rtok and Cox export telemetry only if you point them at an OTLP endpoint. Pro data handling terms are a placeholder and will be published before any launch." } }
                item("Can I self-host?") { p { +"The free editions are local binaries — there is nothing to host. A self-hosted Pro option is a placeholder in this MVP." } }
                item("How do I upgrade a tool to Pro?") { p { +"Keep your existing install; Pro (placeholder) is designed to connect on top of the same binary, one tool at a time." } }
            }
        }

        // FINAL CTA
        section("container") {
            attr("aria-labelledby", "cta-title")
            div("cta glass glass--thick") {
                div("cta__copy") { h2("h2") { id = "cta-title"; +"Find your next tool." }; p("muted") { +"Install one binary in under a minute. No account needed." } }
                div("cta__actions") {
                    a(url("/#catalog"), classes = "btn btn--primary btn--lg") { +"Browse the catalog" }
                    a(url("/${featured.id}/"), classes = "btn btn--glass btn--lg") { +"See ${featured.title}" }
                }
            }
        }
    }
}
