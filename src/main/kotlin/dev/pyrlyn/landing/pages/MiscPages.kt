package dev.pyrlyn.landing.pages

import dev.pyrlyn.landing.content.Data
import dev.pyrlyn.landing.html.*
import kotlinx.html.*
import java.io.File

/** Not-found page; nginx serves 404.html for any missing path. */
fun Ctx.notFoundPage() = page(PageMeta("/404", "Page not found | Pyrlyn", "This page doesn't exist. Browse Pyrlyn's developer tools: Rtok, Ketch, Cox, Runa, Swarfr, Stator and more.", "home", noindex = true, bare = true)) {
    section("err") {
        attr("aria-labelledby", "err-title")
        p("err__code") { attr("aria-hidden", "true"); +"404" }
        h1 { id = "err-title"; +"Page not found." }
        p("err__text") { +"This page doesn't exist or has moved." }
        a(url("/"), classes = "btn btn--primary btn--lg") { +"Back to home" }
    }
}

/** Server-error page; the home link is a literal "/" on purpose. */
fun Ctx.serverErrorPage() = page(PageMeta("/500", "Server error · Pyrlyn", "Something went wrong on our side.", "home", noindex = true, bare = true)) {
    section("err err--500") {
        attr("aria-labelledby", "err-title")
        p("err__code") { attr("aria-hidden", "true"); +"500" }
        h1 { id = "err-title"; +"Something went wrong." }
        p("err__text") { +"It's on our side. Please try again in a moment." }
        a("/", classes = "btn btn--primary btn--lg") { +"Back to home" }
    }
}

private const val SUPPORT_CSS = ".support-form{display:grid;gap:1rem;max-width:36rem;margin:1.5rem 0}.support-form label{display:grid;gap:.35rem;font-weight:600}.support-form input,.support-form textarea{font:inherit;padding:.6rem .75rem;border-radius:8px;border:1px solid var(--line-strong,#334155);background:var(--glass-bg,transparent);color:inherit}.support-form input:focus-visible,.support-form textarea:focus-visible{outline:none;box-shadow:var(--focus-ring)}.support-form button{justify-self:start}"

/** Support: a static form that opens a prefilled GitHub issue in pyrlyn/support. */
fun Ctx.supportPage() = page(PageMeta("/support", "Support | Pyrlyn", "Get help with Pyrlyn tools: write your message here and it opens a prefilled GitHub issue in the pyrlyn organization.", "home", extraCss = SUPPORT_CSS)) {
    section("section container") {
        attr("aria-labelledby", "support-title")
        div("prose") {
            h1 { id = "support-title"; +"Support" }
            p { +"Questions, bugs or ideas? Write them below. Sending opens a prefilled public issue on GitHub (you need a GitHub account); nothing is stored on this site." }
            form("https://github.com/pyrlyn/support/issues/new", classes = "support-form", method = FormMethod.get) {
                target = "_blank"
                label { +"Subject"; input(name = "title") { required = true; maxLength = "200" } }
                label { +"Message"; textArea(rows = "8") { name = "body"; required = true; maxLength = "5000" } }
                button(classes = "btn btn--primary", type = ButtonType.submit) { +"Open issue on GitHub" }
            }
            p {
                +"Existing requests: "; a("https://github.com/pyrlyn/support/issues") { +"pyrlyn/support issues" }
                +". For a bug in one tool, you can also open an issue in its own repository, for example "; a("https://github.com/pyrlyn/rtok/issues") { +"pyrlyn/rtok" }
                +". All repositories: "; a("https://github.com/pyrlyn") { +"github.com/pyrlyn" }; +". Common questions: "; a(url("/#faq")) { +"FAQ" }; +"."
            }
        }
    }
}

private const val TEAM_CSS = ".team__title{text-align:center}.team__orbit{--r:min(38vw,220px);position:relative;width:calc(var(--r)*2 + 120px);aspect-ratio:1;margin:2rem auto 1rem;max-width:100%}.team__me{position:absolute;inset:50% auto auto 50%;transform:translate(-50%,-50%);margin:0;text-align:center}.team__me img{width:clamp(110px,26vw,180px);height:auto;aspect-ratio:1;object-fit:cover;border-radius:50%;border:3px solid var(--accent)}.team__me figcaption{margin-top:.4rem;font-weight:600}.team__tools{list-style:none;margin:0;padding:0}.team__tool{position:absolute;left:50%;top:50%;display:flex;flex-direction:column;align-items:center;width:92px;transform:rotate(calc(360deg / var(--n) * var(--i))) translateY(calc(var(--r) * -1)) rotate(calc(-360deg / var(--n) * var(--i))) translate(-50%,-50%);transform-origin:0 0}.team__head{display:grid;place-items:center;width:48px;height:48px;border-radius:50%;background:var(--glass-bg-strong,#1e293b);border:1px solid var(--line-strong,#334155);color:var(--text,inherit)}.team__mono{font-weight:700;font-size:1.2rem}.team__body{width:34px;height:22px;margin-top:3px;border-radius:17px 17px 6px 6px;background:color-mix(in srgb,var(--accent) 35%,transparent)}.team__name{margin-top:.3rem;font-size:var(--fs-xs,.75rem);color:var(--muted);text-align:center;line-height:1.2}.team__note{text-align:center;font-size:var(--fs-xs,.75rem);color:var(--muted);max-width:40rem;margin:0 auto 2rem}.team__text{margin:0 auto}@media (prefers-reduced-motion:no-preference){.team__head{transition:transform .2s ease}.team__tool:hover .team__head{transform:translateY(-3px)}}@media (max-width:560px){.team__orbit{width:auto;aspect-ratio:auto;display:flex;flex-direction:column;align-items:center;gap:1.25rem}.team__me{position:static;transform:none}.team__tools{display:grid;grid-template-columns:repeat(4,1fr);gap:1rem .5rem}.team__tool{position:static;transform:none;width:auto}}"

