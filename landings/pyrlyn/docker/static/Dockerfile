# Static image for the Kotlin/JS landing (pyrlyn/landing), served by the existing Compose stack
# (/opt/sites/web/web, service `landing`). Build context = landings/pyrlyn/build/site:
#   docker build --platform linux/amd64 -f landings/pyrlyn/docker/static/Dockerfile \
#     --build-context conf=landings/pyrlyn/docker/static -t pyrlyn-landing-kotlin:<sha12> \
#     landings/pyrlyn/build/site
FROM nginx:1.30-alpine
COPY --from=conf default.conf /etc/nginx/conf.d/default.conf
COPY --from=conf security-headers.conf /etc/nginx/security-headers.conf
COPY . /usr/share/nginx/html
