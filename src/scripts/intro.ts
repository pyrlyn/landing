// Light sweep: one WebGL pass, about 1.6 s, then the canvas is removed. The canvas is mounted on
// <body> (one level above the hero container) as a fixed full-viewport layer, so the sweep extends
// past the hero's bounds instead of being clipped by it. It never plays on load: main.ts starts it
// after window load, on the first user interaction. No loading UI.
// Loaded lazily by main.ts only when motion is allowed and the hero is on screen; the page itself
// is fully visible without it (the hero's CSS entrance in motion.css does not depend on this file).
const VERT = `attribute vec2 p;void main(){gl_Position=vec4(p,0.,1.);}`;
// Domain-warped value noise in the page accents, carried by a ring that expands from the hero art.
const FRAG = `precision mediump float;
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
}`;

export function playIntro(hero: HTMLElement, accent: string, accent2: string) {
  const cv = document.createElement("canvas");
  cv.className = "intro-gl";
  cv.setAttribute("aria-hidden", "true");
  const gl = cv.getContext("webgl", { premultipliedAlpha: true, antialias: false, alpha: true, powerPreference: "low-power" });
  if (!gl) return;
  const sh = (type: number, src: string) => { const s = gl.createShader(type)!; gl.shaderSource(s, src); gl.compileShader(s); return s; };
  const pr = gl.createProgram()!;
  gl.attachShader(pr, sh(gl.VERTEX_SHADER, VERT));
  gl.attachShader(pr, sh(gl.FRAGMENT_SHADER, FRAG));
  gl.linkProgram(pr);
  if (!gl.getProgramParameter(pr, gl.LINK_STATUS)) return;
  gl.useProgram(pr);
  gl.bindBuffer(gl.ARRAY_BUFFER, gl.createBuffer());
  gl.bufferData(gl.ARRAY_BUFFER, new Float32Array([-1, -1, 3, -1, -1, 3]), gl.STATIC_DRAW);
  const loc = gl.getAttribLocation(pr, "p");
  gl.enableVertexAttribArray(loc);
  gl.vertexAttribPointer(loc, 2, gl.FLOAT, false, 0, 0);
  gl.enable(gl.BLEND);
  gl.blendFunc(gl.ONE, gl.ONE_MINUS_SRC_ALPHA);
  const rgb = (hex: string) => [1, 3, 5].map((i) => parseInt(hex.slice(i, i + 2), 16) / 255);
  const u = (k: string) => gl.getUniformLocation(pr, k);
  gl.uniform3fv(u("a"), rgb(accent));
  gl.uniform3fv(u("b"), rgb(accent2));

  // Half resolution is plenty for a soft glow and keeps the fill cost low on large screens.
  const scale = Math.min(devicePixelRatio || 1, 1.5) * 0.5;
  const box = { left: 0, top: 0, width: innerWidth, height: innerHeight };
  cv.width = Math.max(1, Math.round(box.width * scale));
  cv.height = Math.max(1, Math.round(box.height * scale));
  gl.viewport(0, 0, cv.width, cv.height);
  gl.uniform2f(u("r"), cv.width, cv.height);
  const art = hero.querySelector(".stage")?.getBoundingClientRect(); // viewport coords, matches the fixed canvas
  const ox = art ? (art.left + art.width / 2 - box.left) / box.width : 0.5;
  const oy = art ? 1 - (art.top + art.height / 2 - box.top) / box.height : 0.5;
  gl.uniform2f(u("o"), ox, oy);
  document.body.append(cv);

  let visible = true, raf = 0, t0 = 0, elapsed = 0;
  const io = new IntersectionObserver(([en]) => { visible = en.isIntersecting; if (visible && !raf) raf = requestAnimationFrame(frame); });
  io.observe(hero);
  const stop = () => { cancelAnimationFrame(raf); io.disconnect(); document.removeEventListener("visibilitychange", onVis); cv.remove(); gl.getExtension("WEBGL_lose_context")?.loseContext(); };
  const onVis = () => { if (document.hidden) stop(); };
  document.addEventListener("visibilitychange", onVis);
  function frame(now: number) {
    if (!t0) t0 = now - elapsed * 1000;
    elapsed = (now - t0) / 1000;
    if (elapsed > 1.65) return stop();
    if (!visible) { raf = 0; t0 = 0; return; } // paused off screen; resumes where it left off
    gl!.uniform1f(u("t"), elapsed);
    gl!.drawArrays(gl!.TRIANGLES, 0, 3);
    raf = requestAnimationFrame(frame);
  }
  raf = requestAnimationFrame(frame);
}
