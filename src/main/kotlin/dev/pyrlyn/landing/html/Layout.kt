package dev.pyrlyn.landing.html

import dev.pyrlyn.landing.content.Catalog
import dev.pyrlyn.landing.content.Site
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/** Page props (port of Base.astro's Props). */
data class PageMeta(
    /** Site path of the page, e.g. "/" or "/rtok/docs/". */
    val path: String,
    val title: String,
    val description: String,
    val theme: String,
    val accent: String? = null,
    val accent2: String? = null,
    val accentLight: String? = null,
    val ogType: String = "website",
    val imageAlt: String? = null,
    val jsonLd: List<JsonElement> = emptyList(),
    val noindex: Boolean = false,
    val analytics: Boolean = true,
    /** Error pages: no site header or footer. */
    val bare: Boolean = false,
    /** Extra page CSS (the Astro pages' own <style> blocks, docs.css). */
    val extraCss: String = "",
)

/**
 * Satoshi variable woff2 on the Fontshare CDN: the exact file api.fontshare.com/v2/css?f[]=satoshi@1
 * references; preloaded with an inline @font-face instead of the render-blocking Fontshare stylesheet.
 */
private const val FONT_URL = "https://cdn.fontshare.com/wf/NWBQYJIM7GCZ5XWD7D26ARB3VDY55ZRT/K63EV2KZIGKLE7RANQ2U42S6SVHU5RJ7/X6XYTKIVDUW7GZTZPZNN4EUM5KH54KHF.woff2"

private fun js(s: String) = JsonPrimitive(s).toString().replace("<", "\\u003c")

