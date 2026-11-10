// Light sweep: one WebGL pass, about 1.6 s, then the canvas is removed. Mounted on <body> as a fixed
// full-viewport layer so the sweep extends past the hero. Loaded by the site bundle only when motion
// is allowed and the hero is on screen; the page is fully visible without it.
package intro

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLElement

private external class IntersectionObserver(callback: (entries: Array<dynamic>, observer: dynamic) -> Unit) {
    fun observe(target: org.w3c.dom.Element)
    fun disconnect()
}

private const val VERT = "attribute vec2 p;void main(){gl_Position=vec4(p,0.,1.);}"

// Domain-warped value noise in the page accents, carried by a ring that expands from the hero art.
private const val FRAG = """precision mediump float;
uniform vec2 r;uniform float t;uniform vec3 a;uniform vec3 b;uniform vec2 o;
float h(vec2 p){return fract(sin(dot(p,vec2(127.1,311.7)))*43758.5453);}
float n(vec2 p){vec2 i=floor(p),f=fract(p);f=f*f*(3.-2.*f);
 return mix(mix(h(i),h(i+vec2(1,0)),f.x),mix(h(i+vec2(0,1)),h(i+1.),f.x),f.y);}
float fbm(vec2 p){float v=0.,s=.5;for(int k=0;k<4;k++){v+=s*n(p);p*=2.03;s*=.5;}return v;}
void main(){
 vec2 uv=gl_FragCoord.xy/r;vec2 q=(gl_FragCoord.xy-o*r)/r.y;
 float d=length(q);
 float e=1.-pow(1.-clamp(t/1.5,0.,1.),3.);
 float R=e*1.9;
 float w=fbm(q*3.+vec2(t*.6,-t*.4)+fbm(q*2.-t*.3));
 float ring=exp(-pow((d-R+(w-.5)*.18)*9.,2.));
 float wake=smoothstep(R,0.,d)*.22*(1.-e);
 float fade=1.-smoothstep(1.,1.6,t);
 vec3 c=mix(a,b,smoothstep(.25,.85,w+uv.x*.3));
 float al=(ring*.9+wake*w)*fade;
 gl_FragColor=vec4(c*al,al);
}"""

private fun cssVar(name: String): String = window.getComputedStyle(document.documentElement.unsafeCast<org.w3c.dom.Element>()).getPropertyValue(name).asDynamic().trim().unsafeCast<String>()

private fun rgb(hex: String): dynamic = js("[1,3,5]").map { i: Int -> js("parseInt")(hex.asDynamic().slice(i, i + 2), 16).unsafeCast<Double>() / 255 }

fun main() {
    val hero = document.querySelector(".hero").unsafeCast<HTMLElement?>() ?: return
    play(hero, cssVar("--accent"), cssVar("--accent-2"))
}

private fun play(hero: HTMLElement, accent: String, accent2: String) {
    val cv = document.createElement("canvas").unsafeCast<HTMLCanvasElement>()
    cv.className = "intro-gl"
    cv.setAttribute("aria-hidden", "true")
    val gl = cv.getContext("webgl", js("({premultipliedAlpha:true,antialias:false,alpha:true,powerPreference:'low-power'})")).asDynamic() ?: return
    fun sh(type: dynamic, src: String): dynamic { val s = gl.createShader(type); gl.shaderSource(s, src); gl.compileShader(s); return s }
    val pr = gl.createProgram()
    gl.attachShader(pr, sh(gl.VERTEX_SHADER, VERT))
    gl.attachShader(pr, sh(gl.FRAGMENT_SHADER, FRAG))
    gl.linkProgram(pr)
    if (gl.getProgramParameter(pr, gl.LINK_STATUS) != true) return
    gl.useProgram(pr)
    gl.bindBuffer(gl.ARRAY_BUFFER, gl.createBuffer())
    gl.bufferData(gl.ARRAY_BUFFER, js("new Float32Array([-1,-1,3,-1,-1,3])"), gl.STATIC_DRAW)
    val loc = gl.getAttribLocation(pr, "p")
    gl.enableVertexAttribArray(loc)
    gl.vertexAttribPointer(loc, 2, gl.FLOAT, false, 0, 0)
    gl.enable(gl.BLEND)
    gl.blendFunc(gl.ONE, gl.ONE_MINUS_SRC_ALPHA)
    fun u(k: String): dynamic = gl.getUniformLocation(pr, k)
    gl.uniform3fv(u("a"), rgb(accent))
    gl.uniform3fv(u("b"), rgb(accent2))

    // Half resolution is plenty for a soft glow and keeps the fill cost low on large screens.
    val dpr = window.devicePixelRatio.let { if (it > 0) it else 1.0 }
    val scale = (if (dpr < 1.5) dpr else 1.5) * 0.5
    val bw = window.innerWidth.toDouble(); val bh = window.innerHeight.toDouble()
    cv.asDynamic().width = js("Math.max")(1, js("Math.round")(bw * scale))
    cv.asDynamic().height = js("Math.max")(1, js("Math.round")(bh * scale))
    gl.viewport(0, 0, cv.width, cv.height)
    gl.uniform2f(u("r"), cv.width, cv.height)
    val art = hero.querySelector(".stage")?.getBoundingClientRect() // viewport coords, match the fixed canvas
    val ox = if (art != null) (art.left + art.width / 2) / bw else 0.5
    val oy = if (art != null) 1 - (art.top + art.height / 2) / bh else 0.5
    gl.uniform2f(u("o"), ox, oy)
    document.body.unsafeCast<HTMLElement>().append(cv)

    // One-shot: if the hero leaves the viewport (anchor nav, back/forward, scroll) the sweep is dropped,
    // not paused, so no frozen half-grown ring stays over other sections.
    var raf = 0
    var t0 = 0.0
    var stopped = false
    var io: IntersectionObserver? = null
    var onVis: (dynamic) -> Unit = {}
    val stop: (dynamic) -> Unit = {
        if (!stopped) {
            stopped = true; window.cancelAnimationFrame(raf); io?.disconnect()
            document.removeEventListener("visibilitychange", onVis); cv.remove()
            gl.getExtension("WEBGL_lose_context")?.loseContext()
        }
    }
    onVis = { if (document.asDynamic().hidden == true) stop(null) }
    io = IntersectionObserver({ es, _ -> if (es[0].isIntersecting != true) stop(null) })
    io.observe(hero)
    document.addEventListener("visibilitychange", onVis)
    window.addEventListener("hashchange", stop, js("({once:true})"))
    window.addEventListener("popstate", stop, js("({once:true})"))
    var frame: (Double) -> Unit = {}
    frame = { now ->
        if (!stopped) {
            if (t0 == 0.0) t0 = now
            val elapsed = (now - t0) / 1000
            if (elapsed > 1.65) stop(null) else {
                gl.uniform1f(u("t"), elapsed)
                gl.drawArrays(gl.TRIANGLES, 0, 3)
                raf = window.requestAnimationFrame(frame)
            }
        }
    }
    raf = window.requestAnimationFrame(frame)
}
