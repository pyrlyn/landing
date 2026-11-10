# syntax=docker/dockerfile:1
# Kotlin landing (pyrlyn/landing): ./gradlew generateSite -> build/site, served by nginx on :80.
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
RUN ./gradlew --no-daemon -Dorg.gradle.configuration-cache=false generateSite && test -f build/site/index.html

FROM nginx:1.30-alpine
COPY docker/paas-nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/build/site /usr/share/nginx/html
EXPOSE 80
