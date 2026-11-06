package dev.pyrlyn.landing.pages

import dev.pyrlyn.landing.content.Data
import dev.pyrlyn.landing.html.*
import kotlinx.html.*

/** Not-found page; nginx serves 404.html for any missing path. */
fun Ctx.notFoundPage() = page(PageMeta("/404", "Page not found | Pyrlyn", "This page doesn't exist. Browse Pyrlyn's developer tools: Rtok, Ketch and Mailune.", "home", noindex = true, bare = true)) {
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

private const val TEAM_CSS = ".team-hero{padding-block:var(--s8,6rem) var(--s7,4rem)}.team-hero__inner{display:grid;gap:var(--s4,1rem);max-width:720px}.team-hero__eyebrow{margin:0;font-family:var(--font-mono,ui-monospace,monospace);font-size:var(--fs-xs,.75rem);text-transform:uppercase;letter-spacing:.14em;color:var(--accent-2)}.team-hero h1,.team-hero .lead{margin:0}.team-hero h1{color:var(--fg-strong)}.team{padding-bottom:var(--s9,8rem)}.team figure{margin:0}.team-photo__frame{position:relative;overflow:hidden;border-radius:16px;aspect-ratio:16/9;background:var(--bg-elev);isolation:isolate;box-shadow:var(--shadow-3),0 0 0 1px var(--hairline)}.team-photo__frame::after{content:\"\";position:absolute;inset:0;border-radius:inherit;box-shadow:inset 0 1px 0 rgba(255,255,255,.08);pointer-events:none}.team-photo__layer{position:absolute;inset:-6%}.team-photo__layer img{display:block;width:100%;height:100%;object-fit:cover}.team-pin{position:absolute;z-index:2;transform:translate(-50%,-50%);display:grid;place-items:center;width:28px;height:28px;border-radius:50%;font-family:var(--font-mono,ui-monospace,monospace);font-size:.75rem;font-weight:600;color:var(--fg-strong);background:rgba(10,15,23,.6);border:1px solid var(--hairline-strong);backdrop-filter:blur(10px)}.team-list{list-style:none;margin:var(--s6,2.5rem) 0 0;padding:0;display:grid;gap:var(--s4,1rem);grid-template-columns:repeat(6,1fr)}.team-card{display:grid;align-content:start;gap:var(--s1,.25rem);padding:var(--s4,1rem);border-radius:12px;border:1px solid var(--hairline);background:var(--surface)}.team-card--lead{border-color:color-mix(in srgb,var(--accent) 40%,transparent);background:linear-gradient(180deg,color-mix(in srgb,var(--accent) 10%,transparent),var(--surface))}.team-card img{width:56px;height:56px;border-radius:50%;object-fit:cover;border:1px solid var(--hairline-strong);margin-bottom:var(--s2,.5rem)}.team-card b{font-family:var(--font-mono,ui-monospace,monospace);font-size:.75rem;font-weight:600;color:var(--accent)}.team-card strong{color:var(--fg-strong);font-weight:600;letter-spacing:-.01em}.team-card span{font-size:.875rem;color:var(--muted)}.team-card__link{display:inline-flex;align-items:center;gap:.25rem;margin-top:var(--s2,.5rem);font-size:.875rem;font-weight:600;color:var(--accent);text-decoration:none}.team-card__link:hover{text-decoration:underline}.team-card__link span{color:inherit;font-size:.75rem}.team__note{margin:var(--s5,1.5rem) 0 0;font-size:.875rem;color:var(--subtle)}.team__note+.team__note{margin-top:var(--s2,.5rem)}.team-photo[data-state=armed]{opacity:0;transform:scale(.96);filter:blur(8px)}.team-photo[data-state=in]{opacity:1;transform:none;filter:none;transition:opacity .6s var(--ease,ease),transform .6s var(--ease,ease),filter .6s var(--ease,ease)}@supports (animation-timeline:view()){.team-photo[data-parallax=css] .team-photo__layer{animation:team-parallax linear both;animation-timeline:view(block);animation-range:cover}@keyframes team-parallax{from{transform:translate3d(0,-6%,0)}to{transform:translate3d(0,6%,0)}}}@media (max-width:1279px){.team-list{grid-template-columns:repeat(3,1fr)}}@media (max-width:900px){.team-list{grid-template-columns:repeat(2,1fr)}}@media (max-width:560px){.team-list{grid-template-columns:1fr}.team-pin{width:22px;height:22px;font-size:10px}.team-photo__frame{border-radius:12px}}@media (prefers-reduced-motion:reduce){.team-photo,.team-photo__layer{opacity:1!important;transform:none!important;filter:none!important;animation:none!important;transition:none!important}}"

private class TeamMember(val name: String, val role: String, val avatar: String, val x: Double, val y: Double, val lead: Boolean = false)

// Pin positions are percentages of the photo (head centers). Robot names are invented; no affiliation implied.
private val TEAM = listOf(
    TeamMember("Gronk Halvorsen", "Engineering · says the quiet part out loud", "grok", 10.9, 14.0),
    TeamMember("Claudette Ambergris", "Docs & product · writes the long answer", "claude", 27.0, 14.0),
    TeamMember("Ivan Tuhai", "Founder · the only one with a face", "ivan", 51.5, 17.0, lead = true),
    TeamMember("Geppetto Tran", "Tooling · autocompletes your sentences", "gpt", 59.8, 14.0),
    TeamMember("Gemma Starling", "Design · thinks in gradients and grids", "gemini", 75.0, 14.0),
    TeamMember("Kimball Moonshot", "Research · reads the whole repo before lunch", "kimi", 89.6, 14.0),
)

private const val TEAM_ALT = "Group photo of the Pyrlyn team in a dark studio: founder Ivan Tuhai in a charcoal sweater, third from left, among five robot teammates in casual clothes with abstract AI-style heads: a black sphere with a white ring and slash, a terracotta rounded square with a cream starburst, a sage sphere with a white knot, a glass sphere with a rainbow four-point star, and a black sphere with a white K-like glyph and a blue dot."

/** Team: one group photo with numbered pins and a numbered caption of avatars (motion in web/Team.kt). */
fun Ctx.teamPage(): String = page(PageMeta("/team", "Team · Pyrlyn", "The people behind Pyrlyn: founder Ivan Tuhai and five very fast AI colleagues building Rtok and other developer tools.", "home", extraCss = TEAM_CSS)) {
    section("container team-hero") {
        attr("aria-labelledby", "team-title")
        div("team-hero__inner") {
            p("team-hero__eyebrow") { +"Team" }
            h1("display") { id = "team-title"; +"The people behind the tools." }
            p("lead") { +"One human, five very fast colleagues. We build developer tools for people who work alongside AI — and only one of us needs coffee." }
        }
    }
    section("container team") {
        attr("aria-label", "Team photo")
        figure {
            div("team-photo") {
                attr("data-team-photo", "")
                div("team-photo__frame") {
                    attr("data-team-frame", "")
                    div("team-photo__layer") {
                        attr("data-team-layer", "")
                        img(TEAM_ALT, url("/team/team-photo.jpg")) { width = "1280"; height = "720"; attr("loading", "eager"); attr("fetchpriority", "high"); attr("decoding", "async") }
                        TEAM.forEachIndexed { i, m ->
                            span("team-pin") { attr("aria-hidden", "true"); style = "left:${m.x}%;top:${m.y}%"; +"${i + 1}" }
                        }
                    }
                }
            }
            figcaption {
                ol("team-list") {
                    attr("aria-label", "Who is who, left to right")
                    TEAM.forEachIndexed { i, m ->
                        li(if (m.lead) "team-card team-card--lead" else "team-card") {
                            img(if (m.lead) "Portrait of Ivan Tuhai" else "Robot head of ${m.name}", url("/team/avatar-${m.avatar}.jpg")) { width = "56"; height = "56"; attr("loading", "lazy"); attr("decoding", "async") }
                            b { +(i + 1).toString().padStart(2, '0') }
                            strong { +m.name }
                            span { +m.role }
                            if (m.lead) {
                                a("https://listepo.dev", classes = "team-card__link") {
                                    attr("rel", "noopener")
                                    +"listepo.dev"
                                    span { attr("aria-hidden", "true"); +"↗" }
                                }
                            }
                        }
                    }
                }
            }
        }
        p("team__note") { +"Ivan is real. His five teammates’ names and faces are invented; the heads only evoke AI assistants we work with, and imply no affiliation or endorsement." }
        p("team__note") { +"Ivan on GitHub: "; a("https://github.com/listepo") { +"listepo" }; +". Need help? See "; a(url("/support")) { +"Support" }; +"." }
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