/** The site shell: head (SEO, theme, font, analytics, inline CSS), header, main, footer (port of Base.astro). */
fun Ctx.page(meta: PageMeta, main: MAIN.() -> Unit): String {
    val theme = Site.theme(meta.theme)
    val accent = meta.accent ?: theme.accent
    val accent2 = meta.accent2 ?: theme.accent2
    val accentLight = meta.accentLight ?: theme.accentLight
    val vars = "--accent:$accent;--accent-dark:$accent;--accent-2:$accent2;--accent-light:$accentLight;--bg:${theme.bg}"
    val og = cfg.abs("/images/${theme.images}/og.jpg")
    val tools = catalog.tools
    val current = catalog.byId(meta.theme)
    val ghHref = current?.repo ?: "https://github.com/pyrlyn"
    val ghLabel = "${current?.title ?: "Pyrlyn"} on GitHub"
    val inDocs = Regex("/docs(/|$)").containsMatchIn(meta.path)
    val canonical = cfg.abs(meta.path.trimEnd('/') + "/")
    val hasOrg = meta.jsonLd.any { (it as? JsonObject)?.get("@id") == JsonPrimitive(seo.orgId) }
    val nodes = if (meta.noindex || hasOrg) meta.jsonLd else listOf(seo.organization()) + meta.jsonLd

    val out = StringBuilder("<!DOCTYPE html>")
    out.appendHTML(prettyPrint = false).html {
        lang = "en"
        style = vars
        attr("data-theme", meta.theme)
        if (inDocs) classes = setOf("is-still")
        head {
            meta(charset = "utf-8")
            if (!inDocs) script { raw("if(!matchMedia(\"(prefers-reduced-motion: reduce)\").matches)document.documentElement.classList.add(\"intro\")") }
            meta("viewport", "width=device-width, initial-scale=1")
            // SEO head (port of SEO.astro)
            title(meta.title)
            meta("description", meta.description)
            meta("robots", if (meta.noindex) "noindex" else "index, follow, max-image-preview:large")
            if (!meta.noindex) link { rel = "canonical"; href = canonical }
            fun prop(p: String, c: String) = meta { attr("property", p); content = c }
            prop("og:type", meta.ogType); prop("og:site_name", Site.BRAND); prop("og:locale", "en_US")
            prop("og:title", meta.title); prop("og:description", meta.description)
            if (!meta.noindex) prop("og:url", canonical)
            prop("og:image", og); prop("og:image:type", "image/jpeg"); prop("og:image:width", "1200"); prop("og:image:height", "630")
            meta.imageAlt?.let { prop("og:image:alt", it) }
            meta("twitter:card", "summary_large_image"); meta("twitter:title", meta.title); meta("twitter:description", meta.description); meta("twitter:image", og)
            meta.imageAlt?.let { meta("twitter:image:alt", it) }
            if (nodes.isNotEmpty()) script(type = "application/ld+json") { raw(seo.graph(nodes)) }
            // Theme: a saved choice (localStorage "pyrlyn-theme") wins, otherwise the system setting; applied before first paint.
            script {
                raw("(function(){const darkBg = ${js(theme.bg)};\n(function(){var d=document.documentElement,s=null;try{s=localStorage.getItem(\"pyrlyn-theme\")}catch(e){}var m=s===\"light\"||s===\"dark\"?s:(matchMedia(\"(prefers-color-scheme: light)\").matches?\"light\":\"dark\");d.setAttribute(\"data-mode\",m);d.dataset.darkBg=darkBg;d.style.colorScheme=m;window.__applyThemeMeta=function(m){var c=document.getElementById(\"meta-theme-color\");if(c)c.setAttribute(\"content\",m===\"light\"?\"#f4f6f9\":darkBg);var k=document.getElementById(\"meta-color-scheme\");if(k)k.setAttribute(\"content\",m)};document.addEventListener(\"DOMContentLoaded\",function(){window.__applyThemeMeta(m)});window.__applyThemeMeta(m)})()})();")
            }
            meta("color-scheme", "dark light") { id = "meta-color-scheme" }
            meta("theme-color", theme.bg) { id = "meta-theme-color" }
            link(url("/pyrlyn/pyrlyn-favicon.svg"), "icon", "image/svg+xml")
            for (s in listOf(16, 32, 64)) link(url("/pyrlyn/pyrlyn-favicon-$s.png"), "icon", "image/png") { attr("sizes", "${s}x$s") }
            link(url("/pyrlyn/pyrlyn-apple-touch-icon.png"), "apple-touch-icon")
            link(url("/sitemap-index.xml"), "sitemap", "application/xml")
            link("https://cdn.fontshare.com", "preconnect") { attr("crossorigin", "") }
            link(FONT_URL, "preload", "font/woff2") { attr("as", "font"); attr("crossorigin", "") }
            style { raw("@font-face{font-family:\"Satoshi\";src:url(\"$FONT_URL\") format(\"woff2\");font-weight:300 900;font-style:normal;font-display:block}") }
            // Analytics: gtag queue and PostHog stub at once, libraries after window load + idle.
            if (meta.analytics && (cfg.gaMeasurementId.isNotEmpty() || cfg.posthogKey.isNotEmpty())) script { raw(analytics()) }
            style { raw(assets.siteCss + meta.extraCss) }
        }
        body {
            a("#main", classes = "skip-link") { +"Skip to content" }
            div("mesh") {
                attr("aria-hidden", "true")
                for (k in 1..5) span("mesh__blob mesh__blob--$k") {}
                span("mesh__grid") {}; span("mesh__grain") {}
            }
            if (!meta.bare) header(if (current != null) "nav tw:max-[459px]:px-2! nav--auto" else "nav tw:max-[459px]:px-2!") {
                if (current != null) {
                    attr("data-nav-auto", "")
                    div("nav__peek") {
                        attr("data-nav-peek", "")
                        div("nav__peek-pill glass") {
                            pyrlynLogo(this@page)
                            button(classes = "nav__peek-btn", type = ButtonType.button) {
                                attr("aria-label", "Show menu"); attr("aria-expanded", "false"); attr("data-nav-pin", "")
                                raw(CHEVRON_UP)
                            }
                        }
                    }
                }
                div("nav__inner tw:max-[459px]:gap-2! tw:max-[459px]:pl-3! tw:max-[359px]:gap-1!") {
                    pyrlynLogo(this@page)
                    nav {
                        attr("aria-label", "Primary")
                        ul("nav__links") {
                            li("menu") { productsMenu(this@page, current?.title, meta.theme) }
                            li { a(url("/#how")) { +"How it works" } }
                            li { a(url("/#pricing")) { +"Pricing" } }
                            li { a(url("/#faq")) { +"FAQ" } }
                        }
                    }
                    div("nav__actions") {
                        search(this@page)
                        button(classes = "theme-toggle", type = ButtonType.button) {
                            attr("data-theme-toggle", ""); attr("aria-label", "Switch to light theme")
                            span("theme-toggle__sun") { icon("sun", 18) }
                            span("theme-toggle__moon") { icon("moon", 18) }
                        }
                        a(ghHref, classes = "nav__gh") { attr("aria-label", ghLabel); title = ghLabel; icon("github", 18) }
                        a(url("/#catalog"), classes = "btn btn--primary nav__cta tw:max-[459px]:px-4! tw:max-[359px]:px-3!") {
                            span("nav__cta-icon") { attr("aria-hidden", "true"); icon("app", 18) }
                            span("nav__cta-label") { +"Browse tools" }
                        }
                        if (current != null) button(classes = "nav__peek-btn nav__unpin", type = ButtonType.button) {
                            attr("aria-label", "Hide menu"); attr("data-nav-pin", ""); raw(CHEVRON_UP)
                        }
                    }
                }
            }
            main("tw:overflow-x-clip") { id = "main"; attr("tabindex", "-1"); main() }
            if (!meta.bare) footer("footer container") {
                div("footer__row") {
                    pyrlynLogo(this@page)
                    nav {
                        attr("aria-label", "Footer")
                        ul("footer__links") {
                            tools.forEach { x -> li { a(url("/${x.id}/")) { +x.title } } }
                            li { a(url("/#pricing")) { +"Pricing" } }
                            li { a(url("/#faq")) { +"FAQ" } }
                            li { a("https://github.com/pyrlyn") { +"GitHub" } }
                            li { a(url("/team")) { +"Team" } }
                            li { a(url("/support")) { +"Support" } }
                        }
                    }
                }
                p { +"A storefront for AI developer tools. Every tool has a free open-source edition; Pro features shown on this site are placeholders, and so are Pro prices the backend does not list yet. Product facts come from each tool’s own README, docs and site copy." }
            }
            // Copy feedback (live region + toast), enhanced by site.js.
            p("sr-only") { attr("aria-live", "polite"); attr("data-copy-live", "") }
            div("toast") {
                attr("aria-hidden", "true"); attr("data-toast", "")
                raw("<svg viewBox=\"0 0 16 16\" width=\"14\" height=\"14\"><path d=\"m3.5 8.5 3 3 6-7\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.8\" stroke-linecap=\"round\" stroke-linejoin=\"round\"></path></svg>")
                span { +"Copied" }
            }
            script(src = url(assets.js)) {
                type = "module"
                attr("data-intro", url(assets.introJs))
            }
        }
    }
    return out.toString()
}

