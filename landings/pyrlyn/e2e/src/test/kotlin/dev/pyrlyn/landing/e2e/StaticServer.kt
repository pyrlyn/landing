package dev.pyrlyn.landing.e2e

import com.sun.net.httpserver.HttpServer
import java.io.File
import java.net.InetSocketAddress
import java.net.URLDecoder

/**
 * Minimal static server for build/site (JDK `com.sun.net.httpserver`, no extra deps).
 * Mirrors production static hosting: `/x/` serves `/x/index.html`, misses return `404.html` with status 404.
 */
class StaticServer(private val root: File) : AutoCloseable {
    private val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
    val port: Int get() = server.address.port
    val origin: String get() = "http://127.0.0.1:$port"

    init {
        server.createContext("/") { ex ->
            val path = URLDecoder.decode(ex.requestURI.rawPath, Charsets.UTF_8)
            val file = resolve(path)
            val (status, body) = if (file != null) 200 to file else 404 to File(root, "404.html")
            ex.responseHeaders.add("Content-Type", contentType(body.name))
            val bytes = if (ex.requestMethod == "HEAD") null else body.readBytes()
            ex.sendResponseHeaders(status, if (bytes == null) -1 else bytes.size.toLong())
            bytes?.let { ex.responseBody.use { o -> o.write(it) } }
            ex.close()
        }
        server.start()
    }

    /** Maps a URL path to a file inside [root], or null when it does not exist (or escapes the root). */
    fun resolve(path: String): File? {
        val rel = path.trimStart('/')
        val f = File(root, rel).let { if (it.isDirectory) File(it, "index.html") else it }.canonicalFile
        return f.takeIf { it.isFile && it.path.startsWith(root.canonicalPath) }
    }

    override fun close() = server.stop(0)

    private fun contentType(name: String) = when (name.substringAfterLast('.', "")) {
        "html" -> "text/html; charset=utf-8"
        "js", "mjs" -> "text/javascript"
        "css" -> "text/css"
        "json" -> "application/json"
        "xml" -> "application/xml"
        "txt" -> "text/plain; charset=utf-8"
        "svg" -> "image/svg+xml"
        "png" -> "image/png"
        "jpg", "jpeg" -> "image/jpeg"
        "webp" -> "image/webp"
        "avif" -> "image/avif"
        "ico" -> "image/x-icon"
        "woff2" -> "font/woff2"
        "webm" -> "video/webm"
        "mp4" -> "video/mp4"
        else -> "application/octet-stream"
    }
}
