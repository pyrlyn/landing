package dev.pyrlyn.landing.html

/** Small inline stroke icons (24px grid, currentColor); port of src/components/Icon.astro. */
object Icons {
    private val P = mapOf(
        "check" to "<path d=\"m5 12.5 4.5 4.5L19 7.5\"/>",
        "arrow" to "<path d=\"M5 12h13m-5-5 5 5-5 5\"/>",
        "info" to "<circle cx=\"12\" cy=\"12\" r=\"9\"/><path d=\"M12 11v5M12 8h.01\"/>",
        "layers" to "<path d=\"m12 3 9 5-9 5-9-5 9-5Z\"/><path d=\"m3 13 9 5 9-5\"/>",
        "chart" to "<path d=\"M4 20V11m5.3 9V5m5.4 15v-6M20 20V9\"/>",
        "undo" to "<path d=\"M9 14 4 9l5-5\"/><path d=\"M4 9h10a6 6 0 0 1 0 12h-3\"/>",
        "shield" to "<path d=\"M12 3 5 6v5c0 4.5 3 8 7 10 4-2 7-5.5 7-10V6l-7-3Z\"/><path d=\"m9 12 2 2 4-4\"/>",
        "graph" to "<circle cx=\"6\" cy=\"6\" r=\"2.5\"/><circle cx=\"18\" cy=\"8\" r=\"2.5\"/><circle cx=\"10\" cy=\"18\" r=\"2.5\"/><path d=\"m8.4 6.5 7.2 1M7 8.3l2.2 7.3m7.4-5.5L11.8 16\"/>",
        "plug" to "<path d=\"M9 3v5m6-5v5M6 8h12v3a6 6 0 0 1-12 0V8Zm6 9v4\"/>",
        "terminal" to "<rect x=\"3\" y=\"4\" width=\"18\" height=\"16\" rx=\"3\"/><path d=\"m7 9 3 3-3 3m5 0h5\"/>",
        "bolt" to "<path d=\"M13 3 5 13h6l-1 8 8-10h-6l1-8Z\"/>",
        "editor" to "<path d=\"M4 20h4L19 9l-4-4L4 16v4Z\"/>",
        "stream" to "<path d=\"M3 8c3 0 3-3 6-3s3 3 6 3 3-3 6-3M3 14c3 0 3-3 6-3s3 3 6 3 3-3 6-3M3 20c3 0 3-3 6-3s3 3 6 3 3-3 6-3\"/>",
        "coin" to "<circle cx=\"12\" cy=\"12\" r=\"9\"/><path d=\"M12 7v10m-2.5-7.5h4a1.5 1.5 0 0 1 0 3h-3a1.5 1.5 0 0 0 0 3h4\"/>",
        "download" to "<path d=\"M12 4v11m-5-5 5 5 5-5M5 20h14\"/>",
        "app" to "<rect x=\"4\" y=\"4\" width=\"16\" height=\"16\" rx=\"4\"/><path d=\"M9 12h6m-3-3v6\"/>",
        "tree" to "<path d=\"M4 6h6l2 2h8v10a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6Z\"/>",
        "lock" to "<rect x=\"5\" y=\"10\" width=\"14\" height=\"10\" rx=\"2.5\"/><path d=\"M8 10V7a4 4 0 0 1 8 0v3\"/>",
        "history" to "<path d=\"M4 12a8 8 0 1 0 2.3-5.6M4 4v4h4\"/><path d=\"M12 8v4l3 2\"/>",
        "eye" to "<path d=\"M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12Z\"/><circle cx=\"12\" cy=\"12\" r=\"3\"/>",
        "users" to "<circle cx=\"9\" cy=\"8\" r=\"3.5\"/><path d=\"M2.5 20a6.5 6.5 0 0 1 13 0M16 4.5a3.5 3.5 0 0 1 0 7M18 14a6 6 0 0 1 3.5 6\"/>",
        "sparkle" to "<path d=\"M12 3v4m0 10v4M3 12h4m10 0h4M6 6l2.5 2.5m7 7L18 18M6 18l2.5-2.5m7-7L18 6\"/>",
        "cloud" to "<path d=\"M7 18a4 4 0 0 1-.5-8 6 6 0 0 1 11.5 1.5A3.5 3.5 0 0 1 17.5 18H7Z\"/>",
        "key" to "<circle cx=\"8\" cy=\"15\" r=\"4\"/><path d=\"m11 12 8-8m-3 3 2 2\"/>",
        "sun" to "<circle cx=\"12\" cy=\"12\" r=\"4\"/><path d=\"M12 2.5v2M12 19.5v2M2.5 12h2M19.5 12h2M5.3 5.3l1.4 1.4M17.3 17.3l1.4 1.4M5.3 18.7l1.4-1.4M17.3 6.7l1.4-1.4\"/>",
        "moon" to "<path d=\"M20 14.5A8 8 0 0 1 9.5 4a8 8 0 1 0 10.5 10.5Z\"/>",
        "box" to "<path d=\"m12 3 8 4.5v9L12 21l-8-4.5v-9L12 3Z\"/><path d=\"m4 7.5 8 4.5 8-4.5M12 12v9\"/>",
    )
    private const val GITHUB = "<path fill=\"currentColor\" d=\"M8 0a8 8 0 0 0-2.53 15.59c.4.07.55-.17.55-.38v-1.33c-2.23.48-2.7-1.07-2.7-1.07-.36-.92-.89-1.17-.89-1.17-.73-.5.05-.49.05-.49.8.06 1.23.83 1.23.83.72 1.22 1.87.87 2.33.66.07-.52.28-.87.5-1.07-1.78-.2-3.65-.89-3.65-3.95 0-.88.31-1.59.83-2.15-.08-.2-.36-1.02.08-2.12 0 0 .67-.22 2.2.82a7.6 7.6 0 0 1 4 0c1.53-1.04 2.2-.82 2.2-.82.44 1.1.16 1.92.08 2.12.51.56.82 1.27.82 2.15 0 3.07-1.87 3.75-3.65 3.95.29.25.54.73.54 1.48v2.2c0 .21.15.46.55.38A8 8 0 0 0 8 0Z\"/>"

    fun svg(name: String, size: Int = 20): String =
        if (name == "github") "<svg viewBox=\"0 0 16 16\" width=\"$size\" height=\"$size\" aria-hidden=\"true\">$GITHUB</svg>"
        else "<svg viewBox=\"0 0 24 24\" width=\"$size\" height=\"$size\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.8\" stroke-linecap=\"round\" stroke-linejoin=\"round\" aria-hidden=\"true\">${P[name] ?: P.getValue("sparkle")}</svg>"
}