private const val CHEVRON_UP = "<svg viewBox=\"0 0 12 12\" width=\"14\" height=\"14\" aria-hidden=\"true\"><path d=\"m3 7.5 3-3 3 3\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.6\" stroke-linecap=\"round\" stroke-linejoin=\"round\"></path></svg>"
private const val CHEVRON_DOWN = "<svg viewBox=\"0 0 12 12\" width=\"12\" height=\"12\" aria-hidden=\"true\"><path d=\"m3 4.5 3 3 3-3\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.6\" stroke-linecap=\"round\" stroke-linejoin=\"round\"></path></svg>"

/** GA4 + PostHog bootstrap, byte-for-byte the Astro inline script (IDs from SiteConfig). */
private fun Ctx.analytics(): String =
    "(function(){const gaId = ${js(cfg.gaMeasurementId)};\nconst phKey = ${js(cfg.posthogKey)};\nconst phHost = ${js(cfg.posthogHost)};\nconst lazy = ${cfg.deferAnalytics};\n(function(){\n" +
        "if(gaId){window.dataLayer=window.dataLayer||[];window.gtag=function(){dataLayer.push(arguments)};gtag(\"js\",new Date());gtag(\"config\",gaId);}\n" +
        "function run(){if(gaId){var g=document.createElement(\"script\");g.async=!0;g.src=\"https://www.googletagmanager.com/gtag/js?id=\"+gaId;document.head.appendChild(g);}\n" +
        "if(phKey){!function(t,e){var o,n,p,r;e.__SV||(window.posthog=e,e._i=[],e.init=function(i,s,a){function g(t,e){var o=e.split(\".\");2==o.length&&(t=t[o[0]],e=o[1]),t[e]=function(){t.push([e].concat(Array.prototype.slice.call(arguments,0)))}}(p=t.createElement(\"script\")).type=\"text/javascript\",p.crossOrigin=\"anonymous\",p.async=!0,p.src=s.api_host.replace(\".i.posthog.com\",\"-assets.i.posthog.com\")+\"/static/array.js\",(r=t.getElementsByTagName(\"script\")[0]).parentNode.insertBefore(p,r);var u=e;for(void 0!==a?u=e[a]=[]:a=\"posthog\",u.people=u.people||[],u.toString=function(t){var e=\"posthog\";return\"posthog\"!==a&&(e+=\".\"+a),t||(e+=\" (stub)\"),e},u.people.toString=function(){return u.toString(1)+\".people (stub)\"},o=\"init capture register register_once unregister identify alias reset opt_in_capturing opt_out_capturing has_opted_out_capturing get_distinct_id getFeatureFlag isFeatureEnabled onFeatureFlags\".split(\" \"),n=0;n<o.length;n++)g(u,o[n]);e._i.push([i,s,a])},e.__SV=1)}(document,window.posthog||[]);posthog.init(phKey,{api_host:phHost,person_profiles:\"identified_only\"});}}\n" +
        "if(!lazy)return run();function idle(){window.requestIdleCallback?requestIdleCallback(run,{timeout:3000}):setTimeout(run,1)}\n" +
        "document.readyState===\"complete\"?idle():addEventListener(\"load\",idle,{once:!0});})()})();"

