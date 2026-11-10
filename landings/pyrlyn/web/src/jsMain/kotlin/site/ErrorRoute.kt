package site

/*
 * Routes uncaught errors from our own bundles to /500.html with location.replace (no Back loop).
 * Filter rules:
 * - window "error": only script errors whose filename starts with <this origin>/_assets/.
 *   Extensions (chrome-extension://, moz-extension://), third-party and inline scripts, cross-origin
 *   "Script error." (empty filename) and resource load errors (img/script/link targets) are ignored.
 * - "unhandledrejection": only when the reason's stack mentions <this origin>/_assets/.
 * - "ResizeObserver loop ..." messages are ignored.
 * - Never active on /404.html, /500.html, /50x.html or any page rendering .err__code (nginx serves
 *   404.html at the missing URL itself), and it fires at most once per page.
 * Test hook: ?__throw=1 throws from this bundle once on load.
 */
fun initErrorRoute() {
    js("""
    (function () {
      var p = location.pathname;
      if (/^\/(404|500|50x)(\.html)?\/?$/.test(p) || document.querySelector('.err__code')) return;
      var own = location.origin + '/_assets/';
      var gone = false;
      function go() { if (gone) return; gone = true; location.replace('/500.html'); }
      function noise(m) { return /ResizeObserver loop/i.test(String(m || '')); }
      window.addEventListener('error', function (e) {
        if (e.target && e.target !== window) return;
        if (noise(e.message)) return;
        if (typeof e.filename !== 'string' || e.filename.indexOf(own) !== 0) return;
        go();
      });
      window.addEventListener('unhandledrejection', function (e) {
        var r = e && e.reason;
        var s = r && r.stack ? String(r.stack) : '';
        if (noise(r && r.message) || s.indexOf(own) < 0) return;
        go();
      });
      if (/[?&]__throw=1(&|$)/.test(location.search)) setTimeout(function () { throw new Error('__throw test'); }, 0);
    })();
    """)
}
