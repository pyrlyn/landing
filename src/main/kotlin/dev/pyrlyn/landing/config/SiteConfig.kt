package dev.pyrlyn.landing.config

/**
 * Build-time site config, read from the environment (port of src/lib/config.ts and astro.config.mjs).
 * Only public values belong here: the Algolia key must be search-only, never the Admin key.
 * The GA4 / PostHog fallbacks are the site's public IDs, kept exactly as in the Astro site; set
 * GA_MEASUREMENT_ID or POSTHOG_KEY to "off" to disable one.
 */
data class SiteConfig(
    /** Absolute origin, e.g. https://pyrlyn.dev */
    val siteUrl: String,
    /** Base path with leading and trailing slash, e.g. "/" or "/landing/". */
    val base: String,
    val gaMeasurementId: String,
    val posthogKey: String,
    val posthogHost: String,
    val algoliaAppId: String,
    val algoliaSearchKey: String,
    val algoliaIndex: String,
    val apiUrl: String,
    /** Load analytics after window load + idle (PERF_DEFER_ANALYTICS=0 loads them at once). */
    val deferAnalytics: Boolean,
) {
    val algoliaEnabled: Boolean get() = algoliaAppId.isNotEmpty() && algoliaSearchKey.isNotEmpty()

    /** Prefix an internal path with the configured base. */
    fun url(path: String = "/"): String {
        val b = base.trimEnd('/')
        val p = if (path.startsWith("/")) path else "/$path"
        return b + p
    }

    /** Absolute URL of an internal path, under the configured base. */
    fun abs(path: String = "/"): String = siteUrl.trimEnd('/') + url(path)

    companion object {
        private const val GA_FALLBACK = "G-61CRNDXXYK"
        private const val POSTHOG_FALLBACK = "phc_oirHFL4DjcX7uyo2fmEaiXB3KA25Qyv8HoAbVEv32xkH"
        private const val POSTHOG_HOST_DEFAULT = "https://eu.i.posthog.com"

        fun fromEnv(env: (String) -> String? = System::getenv): SiteConfig {
            fun v(k: String) = env(k)?.trim().orEmpty()
            val ga = v("GA_MEASUREMENT_ID").ifEmpty { GA_FALLBACK }
            val ph = v("POSTHOG_KEY").ifEmpty { POSTHOG_FALLBACK }
            val api = v("API_URL")
            val rawBase = env("SITE_BASE") ?: "/"
            val base = "/" + rawBase.trim('/').let { if (it.isEmpty()) "" else "$it/" }
            return SiteConfig(
                siteUrl = v("SITE_URL").ifEmpty { "https://pyrlyn.dev" },
                base = base,
                gaMeasurementId = if (Regex("^G-[A-Z0-9]+$").matches(ga)) ga else "",
                posthogKey = if (Regex("^phc_[A-Za-z0-9]+$").matches(ph)) ph else "",
                posthogHost = v("POSTHOG_HOST").ifEmpty { POSTHOG_HOST_DEFAULT },
                algoliaAppId = v("ALGOLIA_APP_ID"),
                algoliaSearchKey = v("ALGOLIA_SEARCH_KEY"),
                algoliaIndex = v("ALGOLIA_INDEX").ifEmpty { "pyrlyn" },
                apiUrl = if (Regex("^https?://\\S+$").matches(api)) api else "",
                deferAnalytics = env("PERF_DEFER_ANALYTICS") != "0",
            )
        }
    }
}
