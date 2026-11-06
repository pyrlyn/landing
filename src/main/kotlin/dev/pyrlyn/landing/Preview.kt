package dev.pyrlyn.landing

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.response.respondFile
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.sse.SSE
import io.ktor.server.sse.sse
import kotlinx.coroutines.delay
import java.io.File

private const val RELOAD_PATH = "/__livereload"

// EventSource reconnects on its own, so a restarted preview or a dropped connection needs no extra code.
private const val RELOAD_SCRIPT = "<script>new EventSource(\"$RELOAD_PATH\").onmessage=()=>location.reload()</script>"

/**
 * Dev preview only (`./gradlew run`, `./gradlew dev`): serves build/site like the production nginx does —
 * `/x/` → `/x/index.html`, `/x` → `/x/index.html` or `/x.html`, unknown paths → 404.html.
 * Not a production server; the site deploys as static files.
 *
 * Args: `<siteDir> [<stampFile> <gradlew>]`. With the stamp and gradlew (`./gradlew dev`) it runs
 * `gradlew --continuous generateSite` as a child and reloads open pages whenever the stamp changes.
 */
fun main(args: Array<String>) {
    val root = File(args.getOrNull(0) ?: "build/site").canonicalFile
    val stamp = args.getOrNull(1)?.let(::File)
    val port = System.getenv("PORT")?.toIntOrNull() ?: 4321
    args.getOrNull(2)?.let(::startContinuousBuild)
    println("landing: preview of $root on http://localhost:$port/" + if (stamp != null) " (live reload)" else "")
    embeddedServer(CIO, port = port) {
        if (stamp != null) install(SSE)
        routing {
            if (stamp != null) {
                sse(RELOAD_PATH) {
                    // Polling one file's mtime is cheap and, unlike WatchService (polling every few seconds
                    // on macOS), reacts at once; the stamp is written only after a successful generateSite.
                    var seen = stamp.lastModified()
                    while (true) {
                        delay(250)
                        val now = stamp.lastModified()
                        if (now != seen) {
                            seen = now
                            send(data = "reload")
                        }
                    }
                }
            }
            get("{path...}") {
                val raw = call.request.local.uri.substringBefore('?').substringBefore('#')
                val rel = java.net.URLDecoder.decode(raw, "UTF-8").trimStart('/')
                val candidates = listOf(rel, "$rel/index.html".trimStart('/'), "$rel.html")
                val file = candidates.map { File(root, it).canonicalFile }
                    .firstOrNull { it.path.startsWith(root.path) && it.isFile }
                val live = stamp != null
                when {
                    file != null && live && file.extension == "html" ->
                        call.respondText(withReload(file.readText()), ContentType.Text.Html)
                    file != null -> call.respondFile(file)
                    else -> {
                        val nf = File(root, "404.html")
                        if (nf.isFile) {
                            val html = nf.readText()
                            call.respondText(if (live) withReload(html) else html, ContentType.Text.Html, HttpStatusCode.NotFound)
                        } else {
                            call.respondText("Not found", status = HttpStatusCode.NotFound)
                        }
                    }
                }
            }
        }
    }.start(wait = true)
}

private fun withReload(html: String): String {
    val at = html.lastIndexOf("</body>")
    return if (at < 0) html + RELOAD_SCRIPT else html.substring(0, at) + RELOAD_SCRIPT + html.substring(at)
}

/** Gradle's own file watching decides what to rebuild; the child dies with the preview. */
private fun startContinuousBuild(gradlew: String) {
    val build = ProcessBuilder(gradlew, "--continuous", "--quiet", "generateSite")
        .directory(File(gradlew).parentFile)
        .inheritIO()
        .start()
    Runtime.getRuntime().addShutdownHook(Thread {
        build.descendants().forEach { it.destroy() }
        build.destroy()
    })
}
