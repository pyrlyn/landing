# syntax=docker/dockerfile:1
# Kotlin landing (pyrlyn/landing): ./gradlew generateSite -> build/site, served by nginx on :80.
# Build context is this directory (landings/pyrlyn/), not the repository root.
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
ARG SITE_URL=https://pyrlyn.dev
ARG SITE_BASE=/
# Build-time only (this stage is discarded). Needed to resolve
# maven.pkg.github.com/pyrlyn/backend (backend.landing-client).
ARG GITHUB_TOKEN
ENV SITE_URL=$SITE_URL SITE_BASE=$SITE_BASE GITHUB_TOKEN=$GITHUB_TOKEN
COPY . .
RUN ./gradlew --no-daemon -Dorg.gradle.configuration-cache=false generateSite && test -f build/site/index.html

FROM nginx:1.30-alpine
COPY docker/paas-nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/build/site /usr/share/nginx/html
EXPOSE 80
