package dev.pyrlyn.landing.html

import dev.pyrlyn.landing.config.SiteConfig
import dev.pyrlyn.landing.content.Catalog
import dev.pyrlyn.landing.content.Docs
import dev.pyrlyn.landing.seo.Seo
import kotlinx.html.Tag
import java.io.File

/** Everything a page template needs: config, content, SEO helpers and the asset manifest. */
class Ctx(
    val cfg: SiteConfig,
    val catalog: Catalog,
    val docs: Docs,
    val publicDir: File,
    val assets: Assets,
) {
    val seo = Seo(cfg)
    fun url(path: String = "/") = cfg.url(path)
}

/** Hashed, minified asset URLs and the CSS inlined into each page. */
class Assets(val siteCss: String, val docsCss: String, val js: String, val introJs: String, val plansJs: String? = null)

fun Tag.attr(name: String, value: String?) { if (value != null) attributes[name] = value }
fun Tag.raw(html: String) = consumer.onTagContentUnsafe { +html }
fun Tag.icon(name: String, size: Int = 20) = raw(Icons.svg(name, size))
