# Static image for the Kotlin/JS landing (pyrlyn/landing), served by the existing Compose stack
# (/opt/sites/web/web, service `landing`). Build context = the landing's build/site directory:
#   docker build --platform linux/amd64 -f docker/static/Dockerfile \
#     --build-context conf=docker/static -t pyrlyn-landing-kotlin:<sha12> build/site
FROM nginx:1.30-alpine
COPY --from=conf default.conf /etc/nginx/conf.d/default.conf
COPY --from=conf security-headers.conf /etc/nginx/security-headers.conf
COPY . /usr/share/nginx/html
