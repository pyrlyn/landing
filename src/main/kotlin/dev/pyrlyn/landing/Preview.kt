package dev.pyrlyn.landing

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.defaultForFile
import io.ktor.server.application.call
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.response.respondFile
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import java.io.File

/**
 * Dev preview only (`./gradlew run`): serves build/site like the production nginx does —
 * `/x/` → `/x/index.html`, `/x` → `/x/index.html` or `/x.html`, unknown paths → 404.html.
 * Not a production server; the site deploys as static files.
 */
fun main(args: Array<String>) {
    val root = File(args.getOrNull(0) ?: "build/site").canonicalFile
    val port = System.getenv("PORT")?.toIntOrNull() ?: 4321
    println("landing: preview of $root on http://localhost:$port/")
    embeddedServer(CIO, port = port) {
        routing {
            get("{path...}") {
                val raw = call.request.local.uri.substringBefore('?').substringBefore('#')
                val rel = java.net.URLDecoder.decode(raw, "UTF-8").trimStart('/')
                val candidates = listOf(rel, "$rel/index.html".trimStart('/'), "$rel.html")
                val file = candidates.map { File(root, it).canonicalFile }
                    .firstOrNull { it.path.startsWith(root.path) && it.isFile }
                if (file != null) call.respondFile(file)
                else {
                    val nf = File(root, "404.html")
                    if (nf.isFile) call.respondText(nf.readText(), ContentType.Text.Html, HttpStatusCode.NotFound)
                    else call.respondText("Not found", status = HttpStatusCode.NotFound)
                }
            }
        }
    }.start(wait = true)
}