/** Products menu: a native <details> disclosure (works without JS); site.js adds Esc / outside-click close. */
private fun FlowContent.productsMenu(ctx: Ctx, currentTitle: String?, themeKey: String) {
    val cat = ctx.catalog
    details("menu__details") {
        attr("data-menu", "")
        summary("menu__trigger tw:max-[459px]:px-2! tw:max-[359px]:px-1!") {
            if (currentTitle != null) attr("aria-current", "page")
            +"Products"
            if (currentTitle != null) span("menu__current") { +"· $currentTitle" }
            raw(CHEVRON_DOWN)
        }
        div("menu__panel glass glass--thick") {
            p("menu__label") { +"All products · ${cat.tools.size}" }
            cat.groupByCategory().forEach { g ->
                section("menu__group") {
                    attr("aria-label", g.category.label)
                    p("menu__cat") { +g.category.label; span { +"${g.tools.size}" } }
                    ul("menu__grid") {
                        g.tools.forEach { x ->
                            li {
                                a(ctx.url("/${x.id}/"), classes = "menu__item shine") {
                                    style = "--card-accent:${x.accent}"
                                    if (x.id == themeKey) attr("aria-current", "page")
                                    span("menu__mark") { attr("aria-hidden", "true"); +x.rawTitle.take(2) }
                                    span("menu__text") { strong { +"${x.title} "; span("menu__ver") { +Catalog.versionLabel(x) } }; small { +x.tagline } }
                                    span("menu__price") { proLabel(cat, x) }
                                }
                            }
                        }
                    }
                }
            }
            p("menu__foot") { a(ctx.url("/#catalog")) { +"Browse the catalog →" }; span { +Site.PRO_PRICE_NOTE } }
        }
    }
}

/** Header search (markup of the Search island); behaviour in site.js. Algolia only with a search-only key. */
private fun FlowContent.search(ctx: Ctx) {
    div("search") {
        attr("data-open", "false"); attr("data-search", "")
        if (ctx.cfg.algoliaEnabled) { attr("data-app-id", ctx.cfg.algoliaAppId); attr("data-search-key", ctx.cfg.algoliaSearchKey); attr("data-index", ctx.cfg.algoliaIndex) }
        button(classes = "search__btn", type = ButtonType.button) {
            attr("aria-label", "Search"); attr("aria-expanded", "false"); attr("aria-controls", "search-input"); title = "Search (/ or ⌘K)"
            raw("<svg width=\"18\" height=\"18\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\" aria-hidden=\"true\"><circle cx=\"11\" cy=\"11\" r=\"7\"></circle><path d=\"m20 20-3.5-3.5\"></path></svg>")
        }
        input(InputType.search, classes = "search__input") {
            id = "search-input"; attr("role", "combobox"); attr("aria-expanded", "false"); attr("aria-controls", "search-results")
            attr("aria-autocomplete", "list"); attr("aria-label", "Search tools and docs"); attr("autocomplete", "off")
            placeholder = "Search tools and docs"; attr("tabindex", "-1"); value = ""
        }
        kbd("search__kbd") { +"Esc" }
        button(classes = "search__cancel", type = ButtonType.button) { attr("tabindex", "-1"); attr("aria-label", "Close search"); +"Cancel" }
        div("search__panel") { id = "search-results"; attr("role", "listbox"); attr("aria-label", "Search results"); attr("hidden", ""); attr("data-visible", "false") }
    }
}