/** About us / Our team. Drop public/team/ivan.jpg to replace the placeholder photo. */
fun Ctx.teamPage(): String {
    val photo = url(if (File(publicDir, "team/ivan.jpg").exists()) "/team/ivan.jpg" else "/team/ivan-placeholder.svg")
    val tools = Data.teamTools
    return page(PageMeta("/team", "Our team | Pyrlyn", "About Pyrlyn: open-source developer tools in Rust, Swift and TypeScript, such as Rtok, Cox, Ketch, Runa and Stator, and the engineer who builds them.", "home", extraCss = TEAM_CSS)) {
        section("section container team") {
            attr("aria-labelledby", "team-title")
            h1("team__title") { id = "team-title"; +"Our team" }
            div("team__orbit") {
                style = "--n:${tools.size}"
                figure("team__me") {
                    img("Ivan Tugay", photo) { width = "240"; height = "240"; attr("loading", "eager") }
                    figcaption { +"Ivan Tugay" }
                }
                ul("team__tools") {
                    attr("aria-label", "AI tools Ivan works with")
                    tools.forEachIndexed { i, t ->
                        li("team__tool") {
                            style = "--i:$i"
                            span("team__head") {
                                attr("aria-hidden", "true")
                                if (t.path != null) raw("<svg aria-hidden=\"true\" viewBox=\"0 0 24 24\" width=\"28\" height=\"28\" fill=\"currentColor\"><path d=\"${esc(t.path)}\"></path></svg>")
                                else span("team__mono") { +(t.monogram ?: "") }
                            }
                            span("team__body") { attr("aria-hidden", "true") }
                            span("team__name") { +t.name }
                        }
                    }
                }
            }
            p("team__note") { +"The figures around the photo are AI tools used day to day. Names and logos belong to their owners; no affiliation or endorsement is implied." }
            div("prose team__text") {
                h2 { +"About Pyrlyn" }
                p { +"Pyrlyn is a GitHub organization of open-source developer tools, most of them written in Rust, with Swift and TypeScript where a platform needs it. Every tool has a free open-source edition. Examples:" }
                ul {
                    li { a(url("/rtok/")) { +"Rtok" }; +": a token-reduction CLI for AI coding agents (hooks, MCP server and API proxy)." }
                    li { a(url("/cox/")) { +"Cox" }; +": a modular terminal coding agent with a safe, event-driven core." }
                    li { a(url("/ketch/")) { +"Ketch" }; +": a single-binary CLI package manager that installs releases straight from GitHub." }
                    li { a(url("/runa/")) { +"Runa" }; +": a local-first AI runner with an OpenAI-compatible server." }
                    li { a(url("/stator/")) { +"Stator" }; +": an ahead-of-time TypeScript/JavaScript compiler to native binaries." }
                    li { a("https://github.com/pyrlyn/crates") { +"crates" }; +": small focused Rust crates shared by Ketch and Rtok." }
                }
                p { +"This site is built from "; a("https://github.com/pyrlyn/landing") { +"pyrlyn/landing" }; +"." }
                h2 { +"About me" }
                p { +"I'm Ivan Tugay ("; a("https://github.com/listepo") { +"listepo" }; +"), an engineer. I work with Cursor and GitHub every day and build Pyrlyn: the backend, this landing site, CI on GitHub Actions and code quality checks with SonarCloud." }
                p { +"Need help? See "; a(url("/support")) { +"Support" }; +"." }
            }
        }
    }
}

/** Targets of the backend's wishlist mails; the one-time token is sent only on a click (site.js). */
fun Ctx.wishlistPage(action: String): String {
    val (title, text, button, pageTitle, desc) = if (action == "confirm")
        listOf("Confirm your Pro wishlist place.", "We mail you once, when paid plans for this tool open.", "Confirm", "Confirm your Pro wishlist place | Pyrlyn", "Confirm the address you gave for a Pyrlyn Pro wishlist, so we can mail you once when paid plans open.")
    else listOf("Leave the Pro wishlist.", "Your address is deleted from this tool's wishlist.", "Unsubscribe", "Leave the Pro wishlist | Pyrlyn", "Remove your address from a Pyrlyn Pro wishlist; you get no more mail about that tool's paid plans.")
    return page(PageMeta("/wishlist/$action/", pageTitle, desc, "home", noindex = true, analytics = false)) {
        section("err") {
            attr("aria-labelledby", "wl-title"); attr("data-wishlist-link", action)
            h1 { id = "wl-title"; +title }
            p("err__text") { +text }
            button(classes = "btn btn--primary btn--lg", type = ButtonType.button) { disabled = true; attr("data-wishlist-action", action); +button }
            p("err__text") { attr("role", "status"); attr("aria-live", "polite"); if (cfg.apiUrl.isEmpty()) +"The wishlist is not available on this site yet." }
            a(url("/")) { +"Back to home" }
        }
    }
}
